package de.janschuri.lunaticFamily;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticFamily.commands.AdoptCommand;
import de.janschuri.lunaticFamily.commands.FamilyCommand;
import de.janschuri.lunaticFamily.commands.GenderCommand;
import de.janschuri.lunaticFamily.commands.MarryCommand;
import de.janschuri.lunaticFamily.commands.SiblingCommand;
import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.database.MySQL;
import de.janschuri.lunaticFamily.database.SQLite;
import de.janschuri.lunaticFamily.external.Vault;
import de.janschuri.lunaticFamily.handler.FamilyTree;
import de.janschuri.lunaticFamily.listener.JoinListener;
import de.janschuri.lunaticFamily.listener.QuitListener;
import de.janschuri.lunaticFamily.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.*;


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

        if (Config.enabledMySQL) {
            db = new MySQL(this);
            if (db.getSQLConnection() == null) {
                Logger.errorLog("Error initializing MySQL database");
                Logger.warnLog("Falling back to SQLite due to initialization error");
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

        getCommand("gender").setExecutor(new GenderCommand(this));
        getCommand("gender").setTabCompleter(new GenderCommand(this));

        getCommand("marry").setExecutor(new MarryCommand(this));
        getCommand("marry").setTabCompleter(new MarryCommand(this));

        getCommand("sibling").setExecutor(new SiblingCommand(this));
        getCommand("sibling").setTabCompleter(new SiblingCommand(this));

    }

    public void loadConfig(LunaticFamily plugin) {

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

        checkSoftDepends();
        if (Config.enabledCrazyAdvancementAPI) {
            FamilyTree.loadAdvancementMap(instance);
        }

        if (Config.enabledVault) {
            new Vault();
        }
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
        } catch (ClassNotFoundException e) {
            if (Config.enabledCrazyAdvancementAPI) {
                Logger.warnLog("CrazyAdvancementsAPI is not installed. Disabling CrazyAdvancementsAPI features.");
                Config.enabledCrazyAdvancementAPI = false;
            }
        }

        try {
            Class.forName("net.milkbowl.vault.economy.Economy");
        } catch (ClassNotFoundException e) {
            if (Config.enabledVault) {
                Logger.warnLog("Vault is not installed. Disabling Vault features.");
                Config.enabledVault = false;
            }
        }

        try {
            Class.forName("at.pcgamingfreaks.Minepacks.Bukkit.API.MinepacksPlugin");
        } catch (ClassNotFoundException e) {
            if (Config.enabledMinepacks) {
                Logger.warnLog("Minepacks is not installed. Disabling Minepacks features.");
                Config.enabledMinepacks = false;
            }
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
