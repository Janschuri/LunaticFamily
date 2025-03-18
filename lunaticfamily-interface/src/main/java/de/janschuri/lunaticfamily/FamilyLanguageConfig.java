package de.janschuri.lunaticfamily;

import java.util.List;
import java.util.Map;

public interface FamilyLanguageConfig {

    String getGenderLang(String key);

    String getColorLang(String key);

    List<String> getColorLangs();

    String getRelation(String relation, String gender);

    List<String> getGenders();

    Map<String, Map<String, String>> getRelationships();
}
