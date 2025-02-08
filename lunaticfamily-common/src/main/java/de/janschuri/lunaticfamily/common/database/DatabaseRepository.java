package de.janschuri.lunaticfamily.common.database;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.config.DatabaseConfig;
import de.janschuri.lunaticfamily.common.handler.query.QAdoption;
import de.janschuri.lunaticfamily.common.handler.query.QFamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.handler.query.QMarriage;
import de.janschuri.lunaticfamily.common.handler.query.QSiblinghood;
import de.janschuri.lunaticfamily.common.utils.Logger;
import io.ebean.Database;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.platform.mysql.MySqlPlatform;
import io.ebean.platform.sqlite.SQLitePlatform;

import java.nio.file.Path;
import java.util.Set;

import static io.ebean.DatabaseFactory.createWithContextClassLoader;

public class DatabaseRepository {

    private static Database db;
    private static DatabaseConfig databaseConfig;

    public static boolean init() {
        Logger.infoLog("Loading database...");

        Path dataDirectory = LunaticFamily.getDataDirectory();
        databaseConfig = new DatabaseConfig(dataDirectory);
        databaseConfig.load();

        loadDatabase();
        runMigrations();

        return true;
    }

    private static String getType() {
        // type can be either "sqlite" or "mysql"
        return databaseConfig.getType().toLowerCase();
    }

    private static void runMigrations() {
    }

    private static void loadDatabase() {
        Set<Class<?>> classes = Set.of(
                QFamilyPlayerImpl.class,
                QAdoption.class,
                QMarriage.class,
                QSiblinghood.class
        );


        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUsername(databaseConfig.getUsername());
        dataSourceConfig.setPassword(databaseConfig.getPassword());
        dataSourceConfig.setUrl(getDatabaseUrl());
        dataSourceConfig.setDriver(getDatabaseDriver());

        io.ebean.config.DatabaseConfig config = new io.ebean.config.DatabaseConfig();
        config.setName("migrations");
        config.setDataSourceConfig(dataSourceConfig);
        config.setDefaultServer(true);
        config.setRegister(true);
        config.setDatabasePlatform(getDatabasePlatform());

        config.setClasses(classes);

        // Enable automatic DDL generation and execution
        config.setDdlGenerate(true);
        config.setDdlRun(true);

        ClassLoader classLoader = LunaticFamily.class.getClassLoader();

        // Create the Ebean database instance
        db = createWithContextClassLoader(config, classLoader);
    }

    private static String getDatabaseUrl() {
        String type = getType();
        switch (type) {
            case "mysql":
                return "jdbc:mysql://" + databaseConfig.getHost() + ":" + databaseConfig.getPort() + "/" + databaseConfig.getDatabase();
            case "sqlite":
            default:
                Path dataDirectory = LunaticFamily.getDataDirectory();
                return "jdbc:sqlite:" + dataDirectory + "/" + databaseConfig.getFilename() + ".db";
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
}

