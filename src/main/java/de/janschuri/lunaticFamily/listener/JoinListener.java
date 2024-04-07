package de.janschuri.lunaticFamily.listener;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.handler.FamilyTree;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinListener implements Listener {



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            public void run() {
                if (Config.enabledCrazyAdvancementAPI) {
                    Player player = event.getPlayer();
                    String uuid = player.getUniqueId().toString();
                    FamilyPlayer familyPlayer = new FamilyPlayer(uuid);
                    new FamilyTree(familyPlayer.getID());
                }
            }
        }.runTaskLater(LunaticFamily.getInstance(), 5L);
    }
}