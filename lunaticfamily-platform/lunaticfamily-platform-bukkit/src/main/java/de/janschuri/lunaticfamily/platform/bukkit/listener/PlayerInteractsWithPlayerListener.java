package de.janschuri.lunaticfamily.platform.bukkit.listener;

import de.janschuri.lunaticfamily.common.listener.PlayerInteractsWithPlayerExecuter;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.bukkit.BukkitLunaticFamily;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerInteractsWithPlayerListener implements Listener {

    private static final List<UUID> cooldown = new ArrayList<>();

    @EventHandler
    public void onPlayerClick(PlayerInteractAtEntityEvent event) {
        Player clickingPlayer = event.getPlayer();

        if (cooldown.contains(clickingPlayer.getUniqueId())) {
            return;
        }

        cooldown.add(clickingPlayer.getUniqueId());

        Bukkit.getScheduler().runTaskLater(BukkitLunaticFamily.getInstance(), () -> {
            cooldown.remove(clickingPlayer.getUniqueId());
        }, 5L);

        if (event.getRightClicked() instanceof Player clickedPlayer) {
            PlayerSender clickingPlayerSender = LunaticLib.getPlatform().getPlayerSender(clickingPlayer.getUniqueId());
            PlayerSender clickedPlayerSender = LunaticLib.getPlatform().getPlayerSender(clickedPlayer.getUniqueId());

            Bukkit.getScheduler().runTaskAsynchronously(BukkitLunaticFamily.getInstance(), () -> {
                boolean success = PlayerInteractsWithPlayerExecuter.execute(
                        clickingPlayerSender,
                        clickedPlayerSender,
                        clickingPlayer.isSneaking()
                );

                if (!success) {
                    clickedPlayerSender.sendMessage("§Error while processing player interaction.");
                }
            });
        }
    }
}
