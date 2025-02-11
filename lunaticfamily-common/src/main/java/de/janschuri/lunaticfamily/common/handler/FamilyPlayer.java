package de.janschuri.lunaticfamily.common.handler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.familytree.FamilyTree;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.FamilyTreeManager;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;
import io.ebean.annotation.Identity;
import io.ebean.annotation.NotNull;
import io.ebean.annotation.Where;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "playerData")
public class FamilyPlayer {

    @Id
    @Identity
    @NotNull
    @GeneratedValue
    private int id;
    private final UUID uuid;
    private String name;
    @Column(name = "skinURL")
    private String skinURL;
    private String gender;
    private String background;

    @OneToMany(mappedBy = "player1")
    @Where(clause = "divorceDate IS NULL")
    private List<Marriage> marriagesAsPlayer1 = new ArrayList<>();
    @OneToMany(mappedBy = "player2")
    @Where(clause = "divorceDate IS NULL")
    private List<Marriage> marriagesAsPlayer2 = new ArrayList<>();
    @OneToMany(mappedBy = "priest")
    private List<Marriage> marriagesAsPriest = new ArrayList<>();

    @OneToMany(mappedBy = "player1")
    @Where(clause = "unsiblingDate IS NULL")
    private List<Siblinghood> siblinghoodsAsPlayer1 = new ArrayList<>();
    @OneToMany(mappedBy = "player2")
    @Where(clause = "unsiblingDate IS NULL")
    private List<Siblinghood> siblinghoodsAsPlayer2 = new ArrayList<>();
    @OneToMany(mappedBy = "priest")
    private List<Siblinghood> siblinghoodsAsPriest = new ArrayList<>();

    @OneToMany(mappedBy = "parent")
    @Where(clause = "unadoptDate IS NULL")
    private List<Adoption> adoptionsAsParent = new ArrayList<>();
    @OneToMany(mappedBy = "child")
    @Where(clause = "unadoptDate IS NULL")
    private List<Adoption> adoptionsAsChild = new ArrayList<>();
    @OneToMany(mappedBy = "priest")
    private List<Adoption> adoptionsAsPriest = new ArrayList<>();

    private static final BiMap<UUID, Integer> ids = HashBiMap.create();
    private static final Map<Integer, FamilyPlayer> familyPlayerMap = new HashMap<>();

    public static final String DEFAULT_SKIN = "https://textures.minecraft.net/texture/2705fd94a0c431927fb4e639b0fcfb49717e412285a02b439e0112da22b2e2ec";

    public static FamilyPlayer find(int id) {
        if (familyPlayerMap.containsKey(id)) {
            return familyPlayerMap.get(id);
        } else {
            FamilyPlayer familyPlayer = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("id", id).findOne();
            if (familyPlayer == null) {
                return null;
            }
            ids.put(familyPlayer.uuid, id);
            familyPlayerMap.put(id, familyPlayer);
            return familyPlayer;
        }
    }

    public static FamilyPlayer findOrCreate(@org.jetbrains.annotations.NotNull UUID uuid) {
        if (ids.containsKey(uuid)) {
            return find(ids.get(uuid));
        } else {
            FamilyPlayer familyPlayer = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("uuid", uuid).findOne();

            if (familyPlayer == null) {
                familyPlayer = new FamilyPlayer(uuid);
                familyPlayer.save();
            }

            ids.put(uuid, familyPlayer.id);
            familyPlayerMap.put(familyPlayer.id, familyPlayer);

            return familyPlayer;
        }
    }

