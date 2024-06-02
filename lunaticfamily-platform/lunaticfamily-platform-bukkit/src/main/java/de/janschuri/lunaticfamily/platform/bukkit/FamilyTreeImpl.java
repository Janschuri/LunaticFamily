package de.janschuri.lunaticfamily.platform.bukkit;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.FamilyTree;
import de.janschuri.lunaticlib.platform.bukkit.util.ItemStackUtils;
import eu.endercentral.crazy_advancements.NameKey;
import eu.endercentral.crazy_advancements.advancement.Advancement;
import eu.endercentral.crazy_advancements.advancement.AdvancementDisplay;
import eu.endercentral.crazy_advancements.advancement.AdvancementFlag;
import eu.endercentral.crazy_advancements.advancement.AdvancementVisibility;
import eu.endercentral.crazy_advancements.manager.AdvancementManager;
import eu.endercentral.crazy_advancements.packet.AdvancementsPacket;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;


public class FamilyTreeImpl implements FamilyTree {


    private static final List<String> advancements = new ArrayList<>();
    private static final List<NameKey> advancementNameKeys = new ArrayList<>();
    private static final Map<String, Advancement> advancementMap= new HashMap<>();

    @Override
    public boolean isFamilyTreeMapLoaded() {
        if (advancements.isEmpty() || advancementMap.isEmpty() || advancementNameKeys.isEmpty()) {
            Logger.debugLog("FamilyTreeMap is not loaded.");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean loadFamilyTreeMap(String JSONContent) {
        JSONArray jsonArray = new JSONArray(JSONContent);

        if (jsonArray.isEmpty()) {
            return false;
        }

        String title = "";
        String description = "";
        AdvancementDisplay.AdvancementFrame frame = AdvancementDisplay.AdvancementFrame.CHALLENGE;
        AdvancementVisibility visibility = AdvancementVisibility.ALWAYS;

        ItemStack icon = new ItemStack(Material.STONE);


        AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);
        display.setBackgroundTexture("moss_block");
        display.setX(13.5f);
        display.setY(7.5f);

        Advancement ego = new Advancement(new NameKey("family_tree", "ego"), display);
        advancementMap.put("ego", ego);
        advancementNameKeys.add(new NameKey("family_tree", "ego"));
        advancements.add("ego");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String parentKey = jsonObject.getString("parent");
            String name = jsonObject.getString("name");
            float x = (float) jsonObject.getDouble("x");
            float y = (float) jsonObject.getDouble("y");

            Advancement parent = advancementMap.get(parentKey);

            Advancement advancement = createAdvancement(parent, name, x, y);
            advancementMap.put(name, advancement);
            advancements.add(name);
            advancementNameKeys.add(new NameKey("family_tree", name));
        }
        return true;
    }

    private Advancement createAdvancement(Advancement parent, String relation, Float x, Float y) {
        AdvancementDisplay.AdvancementFrame frame = AdvancementDisplay.AdvancementFrame.GOAL;
        AdvancementVisibility visibility = AdvancementVisibility.ALWAYS;

        ItemStack icon = new ItemStack(Material.STONE);

        String title = relation;
        String description = relation;
        Advancement advancement;

        AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);
        display.setPositionOrigin(parent);
        display.setX(x);
        display.setY(y);

        if (relation.contains("holder")) {
            advancement = new Advancement(parent, new NameKey("family_tree", relation), display, AdvancementFlag.SEND_WITH_HIDDEN_BOOLEAN);
        } else {
            advancement = new Advancement(parent, new NameKey("family_tree", relation), display);
        }

        return advancement;
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

        AdvancementsPacket packet = new AdvancementsPacket(player, false, null, advancementNameKeys);
        packet.send();

        AdvancementManager manager = new AdvancementManager(new NameKey("manager", uuid.toString()));
        manager.addPlayer(player);

        for (String advancementKey : advancements) {
            String relation = advancementKey
                    .replace("_first_holder", "")
                    .replace("_second_holder", "")
                    .replace("_third_holder", "")
                    .replace("_holder", "");

            Advancement advancement = advancementMap.get(advancementKey);
            if (familyList.contains(relation)) {
                String relationLang = relationLangs.get(relation);
                String title = names.get(relation);
                ItemStack icon = ItemStackUtils.getSkullFromURL(skins.get(relation));

                advancement.getDisplay().setTitle(title);
                advancement.getDisplay().setDescription(relationLang);
                advancement.getDisplay().setIcon(icon);
                if (relation.equalsIgnoreCase("ego")) {
                    advancement.getDisplay().setBackgroundTexture(background);
                }

                Logger.debugLog("Adding advancement " + advancementKey + " to player " + player.getName());
                manager.addAdvancement(advancement);
            }
        }

        return true;
    }

    @Override
    public FamilyPlayerImpl getFamilyPlayer(int id) {
        return new FamilyPlayerImpl(id);
    }

    @Override
    public String getRelation(String relation, String key) {
        return LunaticFamily.getLanguageConfig().getRelation(relation, key);
    }
}
