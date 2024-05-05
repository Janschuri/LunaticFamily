package de.janschuri.lunaticfamily.commands.subcommands.adopt;

import de.janschuri.lunaticfamily.LunaticFamily;
import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.config.PluginConfig;
import de.janschuri.lunaticfamily.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.ClickableDecisionMessage;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AdoptProposeSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "adopt";
    private static final String NAME = "propose";
    private static final String PERMISSION = "lunaticfamily.adopt";
    public AdoptProposeSubcommand() {
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

            boolean confirm = false;
            boolean cancel = false;

            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("confirm")) {
                    confirm = true;
                }
                if (args[2].equalsIgnoreCase("cancel")) {
                    cancel = true;
                }
            }


            if (args.length < 1) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                return true;
            } else if (cancel) {
                sender.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_cancel").replace("%player%", args[2]));
                return true;
            } else if (!playerFam.isMarried() && !PluginConfig.isAllowSingleAdopt()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_no_single_adopt"));
                return true;
            } else if (playerFam.getChildrenAmount() > 1) {
                sender.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_limit"));
                return true;
            }

            AbstractPlayerSender child = AbstractSender.getPlayerSender(args[0]);

            if (!child.exists()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[0]));
                return true;
            }
            if (!child.isOnline()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_offline").replace("%player%", args[0]));
                return true;
            }

            if (!Utils.isPlayerOnRegisteredServer(child.getUniqueId())) {
                player.sendMessage(language.getPrefix() + language.getMessage("player_not_on_whitelisted_server").replace("%player%", child.getName().replace("%server%", child.getServerName())));
                return true;
            }

            if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "adopt_parent")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
                return true;
            }

                if (!player.isSameServer(child.getUniqueId()) && PluginConfig.getAdoptProposeRange() >= 0) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("player_not_same_server").replace("%player%", child.getName()));
                    return true;
                }

                if (!player.isInRange(child.getUniqueId(), PluginConfig.getAdoptProposeRange())) {
                    player.sendMessage(language.getPrefix() + language.getMessage("player_too_far_away").replace("%player%", child.getName()));
                    return true;
                }

                UUID childUUID = child.getUniqueId();
                FamilyPlayer childFam = new FamilyPlayer(childUUID);

                if (args[0].equalsIgnoreCase(player.getName())) {
                    player.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_self_request"));
                } else if (playerFam.isFamilyMember(childFam.getID())) {
                    player.sendMessage(language.getPrefix() + language.getMessage("marry_propose_family_request").replace("%player%", childFam.getName()));
                } else if (LunaticFamily.adoptRequests.containsKey(childUUID)) {
                    player.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_open_request").replace("%player%", childFam.getName()));
                } else if (childFam.getParents() == null) {
                    player.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_already_adopted").replace("%player%", childFam.getName()));
                } else if (childFam.hasSibling() && !confirm) {
                    player.sendMessage(new ClickableDecisionMessage(
                            language.getPrefix() + language.getMessage("adopt_propose_has_sibling").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()),
                            language.getMessage("confirm"),
                            "/family adopt propose " + child.getName() + " confirm",
                            language.getMessage("cancel"),
                            "/family adopt propose " + child.getName() + " cancel"));

                } else if (childFam.hasSibling() && playerFam.getChildrenAmount() > 0) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_has_sibling_limit").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()));
                } else {
                    if (playerFam.isMarried()) {
                        child.sendMessage(new ClickableDecisionMessage(
                                language.getPrefix() + language.getMessage("adopt_propose_request").replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getPartner().getName()),
                                language.getMessage("accept"),
                                "/family adopt accept",
                                language.getMessage("deny"),
                                "/family adopt deny"));
                    } else {
                        child.sendMessage(new ClickableDecisionMessage(
                                language.getPrefix() + language.getMessage("adopt_propose_request_by_single").replace("%player%", playerFam.getName()),
                                language.getMessage("accept"),
                                "/family adopt accept",
                                language.getMessage("deny"),
                                "/family adopt deny"));
                    }
                    LunaticFamily.adoptRequests.put(childUUID, playerUUID);
                    sender.sendMessage(language.getPrefix() + language.getMessage("adopt_propose_request_sent").replace("%player%", childFam.getName()));

                    Runnable runnable = () -> {
                        if (LunaticFamily.adoptRequests.containsKey(childUUID)) {
                            LunaticFamily.adoptRequests.remove(childUUID);
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
