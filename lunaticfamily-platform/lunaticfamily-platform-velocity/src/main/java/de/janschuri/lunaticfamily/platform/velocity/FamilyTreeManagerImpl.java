package de.janschuri.lunaticfamily.platform.velocity;

import de.janschuri.lunaticfamily.TreeAdvancement;
import de.janschuri.lunaticfamily.common.futurerequests.UpdateFamilyTreeRequest;
import de.janschuri.lunaticfamily.platform.FamilyTreeManager;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class FamilyTreeManagerImpl implements FamilyTreeManager {

    @Override
    public CompletableFuture< Boolean> update(String server, UUID uuid, List<TreeAdvancement> treeAdvancements) {
        return new UpdateFamilyTreeRequest().get(server, uuid, treeAdvancements);
    }
}
