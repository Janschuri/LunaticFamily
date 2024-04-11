package de.janschuri.lunaticFamily.config;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.utils.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class DatabaseConfig{
    private final File databaseConfigFile;
    public static String host, database, username, password, filename;
    public static int port;
    public static boolean useMySQL;

    public DatabaseConfig(LunaticFamily plugin) {
        databaseConfigFile = new File(plugin.getDataFolder().getAbsolutePath() + "/database.yml");
        this.load();
    }

    public void load() {
        if (!databaseConfigFile.exists()) {
            LunaticFamily.getInstance().saveResource("database.yml", false);
        } else {
            ConfigUtils.addMissingProperties(databaseConfigFile, databaseConfigFile);
        }

        FileConfiguration databaseConfig = YamlConfiguration.loadConfiguration(databaseConfigFile);

        host = databaseConfig.getString("Database.MySQL.Host", "localhost");
        port = databaseConfig.getInt("Database.MySQL.Port", 3306);
        database = databaseConfig.getString("Database.MySQL.Database", "lunaticfamily");
        username = databaseConfig.getString("Database.MySQL.Username", "root");
        password = databaseConfig.getString("Database.MySQL.Password", "");
        filename = databaseConfig.getString("filename", "lunaticfamily");
        useMySQL = databaseConfig.getBoolean("Database.MySQL.enabled", false);
    }
}
