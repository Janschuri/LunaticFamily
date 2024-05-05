package de.janschuri.lunaticfamily;

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
import de.janschuri.lunaticfamily.commands.velocity.*;
import de.janschuri.lunaticfamily.config.Language;
import de.janschuri.lunaticfamily.database.Database;
import de.janschuri.lunaticlib.utils.Mode;

import java.nio.file.Path;

@Plugin(
        id = "lunaticfamily",
        name = "LunaticFamily",
        version = "1.0.0",
        authors = "janschuri",
        dependencies = {
        @Dependency(id = "lunaticlib")

        }
)
public class VelocityLunaticFamily {

    private static ProxyServer proxy;
    private static Path dataDirectory;
    private static VelocityLunaticFamily instance;
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
        instance = this;

        LunaticFamily.registerRequests();
        proxy.getChannelRegistrar().register(IDENTIFIER);

        LunaticFamily.setDataDirectory(dataDirectory);
        LunaticFamily.loadConfig();
        Database.loadDatabase();

        LunaticFamily.onEnable();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        LunaticFamily.onDisable();
    }

    public static VelocityLunaticFamily getInstance() {
        return instance;
    }

    static void registerCommands() {
        CommandManager commandManager = proxy.getCommandManager();

        for (String command : LunaticFamily.commands) {
            CommandMeta commandMeta = commandManager.metaBuilder(command)
                    .aliases(Language.getLanguage().getAliases(command).toArray(new String[0]))
                    .plugin(getInstance())
                    .build();

            if (commandManager.hasCommand(command)) {
                commandManager.unregister(command);
            }

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
}
