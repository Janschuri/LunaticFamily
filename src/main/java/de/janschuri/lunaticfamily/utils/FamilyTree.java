package de.janschuri.lunaticfamily.utils;

import com.google.common.collect.BiMap;
import de.janschuri.lunaticfamily.Main;
import eu.endercentral.crazy_advancements.CrazyAdvancementsAPI;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementFlag;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.swing.*;

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


        ItemStack iconEgo = Main.getSkull(playerFam.getSkinURL());

        String title = playerFam.getName() + "'s family";
        String description = "Your family tree";

        AdvancementDisplay.AdvancementFrame frame = AdvancementDisplay.AdvancementFrame.TASK;
        AdvancementVisibility visibility = AdvancementVisibility.ALWAYS;

        AdvancementDisplay familyTree = new AdvancementDisplay(iconEgo, title, description, frame, visibility);
        familyTree.setBackgroundTexture("textures/block/moss_block.png");
        familyTree.setX(-1.5f);
        familyTree.setY(-10.0f);
        Advancement rootAdvancement = new Advancement(new NameKey("familyTree", "familyTree"), familyTree);


        Advancement firstGreatGrandparentHolder = createAdvancement(rootAdvancement, "firstGreatGrandparentHolder", -8.5f, -9.0f);
        Advancement secondGreatGrandparentHolder = createAdvancement(rootAdvancement, "secondGreatGrandparentHolder", -6.5f, -9.0f);
        Advancement thirdGreatGrandparentHolder = createAdvancement(rootAdvancement, "thirdGreatGrandparentHolder", -4.5f, -9.0f);
        Advancement fourthGreatGrandparentHolder = createAdvancement(rootAdvancement, "fourthGreatGrandparentHolder", -2.5f, -9.0f);
        Advancement fifthGreatGrandparentHolder = createAdvancement(rootAdvancement, "fifthGreatGrandparentHolder", -0.5f, -9.0f);
        Advancement sixthGreatGrandparentHolder = createAdvancement(rootAdvancement, "sixthGreatGrandparentHolder", 1.5f, -9.0f);
        Advancement seventhGreatGrandparentHolder = createAdvancement(rootAdvancement, "seventhGreatGrandparentHolder", 3.5f, -9.0f);
        Advancement eighthGreatGrandparentHolder = createAdvancement(rootAdvancement, "eighthGreatGrandparentHolder", 5.5f, -9.0f);

        Advancement firstGreatGrandparent = createAdvancement(firstGreatGrandparentHolder, "firstGreatGrandparent", -8.0f, -8.0f);
        Advancement secondGreatGrandparent = createAdvancement(secondGreatGrandparentHolder, "secondGreatGrandparent", -6.0f, -8.0f);
        Advancement thirdGreatGrandparent = createAdvancement(thirdGreatGrandparentHolder, "thirdGreatGrandparent", -4.0f, -8.0f);
        Advancement fourthGreatGrandparent = createAdvancement(fourthGreatGrandparentHolder, "fourthGreatGrandparent", -2.0f, -8.0f);
        Advancement fifthGreatGrandparent = createAdvancement(fifthGreatGrandparentHolder, "fifthGreatGrandparent", 0.0f, -8.0f);
        Advancement sixthGreatGrandparent = createAdvancement(sixthGreatGrandparentHolder, "sixthGreatGrandparent", 2.0f, -8.0f);
        Advancement seventhGreatGrandparent = createAdvancement(seventhGreatGrandparentHolder, "seventhGreatGrandparent", 4.0f, -8.0f);
        Advancement eighthGreatGrandparent = createAdvancement(eighthGreatGrandparentHolder, "eightGreatGrandparent", 6.0f, -8.0f);


        Advancement secondGreatGrandparentFake = createAdvancement(firstGreatGrandparentHolder, "secondGreatGrandparentFake", -6.0f, -8.0f);
        Advancement fourthGreatGrandparentFake = createAdvancement(thirdGreatGrandparentHolder, "fourthGreatGrandparentFake", -2.0f, -8.0f);
        Advancement sixthGreatGrandparentFake = createAdvancement(fifthGreatGrandparentHolder, "sixthGreatGrandparentFake", 2.0f, -8.0f);
        Advancement eighthGreatGrandparentFake = createAdvancement(seventhGreatGrandparentHolder, "eightGreatGrandparentFake", 6.0f, -8.0f);

        Advancement firstGreatAuntOrUncleHolder = createAdvancement(firstGreatGrandparent, "firstGreatAuntOrUncleHolder", -7.75f, -8.0f);
        Advancement firstGrandparentHolder = createAdvancement(firstGreatGrandparent, "firstGrandparentHolder", -7.25f, -8.0f);
        Advancement secondGrandparentHolder = createAdvancement(thirdGreatGrandparent, "secondGrandparentHolder", -3.75f, -8.0f);
        Advancement secondGreatAuntOrUncleHolder = createAdvancement(thirdGreatGrandparent, "secondGreatAuntOrUncleHolder", -3.25f, -8.0f);
        Advancement thirdGreatAuntOrUncleHolder = createAdvancement(fifthGreatGrandparent, "thirdGreatAuntOrUncleHolder", 0.25f, -8.0f);
        Advancement thirdGrandparentHolder = createAdvancement(fifthGreatGrandparent, "thirdGrandparentHolder", 0.75f, -8.0f);
        Advancement fourthGrandparentHolder = createAdvancement(seventhGreatGrandparent, "fourthGrandparentHolder", 4.25f, -8.0f);
        Advancement fourthGreatAuntOrUncleHolder = createAdvancement(seventhGreatGrandparent, "fourthGreatAuntOrUncleHolder", 4.75f, -8.0f);

        Advancement firstGreatAuntOrUncle = createAdvancement(firstGreatAuntOrUncleHolder, "firstGreatAuntOrUncle", -8.0f, -6.0f);
        Advancement firstGrandparent = createAdvancement(firstGrandparentHolder, "firstGrandparent", -6.0f, -6.0f);
        Advancement secondGrandparent = createAdvancement(secondGrandparentHolder, "secondGrandparent", -4.0f, -6.0f);
        Advancement secondGreatAuntOrUncle = createAdvancement(secondGreatAuntOrUncleHolder, "secondGreatAuntOrUncle", -2.0f, -6.0f);
        Advancement thirdGreatAuntOrUncle = createAdvancement(thirdGreatAuntOrUncleHolder, "thirdGreatAuntOrUncle", 0.0f, -6.0f);
        Advancement thirdGrandparent = createAdvancement(thirdGrandparentHolder, "thirdGrandparent", 2.0f, -6.0f);
        Advancement fourthGrandparent = createAdvancement(fourthGrandparentHolder, "fourthGrandparent", 4.0f, -6.0f);
        Advancement fourthGreatAuntOrUncle = createAdvancement(fourthGreatAuntOrUncleHolder, "fourthGreatAuntOrUncle", 6.0f, -6.0f);

        Advancement secondGrandparentFake = createAdvancement(firstGrandparent, "secondGrandparentFake", -4.0f, -6.0f);
        Advancement fourthGrandparentFake = createAdvancement(thirdGrandparent, "fourthGrandparentFake", 4.0f, -6.0f);

        Advancement secondAuntOrUncleHolder = createAdvancement(firstGrandparent, "secondAuntOrUncleHolder", -5.75f, -6.0f);
        Advancement firstParentHolder = createAdvancement(firstGrandparent, "firstParentHolder", -5.25f, -6.0f);

        Advancement secondParentFirstHolder = createAdvancement(thirdGrandparent, "secondParentFirstHolder", 2.25f, -6.0f);
        Advancement thirdAuntOrUncleFirstHolder = createAdvancement(thirdGrandparent, "thirdAuntOrUncleFirstHolder", 2.75f, -6.0f);
        Advancement secondParentSecondHolder = createAdvancement(secondParentFirstHolder, "secondParentSecondHolder", 0.5f, -5.0f);
        Advancement thirdAuntOrUncleSecondHolder = createAdvancement(thirdAuntOrUncleFirstHolder, "thirdAuntOrUncleSecondHolder", 4.5f, -5.0f);

        Advancement secondAuntOrUncle = createAdvancement(secondAuntOrUncleHolder, "secondAuntOrUncle", -8.0f, -4.0f);
        Advancement firstParent = createAdvancement(firstParentHolder, "firstParent", -2.0f, -4.0f);
        Advancement secondParent = createAdvancement(secondParentSecondHolder, "secondParent", 0.0f, -4.0f);
        Advancement thirdAuntOrUncle = createAdvancement(thirdAuntOrUncleSecondHolder, "thirdAuntOrUncle", 6.0f, -4.0f);

        Advancement secondParentFake = createAdvancement(firstParent, "secondParentFake", 0.0f, -4.0f);

        Advancement firstAuntOrUncle = createAdvancement(secondAuntOrUncle, "firstAuntOrUncle", -10.0f, -4.0f);
        Advancement fourthAuntOrUncle = createAdvancement(thirdAuntOrUncle, "fourthAuntOrUncle", 12.0f, -4.0f);

        Advancement firstCousinHolder = createAdvancement(firstAuntOrUncle, "firstCousinHolder", -9.75f, -4.0f);
        Advancement secondCousinHolder = createAdvancement(firstAuntOrUncle, "secondCousinHolder", -9.25f, -4.0f);
        Advancement siblingHolder = createAdvancement(firstParent, "siblingHolder", -1.75f, -4.0f);
        Advancement egoHolder = createAdvancement(firstParent, "egoHolder", -1.25f, -4.0f);
        Advancement thirdCousinHolder = createAdvancement(thirdAuntOrUncle, "thirdCousinHolder", 10.25f, -4.0f);
        Advancement fourthCousinHolder = createAdvancement(thirdAuntOrUncle, "fourthCousinHolder", 10.75f, -4.0f);

        Advancement firstCousin = createAdvancement(firstCousinHolder, "firstCousin", -10.0f, -2.0f);
        Advancement secondCousin = createAdvancement(secondCousinHolder, "secondCousin", -8.0f, -2.0f);
        Advancement sibling = createAdvancement(siblingHolder, "sibling", -2.0f, -2.0f);
        Advancement ego = createAdvancement(egoHolder, "ego", 0.0f, -2.0f);
        Advancement thirdCousin = createAdvancement(thirdCousinHolder, "thirdCousin", 10.0f, -2.0f);
        Advancement fourthCousin = createAdvancement(fourthCousinHolder, "fourthCousin", 12.0f, -2.0f);

        Advancement partner = createAdvancement(ego, "partner", 2.0f, -2.0f);

        Advancement firstParentInLawHolder = createAdvancement(partner, "firstParentInLawHolder", 2.25f, -2.0f);
        Advancement firstParentInLaw = createAdvancement(firstParentInLawHolder, "firstParentInLaw", 2.0f, -4.0f);
        Advancement secondParentInLaw = createAdvancement(firstParentInLaw, "secondParentInLaw", 4.0f, -4.0f);

        Advancement secondSiblingInLawFirstHolder = createAdvancement(partner, "secondSiblingInLawFirstHolder", 2.25f, -2.0f);
        Advancement secondSiblingInLawSecondHolder = createAdvancement(secondSiblingInLawFirstHolder, "secondSiblingInLawSecondHolder", 2.75f, -4.0f);
        Advancement secondSiblingInLaw = createAdvancement(secondSiblingInLawSecondHolder, "secondSiblingInLaw", 4.0f, -2.0f);
        Advancement firstSiblingInLaw = createAdvancement(sibling, "firstSiblingInLaw", -6.0f, -2.0f);
        Advancement thirdSiblingInLaw = createAdvancement(secondSiblingInLaw, "thirdSiblingInLaw", 6.0f, -2.0f);

        Advancement firstChildHolder = createAdvancement(ego, "firstChildHolder", 0.25f, -2.0f);
        Advancement secondChildHolder = createAdvancement(ego, "secondChildHolder", 0.75f, -2.0f);
        Advancement firstChild = createAdvancement(firstChildHolder, "firstChild", 0.0f, 0.0f);
        Advancement secondChild = createAdvancement(secondChildHolder, "secondChild", 2.0f, 0.0f);


        AdvancementManager manager = new AdvancementManager(new NameKey("manager", "manager_name"));
        manager.addPlayer(player);

        manager.addAdvancement(rootAdvancement);

        manager.addAdvancement(firstGreatGrandparentHolder);
        manager.addAdvancement(secondGreatGrandparentHolder);
        manager.addAdvancement(thirdGreatGrandparentHolder);
        manager.addAdvancement(fourthGreatGrandparentHolder);
        manager.addAdvancement(fifthGreatGrandparentHolder);
        manager.addAdvancement(sixthGreatGrandparentHolder);
        manager.addAdvancement(seventhGreatGrandparentHolder);
        manager.addAdvancement(eighthGreatGrandparentHolder);

        manager.addAdvancement(firstGreatGrandparent);
        manager.addAdvancement(secondGreatGrandparent);
        manager.addAdvancement(thirdGreatGrandparent);
        manager.addAdvancement(fourthGreatGrandparent);
        manager.addAdvancement(fifthGreatGrandparent);
        manager.addAdvancement(sixthGreatGrandparent);
        manager.addAdvancement(seventhGreatGrandparent);
        manager.addAdvancement(eighthGreatGrandparent);

        manager.addAdvancement(secondGreatGrandparentFake);
        manager.addAdvancement(fourthGreatGrandparentFake);
        manager.addAdvancement(sixthGreatGrandparentFake);
        manager.addAdvancement(eighthGreatGrandparentFake);

        manager.addAdvancement(firstGreatAuntOrUncleHolder);
        manager.addAdvancement(firstGrandparentHolder);
        manager.addAdvancement(secondGrandparentHolder);
        manager.addAdvancement(secondGreatAuntOrUncleHolder);
        manager.addAdvancement(thirdGreatAuntOrUncleHolder);
        manager.addAdvancement(thirdGrandparentHolder);
        manager.addAdvancement(fourthGrandparentHolder);
        manager.addAdvancement(fourthGreatAuntOrUncleHolder);

        manager.addAdvancement(firstGreatAuntOrUncle);
        manager.addAdvancement(firstGrandparent);
        manager.addAdvancement(secondGrandparent);
        manager.addAdvancement(secondGreatAuntOrUncle);
        manager.addAdvancement(thirdGreatAuntOrUncle);
        manager.addAdvancement(thirdGrandparent);
        manager.addAdvancement(fourthGrandparent);
        manager.addAdvancement(fourthGreatAuntOrUncle);

        manager.addAdvancement(secondGrandparentFake);
        manager.addAdvancement(fourthGrandparentFake);

        manager.addAdvancement(secondAuntOrUncleHolder);
        manager.addAdvancement(firstParentHolder);

        manager.addAdvancement(secondParentFirstHolder);
        manager.addAdvancement(thirdAuntOrUncleFirstHolder);
        manager.addAdvancement(secondParentSecondHolder);
        manager.addAdvancement(thirdAuntOrUncleSecondHolder);

        manager.addAdvancement(secondAuntOrUncle);
        manager.addAdvancement(firstParent);
        manager.addAdvancement(secondParent);
        manager.addAdvancement(thirdAuntOrUncle);

        manager.addAdvancement(secondParentFake);

        manager.addAdvancement(firstAuntOrUncle);
        manager.addAdvancement(fourthAuntOrUncle);

        manager.addAdvancement(firstCousinHolder);
        manager.addAdvancement(secondCousinHolder);
        manager.addAdvancement(siblingHolder);
        manager.addAdvancement(egoHolder);
        manager.addAdvancement(thirdCousinHolder);
        manager.addAdvancement(fourthCousinHolder);

        manager.addAdvancement(firstCousin);
        manager.addAdvancement(secondCousin);
        manager.addAdvancement(sibling);
        manager.addAdvancement(ego);
        manager.addAdvancement(thirdCousin);
        manager.addAdvancement(fourthCousin);

        manager.addAdvancement(partner);
        manager.addAdvancement(firstParentInLawHolder);
        manager.addAdvancement(firstParentInLaw);
        manager.addAdvancement(secondParentInLaw);

        manager.addAdvancement(firstSiblingInLaw);
        manager.addAdvancement(secondSiblingInLawFirstHolder);
        manager.addAdvancement(secondSiblingInLawSecondHolder);
        manager.addAdvancement(secondSiblingInLaw);
        manager.addAdvancement(thirdSiblingInLaw);

        manager.addAdvancement(firstChildHolder);
        manager.addAdvancement(secondChildHolder);
        manager.addAdvancement(firstChild);
        manager.addAdvancement(secondChild);





    }

    private Advancement createAdvancement(Advancement parent, String relation, Float x, Float y) {
        AdvancementDisplay.AdvancementFrame frame = AdvancementDisplay.AdvancementFrame.GOAL;
        AdvancementVisibility visibility = AdvancementVisibility.ALWAYS;
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
            display.setX(x);
            display.setY(y);

            advancement = new Advancement(parent, new NameKey("familyTree", relation), display);
        }

        else {
            AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);
            display.setX(x);
            display.setY(y);

            advancement = new Advancement(parent, new NameKey("familyTree", relation), display
                    //, AdvancementFlag.SEND_WITH_HIDDEN_BOOLEAN
            );
        }





        return advancement;
    }

}
