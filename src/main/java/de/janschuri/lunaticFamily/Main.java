package de.janschuri.lunaticFamily;

import at.pcgamingfreaks.Minepacks.Bukkit.API.Backpack;
import at.pcgamingfreaks.Minepacks.Bukkit.API.MinepacksPlugin;
import de.janschuri.lunaticFamily.commands.*;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.profile.PlayerProfile;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.database.MySQL;
import de.janschuri.lunaticFamily.database.SQLite;
import de.janschuri.lunaticFamily.utils.JoinListener;
import de.janschuri.lunaticFamily.utils.QuitListener;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.Vector;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

//TODO hook into Vault

public final class Main extends JavaPlugin {
    private static Database db;
    private static FileConfiguration config;
    private static FileConfiguration lang;
    public String language;
    public String prefix;
    public String defaultGender;
    public String defaultBackground;
    public boolean allowSingleAdopt;

    public Map<String, String> messages = new HashMap<>();

    public Map<String, Map> aliases = new HashMap<>();
    public List<String> familyList;
    public List<String> backgrounds;

    public Map<String, Map<String, String>> relationships = new HashMap<>();

    public List<String> familyCommands = new ArrayList<>();
    public List<String> familySubcommands = new ArrayList<>();
    public List<String> genderCommands = new ArrayList<>();
    public List<String> genderSubcommands = new ArrayList<>();
    public List<String> genderAdminSubcommands = new ArrayList<>();
    public List<String> familyAdminSubcommands = new ArrayList<>();
    public List<String> adoptCommands = new ArrayList<>();
    public List<String> adoptSubcommands = new ArrayList<>();
    public List<String> adoptAdminSubcommands = new ArrayList<>();
    public List<String> marryCommands = new ArrayList<>();
    public List<String> marrySubcommands = new ArrayList<>();
    public List<String> marryPriestSubcommands = new ArrayList<>();
    public List<String> marryAdminSubcommands = new ArrayList<>();
    public List<String> siblingCommands = new ArrayList<>();
    public List<String> siblingSubcommands = new ArrayList<>();
    public List<String> siblingAdminSubcommands = new ArrayList<>();


    public BiMap<String, String> marryRequests = HashBiMap.create();
    public BiMap<String, String> marryPriestRequests = HashBiMap.create();
    public BiMap<String, String> marryPriest = HashBiMap.create();
    public BiMap<String, String> adoptRequests = HashBiMap.create();
    public BiMap<String, String> siblingRequests = HashBiMap.create();

    @Override
    public void onEnable() {

        saveDefaultConfig();

        loadConfig(this);

        isCrazyAdvancementAPILoaded();

        if (config.getBoolean("Database.MySQL.enabled")) {
            db = new MySQL(this);
            if (db.getSQLConnection() == null) {
                Bukkit.getLogger().log(Level.SEVERE, "Error initializing MySQL database");
                Bukkit.getLogger().info("Falling back to SQLite due to initialization error");

                db = new SQLite(this);

            }
        }
        else {
            db = new SQLite(this);
        }

        db.load();

        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new QuitListener(this), this);

        getCommand("family").setExecutor(new FamilyCommand(this));
        getCommand("family").setTabCompleter(new FamilyCommand(this));

        getCommand("adopt").setExecutor(new AdoptCommand(this));
        getCommand("adopt").setTabCompleter(new AdoptCommand(this));

        getCommand("gender").setExecutor(new GenderCommand(this));
        getCommand("gender").setTabCompleter(new GenderCommand(this));

        getCommand("marry").setExecutor(new MarryCommand(this));
        getCommand("marry").setTabCompleter(new MarryCommand(this));

