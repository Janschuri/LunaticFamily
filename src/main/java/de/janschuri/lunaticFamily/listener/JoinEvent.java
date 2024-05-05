package de.janschuri.lunaticFamily.listener;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.futurerequests.UpdateFamilyTreeRequest;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.LunaticLib;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.Mode;

import java.util.concurrent.TimeUnit;

public class JoinEvent {

    public boolean execute(AbstractPlayerSender sender) {

        if (!Utils.isPlayerOnRegisteredServer(sender.getUniqueId())) {
            return true;
        }

        Language language = Language.getLanguage();
        FamilyPlayer playerFam = new FamilyPlayer(sender.getUniqueId());

        if (LunaticFamily.getMode() == Mode.BACKEND) {
            return true;
        }

        Runnable runnable = () -> {
            if (PluginConfig.isUseCrazyAdvancementAPI()) {
                if (LunaticLib.getMode() == Mode.PROXY) {
                    new UpdateFamilyTreeRequest().get(playerFam.getID());
                } else {
                    playerFam.updateFamilyTree();
                }
            }

            if (playerFam.isMarried()) {
                AbstractPlayerSender partner = AbstractSender.getPlayerSender(playerFam.getPartner().getUniqueId());
                if (partner.isOnline()) {
                    partner.sendMessage(language.getPrefix() + language.getMessage("marry_partner_online"));
                    sender.sendMessage(language.getPrefix() + language.getMessage("marry_partner_online"));
                } else {
                    sender.sendMessage(language.getPrefix() + language.getMessage("marry_partner_offline"));
                }
            }
        };

        Utils.scheduleTask(runnable, 200L, TimeUnit.MILLISECONDS);

        return true;
    }
}
