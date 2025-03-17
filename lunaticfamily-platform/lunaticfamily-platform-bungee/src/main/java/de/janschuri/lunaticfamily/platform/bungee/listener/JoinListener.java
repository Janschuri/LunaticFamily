package de.janschuri.lunaticfamily.platform.bungee.listener;

import de.janschuri.lunaticfamily.common.listener.JoinListenerExecuter;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.bungee.BungeeLunaticFamily;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.platform.bungee.PlatformImpl;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(ServerConnectEvent event) {

        if (event.getReason() != ServerConnectEvent.Reason.JOIN_PROXY) {
            return;
        }

        PlayerSender commandSender = (PlayerSender) new PlatformImpl().getSender(event.getPlayer());

        ProxyServer.getInstance().getScheduler().schedule(BungeeLunaticFamily.getInstance(), () -> {
            JoinListenerExecuter.execute(commandSender);
        }, 200, TimeUnit.MILLISECONDS);
    }
}
