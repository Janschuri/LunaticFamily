package de.janschuri.lunaticfamily.platform.bungee;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.futurerequests.UpdateFamilyTreeRequest;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.platform.FamilyTree;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FamilyTreeImpl implements FamilyTree {

    private static String JSONContent = null;


    @Override
    public void loadJSONContent() {

    }

    @Override
    public String getJSONContent() {
        if (JSONContent == null) {
            loadJSONContent();
        }

        return JSONContent;
    }

    @Override
    public boolean update(UUID uuid, String background, List<String> familyList, Map<String, String> names, Map<String, String> skins, Map<String, String> relationLangs) {
        return new UpdateFamilyTreeRequest().get(uuid, background, familyList, names, skins, relationLangs);
    }

    @Override
    public FamilyPlayerImpl getFamilyPlayer(int id) {
        return new FamilyPlayerImpl(id);
    }

    @Override
    public String getRelation(String relation, String key) {
        return LunaticFamily.getLanguageConfig().getRelation(relation, key);
    }
}
