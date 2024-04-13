package de.janschuri.lunaticFamily;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Plugin(
        id = "lunaticfamily",
        name = "LunaticFamily",
        version = "1.0-SNAPSHOT",
        authors = "janschuri"
)
public class Velocity {

    private final ProxyServer proxy;
    private final Path dataDirectory;
    private final Logger logger;
    private static File databaseConfigFile;
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.from("velocity:lunaticfamily");

    @Inject
    public Velocity(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    private File loadConfig() {
        File file = new File(dataDirectory.toFile(), "database.yml");

        if (!dataDirectory.toFile().exists()) {
            try {
                Files.createDirectories(dataDirectory.toFile().toPath());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        
        if (!file.exists()) {
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("database.yml")) {
                if (inputStream != null) {
                    Files.copy(inputStream, file.toPath());
                } else {
                    throw new IOException("Resource '" + "database.yml" + "' not found");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        proxy.getChannelRegistrar().register(IDENTIFIER);
        databaseConfigFile = loadConfig();
    }


    @Subscribe
    public void onPlayerConnect(ServerPostConnectEvent event) {
        proxy.getAllServers().forEach(serverConnection -> serverConnection.sendPluginMessage(IDENTIFIER, getOnlinePlayers()));
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        if (event.getPreviousServer() != null) {
            out.writeUTF("PlayerJumpEvent");
        } else {
            out.writeUTF("PlayerJoinEvent");
        }
        String uuid = event.getPlayer().getUniqueId().toString();
        out.writeUTF(uuid);
        proxy.getAllServers().forEach(serverConnection -> serverConnection.sendPluginMessage(IDENTIFIER, out.toByteArray()));
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        proxy.getAllServers().forEach(serverConnection -> serverConnection.sendPluginMessage(IDENTIFIER, getOnlinePlayers()));
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerLeaveEvent");
        out.writeUTF(uuid);
        proxy.getAllServers().forEach(serverConnection -> serverConnection.sendPluginMessage(IDENTIFIER, out.toByteArray()));
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        byte[] message = event.getData();
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        String subchannel = in.readUTF();
        if (subchannel.equals("OnlinePlayers")) {
            RegisteredServer server = proxy.getServer(in.readUTF()).orElse(null);
            proxy.getAllServers().forEach(serverConnection -> serverConnection.sendPluginMessage(IDENTIFIER, getOnlinePlayers()));
            return;
        }
        if (subchannel.equals("MessageToPlayer")) {
            out.writeUTF("MessageToPlayer");
            out.writeUTF(in.readUTF());
            out.writeUTF(in.readUTF());
            proxy.getAllServers().forEach(serverConnection -> serverConnection.sendPluginMessage(IDENTIFIER, out.toByteArray()));
            return;
        }
        if (subchannel.equals("ComponentMessageToPlayer")) {
            out.writeUTF("ComponentMessageToPlayer");
            out.writeUTF(in.readUTF());
            out.writeUTF(in.readUTF());
            proxy.getAllServers().forEach(serverConnection -> serverConnection.sendPluginMessage(IDENTIFIER, out.toByteArray()));
            return;
        }
        if (subchannel.equals("DropItemToPlayer")) {
            out.writeUTF("DropItemToPlayer");
            out.writeUTF(in.readUTF());
            int arrayLength = in.readInt();
            out.writeInt(arrayLength);
            for (int i = 0; i < arrayLength; i++) {
                out.writeByte(in.readByte());
            }
            proxy.getAllServers().forEach(serverConnection -> serverConnection.sendPluginMessage(IDENTIFIER, out.toByteArray()));
            return;
        }

    }

    private byte[] getOnlinePlayers() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("OnlinePlayers");
        Collection<Player> players = proxy.getAllPlayers();
        for (Player player : players) {
            out.writeUTF(player.getUniqueId().toString());
        }
        return out.toByteArray();
    }
}
