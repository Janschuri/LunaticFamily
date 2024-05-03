package de.janschuri.lunaticFamily.database;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.DatabaseConfig;
import de.janschuri.lunaticFamily.database.tables.AdoptionsTable;
import de.janschuri.lunaticFamily.database.tables.MarriagesTable;
import de.janschuri.lunaticFamily.database.tables.PlayerDataTable;
import de.janschuri.lunaticFamily.database.tables.SiblinghoodsTable;
import de.janschuri.lunaticFamily.utils.Logger;
import de.janschuri.lunaticlib.database.Table;

public class Database {

    private static de.janschuri.lunaticlib.database.Database db;
    private static final Table[] tables = {
        PlayerDataTable.getTable(),
        SiblinghoodsTable.getTable(),
        AdoptionsTable.getTable(),
        MarriagesTable.getTable(),
    };

    public static boolean loadDatabase() {
        DatabaseConfig databaseConfig = new DatabaseConfig(LunaticFamily.getDataDirectory());
        db = de.janschuri.lunaticlib.database.Database.getDatabase(databaseConfig, tables);

        return db != null;
    }

    public static de.janschuri.lunaticlib.database.Database getDatabase() {
        return db;
    }
}
