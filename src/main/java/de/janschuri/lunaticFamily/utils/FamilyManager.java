package de.janschuri.lunaticFamily.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticFamily.Main;
import org.bukkit.Bukkit;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FamilyManager {


    private final Main plugin;
    private int id;
    private final String uuid;
    private String name;
    private final String skinURL;
    private int partner;
    private Timestamp marryDate;
    private int sibling;

    private List<Integer> parents;
    private List<Integer> children;
    private String gender;
    private String background;
    private final BiMap<String, Integer> familyList = HashBiMap.create();

    public FamilyManager(int id, Main plugin) {

        this.plugin = plugin;
        this.id = id;

        uuid = Main.getDatabase().getUUID(id);

        partner = Main.getDatabase().getPartner(id);
        marryDate = Main.getDatabase().getMarryDate(id);
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
            gender = plugin.defaultGender;
        } else {
            gender = Main.getDatabase().getGender(id);
        }

        if (Main.getDatabase().getBackground(uuid) == null) {
            background = plugin.defaultBackground;
        } else {
            background = Main.getDatabase().getBackground(uuid);
        }


        savePlayerData();

    }

    public FamilyManager(String uuid, Main plugin) {

        this.plugin = plugin;
        this.uuid = uuid;

        if(Main.getDatabase().getID(uuid) != 0) {
            id = Main.getDatabase().getID(uuid);
        } else {
            savePlayerData();
            id = Main.getDatabase().getID(uuid);
        }

        partner = Main.getDatabase().getPartner(id);
        marryDate = Main.getDatabase().getMarryDate(id);
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
            gender = plugin.defaultGender;
        } else {
            gender = Main.getDatabase().getGender(id);
        }

        if (Main.getDatabase().getBackground(uuid) == null) {
            background = plugin.defaultBackground;
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
            return new FamilyManager(this.partner, plugin);
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


    public FamilyManager getSibling() {
        if (this.sibling != 0) {
            return new FamilyManager(this.sibling, plugin);
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
        return !children.isEmpty();
    }

    public List<FamilyManager> getParents() {
        List<FamilyManager> list = new ArrayList<>();

        for (int parent : this.parents) {
            list.add(new FamilyManager(parent, plugin));
        }

        return list;
    }

    public List<FamilyManager> getChildren() {
        List<FamilyManager> list = new ArrayList<>();
        
        for (int child : this.children) {
            list.add(new FamilyManager(child, plugin));
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

    public void marry(int partnerID) {
        FamilyManager playerFam = this;
        FamilyManager partnerFam = new FamilyManager(partnerID, plugin);
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

    public void divorce() {
        FamilyManager playerFam = this;
        FamilyManager partnerFam = playerFam.getPartner();

        List<FamilyManager> playerChildren = playerFam.getChildren();
        for (FamilyManager child : playerChildren) {
            playerFam.deleteAdoption(child.getID());
        }

        playerFam.deleteMarriage();

        if (!plugin.allowSingleAdopt) {
            for (FamilyManager child : playerChildren) {
                partnerFam.deleteAdoption(child.getID());
            }
        }

    }

    public void adopt(int childID) {
        FamilyManager playerFam = this;
        FamilyManager childFam = new FamilyManager(childID, plugin);
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
        FamilyManager childFam = new FamilyManager(childID, plugin);
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
            FamilyManager partnerFam = new FamilyManager(partner, plugin);

            if (partnerFam.getSibling() != null) {
                int secondSiblingInLaw = partnerFam.getSibling().getID();
                familyList.put("secondSiblingInLaw", secondSiblingInLaw);
                FamilyManager secondSiblingInLawFam = new FamilyManager(secondSiblingInLaw, plugin);

            }

            if (partnerFam.getParents().size() > 0) {
                int firstParentInLaw = partnerFam.getParents().get(0).getID();
                familyList.put("firstParentInLaw", firstParentInLaw);
            }

            if (partnerFam.getParents().size() > 1) {
                int secondParentInLaw = partnerFam.getParents().get(1).getID();
                familyList.put("secondParentInLaw", secondParentInLaw);
            }
        }

        if (this.getSibling() != null) {
            int sibling = this.getSibling().getID();
            familyList.put("sibling", sibling);
            FamilyManager siblingFam = new FamilyManager(sibling, plugin);

            if (siblingFam.getPartner() != null) {
                int firstSiblingInLaw = siblingFam.getPartner().getID();
                familyList.put("firstSiblingInLaw", firstSiblingInLaw);
            }

            if (siblingFam.getChildren().size() > 0) {
                int firstNieceOrNephew = siblingFam.getChildren().get(0).getID();
                familyList.put("firstNieceOrNephew", firstNieceOrNephew);
            }

            if (siblingFam.getChildren().size() > 1) {
                int secondNieceOrNephew = siblingFam.getChildren().get(1).getID();
                familyList.put("secondNieceOrNephew", secondNieceOrNephew);
            }
        }

        if (this.getParents().size() > 0) {
            int firstParent = this.getParents().get(0).getID();
            familyList.put("firstParent", firstParent);
            FamilyManager firstParentFam = new FamilyManager(firstParent, plugin);
            if (firstParentFam.getSibling() != null) {
                int secondAuntOrUncle = firstParentFam.getSibling().getID();
                familyList.put("secondAuntOrUncle", secondAuntOrUncle);
                FamilyManager secondAuntOrUncleFam = new FamilyManager(secondAuntOrUncle, plugin);
                if (secondAuntOrUncleFam.getPartner() != null) {
                    int firstAuntOrUncle = secondAuntOrUncleFam.getPartner().getID();
                    familyList.put("firstAuntOrUncle", firstAuntOrUncle);
                }
                if (secondAuntOrUncleFam.getChildren().size() > 0) {
                    int firstCousin = secondAuntOrUncleFam.getChildren().get(0).getID();
                    familyList.put("firstCousin", firstCousin);
                }
                if (secondAuntOrUncleFam.getChildren().size() > 1) {
                    int secondCousin = secondAuntOrUncleFam.getChildren().get(1).getID();
                    familyList.put("secondCousin", secondCousin);
                }
            }
            if (firstParentFam.getParents().size() > 0) {
                int firstGrandparent = firstParentFam.getParents().get(0).getID();
                familyList.put("firstGrandparent", firstGrandparent);
                FamilyManager firstGrandparentFam = new FamilyManager(firstGrandparent, plugin);

                if (firstGrandparentFam.getSibling() != null) {
                    int firstGreatAuntOrUncle = firstGrandparentFam.getSibling().getID();
                    familyList.put("firstGreatAuntOrUncle", firstGreatAuntOrUncle);
                }
                if (firstGrandparentFam.getParents().size() > 0) {
                    int firstGreatGrandparent = firstGrandparentFam.getParents().get(0).getID();
                    familyList.put("firstGreatGrandparent", firstGreatGrandparent);
                }
                if (firstGrandparentFam.getParents().size() > 1) {
                    int secondGreatGrandparent = firstGrandparentFam.getParents().get(1).getID();
                    familyList.put("secondGreatGrandparent", secondGreatGrandparent);
                }
            }
            if (firstParentFam.getParents().size() > 1) {
                int secondGrandparent = firstParentFam.getParents().get(1).getID();
                familyList.put("secondGrandparent", secondGrandparent);
                FamilyManager secondGrandparentFam = new FamilyManager(secondGrandparent, plugin);

                if (secondGrandparentFam.getSibling() != null) {
                    int secondGreatAuntOrUncle = secondGrandparentFam.getSibling().getID();
                    familyList.put("secondGreatAuntOrUncle", secondGreatAuntOrUncle);
                }
                if (secondGrandparentFam.getParents().size() > 0) {
                    int thirdGreatGrandparent = secondGrandparentFam.getParents().get(0).getID();
                    familyList.put("thirdGreatGrandparent", thirdGreatGrandparent);
                }
                if (secondGrandparentFam.getParents().size() > 1) {
                    int fourthGreatGrandparent = secondGrandparentFam.getParents().get(1).getID();
                    familyList.put("fourthGreatGrandparent", fourthGreatGrandparent);
                }
            }
        }

        if (this.getParents().size() > 1) {
            int secondParent = this.getParents().get(1).getID();
            familyList.put("secondParent", secondParent);
            FamilyManager secondParentFam = new FamilyManager(secondParent, plugin);

            if (secondParentFam.getSibling() != null) {
                int thirdAuntOrUncle = secondParentFam.getSibling().getID();
                familyList.put("thirdAuntOrUncle", thirdAuntOrUncle);
                FamilyManager thirdAuntOrUncleFam = new FamilyManager(thirdAuntOrUncle, plugin);
                if (thirdAuntOrUncleFam.getPartner() != null) {
                    int fourthAuntOrUncle = thirdAuntOrUncleFam.getPartner().getID();
                    familyList.put("fourthAuntOrUncle", fourthAuntOrUncle);
                }
                if (thirdAuntOrUncleFam.getChildren().size() > 0) {
                    int thirdCousin = thirdAuntOrUncleFam.getChildren().get(0).getID();
                    familyList.put("thirdCousin", thirdCousin);
                }
                if (thirdAuntOrUncleFam.getChildren().size() > 1) {
                    int fourthCousin = thirdAuntOrUncleFam.getChildren().get(1).getID();
                    familyList.put("fourthCousin", fourthCousin);
                }
            }
            if (secondParentFam.getParents().size() > 0) {
                int thirdGrandparent = secondParentFam.getParents().get(0).getID();
                familyList.put("thirdGrandparent", thirdGrandparent);
                FamilyManager thirdGrandparentFam = new FamilyManager(thirdGrandparent, plugin);

                if (thirdGrandparentFam.getSibling() != null) {
                    int thirdGreatAuntOrUncle = thirdGrandparentFam.getSibling().getID();
                    familyList.put("thirdGreatAuntOrUncle", thirdGreatAuntOrUncle);
                }
                if (thirdGrandparentFam.getParents().size() > 0) {
                    int fifthGreatGrandparent = thirdGrandparentFam.getParents().get(0).getID();
                    familyList.put("fifthGreatGrandparent", fifthGreatGrandparent);
                }
                if (thirdGrandparentFam.getParents().size() > 1) {
                    int sixthGreatGrandparent = thirdGrandparentFam.getParents().get(1).getID();
                    familyList.put("sixthGreatGrandparent", sixthGreatGrandparent);
                }
            }
            if (secondParentFam.getParents().size() > 1) {
                int fourthGrandparent = secondParentFam.getParents().get(1).getID();
                familyList.put("fourthGrandparent", fourthGrandparent);
                FamilyManager fourthGrandparentFam = new FamilyManager(fourthGrandparent, plugin);

                if (fourthGrandparentFam.getSibling() != null) {
                    int fourthGreatAuntOrUncle = fourthGrandparentFam.getSibling().getID();
                    familyList.put("fourthGreatAuntOrUncle", fourthGreatAuntOrUncle);
                }
                if (fourthGrandparentFam.getParents().size() > 0) {
                    int seventhGreatGrandparent = fourthGrandparentFam.getParents().get(0).getID();
                    familyList.put("seventhGreatGrandparent", seventhGreatGrandparent);
                }
                if (fourthGrandparentFam.getParents().size() > 1) {
                    int eighthGreatGrandparent = fourthGrandparentFam.getParents().get(1).getID();
                    familyList.put("eighthGreatGrandparent", eighthGreatGrandparent);
                }
            }
        }

        if (this.getChildren().size() > 0) {
            int firstChild = this.getChildren().get(0).getID();
            familyList.put("firstChild", firstChild);
            FamilyManager firstChildFam = new FamilyManager(firstChild, plugin);

            if (firstChildFam.getPartner() != null) {
                int firstChildInLaw = firstChildFam.getPartner().getID();
                familyList.put("firstChildInLaw", firstChildInLaw);
            }
            if (firstChildFam.getChildren().size() > 0) {
                int firstGrandchild = firstChildFam.getChildren().get(0).getID();
                familyList.put("firstGrandchild", firstGrandchild);
                FamilyManager firstGrandchildFam = new FamilyManager(firstGrandchild, plugin);
                if (firstGrandchildFam.getPartner() != null) {
                    int firstGrandchildInLaw = firstGrandchildFam.getPartner().getID();
                    familyList.put("firstGrandchildInLaw", firstGrandchildInLaw);
                }
                if (firstGrandchildFam.getChildren().size() > 0) {
                    int firstGreatGrandchild = firstGrandchildFam.getChildren().get(0).getID();
                    familyList.put("firstGreatGrandchild", firstGreatGrandchild);
                }
                if (firstGrandchildFam.getChildren().size() > 1) {
                    int secondGreatGrandchild = firstGrandchildFam.getChildren().get(1).getID();
                    familyList.put("secondGreatGrandchild", secondGreatGrandchild);
                }
            }
            if (firstChildFam.getChildren().size() > 1) {
                int secondGrandchild = firstChildFam.getChildren().get(1).getID();
                familyList.put("secondGrandchild", secondGrandchild);
                FamilyManager secondGrandchildFam = new FamilyManager(secondGrandchild, plugin);
                if (secondGrandchildFam.getPartner() != null) {
                    int secondGrandchildInLaw = secondGrandchildFam.getPartner().getID();
                    familyList.put("secondGrandchildInLaw", secondGrandchildInLaw);
                }
                if (secondGrandchildFam.getChildren().size() > 0) {
                    int thirdGreatGrandchild = secondGrandchildFam.getChildren().get(0).getID();
                    familyList.put("thirdGreatGrandchild", thirdGreatGrandchild);
                }
                if (secondGrandchildFam.getChildren().size() > 1) {
                    int fourthGreatGrandchild = secondGrandchildFam.getChildren().get(1).getID();
                    familyList.put("fourthGreatGrandchild", fourthGreatGrandchild);
                }
            }
        }

        if (this.getChildren().size() > 1) {
            int secondChild = this.getChildren().get(1).getID();
            familyList.put("secondChild", secondChild);
            FamilyManager secondChildFam = new FamilyManager(secondChild, plugin);

            if (secondChildFam.getPartner() != null) {
                int secondChildInLaw = secondChildFam.getPartner().getID();
                familyList.put("secondChildInLaw", secondChildInLaw);
            }
            if (secondChildFam.getChildren().size() > 0) {
                int thirdGrandchild = secondChildFam.getChildren().get(0).getID();
                familyList.put("thirdGrandchild", thirdGrandchild);
                FamilyManager thirdGrandchildFam = new FamilyManager(thirdGrandchild, plugin);
                if (thirdGrandchildFam.getPartner() != null) {
                    int thirdGrandchildInLaw = thirdGrandchildFam.getPartner().getID();
                    familyList.put("thirdGrandchildInLaw", thirdGrandchildInLaw);
                }
                if (thirdGrandchildFam.getChildren().size() > 0) {
                    int fifthGreatGrandchild = thirdGrandchildFam.getChildren().get(0).getID();
                    familyList.put("fifthGreatGrandchild", fifthGreatGrandchild);
                }
                if (thirdGrandchildFam.getChildren().size() > 1) {
                    int sixthGreatGrandchild = thirdGrandchildFam.getChildren().get(1).getID();
                    familyList.put("sixthGreatGrandchild", sixthGreatGrandchild);
                }
            }
            if (secondChildFam.getChildren().size() > 1) {
                int fourthGrandchild = secondChildFam.getChildren().get(1).getID();
                familyList.put("fourthGrandchild", fourthGrandchild);
                FamilyManager fourthGrandchildFam = new FamilyManager(fourthGrandchild, plugin);
                if (fourthGrandchildFam.getPartner() != null) {
                    int fourthGrandchildInLaw = fourthGrandchildFam.getPartner().getID();
                    familyList.put("fourthGrandchildInLaw", fourthGrandchildInLaw);
                }
                if (fourthGrandchildFam.getChildren().size() > 0) {
                    int seventhGreatGrandchild = fourthGrandchildFam.getChildren().get(0).getID();
                    familyList.put("seventhGreatGrandchild", seventhGreatGrandchild);
                }
                if (fourthGrandchildFam.getChildren().size() > 1) {
                    int eighthGreatGrandchild = fourthGrandchildFam.getChildren().get(1).getID();
                    familyList.put("eighthGreatGrandchild", eighthGreatGrandchild);
                }
            }
        }

        return familyList;
    }

}


