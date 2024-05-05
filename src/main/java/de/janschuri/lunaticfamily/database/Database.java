package de.janschuri.lunaticfamily.database;

import de.janschuri.lunaticfamily.LunaticFamily;
import de.janschuri.lunaticfamily.config.DatabaseConfig;
import de.janschuri.lunaticfamily.database.tables.AdoptionsTable;
import de.janschuri.lunaticfamily.database.tables.MarriagesTable;
import de.janschuri.lunaticfamily.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.database.tables.SiblinghoodsTable;
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
