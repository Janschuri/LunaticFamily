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

        FamilyTree familyTree = new FamilyTree(uuid, root);

        if (familyTree.getPlayer() == null) {
            return true;
        }

        TreeAdvancement.HiddenAdvancement egoAnchor = familyTree.addEgoAnchor();

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

        List<FamilyPlayerImpl> parents = familyPlayer.getParents();

        if (parents != null && !parents.isEmpty()) {
            TreeAdvancement.HiddenAdvancement parentsAnchor = familyTree.addParentsAnchor(egoAnchor, FamilyTree.Side.LEFT, egoKey);

            int i = 0;
            for (FamilyPlayerImpl parent : parents) {
                String parentKey = "parent_" + i;
                TreeAdvancement.HiddenAdvancement parentAnchor = familyTree.addParentAdvancement(parentsAnchor, FamilyTree.Side.LEFT, parent, parentKey);
                i++;
            }
        }

        familyTree.send();

        return true;
    }

    private List<TreeAdvancement> getTreeAdvancements(FamilyPlayerImpl familyPlayer, Map<Integer, Integer> rows) {
        List<TreeAdvancement> treeAdvancements = new ArrayList<>();

        return treeAdvancements;
    }


    public static String getRelationLang(String key) {
        return key;
    }
}
