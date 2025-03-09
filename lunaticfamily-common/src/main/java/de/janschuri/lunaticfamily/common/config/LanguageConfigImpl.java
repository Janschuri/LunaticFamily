package de.janschuri.lunaticfamily.common.config;

import de.janschuri.lunaticfamily.FamilyLanguageConfig;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticlib.common.config.LunaticLanguageConfig;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;

public class LanguageConfigImpl extends LunaticLanguageConfig implements FamilyLanguageConfig {
    private Map<String, String> genderLang = new HashMap<>();
    private List<String> genders = new ArrayList<>();
    private Map<String, String> colorsTranslations = new HashMap<>();
    private final Map<String, Map<String, String>> relationships = new HashMap<>();

    private static Map<String, String> remappedRelationships = new LinkedHashMap<>(Map.of(
            "grandchild", "child_child",
            "grandparent", "parent_parent",
            "aunt_or_uncle", "parent_sibling",
            "great_child", "child_child",
            "great_parent", "parent_parent",
            "cousin", "parent_sibling_child",
            "niece_or_nephew", "sibling_child",
            "sibling_in_law", "partner_sibling",
            "partner_in_law", "partner_parent",
            "child_in_law", "child_partner"
    ));

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
        Map<String, String> remappedMap = new HashMap<>(map);

        for (Map.Entry<String, String> entry : map.entrySet()) {
            for (Map.Entry<String, String> remap : remappedRelationships.entrySet()) {
                if (entry.getKey().contains(remap.getKey())) {
                    String newKey = entry.getKey().replace(remap.getKey(), remap.getValue());

                    remappedMap.put(newKey, entry.getValue());
                }
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
