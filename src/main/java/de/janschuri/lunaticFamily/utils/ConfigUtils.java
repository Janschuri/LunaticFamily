package de.janschuri.lunaticFamily.utils;

import de.janschuri.lunaticFamily.utils.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigUtils {

    public static void addMissingProperties(File file, File defaultFile) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultFile);

        YamlConfiguration newConfig = new YamlConfiguration();

        Set<String> keys = config.getKeys(true);


        for (String key : defaultConfig.getKeys(true)) {
            if (!config.contains(key)) {
                newConfig.set(key, defaultConfig.get(key));

                List<String> comments = defaultConfig.getComments(key);
                if (!comments.isEmpty()) {
                    newConfig.setComments(key, comments);
                }
            } else {
                newConfig.set(key, config.get(key));

                List<String> defaultComments = defaultConfig.getComments(key);
                List<String> configComments = config.getComments(key);
                List<String> comments = new ArrayList<>();

                if (!new HashSet<>(configComments).containsAll(defaultComments)) {
                    comments.addAll(defaultComments);
                }

                comments.addAll(configComments);

                if (!comments.isEmpty()) {
                    newConfig.setComments(key, comments);
                }

                keys.remove(key);
            }
        }

        // Transfer remaining properties without comments
        for (String key : keys) {
            newConfig.set(key, config.get(key));
        }

        try {
            // Save the merged configuration with comments
            newConfig.save(file);
        } catch (IOException e) {
            Logger.errorLog("Could not save file: " + file.getName());
            e.printStackTrace();
        }
    }

    public static Map<String, String> getStringsFromSection(FileConfiguration config, String sectionName) {
        ConfigurationSection section = config.getConfigurationSection(sectionName);
        Map<String, String> map = new HashMap<>();
        if (section != null) {
            for (String key : section.getKeys(false)) {
                map.put(key, section.getString(key));
            }
        } else {
            Logger.warnLog("Could not find '" + sectionName + "' in config.yml");
        }
        return map;
    }

    public static Map<String, Double> getDoublesFromSection(FileConfiguration config, String sectionName) {
        ConfigurationSection section = config.getConfigurationSection(sectionName);
        Map<String, Double> map = new HashMap<>();
        if (section != null) {
            for (String key : section.getKeys(false)) {
                map.put(key, section.getDouble(key));
            }
        } else {
            Logger.warnLog("Could not find '" + sectionName + "' in config.yml");
        }
        return map;
    }

    public static Map<String, List<String>> getStringListsFromSection(FileConfiguration config, String sectionName) {
        ConfigurationSection section = config.getConfigurationSection(sectionName);
        Map<String, List<String>> map = new HashMap<>();
        if (section != null) {
            for (String key : section.getKeys(false)) {
                map.put(key, section.getStringList(key));
            }
        } else {
            Logger.warnLog("Could not find '" + sectionName + "' in config.yml");
        }
        return map;
    }
}
