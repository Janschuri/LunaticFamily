package de.janschuri.lunaticfamily.common.database;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.config.DatabaseConfig;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.handler.Marriage;
import de.janschuri.lunaticfamily.common.handler.query.QAdoption;
import de.janschuri.lunaticfamily.common.handler.query.QFamilyPlayer;
import de.janschuri.lunaticfamily.common.handler.query.QMarriage;
import de.janschuri.lunaticfamily.common.handler.query.QSiblinghood;
import de.janschuri.lunaticfamily.common.utils.Logger;
import io.ebean.Database;
import io.ebean.Expr;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.platform.mysql.MySqlPlatform;
import io.ebean.platform.sqlite.SQLitePlatform;
import org.jetbrains.annotations.NotNull;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.QOM;
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
import java.sql.Timestamp;
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

            String migrationsTable = "lunaticfamily_migrations";
            Query query = context.createTableIfNotExists(migrationsTable)
                    .column("id", SQLDataType.BIGINTUNSIGNED
                            .nullable(false)
                            .identity(true)
                    )
                    .column("name", SQLDataType.VARCHAR(255).nullable(false))
                    .column("batch", SQLDataType.BIGINTUNSIGNED.nullable(false))
                    .constraint(DSL.constraint("name_unique").unique("name"))
                    .primaryKey("id");

            query.execute();

            List<String> names = context.select(DSL.field("name", String.class))
                    .from(DSL.table(migrationsTable))
                    .fetch(DSL.field("name", String.class));

            List<Migration> migrations = getMigrations()
                    .stream()
                    .filter(migration -> !names.contains(migration.getClass().getSimpleName()))
                    .sorted(Comparator.comparing((Migration m) -> m.getClass().getSimpleName()))
                    .toList();


            Logger.infoLog("Migrations to run: " + migrations.size());
            conn.setAutoCommit(false);

            long batch = context.select(DSL.field("batch", Long.class))
                    .from(DSL.table(migrationsTable))
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

                    context.insertInto(DSL.table(migrationsTable))
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

    public static boolean canMigrate() {
        try (Connection conn = getConnection()) {
            DSLContext context = DSL.using(conn, getSQLDialect());

            if (!tableExists(context, "marry_players") || !tableExists(context, "marry_partners")) {
                return false;
            }
        } catch (SQLException e) {
            Logger.errorLog("Error while checking migration: " + e.getMessage());
            return false;
        }

        return true;
    }

    public static boolean migrateMarriageMaster() {
        try (Connection conn = getConnection()) {
            DSLContext context = DSL.using(conn, getSQLDialect());

            String oldPlayerTableName = "marry_players";
            String oldMarriagesTableName = "marry_partners";

            if (!tableExists(context, oldPlayerTableName) || !tableExists(context, oldMarriagesTableName)) {
                Logger.errorLog("Old tables do not exist");
                return false;
            }

            List<Record> players = context.select()
                    .from(oldPlayerTableName)
                    .fetch();

            Map<Long, UUID> playerMapping = new HashMap<>();
            List<FamilyPlayer> familyPlayers = new ArrayList<>();

            boolean first = true;

            for (Record player : players) {
                if (first) {
                    first = false;
                    // log all columns with values
                    for (Field<?> field : player.fields()) {
                        Logger.debugLog(field.getName() + ": " + player.get(field));
                    }
                }

                long id = player.get("player_id", Long.class);

                String uuidString = player.get("uuid", String.class);
                String formattedUUID = uuidString.replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                        "$1-$2-$3-$4-$5"
                );

                UUID uuid = UUID.fromString(formattedUUID);

                playerMapping.put(id, uuid);

                FamilyPlayer familyPlayer = FamilyPlayer.findOrCreate(uuid);
                if (familyPlayer != null) {
                    String name = player.get("name", String.class);
                    familyPlayer.setName(name);

                    familyPlayers.add(familyPlayer);
                }
            }

            getDatabase().saveAll(familyPlayers);


            Map<Long, Long> idMapping = new HashMap<>();

            List<FamilyPlayer> newPlayers = getDatabase().find(FamilyPlayer.class)
                    .where()
                    .in("uuid", playerMapping.values())
                    .findList();

            for (FamilyPlayer newPlayer : newPlayers) {
                long id = newPlayer.getId();
                UUID uuid = newPlayer.getUUID();

                for (Map.Entry<Long, UUID> entry : playerMapping.entrySet()) {
                    if (entry.getValue().equals(uuid)) {
                        idMapping.put(entry.getKey(), id);
                        break;
                    }
                }
            }

            List<Record> marriages = context.select()
                    .from(oldMarriagesTableName)
                    .fetch();

            List<Marriage> newMarriages = new ArrayList<>();

            for (Record marriage : marriages) {
                long oldPlayer1Id = marriage.get("player1", Long.class);
                long oldPlayer2Id = marriage.get("player2", Long.class);
                long oldPriestId = marriage.get("priest", Long.class);
                Timestamp date = marriage.get("date", Timestamp.class);

                long newPlayer1Id = idMapping.get(oldPlayer1Id);
                long newPlayer2Id = idMapping.get(oldPlayer2Id);
                long newPriestId = idMapping.get(oldPriestId);
                if (newPriestId == newPlayer1Id || newPriestId == newPlayer2Id) {
                    newPriestId = -1;
                }

                boolean oldMarriage = getDatabase().find(Marriage.class)
                        .where()
                        .or(
                                Expr.and(Expr.eq("player1_id", newPlayer1Id), Expr.eq("player2_id", newPlayer2Id)),
                                Expr.and(Expr.eq("player1_id", newPlayer2Id), Expr.eq("player2_id", newPlayer1Id))
                        )
                        .exists();

                if (oldMarriage) {
                    continue;
                }

                Marriage newMarriage = new Marriage(newPlayer1Id, newPlayer2Id, date);

                if (newPriestId != -1) {
                    newMarriage.setPriest(FamilyPlayer.find(newPriestId));
                }

                newMarriages.add(newMarriage);
            }

            getDatabase().saveAll(newMarriages);

        } catch (SQLException e) {
            Logger.errorLog("Error while migrating marriage master: " + e.getMessage());
            return false;
        }

        return true;
    }

    protected static boolean tableExists(DSLContext create, String tableName) {
        @NotNull List<Table<?>> tables = create.meta().getTables(tableName);
        return !tables.isEmpty();
    }
}

