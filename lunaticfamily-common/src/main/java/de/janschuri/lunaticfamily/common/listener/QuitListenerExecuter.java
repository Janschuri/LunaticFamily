package de.janschuri.lunaticfamily.common.listener;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.adopt.AdoptDeny;
import de.janschuri.lunaticfamily.common.commands.marry.Marry;
import de.janschuri.lunaticfamily.common.commands.marry.MarryDeny;
import de.janschuri.lunaticfamily.common.commands.sibling.SiblingDeny;
import de.janschuri.lunaticfamily.common.config.LanguageConfigImpl;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.command.LunaticCommandMessageKey;
import de.janschuri.lunaticlib.common.command.LunaticMessageKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.UUID;

import static de.janschuri.lunaticfamily.common.handler.FamilyPlayer.findOrCreate;

public class QuitListenerExecuter {

    private static final MessageKey PLAYER_QUIT = new LunaticMessageKey("player_quit");
    private static final CommandMessageKey MARRY_CANCEL_MK = new LunaticCommandMessageKey(new MarryDeny(), "cancel");
    private static final CommandMessageKey ADOPT_CANCEL_MK = new LunaticCommandMessageKey(new AdoptDeny(), "cancel");
    private static final CommandMessageKey SIBLING_CANCEL_MK = new LunaticCommandMessageKey(new SiblingDeny(), "cancel");
    private static final CommandMessageKey MARRY_PARTNER_LEFT_MK = new LunaticCommandMessageKey(new Marry(), "partner_left");


    public static boolean execute(PlayerSender player) {

        Logger.debugLog("QuitListenerExecuter: " + player.getName() + " quit");

        LanguageConfigImpl languageConfig = LunaticFamily.getLanguageConfig();
        UUID uuid = player.getUniqueId();
        FamilyPlayer playerFam = findOrCreate(uuid);

        if (LunaticFamily.marryRequests.containsValue(uuid) || LunaticFamily.marryRequests.containsKey(uuid) || LunaticFamily.marryPriests.containsKey(uuid)) {

            if (LunaticFamily.marryPriests.containsKey(uuid)) {

                UUID priestUUID = LunaticFamily.marryPriests.get(uuid);
                PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
                priest.chat(languageConfig.getMessage(PLAYER_QUIT.noPrefix()).replaceText(getTextReplacementConfig("%player%", playerFam.getName())) + " " + languageConfig.getMessage(MARRY_CANCEL_MK.noPrefix()));
            } else {
                UUID partnerUUID = LunaticFamily.marryRequests.get(uuid);
                PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);
                partner.sendMessage(languageConfig.getMessage(PLAYER_QUIT)
                        .replaceText(getTextReplacementConfig("%player%", playerFam.getName()))
                        .append(Component.space())
                        .append(languageConfig.getMessage(MARRY_CANCEL_MK.noPrefix())));
            }
        }

        if (LunaticFamily.adoptRequests.containsKey(uuid)) {
            UUID firstParentUUID = LunaticFamily.adoptRequests.get(uuid);
            PlayerSender firstParent = LunaticLib.getPlatform().getPlayerSender(firstParentUUID);
            firstParent.sendMessage(languageConfig.getMessage(PLAYER_QUIT)
                    .replaceText(getTextReplacementConfig("%player%", playerFam.getName()))
                    .append(languageConfig.getMessage(ADOPT_CANCEL_MK)));
        }

        if (LunaticFamily.siblingRequests.containsValue(uuid)) {
            UUID siblingUUID = LunaticFamily.siblingRequests.inverse().get(uuid);
            PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingUUID);
            sibling.sendMessage(languageConfig.getMessage(PLAYER_QUIT)
                    .replaceText(getTextReplacementConfig("%player%", playerFam.getName()))
                    .append(languageConfig.getMessage(SIBLING_CANCEL_MK)));
        }

        if (playerFam.isMarried()) {
            PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(playerFam.getPartner().getUUID());
            if (partner.isOnline()) {
                partner.sendMessage(languageConfig.getMessage(MARRY_PARTNER_LEFT_MK));
            }
        }

        removeAllRequests(uuid);

        return true;
    }

    private static TextReplacementConfig getTextReplacementConfig(String match, String replacement) {
        return TextReplacementConfig.builder()
                .match(match)
                .replacement(replacement)
                .build();
    }

    private static void removeAllRequests(UUID uuid) {
        LunaticFamily.marryRequests.remove(uuid);
        LunaticFamily.marryRequests.inverse().remove(uuid);
        LunaticFamily.marryPriestRequests.remove(uuid);
        LunaticFamily.marryPriestRequests.inverse().remove(uuid);
        LunaticFamily.marryPriests.remove(uuid);
        LunaticFamily.marryPriests.inverse().remove(uuid);
        LunaticFamily.adoptRequests.remove(uuid);
        LunaticFamily.adoptRequests.inverse().remove(uuid);
        LunaticFamily.adoptPriestRequests.remove(uuid);
        LunaticFamily.adoptPriestRequests.inverse().remove(uuid);
        LunaticFamily.adoptPriests.remove(uuid);
        LunaticFamily.adoptPriests.inverse().remove(uuid);
        LunaticFamily.siblingRequests.remove(uuid);
        LunaticFamily.siblingRequests.inverse().remove(uuid);
        LunaticFamily.siblingPriestRequests.remove(uuid);
        LunaticFamily.siblingPriestRequests.inverse().remove(uuid);
        LunaticFamily.siblingPriests.remove(uuid);
        LunaticFamily.siblingPriests.inverse().remove(uuid);

    }
}
