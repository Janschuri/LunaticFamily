package de.janschuri.lunaticfamily.common.handler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticfamily.FamilyPlayer;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.tables.AdoptionsTable;
import de.janschuri.lunaticfamily.common.database.tables.MarriagesTable;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.database.tables.SiblinghoodsTable;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.FamilyTreeManager;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.*;

public class FamilyPlayerImpl implements FamilyPlayer {


    private final int id;
    private final UUID uuid;
    private String name;
    private String skinURL;
    private String gender;
    private String background;
    private List<Marriage> marriages;
    private List<Siblinghood> siblinghoods;
    private List<Adoption> adoptionsAsParent;
    private List<Adoption> adoptionsAsChild;

    private final Map<Integer, String> familyMap = new HashMap<>();

    private static final BiMap<UUID, Integer> ids = HashBiMap.create();
    private static final Map<Integer, FamilyPlayerImpl> familyPlayerMap = new HashMap<>();

    public static final String DEFAULT_SKIN = "http://textures.minecraft.net/texture/2705fd94a0c431927fb4e639b0fcfb49717e412285a02b439e0112da22b2e2ec";

    public static FamilyPlayerImpl getFamilyPlayer(int id) {
        if (familyPlayerMap.containsKey(id)) {
            return familyPlayerMap.get(id);
        } else {
            FamilyPlayerImpl familyPlayer = PlayerDataTable.getFamilyPlayer(id);
            if (familyPlayer == null) {
                return null;
            }
            familyPlayerMap.put(id, familyPlayer);
            return familyPlayer;
        }
    }

    public static FamilyPlayerImpl getFamilyPlayer(UUID uuid) {
        if (ids.containsKey(uuid)) {
            return getFamilyPlayer(ids.get(uuid));
        } else {
            return new FamilyPlayerImpl(uuid);
        }
    }

    public FamilyPlayerImpl(
            int id,
            UUID uuid,
            String name,
            String skinURL,
            String gender,
            String background
    ) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.skinURL = skinURL;
        this.gender = gender;
        this.background = background;

        ids.put(uuid, id);
        familyPlayerMap.put(id, this);

