package de.janschuri.lunaticFamily.listener.paper;

import de.janschuri.lunaticFamily.commands.paper.PlayerCommandSender;
import de.janschuri.lunaticFamily.listener.QuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    QuitEvent quitSubevent = new QuitEvent();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        PlayerCommandSender playerCommandSender = new PlayerCommandSender(event.getPlayer());
        quitSubevent.execute(playerCommandSender);
    }
}