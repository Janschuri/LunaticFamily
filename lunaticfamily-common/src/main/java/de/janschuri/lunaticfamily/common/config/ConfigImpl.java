package de.janschuri.lunaticfamily.common.config;

import de.janschuri.lunaticfamily.FamilyConfig;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.ConfigKey;
import de.janschuri.lunaticlib.common.config.LunaticConfig;
import de.janschuri.lunaticlib.common.config.LunaticConfigKey;
import de.janschuri.lunaticlib.common.utils.Mode;

import java.nio.file.Path;
import java.util.*;

public class ConfigImpl extends LunaticConfig implements FamilyConfig {
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
    private Set<String> familyList = new LinkedHashSet<>();
    private List<String> backgrounds;
    private Map<String, Double> commandWithdraws = new HashMap<>();
    private Map<String, String> colors = new HashMap<>();

    private ConfigKey<Integer> siblingLimitCK = new LunaticConfigKey<Integer>("sibling_limit")
            .defaultValue(1)
            .keyBlockComment("The maximum number of siblings a player can have. -1 means unlimited.");

    private ConfigKey<Integer> adoptLimitCK = new LunaticConfigKey<Integer>("adopt_limit")
            .defaultValue(2)
            .keyBlockComment("The maximum children a player can adopt. -1 means unlimited.");

    private ConfigKey<String> coloredEmojiPatter = new LunaticConfigKey<String>("colored_emoji_pattern")
            .defaultValue("<%hexcolor%>%emoji%")
            .keyBlockComment("The pattern for colored emojis in placeholders. Use %hexcolor% for the color and %emoji% for the emoji.");

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
        List<String> familyList = getStringList("family_list");
        this.familyList = getRemappedFamilyList(familyList);
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

    private static Set<String> getRemappedFamilyList(List<String> familyList) {
        Set<String> remappedFamilyList = new LinkedHashSet<>();

        for (String relation : familyList) {
            String remappedRelation = relation
                    .toLowerCase()
                    .replace("first_", "")
                    .replace("second_", "")
                    .replace("third_", "")
                    .replace("fourth_", "")
                    .replace("fifth_", "")
                    .replace("sixth_", "")
                    .replace("seventh_", "")
                    .replace("eighth_", "");

            for (Map.Entry<String, String> remap : ConfigImpl.getRemappedRelationships().entrySet()) {
                    remappedRelation = remappedRelation.replaceAll(remap.getKey(), remap.getValue());
            }

            if (remappedRelation.contains("sibling_in_law")) {
                String newKey1 = remappedRelation.replaceAll("sibling_in_law", "sibling_partner");
                String newKey2 = remappedRelation.replaceAll("sibling_in_law", "partner_sibling");

                remappedFamilyList.add(newKey1);
                remappedFamilyList.add(newKey2);
            } else {
                remappedFamilyList.add(remappedRelation);
            }
        }

        return remappedFamilyList;
    }

    public static Map<String, String> getRemappedRelationships() {
        Map<String, String> remappedRelationships = new LinkedHashMap<>();

        remappedRelationships.put("grandchild", "child_child");
        remappedRelationships.put("grandparent", "parent_parent");
        remappedRelationships.put("aunt_or_uncle", "parent_sibling");
        remappedRelationships.put("great_child", "child_child");
        remappedRelationships.put("great_parent", "parent_parent");
        remappedRelationships.put("cousin", "parent_sibling_child");
        remappedRelationships.put("niece_or_nephew", "sibling_child");
        remappedRelationships.put("partner_in_law", "partner_parent");
        remappedRelationships.put("child_in_law", "child_partner");
        remappedRelationships.put("parent_in_law", "partner_parent");

        return remappedRelationships;
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

    public Set<String> getFamilyList() {
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

    public int getSiblingLimit() {
        return getInt("sibling_limit", siblingLimitCK.getDefault());
    }

    public int getAdoptLimit() {
        return getInt("adopt_limit", adoptLimitCK.getDefault());
    }

    public String getColoredEmojiPattern() {
        return getString("colored_emoji_pattern", coloredEmojiPatter.getDefault());
    }
}
