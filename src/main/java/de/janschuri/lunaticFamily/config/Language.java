package de.janschuri.lunaticFamily.config;

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

    private static Map<String, Map<String, String>> relationships = new HashMap<>();


    private static final Map<String, Map<String, List<String>>> aliases = new HashMap<>();

    public static List<String> familyCommandsAliases = new ArrayList<>();
    public static List<String> familySubcommandsAliases = new ArrayList<>();
    public static List<String> genderCommandsAliases = new ArrayList<>();
    public static List<String> genderSubcommandsAliases = new ArrayList<>();
    public static List<String> genderAdminSubcommandsAliases = new ArrayList<>();
    public static List<String> familyAdminSubcommandsAliases = new ArrayList<>();
    public static List<String> adoptCommandsAliases = new ArrayList<>();
    public static List<String> adoptSubcommandsAliases = new ArrayList<>();
    public static List<String> adoptAdminSubcommandsAliases = new ArrayList<>();
    public static List<String> marryCommandsAliases = new ArrayList<>();
    public static List<String> marrySubcommandsAliases = new ArrayList<>();
    public static List<String> marryAdminSubcommandsAliases = new ArrayList<>();
    public static List<String> siblingCommandsAliases = new ArrayList<>();
    public static List<String> siblingSubcommandsAliases = new ArrayList<>();
    public static List<String> siblingAdminSubcommandsAliases = new ArrayList<>();

    public Language(LunaticFamily plugin) {
        this.plugin = plugin;
        this.load();
    }

    private void load(){
        plugin.saveResource("lang/EN.yml", true);
        plugin.saveResource("lang/DE.yml", true);

        File langfile = new File(plugin.getDataFolder().getAbsolutePath() + "/lang.yml");

        if (!langfile.exists()) {
            plugin.saveResource("lang.yml", false);
            Utils.addMissingProperties(langfile, "/lang/" + Config.language + ".yml", plugin);
        } else {
            Utils.addMissingProperties(langfile, "/lang/" + Config.language + ".yml", plugin);
        }

        lang = YamlConfiguration.loadConfiguration(langfile);

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

        familyCommandsAliases = getAliases("family");
        familySubcommandsAliases.addAll(getAliases("family", "list", "background", "help"));
        familyAdminSubcommandsAliases.addAll(getAliases("family", "reload"));

        genderCommandsAliases = getAliases("gender");
        genderSubcommandsAliases.addAll(getAliases("gender", "info", "set", "help"));

        marryCommandsAliases = getAliases("marry");
        marrySubcommandsAliases.addAll(getAliases("marry", "propose", "accept", "deny", "divorce", "list", "kiss", "gift", "backpack", "help", "priest"));
        marryAdminSubcommandsAliases.addAll(getAliases("marry", "set", "unset"));

        adoptCommandsAliases = getAliases("adopt");
        adoptSubcommandsAliases.addAll(getAliases("adopt", "propose", "accept", "deny", "kickout", "moveout", "list", "help"));
        adoptAdminSubcommandsAliases.addAll(getAliases("adopt", "set", "unset"));

        siblingCommandsAliases = getAliases("sibling");
        siblingSubcommandsAliases.addAll(getAliases("sibling", "propose", "accept", "deny", "unsibling", "help"));
        siblingAdminSubcommandsAliases.addAll(getAliases("sibling", "set", "unset"));
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
}
