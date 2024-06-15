package de.janschuri.lunaticfamily.common.config;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticlib.common.config.LunaticConfigImpl;
import de.janschuri.lunaticlib.common.utils.Mode;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigImpl extends LunaticConfigImpl implements de.janschuri.lunaticfamily.Config {
    private static final String CONFIG_FILE = "config.yml";
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

    public ConfigImpl(Path dataDirectory) {
        super(dataDirectory, CONFIG_FILE, (LunaticFamily.getMode() == Mode.PROXY || LunaticFamily.getMode() == Mode.BACKEND) ? "proxyConfig.yml" : "config.yml");
    }

    public void load() {
        super.load();

        LunaticFamily.isDebug = getBoolean("debug", false);
        useProxy = getBoolean("use_proxy", false);

        if (useProxy && LunaticFamily.getMode() != Mode.PROXY) {
            return;
        }

        if (LunaticFamily.getMode() == Mode.PROXY) {
            servers = getStringList("servers");
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
    }

    public String getLanguageKey() {
        return languageKey;
    }

    public String getDefaultGender() {
        return defaultGender;
    }

    public String getDefaultBackground() {
        return defaultBackground;
    }

    public boolean isAllowSingleAdopt() {
        return allowSingleAdopt;
    }

    public List<String> getServers() {
        return servers;
    }

    public boolean isUseCrazyAdvancementAPI() {
        return useCrazyAdvancementAPI;
    }

    public boolean isUseVault() {
        return useVault;
    }

    public boolean isUseProxy() {
        return useProxy;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public double getMarryKissRange() {
        return marryKissRange;
    }

    public double getMarryProposeRange() {
        return marryProposeRange;
    }

    public double getMarryPriestRange() {
        return marryPriestRange;
    }

    public double getAdoptProposeRange() {
        return adoptProposeRange;
    }

    public double getSiblingProposeRange() {
        return siblingProposeRange;
    }

    public List<String> getSuccessCommands(String key) {
        return successCommands.get(key);
    }

    public List<String> getFamilyList() {
        return familyList;
    }

    public List<String> getBackgrounds() {
        return backgrounds;
    }

    public Double getCommandWithdraw(String key) {
        return commandWithdraws.get(key);
    }

    public Map<String, String> getColors() {
        return colors;
    }

    public String getColor(String key) {
        return colors.get(key);
    }
}
