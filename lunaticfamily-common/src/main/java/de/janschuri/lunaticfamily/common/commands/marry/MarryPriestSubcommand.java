package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.MarrySubcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
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

public class MarryPriestSubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey alreadyPriestMK = new CommandMessageKey(this,"already_priest");
    private final CommandMessageKey requestMK = new CommandMessageKey(this,"request");
    private final CommandMessageKey playerAlreadyMarriedMK = new CommandMessageKey(this,"player_already_married");
    private final CommandMessageKey tooManyChildrenMK = new CommandMessageKey(this,"too_many_children");
    private final CommandMessageKey selfRequestMK = new CommandMessageKey(this,"self_request");
    private final CommandMessageKey openRequestMK = new CommandMessageKey(this,"open_request");
    private final CommandMessageKey requestExpiredPriestMK = new CommandMessageKey(this,"request_expired_priest");
    private final CommandMessageKey requestExpiredPlayerMK = new CommandMessageKey(this,"request_expired_player");
    private final CommandMessageKey samePlayerMK = new CommandMessageKey(this,"same_player");
    private final CommandMessageKey marryYesMK = new CommandMessageKey(new MarrySubcommand(),"yes");
    private final CommandMessageKey marryNoMK = new CommandMessageKey(new MarrySubcommand(),"no");


    @Override
    public String getPermission() {
        return "lunaticfamily.marry.priest";
    }

    @Override
    public String getName() {
        return "priest";
    }

    @Override
    public MarrySubcommand getParentCommand() {
        return new MarrySubcommand();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
        } else if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
        } else {
            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();

            if (!player.hasPermission("lunaticFamily.marry.priest")) {
                sender.sendMessage(getMessage(NO_PERMISSION_MK));
            } else if (args.length < 2) {
                sender.sendMessage(getMessage(WRONG_USAGE_MK));
                Logger.debugLog("MarryPriestSubcommand: Wrong usage");
            } else if (LunaticFamily.marryPriest.containsValue(playerUUID)) {
                sender.sendMessage(getMessage(alreadyPriestMK));
            } else if (args[0].equalsIgnoreCase(player.getName()) || args[1].equalsIgnoreCase(player.getName())) {
                player.sendMessage(getMessage(selfRequestMK));
            } else {

                String player1Name = args[0];
                String player2Name = args[1];

                UUID player1UUID = PlayerDataTable.getUUID(player1Name);
                UUID player2UUID = PlayerDataTable.getUUID(player2Name);

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

                FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(player1UUID);
                FamilyPlayerImpl player2Fam = new FamilyPlayerImpl(player2UUID);

                if (!Utils.hasEnoughMoney(player.getServerName(), player1UUID, WithdrawKey.MARRY_PRIEST_PLAYER)) {
                    sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                            .replaceText(getTextReplacementConfig("%player%", player1.getName())));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), player2UUID, WithdrawKey.MARRY_PRIEST_PLAYER)) {
                    sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK)
                            .replaceText(getTextReplacementConfig("%player%", player2.getName())));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_PRIEST)) {
                    sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK)
                            .replaceText(getTextReplacementConfig("%player%", player.getName())));
                } else {


                    if (player1Fam.isMarried()) {
                        sender.sendMessage(getMessage(playerAlreadyMarriedMK)
                                .replaceText(getTextReplacementConfig("%player%", player1Fam.getName())));
                    } else if (player2Fam.isMarried()) {
                        sender.sendMessage(getMessage(playerAlreadyMarriedMK)
                                .replaceText(getTextReplacementConfig("%player%", player2Fam.getName())));
                    } else if (LunaticFamily.marryRequests.containsKey(player1UUID) || LunaticFamily.marryPriest.containsValue(player1UUID)) {
                        sender.sendMessage(getMessage(openRequestMK)
                                .replaceText(getTextReplacementConfig("%player%", player1Fam.getName())));
                    } else if (LunaticFamily.marryRequests.containsKey(player2UUID) || LunaticFamily.marryPriest.containsValue(player2UUID)) {
                        sender.sendMessage(getMessage(openRequestMK)
                                .replaceText(getTextReplacementConfig("%player%", player2Fam.getName())));
                    } else if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
                        int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                        sender.sendMessage(getMessage(tooManyChildrenMK)
                                .replaceText(getTextReplacementConfig("%player1%", player1Fam.getName()))
                                .replaceText(getTextReplacementConfig("%player2%", player2Fam.getName()))
                                .replaceText(getTextReplacementConfig("%amount%", Integer.toString(amountDiff))));
                    } else {
                        player.chat(getMessage(requestMK)
                                .replaceText(getTextReplacementConfig("%player1%", player1Fam.getName()))
                                .replaceText(getTextReplacementConfig("%player2%", player2Fam.getName())).toString());

                        player1.sendMessage(Utils.getClickableDecisionMessage(
                                getPrefix(),
                                getMessage(marryYesMK),
                                "/family marry accept",
                                getMessage(marryNoMK),
                                "/family marry deny"));

                        LunaticFamily.marryPriestRequests.put(player1UUID, player2UUID);
                        LunaticFamily.marryPriest.put(player1UUID, playerUUID);

                        Runnable runnable = () -> {
                            if (LunaticFamily.marryPriestRequests.containsKey(player1UUID)) {
                                LunaticFamily.marryPriestRequests.remove(player1UUID);
                                LunaticFamily.marryPriest.remove(player1UUID);
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
                    }
                }
            }
        }
        return true;
    }

    @Override
    public Component getParamsName() {
        return getMessage(PLAYER_NAME_MK, false);
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam(), getOnlinePlayersParam());
    }
}
