package de.janschuri.lunaticFamily.handler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.external.Vault;
import de.janschuri.lunaticFamily.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FamilyPlayer {


    private final int id;
    private final String uuid;
    private final String name;
    private final String skinURL;
    private final int partner;
    private final Timestamp marryDate;
    private final int priest;
    private final int sibling;

    private final List<Integer> parents;
    private final List<Integer> children;
    private String gender;
    private String background;
    private final BiMap<String, Integer> familyList = HashBiMap.create();

    public FamilyPlayer(int id) {

        this.id = id;

        uuid = LunaticFamily.getDatabase().getUUID(id);

        partner = LunaticFamily.getDatabase().getPartner(id);
        marryDate = LunaticFamily.getDatabase().getMarryDate(id);
        priest = LunaticFamily.getDatabase().getPriest(id);
        sibling = LunaticFamily.getDatabase().getSibling(id);
        parents = LunaticFamily.getDatabase().getParents(id);
        children = LunaticFamily.getDatabase().getChildren(id);


        if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() != null) {
            name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
        } else {
            name = LunaticFamily.getDatabase().getName(id);
        }

        if (Bukkit.getPlayer(UUID.fromString(uuid)) != null) {
            skinURL = Bukkit.getPlayer(UUID.fromString(uuid)).getPlayerProfile().getTextures().getSkin().toString();
        } else if (LunaticFamily.getDatabase().getSkinURL(id) != null) {
            skinURL = LunaticFamily.getDatabase().getSkinURL(id);
        } else {
            skinURL = "http://textures.minecraft.net/texture/2705fd94a0c431927fb4e639b0fcfb49717e412285a02b439e0112da22b2e2ec";
        }


        if (LunaticFamily.getDatabase().getGender(id) == null) {
            gender = PluginConfig.defaultGender;
        } else {
            gender = LunaticFamily.getDatabase().getGender(id);
        }

        if (LunaticFamily.getDatabase().getBackground(uuid) == null) {
            background = PluginConfig.defaultBackground;
        } else {
            background = LunaticFamily.getDatabase().getBackground(uuid);
        }


        savePlayerData();

    }

    public FamilyPlayer(String uuid) {

        this.uuid = uuid;

        if (LunaticFamily.getDatabase().getID(uuid) != 0) {
            id = LunaticFamily.getDatabase().getID(uuid);
        } else {
            savePlayerData();
            id = LunaticFamily.getDatabase().getID(uuid);
        }

        partner = LunaticFamily.getDatabase().getPartner(id);
        marryDate = LunaticFamily.getDatabase().getMarryDate(id);
        priest = LunaticFamily.getDatabase().getPriest(id);
        sibling = LunaticFamily.getDatabase().getSibling(id);
        parents = LunaticFamily.getDatabase().getParents(id);
        children = LunaticFamily.getDatabase().getChildren(id);


        if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() != null) {
            name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
        } else {
            name = LunaticFamily.getDatabase().getName(id);
        }

        if (Bukkit.getPlayer(UUID.fromString(uuid)) != null) {
            skinURL = Bukkit.getPlayer(UUID.fromString(uuid)).getPlayerProfile().getTextures().getSkin().toString();
        } else if (LunaticFamily.getDatabase().getSkinURL(id) != null) {
            skinURL = LunaticFamily.getDatabase().getSkinURL(id);
        } else {
            skinURL = "http://textures.minecraft.net/texture/2705fd94a0c431927fb4e639b0fcfb49717e412285a02b439e0112da22b2e2ec";
        }


        if (LunaticFamily.getDatabase().getGender(id) == null) {
            gender = PluginConfig.defaultGender;
        } else {
            gender = LunaticFamily.getDatabase().getGender(id);
        }

        if (LunaticFamily.getDatabase().getBackground(uuid) == null) {
            background = PluginConfig.defaultBackground;
        } else {
            background = LunaticFamily.getDatabase().getBackground(uuid);
        }


        savePlayerData();

    }

    public String getName() {
        if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() != null) {
            return Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
        } else {
            return LunaticFamily.getDatabase().getName(id);
        }
    }

    public int getID() {
        return id;
    }

    public String getUUID() {
        return uuid;
    }

    private void savePlayerData() {
        if (id == 0) {
            LunaticFamily.getDatabase().savePlayerData(uuid, name, skinURL, gender, background);
        } else {
            LunaticFamily.getDatabase().updatePlayerData(id, uuid, name, skinURL, gender, background);
        }
    }

    private void saveMarriage(int partnerID) {
        LunaticFamily.getDatabase().saveMarriage(this.id, partnerID);
    }

    private void saveMarriage(int partnerID, int priest) {
        LunaticFamily.getDatabase().saveMarriage(this.id, partnerID, priest);
    }

    private void saveMarriageHeartColor(String color) {
        LunaticFamily.getDatabase().saveMarriageHeartColor(this.id, color);
    }

    private void deleteMarriage() {
        LunaticFamily.getDatabase().deleteMarriage(this.id);
    }

    private void saveSiblinghood(int siblingID) {
        LunaticFamily.getDatabase().saveSiblinghood(this.id, siblingID);
    }

    private void deleteSiblinghood() {
        LunaticFamily.getDatabase().deleteSiblinghood(this.id);
    }

    private void saveAdoption(int childID) {
        LunaticFamily.getDatabase().saveAdoption(this.id, childID);
    }

    private void deleteAdoption(int childID) {
        LunaticFamily.getDatabase().deleteAdoption(this.id, childID);
    }

    public String getSkinURL() {
        return skinURL;
    }

    public void setHeartColor(String color) {
        saveMarriageHeartColor(color);
    }
    public TextColor getHeartColor() {
        return TextColor.fromHexString(LunaticFamily.getDatabase().getMarriageHeartColor(this.id));
    }

    public FamilyPlayer getPartner() {
        if (this.partner != 0) {
            return new FamilyPlayer(this.partner);
        } else {
            return null;
        }
    }

    public boolean isMarried() {
        return this.partner != 0;
    }

    public String getMarriageDate() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PluginConfig.dateFormat);

        return formatter.format(this.marryDate.toLocalDateTime());
    }

    public FamilyPlayer getPriest() {
        if (this.priest != 0) {
            return new FamilyPlayer(this.priest);
        } else {
            return null;
        }
    }

    public FamilyPlayer getSibling() {
        if (this.sibling != 0) {
            return new FamilyPlayer(this.sibling);
        } else {
            return null;
        }
    }

    public boolean hasSibling() {
        return this.sibling != 0;
    }

    public boolean isAdopted() {
        return !parents.isEmpty();
    }

    public boolean isChildOf(int parentID) {
        return parents.contains(parentID);
    }

    public List<FamilyPlayer> getParents() {
        List<FamilyPlayer> list = new ArrayList<>();

        for (int parent : this.parents) {
            list.add(new FamilyPlayer(parent));
        }

        return list;
    }

    public List<FamilyPlayer> getChildren() {
        List<FamilyPlayer> list = new ArrayList<>();

        for (int child : this.children) {
            list.add(new FamilyPlayer(child));
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
        return children.size();
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(UUID.fromString(this.uuid));
    }

    public Player getPlayer() {
        if (Bukkit.getPlayer(UUID.fromString(uuid)) != null) {
            return Bukkit.getPlayer(UUID.fromString(this.uuid));
        } else {
            return null;
        }
    }

    public boolean isOnline() {
        return Bukkit.getPlayer(UUID.fromString(uuid)) != null;
    }

    public void withdrawPlayer(String... withdrawKeys) {
        if (PluginConfig.enabledVault) {
            OfflinePlayer player = this.getOfflinePlayer();

            double amount = 0.0;
            for (String key : withdrawKeys) {
                if (PluginConfig.commandWithdraws.containsKey(key)) {
                    amount += PluginConfig.commandWithdraws.get(key);
                }
            }
            if (amount > 0) {
                Vault.getEconomy().withdrawPlayer(player, amount);
                if (player.getPlayer() != null) {
                    player.getPlayer().sendMessage(Language.prefix + Language.getMessage("withdraw").replace("%amount%", amount + ""));
                }
            }
        }
    }

    public void withdrawPlayer(String withdrawKey, double factor) {
        if (PluginConfig.enabledVault) {
            OfflinePlayer player = this.getOfflinePlayer();

            double amount = 0.0;
            amount += PluginConfig.commandWithdraws.get(withdrawKey) * factor;


            if (amount > 0) {
                Vault.getEconomy().withdrawPlayer(player, amount);
                if (player.getPlayer() != null) {
                    player.getPlayer().sendMessage(Language.prefix + Language.getMessage("withdraw").replace("%amount%", amount + ""));
                }
            }
        }
    }

    public boolean hasEnoughMoney(String... withdrawKeys) {
        if (PluginConfig.enabledVault) {
            OfflinePlayer player = this.getOfflinePlayer();
            double amount = 0.0;
            for (String key : withdrawKeys) {
                if (PluginConfig.commandWithdraws.containsKey(key)) {
                    amount += PluginConfig.commandWithdraws.get(key);
                }
            }
            return (amount < Vault.getEconomy().getBalance(player));
        } else {
            return true;
        }
    }

    public boolean hasEnoughMoney(String withdrawKey, double factor) {
        if (PluginConfig.enabledVault) {
            OfflinePlayer player = this.getOfflinePlayer();
            return (PluginConfig.commandWithdraws.get(withdrawKey) * factor < Vault.getEconomy().getBalance(player));
        } else {
            return true;
        }
    }

    public boolean sendMessage(String message) {
        if (this.getPlayer() != null) {
            this.getPlayer().sendMessage(message);
            return true;
        } else {
            return false;
        }
    }

    public boolean sendMessage(Component message) {
        if (this.getPlayer() != null) {
            this.getPlayer().sendMessage(message);
            return true;
        } else {
            return false;
        }
    }

    public boolean chat(String message) {
        if (this.getPlayer() != null) {
            this.getPlayer().chat(message);
            return true;
        } else {
            return false;
        }
    }


    public void marry(int partnerID) {
        FamilyPlayer playerFam = this;
        FamilyPlayer partnerFam = new FamilyPlayer(partnerID);
        List<FamilyPlayer> playerChildren = playerFam.getChildren();
        List<FamilyPlayer> partnerChildren = partnerFam.getChildren();

        for (FamilyPlayer child : playerChildren) {
            partnerFam.saveAdoption(child.getID());
        }
        for (FamilyPlayer child : partnerChildren) {
            playerFam.saveAdoption(child.getID());
        }

        if (playerChildren.size() == 1 && partnerChildren.size() == 1) {
            FamilyPlayer childFam = playerChildren.get(0);
            childFam.saveSiblinghood(partnerChildren.get(0).getID());
        }

        playerFam.saveMarriage(partnerID);
        playerFam.updateFamilyTree();
        partnerFam.updateFamilyTree();
    }

    public void marry(int partnerID, int priest) {
        FamilyPlayer playerFam = this;
        FamilyPlayer partnerFam = new FamilyPlayer(partnerID);
        List<FamilyPlayer> playerChildren = playerFam.getChildren();
        List<FamilyPlayer> partnerChildren = partnerFam.getChildren();

        for (FamilyPlayer child : playerChildren) {
            partnerFam.saveAdoption(child.getID());
        }
        for (FamilyPlayer child : partnerChildren) {
            playerFam.saveAdoption(child.getID());
        }

        if (playerChildren.size() == 1 && partnerChildren.size() == 1) {
            FamilyPlayer childFam = playerChildren.get(0);
            childFam.saveSiblinghood(partnerChildren.get(0).getID());
        }

        playerFam.saveMarriage(partnerID, priest);

        playerFam.updateFamilyTree();
        partnerFam.updateFamilyTree();
    }

    public void divorce() {
        FamilyPlayer playerFam = this;
        FamilyPlayer partnerFam = playerFam.getPartner();

        List<FamilyPlayer> playerChildren = playerFam.getChildren();
        for (FamilyPlayer child : playerChildren) {
            playerFam.deleteAdoption(child.getID());
        }

        playerFam.deleteMarriage();

        if (!PluginConfig.allowSingleAdopt) {
            for (FamilyPlayer child : playerChildren) {
                partnerFam.deleteAdoption(child.getID());
            }
        }

        playerFam.updateFamilyTree();
        partnerFam.updateFamilyTree();
    }

    public void adopt(int childID) {
        FamilyPlayer playerFam = this;
        FamilyPlayer childFam = new FamilyPlayer(childID);
        playerFam.saveAdoption(childID);

        if (playerFam.isMarried()) {
            FamilyPlayer partnerFam = playerFam.getPartner();
            partnerFam.saveAdoption(childID);
        }

        if (childFam.hasSibling()) {
            playerFam.adopt(childID);
            if (playerFam.isMarried()) {
                FamilyPlayer partnerFam = playerFam.getPartner();
                partnerFam.saveAdoption(childID);
            }
        }

        playerFam.updateFamilyTree();
        childFam.updateFamilyTree();
    }

    public void unadopt(int childID) {
        FamilyPlayer playerFam = this;
        FamilyPlayer childFam = new FamilyPlayer(childID);
        playerFam.deleteAdoption(childID);

        if (playerFam.isMarried()) {
            FamilyPlayer partnerFam = playerFam.getPartner();
            partnerFam.deleteAdoption(childID);
        }

        if (childFam.hasSibling()) {
            childFam.deleteSiblinghood();
        }

        playerFam.updateFamilyTree();
        childFam.updateFamilyTree();
    }

    public void addSibling(int siblingID) {
        FamilyPlayer playerFam = this;
        FamilyPlayer siblingFam = new FamilyPlayer(siblingID);
        playerFam.saveSiblinghood(siblingID);

        playerFam.updateFamilyTree();
        siblingFam.updateFamilyTree();
    }

    public void removeSibling() {
        FamilyPlayer playerFam = this;
        FamilyPlayer siblingFam = playerFam.getSibling();

        playerFam.deleteSiblinghood();

        playerFam.updateFamilyTree();
        siblingFam.updateFamilyTree();
    }

    public boolean isFamilyMember(int id) {
        return this.getFamilyList().containsValue(id);
    }

    public ItemStack getSkull() {
        return Utils.getSkull(this.getSkinURL());
    }

    public BiMap<String, Integer> getFamilyList() {

        if (this.getPartner() != null) {
            int partner = this.getPartner().getID();
            familyList.put("partner", partner);
            FamilyPlayer partnerFam = new FamilyPlayer(partner);

            if (partnerFam.getSibling() != null) {
                int secondSiblingInLaw = partnerFam.getSibling().getID();
                familyList.put("second_sibling_in_law", secondSiblingInLaw);
                FamilyPlayer secondSiblingInLawFam = new FamilyPlayer(secondSiblingInLaw);

            }

            if (partnerFam.getParents().size() > 0) {
                int firstParentInLaw = partnerFam.getParents().get(0).getID();
                familyList.put("first_parent_in_law", firstParentInLaw);
            }

            if (partnerFam.getParents().size() > 1) {
                int secondParentInLaw = partnerFam.getParents().get(1).getID();
                familyList.put("second_parent_in_law", secondParentInLaw);
            }
        }

        if (this.getSibling() != null) {
            int sibling = this.getSibling().getID();
            familyList.put("sibling", sibling);
            FamilyPlayer siblingFam = new FamilyPlayer(sibling);

            if (siblingFam.getPartner() != null) {
                int firstSiblingInLaw = siblingFam.getPartner().getID();
                familyList.put("first_sibling_in_law", firstSiblingInLaw);
            }

            if (siblingFam.getChildren().size() > 0) {
                int firstNieceOrNephew = siblingFam.getChildren().get(0).getID();
                familyList.put("first_niece_or_nephew", firstNieceOrNephew);
            }

            if (siblingFam.getChildren().size() > 1) {
                int secondNieceOrNephew = siblingFam.getChildren().get(1).getID();
                familyList.put("second_niece_or_nephew", secondNieceOrNephew);
            }
        }

        if (this.getParents().size() > 0) {
            int firstParent = this.getParents().get(0).getID();
            familyList.put("first_parent", firstParent);
            FamilyPlayer firstParentFam = new FamilyPlayer(firstParent);
            if (firstParentFam.getSibling() != null) {
                int secondAuntOrUncle = firstParentFam.getSibling().getID();
                familyList.put("second_aunt_or_uncle", secondAuntOrUncle);
                FamilyPlayer secondAuntOrUncleFam = new FamilyPlayer(secondAuntOrUncle);
                if (secondAuntOrUncleFam.getPartner() != null) {
                    int firstAuntOrUncle = secondAuntOrUncleFam.getPartner().getID();
                    familyList.put("first_aunt_or_uncle", firstAuntOrUncle);
                }
                if (secondAuntOrUncleFam.getChildren().size() > 0) {
                    int firstCousin = secondAuntOrUncleFam.getChildren().get(0).getID();
                    familyList.put("first_cousin", firstCousin);
                }
                if (secondAuntOrUncleFam.getChildren().size() > 1) {
                    int secondCousin = secondAuntOrUncleFam.getChildren().get(1).getID();
                    familyList.put("second_cousin", secondCousin);
                }
            }
            if (firstParentFam.getParents().size() > 0) {
                int firstGrandparent = firstParentFam.getParents().get(0).getID();
                familyList.put("first_grandparent", firstGrandparent);
                FamilyPlayer firstGrandparentFam = new FamilyPlayer(firstGrandparent);

                if (firstGrandparentFam.getSibling() != null) {
                    int firstGreatAuntOrUncle = firstGrandparentFam.getSibling().getID();
                    familyList.put("first_great_aunt_or_uncle", firstGreatAuntOrUncle);
                }
                if (firstGrandparentFam.getParents().size() > 0) {
                    int firstGreatGrandparent = firstGrandparentFam.getParents().get(0).getID();
                    familyList.put("first_great_grandparent", firstGreatGrandparent);
                }
                if (firstGrandparentFam.getParents().size() > 1) {
                    int secondGreatGrandparent = firstGrandparentFam.getParents().get(1).getID();
                    familyList.put("second_great_grandparent", secondGreatGrandparent);
                }
            }
            if (firstParentFam.getParents().size() > 1) {
                int secondGrandparent = firstParentFam.getParents().get(1).getID();
                familyList.put("second_grandparent", secondGrandparent);
                FamilyPlayer secondGrandparentFam = new FamilyPlayer(secondGrandparent);

                if (secondGrandparentFam.getSibling() != null) {
                    int secondGreatAuntOrUncle = secondGrandparentFam.getSibling().getID();
                    familyList.put("second_great_aunt_or_uncle", secondGreatAuntOrUncle);
                }
                if (secondGrandparentFam.getParents().size() > 0) {
                    int thirdGreatGrandparent = secondGrandparentFam.getParents().get(0).getID();
                    familyList.put("third_great_grandparent", thirdGreatGrandparent);
                }
                if (secondGrandparentFam.getParents().size() > 1) {
                    int fourthGreatGrandparent = secondGrandparentFam.getParents().get(1).getID();
                    familyList.put("fourth_great_grandparent", fourthGreatGrandparent);
                }
            }
        }

        if (this.getParents().size() > 1) {
            int secondParent = this.getParents().get(1).getID();
            familyList.put("second_parent", secondParent);
            FamilyPlayer secondParentFam = new FamilyPlayer(secondParent);

            if (secondParentFam.getSibling() != null) {
                int thirdAuntOrUncle = secondParentFam.getSibling().getID();
                familyList.put("third_aunt_or_uncle", thirdAuntOrUncle);
                FamilyPlayer thirdAuntOrUncleFam = new FamilyPlayer(thirdAuntOrUncle);
                if (thirdAuntOrUncleFam.getPartner() != null) {
                    int fourthAuntOrUncle = thirdAuntOrUncleFam.getPartner().getID();
                    familyList.put("fourth_aunt_or_uncle", fourthAuntOrUncle);
                }
                if (thirdAuntOrUncleFam.getChildren().size() > 0) {
                    int thirdCousin = thirdAuntOrUncleFam.getChildren().get(0).getID();
                    familyList.put("third_cousin", thirdCousin);
                }
                if (thirdAuntOrUncleFam.getChildren().size() > 1) {
                    int fourthCousin = thirdAuntOrUncleFam.getChildren().get(1).getID();
                    familyList.put("fourth_cousin", fourthCousin);
                }
            }
            if (secondParentFam.getParents().size() > 0) {
                int thirdGrandparent = secondParentFam.getParents().get(0).getID();
                familyList.put("third_grandparent", thirdGrandparent);
                FamilyPlayer thirdGrandparentFam = new FamilyPlayer(thirdGrandparent);

                if (thirdGrandparentFam.getSibling() != null) {
                    int thirdGreatAuntOrUncle = thirdGrandparentFam.getSibling().getID();
                    familyList.put("third_great_aunt_or_uncle", thirdGreatAuntOrUncle);
                }
                if (thirdGrandparentFam.getParents().size() > 0) {
                    int fifthGreatGrandparent = thirdGrandparentFam.getParents().get(0).getID();
                    familyList.put("fifth_great_grandparent", fifthGreatGrandparent);
                }
                if (thirdGrandparentFam.getParents().size() > 1) {
                    int sixthGreatGrandparent = thirdGrandparentFam.getParents().get(1).getID();
                    familyList.put("sixth_great_grandparent", sixthGreatGrandparent);
                }
            }
            if (secondParentFam.getParents().size() > 1) {
                int fourthGrandparent = secondParentFam.getParents().get(1).getID();
                familyList.put("fourth_grandparent", fourthGrandparent);
                FamilyPlayer fourthGrandparentFam = new FamilyPlayer(fourthGrandparent);

                if (fourthGrandparentFam.getSibling() != null) {
                    int fourthGreatAuntOrUncle = fourthGrandparentFam.getSibling().getID();
                    familyList.put("fourth_great_aunt_or_uncle", fourthGreatAuntOrUncle);
                }
                if (fourthGrandparentFam.getParents().size() > 0) {
                    int seventhGreatGrandparent = fourthGrandparentFam.getParents().get(0).getID();
                    familyList.put("seventh_great_grandparent", seventhGreatGrandparent);
                }
                if (fourthGrandparentFam.getParents().size() > 1) {
                    int eighthGreatGrandparent = fourthGrandparentFam.getParents().get(1).getID();
                    familyList.put("eighth_great_grandparent", eighthGreatGrandparent);
                }
            }
        }

        if (this.getChildren().size() > 0) {
            int firstChild = this.getChildren().get(0).getID();
            familyList.put("first_child", firstChild);
            FamilyPlayer firstChildFam = new FamilyPlayer(firstChild);

            if (firstChildFam.getPartner() != null) {
                int firstChildInLaw = firstChildFam.getPartner().getID();
                familyList.put("first_child_in_law", firstChildInLaw);
            }
            if (firstChildFam.getChildren().size() > 0) {
                int firstGrandchild = firstChildFam.getChildren().get(0).getID();
                familyList.put("first_grandchild", firstGrandchild);
                FamilyPlayer firstGrandchildFam = new FamilyPlayer(firstGrandchild);
                if (firstGrandchildFam.getPartner() != null) {
                    int firstGrandchildInLaw = firstGrandchildFam.getPartner().getID();
                    familyList.put("first_grandchild_in_law", firstGrandchildInLaw);
                }
                if (firstGrandchildFam.getChildren().size() > 0) {
                    int firstGreatGrandchild = firstGrandchildFam.getChildren().get(0).getID();
                    familyList.put("first_great_grandchild", firstGreatGrandchild);
                }
                if (firstGrandchildFam.getChildren().size() > 1) {
                    int secondGreatGrandchild = firstGrandchildFam.getChildren().get(1).getID();
                    familyList.put("second_great_grandchild", secondGreatGrandchild);
                }
            }
            if (firstChildFam.getChildren().size() > 1) {
                int secondGrandchild = firstChildFam.getChildren().get(1).getID();
                familyList.put("second_grandchild", secondGrandchild);
                FamilyPlayer secondGrandchildFam = new FamilyPlayer(secondGrandchild);
                if (secondGrandchildFam.getPartner() != null) {
                    int secondGrandchildInLaw = secondGrandchildFam.getPartner().getID();
                    familyList.put("second_grandchild_in_law", secondGrandchildInLaw);
                }
                if (secondGrandchildFam.getChildren().size() > 0) {
                    int thirdGreatGrandchild = secondGrandchildFam.getChildren().get(0).getID();
                    familyList.put("third_great_grandchild", thirdGreatGrandchild);
                }
                if (secondGrandchildFam.getChildren().size() > 1) {
                    int fourthGreatGrandchild = secondGrandchildFam.getChildren().get(1).getID();
                    familyList.put("fourth_great_grandchild", fourthGreatGrandchild);
                }
            }
        }

        if (this.getChildren().size() > 1) {
            int secondChild = this.getChildren().get(1).getID();
            familyList.put("second_child", secondChild);
            FamilyPlayer secondChildFam = new FamilyPlayer(secondChild);

            if (secondChildFam.getPartner() != null) {
                int secondChildInLaw = secondChildFam.getPartner().getID();
                familyList.put("second_child_in_law", secondChildInLaw);
            }
            if (secondChildFam.getChildren().size() > 0) {
                int thirdGrandchild = secondChildFam.getChildren().get(0).getID();
                familyList.put("third_grandchild", thirdGrandchild);
                FamilyPlayer thirdGrandchildFam = new FamilyPlayer(thirdGrandchild);
                if (thirdGrandchildFam.getPartner() != null) {
                    int thirdGrandchildInLaw = thirdGrandchildFam.getPartner().getID();
                    familyList.put("third_grandchild_in_law", thirdGrandchildInLaw);
                }
                if (thirdGrandchildFam.getChildren().size() > 0) {
                    int fifthGreatGrandchild = thirdGrandchildFam.getChildren().get(0).getID();
                    familyList.put("fifth_great_grandchild", fifthGreatGrandchild);
                }
                if (thirdGrandchildFam.getChildren().size() > 1) {
                    int sixthGreatGrandchild = thirdGrandchildFam.getChildren().get(1).getID();
                    familyList.put("sixth_great_grandchild", sixthGreatGrandchild);
                }
            }
            if (secondChildFam.getChildren().size() > 1) {
                int fourthGrandchild = secondChildFam.getChildren().get(1).getID();
                familyList.put("fourth_grandchild", fourthGrandchild);
                FamilyPlayer fourthGrandchildFam = new FamilyPlayer(fourthGrandchild);
                if (fourthGrandchildFam.getPartner() != null) {
                    int fourthGrandchildInLaw = fourthGrandchildFam.getPartner().getID();
                    familyList.put("fourth_grandchild_in_law", fourthGrandchildInLaw);
                }
                if (fourthGrandchildFam.getChildren().size() > 0) {
                    int seventhGreatGrandchild = fourthGrandchildFam.getChildren().get(0).getID();
                    familyList.put("seventh_great_grandchild", seventhGreatGrandchild);
                }
                if (fourthGrandchildFam.getChildren().size() > 1) {
                    int eighthGreatGrandchild = fourthGrandchildFam.getChildren().get(1).getID();
                    familyList.put("eighth_great_grandchild", eighthGreatGrandchild);
                }
            }
        }

        return familyList;
    }

    public void updateFamilyTree() {
        if (this.getPlayer() != null && PluginConfig.enabledCrazyAdvancementAPI) {
            new FamilyTree(this.id);
        }
    }

}


