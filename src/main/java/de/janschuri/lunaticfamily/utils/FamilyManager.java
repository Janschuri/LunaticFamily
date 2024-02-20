package de.janschuri.lunaticfamily.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticfamily.Main;
import org.bukkit.Bukkit;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FamilyManager {


    private final Main plugin;
    private final String uuid;
    private String name;
    private final String skinURL;
    private String partner;
    private Timestamp marryDate;
    private String sibling;
    private String firstParent;
    private String secondParent;
    private String firstChild;
    private String secondChild;
    private String gender;
    private String background;
    private final BiMap<String, String> familyList = HashBiMap.create();

    public FamilyManager(String uuid, Main plugin) {

        this.plugin = plugin;
        this.uuid = uuid;

        partner = Main.getDatabase().getPartner(uuid);
        marryDate = Main.getDatabase().getMarryDate(uuid);
        sibling = Main.getDatabase().getSibling(uuid);
        firstParent = Main.getDatabase().getFirstParent(uuid);
        secondParent = Main.getDatabase().getSecondParent(uuid);
        firstChild = Main.getDatabase().getFirstChild(uuid);
        secondChild = Main.getDatabase().getSecondChild(uuid);


        if (Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() != null) {
            name = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
        } else {
            name = Main.getDatabase().getName(uuid);
        }


        if (Bukkit.getPlayer(UUID.fromString(uuid)) != null) {
            skinURL = Bukkit.getPlayer(UUID.fromString(uuid)).getPlayerProfile().getTextures().getSkin().toString();
        } else if (Main.getDatabase().getSkinURL(uuid) != null) {
            skinURL = Main.getDatabase().getSkinURL(uuid);
        } else {
            skinURL = "http://textures.minecraft.net/texture/2705fd94a0c431927fb4e639b0fcfb49717e412285a02b439e0112da22b2e2ec";
        }


        if (Main.getDatabase().getGender(uuid) == null) {
            gender = plugin.defaultGender;
        } else {
            gender = Main.getDatabase().getGender(uuid);
        }

        if (Main.getDatabase().getGender(uuid) == null) {
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
            return Main.getDatabase().getName(uuid);
        }
    }

    public void savePlayerData() {
        Main.getDatabase().saveData(uuid, name, skinURL, partner, marryDate, sibling, firstParent, secondParent, firstChild, secondChild, gender, background);
    }

    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
        savePlayerData();
    }

    public String getSkinURL() {
        return skinURL;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
        savePlayerData();
    }

    public String getMarryDate() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format the timestamp
        String formattedDate = formatter.format(this.marryDate.toLocalDateTime());

        return formattedDate;
    }

    public void setMarryDate(Timestamp marryDate) {
        this.marryDate = marryDate;
        savePlayerData();
    }

    public String getSibling() {
        return sibling;
    }

    public void setSibling(String sibling) {
        this.sibling = sibling;
        savePlayerData();
    }

    public String getFirstParent() {
        return firstParent;
    }

    public String getSecondParent() {
        return secondParent;
    }

    public void setFirstParent(String firstParent) {
        this.firstParent = firstParent;
        savePlayerData();
    }

    public void setSecondParent(String secondParent) {
        this.secondParent = secondParent;
        savePlayerData();
    }


    public String getFirstChild() {
        return firstChild;
    }

    public String getSecondChild() {
        return secondChild;
    }

    public void setFirstChild(String firstChild) {
        this.firstChild = firstChild;
        savePlayerData();
    }

    public void setSecondChild(String secondChild) {
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

    public BiMap<String, String> getFamilyList() {

        if (this.getPartner() != null) {
            String partner = this.getPartner();
            familyList.put("partner", partner);
            FamilyManager partnerFam = new FamilyManager(partner, plugin);

            if (partnerFam.getSibling() != null) {
                String secondSiblingInLaw = partnerFam.getSibling();
                familyList.put("secondSiblingInLaw", secondSiblingInLaw);
                FamilyManager secondSiblingInLawFam = new FamilyManager(secondSiblingInLaw, plugin);

                if (secondSiblingInLawFam.getPartner() != null) {
                    String thirdSiblingInLaw = secondSiblingInLawFam.getPartner();
                    familyList.put("thirdSiblingInLaw", thirdSiblingInLaw);
                }

                if (secondSiblingInLawFam.getFirstChild() != null) {
                    String thirdNieceOrNephew = secondSiblingInLawFam.getFirstChild();
                    familyList.put("thirdNieceOrNephew", thirdNieceOrNephew);
                }

                if (secondSiblingInLawFam.getSecondChild() != null) {
                    String fourthNieceOrNephew = secondSiblingInLawFam.getSecondChild();
                    familyList.put("fourthNieceOrNephew", fourthNieceOrNephew);
                }

            }

            if (partnerFam.getFirstParent() != null) {
                String firstParentInLaw = partnerFam.getFirstParent();
                familyList.put("firstParentInLaw", firstParentInLaw);
            }

            if (partnerFam.getSecondParent() != null) {
                String secondParentInLaw = partnerFam.getSecondParent();
                familyList.put("secondParentInLaw", secondParentInLaw);
            }
        }

        if (this.getSibling() != null) {
            String sibling = this.getSibling();
            familyList.put("sibling", sibling);
            FamilyManager siblingFam = new FamilyManager(sibling, plugin);

            if (siblingFam.getPartner() != null) {
                String firstSiblingInLaw = siblingFam.getPartner();
                familyList.put("firstSiblingInLaw", firstSiblingInLaw);
            }

            if (siblingFam.getFirstChild() != null) {
                String firstNieceOrNephew = siblingFam.getFirstChild();
                familyList.put("firstNieceOrNephew", firstNieceOrNephew);
            }

            if (siblingFam.getSecondChild() != null) {
                String secondNieceOrNephew = siblingFam.getSecondChild();
                familyList.put("secondNieceOrNephew", secondNieceOrNephew);
            }
        }

        if (this.getFirstParent() != null) {
            String firstParent = this.getFirstParent();
            familyList.put("firstParent", firstParent);
            FamilyManager firstParentFam = new FamilyManager(firstParent, plugin);
            if (firstParentFam.getSibling() != null) {
                String secondAuntOrUncle = firstParentFam.getSibling();
                familyList.put("secondAuntOrUncle", secondAuntOrUncle);
                FamilyManager secondAuntOrUncleFam = new FamilyManager(secondAuntOrUncle, plugin);
                if (secondAuntOrUncleFam.getPartner() != null) {
                    String firstAuntOrUncle = secondAuntOrUncleFam.getPartner();
                    familyList.put("firstAuntOrUncle", firstAuntOrUncle);
                }
                if (secondAuntOrUncleFam.getFirstChild() != null) {
                    String firstCousin = secondAuntOrUncleFam.getFirstChild();
                    familyList.put("firstCousin", firstCousin);
                }
                if (secondAuntOrUncleFam.getSecondChild() != null) {
                    String secondCousin = secondAuntOrUncleFam.getSecondChild();
                    familyList.put("secondCousin", secondCousin);
                }
            }
            if (firstParentFam.getFirstParent() != null) {
                String firstGrandparent = firstParentFam.getFirstParent();
                familyList.put("firstGrandparent", firstGrandparent);
                FamilyManager firstGrandparentFam = new FamilyManager(firstGrandparent, plugin);

                if (firstGrandparentFam.getSibling() != null) {
                    String firstGreatAuntOrUncle = firstGrandparentFam.getSibling();
                    familyList.put("firstGreatAuntOrUncle", firstGreatAuntOrUncle);
                }
                if (firstGrandparentFam.getFirstParent() != null) {
                    String firstGreatGrandparent = firstGrandparentFam.getFirstParent();
                    familyList.put("firstGreatGrandparent", firstGreatGrandparent);
                }
                if (firstGrandparentFam.getSecondParent() != null) {
                    String secondGreatGrandparent = firstGrandparentFam.getSecondParent();
                    familyList.put("secondGreatGrandparent", secondGreatGrandparent);
                }
            }
            if (firstParentFam.getSecondParent() != null) {
                String secondGrandparent = firstParentFam.getSecondParent();
                familyList.put("secondGrandparent", secondGrandparent);
                FamilyManager secondGrandparentFam = new FamilyManager(secondGrandparent, plugin);

                if (secondGrandparentFam.getSibling() != null) {
                    String secondGreatAuntOrUncle = secondGrandparentFam.getSibling();
                    familyList.put("secondGreatAuntOrUncle", secondGreatAuntOrUncle);
                }
                if (secondGrandparentFam.getFirstParent() != null) {
                    String thirdGreatGrandparent = secondGrandparentFam.getFirstParent();
                    familyList.put("thirdGreatGrandparent", thirdGreatGrandparent);
                }
                if (secondGrandparentFam.getSecondParent() != null) {
                    String fourthGreatGrandparent = secondGrandparentFam.getSecondParent();
                    familyList.put("fourthGreatGrandparent", fourthGreatGrandparent);
                }
            }
        }

        if (this.getSecondParent() != null) {
            String secondParent = this.getSecondParent();
            familyList.put("secondParent", secondParent);
            FamilyManager secondParentFam = new FamilyManager(secondParent, plugin);

            if (secondParentFam.getSibling() != null) {
                String thirdAuntOrUncle = secondParentFam.getSibling();
                familyList.put("thirdAuntOrUncle", thirdAuntOrUncle);
                FamilyManager thirdAuntOrUncleFam = new FamilyManager(thirdAuntOrUncle, plugin);
                if (thirdAuntOrUncleFam.getPartner() != null) {
                    String fourthAuntOrUncle = thirdAuntOrUncleFam.getPartner();
                    familyList.put("fourthAuntOrUncle", fourthAuntOrUncle);
                }
                if (thirdAuntOrUncleFam.getFirstChild() != null) {
                    String thirdCousin = thirdAuntOrUncleFam.getFirstChild();
                    familyList.put("thirdCousin", thirdCousin);
                }
                if (thirdAuntOrUncleFam.getSecondChild() != null) {
                    String fourthCousin = thirdAuntOrUncleFam.getSecondChild();
                    familyList.put("fourthCousin", fourthCousin);
                }
            }
            if (secondParentFam.getFirstParent() != null) {
                String thirdGrandparent = secondParentFam.getFirstParent();
                familyList.put("thirdGrandparent", thirdGrandparent);
                FamilyManager thirdGrandparentFam = new FamilyManager(thirdGrandparent, plugin);

                if (thirdGrandparentFam.getSibling() != null) {
                    String thirdGreatAuntOrUncle = thirdGrandparentFam.getSibling();
                    familyList.put("thirdGreatAuntOrUncle", thirdGreatAuntOrUncle);
                }
                if (thirdGrandparentFam.getFirstParent() != null) {
                    String fifthGreatGrandparent = thirdGrandparentFam.getFirstParent();
                    familyList.put("fifthGreatGrandparent", fifthGreatGrandparent);
                }
                if (thirdGrandparentFam.getSecondParent() != null) {
                    String sixthGreatGrandparent = thirdGrandparentFam.getSecondParent();
                    familyList.put("sixthGreatGrandparent", sixthGreatGrandparent);
                }
            }
            if (secondParentFam.getSecondParent() != null) {
                String fourthGrandparent = secondParentFam.getSecondParent();
                familyList.put("fourthGrandparent", fourthGrandparent);
                FamilyManager fourthGrandparentFam = new FamilyManager(fourthGrandparent, plugin);

                if (fourthGrandparentFam.getSibling() != null) {
                    String fourthGreatAuntOrUncle = fourthGrandparentFam.getSibling();
                    familyList.put("fourthGreatAuntOrUncle", fourthGreatAuntOrUncle);
                }
                if (fourthGrandparentFam.getFirstParent() != null) {
                    String seventhGreatGrandparent = fourthGrandparentFam.getFirstParent();
                    familyList.put("seventhGreatGrandparent", seventhGreatGrandparent);
                }
                if (fourthGrandparentFam.getSecondParent() != null) {
                    String eighthGreatGrandparent = fourthGrandparentFam.getSecondParent();
                    familyList.put("eighthGreatGrandparent", eighthGreatGrandparent);
                }
            }
        }

        if (this.getFirstChild() != null) {
            String firstChild = this.getFirstChild();
            familyList.put("firstChild", firstChild);
            FamilyManager firstChildFam = new FamilyManager(firstChild, plugin);

            if (firstChildFam.getPartner() != null) {
                String firstChildInLaw = firstChildFam.getPartner();
                familyList.put("firstChildInLaw", firstChildInLaw);
            }
            if (firstChildFam.getFirstChild() != null) {
                String firstGrandchild = firstChildFam.getFirstChild();
                familyList.put("firstGrandchild", firstGrandchild);
                FamilyManager firstGrandchildFam = new FamilyManager(firstGrandchild, plugin);
                if (firstGrandchildFam.getPartner() != null) {
                    String firstGrandchildInLaw = firstGrandchildFam.getPartner();
                    familyList.put("firstGrandchildInLaw", firstGrandchildInLaw);
                }
                if (firstGrandchildFam.getFirstChild() != null) {
                    String firstGreatGrandchild = firstGrandchildFam.getFirstChild();
                    familyList.put("firstGreatGrandchild", firstGreatGrandchild);
                }
                if (firstGrandchildFam.getSecondChild() != null) {
                    String secondGreatGrandchild = firstGrandchildFam.getSecondChild();
                    familyList.put("secondGreatGrandchild", secondGreatGrandchild);
                }
            }
            if (firstChildFam.getSecondChild() != null) {
                String secondGrandchild = firstChildFam.getSecondChild();
                familyList.put("secondGrandchild", secondGrandchild);
                FamilyManager secondGrandchildFam = new FamilyManager(secondGrandchild, plugin);
                if (secondGrandchildFam.getPartner() != null) {
                    String secondGrandchildInLaw = secondGrandchildFam.getPartner();
                    familyList.put("secondGrandchildInLaw", secondGrandchildInLaw);
                }
                if (secondGrandchildFam.getFirstChild() != null) {
                    String thirdGreatGrandchild = secondGrandchildFam.getFirstChild();
                    familyList.put("thirdGreatGrandchild", thirdGreatGrandchild);
                }
                if (secondGrandchildFam.getSecondChild() != null) {
                    String fourthGreatGrandchild = secondGrandchildFam.getSecondChild();
                    familyList.put("fourthGreatGrandchild", fourthGreatGrandchild);
                }
            }
        }

        if (this.getSecondChild() != null) {
            String secondChild = this.getSecondChild();
            familyList.put("secondChild", secondChild);
            FamilyManager secondChildFam = new FamilyManager(secondChild, plugin);

            if (secondChildFam.getPartner() != null) {
                String secondChildInLaw = secondChildFam.getPartner();
                familyList.put("secondChildInLaw", secondChildInLaw);
            }
            if (secondChildFam.getFirstChild() != null) {
                String thirdGrandchild = secondChildFam.getFirstChild();
                familyList.put("thirdGrandchild", thirdGrandchild);
                FamilyManager thirdGrandchildFam = new FamilyManager(thirdGrandchild, plugin);
                if (thirdGrandchildFam.getPartner() != null) {
                    String thirdGrandchildInLaw = thirdGrandchildFam.getPartner();
                    familyList.put("thirdGrandchildInLaw", thirdGrandchildInLaw);
                }
                if (thirdGrandchildFam.getFirstChild() != null) {
                    String fifthGreatGrandchild = thirdGrandchildFam.getFirstChild();
                    familyList.put("fifthGreatGrandchild", fifthGreatGrandchild);
                }
                if (thirdGrandchildFam.getSecondChild() != null) {
                    String sixthGreatGrandchild = thirdGrandchildFam.getSecondChild();
                    familyList.put("sixthGreatGrandchild", sixthGreatGrandchild);
                }
            }
            if (secondChildFam.getSecondChild() != null) {
                String fourthGrandchild = secondChildFam.getSecondChild();
                familyList.put("fourthGrandchild", fourthGrandchild);
                FamilyManager fourthGrandchildFam = new FamilyManager(fourthGrandchild, plugin);
                if (fourthGrandchildFam.getPartner() != null) {
                    String fourthGrandchildInLaw = fourthGrandchildFam.getPartner();
                    familyList.put("fourthGrandchildInLaw", fourthGrandchildInLaw);
                }
                if (fourthGrandchildFam.getFirstChild() != null) {
                    String seventhGreatGrandchild = fourthGrandchildFam.getFirstChild();
                    familyList.put("seventhGreatGrandchild", seventhGreatGrandchild);
                }
                if (fourthGrandchildFam.getSecondChild() != null) {
                    String eighthGreatGrandchild = fourthGrandchildFam.getSecondChild();
                    familyList.put("eighthGreatGrandchild", eighthGreatGrandchild);
                }
            }
        }

        return familyList;
    }
}


