package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.MarrySubcommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestMarrySubcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryAcceptSubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey openRequestPartnerMK = new CommandMessageKey(this,"open_request_partner");
    private final CommandMessageKey noRequestMK = new CommandMessageKey(this,"no_request");
    private final CommandMessageKey tooManyChildrenMK = new CommandMessageKey(this,"too_many_children");
    private final CommandMessageKey completeMK = new CommandMessageKey(this,"complete");
    private final CommandMessageKey priestRequestMK = new CommandMessageKey(new PriestMarrySubcommand(),"request");
    private final CommandMessageKey priestYesMK = new CommandMessageKey(new PriestMarrySubcommand(),"yes");
    private final CommandMessageKey priestNoMK = new CommandMessageKey(new PriestMarrySubcommand(),"no");
    private final CommandMessageKey priestCompleteMK = new CommandMessageKey(new PriestMarrySubcommand(),"complete");
    private final CommandMessageKey priestRequestExpiredPriestMK = new CommandMessageKey(new PriestMarrySubcommand(),"request_expired_priest");
    private final CommandMessageKey priestRequestExpiredPlayerMK = new CommandMessageKey(new PriestMarrySubcommand(),"request_expired_player");



    @Override
    public String getPermission() {
        return "lunaticfamily.marry";
    }

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public MarrySubcommand getParentCommand() {
        return new MarrySubcommand();
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
            sender.sendMessage(getMessage(openRequestPartnerMK));
            return true;
        }

        if (LunaticFamily.marryRequests.containsKey(player.getUniqueId())) {
            return proceedRequest(player);
        }

        if (LunaticFamily.marryPriestRequests.containsKey(player.getUniqueId())) {
            return proceedPriestRequest(player);
        }

        sender.sendMessage(getMessage(noRequestMK));
        return true;
    }

    private boolean proceedRequest(PlayerSender player) {
        UUID playerUUID = player.getUniqueId();
        FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

        UUID partnerUUID = LunaticFamily.marryRequests.get(playerUUID);
        FamilyPlayerImpl partnerFam = new FamilyPlayerImpl(partnerUUID);
        PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

        if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
            int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
            player.sendMessage(getMessage(tooManyChildrenMK)
                    .replaceText(getTextReplacementConfig("%partner%", partnerFam.getName()))
                    .replaceText(getTextReplacementConfig("%amount%", Integer.toString(amountDiff))));
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
        FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);
        UUID partnerUUID = LunaticFamily.marryPriestRequests.get(playerUUID);
        FamilyPlayerImpl partnerFam = new FamilyPlayerImpl(partnerUUID);
        PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

        if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
            int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
            player.sendMessage(getMessage(tooManyChildrenMK)
                    .replaceText(getTextReplacementConfig("%partner%", partnerFam.getName()))
                    .replaceText(getTextReplacementConfig("%amount%", Integer.toString(amountDiff))));
            return true;
        }

        UUID priestUUID = LunaticFamily.marryPriests.get(playerUUID);
        PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);

        if (!Utils.hasEnoughMoney(player.getServerName(), partnerUUID, WithdrawKey.MARRY_PROPOSING_PLAYER)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", partnerFam.getName())));
            return true;
        }
        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_PROPOSED_PLAYER)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", player.getName())));
            return true;
        }

        LunaticFamily.marryPriestRequests.remove(playerUUID);
        LunaticFamily.marryRequests.put(partnerUUID, playerUUID);

        Runnable runnable = () -> {
            if (LunaticFamily.marryRequests.containsKey(partnerUUID)) {
                LunaticFamily.marryRequests.remove(partnerUUID);
                LunaticFamily.marryPriests.remove(partnerUUID);
                priest.sendMessage(getMessage(priestRequestExpiredPriestMK)
                        .replaceText(getTextReplacementConfig("%player1%", player.getName()))
                        .replaceText(getTextReplacementConfig("%player2%", partner.getName())));
                player.sendMessage(getMessage(priestRequestExpiredPlayerMK)
                        .replaceText(getTextReplacementConfig("%player%", partner.getName())));
                partner.sendMessage(getMessage(priestRequestExpiredPlayerMK)
                        .replaceText(getTextReplacementConfig("%player%", player.getName())));
            }
        };

        Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);

        player.chat(getLanguageConfig().getMessageAsString(priestYesMK, false));


        priest.chat(getLanguageConfig().getMessageAsString(priestRequestMK, false)
                .replace("%player1%", partnerFam.getName())
                .replace("%player2%", playerFam.getName()));


        partner.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                getMessage(priestYesMK, false),
                "/family marry accept",
                getMessage(priestNoMK, false),
                "/family marry deny"));

        return true;
    }

    private boolean acceptRequest(PlayerSender player, PlayerSender partner) {
        UUID playerUUID = player.getUniqueId();
        UUID partnerUUID = partner.getUniqueId();

        FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);
        FamilyPlayerImpl partnerFam = new FamilyPlayerImpl(partnerUUID);

        if (!Utils.hasEnoughMoney(player.getServerName(), partnerUUID, WithdrawKey.MARRY_PROPOSED_PLAYER)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", partnerFam.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_PROPOSED_PLAYER)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", player.getName())));
            return true;
        }

        player.sendMessage(getMessage(completeMK));
        partner.sendMessage(getMessage(completeMK));

        Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_PROPOSED_PLAYER);
        Utils.withdrawMoney(player.getServerName(), partnerUUID, WithdrawKey.MARRY_PROPOSING_PLAYER);

        LunaticFamily.marryRequests.remove(playerUUID);
        LunaticFamily.marryPriestRequests.remove(partnerUUID);
        LunaticFamily.marryPriests.remove(partnerUUID);

        playerFam.marry(partnerFam.getId());

        for (String command : LunaticFamily.getConfig().getSuccessCommands("marry")) {
            command = command.replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName());
            LunaticLib.getPlatform().sendConsoleCommand(command);
        }

        return true;
    }

    private boolean acceptPriestRequest(PlayerSender player, PlayerSender partner) {
        UUID playerUUID = player.getUniqueId();
        UUID partnerUUID = partner.getUniqueId();

        FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);
        FamilyPlayerImpl partnerFam = new FamilyPlayerImpl(partnerUUID);

        UUID priestUUID = LunaticFamily.marryPriests.get(partnerUUID);
        FamilyPlayerImpl priestFam = new FamilyPlayerImpl(priestUUID);
        PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);

        if (!Utils.hasEnoughMoney(player.getServerName(), priestUUID, WithdrawKey.PRIEST_MARRY)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", priestFam.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_MARRY_PLAYER)) {
            player.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", player.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), partnerUUID, WithdrawKey.PRIEST_MARRY_PLAYER)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", partnerFam.getName())));
            return true;
        }



        Utils.withdrawMoney(player.getServerName(), priestUUID, WithdrawKey.PRIEST_MARRY);
        Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_MARRY_PLAYER);
        Utils.withdrawMoney(player.getServerName(), partnerUUID, WithdrawKey.PRIEST_MARRY_PLAYER);

        player.chat(getLanguageConfig().getMessageAsString(priestYesMK, false));

        priest.chat(getLanguageConfig().getMessageAsString(priestCompleteMK, false)
                .replace("%player1%", playerFam.getName())
                .replace("%player2%", partnerFam.getName()));

        LunaticFamily.marryRequests.remove(playerUUID);
        LunaticFamily.marryPriestRequests.remove(partnerUUID);
        LunaticFamily.marryPriests.remove(partnerUUID);

        playerFam.marry(partnerFam.getId(), priestFam.getId());

        for (String command : LunaticFamily.getConfig().getSuccessCommands("marry_priest")) {
            command = command.replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()).replace("%priest%", priestFam.getName());
            LunaticLib.getPlatform().sendConsoleCommand(command);
        }

        return true;
    }
}
