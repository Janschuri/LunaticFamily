package de.janschuri.lunaticfamily.platform;

import de.janschuri.lunaticfamily.TreeAdvancement;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface FamilyTreeManager {


    CompletableFuture<Boolean> update(String server, UUID uuid, List<TreeAdvancement> treeAdvancements);

}
