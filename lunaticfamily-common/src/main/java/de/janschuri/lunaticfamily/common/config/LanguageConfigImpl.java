package de.janschuri.lunaticfamily.common.config;

import de.janschuri.lunaticfamily.FamilyLanguageConfig;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.common.config.LunaticLanguageConfig;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;

public class LanguageConfigImpl extends LunaticLanguageConfig implements FamilyLanguageConfig {
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
            Map<String, String> remappedMap = getRemappedMap(map);

            relationships.put(gender, remappedMap);
        }

        colorsTranslations = getStringMap("color_translations");
    }

    private static @NotNull Map<String, String> getRemappedMap(Map<String, String> map) {
        Map<String, String> remappedMap = new HashMap<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {

            String newKey = entry.getKey().toLowerCase();
            for (Map.Entry<String, String> remap : ConfigImpl.getRemappedRelationships().entrySet()) {
                 newKey = newKey.replaceAll(remap.getKey(), remap.getValue());
            }


            if (entry.getKey().contains("sibling_in_law")) {
                Logger.debugLog("Key: " + entry.getKey() + " -> " + newKey);
                String newKey1 = newKey.replaceAll("sibling_in_law", "sibling_partner");
                String newKey2 = newKey.replaceAll("sibling_in_law", "partner_sibling");

                remappedMap.put(newKey1, entry.getValue());
                remappedMap.put(newKey2, entry.getValue());

            } else {
                remappedMap.put(newKey, entry.getValue());
            }

        }

        return remappedMap;
    }

    @Override
    protected String getPackage() {
        return "de.janschuri.lunaticfamily.common";
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

    public String getRelation(String relation, String gender) {
        String relationKey = relation.replaceAll("_\\d+", "");

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

    public List<String> getGenders() {
        return genders;
    }

    public Map<String, Map<String, String>> getRelationships() {
        return relationships;
    }

    public String getGenderEmoji(String gender) {
        String emoji = getString("gender_emoji." + gender);
        return Objects.requireNonNullElse(emoji, "undefined");
    }
}
