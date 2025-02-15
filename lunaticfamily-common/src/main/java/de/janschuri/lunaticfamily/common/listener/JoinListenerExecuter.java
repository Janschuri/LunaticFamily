package de.janschuri.lunaticfamily.common.listener;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.marry.Marry;
import de.janschuri.lunaticfamily.common.config.LanguageConfigImpl;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static de.janschuri.lunaticfamily.common.handler.FamilyPlayer.findOrCreate;

public class JoinListenerExecuter {

    private static final CommandMessageKey MARRY_PARTNER_OFFLINE_MK = new LunaticCommandMessageKey(new Marry(), "partner_offline");
    private static final CommandMessageKey MARRY_PARTNER_ONLINE_MK = new LunaticCommandMessageKey(new Marry(), "partner_online");
    private static final CommandMessageKey MARRY_PARTNER_JOINED_MK = new LunaticCommandMessageKey(new Marry(), "partner_joined");

    private static final List<String> registeredServers = new ArrayList<>();


    public static boolean execute(PlayerSender sender) {

        Logger.debugLog("JoinListenerExecuter: " + sender.getName() + " joined");

        if (!Utils.isPlayerOnRegisteredServer(sender))  {
            Logger.debugLog("JoinListenerExecuter: " + sender.getName() + " is not on a registered server");
            return true;
        }


        LanguageConfigImpl languageConfig = LunaticFamily.getLanguageConfig();
        FamilyPlayer playerFam = findOrCreate(sender.getUniqueId());
        playerFam.save();

        Runnable runnable = () -> {
            playerFam.updateFamilyTree();

            if (playerFam.isMarried()) {
                PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(playerFam.getPartner().getUUID());

                if (partner.isOnline()) {
                    partner.sendMessage(languageConfig.getMessage(MARRY_PARTNER_JOINED_MK));
                    sender.sendMessage(languageConfig.getMessage(MARRY_PARTNER_ONLINE_MK));
                } else {
                    sender.sendMessage(languageConfig.getMessage(MARRY_PARTNER_OFFLINE_MK));
                }
            }
        };

        Utils.scheduleTask(runnable, 200L, TimeUnit.MILLISECONDS);

        return true;
    }
}
