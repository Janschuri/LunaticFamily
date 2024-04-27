package de.janschuri.lunaticFamily.listener.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import de.janschuri.lunaticFamily.listener.QuitEvent;
import de.janschuri.lunaticlib.senders.velocity.PlayerSender;

public class QuitListener {

    QuitEvent quitEvent = new QuitEvent();

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        PlayerSender playerCommandSender = new PlayerSender(event.getPlayer().getUniqueId());
        quitEvent.execute(playerCommandSender);
    }
}
