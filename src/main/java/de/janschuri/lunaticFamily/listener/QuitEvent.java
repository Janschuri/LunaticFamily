package de.janschuri.lunaticFamily.listener;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.Mode;

import java.util.UUID;

public class QuitEvent {

    public boolean execute(AbstractPlayerSender player) {
        Language language = Language.getLanguage();
        UUID uuid = player.getUniqueId();
        FamilyPlayer playerFam = new FamilyPlayer(uuid);

        if (LunaticFamily.getMode() == Mode.BACKEND) {
            return true;
        }

        if (LunaticFamily.marryRequests.containsValue(uuid) || LunaticFamily.marryRequests.containsKey(uuid) || LunaticFamily.marryPriest.containsKey(uuid)) {

            if (LunaticFamily.marryPriest.containsKey(uuid)) {

                UUID priestUUID = LunaticFamily.marryPriest.get(uuid);
                AbstractPlayerSender priest = AbstractSender.getPlayerSender(priestUUID);
                priest.chat(language.getMessage("player_quit").replace("%player%", playerFam.getName()) + " " + language.getMessage("marry_cancel"));
            } else {
                UUID partnerUUID = LunaticFamily.marryRequests.get(uuid);
                AbstractPlayerSender partner = AbstractSender.getPlayerSender(partnerUUID);
                partner.sendMessage(language.getMessage("player_quit").replace("%player%", playerFam.getName()) + " " + language.getMessage("marry_cancel"));
            }

            LunaticFamily.marryRequests.remove(uuid);
            LunaticFamily.marryRequests.inverse().remove(uuid);
            LunaticFamily.marryPriestRequests.remove(uuid);
            LunaticFamily.marryPriestRequests.inverse().remove(uuid);
            LunaticFamily.marryPriest.remove(uuid);
            LunaticFamily.marryPriest.inverse().remove(uuid);
        }

        if (LunaticFamily.adoptRequests.containsKey(uuid)) {
            UUID firstParentUUID = LunaticFamily.adoptRequests.get(uuid);
            AbstractPlayerSender firstParent = AbstractSender.getPlayerSender(firstParentUUID);
            firstParent.sendMessage(language.getPrefix() + language.getMessage("player_offline").replace("%player%", playerFam.getName()) + language.getMessage("adopt_cancel"));
            LunaticFamily.adoptRequests.remove(uuid);
        }
        if (LunaticFamily.adoptRequests.containsValue(uuid)) {
            UUID childUUID = LunaticFamily.adoptRequests.inverse().get(uuid);
            AbstractPlayerSender child = AbstractSender.getPlayerSender(childUUID);
            child.sendMessage(language.getPrefix() + language.getMessage("player_offline").replace("%player%", playerFam.getName()) + language.getMessage("adoptCancel"));
            LunaticFamily.adoptRequests.inverse().remove(uuid);
        }

        if (playerFam.isMarried()) {
            AbstractPlayerSender partner = AbstractSender.getPlayerSender(playerFam.getPartner().getUniqueId());
            if (partner.isOnline()) {
                partner.sendMessage(language.getPrefix() + language.getMessage("marry_partner_offline"));
            }
        }


        return true;
    }
}
