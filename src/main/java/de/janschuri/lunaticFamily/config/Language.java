package de.janschuri.lunaticFamily.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Language extends de.janschuri.lunaticlib.config.Language {
    private Map<String, String> genderLang = new HashMap<>();
    private List<String> genders = new ArrayList<>();
    private Map<String, String> colorsTranslations = new HashMap<>();
    private static Language instance;
    private final Map<String, Map<String, String>> relationships = new HashMap<>();

    public Language(Path dataDirectory, String[] commands) {
        super(dataDirectory, commands, PluginConfig.getLanguageKey());
        instance = this;
        load();
    }

    public void load() {
        super.load();

        genderLang = getStringMap("genders");

        genders = getKeys("family_relationships");

        for (String gender : genders) {
            Map<String, String> map = getStringMap("family_relationships." + gender);
            relationships.put(gender, map);
        }

        colorsTranslations = getStringMap("color_translations");
    }

    public static Language getLanguage() {
        return instance;
    }

    public static String getGenderLang(String key) {
        if (getLanguage().genderLang.containsKey(key)) {
            return translateAlternateColorCodes('&', getLanguage().genderLang.get(key.toLowerCase()));
        } else {
            return "undefined";
        }
    }

    public static String getColorLang(String key) {

        if (getLanguage().colorsTranslations.containsKey(key)) {
            return translateAlternateColorCodes('&', getLanguage().colorsTranslations.get(key.toLowerCase()));
        } else {
            return "undefined";
        }
    }

    public static List<String> getColorLangs() {
        List<String> list = new ArrayList<>();
        for (String color : PluginConfig.getColors().keySet()) {
            list.add(getColorLang(color));
        }
        return list;
    }
    public static String getColorKeyFromLang(String key) {

        for (String colorLang : getColorLangs()) {
            if (colorLang.equalsIgnoreCase(key)) {
                BiMap<String, String> colorsTranslations = HashBiMap.create(getLanguage().colorsTranslations);
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
        String relationKey = relation
                .replace("first_", "")
                .replace("second_", "")
                .replace("third_", "")
                .replace("fourth_", "")
                .replace("fifth_", "")
                .replace("sixth_", "")
                .replace("seventh__", "")
                .replace("eighth_", "");


        if (getLanguage().genders.contains(gender)) {
            Map<String, String> relations = getLanguage().relationships.get(gender);
            if (relations.get(relationKey) != null) {
                return translateAlternateColorCodes('&', relations.get(relationKey));
            } else {
                return "undefined";
            }
        } else {
            gender = getLanguage().genders.get(0);
            Map<String, String> relations = getLanguage().relationships.get(gender);
            if (relations.get(relationKey) != null) {
                return translateAlternateColorCodes('&', relations.get(relationKey));
            } else {
                return "undefined";
            }
        }
    }

    public static Map<String, String> getGenderLang() {
        return getLanguage().genderLang;
    }

    public static List<String> getGenders() {
        return getLanguage().genders;
    }

    public static Map<String, String> getColorsTranslations() {
        return getLanguage().colorsTranslations;
    }

    public static Map<String, Map<String, String>> getRelationships() {
        return getLanguage().relationships;
    }
}
