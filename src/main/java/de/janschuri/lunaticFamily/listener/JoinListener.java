package de.janschuri.lunaticFamily.listener;

import de.janschuri.lunaticFamily.commands.senders.PaperPlayerCommandSender;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    JoinSubevent joinSubevent = new JoinSubevent();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PaperPlayerCommandSender playerCommandSender = new PaperPlayerCommandSender(event.getPlayer());
        new FamilyPlayer(playerCommandSender.getUniqueId());
        joinSubevent.execute(playerCommandSender);
    }
}