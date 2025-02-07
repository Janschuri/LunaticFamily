package de.janschuri.lunaticfamily.common.database;


import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.config.DatabaseConfig;
import de.janschuri.lunaticfamily.common.handler.query.QAdoption;
import de.janschuri.lunaticfamily.common.handler.query.QFamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.handler.query.QMarriage;
import de.janschuri.lunaticfamily.common.handler.query.QSiblinghood;
import de.janschuri.lunaticfamily.common.utils.Logger;
import io.ebean.Database;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.platform.sqlite.SQLitePlatform;

import java.util.Set;

import static io.ebean.DatabaseFactory.createWithContextClassLoader;

public class DatabaseRepository {

    private static Database db;

    public static boolean loadDatabase() {
        Logger.infoLog("Loading database...");
        DatabaseConfig databaseConfig = new DatabaseConfig(LunaticFamily.getDataDirectory());
        databaseConfig.load();


        Set<Class<?>> classes = Set.of(
                QFamilyPlayerImpl.class,
                QAdoption.class,
                QMarriage.class,
                QSiblinghood.class
        );

        databaseConfig.load();

        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUsername(databaseConfig.getUsername());
        dataSourceConfig.setPassword(databaseConfig.getPassword());
        dataSourceConfig.setUrl("jdbc:sqlite:./plugins/LunaticFamily/" + databaseConfig.getFilename() + ".db");
        dataSourceConfig.setDriver("org.sqlite.JDBC");

        io.ebean.config.DatabaseConfig config = new io.ebean.config.DatabaseConfig();
        config.setName("db");
        config.setDataSourceConfig(dataSourceConfig);
        config.setDefaultServer(true);
        config.setRegister(true);
        config.setDatabasePlatform(new SQLitePlatform());

        config.setClasses(classes);

        // Enable automatic DDL generation and execution
        config.setDdlGenerate(true);
        config.setDdlRun(true);


        ClassLoader classLoader = LunaticFamily.class.getClassLoader();

        // Create the Ebean database instance
        db = createWithContextClassLoader(config, classLoader);

        return true;
    }

    public static Database getDatabase() {
        Logger.debugLog("Returning database instance.");
        return db;
    }
}
