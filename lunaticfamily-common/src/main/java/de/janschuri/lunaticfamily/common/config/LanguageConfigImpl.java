package de.janschuri.lunaticfamily.common.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticfamily.LanguageConfig;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticlib.common.config.LunaticLanguageConfigImpl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageConfigImpl extends LunaticLanguageConfigImpl implements LanguageConfig {
    private Map<String, String> genderLang = new HashMap<>();
    private List<String> genders = new ArrayList<>();
    private Map<String, String> colorsTranslations = new HashMap<>();
    private final Map<String, Map<String, String>> relationships = new HashMap<>();

    public LanguageConfigImpl(Path dataDirectory, String languageKey) {
        super(dataDirectory, languageKey);
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

    public String getGenderLang(String key) {
        if (genderLang.containsKey(key)) {
            return translateAlternateColorCodes('&', genderLang.get(key.toLowerCase()));
        } else {
            return "undefined";
        }
    }

    public String getColorLang(String key) {

        if (colorsTranslations.containsKey(key)) {
            return translateAlternateColorCodes('&', colorsTranslations.get(key.toLowerCase()));
        } else {
            return "undefined";
        }
    }

    public List<String> getColorLangs() {
        List<String> list = new ArrayList<>();
        for (String color : LunaticFamily.getConfig().getColors().keySet()) {
            list.add(getColorLang(color));
        }
        return list;
    }
    public String getColorKeyFromLang(String key) {
        for (String colorLang : getColorLangs()) {
            if (colorLang.equalsIgnoreCase(key)) {
                BiMap<String, String> inverseColorsTranslations = HashBiMap.create(colorsTranslations).inverse();
                return inverseColorsTranslations.get(colorLang);
            }
        }
        return "#FFFFFF";
    }

    public boolean isColorLang(String key) {

        for (String colorLang : getColorLangs()) {
            if (colorLang.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }


    public String getRelation(String relation, String gender) {
        String relationKey = relation
                .replace("first_", "")
                .replace("second_", "")
                .replace("third_", "")
                .replace("fourth_", "")
                .replace("fifth_", "")
                .replace("sixth_", "")
                .replace("seventh__", "")
                .replace("eighth_", "");


        if (genders.contains(gender)) {
            Map<String, String> relations = relationships.get(gender);
            if (relations.get(relationKey) != null) {
                return translateAlternateColorCodes('&', relations.get(relationKey));
            } else {
                return "undefined";
            }
        } else {
            gender = genders.get(0);
            Map<String, String> relations = relationships.get(gender);
            if (relations.get(relationKey) != null) {
                return translateAlternateColorCodes('&', relations.get(relationKey));
            } else {
                return "undefined";
            }
        }
    }

    public Map<String, String> getGenderLang() {
        return genderLang;
    }

    public List<String> getGenders() {
        return genders;
    }

    public Map<String, String> getColorsTranslations() {
        return colorsTranslations;
    }

    public Map<String, Map<String, String>> getRelationships() {
        return relationships;
    }
}
