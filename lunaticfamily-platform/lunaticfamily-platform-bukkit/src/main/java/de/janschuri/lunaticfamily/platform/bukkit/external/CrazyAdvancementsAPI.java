package de.janschuri.lunaticfamily.platform.bukkit.external;

import de.janschuri.lunaticfamily.TreeAdvancement;
import de.janschuri.lunaticfamily.common.handler.familytree.HiddenAdvancement;
import de.janschuri.lunaticfamily.common.handler.familytree.RootAdvancement;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.platform.bukkit.util.ItemStackUtils;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementFlag;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import eu.endercentral.crazy_advancements.packet.AdvancementsPacket;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.nio.file.LinkOption;
import java.util.*;

public class CrazyAdvancementsAPI {

    private static final Map<UUID, AdvancementManager> managerMap = new HashMap<>();

    private static AdvancementManager getManager(Player player) {
        NameKey key = new NameKey("lunaticfamily", player.getUniqueId().toString());
        return managerMap.computeIfAbsent(player.getUniqueId(), uuid -> new AdvancementManager(key, player));
    }

    public static void send(Player player, List<TreeAdvancement> treeAdvancements) {
        AdvancementManager manager = getManager(player);
        reset(manager, player);

        HashMap<String, Advancement> advancementMap = new HashMap<>();

        for (TreeAdvancement treeAdvancement : treeAdvancements) {
            Advancement advancement = createAdvancement(treeAdvancement, advancementMap);
            advancementMap.put(treeAdvancement.getKey(), advancement);
            manager.addPlayer(player);
            manager.addAdvancement(advancement);
        }
    }

    public static void reset(AdvancementManager manager, Player player) {
        List<Advancement> advancements = manager.getAdvancements();
        AdvancementsPacket packet = new AdvancementsPacket(player, false, advancements, null);
        packet.send();
        manager.removeAdvancement(advancements.toArray(new Advancement[0]));
    }

    private static ItemStack getIcon(TreeAdvancement treeAdvancement) {
        String skinUrl = treeAdvancement.getSkinUrl();

        if (skinUrl == null) {
            return new ItemStack(Material.STONE);
        }

        return ItemStackUtils.getSkullFromURL(skinUrl);
    }

    private static Advancement createAdvancement(TreeAdvancement treeAdvancement, Map<String, Advancement> advancementMap) {
        AdvancementDisplay.AdvancementFrame frame;
        if (treeAdvancement instanceof RootAdvancement) {
            frame = AdvancementDisplay.AdvancementFrame.CHALLENGE;
        } else {
            frame = AdvancementDisplay.AdvancementFrame.GOAL;
        }

        AdvancementVisibility visibility = AdvancementVisibility.ALWAYS;

        ItemStack icon = getIcon(treeAdvancement);
        String title = treeAdvancement.getTitle();
        title = title == null ? treeAdvancement.getKey() : title;
        String description = treeAdvancement.getDescription();
        description = description == null ? treeAdvancement.getKey() : description;

        AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);
        float x = treeAdvancement.getX();
        float y = treeAdvancement.getY();
        display.setX(x * (treeAdvancement.getSide() == TreeAdvancement.Side.LEFT ? -1 : 1));
        display.setY(-y);

        if (treeAdvancement instanceof RootAdvancement rootTreeAdvancement) {
            Logger.debugLog("RootAdvancement: " + rootTreeAdvancement.getKey() + " at " + x + ", " + y);
            display.setBackgroundTexture(rootTreeAdvancement.getBackground());
        } else {
            Advancement ego = advancementMap.get("ego");
            display.setPositionOrigin(ego);
        }

        List<AdvancementFlag> flags = new ArrayList<>();

        if (treeAdvancement instanceof HiddenAdvancement hiddenAdvancement) {
            if (!hiddenAdvancement.isDebug()) {
                flags.add(AdvancementFlag.SEND_WITH_HIDDEN_BOOLEAN);
            }
        }

        Advancement parent = treeAdvancement.getParent() == null ? null : advancementMap.get(treeAdvancement.getParent().getKey());

        return new Advancement(parent, new NameKey("family_tree", treeAdvancement.getKey()), display, flags.toArray(new AdvancementFlag[0]));
    }

}
