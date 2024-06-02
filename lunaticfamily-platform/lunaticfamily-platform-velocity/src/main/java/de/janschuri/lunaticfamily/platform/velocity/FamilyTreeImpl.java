package de.janschuri.lunaticfamily.platform.velocity;

import de.janschuri.lunaticfamily.FamilyPlayer;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.futurerequests.UpdateFamilyTreeRequest;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.platform.FamilyTree;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FamilyTreeImpl implements FamilyTree {


    @Override
    public boolean isFamilyTreeMapLoaded() {
        return true;
    }

    @Override
    public boolean loadFamilyTreeMap(String JSONContent) {
        return true;
    }


    @Override
    public boolean update(String server, UUID uuid, String background, List<String> familyList, Map<String, String> names, Map<String, String> skins, Map<String, String> relationLangs) {
        return new UpdateFamilyTreeRequest().get(server, uuid, background, familyList, names, skins, relationLangs);
    }

    @Override
    public FamilyPlayer getFamilyPlayer(int id) {
        return new FamilyPlayerImpl(id);
    }

    @Override
    public String getRelation(String relation, String key) {
        return LunaticFamily.getLanguageConfig().getRelation(relation, key);
    }
}
