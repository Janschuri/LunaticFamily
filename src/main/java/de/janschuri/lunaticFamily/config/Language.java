package de.janschuri.lunaticFamily.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.utils.Logger;
import de.janschuri.lunaticFamily.utils.LoggingSeverity;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class Language {
    private final LunaticFamily plugin;
    private static Map<String, String> messages = new HashMap<>();
    private static Map<String, String> genderLang = new HashMap<>();
    public static List<String> genders = new ArrayList<>();
    public static FileConfiguration lang;

    public static String prefix;
    private static final BiMap<String, String> colorsTranslations = HashBiMap.create();

    private static final Map<String, Map<String, String>> relationships = new HashMap<>();


    private static final Map<String, Map<String, List<String>>> aliases = new HashMap<>();

    public Language(LunaticFamily plugin) {
        this.plugin = plugin;
        this.load();
    }

    private void load(){
        plugin.saveResource("lang/EN.yml", true);
        plugin.saveResource("lang/DE.yml", true);

        File defaultLangFile = new File(plugin.getDataFolder().getAbsolutePath() + "/lang/" + Config.language + ".yml");
        File langFile = new File(plugin.getDataFolder().getAbsolutePath() + "/lang.yml");

        if (!langFile.exists()) {
            plugin.saveResource("lang.yml", false);
            Utils.addMissingProperties(langFile, defaultLangFile);
        } else {
            Utils.addMissingProperties(langFile, defaultLangFile);
        }

        lang = YamlConfiguration.loadConfiguration(langFile);

        prefix = ChatColor.translateAlternateColorCodes('&', lang.getString("prefix", "&8[&6LunaticFamily&8] "));

        ConfigurationSection messagesSection = lang.getConfigurationSection("messages");
        if (messagesSection != null) {
            for (String key : messagesSection.getKeys(false)) {
                messages.put(key, ChatColor.translateAlternateColorCodes('&', messagesSection.getString(key, key)));
            }
        } else {
            Logger.log("Could not find 'messages' section in lang.yml", LoggingSeverity.WARN);
        }

        ConfigurationSection gendersSection = lang.getConfigurationSection("genders");
        if (gendersSection != null) {
            for (String key : gendersSection.getKeys(false)) {
                genderLang.put(key, ChatColor.translateAlternateColorCodes('&', gendersSection.getString(key, key)));
            }
        } else {
            Logger.log("Could not find 'genders' section in lang.yml", LoggingSeverity.WARN);
        }

        ConfigurationSection familyRelationships = lang.getConfigurationSection("family_relationships");
        genders = new ArrayList<>(familyRelationships.getKeys(false));

        for (String gender : genders) {
            Map<String, String> map = new HashMap<>();

            ConfigurationSection relations = lang.getConfigurationSection("family_relationships." + gender);

            for (String key : relations.getKeys(false)) {


                map.put(key, ChatColor.translateAlternateColorCodes('&', relations.getString(key)));
            }

            relationships.put(gender, map);
        }

        List<String> commands = Arrays.asList("family", "marry", "sibling", "adopt", "gender");

        for (String command : commands) {
            Map<String, List<String>> map = new HashMap<>();
            ConfigurationSection section = lang.getConfigurationSection("aliases." + command);
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    List<String> list = section.getStringList(key);
                    map.put(key, list);
                }
            } else {
                Logger.log("Could not find 'aliases." + command + "' section in lang.yml", LoggingSeverity.WARN);
            }
            aliases.put(command, map);
        }

        ConfigurationSection colorsSection = lang.getConfigurationSection("color_translations");
        if (colorsSection != null) {
            for (String key : colorsSection.getKeys(false)) {
                colorsTranslations.put(key.toLowerCase(), ChatColor.translateAlternateColorCodes('&', colorsSection.getString(key, key)));
            }
        } else {
            Logger.log("Could not find 'colors' section in lang.yml", LoggingSeverity.WARN);
        }


    }

    public static String getMessage(String key) {

        if (messages.containsKey(key.toLowerCase())) {
            return messages.get(key);
        } else {
            return "Message '" + key.toLowerCase() + "' not found!";
        }
    }
    public static String getGenderLang(String key) {

        if (genderLang.containsKey(key)) {
            return genderLang.get(key.toLowerCase());
        } else {
            return "undefined";
        }
    }

    public static String getColorLang(String key) {

        if (colorsTranslations.containsKey(key)) {
            return colorsTranslations.get(key.toLowerCase());
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

        for (String colorLang : colorsTranslations.values()) {
            if (colorLang.equalsIgnoreCase(key)) {
                return colorsTranslations.inverse().get(colorLang);
            }
        }
        return "#FFFFFF";
    }

    public static boolean isColorLang(String key) {

        for (String colorLang : colorsTranslations.values()) {
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
                return relations.get(relation);
            } else {
                return "undefined";
            }
        } else {
            gender = genders.get(0);
            Map<String, String> relations = relationships.get(gender);
            if (relations.get(relation) != null) {
                return relations.get(relation);
            } else {
                return "undefined";
            }
        }
    }

    public static List<String> getAliases(String command, String... subcommands) {
        Map<String, List<String>> commandAliases = aliases.getOrDefault(command, new HashMap<>());

        List<String> subcommandsList = new ArrayList<>();

        for (String subcommand : subcommands) {
            List<String> list = commandAliases.getOrDefault(subcommand, new ArrayList<>());

            if (list.isEmpty()) {
                list.add(subcommand);
            }
            subcommandsList.addAll(list);
        }
        return subcommandsList;
    }

    public static List<String> getAliases(String command) {
        Map<String, List<String>> commandAliases = aliases.getOrDefault(command, new HashMap<>());

        List<String> list = commandAliases.getOrDefault("base_command", new ArrayList<>());
        if (list.isEmpty()) {
            list.add(command);
        }
        return list;
    }

    public static boolean checkIsSubcommand(final String command, final String subcommand, final String arg) {
        return subcommand.equalsIgnoreCase(arg) || Language.getAliases(command, subcommand).stream().anyMatch(element -> arg.equalsIgnoreCase(element));
    }
}
