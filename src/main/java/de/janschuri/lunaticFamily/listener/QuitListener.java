package de.janschuri.lunaticFamily.listener;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        FamilyPlayer playerFam = new FamilyPlayer(uuid);

        if (LunaticFamily.marryRequests.containsValue(uuid) || LunaticFamily.marryRequests.containsKey(uuid) || LunaticFamily.marryPriest.containsKey(uuid)) {

            if (LunaticFamily.marryPriest.containsKey(uuid)) {

                String priestUUID = LunaticFamily.marryPriest.get(uuid);
                FamilyPlayer priestFam = new FamilyPlayer(priestUUID);
                priestFam.chat(LunaticFamily.getMessage("player_quit").replace("%player%", playerFam.getName()) + " " + LunaticFamily.getMessage("marry_cancel"));
            } else {
                String partnerUUID = LunaticFamily.marryRequests.get(uuid);
                FamilyPlayer partnerFam = new FamilyPlayer(partnerUUID);
                partnerFam.sendMessage(LunaticFamily.getMessage("player_quit").replace("%player%", playerFam.getName()) + " " + LunaticFamily.getMessage("marry_cancel"));
            }

            LunaticFamily.marryRequests.remove(uuid);
            LunaticFamily.marryRequests.inverse().remove(uuid);
            LunaticFamily.marryPriestRequests.remove(uuid);
            LunaticFamily.marryPriestRequests.inverse().remove(uuid);
            LunaticFamily.marryPriest.remove(uuid);
            LunaticFamily.marryPriest.inverse().remove(uuid);
        }

        if (LunaticFamily.adoptRequests.containsKey(uuid)) {
            String firstParentUUID = LunaticFamily.adoptRequests.get(uuid);
            FamilyPlayer firstParentFam = new FamilyPlayer(firstParentUUID);
            firstParentFam.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("player_offline").replace("%player%", playerFam.getName()) + LunaticFamily.getMessage("adopt_cancel"));
            LunaticFamily.adoptRequests.remove(uuid);
        }
        if (LunaticFamily.adoptRequests.containsValue(uuid)) {
            String childUUID = LunaticFamily.adoptRequests.inverse().get(uuid);
            FamilyPlayer childFam = new FamilyPlayer(childUUID);
            childFam.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("player_offline").replace("%player%", playerFam.getName()) + LunaticFamily.getMessage("adoptCancel"));
            LunaticFamily.adoptRequests.inverse().remove(uuid);
        }


    }
}