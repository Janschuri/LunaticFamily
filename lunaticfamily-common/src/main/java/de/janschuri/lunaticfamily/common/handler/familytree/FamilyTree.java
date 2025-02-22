package de.janschuri.lunaticfamily.common.handler.familytree;

import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.TreeAdvancement;

import java.util.*;

public class FamilyTree {

    private final FamilyPlayer familyPlayer;
    private final List<TreeAdvancement> treeAdvancements = new ArrayList<>();
    private final Map<Integer, Integer> leftRows = new HashMap<>();
    private final Map<Integer, Integer> rightRows = new HashMap<>();

    public FamilyTree(FamilyPlayer playerFam) {
        this.familyPlayer = playerFam;
    }

    public List<TreeAdvancement> getTreeAdvancements() {
        return treeAdvancements;
    }

    public List<RelationAdvancement> getRelationAdvancements() {
        return treeAdvancements.stream()
                .filter(adv -> adv instanceof RelationAdvancement)
                .map(adv -> (RelationAdvancement) adv)
                .toList();
    }

    private static String getRelationLang(String key) {
        return key;
    }

    private int getNextX(int y, TreeAdvancement.Side side) {
        return getNextX(y, side, 0);
    }

    private int getNextX(int y, TreeAdvancement.Side side, int min) {
        y = y / 2;
        min = min / 2;
        if (side == TreeAdvancement.Side.LEFT) {
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

    public boolean isFamilyMember(FamilyPlayer player) {
        return getRelationAdvancements().stream()
                .anyMatch(adv -> adv.getId() == player.getId());
    }

    public void update() {

        treeAdvancements.clear();

        int egoId = familyPlayer.getId();
        UUID egoUUID = familyPlayer.getUUID();
        String egoGender = familyPlayer.getGender();
        String egoKey = "ego";
        String egoLang = getRelationLang(egoKey);
        String egoTitle = familyPlayer.getName();
        String background = familyPlayer.getBackground();
        String egoSkinURL = familyPlayer.getSkinURL();

        RootAdvancement root = new RootAdvancement(egoKey,  background, null, egoId, egoUUID, egoGender, egoTitle, egoLang, egoSkinURL, 13.5f, 7.5f, TreeAdvancement.Side.LEFT);
        treeAdvancements.add(root);
        leftRows.put(0, 1);

        HiddenAdvancement egoAnchor = addEgoAnchor(root);

        List<FamilyPlayer> parents = familyPlayer.getParents();

        if (parents != null && !parents.isEmpty()) {
            HiddenAdvancement parentsAnchor = addParentsAnchor(egoAnchor, TreeAdvancement.Side.CENTER, egoKey);

            int i = 0;
            for (FamilyPlayer parent : parents) {
                String parentKey = "parent_" + i;
                TreeAdvancement.Side parentSide = i % 2 == 0 ? TreeAdvancement.Side.LEFT : TreeAdvancement.Side.RIGHT;
                HiddenAdvancement parentAnchor = addParentAdvancement(parentsAnchor, parentSide, parent, parentKey);
                addAllUpwards(parent, parentAnchor, parentSide, parentKey);
                i++;
            }
        }

        FamilyPlayer partnerFam = familyPlayer.getPartner();

        if (partnerFam != null) {
            String partnerKey = "partner";

            HiddenAdvancement partnerAnchor = addPartnerAdvancement(egoAnchor, TreeAdvancement.Side.RIGHT, partnerFam, partnerKey);

            List<FamilyPlayer> partnerSiblings = partnerFam.getSiblings();

            if (partnerSiblings != null && !partnerSiblings.isEmpty()) {
                HiddenAdvancement partnerSiblingsAnchor = addSiblingsAnchor(partnerAnchor, TreeAdvancement.Side.RIGHT, partnerKey);

                int i = 0;
                for (FamilyPlayer sibling : partnerSiblings) {
                    String siblingKey = "partner_sibling_" + i;
                    HiddenAdvancement siblingAnchor = addSiblingAdvancement(partnerSiblingsAnchor, TreeAdvancement.Side.RIGHT, sibling, siblingKey);
                    i++;
                }
            }

            List<FamilyPlayer> partnerParents = partnerFam.getParents();

            if (partnerParents != null && !partnerParents.isEmpty()) {
                HiddenAdvancement partnerParentsAnchor = addParentsAnchor(partnerAnchor, TreeAdvancement.Side.RIGHT, partnerKey);

                int i = 0;
                for (FamilyPlayer parent : partnerParents) {
                    String parentKey = "partner_parent_" + i;
                    HiddenAdvancement parentAnchor = addParentAdvancement(partnerParentsAnchor, TreeAdvancement.Side.RIGHT, parent, parentKey);
                    i++;
                }
            }
        }

        List<FamilyPlayer> children = familyPlayer.getChildren();

        if (children != null && !children.isEmpty()) {
            HiddenAdvancement childrenAnchor = addChildrenAnchor(egoAnchor, TreeAdvancement.Side.CENTER, egoKey);

            int i = 0;
            for (FamilyPlayer child : children) {
                String childKey = "child_" + i;
                TreeAdvancement.Side side = i % 2 == 0 ? TreeAdvancement.Side.LEFT : TreeAdvancement.Side.RIGHT;
                HiddenAdvancement childAnchor = addChildAdvancement(childrenAnchor, side, child, childKey);

                addAllDownwards(child, childAnchor, side, childKey);

                i++;
            }
        }
    }

    public HiddenAdvancement addEgoAnchor(TreeAdvancement root) {
        HiddenAdvancement egoAnchor = new HiddenAdvancement(root.getKey()+"_anchor", root, 0.5f, 0, TreeAdvancement.Side.LEFT);
        treeAdvancements.add(egoAnchor);
        return egoAnchor;
    }

    public HiddenAdvancement addPartnerAdvancement(TreeAdvancement sourcePartnerAdv, TreeAdvancement.Side side, FamilyPlayer partner, String partnerKey) {
        int y = (int) sourcePartnerAdv.getY();

        int x = getNextX(y, side);

        int partnerId = partner.getId();
        UUID partnerUUID = partner.getUUID();
        String partnerGender = partner.getGender();
        String partnerLang = getRelationLang(partnerKey);
        String partnerTitle = partner.getName();
        String partnerSkinURL = partner.getSkinURL();


        float anchorModifier = side == TreeAdvancement.Side.LEFT ? 0.5f : -0.5f;

        HiddenAdvancement partnerAnchor = new HiddenAdvancement(partnerKey+"_anchor", sourcePartnerAdv, x+anchorModifier, y, side);
        RelationAdvancement partnerAdv = new RelationAdvancement(partnerKey, partnerAnchor, partnerId, partnerUUID, partnerGender, partnerTitle, partnerLang, partnerSkinURL, x, y, side);


        treeAdvancements.add(partnerAnchor);
        treeAdvancements.add(partnerAdv);

        return partnerAnchor;
    }

    public HiddenAdvancement addSiblingsAnchor(TreeAdvancement anchor, TreeAdvancement.Side side, String key) {
        HiddenAdvancement partnerSiblingsAnchor = new HiddenAdvancement(key+"_siblings_anchor", anchor, anchor.getX(), anchor.getY()+1.0f, side);
        treeAdvancements.add(partnerSiblingsAnchor);
        return partnerSiblingsAnchor;
    }

    public HiddenAdvancement addSiblingAdvancement(TreeAdvancement siblingsAnchor, TreeAdvancement.Side side, FamilyPlayer sibling, String siblingKey) {
        int y = (int) (siblingsAnchor.getY() - 1);

        int x = getNextX(y, side);

        int siblingId = sibling.getId();
        UUID siblingUUID = sibling.getUUID();
        String siblingGender = sibling.getGender();
        String siblingLang = getRelationLang(siblingKey);
        String siblingTitle = sibling.getName();
        String siblingSkinURL = sibling.getSkinURL();

        float anchorModifier = side == TreeAdvancement.Side.LEFT ? 0.5f : -0.5f;

        HiddenAdvancement siblingHolder = new HiddenAdvancement(siblingKey+"_holder", siblingsAnchor,x+anchorModifier, siblingsAnchor.getY(), side);
        HiddenAdvancement siblingAnchor = new HiddenAdvancement(siblingKey+"_anchor", siblingHolder, siblingHolder.getX(), siblingHolder.getY()-1.0f, side);
        RelationAdvancement siblingAdvancement = new RelationAdvancement(siblingKey, siblingAnchor, siblingId, siblingUUID, siblingGender, siblingTitle, siblingLang, siblingSkinURL, x, siblingAnchor.getY(), side);


        treeAdvancements.add(siblingHolder);
        treeAdvancements.add(siblingAnchor);
        treeAdvancements.add(siblingAdvancement);

        return siblingAnchor;
    }

    public HiddenAdvancement addParentsAnchor(TreeAdvancement anchor, TreeAdvancement.Side side, String key) {

        int rowAbove;
        int modifier = 1;
        int y = (int) (anchor.getY()/2);
        if (side == TreeAdvancement.Side.LEFT) {
            rowAbove = leftRows.getOrDefault(y+1, 0);
        } else {
            rowAbove = rightRows.getOrDefault(y+1, 0);
        }

        Logger.debugLog("Row above: " + rowAbove);
        Logger.debugLog("Anchor X: " + anchor.getX());

        int x = (int) anchor.getX();
        if (x < rowAbove && side != TreeAdvancement.Side.CENTER) {
            modifier = (rowAbove - (int) anchor.getX()) + 3;
        }

        if (side == TreeAdvancement.Side.CENTER) {
            modifier = 0;
        }

        HiddenAdvancement parentsHolder1 = new HiddenAdvancement(key+"_parents_holder1", anchor, anchor.getX(), anchor.getY()+1.0f, side);
        HiddenAdvancement parentsHolder2 = new HiddenAdvancement(key+"_parents_holder2", parentsHolder1, parentsHolder1.getX()+modifier, parentsHolder1.getY(), side);
        HiddenAdvancement parentsAnchor = new HiddenAdvancement(key+"_parents_anchor", parentsHolder2, parentsHolder2.getX(), parentsHolder2.getY()+1.0f, side);

        treeAdvancements.add(parentsHolder1);
        treeAdvancements.add(parentsHolder2);
        treeAdvancements.add(parentsAnchor);

        return parentsAnchor;
    }

    public HiddenAdvancement addParentAdvancement(TreeAdvancement parentsAnchor, TreeAdvancement.Side side, FamilyPlayer parent, String parentKey) {
        int y = (int) (parentsAnchor.getY());

        int x = getNextX(y, side);

        int parentId = parent.getId();
        UUID parentUUID = parent.getUUID();
        String parentGender = parent.getGender();
        String parentLang = getRelationLang(parentKey);
        String parentTitle = parent.getName();
        String parentSkinURL = parent.getSkinURL();


        float anchorModifier = side == TreeAdvancement.Side.LEFT ? 0.5f : -0.5f;

        HiddenAdvancement parentAnchor = new HiddenAdvancement(parentKey+"_holder", parentsAnchor, x+anchorModifier, y, side);
        RelationAdvancement parentAdv = new RelationAdvancement(parentKey, parentAnchor, parentId, parentUUID, parentGender, parentTitle, parentLang, parentSkinURL, x, y, side);


        treeAdvancements.add(parentAnchor);
        treeAdvancements.add(parentAdv);

        return parentAnchor;
    }

    public HiddenAdvancement addChildrenAnchor(TreeAdvancement anchor, TreeAdvancement.Side side, String key) {
        int modifier = side == TreeAdvancement.Side.RIGHT ? 1 : 0;

        HiddenAdvancement childrenHolder = new HiddenAdvancement(key+"_children_holder", anchor, anchor.getX()+modifier, anchor.getY(), side);
        HiddenAdvancement childrenAnchor = new HiddenAdvancement(key+"_children_anchor", childrenHolder, childrenHolder.getX(), childrenHolder.getY()-1.0f, side);


        treeAdvancements.add(childrenHolder);
        treeAdvancements.add(childrenAnchor);

        return childrenAnchor;
    }

    public HiddenAdvancement addChildAdvancement(TreeAdvancement childrenAnchor, TreeAdvancement.Side side, FamilyPlayer child, String childKey) {
        int y = (int) (childrenAnchor.getY()-1);

        int min = (int) (childrenAnchor.getX() - 1);

        if (child.hasChildren()) {
            int rowBelow;
            if (side == TreeAdvancement.Side.LEFT) {
                rowBelow = leftRows.getOrDefault(y-1, 0);
            } else {
                rowBelow = rightRows.getOrDefault(y-1, 0);
            }

            min = Integer.max(min, rowBelow-1);
        }

        int x = getNextX(y, side, min);

        int childId = child.getId();
        UUID childUUID = child.getUUID();
        String childGender = child.getGender();
        String childLang = getRelationLang(childKey);
        String childTitle = child.getName();
        String childSkinURL = child.getSkinURL();


        float anchorModifier = side == TreeAdvancement.Side.LEFT ? 0.5f : -0.5f;

        HiddenAdvancement childHolder = new HiddenAdvancement(childKey+"_holder1", childrenAnchor, x+anchorModifier, y+1, side);
        HiddenAdvancement childAnchor = new HiddenAdvancement(childKey+"_holder2", childHolder, x+anchorModifier, y, side);
        RelationAdvancement childAdv = new RelationAdvancement(childKey, childAnchor, childId, childUUID, childGender, childTitle, childLang, childSkinURL, x, y, side);


        treeAdvancements.add(childHolder);
        treeAdvancements.add(childAnchor);
        treeAdvancements.add(childAdv);

        return childAnchor;
    }

    public void addAllDownwards(FamilyPlayer familyPlayer, HiddenAdvancement anchor, TreeAdvancement.Side side, String key) {

        FamilyPlayer partnerFam = familyPlayer.getPartner();

        if (partnerFam != null) {
            String partnerKey = key + "_partner";
            addPartnerAdvancement(anchor, side, partnerFam, partnerKey);
        }

        List<FamilyPlayer> children = familyPlayer.getChildren();

        if (children != null && !children.isEmpty()) {
            HiddenAdvancement childrenAnchor = addChildrenAnchor(anchor, side, key);

            int i = 0;
            for (FamilyPlayer child : children) {
                String childKey = key + "_child_" + i;
                HiddenAdvancement childAnchor = addChildAdvancement(childrenAnchor, side, child, childKey);

                addAllDownwards(child, childAnchor, side, childKey);

                i++;
            }
        }
    }

    public void addAllUpwards(FamilyPlayer familyPlayer, HiddenAdvancement anchor, TreeAdvancement.Side side, String key) {

        List<FamilyPlayer> siblings = familyPlayer.getSiblings();

        if (siblings != null && !siblings.isEmpty()) {
            HiddenAdvancement siblingsAnchor = addSiblingsAnchor(anchor, side, key);

            int i = 0;
            for (FamilyPlayer sibling : siblings) {
                if (sibling == null) {
                    Logger.errorLog("Sibling is null");
                    continue;
                }

                String siblingKey = key + "_sibling_" + i;
                HiddenAdvancement siblingAnchor = addSiblingAdvancement(siblingsAnchor, side, sibling, siblingKey);
                i++;
            }
        }

        List<FamilyPlayer> parents = familyPlayer.getParents();

        if (parents != null && !parents.isEmpty()) {
            HiddenAdvancement parentsAnchor = addParentsAnchor(anchor, side, key);

            int i = 0;
            for (FamilyPlayer parent : parents) {
                String parentKey = key + "_parent_" + i;
                HiddenAdvancement parentAnchor = addParentAdvancement(parentsAnchor, side, parent, parentKey);
                addAllUpwards(parent, parentAnchor, side, parentKey);
                i++;
            }
        }

    }
}
