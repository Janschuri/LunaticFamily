package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.ClickableDecisionMessage;
import de.janschuri.lunaticFamily.commands.CommandSender;
import de.janschuri.lunaticFamily.commands.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;

import java.util.UUID;

public class MarryProposeSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "propose";
    private static final String permission = "lunaticfamily.marry";

    public MarryProposeSubcommand() {
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
            UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (args.length < 2) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                return true;
            } else if (playerFam.getName().equalsIgnoreCase(args[1])) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_self_request"));
                return true;
            } else if (playerFam.isMarried()) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_already_married").replace("%player%", playerFam.getName()));
                return true;
            }

            PlayerCommandSender partner = player.getPlayerCommandSender(args[1]);
            UUID partnerUUID = partner.getUniqueId();

            if (!partner.exists())    {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                return true;
            }

            if (!partner.isOnline()) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", partner.getName()));
                return true;
            }

            if (!Utils.getUtils().isPlayerOnWhitelistedServer(partner.getUniqueId())) {
                player.sendMessage(Language.prefix + Language.getMessage("player_not_on_whitelisted_server").replace("%player%", partner.getName().replace("%server%", partner.getServerName())));
                return true;
            }

            if (!player.isSameServer(partnerUUID) && PluginConfig.marryProposeRange >= 0) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_same_server").replace("%player%", partner.getName()));
                return true;
            }

            if (!player.hasEnoughMoney("marry_proposing_player")) {
                sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                return true;
            }

                FamilyPlayer partnerFam = partner.getFamilyPlayer();
                if (playerFam.isFamilyMember(partnerFam.getID())) {
                    sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_family_request").replace("%player%", partnerFam.getName()));
                } else if (partnerFam.isFamilyMember(playerFam.getID())) {
                    sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_family_request").replace("%player%", partnerFam.getName()));
                } else if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                    int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                    sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_too_many_children").replace("%player%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                } else if (LunaticFamily.marryRequests.containsKey(partner.getUniqueId().toString()) || LunaticFamily.marryPriest.containsKey(partner.getUniqueId().toString())) {
                    sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_open_request").replace("%player%", partnerFam.getName()));
                } else if (partnerFam.isMarried()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_player_already_married").replace("%player%", partnerFam.getName()));
                } else {

                    if (!player.isInRange(partner.getUniqueId(), PluginConfig.marryProposeRange)) {
                        player.sendMessage(Language.prefix + Language.getMessage("player_too_far_away").replace("%player%", partner.getName()));
                        return true;
                    }

                    partner.sendMessage(new ClickableDecisionMessage(Language.prefix +
                            Language.getMessage("marry_propose_request").replace("%player1%", partnerFam.getName()).replace("%player2%", playerFam.getName()),
                            Language.getMessage("marry_yes"),
                            "/family marry accept",
                            Language.getMessage("marry_no"),
                            "/family marry deny"));


                    LunaticFamily.marryRequests.put(partnerUUID.toString(), playerUUID.toString());

                    sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_request_sent").replace("%player%", partnerFam.getName()));
                }

        }
        return true;
    }
}
