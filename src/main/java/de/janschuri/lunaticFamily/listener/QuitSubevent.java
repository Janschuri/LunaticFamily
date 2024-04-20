package de.janschuri.lunaticFamily.listener;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;

import java.util.UUID;

public class QuitSubevent {

    public boolean execute(PlayerCommandSender sender) {
        PlayerCommandSender player = (PlayerCommandSender) sender;
        String uuid = player.getUniqueId().toString();
        FamilyPlayer playerFam = new FamilyPlayer(uuid);

        if (LunaticFamily.marryRequests.containsValue(uuid) || LunaticFamily.marryRequests.containsKey(uuid) || LunaticFamily.marryPriest.containsKey(uuid)) {

            if (LunaticFamily.marryPriest.containsKey(uuid)) {

                UUID priestUUID = UUID.fromString(LunaticFamily.marryPriest.get(uuid));
                PlayerCommandSender priest = player.getPlayerCommandSender(priestUUID);
                priest.chat(Language.getMessage("player_quit").replace("%player%", playerFam.getName()) + " " + Language.getMessage("marry_cancel"));
            } else {
                UUID partnerUUID = UUID.fromString(LunaticFamily.marryRequests.get(uuid));
                PlayerCommandSender partner = player.getPlayerCommandSender(partnerUUID);
                partner.sendMessage(Language.getMessage("player_quit").replace("%player%", playerFam.getName()) + " " + Language.getMessage("marry_cancel"));
            }

            LunaticFamily.marryRequests.remove(uuid);
            LunaticFamily.marryRequests.inverse().remove(uuid);
            LunaticFamily.marryPriestRequests.remove(uuid);
            LunaticFamily.marryPriestRequests.inverse().remove(uuid);
            LunaticFamily.marryPriest.remove(uuid);
            LunaticFamily.marryPriest.inverse().remove(uuid);
        }

        if (LunaticFamily.adoptRequests.containsKey(uuid)) {
            UUID firstParentUUID = UUID.fromString(LunaticFamily.adoptRequests.get(uuid));
            PlayerCommandSender firstParent = player.getPlayerCommandSender(firstParentUUID);
            firstParent.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", playerFam.getName()) + Language.getMessage("adopt_cancel"));
            LunaticFamily.adoptRequests.remove(uuid);
        }
        if (LunaticFamily.adoptRequests.containsValue(uuid)) {
            UUID childUUID = UUID.fromString(LunaticFamily.adoptRequests.inverse().get(uuid));
            PlayerCommandSender child = player.getPlayerCommandSender(childUUID);
            child.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", playerFam.getName()) + Language.getMessage("adoptCancel"));
            LunaticFamily.adoptRequests.inverse().remove(uuid);
        }

        if (playerFam.isMarried() && !LunaticFamily.isProxy) {
            UUID partnerUUID = playerFam.getPartner().getUniqueId();
            PlayerCommandSender partner = player.getPlayerCommandSender(partnerUUID);
            partner.sendMessage(Language.prefix + Language.getMessage("marry_partner_offline") + 7);
        }


        return true;
    }
}
