package de.janschuri.lunaticFamily.utils;

import de.janschuri.lunaticFamily.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinListener implements Listener {

    private final Main plugin;

    public JoinListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        new BukkitRunnable() {
            public void run() {
                Player player = event.getPlayer();
                String uuid = player.getUniqueId().toString();
                FamilyManager playerFam = new FamilyManager(uuid, plugin);


            }
        }.runTaskLater(plugin, 10L);

        new BukkitRunnable() {
            public void run() {
                Player player = event.getPlayer();
                String uuid = player.getUniqueId().toString();
                FamilyTree familyTree = new FamilyTree(uuid, plugin);
                player.sendMessage(plugin.messages.get("tree_loaded"));

            }
        }.runTaskLater(plugin, 100L);

    }
}