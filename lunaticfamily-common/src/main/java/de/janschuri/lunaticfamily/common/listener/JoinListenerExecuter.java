package de.janschuri.lunaticfamily.common.listener;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.family.MarrySubcommand;
import de.janschuri.lunaticfamily.common.config.LanguageConfigImpl;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.concurrent.TimeUnit;

public class JoinListenerExecuter {

    private static final CommandMessageKey marryPartnerOfflineMK = new CommandMessageKey(new MarrySubcommand(), "partner_offline");
    private static final CommandMessageKey marryPartnerOnlineMK = new CommandMessageKey(new MarrySubcommand(), "partner_online");


    public static boolean execute(PlayerSender sender) {

        if (!Utils.isPlayerOnRegisteredServer(sender.getUniqueId())) {
            return true;
        }

        LanguageConfigImpl languageConfig = LunaticFamily.getLanguageConfig();
        FamilyPlayerImpl playerFam = new FamilyPlayerImpl(sender.getUniqueId());

        Runnable runnable = () -> {
            playerFam.updateFamilyTree();

            if (playerFam.isMarried()) {
                PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(playerFam.getPartner().getUniqueId());
                if (partner.isOnline()) {
                    partner.sendMessage(languageConfig.getMessage(marryPartnerOfflineMK));
                    sender.sendMessage(languageConfig.getMessage(marryPartnerOnlineMK));
                } else {
                    sender.sendMessage(languageConfig.getMessage(marryPartnerOfflineMK));
                }
            }
        };

        Utils.scheduleTask(runnable, 200L, TimeUnit.MILLISECONDS);

        return true;
    }
}
