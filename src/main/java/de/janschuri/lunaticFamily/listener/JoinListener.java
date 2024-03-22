package de.janschuri.lunaticFamily.listener;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.handler.FamilyTree;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinListener implements Listener {

    private final LunaticFamily plugin;

    public JoinListener(LunaticFamily plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        new BukkitRunnable() {
            public void run() {
                Player player = event.getPlayer();
                String uuid = player.getUniqueId().toString();
                FamilyPlayer playerFam = new FamilyPlayer(uuid);


            }
        }.runTaskLater(plugin, 10L);

        new BukkitRunnable() {
            public void run() {
                Player player = event.getPlayer();
                String uuid = player.getUniqueId().toString();
                FamilyPlayer familyPlayer = new FamilyPlayer(uuid);
                FamilyTree familyTree = new FamilyTree(familyPlayer.getID());
                player.sendMessage(LunaticFamily.getMessage("tree_loaded"));

            }
        }.runTaskLater(plugin, 100L);

    }
}