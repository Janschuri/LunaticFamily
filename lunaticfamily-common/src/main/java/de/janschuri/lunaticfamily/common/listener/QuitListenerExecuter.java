package de.janschuri.lunaticfamily.common.listener;

import de.janschuri.lunaticfamily.LanguageConfig;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;

public class QuitListenerExecuter {

    public static boolean execute(PlayerSender player) {
        LanguageConfig languageConfig = LunaticFamily.getLanguageConfig();
        UUID uuid = player.getUniqueId();
        FamilyPlayerImpl playerFam = new FamilyPlayerImpl(uuid);

        if (LunaticFamily.marryRequests.containsValue(uuid) || LunaticFamily.marryRequests.containsKey(uuid) || LunaticFamily.marryPriest.containsKey(uuid)) {

            if (LunaticFamily.marryPriest.containsKey(uuid)) {

                UUID priestUUID = LunaticFamily.marryPriest.get(uuid);
                PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
                priest.chat(languageConfig.getMessage("player_quit").replace("%player%", playerFam.getName()) + " " + languageConfig.getMessage("marry_cancel"));
            } else {
                UUID partnerUUID = LunaticFamily.marryRequests.get(uuid);
                PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);
                partner.sendMessage(languageConfig.getMessage("player_quit").replace("%player%", playerFam.getName()) + " " + languageConfig.getMessage("marry_cancel"));
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
            PlayerSender firstParent = LunaticLib.getPlatform().getPlayerSender(firstParentUUID);
            firstParent.sendMessage(languageConfig.getPrefix() + languageConfig.getMessage("player_offline").replace("%player%", playerFam.getName()) + languageConfig.getMessage("adopt_cancel"));
            LunaticFamily.adoptRequests.remove(uuid);
        }
        if (LunaticFamily.adoptRequests.containsValue(uuid)) {
            UUID childUUID = LunaticFamily.adoptRequests.inverse().get(uuid);
            PlayerSender child = LunaticLib.getPlatform().getPlayerSender(childUUID);
            child.sendMessage(languageConfig.getPrefix() + languageConfig.getMessage("player_offline").replace("%player%", playerFam.getName()) + languageConfig.getMessage("adoptCancel"));
            LunaticFamily.adoptRequests.inverse().remove(uuid);
        }

        if (playerFam.isMarried()) {
            PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(playerFam.getPartner().getUniqueId());
            if (partner.isOnline()) {
                partner.sendMessage(languageConfig.getPrefix() + languageConfig.getMessage("marry_partner_offline"));
            }
        }


        return true;
    }
}
