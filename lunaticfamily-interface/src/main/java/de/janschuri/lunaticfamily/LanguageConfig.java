package de.janschuri.lunaticfamily;

import de.janschuri.lunaticlib.LunaticLanguageConfig;

import java.util.List;
import java.util.Map;

public interface LanguageConfig extends LunaticLanguageConfig {

    String getGenderLang(String key);

    String getColorLang(String key);

    List<String> getColorLangs();

    String getRelation(String relation, String gender);

    List<String> getGenders();

    Map<String, Map<String, String>> getRelationships();
}
