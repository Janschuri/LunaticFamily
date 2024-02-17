package de.janschuri.lunaticfamily.utils;

import de.janschuri.lunaticfamily.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class QuitListener implements Listener {

    private final Main plugin;

    public QuitListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        FamilyManager playerFam = new FamilyManager(uuid, plugin);

        if (plugin.marryRequests.containsValue(uuid) || plugin.marryRequests.containsKey(uuid) || plugin.marryPriest.containsKey(uuid)) {

            if (plugin.marryPriest.containsKey(uuid)) {

                String priest = plugin.marryPriest.get(uuid);
                Bukkit.getPlayer(UUID.fromString(priest)).chat(plugin.messages.get("player_quit").replace("%player%", playerFam.getName()) + " " + plugin.messages.get("marry_cancel"));
            } else {
                String partner = plugin.marryRequests.get(uuid);
                Bukkit.getPlayer(UUID.fromString(partner)).sendMessage(plugin.messages.get("player_quit").replace("%player%", playerFam.getName()) + " " + plugin.messages.get("marry_cancel"));
            }

            plugin.marryRequests.remove(uuid);
            plugin.marryRequests.inverse().remove(uuid);
            plugin.marryPriestRequests.remove(uuid);
            plugin.marryPriestRequests.inverse().remove(uuid);
            plugin.marryPriest.remove(uuid);
            plugin.marryPriest.inverse().remove(uuid);
        }

        if (plugin.adoptRequests.containsKey(uuid)) {
            String firstParent = plugin.adoptRequests.get(uuid);
            Bukkit.getPlayer(UUID.fromString(firstParent)).sendMessage(plugin.prefix + plugin.messages.get("player_offline").replace("%player%", playerFam.getName()) + plugin.messages.get("adopt_cancel"));
            plugin.adoptRequests.remove(uuid);
        }
        if (plugin.adoptRequests.containsValue(uuid)) {
            String child = plugin.adoptRequests.inverse().get(uuid);
            Bukkit.getPlayer(UUID.fromString(child)).sendMessage(plugin.prefix + plugin.messages.get("player_offline").replace("%player%", playerFam.getName()) + plugin.messages.get("adoptCancel"));
            plugin.adoptRequests.inverse().remove(uuid);
        }


    }
}