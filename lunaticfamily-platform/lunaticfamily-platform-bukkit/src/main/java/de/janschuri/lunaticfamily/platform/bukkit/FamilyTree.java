package de.janschuri.lunaticfamily.platform.bukkit;

import de.janschuri.lunaticfamily.common.utils.Logger;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementFlag;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import eu.endercentral.crazy_advancements.packet.AdvancementsPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;

import java.util.*;

public class FamilyTree {


    private final Map<String, Advancement> advancementMap= new HashMap<>();

    private UUID uuid;
    private TreeAdvancement.RootTreeAdvancement root;
    private Player player;
    private AdvancementManager manager;

    public FamilyTree(UUID uuid, TreeAdvancement.RootTreeAdvancement root) {
        this.uuid = uuid;
        this.root = root;
        this.player = Bukkit.getPlayer(uuid);

        AdvancementsPacket packet = new AdvancementsPacket(player, false, null, List.of(new NameKey("family_tree", "ego")));
        packet.send();

        this.manager = new AdvancementManager(new NameKey("manager", uuid.toString()));

        manager.addPlayer(player);

        AdvancementDisplay.AdvancementFrame frame = AdvancementDisplay.AdvancementFrame.CHALLENGE;
        AdvancementVisibility visibility = AdvancementVisibility.ALWAYS;


        AdvancementDisplay display = new AdvancementDisplay(root.getIcon(), root.getTitle(), root.getDescription(), frame, visibility);
        display.setBackgroundTexture(root.getBackground());
        display.setX(root.getX());
        display.setY(root.getY());

        Advancement ego = new Advancement(new NameKey("family_tree", "ego"), display);
        advancementMap.put(root.getKey(), ego);

        this.manager.addAdvancement(ego);

    }

    public Player getPlayer() {
        return player;
    }

    public FamilyTree addTreeAdvancements(TreeAdvancement... treeAdvancements) {

        for (TreeAdvancement treeAdvancement : treeAdvancements) {
            Advancement advancement = createAdvancement(treeAdvancement);
            manager.addAdvancement(advancement);
            advancementMap.put(treeAdvancement.getKey(), advancement);
        }

        return this;
    }


    private Advancement createAdvancement(TreeAdvancement treeAdvancement) {
        AdvancementDisplay.AdvancementFrame frame = AdvancementDisplay.AdvancementFrame.GOAL;
        AdvancementVisibility visibility = AdvancementVisibility.ALWAYS;

        ItemStack icon = treeAdvancement.getIcon();
        String title = treeAdvancement.getTitle();
        String description = treeAdvancement.getDescription();

        AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);
        Advancement ego = advancementMap.get("ego");
        display.setPositionOrigin(ego);
        display.setX(treeAdvancement.getX());
        display.setY(treeAdvancement.getY());

        Advancement parent = advancementMap.get(treeAdvancement.getParent().getKey());

        if (treeAdvancement.isHidden()) {
            return new Advancement(parent, new NameKey("family_tree", treeAdvancement.getKey()), display, AdvancementFlag.SEND_WITH_HIDDEN_BOOLEAN);
        } else {
            return new Advancement(parent, new NameKey("family_tree", treeAdvancement.getKey()), display);
        }
    }
}
