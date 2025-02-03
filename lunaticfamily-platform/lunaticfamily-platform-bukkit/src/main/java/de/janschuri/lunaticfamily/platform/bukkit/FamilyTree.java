package de.janschuri.lunaticfamily.platform.bukkit;

import de.janschuri.lunaticfamily.common.utils.Logger;
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

public class FamilyTree {

    private static final List<String> advancements = new ArrayList<>();
    private static final List<NameKey> advancementNameKeys = new ArrayList<>();
    private static final Map<String, Advancement> advancementMap= new HashMap<>();

    private UUID uuid;
    private String background;
    private TreeAdvancement.RootTreeAdvancement root;
    private Map<String, TreeAdvancement> treeAdvancements = new HashMap<>();
    private Player player;
    private AdvancementManager manager;

    public FamilyTree(UUID uuid, String background, TreeAdvancement.RootTreeAdvancement root) {
        this.uuid = uuid;
        this.background = background;
        this.root = root;
        this.player = Bukkit.getPlayer(uuid);

        AdvancementsPacket packet = new AdvancementsPacket(player, false, null, advancementNameKeys);
        packet.send();

        this.manager = new AdvancementManager(new NameKey("manager", uuid.toString()));

        manager.addPlayer(player);

        Advancement ego = advancementMap.get("ego");
        ego.getDisplay().setBackgroundTexture(background);
        ego.getDisplay().setIcon(root.getIcon());
        ego.getDisplay().setTitle(root.getTitle());
        ego.getDisplay().setDescription(root.getDescription());

        manager.addAdvancement(ego);

    }

    public static boolean isLoaded() {
        if (advancementMap.isEmpty() || advancementNameKeys.isEmpty()) {
            Logger.debugLog("FamilyTreeMap is not loaded.");
            return false;
        } else {
            return true;
        }
    }

    public static List<String> getAdvancements() {
        return advancements;
    }

    public Player getPlayer() {
        return player;
    }

    public FamilyTree addTreeAdvancements(TreeAdvancement... treeAdvancements) {
        Arrays.stream(treeAdvancements).forEach(treeAdvancement -> this.treeAdvancements.put(treeAdvancement.getKey(), treeAdvancement));

        for (TreeAdvancement treeAdvancement : treeAdvancements) {
            Advancement advancement = advancementMap.get(treeAdvancement.getKey());
            AdvancementDisplay display = advancement.getDisplay();
            display.setTitle(treeAdvancement.getTitle());
            display.setDescription(treeAdvancement.getDescription());
            display.setIcon(treeAdvancement.getIcon());

            manager.addAdvancement(advancement);
        }

        return this;
    }

    public static boolean load(JSONArray jsonArray) {
        String title = "";
        String description = "";
        AdvancementDisplay.AdvancementFrame frame = AdvancementDisplay.AdvancementFrame.CHALLENGE;
        AdvancementVisibility visibility = AdvancementVisibility.ALWAYS;

        ItemStack icon = new ItemStack(Material.STONE);


        AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);
        display.setBackgroundTexture("moss_block");
        display.setX(13.5f);
        display.setY(7.5f);

        eu.endercentral.crazy_advancements.advancement.Advancement ego = new eu.endercentral.crazy_advancements.advancement.Advancement(new NameKey("family_tree", "ego"), display);
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

            Advancement treeAdvancement = createAdvancement(parent, name, x, y);
            advancementMap.put(name, treeAdvancement);
            advancements.add(name);
            advancementNameKeys.add(new NameKey("family_tree", name));
        }

        return true;
    }

    private static Advancement createAdvancement(Advancement parent, String relation, Float x, Float y) {
        AdvancementDisplay.AdvancementFrame frame = AdvancementDisplay.AdvancementFrame.GOAL;
        AdvancementVisibility visibility = AdvancementVisibility.ALWAYS;

        ItemStack icon = new ItemStack(Material.STONE);

        String title = relation;
        String description = relation;
        eu.endercentral.crazy_advancements.advancement.Advancement advancement;

        AdvancementDisplay display = new AdvancementDisplay(icon, title, description, frame, visibility);
        display.setPositionOrigin(parent);
        display.setX(x);
        display.setY(y);

        if (relation.contains("holder")) {
            advancement = new eu.endercentral.crazy_advancements.advancement.Advancement(parent, new NameKey("family_tree", relation), display, AdvancementFlag.SEND_WITH_HIDDEN_BOOLEAN);
        } else {
            advancement = new eu.endercentral.crazy_advancements.advancement.Advancement(parent, new NameKey("family_tree", relation), display);
        }

        return advancement;
    }
}
