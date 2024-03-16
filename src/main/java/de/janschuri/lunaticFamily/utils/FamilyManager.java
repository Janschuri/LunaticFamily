package de.janschuri.lunaticFamily.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticFamily.Main;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FamilyManager {


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

    public FamilyManager(int id) {

        this.id = id;

        uuid = Main.getDatabase().getUUID(id);

        partner = Main.getDatabase().getPartner(id);
        marryDate = Main.getDatabase().getMarryDate(id);
        priest = Main.getDatabase().getPriest(id);
        sibling = Main.getDatabase().getSibling(id);
        parents = Main.getDatabase().getParents(id);
        children = Main.getDatabase().getChilds(id);


        if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() != null) {
            name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
        } else {
            name = Main.getDatabase().getName(id);
        }

        if (Bukkit.getPlayer(UUID.fromString(uuid)) != null) {
            skinURL = Bukkit.getPlayer(UUID.fromString(uuid)).getPlayerProfile().getTextures().getSkin().toString();
        } else if (Main.getDatabase().getSkinURL(id) != null) {
            skinURL = Main.getDatabase().getSkinURL(id);
        } else {
            skinURL = "http://textures.minecraft.net/texture/2705fd94a0c431927fb4e639b0fcfb49717e412285a02b439e0112da22b2e2ec";
        }


        if (Main.getDatabase().getGender(id) == null) {
            gender = Main.defaultGender;
        } else {
            gender = Main.getDatabase().getGender(id);
        }

        if (Main.getDatabase().getBackground(uuid) == null) {
            background = Main.defaultBackground;
        } else {
            background = Main.getDatabase().getBackground(uuid);
        }


        savePlayerData();

    }

    public FamilyManager(String uuid) {

        this.uuid = uuid;

        if(Main.getDatabase().getID(uuid) != 0) {
            id = Main.getDatabase().getID(uuid);
        } else {
            savePlayerData();
            id = Main.getDatabase().getID(uuid);
        }

        partner = Main.getDatabase().getPartner(id);
        marryDate = Main.getDatabase().getMarryDate(id);
        priest = Main.getDatabase().getPriest(id);
        sibling = Main.getDatabase().getSibling(id);
        parents = Main.getDatabase().getParents(id);
        children = Main.getDatabase().getChilds(id);


        if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() != null) {
            name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
        } else {
            name = Main.getDatabase().getName(id);
        }

        if (Bukkit.getPlayer(UUID.fromString(uuid)) != null) {
            skinURL = Bukkit.getPlayer(UUID.fromString(uuid)).getPlayerProfile().getTextures().getSkin().toString();
        } else if (Main.getDatabase().getSkinURL(id) != null) {
            skinURL = Main.getDatabase().getSkinURL(id);
        } else {
            skinURL = "http://textures.minecraft.net/texture/2705fd94a0c431927fb4e639b0fcfb49717e412285a02b439e0112da22b2e2ec";
        }


        if (Main.getDatabase().getGender(id) == null) {
            gender = Main.defaultGender;
        } else {
            gender = Main.getDatabase().getGender(id);
        }

        if (Main.getDatabase().getBackground(uuid) == null) {
            background = Main.defaultBackground;
        } else {
            background = Main.getDatabase().getBackground(uuid);
        }


        savePlayerData();

    }

    public String getName() {
        if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() != null) {
            return Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
        } else {
            return Main.getDatabase().getName(id);
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
            Main.getDatabase().savePlayerData(uuid, name, skinURL, gender, background);
        } else {
            Main.getDatabase().updatePlayerData(id, uuid, name, skinURL, gender, background);
        }
    }

    private void saveMarriage(int partnerID) {
        Main.getDatabase().saveMarriage(this.id, partnerID);
    }
    private void saveMarriage(int partnerID, int priest) {
        Main.getDatabase().saveMarriage(this.id, partnerID, priest);
    }

    private void deleteMarriage() {
        Main.getDatabase().deleteMarriage(this.id);
    }
    private void saveSiblinghood(int siblingID) {
        Main.getDatabase().saveSiblinghood(this.id, siblingID);
    }

    private void deleteSiblinghood() {
        Main.getDatabase().deleteSiblinghood(this.id);
    }
    private void saveAdoption(int childID) {
        Main.getDatabase().saveAdoption(this.id, childID);
    }

    private void deleteAdoption(int childID) {
        Main.getDatabase().deleteAdoption(this.id, childID);
    }

    public String getSkinURL() {
        return skinURL;
    }

    public FamilyManager getPartner() {
        if (this.partner != 0) {
            return new FamilyManager(this.partner);
        } else {
            return null;
        }
    }

    public boolean isMarried() {
        return this.partner != 0;
    }

    public String getMarriageDate() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return formatter.format(this.marryDate.toLocalDateTime());
    }
    public FamilyManager getPriest() {
        if (this.priest != 0) {
            return new FamilyManager(this.priest);
        } else {
            return null;
        }
    }

    public FamilyManager getSibling() {
        if (this.sibling != 0) {
            return new FamilyManager(this.sibling);
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

    public boolean isChildOf (int parentID) {
        return parents.contains(parentID);
    }

    public List<FamilyManager> getParents() {
        List<FamilyManager> list = new ArrayList<>();

        for (int parent : this.parents) {
            list.add(new FamilyManager(parent));
        }

        return list;
    }

    public List<FamilyManager> getChildren() {
        List<FamilyManager> list = new ArrayList<>();
        
        for (int child : this.children) {
            list.add(new FamilyManager(child));
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
        this.background = "textures/block/"+background+".png";
        savePlayerData();
    }

    public Integer getChildrenAmount (){
        return children.size();
    }

    public OfflinePlayer getOfflinePlayer (){
        return Bukkit.getOfflinePlayer(UUID.fromString(this.uuid));
    }

    public Player getPlayer (){
        if (Bukkit.getPlayer(UUID.fromString(uuid)) != null) {
            return Bukkit.getPlayer(UUID.fromString(this.uuid));
        } else {
            return null;
        }
    }

    public boolean isOnline (){
        return Bukkit.getPlayer(UUID.fromString(uuid)) != null;
    }

    public void withdrawPlayer (String... withdrawKeys) {
        if(Main.enabledVault) {
            OfflinePlayer player = this.getOfflinePlayer();

            double amount = 0.0;
            for (String key : withdrawKeys) {
                if (Main.commandWithdraws.containsKey(key)) {
                    amount += Main.commandWithdraws.get(key);
                }
            }
            if (amount > 0) {
                Vault.getEconomy().withdrawPlayer(player, amount);
                if (player.getPlayer() != null) {
                    player.getPlayer().sendMessage(Main.prefix + Main.getMessage("withdraw").replace("%amount%", amount + ""));
                }
            }
        }
    }

    public void withdrawPlayer (String withdrawKey, double factor) {
        if(Main.enabledVault) {
            OfflinePlayer player = this.getOfflinePlayer();

            double amount = 0.0;
            amount += Main.commandWithdraws.get(withdrawKey) * factor;


            if (amount > 0) {
                Vault.getEconomy().withdrawPlayer(player, amount);
                if (player.getPlayer() != null) {
                    player.getPlayer().sendMessage(Main.prefix + Main.getMessage("withdraw").replace("%amount%", amount + ""));
                }
            }
        }
    }

    public boolean hasEnoughMoney(String... withdrawKeys) {
        if(Main.enabledVault) {
            OfflinePlayer player = this.getOfflinePlayer();
            double amount = 0.0;
            for (String key : withdrawKeys) {
                if (Main.commandWithdraws.containsKey(key)) {
                    amount += Main.commandWithdraws.get(key);
                }
            }
            return (amount < Vault.getEconomy().getBalance(player));
        } else {
            return true;
        }
    }

    public boolean hasEnoughMoney(String withdrawKey, double factor) {
        if(Main.enabledVault) {
            OfflinePlayer player = this.getOfflinePlayer();
            return (Main.commandWithdraws.get(withdrawKey) * factor < Vault.getEconomy().getBalance(player));
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

    public boolean sendMessage(TextComponent message) {
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
        FamilyManager playerFam = this;
        FamilyManager partnerFam = new FamilyManager(partnerID);
        List<FamilyManager> playerChildren = playerFam.getChildren();
        List<FamilyManager> partnerChildren = partnerFam.getChildren();

        for (FamilyManager child : playerChildren) {
            partnerFam.saveAdoption(child.getID());
        }
        for (FamilyManager child : partnerChildren) {
            playerFam.saveAdoption(child.getID());
        }

        if (playerChildren.size() == 1 && partnerChildren.size() == 1) {
            FamilyManager childFam = playerChildren.get(0);
            childFam.saveSiblinghood(partnerChildren.get(0).getID());
        }

        playerFam.saveMarriage(partnerID);


    }

    public void marry(int partnerID, int priest) {
        FamilyManager playerFam = this;
        FamilyManager partnerFam = new FamilyManager(partnerID);
        List<FamilyManager> playerChildren = playerFam.getChildren();
        List<FamilyManager> partnerChildren = partnerFam.getChildren();

        for (FamilyManager child : playerChildren) {
            partnerFam.saveAdoption(child.getID());
        }
        for (FamilyManager child : partnerChildren) {
            playerFam.saveAdoption(child.getID());
        }

        if (playerChildren.size() == 1 && partnerChildren.size() == 1) {
            FamilyManager childFam = playerChildren.get(0);
            childFam.saveSiblinghood(partnerChildren.get(0).getID());
        }

        playerFam.saveMarriage(partnerID, priest);


    }

    public void divorce() {
        FamilyManager playerFam = this;
        FamilyManager partnerFam = playerFam.getPartner();

        List<FamilyManager> playerChildren = playerFam.getChildren();
        for (FamilyManager child : playerChildren) {
            playerFam.deleteAdoption(child.getID());
        }

        playerFam.deleteMarriage();

        if (!Main.allowSingleAdopt) {
            for (FamilyManager child : playerChildren) {
                partnerFam.deleteAdoption(child.getID());
            }
        }

    }

    public void adopt(int childID) {
        FamilyManager playerFam = this;
        FamilyManager childFam = new FamilyManager(childID);
        playerFam.saveAdoption(childID);

        if (playerFam.isMarried()) {
            FamilyManager partnerFam = playerFam.getPartner();
            partnerFam.saveAdoption(childID);
        }

        if (childFam.hasSibling()) {
           playerFam.adopt(childID);
            if (playerFam.isMarried()) {
                FamilyManager partnerFam = playerFam.getPartner();
                partnerFam.saveAdoption(childID);
            }
        }
    }

    public void unadopt(int childID) {
        FamilyManager playerFam = this;
        FamilyManager childFam = new FamilyManager(childID);
        playerFam.deleteAdoption(childID);

        if (playerFam.isMarried()) {
            FamilyManager partnerFam = playerFam.getPartner();
            partnerFam.deleteAdoption(childID);
        }

        if (childFam.hasSibling()) {
            childFam.deleteSiblinghood();
        }

    }

    public void addSibling(int siblingID) {
        FamilyManager playerFam = this;
        playerFam.saveSiblinghood(siblingID);
    }

    public void removeSibling() {
        FamilyManager player1Fam = this;

        player1Fam.deleteSiblinghood();
    }

    public  boolean isFamilyMember (int id){
        return this.getFamilyList().containsValue(id);
    }
    public BiMap<String, Integer> getFamilyList() {

        if (this.getPartner() != null) {
            int partner = this.getPartner().getID();
            familyList.put("partner", partner);
            FamilyManager partnerFam = new FamilyManager(partner);

            if (partnerFam.getSibling() != null) {
                int secondSiblingInLaw = partnerFam.getSibling().getID();
                familyList.put("second_sibling_in_law", secondSiblingInLaw);
                FamilyManager secondSiblingInLawFam = new FamilyManager(secondSiblingInLaw);

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
            FamilyManager siblingFam = new FamilyManager(sibling);

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
            FamilyManager firstParentFam = new FamilyManager(firstParent);
            if (firstParentFam.getSibling() != null) {
                int secondAuntOrUncle = firstParentFam.getSibling().getID();
                familyList.put("second_aunt_or_uncle", secondAuntOrUncle);
                FamilyManager secondAuntOrUncleFam = new FamilyManager(secondAuntOrUncle);
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
                FamilyManager firstGrandparentFam = new FamilyManager(firstGrandparent);

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
                FamilyManager secondGrandparentFam = new FamilyManager(secondGrandparent);

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
            FamilyManager secondParentFam = new FamilyManager(secondParent);

            if (secondParentFam.getSibling() != null) {
                int thirdAuntOrUncle = secondParentFam.getSibling().getID();
                familyList.put("third_aunt_or_uncle", thirdAuntOrUncle);
                FamilyManager thirdAuntOrUncleFam = new FamilyManager(thirdAuntOrUncle);
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
                FamilyManager thirdGrandparentFam = new FamilyManager(thirdGrandparent);

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
                FamilyManager fourthGrandparentFam = new FamilyManager(fourthGrandparent);

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
            FamilyManager firstChildFam = new FamilyManager(firstChild);

            if (firstChildFam.getPartner() != null) {
                int firstChildInLaw = firstChildFam.getPartner().getID();
                familyList.put("first_child_in_law", firstChildInLaw);
            }
            if (firstChildFam.getChildren().size() > 0) {
                int firstGrandchild = firstChildFam.getChildren().get(0).getID();
                familyList.put("first_grandchild", firstGrandchild);
                FamilyManager firstGrandchildFam = new FamilyManager(firstGrandchild);
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
                FamilyManager secondGrandchildFam = new FamilyManager(secondGrandchild);
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
            FamilyManager secondChildFam = new FamilyManager(secondChild);

            if (secondChildFam.getPartner() != null) {
                int secondChildInLaw = secondChildFam.getPartner().getID();
                familyList.put("second_child_in_law", secondChildInLaw);
            }
            if (secondChildFam.getChildren().size() > 0) {
                int thirdGrandchild = secondChildFam.getChildren().get(0).getID();
                familyList.put("third_grandchild", thirdGrandchild);
                FamilyManager thirdGrandchildFam = new FamilyManager(thirdGrandchild);
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
                FamilyManager fourthGrandchildFam = new FamilyManager(fourthGrandchild);
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

}


