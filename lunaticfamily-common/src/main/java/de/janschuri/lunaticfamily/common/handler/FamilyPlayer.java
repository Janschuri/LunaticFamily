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
import java.util.concurrent.CompletableFuture;

@Entity
@Table(name = "lunaticfamily_players")
public class FamilyPlayer {

    @Id
    @Identity
    @NotNull
    @GeneratedValue
    private long id;
    private final UUID uuid;
    private String name;
    private String skinURL;
    private String gender;
    private String background;

    @OneToMany(mappedBy = "player1")
    @Where(clause = "divorce_date IS NULL")
    private List<Marriage> marriagesAsPlayer1 = new ArrayList<>();
    @OneToMany(mappedBy = "player2")
    @Where(clause = "divorce_date IS NULL")
    private List<Marriage> marriagesAsPlayer2 = new ArrayList<>();
    @OneToMany(mappedBy = "priest")
    private final List<Marriage> marriagesAsPriest = new ArrayList<>();

    @OneToMany(mappedBy = "player1")
    @Where(clause = "unsibling_date IS NULL")
    private List<Siblinghood> siblinghoodsAsPlayer1 = new ArrayList<>();
    @OneToMany(mappedBy = "player2")
    @Where(clause = "unsibling_date IS NULL")
    private List<Siblinghood> siblinghoodsAsPlayer2 = new ArrayList<>();
    @OneToMany(mappedBy = "priest")
    private final List<Siblinghood> siblinghoodsAsPriest = new ArrayList<>();

    @OneToMany(mappedBy = "parent")
    @Where(clause = "unadopt_date IS NULL")
    private List<Adoption> adoptionsAsParent = new ArrayList<>();
    @OneToMany(mappedBy = "child")
    @Where(clause = "unadopt_date IS NULL")
    private List<Adoption> adoptionsAsChild = new ArrayList<>();
    @OneToMany(mappedBy = "priest")
    private final List<Adoption> adoptionsAsPriest = new ArrayList<>();

    private static final BiMap<UUID, Long> uuids = HashBiMap.create();
    private static final BiMap<String, Long> names = HashBiMap.create();
    private static final Map<Long, FamilyPlayer> familyPlayerMap = new HashMap<>();

    public static FamilyPlayer find(long id) {
        if (familyPlayerMap.containsKey(id)) {
            return familyPlayerMap.get(id);
        } else {
            FamilyPlayer familyPlayer = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("id", id).findOne();
            if (familyPlayer == null) {
                return null;
            }
            uuids.put(familyPlayer.uuid, id);
            names.put(familyPlayer.name, id);
            familyPlayerMap.put(id, familyPlayer);

            return familyPlayer;
        }
    }

    public static FamilyPlayer find(String name) {
        if (names.containsKey(name)) {
            long id = names.get(name);
            return find(id);
        } else {
            List<FamilyPlayer> familyPlayerList = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("name", name).findList();

            if (familyPlayerList.isEmpty()) {
                return null;
            }

            FamilyPlayer familyPlayer = familyPlayerList.get(0);

            if (familyPlayer == null) {
                return null;
            }
            long id = familyPlayer.getId();
            uuids.put(familyPlayer.uuid, id);
            names.put(name, id);
            familyPlayerMap.put(id, familyPlayer);

            return familyPlayer;
        }
    }

    public static FamilyPlayer find(UUID uuid) {
        if (uuids.containsKey(uuid)) {
            return find(uuids.get(uuid));
        } else {
            FamilyPlayer familyPlayer = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("uuid", uuid).findOne();
            if (familyPlayer == null) {
                return null;
            }
            uuids.put(uuid, familyPlayer.id);
            names.put(familyPlayer.name, familyPlayer.id);
            familyPlayerMap.put(familyPlayer.id, familyPlayer);

            return familyPlayer;
        }
    }

    public static FamilyPlayer findOrCreate(@org.jetbrains.annotations.NotNull UUID uuid) {
        FamilyPlayer familyPlayer = find(uuid);
        if (familyPlayer == null) {
            familyPlayer = new FamilyPlayer(uuid);
        }

        return familyPlayer;
    }

    private FamilyPlayer(@org.jetbrains.annotations.NotNull UUID uuid) {
        this.uuid = uuid;
        PlayerSender player = LunaticLib.getPlatform().getPlayerSender(uuid);
        String name = player.getName();
        this.name = name == null ? "unknown-name" : name;

        String skinURL = player.getSkinURL();
        this.skinURL = skinURL == null ? this.skinURL : skinURL;
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
        return Long.hashCode(id);
    }

    public void update() {
        DatabaseRepository.getDatabase().update(this);
        updateFamilyTree();
    }

    public void delete() {
        DatabaseRepository.getDatabase().delete(this);
    }

    public String getName() {
        if (name == null) {
            return "null";
        }

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

    public long getId() {
        return id;
    }

    public UUID getUUID() {
        return uuid;
    }

    public FamilyPlayer save() {
        DatabaseRepository.getDatabase().save(this);
        uuids.put(uuid, id);
        names.put(name, id);
        familyPlayerMap.put(id, this);
        return this;
    }

    public FamilyPlayer updateSkinURL(String skinURL) {
        if (skinURL == null) {
            return this;
        }

        if (skinURL.equals(this.skinURL)) {
            return this;
        }

        this.skinURL = skinURL;
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

    public boolean isMarriedTo(FamilyPlayer partnerFam) {
        if (partnerFam == null) {
            return false;
        }

        for (Marriage marriage : getMarriages()) {
            if (marriage.getPartner(this).equals(partnerFam)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasChildren() {
        return hasChildren(1);
    }

    public boolean hasChildren(int amount) {
        return getAdoptionsAsParent().size() >= amount;
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
        return hasSiblings(1);
    }

    public boolean hasSiblings(int amount) {
        return getSiblinghoods().size() >= amount;
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

    public Integer getSiblingsAmount() {
        return getSiblinghoods().size();
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
            createAdoption(partnerFam, child, priestFam);
            linkSiblings(child, partnerChildren, priestFam);
        }

        for (FamilyPlayer child : partnerChildren) {
            createAdoption(this, child, priestFam);
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

    public CompletableFuture<Boolean> updateFamilyTree() {
        PlayerSender player = LunaticLib.getPlatform().getPlayerSender(this.uuid);

        if (player == null) {
            return CompletableFuture.completedFuture(true);
        }

        if (player.isOnline() && LunaticFamily.getConfig().isUseCrazyAdvancementAPI()) {
            FamilyTreeManager familyTreeManager = LunaticFamily.getPlatform().getFamilyTreeManager();

            if (familyTreeManager == null) {
                Logger.errorLog("FamilyTreeManager is null. Please check if CrazyAdvancementsAPI is installed or disable it!");
                return CompletableFuture.completedFuture(false);
            } else {

                String serverName = player.getServerName();

                return familyTreeManager.update(serverName, uuid, getFamilyTree().getTreeAdvancements());
            }
        }
        return CompletableFuture.completedFuture(true);
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


