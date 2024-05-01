package de.janschuri.lunaticFamily.listener.bungee;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.listener.JoinEvent;
import de.janschuri.lunaticlib.senders.bungee.PlayerSender;
import de.janschuri.lunaticlib.utils.Mode;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class JoinListener implements Listener {

    JoinEvent joinEvent = new JoinEvent();

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        PlayerSender playerCommandSender = new PlayerSender(event.getPlayer());

        if (LunaticFamily.getMode() == Mode.STANDALONE) {
            joinEvent.execute(playerCommandSender);
        }
    }
}
