package de.janschuri.lunaticfamily.common.listener;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.adopt.AdoptDeny;
import de.janschuri.lunaticfamily.common.commands.marry.Marry;
import de.janschuri.lunaticfamily.common.commands.marry.MarryDeny;
import de.janschuri.lunaticfamily.common.commands.sibling.SiblingDeny;
import de.janschuri.lunaticfamily.common.config.LanguageConfigImpl;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.handler.RequestHandler;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import de.janschuri.lunaticlib.common.config.LunaticMessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.UUID;

import static de.janschuri.lunaticfamily.common.handler.FamilyPlayer.findOrCreate;

public class QuitListenerExecuter {

    private static final MessageKey PLAYER_QUIT = new LunaticMessageKey("player_quit");
    private static final CommandMessageKey MARRY_CANCEL_MK = new LunaticCommandMessageKey(new MarryDeny(), "cancel")
            .defaultMessage("en", "The marriage proposal has been canceled.")
            .defaultMessage("de", "Die Heiratsanfrage wurde abgebrochen.");
    private static final CommandMessageKey ADOPT_CANCEL_MK = new LunaticCommandMessageKey(new AdoptDeny(), "cancel")
            .defaultMessage("en", "The adoption proposal has been canceled.")
            .defaultMessage("de", "Die Adoptionsanfrage wurde abgebrochen.");
    private static final CommandMessageKey SIBLING_CANCEL_MK = new LunaticCommandMessageKey(new SiblingDeny(), "cancel")
            .defaultMessage("en", "The sibling proposal has been canceled.")
            .defaultMessage("de", "Die Geschwisteranfrage wurde abgebrochen.");
    private static final CommandMessageKey MARRY_PARTNER_LEFT_MK = new LunaticCommandMessageKey(new Marry(), "partner_left")
            .defaultMessage("en", "Your partner has left the server.")
            .defaultMessage("de", "Dein Partner hat den Server verlassen.");


    public static boolean execute(PlayerSender player) {

        LanguageConfigImpl languageConfig = LunaticFamily.getLanguageConfig();
        UUID uuid = player.getUniqueId();
        FamilyPlayer playerFam = findOrCreate(uuid);

        if (playerFam.isMarried()) {
            PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(playerFam.getPartner().getUUID());
            if (partner.isOnline()) {
                partner.sendMessage(languageConfig.getMessage(MARRY_PARTNER_LEFT_MK));
            }
        }

        RequestHandler.cancelAllRequests(uuid);

        return true;
    }

    private static TextReplacementConfig getTextReplacementConfig(String match, String replacement) {
        return TextReplacementConfig.builder()
                .match(match)
                .replacement(replacement)
                .build();
    }
}
