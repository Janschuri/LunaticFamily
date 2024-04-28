package de.janschuri.lunaticFamily.listener.velocity;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import de.janschuri.lunaticFamily.VelocityLunaticFamily;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.listener.JoinEvent;
import de.janschuri.lunaticlib.senders.velocity.PlayerSender;

import java.util.UUID;

public class JoinListener {

    @Subscribe
    public void onPlayerConnect(ServerPostConnectEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (event.getPreviousServer() != null) {

        } else {
            JoinEvent joinSubevent = new JoinEvent();
            joinSubevent.execute(new PlayerSender(uuid));
        }

        if (PluginConfig.useCrazyAdvancementAPI) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            FamilyPlayer playerFam = new FamilyPlayer(uuid);
            out.writeUTF("UpdateFamilyTree");
            out.writeInt(playerFam.getID());
            out.writeUTF(uuid.toString());
            VelocityLunaticFamily.sendPluginMessage(out.toByteArray());
        }
    }
}
