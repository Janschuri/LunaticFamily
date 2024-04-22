package de.janschuri.lunaticFamily.listener;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.external.FamilyTree;
import de.janschuri.lunaticFamily.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

public class JoinEvent {

    public boolean execute(PlayerCommandSender sender) {

        FamilyPlayer playerFam = new FamilyPlayer(sender.getUniqueId());

        final Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                if (PluginConfig.enabledCrazyAdvancementAPI) {
                    new FamilyTree(playerFam.getID());
                }
                if (playerFam.isMarried()) {
                    PlayerCommandSender partner = sender.getPlayerCommandSender(playerFam.getPartner().getUniqueId());
                        if (partner.isOnline()) {
                            partner.sendMessage(Language.prefix + Language.getMessage("marry_partner_online"));
                            sender.sendMessage(Language.prefix + Language.getMessage("marry_partner_online"));
                        } else {
                            sender.sendMessage(Language.prefix + Language.getMessage("marry_partner_offline"));
                        }
                }
            }
        };

        Utils.getTimer().schedule(task, (long) (0.2 * 1000));

        return true;
    }
}
