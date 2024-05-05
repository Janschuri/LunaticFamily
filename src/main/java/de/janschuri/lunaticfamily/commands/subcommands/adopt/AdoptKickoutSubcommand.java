package de.janschuri.lunaticfamily.commands.subcommands.adopt;

import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.config.PluginConfig;
import de.janschuri.lunaticfamily.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.ClickableDecisionMessage;

import java.util.UUID;

public class AdoptKickoutSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "adopt";
    private static final String NAME = "kickout";
    private static final String PERMISSION = "lunaticfamily.adopt";

    public AdoptKickoutSubcommand() {
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

            if (playerFam.getChildren().get(0) != null || playerFam.getChildren().get(1) != null) {
                if (args.length == 0) {
                    player.sendMessage(language.getPrefix() + language.getMessage("adopt_kickout_specify_child"));
                } else {

                    AbstractPlayerSender child = AbstractSender.getPlayerSender(args[0]);
                    UUID childUUID = child.getUniqueId();
                    FamilyPlayer childFam = new FamilyPlayer(childUUID);
                    if (childFam.isChildOf(playerFam.getID())) {

                        boolean confirm = false;
                        boolean cancel = false;
                        boolean force = false;
                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("confirm")) {
                                confirm = true;
                            }
                            if (args[1].equalsIgnoreCase("cancel")) {
                                cancel = true;
                            }
                        }
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("force")) {
                                force = true;
                            }
                        }

                        if (cancel) {
                            player.sendMessage(language.getPrefix() + language.getMessage("adopt_kickout_cancel"));
                            return true;
                        }

                        if (!confirm) {
                            player.sendMessage(new ClickableDecisionMessage(
                                    language.getPrefix() + language.getMessage("adopt_kickout_confirm").replace("%player%", child.getName()),
                                    language.getMessage("confirm"),
                                    "/family adopt kickout " + args[0] + " confirm",
                                    language.getMessage("cancel"),
                                    "/family adopt kickout " + args[0] + " cancel"));
                            return true;
                        }

                        if (!force && playerFam.isMarried() && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, 0.5, "adopt_kickout_parent")) {
                            player.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
                            return true;
                        }
                        if (!force && !playerFam.isMarried() && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, "adopt_kickout_parent")) {
                            player.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
                            return true;
                        }

                        if (!Utils.hasEnoughMoney(player.getServerName(), childUUID, "adopt_kickout_child")) {
                            player.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", childFam.getName()));
                            player.sendMessage(new ClickableDecisionMessage(
                                    language.getPrefix()+ language.getMessage("take_payment_confirm"),
                                    language.getMessage("confirm"),
                                    "/family adopt kickout confirm force",
                                    language.getMessage("cancel"),
                                    "/family adopt kickout confirm force"));
                            return true;
                        }

                        if (!force && playerFam.isMarried()) {
                            UUID partnerUUID = playerFam.getPartner().getUniqueId();
                            if (!Utils.hasEnoughMoney(player.getServerName(), partnerUUID, "adopt_kickout_parent")) {
                                player.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", playerFam.getPartner().getName()));
                                player.sendMessage(new ClickableDecisionMessage(
                                        language.getPrefix()+ language.getMessage("take_payment_confirm"),
                                        language.getMessage("confirm"),
                                        "/family adopt kickout confirm force",
                                        language.getMessage("cancel"),
                                        "/family adopt kickout confirm force"));
                                return true;
                            }
                        }
                            player.sendMessage(language.getPrefix() + language.getMessage("adopt_kickout").replace("%player%", childFam.getName()));
                            if (playerFam.isMarried()) {
                                AbstractPlayerSender partner = AbstractSender.getPlayerSender(playerFam.getPartner().getUniqueId());
                                partner.sendMessage(language.getPrefix() + language.getMessage("adopt_kickout_partner").replace("%player1%", playerFam.getName()).replace("%player2%", childFam.getName()));
                            }

                            if (childFam.hasSibling()) {
                                FamilyPlayer siblingFam = childFam.getSibling();
                                AbstractPlayerSender sibling = AbstractSender.getPlayerSender(siblingFam.getUniqueId());
                                sibling.sendMessage(language.getPrefix() + language.getMessage("adopt_kickout_sibling").replace("%player%", playerFam.getName()));
                            }
                            child.sendMessage(language.getPrefix() + language.getMessage("adopt_kickout_child").replace("%player%", playerFam.getName()));

                            if (force) {
                                Utils.withdrawMoney(player.getServerName(), playerUUID, "adopt_kickout_parent", "adopt_kickout_child");
                            } else {
                                if (playerFam.isMarried()) {
                                    UUID partnerUUID = playerFam.getPartner().getUniqueId();
                                    Utils.withdrawMoney(player.getServerName(), partnerUUID, 0.5, "adopt_kickout_parent");
                                    Utils.withdrawMoney(player.getServerName(), playerUUID, 0.5, "adopt_kickout_parent");

                                    for (String command : PluginConfig.getSuccessCommands("kickout")) {
                                        command = command.replace("%parent1%", playerFam.getName()).replace("%parent2%", playerFam.getPartner().getName()).replace("%child%", childFam.getName());
                                        Utils.sendConsoleCommand(command);
                                    }
                                } else {
                                    Utils.withdrawMoney(player.getServerName(), playerUUID, "adopt_kickout_parent");

                                    for (String command : PluginConfig.getSuccessCommands("kickout_single")) {
                                        command = command.replace("%parent%", playerFam.getName()).replace("%child%", childFam.getName());
                                        Utils.sendConsoleCommand(command);
                                    }
                                }
                                Utils.withdrawMoney(player.getServerName(), childUUID, "adopt_kickout_child");
                            }

                            playerFam.unadopt(childFam.getID());


                    } else {
                        sender.sendMessage(language.getPrefix() + language.getMessage("adopt_kickout_not_your_child").replace("%player%", childFam.getName()));
                    }
                }
            } else {
                sender.sendMessage(language.getPrefix() + language.getMessage("adopt_kickout_no_child"));
            }
        }
        return true;
    }
}
