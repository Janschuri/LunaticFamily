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

public class MarryProposeSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "propose";
    private static final String PERMISSION = "lunaticfamily.marry";

    public MarryProposeSubcommand() {
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
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (args.length < 1) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                return true;
            } else if (playerFam.getName().equalsIgnoreCase(args[0])) {
                sender.sendMessage(language.getPrefix() + language.getMessage("marry_propose_self_request"));
                return true;
            } else if (playerFam.isMarried()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("marry_propose_already_married").replace("%player%", playerFam.getName()));
                return true;
            }

            AbstractPlayerSender partner = AbstractSender.getPlayerSender(args[0]);
            UUID partnerUUID = partner.getUniqueId();

            if (!partner.exists())    {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[0]));
                return true;
            }

            if (!partner.isOnline()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_offline").replace("%player%", partner.getName()));
                return true;
            }

            if (!Utils.isPlayerOnRegisteredServer(partner.getUniqueId())) {
                player.sendMessage(language.getPrefix() + language.getMessage("player_not_on_whitelisted_server").replace("%player%", partner.getName().replace("%server%", partner.getServerName())));
                return true;
            }

            if (!player.isSameServer(partnerUUID) && PluginConfig.getMarryProposeRange() >= 0) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_same_server").replace("%player%", partner.getName()));
                return true;
            }

            if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "marry_proposing_player")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
                return true;
            }

                FamilyPlayer partnerFam = new FamilyPlayer(partnerUUID);
                if (playerFam.isFamilyMember(partnerFam.getID())) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("marry_propose_family_request").replace("%player%", partnerFam.getName()));
                } else if (partnerFam.isFamilyMember(playerFam.getID())) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("marry_propose_family_request").replace("%player%", partnerFam.getName()));
                } else if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                    int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                    sender.sendMessage(language.getPrefix() + language.getMessage("marry_propose_too_many_children").replace("%player%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                } else if (LunaticFamily.marryRequests.containsKey(partner.getUniqueId()) || LunaticFamily.marryPriest.containsKey(partner.getUniqueId())) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("marry_propose_open_request").replace("%player%", partnerFam.getName()));
                } else if (partnerFam.isMarried()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("marry_propose_player_already_married").replace("%player%", partnerFam.getName()));
                } else {

                    if (!player.isInRange(partner.getUniqueId(), PluginConfig.getMarryProposeRange())) {
                        player.sendMessage(language.getPrefix() + language.getMessage("player_too_far_away").replace("%player%", partner.getName()));
                        return true;
                    }

                    partner.sendMessage(new ClickableDecisionMessage(language.getPrefix() +
                            language.getPrefix() + language.getMessage("marry_propose_request").replace("%player1%", partnerFam.getName()).replace("%player2%", playerFam.getName()),
                            language.getMessage("marry_yes"),
                            "/family marry accept",
                            language.getMessage("marry_no"),
                            "/family marry deny"));


                    LunaticFamily.marryRequests.put(partnerUUID, playerUUID);

                    sender.sendMessage(language.getPrefix() + language.getMessage("marry_propose_request_sent").replace("%player%", partnerFam.getName()));

                    Runnable runnable = () -> {
                        if (LunaticFamily.marryRequests.containsKey(partnerUUID)) {
                            LunaticFamily.marryRequests.remove(partnerUUID);
                            player.sendMessage(language.getPrefix() + language.getMessage("marry_propose_request_sent_expired").replace("%player%", player.getName()));
                            partner.sendMessage(language.getPrefix() + language.getMessage("marry_propose_request_expired").replace("%player%", partner.getName()));
                        }
                    };

                    Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);

                }

        }
        return true;
    }
}
