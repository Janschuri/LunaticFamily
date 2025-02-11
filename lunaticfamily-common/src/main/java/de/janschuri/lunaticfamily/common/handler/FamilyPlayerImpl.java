package de.janschuri.lunaticfamily.common.handler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticfamily.FamilyPlayer;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.FamilyTreeManager;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;
import io.ebean.annotation.Identity;
import io.ebean.annotation.NotNull;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "playerData")
public class FamilyPlayerImpl implements FamilyPlayer {


    @Id
    @Identity
    @NotNull
    private int id;
    private final UUID uuid;
    private String name;
    @Column(name = "skinURL")
    private String skinURL;
    private String gender;
    private String background;

    @OneToMany(mappedBy = "player1")
    private List<Marriage> marriagesAsPlayer1 = new ArrayList<>();
    @OneToMany(mappedBy = "player2")
    private List<Marriage> marriagesAsPlayer2 = new ArrayList<>();
    @OneToMany(mappedBy = "player1")
    private List<Siblinghood> siblinghoodsAsPlayer1 = new ArrayList<>();
    @OneToMany(mappedBy = "player2")
    private List<Siblinghood> siblinghoodsAsPlayer2 = new ArrayList<>();
    @OneToMany(mappedBy = "parent")
    private List<Adoption> adoptionsAsParent = new ArrayList<>();
    @OneToMany(mappedBy = "child")
    private List<Adoption> adoptionsAsChild = new ArrayList<>();

    @Transient
    private final Map<Integer, String> familyMap = new HashMap<>();

    private static final BiMap<UUID, Integer> ids = HashBiMap.create();
    private static final Map<Integer, FamilyPlayerImpl> familyPlayerMap = new HashMap<>();

    public static final String DEFAULT_SKIN = "http://textures.minecraft.net/texture/2705fd94a0c431927fb4e639b0fcfb49717e412285a02b439e0112da22b2e2ec";

    public static FamilyPlayerImpl find(int id) {
        if (familyPlayerMap.containsKey(id)) {
            return familyPlayerMap.get(id);
        } else {
            FamilyPlayerImpl familyPlayer = DatabaseRepository.getDatabase().find(FamilyPlayerImpl.class).where().eq("id", id).findOne();
            if (familyPlayer == null) {
                return null;
            }
            familyPlayer.loadFamilyMap();
            ids.put(familyPlayer.uuid, id);
            familyPlayerMap.put(id, familyPlayer);
            return familyPlayer;
        }
    }

    public static FamilyPlayerImpl findOrCreate(@org.jetbrains.annotations.NotNull UUID uuid) {
        if (ids.containsKey(uuid)) {
            return find(ids.get(uuid));
        } else {
            FamilyPlayerImpl familyPlayer = DatabaseRepository.getDatabase().find(FamilyPlayerImpl.class).where().eq("uuid", uuid).findOne();

            if (familyPlayer == null) {
                familyPlayer = new FamilyPlayerImpl(uuid);
                familyPlayer.save();
            }

            familyPlayer.loadFamilyMap();
            ids.put(uuid, familyPlayer.id);
            familyPlayerMap.put(familyPlayer.id, familyPlayer);

            return familyPlayer;
        }
    }

    private FamilyPlayerImpl(@org.jetbrains.annotations.NotNull UUID uuid) {
        this.uuid = uuid;
        PlayerSender player = LunaticLib.getPlatform().getPlayerSender(uuid);
        this.name = player.getName();
        this.name = this.name == null ? "null" : this.name;
        this.skinURL = player.getSkinURL();
        this.skinURL = this.skinURL == null ? DEFAULT_SKIN : this.skinURL;
        this.gender = LunaticFamily.getConfig().getDefaultGender();
        this.background = LunaticFamily.getConfig().getDefaultBackground();
    }

    public void update() {
        DatabaseRepository.getDatabase().update(this);
        loadFamilyMap();
        updateFamilyTree();
    }

    public String getName() {
        return name;
    }

    public FamilyPlayerImpl setName(String name) {
        if (name == null) {
            return this;
        }

        if (name.equals(this.name)) {
            return this;
        }

        this.name = name;
        return this;
    }

