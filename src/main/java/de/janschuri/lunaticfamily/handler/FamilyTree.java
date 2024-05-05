package de.janschuri.lunaticfamily.handler;

import de.janschuri.lunaticfamily.LunaticFamily;
import de.janschuri.lunaticfamily.PaperLunaticFamily;
import de.janschuri.lunaticfamily.config.Language;
import de.janschuri.lunaticfamily.utils.Logger;
import de.janschuri.lunaticlib.utils.ItemStackUtils;
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


    private static final List<String> advancements = new ArrayList<>();
    private static final List<NameKey> advancementNameKeys = new ArrayList<>();
    private final static Map<String, Advancement> advancementMap= new HashMap<>();


    public static void loadAdvancementMap() {

        PaperLunaticFamily plugin = PaperLunaticFamily.getInstance();

        if (!LunaticFamily.installedCrazyAdvancementsAPI) {
            Logger.errorLog("CrazyAdvancementsAPI is not installed! Please install CrazyAdvancementsAPI or disable it in plugin config.yml.");
            return;
        }

        File json = new File(plugin.getDataFolder().getAbsolutePath() + "/familyTree.json");

        if (!json.exists()) {
            plugin.saveResource("familyTree.json", false);
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

    public static void updateFamilyTree(int id) {
        FamilyPlayer familyPlayer = new FamilyPlayer(id);
        UUID uuid = familyPlayer.getUniqueId();
        String background = familyPlayer.getBackground();
        Map<String, Integer> familyMap = familyPlayer.getFamilyMap();
        familyMap.put("ego", id);

        List<String> familyList = new ArrayList<>();
        Map<String, String> names = new HashMap<>();
        Map<String, String> skins = new HashMap<>();
        Map<String, String> relationLangs = new HashMap<>();

        for (Map.Entry<String, Integer> entry : familyMap.entrySet()) {
            FamilyPlayer relationFam = new FamilyPlayer(entry.getValue());
            String relationLang = Language.getRelation(entry.getKey(), relationFam.getGender());

            String skinURL = relationFam.getSkinURL();

            familyList.add(entry.getKey());
            names.put(entry.getKey(), relationFam.getName());
            skins.put(entry.getKey(), skinURL);
            relationLangs.put(entry.getKey(), relationLang);
        }

        updateFamilyTree(uuid, background, familyList, names, skins, relationLangs);
    }

    public static void updateFamilyTree(UUID uuid, String background, List<String> familyList, Map<String, String> names, Map<String, String> skins, Map<String, String> relationLangs) {


        Logger.debugLog("Creating FamilyTree for " + uuid);
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
                manager.addAdvancement(advancement);
            }
        }
    }
}
