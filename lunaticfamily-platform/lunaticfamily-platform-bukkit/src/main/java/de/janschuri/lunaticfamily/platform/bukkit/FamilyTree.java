package de.janschuri.lunaticfamily.platform.bukkit;

import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.platform.bukkit.util.ItemStackUtils;
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

import java.util.*;

import static de.janschuri.lunaticfamily.platform.bukkit.FamilyTreeManagerImpl.getRelationLang;

public class FamilyTree {


    private final Map<String, Advancement> advancementMap= new HashMap<>();

    private UUID uuid;
    private TreeAdvancement.RootTreeAdvancement root;
    private Player player;
    private AdvancementManager manager;
    private List<TreeAdvancement> treeAdvancements = new ArrayList<>();
    private Map<Integer, Integer> leftRows = new HashMap<>();
    private Map<Integer, Integer> rightRows = new HashMap<>();

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
        leftRows.put(0, 1);

        this.manager.addAdvancement(ego);

    }

    public Player getPlayer() {
        return player;
    }

    public TreeAdvancement.HiddenAdvancement addEgoAnchor() {
        TreeAdvancement.HiddenAdvancement egoAnchor = new TreeAdvancement.HiddenAdvancement(root.getKey()+"_anchor", root, 0.5f, 0, FamilyTree.Side.LEFT);
        treeAdvancements.add(egoAnchor);
        return egoAnchor;
    }

    public TreeAdvancement.HiddenAdvancement addPartnerAdvancement(TreeAdvancement sourcePartnerAdv, Side side, FamilyPlayerImpl partner, String partnerKey) {
        int x;
        int y = (int) sourcePartnerAdv.getY();
        Logger.debugLog("Y: " + y);

        if (side == Side.LEFT) {
            int column = leftRows.getOrDefault(y, 1);
            leftRows.put(y, column + 1);
            x = column * 2;
        } else {
            int column = rightRows.getOrDefault(y, 0);
            rightRows.put(y, column + 1);
            x = (column + 1) * 2;
        }

        String partnerLang = getRelationLang(partnerKey);
        String partnerTitle = partner.getName();
        String partnerSkinURL = partner.getSkinURL();
        ItemStack partnerIcon = ItemStackUtils.getSkullFromURL(partnerSkinURL);


        float anchorModifier = side == Side.LEFT ? 0.5f : -0.5f;

        TreeAdvancement.HiddenAdvancement partnerAnchor = new TreeAdvancement.HiddenAdvancement(partnerKey+"_anchor", sourcePartnerAdv, x+anchorModifier, sourcePartnerAdv.getY(), side);
        TreeAdvancement.RelationAdvancement partnerAdv = new TreeAdvancement.RelationAdvancement(partnerKey, partnerAnchor, partnerTitle, partnerLang, partnerIcon, x, partnerAnchor.getY(), side);


        treeAdvancements.add(partnerAnchor);
        treeAdvancements.add(partnerAdv);

        return partnerAnchor;
    }

    public TreeAdvancement.HiddenAdvancement addSiblingsAnchor(TreeAdvancement anchor, Side side, String key) {
        TreeAdvancement.HiddenAdvancement partnerSiblingsAnchor = new TreeAdvancement.HiddenAdvancement(key+"_siblings_anchor", anchor, anchor.getX(), anchor.getY()+1.0f, side);
        treeAdvancements.add(partnerSiblingsAnchor);
        return partnerSiblingsAnchor;
    }

    public TreeAdvancement.HiddenAdvancement addSiblingAdvancement(TreeAdvancement siblingsAnchor, Side side, FamilyPlayerImpl sibling, String siblingKey) {
        int x;
        int y = (int) (siblingsAnchor.getY() - 1);

        if (side == Side.LEFT) {
            int column = leftRows.getOrDefault(y, 1);
            leftRows.put(y, column + 1);
            x = column * 2;
        } else {
            int column = rightRows.getOrDefault(y, 0);
            rightRows.put(y, column + 1);
            x = (column + 1) * 2;
        }

        String siblingLang = getRelationLang(siblingKey);
        String siblingTitle = sibling.getName();
        String siblingSkinURL = sibling.getSkinURL();
        ItemStack siblingIcon = ItemStackUtils.getSkullFromURL(siblingSkinURL);

        float anchorModifier = side == Side.LEFT ? 0.5f : -0.5f;

        TreeAdvancement.HiddenAdvancement siblingHolder = new TreeAdvancement.HiddenAdvancement(siblingKey+"_holder", siblingsAnchor,x+anchorModifier, siblingsAnchor.getY(), side);
        TreeAdvancement.HiddenAdvancement siblingAnchor = new TreeAdvancement.HiddenAdvancement(siblingKey+"_anchor", siblingHolder, siblingHolder.getX(), siblingHolder.getY()-1.0f, side);
        TreeAdvancement.RelationAdvancement siblingAdvancement = new TreeAdvancement.RelationAdvancement(siblingKey, siblingAnchor, siblingTitle, siblingLang, siblingIcon, x, siblingAnchor.getY(), side);


        treeAdvancements.add(siblingHolder);
        treeAdvancements.add(siblingAnchor);
        treeAdvancements.add(siblingAdvancement);

        return siblingAnchor;
    }

    public TreeAdvancement.HiddenAdvancement addParentsAnchor(TreeAdvancement anchor, Side side, String key) {
        TreeAdvancement.HiddenAdvancement partnerParentsHolder1 = new TreeAdvancement.HiddenAdvancement(key+"_parents_holder1", anchor, anchor.getX(), anchor.getY()+1.0f, side);
        TreeAdvancement.HiddenAdvancement partnerParentsHolder2 = new TreeAdvancement.HiddenAdvancement(key+"_parents_holder2", partnerParentsHolder1, partnerParentsHolder1.getX()+1.0f, partnerParentsHolder1.getY(), side);
        TreeAdvancement.HiddenAdvancement partnerParentsAnchor = new TreeAdvancement.HiddenAdvancement(key+"_parents_anchor", partnerParentsHolder2, partnerParentsHolder2.getX(), partnerParentsHolder2.getY()+1.0f, side);

        treeAdvancements.add(partnerParentsHolder1);
        treeAdvancements.add(partnerParentsHolder2);
        treeAdvancements.add(partnerParentsAnchor);

        return partnerParentsAnchor;
    }

    public TreeAdvancement.HiddenAdvancement addParentAdvancement(TreeAdvancement parentsAnchor, Side side, FamilyPlayerImpl parent, String parentKey) {
        int x;
        int y = (int) (parentsAnchor.getY());

        if (side == Side.LEFT) {
            int column = leftRows.getOrDefault(y, 1);

            if (column < (int) parentsAnchor.getX() - 2) {
                column = (int) parentsAnchor.getX() - 1;
            }

            leftRows.put(y, column + 1);
            x = column * 2;
        } else {
            int column = rightRows.getOrDefault(y, 0);

            if (column < (int) parentsAnchor.getX() - 2) {
                column = (int) parentsAnchor.getX() - 1;
            }

            rightRows.put(y, column + 1);
            x = (column + 1) * 2;
        }

        Logger.debugLog("X: " + x);

        String parentLang = getRelationLang(parentKey);
        String parentTitle = parent.getName();
        String siblingSkinURL = parent.getSkinURL();
        ItemStack parentIcon = ItemStackUtils.getSkullFromURL(siblingSkinURL);


        float anchorModifier = side == Side.LEFT ? 0.5f : -0.5f;

        TreeAdvancement.HiddenAdvancement parentAnchor = new TreeAdvancement.HiddenAdvancement(parentKey+"_holder", parentsAnchor, x+anchorModifier, y, side);
        TreeAdvancement.RelationAdvancement parentAdv = new TreeAdvancement.RelationAdvancement(parentKey, parentAnchor, parentTitle, parentLang, parentIcon, x, y, side);


        treeAdvancements.add(parentAnchor);
        treeAdvancements.add(parentAdv);

        return parentAnchor;
    }

    public void send() {
        for (TreeAdvancement treeAdvancement : treeAdvancements) {
            Advancement advancement = createAdvancement(treeAdvancement);
            manager.addAdvancement(advancement);
            advancementMap.put(treeAdvancement.getKey(), advancement);
        }
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
        display.setX(treeAdvancement.getX() * (treeAdvancement.getSide() == Side.LEFT ? -1 : 1));
        display.setY(-treeAdvancement.getY());

        Advancement parent = advancementMap.get(treeAdvancement.getParent().getKey());

        if (treeAdvancement.isHidden()) {
            return new Advancement(parent, new NameKey("family_tree", treeAdvancement.getKey()), display, AdvancementFlag.SEND_WITH_HIDDEN_BOOLEAN);
        } else {
            return new Advancement(parent, new NameKey("family_tree", treeAdvancement.getKey()), display);
        }
    }

    public static enum Side {
        LEFT,
        RIGHT
    }
}
