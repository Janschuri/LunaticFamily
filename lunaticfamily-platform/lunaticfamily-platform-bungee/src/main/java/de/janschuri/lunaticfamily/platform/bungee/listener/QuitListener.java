package de.janschuri.lunaticfamily.platform.bungee.listener;

import de.janschuri.lunaticfamily.common.listener.QuitListenerExecuter;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.platform.bungee.PlatformImpl;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class QuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        PlayerSender commandSender = (PlayerSender) new PlatformImpl().getSender(event.getPlayer());
        QuitListenerExecuter.execute(commandSender);
    }
}
