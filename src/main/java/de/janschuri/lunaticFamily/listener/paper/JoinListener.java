package de.janschuri.lunaticFamily.listener.paper;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.listener.JoinEvent;
import de.janschuri.lunaticlib.senders.bukkit.PlayerSender;
import de.janschuri.lunaticlib.utils.Mode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    JoinEvent joinEvent = new JoinEvent();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerSender playerCommandSender = new PlayerSender(event.getPlayer());
        new FamilyPlayer(playerCommandSender.getUniqueId());

        if (LunaticFamily.getMode() == Mode.STANDALONE) {
            joinEvent.execute(playerCommandSender);
        }
    }
}