package de.janschuri.lunaticFamily.handler;

import com.google.common.collect.BiMap;
import de.janschuri.lunaticFamily.LunaticFamily;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementFlag;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;


public class FamilyTree {


    private final BiMap<String, Integer> familyList;

    public FamilyTree(int id) {

        FamilyPlayer playerFam = new FamilyPlayer(id);
        this.familyList = playerFam.getFamilyList();

        String uuid = playerFam.getUUID();


        String title = playerFam.getName();
        String description = "";
        AdvancementDisplay.AdvancementFrame frame = AdvancementDisplay.AdvancementFrame.CHALLENGE;
        AdvancementVisibility visibility = AdvancementVisibility.ALWAYS;

        description = LunaticFamily.getRelation("ego", playerFam.getGender());

        String skinURL = playerFam.getSkinURL();
        ItemStack icon = LunaticFamily.getSkull(skinURL);
        String background = playerFam.getBackground();


        AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);
        display.setBackgroundTexture(background);
        display.setX(0.0f);
        display.setY(-2.0f);

        Advancement ego = new Advancement(new NameKey("family_tree", "ego"), display);

        //own family
            //partner
            Advancement partner = createAdvancement(ego, "partner", 2.0f, 0.0f);
                //family of partner
                    //sibling of partner
                    Advancement secondSiblingInLawFirstHolder = createAdvancement(partner, "second_sibling_in_law_first_holder", -0.5f, 0.0f);
                    Advancement secondSiblingInLawSecondHolder = createAdvancement(secondSiblingInLawFirstHolder, "second_sibling_in_law_second_holder", 0.0f, -1.0f);
                    Advancement secondSiblingInLawThirdHolder = createAdvancement(secondSiblingInLawSecondHolder, "second_sibling_in_law_third_holder", 2.0f, 0.0f);
                    Advancement secondSiblingInLaw = createAdvancement(secondSiblingInLawThirdHolder, "second_sibling_in_law", 0.5f, 1.0f);
                    //first parent of partner
                    Advancement firstParentInLawFirstHolder = createAdvancement(partner, "first_parent_in_law_first_holder", -0.5f, 0.0f);
                    Advancement firstParentInLawSecondHolder = createAdvancement(firstParentInLawFirstHolder, "first_parent_in_law_second_holder", 0.0f, -1.0f);
                    Advancement firstParentInLawThirdHolder = createAdvancement(firstParentInLawSecondHolder, "first_parent_in_law_third_holder", 1.0f, 0.0f);
                    Advancement firstParentInLaw = createAdvancement(firstParentInLawThirdHolder, "first_parent_in_law", -0.5f, -1.0f);
                    //second parent of partner
                    Advancement secondParentInLawFirstHolder = createAdvancement(partner, "second_parent_in_law_first_holder", -0.5f, 0.0f);
                    Advancement secondParentInLawSecondHolder = createAdvancement(secondParentInLawFirstHolder, "second_parent_in_law_second_holder", 0.0f, -1.0f);
                    Advancement secondParentInLawThirdHolder = createAdvancement(secondParentInLawSecondHolder, "second_parent_in_law_third_holder", 1.0f, 0.0f);
                    Advancement secondParentInLaw = createAdvancement(secondParentInLawThirdHolder, "second_parent_in_law", 1.5f, -1.0f);
            //sibling
            Advancement siblingFirstHolder = createAdvancement(ego, "sibling_first_holder", -0.5f, 0.0f);
            Advancement siblingSecondHolder = createAdvancement(siblingFirstHolder, "sibling_second_holder", -2.0f,-1.0f);
            Advancement siblingThirdHolder = createAdvancement(siblingSecondHolder, "sibling_third_holder", 0.0f,1.0f);
            Advancement sibling = createAdvancement(siblingThirdHolder, "sibling", 0.5f,0.0f);
                //family of sibling
                    //partner of sibling
                    Advancement firstSiblingInLaw = createAdvancement(sibling, "first_sibling_in_law", -4.0f, 0.0f);
                    //first child of sibling
                    Advancement firstNieceOrNephewFirstHolder = createAdvancement(sibling, "first_niece_or_nephew_first_holder", -3.5f, 0.0f);
                    Advancement firstNieceOrNephewSecondHolder = createAdvancement(firstNieceOrNephewFirstHolder, "first_niece_or_nephew_second_holder", 0.0f, 1.0f);
                    Advancement firstNieceOrNephewThirdHolder = createAdvancement(firstNieceOrNephewSecondHolder, "first_niece_or_nephew_third_holder", -1.0f, 0.0f);
                    Advancement firstNieceOrNephew = createAdvancement(firstNieceOrNephewThirdHolder, "first_niece_or_nephew", 0.5f, 1.0f);
                    //second child of sibling
                    Advancement secondNieceOrNephewFirstHolder = createAdvancement(sibling, "second_niece_or_nephew_first_holder", -3.5f, 0.0f);
                    Advancement secondNieceOrNephewSecondHolder = createAdvancement(secondNieceOrNephewFirstHolder, "second_niece_or_nephew_second_holder", 1.0f, 1.0f);
                    Advancement secondNieceOrNephew = createAdvancement(secondNieceOrNephewSecondHolder, "second_niece_or_nephew", 0.5f, 1.0f);
            //first parent
            Advancement firstParentFirstHolder = createAdvancement(ego, "first_parent_first_holder", -0.5f,0.0f);
            Advancement firstParentSecondHolder = createAdvancement(firstParentFirstHolder, "first_parent_second_holder", -1.0f,-1.0f);
            Advancement firstParent = createAdvancement(firstParentSecondHolder, "first_parent", -0.5f,-1.0f);
                //family of first parent
                    //sibling of first parent
                    Advancement secondAuntOrUncleFirstHolder = createAdvancement(firstParent, "second_aunt_or_uncle_first_holder", -0.5f, 0.0f);
                    Advancement secondAuntOrUncleSecondHolder = createAdvancement(secondAuntOrUncleFirstHolder, "second_aunt_or_uncle_second_holder", -6.0f,-1.0f);
                    Advancement secondAuntOrUncleThirdHolder = createAdvancement(secondAuntOrUncleSecondHolder, "second_aunt_or_uncle_third_holder", 0.0f,1.0f);
                    Advancement secondAuntOrUncle = createAdvancement(secondAuntOrUncleThirdHolder, "second_aunt_or_uncle", 0.5f,0.0f);
                        //family of sibling of first parent
                            //partner of sibling of first parent
                            Advancement firstAuntOrUncle = createAdvancement(secondAuntOrUncle, "first_aunt_or_uncle", -2.0f, 0.0f);
                            //first child of sibling
                            Advancement firstCousinFirstHolder = createAdvancement(secondAuntOrUncle, "first_cousin_first_holder", -1.5f, 0.0f);
                            Advancement firstCousinSecondHolder = createAdvancement(firstCousinFirstHolder, "first_cousin_second_holder", 0.0f, 1.0f);
                            Advancement firstCousinThirdHolder = createAdvancement(firstCousinSecondHolder, "first_cousin_third_holder", -1.0f, 0.0f);
                            Advancement firstCousin = createAdvancement(firstCousinThirdHolder, "first_cousin", 0.5f, 1.0f);
                            //second child of sibling
                            Advancement secondCousinFirstHolder = createAdvancement(secondAuntOrUncle, "second_cousin_first_holder", -1.5f, 0.0f);
                            Advancement secondCousinSecondHolder = createAdvancement(secondCousinFirstHolder, "second_cousin_second_holder", 1.0f, 1.0f);
                            Advancement secondCousin = createAdvancement(secondCousinSecondHolder, "second_cousin", 0.5f, 1.0f);
                    //first parent of first parent
                    Advancement firstGrandparentFirstHolder = createAdvancement(firstParent, "first_grandparent_first_holder", -0.5f,0.0f);
                    Advancement firstGrandparentSecondHolder = createAdvancement(firstGrandparentFirstHolder, "first_grandparent_second_holder", -3.0f,-1.0f);
                    Advancement firstGrandparent = createAdvancement(firstGrandparentSecondHolder, "first_grandparent", -0.5f,-1.0f);
                        //family of first parent of first parent
                            //sibling of first parent of first parent
                            Advancement firstGreatAuntOrUncleFirstHolder = createAdvancement(firstGrandparent, "first_great_gaunt_or_uncle_first_holder", -0.5f, 0.0f);
                            Advancement firstGreatAuntOrUncleSecondHolder = createAdvancement(firstGreatAuntOrUncleFirstHolder, "first_great_gaunt_or_uncle_second_holder", -2.0f,-1.0f);
                            Advancement firstGreatAuntOrUncleThirdHolder = createAdvancement(firstGreatAuntOrUncleSecondHolder, "first_great_gaunt_or_uncle_third_holder", 0.0f,1.0f);
                            Advancement firstGreatAuntOrUncle = createAdvancement(firstGreatAuntOrUncleThirdHolder, "first_great_gaunt_or_uncle", 0.5f,0.0f);
                            //first parent of first parent of first parent
                            Advancement firstGreatGrandparentFirstHolder = createAdvancement(firstGrandparent, "first_great_grandparent_first_holder", -0.5f,0.0f);
                            Advancement firstGreatGrandparentSecondHolder = createAdvancement(firstGreatGrandparentFirstHolder, "first_great_grandparent_second_holder", -1.0f,-1.0f);
                            Advancement firstGreatGrandparent = createAdvancement(firstGreatGrandparentSecondHolder, "first_great_grandparent", -0.5f,-1.0f);
                            //second parent of first parent of first parent
                            Advancement secondGreatGrandparentFirstHolder = createAdvancement(firstGrandparent, "second_great_grandparent_first_holder", -0.5f,0.0f);
                            Advancement secondGreatGrandparentSecondHolder = createAdvancement(secondGreatGrandparentFirstHolder, "second_great_grandparent_second_holder", -1.0f,-1.0f);
                            Advancement secondGreatGrandparent = createAdvancement(secondGreatGrandparentSecondHolder, "second_great_grandparent", 1.5f,-1.0f);
                    //second parent of first parent
                    Advancement secondGrandparentFirstHolder = createAdvancement(firstParent, "second_grandparent_first_holder", -0.5f,0.0f);
                    Advancement secondGrandparentSecondHolder = createAdvancement(secondGrandparentFirstHolder, "second_grandparent_second_holder", -3.0f,-1.0f);
                    Advancement secondGrandparent = createAdvancement(secondGrandparentSecondHolder, "second_grandparent", 1.5f,-1.0f);
                        //family of second parent of first parent
                            //sibling of second parent of first parent
                            Advancement secondGreatAuntOrUncleFirstHolder = createAdvancement(secondGrandparent, "second_great_gaunt_or_uncle_first_holder", -0.5f, 0.0f);
                            Advancement secondGreatAuntOrUncleSecondHolder = createAdvancement(secondGreatAuntOrUncleFirstHolder, "second_great_gaunt_or_uncle_second_holder", 0.0f, -1.0f);
                            Advancement secondGreatAuntOrUncleThirdHolder = createAdvancement(secondGreatAuntOrUncleSecondHolder, "second_great_gaunt_or_uncle_third_holder", 2.0f, 0.0f);
                            Advancement secondGreatAuntOrUncle = createAdvancement(secondGreatAuntOrUncleThirdHolder, "second_great_gaunt_or_uncle", 0.5f, 1.0f);
                            //first parent of second parent of first parent
                            Advancement thirdGreatGrandparentFirstHolder = createAdvancement(secondGrandparent, "third_great_grandparent_first_holder", -0.5f, 0.0f);
                            Advancement thirdGreatGrandparentSecondHolder = createAdvancement(thirdGreatGrandparentFirstHolder, "third_great_grandparent_second_holder", 0.0f, -1.0f);
                            Advancement thirdGreatGrandparentThirdHolder = createAdvancement(thirdGreatGrandparentSecondHolder, "third_great_grandparent_third_holder", 1.0f, 0.0f);
                            Advancement thirdGreatGrandparent = createAdvancement(thirdGreatGrandparentThirdHolder, "third_great_grandparent", -0.5f, -1.0f);
                            //second parent of second parent of first parent
                            Advancement fourthGreatGrandparentFirstHolder = createAdvancement(secondGrandparent, "fourth_great_grandparent_first_holder", -0.5f, 0.0f);
                            Advancement fourthGreatGrandparentSecondHolder = createAdvancement(fourthGreatGrandparentFirstHolder, "fourth_great_grandparent_second_holder", 0.0f, -1.0f);
                            Advancement fourthGreatGrandparentThirdHolder = createAdvancement(fourthGreatGrandparentSecondHolder, "fourth_great_grandparent_third_holder", 1.0f, 0.0f);
                            Advancement fourthGreatGrandparent = createAdvancement(fourthGreatGrandparentThirdHolder, "fourth_great_grandparent", 1.5f, -1.0f);

