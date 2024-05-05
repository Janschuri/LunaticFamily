package de.janschuri.lunaticfamily.listener.paper;

import de.janschuri.lunaticfamily.listener.QuitEvent;
import de.janschuri.lunaticlib.senders.paper.PlayerSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    QuitEvent quitEvent = new QuitEvent();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerSender playerCommandSender = new PlayerSender(event.getPlayer());
        quitEvent.execute(playerCommandSender);
    }
}