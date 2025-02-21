package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestSibling;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SiblingAccept extends FamilyCommand implements HasParentCommand {

    private static final SiblingAccept INSTANCE = new SiblingAccept();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Accept a sibling request.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Akzeptiere eine Geschwister Anfrage.");
    private static final CommandMessageKey NO_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "no_request")
            .defaultMessage("en", "You have no sibling request.")
            .defaultMessage("de", "Du hast keine Geschwister Anfrage.");
    private static final CommandMessageKey COMPLETE_MK = new LunaticCommandMessageKey(INSTANCE, "complete")
            .defaultMessage("en", "You and %player% are now siblings!")
            .defaultMessage("de", "Du und %player% seid jetzt Geschwister!");
    private static final CommandMessageKey IS_ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE, "is_adopted")
            .defaultMessage("en", "You are adopted. Your parents can adopt a sibling for you.")
            .defaultMessage("de", "Du bist adoptiert. Deine Eltern können ein Geschwister für dich adoptieren.");


    private static final PriestSibling PRIEST_SIBLING_INSTANCE = new PriestSibling();

    private static final CommandMessageKey PRIEST_REQUEST_MK = new LunaticCommandMessageKey(PRIEST_SIBLING_INSTANCE,"request")
            .defaultMessage("en", "%player1%, would you like to be siblings with %player2% on this Minecraft server?")
            .defaultMessage("de", "%player1%, möchtest du Geschwister mit %player2% auf diesem Minecraft Server sein?");
    private static final CommandMessageKey PRIEST_YES_MK = new LunaticCommandMessageKey(PRIEST_SIBLING_INSTANCE,"yes")
            .defaultMessage("en", "Yes. I do!")
            .defaultMessage("de", "Ja. Ich will!");
    private static final CommandMessageKey PRIEST_NO_MK = new LunaticCommandMessageKey(PRIEST_SIBLING_INSTANCE,"no")
            .defaultMessage("en", "No. I don't want to.")
            .defaultMessage("de", "Nein. Ich will nicht.");
    private static final CommandMessageKey PRIEST_COMPLETE_MK = new LunaticCommandMessageKey(PRIEST_SIBLING_INSTANCE,"complete")
            .defaultMessage("en", "You are siblings!")
            .defaultMessage("de", "Ihr seid Geschwister!");
    private static final CommandMessageKey PRIEST_REQUEST_EXPIRED_PRIEST_MK = new LunaticCommandMessageKey(PRIEST_SIBLING_INSTANCE,"request_expired_priest")
            .defaultMessage("en", "The siblinghood between %player1% and %player2% has been canceled.")
            .defaultMessage("de", "Die Geschwisterschaft zwischen %player1% und %player2% wurde abgebrochen.");
    private static final CommandMessageKey PRIEST_REQUEST_EXPIRED_PLAYER_MK = new LunaticCommandMessageKey(PRIEST_SIBLING_INSTANCE,"request_expired_player")
            .defaultMessage("en", "Your siblinghood with %player% has been canceled.")
            .defaultMessage("de", "Deine Geschwisterschaft mit %player% wurde abgebrochen.");
    private static final CommandMessageKey PRIEST_IS_ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE, "is_adopted")
            .defaultMessage("en", "You are adopted. Your parents can adopt a sibling for you.")
            .defaultMessage("de", "Du bist adoptiert. Deine Eltern können ein Geschwister für dich adoptieren.");


    @Override
    public String getPermission() {
        return "lunaticfamily.sibling";
    }

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public Sibling getParentCommand() {
        return new Sibling();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender player)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        UUID playerUUID = player.getUniqueId();

        if (LunaticFamily.siblingRequests.containsKey(playerUUID)) {
            return proceedSiblingRequest(player);
        }

        if (LunaticFamily.siblingPriestRequests.containsKey(playerUUID)) {
            return proceedPriestSiblingRequest(player);
        }

        sender.sendMessage(getMessage(NO_REQUEST_MK));
        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }

    private boolean proceedPriestSiblingRequest(PlayerSender player) {
        UUID playerUUID = player.getUniqueId();
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);
        UUID siblingUUID = LunaticFamily.siblingPriestRequests.get(playerUUID);
        FamilyPlayer siblingFam = getFamilyPlayer(siblingUUID);
        PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingUUID);

        if (playerFam.isAdopted()) {
            player.sendMessage(getMessage(PRIEST_IS_ADOPTED_MK,
                placeholder("%player1%", playerFam.getName()),
                placeholder("%player2%", siblingFam.getName())));
            return true;
        }

        if (siblingFam.isAdopted()) {
            player.sendMessage(getMessage(PRIEST_IS_ADOPTED_MK,
                placeholder("%player1%", siblingFam.getName()),
                placeholder("%player2%", playerFam.getName())));
            return true;
        }

        UUID priestUUID = LunaticFamily.siblingPriests.get(playerUUID);
        PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);

        if (!Utils.hasEnoughMoney(player.getServerName(), siblingUUID, WithdrawKey.SIBLING_PROPOSING_PLAYER)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", siblingFam.getName()))
            );
            return true;
        } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.SIBLING_PROPOSED_PLAYER)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", player.getName()))
            );
            return true;
        }

        LunaticFamily.siblingPriestRequests.remove(playerUUID);
        LunaticFamily.siblingRequests.put(siblingUUID, playerUUID);

        Runnable runnable = () -> {
            if (LunaticFamily.siblingRequests.containsKey(siblingUUID)) {
                LunaticFamily.siblingRequests.remove(siblingUUID);
                LunaticFamily.siblingPriests.remove(siblingUUID);
                priest.sendMessage(getMessage(PRIEST_REQUEST_EXPIRED_PRIEST_MK,
                placeholder("%player1%", player.getName()),
                placeholder("%player2%", sibling.getName())));
                player.sendMessage(getMessage(PRIEST_REQUEST_EXPIRED_PLAYER_MK,
                placeholder("%player%", sibling.getName())));
                sibling.sendMessage(getMessage(PRIEST_REQUEST_EXPIRED_PLAYER_MK,
                placeholder("%player%", player.getName())));
            }
        };

        Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);

        player.chat(getLanguageConfig().getMessageAsString(PRIEST_YES_MK.noPrefix()));


        priest.chat(getLanguageConfig().getMessageAsString(PRIEST_REQUEST_MK.noPrefix())
                .replace("%player1%", siblingFam.getName())
                .replace("%player2%", playerFam.getName()));


        sibling.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                Component.empty(),
                getMessage(PRIEST_YES_MK.noPrefix()),
                "/family sibling accept",
                getMessage(PRIEST_NO_MK.noPrefix()),
                "/family sibling deny"),
                LunaticFamily.getConfig().decisionAsInvGUI()
        );


        return true;
    }

    private boolean proceedSiblingRequest(PlayerSender player) {
        UUID playerUUID = player.getUniqueId();
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);
        UUID siblingUUID = LunaticFamily.siblingRequests.get(playerUUID);
        FamilyPlayer siblingFam = getFamilyPlayer(siblingUUID);
        PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingUUID);

        if (playerFam.isAdopted()) {
            player.sendMessage(getMessage(IS_ADOPTED_MK,
                placeholder("%player1%", playerFam.getName()),
                placeholder("%player2%", siblingFam.getName())));
            return true;
        }

        if (siblingFam.isAdopted()) {
            player.sendMessage(getMessage(IS_ADOPTED_MK,
                placeholder("%player1%", siblingFam.getName()),
                placeholder("%player2%", playerFam.getName())));
            return true;
        }

        if (!sibling.isOnline()) {
            player.sendMessage(getMessage(PLAYER_OFFLINE_MK,
                placeholder("%player%", siblingFam.getName())));
            return true;
        }

        if (!Utils.isPlayerOnRegisteredServer(sibling)) {
            player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK,
                placeholder("%player%", sibling.getName().replace("%server%", sibling.getServerName()))));
            return true;
        }

        if (LunaticFamily.siblingPriests.containsKey(siblingUUID)) {
            return acceptPriestSiblingRequest(player, sibling);
        } else {
            return acceptSiblingRequest(player, sibling);
        }
    }

    private boolean acceptSiblingRequest(PlayerSender player, PlayerSender sibling) {
        UUID playerUUID = player.getUniqueId();
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);
        UUID siblingUUID = sibling.getUniqueId();
        FamilyPlayer siblingFam = getFamilyPlayer(siblingUUID);

        if (!Utils.hasEnoughMoney(player.getServerName(), siblingUUID, WithdrawKey.SIBLING_PROPOSED_PLAYER)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", siblingFam.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.SIBLING_PROPOSED_PLAYER)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", player.getName())));
        }

        player.sendMessage(getMessage(COMPLETE_MK,
            placeholder("%player%", siblingFam.getName())));
        sibling.sendMessage(getMessage(COMPLETE_MK));

        Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.SIBLING_PROPOSED_PLAYER);
        Utils.withdrawMoney(player.getServerName(), siblingUUID, WithdrawKey.SIBLING_PROPOSING_PLAYER);

        LunaticFamily.siblingRequests.remove(playerUUID);
        LunaticFamily.siblingPriestRequests.remove(siblingUUID);
        LunaticFamily.siblingPriests.remove(siblingUUID);

        playerFam.addSibling(siblingFam);

        for (String command : LunaticFamily.getConfig().getSuccessCommands("sibling")) {
            command = command.replace("%player1%", playerFam.getName()).replace("%player2%", siblingFam.getName());
            LunaticLib.getPlatform().sendConsoleCommand(command);
        }

        return true;
    }

    private boolean acceptPriestSiblingRequest(PlayerSender player, PlayerSender sibling) {
        UUID playerUUID = player.getUniqueId();
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);
        UUID siblingUUID = sibling.getUniqueId();
        FamilyPlayer siblingFam = getFamilyPlayer(siblingUUID);

        UUID priestUUID = LunaticFamily.siblingPriests.get(siblingUUID);
        FamilyPlayer priestFam = getFamilyPlayer(priestUUID);
        PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);

        if (!Utils.hasEnoughMoney(player.getServerName(), priestUUID, WithdrawKey.PRIEST_SIBLING)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", priestFam.getName()))
            );
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_SIBLING_PLAYER)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", player.getName()))
            );
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), siblingUUID, WithdrawKey.PRIEST_SIBLING_PLAYER)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", siblingFam.getName()))
            );
            return true;
        }

        Utils.withdrawMoney(player.getServerName(), priestUUID, WithdrawKey.PRIEST_SIBLING);
        Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_SIBLING_PLAYER);
        Utils.withdrawMoney(player.getServerName(), siblingUUID, WithdrawKey.PRIEST_SIBLING_PLAYER);

        player.chat(getLanguageConfig().getMessageAsString(PRIEST_YES_MK.noPrefix()));

        priest.chat(getLanguageConfig().getMessageAsString(PRIEST_COMPLETE_MK.noPrefix())
                .replace("%player1%", playerFam.getName())
                .replace("%player2%", siblingFam.getName()));

        LunaticFamily.siblingRequests.remove(playerUUID);
        LunaticFamily.siblingPriestRequests.remove(siblingUUID);
        LunaticFamily.siblingPriests.remove(siblingUUID);

        playerFam.addSibling(siblingFam, priestFam);

        for (String command : LunaticFamily.getConfig().getSuccessCommands("sibling_priest")) {
            command = command.replace("%player1%", playerFam.getName()).replace("%player2%", siblingFam.getName()).replace("%priest%", priestFam.getName());
            LunaticLib.getPlatform().sendConsoleCommand(command);
        }

        return true;
    }
}
