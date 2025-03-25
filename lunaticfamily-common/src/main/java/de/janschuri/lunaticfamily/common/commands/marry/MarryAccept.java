package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestMarry;
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

public class MarryAccept extends FamilyCommand implements HasParentCommand {

    private static final MarryAccept INSTANCE = new MarryAccept();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Accept a marriage proposal.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Eine Heiratsanfrage annehmen.");
    private static final CommandMessageKey OPEN_REQUEST_PARTNER_MK = new LunaticCommandMessageKey(INSTANCE, "open_request_partner")
            .defaultMessage("en", "You must wait for your future partner's response!")
            .defaultMessage("de", "Du musst auf die Antwort deines zukünftigen Partners warten!");
    private static final CommandMessageKey NO_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "no_request")
            .defaultMessage("en", "You have no marriage proposal.")
            .defaultMessage("de", "Du hast keine Heiratsanfrage.");
    private static final CommandMessageKey TOO_MANY_CHILDREN_MK = new LunaticCommandMessageKey(INSTANCE, "too_many_children")
            .defaultMessage("en", "%player1% and %player2% have more than 2 children together. %player1% and %player2% must remove %amount% children before they can marry.")
            .defaultMessage("de", "%player1% und %player2% haben zusammen mehr als 2 Kinder. %player1% und %player2% müssen %amount% Kinder entfernen, bevor sie heiraten können.");
    private static final CommandMessageKey COMPLETE_MK = new LunaticCommandMessageKey(INSTANCE, "complete")
            .defaultMessage("en", "You are married! You may now kiss!")
            .defaultMessage("de", "Ihr seid verheiratet! Ihr dürft euch jetzt küssen!");

    private final static PriestMarry PRIEST_MARRY_INSTANCE = new PriestMarry();

    private final static CommandMessageKey PRIEST_REQUEST_MK = new LunaticCommandMessageKey(PRIEST_MARRY_INSTANCE, "request")
            .defaultMessage("en", "%player1%, would you like to be siblings with %player2% on this Minecraft server?")
            .defaultMessage("de", "%player1%, möchtest du mit %player2% auf diesem Minecraft-Server Geschwister sein?");
    private final static CommandMessageKey PRIEST_NO_MK = new LunaticCommandMessageKey(PRIEST_MARRY_INSTANCE, "no")
            .defaultMessage("en", "No. I don't want to.")
            .defaultMessage("de", "Nein. Ich möchte nicht.");
    private final static CommandMessageKey PRIEST_YES_MK = new LunaticCommandMessageKey(PRIEST_MARRY_INSTANCE, "yes")
            .defaultMessage("en", "Yes. I do!")
            .defaultMessage("de", "Ja. Ich möchte!");
    private final static CommandMessageKey PRIEST_COMPLETE_MK = new LunaticCommandMessageKey(PRIEST_MARRY_INSTANCE, "complete")
            .defaultMessage("en", "You are siblings!")
            .defaultMessage("de", "Ihr seid Geschwister!");
    private final static CommandMessageKey PRIEST_REQUEST_EXPIRED_PRIEST_MK = new LunaticCommandMessageKey(PRIEST_MARRY_INSTANCE, "request_expired_priest")
            .defaultMessage("en", "The siblinghood between %player1% and %player2% has been canceled.")
            .defaultMessage("de", "Die Geschwisterbeziehung zwischen %player1% und %player2% wurde abgebrochen.");
    private final static CommandMessageKey PRIEST_REQUEST_EXPIRED_PLAYER_MK = new LunaticCommandMessageKey(PRIEST_MARRY_INSTANCE, "request_expired_player")
            .defaultMessage("en", "Your siblinghood with %player% has been canceled.")
            .defaultMessage("de", "Deine Geschwisterbeziehung mit %player% wurde abgebrochen.");


    @Override
    public String getPermission() {
        return "lunaticfamily.marry";
    }

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public Marry getParentCommand() {
        return new Marry();
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

        if (LunaticFamily.marryPriestRequests.containsValue(player.getUniqueId())) {
            sender.sendMessage(getMessage(OPEN_REQUEST_PARTNER_MK));
            return true;
        }

        if (LunaticFamily.marryRequests.containsKey(player.getUniqueId())) {
            return proceedRequest(player);
        }

        if (LunaticFamily.marryPriestRequests.containsKey(player.getUniqueId())) {
            return proceedPriestRequest(player);
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

    private boolean proceedRequest(PlayerSender player) {
        UUID playerUUID = player.getUniqueId();
        FamilyPlayer playerFam = FamilyPlayer.find(playerUUID);

        UUID partnerUUID = LunaticFamily.marryRequests.get(playerUUID);
        FamilyPlayer partnerFam = FamilyPlayer.find(partnerUUID);
        PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

        int newChildrenAmount = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount();

        if (LunaticFamily.exceedsAdoptLimit(newChildrenAmount)) {
            int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
            player.sendMessage(getMessage(TOO_MANY_CHILDREN_MK,
                placeholder("%partner%", partnerFam.getName()),
                placeholder("%amount%", Integer.toString(amountDiff))));
            return true;
        }

        if (LunaticFamily.marryPriests.containsKey(partnerUUID)) {
            return acceptPriestRequest(player, partner);
        } else {
            return acceptRequest(player, partner);
        }
    }

    private boolean proceedPriestRequest(PlayerSender player) {
        UUID playerUUID = player.getUniqueId();
        FamilyPlayer playerFam = FamilyPlayer.find(playerUUID);
        UUID partnerUUID = LunaticFamily.marryPriestRequests.get(playerUUID);
        FamilyPlayer partnerFam = FamilyPlayer.find(partnerUUID);
        PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

        int newChildrenAmount = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount();

        if (LunaticFamily.exceedsAdoptLimit(newChildrenAmount)) {
            int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
            player.sendMessage(getMessage(TOO_MANY_CHILDREN_MK,
                placeholder("%partner%", partnerFam.getName()),
                placeholder("%amount%", Integer.toString(amountDiff))));
            return true;
        }

        UUID priestUUID = LunaticFamily.marryPriests.get(playerUUID);
        PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);

        if (!Utils.hasEnoughMoney(player.getServerName(), partnerUUID, WithdrawKey.MARRY_PROPOSING_PLAYER)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", partnerFam.getName())));
            return true;
        }
        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_PROPOSED_PLAYER)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", player.getName())));
            return true;
        }

        LunaticFamily.marryPriestRequests.remove(playerUUID);
        LunaticFamily.marryRequests.put(partnerUUID, playerUUID);

        Runnable runnable = () -> {
            if (LunaticFamily.marryRequests.containsKey(partnerUUID)) {
                LunaticFamily.marryRequests.remove(partnerUUID);
                LunaticFamily.marryPriests.remove(partnerUUID);
                priest.sendMessage(getMessage(PRIEST_REQUEST_EXPIRED_PRIEST_MK,
                placeholder("%player1%", player.getName()),
                placeholder("%player2%", partner.getName())));
                player.sendMessage(getMessage(PRIEST_REQUEST_EXPIRED_PLAYER_MK,
                placeholder("%player%", partner.getName())));
                partner.sendMessage(getMessage(PRIEST_REQUEST_EXPIRED_PLAYER_MK,
                placeholder("%player%", player.getName())));
            }
        };

        Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);

        player.chat(getLanguageConfig().getMessageAsString(PRIEST_YES_MK.noPrefix()));


        priest.chat(getLanguageConfig().getMessageAsString(PRIEST_REQUEST_MK.noPrefix())
                .replace("%player1%", partnerFam.getName())
                .replace("%player2%", playerFam.getName()));


        partner.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                Component.empty(),
                getMessage(PRIEST_YES_MK.noPrefix()),
                "/family marry accept",
                getMessage(PRIEST_NO_MK.noPrefix()),
                "/family marry deny"),
                LunaticFamily.getConfig().decisionAsInvGUI()
        );

        return true;
    }

    private boolean acceptRequest(PlayerSender player, PlayerSender partner) {
        UUID playerUUID = player.getUniqueId();
        UUID partnerUUID = partner.getUniqueId();

        FamilyPlayer playerFam = FamilyPlayer.find(playerUUID);
        FamilyPlayer partnerFam = FamilyPlayer.find(partnerUUID);

        if (!Utils.hasEnoughMoney(player.getServerName(), partnerUUID, WithdrawKey.MARRY_PROPOSED_PLAYER)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", partnerFam.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_PROPOSED_PLAYER)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", player.getName())));
            return true;
        }

        player.sendMessage(getMessage(COMPLETE_MK));
        partner.sendMessage(getMessage(COMPLETE_MK));

        Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_PROPOSED_PLAYER);
        Utils.withdrawMoney(player.getServerName(), partnerUUID, WithdrawKey.MARRY_PROPOSING_PLAYER);

        LunaticFamily.marryRequests.remove(playerUUID);
        LunaticFamily.marryPriestRequests.remove(partnerUUID);
        LunaticFamily.marryPriests.remove(partnerUUID);

        playerFam.marry(partnerFam);

        for (String command : LunaticFamily.getConfig().getSuccessCommands("marry")) {
            command = command.replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName());
            LunaticLib.getPlatform().sendConsoleCommand(command);
        }

        return true;
    }

    private boolean acceptPriestRequest(PlayerSender player, PlayerSender partner) {
        UUID playerUUID = player.getUniqueId();
        UUID partnerUUID = partner.getUniqueId();

        FamilyPlayer playerFam = FamilyPlayer.find(playerUUID);
        FamilyPlayer partnerFam = FamilyPlayer.find(partnerUUID);

        UUID priestUUID = LunaticFamily.marryPriests.get(partnerUUID);
        FamilyPlayer priestFam = FamilyPlayer.find(priestUUID);
        PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);

        if (!Utils.hasEnoughMoney(player.getServerName(), priestUUID, WithdrawKey.PRIEST_MARRY)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", priestFam.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_MARRY_PLAYER)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", player.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), partnerUUID, WithdrawKey.PRIEST_MARRY_PLAYER)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", partnerFam.getName())));
            return true;
        }



        Utils.withdrawMoney(player.getServerName(), priestUUID, WithdrawKey.PRIEST_MARRY);
        Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_MARRY_PLAYER);
        Utils.withdrawMoney(player.getServerName(), partnerUUID, WithdrawKey.PRIEST_MARRY_PLAYER);

        player.chat(getLanguageConfig().getMessageAsString(PRIEST_YES_MK.noPrefix()));

        priest.chat(getLanguageConfig().getMessageAsString(PRIEST_COMPLETE_MK.noPrefix())
                .replace("%player1%", playerFam.getName())
                .replace("%player2%", partnerFam.getName()));

        LunaticFamily.marryRequests.remove(playerUUID);
        LunaticFamily.marryPriestRequests.remove(partnerUUID);
        LunaticFamily.marryPriests.remove(partnerUUID);

        playerFam.marry(partnerFam, priestFam);

        for (String command : LunaticFamily.getConfig().getSuccessCommands("marry_priest")) {
            command = command.replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()).replace("%priest%", priestFam.getName());
            LunaticLib.getPlatform().sendConsoleCommand(command);
        }

        return true;
    }
}
