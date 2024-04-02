package de.janschuri.lunaticFamily;

import de.janschuri.lunaticFamily.commands.*;
import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.external.Vault;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.database.MySQL;
import de.janschuri.lunaticFamily.database.SQLite;
import de.janschuri.lunaticFamily.listener.JoinListener;
import de.janschuri.lunaticFamily.listener.QuitListener;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;


public final class LunaticFamily extends JavaPlugin {
    private static Database db;
    private static final Map<String, Map<String, List<String>>> aliases = new HashMap<>();


    public static BiMap<String, String> marryRequests = HashBiMap.create();
    public static BiMap<String, String> marryPriestRequests = HashBiMap.create();
    public static BiMap<String, String> marryPriest = HashBiMap.create();
    public static BiMap<String, String> adoptRequests = HashBiMap.create();
    public static BiMap<String, String> siblingRequests = HashBiMap.create();

    private static LunaticFamily instance;

    public static LunaticFamily getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        loadConfig(this);

        checkSoftDepends();

        new Vault();

        if (Config.enabledMySQL) {
            db = new MySQL(this);
            if (db.getSQLConnection() == null) {
                Bukkit.getLogger().log(Level.SEVERE, "Error initializing MySQL database");
                Bukkit.getLogger().info("Falling back to SQLite due to initialization error");
                db = new SQLite(this);
            }
        } else {
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

        new Config(this);
        new Language(this);

        List<String> commands = Arrays.asList("family", "marry", "sibling", "adopt", "gender");

        for (String command : commands) {
            Map<String, List<String>> map = new HashMap<>();
            ConfigurationSection section = Language.lang.getConfigurationSection("aliases." + command);
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

        registerCommand("family");
        registerCommand("marry");
        registerCommand("adopt");
        registerCommand("sibling");
        registerCommand("gender");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Database getDatabase() {
        return LunaticFamily.db;
    }

    public static List<String> getAliases(String command, String subcommand) {
        Map<String, List<String>> commandAliases = LunaticFamily.aliases.getOrDefault(command, new HashMap<>());

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
        Map<String, List<String>> commandAliases = LunaticFamily.aliases.getOrDefault(command, new HashMap<>());

        List<String> list = commandAliases.getOrDefault("base_command", new ArrayList<>());
        if (list.isEmpty()) {
            list.add(command);
        }
        return list;
    }

    public static void checkSoftDepends() {
        try {
            Class.forName("eu.endercentral.crazy_advancements.CrazyAdvancementsAPI");
            Config.enabledCrazyAdvancementAPI = true;
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().warning("Could not find CrazyAdvancementsAPI.");
            Config.enabledCrazyAdvancementAPI = false;
        }

        try {
            Class.forName("net.milkbowl.vault.economy.Economy");
            Config.enabledVault = true;
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().warning("Could not find Vault.");
            Config.enabledVault = false;
        }

        try {
            Class.forName("at.pcgamingfreaks.Minepacks.Bukkit.API.MinepacksPlugin");
            Config.enabledMinepacks = true;
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().warning("Could not find Minepacks.");
            Config.enabledMinepacks = false;
        }


    }

    private void registerCommand(String command) {

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