    public int getId() {
        return id;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public int save() {
        int idInsert;
        if (id < 1) {
            FamilyPlayerImpl familyPlayer = DatabaseRepository.getDatabase().find(FamilyPlayerImpl.class).where().eq("uuid", uuid).findOne();
            if (familyPlayer != null) {
                idInsert = familyPlayer.id;
            } else {
                idInsert = -1;
            }

        } else {
            DatabaseRepository.getDatabase().update(this);
            idInsert = id;
        }

        Logger.debugLog("Saving FamilyPlayer: " + idInsert);

        DatabaseRepository.getDatabase().save(this);
        int id = DatabaseRepository.getDatabase().find(FamilyPlayerImpl.class).where().eq("uuid", uuid).findOne().id;
        return id;
    }

    public String getSkinURL() {
        return skinURL;
    }

    public FamilyPlayerImpl getPartner() {
        if (getMarriages().isEmpty()) {
            return null;
        }
        return getMarriages().get(0).getPartner(id);
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

        return getSiblinghoods().get(0).getSibling(id);
    }

    public List<FamilyPlayerImpl> getSiblings() {
        if (getSiblinghoods().isEmpty()) {
            return null;
        }

        List<FamilyPlayerImpl> siblings = new ArrayList<>();

        for (Siblinghood siblinghood : getSiblinghoods()) {
            siblings.add(siblinghood.getSibling(id));
        }

        return siblings;
    }

    public boolean hasSiblings() {
        return !getSiblinghoods().isEmpty();
    }

    public boolean isAdopted() {
        return !getAdoptionsAsChild().isEmpty();
    }

    public boolean isChildOf(int parentID) {
        for (Adoption adoption : getAdoptionsAsChild()) {
            if (adoption.getParent().getId() == parentID) {
                return true;
            }
        }
        return false;
    }

    public Adoption getAdoptionAsParent(int childID) {
        for (Adoption adoption : getAdoptionsAsParent()) {
            if (adoption.getChild().getId() == childID) {
                return adoption;
            }
        }
        return null;
    }

    public Adoption getAdoptionAsChild(int parentID) {
        for (Adoption adoption : getAdoptionsAsChild()) {
            if (adoption.getParent().getId() == parentID) {
                return adoption;
            }
        }
        return null;
    }

    public List<FamilyPlayerImpl> getParents() {
        List<FamilyPlayerImpl> list = new ArrayList<>();

        for (Adoption adoption : getAdoptionsAsChild()) {
            list.add(adoption.getParent());
        }

        return list;
    }

    public List<FamilyPlayerImpl> getChildren() {
        List<FamilyPlayerImpl> list = new ArrayList<>();

        for (Adoption adoption : getAdoptionsAsParent()) {
            list.add(adoption.getChild());
        }

        return list;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBackground() {
        return this.background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public Integer getChildrenAmount() {
        return getAdoptionsAsParent().size();
    }

    public void marry(int partnerID) {
        marry(partnerID, -1);
    }

    public void marry(int partnerID, int priestID) {
        if (isFamilyMember(partnerID)) {
            Logger.errorLog("Cancelled marriage. Player is already a family member.");
            return;
        }

        if (partnerID == this.id) {
            Logger.errorLog("Cancelled marriage. Player can't marry himself.");
            return;
        }

        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl partnerFam = find(partnerID);
        List<FamilyPlayerImpl> playerChildren = playerFam.getChildren();
        List<FamilyPlayerImpl> partnerChildren = partnerFam.getChildren();

        for (FamilyPlayerImpl child : playerChildren) {
            Adoption adoption = new Adoption(playerFam, child)
                    .setPriest(find(priestID))
                    .save();

            for (FamilyPlayerImpl partnerChild : partnerChildren) {
                Siblinghood siblinghood = new Siblinghood(child, partnerChild)
                        .setPriest(find(priestID))
                        .save();
            }
        }
        for (FamilyPlayerImpl child : partnerChildren) {
            Adoption adoption = new Adoption(partnerFam, child)
                    .setPriest(find(priestID))
                    .save();
        }

        Marriage marriage = new Marriage(playerFam, partnerFam)
                .setPriest(find(priestID))
                .save();

        playerFam.update();
    }

    public void divorce() {
        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl partnerFam = playerFam.getPartner();

        List<Adoption> adoptions = playerFam.getAdoptionsAsParent();
        for (Adoption adoption : adoptions) {
            adoption.setUnadoptDate()
                    .save();
        }

        Marriage marriage = playerFam.getMarriage()
                .setDivorceDate()
                .save();

        if (!LunaticFamily.getConfig().isAllowSingleAdopt()) {
            List<Adoption> partnerAdoptions = partnerFam.getAdoptionsAsParent();
            for (Adoption adoption : partnerAdoptions) {
                adoption.setUnadoptDate()
                        .save();
            }
        }

        playerFam.update();
    }

    public void adopt(int childID) {
        adopt(childID, -1);
    }

    public void adopt(int childID, int priestID) {
        if (isFamilyMember(childID)) {
            Logger.errorLog("Cancelled adoption. Player is already a family member.");
            return;
        }

        if (childID == this.id) {
            Logger.errorLog("Cancelled adoption. Player can't adopt himself.");
            return;
        }

        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl childFam = find(childID);

        new Adoption(playerFam, childFam)
                .setPriest(find(priestID))
                .save();

        if (playerFam.isMarried()) {
            FamilyPlayerImpl partnerFam = playerFam.getPartner();
            Adoption partnerAdoption = new Adoption(partnerFam, childFam)
                    .setPriest(find(priestID))
                    .save();
        }

        List<Adoption> adoptions = playerFam.getAdoptionsAsParent();

        for (Adoption adoption : adoptions) {
            if (adoption.getChild().getId() == childFam.getId()) {
                continue;
            }
            new Siblinghood(childFam, adoption.getChild())
                    .setPriest(find(priestID))
                    .save();
        }

        if (childFam.hasSiblings()) {
            List<Siblinghood> siblinghoods = childFam.getSiblinghoods();

            for (Siblinghood siblinghood : siblinghoods) {
                FamilyPlayerImpl siblingFam = siblinghood.getSibling(childID);
                new Adoption(playerFam, siblingFam)
                        .setPriest(find(priestID))
                        .save();

                if (playerFam.isMarried()) {
                    FamilyPlayerImpl partnerFam = playerFam.getPartner();
                    new Adoption(partnerFam, siblingFam)
                            .setPriest(find(priestID))
                            .save();
                }

                for (Adoption adoption : adoptions) {
                    if (adoption.getChild().getId() == siblingFam.getId()) {
                        continue;
                    }
                    new Siblinghood(siblingFam, adoption.getChild())
                            .setPriest(find(priestID))
                            .save();
                }
            }
        }

        playerFam.update();
    }

    public void unadopt(int childID) {
        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl childFam = find(childID);

        if (!playerFam.isChildOf(childID)) {
            Logger.errorLog("Cancelled unadoption. Player is not a child of the player.");
            return;
        }

        playerFam.getAdoptionAsParent(childID)
                .setUnadoptDate()
                .save();

        if (playerFam.isMarried()) {
            FamilyPlayerImpl partnerFam = playerFam.getPartner();
            partnerFam.getAdoptionAsParent(childID)
                    .setUnadoptDate()
                    .save();
        }

        if (childFam.hasSiblings()) {
            for (Siblinghood siblinghood : childFam.getSiblinghoods()) {
                siblinghood.setUnsiblingDate()
                        .save();
            }
        }

        playerFam.update();
    }

    public void addSibling(int siblingID) {
        addSibling(siblingID, -1);
    }

    public void addSibling(int siblingID, int priestID) {
        if (isFamilyMember(siblingID)) {
            Logger.errorLog("Cancelled Siblinghood. Player is already a family member.");
            return;
        }

        if (siblingID == this.id) {
            Logger.errorLog("Cancelled Siblinghood. Player can't be sibling to himself.");
            return;
        }

        FamilyPlayerImpl playerFam = this;
        FamilyPlayerImpl siblingFam = find(siblingID);

        if (playerFam.isAdopted()) {
            Logger.errorLog("Cancelled Siblinghood. Player is adopted. Try to adopt the sibling instead.");
            return;
        }

        if (siblingFam.isAdopted()) {
            Logger.errorLog("Cancelled Siblinghood. Sibling is adopted. Try to adopt the player instead.");
            return;
        }

        new Siblinghood(playerFam, siblingFam)
                .setPriest(find(priestID))
                .save();

        List<Siblinghood> siblinghoods = playerFam.getSiblinghoods();


        for (Siblinghood siblinghood : siblinghoods) {
            if (siblinghood.getPlayer1().getId() == siblingFam.getId() || siblinghood.getPlayer2().getId() == siblingFam.getId()) {
                continue;
            }

            List<Siblinghood> siblingSiblinghoods = siblingFam.getSiblinghoods();

            for (Siblinghood siblingSiblinghood : siblingSiblinghoods) {
                if (siblingSiblinghood.getPlayer1().getId() == playerFam.getId() || siblingSiblinghood.getPlayer2().getId() == playerFam.getId()) {
                    continue;
                }

                new Siblinghood(siblinghood.getSibling(playerFam.getId()), siblingSiblinghood.getSibling(siblingFam.getId()))
                        .setPriest(find(priestID))
                        .save();
            }
        }


        playerFam.update();;
    }

    public void removeSiblings() {
        FamilyPlayerImpl playerFam = this;

        List<Siblinghood> siblinghoods = playerFam.getSiblinghoods();

        for (Siblinghood siblinghood : siblinghoods) {
            siblinghood.setUnsiblingDate()
                    .save();
        }

        playerFam.update();
    }

    public boolean isFamilyMember(int id) {
        FamilyPlayerImpl familyPlayer = find(id);
        if (familyPlayer == null) {
            return false;
        }

        return familyPlayer.getFamilyMap().containsKey(this.id) || this.getFamilyMap().containsKey(id);
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
            FamilyPlayerImpl partnerFam = find(partner);

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
            FamilyPlayerImpl siblingFam = find(sibling);

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
            FamilyPlayerImpl firstParentFam = find(firstParent);
            if (firstParentFam.getSibling() != null) {
                int secondAuntOrUncle = firstParentFam.getSibling().getId();
                familyList.put(secondAuntOrUncle, "second_aunt_or_uncle");
                FamilyPlayerImpl secondAuntOrUncleFam = find(secondAuntOrUncle);
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
                FamilyPlayerImpl firstGrandparentFam = find(firstGrandparent);

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
                FamilyPlayerImpl secondGrandparentFam = find(secondGrandparent);

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
            FamilyPlayerImpl secondParentFam = find(secondParent);

            if (secondParentFam.getSibling() != null) {
                int thirdAuntOrUncle = secondParentFam.getSibling().getId();
                familyList.put(thirdAuntOrUncle, "third_aunt_or_uncle");
                FamilyPlayerImpl thirdAuntOrUncleFam = find(thirdAuntOrUncle);
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
                FamilyPlayerImpl thirdGrandparentFam = find(thirdGrandparent);

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
                FamilyPlayerImpl fourthGrandparentFam = find(fourthGrandparent);

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

        if (!this.getChildren().isEmpty() && this.getChildren().get(0) != null) {
            int firstChild = this.getChildren().get(0).getId();
            familyList.put(firstChild, "first_child");
            FamilyPlayerImpl firstChildFam = find(firstChild);

            if (firstChildFam.getPartner() != null) {
                int firstChildInLaw = firstChildFam.getPartner().getId();
                familyList.put(firstChildInLaw, "first_child_in_law");
            }
            if (!firstChildFam.getChildren().isEmpty()) {
                int firstGrandchild = firstChildFam.getChildren().get(0).getId();
                familyList.put(firstGrandchild, "first_grandchild");
                FamilyPlayerImpl firstGrandchildFam = find(firstGrandchild);
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
                FamilyPlayerImpl secondGrandchildFam = find(secondGrandchild);
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

        if (this.getChildren().size() > 1 && this.getChildren().get(0) != null) {
            int secondChild = this.getChildren().get(1).getId();
            familyList.put(secondChild, "second_child");
            FamilyPlayerImpl secondChildFam = find(secondChild);

            if (secondChildFam.getPartner() != null) {
                int secondChildInLaw = secondChildFam.getPartner().getId();
                familyList.put(secondChildInLaw, "second_child_in_law");
            }
            if (!secondChildFam.getChildren().isEmpty()) {
                int thirdGrandchild = secondChildFam.getChildren().get(0).getId();
                familyList.put(thirdGrandchild, "third_grandchild");
                FamilyPlayerImpl thirdGrandchildFam = find(thirdGrandchild);
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
                FamilyPlayerImpl fourthGrandchildFam = find(fourthGrandchild);
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

    public Marriage getMarriage() {
        return getMarriages().get(0);
    }

    public List<Marriage> getMarriages() {
        List<Marriage> marriages = new ArrayList<>();

        marriages.addAll(marriagesAsPlayer1);
        marriages.addAll(marriagesAsPlayer2);

        return marriages;
    }

    public List<Siblinghood> getSiblinghoods() {
        List<Siblinghood> siblinghoods = new ArrayList<>();

        siblinghoods.addAll(siblinghoodsAsPlayer1);
        siblinghoods.addAll(siblinghoodsAsPlayer2);

        return siblinghoods;
    }

    public List<Adoption> getAdoptionsAsParent() {
        return adoptionsAsParent;
    }

    public List<Adoption> getAdoptionsAsChild() {
        return adoptionsAsChild;
    }
}


