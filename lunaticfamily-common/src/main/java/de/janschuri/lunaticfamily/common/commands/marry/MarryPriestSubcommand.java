package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryPriestSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "priest";
    private static final String PERMISSION = "lunaticfamily.marry.priest";

    public MarryPriestSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getPrefix() + getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else {
            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();

            if (!player.hasPermission("lunaticFamily.marry.priest")) {
                sender.sendMessage(getPrefix() + getMessage("no_permission"));
            } else if (args.length < 2) {
                sender.sendMessage(getPrefix() + getMessage("wrong_usage"));
                Logger.debugLog("MarryPriestSubcommand: Wrong usage");
            } else if (LunaticFamily.marryPriest.containsValue(playerUUID)) {
                sender.sendMessage(getPrefix() + getMessage("marry_priest_already_priest"));
            } else if (args[0].equalsIgnoreCase(player.getName()) || args[1].equalsIgnoreCase(player.getName())) {
                player.sendMessage(getPrefix() + getMessage("marry_priest_self_request"));
            } else {

                String player1Name = args[0];
                String player2Name = args[1];

                UUID player1UUID = PlayerDataTable.getUUID(player1Name);
                UUID player2UUID = PlayerDataTable.getUUID(player2Name);

                if (player1UUID == null) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", player1Name));
                    return true;
                }

                if (player2UUID == null) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", player2Name));
                    return true;
                }

                if (player1UUID.equals(player2UUID)) {
                    sender.sendMessage(getPrefix() + getMessage("marry_priest_same_player"));
                    return true;
                }

                PlayerSender player1 = LunaticLib.getPlatform().getPlayerSender(player1UUID);
                PlayerSender player2 = LunaticLib.getPlatform().getPlayerSender(player2UUID);

                if (!player1.isOnline()) {
                    sender.sendMessage(getPrefix() + getMessage("player_offline").replace("%player%", player1.getName()));
                    return true;
                }

                if (!Utils.isPlayerOnRegisteredServer(player1.getUniqueId())) {
                    player.sendMessage(getPrefix() + getMessage("player_not_on_whitelisted_server").replace("%player%", player1.getName().replace("%server%", player1.getServerName())));
                    return true;
                }

                if (!player2.isOnline()) {
                    sender.sendMessage(getPrefix() + getMessage("player_offline").replace("%player%", player2.getName()));
                    return true;
                }

                if (!Utils.isPlayerOnRegisteredServer(player2.getUniqueId())) {
                    player.sendMessage(getPrefix() + getMessage("player_not_on_whitelisted_server").replace("%player%", player2.getName().replace("%server%", player2.getServerName())));
                    return true;
                }

                if (!player.isSameServer(player1.getUniqueId()) && LunaticFamily.getConfig().getMarryPriestRange() >= 0) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_same_server").replace("%player%", player1.getName()));
                    return true;
                }

                if (!player.isSameServer(player2.getUniqueId()) && LunaticFamily.getConfig().getMarryPriestRange() >= 0) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_same_server").replace("%player%", player2.getName()));
                    return true;
                }

                if (!player.isInRange(player1.getUniqueId(), LunaticFamily.getConfig().getMarryPriestRange())) {
                    player.sendMessage(getPrefix() + getMessage("player_too_far_away").replace("%player%", player1.getName()));
                    return true;
                }

                if (!player.isInRange(player2.getUniqueId(), LunaticFamily.getConfig().getMarryPriestRange())) {
                    player.sendMessage(getPrefix() + getMessage("player_too_far_away").replace("%player%", player2.getName()));
                    return true;
                }

                FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(player1UUID);
                FamilyPlayerImpl player2Fam = new FamilyPlayerImpl(player2UUID);

                if (!Utils.hasEnoughMoney(player.getServerName(), player1UUID, "marry_priest_player")) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_enough_money").replace("%player%", player1.getName()));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), player2UUID, "marry_priest_player")) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_enough_money").replace("%player%", player2.getName()));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "marry_priest")) {
                    sender.sendMessage(getPrefix() + getMessage("not_enough_money").replace("%player%", player.getName()));
                } else {


                    if (player1Fam.isMarried()) {
                        sender.sendMessage(getPrefix() + getMessage("marry_priest_player_already_married").replace("%player%", player1Fam.getName()));
                    } else if (player2Fam.isMarried()) {
                        sender.sendMessage(getPrefix() + getMessage("marry_priest_player_already_married").replace("%player%", player2Fam.getName()));
                    } else if (LunaticFamily.marryRequests.containsKey(player1UUID) || LunaticFamily.marryPriest.containsValue(player1UUID)) {
                        sender.sendMessage(getPrefix() + getMessage("marry_priest_open_request").replace("%player%", player1Fam.getName()));
                    } else if (LunaticFamily.marryRequests.containsKey(player2UUID) || LunaticFamily.marryPriest.containsValue(player2UUID)) {
                        sender.sendMessage(getPrefix() + getMessage("marry_priest_open_request").replace("%player%", player2Fam.getName()));
                    } else if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
                        int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                        sender.sendMessage(getPrefix() + getMessage("marry_priest_too_many_children").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                    } else {
                        player.chat(getMessage("marry_priest_request").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));

                        player1.sendMessage(Utils.getClickableDecisionMessage(
                                getPrefix(),
                                getMessage("marry_yes"),
                                "/family marry accept",
                                getMessage("marry_no"),
                                "/family marry deny"));

                        LunaticFamily.marryPriestRequests.put(player1UUID, player2UUID);
                        LunaticFamily.marryPriest.put(player1UUID, playerUUID);

                        Runnable runnable = () -> {
                            if (LunaticFamily.marryPriestRequests.containsKey(player1UUID)) {
                                LunaticFamily.marryPriestRequests.remove(player1UUID);
                                LunaticFamily.marryPriest.remove(player1UUID);
                                player.sendMessage(getPrefix() + getMessage("marry_priest_request_expired_priest").replace("%player1%", player1.getName()).replace("%player2%", player2.getName()));
                                player1.sendMessage(getPrefix() + getMessage("marry_priest_request_expired_player").replace("%player%", player2.getName()));
                                player2.sendMessage(getPrefix() + getMessage("marry_priest_request_expired_player").replace("%player%", player1.getName()));
                            }
                        };

                        Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);
                    }
                }
            }
        }
        return true;
    }
}
