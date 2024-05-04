package de.janschuri.lunaticFamily.commands.subcommands.sibling;

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

public class SiblingProposeSubcommand extends Subcommand {
    private static final String mainCommand = "sibling";
    private static final String name = "propose";
    private static final String permission = "lunaticfamily.sibling";

    public SiblingProposeSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!(sender instanceof AbstractPlayerSender)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            AbstractPlayerSender player = (AbstractPlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (playerFam.hasSibling()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("sibling_propose_has_sibling").replace("%player%", playerFam.getName()));
                return true;
            } else if (playerFam.isAdopted()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("sibling_propose_is_adopted").replace("%player%", playerFam.getName()));
                return true;
            } else if (args.length < 2) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                return true;
            }

            AbstractPlayerSender sibling = AbstractSender.getPlayerSender(args[1]);

            if (!sibling.exists()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[1]));
                return true;
            }
            if (sibling.isOnline()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_offline").replace("%player%", sibling.getName()));
                return true;
            }

            if (!Utils.isPlayerOnRegisteredServer(sibling.getUniqueId())) {
                player.sendMessage(language.getPrefix() + language.getMessage("player_not_on_whitelisted_server").replace("%player%", sibling.getName().replace("%server%", sibling.getServerName())));
                return true;
            }

                UUID siblingUUID = sibling.getUniqueId();
                FamilyPlayer siblingFam = new FamilyPlayer(siblingUUID);
                if (playerFam.getID() == siblingFam.getID()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("sibling_propose_self_request"));
                } else if (playerFam.isFamilyMember(siblingFam.getID())) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("sibling_propose_family_request").replace("%player%", siblingFam.getName()));
                } else if (siblingFam.isFamilyMember(playerFam.getID())) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("sibling_propose_family_request").replace("%player%", siblingFam.getName()));
                } else if (siblingFam.isAdopted()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("sibling_propose_sibling_is_adopted").replace("%player%", siblingFam.getName()));
                } else if (LunaticFamily.siblingRequests.containsKey(siblingUUID)) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("sibling_propose_open_request").replace("%player%", siblingFam.getName()));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "sibling_proposing_player")) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
                } else {

                    if (!player.isSameServer(sibling.getUniqueId()) && PluginConfig.siblingProposeRange >= 0) {
                        sender.sendMessage(language.getPrefix() + language.getMessage("player_not_same_server").replace("%player%", sibling.getName()));
                        return true;
                    }

                    if (!player.isInRange(sibling.getUniqueId(), PluginConfig.siblingProposeRange)) {
                        player.sendMessage(language.getPrefix() + language.getMessage("player_too_far_away").replace("%player%", sibling.getName()));
                        return true;
                    }

                    sibling.sendMessage(new ClickableDecisionMessage(
                            language.getMessage("sibling_propose_request").replace("%player%", siblingFam.getName()),
                            language.getMessage("accept"),
                            "/family sibling accept",
                            language.getMessage("deny"),
                            "/family sibling deny"));

                    LunaticFamily.siblingRequests.put(siblingUUID, playerUUID);

                    sender.sendMessage(language.getPrefix() + language.getMessage("sibling_propose_request_sent").replace("%player%", siblingFam.getName()));

                    Runnable runnable = () -> {
                        if (LunaticFamily.siblingRequests.containsKey(siblingUUID)) {
                            LunaticFamily.siblingRequests.remove(siblingUUID);
                            sibling.sendMessage(language.getPrefix() + language.getMessage("sibling_propose_request_expired").replace("%player%", player.getName()));

                            player.sendMessage(language.getPrefix() + language.getMessage("sibling_propose_request_sent_expired").replace("%player%", sibling.getName()));
                        }
                    };

                    Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);
                }

        }
        return true;
    }
}
