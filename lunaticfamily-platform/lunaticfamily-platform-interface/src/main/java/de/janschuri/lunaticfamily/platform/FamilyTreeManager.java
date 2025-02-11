package de.janschuri.lunaticfamily.platform;

import de.janschuri.lunaticfamily.TreeAdvancement;

import java.util.List;
import java.util.UUID;

public interface FamilyTreeManager {


    boolean update(String server, UUID uuid, List<TreeAdvancement> treeAdvancements);

}
