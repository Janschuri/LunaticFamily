package de.janschuri.lunaticfamily.listener.bungee;

import de.janschuri.lunaticfamily.LunaticFamily;
import de.janschuri.lunaticfamily.listener.JoinEvent;
import de.janschuri.lunaticlib.senders.bungee.PlayerSender;
import de.janschuri.lunaticlib.utils.Mode;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        PlayerSender playerCommandSender = new PlayerSender(event.getPlayer());

        if (LunaticFamily.getMode() == Mode.STANDALONE) {
            new JoinEvent().execute(playerCommandSender);
        }
    }
}
