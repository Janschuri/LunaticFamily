package de.janschuri.lunaticFamily.listener.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import de.janschuri.lunaticFamily.listener.QuitEvent;
import de.janschuri.lunaticFamily.senders.paper.PlayerCommandSender;

public class QuitListener {

    QuitEvent quitEvent = new QuitEvent();

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        PlayerCommandSender playerCommandSender = new PlayerCommandSender(event.getPlayer().getUniqueId());
        quitEvent.execute(playerCommandSender);
    }
}
