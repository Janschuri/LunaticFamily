package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;

public class AdoptMoveoutSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "adopt";
    private static final String NAME = "moveout";
    private static final String PERMISSION = "lunaticfamily.adopt";
    public AdoptMoveoutSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getPrefix() + getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else {
            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

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
                player.sendMessage(getPrefix() + getMessage("adopt_moveout_no_parents"));
                return true;
            }
            if (cancel) {
                sender.sendMessage(getPrefix() + getMessage("adopt_moveout_cancel"));
                return true;
            }
            if (!confirm) {
                player.sendMessage(Utils.getClickableDecisionMessage(
                        getPrefix()+ getMessage("adopt_moveout_confirm"),
                        getMessage("confirm"),
                        "/family adopt moveout confirm",
                        getMessage("cancel"),
                        "/family adopt moveout cancel"));
                return true;
            }
            if (!force && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, "adopt_moveout_child")) {
                sender.sendMessage(getPrefix() + getMessage("not_enough_money"));
                return true;
            }

            UUID parent1UUID = playerFam.getParents().get(0).getUniqueId();
            PlayerSender firstParent = LunaticLib.getPlatform().getPlayerSender(parent1UUID);

            if (!force && playerFam.getParents().size() == 2 && !Utils.hasEnoughMoney(player.getServerName(), parent1UUID, 0.5, "adopt_moveout_parent")) {
                player.sendMessage(getPrefix() + getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(1).getName()));
                player.sendMessage(Utils.getClickableDecisionMessage(
                        getPrefix()+ getMessage("take_payment_confirm"),
                        getMessage("confirm"),
                        "/family adopt moveout confirm force",
                        getMessage("cancel"),
                        "/family adopt moveout cancel"));
                return true;
            } else if (!force && playerFam.getParents().size() == 1 && !Utils.hasEnoughMoney(player.getServerName(), parent1UUID, "adopt_moveout_parent")) {
                player.sendMessage(getPrefix() + getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(0).getName()));
                player.sendMessage(Utils.getClickableDecisionMessage(
                        getPrefix()+ getMessage("take_payment_confirm"),
                        getMessage("confirm"),
                        "/family adopt moveout confirm force",
                        getMessage("cancel"),
                        "/family adopt moveout cancel"));
                return true;
            }

            UUID parent2UUID = playerFam.getParents().get(1).getUniqueId();
            PlayerSender secondParent = LunaticLib.getPlatform().getPlayerSender(parent2UUID);

            if (!force && playerFam.getParents().size() == 2 && !Utils.hasEnoughMoney(player.getServerName(), parent2UUID, 0.5, "adopt_moveout_parent")) {
                player.sendMessage(getPrefix() + getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(1).getName()));
                player.sendMessage(Utils.getClickableDecisionMessage(
                        getPrefix()+ getMessage("take_payment_confirm"),
                        getMessage("confirm"),
                        "/family adopt moveout confirm force",
                        getMessage("cancel"),
                        "/family adopt moveout cancel"));
            } else if (force && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, "adopt_moveout_parent", "adopt_moveout_child")) {
                sender.sendMessage(getPrefix() + getMessage("not_enough_money"));
            } else {
                FamilyPlayerImpl firstParentFam = (FamilyPlayerImpl) playerFam.getParents().get(0);

                if (playerFam.hasSibling()) {
                    FamilyPlayerImpl siblingFam = playerFam.getSibling();
                    Sender sibling = LunaticLib.getPlatform().getPlayerSender(siblingFam.getUniqueId());
                    sibling.sendMessage(getPrefix() + getMessage("adopt_moveout_sibling"));
                }

                sender.sendMessage(getPrefix() + getMessage("adopt_moveout"));


                firstParent.sendMessage(getPrefix() + getMessage("adopt_moveout_child").replace("%player%", playerFam.getName()));
                if (firstParentFam.isMarried()) {
                    secondParent.sendMessage(getPrefix() + getMessage("adopt_moveout_child").replace("%player%", playerFam.getName()));
                }

                if (force) {
                    Utils.withdrawMoney(player.getServerName(), playerUUID, "moveout_child");
                    Utils.withdrawMoney(player.getServerName(), playerUUID, 0.5, "adopt_moveout_parent");
                    Utils.withdrawMoney(player.getServerName(), playerUUID, 0.5, "adopt_moveout_parent");
                } else {
                    if (firstParentFam.isMarried()) {
                        FamilyPlayerImpl secondParentFam = firstParentFam.getPartner();
                        Utils.withdrawMoney(player.getServerName(), parent2UUID, 0.5, "adopt_moveout_parent");
                        Utils.withdrawMoney(player.getServerName(), parent1UUID, 0.5, "adopt_moveout_parent");

                        for (String command : LunaticFamily.getConfig().getSuccessCommands("moveout")) {
                            command = command.replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()).replace("%child%", playerFam.getName());
                            LunaticLib.getPlatform().sendConsoleCommand(command);
                        }
                    } else {
                        Utils.withdrawMoney(player.getServerName(), parent1UUID, "adopt_moveout_parent");

                        for (String command : LunaticFamily.getConfig().getSuccessCommands("moveout_single")) {
                            command = command.replace("%parent%", firstParentFam.getName()).replace("%child%", playerFam.getName());
                            LunaticLib.getPlatform().sendConsoleCommand(command);
                        }
                    }
                    Utils.withdrawMoney(player.getServerName(), playerUUID, "adopt_moveout_child");
                }

                firstParentFam.unadopt(playerFam.getId());

            }
        }
        return true;
    }
}
