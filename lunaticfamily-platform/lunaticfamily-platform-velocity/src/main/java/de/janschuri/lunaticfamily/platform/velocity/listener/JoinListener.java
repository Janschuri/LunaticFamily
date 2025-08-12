package de.janschuri.lunaticfamily.platform.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import de.janschuri.lunaticfamily.common.listener.JoinListenerExecuter;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;

public class JoinListener {

    @Subscribe
    public void onPlayerConnect(ServerPostConnectEvent event) {
        PlayerSender playerCommandSender = (PlayerSender) LunaticLib.getPlatform().getSender(event.getPlayer());

        boolean isFirstJoin = event.getPreviousServer() == null;

        JoinListenerExecuter.execute(playerCommandSender, isFirstJoin);
    }
}
