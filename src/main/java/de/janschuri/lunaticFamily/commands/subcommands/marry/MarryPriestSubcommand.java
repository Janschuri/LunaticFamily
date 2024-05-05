package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.ClickableDecisionMessage;

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
    public boolean execute(AbstractSender sender, String[] args) {
        if (!(sender instanceof AbstractPlayerSender)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            AbstractPlayerSender player = (AbstractPlayerSender) sender;
            UUID playerUUID = player.getUniqueId();

            if (!player.hasPermission("lunaticFamily.marry.priest")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
            } else if (args.length < 2) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
            } else if (LunaticFamily.marryPriest.containsValue(playerUUID)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("marry_priest_already_priest"));
            } else if (args[0].equalsIgnoreCase(player.getName()) || args[1].equalsIgnoreCase(player.getName())) {
                player.sendMessage(language.getPrefix() + language.getMessage("marry_priest_self_request"));
            } else {

                AbstractPlayerSender player1 = AbstractSender.getPlayerSender(args[0]);
                AbstractPlayerSender player2 = AbstractSender.getPlayerSender(args[1]);

                if (!player1.exists()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", player1.getName()));
                    return true;
                }

                if (!player1.isOnline()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("player_offline").replace("%player%", player1.getName()));
                    return true;
                }

                if (!Utils.isPlayerOnRegisteredServer(player1.getUniqueId())) {
                    player.sendMessage(language.getPrefix() + language.getMessage("player_not_on_whitelisted_server").replace("%player%", player1.getName().replace("%server%", player1.getServerName())));
                    return true;
                }

                if (!player2.exists()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", player2.getName()));
                    return true;
                }

                if (!player2.isOnline()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("player_offline").replace("%player%", player2.getName()));
                    return true;
                }

                if (!Utils.isPlayerOnRegisteredServer(player2.getUniqueId())) {
                    player.sendMessage(language.getPrefix() + language.getMessage("player_not_on_whitelisted_server").replace("%player%", player2.getName().replace("%server%", player2.getServerName())));
                    return true;
                }

                if (!player.isSameServer(player1.getUniqueId()) && PluginConfig.getMarryPriestRange() >= 0) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("player_not_same_server").replace("%player%", player1.getName()));
                    return true;
                }

                if (!player.isSameServer(player2.getUniqueId()) && PluginConfig.getMarryPriestRange() >= 0) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("player_not_same_server").replace("%player%", player2.getName()));
                    return true;
                }

                if (!player.isInRange(player1.getUniqueId(), PluginConfig.getMarryPriestRange())) {
                    player.sendMessage(language.getPrefix() + language.getMessage("player_too_far_away").replace("%player%", player1.getName()));
                    return true;
                }

                if (!player.isInRange(player2.getUniqueId(), PluginConfig.getMarryPriestRange())) {
                    player.sendMessage(language.getPrefix() + language.getMessage("player_too_far_away").replace("%player%", player2.getName()));
                    return true;
                }

                UUID player1UUID = player1.getUniqueId();
                FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);
                UUID player2UUID = player2.getUniqueId();
                FamilyPlayer player2Fam = new FamilyPlayer(player2UUID);

                if (!Utils.hasEnoughMoney(player.getServerName(), player1UUID, "marry_priest_player")) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", player1.getName()));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), player2UUID, "marry_priest_player")) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", player2.getName()));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "marry_priest")) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money").replace("%player%", player.getName()));
                } else {


                    if (player1Fam.isMarried()) {
                        sender.sendMessage(language.getPrefix() + language.getMessage("marry_priest_player_already_married").replace("%player%", player1Fam.getName()));
                    } else if (player2Fam.isMarried()) {
                        sender.sendMessage(language.getPrefix() + language.getMessage("marry_priest_player_already_married").replace("%player%", player2Fam.getName()));
                    } else if (LunaticFamily.marryRequests.containsKey(player1UUID) || LunaticFamily.marryPriest.containsValue(player1UUID)) {
                        sender.sendMessage(language.getPrefix() + language.getMessage("marry_priest_open_request").replace("%player%", player1Fam.getName()));
                    } else if (LunaticFamily.marryRequests.containsKey(player2UUID) || LunaticFamily.marryPriest.containsValue(player2UUID)) {
                        sender.sendMessage(language.getPrefix() + language.getMessage("marry_priest_open_request").replace("%player%", player2Fam.getName()));
                    } else if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
                        int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                        sender.sendMessage(language.getPrefix() + language.getMessage("marry_priest_too_many_children").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                    } else {
                        player.chat(language.getMessage("marry_priest_request").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));

                        player1.sendMessage(new ClickableDecisionMessage(
                                language.getPrefix(),
                                language.getMessage("marry_yes"),
                                "/family marry accept",
                                language.getMessage("marry_no"),
                                "/family marry deny"));

                        LunaticFamily.marryPriestRequests.put(player1UUID, player2UUID);
                        LunaticFamily.marryPriest.put(player1UUID, playerUUID);

                        Runnable runnable = () -> {
                            if (LunaticFamily.marryPriestRequests.containsKey(player1UUID)) {
                                LunaticFamily.marryPriestRequests.remove(player1UUID);
                                LunaticFamily.marryPriest.remove(player1UUID);
                                player.sendMessage(language.getPrefix() + language.getMessage("marry_priest_request_expired_priest").replace("%player1%", player1.getName()).replace("%player2%", player2.getName()));
                                player1.sendMessage(language.getPrefix() + language.getMessage("marry_priest_request_expired_player").replace("%player%", player2.getName()));
                                player2.sendMessage(language.getPrefix() + language.getMessage("marry_priest_request_expired_player").replace("%player%", player1.getName()));
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
