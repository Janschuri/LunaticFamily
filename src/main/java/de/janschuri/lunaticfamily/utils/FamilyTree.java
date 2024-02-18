package de.janschuri.lunaticfamily.utils;

import com.google.common.collect.BiMap;
import de.janschuri.lunaticfamily.Main;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementFlag;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class FamilyTree {

    private final Main plugin;
    private final Player player;
    private final FamilyManager playerFam;

    private final BiMap familyList;

    public FamilyTree(Player player, Main plugin) {
        this.plugin = plugin;
        this.player = player;

        String uuid = player.getUniqueId().toString();
        this.playerFam = new FamilyManager(uuid, plugin);
        this.familyList = playerFam.getFamilyList();


        String title = playerFam.getName();
        String description = "";
        AdvancementDisplay.AdvancementFrame frame = AdvancementDisplay.AdvancementFrame.CHALLENGE;
        AdvancementVisibility visibility = AdvancementVisibility.ALWAYS;

        if (playerFam.getGender().equalsIgnoreCase("fe")) {
            description = plugin.relationshipsFe.get("ego").toString();
        }
        if (playerFam.getGender().equalsIgnoreCase("ma")) {
            description = plugin.relationshipsMa.get("ego").toString();
        }

        String skinURL = playerFam.getSkinURL();
        ItemStack icon = Main.getSkull(skinURL);


        AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);
        display.setBackgroundTexture("textures/block/moss_block.png");
        display.setX(0.0f);
        display.setY(-2.0f);

        Advancement ego = new Advancement(new NameKey("familyTree", "ego"), display);

        //own family
            //partner
            Advancement partner = createAdvancement(ego, "partner", 2.0f, 0.0f);
                //family of partner
                    //sibling of partner
                    Advancement secondSiblingInLawFirstHolder = createAdvancement(partner, "secondSiblingInLawFirstHolder", -0.5f, 0.0f);
                    Advancement secondSiblingInLawSecondHolder = createAdvancement(secondSiblingInLawFirstHolder, "secondSiblingInLawSecondHolder", 0.0f, -1.0f);
                    Advancement secondSiblingInLawThirdHolder = createAdvancement(secondSiblingInLawSecondHolder, "secondSiblingInLawThirdHolder", 2.0f, 0.0f);
                    Advancement secondSiblingInLaw = createAdvancement(secondSiblingInLawThirdHolder, "secondSiblingInLaw", 0.5f, 1.0f);
                        //family of sibling of partner
                            //partner of sibling of partner
                            Advancement thirdSiblingInLaw = createAdvancement(secondSiblingInLaw, "thirdSiblingInLaw", 4.0f, 0.0f);
                            //first child of sibling of partner
                            Advancement thirdNieceOrNephewFirstHolder = createAdvancement(secondSiblingInLaw, "thirdNieceOrNephewFirstHolder", 2.5f,0.0f);
                            Advancement thirdNieceOrNephewSecondHolder = createAdvancement(thirdNieceOrNephewFirstHolder, "thirdNieceOrNephewSecondHolder", 0.0f,1.0f);
                            Advancement thirdNieceOrNephewThirdHolder = createAdvancement(thirdNieceOrNephewSecondHolder, "thirdNieceOrNephewThirdHolder", -1.0f,0.0f);
                            Advancement thirdNieceOrNephew = createAdvancement(thirdNieceOrNephewThirdHolder, "thirdNieceOrNephew", 0.5f,1.0f);
                            //second child of sibling of partner
                            Advancement fourthNieceOrNephewFirstHolder = createAdvancement(secondSiblingInLaw, "fourthNieceOrNephewFirstHolder", 2.5f,0.0f);
                            Advancement fourthNieceOrNephewSecondHolder = createAdvancement(fourthNieceOrNephewFirstHolder, "fourthNieceOrNephewSecondHolder", 1.0f,1.0f);
                            Advancement fourthNieceOrNephew = createAdvancement(fourthNieceOrNephewSecondHolder, "fourthNieceOrNephew", 0.5f,1.0f);

                    //first parent of partner
                    Advancement firstParentInLawFirstHolder = createAdvancement(partner, "firstParentInLawFirstHolder", -0.5f, 0.0f);
                    Advancement firstParentInLawSecondHolder = createAdvancement(firstParentInLawFirstHolder, "firstParentInLawSecondHolder", 0.0f, -1.0f);
                    Advancement firstParentInLawThirdHolder = createAdvancement(firstParentInLawSecondHolder, "firstParentInLawThirdHolder", 1.0f, 0.0f);
                    Advancement firstParentInLaw = createAdvancement(firstParentInLawThirdHolder, "firstParentInLaw", -0.5f, -1.0f);
                    //second parent of partner
                    Advancement secondParentInLawFirstHolder = createAdvancement(partner, "secondParentInLawFirstHolder", -0.5f, 0.0f);
                    Advancement secondParentInLawSecondHolder = createAdvancement(secondParentInLawFirstHolder, "secondParentInLawSecondHolder", 0.0f, -1.0f);
                    Advancement secondParentInLawThirdHolder = createAdvancement(secondParentInLawSecondHolder, "secondParentInLawThirdHolder", 1.0f, 0.0f);
                    Advancement secondParentInLaw = createAdvancement(secondParentInLawThirdHolder, "secondParentInLaw", 1.5f, -1.0f);
            //sibling
            Advancement siblingFirstHolder = createAdvancement(ego, "siblingFirstHolder", -0.5f, 0.0f);
            Advancement siblingSecondHolder = createAdvancement(siblingFirstHolder, "siblingSecondHolder", -2.0f,-1.0f);
            Advancement siblingThirdHolder = createAdvancement(siblingSecondHolder, "siblingThirdHolder", 0.0f,1.0f);
            Advancement sibling = createAdvancement(siblingThirdHolder, "sibling", 0.5f,0.0f);
                //family of sibling
                    //partner of sibling
                    Advancement firstSiblingInLaw = createAdvancement(sibling, "firstSiblingInLaw", -4.0f, 0.0f);
                    //first child of sibling
                    Advancement firstNieceOrNephewFirstHolder = createAdvancement(sibling, "firstNieceOrNephewFirstHolder", -3.5f, 0.0f);
                    Advancement firstNieceOrNephewSecondHolder = createAdvancement(firstNieceOrNephewFirstHolder, "firstNieceOrNephewSecondHolder", 0.0f, 1.0f);
                    Advancement firstNieceOrNephewThirdHolder = createAdvancement(firstNieceOrNephewSecondHolder, "firstNieceOrNephewThirdHolder", -1.0f, 0.0f);
                    Advancement firstNieceOrNephew = createAdvancement(firstNieceOrNephewThirdHolder, "firstNieceOrNephew", 0.5f, 1.0f);
                    //second child of sibling
                    Advancement secondNieceOrNephewFirstHolder = createAdvancement(sibling, "secondNieceOrNephewFirstHolder", -3.5f, 0.0f);
                    Advancement secondNieceOrNephewSecondHolder = createAdvancement(secondNieceOrNephewFirstHolder, "secondNieceOrNephewSecondHolder", 1.0f, 1.0f);
                    Advancement secondNieceOrNephew = createAdvancement(secondNieceOrNephewSecondHolder, "secondNieceOrNephew", 0.5f, 1.0f);
            //first parent
            Advancement firstParentFirstHolder = createAdvancement(ego, "firstParentFirstHolder", -0.5f,0.0f);
            Advancement firstParentSecondHolder = createAdvancement(firstParentFirstHolder, "firstParentSecondHolder", -1.0f,-1.0f);
            Advancement firstParent = createAdvancement(firstParentSecondHolder, "firstParent", -0.5f,-1.0f);
                //family of first parent
                    //sibling of first parent
                    Advancement secondAuntOrUncleFirstHolder = createAdvancement(firstParent, "secondAuntOrUncleFirstHolder", -0.5f, 0.0f);
                    Advancement secondAuntOrUncleSecondHolder = createAdvancement(secondAuntOrUncleFirstHolder, "secondAuntOrUncleSecondHolder", -6.0f,-1.0f);
                    Advancement secondAuntOrUncleThirdHolder = createAdvancement(secondAuntOrUncleSecondHolder, "secondAuntOrUncleThirdHolder", 0.0f,1.0f);
                    Advancement secondAuntOrUncle = createAdvancement(secondAuntOrUncleThirdHolder, "secondAuntOrUncle", 0.5f,0.0f);
                        //family of sibling of first parent
                            //partner of sibling of first parent
                            Advancement firstAuntOrUncle = createAdvancement(secondAuntOrUncle, "firstAuntOrUncle", -2.0f, 0.0f);
                            //first child of sibling
                            Advancement firstCousinFirstHolder = createAdvancement(secondAuntOrUncle, "firstCousinFirstHolder", -1.5f, 0.0f);
                            Advancement firstCousinSecondHolder = createAdvancement(firstCousinFirstHolder, "firstCousinSecondHolder", 0.0f, 1.0f);
                            Advancement firstCousinThirdHolder = createAdvancement(firstCousinSecondHolder, "firstCousinThirdHolder", -1.0f, 0.0f);
                            Advancement firstCousin = createAdvancement(firstCousinThirdHolder, "firstCousin", 0.5f, 1.0f);
                            //second child of sibling
                            Advancement secondCousinFirstHolder = createAdvancement(secondAuntOrUncle, "secondCousinFirstHolder", -1.5f, 0.0f);
                            Advancement secondCousinSecondHolder = createAdvancement(secondCousinFirstHolder, "secondCousinSecondHolder", 1.0f, 1.0f);
                            Advancement secondCousin = createAdvancement(secondCousinSecondHolder, "secondCousin", 0.5f, 1.0f);
                    //first parent of first parent
                    Advancement firstGrandparentFirstHolder = createAdvancement(firstParent, "firstGrandparentFirstHolder", -0.5f,0.0f);
                    Advancement firstGrandparentSecondHolder = createAdvancement(firstGrandparentFirstHolder, "firstGrandparentSecondHolder", -3.0f,-1.0f);
                    Advancement firstGrandparent = createAdvancement(firstGrandparentSecondHolder, "firstGrandparent", -0.5f,-1.0f);
                        //family of first parent of first parent
                            //sibling of first parent of first parent
                            Advancement firstGreatAuntOrUncleFirstHolder = createAdvancement(firstGrandparent, "firstGreatAuntOrUncleFirstHolder", -0.5f, 0.0f);
                            Advancement firstGreatAuntOrUncleSecondHolder = createAdvancement(firstGreatAuntOrUncleFirstHolder, "firstGreatAuntOrUncleSecondHolder", -2.0f,-1.0f);
                            Advancement firstGreatAuntOrUncleThirdHolder = createAdvancement(firstGreatAuntOrUncleSecondHolder, "firstGreatAuntOrUncleThirdHolder", 0.0f,1.0f);
                            Advancement firstGreatAuntOrUncle = createAdvancement(firstGreatAuntOrUncleThirdHolder, "firstGreatAuntOrUncle", 0.5f,0.0f);
                            //first parent of first parent of first parent
                            Advancement firstGreatGrandparentFirstHolder = createAdvancement(firstGrandparent, "firstGreatGrandparentFirstHolder", -0.5f,0.0f);
                            Advancement firstGreatGrandparentSecondHolder = createAdvancement(firstGreatGrandparentFirstHolder, "firstGreatGrandparentSecondHolder", -1.0f,-1.0f);
                            Advancement firstGreatGrandparent = createAdvancement(firstGreatGrandparentSecondHolder, "firstGreatGrandparent", -0.5f,-1.0f);
                            //second parent of first parent of first parent
                            Advancement secondGreatGrandparentFirstHolder = createAdvancement(firstGrandparent, "secondGreatGrandparentFirstHolder", -0.5f,0.0f);
                            Advancement secondGreatGrandparentSecondHolder = createAdvancement(secondGreatGrandparentFirstHolder, "secondGreatGrandparentSecondHolder", -1.0f,-1.0f);
                            Advancement secondGreatGrandparent = createAdvancement(secondGreatGrandparentSecondHolder, "secondGreatGrandparent", 1.5f,-1.0f);
                    //second parent of first parent
                    Advancement secondGrandparentFirstHolder = createAdvancement(firstParent, "secondGrandparentFirstHolder", -0.5f,0.0f);
                    Advancement secondGrandparentSecondHolder = createAdvancement(secondGrandparentFirstHolder, "secondGrandparentSecondHolder", -3.0f,-1.0f);
                    Advancement secondGrandparent = createAdvancement(secondGrandparentSecondHolder, "secondGrandparent", 1.5f,-1.0f);
                        //family of second parent of first parent
                            //sibling of second parent of first parent
                            Advancement secondGreatAuntOrUncleFirstHolder = createAdvancement(secondGrandparent, "secondGreatAuntOrUncleFirstHolder", -0.5f, 0.0f);
                            Advancement secondGreatAuntOrUncleSecondHolder = createAdvancement(secondGreatAuntOrUncleFirstHolder, "secondGreatAuntOrUncleSecondHolder", 0.0f, -1.0f);
                            Advancement secondGreatAuntOrUncleThirdHolder = createAdvancement(secondGreatAuntOrUncleSecondHolder, "secondGreatAuntOrUncleThirdHolder", 2.0f, 0.0f);
                            Advancement secondGreatAuntOrUncle = createAdvancement(secondGreatAuntOrUncleThirdHolder, "secondGreatAuntOrUncle", 0.5f, 1.0f);
                            //first parent of second parent of first parent
                            Advancement thirdGreatGrandparentFirstHolder = createAdvancement(secondGrandparent, "thirdGreatGrandparentFirstHolder", -0.5f, 0.0f);
                            Advancement thirdGreatGrandparentSecondHolder = createAdvancement(thirdGreatGrandparentFirstHolder, "thirdGreatGrandparentSecondHolder", 0.0f, -1.0f);
                            Advancement thirdGreatGrandparentThirdHolder = createAdvancement(thirdGreatGrandparentSecondHolder, "thirdGreatGrandparentThirdHolder", 1.0f, 0.0f);
                            Advancement thirdGreatGrandparent = createAdvancement(thirdGreatGrandparentThirdHolder, "thirdGreatGrandparent", -0.5f, -1.0f);
                            //second parent of second parent of first parent
                            Advancement fourthGreatGrandparentFirstHolder = createAdvancement(secondGrandparent, "fourthGreatGrandparentFirstHolder", -0.5f, 0.0f);
                            Advancement fourthGreatGrandparentSecondHolder = createAdvancement(fourthGreatGrandparentFirstHolder, "fourthGreatGrandparentSecondHolder", 0.0f, -1.0f);
                            Advancement fourthGreatGrandparentThirdHolder = createAdvancement(fourthGreatGrandparentSecondHolder, "fourthGreatGrandparentThirdHolder", 1.0f, 0.0f);
                            Advancement fourthGreatGrandparent = createAdvancement(fourthGreatGrandparentThirdHolder, "fourthGreatGrandparent", 1.5f, -1.0f);

            //second parent
            Advancement secondParentFirstHolder = createAdvancement(ego, "secondParentFirstHolder", -0.5f,0.0f);
            Advancement secondParentSecondHolder = createAdvancement(secondParentFirstHolder, "secondParentSecondHolder", -1.0f,-1.0f);
            Advancement secondParent = createAdvancement(secondParentSecondHolder, "secondParent", 1.5f,-1.0f);
                //family of second parent
                    //sibling of second parent
                    Advancement thirdAuntOrUncleFirstHolder = createAdvancement(secondParent, "thirdAuntOrUncleFirstHolder", -0.5f, 0.0f);
                    Advancement thirdAuntOrUncleSecondHolder = createAdvancement(thirdAuntOrUncleFirstHolder, "thirdAuntOrUncleSecondHolder", 0.0f, -1.0f);
                    Advancement thirdAuntOrUncleThirdHolder = createAdvancement(thirdAuntOrUncleSecondHolder, "thirdAuntOrUncleThirdHolder", 6.0f, 0.0f);
                    Advancement thirdAuntOrUncle = createAdvancement(thirdAuntOrUncleThirdHolder, "thirdAuntOrUncle", 0.5f, 1.0f);
                        //family of sibling of second Parent
                            //partner of sibling of second Parent
                            Advancement fourthAuntOrUncle = createAdvancement(thirdAuntOrUncle, "fourthAuntOrUncle", 6.0f, 0.0f);
                            //first child of sibling of second Parent
                            Advancement thirdCousinFirstHolder = createAdvancement(thirdAuntOrUncle, "thirdCousinFirstHolder", 4.5f,0.0f);
                            Advancement thirdCousinSecondHolder = createAdvancement(thirdCousinFirstHolder, "thirdCousinSecondHolder", 0.0f,1.0f);
                            Advancement thirdCousinThirdHolder = createAdvancement(thirdCousinSecondHolder, "thirdCousinThirdHolder", -1.0f,0.0f);
                            Advancement thirdCousin = createAdvancement(thirdCousinThirdHolder, "thirdCousin", 0.5f,1.0f);
                            //second child of sibling of second Parent
                            Advancement fourthCousinFirstHolder = createAdvancement(thirdAuntOrUncle, "fourthCousinFirstHolder", 4.5f,0.0f);
                            Advancement fourthCousinSecondHolder = createAdvancement(fourthCousinFirstHolder, "fourthCousinSecondHolder", 1.0f,1.0f);
                            Advancement fourthCousin = createAdvancement(fourthCousinSecondHolder, "fourthCousin", 0.5f,1.0f);
                    //first parent of second parent
                    Advancement thirdGrandparentFirstHolder = createAdvancement(secondParent, "thirdGrandparentFirstHolder", -0.5f, 0.0f);
                    Advancement thirdGrandparentSecondHolder = createAdvancement(thirdGrandparentFirstHolder, "thirdGrandparentSecondHolder", 0.0f, -1.0f);
                    Advancement thirdGrandparentThirdHolder = createAdvancement(thirdGrandparentSecondHolder, "thirdGrandparentThirdHolder", 3.0f, 0.0f);
                    Advancement thirdGrandparent = createAdvancement(thirdGrandparentThirdHolder, "thirdGrandparent", -0.5f, -1.0f);
                        //family of first parent of second parent
                            //sibling of first parent of second parent
                            Advancement thirdGreatAuntOrUncleFirstHolder = createAdvancement(thirdGrandparent, "thirdGreatAuntOrUncleFirstHolder", -0.5f, 0.0f);
                            Advancement thirdGreatAuntOrUncleSecondHolder = createAdvancement(thirdGreatAuntOrUncleFirstHolder, "thirdGreatAuntOrUncleSecondHolder", -2.0f,-1.0f);
                            Advancement thirdGreatAuntOrUncleThirdHolder = createAdvancement(thirdGreatAuntOrUncleSecondHolder, "thirdGreatAuntOrUncleThirdHolder", 0.0f,1.0f);
                            Advancement thirdGreatAuntOrUncle = createAdvancement(thirdGreatAuntOrUncleThirdHolder, "thirdGreatAuntOrUncle", 0.5f,0.0f);
                            //first parent of first parent of second parent
                            Advancement fifthGreatGrandparentFirstHolder = createAdvancement(thirdGrandparent, "fifthGreatGrandparentFirstHolder", -0.5f,0.0f);
                            Advancement fifthGreatGrandparentSecondHolder = createAdvancement(fifthGreatGrandparentFirstHolder, "fifthGreatGrandparentSecondHolder", -1.0f,-1.0f);
                            Advancement fifthGreatGrandparent = createAdvancement(fifthGreatGrandparentSecondHolder, "fifthGreatGrandparent", -0.5f,-1.0f);
                            //second parent of first parent of second parent
                            Advancement sixthGreatGrandparentFirstHolder = createAdvancement(thirdGrandparent, "sixthGreatGrandparentFirstHolder", -0.5f,0.0f);
                            Advancement sixthGreatGrandparentSecondHolder = createAdvancement(sixthGreatGrandparentFirstHolder, "sixthGreatGrandparentSecondHolder", -1.0f,-1.0f);
                            Advancement sixthGreatGrandparent = createAdvancement(sixthGreatGrandparentSecondHolder, "sixthGreatGrandparent", 1.5f,-1.0f);
                    //second parent of second parent
                    Advancement fourthGrandparentFirstHolder = createAdvancement(secondParent, "fourthGrandparentFirstHolder", -0.5f, 0.0f);
                    Advancement fourthGrandparentSecondHolder = createAdvancement(fourthGrandparentFirstHolder, "fourthGrandparentSecondHolder", 0.0f, -1.0f);
                    Advancement fourthGrandparentThirdHolder = createAdvancement(fourthGrandparentSecondHolder, "fourthGrandparentThirdHolder", 3.0f, 0.0f);
                    Advancement fourthGrandparent = createAdvancement(fourthGrandparentThirdHolder, "fourthGrandparent", 1.5f, -1.0f);
                        //family of second parent of first parent
                            //sibling of second parent of first parent
                            Advancement fourthGreatAuntOrUncleFirstHolder = createAdvancement(fourthGrandparent, "fourthGreatAuntOrUncleFirstHolder", -0.5f, 0.0f);
                            Advancement fourthGreatAuntOrUncleSecondHolder = createAdvancement(fourthGreatAuntOrUncleFirstHolder, "fourthGreatAuntOrUncleSecondHolder", 0.0f, -1.0f);
                            Advancement fourthGreatAuntOrUncleThirdHolder = createAdvancement(fourthGreatAuntOrUncleSecondHolder, "fourthGreatAuntOrUncleThirdHolder", 2.0f, 0.0f);
                            Advancement fourthGreatAuntOrUncle = createAdvancement(fourthGreatAuntOrUncleThirdHolder, "fourthGreatAuntOrUncle", 0.5f, 1.0f);
                            //first parent of second parent of first parent
                            Advancement seventhGreatGrandparentFirstHolder = createAdvancement(fourthGrandparent, "seventhGreatGrandparentFirstHolder", -0.5f, 0.0f);
                            Advancement seventhGreatGrandparentSecondHolder = createAdvancement(seventhGreatGrandparentFirstHolder, "seventhGreatGrandparentSecondHolder", 0.0f, -1.0f);
                            Advancement seventhGreatGrandparentThirdHolder = createAdvancement(seventhGreatGrandparentSecondHolder, "seventhGreatGrandparentThirdHolder", 1.0f, 0.0f);
                            Advancement seventhGreatGrandparent = createAdvancement(seventhGreatGrandparentThirdHolder, "seventhGreatGrandparent", -0.5f, -1.0f);
                            //second parent of second parent of first parent
                            Advancement eighthGreatGrandparentFirstHolder = createAdvancement(fourthGrandparent, "eighthGreatGrandparentFirstHolder", -0.5f, 0.0f);
                            Advancement eighthGreatGrandparentSecondHolder = createAdvancement(eighthGreatGrandparentFirstHolder, "eighthGreatGrandparentSecondHolder", 0.0f, -1.0f);
                            Advancement eighthGreatGrandparentThirdHolder = createAdvancement(eighthGreatGrandparentSecondHolder, "eighthGreatGrandparentThirdHolder", 1.0f, 0.0f);
                            Advancement eighthGreatGrandparent = createAdvancement(eighthGreatGrandparentThirdHolder, "eighthGreatGrandparent", 1.5f, -1.0f);
            //first child
            Advancement firstChildFirstHolder = createAdvancement(ego, "firstChildFirstHolder", 0.5f,0.0f);
            Advancement firstChildSecondHolder = createAdvancement(firstChildFirstHolder, "firstChildSecondHolder", 0.0f,1.0f);
            Advancement firstChildThirdHolder = createAdvancement(firstChildSecondHolder, "firstChildThirdHolder", -1.0f,0.0f);
            Advancement firstChild = createAdvancement(firstChildThirdHolder, "firstChild", 0.5f,1.0f);
                //family of first child
                    //partner of first child
                    Advancement firstChildInLaw = createAdvancement(firstChild, "firstChildInLaw", -2.0f, 0.0f);
                    //first child of first child
                    Advancement firstGrandchildFirstHolder = createAdvancement(firstChild, "firstGrandchildFirstHolder", -1.5f,0.0f);
                    Advancement firstGrandchildSecondHolder = createAdvancement(firstGrandchildFirstHolder, "firstGrandchildSecondHolder", 0.0f,1.0f);
                    Advancement firstGrandchildThirdHolder = createAdvancement(firstGrandchildSecondHolder, "firstGrandchildThirdHolder", -3.0f,0.0f);
                    Advancement firstGrandchild = createAdvancement(firstGrandchildThirdHolder, "firstGrandchild", 0.5f,1.0f);
                        //family of first child of first child
                            //partner of first child of first child
                            Advancement firstGrandchildInLaw = createAdvancement(firstGrandchild, "firstGrandchildInLaw", -2.0f, 0.0f);
                            //first child of first child of first child
                            Advancement firstGreatGrandchildFirstHolder = createAdvancement(firstGrandchild, "firstGreatGrandchildFirstHolder", -1.5f, 0.0f);
                            Advancement firstGreatGrandchildSecondHolder = createAdvancement(firstGreatGrandchildFirstHolder, "firstGreatGrandchildSecondHolder", 0.0f, 1.0f);
                            Advancement firstGreatGrandchildThirdHolder = createAdvancement(firstGreatGrandchildSecondHolder, "firstGreatGrandchildThirdHolder", -1.0f, 0.0f);
                            Advancement firstGreatGrandchild = createAdvancement(firstGreatGrandchildThirdHolder, "firstGreatGrandchild", 0.5f, 1.0f);
                            //second child of first child of first child
                            Advancement secondGreatGrandchildFirstHolder = createAdvancement(firstGrandchild, "secondGreatGrandchildFirstHolder", -1.5f, 0.0f);
                            Advancement secondGreatGrandchildSecondHolder = createAdvancement(secondGreatGrandchildFirstHolder, "secondGreatGrandchildSecondHolder", 1.0f, 1.0f);
                            Advancement secondGreatGrandchild = createAdvancement(secondGreatGrandchildSecondHolder, "secondGreatGrandchild", 0.5f, 1.0f);
                    //second child of first child
                    Advancement secondGrandchildFirstHolder = createAdvancement(firstChild, "secondGrandchildFirstHolder", -1.5f,0.0f);
                    Advancement secondGrandchildSecondHolder = createAdvancement(secondGrandchildFirstHolder, "secondGrandchildSecondHolder", -2.0f,1.0f);
                    Advancement secondGrandchildThirdHolder = createAdvancement(secondGrandchildSecondHolder, "secondGrandchildThirdHolder", 1.0f,0.0f);
                    Advancement secondGrandchild = createAdvancement(secondGrandchildThirdHolder, "secondGrandchild", 0.5f,1.0f);
                        //family of child of first child
                            //partner of child of first child
                            Advancement secondGrandchildInLaw = createAdvancement(secondGrandchild, "secondGrandchildInLaw", 2.0f, 0.0f);
                            //first child of child of first child
                            Advancement thirdGreatGrandchildFirstHolder = createAdvancement(secondGrandchild, "thirdGreatGrandchildFirstHolder", 0.5f,0.0f);
                            Advancement thirdGreatGrandchildSecondHolder = createAdvancement(thirdGreatGrandchildFirstHolder, "thirdGreatGrandchildSecondHolder", 0.0f,1.0f);
                            Advancement thirdGreatGrandchildThirdHolder = createAdvancement(thirdGreatGrandchildSecondHolder, "thirdGreatGrandchildThirdHolder", -1.0f,0.0f);
                            Advancement thirdGreatGrandchild = createAdvancement(thirdGreatGrandchildThirdHolder, "thirdGreatGrandchild", 0.5f,1.0f);
                            //second child of child of first child
                            Advancement fourthGreatGrandchildFirstHolder = createAdvancement(secondGrandchild, "fourthGreatGrandchildFirstHolder", 0.5f,0.0f);
                            Advancement fourthGreatGrandchildSecondHolder = createAdvancement(fourthGreatGrandchildFirstHolder, "fourthGreatGrandchildSecondHolder", 1.0f,1.0f);
                            Advancement fourthGreatGrandchild = createAdvancement(fourthGreatGrandchildSecondHolder, "fourthGreatGrandchild", 0.5f,1.0f);
            //second child
            Advancement secondChildFirstHolder = createAdvancement(ego, "secondChildFirstHolder", 0.5f,0.0f);
            Advancement secondChildSecondHolder = createAdvancement(secondChildFirstHolder, "secondChildSecondHolder", 1.0f,1.0f);
            Advancement secondChild = createAdvancement(secondChildSecondHolder, "secondChild", 0.5f,1.0f);
                //family of second child
                    //partner of second child
                    Advancement secondChildInLaw = createAdvancement(secondChild, "secondChildInLaw", 2.0f, 0.0f);
                    //first child of second child
                    Advancement thirdGrandchildFirstHolder = createAdvancement(secondChild, "thirdGrandchildFirstHolder", 0.5f,0.0f);
                    Advancement thirdGrandchildSecondHolder = createAdvancement(thirdGrandchildFirstHolder, "thirdGrandchildSecondHolder", 2.0f,1.0f);
                    Advancement thirdGrandchildThirdHolder = createAdvancement(thirdGrandchildSecondHolder, "thirdGrandchildThirdHolder", -1.0f,0.0f);
                    Advancement thirdGrandchild = createAdvancement(thirdGrandchildThirdHolder, "thirdGrandchild", 0.5f,1.0f);
                        //family of first Child of second Child
                            //partner of first Child of second Child
                            Advancement thirdGrandchildInLaw = createAdvancement(thirdGrandchild, "thirdGrandchildInLaw", -2.0f, 0.0f);
                            //first child of first Child of second Child
                            Advancement fifthGreatGrandchildFirstHolder = createAdvancement(thirdGrandchild, "fifthGreatGrandchildFirstHolder", -1.5f, 0.0f);
                            Advancement fifthGreatGrandchildSecondHolder = createAdvancement(fifthGreatGrandchildFirstHolder, "fifthGreatGrandchildSecondHolder", 0.0f, 1.0f);
                            Advancement fifthGreatGrandchildThirdHolder = createAdvancement(fifthGreatGrandchildSecondHolder, "fifthGreatGrandchildThirdHolder", -1.0f, 0.0f);
                            Advancement fifthGreatGrandchild = createAdvancement(fifthGreatGrandchildThirdHolder, "fifthGreatGrandchild", 0.5f, 1.0f);
                            //second child of first Child of second Child
                            Advancement sixthGreatGrandchildFirstHolder = createAdvancement(thirdGrandchild, "sixthGreatGrandchildFirstHolder", -1.5f, 0.0f);
                            Advancement sixthGreatGrandchildSecondHolder = createAdvancement(sixthGreatGrandchildFirstHolder, "sixthGreatGrandchildSecondHolder", 1.0f, 1.0f);
                            Advancement sixthGreatGrandchild = createAdvancement(sixthGreatGrandchildSecondHolder, "sixthGreatGrandchild", 0.5f, 1.0f);
                    //second child of second child
                    Advancement fourthGrandchildFirstHolder = createAdvancement(secondChild, "fourthGrandchildFirstHolder", 0.5f,0.0f);
                    Advancement fourthGrandchildSecondHolder = createAdvancement(fourthGrandchildFirstHolder, "fourthGrandchildSecondHolder", 0.0f,1.0f);
                    Advancement fourthGrandchildThirdHolder = createAdvancement(fourthGrandchildSecondHolder, "fourthGrandchildThirdHolder", 3.0f,0.0f);
                    Advancement fourthGrandchild = createAdvancement(fourthGrandchildThirdHolder, "fourthGrandchild", 0.5f,1.0f);
                        //family of second child of second child
                            //partner of second child of second child
                            Advancement fourthGrandchildInLaw = createAdvancement(fourthGrandchild, "fourthGrandchildInLaw", 2.0f, 0.0f);
                            //first child of second child of second child
                            Advancement seventhGreatGrandchildFirstHolder = createAdvancement(fourthGrandchild, "seventhGreatGrandchildFirstHolder", 0.5f,0.0f);
                            Advancement seventhGreatGrandchildSecondHolder = createAdvancement(seventhGreatGrandchildFirstHolder, "seventhGreatGrandchildSecondHolder", 0.0f,1.0f);
                            Advancement seventhGreatGrandchildThirdHolder = createAdvancement(seventhGreatGrandchildSecondHolder, "seventhGreatGrandchildThirdHolder", -1.0f,0.0f);
                            Advancement seventhGreatGrandchild = createAdvancement(seventhGreatGrandchildThirdHolder, "seventhGreatGrandchild", 0.5f,1.0f);
                            //second child of second child of second child
                            Advancement eighthGreatGrandchildFirstHolder = createAdvancement(fourthGrandchild, "eighthGreatGrandchildFirstHolder", 0.5f,0.0f);
                            Advancement eighthGreatGrandchildSecondHolder = createAdvancement(eighthGreatGrandchildFirstHolder, "eighthGreatGrandchildSecondHolder", 1.0f,1.0f);
                            Advancement eighthGreatGrandchild = createAdvancement(eighthGreatGrandchildSecondHolder, "eighthGreatGrandchild", 0.5f,1.0f);



        AdvancementManager manager = new AdvancementManager(new NameKey("manager", player.getUniqueId().toString()));
        manager.addPlayer(player);

        manager.addAdvancement(ego);

        //partner
        manager.addAdvancement(partner);
            //family of partner
                //sibling of partner
                manager.addAdvancement(secondSiblingInLawFirstHolder);
                manager.addAdvancement(secondSiblingInLawSecondHolder);
                manager.addAdvancement(secondSiblingInLawThirdHolder);
                manager.addAdvancement(secondSiblingInLaw);
                    //family of sibling of partner
                        //partner of sibling of partner
                        manager.addAdvancement(thirdSiblingInLaw);
                        //first child of sibling of partner
                        manager.addAdvancement(thirdNieceOrNephewFirstHolder);
                        manager.addAdvancement(thirdNieceOrNephewSecondHolder);
                        manager.addAdvancement(thirdNieceOrNephewThirdHolder);
                        manager.addAdvancement(thirdNieceOrNephew);
                        //second child of sibling of partner
                        manager.addAdvancement(fourthNieceOrNephewFirstHolder);
                        manager.addAdvancement(fourthNieceOrNephewSecondHolder);
                        manager.addAdvancement(fourthNieceOrNephew);
                //first parent of partner
                manager.addAdvancement(firstParentInLawFirstHolder);
                manager.addAdvancement(firstParentInLawSecondHolder);
                manager.addAdvancement(firstParentInLawThirdHolder);
                manager.addAdvancement(firstParentInLaw);
                //second parent of partner
                manager.addAdvancement(secondParentInLawFirstHolder);
                manager.addAdvancement(secondParentInLawSecondHolder);
                manager.addAdvancement(secondParentInLawThirdHolder);
                manager.addAdvancement(secondParentInLaw);

        //sibling
        manager.addAdvancement(siblingFirstHolder);
        manager.addAdvancement(siblingSecondHolder);
        manager.addAdvancement(siblingThirdHolder);
        manager.addAdvancement(sibling);
            //family of sibling
                //partner of sibling
                manager.addAdvancement(firstSiblingInLaw);
                //first child of sibling
                manager.addAdvancement(firstNieceOrNephewFirstHolder);
                manager.addAdvancement(firstNieceOrNephewSecondHolder);
                manager.addAdvancement(firstNieceOrNephewThirdHolder);
                manager.addAdvancement(firstNieceOrNephew);
                //second child of sibling
                manager.addAdvancement(secondNieceOrNephewFirstHolder);
                manager.addAdvancement(secondNieceOrNephewSecondHolder);
                manager.addAdvancement(secondNieceOrNephew);

        //first parent
        manager.addAdvancement(firstParentFirstHolder);
        manager.addAdvancement(firstParentSecondHolder);
        manager.addAdvancement(firstParent);
            //family of first parent
                //sibling of first parent
                manager.addAdvancement(secondAuntOrUncleFirstHolder);
                manager.addAdvancement(secondAuntOrUncleSecondHolder);
                manager.addAdvancement(secondAuntOrUncleThirdHolder);
                manager.addAdvancement(secondAuntOrUncle);
                    //family of sibling of first parent
                        //partner of sibling of first parent
                        manager.addAdvancement(firstAuntOrUncle);
                        //first child of sibling of first parent
                        manager.addAdvancement(firstCousinFirstHolder);
                        manager.addAdvancement(firstCousinSecondHolder);
                        manager.addAdvancement(firstCousinThirdHolder);
                        manager.addAdvancement(firstCousin);
                        //second child of sibling of first parent
                        manager.addAdvancement(secondCousinFirstHolder);
                        manager.addAdvancement(secondCousinSecondHolder);
                        manager.addAdvancement(secondCousin);
                //first parent of first parent
                manager.addAdvancement(firstGrandparentFirstHolder);
                manager.addAdvancement(firstGrandparentSecondHolder);
                manager.addAdvancement(firstGrandparent);
                    //family of first parent of first parent
                        //sibling of first parent of first parent
                        manager.addAdvancement(firstGreatAuntOrUncleFirstHolder);
                        manager.addAdvancement(firstGreatAuntOrUncleSecondHolder);
                        manager.addAdvancement(firstGreatAuntOrUncleThirdHolder);
                        manager.addAdvancement(firstGreatAuntOrUncle);
                        //first parent of first parent of first parent
                        manager.addAdvancement(firstGreatGrandparentFirstHolder);
                        manager.addAdvancement(firstGreatGrandparentSecondHolder);
                        manager.addAdvancement(firstGreatGrandparent);
                        //second parent of first parent of first parent
                        manager.addAdvancement(secondGreatGrandparentFirstHolder);
                        manager.addAdvancement(secondGreatGrandparentSecondHolder);
                        manager.addAdvancement(secondGreatGrandparent);
                //second parent of first parent
                manager.addAdvancement(secondGrandparentFirstHolder);
                manager.addAdvancement(secondGrandparentSecondHolder);
                manager.addAdvancement(secondGrandparent);
                    //family of first parent of first parent
                        //sibling of first parent of first parent
                        manager.addAdvancement(secondGreatAuntOrUncleFirstHolder);
                        manager.addAdvancement(secondGreatAuntOrUncleSecondHolder);
                        manager.addAdvancement(secondGreatAuntOrUncleThirdHolder);
                        manager.addAdvancement(secondGreatAuntOrUncle);
                        //first parent of first parent of first parent
                        manager.addAdvancement(thirdGreatGrandparentFirstHolder);
                        manager.addAdvancement(thirdGreatGrandparentSecondHolder);
                        manager.addAdvancement(thirdGreatGrandparentThirdHolder);
                        manager.addAdvancement(thirdGreatGrandparent);
                        //second parent of first parent of first parent
                        manager.addAdvancement(fourthGreatGrandparentFirstHolder);
                        manager.addAdvancement(fourthGreatGrandparentSecondHolder);
                        manager.addAdvancement(fourthGreatGrandparentThirdHolder);
                        manager.addAdvancement(fourthGreatGrandparent);

        //second parent
        manager.addAdvancement(secondParentFirstHolder);
        manager.addAdvancement(secondParentSecondHolder);
        manager.addAdvancement(secondParent);
            //family of second parent
                //sibling of second parent
                manager.addAdvancement(thirdAuntOrUncleFirstHolder);
                manager.addAdvancement(thirdAuntOrUncleSecondHolder);
                manager.addAdvancement(thirdAuntOrUncleThirdHolder);
                manager.addAdvancement(thirdAuntOrUncle);
                    //family of sibling of second parent
                        //partner of sibling of second parent
                        manager.addAdvancement(fourthAuntOrUncle);
                        //first child of sibling of second parent
                        manager.addAdvancement(thirdCousinFirstHolder);
                        manager.addAdvancement(thirdCousinSecondHolder);
                        manager.addAdvancement(thirdCousinThirdHolder);
                        manager.addAdvancement(thirdCousin);
                        //second child of sibling of second parent
                        manager.addAdvancement(fourthCousinFirstHolder);
                        manager.addAdvancement(fourthCousinSecondHolder);
                        manager.addAdvancement(fourthCousin);
                //first parent of second parent
                manager.addAdvancement(thirdGrandparentFirstHolder);
                manager.addAdvancement(thirdGrandparentSecondHolder);
                manager.addAdvancement(thirdGrandparentThirdHolder);
                manager.addAdvancement(thirdGrandparent);
                    //family of first parent of second parent
                        //sibling of first parent of first parent
                        manager.addAdvancement(thirdGreatAuntOrUncleFirstHolder);
                        manager.addAdvancement(thirdGreatAuntOrUncleSecondHolder);
                        manager.addAdvancement(thirdGreatAuntOrUncleThirdHolder);
                        manager.addAdvancement(thirdGreatAuntOrUncle);
                        //first parent of first parent of second parent
                        manager.addAdvancement(fifthGreatGrandparentFirstHolder);
                        manager.addAdvancement(fifthGreatGrandparentSecondHolder);
                        manager.addAdvancement(fifthGreatGrandparent);
                        //second parent of first parent of second parent
                        manager.addAdvancement(sixthGreatGrandparentFirstHolder);
                        manager.addAdvancement(sixthGreatGrandparentSecondHolder);
                        manager.addAdvancement(sixthGreatGrandparent);
                //second parent of second parent
                manager.addAdvancement(fourthGrandparentFirstHolder);
                manager.addAdvancement(fourthGrandparentSecondHolder);
                manager.addAdvancement(fourthGrandparentThirdHolder);
                manager.addAdvancement(fourthGrandparent);
                    //family of second parent of second parent
                        //sibling of second parent of second parent
                        manager.addAdvancement(fourthGreatAuntOrUncleFirstHolder);
                        manager.addAdvancement(fourthGreatAuntOrUncleSecondHolder);
                        manager.addAdvancement(fourthGreatAuntOrUncleThirdHolder);
                        manager.addAdvancement(fourthGreatAuntOrUncle);
                        //first parent of second parent of second parent
                        manager.addAdvancement(seventhGreatGrandparentFirstHolder);
                        manager.addAdvancement(seventhGreatGrandparentSecondHolder);
                        manager.addAdvancement(seventhGreatGrandparentThirdHolder);
                        manager.addAdvancement(seventhGreatGrandparent);
                        //second parent of second parent of second parent
                        manager.addAdvancement(eighthGreatGrandparentFirstHolder);
                        manager.addAdvancement(eighthGreatGrandparentSecondHolder);
                        manager.addAdvancement(eighthGreatGrandparentThirdHolder);
                        manager.addAdvancement(eighthGreatGrandparent);

        //first child
        manager.addAdvancement(firstChildFirstHolder);
        manager.addAdvancement(firstChildSecondHolder);
        manager.addAdvancement(firstChildThirdHolder);
        manager.addAdvancement(firstChild);
            //family of first child
                //partner of first child
                manager.addAdvancement(firstChildInLaw);
                //first child of first child
                manager.addAdvancement(firstGrandchildFirstHolder);
                manager.addAdvancement(firstGrandchildSecondHolder);
                manager.addAdvancement(firstGrandchildThirdHolder);
                manager.addAdvancement(firstGrandchild);
                    //family of first child of first child
                        //partner of sibling of first parent
                        manager.addAdvancement(firstGrandchildInLaw);
                        //first child of first child of first child
                        manager.addAdvancement(firstGreatGrandchildFirstHolder);
                        manager.addAdvancement(firstGreatGrandchildSecondHolder);
                        manager.addAdvancement(firstGreatGrandchildThirdHolder);
                        manager.addAdvancement(firstGreatGrandchild);
                        //second child of first child of first child
                        manager.addAdvancement(secondGreatGrandchildFirstHolder);
                        manager.addAdvancement(secondGreatGrandchildSecondHolder);
                        manager.addAdvancement(secondGreatGrandchild);
                //second child of first child
                manager.addAdvancement(secondGrandchildFirstHolder);
                manager.addAdvancement(secondGrandchildSecondHolder);
                manager.addAdvancement(secondGrandchildThirdHolder);
                manager.addAdvancement(secondGrandchild);
                    //family of second child of first child
                        //partner of second child of first child
                        manager.addAdvancement(secondGrandchildInLaw);
                        //first child of second child of first child
                        manager.addAdvancement(thirdGreatGrandchildFirstHolder);
                        manager.addAdvancement(thirdGreatGrandchildSecondHolder);
                        manager.addAdvancement(thirdGreatGrandchildThirdHolder);
                        manager.addAdvancement(thirdGreatGrandchild);
                        //second child of second child of first child
                        manager.addAdvancement(fourthGreatGrandchildFirstHolder);
                        manager.addAdvancement(fourthGreatGrandchildSecondHolder);
                        manager.addAdvancement(fourthGreatGrandchild);

        //second child
        manager.addAdvancement(secondChildFirstHolder);
        manager.addAdvancement(secondChildSecondHolder);
        manager.addAdvancement(secondChild);
            //family of second child
                //partner of second child
                manager.addAdvancement(secondChildInLaw);
                //first child of second child
                manager.addAdvancement(thirdGrandchildFirstHolder);
                manager.addAdvancement(thirdGrandchildSecondHolder);
                manager.addAdvancement(thirdGrandchildThirdHolder);
                manager.addAdvancement(thirdGrandchild);
                    //family of first child of second child
                        //partner of first child of second child
                        manager.addAdvancement(thirdGrandchildInLaw);
                        //first child of first child of second child
                        manager.addAdvancement(fifthGreatGrandchildFirstHolder);
                        manager.addAdvancement(fifthGreatGrandchildSecondHolder);
                        manager.addAdvancement(fifthGreatGrandchildThirdHolder);
                        manager.addAdvancement(fifthGreatGrandchild);
                        //second child of first child of second child
                        manager.addAdvancement(sixthGreatGrandchildFirstHolder);
                        manager.addAdvancement(sixthGreatGrandchildSecondHolder);
                        manager.addAdvancement(sixthGreatGrandchild);
                //second child of second child
                manager.addAdvancement(fourthGrandchildFirstHolder);
                manager.addAdvancement(fourthGrandchildSecondHolder);
                manager.addAdvancement(fourthGrandchildThirdHolder);
                manager.addAdvancement(fourthGrandchild);
                    //family of second child of second child
                        //partner of second child of second child
                        manager.addAdvancement(fourthGrandchildInLaw);
                        //first child of second child of second child
                        manager.addAdvancement(seventhGreatGrandchildFirstHolder);
                        manager.addAdvancement(seventhGreatGrandchildSecondHolder);
                        manager.addAdvancement(seventhGreatGrandchildThirdHolder);
                        manager.addAdvancement(seventhGreatGrandchild);
                        //second child of second child of second child
                        manager.addAdvancement(eighthGreatGrandchildFirstHolder);
                        manager.addAdvancement(eighthGreatGrandchildSecondHolder);
                        manager.addAdvancement(eighthGreatGrandchild);




    }

    private Advancement createAdvancement(Advancement parent, String relation, Float x, Float y) {
        AdvancementDisplay.AdvancementFrame frame = AdvancementDisplay.AdvancementFrame.GOAL;
        AdvancementVisibility visibility = AdvancementVisibility.ALWAYS;

        if (!(familyList.containsKey(relation.replace("FirstHolder", "")
                .replace("SecondHolder", "")
                .replace("ThirdHolder", "")
                .replace("Holder", "")))) {
            visibility = AdvancementVisibility.HIDDEN;
        }


        ItemStack icon = new ItemStack(Material.STONE);

        String title = relation;
        String description = relation;
        Advancement advancement;

        if (familyList.containsKey(relation)) {
            String uuid = familyList.get(relation).toString();
            FamilyManager relationFam = new FamilyManager(uuid, plugin);
            title = relationFam.getName();

                String relationKey = relation.replace("first", "")
                        .replace("second", "")
                        .replace("third", "")
                        .replace("fourth", "")
                        .replace("fifth", "")
                        .replace("sixth", "")
                        .replace("seventh", "")
                        .replace("eighth", "");

                if (relationFam.getGender().equalsIgnoreCase("fe")) {
                    description = plugin.relationshipsFe.get(relationKey).toString();
                }
                if (relationFam.getGender().equalsIgnoreCase("ma")) {
                    description = plugin.relationshipsMa.get(relationKey).toString();
                }

                String skinURL = relationFam.getSkinURL();
                icon = Main.getSkull(skinURL);


            AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);
            display.setPositionOrigin(parent);
            display.setX(x);
            display.setY(y);

            advancement = new Advancement(parent, new NameKey("familyTree", relation), display);
        } else if (relation.equalsIgnoreCase("ego")) {
            title = playerFam.getName();

            if (playerFam.getGender().equalsIgnoreCase("fe")) {
                description = plugin.relationshipsFe.get("ego").toString();
            }
            if (playerFam.getGender().equalsIgnoreCase("ma")) {
                description = plugin.relationshipsMa.get("ego").toString();
            }

            String skinURL = playerFam.getSkinURL();
            icon = Main.getSkull(skinURL);


            AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);
            display.setPositionOrigin(parent);
            display.setX(x);
            display.setY(y);

            advancement = new Advancement(parent, new NameKey("familyTree", relation), display);
        }

        else {
            AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);
            display.setPositionOrigin(parent);
            display.setX(x);
            display.setY(y);

            advancement = new Advancement(parent, new NameKey("familyTree", relation), display
                    , AdvancementFlag.SEND_WITH_HIDDEN_BOOLEAN
            );
        }

        return advancement;
    }

    public void reloadTree (Player player){
        AdvancementManager manager = new AdvancementManager(new NameKey("manager", player.getUniqueId().toString()));
        manager.removePlayer(player);
        manager.addPlayer(player);
    }

}
