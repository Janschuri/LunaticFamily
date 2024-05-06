package de.janschuri.lunaticfamily.listener.paper;

import de.janschuri.lunaticfamily.LunaticFamily;
import de.janschuri.lunaticfamily.listener.JoinEvent;
import de.janschuri.lunaticlib.senders.paper.PlayerSender;
import de.janschuri.lunaticlib.utils.Mode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    JoinEvent joinEvent = new JoinEvent();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerSender playerCommandSender = new PlayerSender(event.getPlayer());

        if (LunaticFamily.getMode() == Mode.STANDALONE) {
            joinEvent.execute(playerCommandSender);
        }
    }
}