package de.janschuri.lunaticFamily.utils;


import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.Player;
import de.janschuri.lunaticFamily.Velocity;
import de.janschuri.lunaticFamily.commands.senders.VelocityPlayerCommandSender;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;

import java.io.BufferedOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class VelocityUtils extends Utils {
    @Override
    public String getPlayerName(UUID uuid) {
        Optional<Player> player = Velocity.getProxy().getPlayer(uuid);
        return player.map(Player::getUsername).orElse(null);
    }

    @Override
    public void sendConsoleCommand(String command) {

    }

    @Override
    public void updateFamilyTree(int id) {
        FamilyPlayer playerFam = new FamilyPlayer(id);
        UUID uuid = playerFam.getUniqueId();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("UpdateFamilyTree");
        out.writeInt(id);
        out.writeUTF(uuid.toString());
        Velocity.sendPluginMessage(out.toByteArray());
    }

    @Override
    public boolean isPlayerOnWhitelistedServer(UUID uuid) {
        if(!PluginConfig.enabledServerWhitelist) {
            return true;
        }
        List<String> servers = PluginConfig.serverWhitelist;
        Optional<Player> player = Velocity.getProxy().getPlayer(uuid);
        return player.map(value -> servers.contains(value.getCurrentServer().get().getServerInfo().getName())).orElse(false);
    }
}
