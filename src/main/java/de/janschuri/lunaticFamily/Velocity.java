package de.janschuri.lunaticFamily;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import de.janschuri.lunaticFamily.commands.senders.VelocityPlayerCommandSender;
import de.janschuri.lunaticFamily.commands.velocity.*;
import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.config.DatabaseConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.listener.JoinSubevent;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticFamily.utils.VelocityUtils;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Plugin(
        id = "lunaticfamily",
        name = "LunaticFamily",
        version = "1.0-SNAPSHOT",
        authors = "janschuri"
)
public class Velocity {

    private static ProxyServer proxy;
    private static Path dataDirectory;
    private static Logger logger;
    private static File databaseConfigFile;
    private static Velocity instance;
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("velocity:lunaticfamily");
    public static final ConcurrentHashMap<Integer, CompletableFuture<Boolean>> booleanRequestMap = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Integer, CompletableFuture<byte[]>> byteArrayRequestMap = new ConcurrentHashMap<>();

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
        proxy.getChannelRegistrar().register(IDENTIFIER);
        de.janschuri.lunaticFamily.utils.logger.Logger.loadLogger(new de.janschuri.lunaticFamily.utils.logger.VelocityLogger(logger));
        Utils.loadUtils(new VelocityUtils());

        new PluginConfig(dataDirectory);
        new Language(dataDirectory);
        new DatabaseConfig(dataDirectory);

        Database.loadDatabase(dataDirectory);

        CommandManager commandManager = proxy.getCommandManager();

        CommandMeta familyCommandMeta = commandManager.metaBuilder("family")
                .aliases(Language.getAliases("family").toArray(new String[0]))
                .plugin(this)
                .build();

        CommandMeta marryCommandMeta = commandManager.metaBuilder("marry")
                .aliases(Language.getAliases("marry").toArray(new String[0]))
                .plugin(this)
                .build();

        CommandMeta adoptCommandMeta = commandManager.metaBuilder("adopt")
                .aliases(Language.getAliases("adopt").toArray(new String[0]))
                .plugin(this)
                .build();

        CommandMeta genderCommandMeta = commandManager.metaBuilder("gender")
                .aliases(Language.getAliases("gender").toArray(new String[0]))
                .plugin(this)
                .build();

        CommandMeta siblingCommandMeta = commandManager.metaBuilder("sibling")
                .aliases(Language.getAliases("sibling").toArray(new String[0]))
                .plugin(this)
                .build();

        SimpleCommand familyCommand = new VelocityFamilyCommand();
        SimpleCommand marryCommand = new VelocityMarryCommand();
        SimpleCommand adoptCommand = new VelocityAdoptCommand();
        SimpleCommand genderCommand = new VelocityGenderCommand();
        SimpleCommand siblingCommand = new VelocitySiblingCommand();

        commandManager.register(familyCommandMeta, familyCommand);
        commandManager.register(marryCommandMeta, marryCommand);
        commandManager.register(adoptCommandMeta, adoptCommand);
        commandManager.register(genderCommandMeta, genderCommand);
        commandManager.register(siblingCommandMeta, siblingCommand);

    }


    @Subscribe
    public void onPlayerConnect(ServerPostConnectEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (event.getPreviousServer() != null) {

        } else {
            JoinSubevent joinSubevent = new JoinSubevent();
            joinSubevent.execute(new VelocityPlayerCommandSender(uuid));
        }

        if (PluginConfig.enabledCrazyAdvancementAPI) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            FamilyPlayer playerFam = new FamilyPlayer(uuid);
            out.writeUTF("UpdateFamilyTree");
            out.writeInt(playerFam.getID());
            out.writeUTF(uuid.toString());
            Velocity.sendPluginMessage(out.toByteArray());
        }
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        byte[] message = event.getData();
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        String subchannel = in.readUTF();
        if (subchannel.equals("IsInRangeResponse")) {
            int requestId = in.readInt();
            boolean isInRange = in.readBoolean();
            CompletableFuture<Boolean> request = booleanRequestMap.get(requestId);
            request.complete(isInRange);
        }
        if (subchannel.equals("HasEnoughMoneyResponse")) {
            int requestId = in.readInt();
            boolean hasEnoughMoney = in.readBoolean();
            CompletableFuture<Boolean> request = booleanRequestMap.get(requestId);
            request.complete(hasEnoughMoney);
        }
        if (subchannel.equals("HasItemInMainHandResponse")) {
            int requestId = in.readInt();
            boolean hasItem = in.readBoolean();
            CompletableFuture<Boolean> request = booleanRequestMap.get(requestId);
            request.complete(hasItem);
        }
        if (subchannel.equals("GetItemInMainHandResponse")) {
            int requestId = in.readInt();
            byte[] item = new byte[in.readInt()];
            in.readFully(item);
            CompletableFuture<byte[]> request = byteArrayRequestMap.get(requestId);
            request.complete(item);
        }
        if (subchannel.equals("RemoveItemInMainHandResponse")) {
            int requestId = in.readInt();
            boolean success = in.readBoolean();
            CompletableFuture<Boolean> request = booleanRequestMap.get(requestId);
            request.complete(success);
        }
        if (subchannel.equals("GiveItemDropResponse")) {
            int requestId = in.readInt();
            boolean success = in.readBoolean();
            CompletableFuture<Boolean> request = booleanRequestMap.get(requestId);
            request.complete(success);
        }
    }

    public static void sendPluginMessage(byte[] message) {
        de.janschuri.lunaticFamily.utils.logger.Logger.debugLog("PluginMessage sent.");
        proxy.getAllServers().forEach(serverConnection -> serverConnection.sendPluginMessage(IDENTIFIER, message));
    }
}
