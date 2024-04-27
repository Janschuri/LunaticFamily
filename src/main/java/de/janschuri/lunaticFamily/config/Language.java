package de.janschuri.lunaticFamily.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.nio.file.Path;
import java.util.*;

public class Language extends de.janschuri.lunaticlib.config.Language {
    private static Map<String, String> messages = new HashMap<>();
    private static Map<String, String> genderLang = new HashMap<>();
    public static List<String> genders = new ArrayList<>();
    private static Map<String, String> colorsTranslations = new HashMap<>();
    private static Language instance;

    private static final Map<String, Map<String, String>> relationships = new HashMap<>();


    private static final Map<String, Map<String, List<String>>> aliases = new HashMap<>();

    public Language(Path dataDirectory, List<String> commands) {
        super(dataDirectory, commands, PluginConfig.language);
        instance = this;
        this.load();
    }

    public void load(){
        super.load();

        genderLang = getStringMap("genders");

        genders = getKeys("family_relationships");

        for (String gender : genders) {
            Map<String, String> map = getStringMap("family_relationships." + gender);
            relationships.put(gender, map);
        }

        colorsTranslations = getStringMap("color_translations");
    }
    public static String getGenderLang(String key) {

        if (genderLang.containsKey(key)) {
            return translateAlternateColorCodes('&', genderLang.get(key.toLowerCase()));
        } else {
            return "undefined";
        }
    }

    public static Language getInstance() {
        return instance;
    }

    public static String getColorLang(String key) {

        if (colorsTranslations.containsKey(key)) {
            return translateAlternateColorCodes('&', colorsTranslations.get(key.toLowerCase()));
        } else {
            return "undefined";
        }
    }

    public static List<String> getColorLangs() {
        List<String> list = new ArrayList<>();
        for (String color : PluginConfig.colors.keySet()) {
            list.add(getColorLang(color));
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
                return translateAlternateColorCodes('&', relations.get(relation));
            } else {
                return "undefined";
            }
        } else {
            gender = genders.get(0);
            Map<String, String> relations = relationships.get(gender);
            if (relations.get(relation) != null) {
                return translateAlternateColorCodes('&', relations.get(relation));
            } else {
                return "undefined";
            }
        }
    }
}
