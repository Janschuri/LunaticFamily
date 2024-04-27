package de.janschuri.lunaticFamily.listener;

import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.external.FamilyTree;
import de.janschuri.lunaticFamily.utils.Logger;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class JoinEvent {

    public boolean execute(AbstractPlayerSender sender) {
        Language language = Language.getInstance();

        FamilyPlayer playerFam = new FamilyPlayer(sender.getUniqueId());
        Logger.debugLog("Player " + playerFam.getName() + " joined the server.");

        Runnable runnable = () -> {
            if (PluginConfig.enabledCrazyAdvancementAPI) {
                new FamilyTree(playerFam.getID());
            }
            if (playerFam.isMarried()) {
                AbstractPlayerSender partner = sender.getPlayerCommandSender(playerFam.getPartner().getUniqueId());
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
