package de.janschuri.lunaticFamily.external;

import com.google.common.collect.BiMap;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.PaperUtils;
import de.janschuri.lunaticFamily.utils.Utils;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class FamilyTree {


    private final BiMap<String, Integer> familyList;
    private static final List<String> advancements = new ArrayList<>();
    private static final List<NameKey> advancementNameKeys = new ArrayList<>();
    private final static Map<String, Advancement> advancementMap= new HashMap<>();

    public static void loadAdvancementMap(LunaticFamily plugin) {

        File json = new File(plugin.getDataFolder().getAbsolutePath() + "/familyTree.json");

        if (!json.exists()) {
            plugin.saveResource("familyTree.json", false);
        }


        String title = "";
        String description = "";
        AdvancementDisplay.AdvancementFrame frame = AdvancementDisplay.AdvancementFrame.CHALLENGE;
        AdvancementVisibility visibility = AdvancementVisibility.ALWAYS;

        ItemStack icon = new ItemStack(Material.STONE);
        String background = PluginConfig.defaultBackground;


        AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);
        display.setBackgroundTexture(background);
        display.setX(13.5f);
        display.setY(7.5f);

        Advancement ego = new Advancement(new NameKey("family_tree", "ego"), display);
        advancementMap.put("ego", ego);
        advancementNameKeys.add(new NameKey("family_tree", "ego"));
        advancements.add("ego");

        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get(plugin.getDataFolder().getAbsolutePath() + "/familyTree.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONArray jsonArray = new JSONArray(content);

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
    }

    private static Advancement createAdvancement(Advancement parent, String relation, Float x, Float y) {
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

    public FamilyTree(int id) {

        FamilyPlayer playerFam = new FamilyPlayer(id);
        this.familyList = playerFam.getFamilyList();
        this.familyList.put("ego", id);

        UUID uuid = Database.getDatabase().getUUID(id);
        Player player = Bukkit.getPlayer(uuid);

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

            String relationKey = relation
                    .replace("first_", "")
                    .replace("second_", "")
                    .replace("third_", "")
                    .replace("fourth_", "")
                    .replace("fifth_", "")
                    .replace("sixth_", "")
                    .replace("seventh__", "")
                    .replace("eighth_", "");

            Advancement advancement = advancementMap.get(advancementKey);
            if (familyList.containsKey(relation)) {
                FamilyPlayer relationFam = new FamilyPlayer(familyList.get(relation));
                advancement.getDisplay().setTitle(relationFam.getName());
                advancement.getDisplay().setDescription(Language.getRelation(relationKey, relationFam.getGender()));
                advancement.getDisplay().setIcon(PaperUtils.getSkull(relationFam.getSkinURL()));
                if (relation.equalsIgnoreCase("ego")) {
                    advancement.getDisplay().setBackgroundTexture(playerFam.getBackground());
                }
                manager.addAdvancement(advancement);
            }
        }
    }
}
