package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.commands.ClickableDecisionMessage;
import de.janschuri.lunaticFamily.commands.senders.CommandSender;
import de.janschuri.lunaticFamily.commands.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;

public class AdoptMoveoutSubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "moveout";
    private static final String permission = "lunaticfamily.adopt";
    public AdoptMoveoutSubcommand() {
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


            if (!playerFam.isAdopted()) {
                player.sendMessage(Language.prefix + Language.getMessage("adopt_moveout_no_parents"));
                return true;
            } else if (!confirm) {
                player.sendMessage(new ClickableDecisionMessage(
                        Language.getMessage("adopt_moveout_confirm"),
                        Language.getMessage("confirm"),
                        "/family adopt moveout confirm",
                        Language.getMessage("cancel"),
                        "/family adopt moveout cancel"));
                return true;
            } else if (cancel) {
                sender.sendMessage(Language.prefix + Language.getMessage("adopt_moveout_cancel"));
                return true;
            } else if (!force && !player.hasEnoughMoney("adopt_moveout_child")) {
                sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                return true;
            }

            PlayerCommandSender firstParent = player.getPlayerCommandSender(playerFam.getParents().get(0).getUniqueId());

            if (!force && playerFam.getParents().size() == 2 && !firstParent.hasEnoughMoney(0.5, "adopt_moveout_parent")) {
                player.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(1).getName()));
                player.sendMessage(new ClickableDecisionMessage(
                        Language.getMessage("take_payment_confirm"),
                        Language.getMessage("confirm"),
                        "/family adopt moveout confirm force",
                        Language.getMessage("cancel"),
                        "/family adopt moveout cancel"));
                return true;
            } else if (!force && playerFam.getParents().size() == 1 && !firstParent.hasEnoughMoney("adopt_moveout_parent")) {
                player.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(0).getName()));
                player.sendMessage(new ClickableDecisionMessage(
                        Language.getMessage("take_payment_confirm"),
                        Language.getMessage("confirm"),
                        "/family adopt moveout confirm force",
                        Language.getMessage("cancel"),
                        "/family adopt moveout cancel"));
                return true;
            }

            PlayerCommandSender secondParent = player.getPlayerCommandSender(playerFam.getParents().get(1).getUniqueId());

            if (!force && playerFam.getParents().size() == 2 && !secondParent.hasEnoughMoney(0.5, "adopt_moveout_parent")) {
                player.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(1).getName()));
                player.sendMessage(new ClickableDecisionMessage(
                        Language.getMessage("take_payment_confirm"),
                        Language.getMessage("confirm"),
                        "/family adopt moveout confirm force",
                        Language.getMessage("cancel"),
                        "/family adopt moveout cancel"));
            } else if (force && !player.hasEnoughMoney("adopt_moveout_parent", "adopt_moveout_child")) {
                sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
            } else {
                FamilyPlayer firstParentFam = playerFam.getParents().get(0);

                if (playerFam.hasSibling()) {
                    FamilyPlayer siblingFam = playerFam.getSibling();
                    CommandSender sibling = player.getPlayerCommandSender(siblingFam.getUniqueId());
                    sibling.sendMessage(Language.prefix + Language.getMessage("adopt_moveout_sibling"));
                }

                sender.sendMessage(Language.prefix + Language.getMessage("adopt_moveout"));


                firstParent.sendMessage(Language.prefix + Language.getMessage("adopt_moveout_child").replace("%player%", playerFam.getName()));
                if (firstParentFam.isMarried()) {
                    FamilyPlayer secondParentFam = firstParentFam.getPartner();
                    secondParent.sendMessage(Language.prefix + Language.getMessage("adopt_moveout_child").replace("%player%", playerFam.getName()));
                }

                if (force) {
                    player.withdrawMoney("moveout_child");
                    player.withdrawMoney(0.5, "adopt_moveout_parent");
                    player.withdrawMoney(0.5, "adopt_moveout_parent");
                } else {
                    if (firstParentFam.isMarried()) {
                        FamilyPlayer secondParentFam = firstParentFam.getPartner();
                        secondParent.withdrawMoney(0.5, "adopt_moveout_parent");
                        firstParent.withdrawMoney(0.5, "adopt_moveout_parent");

                        for (String command : PluginConfig.successCommands.get("moveout")) {
                            command = command.replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()).replace("%child%", playerFam.getName());
                            Utils.getUtils().sendConsoleCommand(command);
                        }
                    } else {
                        firstParent.withdrawMoney("adopt_moveout_parent");

                        for (String command : PluginConfig.successCommands.get("moveout_single")) {
                            command = command.replace("%parent%", firstParentFam.getName()).replace("%child%", playerFam.getName());
                            Utils.getUtils().sendConsoleCommand(command);
                        }
                    }
                    player.withdrawMoney("adopt_moveout_child");
                }

                firstParentFam.unadopt(playerFam.getID());

            }
        }
        return true;
    }
}
