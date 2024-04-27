package de.janschuri.lunaticFamily;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import de.janschuri.lunaticFamily.commands.velocity.*;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.listener.velocity.JoinListener;
import de.janschuri.lunaticFamily.listener.velocity.MessageListener;
import de.janschuri.lunaticFamily.listener.velocity.QuitListener;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticFamily.utils.VelocityUtils;
import de.janschuri.lunaticlib.Mode;
import de.janschuri.lunaticlib.utils.logger.VelocityLogger;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Plugin(
        id = "lunaticfamily",
        name = "LunaticFamily",
        version = "1.0-SNAPSHOT",
        authors = "janschuri",
        dependencies = {
        @Dependency(id = "lunaticlib")

        }
)
public class Velocity {

    private static ProxyServer proxy;
    private static Path dataDirectory;
    private static Logger logger;
    private static Velocity instance;
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("lunaticfamily:proxy");
    public static final ConcurrentHashMap<Integer, CompletableFuture<Boolean>> booleanRequestMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Integer, CompletableFuture<byte[]>> byteArrayRequestMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Integer, CompletableFuture<double[]>> doubleArrayRequestMap = new ConcurrentHashMap<>();

    @Inject
    public Velocity(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        Velocity.proxy = proxy;
        Velocity.logger = logger;
        Velocity.dataDirectory = dataDirectory;
        Velocity.instance = this;
    }

    public static ProxyServer getProxy() {
        return proxy;
    }

    public static Velocity getInstance() {
        return instance;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        LunaticFamily.mode = Mode.PROXY;
        proxy.getChannelRegistrar().register(IDENTIFIER);
        proxy.getEventManager().register(this, new MessageListener());
        proxy.getEventManager().register(this, new JoinListener());
        proxy.getEventManager().register(this, new QuitListener());
        new de.janschuri.lunaticFamily.utils.Logger(new VelocityLogger(logger));
        Utils.loadUtils(new VelocityUtils());

        LunaticFamily.setDataDirectory(dataDirectory);
        LunaticFamily.loadConfig();
        Database.loadDatabase(dataDirectory);

        CommandManager commandManager = proxy.getCommandManager();

        for (String command : LunaticFamily.commands) {
            CommandMeta commandMeta = commandManager.metaBuilder(command)
                    .aliases(Language.getInstance().getAliases(command).toArray(new String[0]))
                    .plugin(this)
                    .build();

            switch (command) {
                case "family":
                    commandManager.register(commandMeta, new FamilyCommand());
                    break;
                case "marry":
                    commandManager.register(commandMeta, new MarryCommand());
                    break;
                case "adopt":
                    commandManager.register(commandMeta, new AdoptCommand());
                    break;
                case "gender":
                    commandManager.register(commandMeta, new GenderCommand());
                    break;
                case "sibling":
                    commandManager.register(commandMeta, new SiblingCommand());
                    break;
            }
        }
    }

    public static void sendPluginMessage(byte[] message) {
        de.janschuri.lunaticFamily.utils.Logger.debugLog("PluginMessage sent.");

        if (PluginConfig.enabledServerWhitelist) {
            proxy.getAllServers().stream()
                    .filter(serverConnection -> PluginConfig.serverWhitelist.contains(serverConnection.getServerInfo().getName()))
                    .forEach(serverConnection -> serverConnection.sendPluginMessage(IDENTIFIER, message));
        } else {
            proxy.getAllServers().forEach(serverConnection -> serverConnection.sendPluginMessage(IDENTIFIER, message));
        }
    }
}
