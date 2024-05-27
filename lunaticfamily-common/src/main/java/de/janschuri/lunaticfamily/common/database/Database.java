package de.janschuri.lunaticfamily.common.database;


import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.config.DatabaseConfig;
import de.janschuri.lunaticfamily.common.database.tables.AdoptionsTable;
import de.janschuri.lunaticfamily.common.database.tables.MarriagesTable;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.database.tables.SiblinghoodsTable;
import de.janschuri.lunaticlib.common.database.Table;

public class Database {

    private static de.janschuri.lunaticlib.common.database.Database db;

    private static final Table[] tables = {
        PlayerDataTable.getTable(),
        SiblinghoodsTable.getTable(),
        AdoptionsTable.getTable(),
        MarriagesTable.getTable(),
    };

    public static boolean loadDatabase() {
        DatabaseConfig databaseConfig = new DatabaseConfig(LunaticFamily.getDataDirectory());
        databaseConfig.load();
        db = de.janschuri.lunaticlib.common.database.Database.getDatabase(databaseConfig, tables);

        return db != null;
    }

    public static de.janschuri.lunaticlib.common.database.Database getDatabase() {
        return db;
    }
}
