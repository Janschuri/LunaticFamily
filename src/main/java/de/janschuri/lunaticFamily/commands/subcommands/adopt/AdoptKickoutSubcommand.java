package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.ClickableDecisionMessage;

import java.util.UUID;

public class AdoptKickoutSubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "kickout";
    private static final String permission = "lunaticfamily.adopt";

    public AdoptKickoutSubcommand() {
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

            if (playerFam.getChildren().get(0) != null || playerFam.getChildren().get(1) != null) {
                if (args.length == 1) {
                    player.sendMessage(language.getPrefix() + language.getMessage("adopt_kickout_specify_child"));
                } else {

                    AbstractPlayerSender child = AbstractSender.getPlayerSender(args[1]);
                    UUID childUUID = child.getUniqueId();
                    FamilyPlayer childFam = new FamilyPlayer(childUUID);
                    if (childFam.isChildOf(playerFam.getID())) {

                        boolean confirm = false;
                        boolean cancel = false;
                        boolean force = false;
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("confirm")) {
                                confirm = true;
                            }
                            if (args[2].equalsIgnoreCase("cancel")) {
                                cancel = true;
                            }
                        }
                        if (args.length > 3) {
                            if (args[3].equalsIgnoreCase("force")) {
                                force = true;
                            }
                        }



                        if (!confirm) {
                            player.sendMessage(new ClickableDecisionMessage(
                                    language.getMessage("adopt_kickout_confirm").replace("%player%", child.getName()),
                                    language.getMessage("confirm"),
                                    "/family adopt kickout " + args[1] + " confirm",
                                    language.getMessage("cancel"),
                                    "/family adopt kickout " + args[1] + " cancel"));
                            return true;
                        } else if (cancel) {
                            player.sendMessage(language.getPrefix() + language.getMessage("adopt_kickout_cancel"));
                            return true;
                        } else if (!force && playerFam.isMarried() && !Utils.getUtils().hasEnoughMoney(playerUUID, 0.5, "adopt_kickout_parent")) {
                            player.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
                            return true;
                        } else if (!force && !playerFam.isMarried() && !Utils.getUtils().hasEnoughMoney(playerUUID, "adopt_kickout_parent")) {
                            player.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
                            return true;
                        }

                        if (!Utils.getUtils().hasEnoughMoney(childUUID, "adopt_kickout_child")) {
                            player.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", childFam.getName()));
                            player.sendMessage(new ClickableDecisionMessage(
                                    language.getMessage("take_payment_confirm"),
                                    language.getMessage("confirm"),
                                    "/family adopt kickout confirm force",
                                    language.getMessage("cancel"),
                                    "/family adopt kickout confirm force"));
                            return true;
                        }

                        if (!force && playerFam.isMarried()) {
                            UUID partnerUUID = playerFam.getPartner().getUniqueId();
                            if (!Utils.getUtils().hasEnoughMoney(partnerUUID, "adopt_kickout_parent")) {
                                player.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", playerFam.getPartner().getName()));
                                player.sendMessage(new ClickableDecisionMessage(
                                        language.getMessage("take_payment_confirm"),
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
                                Utils.getUtils().withdrawMoney(playerUUID, "adopt_kickout_parent", "adopt_kickout_child");
                            } else {
                                if (playerFam.isMarried()) {
                                    UUID partnerUUID = playerFam.getPartner().getUniqueId();
                                    Utils.getUtils().withdrawMoney(partnerUUID, 0.5, "adopt_kickout_parent");
                                    Utils.getUtils().withdrawMoney(playerUUID, 0.5, "adopt_kickout_parent");

                                    for (String command : PluginConfig.successCommands.get("kickout")) {
                                        command = command.replace("%parent1%", playerFam.getName()).replace("%parent2%", playerFam.getPartner().getName()).replace("%child%", childFam.getName());
                                        Utils.getUtils().sendConsoleCommand(command);
                                    }
                                } else {
                                    Utils.getUtils().withdrawMoney(playerUUID, "adopt_kickout_parent");

                                    for (String command : PluginConfig.successCommands.get("kickout_single")) {
                                        command = command.replace("%parent%", playerFam.getName()).replace("%child%", childFam.getName());
                                        Utils.getUtils().sendConsoleCommand(command);
                                    }
                                }
                                Utils.getUtils().withdrawMoney(childUUID, "adopt_kickout_child");
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
