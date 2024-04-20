package de.janschuri.lunaticFamily.config;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginConfig extends Config {
    private static final String CONFIG_FILE = "config.yml";
    public static boolean isDebug;
    public static boolean useProxy;
    public static String language;
    public static String defaultGender;
    public static String defaultBackground;
    public static boolean allowSingleAdopt;
    public static boolean enabledCrazyAdvancementAPI;
    public static boolean enabledVault;
    public static String dateFormat;
    public static double marryKissRange;
    public static double marryProposeRange;
    public static double marryPriestRange;
    public static double adoptProposeRange;
    public static double siblingProposeRange;
    public static Map<String, List<String>> successCommands = new HashMap<>();
    public static List<String> familyList;
    public static List<String> backgrounds;
    public static Map<String, Double> commandWithdraws = new HashMap<>();
    public static Map<String, String> colors = new HashMap<>();

    public PluginConfig(Path dataDirectory) {
        super(dataDirectory, CONFIG_FILE);
        this.load();
    }

    public void load() {
        super.load();

        allowSingleAdopt = getBoolean("allow_single_adopt");
        defaultBackground = "textures/block/" + getString("default_background") + ".png";
        defaultGender = getString("default_gender");
        familyList = getStringList("family_list");
        backgrounds = getStringList("backgrounds");
        language = getString("language");
        dateFormat = getString("date_format");

        marryKissRange = getDouble("distances.marry_kiss_range");
        marryProposeRange = getDouble("distances.marry_propose_range");
        marryPriestRange = getDouble("distances.marry_priest_range");
        adoptProposeRange = getDouble("distances.adopt_propose_range");
        siblingProposeRange = getDouble("distances.sibling_propose_range");

        isDebug = getBoolean("is_debug", true);
        useProxy = getBoolean("use_proxy", false);

        enabledVault = getBoolean("use_vault");
        enabledCrazyAdvancementAPI = getBoolean("use_crazy_advancement_api");

        successCommands = getStringListMap("success_commands");
        commandWithdraws = getDoubleMap("command_withdraws");
        colors = getStringMap("colors");
    }
}
