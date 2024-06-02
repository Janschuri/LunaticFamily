package de.janschuri.lunaticfamily;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface FamilyPlayer {

    String getName();

    int getId();

    UUID getUniqueId();

    String getSkinURL();

    void setHeartColor(String color);

    String getHeartColor();

    FamilyPlayer getPartner();

    boolean isMarried();

    String getMarriageDate();

    FamilyPlayer getPriest();

    FamilyPlayer getSibling();

    boolean hasSibling();

    boolean isAdopted();

    boolean isChildOf(int parentID);

    List<FamilyPlayer> getParents();

    List<FamilyPlayer> getChildren();

    String getGender();

    void setGender(String gender);

    String getBackground();

    void setBackground(String background);

    Integer getChildrenAmount();


    void marry(int partnerID);

    void marry(int partnerID, int priest);

    void divorce();

    void adopt(int childID);

    void unadopt(int childID);

    void addSibling(int siblingID);

    void removeSibling();

    boolean isFamilyMember(int id);

    Map<String, Integer> getFamilyMap();

    boolean updateFamilyTree();
}
