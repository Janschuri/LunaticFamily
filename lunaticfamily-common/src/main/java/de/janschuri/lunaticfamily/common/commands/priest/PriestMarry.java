package de.janschuri.lunaticfamily.common.commands.priest;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PriestMarry extends Subcommand {

    private static final PriestMarry instance = new PriestMarry();
    private final CommandMessageKey helpMK = new CommandMessageKey(instance,"help");
    private final CommandMessageKey alreadyPriestMK = new CommandMessageKey(instance,"already_priest");
    private final CommandMessageKey requestMK = new CommandMessageKey(instance,"request");
    private final CommandMessageKey playerAlreadyMarriedMK = new CommandMessageKey(instance,"player_already_married");
    private final CommandMessageKey tooManyChildrenMK = new CommandMessageKey(instance,"too_many_children");
    private final CommandMessageKey selfRequestMK = new CommandMessageKey(instance,"self_request");
    private final CommandMessageKey openRequestMK = new CommandMessageKey(instance,"open_request");
    private final CommandMessageKey requestExpiredPriestMK = new CommandMessageKey(instance,"request_expired_priest");
    private final CommandMessageKey requestExpiredPlayerMK = new CommandMessageKey(instance,"request_expired_player");
    private final CommandMessageKey samePlayerMK = new CommandMessageKey(instance,"same_player");
    private final CommandMessageKey yesMK = new CommandMessageKey(instance,"yes");
    private final CommandMessageKey noMK = new CommandMessageKey(instance,"no");
    private final CommandMessageKey familyRequestMK = new CommandMessageKey(instance,"family_request");


    @Override
    public String getPermission() {
        return "lunaticfamily.priest.marry";
    }

    @Override
    public String getName() {
        return "marry";
    }

    @Override
    public Subcommand getParentCommand() {
        return new Priest();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        PlayerSender player = (PlayerSender) sender;
        UUID playerUUID = player.getUniqueId();

        if (args.length < 2) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            Logger.debugLog("PriestMarrySubcommand: Wrong usage");
            return true;
        }

        if (Utils.isPriest(playerUUID)) {
            sender.sendMessage(getMessage(alreadyPriestMK));
            return true;
        }

        if (args[0].equalsIgnoreCase(player.getName()) || args[1].equalsIgnoreCase(player.getName())) {
            player.sendMessage(getMessage(selfRequestMK));
            return true;
        }



        String player1Name = args[0];
        String player2Name = args[1];

        UUID player1UUID = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("name", player1Name).findOne().getUUID();
        UUID player2UUID = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("name", player2Name).findOne().getUUID();

        if (player1UUID == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                    .replaceText(getTextReplacementConfig("%player%", player1Name)));
            return true;
        }

        if (player2UUID == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                    .replaceText(getTextReplacementConfig("%player%", player2Name)));
            return true;
        }

        if (player1UUID.equals(player2UUID)) {
            sender.sendMessage(getMessage(samePlayerMK));
            return true;
        }

        FamilyPlayer player1Fam = getFamilyPlayer(player1UUID);
        FamilyPlayer player2Fam = getFamilyPlayer(player2UUID);

        player1Fam.update();
        player2Fam.update();

        if (player1Fam.isFamilyMember(player2Fam)) {
            sender.sendMessage(getMessage(familyRequestMK)
                    .replaceText(getTextReplacementConfig("%player1%", player1Fam.getName()))
                    .replaceText(getTextReplacementConfig("%player2%", player2Fam.getName())));
            return true;
        }

        if (player2Fam.isFamilyMember(player1Fam)) {
            sender.sendMessage(getMessage(familyRequestMK)
                    .replaceText(getTextReplacementConfig("%player1%", player1Fam.getName()))
                    .replaceText(getTextReplacementConfig("%player2%", player2Fam.getName())));
            return true;
        }

        PlayerSender player1 = LunaticLib.getPlatform().getPlayerSender(player1UUID);
        PlayerSender player2 = LunaticLib.getPlatform().getPlayerSender(player2UUID);

        if (!player1.isOnline()) {
            sender.sendMessage(getMessage(PLAYER_OFFLINE_MK)
                    .replaceText(getTextReplacementConfig("%player%", player1.getName())));
            return true;
        }

        if (!Utils.isPlayerOnRegisteredServer(player1)) {
            player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK)
                    .replaceText(getTextReplacementConfig("%player%", player1.getName()))
                    .replaceText(getTextReplacementConfig("%server%", player1.getServerName())));
            return true;
        }

        if (!player2.isOnline()) {
            sender.sendMessage(getMessage(PLAYER_OFFLINE_MK)
                    .replaceText(getTextReplacementConfig("%player%", player2.getName())));
            return true;
        }

        if (!Utils.isPlayerOnRegisteredServer(player2)) {
            player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK)
                    .replaceText(getTextReplacementConfig("%player%", player2.getName()))
                    .replaceText(getTextReplacementConfig("%server%", player2.getServerName())));

            return true;
        }

        if (!player.isSameServer(player1.getUniqueId()) && LunaticFamily.getConfig().getMarryPriestRange() >= 0) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK)
                    .replaceText(getTextReplacementConfig("%player%", player1.getName())));
            return true;
        }

        if (!player.isSameServer(player2.getUniqueId()) && LunaticFamily.getConfig().getMarryPriestRange() >= 0) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK)
                    .replaceText(getTextReplacementConfig("%player%", player2.getName())));
            return true;
        }

        if (!player.isInRange(player1.getUniqueId(), LunaticFamily.getConfig().getMarryPriestRange())) {
            player.sendMessage(getMessage(PLAYER_TOO_FAR_AWAY_MK)
                    .replaceText(getTextReplacementConfig("%player%", player1.getName())));
            return true;
        }

        if (!player.isInRange(player2.getUniqueId(), LunaticFamily.getConfig().getMarryPriestRange())) {
            player.sendMessage(getMessage(PLAYER_TOO_FAR_AWAY_MK)
                    .replaceText(getTextReplacementConfig("%player%", player2.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), player1UUID, WithdrawKey.PRIEST_MARRY_PLAYER)) {
            sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", player1.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), player2UUID, WithdrawKey.PRIEST_MARRY_PLAYER)) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK)
                    .replaceText(getTextReplacementConfig("%player%", player2.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_MARRY)) {
            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", player.getName())));
            return true;
        }


        if (player1Fam.isMarried()) {
            sender.sendMessage(getMessage(playerAlreadyMarriedMK)
                    .replaceText(getTextReplacementConfig("%player%", player1Fam.getName())));
            return true;
        }

        if (player2Fam.isMarried()) {
            sender.sendMessage(getMessage(playerAlreadyMarriedMK)
                    .replaceText(getTextReplacementConfig("%player%", player2Fam.getName())));
            return true;
        }

        if (LunaticFamily.marryRequests.containsKey(player1UUID) || LunaticFamily.marryPriests.containsValue(player1UUID)) {
            sender.sendMessage(getMessage(openRequestMK)
                    .replaceText(getTextReplacementConfig("%player%", player1Fam.getName())));
            return true;
        }

        if (LunaticFamily.marryRequests.containsKey(player2UUID) || LunaticFamily.marryPriests.containsValue(player2UUID)) {
            sender.sendMessage(getMessage(openRequestMK)
                    .replaceText(getTextReplacementConfig("%player%", player2Fam.getName())));
            return true;
        }

        if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
            int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
            sender.sendMessage(getMessage(tooManyChildrenMK)
                    .replaceText(getTextReplacementConfig("%player1%", player1Fam.getName()))
                    .replaceText(getTextReplacementConfig("%player2%", player2Fam.getName()))
                    .replaceText(getTextReplacementConfig("%amount%", Integer.toString(amountDiff))));
            return true;
        }

        player.chat(getLanguageConfig().getMessageAsString(requestMK, false)
                .replace("%player1%", player1Fam.getName())
                .replace("%player2%", player2Fam.getName()));

        player1.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                Component.empty(),
                getMessage(yesMK),
                "/family marry accept",
                getMessage(noMK),
                "/family marry deny"),
                LunaticFamily.getConfig().decisionAsInvGUI()
        );

        LunaticFamily.marryPriestRequests.put(player1UUID, player2UUID);
        LunaticFamily.marryPriests.put(player1UUID, playerUUID);

        Runnable runnable = () -> {
            if (LunaticFamily.marryPriestRequests.containsKey(player1UUID)) {
                LunaticFamily.marryPriestRequests.remove(player1UUID);
                LunaticFamily.marryPriests.remove(player1UUID);
                player.sendMessage(getMessage(requestExpiredPriestMK)
                        .replaceText(getTextReplacementConfig("%player1%", player1.getName()))
                        .replaceText(getTextReplacementConfig("%player2%", player2.getName())));
                player1.sendMessage(getMessage(requestExpiredPlayerMK)
                        .replaceText(getTextReplacementConfig("%player%", player2.getName())));
                player2.sendMessage(getMessage(requestExpiredPlayerMK)
                        .replaceText(getTextReplacementConfig("%player%", player1.getName())));
            }
        };

        Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);

        return true;
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(PLAYER_NAME_MK, false),
                getMessage(PLAYER_NAME_MK, false)
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam(), getOnlinePlayersParam());
    }
}
