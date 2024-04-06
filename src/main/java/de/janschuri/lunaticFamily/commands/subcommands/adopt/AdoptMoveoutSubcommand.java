package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdoptMoveoutSubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "moveout";
    private static final String permission = "lunaticfamily.adopt";
    public AdoptMoveoutSubcommand() {
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
                sender.sendMessage(Language.prefix + Language.getMessage("adopt_moveout_no_parents"));
            } else if (!confirm) {
                sender.sendMessage(Utils.createClickableMessage(
                        Language.getMessage("adopt_moveout_confirm"),
                        Language.getMessage("confirm"),
                        "/lunaticfamily:adopt moveout confirm",
                        Language.getMessage("cancel"),
                        "/lunaticfamily:adopt moveout cancel"));
            } else if (cancel) {
                sender.sendMessage(Language.prefix + Language.getMessage("adopt_moveout_cancel"));
            } else if (!force && !playerFam.hasEnoughMoney("adopt_moveout_child")) {
                sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
            } else if (!force && playerFam.getParents().size() == 2 && !playerFam.getParents().get(0).hasEnoughMoney("adopt_moveout_parent", 0.5)) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(1).getName()));
                sender.sendMessage(Utils.createClickableMessage(
                        Language.getMessage("take_payment_confirm"),
                        Language.getMessage("confirm"),
                        "/lunaticfamily:adopt moveout confirm force",
                        Language.getMessage("cancel"),
                        "/lunaticfamily:adopt moveout cancel"));
            } else if (!force && playerFam.getParents().size() == 1 && !playerFam.getParents().get(0).hasEnoughMoney("adopt_moveout_parent")) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(0).getName()));
                sender.sendMessage(Utils.createClickableMessage(
                        Language.getMessage("take_payment_confirm"),
                        Language.getMessage("confirm"),
                        "/lunaticfamily:adopt moveout confirm force",
                        Language.getMessage("cancel"),
                        "/lunaticfamily:adopt moveout cancel"));
            } else if (!force && playerFam.getParents().size() == 2 && !playerFam.getParents().get(1).hasEnoughMoney("adopt_moveout_parent", 0.5)) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(1).getName()));
                sender.sendMessage(Utils.createClickableMessage(
                        Language.getMessage("take_payment_confirm"),
                        Language.getMessage("confirm"),
                        "/lunaticfamily:adopt moveout confirm force",
                        Language.getMessage("cancel"),
                        "/lunaticfamily:adopt moveout cancel"));
            } else if (force && !playerFam.hasEnoughMoney("adopt_moveout_parent", "adopt_moveout_child")) {
                sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
            } else {
                FamilyPlayer firstParentFam = playerFam.getParents().get(0);

                if (playerFam.hasSibling()) {
                    FamilyPlayer siblingFam = playerFam.getSibling();
                    siblingFam.sendMessage(Language.prefix + Language.getMessage("adopt_moveout_sibling"));
                }

                sender.sendMessage(Language.prefix + Language.getMessage("adopt_moveout"));


                firstParentFam.sendMessage(Language.prefix + Language.getMessage("adopt_moveout_child").replace("%player%", playerFam.getName()));
                if (firstParentFam.isMarried()) {
                    FamilyPlayer secondParentFam = firstParentFam.getPartner();
                    secondParentFam.sendMessage(Language.prefix + Language.getMessage("adopt_moveout_child").replace("%player%", playerFam.getName()));
                }

                if (force) {
                    playerFam.withdrawPlayer("moveout_child");
                    playerFam.withdrawPlayer("adopt_moveout_parent", 0.5);
                    playerFam.withdrawPlayer("adopt_moveout_parent", 0.5);
                } else {
                    if (firstParentFam.isMarried()) {
                        FamilyPlayer secondParentFam = firstParentFam.getPartner();
                        secondParentFam.withdrawPlayer("adopt_moveout_parent", 0.5);
                        firstParentFam.withdrawPlayer("adopt_moveout_parent", 0.5);

                        for (String command : Config.successCommands.get("moveout")) {
                            command = command.replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()).replace("%child%", playerFam.getName());
                            Utils.sendConsoleCommand(command);
                        }
                    } else {
                        firstParentFam.withdrawPlayer("adopt_moveout_parent");

                        for (String command : Config.successCommands.get("moveout_single")) {
                            command = command.replace("%parent%", firstParentFam.getName()).replace("%child%", playerFam.getName());
                            Utils.sendConsoleCommand(command);
                        }
                    }
                    playerFam.withdrawPlayer("adopt_moveout_child");
                }

                firstParentFam.unadopt(playerFam.getID());

            }
        }
    }
}
