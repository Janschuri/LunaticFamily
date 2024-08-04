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

    private static final BiMap<UUID, Integer> ids = HashBiMap.create();
    private static final List<Integer> isLoaded = new ArrayList<>();
    private static final Map<Integer, String> names = new HashMap<>();
    private static final Map<Integer, String> skinURLs = new HashMap<>();
    private static final Map<Integer, String> genders = new HashMap<>();
    private static final Map<Integer, String> backgrounds = new HashMap<>();
    private static final Map<Integer, PlayerSender> players = new HashMap<>();
    private static final Map<Integer, Map<Integer, String>> familyLists = new HashMap<>();
    private static final Map<Integer, List<Adoption>> adoptionsAsParent = new HashMap<>();
    private static final Map<Integer, List<Siblinghood>> siblinghoods = new HashMap<>();
    private static final Map<Integer, List<Adoption>> adoptionsAsChild = new HashMap<>();
    private static final Map<Integer, List<Marriage>> marriages = new HashMap<>();

    public static final String DEFAULT_SKIN = "http://textures.minecraft.net/texture/2705fd94a0c431927fb4e639b0fcfb49717e412285a02b439e0112da22b2e2ec";


    public FamilyPlayerImpl(int id) {
        this(
                Objects.requireNonNull(getUUID(id)),
                Objects.requireNonNull(getName(id))
        );
    }

    public FamilyPlayerImpl(UUID uuid) {
        this(uuid, null);
    }

    public FamilyPlayerImpl(UUID uuid, String name) {
        this.uuid = uuid;

        if (ids.containsKey(uuid)) {
            id = ids.get(uuid);
        } else {
            Logger.debugLog("UUID " + uuid + " not found in the map.");

            int id = PlayerDataTable.getID(uuid);

            if (id < 1) {
                savePlayerData();
                this.id = PlayerDataTable.getID(uuid);
            } else {
                this.id = id;
            }

            Logger.debugLog("Put UUID " + uuid + " with ID " + this.id + " into the map.");
            ids.put(uuid, this.id);
        }

        if (name != null) {
            names.put(id, name);
        }

        if (!isLoaded.contains(id)) {
            update();
        }
    }

    public void update() {

        isLoaded.add(id);

        players.put(id, LunaticLib.getPlatform().getPlayerSender(uuid));

        if (getPlayer().getName() != null) {
            names.put(id, getPlayer().getName());
        }

        if (getName() == null) {
            names.put(id, PlayerDataTable.getName(id));
        }

        if (getName() == null) {
            names.put(id, "unknown");
        }

        skinURLs.put(id, getPlayer().getSkinURL());

        if (getSkinURL() == null) {
            skinURLs.put(id, PlayerDataTable.getSkinURL(id));
        }
        if (getSkinURL() == null) {
            skinURLs.put(id, DEFAULT_SKIN);
        }

        String gender = PlayerDataTable.getGender(id);
        if (gender == null) {
            genders.put(id, LunaticFamily.getConfig().getDefaultGender());
        } else {
            genders.put(id, gender);
        }


        String background = PlayerDataTable.getBackground(id);
        if (background == null) {
            backgrounds.put(id, LunaticFamily.getConfig().getDefaultBackground());
        } else {
            backgrounds.put(id, background);
        }

        marriages.put(id, MarriagesTable.getPlayersMarriages(id));
        siblinghoods.put(id, SiblinghoodsTable.getPlayersSiblinghoods(id));
        adoptionsAsParent.put(id, AdoptionsTable.getPlayerAsParentAdoptions(id));
        adoptionsAsChild.put(id, AdoptionsTable.getPlayerAsChildAdoptions(id));

        loadFamilyMap();

        savePlayerData();
    }

    public static String getName(int id) {
        if (names.containsKey(id)) {
            return names.get(id);
        } else {
            String name = PlayerDataTable.getName(id);
            names.put(id, name);
            return name;
        }
    }

    public static int getID(UUID uuid) {
        if (ids.containsKey(uuid)) {
            return ids.get(uuid);
        } else {
            int id = PlayerDataTable.getID(uuid);
            ids.put(uuid, id);
            return id;
        }
    }

    public static UUID getUUID(int id) {
        if (ids.containsValue(id)) {
            return ids.inverse().get(id);
        } else {
            UUID uuid = PlayerDataTable.getUUID(id);
            ids.put(uuid, id);
            return uuid;
        }
    }

    public String getName() {
        return names.get(id);
    }

    public PlayerSender getPlayer() {
        return players.get(id);
    }

    public int getId() {
        return id;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    private void savePlayerData() {
        if (id < 1) {
            PlayerDataTable.savePlayerData(uuid.toString(), getName(), getSkinURL(), getGender(), getBackground());
        } else {
            PlayerDataTable.updatePlayerData(id, uuid.toString(), getName(), getSkinURL(), getGender(), getBackground());
        }
    }

    private void saveMarriage(int partnerID, int priestID) {
        MarriagesTable.saveMarriage(this.id, partnerID, priestID);
        update();
    }

    private void divorceMarriage() {
        MarriagesTable.divorceMarriage(this.id);
        update();
    }

    private void saveSiblinghood(int siblingID, int priestID) {
        SiblinghoodsTable.saveSiblinghood(this.id, siblingID, priestID);
        update();
    }

    private void unsiblingSiblinghood() {
        SiblinghoodsTable.unsiblingSiblinghood(this.id);
        update();
    }

    private void saveAdoption(int childID, int priestID) {
        AdoptionsTable.saveAdoption(this.id, childID, priestID);
        update();
    }

    private void unadoptAdoption(int childID) {
        AdoptionsTable.unadoptAdoption(this.id, childID);
        update();
    }

    public String getSkinURL() {
        return skinURLs.get(id);
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
        return genders.get(id);
    }

    public void setGender(String gender) {
        genders.put(id, gender);
        savePlayerData();
    }

    public String getBackground() {
        return backgrounds.get(id);
    }

    public void setBackground(String background) {
        backgrounds.put(id, "textures/block/" + background + ".png");
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
        return getFamilyMap().containsKey(id);
    }

    @Override
    public Map<Integer, String> getFamilyMap() {
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
        return marriages.get(this.id);
    }

    public List<Siblinghood> getSiblinghoods() {
        return siblinghoods.get(this.id);
    }

    public List<Adoption> getAdoptionsAsParent() {
        return adoptionsAsParent.get(this.id);
    }

    public List<Adoption> getAdoptionsAsChild() {
        return adoptionsAsChild.get(this.id);
    }
}


