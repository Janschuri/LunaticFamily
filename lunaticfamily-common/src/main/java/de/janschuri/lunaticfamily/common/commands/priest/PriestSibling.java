package de.janschuri.lunaticfamily.common.commands.priest;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.adopt.Adopt;
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

public class PriestSibling extends Subcommand {

    private static final PriestSibling instance = new PriestSibling();
    private final CommandMessageKey helpMK = new CommandMessageKey(instance,"help");
    private final CommandMessageKey alreadyPriestMK = new CommandMessageKey(instance,"already_priest");
    private final CommandMessageKey requestMK = new CommandMessageKey(instance,"request");
    private final CommandMessageKey alreadySiblingMK = new CommandMessageKey(instance,"already_sibling");
    private final CommandMessageKey isAdoptedMK = new CommandMessageKey(instance,"is_adopted");
    private final CommandMessageKey selfRequestMK = new CommandMessageKey(instance,"self_request");
    private final CommandMessageKey openRequestMK = new CommandMessageKey(instance,"open_request");
    private final CommandMessageKey requestExpiredPriestMK = new CommandMessageKey(instance,"request_expired_priest");
    private final CommandMessageKey requestExpiredPlayerMK = new CommandMessageKey(instance,"request_expired_player");
    private final CommandMessageKey samePlayerMK = new CommandMessageKey(instance,"same_player");
    private final CommandMessageKey yesMK = new CommandMessageKey(new Adopt(),"yes");
    private final CommandMessageKey noMK = new CommandMessageKey(new Adopt(),"no");
    private final CommandMessageKey familyRequestMK = new CommandMessageKey(instance,"family_request");


    @Override
    public String getPermission() {
        return "lunaticfamily.priest.sibling";
    }

    @Override
    public String getName() {
        return "sibling";
    }

    @Override
    public Subcommand getParentCommand() {
        return new Priest();
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

        if (args.length < 2) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            Logger.debugLog("PriestSiblingSubcommand: Wrong usage");
            return true;
        }

        if (LunaticFamily.siblingPriests.containsValue(playerUUID)) {
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
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", player1Name)));
            return true;
        }

        if (player2UUID == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", player2Name)));
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
            sender.sendMessage(getMessage(familyRequestMK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", player2Fam.getName())));
            return true;
        }

        if (player2Fam.isFamilyMember(player1Fam)) {
            sender.sendMessage(getMessage(familyRequestMK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", player2Fam.getName())));
            return true;
        }

        PlayerSender player1 = LunaticLib.getPlatform().getPlayerSender(player1UUID);
        PlayerSender player2 = LunaticLib.getPlatform().getPlayerSender(player2UUID);

        if (!player1.isOnline()) {
            sender.sendMessage(getMessage(PLAYER_OFFLINE_MK,
                placeholder("%player%", player1.getName())));
            return true;
        }

        if (!Utils.isPlayerOnRegisteredServer(player1)) {
            player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK,
                placeholder("%player%", player1.getName()),
                placeholder("%server%", player1.getServerName())));
            return true;
        }

        if (!player2.isOnline()) {
            sender.sendMessage(getMessage(PLAYER_OFFLINE_MK,
                placeholder("%player%", player2.getName())));
            return true;
        }

        if (!Utils.isPlayerOnRegisteredServer(player2)) {
            player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK,
                placeholder("%player%", player2.getName()),
                placeholder("%server%", player2.getServerName())));

            return true;
        }

        if (!player.isSameServer(player1.getUniqueId()) && LunaticFamily.getConfig().getAdoptPriestRange() >= 0) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK,
                placeholder("%player%", player1.getName())));
            return true;
        }

        if (!player.isSameServer(player2.getUniqueId()) && LunaticFamily.getConfig().getAdoptPriestRange() >= 0) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK,
                placeholder("%player%", player2.getName())));
            return true;
        }

        if (!player.isInRange(player1.getUniqueId(), LunaticFamily.getConfig().getAdoptPriestRange())) {
            player.sendMessage(getMessage(PLAYER_TOO_FAR_AWAY_MK,
                placeholder("%player%", player1.getName())));
            return true;
        }

        if (!player.isInRange(player2.getUniqueId(), LunaticFamily.getConfig().getAdoptPriestRange())) {
            player.sendMessage(getMessage(PLAYER_TOO_FAR_AWAY_MK,
                placeholder("%player%", player2.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), player1UUID, WithdrawKey.PRIEST_SIBLING_PLAYER)) {
            sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", player1.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), player2UUID, WithdrawKey.PRIEST_SIBLING_PLAYER)) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK,
                placeholder("%player%", player2.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_SIBLING)) {
            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", player.getName())));
            return true;
        }


        if (player1Fam.hasSiblings()) {
            sender.sendMessage(getMessage(alreadySiblingMK,
                placeholder("%player%", player1Fam.getName())));
            return true;
        }

        if (player2Fam.isMarried()) {
            sender.sendMessage(getMessage(alreadySiblingMK,
                placeholder("%player%", player2Fam.getName())));
            return true;
        }

        if (LunaticFamily.siblingRequests.containsKey(player1UUID) || LunaticFamily.siblingPriests.containsValue(player1UUID)) {
            sender.sendMessage(getMessage(openRequestMK,
                placeholder("%player%", player1Fam.getName())));
            return true;
        }

        if (LunaticFamily.siblingRequests.containsKey(player2UUID) || LunaticFamily.siblingPriests.containsValue(player2UUID)) {
            sender.sendMessage(getMessage(openRequestMK,
                placeholder("%player%", player2Fam.getName())));
            return true;
        }

        if (player1Fam.isAdopted()) {
            sender.sendMessage(getMessage(isAdoptedMK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", player2Fam.getName())));
            return true;
        }

        if (player2Fam.isAdopted()) {
            sender.sendMessage(getMessage(isAdoptedMK,
                placeholder("%player1%", player2Fam.getName()),
                placeholder("%player2%", player1Fam.getName())));
            return true;
        }

        player.chat(getLanguageConfig().getMessageAsString(requestMK.noPrefix())
                .replace("%player1%", player1Fam.getName())
                .replace("%player2%", player2Fam.getName()));

        player1.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                Component.empty(),
                getMessage(yesMK),
                "/family sibling accept",
                getMessage(noMK),
                "/family sibling deny"),
                LunaticFamily.getConfig().decisionAsInvGUI()
        );

        LunaticFamily.siblingPriestRequests.put(player1UUID, player2UUID);
        LunaticFamily.siblingPriests.put(player1UUID, playerUUID);

        Runnable runnable = () -> {
            if (LunaticFamily.siblingPriestRequests.containsKey(player1UUID)) {
                LunaticFamily.siblingPriestRequests.remove(player1UUID);
                LunaticFamily.siblingPriests.remove(player1UUID);
                player.sendMessage(getMessage(requestExpiredPriestMK,
                placeholder("%player1%", player1.getName()),
                placeholder("%player2%", player2.getName())));
                player1.sendMessage(getMessage(requestExpiredPlayerMK,
                placeholder("%player%", player2.getName())));
                player2.sendMessage(getMessage(requestExpiredPlayerMK,
                placeholder("%player%", player1.getName())));
            }
        };

        Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);

        return true;
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(PLAYER_NAME_MK.noPrefix()),
                getMessage(PLAYER_NAME_MK.noPrefix())
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam(), getOnlinePlayersParam());
    }
}