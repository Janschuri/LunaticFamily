package de.janschuri.lunaticfamily;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface FamilyPlayer {

    String getName();

    int getId();

    UUID getUniqueId();

    String getSkinURL();

    String getGender();

    String getBackground();

    boolean isFamilyMember(int id);

    Map<Integer, String> getFamilyMap();

    boolean updateFamilyTree();
}
