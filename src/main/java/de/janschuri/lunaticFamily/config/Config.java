package de.janschuri.lunaticFamily.config;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.utils.Logger;
import de.janschuri.lunaticFamily.utils.LoggingSeverity;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Config {
    private final LunaticFamily plugin;
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

    public Config(LunaticFamily plugin) {
        this.plugin = plugin;
        this.load();
    }

    public void load() {

        File cfgfile = new File(plugin.getDataFolder().getAbsolutePath() + "/config.yml");
        plugin.saveResource("defaultConfig.yml", true);

        if (!cfgfile.exists()) {
            plugin.saveResource("/config.yml", false);
            Utils.addMissingProperties(cfgfile, "defaultConfig.yml", plugin);
        } else {
            Utils.addMissingProperties(cfgfile, "defaultConfig.yml", plugin);
        }

        config = YamlConfiguration.loadConfiguration(cfgfile);

        allowSingleAdopt = config.getBoolean("allow_single_adopt");
        marryBackpackOffline = config.getBoolean("marry_backpack_offline_access");
        defaultBackground = "textures/block/" + config.getString("default_background") + ".png";
        defaultGender = config.getString("default_gender");
        familyList = Objects.requireNonNull(config.getStringList("family_list"));
        backgrounds = Objects.requireNonNull(config.getStringList("backgrounds"));
        language = config.getString("language");
        dateFormat = config.getString("date_format");
        marryKissRange = config.getDouble("marry_kiss_range");

        ConfigurationSection successCommandsSection = config.getConfigurationSection("success_commands");
        if (successCommandsSection != null) {
            for (String key : successCommandsSection.getKeys(false)) {
                successCommands.put(key, successCommandsSection.getStringList(key));
            }
        } else {
            Logger.log("Could not find 'success_commands' section in config.yml", LoggingSeverity.WARN);
        }

        isDebug = config.getBoolean("is_debug");

        enabledMinepacks = config.getBoolean("use_minepacks");
        enabledVault = config.getBoolean("use_vault");
        enabledCrazyAdvancementAPI = config.getBoolean("use_crazy_advancement_api");
        enabledMySQL = config.getBoolean("Database.MySQL.enabled");


        ConfigurationSection withdrawsSection = config.getConfigurationSection("command_withdraws");
        if (withdrawsSection != null) {
            for (String key : withdrawsSection.getKeys(false)) {
                commandWithdraws.put(key, withdrawsSection.getDouble(key, 0.0));
            }
        } else {
            Logger.log("Could not find 'command_withdraw' section in config.yml", LoggingSeverity.WARN);
        }
    }
}
