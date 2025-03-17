package de.janschuri.lunaticfamily.common.database;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.config.DatabaseConfig;
import de.janschuri.lunaticfamily.common.handler.query.QAdoption;
import de.janschuri.lunaticfamily.common.handler.query.QFamilyPlayer;
import de.janschuri.lunaticfamily.common.handler.query.QMarriage;
import de.janschuri.lunaticfamily.common.handler.query.QSiblinghood;
import de.janschuri.lunaticfamily.common.utils.Logger;
import io.ebean.Database;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.platform.mysql.MySqlPlatform;
import io.ebean.platform.sqlite.SQLitePlatform;
import org.jooq.*;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.SQLDataType;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;

import static io.ebean.DatabaseFactory.createWithContextClassLoader;

public class DatabaseRepository {

    private static Database db;
    private static DatabaseConfig databaseConfig;

    public static boolean init() {
        Logger.infoLog("Loading database...");

        Path dataDirectory = LunaticFamily.getDataDirectory();
        databaseConfig = new DatabaseConfig(dataDirectory);
        databaseConfig.load();

        if (getType().equalsIgnoreCase("sqlite")) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                Logger.errorLog("Could not load SQLite JDBC driver.");
                e.printStackTrace();
                return false;
            }
        }

        createDatabaseIfNotExists();
        runMigrations();
        loadDatabase();


        return true;
    }

    private static String getType() {
        return databaseConfig.getType();
    }

    private static String getDatabaseName() {
        if (getType().equalsIgnoreCase("sqlite")) {
            return databaseConfig.getFilename() + ".db";
        }
        if (getType().equalsIgnoreCase("mysql")) {
            return databaseConfig.getDatabase();
        }

        return null;
    }

    public static void shutdown() {
        if (db != null) {
            db.shutdown();
        }
    }

    private static void runMigrations() {
        try (Connection conn = getConnection()) {
            if (conn == null) {
                Logger.errorLog("Could not connect to database");
                return;
            }

            java.util.logging.Logger.getLogger("org.jooq.Constants").setLevel(java.util.logging.Level.SEVERE);

            Settings settings = new Settings()
                    .withRenderFormatted(false)
                    .withExecuteLogging(false);

            Configuration configuration = new DefaultConfiguration()
                    .set(conn)
                    .set(getSQLDialect())
                    .set(settings);

            DSLContext context = DSL.using(configuration);

            Query query = context.createTableIfNotExists("migrations")
                    .column("id", SQLDataType.BIGINT
                            .nullable(false)
                            .identity(true)
                    )
                    .column("name", SQLDataType.VARCHAR(255).nullable(false))
                    .column("batch", SQLDataType.BIGINT.nullable(false))
                    .constraint(DSL.constraint("name_unique").unique("name"))
                    .primaryKey("id");

            query.execute();

            List<String> names = context.select(DSL.field("name", String.class))  // Specify the type as String
                    .from(DSL.table("migrations"))
                    .fetch(DSL.field("name", String.class));

            List<Migration> migrations = getMigrations()
                    .stream()
                    .filter(migration -> !names.contains(migration.getClass().getSimpleName()))
                    .sorted(Comparator.comparing((Migration m) -> m.getClass().getSimpleName()))
                    .toList();


            Logger.infoLog("Migrations to run: " + migrations.size());
            conn.setAutoCommit(false);

            long batch = context.select(DSL.field("batch", Long.class))
                    .from(DSL.table("migrations"))
                    .stream().max((o1, o2) -> (int) (o1.get(DSL.field("batch", Long.class)) - o2.get(DSL.field("batch", Long.class))))
                    .map(record -> record.get(DSL.field("batch", Long.class)))
                    .orElse(0L) + 1;

            for (Migration migration : migrations) {
                if (names.contains(migration.getClass().getSimpleName())) {
                    continue;
                }

                String name = migration.getClass().getSimpleName();
                Logger.infoLog("Running migration: " + name);

                try {
                    migration.run(context);

                    context.insertInto(DSL.table("migrations"))
                            .columns(DSL.field("name"), DSL.field("batch"))
                            .values(name, batch)
                            .execute();

                    conn.commit();
                } catch (Exception e) {
                    conn.rollback();
                    Logger.errorLog("Error while running migration: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            Logger.infoLog("Migrations ran successfully");

        } catch (Exception e) {
            Logger.errorLog("Error while running migrations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Connection getConnection() {
        return getConnection(false);
    }

    private static Connection getConnection(boolean baseUrl) {
        String url = getDatabaseUrl();
        String user = databaseConfig.getUsername();
        String password = databaseConfig.getPassword();

        if (!baseUrl) {
            url = url + "/" + getDatabaseName();
        }

        String dialect = getType();

        try {
            switch (dialect.toLowerCase()) {
                case "sqlite":
                    return DriverManager.getConnection(url);
                case "mysql":
                    return DriverManager.getConnection(url, user, password);
                default:
                    throw new IllegalArgumentException("Unsupported dialect: " + dialect);
            }
        } catch (SQLException e) {
            Logger.errorLog("Error while connecting to database: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private static void loadDatabase() {
        Set<Class<?>> classes = Set.of(
                QFamilyPlayer.class,
                QAdoption.class,
                QMarriage.class,
                QSiblinghood.class
        );


        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUsername(databaseConfig.getUsername());
        dataSourceConfig.setPassword(databaseConfig.getPassword());
        String url = getDatabaseUrl() + "/" + getDatabaseName();
        Logger.infoLog("Loading data from database: " + url);
        dataSourceConfig.setUrl(url);
        dataSourceConfig.setDriver(getDatabaseDriver());

        io.ebean.config.DatabaseConfig config = new io.ebean.config.DatabaseConfig();
        config.setName("db");
        config.setDataSourceConfig(dataSourceConfig);
        config.setDefaultServer(true);
        config.setRegister(true);
        config.setDatabasePlatform(getDatabasePlatform());

        config.setClasses(classes);

        ClassLoader classLoader = LunaticFamily.class.getClassLoader();

        // Create the Ebean database instance
        db = createWithContextClassLoader(config, classLoader);
    }

    private static void createDatabaseIfNotExists() {
        if (getType().equalsIgnoreCase("sqlite")) {
            Path dataDirectory = LunaticFamily.getDataDirectory();
            Path databasePath = dataDirectory.resolve(getDatabaseName());

            if (!databasePath.toFile().exists()) {
                try {
                    databasePath.toFile().createNewFile();
                } catch (Exception e) {
                    Logger.errorLog("Error while creating database: " + e.getMessage());
                }
            }
        }

        if (getType().equalsIgnoreCase("mysql")) {
            try (Connection conn = getConnection(true)) {
                DSLContext context = DSL.using(conn, getSQLDialect());
                String databaseName = getDatabaseName();
                context.createDatabaseIfNotExists(databaseName)
                        .execute();
            } catch (SQLException e) {
                Logger.errorLog("Error while creating database: " + e.getMessage());
            }
        }
    }

    private static String getDatabaseUrl() {
        String type = getType();
        switch (type) {
            case "mysql":
                return "jdbc:mysql://" + databaseConfig.getHost() + ":" + databaseConfig.getPort();
            case "sqlite":
            default:
                Path dataDirectory = LunaticFamily.getDataDirectory();
                return "jdbc:sqlite:" + dataDirectory;
        }
    }

    private static String getDatabaseDriver() {
        String type = getType();
        switch (type) {
            case "mysql":
                return "com.mysql.cj.jdbc.Driver";
            case "sqlite":
            default:
                return "org.sqlite.JDBC";
        }
    }

    private static SQLDialect getSQLDialect() {
        String type = getType();
        switch (type) {
            case "mysql":
                return SQLDialect.MYSQL;
            case "sqlite":
            default:
                return SQLDialect.SQLITE;
        }
    }

    private static DatabasePlatform getDatabasePlatform() {
        String type = getType();
        switch (type) {
            case "mysql":
                return new MySqlPlatform();
            case "sqlite":
            default:
                return new SQLitePlatform();
        }
    }


    public static Database getDatabase() {
        return db;
    }

    private static List<Migration> getMigrations() {
        String packageName = "de.janschuri.lunaticfamily.common.database.migrations";
        ClassLoader pluginClassLoader = LunaticFamily.class.getClassLoader();

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageName, pluginClassLoader))
                .setScanners(new SubTypesScanner(false))
                .filterInputsBy(new FilterBuilder().includePackage(packageName))
                .addClassLoaders(pluginClassLoader)
        );


        return reflections.getSubTypesOf(Migration.class).stream()
                .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                .map(clazz -> {
                    try {
                        return clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        Logger.errorLog("Error while creating migration: " + e.getMessage());
                        return null;
                    }
                })
                .filter(migration -> migration != null)
                .sorted(Comparator.comparing((Migration m) -> m.getClass().getSimpleName()))
                .collect(Collectors.toList());
    }
}

