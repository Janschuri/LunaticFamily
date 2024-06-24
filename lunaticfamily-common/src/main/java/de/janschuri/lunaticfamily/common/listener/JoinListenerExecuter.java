package de.janschuri.lunaticfamily.common.listener;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.marry.Marry;
import de.janschuri.lunaticfamily.common.config.LanguageConfigImpl;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JoinListenerExecuter {

    private static final CommandMessageKey marryPartnerOfflineMK = new CommandMessageKey(new Marry(), "partner_offline");
    private static final CommandMessageKey marryPartnerOnlineMK = new CommandMessageKey(new Marry(), "partner_online");

    private static List<String> registeredServers = new ArrayList<>();


    public static boolean execute(PlayerSender sender) {

        Logger.debugLog("JoinListenerExecuter: " + sender.getName() + " joined");

        if (!Utils.isPlayerOnRegisteredServer(sender))  {
            Logger.debugLog("JoinListenerExecuter: " + sender.getName() + " is not on a registered server");
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
