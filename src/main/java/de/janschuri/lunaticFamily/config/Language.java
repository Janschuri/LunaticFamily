package de.janschuri.lunaticFamily.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.utils.ConfigUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class Language {
    private final File defaultLangFile;
    private final File langFile;
    private static Map<String, String> messages = new HashMap<>();
    private static Map<String, String> genderLang = new HashMap<>();
    public static List<String> genders = new ArrayList<>();
    public static FileConfiguration lang;

    public static String prefix;
    private static Map<String, String> colorsTranslations = new HashMap<>();

    private static final Map<String, Map<String, String>> relationships = new HashMap<>();


    private static final Map<String, Map<String, List<String>>> aliases = new HashMap<>();

    public Language(LunaticFamily plugin) {
        this.defaultLangFile = new File(plugin.getDataFolder().getAbsolutePath() + "/lang/" + Config.language + ".yml");
        this.langFile = new File(plugin.getDataFolder().getAbsolutePath() + "/lang.yml");
        this.load();
    }

    public void load(){

        if (!langFile.exists()) {
            ConfigUtils.addMissingProperties(langFile, defaultLangFile);
        } else {
            ConfigUtils.addMissingProperties(langFile, defaultLangFile);
        }

        lang = YamlConfiguration.loadConfiguration(langFile);

        prefix = ChatColor.translateAlternateColorCodes('&', lang.getString("prefix", "&8[&6LunaticFamily&8] "));

        messages = ConfigUtils.getStringsFromSection(lang, "messages");

        genderLang = ConfigUtils.getStringsFromSection(lang, "genders");

        ConfigurationSection familyRelationships = lang.getConfigurationSection("family_relationships");
        genders = new ArrayList<>(familyRelationships.getKeys(false));

        for (String gender : genders) {
            Map<String, String> map = ConfigUtils.getStringsFromSection(lang, "family_relationships." + gender);

            relationships.put(gender, map);
        }

        List<String> commands = Arrays.asList("family", "marry", "sibling", "adopt", "gender");

        for (String command : commands) {
            Map<String, List<String>> map = ConfigUtils.getStringListsFromSection(lang, "aliases." + command);
            aliases.put(command, map);
        }

        colorsTranslations = ConfigUtils.getStringsFromSection(lang, "color_translations");
    }

    public static String getMessage(String key) {

        if (messages.containsKey(key.toLowerCase())) {
            return ChatColor.translateAlternateColorCodes('&', messages.get(key));
        } else {
            return "Message '" + key.toLowerCase() + "' not found!";
        }
    }
    public static String getGenderLang(String key) {

        if (genderLang.containsKey(key)) {
            return ChatColor.translateAlternateColorCodes('&', genderLang.get(key.toLowerCase()));
        } else {
            return "undefined";
        }
    }

    public static String getColorLang(String key) {

        if (colorsTranslations.containsKey(key)) {
            return ChatColor.translateAlternateColorCodes('&', colorsTranslations.get(key.toLowerCase()));
        } else {
            return "undefined";
        }
    }

    public static List<String> getColorLangs() {
        List<String> list = new ArrayList<>();
        for (String color : Config.colors.keySet()) {
            list.add(Language.getColorLang(color));
        }
        return list;
    }
    public static String getColorKeyFromLang(String key) {

        for (String colorLang : getColorLangs()) {
            if (colorLang.equalsIgnoreCase(key)) {
                BiMap<String, String> colorsTranslations = HashBiMap.create(Language.colorsTranslations);
                return colorsTranslations.inverse().get(colorLang);
            }
        }
        return "#FFFFFF";
    }

    public static boolean isColorLang(String key) {

        for (String colorLang : getColorLangs()) {
            if (colorLang.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }


    public static String getRelation(String relation, String gender) {
        if (genders.contains(gender)) {
            Map<String, String> relations = relationships.get(gender);
            if (relations.get(relation) != null) {
                return ChatColor.translateAlternateColorCodes('&', relations.get(relation));
            } else {
                return "undefined";
            }
        } else {
            gender = genders.get(0);
            Map<String, String> relations = relationships.get(gender);
            if (relations.get(relation) != null) {
                return ChatColor.translateAlternateColorCodes('&', relations.get(relation));
            } else {
                return "undefined";
            }
        }
    }

    public static List<String> getAliases(String command, String subcommand) {
        Map<String, List<String>> commandAliases = aliases.getOrDefault(command, new HashMap<>());

        List<String> subcommandsList = new ArrayList<>();

        List<String> list = commandAliases.getOrDefault(subcommand, new ArrayList<>());

        if (list.isEmpty()) {
            list.add(subcommand);
        }
        subcommandsList.addAll(list);

        return subcommandsList;
    }

    public static List<String> getAliases(String command) {
        return getAliases(command, "basecommand");
    }
}
