package de.janschuri.lunaticfamily.common.config;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.common.config.LunaticConfigImpl;
import de.janschuri.lunaticlib.common.utils.Mode;

import java.nio.file.Path;
import java.util.ArrayList;
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
    private boolean useProxy = false;
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
        super(dataDirectory, CONFIG_FILE);

    }

    public void load() {
        String defaultPath = (LunaticFamily.getMode() == Mode.PROXY || LunaticFamily.getMode() == Mode.BACKEND) ? "proxyConfig.yml" : "config.yml";
        super.load(defaultPath);

        LunaticFamily.isDebug = getBoolean("debug", false);



        if (LunaticFamily.getMode() == Mode.PROXY) {
            servers = getStringList("servers");
        } else {
            useProxy = getBoolean("use_proxy", false);
            if (useProxy) {
                return;
            }
        }

        useVault = getBoolean("use_vault", false);
        useCrazyAdvancementAPI = getBoolean("use_crazy_advancement_api", false);

        allowSingleAdopt = getBoolean("allow_single_adopt", true);
        defaultBackground = "textures/block/" + getString("default_background", "moss_block") + ".png";
        defaultGender = getString("default_gender", "fe");
        familyList = getStringList("family_list");
        backgrounds = getStringList("backgrounds");
        languageKey = getString("language", "en");
        dateFormat = getString("date_format", "dd.MM.yyyy HH:mm:ss");

        marryKissRange = getDouble("distances.marry_kiss_range", 2.0);
        marryProposeRange = getDouble("distances.marry_propose_range", 2.0);
        marryPriestRange = getDouble("distances.marry_priest_range", 2.0);
        adoptProposeRange = getDouble("distances.adopt_propose_range", 2.0);
        siblingProposeRange = getDouble("distances.sibling_propose_range", 2.0);

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

    public double getAdoptPriestRange() {
        return adoptProposeRange;
    }

    public double getSiblingPriestRange() {
        return siblingProposeRange;
    }

    public double getAdoptProposeRange() {
        return adoptProposeRange;
    }

    public double getSiblingProposeRange() {
        return siblingProposeRange;
    }

    public List<String> getSuccessCommands(String key) {
        if (!successCommands.containsKey(key)) {
            Logger.errorLog("No success commands found for key " + key);
            return new ArrayList<>();
        }
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

    public boolean decisionAsInvGUI() {
        return getBoolean("decision_as_inv_gui", false);
    }

    public String getColor(String key) {
        if (!colors.containsKey(key)) {
            Logger.errorLog("No color found for key " + key);
            return "#FFFFFF";
        }

        return colors.get(key);
    }

    public String getDefaultMarryEmojiColor() {
        String colorString = getString("emoji_colors.married", "#FFFFFF");

        if (Utils.isValidHexCode(colorString)) {
            return colorString;
        }

        if (colors.containsKey(colorString)) {
            return colors.get(colorString);
        }

        Logger.errorLog("Invalid color code or undefined color for married in config.yml. Using default color #FFFFFF.");
        return colorString;
    }

    public String getUnmarriedEmojiColor() {
        String colorString = getString("emoji_colors.unmarried", "#AAAAAA");

        if (Utils.isValidHexCode(colorString)) {
            return colorString;
        }

        if (colors.containsKey(colorString)) {
            return colors.get(colorString);
        }

        Logger.errorLog("Invalid color code or undefined color for unmarried in config.yml. Using default color #AAAAAA.");
        return colorString;
    }


    public String getDefaultAdoptEmojiColor() {
        String colorString = getString("emoji_colors.adopted", "#FFFFFF");

        if (Utils.isValidHexCode(colorString)) {
            return colorString;
        }

        if (colors.containsKey(colorString)) {
            return colors.get(colorString);
        }

        Logger.errorLog("Invalid color code or undefined color for adopted in config.yml. Using default color #FFFFFF.");
        return colorString;
    }

    public String getUnadoptedEmojiColor() {
        String colorString = getString("emoji_colors.unadopted", "#AAAAAA");

        if (Utils.isValidHexCode(colorString)) {
            return colorString;
        }

        if (colors.containsKey(colorString)) {
            return colors.get(colorString);
        }

        Logger.errorLog("Invalid color code or undefined color for unadopted in config.yml. Using default color #AAAAAA.");
        return colorString;
    }

    public String getUnparentEmojiColor() {
        String colorString = getString("emoji_colors.unparent", "#AAAAAA");

        if (Utils.isValidHexCode(colorString)) {
            return colorString;
        }

        if (colors.containsKey(colorString)) {
            return colors.get(colorString);
        }

        Logger.errorLog("Invalid color code or undefined color for unparent in config.yml. Using default color #AAAAAA.");
        return colorString;
    }

    public String getDefaultSiblingEmojiColor() {
        String colorString = getString("emoji_colors.siblinged", "#FFFFFF");

        if (Utils.isValidHexCode(colorString)) {
            return colorString;
        }

        if (colors.containsKey(colorString)) {
            return colors.get(colorString);
        }

        Logger.errorLog("Invalid color code or undefined color for default_heart_color in config.yml. Using default color #FFFFFF.");
        return colorString;
    }

    public String getUnsiblingedEmojiColor() {
        String colorString = getString("emoji_colors.unsiblinged", "#AAAAAA");

        if (Utils.isValidHexCode(colorString)) {
            return colorString;
        }

        if (colors.containsKey(colorString)) {
            return colors.get(colorString);
        }

        Logger.errorLog("Invalid color code or undefined color for unmarried_heart_color in config.yml. Using default color #AAAAAA.");
        return colorString;
    }
}