            //second parent
            Advancement secondParentFirstHolder = createAdvancement(ego, "second_parent_first_holder", -0.5f,0.0f);
            Advancement secondParentSecondHolder = createAdvancement(secondParentFirstHolder, "second_parent_second_holder", -1.0f,-1.0f);
            Advancement secondParent = createAdvancement(secondParentSecondHolder, "second_parent", 1.5f,-1.0f);
                //family of second parent
                    //sibling of second parent
                    Advancement thirdAuntOrUncleFirstHolder = createAdvancement(secondParent, "third_aunt_or_uncle_first_holder", -0.5f, 0.0f);
                    Advancement thirdAuntOrUncleSecondHolder = createAdvancement(thirdAuntOrUncleFirstHolder, "third_aunt_or_uncle_second_holder", 0.0f, -1.0f);
                    Advancement thirdAuntOrUncleThirdHolder = createAdvancement(thirdAuntOrUncleSecondHolder, "third_aunt_or_uncle_third_holder", 6.0f, 0.0f);
                    Advancement thirdAuntOrUncle = createAdvancement(thirdAuntOrUncleThirdHolder, "third_aunt_or_uncle", 0.5f, 1.0f);
                        //family of sibling of second Parent
                            //partner of sibling of second Parent
                            Advancement fourthAuntOrUncle = createAdvancement(thirdAuntOrUncle, "fourth_aunt_or_uncle", 2.0f, 0.0f);
                            //first child of sibling of second Parent
                            Advancement thirdCousinFirstHolder = createAdvancement(thirdAuntOrUncle, "third_cousin_first_holder", 0.5f,0.0f);
                            Advancement thirdCousinSecondHolder = createAdvancement(thirdCousinFirstHolder, "third_cousin_second_holder", 0.0f,1.0f);
                            Advancement thirdCousinThirdHolder = createAdvancement(thirdCousinSecondHolder, "third_cousin_third_holder", -1.0f,0.0f);
                            Advancement thirdCousin = createAdvancement(thirdCousinThirdHolder, "third_cousin", 0.5f,1.0f);
                            //second child of sibling of second Parent
                            Advancement fourthCousinFirstHolder = createAdvancement(thirdAuntOrUncle, "fourth_cousin_first_holder", 0.5f,0.0f);
                            Advancement fourthCousinSecondHolder = createAdvancement(fourthCousinFirstHolder, "fourth_cousin_second_holder", 1.0f,1.0f);
                            Advancement fourthCousin = createAdvancement(fourthCousinSecondHolder, "fourth_cousin", 0.5f,1.0f);
                    //first parent of second parent
                    Advancement thirdGrandparentFirstHolder = createAdvancement(secondParent, "third_grandparent_first_holder", -0.5f, 0.0f);
                    Advancement thirdGrandparentSecondHolder = createAdvancement(thirdGrandparentFirstHolder, "third_grandparent_second_holder", 0.0f, -1.0f);
                    Advancement thirdGrandparentThirdHolder = createAdvancement(thirdGrandparentSecondHolder, "third_grandparent_third_holder", 3.0f, 0.0f);
                    Advancement thirdGrandparent = createAdvancement(thirdGrandparentThirdHolder, "third_grandparent", -0.5f, -1.0f);
                        //family of first parent of second parent
                            //sibling of first parent of second parent
                            Advancement thirdGreatAuntOrUncleFirstHolder = createAdvancement(thirdGrandparent, "third_great_gaunt_or_uncle_first_holder", -0.5f, 0.0f);
                            Advancement thirdGreatAuntOrUncleSecondHolder = createAdvancement(thirdGreatAuntOrUncleFirstHolder, "third_great_gaunt_or_uncle_second_holder", -2.0f,-1.0f);
                            Advancement thirdGreatAuntOrUncleThirdHolder = createAdvancement(thirdGreatAuntOrUncleSecondHolder, "third_great_gaunt_or_uncle_third_holder", 0.0f,1.0f);
                            Advancement thirdGreatAuntOrUncle = createAdvancement(thirdGreatAuntOrUncleThirdHolder, "third_great_gaunt_or_uncle", 0.5f,0.0f);
                            //first parent of first parent of second parent
                            Advancement fifthGreatGrandparentFirstHolder = createAdvancement(thirdGrandparent, "fifth_great_grandparent_first_holder", -0.5f,0.0f);
                            Advancement fifthGreatGrandparentSecondHolder = createAdvancement(fifthGreatGrandparentFirstHolder, "fifth_great_grandparent_second_holder", -1.0f,-1.0f);
                            Advancement fifthGreatGrandparent = createAdvancement(fifthGreatGrandparentSecondHolder, "fifth_great_grandparent", -0.5f,-1.0f);
                            //second parent of first parent of second parent
                            Advancement sixthGreatGrandparentFirstHolder = createAdvancement(thirdGrandparent, "sixth_great_grandparent_first_holder", -0.5f,0.0f);
                            Advancement sixthGreatGrandparentSecondHolder = createAdvancement(sixthGreatGrandparentFirstHolder, "sixth_great_grandparent_second_holder", -1.0f,-1.0f);
                            Advancement sixthGreatGrandparent = createAdvancement(sixthGreatGrandparentSecondHolder, "sixth_great_grandparent", 1.5f,-1.0f);
                    //second parent of second parent
                    Advancement fourthGrandparentFirstHolder = createAdvancement(secondParent, "fourth_grandparent_first_holder", -0.5f, 0.0f);
                    Advancement fourthGrandparentSecondHolder = createAdvancement(fourthGrandparentFirstHolder, "fourth_grandparent_second_holder", 0.0f, -1.0f);
                    Advancement fourthGrandparentThirdHolder = createAdvancement(fourthGrandparentSecondHolder, "fourth_grandparent_third_holder", 3.0f, 0.0f);
                    Advancement fourthGrandparent = createAdvancement(fourthGrandparentThirdHolder, "fourth_grandparent", 1.5f, -1.0f);
                        //family of second parent of first parent
                            //sibling of second parent of first parent
                            Advancement fourthGreatAuntOrUncleFirstHolder = createAdvancement(fourthGrandparent, "fourth_great_gaunt_or_uncle_first_holder", -0.5f, 0.0f);
                            Advancement fourthGreatAuntOrUncleSecondHolder = createAdvancement(fourthGreatAuntOrUncleFirstHolder, "fourth_great_gaunt_or_uncle_second_holder", 0.0f, -1.0f);
                            Advancement fourthGreatAuntOrUncleThirdHolder = createAdvancement(fourthGreatAuntOrUncleSecondHolder, "fourth_great_gaunt_or_uncle_third_holder", 2.0f, 0.0f);
                            Advancement fourthGreatAuntOrUncle = createAdvancement(fourthGreatAuntOrUncleThirdHolder, "fourth_great_gaunt_or_uncle", 0.5f, 1.0f);
                            //first parent of second parent of first parent
                            Advancement seventhGreatGrandparentFirstHolder = createAdvancement(fourthGrandparent, "seventh_great_grandparent_first_holder", -0.5f, 0.0f);
                            Advancement seventhGreatGrandparentSecondHolder = createAdvancement(seventhGreatGrandparentFirstHolder, "seventh_great_grandparent_second_holder", 0.0f, -1.0f);
                            Advancement seventhGreatGrandparentThirdHolder = createAdvancement(seventhGreatGrandparentSecondHolder, "seventh_great_grandparent_third_holder", 1.0f, 0.0f);
                            Advancement seventhGreatGrandparent = createAdvancement(seventhGreatGrandparentThirdHolder, "seventh_great_grandparent", -0.5f, -1.0f);
                            //second parent of second parent of first parent
                            Advancement eighthGreatGrandparentFirstHolder = createAdvancement(fourthGrandparent, "eighth_great_grandparent_first_holder", -0.5f, 0.0f);
                            Advancement eighthGreatGrandparentSecondHolder = createAdvancement(eighthGreatGrandparentFirstHolder, "eighth_great_grandparent_second_holder", 0.0f, -1.0f);
                            Advancement eighthGreatGrandparentThirdHolder = createAdvancement(eighthGreatGrandparentSecondHolder, "eighth_great_grandparent_third_holder", 1.0f, 0.0f);
                            Advancement eighthGreatGrandparent = createAdvancement(eighthGreatGrandparentThirdHolder, "eighth_great_grandparent", 1.5f, -1.0f);
            //first child
            Advancement firstChildFirstHolder = createAdvancement(ego, "first_child_first_holder", 0.5f,0.0f);
            Advancement firstChildSecondHolder = createAdvancement(firstChildFirstHolder, "first_child_second_holder", 0.0f,1.0f);
            Advancement firstChildThirdHolder = createAdvancement(firstChildSecondHolder, "first_child_third_holder", -1.0f,0.0f);
            Advancement firstChild = createAdvancement(firstChildThirdHolder, "first_child", 0.5f,1.0f);
                //family of first child
                    //partner of first child
                    Advancement firstChildInLaw = createAdvancement(firstChild, "first_child_in_law", -2.0f, 0.0f);
                    //first child of first child
                    Advancement firstGrandchildFirstHolder = createAdvancement(firstChild, "first_grandchild_first_holder", -1.5f,0.0f);
                    Advancement firstGrandchildSecondHolder = createAdvancement(firstGrandchildFirstHolder, "first_grandchild_second_holder", 0.0f,1.0f);
                    Advancement firstGrandchildThirdHolder = createAdvancement(firstGrandchildSecondHolder, "first_grandchild_third_holder", -3.0f,0.0f);
                    Advancement firstGrandchild = createAdvancement(firstGrandchildThirdHolder, "first_grandchild", 0.5f,1.0f);
                        //family of first child of first child
                            //partner of first child of first child
                            Advancement firstGrandchildInLaw = createAdvancement(firstGrandchild, "first_grandchild_in_law", -2.0f, 0.0f);
                            //first child of first child of first child
                            Advancement firstGreatGrandchildFirstHolder = createAdvancement(firstGrandchild, "first_great_grandchild_first_holder", -1.5f, 0.0f);
                            Advancement firstGreatGrandchildSecondHolder = createAdvancement(firstGreatGrandchildFirstHolder, "first_great_grandchild_second_holder", 0.0f, 1.0f);
                            Advancement firstGreatGrandchildThirdHolder = createAdvancement(firstGreatGrandchildSecondHolder, "first_great_grandchild_third_holder", -1.0f, 0.0f);
                            Advancement firstGreatGrandchild = createAdvancement(firstGreatGrandchildThirdHolder, "first_great_grandchild", 0.5f, 1.0f);
                            //second child of first child of first child
                            Advancement secondGreatGrandchildFirstHolder = createAdvancement(firstGrandchild, "second_great_grandchild_first_holder", -1.5f, 0.0f);
                            Advancement secondGreatGrandchildSecondHolder = createAdvancement(secondGreatGrandchildFirstHolder, "second_great_grandchild_second_holder", 1.0f, 1.0f);
                            Advancement secondGreatGrandchild = createAdvancement(secondGreatGrandchildSecondHolder, "second_great_grandchild", 0.5f, 1.0f);
                    //second child of first child
                    Advancement secondGrandchildFirstHolder = createAdvancement(firstChild, "second_grandchild_first_holder", -1.5f,0.0f);
                    Advancement secondGrandchildSecondHolder = createAdvancement(secondGrandchildFirstHolder, "second_grandchild_second_holder", -2.0f,1.0f);
                    Advancement secondGrandchildThirdHolder = createAdvancement(secondGrandchildSecondHolder, "second_grandchild_third_holder", 1.0f,0.0f);
                    Advancement secondGrandchild = createAdvancement(secondGrandchildThirdHolder, "second_grandchild", 0.5f,1.0f);
                        //family of child of first child
                            //partner of child of first child
                            Advancement secondGrandchildInLaw = createAdvancement(secondGrandchild, "second_grandchild_in_law", 2.0f, 0.0f);
                            //first child of child of first child
                            Advancement thirdGreatGrandchildFirstHolder = createAdvancement(secondGrandchild, "third_great_grandchild_first_holder", 0.5f,0.0f);
                            Advancement thirdGreatGrandchildSecondHolder = createAdvancement(thirdGreatGrandchildFirstHolder, "third_great_grandchild_second_holder", 0.0f,1.0f);
                            Advancement thirdGreatGrandchildThirdHolder = createAdvancement(thirdGreatGrandchildSecondHolder, "third_great_grandchild_third_holder", -1.0f,0.0f);
                            Advancement thirdGreatGrandchild = createAdvancement(thirdGreatGrandchildThirdHolder, "third_great_grandchild", 0.5f,1.0f);
                            //second child of child of first child
                            Advancement fourthGreatGrandchildFirstHolder = createAdvancement(secondGrandchild, "fourth_great_grandchild_first_holder", 0.5f,0.0f);
                            Advancement fourthGreatGrandchildSecondHolder = createAdvancement(fourthGreatGrandchildFirstHolder, "fourth_great_grandchild_second_holder", 1.0f,1.0f);
                            Advancement fourthGreatGrandchild = createAdvancement(fourthGreatGrandchildSecondHolder, "fourth_great_grandchild", 0.5f,1.0f);
            //second child
            Advancement secondChildFirstHolder = createAdvancement(ego, "second_child_first_holder", 0.5f,0.0f);
            Advancement secondChildSecondHolder = createAdvancement(secondChildFirstHolder, "second_child_second_holder", 1.0f,1.0f);
            Advancement secondChild = createAdvancement(secondChildSecondHolder, "second_child", 0.5f,1.0f);
                //family of second child
                    //partner of second child
                    Advancement secondChildInLaw = createAdvancement(secondChild, "second_child_in_law", 2.0f, 0.0f);
                    //first child of second child
                    Advancement thirdGrandchildFirstHolder = createAdvancement(secondChild, "third_grandchild_first_holder", 0.5f,0.0f);
                    Advancement thirdGrandchildSecondHolder = createAdvancement(thirdGrandchildFirstHolder, "third_grandchild_second_holder", 2.0f,1.0f);
                    Advancement thirdGrandchildThirdHolder = createAdvancement(thirdGrandchildSecondHolder, "third_grandchild_third_holder", -1.0f,0.0f);
                    Advancement thirdGrandchild = createAdvancement(thirdGrandchildThirdHolder, "third_grandchild", 0.5f,1.0f);
                        //family of first Child of second Child
                            //partner of first Child of second Child
                            Advancement thirdGrandchildInLaw = createAdvancement(thirdGrandchild, "third_grandchild_in_law", -2.0f, 0.0f);
                            //first child of first Child of second Child
                            Advancement fifthGreatGrandchildFirstHolder = createAdvancement(thirdGrandchild, "fifth_great_grandchild_first_holder", -1.5f, 0.0f);
                            Advancement fifthGreatGrandchildSecondHolder = createAdvancement(fifthGreatGrandchildFirstHolder, "fifth_great_grandchild_second_holder", 0.0f, 1.0f);
                            Advancement fifthGreatGrandchildThirdHolder = createAdvancement(fifthGreatGrandchildSecondHolder, "fifth_great_grandchild_third_holder", -1.0f, 0.0f);
                            Advancement fifthGreatGrandchild = createAdvancement(fifthGreatGrandchildThirdHolder, "fifth_great_grandchild", 0.5f, 1.0f);
                            //second child of first Child of second Child
                            Advancement sixthGreatGrandchildFirstHolder = createAdvancement(thirdGrandchild, "sixth_great_grandchild_first_holder", -1.5f, 0.0f);
                            Advancement sixthGreatGrandchildSecondHolder = createAdvancement(sixthGreatGrandchildFirstHolder, "sixth_great_grandchild_second_holder", 1.0f, 1.0f);
                            Advancement sixthGreatGrandchild = createAdvancement(sixthGreatGrandchildSecondHolder, "sixth_great_grandchild", 0.5f, 1.0f);
                    //second child of second child
                    Advancement fourthGrandchildFirstHolder = createAdvancement(secondChild, "fourth_grandchild_first_holder", 0.5f,0.0f);
                    Advancement fourthGrandchildSecondHolder = createAdvancement(fourthGrandchildFirstHolder, "fourth_grandchild_second_holder", 0.0f,1.0f);
                    Advancement fourthGrandchildThirdHolder = createAdvancement(fourthGrandchildSecondHolder, "fourth_grandchild_third_holder", 3.0f,0.0f);
                    Advancement fourthGrandchild = createAdvancement(fourthGrandchildThirdHolder, "fourth_grandchild", 0.5f,1.0f);
                        //family of second child of second child
                            //partner of second child of second child
                            Advancement fourthGrandchildInLaw = createAdvancement(fourthGrandchild, "fourth_grandchild_in_law", 2.0f, 0.0f);
                            //first child of second child of second child
                            Advancement seventhGreatGrandchildFirstHolder = createAdvancement(fourthGrandchild, "seventh_great_grandchild_first_holder", 0.5f,0.0f);
                            Advancement seventhGreatGrandchildSecondHolder = createAdvancement(seventhGreatGrandchildFirstHolder, "seventh_great_grandchild_second_holder", 0.0f,1.0f);
                            Advancement seventhGreatGrandchildThirdHolder = createAdvancement(seventhGreatGrandchildSecondHolder, "seventh_great_grandchild_third_holder", -1.0f,0.0f);
                            Advancement seventhGreatGrandchild = createAdvancement(seventhGreatGrandchildThirdHolder, "seventh_great_grandchild", 0.5f,1.0f);
                            //second child of second child of second child
                            Advancement eighthGreatGrandchildFirstHolder = createAdvancement(fourthGrandchild, "eighth_great_grandchild_first_holder", 0.5f,0.0f);
                            Advancement eighthGreatGrandchildSecondHolder = createAdvancement(eighthGreatGrandchildFirstHolder, "eighth_great_grandchild_second_holder", 1.0f,1.0f);
                            Advancement eighthGreatGrandchild = createAdvancement(eighthGreatGrandchildSecondHolder, "eighth_great_grandchild", 0.5f,1.0f);


