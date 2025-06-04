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
import de.janschuri.lunaticlib.common.config.HasMessageKeys;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;

import java.nio.file.LinkOption;
import java.util.concurrent.TimeUnit;

import static de.janschuri.lunaticfamily.common.handler.FamilyPlayer.find;
import static de.janschuri.lunaticfamily.common.handler.FamilyPlayer.findOrCreate;

public class JoinListenerExecuter implements HasMessageKeys {

    private static final CommandMessageKey MARRY_PARTNER_OFFLINE_MK = new LunaticCommandMessageKey(new Marry(), "partner_offline")
            .defaultMessage("en", "Your partner is offline.")
            .defaultMessage("de", "Dein Partner ist offline.");
    private static final CommandMessageKey MARRY_PARTNER_ONLINE_MK = new LunaticCommandMessageKey(new Marry(), "partner_online")
            .defaultMessage("en", "Your partner is online.")
            .defaultMessage("de", "Dein Partner ist online.");
    private static final CommandMessageKey MARRY_PARTNER_JOINED_MK = new LunaticCommandMessageKey(new Marry(), "partner_joined")
            .defaultMessage("en", "Your partner has joined the server.")
            .defaultMessage("de", "Dein Partner ist dem Server beigetreten.");


    public static boolean execute(PlayerSender sender) {
        return execute(sender, true);
    }

    public static boolean execute(PlayerSender sender, boolean isFirstJoin) {
        Runnable runnable = () -> {
            LanguageConfigImpl languageConfig = LunaticFamily.getLanguageConfig();
            FamilyPlayer playerFam = findOrCreate(sender.getUniqueId());
            String skinURL = sender.getSkinURL();

            if (skinURL != null) {
                playerFam.updateSkinURL(skinURL);
            }
            playerFam.save();

            playerFam.updateFamilyTree();

            if (!isFirstJoin) {
                return;
            }

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
