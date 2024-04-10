package de.janschuri.lunaticFamily.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        FamilyPlayer playerFam = new FamilyPlayer(uuid);

        if (LunaticFamily.marryRequests.containsValue(uuid) || LunaticFamily.marryRequests.containsKey(uuid) || LunaticFamily.marryPriest.containsKey(uuid)) {

            if (LunaticFamily.marryPriest.containsKey(uuid)) {

                String priestUUID = LunaticFamily.marryPriest.get(uuid);
                FamilyPlayer priestFam = new FamilyPlayer(priestUUID);
                priestFam.chat(Language.getMessage("player_quit").replace("%player%", playerFam.getName()) + " " + Language.getMessage("marry_cancel"));
            } else {
                String partnerUUID = LunaticFamily.marryRequests.get(uuid);
                FamilyPlayer partnerFam = new FamilyPlayer(partnerUUID);
                partnerFam.sendMessage(Language.getMessage("player_quit").replace("%player%", playerFam.getName()) + " " + Language.getMessage("marry_cancel"));
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
            firstParentFam.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", playerFam.getName()) + Language.getMessage("adopt_cancel"));
            LunaticFamily.adoptRequests.remove(uuid);
        }
        if (LunaticFamily.adoptRequests.containsValue(uuid)) {
            String childUUID = LunaticFamily.adoptRequests.inverse().get(uuid);
            FamilyPlayer childFam = new FamilyPlayer(childUUID);
            childFam.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", playerFam.getName()) + Language.getMessage("adoptCancel"));
            LunaticFamily.adoptRequests.inverse().remove(uuid);
        }

        if (playerFam.isMarried() && !LunaticFamily.isProxy) {
            String parterUUID = playerFam.getPartner().getUUID();
            FamilyPlayer partnerFam = new FamilyPlayer(parterUUID);
            partnerFam.sendMessage(Language.prefix + Language.getMessage("marry_partner_offline") + 7);
        }
    }
}