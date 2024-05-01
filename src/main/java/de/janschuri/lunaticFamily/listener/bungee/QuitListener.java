package de.janschuri.lunaticFamily.listener.bungee;

import de.janschuri.lunaticFamily.listener.QuitEvent;
import de.janschuri.lunaticlib.senders.bungee.PlayerSender;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class QuitListener implements Listener {

    QuitEvent quitEvent = new QuitEvent();

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        PlayerSender playerCommandSender = new PlayerSender(event.getPlayer());
        quitEvent.execute(playerCommandSender);
    }
}
