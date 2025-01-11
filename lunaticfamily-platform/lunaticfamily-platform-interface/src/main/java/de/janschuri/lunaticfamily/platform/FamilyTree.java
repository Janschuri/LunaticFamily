package de.janschuri.lunaticfamily.platform;

import de.janschuri.lunaticfamily.FamilyPlayer;

import java.util.*;

public interface FamilyTree {

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

        List<FamilyRelation> partners = new ArrayList<>();
        List<FamilyRelation> siblings = new ArrayList<>();
        List<FamilyRelation> parents = new ArrayList<>();
        List<FamilyRelation> children = new ArrayList<>();

        FamilyRelation ego = new FamilyRelation(
                "ego",
                "ego",
                familyPlayer.getName(),
                familyPlayer.getSkinURL(),
                children,
                parents,
                siblings,
                partners
        );

        return update(server, uuid, background, ego);
    }

    boolean isFamilyTreeMapLoaded();

    boolean loadFamilyTreeMap(String JSONContent);

    boolean update(String server, UUID uuid, String background, FamilyRelation familyRelation);

    FamilyPlayer getFamilyPlayer(int id);

    String getRelation(String relation, String key);

}
