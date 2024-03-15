package de.janschuri.lunaticFamily;

import de.janschuri.lunaticFamily.commands.*;
import de.janschuri.lunaticFamily.utils.Vault;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
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
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;


public final class Main extends JavaPlugin {
    private static Database db;
    private static FileConfiguration config;
    private static FileConfiguration lang;
    public static String language;
    public static String prefix;
    public static String defaultGender;
    public static String defaultBackground;
    public static boolean allowSingleAdopt;
    public static boolean enabledCrazyAdvancementAPI;
    public static boolean enabledVault;
    public static boolean enabledMinepacks;
    public static boolean marryBackpackOffline;
    private static Map<String, String> messages = new HashMap<>();
    private static Map<String, String> genderLang = new HashMap<>();
    private static final Map<String, Map<String, List<String>>> aliases = new HashMap<>();
    public static Map<String, Double> commandWithdraws = new HashMap<>();
    public static List<String> familyList;
    public static List<String> backgrounds;
    public static List<String> genders = new ArrayList<>();

    private static Map<String, Map<String, String>> relationships = new HashMap<>();

    public static List<String> familyCommands = new ArrayList<>();
    public static List<String> familySubcommands = new ArrayList<>();
    public static List<String> genderCommands = new ArrayList<>();
    public static List<String> genderSubcommands = new ArrayList<>();
    public static List<String> genderAdminSubcommands = new ArrayList<>();
    public static List<String> familyAdminSubcommands = new ArrayList<>();
    public static List<String> adoptCommands = new ArrayList<>();
    public static List<String> adoptSubcommands = new ArrayList<>();
    public static List<String> adoptAdminSubcommands = new ArrayList<>();
    public static List<String> marryCommands = new ArrayList<>();
    public static List<String> marrySubcommands = new ArrayList<>();
    public static List<String> marryPriestSubcommands = new ArrayList<>();
    public static List<String> marryAdminSubcommands = new ArrayList<>();
    public static List<String> siblingCommands = new ArrayList<>();
    public static List<String> siblingSubcommands = new ArrayList<>();
    public static List<String> siblingAdminSubcommands = new ArrayList<>();


    public static BiMap<String, String> marryRequests = HashBiMap.create();
    public static BiMap<String, String> marryPriestRequests = HashBiMap.create();
    public static BiMap<String, String> marryPriest = HashBiMap.create();
    public static BiMap<String, String> adoptRequests = HashBiMap.create();
    public static BiMap<String, String> siblingRequests = HashBiMap.create();

    @Override
    public void onEnable() {

        saveDefaultConfig();

        loadConfig(this);

        checkSoftDepends();

        new Vault();

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
        getServer().getPluginManager().registerEvents(new QuitListener(), this);

        getCommand("family").setExecutor(new FamilyCommand(this));
        getCommand("family").setTabCompleter(new FamilyCommand(this));

        getCommand("adopt").setExecutor(new AdoptCommand(this));
        getCommand("adopt").setTabCompleter(new AdoptCommand(this));

        getCommand("gender").setExecutor(new GenderCommand());
        getCommand("gender").setTabCompleter(new GenderCommand());

        getCommand("marry").setExecutor(new MarryCommand(this));
        getCommand("marry").setTabCompleter(new MarryCommand(this));

        getCommand("sibling").setExecutor(new SiblingCommand(this));
        getCommand("sibling").setTabCompleter(new SiblingCommand(this));


    }

