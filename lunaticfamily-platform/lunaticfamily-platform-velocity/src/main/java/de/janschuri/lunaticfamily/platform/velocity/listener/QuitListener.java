package de.janschuri.lunaticfamily.platform.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import de.janschuri.lunaticfamily.common.listener.QuitListenerExecuter;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;

public class QuitListener {

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        PlayerSender playerCommandSender = (PlayerSender) LunaticLib.getPlatform().getSender(event.getPlayer());
        QuitListenerExecuter.execute(playerCommandSender);
    }
}
