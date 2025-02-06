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

    private static final Map<UUID, FamilyTree> familyTrees = new HashMap<>();

    private UUID uuid;
    private FamilyPlayerImpl familyPlayer;
    private AdvancementManager manager;
    private final List<TreeAdvancement> treeAdvancements = new ArrayList<>();
    private final Map<String, Advancement> advancementMap = new HashMap<>();
    private final Map<Integer, Integer> leftRows = new HashMap<>();
    private final Map<Integer, Integer> rightRows = new HashMap<>();

    private FamilyTree(FamilyPlayerImpl playerFam) {
        this.uuid = playerFam.getUniqueId();
        this.manager = new AdvancementManager(new NameKey("manager", uuid.toString()));
        this.familyPlayer = playerFam;

        familyTrees.put(uuid, this);
    }

    public static FamilyTree getFamilyTree(int playerFamId) {
        FamilyPlayerImpl playerFam = FamilyPlayerImpl.getFamilyPlayer(playerFamId);

        return familyTrees.getOrDefault(playerFam.getUniqueId(), new FamilyTree(playerFam));
    }

    private int getNextX(int y, Side side) {
        return getNextX(y, side, 0);
    }

    private int getNextX(int y, Side side, int min) {
        y = y / 2;
        min = min / 2;
        if (side == Side.LEFT) {
            int column = leftRows.getOrDefault(y, 0);

            if (column < min) {
                column = min;
            }

            leftRows.put(y, column + 1);
            return column * 2;
        } else {
            int column = rightRows.getOrDefault(y, 0);

            if (column < min) {
                column = min;
            }

            rightRows.put(y, column + 1);
            return (column + 1) * 2;
        }
    }

    public boolean update() {

        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            return false;
        }


        String egoKey = "ego";
        String egoLang = getRelationLang(egoKey);
        String egoTitle = familyPlayer.getName();
        String background = familyPlayer.getBackground();
        String egoSkinURL = familyPlayer.getSkinURL();
        ItemStack egoIcon = ItemStackUtils.getSkullFromURL(egoSkinURL);

        TreeAdvancement.RootTreeAdvancement root = new TreeAdvancement.RootTreeAdvancement(egoKey,  background, null, egoTitle, egoLang, egoIcon, 13.5f, 7.5f, FamilyTree.Side.LEFT);
        treeAdvancements.add(root);
        leftRows.put(0, 1);

        TreeAdvancement.HiddenAdvancement egoAnchor = addEgoAnchor(root);

        List<FamilyPlayerImpl> parents = familyPlayer.getParents();

        if (parents != null && !parents.isEmpty()) {
            TreeAdvancement.HiddenAdvancement parentsAnchor = addParentsAnchor(egoAnchor, FamilyTree.Side.CENTER, egoKey);

            int i = 0;
            for (FamilyPlayerImpl parent : parents) {
                String parentKey = "parent_" + i;
                FamilyTree.Side parentSide = i % 2 == 0 ? FamilyTree.Side.LEFT : FamilyTree.Side.RIGHT;
                TreeAdvancement.HiddenAdvancement parentAnchor = addParentAdvancement(parentsAnchor, parentSide, parent, parentKey);
                i++;
            }
        }

        FamilyPlayerImpl partnerFam = familyPlayer.getPartner();

        if (partnerFam != null) {
            String partnerKey = "partner";

            TreeAdvancement.HiddenAdvancement partnerAnchor = addPartnerAdvancement(egoAnchor, FamilyTree.Side.RIGHT, partnerFam, partnerKey);

            List<FamilyPlayerImpl> partnerSiblings = partnerFam.getSiblings();

            if (partnerSiblings != null && !partnerSiblings.isEmpty()) {
                TreeAdvancement.HiddenAdvancement partnerSiblingsAnchor = addSiblingsAnchor(partnerAnchor, FamilyTree.Side.RIGHT, partnerKey);

                int i = 0;
                for (FamilyPlayerImpl sibling : partnerSiblings) {
                    String siblingKey = "partner_sibling_" + i;
                    TreeAdvancement.HiddenAdvancement siblingAnchor = addSiblingAdvancement(partnerSiblingsAnchor, FamilyTree.Side.RIGHT, sibling, siblingKey);
                    i++;
                }
            }

            List<FamilyPlayerImpl> partnerParents = partnerFam.getParents();

            if (partnerParents != null && !partnerParents.isEmpty()) {
                TreeAdvancement.HiddenAdvancement partnerParentsAnchor = addParentsAnchor(partnerAnchor, FamilyTree.Side.RIGHT, partnerKey);

                int i = 0;
                for (FamilyPlayerImpl parent : partnerParents) {
                    String parentKey = "partner_parent_" + i;
                    TreeAdvancement.HiddenAdvancement parentAnchor = addParentAdvancement(partnerParentsAnchor, FamilyTree.Side.RIGHT, parent, parentKey);
                    i++;
                }
            }
        }

        List<FamilyPlayerImpl> children = familyPlayer.getChildren();

        if (children != null && !children.isEmpty()) {
            TreeAdvancement.HiddenAdvancement childrenAnchor = addChildrenAnchor(egoAnchor, FamilyTree.Side.CENTER, egoKey);

            int i = 0;
            for (FamilyPlayerImpl child : children) {
                String childKey = "child_" + i;
                FamilyTree.Side side = i % 2 == 0 ? FamilyTree.Side.LEFT : FamilyTree.Side.RIGHT;
                TreeAdvancement.HiddenAdvancement childAnchor = addChildAdvancement(childrenAnchor, side, child, childKey);

                addAllDownwards(child, childAnchor, side, childKey);

                i++;
            }

            for (FamilyPlayerImpl child : children) {
                String childKey = "child_" + i;
                FamilyTree.Side side = i % 2 == 0 ? FamilyTree.Side.LEFT : FamilyTree.Side.RIGHT;
                TreeAdvancement.HiddenAdvancement childAnchor = addChildAdvancement(childrenAnchor, side, child, childKey);

                addAllDownwards(child, childAnchor, side, childKey);

                i++;
            }
        }

        send(player);

        return true;
    }

    public TreeAdvancement.HiddenAdvancement addEgoAnchor(TreeAdvancement root) {
        TreeAdvancement.HiddenAdvancement egoAnchor = new TreeAdvancement.HiddenAdvancement(root.getKey()+"_anchor", root, 0.5f, 0, FamilyTree.Side.LEFT);
        treeAdvancements.add(egoAnchor);
        return egoAnchor;
    }

    public TreeAdvancement.HiddenAdvancement addPartnerAdvancement(TreeAdvancement sourcePartnerAdv, Side side, FamilyPlayerImpl partner, String partnerKey) {
        int y = (int) sourcePartnerAdv.getY();

        int x = getNextX(y, side);

        String partnerLang = getRelationLang(partnerKey);
        String partnerTitle = partner.getName();
        String partnerSkinURL = partner.getSkinURL();
        ItemStack partnerIcon = ItemStackUtils.getSkullFromURL(partnerSkinURL);


        float anchorModifier = side == Side.LEFT ? 0.5f : -0.5f;

        TreeAdvancement.HiddenAdvancement partnerAnchor = new TreeAdvancement.HiddenAdvancement(partnerKey+"_anchor", sourcePartnerAdv, x+anchorModifier, y, side);
        TreeAdvancement.RelationAdvancement partnerAdv = new TreeAdvancement.RelationAdvancement(partnerKey, partnerAnchor, partnerTitle, partnerLang, partnerIcon, x, y, side);


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
        int y = (int) (siblingsAnchor.getY() - 1);

        int x = getNextX(y, side);

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

        int rowAbove;
        int modifier = 1;
        int y = (int) (anchor.getY()/2);
        if (side == Side.LEFT) {
            rowAbove = leftRows.getOrDefault(y+1, 0);
        } else {
            rowAbove = rightRows.getOrDefault(y+1, 0);
        }

        Logger.debugLog("Row above: " + rowAbove);
        Logger.debugLog("Anchor X: " + anchor.getX());

        int x = (int) anchor.getX();
        if (x <= rowAbove && side != Side.CENTER) {
            modifier = (rowAbove - (int) anchor.getX()) + 3;
        }

        if (side == Side.CENTER) {
            modifier = 0;
        }

        TreeAdvancement.HiddenAdvancement parentsHolder1 = new TreeAdvancement.HiddenAdvancement(key+"_parents_holder1", anchor, anchor.getX(), anchor.getY()+1.0f, side);
        TreeAdvancement.HiddenAdvancement parentsHolder2 = new TreeAdvancement.HiddenAdvancement(key+"_parents_holder2", parentsHolder1, parentsHolder1.getX()+modifier, parentsHolder1.getY(), side);
        TreeAdvancement.HiddenAdvancement parentsAnchor = new TreeAdvancement.HiddenAdvancement(key+"_parents_anchor", parentsHolder2, parentsHolder2.getX(), parentsHolder2.getY()+1.0f, side);

        treeAdvancements.add(parentsHolder1);
        treeAdvancements.add(parentsHolder2);
        treeAdvancements.add(parentsAnchor);

        return parentsAnchor;
    }

    public TreeAdvancement.HiddenAdvancement addParentAdvancement(TreeAdvancement parentsAnchor, Side side, FamilyPlayerImpl parent, String parentKey) {
        int y = (int) (parentsAnchor.getY());

        int x = getNextX(y, side);

        String parentLang = getRelationLang(parentKey);
        String parentTitle = parent.getName();
        String parentSkinURL = parent.getSkinURL();
        ItemStack parentIcon = ItemStackUtils.getSkullFromURL(parentSkinURL);


        float anchorModifier = side == Side.LEFT ? 0.5f : -0.5f;

        TreeAdvancement.HiddenAdvancement parentAnchor = new TreeAdvancement.HiddenAdvancement(parentKey+"_holder", parentsAnchor, x+anchorModifier, y, side);
        TreeAdvancement.RelationAdvancement parentAdv = new TreeAdvancement.RelationAdvancement(parentKey, parentAnchor, parentTitle, parentLang, parentIcon, x, y, side);


        treeAdvancements.add(parentAnchor);
        treeAdvancements.add(parentAdv);

        return parentAnchor;
    }

    public TreeAdvancement.HiddenAdvancement addChildrenAnchor(TreeAdvancement anchor, Side side, String key) {
        int modifier = side == Side.RIGHT ? 1 : 0;

        TreeAdvancement.HiddenAdvancement childrenHolder = new TreeAdvancement.HiddenAdvancement(key+"_children_holder", anchor, anchor.getX()+modifier, anchor.getY(), side);
        TreeAdvancement.HiddenAdvancement childrenAnchor = new TreeAdvancement.HiddenAdvancement(key+"_children_anchor", childrenHolder, childrenHolder.getX(), childrenHolder.getY()-1.0f, side);


        treeAdvancements.add(childrenHolder);
        treeAdvancements.add(childrenAnchor);

        return childrenAnchor;
    }

    public TreeAdvancement.HiddenAdvancement addChildAdvancement(TreeAdvancement childrenAnchor, Side side, FamilyPlayerImpl child, String childKey) {
        int y = (int) (childrenAnchor.getY()-1);

        int min = (int) (childrenAnchor.getX() - 1);

        if (child.hasChildren()) {
            int rowBelow;
            if (side == Side.LEFT) {
                rowBelow = leftRows.getOrDefault(y-1, 0);
            } else {
                rowBelow = rightRows.getOrDefault(y-1, 0);
            }

            min = Integer.max(min, rowBelow-1);
        }

        int x = getNextX(y, side, min);
        
        String childLang = getRelationLang(childKey);
        String childTitle = child.getName();
        String childSkinURL = child.getSkinURL();
        ItemStack childIcon = ItemStackUtils.getSkullFromURL(childSkinURL);


        float anchorModifier = side == Side.LEFT ? 0.5f : -0.5f;

        TreeAdvancement.HiddenAdvancement childHolder = new TreeAdvancement.HiddenAdvancement(childKey+"_holder1", childrenAnchor, x+anchorModifier, y+1, side);
        TreeAdvancement.HiddenAdvancement childAnchor = new TreeAdvancement.HiddenAdvancement(childKey+"_holder2", childHolder, x+anchorModifier, y, side);
        TreeAdvancement.RelationAdvancement childAdv = new TreeAdvancement.RelationAdvancement(childKey, childAnchor, childTitle, childLang, childIcon, x, y, side);


        treeAdvancements.add(childHolder);
        treeAdvancements.add(childAnchor);
        treeAdvancements.add(childAdv);

        return childAnchor;
    }

    public void addAllDownwards(FamilyPlayerImpl familyPlayer, TreeAdvancement.HiddenAdvancement anchor, FamilyTree.Side side, String key) {

        FamilyPlayerImpl partnerFam = familyPlayer.getPartner();

        if (partnerFam != null) {
            String partnerKey = key + "_partner";
            TreeAdvancement.HiddenAdvancement partnerAnchor = addPartnerAdvancement(anchor, side, partnerFam, partnerKey);
        }

        List<FamilyPlayerImpl> children = familyPlayer.getChildren();

        if (children != null && !children.isEmpty()) {
            TreeAdvancement.HiddenAdvancement childrenAnchor = addChildrenAnchor(anchor, side, key);

            int i = 0;
            for (FamilyPlayerImpl child : children) {
                String childKey = key + "_child_" + i;
                TreeAdvancement.HiddenAdvancement childAnchor = addChildAdvancement(childrenAnchor, side, child, childKey);

                addAllDownwards(child, childAnchor, side, childKey);

                i++;
            }
        }
    }

    public void send(Player player) {
        reset(player);

        for (TreeAdvancement treeAdvancement : treeAdvancements) {
            Advancement advancement = createAdvancement(treeAdvancement);
            advancementMap.put(treeAdvancement.getKey(), advancement);
            manager.addAdvancement(advancement);
        }
    }

    public void reset(Player player) {
        AdvancementsPacket packet = new AdvancementsPacket(player, false, advancementMap.values().stream().toList(), null);
        packet.send();
        advancementMap.clear();
    }

    private Advancement createAdvancement(TreeAdvancement treeAdvancement) {
        AdvancementDisplay.AdvancementFrame frame;
        if (treeAdvancement instanceof TreeAdvancement.RootTreeAdvancement) {
            frame = AdvancementDisplay.AdvancementFrame.CHALLENGE;
        } else {
            frame = AdvancementDisplay.AdvancementFrame.GOAL;
        }

        AdvancementVisibility visibility = AdvancementVisibility.ALWAYS;

        ItemStack icon = treeAdvancement.getIcon();
        String title = treeAdvancement.getTitle();
        String description = treeAdvancement.getDescription();

        AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);
        Advancement ego = advancementMap.get("ego");
        display.setPositionOrigin(ego);
        display.setX(treeAdvancement.getX() * (treeAdvancement.getSide() == Side.LEFT ? -1 : 1));
        display.setY(-treeAdvancement.getY());

        if (treeAdvancement instanceof TreeAdvancement.RootTreeAdvancement rootTreeAdvancement) {
            display.setBackgroundTexture(rootTreeAdvancement.getBackground());
        }

        Advancement parent = treeAdvancement.getParent() == null ? null : advancementMap.get(treeAdvancement.getParent().getKey());

        if (treeAdvancement.isHidden()) {
            return new Advancement(parent, new NameKey("family_tree", treeAdvancement.getKey()), display, AdvancementFlag.SEND_WITH_HIDDEN_BOOLEAN);
        } else {
            return new Advancement(parent, new NameKey("family_tree", treeAdvancement.getKey()), display);
        }
    }

    public enum Side {
        LEFT,
        RIGHT,
        CENTER,
    }
}
