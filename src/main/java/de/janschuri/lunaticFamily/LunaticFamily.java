package de.janschuri.lunaticFamily;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.janschuri.lunaticFamily.commands.paper.*;
import de.janschuri.lunaticFamily.config.DatabaseConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.external.Vault;
import de.janschuri.lunaticFamily.external.FamilyTree;
import de.janschuri.lunaticFamily.listener.paper.JoinListener;
import de.janschuri.lunaticFamily.listener.paper.MessageListener;
import de.janschuri.lunaticFamily.listener.paper.QuitListener;
import de.janschuri.lunaticFamily.utils.Logger;
import de.janschuri.lunaticFamily.utils.PaperUtils;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.utils.Mode;
import de.janschuri.lunaticlib.utils.Platform;
import de.janschuri.lunaticlib.utils.logger.BukkitLogger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public final class LunaticFamily extends JavaPlugin {
    public static BiMap<UUID, UUID> marryRequests = HashBiMap.create();
    public static BiMap<UUID, UUID> marryPriestRequests = HashBiMap.create();
    public static BiMap<UUID, UUID> marryPriest = HashBiMap.create();
    public static BiMap<UUID, UUID> adoptRequests = HashBiMap.create();
    public static BiMap<UUID, UUID> siblingRequests = HashBiMap.create();
    public static boolean enabledProxy = false;
    public static boolean installedVault = false;
    public static boolean installedCrazyAdvancementsAPI = false;
    private static final String IDENTIFIER = "lunaticfamily:proxy";

    static final List<String> commands = Arrays.asList("family", "marry", "sibling", "adopt", "gender");
    private static Path dataDirectory;
    static Mode mode = Mode.STANDALONE;

    private static LunaticFamily instance;

    public static LunaticFamily getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;
        dataDirectory = getDataFolder().toPath();
        getServer().getMessenger().registerIncomingPluginChannel(this, IDENTIFIER, new MessageListener());
        getServer().getMessenger().registerOutgoingPluginChannel(this, IDENTIFIER);
        new Logger(new BukkitLogger(this));

        if (Utils.classExists("eu.endercentral.crazy_advancements.CrazyAdvancementsAPI")) {
            installedCrazyAdvancementsAPI = true;
        }

        if (Utils.classExists("net.milkbowl.vault.economy.Economy")) {
            installedVault = true;
        }

        Utils.loadUtils(new PaperUtils());

        if (!loadConfig()) {
            disable();
            return;
        }

        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new QuitListener(), this);

        if (!PluginConfig.useProxy) {
            LunaticFamily.mode = Mode.STANDALONE;

            for (String command : commands) {
                Command cmd = getCommand(command);
                assert cmd != null;
                try {
                    final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                    bukkitCommandMap.setAccessible(true);
                    CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

                    List<String> list = Language.getInstance().getAliases(command);

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

        if (!Database.loadDatabase(dataDirectory)) {
            disable();
        }
    }

    public static Mode getMode() {
        return mode;
    }

    public static void setDataDirectory(Path dataDirectory) {
        LunaticFamily.dataDirectory = dataDirectory;
    }

    public static boolean loadConfig() {

        new PluginConfig(dataDirectory);


        if (mode == Mode.STANDALONE || mode == Mode.PROXY) {
            new Language(dataDirectory, commands);
        }
        new DatabaseConfig(dataDirectory);


        if (mode == Mode.STANDALONE || mode == Mode.BACKEND) {
            if (PluginConfig.useCrazyAdvancementAPI) {

                if (!installedCrazyAdvancementsAPI) {
                    Logger.errorLog("CrazyAdvancementsAPI is not installed! Please install CrazyAdvancementsAPI or disable it in plugin config.yml.");
                    return false;
                }

                FamilyTree.loadAdvancementMap(instance);
                Logger.infoLog("Loaded family tree.");
            }

            if (PluginConfig.useVault) {

                if (!installedVault) {
                    Logger.errorLog("Vault is not installed! Please install Vault or disable it in plugin config.yml.");
                    return false;
                }

                new Vault();
                Logger.infoLog("Loaded Vault.");
            }
        }

        return true;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private static void disable() {
        Logger.errorLog("Disabling LunaticFamily...");
        Bukkit.getServer().getPluginManager().disablePlugin(getInstance());
    }

    public static void sendPluginMessage(byte[] message) {
        getInstance().getServer().sendPluginMessage(getInstance(), IDENTIFIER, message);
    }
}
