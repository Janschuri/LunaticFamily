package de.janschuri.lunaticfamily.common.handler;

import de.janschuri.lunaticfamily.Marriage;
import de.janschuri.lunaticfamily.Siblinghood;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.io.Serializable;
import java.util.*;

public abstract class FamilyPlayerImpl implements Serializable {


    private final int id;
    private final UUID uuid;
    private String name;
    private String skinURL;
    private PlayerSender player;
    private String gender;
    private String background;
    private List<Integer> familyList;
    private List<de.janschuri.lunaticfamily.Marriage> marriages;
    private List<de.janschuri.lunaticfamily.Siblinghood> siblinghoods;
    private List<Adoption> adoptionsAsParent;
    private List<Adoption> adoptionsAsChild;

    public static final String DEFAULT_SKIN = "http://textures.minecraft.net/texture/2705fd94a0c431927fb4e639b0fcfb49717e412285a02b439e0112da22b2e2ec";


    public FamilyPlayerImpl(
            int id,
            UUID uuid,
            String name,
            String skinURL,
            String gender,
            String background,
            List<Integer> familyList,
            List<de.janschuri.lunaticfamily.Marriage> marriages,
            List<de.janschuri.lunaticfamily.Siblinghood> siblinghoods,
            List<Adoption> adoptionsAsParent,
            List<Adoption> adoptionsAsChild
    ) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.skinURL = skinURL;
        this.gender = gender;
        this.background = background;
        this.familyList = familyList;
        this.marriages = marriages;
        this.siblinghoods = siblinghoods;
        this.adoptionsAsParent = adoptionsAsParent;
        this.adoptionsAsChild = adoptionsAsChild;
    }

    public abstract FamilyPlayerImpl getFamilyPlayer(int id);
    public abstract FamilyPlayerImpl getFamilyPlayer(UUID uuid);

    public abstract void update();
    public abstract void save();

    protected abstract void saveMarriage(int partnerID, int priestID);
    protected abstract void divorceMarriage();
    protected abstract void saveSiblinghood(int siblingID, int priestID);
    protected abstract void unsiblingSiblinghood();
    protected abstract void saveAdoption(int childID, int priestID);
    protected abstract void unadoptAdoption(int childID);

    public final int getId() {
        return id;
    }

    public final UUID getUniqueId() {
        return uuid;
    }

    public final String getName() {
        return name;
    }

    public final String getSkinURL() {
        return skinURL;
    }

    public final PlayerSender getPlayer() {
        if (player == null) {
            player = LunaticLib.getPlatform().getPlayerSender(uuid);
        }
        return player;
    }

    public final void updateAll() {
        update();
        Map<Integer, String> familyMap = getFamilyList();
        for (int id : familyMap.keySet()) {
            getFamilyPlayer(id).update();
        }
    }

    public final FamilyPlayerImpl getPartner() {
        if (getMarriages().isEmpty()) {
            return null;
        }

        int partnerID = getMarriages().get(0).getPartnerID(id);
        return getFamilyPlayer(partnerID);
    }

    public final boolean isMarried() {
        return !getMarriages().isEmpty();
    }

    public final boolean hasChildren() {
        return !getAdoptionsAsParent().isEmpty();
    }

    public final FamilyPlayerImpl getSibling() {
        if (getSiblinghoods().isEmpty()) {
            return null;
        }

        int siblingID = getSiblinghoods().get(0).getSiblingID(id);
        return getFamilyPlayer(siblingID);
    }

    public final boolean hasSibling() {
        return !getSiblinghoods().isEmpty();
    }

    public final boolean isAdopted() {
        return !getAdoptionsAsChild().isEmpty();
    }

    public final boolean isChildOf(int parentID) {
        for (Adoption adoption : getAdoptionsAsChild()) {
            if (adoption.getParentID() == parentID) {
                return true;
            }
        }
        return false;
    }

    public final List<FamilyPlayerImpl> getParents() {
        List<FamilyPlayerImpl> list = new ArrayList<>();

        for (Adoption adoption : getAdoptionsAsChild()) {
            list.add(getFamilyPlayer(adoption.getParentID()));
        }

        return list;
    }

    public final List<FamilyPlayerImpl> getChildren() {
        List<FamilyPlayerImpl> list = new ArrayList<>();

        for (Adoption adoption : getAdoptionsAsParent()) {
            list.add(getFamilyPlayer(adoption.getChildID()));
        }

        return list;
    }

    public final String getGender() {
        return gender;
    }

    public final void setGender(String gender) {
        this.gender = gender;
    }

    public final String getBackground() {
        return background;
    }

    public final void setBackground(String background) {
//        backgrounds.put(id, "textures/block/" + background + ".png");
        this.background = background;
    }

    public final Integer getChildrenAmount() {
        return getAdoptionsAsParent().size();
    }


    public final void marry(int partnerID) {
        marry(partnerID, -1);
    }

    public final void marry(int partnerID, int priestID) {
        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl partnerFam = getFamilyPlayer(partnerID);

        if

        if (getFamilyPlayer(partnerID).isFamilyMember(this.id)) {
            return;
        }

        if (partnerID == this.id) {
            Logger.errorLog("Cancelled marriage. Player can't marry himself.");
            return;
        }

        List<FamilyPlayerImpl> playerChildren = playerFam.getChildren();
        List<FamilyPlayerImpl> partnerChildren = partnerFam.getChildren();

        for (FamilyPlayerImpl child : playerChildren) {
            partnerFam.saveAdoption(child.getId(), priestID);
        }
        for (FamilyPlayerImpl child : partnerChildren) {
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

    public final void divorce() {
        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl partnerFam = playerFam.getPartner();

        List<FamilyPlayerImpl> playerChildren = playerFam.getChildren();
        for (FamilyPlayerImpl child : playerChildren) {
            playerFam.unadoptAdoption(child.getId());
        }

        playerFam.divorceMarriage();

        if (!LunaticFamily.getConfig().isAllowSingleAdopt()) {
            for (FamilyPlayerImpl child : playerChildren) {
                partnerFam.unadoptAdoption(child.getId());
            }
        }

        playerFam.updateFamilyTree();
        partnerFam.updateFamilyTree();
    }

    public final void adopt(int childID) {
        adopt(childID, -1);
    }

    public final void adopt(int childID, int priestID) {
        if (new FamilyPlayerImpl(childID).isFamilyMember(this.id)) {
            Logger.errorLog("Cancelled adoption. Player is already a family member.");
            return;
        }

        if (childID == this.id) {
            Logger.errorLog("Cancelled adoption. Player can't adopt himself.");
            return;
        }

        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl childFam = new FamilyPlayerImpl(childID);
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

    public final void unadopt(int childID) {
        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl childFam = new FamilyPlayerImpl(childID);
        playerFam.unadoptAdoption(childID);

        if (playerFam.isMarried()) {
            FamilyPlayerImpl partnerFam = playerFam.getPartner();
            partnerFam.unadoptAdoption(childID);
        }

        if (childFam.hasSibling()) {
            childFam.unsiblingSiblinghood();
        }

        playerFam.updateFamilyTree();
        childFam.update();
        childFam.updateFamilyTree();
    }

    public final void addSibling(int siblingID) {
        addSibling(siblingID, -1);
    }

    public final void addSibling(int siblingID, int priestID) {
        if (new FamilyPlayerImpl(siblingID).isFamilyMember(this.id)) {
            Logger.errorLog("Cancelled Siblinghood. Player is already a family member.");
            return;
        }

        if (siblingID == this.id) {
            Logger.errorLog("Cancelled Siblinghood. Player can't be sibling to himself.");
            return;
        }

        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl siblingFam = new FamilyPlayerImpl(siblingID);
        playerFam.saveSiblinghood(siblingID, priestID);

        playerFam.updateFamilyTree();
        siblingFam.updateFamilyTree();
    }

    public final void removeSibling() {
        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl siblingFam = playerFam.getSibling();

        playerFam.unsiblingSiblinghood();

        playerFam.updateFamilyTree();
        siblingFam.updateFamilyTree();
    }

    public final boolean isFamilyMember(int id) {
        return getFamilyList().containsKey(id);
    }

    @Override
    public Map<Integer, String> getFamilyList() {
        return familyLists.get(id);
    }

    public void loadFamilyMap() {

        Map<Integer, String> familyList = new HashMap<>();

        if (this.getPartner() != null) {
            int partner = this.getPartner().getId();
            familyList.put(partner, "partner");
            FamilyPlayerImpl partnerFam = new FamilyPlayerImpl(partner);

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
            FamilyPlayerImpl siblingFam = new FamilyPlayerImpl(sibling);

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
            FamilyPlayerImpl firstParentFam = new FamilyPlayerImpl(firstParent);
            if (firstParentFam.getSibling() != null) {
                int secondAuntOrUncle = firstParentFam.getSibling().getId();
                familyList.put(secondAuntOrUncle, "second_aunt_or_uncle");
                FamilyPlayerImpl secondAuntOrUncleFam = new FamilyPlayerImpl(secondAuntOrUncle);
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
                FamilyPlayerImpl firstGrandparentFam = new FamilyPlayerImpl(firstGrandparent);

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
                FamilyPlayerImpl secondGrandparentFam = new FamilyPlayerImpl(secondGrandparent);

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
            FamilyPlayerImpl secondParentFam = new FamilyPlayerImpl(secondParent);

            if (secondParentFam.getSibling() != null) {
                int thirdAuntOrUncle = secondParentFam.getSibling().getId();
                familyList.put(thirdAuntOrUncle, "third_aunt_or_uncle");
                FamilyPlayerImpl thirdAuntOrUncleFam = new FamilyPlayerImpl(thirdAuntOrUncle);
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
                FamilyPlayerImpl thirdGrandparentFam = new FamilyPlayerImpl(thirdGrandparent);

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
                FamilyPlayerImpl fourthGrandparentFam = new FamilyPlayerImpl(fourthGrandparent);

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
            FamilyPlayerImpl firstChildFam = new FamilyPlayerImpl(firstChild);

            if (firstChildFam.getPartner() != null) {
                int firstChildInLaw = firstChildFam.getPartner().getId();
                familyList.put(firstChildInLaw, "first_child_in_law");
            }
            if (!firstChildFam.getChildren().isEmpty()) {
                int firstGrandchild = firstChildFam.getChildren().get(0).getId();
                familyList.put(firstGrandchild, "first_grandchild");
                FamilyPlayerImpl firstGrandchildFam = new FamilyPlayerImpl(firstGrandchild);
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
                FamilyPlayerImpl secondGrandchildFam = new FamilyPlayerImpl(secondGrandchild);
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
            FamilyPlayerImpl secondChildFam = new FamilyPlayerImpl(secondChild);

            if (secondChildFam.getPartner() != null) {
                int secondChildInLaw = secondChildFam.getPartner().getId();
                familyList.put(secondChildInLaw, "second_child_in_law");
            }
            if (!secondChildFam.getChildren().isEmpty()) {
                int thirdGrandchild = secondChildFam.getChildren().get(0).getId();
                familyList.put(thirdGrandchild, "third_grandchild");
                FamilyPlayerImpl thirdGrandchildFam = new FamilyPlayerImpl(thirdGrandchild);
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
                FamilyPlayerImpl fourthGrandchildFam = new FamilyPlayerImpl(fourthGrandchild);
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

        familyLists.put(id, familyList);
    }

    public boolean updateFamilyTree() {
        if (getPlayer().isOnline() && LunaticFamily.getConfig().isUseCrazyAdvancementAPI()) {
            FamilyTree familyTree = LunaticFamily.getPlatform().getFamilyTree();

            if (familyTree == null) {
                Logger.errorLog("FamilyTree is null. Please check if CrazyAdvancementsAPI is installed or disable it!");
                return false;
            } else {
                return familyTree.update(getPlayer().getServerName(), this.uuid, this.id);
            }
        }
        return true;
    }

    public List<Marriage> getMarriages() {
        if (!marriages.containsKey(this.id)) {
            marriages.put(this.id, new ArrayList<>());
        }

        return marriages.get(this.id);
    }

    public List<Siblinghood> getSiblinghoods() {
        if (!siblinghoods.containsKey(this.id)) {
            siblinghoods.put(this.id, new ArrayList<>());
        }

        return siblinghoods.get(this.id);
    }

    public List<Adoption> getAdoptionsAsParent() {
        if (!adoptionsAsParent.containsKey(this.id)) {
            adoptionsAsParent.put(this.id, new ArrayList<>());
        }

        return adoptionsAsParent.get(this.id);
    }

    public List<Adoption> getAdoptionsAsChild() {
        if (!adoptionsAsChild.containsKey(this.id)) {
            adoptionsAsChild.put(this.id, new ArrayList<>());
        }

        return adoptionsAsChild.get(this.id);
    }
}


