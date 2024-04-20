package de.janschuri.lunaticFamily.listener;

import de.janschuri.lunaticFamily.commands.senders.PaperPlayerCommandSender;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    QuitSubevent quitSubevent = new QuitSubevent();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        PaperPlayerCommandSender playerCommandSender = new PaperPlayerCommandSender(event.getPlayer());
        quitSubevent.execute(playerCommandSender);
    }
}