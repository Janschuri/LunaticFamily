package de.janschuri.lunaticfamily.platform.bukkit.listener;

import de.janschuri.lunaticfamily.common.listener.JoinListenerExecuter;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.platform.bukkit.PlatformImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerSender playerSender = (PlayerSender) new PlatformImpl().getSender(event.getPlayer());
        JoinListenerExecuter.execute(playerSender);
    }
}