    public void loadConfig(Plugin plugin) {

        File cfgfile = new File(plugin.getDataFolder().getAbsolutePath() + "/config.yml");
        plugin.saveResource("defaultConfig.yml", true);

        if (!cfgfile.exists()) {
            plugin.saveResource("/config.yml", false);
            addMissingProperties(cfgfile, "defaultConfig.yml");
        } else {
            addMissingProperties(cfgfile, "defaultConfig.yml");
        }

        config = YamlConfiguration.loadConfiguration(cfgfile);


        plugin.saveResource("lang/EN.yml", true);
        plugin.saveResource("lang/DE.yml", true);

        language = config.getString("language");

        File langfile = new File(plugin.getDataFolder().getAbsolutePath() + "/lang.yml");

        if (!langfile.exists()) {
            plugin.saveResource("lang.yml", false);
            addMissingProperties(langfile, "/lang/" + language + ".yml");
        } else {
            addMissingProperties(langfile, "/lang/" + language + ".yml");
        }

        lang = YamlConfiguration.loadConfiguration(langfile);
        allowSingleAdopt = config.getBoolean("allow_single_adopt");
        marryBackpackOffline = config.getBoolean("marry_backpack_offline_access");
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
        familySubcommands.addAll(getAliases("family", "help"));
        familyAdminSubcommands.addAll(getAliases("family", "reload"));

        genderCommands = getAliases("gender");
        genderSubcommands.addAll(getAliases("gender", "info"));
        genderSubcommands.addAll(getAliases("gender", "set"));
        genderSubcommands.addAll(getAliases("gender", "help"));

        marryCommands = getAliases("marry");
        marrySubcommands.addAll(getAliases("marry", "propose"));
        marrySubcommands.addAll(getAliases("marry", "accept"));
        marrySubcommands.addAll(getAliases("marry", "deny"));
        marrySubcommands.addAll(getAliases("marry", "divorce"));
        marrySubcommands.addAll(getAliases("marry", "list"));
        marrySubcommands.addAll(getAliases("marry", "kiss"));
        marrySubcommands.addAll(getAliases("marry", "gift"));
        marrySubcommands.addAll(getAliases("marry", "backpack"));
        marrySubcommands.addAll(getAliases("marry", "help"));
        marryAdminSubcommands.addAll(getAliases("marry", "set"));
        marryAdminSubcommands.addAll(getAliases("marry", "unset"));

        adoptCommands = getAliases("adopt");
        adoptSubcommands.addAll(getAliases("adopt", "propose"));
        adoptSubcommands.addAll(getAliases("adopt", "accept"));
        adoptSubcommands.addAll(getAliases("adopt", "deny"));
        adoptSubcommands.addAll(getAliases("adopt", "kickout"));
        adoptSubcommands.addAll(getAliases("adopt", "moveout"));
        adoptSubcommands.addAll(getAliases("adopt", "list"));
        adoptSubcommands.addAll(getAliases("adopt", "help"));
        adoptAdminSubcommands.addAll(getAliases("adopt", "set"));
        adoptAdminSubcommands.addAll(getAliases("adopt", "unset"));

        siblingCommands = getAliases("sibling");
        siblingSubcommands.addAll(getAliases("sibling", "propose"));
        siblingSubcommands.addAll(getAliases("sibling", "accept"));
        siblingSubcommands.addAll(getAliases("sibling", "deny"));
        siblingSubcommands.addAll(getAliases("sibling", "unsibling"));
        siblingSubcommands.addAll(getAliases("sibling", "help"));
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
                messages.put(key, ChatColor.translateAlternateColorCodes('&', messagesSection.getString(key, key)));
            }
        } else {
            getLogger().warning("Could not find 'messages' section in lang.yml");
        }

        //command withdraws
        ConfigurationSection withdrawsSection = config.getConfigurationSection("command_withdraws");
        if (withdrawsSection != null) {
            for (String key : withdrawsSection.getKeys(false)) {
                commandWithdraws.put(key, withdrawsSection.getDouble(key, 0.0));
            }
        } else {
            getLogger().warning("Could not find 'command_withdraw' section in config.yml");
        }

        //genders
        ConfigurationSection gendersSection = lang.getConfigurationSection("genders");
        if (gendersSection != null) {
            for (String key : gendersSection.getKeys(false)) {
                genderLang.put(key, ChatColor.translateAlternateColorCodes('&', gendersSection.getString(key, key)));
            }
        } else {
            getLogger().warning("Could not find 'genders' section in lang.yml");
        }

        familyList = Objects.requireNonNull(config.getStringList("family_list"));
        backgrounds = Objects.requireNonNull(config.getStringList("backgrounds"));

        //family relationships
        ConfigurationSection familyRelationships = lang.getConfigurationSection("family_relationships");
        genders = new ArrayList<>(familyRelationships.getKeys(false));

            for (String gender : genders) {
                Map<String, String> map = new HashMap<>();

                ConfigurationSection relations = lang.getConfigurationSection("family_relationships." + gender);

                for (String key : relations.getKeys(false)) {


                    map.put(key, ChatColor.translateAlternateColorCodes('&', relations.getString(key)));
                }

                relationships.put(gender, map);
}
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Database getDatabase() {
        return Main.db;
    }

    public static String getMessage(String key) {

        if (messages.containsKey(key.toLowerCase())) {
            return messages.get(key);
        } else {
            return "Message '" + key.toLowerCase() + "' not found!";
        }
    }
    public static String getGenderLang(String key) {

        if (genderLang.containsKey(key)) {
            return genderLang.get(key.toLowerCase());
        } else {
            return "undefined";
        }
    }

    private void addMissingProperties(File file, String filePath) {
        YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(file);
        YamlConfiguration defaultLangConfig = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), filePath)); // or "DE.yml" for German

        for (String key : defaultLangConfig.getKeys(true)) {
            if (!langConfig.contains(key)) {
                langConfig.set(key, defaultLangConfig.get(key));
            }
        }

        try {
            langConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ItemStack getSkull(String url) {

        PlayerProfile pProfile = getProfile(url);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwnerProfile(pProfile);
        head.setItemMeta(meta);

        return head;
    }

    public static List<String> getAliases(String command, String subcommand) {
        Map<String, List<String>> commandAliases = Main.aliases.getOrDefault(command, new HashMap<>());

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

    public static List<String> getAliases(String command) {
        Map<String, List<String>> commandAliases = Main.aliases.getOrDefault(command, new HashMap<>());

        List<String> list = commandAliases.getOrDefault("base_command", new ArrayList<>());
        if (list.isEmpty()) {
            list.add(command);
        }
        return list;
    }

    public static String getRelation(String relation, String gender) {
        if (genders.contains(gender)) {
            Map<String, String> relations = relationships.get(gender);
            if (relations.get(relation) != null) {
                return relations.get(relation);
            } else {
                return "undefined";
            }
        } else {
            gender = genders.get(0);
            Map<String, String> relations = relationships.get(gender);
            if (relations.get(relation) != null) {
                return relations.get(relation);
            } else {
                return "undefined";
            }
        }
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

    public static void spawnParticles(Location location, Particle particle) {
        World world = location.getWorld();

        Random random = new Random();

        double range = 2.0;

        for (int i = 0; i < 10; i++) {
            double offsetX = (random.nextDouble() - 0.5) * range * 2;
            double offsetY = (random.nextDouble() - 0.5) * range * 2;
            double offsetZ = (random.nextDouble() - 0.5) * range * 2;

            Location particleLocation = location.clone().add(offsetX, offsetY, offsetZ);

            world.spawnParticle(particle, particleLocation, 1);
        }
    }
    public static Location getPositionBetweenLocations(Location loc1, Location loc2) {
        Vector vec1 = new Vector(loc1.getX(), loc1.getY(), loc1.getZ());
        Vector vec2 = new Vector(loc2.getX(), loc2.getY(), loc2.getZ());

        Vector midpoint = vec1.clone().add(vec2).multiply(0.5);

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



    public static boolean isInRange(Location firstLocation, Location secondLocation, double range) {

        double distance = firstLocation.distance(secondLocation);

        return distance <= range;
    }

    public static void checkSoftDepends(){
        try {
            Class.forName("eu.endercentral.crazy_advancements.CrazyAdvancementsAPI");
            enabledCrazyAdvancementAPI = true;
        } catch(ClassNotFoundException e) {
            Bukkit.getLogger().warning("Could not find CrazyAdvancementsAPI.");
        }

        try {
            Class.forName("net.milkbowl.vault.economy.Economy");
            enabledVault = true;
        } catch(ClassNotFoundException e) {
            Bukkit.getLogger().warning("Could not find Vault.");
        }

        try {
            Class.forName("at.pcgamingfreaks.Minepacks.Bukkit.API.MinepacksPlugin");
            enabledMinepacks = true;
        } catch(ClassNotFoundException e) {
            Bukkit.getLogger().warning("Could not find Minepacks.");
        }


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

    public static TextComponent createClickableMessage(String message, String confirmHoverText, String confirmCommand, String cancelHoverText, String cancelCommand) {
        TextComponent confirm = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD +" ✓");
        confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, confirmCommand));
        confirm.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(confirmHoverText).create()));


        TextComponent cancel = new TextComponent(ChatColor.RED +  " ❌");
        cancel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cancelCommand));
        cancel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(cancelHoverText).create()));

        TextComponent prefix = new TextComponent(Main.prefix);
        TextComponent msg = new TextComponent(message);


        return new TextComponent(prefix, msg, confirm, cancel);
    }

    public static String getName(String name) {
        if (Bukkit.getOfflinePlayer(name).getName() != null) {
            return Bukkit.getOfflinePlayer(name).getName();
        } else {
            return name;
        }
    }

    public static String getUUID(String name) {
        return Bukkit.getOfflinePlayer(name).getUniqueId().toString();
    }
}
