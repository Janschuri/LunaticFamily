package de.janschuri.lunaticfamily;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.janschuri.lunaticfamily.commands.AdoptCommand;
import de.janschuri.lunaticfamily.commands.FamilyCommand;
import de.janschuri.lunaticfamily.commands.MarryCommand;
import de.janschuri.lunaticfamily.database.Database;
import de.janschuri.lunaticfamily.database.MySQL;
import de.janschuri.lunaticfamily.database.SQLite;
import de.janschuri.lunaticfamily.utils.JoinListener;
import de.janschuri.lunaticfamily.utils.QuitListener;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public final class Main extends JavaPlugin {
    private static Database db;

    public String prefix;
    public String defaultGender;

    public Map<String, String> messages = new HashMap<>();
    public List<String> familyList;
    public Map relationshipsFe = new HashMap<>();
    public Map relationshipsMa = new HashMap<>();


    //family relationships
    public String ego;

    public BiMap<String, String> marryRequests = HashBiMap.create();
    public BiMap<String, String> marryPriestRequests = HashBiMap.create();
    public BiMap<String, String> marryPriest = HashBiMap.create();
    public BiMap<String, String> adoptRequests = HashBiMap.create();

    @Override
    public void onEnable() {


//        if (getConfig().getBoolean("database.MySQL.enabled")) {
        db = new MySQL(this);
//        Bukkit.getLogger().info("test");
//
//        }
//        else {
//            db = new SQLite(this);
//        }

        db.load();

        saveDefaultConfig();

        loadConfig(this);

        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new QuitListener(this), this);

        getCommand("family").setExecutor(new FamilyCommand(this));
        getCommand("family").setTabCompleter(new FamilyCommand(this));

        getCommand("marry").setExecutor(new MarryCommand(this));
        getCommand("marry").setTabCompleter(new MarryCommand(this));

        getCommand("adopt").setExecutor(new AdoptCommand(this));
        getCommand("adopt").setTabCompleter(new AdoptCommand(this));

    }

    public void loadConfig(Plugin plugin) {
        File cfgfile = new File(plugin.getDataFolder().getAbsolutePath() + "/config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(cfgfile);

        File langfile = new File(plugin.getDataFolder().getAbsolutePath() + "/lang.yml");

        FileConfiguration lang;

        if (!langfile.exists()) {
            langfile.getParentFile().mkdirs();
            plugin.saveResource("lang.yml", false);
        }

        lang = YamlConfiguration.loadConfiguration(langfile);

        defaultGender = config.getString("default_gender");
        prefix = ChatColor.translateAlternateColorCodes('&', lang.getString("prefix"));

        //messages
        ConfigurationSection messagesSection = lang.getConfigurationSection("messages");
        if (messagesSection != null) {
            for (String key : messagesSection.getKeys(false)) {
                messages.put(key, ChatColor.translateAlternateColorCodes('&', messagesSection.getString(key)));
            }
        } else {
            getLogger().warning("Could not find 'messages' section in config.yml");
        }

        familyList = Objects.requireNonNull(config.getStringList("family"));

        //family relationships
        ego = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(lang.getString("family_relationships.ego")));

        relationshipsFe.put("partner", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.wife")));
        relationshipsFe.put("Child", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.daughter")));
        relationshipsFe.put("Parent", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.mother")));
        relationshipsFe.put("sibling", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.sister")));
        relationshipsFe.put("Grandparent", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.grandmother")));
        relationshipsFe.put("GreatGrandparent", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.great_grandmother")));
        relationshipsFe.put("Grandchild", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.granddaughter")));
        relationshipsFe.put("GreatGrandchild", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.great_granddaughter")));
        relationshipsFe.put("AuntOrUncle", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.aunt")));
        relationshipsFe.put("NieceOrNephew", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.niece")));
        relationshipsFe.put("Cousin", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.cousin_fe")));
        relationshipsFe.put("GreatAuntOrUncle", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.great_aunt")));
        relationshipsFe.put("ParentInLaw", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.mother_in_law")));
        relationshipsFe.put("SiblingInLaw", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.sister_in_law")));
        relationshipsFe.put("ChildInLaw", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.daughter_in_law")));
        relationshipsFe.put("GrandchildInLaw", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.granddaughter_in_law")));


        relationshipsMa.put("partner", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.husband")));
        relationshipsMa.put("Child", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.son")));
        relationshipsMa.put("Parent", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.father")));
        relationshipsMa.put("sibling", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.brother")));
        relationshipsMa.put("Grandparent", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.grandfather")));
        relationshipsMa.put("GreatGrandparent", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.great_grandfather")));
        relationshipsMa.put("Grandchild", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.granddaughter")));
        relationshipsMa.put("GreatGrandchild", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.great_grandson")));
        relationshipsMa.put("AuntOrUncle", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.uncle")));
        relationshipsMa.put("NieceOrNephew", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.nephew")));
        relationshipsMa.put("Cousin", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.cousin_ma")));
        relationshipsMa.put("GreatAuntOrUncle", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.great_uncle")));
        relationshipsMa.put("ParentInLaw", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.father_in_law")));
        relationshipsMa.put("SiblingInLaw", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.brother_in_law")));
        relationshipsMa.put("ChildInLaw", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.son_in_law")));
        relationshipsMa.put("GrandchildInLaw", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships.grandson_in_law")));


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Database getDatabase() {
        return Main.db;

    }

    public static ItemStack getSkull(String url) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        if (url == null || url.isEmpty())
            return skull;
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField.setAccessible(true);
        try {
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        skull.setItemMeta(skullMeta);
        return skull;
    }


}
