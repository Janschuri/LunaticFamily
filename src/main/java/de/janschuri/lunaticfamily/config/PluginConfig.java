package de.janschuri.lunaticfamily.config;

import de.janschuri.lunaticfamily.LunaticFamily;
import de.janschuri.lunaticlib.config.Config;
import de.janschuri.lunaticlib.utils.Mode;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginConfig extends Config {
    private static final String CONFIG_FILE = "config.yml";
    private static PluginConfig instance;
    private String languageKey;
    private String defaultGender;
    private String defaultBackground;
    private boolean allowSingleAdopt;
    private List<String> servers;
    private boolean useCrazyAdvancementAPI;
    private boolean useVault;
    private boolean useProxy;
    private String dateFormat;
    private double marryKissRange;
    private double marryProposeRange;
    private double marryPriestRange;
    private double adoptProposeRange;
    private double siblingProposeRange;
    private Map<String, List<String>> successCommands = new HashMap<>();
    private List<String> familyList;
    private List<String> backgrounds;
    private Map<String, Double> commandWithdraws = new HashMap<>();
    private Map<String, String> colors = new HashMap<>();

    public PluginConfig(Path dataDirectory) {
        super(dataDirectory, CONFIG_FILE, (LunaticFamily.getMode() == Mode.PROXY || LunaticFamily.getMode() == Mode.BACKEND) ? "proxyConfig.yml" : "config.yml");
        instance = this;
        load();
    }

    public void load() {
        super.load();

        LunaticFamily.isDebug = getBoolean("debug", false);
        useProxy = getBoolean("use_proxy", false);

        if (useProxy && LunaticFamily.getMode() != Mode.PROXY) {
            return;
        }

        useVault = getBoolean("use_vault");
        useCrazyAdvancementAPI = getBoolean("use_crazy_advancement_api");

        allowSingleAdopt = getBoolean("allow_single_adopt");
        defaultBackground = "textures/block/" + getString("default_background") + ".png";
        defaultGender = getString("default_gender");
        familyList = getStringList("family_list");
        backgrounds = getStringList("backgrounds");
        languageKey = getString("language");
        dateFormat = getString("date_format");

        marryKissRange = getDouble("distances.marry_kiss_range");
        marryProposeRange = getDouble("distances.marry_propose_range");
        marryPriestRange = getDouble("distances.marry_priest_range");
        adoptProposeRange = getDouble("distances.adopt_propose_range");
        siblingProposeRange = getDouble("distances.sibling_propose_range");

        successCommands = getStringListMap("success_commands");
        commandWithdraws = getDoubleMap("command_withdraws");
        colors = getStringMap("colors");

        servers = getStringList("servers");
    }

    public static PluginConfig getConfig() {
        return instance;
    }

    public static String getLanguageKey() {
        return getConfig().languageKey;
    }

    public static String getDefaultGender() {
        return getConfig().defaultGender;
    }

    public static String getDefaultBackground() {
        return getConfig().defaultBackground;
    }

    public static boolean isAllowSingleAdopt() {
        return getConfig().allowSingleAdopt;
    }

    public static List<String> getServers() {
        return getConfig().servers;
    }

    public static boolean isUseCrazyAdvancementAPI() {
        return getConfig().useCrazyAdvancementAPI;
    }

    public static boolean isUseVault() {
        return getConfig().useVault;
    }

    public static boolean isUseProxy() {
        return getConfig().useProxy;
    }

    public static String getDateFormat() {
        return getConfig().dateFormat;
    }

    public static double getMarryKissRange() {
        return getConfig().marryKissRange;
    }

    public static double getMarryProposeRange() {
        return getConfig().marryProposeRange;
    }

    public static double getMarryPriestRange() {
        return getConfig().marryPriestRange;
    }

    public static double getAdoptProposeRange() {
        return getConfig().adoptProposeRange;
    }

    public static double getSiblingProposeRange() {
        return getConfig().siblingProposeRange;
    }

    public static List<String> getSuccessCommands(String key) {
        return getConfig().successCommands.get(key);
    }

    public static List<String> getFamilyList() {
        return getConfig().familyList;
    }

    public static List<String> getBackgrounds() {
        return getConfig().backgrounds;
    }

    public static Double getCommandWithdraw(String key) {
        return getConfig().commandWithdraws.get(key);
    }

    public static Map<String, String> getColors() {
        return getConfig().colors;
    }

    public static String getColor(String key) {
        return getConfig().colors.get(key);
    }
}
