package de.janschuri.lunaticFamily.config;

import java.nio.file.Path;

public class DatabaseConfig extends Config {
//    private final File databaseConfigFile;
    public static String host, database, username, password, filename;
    public static int port;

    private static final String DATABASE_FILE = "database.yml";
    public static boolean useMySQL;

    public DatabaseConfig(Path dataDirectory) {
        super(dataDirectory, DATABASE_FILE);
        this.load();
    }

    @Override
    public void load() {
        super.load();
        host = getString("MySQL.host", "localhost");
        port = getInt("MySQL.port", 3306);
        database = getString("MySQL.database", "lunaticfamily");
        username = getString("MySQL.username", "root");
        password = getString("MySQL.password", "");
        filename = getString("SQLite.filename", "lunaticfamily");
        useMySQL = getBoolean("MySQL.enabled", false);
    }
}