        update();
    }

    private FamilyPlayerImpl(UUID uuid) {
        this.uuid = uuid;
        PlayerSender player = LunaticLib.getPlatform().getPlayerSender(uuid);
        this.name = player.getName();
        this.skinURL = player.getSkinURL();
        this.skinURL = this.skinURL == null ? DEFAULT_SKIN : this.skinURL;
        this.gender = LunaticFamily.getConfig().getDefaultGender();
        this.background = LunaticFamily.getConfig().getDefaultBackground();

        this.id = save();

        ids.put(uuid, this.id);
        familyPlayerMap.put(this.id, this);

        update();
    }

    public void update() {
        this.marriages = MarriagesTable.getPlayersMarriages(id);
        this.siblinghoods = SiblinghoodsTable.getPlayersSiblinghoods(id);
        this.adoptionsAsParent = AdoptionsTable.getPlayerAsParentAdoptions(id);
        this.adoptionsAsChild = AdoptionsTable.getPlayerAsChildAdoptions(id);

        loadFamilyMap();
    }

    public String getName() {
        return name;
    }

    public FamilyPlayerImpl setName(String name) {
        this.name = name;
        save();
        return this;
    }

    public int getId() {
        return id;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    private int save() {
        if (id < 1) {
            return PlayerDataTable.save(uuid, getName(), getSkinURL(), getGender(), getBackground());
        } else {
            return PlayerDataTable.update(id, uuid, getName(), getSkinURL(), getGender(), getBackground());
        }
    }

    private void saveMarriage(int partnerID, int priestID) {
        MarriagesTable.saveMarriage(this.id, partnerID, priestID);
        update();
        updateAll();
    }

    private void divorceMarriage() {
        MarriagesTable.divorceMarriage(this.id);
        update();
        updateAll();
    }

    private void saveSiblinghood(int siblingID, int priestID) {
        SiblinghoodsTable.saveSiblinghood(this.id, siblingID, priestID);
        update();
        updateAll();
    }

    private void unsiblingSiblinghood() {
        SiblinghoodsTable.unsiblingSiblinghood(this.id);
        update();
        updateAll();
    }

    private void saveAdoption(int childID, int priestID) {
        AdoptionsTable.saveAdoption(this.id, childID, priestID);
        update();
        updateAll();
    }

    private void unadoptAdoption(int childID) {
        AdoptionsTable.unadoptAdoption(this.id, childID);
        update();
        updateAll();
    }

    public void updateAll() {
        Map<Integer, String> familyMap = getFamilyMap();
        for (int id : familyMap.keySet()) {
            FamilyPlayerImpl familyPlayer = getFamilyPlayer(id);
            familyPlayer.update();
        }
    }

    public String getSkinURL() {
        return skinURL;
    }

    public FamilyPlayerImpl getPartner() {
        if (getMarriages().isEmpty()) {
            return null;
        }

        int partnerID = getMarriages().get(0).getPartnerID(id);
        return getFamilyPlayer(partnerID);
    }

    public boolean isMarried() {
        return !getMarriages().isEmpty();
    }

    public boolean hasChildren() {
        return !getAdoptionsAsParent().isEmpty();
    }

    public FamilyPlayerImpl getSibling() {
        if (getSiblinghoods().isEmpty()) {
            return null;
        }

        int siblingID = getSiblinghoods().get(0).getSiblingID(id);
        return getFamilyPlayer(siblingID);
    }

    public List<FamilyPlayerImpl> getSiblings() {
        if (getSiblinghoods().isEmpty()) {
            return null;
        }

        List<FamilyPlayerImpl> siblings = new ArrayList<>();

        for (Siblinghood siblinghood : getSiblinghoods()) {
            int siblingID = siblinghood.getSiblingID(id);
            siblings.add(getFamilyPlayer(siblingID));
        }

        return siblings;
    }

    public boolean hasSibling() {
        return !getSiblinghoods().isEmpty();
    }

    public boolean isAdopted() {
        return !getAdoptionsAsChild().isEmpty();
    }

    public boolean isChildOf(int parentID) {
        for (Adoption adoption : getAdoptionsAsChild()) {
            if (adoption.getParentID() == parentID) {
                return true;
            }
        }
        return false;
    }

    public List<FamilyPlayerImpl> getParents() {
        List<FamilyPlayerImpl> list = new ArrayList<>();

        for (Adoption adoption : getAdoptionsAsChild()) {
            int parentID = adoption.getParentID();
            list.add(getFamilyPlayer(parentID));
        }

        return list;
    }

    public List<FamilyPlayer> getChildren() {
        List<FamilyPlayer> list = new ArrayList<>();

        for (Adoption adoption : getAdoptionsAsParent()) {
            int childID = adoption.getChildID();
            list.add(getFamilyPlayer(childID));
        }

        return list;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        save();
    }

    public String getBackground() {
        return this.background;
    }

    public void setBackground(String background) {
        this.background = background;
        save();
    }

    public Integer getChildrenAmount() {
        return getAdoptionsAsParent().size();
    }


    public void marry(int partnerID) {
        marry(partnerID, -1);
    }

    public void marry(int partnerID, int priestID) {
        if (getFamilyPlayer(partnerID).isFamilyMember(this.id)) {
            Logger.errorLog("Cancelled marriage. Player is already a family member.");
            return;
        }

        if (partnerID == this.id) {
            Logger.errorLog("Cancelled marriage. Player can't marry himself.");
            return;
        }

        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl partnerFam = getFamilyPlayer(partnerID);
        List<FamilyPlayer> playerChildren = playerFam.getChildren();
        List<FamilyPlayer> partnerChildren = partnerFam.getChildren();

        for (FamilyPlayer child : playerChildren) {
            partnerFam.saveAdoption(child.getId(), priestID);
        }
        for (FamilyPlayer child : partnerChildren) {
            playerFam.saveAdoption(child.getId(), priestID);
        }

        if (playerChildren.size() == 1 && partnerChildren.size() == 1) {
            FamilyPlayerImpl childFam = (FamilyPlayerImpl) playerChildren.get(0);
            childFam.saveSiblinghood(partnerChildren.get(0).getId(), priestID);
        }

        playerFam.saveMarriage(partnerID, priestID);

        playerFam.updateFamilyTree();
        partnerFam.updateFamilyTree();
    }

    public void divorce() {
        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl partnerFam = playerFam.getPartner();

        List<FamilyPlayer> playerChildren = playerFam.getChildren();
        for (FamilyPlayer child : playerChildren) {
            playerFam.unadoptAdoption(child.getId());
        }

        playerFam.divorceMarriage();

        if (!LunaticFamily.getConfig().isAllowSingleAdopt()) {
            for (FamilyPlayer child : playerChildren) {
                partnerFam.unadoptAdoption(child.getId());
            }
        }

        playerFam.updateFamilyTree();
        partnerFam.updateFamilyTree();
    }

    public void adopt(int childID) {
        adopt(childID, -1);
    }

    public void adopt(int childID, int priestID) {
        if (getFamilyPlayer(childID).isFamilyMember(this.id)) {
            Logger.errorLog("Cancelled adoption. Player is already a family member.");
            return;
        }

        if (childID == this.id) {
            Logger.errorLog("Cancelled adoption. Player can't adopt himself.");
            return;
        }

        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl childFam = getFamilyPlayer(childID);
        playerFam.saveAdoption(childID, priestID);

        if (playerFam.isMarried()) {
            FamilyPlayerImpl partnerFam = playerFam.getPartner();
            partnerFam.saveAdoption(childID, priestID);
        }

        if (childFam.hasSibling()) {
            FamilyPlayerImpl siblingFam = childFam.getSibling();
            playerFam.saveAdoption(siblingFam.getId(), priestID);
            if (playerFam.isMarried()) {
                FamilyPlayerImpl partnerFam = playerFam.getPartner();
                partnerFam.saveAdoption(siblingFam.getId(), priestID);
            }
        }

        playerFam.updateFamilyTree();
        childFam.updateFamilyTree();
    }

    public void unadopt(int childID) {
        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl childFam = getFamilyPlayer(childID);
        playerFam.unadoptAdoption(childID);

        if (playerFam.isMarried()) {
            FamilyPlayerImpl partnerFam = playerFam.getPartner();
            partnerFam.unadoptAdoption(childID);
        }

        if (childFam.hasSibling()) {
            childFam.unsiblingSiblinghood();
        }

        playerFam.updateFamilyTree();
        childFam.updateFamilyTree();
    }

    public void addSibling(int siblingID) {
        addSibling(siblingID, -1);
    }

    public void addSibling(int siblingID, int priestID) {
        if (getFamilyPlayer(siblingID).isFamilyMember(this.id)) {
            Logger.errorLog("Cancelled Siblinghood. Player is already a family member.");
            return;
        }

        if (siblingID == this.id) {
            Logger.errorLog("Cancelled Siblinghood. Player can't be sibling to himself.");
            return;
        }

        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl siblingFam = getFamilyPlayer(siblingID);
        playerFam.saveSiblinghood(siblingID, priestID);

        playerFam.updateFamilyTree();
        siblingFam.updateFamilyTree();
    }

    public void removeSibling() {
        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl siblingFam = playerFam.getSibling();

        playerFam.unsiblingSiblinghood();

        playerFam.updateFamilyTree();
        siblingFam.updateFamilyTree();
    }

    public boolean isFamilyMember(int id) {
        return getFamilyMap().containsKey(id);
    }

    @Override
    public Map<Integer, String> getFamilyMap() {
        return familyMap;
    }

    public void loadFamilyMap() {

        Map<Integer, String> familyList = new HashMap<>();

        if (this.getPartner() != null) {
            int partner = this.getPartner().getId();
            familyList.put(partner, "partner");
            FamilyPlayerImpl partnerFam = getFamilyPlayer(partner);

            if (partnerFam.getSibling() != null) {
                int secondSiblingInLaw = partnerFam.getSibling().getId();
                familyList.put(secondSiblingInLaw, "second_sibling_in_law");

            }

            if (!partnerFam.getParents().isEmpty()) {
                int firstParentInLaw = partnerFam.getParents().get(0).getId();
                familyList.put(firstParentInLaw, "first_parent_in_law");
            }

            if (partnerFam.getParents().size() > 1) {
                int secondParentInLaw = partnerFam.getParents().get(1).getId();
                familyList.put(secondParentInLaw, "second_parent_in_law");
            }
        }

        if (this.getSibling() != null) {
            int sibling = this.getSibling().getId();
            familyList.put(sibling, "sibling");
            FamilyPlayerImpl siblingFam = getFamilyPlayer(sibling);

            if (siblingFam.getPartner() != null) {
                int firstSiblingInLaw = siblingFam.getPartner().getId();
                familyList.put(firstSiblingInLaw, "first_sibling_in_law");
            }

            if (!siblingFam.getChildren().isEmpty()) {
                int firstNieceOrNephew = siblingFam.getChildren().get(0).getId();
                familyList.put(firstNieceOrNephew, "first_niece_or_nephew");
            }

            if (siblingFam.getChildren().size() > 1) {
                int secondNieceOrNephew = siblingFam.getChildren().get(1).getId();
                familyList.put(secondNieceOrNephew, "second_niece_or_nephew");
            }
        }

        if (!this.getParents().isEmpty()) {
            int firstParent = this.getParents().get(0).getId();
            familyList.put(firstParent, "first_parent");
            FamilyPlayerImpl firstParentFam = getFamilyPlayer(firstParent);
            if (firstParentFam.getSibling() != null) {
                int secondAuntOrUncle = firstParentFam.getSibling().getId();
                familyList.put(secondAuntOrUncle, "second_aunt_or_uncle");
                FamilyPlayerImpl secondAuntOrUncleFam = getFamilyPlayer(secondAuntOrUncle);
                if (secondAuntOrUncleFam.getPartner() != null) {
                    int firstAuntOrUncle = secondAuntOrUncleFam.getPartner().getId();
                    familyList.put(firstAuntOrUncle, "first_aunt_or_uncle");
                }
                if (!secondAuntOrUncleFam.getChildren().isEmpty()) {
                    int firstCousin = secondAuntOrUncleFam.getChildren().get(0).getId();
                    familyList.put(firstCousin, "first_cousin");
                }
                if (secondAuntOrUncleFam.getChildren().size() > 1) {
                    int secondCousin = secondAuntOrUncleFam.getChildren().get(1).getId();
                    familyList.put(secondCousin, "second_cousin");
                }
            }
            if (!firstParentFam.getParents().isEmpty()) {
                int firstGrandparent = firstParentFam.getParents().get(0).getId();
                familyList.put(firstGrandparent, "first_grandparent");
                FamilyPlayerImpl firstGrandparentFam = getFamilyPlayer(firstGrandparent);

                if (firstGrandparentFam.getSibling() != null) {
                    int firstGreatAuntOrUncle = firstGrandparentFam.getSibling().getId();
                    familyList.put(firstGreatAuntOrUncle, "first_great_aunt_or_uncle");
                }
                if (!firstGrandparentFam.getParents().isEmpty()) {
                    int firstGreatGrandparent = firstGrandparentFam.getParents().get(0).getId();
                    familyList.put(firstGreatGrandparent, "first_great_grandparent");
                }
                if (firstGrandparentFam.getParents().size() > 1) {
                    int secondGreatGrandparent = firstGrandparentFam.getParents().get(1).getId();
                    familyList.put(secondGreatGrandparent, "second_great_grandparent");
                }
            }
            if (firstParentFam.getParents().size() > 1) {
                int secondGrandparent = firstParentFam.getParents().get(1).getId();
                familyList.put(secondGrandparent, "second_grandparent");
                FamilyPlayerImpl secondGrandparentFam = getFamilyPlayer(secondGrandparent);

                if (secondGrandparentFam.getSibling() != null) {
                    int secondGreatAuntOrUncle = secondGrandparentFam.getSibling().getId();
                    familyList.put(secondGreatAuntOrUncle, "second_great_aunt_or_uncle");
                }
                if (!secondGrandparentFam.getParents().isEmpty()) {
                    int thirdGreatGrandparent = secondGrandparentFam.getParents().get(0).getId();
                    familyList.put(thirdGreatGrandparent, "third_great_grandparent");
                }
                if (secondGrandparentFam.getParents().size() > 1) {
                    int fourthGreatGrandparent = secondGrandparentFam.getParents().get(1).getId();
                    familyList.put(fourthGreatGrandparent, "fourth_great_grandparent");
                }
            }
        }

        if (this.getParents().size() > 1) {
            int secondParent = this.getParents().get(1).getId();
            familyList.put(secondParent, "second_parent");
            FamilyPlayerImpl secondParentFam = getFamilyPlayer(secondParent);

            if (secondParentFam.getSibling() != null) {
                int thirdAuntOrUncle = secondParentFam.getSibling().getId();
                familyList.put(thirdAuntOrUncle, "third_aunt_or_uncle");
                FamilyPlayerImpl thirdAuntOrUncleFam = getFamilyPlayer(thirdAuntOrUncle);
                if (thirdAuntOrUncleFam.getPartner() != null) {
                    int fourthAuntOrUncle = thirdAuntOrUncleFam.getPartner().getId();
                    familyList.put(fourthAuntOrUncle, "fourth_aunt_or_uncle");
                }
                if (!thirdAuntOrUncleFam.getChildren().isEmpty()) {
                    int thirdCousin = thirdAuntOrUncleFam.getChildren().get(0).getId();
                    familyList.put(thirdCousin, "third_cousin");
                }
                if (thirdAuntOrUncleFam.getChildren().size() > 1) {
                    int fourthCousin = thirdAuntOrUncleFam.getChildren().get(1).getId();
                    familyList.put(fourthCousin, "fourth_cousin");
                }
            }
            if (!secondParentFam.getParents().isEmpty()) {
                int thirdGrandparent = secondParentFam.getParents().get(0).getId();
                familyList.put(thirdGrandparent, "third_grandparent");
                FamilyPlayerImpl thirdGrandparentFam = getFamilyPlayer(thirdGrandparent);

                if (thirdGrandparentFam.getSibling() != null) {
                    int thirdGreatAuntOrUncle = thirdGrandparentFam.getSibling().getId();
                    familyList.put(thirdGreatAuntOrUncle, "third_great_aunt_or_uncle");
                }
                if (!thirdGrandparentFam.getParents().isEmpty()) {
                    int fifthGreatGrandparent = thirdGrandparentFam.getParents().get(0).getId();
                    familyList.put(fifthGreatGrandparent, "fifth_great_grandparent");
                }
                if (thirdGrandparentFam.getParents().size() > 1) {
                    int sixthGreatGrandparent = thirdGrandparentFam.getParents().get(1).getId();
                    familyList.put(sixthGreatGrandparent, "sixth_great_grandparent");
                }
            }
            if (secondParentFam.getParents().size() > 1) {
                int fourthGrandparent = secondParentFam.getParents().get(1).getId();
                familyList.put(fourthGrandparent, "fourth_grandparent");
                FamilyPlayerImpl fourthGrandparentFam = getFamilyPlayer(fourthGrandparent);

                if (fourthGrandparentFam.getSibling() != null) {
                    int fourthGreatAuntOrUncle = fourthGrandparentFam.getSibling().getId();
                    familyList.put(fourthGreatAuntOrUncle, "fourth_great_aunt_or_uncle");
                }
                if (!fourthGrandparentFam.getParents().isEmpty()) {
                    int seventhGreatGrandparent = fourthGrandparentFam.getParents().get(0).getId();
                    familyList.put(seventhGreatGrandparent, "seventh_great_grandparent");
                }
                if (fourthGrandparentFam.getParents().size() > 1) {
                    int eighthGreatGrandparent = fourthGrandparentFam.getParents().get(1).getId();
                    familyList.put(eighthGreatGrandparent, "eighth_great_grandparent");
                }
            }
        }

        if (!this.getChildren().isEmpty()) {
            int firstChild = this.getChildren().get(0).getId();
            familyList.put(firstChild, "first_child");
            FamilyPlayerImpl firstChildFam = getFamilyPlayer(firstChild);

            if (firstChildFam.getPartner() != null) {
                int firstChildInLaw = firstChildFam.getPartner().getId();
                familyList.put(firstChildInLaw, "first_child_in_law");
            }
            if (!firstChildFam.getChildren().isEmpty()) {
                int firstGrandchild = firstChildFam.getChildren().get(0).getId();
                familyList.put(firstGrandchild, "first_grandchild");
                FamilyPlayerImpl firstGrandchildFam = getFamilyPlayer(firstGrandchild);
                if (firstGrandchildFam.getPartner() != null) {
                    int firstGrandchildInLaw = firstGrandchildFam.getPartner().getId();
                    familyList.put(firstGrandchildInLaw, "first_grandchild_in_law");
                }
                if (!firstGrandchildFam.getChildren().isEmpty()) {
                    int firstGreatGrandchild = firstGrandchildFam.getChildren().get(0).getId();
                    familyList.put(firstGreatGrandchild, "first_great_grandchild");
                }
                if (firstGrandchildFam.getChildren().size() > 1) {
                    int secondGreatGrandchild = firstGrandchildFam.getChildren().get(1).getId();
                    familyList.put(secondGreatGrandchild, "second_great_grandchild");
                }
            }
            if (firstChildFam.getChildren().size() > 1) {
                int secondGrandchild = firstChildFam.getChildren().get(1).getId();
                familyList.put(secondGrandchild, "second_grandchild");
                FamilyPlayerImpl secondGrandchildFam = getFamilyPlayer(secondGrandchild);
                if (secondGrandchildFam.getPartner() != null) {
                    int secondGrandchildInLaw = secondGrandchildFam.getPartner().getId();
                    familyList.put(secondGrandchildInLaw, "second_grandchild_in_law");
                }
                if (!secondGrandchildFam.getChildren().isEmpty()) {
                    int thirdGreatGrandchild = secondGrandchildFam.getChildren().get(0).getId();
                    familyList.put(thirdGreatGrandchild, "third_great_grandchild");
                }
                if (secondGrandchildFam.getChildren().size() > 1) {
                    int fourthGreatGrandchild = secondGrandchildFam.getChildren().get(1).getId();
                    familyList.put(fourthGreatGrandchild, "fourth_great_grandchild");
                }
            }
        }

        if (this.getChildren().size() > 1) {
            int secondChild = this.getChildren().get(1).getId();
            familyList.put(secondChild, "second_child");
            FamilyPlayerImpl secondChildFam = getFamilyPlayer(secondChild);

            if (secondChildFam.getPartner() != null) {
                int secondChildInLaw = secondChildFam.getPartner().getId();
                familyList.put(secondChildInLaw, "second_child_in_law");
            }
            if (!secondChildFam.getChildren().isEmpty()) {
                int thirdGrandchild = secondChildFam.getChildren().get(0).getId();
                familyList.put(thirdGrandchild, "third_grandchild");
                FamilyPlayerImpl thirdGrandchildFam = getFamilyPlayer(thirdGrandchild);
                if (thirdGrandchildFam.getPartner() != null) {
                    int thirdGrandchildInLaw = thirdGrandchildFam.getPartner().getId();
                    familyList.put(thirdGrandchildInLaw, "third_grandchild_in_law");
                }
                if (!thirdGrandchildFam.getChildren().isEmpty()) {
                    int fifthGreatGrandchild = thirdGrandchildFam.getChildren().get(0).getId();
                    familyList.put(fifthGreatGrandchild, "fifth_great_grandchild");
                }
                if (thirdGrandchildFam.getChildren().size() > 1) {
                    int sixthGreatGrandchild = thirdGrandchildFam.getChildren().get(1).getId();
                    familyList.put(sixthGreatGrandchild, "sixth_great_grandchild");
                }
            }
            if (secondChildFam.getChildren().size() > 1) {
                int fourthGrandchild = secondChildFam.getChildren().get(1).getId();
                familyList.put(fourthGrandchild, "fourth_grandchild");
                FamilyPlayerImpl fourthGrandchildFam = getFamilyPlayer(fourthGrandchild);
                if (fourthGrandchildFam.getPartner() != null) {
                    int fourthGrandchildInLaw = fourthGrandchildFam.getPartner().getId();
                    familyList.put(fourthGrandchildInLaw, "fourth_grandchild_in_law");
                }
                if (!fourthGrandchildFam.getChildren().isEmpty()) {
                    int seventhGreatGrandchild = fourthGrandchildFam.getChildren().get(0).getId();
                    familyList.put(seventhGreatGrandchild, "seventh_great_grandchild");
                }
                if (fourthGrandchildFam.getChildren().size() > 1) {
                    int eighthGreatGrandchild = fourthGrandchildFam.getChildren().get(1).getId();
                    familyList.put(eighthGreatGrandchild, "eighth_great_grandchild");
                }
            }
        }

        this.familyMap.clear();
        this.familyMap.putAll(familyList);
    }

    public boolean updateFamilyTree() {
        PlayerSender player = LunaticLib.getPlatform().getPlayerSender(this.uuid);

        if (player == null) {
            return true;
        }

        if (player.isOnline() && LunaticFamily.getConfig().isUseCrazyAdvancementAPI()) {
            FamilyTreeManager familyTreeManager = LunaticFamily.getPlatform().getFamilyTree();

            if (familyTreeManager == null) {
                Logger.errorLog("FamilyTree is null. Please check if CrazyAdvancementsAPI is installed or disable it!");
                return false;
            } else {

                String serverName = player.getServerName();

                return familyTreeManager.update(serverName, this.id);
            }
        }
        return true;
    }

    public List<Marriage> getMarriages() {
        return marriages;
    }

    public List<Siblinghood> getSiblinghoods() {
        return siblinghoods;
    }

    public List<Adoption> getAdoptionsAsParent() {
        return adoptionsAsParent;
    }

    public List<Adoption> getAdoptionsAsChild() {
        return adoptionsAsChild;
    }
}


