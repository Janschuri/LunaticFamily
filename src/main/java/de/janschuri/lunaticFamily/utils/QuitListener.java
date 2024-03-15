package de.janschuri.lunaticFamily.utils;

import de.janschuri.lunaticFamily.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        FamilyManager playerFam = new FamilyManager(uuid);

        if (Main.marryRequests.containsValue(uuid) || Main.marryRequests.containsKey(uuid) || Main.marryPriest.containsKey(uuid)) {

            if (Main.marryPriest.containsKey(uuid)) {

                String priestUUID = Main.marryPriest.get(uuid);
                FamilyManager priestFam = new FamilyManager(priestUUID);
                priestFam.chat(Main.getMessage("player_quit").replace("%player%", playerFam.getName()) + " " + Main.getMessage("marry_cancel"));
            } else {
                String partnerUUID = Main.marryRequests.get(uuid);
                FamilyManager partnerFam = new FamilyManager(partnerUUID);
                partnerFam.sendMessage(Main.getMessage("player_quit").replace("%player%", playerFam.getName()) + " " + Main.getMessage("marry_cancel"));
            }

            Main.marryRequests.remove(uuid);
            Main.marryRequests.inverse().remove(uuid);
            Main.marryPriestRequests.remove(uuid);
            Main.marryPriestRequests.inverse().remove(uuid);
            Main.marryPriest.remove(uuid);
            Main.marryPriest.inverse().remove(uuid);
        }

        if (Main.adoptRequests.containsKey(uuid)) {
            String firstParentUUID = Main.adoptRequests.get(uuid);
            FamilyManager firstParentFam = new FamilyManager(firstParentUUID);
            firstParentFam.sendMessage(Main.prefix + Main.getMessage("player_offline").replace("%player%", playerFam.getName()) + Main.getMessage("adopt_cancel"));
            Main.adoptRequests.remove(uuid);
        }
        if (Main.adoptRequests.containsValue(uuid)) {
            String childUUID = Main.adoptRequests.inverse().get(uuid);
            FamilyManager childFam = new FamilyManager(childUUID);
            childFam.sendMessage(Main.prefix + Main.getMessage("player_offline").replace("%player%", playerFam.getName()) + Main.getMessage("adoptCancel"));
            Main.adoptRequests.inverse().remove(uuid);
        }


    }
}