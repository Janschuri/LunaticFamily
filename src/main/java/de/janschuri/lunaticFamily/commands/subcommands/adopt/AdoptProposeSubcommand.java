package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.ClickableDecisionMessage;

import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AdoptProposeSubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "propose";
    private static final String permission = "lunaticfamily.adopt";
    public AdoptProposeSubcommand() {
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

            boolean confirm = false;
            boolean cancel = false;

            if (args.length > 3) {
                if (args[3].equalsIgnoreCase("confirm")) {
                    confirm = true;
                }
                if (args[3].equalsIgnoreCase("cancel")) {
                    cancel = true;
                }
            }


            if (args.length < 2) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                return true;
            } else if (cancel) {
                sender.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_cancel").replace("%player%", args[2]));
                return true;
            } else if (!playerFam.isMarried() && !PluginConfig.allowSingleAdopt) {
                sender.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_no_single_adopt"));
                return true;
            } else if (playerFam.getChildrenAmount() > 1) {
                sender.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_limit"));
                return true;
            }

            AbstractPlayerSender child = AbstractSender.getPlayerSender(args[1]);

            if (!child.exists()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[1]));
                return true;
            }
            if (!child.isOnline()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_offline").replace("%player%", args[1]));
                return true;
            }

            if (!Utils.getUtils().isPlayerOnWhitelistedServer(child.getUniqueId())) {
                player.sendMessage(language.getPrefix() + language.getMessage("player_not_on_whitelisted_server").replace("%player%", child.getName().replace("%server%", child.getServerName())));
                return true;
            }

            if (!Utils.getUtils().hasEnoughMoney(playerUUID, "adopt_parent")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
            }

                if (!player.isSameServer(child.getUniqueId()) && PluginConfig.adoptProposeRange >= 0) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("player_not_same_server").replace("%player%", child.getName()));
                    return true;
                }

                if (!player.isInRange(child.getUniqueId(), PluginConfig.adoptProposeRange)) {
                    player.sendMessage(language.getPrefix() + language.getMessage("player_too_far_away").replace("%player%", child.getName()));
                    return true;
                }

                UUID childUUID = child.getUniqueId();
                FamilyPlayer childFam = new FamilyPlayer(childUUID);

                if (args[1].equalsIgnoreCase(player.getName())) {
                    player.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_self_request"));
                } else if (playerFam.isFamilyMember(childFam.getID())) {
                    player.sendMessage(language.getPrefix() + language.getMessage("marry_propose_family_request").replace("%player%", childFam.getName()));
                } else if (LunaticFamily.adoptRequests.containsKey(childUUID)) {
                    player.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_open_request").replace("%player%", childFam.getName()));
                } else if (childFam.getParents() == null) {
                    player.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_already_adopted").replace("%player%", childFam.getName()));
                } else if (childFam.hasSibling() && !confirm) {
                    player.sendMessage(new ClickableDecisionMessage(
                            language.getMessage("adopt_propose_has_sibling").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()),
                            language.getMessage("confirm"),
                            "/family adopt propose " + child.getName() + " confirm",
                            language.getMessage("cancel"),
                            "/family adopt propose " + child.getName() + " cancel"));

                } else if (childFam.hasSibling() && playerFam.getChildrenAmount() > 0) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_has_sibling_limit").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()));
                } else {
                    if (playerFam.isMarried()) {
                        child.sendMessage(new ClickableDecisionMessage(
                                language.getMessage("adopt_propose_request").replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getPartner().getName()),
                                language.getMessage("accept"),
                                "/family adopt accept",
                                language.getMessage("deny"),
                                "/family adopt deny"));
                    } else {
                        child.sendMessage(new ClickableDecisionMessage(
                                language.getMessage("adopt_propose_request_by_single").replace("%player%", playerFam.getName()),
                                language.getMessage("accept"),
                                "/family adopt accept",
                                language.getMessage("deny"),
                                "/family adopt deny"));
                    }
                    LunaticFamily.adoptRequests.put(childUUID, playerUUID);
                    sender.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_request_sent").replace("%player%", childFam.getName()));

                    Runnable runnable = () -> {
                        if (LunaticFamily.adoptRequests.containsKey(childUUID.toString())) {
                            LunaticFamily.adoptRequests.remove(childUUID.toString());
                            if (playerFam.isMarried()) {
                                FamilyPlayer partnerFam = playerFam.getPartner();
                                child.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_request_expired").replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()));
                            } else {
                                child.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_request_by_single_expired").replace("%player%", playerFam.getName()));
                            }
                            player.sendMessage(language.getPrefix() + language.getMessage("adopt_request_sent_expired").replace("%player%", childFam.getName()));
                        }
                    };

                    Utils.scheduleTask(runnable, 30, TimeUnit.SECONDS);
                }
        }
        return true;
    }
}
