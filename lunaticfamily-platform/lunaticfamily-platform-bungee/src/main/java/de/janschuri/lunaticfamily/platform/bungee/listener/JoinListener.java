package de.janschuri.lunaticfamily.platform.bungee.listener;

import de.janschuri.lunaticfamily.common.listener.JoinListenerExecuter;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.platform.bungee.PlatformImpl;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(ServerConnectedEvent event) {
        Logger.debugLog(String.format("Executing join listener for %s", event.getPlayer().getName()));
        PlayerSender commandSender = (PlayerSender) new PlatformImpl().getSender(event.getPlayer());
        JoinListenerExecuter.execute(commandSender);
    }
}