    private FamilyPlayer(@org.jetbrains.annotations.NotNull UUID uuid) {
        this.uuid = uuid;
        PlayerSender player = LunaticLib.getPlatform().getPlayerSender(uuid);
        this.name = player.getName();
        this.name = this.name == null ? "null" : this.name;
        this.skinURL = player.getSkinURL();
        this.skinURL = this.skinURL == null ? DEFAULT_SKIN : this.skinURL;
        this.gender = LunaticFamily.getConfig().getDefaultGender();
        this.background = LunaticFamily.getConfig().getDefaultBackground();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FamilyPlayer that)) return false;
        return id == that.id;
    }


    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    public boolean update() {
        DatabaseRepository.getDatabase().update(this);
        return updateFamilyTree();
    }

    public String getName() {
        return name;
    }

    public FamilyPlayer setName(String name) {
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

    public UUID getUUID() {
        return uuid;
    }

    public FamilyPlayer save() {
        int idInsert;
        if (id < 1) {
            FamilyPlayer familyPlayer = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("uuid", uuid).findOne();
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
        return this;
    }

    public String getSkinURL() {
        return skinURL;
    }

    public FamilyPlayer getPartner() {
        if (getMarriages().isEmpty()) {
            return null;
        }
        return getMarriages().get(0).getPartner(this);
    }

    public boolean isMarried() {
        return !getMarriages().isEmpty();
    }

    public boolean hasChildren() {
        return !getAdoptionsAsParent().isEmpty();
    }

    public FamilyPlayer getSibling() {
        if (getSiblinghoods().isEmpty()) {
            return null;
        }

        return getSiblinghoods().get(0).getSibling(this);
    }

    public List<FamilyPlayer> getSiblings() {
        if (getSiblinghoods().isEmpty()) {
            return Collections.emptyList();
        }

        List<FamilyPlayer> siblings = new ArrayList<>();

        for (Siblinghood siblinghood : getSiblinghoods()) {
            siblings.add(siblinghood.getSibling(this));
        }

        return siblings;
    }

    public boolean hasSiblings() {
        return !getSiblinghoods().isEmpty();
    }

    public boolean isAdopted() {
        return !getAdoptionsAsChild().isEmpty();
    }

    public boolean isNotChildOf(FamilyPlayer parentFam) {
        for (Adoption adoption : getAdoptionsAsChild()) {
            if (adoption.getParent().equals(parentFam)) {
                return false;
            }
        }
        return true;
    }

    public Adoption getAdoptionAsParent(FamilyPlayer childFam) {
        for (Adoption adoption : getAdoptionsAsParent()) {
            if (adoption.getChild().equals(childFam)) {
                return adoption;
            }
        }
        return null;
    }

    public List<FamilyPlayer> getParents() {
        List<FamilyPlayer> list = new ArrayList<>();

        for (Adoption adoption : getAdoptionsAsChild()) {
            list.add(adoption.getParent());
        }

        return list;
    }

    public List<FamilyPlayer> getChildren() {
        List<FamilyPlayer> list = new ArrayList<>();

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

    public void marry(FamilyPlayer partnerFam) {
        marry(partnerFam, null);
    }

    public void marry(FamilyPlayer partnerFam, FamilyPlayer priestFam) {
        if (!canMarry(partnerFam)) return;

        createAdoptionsAndSiblings(partnerFam, priestFam);
        createMarriage(partnerFam, priestFam);

        this.update();
    }

    private boolean canMarry(FamilyPlayer partnerFam) {
        if (this.equals(partnerFam)) {
            Logger.errorLog("Cancelled marriage. Player can't marry himself.");
            return false;
        }

        if (isFamilyMember(partnerFam)) {
            Logger.errorLog("Cancelled marriage. Player is already a family member.");
            return false;
        }

        return true;
    }

    private void createAdoptionsAndSiblings(FamilyPlayer partnerFam, FamilyPlayer priestFam) {
        List<FamilyPlayer> playerChildren = this.getChildren();
        List<FamilyPlayer> partnerChildren = partnerFam.getChildren();

        for (FamilyPlayer child : playerChildren) {
            createAdoption(this, child, priestFam);
            linkSiblings(child, partnerChildren, priestFam);
        }

        for (FamilyPlayer child : partnerChildren) {
            createAdoption(partnerFam, child, priestFam);
        }
    }

    private void createAdoption(FamilyPlayer parentFam, FamilyPlayer childFam, FamilyPlayer priestFam) {
        new Adoption(parentFam, childFam)
                .setPriest(priestFam)
                .save();
    }

    private void linkSiblings(FamilyPlayer childFam, List<FamilyPlayer> partnerChildren, FamilyPlayer priestFam) {
        for (FamilyPlayer partnerChild : partnerChildren) {
            new Siblinghood(childFam, partnerChild)
                    .setPriest(priestFam)
                    .save();
        }
    }

    private void createMarriage(FamilyPlayer partnerFam, FamilyPlayer priestFam) {
        new Marriage(this, partnerFam)
                .setPriest(priestFam)
                .save();
    }

    public void divorce() {
        FamilyPlayer partnerFam = this.getPartner();

        processAdoptionsAsParent();
        processDivorce();
        processPartnerAdoptions(partnerFam);

        this.update();
    }

    private void processAdoptionsAsParent() {
        for (Adoption adoption : this.getAdoptionsAsParent()) {
            adoption.setUnadoptDate().save();
        }
    }

    private void processDivorce() {
        this.getMarriage()
                .setDivorceDate()
                .save();
    }

    private void processPartnerAdoptions(FamilyPlayer partnerFam) {
        if (!LunaticFamily.getConfig().isAllowSingleAdopt()) {
            for (Adoption adoption : partnerFam.getAdoptionsAsParent()) {
                adoption.setUnadoptDate().save();
            }
        }
    }

    public void adopt(FamilyPlayer childFam) {
        adopt(childFam, null);
    }

    public void adopt(FamilyPlayer childFam, FamilyPlayer priestFam) {
        if (!canAdopt(childFam)) return;

        processAdoption(childFam, priestFam);
        processMarriageAdoption(childFam, priestFam);
        processSiblingConnections(childFam, priestFam);

        this.update();
    }

    private boolean canAdopt(FamilyPlayer childFam) {
        if (this.equals(childFam)) {
            Logger.errorLog("Cancelled adoption. Player can't adopt himself.");
            return false;
        }

        if (isFamilyMember(childFam)) {
            Logger.errorLog("Cancelled adoption. Player is already a family member.");
            return false;
        }

        return true;
    }

    private void processAdoption(FamilyPlayer childFam, FamilyPlayer priestFam) {
        new Adoption(this, childFam).setPriest(priestFam).save();
    }

    private void processMarriageAdoption(FamilyPlayer childFam, FamilyPlayer priestFam) {
        if (!this.isMarried()) return;

        FamilyPlayer partnerFam = this.getPartner();
        new Adoption(partnerFam, childFam).setPriest(priestFam).save();
    }

    private void processSiblingConnections(FamilyPlayer childFam, FamilyPlayer priestFam) {
        List<Adoption> adoptions = this.getAdoptionsAsParent();

        for (Adoption adoption : adoptions) {
            if (adoption.getChild().getId() == childFam.getId()) continue;

            new Siblinghood(childFam, adoption.getChild()).setPriest(priestFam).save();
        }

        if (!childFam.hasSiblings()) return;

        for (Siblinghood siblinghood : childFam.getSiblinghoods()) {
            FamilyPlayer siblingFam = siblinghood.getSibling(childFam);
            processSiblingAdoption(siblingFam, priestFam, adoptions);
        }
    }

    private void processSiblingAdoption(FamilyPlayer siblingFam, FamilyPlayer priestFam, List<Adoption> adoptions) {
        new Adoption(this, siblingFam).setPriest(priestFam).save();

        if (this.isMarried()) {
            FamilyPlayer partnerFam = this.getPartner();
            new Adoption(partnerFam, siblingFam).setPriest(priestFam).save();
        }

        for (Adoption adoption : adoptions) {
            if (adoption.getChild().getId() == siblingFam.getId()) continue;

            new Siblinghood(siblingFam, adoption.getChild()).setPriest(priestFam).save();
        }
    }

    public void unadopt(FamilyPlayer childFam) {
        if (!canUnadopt(childFam)) return;

        processUnadoption(childFam);
        processMarriageUnadoption(childFam);
        processSiblingUnadoption(childFam);

        this.update();
    }

    private boolean canUnadopt(FamilyPlayer childFam) {
        if (childFam.isNotChildOf(this)) {
            Logger.errorLog("Cancelled unadoption. Player is not a child of the player.");
            return false;
        }
        return true;
    }

    private void processUnadoption(FamilyPlayer childFam) {
        this.getAdoptionAsParent(childFam)
                .setUnadoptDate()
                .save();
    }

    private void processMarriageUnadoption(FamilyPlayer childFam) {
        if (!this.isMarried()) return;

        FamilyPlayer partnerFam = this.getPartner();
        partnerFam.getAdoptionAsParent(childFam)
                .setUnadoptDate()
                .save();
    }

    private void processSiblingUnadoption(FamilyPlayer childFam) {
        if (!childFam.hasSiblings()) return;

        for (Siblinghood siblinghood : childFam.getSiblinghoods()) {
            siblinghood.setUnsiblingDate()
                    .save();
        }
    }

    public void addSibling(FamilyPlayer siblingFam) {
        addSibling(siblingFam, null);
    }

    public void addSibling(FamilyPlayer siblingFam, FamilyPlayer priestFam) {
        if (!canAddSibling(siblingFam)) return;

        createSiblinghood(this, siblingFam, priestFam);
        linkExtendedSiblings(siblingFam, priestFam);

        this.update();
    }

    private boolean canAddSibling(FamilyPlayer siblingFam) {
        if (this.equals(siblingFam)) {
            Logger.errorLog("Cancelled Siblinghood. Player can't be sibling to himself.");
            return false;
        }

        if (isFamilyMember(siblingFam)) {
            Logger.errorLog("Cancelled Siblinghood. Player is already a family member.");
            return false;
        }

        if (this.isAdopted()) {
            Logger.errorLog("Cancelled Siblinghood. Player is adopted. Try to adopt the sibling instead.");
            return false;
        }

        if (siblingFam.isAdopted()) {
            Logger.errorLog("Cancelled Siblinghood. Sibling is adopted. Try to adopt the player instead.");
            return false;
        }

        return true;
    }

    private void createSiblinghood(FamilyPlayer fam1, FamilyPlayer fam2, FamilyPlayer priestFam) {
        new Siblinghood(fam1, fam2)
                .setPriest(priestFam)
                .save();
    }

    private void linkExtendedSiblings(FamilyPlayer siblingFam, FamilyPlayer priestFam) {
        for (Siblinghood siblinghood : this.getSiblinghoods()) {
            FamilyPlayer existingSibling = siblinghood.getSibling(this);

            if (existingSibling.equals(siblingFam)) continue;

            for (Siblinghood siblingSiblinghood : siblingFam.getSiblinghoods()) {
                FamilyPlayer extendedSibling = siblingSiblinghood.getSibling(siblingFam);

                if (extendedSibling.equals(this)) continue;

                createSiblinghood(existingSibling, extendedSibling, priestFam);
            }
        }
    }

    public void removeSiblings() {
        if (!hasSiblings()) return;

        unsiblingAll();
        this.update();
    }

    private void unsiblingAll() {
        for (Siblinghood siblinghood : this.getSiblinghoods()) {
            siblinghood.setUnsiblingDate().save();
        }
    }

    public boolean isFamilyMember(FamilyPlayer familyPlayer) {
        if (familyPlayer == null) {
            return false;
        }

        return getFamilyTree().isFamilyMember(familyPlayer);
    }

    public FamilyTree getFamilyTree() {
        DatabaseRepository.getDatabase().refresh(this);
        FamilyTree familyTree = new FamilyTree(this);

        familyTree.update();

        return familyTree;
    }

    public boolean updateFamilyTree() {
        PlayerSender player = LunaticLib.getPlatform().getPlayerSender(this.uuid);

        if (player == null) {
            return true;
        }

        if (player.isOnline() && LunaticFamily.getConfig().isUseCrazyAdvancementAPI()) {
            FamilyTreeManager familyTreeManager = LunaticFamily.getPlatform().getFamilyTreeManager();

            if (familyTreeManager == null) {
                Logger.errorLog("FamilyTreeManager is null. Please check if CrazyAdvancementsAPI is installed or disable it!");
                return false;
            } else {

                String serverName = player.getServerName();

                return familyTreeManager.update(serverName, uuid, getFamilyTree().getTreeAdvancements());
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

    public List<Adoption> getAdoptionsAsPriest() {
        return adoptionsAsPriest;
    }

    public List<Marriage> getMarriagesAsPriest() {
        return marriagesAsPriest;
    }

    public List<Siblinghood> getSiblinghoodsAsPriest() {
        return siblinghoodsAsPriest;
    }
}


