package de.janschuri.lunaticFamily.config;

import de.janschuri.lunaticFamily.LunaticFamily;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PluginConfig extends Config {
    private final LunaticFamily plugin = LunaticFamily.getInstance();
    private final File defaultConfigFile = new File(plugin.getDataFolder().getAbsolutePath() + "/defaultConfig.yml");
    private final File configFile = new File(plugin.getDataFolder().getAbsolutePath() + "/config.yml");
    private static FileConfiguration config;
    public static boolean isDebug;
    public static String language;
    public static String defaultGender;
    public static String defaultBackground;
    public static boolean allowSingleAdopt;
    public static boolean enabledCrazyAdvancementAPI;
    public static boolean enabledVault;
    public static boolean enabledMinepacks;
    public static boolean enabledMySQL;
    public static boolean marryBackpackOffline;
    public static String dateFormat;
    public static double marryKissRange;
    public static Map<String, List<String>> successCommands = new HashMap<>();
    public static List<String> familyList;
    public static List<String> backgrounds;
    public static Map<String, Double> commandWithdraws = new HashMap<>();
    public static Map<String, String> colors = new HashMap<>();

    public PluginConfig() {
        this.load();
    }

    public void load() {

        plugin.saveResource("defaultConfig.yml", true);

        if (!configFile.exists()) {
            plugin.saveResource("/config.yml", false);
            addMissingProperties(configFile, defaultConfigFile);
        } else {
            addMissingProperties(configFile, defaultConfigFile);
        }


        config = YamlConfiguration.loadConfiguration(configFile);

        allowSingleAdopt = config.getBoolean("allow_single_adopt");
        marryBackpackOffline = config.getBoolean("marry_backpack_offline_access");
        defaultBackground = "textures/block/" + config.getString("default_background") + ".png";
        defaultGender = config.getString("default_gender");
        familyList = Objects.requireNonNull(config.getStringList("family_list"));
        backgrounds = Objects.requireNonNull(config.getStringList("backgrounds"));
        language = config.getString("language");
        dateFormat = config.getString("date_format");
        marryKissRange = config.getDouble("marry_kiss_range");

        isDebug = config.getBoolean("is_debug");

        enabledMinepacks = config.getBoolean("use_minepacks");
        enabledVault = config.getBoolean("use_vault");
        enabledCrazyAdvancementAPI = config.getBoolean("use_crazy_advancement_api");
        enabledMySQL = config.getBoolean("Database.MySQL.enabled");

        successCommands = getStringListsFromSection(config, "success_commands");
        commandWithdraws = getDoublesFromSection(config, "command_withdraws");
        colors = getStringsFromSection(config, "colors");
    }
}
