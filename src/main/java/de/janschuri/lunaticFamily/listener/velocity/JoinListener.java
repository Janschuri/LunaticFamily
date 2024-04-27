package de.janschuri.lunaticFamily.listener.velocity;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import de.janschuri.lunaticFamily.Velocity;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.listener.JoinEvent;
import de.janschuri.lunaticFamily.utils.Logger;
import de.janschuri.lunaticlib.senders.velocity.PlayerSender;

import java.util.UUID;

public class JoinListener {

    @Subscribe
    public void onPlayerConnect(ServerPostConnectEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Logger.debugLog("test");
        if (event.getPreviousServer() != null) {

        } else {
            JoinEvent joinSubevent = new JoinEvent();
            joinSubevent.execute(new PlayerSender(uuid));
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
}
