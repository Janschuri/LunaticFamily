package de.janschuri.lunaticFamily.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticFamily.Main;
import org.bukkit.Bukkit;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
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
    private int firstParent;
    private int secondParent;
    private int firstChild;
    private int secondChild;
    private String gender;
    private String background;
    private int fake;
    private final BiMap<String, Integer> familyList = HashBiMap.create();

    public FamilyManager(int id, Main plugin) {

        this.plugin = plugin;
        this.id = id;

        uuid = Main.getDatabase().getUUID(id);

        partner = Main.getDatabase().getPartner(id);
        marryDate = Main.getDatabase().getMarryDate(id);
        sibling = Main.getDatabase().getSibling(id);
        firstParent = Main.getDatabase().getFirstParent(id);
        secondParent = Main.getDatabase().getSecondParent(id);
        firstChild = Main.getDatabase().getFirstChild(id);
        secondChild = Main.getDatabase().getSecondChild(id);


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
        firstParent = Main.getDatabase().getFirstParent(id);
        secondParent = Main.getDatabase().getSecondParent(id);
        firstChild = Main.getDatabase().getFirstChild(id);
        secondChild = Main.getDatabase().getSecondChild(id);


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
            Main.getDatabase().savePlayerData(id, uuid, name, skinURL, gender, background);
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

    private void deleteAdoption() {
        Main.getDatabase().deleteAdoption(this.id);
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
        if (this.firstParent != 0) {
            return true;
        } else return this.secondParent != 0;
    }

    public boolean isChildOf (int parentID) {
        if (parentID == this.firstParent) {
            return true;
        } else return parentID == this.secondParent;
    }


    public FamilyManager getFirstChild() {
        if (this.firstChild != 0) {
            return new FamilyManager(this.firstChild, plugin);
        } else {
            return null;
        }
    }

    public FamilyManager getSecondChild() {
        if (this.secondChild != 0) {
            return new FamilyManager(this.secondChild, plugin);
        } else {
            return null;
        }
    }

    private void setFirstChild(int firstChild) {
        this.firstChild = firstChild;
        savePlayerData();
    }

    private void setSecondChild(int secondChild) {
        this.secondChild = secondChild;
        savePlayerData();
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
        Integer amount = 0;
        if (this.firstChild != 0) {
            amount++;
        }
        if (this.secondChild != 0) {
            amount++;
        }

        return amount;
    }

    public void marry(int partnerID) {
        FamilyManager playerFam = this;
        FamilyManager partnerFam = new FamilyManager(partnerID, plugin);
        int playerID = playerFam.getID();

        int firstChildID = 0;
        int secondChildID = 0;



        if (playerFam.getFirstChild() != null) {
            firstChildID = playerFam.getFirstChild().getID();
        }
        if (playerFam.getSecondChild() != null) {
            secondChildID = playerFam.getSecondChild().getID();
        }

        if (firstChildID == 0) {
            if (partnerFam.getFirstChild() != null) {
                firstChildID = partnerFam.getFirstChild().getID();
            }
            if (firstChildID == 0) {
                if (partnerFam.getSecondChild() != null) {
                firstChildID = partnerFam.getSecondChild().getID();
                }
            }

        }
        if (secondChildID == 0) {
            if (partnerFam.getFirstChild() != null) {
                secondChildID = partnerFam.getFirstChild().getID();
            }
            if (secondChildID == 0) {
                if (partnerFam.getSecondChild() != null) {
                    secondChildID = partnerFam.getSecondChild().getID();
                }
            }
        }


        playerFam.setFirstChild(firstChildID);
        playerFam.setSecondChild(secondChildID);

        partnerFam.setFirstChild(firstChildID);
        partnerFam.setSecondChild(secondChildID);

        playerFam.saveMarriage(partnerID);

        if (playerFam.getFirstChild() != null) {
            FamilyManager childFam = new FamilyManager(playerFam.getFirstChild().getID(), plugin);
            childFam.setFirstParent(playerID);
            childFam.setSecondParent(partnerID);
            if (playerFam.getSecondChild() != null) {
                if(playerFam.getSecondChild().getID() != childFam.getID()) {
                    childFam.saveSiblinghood(playerFam.getSecondChild().getID());
                }
            }
        }
        if (playerFam.getSecondChild() != null) {
            FamilyManager childFam = new FamilyManager(playerFam.getSecondChild().getID(), plugin);
            childFam.setFirstParent(playerID);
            childFam.setSecondParent(partnerID);
            if (playerFam.getFirstChild() != null) {
                if(playerFam.getFirstChild().getID() != childFam.getID()) {
                    childFam.saveSiblinghood(playerFam.getFirstChild().getID());
                }
            }
        }
    }

    public void divorce() {
        FamilyManager playerFam = this;
        FamilyManager partnerFam = playerFam.getPartner();

        if (playerFam.getFirstChild() != null) {
            FamilyManager childFam = playerFam.getFirstChild();
            childFam.setFirstParent(0);
            childFam.setSecondParent(0);
            if (plugin.allowSingleAdopt) {
                childFam.setFirstParent(partnerFam.getID());
            } else {
                childFam.deleteSiblinghood();
            }
        }

        if (playerFam.getSecondChild() != null) {
            FamilyManager childFam = playerFam.getSecondChild();
            childFam.setFirstParent(0);
            childFam.setSecondParent(0);
            if (plugin.allowSingleAdopt) {
                childFam.setFirstParent(partnerFam.getID());
            } else {
                childFam.deleteSiblinghood();
            }
        }

        playerFam.setFirstChild(0);
        playerFam.setSecondChild(0);

        playerFam.deleteMarriage();

        if (!plugin.allowSingleAdopt) {
            partnerFam.setFirstChild(0);
            partnerFam.setSecondChild(0);

        }

    }

    public void adopt(int childID) {
        FamilyManager playerFam = this;
        int playerID = playerFam.getID();

        FamilyManager childFam = new FamilyManager(childID, plugin);
        childFam.setFirstParent(playerID);

        if (playerFam.isMarried()) {
            FamilyManager partnerFam = playerFam.getPartner();
            childFam.setSecondParent(partnerFam.getID());
            if (partnerFam.getFirstChild() != null) {
                partnerFam.setSecondChild(childID);
            } else {
                partnerFam.setFirstChild(childID);
            }
        }

        if (childFam.hasSibling()) {
            FamilyManager siblingFam = childFam.getSibling();
            siblingFam.setFirstParent(playerID);
            if (playerFam.isMarried()) {
                FamilyManager partnerFam = playerFam.getPartner();
                childFam.setSecondParent(partnerFam.getID());
                siblingFam.setSecondParent(partnerFam.getID());
                partnerFam.setFirstChild(childID);
                partnerFam.setSecondChild(siblingFam.getID());
            }

            playerFam.setFirstChild(childID);
            playerFam.setSecondChild(childFam.getSibling().getID());
        } else {
            if (playerFam.getFirstChild() != null) {
                playerFam.setSecondChild(childID);
                childFam.saveSiblinghood(playerFam.getFirstChild().getID());
            } else {
                playerFam.setFirstChild(childID);
                if (playerFam.getSecondChild() != null) {
                    childFam.saveSiblinghood(playerFam.getSecondChild().getID());
                }
            }
        }
    }

    public void unadopt(int childID) {
        FamilyManager playerFam = this;
        int playerID = playerFam.getID();
        int firstChildID = childID;
        FamilyManager childFam = new FamilyManager(firstChildID, plugin);

        childFam.setFirstParent(0);
        childFam.setSecondParent(0);

        if (playerFam.isMarried()) {
            FamilyManager partnerFam = playerFam.getPartner();
            if (partnerFam.getFirstChild() != null && partnerFam.getFirstChild().getID() == childID) {
                partnerFam.setFirstChild(0);
            }
            if (partnerFam.getSecondChild() != null && partnerFam.getSecondChild().getID() == childID) {
                partnerFam.setSecondChild(0);
            }
        }

        if (playerFam.getFirstChild() != null) {
            if (playerFam.getFirstChild().getID() == childID) {
                playerFam.setFirstChild(0);
            }
        }
        if (playerFam.getSecondChild() != null) {
            if (playerFam.getSecondChild().getID() == childID) {
                playerFam.setSecondChild(0);
            }
        }

        childFam.deleteSiblinghood();

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

            if (partnerFam.getFirstParent() != null) {
                int firstParentInLaw = partnerFam.getFirstParent().getID();
                familyList.put("firstParentInLaw", firstParentInLaw);
            }

            if (partnerFam.getSecondParent() != null) {
                int secondParentInLaw = partnerFam.getSecondParent().getID();
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

            if (siblingFam.getFirstChild() != null) {
                int firstNieceOrNephew = siblingFam.getFirstChild().getID();
                familyList.put("firstNieceOrNephew", firstNieceOrNephew);
            }

            if (siblingFam.getSecondChild() != null) {
                int secondNieceOrNephew = siblingFam.getSecondChild().getID();
                familyList.put("secondNieceOrNephew", secondNieceOrNephew);
            }
        }

        if (this.getFirstParent() != null) {
            int firstParent = this.getFirstParent().getID();
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
                if (secondAuntOrUncleFam.getFirstChild() != null) {
                    int firstCousin = secondAuntOrUncleFam.getFirstChild().getID();
                    familyList.put("firstCousin", firstCousin);
                }
                if (secondAuntOrUncleFam.getSecondChild() != null) {
                    int secondCousin = secondAuntOrUncleFam.getSecondChild().getID();
                    familyList.put("secondCousin", secondCousin);
                }
            }
            if (firstParentFam.getFirstParent() != null) {
                int firstGrandparent = firstParentFam.getFirstParent().getID();
                familyList.put("firstGrandparent", firstGrandparent);
                FamilyManager firstGrandparentFam = new FamilyManager(firstGrandparent, plugin);

                if (firstGrandparentFam.getSibling() != null) {
                    int firstGreatAuntOrUncle = firstGrandparentFam.getSibling().getID();
                    familyList.put("firstGreatAuntOrUncle", firstGreatAuntOrUncle);
                }
                if (firstGrandparentFam.getFirstParent() != null) {
                    int firstGreatGrandparent = firstGrandparentFam.getFirstParent().getID();
                    familyList.put("firstGreatGrandparent", firstGreatGrandparent);
                }
                if (firstGrandparentFam.getSecondParent() != null) {
                    int secondGreatGrandparent = firstGrandparentFam.getSecondParent().getID();
                    familyList.put("secondGreatGrandparent", secondGreatGrandparent);
                }
            }
            if (firstParentFam.getSecondParent() != null) {
                int secondGrandparent = firstParentFam.getSecondParent().getID();
                familyList.put("secondGrandparent", secondGrandparent);
                FamilyManager secondGrandparentFam = new FamilyManager(secondGrandparent, plugin);

                if (secondGrandparentFam.getSibling() != null) {
                    int secondGreatAuntOrUncle = secondGrandparentFam.getSibling().getID();
                    familyList.put("secondGreatAuntOrUncle", secondGreatAuntOrUncle);
                }
                if (secondGrandparentFam.getFirstParent() != null) {
                    int thirdGreatGrandparent = secondGrandparentFam.getFirstParent().getID();
                    familyList.put("thirdGreatGrandparent", thirdGreatGrandparent);
                }
                if (secondGrandparentFam.getSecondParent() != null) {
                    int fourthGreatGrandparent = secondGrandparentFam.getSecondParent().getID();
                    familyList.put("fourthGreatGrandparent", fourthGreatGrandparent);
                }
            }
        }

        if (this.getSecondParent() != null) {
            int secondParent = this.getSecondParent().getID();
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
                if (thirdAuntOrUncleFam.getFirstChild() != null) {
                    int thirdCousin = thirdAuntOrUncleFam.getFirstChild().getID();
                    familyList.put("thirdCousin", thirdCousin);
                }
                if (thirdAuntOrUncleFam.getSecondChild() != null) {
                    int fourthCousin = thirdAuntOrUncleFam.getSecondChild().getID();
                    familyList.put("fourthCousin", fourthCousin);
                }
            }
            if (secondParentFam.getFirstParent() != null) {
                int thirdGrandparent = secondParentFam.getFirstParent().getID();
                familyList.put("thirdGrandparent", thirdGrandparent);
                FamilyManager thirdGrandparentFam = new FamilyManager(thirdGrandparent, plugin);

                if (thirdGrandparentFam.getSibling() != null) {
                    int thirdGreatAuntOrUncle = thirdGrandparentFam.getSibling().getID();
                    familyList.put("thirdGreatAuntOrUncle", thirdGreatAuntOrUncle);
                }
                if (thirdGrandparentFam.getFirstParent() != null) {
                    int fifthGreatGrandparent = thirdGrandparentFam.getFirstParent().getID();
                    familyList.put("fifthGreatGrandparent", fifthGreatGrandparent);
                }
                if (thirdGrandparentFam.getSecondParent() != null) {
                    int sixthGreatGrandparent = thirdGrandparentFam.getSecondParent().getID();
                    familyList.put("sixthGreatGrandparent", sixthGreatGrandparent);
                }
            }
            if (secondParentFam.getSecondParent() != null) {
                int fourthGrandparent = secondParentFam.getSecondParent().getID();
                familyList.put("fourthGrandparent", fourthGrandparent);
                FamilyManager fourthGrandparentFam = new FamilyManager(fourthGrandparent, plugin);

                if (fourthGrandparentFam.getSibling() != null) {
                    int fourthGreatAuntOrUncle = fourthGrandparentFam.getSibling().getID();
                    familyList.put("fourthGreatAuntOrUncle", fourthGreatAuntOrUncle);
                }
                if (fourthGrandparentFam.getFirstParent() != null) {
                    int seventhGreatGrandparent = fourthGrandparentFam.getFirstParent().getID();
                    familyList.put("seventhGreatGrandparent", seventhGreatGrandparent);
                }
                if (fourthGrandparentFam.getSecondParent() != null) {
                    int eighthGreatGrandparent = fourthGrandparentFam.getSecondParent().getID();
                    familyList.put("eighthGreatGrandparent", eighthGreatGrandparent);
                }
            }
        }

        if (this.getFirstChild() != null) {
            int firstChild = this.getFirstChild().getID();
            familyList.put("firstChild", firstChild);
            FamilyManager firstChildFam = new FamilyManager(firstChild, plugin);

            if (firstChildFam.getPartner() != null) {
                int firstChildInLaw = firstChildFam.getPartner().getID();
                familyList.put("firstChildInLaw", firstChildInLaw);
            }
            if (firstChildFam.getFirstChild() != null) {
                int firstGrandchild = firstChildFam.getFirstChild().getID();
                familyList.put("firstGrandchild", firstGrandchild);
                FamilyManager firstGrandchildFam = new FamilyManager(firstGrandchild, plugin);
                if (firstGrandchildFam.getPartner() != null) {
                    int firstGrandchildInLaw = firstGrandchildFam.getPartner().getID();
                    familyList.put("firstGrandchildInLaw", firstGrandchildInLaw);
                }
                if (firstGrandchildFam.getFirstChild() != null) {
                    int firstGreatGrandchild = firstGrandchildFam.getFirstChild().getID();
                    familyList.put("firstGreatGrandchild", firstGreatGrandchild);
                }
                if (firstGrandchildFam.getSecondChild() != null) {
                    int secondGreatGrandchild = firstGrandchildFam.getSecondChild().getID();
                    familyList.put("secondGreatGrandchild", secondGreatGrandchild);
                }
            }
            if (firstChildFam.getSecondChild() != null) {
                int secondGrandchild = firstChildFam.getSecondChild().getID();
                familyList.put("secondGrandchild", secondGrandchild);
                FamilyManager secondGrandchildFam = new FamilyManager(secondGrandchild, plugin);
                if (secondGrandchildFam.getPartner() != null) {
                    int secondGrandchildInLaw = secondGrandchildFam.getPartner().getID();
                    familyList.put("secondGrandchildInLaw", secondGrandchildInLaw);
                }
                if (secondGrandchildFam.getFirstChild() != null) {
                    int thirdGreatGrandchild = secondGrandchildFam.getFirstChild().getID();
                    familyList.put("thirdGreatGrandchild", thirdGreatGrandchild);
                }
                if (secondGrandchildFam.getSecondChild() != null) {
                    int fourthGreatGrandchild = secondGrandchildFam.getSecondChild().getID();
                    familyList.put("fourthGreatGrandchild", fourthGreatGrandchild);
                }
            }
        }

        if (this.getSecondChild() != null) {
            int secondChild = this.getSecondChild().getID();
            familyList.put("secondChild", secondChild);
            FamilyManager secondChildFam = new FamilyManager(secondChild, plugin);

            if (secondChildFam.getPartner() != null) {
                int secondChildInLaw = secondChildFam.getPartner().getID();
                familyList.put("secondChildInLaw", secondChildInLaw);
            }
            if (secondChildFam.getFirstChild() != null) {
                int thirdGrandchild = secondChildFam.getFirstChild().getID();
                familyList.put("thirdGrandchild", thirdGrandchild);
                FamilyManager thirdGrandchildFam = new FamilyManager(thirdGrandchild, plugin);
                if (thirdGrandchildFam.getPartner() != null) {
                    int thirdGrandchildInLaw = thirdGrandchildFam.getPartner().getID();
                    familyList.put("thirdGrandchildInLaw", thirdGrandchildInLaw);
                }
                if (thirdGrandchildFam.getFirstChild() != null) {
                    int fifthGreatGrandchild = thirdGrandchildFam.getFirstChild().getID();
                    familyList.put("fifthGreatGrandchild", fifthGreatGrandchild);
                }
                if (thirdGrandchildFam.getSecondChild() != null) {
                    int sixthGreatGrandchild = thirdGrandchildFam.getSecondChild().getID();
                    familyList.put("sixthGreatGrandchild", sixthGreatGrandchild);
                }
            }
            if (secondChildFam.getSecondChild() != null) {
                int fourthGrandchild = secondChildFam.getSecondChild().getID();
                familyList.put("fourthGrandchild", fourthGrandchild);
                FamilyManager fourthGrandchildFam = new FamilyManager(fourthGrandchild, plugin);
                if (fourthGrandchildFam.getPartner() != null) {
                    int fourthGrandchildInLaw = fourthGrandchildFam.getPartner().getID();
                    familyList.put("fourthGrandchildInLaw", fourthGrandchildInLaw);
                }
                if (fourthGrandchildFam.getFirstChild() != null) {
                    int seventhGreatGrandchild = fourthGrandchildFam.getFirstChild().getID();
                    familyList.put("seventhGreatGrandchild", seventhGreatGrandchild);
                }
                if (fourthGrandchildFam.getSecondChild() != null) {
                    int eighthGreatGrandchild = fourthGrandchildFam.getSecondChild().getID();
                    familyList.put("eighthGreatGrandchild", eighthGreatGrandchild);
                }
            }
        }

        return familyList;
    }

}


