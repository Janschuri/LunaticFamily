package de.janschuri.lunaticfamily.common.listener;

import de.janschuri.lunaticfamily.LanguageConfig;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.concurrent.TimeUnit;

public class JoinListenerExecuter {

    public static boolean execute(PlayerSender sender) {

        if (!Utils.isPlayerOnRegisteredServer(sender.getUniqueId())) {
            return true;
        }

        LanguageConfig languageConfig = LunaticFamily.getLanguageConfig();
        FamilyPlayerImpl playerFam = new FamilyPlayerImpl(sender.getUniqueId());

        Runnable runnable = () -> {
            playerFam.updateFamilyTree();

            if (playerFam.isMarried()) {
                PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(playerFam.getPartner().getUniqueId());
                if (partner.isOnline()) {
                    partner.sendMessage(languageConfig.getPrefix() + languageConfig.getMessage("marry_partner_online"));
                    sender.sendMessage(languageConfig.getPrefix() + languageConfig.getMessage("marry_partner_online"));
                } else {
                    sender.sendMessage(languageConfig.getPrefix() + languageConfig.getMessage("marry_partner_offline"));
                }
            }
        };

        Utils.scheduleTask(runnable, 200L, TimeUnit.MILLISECONDS);

        return true;
    }
}
