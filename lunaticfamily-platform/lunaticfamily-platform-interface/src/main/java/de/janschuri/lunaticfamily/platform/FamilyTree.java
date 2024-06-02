package de.janschuri.lunaticfamily.platform;

import de.janschuri.lunaticfamily.FamilyPlayer;

import java.util.*;

public interface FamilyTree {

    default boolean update(String server, UUID uuid, int id) {
        FamilyPlayer familyPlayer = getFamilyPlayer(id);
        String background = familyPlayer.getBackground();
        Map<String, Integer> familyMap = familyPlayer.getFamilyMap();
        familyMap.put("ego", id);

        List<String> familyList = new ArrayList<>();
        Map<String, String> names = new HashMap<>();
        Map<String, String> skins = new HashMap<>();
        Map<String, String> relationLangs = new HashMap<>();

        for (Map.Entry<String, Integer> entry : familyMap.entrySet()) {
            FamilyPlayer relationFam = getFamilyPlayer(entry.getValue());
            String relationLang = getRelation(entry.getKey(), relationFam.getGender());

            String skinURL = relationFam.getSkinURL();

            familyList.add(entry.getKey());
            names.put(entry.getKey(), relationFam.getName());
            skins.put(entry.getKey(), skinURL);
            relationLangs.put(entry.getKey(), relationLang);
        }

        return update(server, uuid, background, familyList, names, skins, relationLangs);
    }

    boolean isFamilyTreeMapLoaded();

    boolean loadFamilyTreeMap(String JSONContent);

    boolean update(String server, UUID uuid, String background, List<String> familyList, Map<String, String> names, Map<String, String> skins, Map<String, String> relationLangs);

    FamilyPlayer getFamilyPlayer(int id);

    String getRelation(String relation, String key);

}
