package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdoptKickoutSubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "kickout";
    private static final String permission = "lunaticfamily.adopt";

    public AdoptKickoutSubcommand() {
        super(mainCommand, name, permission);
    }

    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            Player player = (Player) sender;
            String playerUUID = player.getUniqueId().toString();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (playerFam.getChildren().get(0) != null || playerFam.getChildren().get(1) != null) {
                if (args.length == 1) {
                    sender.sendMessage(Language.prefix + Language.getMessage("adopt_kickout_specify_child"));
                } else {
                    String childUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
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
                            sender.sendMessage(Utils.createClickableMessage(
                                    Language.getMessage("adopt_kickout_confirm").replace("%player%", Utils.getName(args[1])),
                                    Language.getMessage("confirm"),
                                    "/lunaticfamily:adopt kickout " + args[1] + " confirm",
                                    Language.getMessage("cancel"),
                                    "/lunaticfamily:adopt kickout " + args[1] + " cancel"));
                        } else if (cancel) {
                            sender.sendMessage(Language.prefix + Language.getMessage("adopt_kickout_cancel"));
                        } else if (!force && playerFam.isMarried() && !playerFam.hasEnoughMoney("adopt_kickout_parent", 0.5)) {
                            sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                        } else if (!force && !playerFam.isMarried() && !playerFam.hasEnoughMoney("adopt_kickout_parent")) {
                            sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                        } else if (!childFam.hasEnoughMoney("adopt_kickout_child")) {
                            sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", childFam.getName()));
                            sender.sendMessage(Utils.createClickableMessage(
                                    Language.getMessage("take_payment_confirm"),
                                    Language.getMessage("confirm"),
                                    "/lunaticfamily:adopt kickout confirm force",
                                    Language.getMessage("cancel"),
                                    "/lunaticfamily:adopt kickout confirm force"));
                        } else if (!force && playerFam.isMarried() && !playerFam.getPartner().hasEnoughMoney("adopt_kickout_parent")) {
                            sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", playerFam.getPartner().getName()));
                            sender.sendMessage(Utils.createClickableMessage(
                                    Language.getMessage("take_payment_confirm"),
                                    Language.getMessage("confirm"),
                                    "/lunaticfamily:adopt kickout confirm force",
                                    Language.getMessage("cancel"),
                                    "/lunaticfamily:adopt kickout confirm force"));
                        } else {
                            sender.sendMessage(Language.prefix + Language.getMessage("adopt_kickout").replace("%player%", childFam.getName()));
                            if (playerFam.isMarried()) {
                                playerFam.getPartner().sendMessage(Language.prefix + Language.getMessage("adopt_kickout_partner").replace("%player1%", playerFam.getName()).replace("%player2%", childFam.getName()));
                            }

                            if (childFam.hasSibling()) {
                                FamilyPlayer siblingFam = childFam.getSibling();
                                siblingFam.sendMessage(Language.prefix + Language.getMessage("adopt_kickout_sibling").replace("%player%", playerFam.getName()));
                            }
                            childFam.sendMessage(Language.prefix + Language.getMessage("adopt_kickout_child").replace("%player%", playerFam.getName()));

                            if (force) {
                                playerFam.withdrawPlayer("adopt_kickout_parent", "adopt_kickout_child");
                            } else {
                                if (playerFam.isMarried()) {
                                    playerFam.getPartner().withdrawPlayer("adopt_kickout_parent", 0.5);
                                    playerFam.withdrawPlayer("adopt_kickout_parent", 0.5);

                                    for (String command : Config.successCommands.get("kickout")) {
                                        command = command.replace("%parent1%", playerFam.getName()).replace("%parent2%", playerFam.getPartner().getName()).replace("%child%", childFam.getName());
                                        Utils.sendConsoleCommand(command);
                                    }
                                } else {
                                    playerFam.withdrawPlayer("adopt_kickout_parent");

                                    for (String command : Config.successCommands.get("kickout_single")) {
                                        command = command.replace("%parent%", playerFam.getName()).replace("%child%", childFam.getName());
                                        Utils.sendConsoleCommand(command);
                                    }
                                }
                                childFam.withdrawPlayer("adopt_kickout_child");
                            }

                            playerFam.unadopt(childFam.getID());
                        }

                    } else {
                        sender.sendMessage(Language.prefix + Language.getMessage("adopt_kickout_not_your_child").replace("%player%", childFam.getName()));
                    }
                }
            } else {
                sender.sendMessage(Language.prefix + Language.getMessage("adopt_kickout_no_child"));
            }
        }
    }
}
