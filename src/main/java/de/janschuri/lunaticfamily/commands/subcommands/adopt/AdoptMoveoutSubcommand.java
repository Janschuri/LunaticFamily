package de.janschuri.lunaticfamily.commands.subcommands.adopt;

import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.config.PluginConfig;
import de.janschuri.lunaticfamily.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.ClickableDecisionMessage;

import java.util.UUID;

public class AdoptMoveoutSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "adopt";
    private static final String NAME = "moveout";
    private static final String PERMISSION = "lunaticfamily.adopt";
    public AdoptMoveoutSubcommand() {
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
            boolean force = false;

            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("confirm")) {
                    confirm = true;
                }
                if (args[0].equalsIgnoreCase("cancel")) {
                    cancel = true;
                }
            }
            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("force")) {
                    force = true;
                }
            }


            if (!playerFam.isAdopted()) {
                player.sendMessage(language.getPrefix() + language.getMessage("adopt_moveout_no_parents"));
                return true;
            }
            if (cancel) {
                sender.sendMessage(language.getPrefix() + language.getMessage("adopt_moveout_cancel"));
                return true;
            }
            if (!confirm) {
                player.sendMessage(new ClickableDecisionMessage(
                        language.getPrefix()+ language.getMessage("adopt_moveout_confirm"),
                        language.getMessage("confirm"),
                        "/family adopt moveout confirm",
                        language.getMessage("cancel"),
                        "/family adopt moveout cancel"));
                return true;
            }
            if (!force && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, "adopt_moveout_child")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
                return true;
            }

            UUID parent1UUID = playerFam.getParents().get(0).getUniqueId();
            AbstractPlayerSender firstParent = AbstractSender.getPlayerSender(parent1UUID);

            if (!force && playerFam.getParents().size() == 2 && !Utils.hasEnoughMoney(player.getServerName(), parent1UUID, 0.5, "adopt_moveout_parent")) {
                player.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(1).getName()));
                player.sendMessage(new ClickableDecisionMessage(
                        language.getPrefix()+ language.getMessage("take_payment_confirm"),
                        language.getMessage("confirm"),
                        "/family adopt moveout confirm force",
                        language.getMessage("cancel"),
                        "/family adopt moveout cancel"));
                return true;
            } else if (!force && playerFam.getParents().size() == 1 && !Utils.hasEnoughMoney(player.getServerName(), parent1UUID, "adopt_moveout_parent")) {
                player.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(0).getName()));
                player.sendMessage(new ClickableDecisionMessage(
                        language.getPrefix()+ language.getMessage("take_payment_confirm"),
                        language.getMessage("confirm"),
                        "/family adopt moveout confirm force",
                        language.getMessage("cancel"),
                        "/family adopt moveout cancel"));
                return true;
            }

            UUID parent2UUID = playerFam.getParents().get(1).getUniqueId();
            AbstractPlayerSender secondParent = AbstractSender.getPlayerSender(parent2UUID);

            if (!force && playerFam.getParents().size() == 2 && !Utils.hasEnoughMoney(player.getServerName(), parent2UUID, 0.5, "adopt_moveout_parent")) {
                player.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(1).getName()));
                player.sendMessage(new ClickableDecisionMessage(
                        language.getPrefix()+ language.getMessage("take_payment_confirm"),
                        language.getMessage("confirm"),
                        "/family adopt moveout confirm force",
                        language.getMessage("cancel"),
                        "/family adopt moveout cancel"));
            } else if (force && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, "adopt_moveout_parent", "adopt_moveout_child")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
            } else {
                FamilyPlayer firstParentFam = playerFam.getParents().get(0);

                if (playerFam.hasSibling()) {
                    FamilyPlayer siblingFam = playerFam.getSibling();
                    AbstractSender sibling = AbstractSender.getPlayerSender(siblingFam.getUniqueId());
                    sibling.sendMessage(language.getPrefix() + language.getMessage("adopt_moveout_sibling"));
                }

                sender.sendMessage(language.getPrefix() + language.getMessage("adopt_moveout"));


                firstParent.sendMessage(language.getPrefix() + language.getMessage("adopt_moveout_child").replace("%player%", playerFam.getName()));
                if (firstParentFam.isMarried()) {
                    secondParent.sendMessage(language.getPrefix() + language.getMessage("adopt_moveout_child").replace("%player%", playerFam.getName()));
                }

                if (force) {
                    Utils.withdrawMoney(player.getServerName(), playerUUID, "moveout_child");
                    Utils.withdrawMoney(player.getServerName(), playerUUID, 0.5, "adopt_moveout_parent");
                    Utils.withdrawMoney(player.getServerName(), playerUUID, 0.5, "adopt_moveout_parent");
                } else {
                    if (firstParentFam.isMarried()) {
                        FamilyPlayer secondParentFam = firstParentFam.getPartner();
                        Utils.withdrawMoney(player.getServerName(), parent2UUID, 0.5, "adopt_moveout_parent");
                        Utils.withdrawMoney(player.getServerName(), parent1UUID, 0.5, "adopt_moveout_parent");

                        for (String command : PluginConfig.getSuccessCommands("moveout")) {
                            command = command.replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()).replace("%child%", playerFam.getName());
                            Utils.sendConsoleCommand(command);
                        }
                    } else {
                        Utils.withdrawMoney(player.getServerName(), parent1UUID, "adopt_moveout_parent");

                        for (String command : PluginConfig.getSuccessCommands("moveout_single")) {
                            command = command.replace("%parent%", firstParentFam.getName()).replace("%child%", playerFam.getName());
                            Utils.sendConsoleCommand(command);
                        }
                    }
                    Utils.withdrawMoney(player.getServerName(), playerUUID, "adopt_moveout_child");
                }

                firstParentFam.unadopt(playerFam.getID());

            }
        }
        return true;
    }
}