        AdvancementManager manager = new AdvancementManager(new NameKey("manager", uuid));

        if (Bukkit.getPlayer(UUID.fromString(uuid)) != null) {
            Player player = Bukkit.getPlayer(UUID.fromString(uuid));
            manager.addPlayer(player);
        }

        manager.addAdvancement(ego);

        //partner
        manager.addAdvancement(partner);
            //family of partner
                //sibling of partner
                manager.addAdvancement(secondSiblingInLawFirstHolder);
                manager.addAdvancement(secondSiblingInLawSecondHolder);
                manager.addAdvancement(secondSiblingInLawThirdHolder);
                manager.addAdvancement(secondSiblingInLaw);
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

        if (!(familyList.containsKey(relation.replace("_first_holder", "")
                .replace("_second_holder", "")
                .replace("_third_holder", "")
                .replace("_holder", "")))) {
            visibility = AdvancementVisibility.HIDDEN;
        }


        ItemStack icon = new ItemStack(Material.STONE);

        String title = relation;
        String description = relation;
        Advancement advancement;

        if (familyList.containsKey(relation)) {
            int id = (int) familyList.get(relation);
            FamilyPlayer relationFam = new FamilyPlayer(id);
            title = relationFam.getName();

                String relationKey = relation.replace("first_", "")
                        .replace("second_", "")
                        .replace("third_", "")
                        .replace("fourth_", "")
                        .replace("fifth_", "")
                        .replace("sixth_", "")
                        .replace("seventh__", "")
                        .replace("eighth_", "");


                description = LunaticFamily.getRelation(relationKey, relationFam.getGender());

                String skinURL = relationFam.getSkinURL();
                icon = LunaticFamily.getSkull(skinURL);


            AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);
            display.setPositionOrigin(parent);
            display.setX(x);
            display.setY(y);

            advancement = new Advancement(parent, new NameKey("family_tree", relation), display);
        } else {
            AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);
            display.setPositionOrigin(parent);
            display.setX(x);
            display.setY(y);

            advancement = new Advancement(parent, new NameKey("family_tree", relation), display
                    , AdvancementFlag.SEND_WITH_HIDDEN_BOOLEAN
            );
        }

        return advancement;
    }

}
