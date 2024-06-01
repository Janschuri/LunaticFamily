package de.janschuri.lunaticfamily.common.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticlib.common.config.LanguageConfig;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageConfigImpl extends LanguageConfig {
    private Map<String, String> genderLang = new HashMap<>();
    private List<String> genders = new ArrayList<>();
    private Map<String, String> colorsTranslations = new HashMap<>();
    private final Map<String, Map<String, String>> relationships = new HashMap<>();

    protected static final String NO_NUMBER = "no_number";
    protected static final String WRONG_USAGE = "wrong_usage";
    protected static final String PLAYER_NOT_EXIST = "player_not_exist";
    protected static final String NOT_ENOUGH_MONEY = "not_enough_money";
    protected static final String PLAYER_NOT_ENOUGH_MONEY = "player_not_enough_money";
    protected static final String WITHDRAW = "withdraw";
    protected static final String PLAYER_QUIT = "player_quit";
    protected static final String NO_CONSOLE_COMMAND = "no_console_command";
    protected static final String PLAYER_TOO_FAR_AWAY = "player_too_far_away";
    protected static final String PLAYER_OFFLINE = "player_offline";
    protected static final String PLAYER_NOT_SAME_SERVER = "player_not_same_server";
    protected static final String NO_PERMISSION = "no_permission";
    protected static final String DISABLED_FEATURE = "disabled_feature";
    protected static final String TAKE_PAYMENT_CONFIRM = "take_payment_confirm";
    protected static final String PLAYER_NOT_ON_WHITELISTED_SERVER = "player_not_on_whitelisted_server";
    protected static final String YES = "yes";
    protected static final String NO = "no";
    protected static final String ACCEPT = "accept";
    protected static final String DENY = "deny";
    protected static final String CONFIRM = "confirm";
    protected static final String CANCEL = "cancel";
    protected static final String GENDER = "gender";
    protected static final String PLAYER_NAME = "player_name";
    protected static final String COLOR = "color";

    public LanguageConfigImpl(Path dataDirectory, String[] commands) {
        super(dataDirectory, LunaticFamily.getConfig().getLanguageKey());
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
