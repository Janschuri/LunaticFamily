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
import de.janschuri.lunaticfamily.platform.FamilyTree;
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
    private final PlayerSender player;
    private final BiMap<String, Integer> familyList = HashBiMap.create();
    public static final String DEFAULT_SKIN = "http://textures.minecraft.net/texture/2705fd94a0c431927fb4e639b0fcfb49717e412285a02b439e0112da22b2e2ec";


    public FamilyPlayerImpl(int id) {
        this(
                Objects.requireNonNull(PlayerDataTable.getUUID(id)),
                Objects.requireNonNull(PlayerDataTable.getName(id))
        );
    }

    public FamilyPlayerImpl(UUID uuid) {
        this(uuid, null);
    }

    public FamilyPlayerImpl(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        player = LunaticLib.getPlatform().getPlayerSender(uuid);

        if (PlayerDataTable.getID(uuid) < 1) {
            savePlayerData();
        }
        id = PlayerDataTable.getID(uuid);

        if (player.getName() != null) {
            this.name = player.getName();
        }

        if (name == null) {
            this.name = PlayerDataTable.getName(id);
        }

        if (name == null) {
            this.name = "unknown";
        }

        skinURL = player.getSkinURL();

        if (skinURL == null) {
            skinURL = PlayerDataTable.getSkinURL(id);
        }
        if (skinURL == null) {
            skinURL = DEFAULT_SKIN;
        }


        if (PlayerDataTable.getGender(id) == null) {
            gender = LunaticFamily.getConfig().getDefaultGender();
        } else {
            gender = PlayerDataTable.getGender(id);
        }

        if (PlayerDataTable.getBackground(id) == null) {
            background = LunaticFamily.getConfig().getDefaultBackground();
        } else {
            background = PlayerDataTable.getBackground(id);
        }


        savePlayerData();

    }

    public String getName() {
        String name = player.getName();
        if (name == null) {
            name = PlayerDataTable.getName(id);
        }

        return name;
    }

    public int getId() {
        return id;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    private void savePlayerData() {
        if (id < 1) {
            PlayerDataTable.savePlayerData(uuid.toString(), name, skinURL, gender, background);
        } else {
            PlayerDataTable.updatePlayerData(id, uuid.toString(), name, skinURL, gender, background);
        }
    }

    private void saveMarriage(int partnerID, int priestID) {
        MarriagesTable.saveMarriage(this.id, partnerID, priestID);
    }

    private void divorceMarriage() {
        MarriagesTable.divorceMarriage(this.id);
    }

    private void saveSiblinghood(int siblingID, int priestID) {
        SiblinghoodsTable.saveSiblinghood(this.id, siblingID, priestID);
    }

    private void unsiblingSiblinghood() {
        SiblinghoodsTable.unsiblingSiblinghood(this.id);
    }

    private void saveAdoption(int childID, int priestID) {
        AdoptionsTable.saveAdoption(this.id, childID, priestID);
    }

    private void unadoptAdoption(int childID) {
        AdoptionsTable.unadoptAdoption(this.id, childID);
    }

    public String getSkinURL() {
        return skinURL;
    }

    public FamilyPlayerImpl getPartner() {
        if (getMarriages().isEmpty()) {
            return null;
        }

        int partnerID = getMarriages().get(0).getPartnerID(id);
        return new FamilyPlayerImpl(partnerID);
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
        return new FamilyPlayerImpl(siblingID);
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

    public List<FamilyPlayer> getParents() {
        List<FamilyPlayer> list = new ArrayList<>();

        for (Adoption adoption : getAdoptionsAsChild()) {
            list.add(new FamilyPlayerImpl(adoption.getParentID()));
        }

        return list;
    }

    public List<FamilyPlayer> getChildren() {
        List<FamilyPlayer> list = new ArrayList<>();

        for (Adoption adoption : getAdoptionsAsParent()) {
            list.add(new FamilyPlayerImpl(adoption.getChildID()));
        }

        return list;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        savePlayerData();
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = "textures/block/" + background + ".png";
        savePlayerData();
    }

    public Integer getChildrenAmount() {
        return getAdoptionsAsParent().size();
    }


    public void marry(int partnerID) {
        marry(partnerID, -1);
    }

    public void marry(int partnerID, int priestID) {
        if (new FamilyPlayerImpl(partnerID).isFamilyMember(this.id)) {
            Logger.errorLog("Cancelled marriage. Player is already a family member.");
            return;
        }

        if (partnerID == this.id) {
            Logger.errorLog("Cancelled marriage. Player can't marry himself.");
            return;
        }

        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl partnerFam = new FamilyPlayerImpl(partnerID);
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

    public void unadopt(int childID) {
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
        childFam.updateFamilyTree();
    }

    public void addSibling(int siblingID) {
        addSibling(siblingID, -1);
    }

    public void addSibling(int siblingID, int priestID) {
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

    public void removeSibling() {
        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl siblingFam = playerFam.getSibling();

        playerFam.unsiblingSiblinghood();

        playerFam.updateFamilyTree();
        siblingFam.updateFamilyTree();
    }

    public boolean isFamilyMember(int id) {
        return this.getFamilyMap().containsValue(id);
    }

    public Map<String, Integer> getFamilyMap() {

        if (this.getPartner() != null) {
            int partner = this.getPartner().getId();
            familyList.put("partner", partner);
            FamilyPlayerImpl partnerFam = new FamilyPlayerImpl(partner);

            if (partnerFam.getSibling() != null) {
                int secondSiblingInLaw = partnerFam.getSibling().getId();
                familyList.put("second_sibling_in_law", secondSiblingInLaw);

            }

            if (!partnerFam.getParents().isEmpty()) {
                int firstParentInLaw = partnerFam.getParents().get(0).getId();
                familyList.put("first_parent_in_law", firstParentInLaw);
            }

            if (partnerFam.getParents().size() > 1) {
                int secondParentInLaw = partnerFam.getParents().get(1).getId();
                familyList.put("second_parent_in_law", secondParentInLaw);
            }
        }

        if (this.getSibling() != null) {
            int sibling = this.getSibling().getId();
            familyList.put("sibling", sibling);
            FamilyPlayerImpl siblingFam = new FamilyPlayerImpl(sibling);

            if (siblingFam.getPartner() != null) {
                int firstSiblingInLaw = siblingFam.getPartner().getId();
                familyList.put("first_sibling_in_law", firstSiblingInLaw);
            }

            if (!siblingFam.getChildren().isEmpty()) {
                int firstNieceOrNephew = siblingFam.getChildren().get(0).getId();
                familyList.put("first_niece_or_nephew", firstNieceOrNephew);
            }

            if (siblingFam.getChildren().size() > 1) {
                int secondNieceOrNephew = siblingFam.getChildren().get(1).getId();
                familyList.put("second_niece_or_nephew", secondNieceOrNephew);
            }
        }

        if (!this.getParents().isEmpty()) {
            int firstParent = this.getParents().get(0).getId();
            familyList.put("first_parent", firstParent);
            FamilyPlayerImpl firstParentFam = new FamilyPlayerImpl(firstParent);
            if (firstParentFam.getSibling() != null) {
                int secondAuntOrUncle = firstParentFam.getSibling().getId();
                familyList.put("second_aunt_or_uncle", secondAuntOrUncle);
                FamilyPlayerImpl secondAuntOrUncleFam = new FamilyPlayerImpl(secondAuntOrUncle);
                if (secondAuntOrUncleFam.getPartner() != null) {
                    int firstAuntOrUncle = secondAuntOrUncleFam.getPartner().getId();
                    familyList.put("first_aunt_or_uncle", firstAuntOrUncle);
                }
                if (!secondAuntOrUncleFam.getChildren().isEmpty()) {
                    int firstCousin = secondAuntOrUncleFam.getChildren().get(0).getId();
                    familyList.put("first_cousin", firstCousin);
                }
                if (secondAuntOrUncleFam.getChildren().size() > 1) {
                    int secondCousin = secondAuntOrUncleFam.getChildren().get(1).getId();
                    familyList.put("second_cousin", secondCousin);
                }
            }
            if (!firstParentFam.getParents().isEmpty()) {
                int firstGrandparent = firstParentFam.getParents().get(0).getId();
                familyList.put("first_grandparent", firstGrandparent);
                FamilyPlayerImpl firstGrandparentFam = new FamilyPlayerImpl(firstGrandparent);

                if (firstGrandparentFam.getSibling() != null) {
                    int firstGreatAuntOrUncle = firstGrandparentFam.getSibling().getId();
                    familyList.put("first_great_aunt_or_uncle", firstGreatAuntOrUncle);
                }
                if (!firstGrandparentFam.getParents().isEmpty()) {
                    int firstGreatGrandparent = firstGrandparentFam.getParents().get(0).getId();
                    familyList.put("first_great_grandparent", firstGreatGrandparent);
                }
                if (firstGrandparentFam.getParents().size() > 1) {
                    int secondGreatGrandparent = firstGrandparentFam.getParents().get(1).getId();
                    familyList.put("second_great_grandparent", secondGreatGrandparent);
                }
            }
            if (firstParentFam.getParents().size() > 1) {
                int secondGrandparent = firstParentFam.getParents().get(1).getId();
                familyList.put("second_grandparent", secondGrandparent);
                FamilyPlayerImpl secondGrandparentFam = new FamilyPlayerImpl(secondGrandparent);

                if (secondGrandparentFam.getSibling() != null) {
                    int secondGreatAuntOrUncle = secondGrandparentFam.getSibling().getId();
                    familyList.put("second_great_aunt_or_uncle", secondGreatAuntOrUncle);
                }
                if (!secondGrandparentFam.getParents().isEmpty()) {
                    int thirdGreatGrandparent = secondGrandparentFam.getParents().get(0).getId();
                    familyList.put("third_great_grandparent", thirdGreatGrandparent);
                }
                if (secondGrandparentFam.getParents().size() > 1) {
                    int fourthGreatGrandparent = secondGrandparentFam.getParents().get(1).getId();
                    familyList.put("fourth_great_grandparent", fourthGreatGrandparent);
                }
            }
        }

        if (this.getParents().size() > 1) {
            int secondParent = this.getParents().get(1).getId();
            familyList.put("second_parent", secondParent);
            FamilyPlayerImpl secondParentFam = new FamilyPlayerImpl(secondParent);

            if (secondParentFam.getSibling() != null) {
                int thirdAuntOrUncle = secondParentFam.getSibling().getId();
                familyList.put("third_aunt_or_uncle", thirdAuntOrUncle);
                FamilyPlayerImpl thirdAuntOrUncleFam = new FamilyPlayerImpl(thirdAuntOrUncle);
                if (thirdAuntOrUncleFam.getPartner() != null) {
                    int fourthAuntOrUncle = thirdAuntOrUncleFam.getPartner().getId();
                    familyList.put("fourth_aunt_or_uncle", fourthAuntOrUncle);
                }
                if (!thirdAuntOrUncleFam.getChildren().isEmpty()) {
                    int thirdCousin = thirdAuntOrUncleFam.getChildren().get(0).getId();
                    familyList.put("third_cousin", thirdCousin);
                }
                if (thirdAuntOrUncleFam.getChildren().size() > 1) {
                    int fourthCousin = thirdAuntOrUncleFam.getChildren().get(1).getId();
                    familyList.put("fourth_cousin", fourthCousin);
                }
            }
            if (!secondParentFam.getParents().isEmpty()) {
                int thirdGrandparent = secondParentFam.getParents().get(0).getId();
                familyList.put("third_grandparent", thirdGrandparent);
                FamilyPlayerImpl thirdGrandparentFam = new FamilyPlayerImpl(thirdGrandparent);

                if (thirdGrandparentFam.getSibling() != null) {
                    int thirdGreatAuntOrUncle = thirdGrandparentFam.getSibling().getId();
                    familyList.put("third_great_aunt_or_uncle", thirdGreatAuntOrUncle);
                }
                if (!thirdGrandparentFam.getParents().isEmpty()) {
                    int fifthGreatGrandparent = thirdGrandparentFam.getParents().get(0).getId();
                    familyList.put("fifth_great_grandparent", fifthGreatGrandparent);
                }
                if (thirdGrandparentFam.getParents().size() > 1) {
                    int sixthGreatGrandparent = thirdGrandparentFam.getParents().get(1).getId();
                    familyList.put("sixth_great_grandparent", sixthGreatGrandparent);
                }
            }
            if (secondParentFam.getParents().size() > 1) {
                int fourthGrandparent = secondParentFam.getParents().get(1).getId();
                familyList.put("fourth_grandparent", fourthGrandparent);
                FamilyPlayerImpl fourthGrandparentFam = new FamilyPlayerImpl(fourthGrandparent);

                if (fourthGrandparentFam.getSibling() != null) {
                    int fourthGreatAuntOrUncle = fourthGrandparentFam.getSibling().getId();
                    familyList.put("fourth_great_aunt_or_uncle", fourthGreatAuntOrUncle);
                }
                if (!fourthGrandparentFam.getParents().isEmpty()) {
                    int seventhGreatGrandparent = fourthGrandparentFam.getParents().get(0).getId();
                    familyList.put("seventh_great_grandparent", seventhGreatGrandparent);
                }
                if (fourthGrandparentFam.getParents().size() > 1) {
                    int eighthGreatGrandparent = fourthGrandparentFam.getParents().get(1).getId();
                    familyList.put("eighth_great_grandparent", eighthGreatGrandparent);
                }
            }
        }

        if (!this.getChildren().isEmpty()) {
            int firstChild = this.getChildren().get(0).getId();
            familyList.put("first_child", firstChild);
            FamilyPlayerImpl firstChildFam = new FamilyPlayerImpl(firstChild);

            if (firstChildFam.getPartner() != null) {
                int firstChildInLaw = firstChildFam.getPartner().getId();
                familyList.put("first_child_in_law", firstChildInLaw);
            }
            if (!firstChildFam.getChildren().isEmpty()) {
                int firstGrandchild = firstChildFam.getChildren().get(0).getId();
                familyList.put("first_grandchild", firstGrandchild);
                FamilyPlayerImpl firstGrandchildFam = new FamilyPlayerImpl(firstGrandchild);
                if (firstGrandchildFam.getPartner() != null) {
                    int firstGrandchildInLaw = firstGrandchildFam.getPartner().getId();
                    familyList.put("first_grandchild_in_law", firstGrandchildInLaw);
                }
                if (!firstGrandchildFam.getChildren().isEmpty()) {
                    int firstGreatGrandchild = firstGrandchildFam.getChildren().get(0).getId();
                    familyList.put("first_great_grandchild", firstGreatGrandchild);
                }
                if (firstGrandchildFam.getChildren().size() > 1) {
                    int secondGreatGrandchild = firstGrandchildFam.getChildren().get(1).getId();
                    familyList.put("second_great_grandchild", secondGreatGrandchild);
                }
            }
            if (firstChildFam.getChildren().size() > 1) {
                int secondGrandchild = firstChildFam.getChildren().get(1).getId();
                familyList.put("second_grandchild", secondGrandchild);
                FamilyPlayerImpl secondGrandchildFam = new FamilyPlayerImpl(secondGrandchild);
                if (secondGrandchildFam.getPartner() != null) {
                    int secondGrandchildInLaw = secondGrandchildFam.getPartner().getId();
                    familyList.put("second_grandchild_in_law", secondGrandchildInLaw);
                }
                if (!secondGrandchildFam.getChildren().isEmpty()) {
                    int thirdGreatGrandchild = secondGrandchildFam.getChildren().get(0).getId();
                    familyList.put("third_great_grandchild", thirdGreatGrandchild);
                }
                if (secondGrandchildFam.getChildren().size() > 1) {
                    int fourthGreatGrandchild = secondGrandchildFam.getChildren().get(1).getId();
                    familyList.put("fourth_great_grandchild", fourthGreatGrandchild);
                }
            }
        }

        if (this.getChildren().size() > 1) {
            int secondChild = this.getChildren().get(1).getId();
            familyList.put("second_child", secondChild);
            FamilyPlayerImpl secondChildFam = new FamilyPlayerImpl(secondChild);

            if (secondChildFam.getPartner() != null) {
                int secondChildInLaw = secondChildFam.getPartner().getId();
                familyList.put("second_child_in_law", secondChildInLaw);
            }
            if (!secondChildFam.getChildren().isEmpty()) {
                int thirdGrandchild = secondChildFam.getChildren().get(0).getId();
                familyList.put("third_grandchild", thirdGrandchild);
                FamilyPlayerImpl thirdGrandchildFam = new FamilyPlayerImpl(thirdGrandchild);
                if (thirdGrandchildFam.getPartner() != null) {
                    int thirdGrandchildInLaw = thirdGrandchildFam.getPartner().getId();
                    familyList.put("third_grandchild_in_law", thirdGrandchildInLaw);
                }
                if (!thirdGrandchildFam.getChildren().isEmpty()) {
                    int fifthGreatGrandchild = thirdGrandchildFam.getChildren().get(0).getId();
                    familyList.put("fifth_great_grandchild", fifthGreatGrandchild);
                }
                if (thirdGrandchildFam.getChildren().size() > 1) {
                    int sixthGreatGrandchild = thirdGrandchildFam.getChildren().get(1).getId();
                    familyList.put("sixth_great_grandchild", sixthGreatGrandchild);
                }
            }
            if (secondChildFam.getChildren().size() > 1) {
                int fourthGrandchild = secondChildFam.getChildren().get(1).getId();
                familyList.put("fourth_grandchild", fourthGrandchild);
                FamilyPlayerImpl fourthGrandchildFam = new FamilyPlayerImpl(fourthGrandchild);
                if (fourthGrandchildFam.getPartner() != null) {
                    int fourthGrandchildInLaw = fourthGrandchildFam.getPartner().getId();
                    familyList.put("fourth_grandchild_in_law", fourthGrandchildInLaw);
                }
                if (!fourthGrandchildFam.getChildren().isEmpty()) {
                    int seventhGreatGrandchild = fourthGrandchildFam.getChildren().get(0).getId();
                    familyList.put("seventh_great_grandchild", seventhGreatGrandchild);
                }
                if (fourthGrandchildFam.getChildren().size() > 1) {
                    int eighthGreatGrandchild = fourthGrandchildFam.getChildren().get(1).getId();
                    familyList.put("eighth_great_grandchild", eighthGreatGrandchild);
                }
            }
        }

        return familyList;
    }

    public boolean updateFamilyTree() {
        if (player.isOnline() && LunaticFamily.getConfig().isUseCrazyAdvancementAPI()) {
            FamilyTree familyTree = LunaticFamily.getPlatform().getFamilyTree();

            if (familyTree == null) {
                Logger.errorLog("FamilyTree is null. Please check if CrazyAdvancementsAPI is installed or disable it!");
                return false;
            } else {
                return familyTree.update(player.getServerName(), this.uuid, this.id);
            }
        }
        return true;
    }

    public List<Marriage> getMarriages() {
        return MarriagesTable.getPlayersMarriages(this.id);
    }

    public List<Siblinghood> getSiblinghoods() {
        return SiblinghoodsTable.getPlayersSiblinghoods(this.id);
    }

    public List<Adoption> getAdoptionsAsParent() {
        return AdoptionsTable.getPlayerAsParentAdoptions(this.id);
    }

    public List<Adoption> getAdoptionsAsChild() {
        return AdoptionsTable.getPlayerAsChildAdoptions(this.id);
    }
}


