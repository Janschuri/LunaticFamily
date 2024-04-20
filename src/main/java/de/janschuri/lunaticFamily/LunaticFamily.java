package de.janschuri.lunaticFamily;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticFamily.commands.paper.*;
import de.janschuri.lunaticFamily.config.DatabaseConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.external.Vault;
import de.janschuri.lunaticFamily.handler.FamilyTree;
import de.janschuri.lunaticFamily.listener.JoinListener;
import de.janschuri.lunaticFamily.listener.ProxyListener;
import de.janschuri.lunaticFamily.listener.QuitListener;
import de.janschuri.lunaticFamily.utils.logger.BukkitLogger;
import de.janschuri.lunaticFamily.utils.logger.Logger;
import de.janschuri.lunaticFamily.utils.PaperUtils;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.*;
import java.util.List;


public final class LunaticFamily extends JavaPlugin {
    public static BiMap<String, String> marryRequests = HashBiMap.create();
    public static BiMap<String, String> marryPriestRequests = HashBiMap.create();
    public static BiMap<String, String> marryPriest = HashBiMap.create();
    public static BiMap<String, String> adoptRequests = HashBiMap.create();
    public static BiMap<String, String> siblingRequests = HashBiMap.create();
    public static Set<String> proxyPlayers = new HashSet<>();
    private static final String IDENTIFIER = "velocity:lunaticfamily";
    private static Path dataDirectory;
    static Mode mode = Mode.STANDALONE;

    private static LunaticFamily instance;
    public static boolean isProxy = false;

    public static LunaticFamily getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;
        dataDirectory = getDataFolder().toPath();
        getServer().getMessenger().registerIncomingPluginChannel(this, IDENTIFIER, new ProxyListener());
        getServer().getMessenger().registerOutgoingPluginChannel(this, IDENTIFIER);
        Logger.loadLogger(new BukkitLogger());

        Utils.loadUtils(new PaperUtils());

        loadConfig();

        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new QuitListener(), this);

        if (!PluginConfig.isBackend) {
            LunaticFamily.mode = Mode.STANDALONE;

            List<String> commands = Arrays.asList("family", "marry", "sibling", "adopt", "gender");

            for (String command : commands) {
                Command cmd = getCommand(command);
                assert cmd != null;
                try {
                    final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                    bukkitCommandMap.setAccessible(true);
                    CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

                    List<String> list = Language.getAliases(command);

                    list.forEach(alias -> {
                        commandMap.register(alias, instance.getName(), cmd);
                    });
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            getCommand("family").setExecutor(new FamilyCommand());
            getCommand("family").setTabCompleter(new FamilyCommand());
            getCommand("family").setPermission("lunaticfamily.family");

            getCommand("adopt").setExecutor(new AdoptCommand());
            getCommand("adopt").setTabCompleter(new AdoptCommand());
            getCommand("adopt").setPermission("lunaticfamily.adopt");

            getCommand("gender").setExecutor(new GenderCommand());
            getCommand("gender").setTabCompleter(new GenderCommand());
            getCommand("gender").setPermission("lunaticfamily.gender");

            getCommand("marry").setExecutor(new MarryCommand());
            getCommand("marry").setTabCompleter(new MarryCommand());
            getCommand("marry").setPermission("lunaticfamily.marry");

            getCommand("sibling").setExecutor(new SiblingCommand());
            getCommand("sibling").setTabCompleter(new SiblingCommand());
            getCommand("sibling").setPermission("lunaticfamily.sibling");

        } else {
            LunaticFamily.mode = Mode.BACKEND;
            Logger.infoLog("Backend mode enabled.");
        }

    }

    public static Mode getMode() {
        return mode;
    }

    public static Path getDataDirectory() {
        return dataDirectory;
    }

    public static void setDataDirectory(Path dataDirectory) {
        LunaticFamily.dataDirectory = dataDirectory;
    }

    public static void loadConfig() {

        new PluginConfig(dataDirectory);


        if (mode == Mode.STANDALONE || mode == Mode.PROXY) {
            new Language(dataDirectory);
        }
        new DatabaseConfig(dataDirectory);


        if (mode == Mode.STANDALONE || mode == Mode.BACKEND) {
            checkSoftDepends();
            if (PluginConfig.enabledCrazyAdvancementAPI) {
                FamilyTree.loadAdvancementMap(instance);
                Logger.infoLog("Loaded family tree.");
            }

            if (PluginConfig.enabledVault) {
                new Vault();
                Logger.infoLog("Loaded Vault.");
            }
        }

        Database.loadDatabase(dataDirectory);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static void sendPluginMessage(byte[] message) {
        getInstance().getServer().sendPluginMessage(getInstance(), IDENTIFIER, message);
    }

    public static void checkSoftDepends() {
        try {
            Class.forName("eu.endercentral.crazy_advancements.CrazyAdvancementsAPI");
        } catch (ClassNotFoundException e) {
            if (PluginConfig.enabledCrazyAdvancementAPI) {
                Logger.warnLog("CrazyAdvancementsAPI is not installed. Disabling CrazyAdvancementsAPI features.");
                PluginConfig.enabledCrazyAdvancementAPI = false;
            }
        }

        try {
            Class.forName("net.milkbowl.vault.economy.Economy");
        } catch (ClassNotFoundException e) {
            if (PluginConfig.enabledVault) {
                Logger.warnLog("Vault is not installed. Disabling Vault features.");
                PluginConfig.enabledVault = false;
            }
        }
    }
}
