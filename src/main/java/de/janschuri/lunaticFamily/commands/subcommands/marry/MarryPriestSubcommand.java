package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.ClickableDecisionMessage;
import de.janschuri.lunaticFamily.commands.ClickableMessage;
import de.janschuri.lunaticFamily.commands.senders.CommandSender;
import de.janschuri.lunaticFamily.commands.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;

import java.util.UUID;

public class MarryPriestSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "priest";
    private static final String permission = "lunaticfamily.marry.priest";

    public MarryPriestSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof PlayerCommandSender)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            PlayerCommandSender player = (PlayerCommandSender) sender;
            String playerUUID = player.getUniqueId().toString();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (!player.hasPermission("lunaticFamily.marry.priest")) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
            } else if (args.length < 3) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
            } else if (LunaticFamily.marryPriest.containsValue(playerUUID)) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_priest_already_priest"));
            } else if (args[1].equalsIgnoreCase(player.getName()) || args[2].equalsIgnoreCase(player.getName())) {
                player.sendMessage(Language.prefix + Language.getMessage("marry_priest_self_request"));
            } else {

                PlayerCommandSender player1 = player.getPlayerCommandSender(args[1]);
                PlayerCommandSender player2 = player.getPlayerCommandSender(args[2]);

                if (!player1.exists()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", player1.getName()));
                    return true;
                }

                if (!player1.isOnline()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", player1.getName()));
                    return true;
                }

                if (!Utils.getUtils().isPlayerOnWhitelistedServer(player1.getUniqueId())) {
                    player.sendMessage(Language.prefix + Language.getMessage("player_not_on_whitelisted_server").replace("%player%", player1.getName().replace("%server%", player1.getServerName())));
                    return true;
                }

                if (!player2.exists()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", player2.getName()));
                    return true;
                }

                if (!player2.isOnline()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", player2.getName()));
                    return true;
                }

                if (!Utils.getUtils().isPlayerOnWhitelistedServer(player2.getUniqueId())) {
                    player.sendMessage(Language.prefix + Language.getMessage("player_not_on_whitelisted_server").replace("%player%", player2.getName().replace("%server%", player2.getServerName())));
                    return true;
                }

                if (!player.isSameServer(player1.getUniqueId()) && PluginConfig.marryPriestRange >= 0) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_same_server").replace("%player%", player1.getName()));
                    return true;
                }

                if (!player.isSameServer(player2.getUniqueId()) && PluginConfig.marryPriestRange >= 0) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_same_server").replace("%player%", player2.getName()));
                    return true;
                }

                if (!player.isInRange(player1.getUniqueId(), PluginConfig.marryPriestRange)) {
                    player.sendMessage(Language.prefix + Language.getMessage("player_too_far_away").replace("%player%", player1.getName()));
                    return true;
                }

                if (!player.isInRange(player2.getUniqueId(), PluginConfig.marryPriestRange)) {
                    player.sendMessage(Language.prefix + Language.getMessage("player_too_far_away").replace("%player%", player2.getName()));
                    return true;
                }

                UUID player1UUID = player1.getUniqueId();
                FamilyPlayer player1Fam = player1.getFamilyPlayer();
                UUID player2UUID = player2.getUniqueId();
                FamilyPlayer player2Fam = player2.getFamilyPlayer();

                if (!player1.hasEnoughMoney("marry_priest_player")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", player1.getName()));
                } else if (!player1.hasEnoughMoney("marry_priest_player")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", player2.getName()));
                } else if (!player.hasEnoughMoney("marry_priest")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money").replace("%player%", player.getName()));
                } else {


                    if (player1Fam.isMarried()) {
                        sender.sendMessage(Language.prefix + Language.getMessage("marry_priest_player_already_married").replace("%player%", player1Fam.getName()));
                    } else if (player2Fam.isMarried()) {
                        sender.sendMessage(Language.prefix + Language.getMessage("marry_priest_player_already_married").replace("%player%", player2Fam.getName()));
                    } else if (LunaticFamily.marryRequests.containsKey(player1UUID) || LunaticFamily.marryPriest.containsValue(player1UUID)) {
                        sender.sendMessage(Language.prefix + Language.getMessage("marry_priest_open_request").replace("%player%", player1Fam.getName()));
                    } else if (LunaticFamily.marryRequests.containsKey(player2UUID) || LunaticFamily.marryPriest.containsValue(player2UUID)) {
                        sender.sendMessage(Language.prefix + Language.getMessage("marry_priest_open_request").replace("%player%", player2Fam.getName()));
                    } else if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
                        int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                        sender.sendMessage(Language.prefix + Language.getMessage("marry_priest_too_many_children").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                    } else {
                        player.chat(Language.getMessage("marry_priest_request").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));

                        player1.sendMessage(new ClickableDecisionMessage(
                                "",
                                Language.getMessage("marry_yes"),
                                "/family marry accept",
                                Language.getMessage("marry_no"),
                                "/family marry deny"));

                        LunaticFamily.marryPriestRequests.put(player1UUID.toString(), player2UUID.toString());
                        LunaticFamily.marryPriest.put(player1UUID.toString(), playerUUID);

                        player.sendMarryPriestRequest(player1UUID, player2UUID);
                    }
                }
            }
        }
        return true;
    }
}
