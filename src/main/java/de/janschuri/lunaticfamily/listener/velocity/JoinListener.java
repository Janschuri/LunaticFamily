package de.janschuri.lunaticfamily.listener.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import de.janschuri.lunaticfamily.listener.JoinEvent;
import de.janschuri.lunaticlib.senders.velocity.PlayerSender;

import java.util.UUID;

public class JoinListener {

    @Subscribe
    public void onPlayerConnect(ServerPostConnectEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (event.getPreviousServer() != null) {

        } else {
            new JoinEvent().execute(new PlayerSender(uuid));
        }
    }
}
