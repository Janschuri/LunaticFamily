package de.janschuri.lunaticfamily.platform.velocity;

import de.janschuri.lunaticfamily.FamilyPlayer;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.futurerequests.UpdateFamilyTreeRequest;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.platform.FamilyTreeManager;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FamilyTreeManagerImpl implements FamilyTreeManager {

    @Override
    public boolean update(String server, int familyPlayerID) {
        return new UpdateFamilyTreeRequest().get(server, familyPlayerID);
    }
}
