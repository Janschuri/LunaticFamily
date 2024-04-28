package de.janschuri.lunaticFamily.listener;

import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.concurrent.TimeUnit;

public class JoinEvent {

    public boolean execute(AbstractPlayerSender sender) {
        Language language = Language.getInstance();

        FamilyPlayer playerFam = new FamilyPlayer(sender.getUniqueId());

        Runnable runnable = () -> {
            if (PluginConfig.useCrazyAdvancementAPI) {
                Utils.getUtils().updateFamilyTree(playerFam.getID());
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
