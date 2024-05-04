package de.janschuri.lunaticFamily;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import de.janschuri.lunaticFamily.commands.velocity.*;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.listener.velocity.JoinListener;
import de.janschuri.lunaticFamily.listener.velocity.QuitListener;
import de.janschuri.lunaticlib.utils.Mode;
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
public class VelocityLunaticFamily {

    private static ProxyServer proxy;
    private static Path dataDirectory;
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("lunaticfamily:proxy");

    @Inject
    public VelocityLunaticFamily(ProxyServer proxy, @DataDirectory Path dataDirectory) {
        VelocityLunaticFamily.proxy = proxy;
        VelocityLunaticFamily.dataDirectory = dataDirectory;
    }

    public static ProxyServer getProxy() {
        return proxy;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        LunaticFamily.mode = Mode.PROXY;

        LunaticFamily.registerRequests();
        proxy.getChannelRegistrar().register(IDENTIFIER);
        proxy.getEventManager().register(this, new JoinListener());
        proxy.getEventManager().register(this, new QuitListener());

        LunaticFamily.setDataDirectory(dataDirectory);
        LunaticFamily.loadConfig();
        Database.loadDatabase();

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

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        LunaticFamily.unregisterRequests();
        de.janschuri.lunaticFamily.utils.Logger.infoLog("LunaticFamily disabled.");
    }
}
