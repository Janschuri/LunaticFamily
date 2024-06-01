package de.janschuri.lunaticfamily.platform.bukkit.listener;

import de.janschuri.lunaticfamily.common.listener.QuitListenerExecuter;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.platform.bukkit.PlatformImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerSender playerSender = (PlayerSender) new PlatformImpl().getSender(event.getPlayer());
        QuitListenerExecuter.execute(playerSender);
    }
}