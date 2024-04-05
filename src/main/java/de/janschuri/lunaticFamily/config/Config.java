package de.janschuri.lunaticFamily.config;

import com.tchristofferson.configupdater.ConfigUpdater;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.utils.Logger;
import de.janschuri.lunaticFamily.utils.LoggingSeverity;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

        File cfgFile = new File(plugin.getDataFolder().getAbsolutePath() + "/config.yml");

        try {
            ConfigUpdater.update(plugin, "config.yml", cfgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        config = YamlConfiguration.loadConfiguration(cfgFile);

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
