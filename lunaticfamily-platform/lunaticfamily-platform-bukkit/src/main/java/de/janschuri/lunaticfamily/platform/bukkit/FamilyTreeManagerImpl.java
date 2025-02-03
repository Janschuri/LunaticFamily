package de.janschuri.lunaticfamily.platform.bukkit;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.FamilyTreeManager;
import de.janschuri.lunaticlib.platform.bukkit.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;

import java.util.*;


public class FamilyTreeManagerImpl implements FamilyTreeManager {

    @Override
    public boolean isFamilyTreeMapLoaded() {
        return FamilyTree.isLoaded();
    }

    @Override
    public boolean loadFamilyTreeMap(String JSONContent) {
        JSONArray jsonArray = new JSONArray(JSONContent);

        if (jsonArray.isEmpty()) {
            return false;
        }

        return FamilyTree.load(jsonArray);
    }

    @Override
    public boolean update(String server, UUID uuid, String background, List<String> familyList, Map<String, String> names, Map<String, String> skins, Map<String, String> relationLangs) {
        if (!isFamilyTreeMapLoaded()) {
            return false;
        }

        Logger.debugLog("Creating FamilyTree for " + uuid);
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) {
            Logger.errorLog("Player with UUID " + uuid + " not found on the server.");
            return false;
        }


        String egoKey = "ego";
        String egoLang = relationLangs.get(egoKey);
        String egoTitle = names.get(egoKey);
        ItemStack egoIcon = ItemStackUtils.getSkullFromURL(skins.get(egoKey));

        TreeAdvancement.RootTreeAdvancement root = (TreeAdvancement.RootTreeAdvancement) new TreeAdvancement.RootTreeAdvancement("ego", background)
                .title(egoTitle)
                .description(egoLang)
                .icon(egoIcon);

        FamilyTree familyTree = new FamilyTree(uuid, background, root);

        if (familyTree.getPlayer() == null) {
            return true;
        }

        List<TreeAdvancement> treeAdvancements = new ArrayList<>();

        for (String advancementKey : FamilyTree.getAdvancements()) {

            if (advancementKey.equalsIgnoreCase("ego")) {
                continue;
            }

            String relationKey = advancementKey
                    .replace("_first_holder", "")
                    .replace("_second_holder", "")
                    .replace("_third_holder", "")
                    .replace("_holder", "");


            if (familyList.contains(relationKey)) {
                String relationLang = relationLangs.get(relationKey);
                String title = names.get(relationKey);
                ItemStack icon = ItemStackUtils.getSkullFromURL(skins.get(relationKey));

                TreeAdvancement relationAdv = new TreeAdvancement(advancementKey)
                        .title(title)
                        .description(relationLang)
                        .icon(icon);

                treeAdvancements.add(relationAdv);
            }
        }

        familyTree.addTreeAdvancements(treeAdvancements.toArray(new TreeAdvancement[0]));

        return true;
    }

    @Override
    public FamilyPlayerImpl getFamilyPlayer(int id) {
        return FamilyPlayerImpl.getFamilyPlayer(id);
    }

    @Override
    public String getRelation(String relation, String key) {
        return LunaticFamily.getLanguageConfig().getRelation(relation, key);
    }
}
