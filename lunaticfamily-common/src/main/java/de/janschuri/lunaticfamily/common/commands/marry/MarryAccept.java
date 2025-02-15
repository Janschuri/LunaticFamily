package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestMarry;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
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

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryAccept extends FamilyCommand implements HasParentCommand {

    private final CommandMessageKey helpMK = new LunaticCommandMessageKey(this,"help");
    private final CommandMessageKey openRequestPartnerMK = new LunaticCommandMessageKey(this,"open_request_partner");
    private final CommandMessageKey noRequestMK = new LunaticCommandMessageKey(this,"no_request");
    private final CommandMessageKey tooManyChildrenMK = new LunaticCommandMessageKey(this,"too_many_children");
    private final CommandMessageKey completeMK = new LunaticCommandMessageKey(this,"complete");
    private final CommandMessageKey priestRequestMK = new LunaticCommandMessageKey(new PriestMarry(),"request");
    private final CommandMessageKey priestYesMK = new LunaticCommandMessageKey(new PriestMarry(),"yes");
    private final CommandMessageKey priestNoMK = new LunaticCommandMessageKey(new PriestMarry(),"no");
    private final CommandMessageKey priestCompleteMK = new LunaticCommandMessageKey(new PriestMarry(),"complete");
    private final CommandMessageKey priestRequestExpiredPriestMK = new LunaticCommandMessageKey(new PriestMarry(),"request_expired_priest");
    private final CommandMessageKey priestRequestExpiredPlayerMK = new LunaticCommandMessageKey(new PriestMarry(),"request_expired_player");



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
        int playerID = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("uuid", playerUUID).findOne().getId();
        FamilyPlayer playerFam = getFamilyPlayer(playerID);

        UUID partnerUUID = LunaticFamily.marryRequests.get(playerUUID);
        int partnerID = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("uuid", partnerUUID).findOne().getId();
        FamilyPlayer partnerFam = getFamilyPlayer(partnerID);
        PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

        if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
            int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
            player.sendMessage(getMessage(tooManyChildrenMK,
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
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);
        UUID partnerUUID = LunaticFamily.marryPriestRequests.get(playerUUID);
        FamilyPlayer partnerFam = getFamilyPlayer(partnerUUID);
        PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

        if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
            int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
            player.sendMessage(getMessage(tooManyChildrenMK,
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
                priest.sendMessage(getMessage(priestRequestExpiredPriestMK,
                placeholder("%player1%", player.getName()),
                placeholder("%player2%", partner.getName())));
                player.sendMessage(getMessage(priestRequestExpiredPlayerMK,
                placeholder("%player%", partner.getName())));
                partner.sendMessage(getMessage(priestRequestExpiredPlayerMK,
                placeholder("%player%", player.getName())));
            }
        };

        Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);

        player.chat(getLanguageConfig().getMessageAsString(priestYesMK.noPrefix()));


        priest.chat(getLanguageConfig().getMessageAsString(priestRequestMK.noPrefix())
                .replace("%player1%", partnerFam.getName())
                .replace("%player2%", playerFam.getName()));


        partner.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                Component.empty(),
                getMessage(priestYesMK.noPrefix()),
                "/family marry accept",
                getMessage(priestNoMK.noPrefix()),
                "/family marry deny"),
                LunaticFamily.getConfig().decisionAsInvGUI()
        );

        return true;
    }

    private boolean acceptRequest(PlayerSender player, PlayerSender partner) {
        UUID playerUUID = player.getUniqueId();
        UUID partnerUUID = partner.getUniqueId();

        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);
        FamilyPlayer partnerFam = getFamilyPlayer(partnerUUID);

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

        player.sendMessage(getMessage(completeMK));
        partner.sendMessage(getMessage(completeMK));

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

        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);
        FamilyPlayer partnerFam = getFamilyPlayer(partnerUUID);

        UUID priestUUID = LunaticFamily.marryPriests.get(partnerUUID);
        FamilyPlayer priestFam = getFamilyPlayer(priestUUID);
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

        player.chat(getLanguageConfig().getMessageAsString(priestYesMK.noPrefix()));

        priest.chat(getLanguageConfig().getMessageAsString(priestCompleteMK.noPrefix())
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
