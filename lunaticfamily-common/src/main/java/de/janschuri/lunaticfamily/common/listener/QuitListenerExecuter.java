package de.janschuri.lunaticfamily.common.listener;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.adopt.AdoptDenySubcommand;
import de.janschuri.lunaticfamily.common.commands.family.MarrySubcommand;
import de.janschuri.lunaticfamily.common.commands.marry.MarryDenySubcommand;
import de.janschuri.lunaticfamily.common.config.LanguageConfigImpl;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import java.util.UUID;

public class QuitListenerExecuter {

    private static final MessageKey playerQuitMK = new MessageKey("player_quit");
    private static final CommandMessageKey marryCancelMK = new CommandMessageKey(new MarryDenySubcommand(), "cancel");
    private static final CommandMessageKey adoptCancelMK = new CommandMessageKey(new AdoptDenySubcommand(), "cancel");
    private static final CommandMessageKey marryPartnerOfflineMK = new CommandMessageKey(new MarrySubcommand(), "partner_offline");


    public static boolean execute(PlayerSender player) {

        Logger.debugLog("QuitListenerExecuter: " + player.getName() + " quit");

        LanguageConfigImpl languageConfig = LunaticFamily.getLanguageConfig();
        UUID uuid = player.getUniqueId();
        FamilyPlayerImpl playerFam = new FamilyPlayerImpl(uuid);

        if (LunaticFamily.marryRequests.containsValue(uuid) || LunaticFamily.marryRequests.containsKey(uuid) || LunaticFamily.marryPriest.containsKey(uuid)) {

            if (LunaticFamily.marryPriest.containsKey(uuid)) {

                UUID priestUUID = LunaticFamily.marryPriest.get(uuid);
                PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);
                priest.chat(languageConfig.getMessage(playerQuitMK, false).replaceText(getTextReplacementConfig("%player%", playerFam.getName())) + " " + languageConfig.getMessage(marryCancelMK, false));
            } else {
                UUID partnerUUID = LunaticFamily.marryRequests.get(uuid);
                PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);
                partner.sendMessage(languageConfig.getMessage(playerQuitMK)
                        .replaceText(getTextReplacementConfig("%player%", playerFam.getName()))
                        .append(Component.space())
                        .append(languageConfig.getMessage(marryCancelMK, false)));
            }

            LunaticFamily.marryRequests.remove(uuid);
            LunaticFamily.marryRequests.inverse().remove(uuid);
            LunaticFamily.marryPriestRequests.remove(uuid);
            LunaticFamily.marryPriestRequests.inverse().remove(uuid);
            LunaticFamily.marryPriest.remove(uuid);
            LunaticFamily.marryPriest.inverse().remove(uuid);
        }

        if (LunaticFamily.adoptRequests.containsKey(uuid)) {
            UUID firstParentUUID = LunaticFamily.adoptRequests.get(uuid);
            PlayerSender firstParent = LunaticLib.getPlatform().getPlayerSender(firstParentUUID);
            firstParent.sendMessage(languageConfig.getMessage(playerQuitMK)
                    .replaceText(getTextReplacementConfig("%player%", playerFam.getName()))
                    .append(languageConfig.getMessage(adoptCancelMK)));
            LunaticFamily.adoptRequests.remove(uuid);
        }

        if (LunaticFamily.adoptRequests.containsValue(uuid)) {
            UUID childUUID = LunaticFamily.adoptRequests.inverse().get(uuid);
            PlayerSender child = LunaticLib.getPlatform().getPlayerSender(childUUID);
            child.sendMessage(languageConfig.getMessage(playerQuitMK)
                    .replaceText(getTextReplacementConfig("%player%", playerFam.getName()))
                    .append(languageConfig.getMessage(adoptCancelMK)));
            LunaticFamily.adoptRequests.inverse().remove(uuid);
        }

        if (playerFam.isMarried()) {
            PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(playerFam.getPartner().getUniqueId());
            if (partner.isOnline()) {
                partner.sendMessage(languageConfig.getMessage(marryPartnerOfflineMK));
            }
        }


        return true;
    }

    private static TextReplacementConfig getTextReplacementConfig(String match, String replacement) {
        return TextReplacementConfig.builder()
                .match(match)
                .replacement(replacement)
                .build();
    }
}
