package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;

public class AdoptKickoutSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "adopt";
    private static final String NAME = "kickout";
    private static final String PERMISSION = "lunaticfamily.adopt";

    public AdoptKickoutSubcommand() {
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

            if (playerFam.getChildren().get(0) != null || playerFam.getChildren().get(1) != null) {
                if (args.length == 0) {
                    player.sendMessage(getPrefix() + getMessage("adopt_kickout_specify_child"));
                } else {

                    String childName = args[0];

                    UUID childUUID = PlayerDataTable.getUUID(childName);

                    if (childUUID == null) {
                        player.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", childName));
                        return true;
                    }

                    PlayerSender child = LunaticLib.getPlatform().getPlayerSender(childUUID);
                    FamilyPlayerImpl childFam = new FamilyPlayerImpl(childUUID);
                    if (childFam.isChildOf(playerFam.getId())) {

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
                            player.sendMessage(getPrefix() + getMessage("adopt_kickout_cancel"));
                            return true;
                        }

                        if (!confirm) {
                            player.sendMessage(Utils.getClickableDecisionMessage(
                                    getPrefix() + getMessage("adopt_kickout_confirm").replace("%player%", child.getName()),
                                    getMessage("confirm"),
                                    "/family adopt kickout " + args[0] + " confirm",
                                    getMessage("cancel"),
                                    "/family adopt kickout " + args[0] + " cancel"));
                            return true;
                        }

                        if (!force && playerFam.isMarried() && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, 0.5, "adopt_kickout_parent")) {
                            player.sendMessage(getPrefix() + getMessage("not_enough_money"));
                            return true;
                        }
                        if (!force && !playerFam.isMarried() && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, "adopt_kickout_parent")) {
                            player.sendMessage(getPrefix() + getMessage("not_enough_money"));
                            return true;
                        }

                        if (!Utils.hasEnoughMoney(player.getServerName(), childUUID, "adopt_kickout_child")) {
                            player.sendMessage(getPrefix() + getMessage("player_not_enough_money").replace("%player%", childFam.getName()));
                            player.sendMessage(Utils.getClickableDecisionMessage(
                                    getPrefix()+ getMessage("take_payment_confirm"),
                                    getMessage("confirm"),
                                    "/family adopt kickout confirm force",
                                    getMessage("cancel"),
                                    "/family adopt kickout confirm force"));
                            return true;
                        }

                        if (!force && playerFam.isMarried()) {
                            UUID partnerUUID = playerFam.getPartner().getUniqueId();
                            if (!Utils.hasEnoughMoney(player.getServerName(), partnerUUID, "adopt_kickout_parent")) {
                                player.sendMessage(getPrefix() + getMessage("player_not_enough_money").replace("%player%", playerFam.getPartner().getName()));
                                player.sendMessage(Utils.getClickableDecisionMessage(
                                        getPrefix()+ getMessage("take_payment_confirm"),
                                        getMessage("confirm"),
                                        "/family adopt kickout confirm force",
                                        getMessage("cancel"),
                                        "/family adopt kickout confirm force"));
                                return true;
                            }
                        }
                            player.sendMessage(getPrefix() + getMessage("adopt_kickout").replace("%player%", childFam.getName()));
                            if (playerFam.isMarried()) {
                                PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(playerFam.getPartner().getUniqueId());
                                partner.sendMessage(getPrefix() + getMessage("adopt_kickout_partner").replace("%player1%", playerFam.getName()).replace("%player2%", childFam.getName()));
                            }

                            if (childFam.hasSibling()) {
                                FamilyPlayerImpl siblingFam = childFam.getSibling();
                                PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingFam.getUniqueId());
                                sibling.sendMessage(getPrefix() + getMessage("adopt_kickout_sibling").replace("%player%", playerFam.getName()));
                            }
                            child.sendMessage(getPrefix() + getMessage("adopt_kickout_child").replace("%player%", playerFam.getName()));

                            if (force) {
                                Utils.withdrawMoney(player.getServerName(), playerUUID, "adopt_kickout_parent", "adopt_kickout_child");
                            } else {
                                if (playerFam.isMarried()) {
                                    UUID partnerUUID = playerFam.getPartner().getUniqueId();
                                    Utils.withdrawMoney(player.getServerName(), partnerUUID, 0.5, "adopt_kickout_parent");
                                    Utils.withdrawMoney(player.getServerName(), playerUUID, 0.5, "adopt_kickout_parent");

                                    for (String command : LunaticFamily.getConfig().getSuccessCommands("kickout")) {
                                        command = command.replace("%parent1%", playerFam.getName()).replace("%parent2%", playerFam.getPartner().getName()).replace("%child%", childFam.getName());
                                        LunaticLib.getPlatform().sendConsoleCommand(command);
                                    }
                                } else {
                                    Utils.withdrawMoney(player.getServerName(), playerUUID, "adopt_kickout_parent");

                                    for (String command : LunaticFamily.getConfig().getSuccessCommands("kickout_single")) {
                                        command = command.replace("%parent%", playerFam.getName()).replace("%child%", childFam.getName());
                                        LunaticLib.getPlatform().sendConsoleCommand(command);
                                    }
                                }
                                Utils.withdrawMoney(player.getServerName(), childUUID, "adopt_kickout_child");
                            }

                            playerFam.unadopt(childFam.getId());


                    } else {
                        sender.sendMessage(getPrefix() + getMessage("adopt_kickout_not_your_child").replace("%player%", childFam.getName()));
                    }
                }
            } else {
                sender.sendMessage(getPrefix() + getMessage("adopt_kickout_no_child"));
            }
        }
        return true;
    }
}
