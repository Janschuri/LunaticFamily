package de.janschuri.lunaticfamily.platform.bukkit;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.FamilyTreeManager;
import de.janschuri.lunaticlib.platform.bukkit.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;


public class FamilyTreeManagerImpl implements FamilyTreeManager {

    private static final Map<String, String> relationLangs = new HashMap<>();

    @Override
    public boolean update(String server, int familyPlayerID) {
        FamilyPlayerImpl familyPlayer = LunaticFamily.getFamilyPlayer(familyPlayerID);
        UUID uuid = familyPlayer.getUniqueId();

        Logger.debugLog("Creating FamilyTree for " + uuid);
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            Logger.errorLog("Player with UUID " + uuid + " not found on the server.");
            return false;
        }


        String egoKey = "ego";
        String egoLang = getRelationLang(egoKey);
        String egoTitle = familyPlayer.getName();
        String background = familyPlayer.getBackground();
        String egoSkinURL = familyPlayer.getSkinURL();
        ItemStack egoIcon = ItemStackUtils.getSkullFromURL(egoSkinURL);

        TreeAdvancement.RootTreeAdvancement root = new TreeAdvancement.RootTreeAdvancement(egoKey,  background, null, egoTitle, egoLang, egoIcon, 13.5f, 7.5f, FamilyTree.Side.LEFT);

        FamilyTree familyTree = new FamilyTree(familyPlayer, root);

        if (familyTree.getPlayer() == null) {
            return true;
        }

        TreeAdvancement.HiddenAdvancement egoAnchor = familyTree.addEgoAnchor();

        List<FamilyPlayerImpl> parents = familyPlayer.getParents();

        if (parents != null && !parents.isEmpty()) {
            TreeAdvancement.HiddenAdvancement parentsAnchor = familyTree.addParentsAnchor(egoAnchor, FamilyTree.Side.CENTER, egoKey);

            int i = 0;
            for (FamilyPlayerImpl parent : parents) {
                String parentKey = "parent_" + i;
                FamilyTree.Side parentSide = i % 2 == 0 ? FamilyTree.Side.LEFT : FamilyTree.Side.RIGHT;
                TreeAdvancement.HiddenAdvancement parentAnchor = familyTree.addParentAdvancement(parentsAnchor, parentSide, parent, parentKey);
                i++;
            }
        }

        FamilyPlayerImpl partnerFam = familyPlayer.getPartner();

        if (partnerFam != null) {
            String partnerKey = "partner";

            TreeAdvancement.HiddenAdvancement partnerAnchor = familyTree.addPartnerAdvancement(egoAnchor, FamilyTree.Side.RIGHT, partnerFam, partnerKey);

            List<FamilyPlayerImpl> partnerSiblings = partnerFam.getSiblings();

            if (partnerSiblings != null && !partnerSiblings.isEmpty()) {
                TreeAdvancement.HiddenAdvancement partnerSiblingsAnchor = familyTree.addSiblingsAnchor(partnerAnchor, FamilyTree.Side.RIGHT, partnerKey);

                int i = 0;
                for (FamilyPlayerImpl sibling : partnerSiblings) {
                    String siblingKey = "partner_sibling_" + i;
                    TreeAdvancement.HiddenAdvancement siblingAnchor = familyTree.addSiblingAdvancement(partnerSiblingsAnchor, FamilyTree.Side.RIGHT, sibling, siblingKey);
                    i++;
                }
            }

            List<FamilyPlayerImpl> partnerParents = partnerFam.getParents();

            if (partnerParents != null && !partnerParents.isEmpty()) {
                TreeAdvancement.HiddenAdvancement partnerParentsAnchor = familyTree.addParentsAnchor(partnerAnchor, FamilyTree.Side.RIGHT, partnerKey);

                int i = 0;
                for (FamilyPlayerImpl parent : partnerParents) {
                    String parentKey = "partner_parent_" + i;
                    TreeAdvancement.HiddenAdvancement parentAnchor = familyTree.addParentAdvancement(partnerParentsAnchor, FamilyTree.Side.RIGHT, parent, parentKey);
                    i++;
                }
            }
        }

        List<FamilyPlayerImpl> children = familyPlayer.getChildren();

        if (children != null && !children.isEmpty()) {
            TreeAdvancement.HiddenAdvancement childrenAnchor = familyTree.addChildrenAnchor(egoAnchor, FamilyTree.Side.CENTER, egoKey);

            int i = 0;
            for (FamilyPlayerImpl child : children) {
                String childKey = "child_" + i;
                FamilyTree.Side side = i % 2 == 0 ? FamilyTree.Side.LEFT : FamilyTree.Side.RIGHT;
                TreeAdvancement.HiddenAdvancement childAnchor = familyTree.addChildAdvancement(childrenAnchor, side, child, childKey);

                familyTree.addAllDownwards(child, childAnchor, side, childKey);

                i++;
            }

            for (FamilyPlayerImpl child : children) {
                String childKey = "child_" + i;
                FamilyTree.Side side = i % 2 == 0 ? FamilyTree.Side.LEFT : FamilyTree.Side.RIGHT;
                TreeAdvancement.HiddenAdvancement childAnchor = familyTree.addChildAdvancement(childrenAnchor, side, child, childKey);

                familyTree.addAllDownwards(child, childAnchor, side, childKey);

                i++;
            }
        }

        familyTree.send();

        return true;
    }


    public static String getRelationLang(String key) {
        return key;
    }
}
