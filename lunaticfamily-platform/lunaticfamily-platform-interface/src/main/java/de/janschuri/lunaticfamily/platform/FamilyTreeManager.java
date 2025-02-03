package de.janschuri.lunaticfamily.platform;

import de.janschuri.lunaticfamily.FamilyPlayer;

import java.util.*;

public interface FamilyTreeManager {

    default boolean update(String server, UUID uuid, int id) {
        FamilyPlayer familyPlayer = getFamilyPlayer(id);
        String background = familyPlayer.getBackground();
        Map<Integer, String> familyMap = familyPlayer.getFamilyMap();
        familyMap.put(id, "ego");

        List<String> familyList = new ArrayList<>();
        Map<String, String> names = new HashMap<>();
        Map<String, String> skins = new HashMap<>();
        Map<String, String> relationLangs = new HashMap<>();

        for (Map.Entry<Integer, String> entry : familyMap.entrySet()) {
            FamilyPlayer relationFam = getFamilyPlayer(entry.getKey());
            String relationLang = getRelation(entry.getValue(), relationFam.getGender());

            String skinURL = relationFam.getSkinURL();

            familyList.add(entry.getValue());
            names.put(entry.getValue(), relationFam.getName());
            skins.put(entry.getValue(), skinURL);
            relationLangs.put(entry.getValue(), relationLang);
        }

        return update(server, uuid, background, familyList, names, skins, relationLangs);
    }

    boolean isFamilyTreeMapLoaded();

    boolean loadFamilyTreeMap(String JSONContent);

    boolean update(String server, UUID uuid, String background, List<String> familyList, Map<String, String> names, Map<String, String> skins, Map<String, String> relationLangs);

    FamilyPlayer getFamilyPlayer(int id);

    String getRelation(String relation, String key);

}