        getCommand("sibling").setExecutor(new SiblingCommand(this));
        getCommand("sibling").setTabCompleter(new SiblingCommand(this));


    }

    public void loadConfig(Plugin plugin) {

        File cfgfile = new File(plugin.getDataFolder().getAbsolutePath() + "/config.yml");
        config = YamlConfiguration.loadConfiguration(cfgfile);

        File langfileEN = new File(plugin.getDataFolder().getAbsolutePath() + "/langEN.yml");
        File langfileDE = new File(plugin.getDataFolder().getAbsolutePath() + "/langDE.yml");


        if (!langfileEN.exists()) {
            langfileEN.getParentFile().mkdirs();
            plugin.saveResource("langEN.yml", false);
        }

        if (!langfileDE.exists()) {
            langfileDE.getParentFile().mkdirs();
            plugin.saveResource("langDE.yml", false);
        }

        language = config.getString("language");
        allowSingleAdopt = config.getBoolean("allow_single_adopt");

        if (language.equalsIgnoreCase("EN"))
        {
            lang = YamlConfiguration.loadConfiguration(langfileEN);
        }

        if (language.equalsIgnoreCase("DE"))
        {
            lang = YamlConfiguration.loadConfiguration(langfileDE);
        }

        defaultBackground = "textures/block/" + config.getString("default_background") + ".png";
        defaultGender = config.getString("default_gender");
        prefix = ChatColor.translateAlternateColorCodes('&', lang.getString("prefix"));

        List<String> commands = Arrays.asList("family", "marry", "sibling", "adopt", "gender");

        for (String command : commands) {
            Map<String, List<String>> map = new HashMap<>();
            ConfigurationSection section = lang.getConfigurationSection("aliases." + command);
            if (section != null) {
                for (String key : section.getKeys(false)) {
                    List<String> list = section.getStringList(key);
                    map.put(key, list);
                }
            } else {
                getLogger().warning("Could not find 'aliases." + command + "' section in lang.yml");
            }
            aliases.put(command, map);
        }

        familyCommands = getAliases("family");
        familySubcommands.addAll(getAliases("family", "tree"));
        familySubcommands.addAll(getAliases("family", "list"));
        familySubcommands.addAll(getAliases("family", "background"));
        familyAdminSubcommands.addAll(getAliases("family", "reload"));

        genderCommands = getAliases("gender");
        genderSubcommands.addAll(getAliases("gender", "fe"));
        genderSubcommands.addAll(getAliases("gender", "ma"));
        genderAdminSubcommands.addAll(getAliases("gender", "set"));

        marryCommands = getAliases("marry");
        marrySubcommands.addAll(getAliases("marry", "propose"));
        marrySubcommands.addAll(getAliases("marry", "accept"));
        marrySubcommands.addAll(getAliases("marry", "deny"));
        marrySubcommands.addAll(getAliases("marry", "divorce"));
        marrySubcommands.addAll(getAliases("marry", "list"));
        marrySubcommands.addAll(getAliases("marry", "kiss"));
        marrySubcommands.addAll(getAliases("marry", "gift"));
        marrySubcommands.addAll(getAliases("marry", "backpack"));
        marryAdminSubcommands.addAll(getAliases("marry", "set"));
        marryAdminSubcommands.addAll(getAliases("marry", "unset"));

        adoptCommands = getAliases("adopt");
        adoptSubcommands.addAll(getAliases("adopt", "propose"));
        adoptSubcommands.addAll(getAliases("adopt", "accept"));
        adoptSubcommands.addAll(getAliases("adopt", "deny"));
        adoptSubcommands.addAll(getAliases("adopt", "kickout"));
        adoptSubcommands.addAll(getAliases("adopt", "moveout"));
        adoptSubcommands.addAll(getAliases("adopt", "list"));
        adoptAdminSubcommands.addAll(getAliases("adopt", "set"));
        adoptAdminSubcommands.addAll(getAliases("adopt", "unset"));

        siblingCommands = getAliases("sibling");
        siblingSubcommands.addAll(getAliases("sibling", "propose"));
        siblingSubcommands.addAll(getAliases("sibling", "accept"));
        siblingSubcommands.addAll(getAliases("sibling", "deny"));
        siblingSubcommands.addAll(getAliases("sibling", "unsibling"));
        siblingAdminSubcommands.addAll(getAliases("sibling", "set"));
        siblingAdminSubcommands.addAll(getAliases("sibling", "unset"));

        registerCommand("family");
        registerCommand("marry");
        registerCommand("adopt");
        registerCommand("sibling");
        registerCommand("gender");

        //messages
        ConfigurationSection messagesSection = lang.getConfigurationSection("messages");
        if (messagesSection != null) {
            for (String key : messagesSection.getKeys(false)) {
                messages.put(key, ChatColor.translateAlternateColorCodes('&', messagesSection.getString(key)));
            }
        } else {
            getLogger().warning("Could not find 'messages' section in lang.yml");
        }

        familyList = Objects.requireNonNull(config.getStringList("family_list"));
        backgrounds = Objects.requireNonNull(config.getStringList("backgrounds"));

        //family relationships
        ConfigurationSection familyRelationships = lang.getConfigurationSection("family_relationships");
        Set<String> genders = familyRelationships.getKeys(false);
        Bukkit.getLogger().info(String.valueOf(genders));

        for (String gender : genders) {
            Map<String, String> map = new HashMap<>();
            map.put("ego", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".ego")));
            map.put("partner", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".partner")));
            map.put("child", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".child")));
            map.put("parent", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".parent")));
            map.put("sibling", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".sibling")));
            map.put("grandparent", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".grandparent")));
            map.put("great_grandparent", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".great_grandparent")));
            map.put("grandchild", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".grandchild")));
            map.put("great_grandchild", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".great_grandchild")));
            map.put("aunt_or_uncle", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".aunt_or_uncle")));
            map.put("niece_or_nephew", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".niece_or_nephew")));
            map.put("cousin", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".cousin")));
            map.put("great_aunt_or_uncle", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".great_aunt_or_uncle")));
            map.put("parent_in_law", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".parent_in_law")));
            map.put("sibling_in_law", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".sibling_in_law")));
            map.put("child_in_law", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".child_in_law")));
            map.put("grandchild_in_law", ChatColor.translateAlternateColorCodes('&', lang.getString("family_relationships." + gender + ".grandchild_in_law")));
            relationships.put(gender, map);
        }

        Bukkit.getLogger().info(String.valueOf(relationships.get("ma").get("parent")));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Database getDatabase() {
        return Main.db;

    }

    public static ItemStack getSkull(String url) {

        PlayerProfile pProfile = getProfile(url);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwnerProfile(pProfile);
        head.setItemMeta(meta);

        return head;
    }

    public List<String> getAliases(String command, String subcommand) {
        Map<String, List<String>> commandAliases = this.aliases.getOrDefault(command, new HashMap<>());

        List<String> list = commandAliases.getOrDefault(subcommand, new ArrayList<>());
        if (list.isEmpty()) {
            if (subcommand.equalsIgnoreCase("base_command")) {
                list.add(command);
            } else {
                list.add(subcommand);
            }
        }
        return list;
    }

    public List<String> getAliases(String command) {
        Map<String, List<String>> commandAliases = this.aliases.getOrDefault(command, new HashMap<>());

        List<String> list = commandAliases.getOrDefault("base_command", new ArrayList<>());
        if (list.isEmpty()) {
            list.add(command);
        }
        return list;
    }

    private static PlayerProfile getProfile(String url) {
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = new URL(url);
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject);
        profile.setTextures(textures);
        return profile;
    }

    // Method to spawn particles
    public static void spawnParticles(Location location, Particle particle) {
        World world = location.getWorld();

        Random random = new Random();

        // Define the range for random offsets
        double range = 2.0;

        // Spawn particles in a cloud-like pattern around the player
        for (int i = 0; i < 10; i++) { // Adjust the number of particles as needed
            // Generate random offsets
            double offsetX = (random.nextDouble() - 0.5) * range * 2;
            double offsetY = (random.nextDouble() - 0.5) * range * 2;
            double offsetZ = (random.nextDouble() - 0.5) * range * 2;

            // Adjust particle location
            Location particleLocation = location.clone().add(offsetX, offsetY, offsetZ);

            // Spawn particles
            world.spawnParticle(particle, particleLocation, 1);
        }
    }
    public static Location getPositionBetweenLocations(Location loc1, Location loc2) {
        // Get the vectors of the two locations
        Vector vec1 = new Vector(loc1.getX(), loc1.getY(), loc1.getZ());
        Vector vec2 = new Vector(loc2.getX(), loc2.getY(), loc2.getZ());

        // Calculate the midpoint vector
        Vector midpoint = vec1.clone().add(vec2).multiply(0.5);

        // Convert the midpoint vector to a location
        Location position = new Location(loc1.getWorld(), midpoint.getX(), midpoint.getY(), midpoint.getZ());

        return position;
    }

    public static String getKey(Material material){
        if(material.isBlock()){
            String id = material.getKey().getKey();

            return "block.minecraft."+id;
        } else if(material.isItem()){
            String id = material.getKey().getKey();

            return "item.minecraft."+id;
        }
        return "block.minecraft.dirt";
    }



    // Method to check if a player is within a certain range of blocks from a location
    public static boolean isInRange(Location firstLocation, Location secondLocation, double range) {

        // Calculate the distance between player's location and target location
        double distance = firstLocation.distance(secondLocation);

        // Check if the distance is within the range
        return distance <= range;
    }

    public static boolean isCrazyAdvancementAPILoaded(){
        Boolean isLoaded = false;
        try {
            Class.forName("eu.endercentral.crazy_advancements.CrazyAdvancementsAPI");
            Bukkit.getLogger().info("CrazyAdvancementAPI loaded.");
            isLoaded = true;
        } catch(ClassNotFoundException e) {
            Bukkit.getLogger().warning("Could not find CrazyAdvancementsAPI.");
        }

        return isLoaded;
    }

    public static boolean playerExists(String name) {
        for(OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if(player.getName().equals(name)) return true;
        }

        String uuid = Bukkit.getOfflinePlayer(name).getUniqueId().toString();

        if (db.getID(uuid) != 0) {
            return true;
        }
        return false;
    }

    private void registerCommand (String command) {

        PluginCommand cmd = getCommand(command);

        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            List<String> list = this.getAliases(command);

            list.forEach(alias -> {
                commandMap.register(alias, "lunaticFamily", cmd);
            });
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
