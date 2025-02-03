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

        TreeAdvancement.RootTreeAdvancement root = new TreeAdvancement.RootTreeAdvancement(egoKey,  background, null, egoTitle, egoLang, egoIcon, 13.5f, 7.5f);

        FamilyTree familyTree = new FamilyTree(uuid, root);

        if (familyTree.getPlayer() == null) {
            return true;
        }

        List<TreeAdvancement> treeAdvancements = new ArrayList<>();

        Map<Integer, Integer> leftRows = new HashMap<>();
        Map<Integer, Integer> rightRows = new HashMap<>();

        FamilyPlayerImpl partnerFam = familyPlayer.getPartner();

        if (partnerFam != null) {
            String partnerKey = "partner";
            String partnerLang = getRelationLang(partnerKey);
            String partnerTitle = partnerFam.getName();
            String partnerSkinURL = partnerFam.getSkinURL();
            ItemStack partnerIcon = ItemStackUtils.getSkullFromURL(partnerSkinURL);
            float partnerX = 2.0f;
            float partnerY = 0.0f;

            TreeAdvancement partnerAnchor = new TreeAdvancement(partnerKey+"_anchor", root,partnerX-0.5f, partnerY);
            TreeAdvancement.RelationAdvancement partnerAdv = new TreeAdvancement.RelationAdvancement(partnerKey, partnerAnchor, partnerTitle, partnerLang, partnerIcon, partnerX, partnerY);
            rightRows.put(0, 1);

            treeAdvancements.add(partnerAnchor);
            treeAdvancements.add(partnerAdv);

            FamilyPlayerImpl partnerSiblingFam = partnerFam.getSibling();

            if (partnerSiblingFam != null) {
                String partnerSiblingKey = "partner_sibling";
                String partnerSiblingLang = getRelationLang(partnerSiblingKey);
                String partnerSiblingTitle = partnerSiblingFam.getName();
                String partnerSiblingSkinURL = partnerSiblingFam.getSkinURL();
                ItemStack partnerSiblingIcon = ItemStackUtils.getSkullFromURL(partnerSiblingSkinURL);

                TreeAdvancement partnerSiblingHolder1 = new TreeAdvancement(partnerSiblingKey+"_holder2", partnerAnchor,partnerAdv.getX()-0.5f, 1);
                TreeAdvancement partnerSiblingHolder2 = new TreeAdvancement(partnerSiblingKey+"_holder3", partnerSiblingHolder1,partnerAdv.getX()+1.5f, 1);

                TreeAdvancement partnerSiblingAnchor = new TreeAdvancement(partnerSiblingKey+"_anchor", partnerSiblingHolder2,partnerAdv.getX()+1.5f, 0);

                TreeAdvancement.RelationAdvancement partnerSiblingAdvancement = new TreeAdvancement.RelationAdvancement(partnerSiblingKey, partnerSiblingAnchor, partnerSiblingTitle, partnerSiblingLang, partnerSiblingIcon, partnerAdv.getX()+2.0f, 0);

                treeAdvancements.add(partnerSiblingHolder1);
                treeAdvancements.add(partnerSiblingHolder2);
                treeAdvancements.add(partnerSiblingAnchor);
                treeAdvancements.add(partnerSiblingAdvancement);

                rightRows.put(0, 2);

                treeAdvancements.add(partnerSiblingAdvancement);
            }
        }

        familyTree.addTreeAdvancements(treeAdvancements.toArray(new TreeAdvancement[0]));

        return true;
    }

    private List<TreeAdvancement> getTreeAdvancements(FamilyPlayerImpl familyPlayer, Map<Integer, Integer> rows) {
        List<TreeAdvancement> treeAdvancements = new ArrayList<>();

        return treeAdvancements;
    }

    private String getRelationLang(String key) {
        return key;
    }
}
