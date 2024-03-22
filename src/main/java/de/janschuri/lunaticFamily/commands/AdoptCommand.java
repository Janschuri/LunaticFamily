package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AdoptCommand implements CommandExecutor, TabCompleter {

    private final LunaticFamily plugin;

    public AdoptCommand(LunaticFamily plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 0) {
            if (!sender.hasPermission("lunaticFamily." + label)) {
                sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("no_permission"));
            } else {
                final String[] subcommandsHelp = {"propose", "kickout", "moveout"};

                StringBuilder msg = new StringBuilder(LunaticFamily.prefix + " " + LunaticFamily.getMessage(label + "_help") + "\n");

                for (String subcommand : subcommandsHelp) {
                    msg.append(LunaticFamily.prefix).append(" ").append(LunaticFamily.getMessage(label + "_" + subcommand + "_help")).append("\n");
                }
                sender.sendMessage(msg.toString());
            }
        } else {
            final String subcommand = args[0];
            if (checkIsSubcommand("set", subcommand)) {

                boolean force = false;

                if (args.length > 3) {
                    if (args[3].equalsIgnoreCase("force")) {
                        force = true;
                    }
                }

                if (!sender.hasPermission("lunaticFamily.admin.adopt.set")) {
                    sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("no_permission"));
                } else if (args.length < 3) {
                    sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("wrong_usage"));
                } else if (!LunaticFamily.playerExists(args[1]) && !force) {
                    sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("player_not_exist").replace("%player%", args[1]));
                } else if (!LunaticFamily.playerExists(args[2]) && !force) {
                    sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("player_not_exist").replace("%player%", args[2]));
                } else if (args[1].equalsIgnoreCase(args[2])) {
                    sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("admin_adopt_set_same_player"));
                } else {

                    String firstParentUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyPlayer firstParentFam = new FamilyPlayer(firstParentUUID);
                    String childUUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                    FamilyPlayer childFam = new FamilyPlayer(childUUID);

                    if (!firstParentFam.isMarried() && !LunaticFamily.allowSingleAdopt) {
                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("admin_adopt_set_no_single_adopt").replace("%player%", firstParentFam.getName()));
                    } else if (childFam.isAdopted()) {
                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("admin_adopt_set_already_adopted").replace("%child%", childFam.getName()));
                    } else if (firstParentFam.getChildrenAmount() > 1) {
                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("admin_adopt_limit").replace("%player%", firstParentFam.getName()));
                    } else if (childFam.hasSibling()) {
                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("admin_adopt_set_has_sibling").replace("%player%", childFam.getName()));
                    } else if (childFam.hasSibling() && firstParentFam.getChildrenAmount() > 0) {
                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("admin_adopt_set_has_sibling_limit").replace("%player1%", childFam.getName()).replace("%player2%", firstParentFam.getName()));
                    } else {

                        if (!firstParentFam.isMarried()) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("admin_adopt_set_by_single").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                        } else {
                            FamilyPlayer secondParentFam = firstParentFam.getPartner();
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("admin_adopt_set").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                        }

                        LunaticFamily.adoptRequests.remove(childUUID);
                        firstParentFam.adopt(childFam.getID());
                    }

                }
            } else if (checkIsSubcommand("unset", subcommand)) {

                if (!sender.hasPermission("lunaticFamily.admin.adopt.unset")) {
                    sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("no_permission"));
                } else if (args.length < 2) {
                    sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("wrong_usage"));
                } else if (!LunaticFamily.playerExists(args[1])) {
                    sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("player_not_exist").replace("%player%", LunaticFamily.getName(args[1])));
                } else {

                    String childUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyPlayer childFam = new FamilyPlayer(childUUID);

                    if (!childFam.isAdopted()) {
                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("admin_adopt_unset_not_adopted").replace("%player%", childFam.getName()));
                    } else {
                        FamilyPlayer firstParentFam = childFam.getParents().get(0);

                        if (firstParentFam.isMarried()) {
                            FamilyPlayer secondParentFam = firstParentFam.getPartner();
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("admin_adopt_unset").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                        } else {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("admin_adopt_unset_by_single").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                        }
                        firstParentFam.unadopt(childFam.getID());
                    }
                }

            } else if (!(sender instanceof Player)) {
                sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("no_console_command"));
            } else {
                Player player = (Player) sender;
                if (!player.hasPermission("lunaticFamily.adopt")) {
                    sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("no_permission"));
                } else {
                    String playerUUID = player.getUniqueId().toString();
                    FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

                    if (args[0].equalsIgnoreCase("propose") || LunaticFamily.getAliases("adopt", "propose").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

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
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("wrong_usage"));
                        } else if (cancel) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_propose_cancel").replace("%player%", args[2]));
                        } else if (!playerFam.isMarried() && !LunaticFamily.allowSingleAdopt) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_propose_no_single_adopt"));
                        } else if (playerFam.getChildrenAmount() > 1) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_propose_limit"));
                        } else if (!LunaticFamily.playerExists(args[1])) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("player_not_exist").replace("%player%", args[1]));
                        } else if (Bukkit.getPlayer(args[1]) == null) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("player_offline").replace("%player%", args[1]));
                        } else if (!playerFam.hasEnoughMoney("adopt_parent")) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("not_enough_money"));
                        } else {
                            String child = LunaticFamily.getUUID(args[1]);
                            FamilyPlayer childFam = new FamilyPlayer(child);

                            if (args[1].equalsIgnoreCase(player.getName())) {
                                player.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_propose_self_request"));
                            } else if (playerFam.isFamilyMember(childFam.getID())) {
                                sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("marry_propose_family_request").replace("%player%", childFam.getName()));
                            } else if (LunaticFamily.adoptRequests.containsKey(child)) {
                                sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_propose_open_request").replace("%player%", childFam.getName()));
                            } else if (childFam.getParents() == null) {
                                sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_propose_already_adopted").replace("%player%", childFam.getName()));
                            } else if (childFam.hasSibling() && !confirm) {
                                sender.sendMessage(LunaticFamily.createClickableMessage(
                                        LunaticFamily.getMessage("adopt_propose_has_sibling").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()),
                                        LunaticFamily.getMessage("confirm"),
                                        "/adopt propose " + LunaticFamily.getName(args[1]) + " confirm",
                                        LunaticFamily.getMessage("cancel"),
                                        "/adopt propose " + LunaticFamily.getName(args[1]) + " cancel"));

                            } else if (childFam.hasSibling() && playerFam.getChildrenAmount() > 0) {
                                sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_propose_has_sibling_limit").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()));
                            } else {
                                if (playerFam.isMarried()) {
                                    childFam.sendMessage(LunaticFamily.createClickableMessage(
                                            LunaticFamily.getMessage("adopt_propose_request").replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getPartner().getName()),
                                            LunaticFamily.getMessage("accept"),
                                            "/adopt accept",
                                            LunaticFamily.getMessage("deny"),
                                            "/adopt deny"));
                                } else {
                                    childFam.sendMessage(LunaticFamily.createClickableMessage(
                                            LunaticFamily.getMessage("adopt_propose_request_by_single").replace("%player%", playerFam.getName()),
                                            LunaticFamily.getMessage("accept"),
                                            "/adopt accept",
                                            LunaticFamily.getMessage("deny"),
                                            "/adopt deny"));
                                }
                                LunaticFamily.adoptRequests.put(child, playerUUID);
                                sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_propose_request_sent").replace("%player%", childFam.getName()));

                                new BukkitRunnable() {
                                    public void run() {
                                        if (LunaticFamily.adoptRequests.containsKey(child)) {
                                            LunaticFamily.adoptRequests.remove(child);
                                            if (playerFam.isMarried()) {
                                                FamilyPlayer partnerFam = playerFam.getPartner();
                                                childFam.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_propose_request_expired").replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()));
                                            } else {
                                                childFam.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_propose_request_by_single_expired").replace("%player%", playerFam.getName()));
                                            }
                                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_request_sent_expired").replace("%player%", childFam.getName()));
                                        }
                                    }
                                }.runTaskLater(plugin, 600L);
                            }
                        }

                    } else if (checkIsSubcommand("accept", subcommand)) {

                        //check for request
                        if (!LunaticFamily.adoptRequests.containsKey(playerUUID)) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_accept_no_request"));
                        } else {

                            String parent = LunaticFamily.adoptRequests.get(playerUUID);
                            FamilyPlayer parentFam = new FamilyPlayer(parent);

                            if (parentFam.getChildrenAmount() > 1) {
                                sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_accept_parent_limit").replace("%player%", parentFam.getName()));
                            } else if (!playerFam.hasEnoughMoney("adopt_child")) {
                                sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("not_enough_money"));
                            } else if (!parentFam.hasEnoughMoney("adopt_parent")) {
                                sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("player_not_enough_money").replace("%player%", parentFam.getName()));
                            } else {

                                if (parentFam.isMarried()) {
                                    sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_accept_got_adopted").replace("%player1%", parentFam.getName()).replace("%player2%", parentFam.getPartner().getName()));
                                    parentFam.getPartner().sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_accept_adopted").replace("%player%", playerFam.getName()));
                                    parentFam.getPartner().withdrawPlayer("adopt_parent", 0.5);
                                    parentFam.withdrawPlayer("adopt_parent", 0.5);
                                } else {
                                    sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_accept_adopted_by_single").replace("%player%", parentFam.getName()));
                                }
                                playerFam.withdrawPlayer("adopt_child");

                                parentFam.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_accept_adopted").replace("%player%", playerFam.getName()));
                                LunaticFamily.adoptRequests.remove(playerUUID);
                                parentFam.adopt(playerFam.getID());
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("list") || LunaticFamily.getAliases("adopt", "list").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                        if (playerFam.getChildren().get(0) != null || playerFam.getChildren().get(1) != null) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_list_no_child"));
                        } else {
                            String msg = LunaticFamily.prefix + LunaticFamily.getMessage("adopt_list") + "\n";
                            if (playerFam.getChildren().get(0) != null) {
                                msg = msg + playerFam.getChildren().get(0).getName() + "\n";
                            }
                            if (playerFam.getChildren().get(1) != null) {
                                msg = msg + playerFam.getChildren().get(1).getName() + "\n";
                            }
                            sender.sendMessage(msg);
                        }
                    } else if (args[0].equalsIgnoreCase("deny") || LunaticFamily.getAliases("adopt", "deny").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                        if (!LunaticFamily.adoptRequests.containsKey(playerUUID)) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_deny_no_request"));
                        } else {
                            String parent = LunaticFamily.adoptRequests.get(playerUUID);
                            FamilyPlayer parentFam = new FamilyPlayer(parent);
                            parentFam.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_deny").replace("%player%", playerFam.getName()));
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_denied").replace("%player%", parentFam.getName()));
                            LunaticFamily.adoptRequests.remove(playerUUID);
                        }
                    } else if (args[0].equalsIgnoreCase("moveout") || LunaticFamily.getAliases("adopt", "moveout").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

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
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_moveout_no_parents"));
                        } else if (!confirm) {
                            sender.sendMessage(LunaticFamily.createClickableMessage(
                                    LunaticFamily.getMessage("adopt_moveout_confirm"),
                                    LunaticFamily.getMessage("confirm"),
                                    "/adopt moveout confirm",
                                    LunaticFamily.getMessage("cancel"),
                                    "/adopt moveout cancel"));
                        } else if (cancel) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_moveout_cancel"));
                        } else if (!force && !playerFam.hasEnoughMoney("adopt_moveout_child")) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("not_enough_money"));
                        } else if (!force && playerFam.getParents().size() == 2 && !playerFam.getParents().get(0).hasEnoughMoney("adopt_moveout_parent", 0.5)) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(1).getName()));
                            sender.sendMessage(LunaticFamily.createClickableMessage(
                                    LunaticFamily.getMessage("take_payment_confirm"),
                                    LunaticFamily.getMessage("confirm"),
                                    "/adopt moveout confirm force",
                                    LunaticFamily.getMessage("cancel"),
                                    "/adopt moveout cancel"));
                        } else if (!force && playerFam.getParents().size() == 1 && !playerFam.getParents().get(0).hasEnoughMoney("adopt_moveout_parent")) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(0).getName()));
                            sender.sendMessage(LunaticFamily.createClickableMessage(
                                    LunaticFamily.getMessage("take_payment_confirm"),
                                    LunaticFamily.getMessage("confirm"),
                                    "/adopt moveout confirm force",
                                    LunaticFamily.getMessage("cancel"),
                                    "/adopt moveout cancel"));
                        } else if (!force && playerFam.getParents().size() == 2 && !playerFam.getParents().get(1).hasEnoughMoney("adopt_moveout_parent", 0.5)) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(1).getName()));
                            sender.sendMessage(LunaticFamily.createClickableMessage(
                                    LunaticFamily.getMessage("take_payment_confirm"),
                                    LunaticFamily.getMessage("confirm"),
                                    "/adopt moveout confirm force",
                                    LunaticFamily.getMessage("cancel"),
                                    "/adopt moveout cancel"));
                        } else if (force && !playerFam.hasEnoughMoney("adopt_moveout_parent", "adopt_moveout_child")) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("not_enough_money"));
                        } else {
                            FamilyPlayer firstParentFam = playerFam.getParents().get(0);

                            if (playerFam.hasSibling()) {
                                FamilyPlayer siblingFam = playerFam.getSibling();
                                siblingFam.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_moveout_sibling"));
                            }

                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_moveout"));


                            firstParentFam.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_moveout_child").replace("%player%", playerFam.getName()));
                            if (firstParentFam.isMarried()) {
                                FamilyPlayer secondParentFam = firstParentFam.getPartner();
                                secondParentFam.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_moveout_child").replace("%player%", playerFam.getName()));
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
                                } else {
                                    firstParentFam.withdrawPlayer("adopt_moveout_parent");

                                }
                                playerFam.withdrawPlayer("adopt_moveout_child");
                            }

                            firstParentFam.unadopt(playerFam.getID());

                        }
                    } else if (checkIsSubcommand("kickout", subcommand)) {
                        if (playerFam.getChildren().get(0) != null || playerFam.getChildren().get(1) != null) {
                            if (args.length == 1) {
                                sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_kickout_specify_child"));
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
                                        sender.sendMessage(LunaticFamily.createClickableMessage(
                                                LunaticFamily.getMessage("adopt_kickout_confirm").replace("%player%", LunaticFamily.getName(args[1])),
                                                LunaticFamily.getMessage("confirm"),
                                                "/adopt kickout " + args[1] + " confirm",
                                                LunaticFamily.getMessage("cancel"),
                                                "/adopt kickout " + args[1] + " cancel"));
                                    } else if (cancel) {
                                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_kickout_cancel"));
                                    } else if (!force && playerFam.isMarried() && !playerFam.hasEnoughMoney("adopt_kickout_parent", 0.5)) {
                                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("not_enough_money"));
                                    } else if (!force && !playerFam.isMarried() && !playerFam.hasEnoughMoney("adopt_kickout_parent")) {
                                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("not_enough_money"));
                                    } else if (!childFam.hasEnoughMoney("adopt_kickout_child")) {
                                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("player_not_enough_money").replace("%player%", childFam.getName()));
                                        sender.sendMessage(LunaticFamily.createClickableMessage(
                                                LunaticFamily.getMessage("take_payment_confirm"),
                                                LunaticFamily.getMessage("confirm"),
                                                "/adopt kickout confirm force",
                                                LunaticFamily.getMessage("cancel"),
                                                "/adopt kickout confirm force"));
                                    } else if (!force && playerFam.isMarried() && !playerFam.getPartner().hasEnoughMoney("adopt_kickout_parent")) {
                                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("player_not_enough_money").replace("%player%", playerFam.getPartner().getName()));
                                        sender.sendMessage(LunaticFamily.createClickableMessage(
                                                LunaticFamily.getMessage("take_payment_confirm"),
                                                LunaticFamily.getMessage("confirm"),
                                                "/adopt kickout confirm force",
                                                LunaticFamily.getMessage("cancel"),
                                                "/adopt kickout confirm force"));
                                    } else {
                                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_kickout").replace("%player%", childFam.getName()));
                                        if (playerFam.isMarried()) {
                                            playerFam.getPartner().sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_kickout_partner").replace("%player1%", playerFam.getName()).replace("%player2%", childFam.getName()));
                                        }

                                        if (childFam.hasSibling()) {
                                            FamilyPlayer siblingFam = childFam.getSibling();
                                            siblingFam.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_kickout_sibling").replace("%player%", playerFam.getName()));
                                        }
                                        childFam.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_kickout_child").replace("%player%", playerFam.getName()));

                                        if (force) {
                                            playerFam.withdrawPlayer("adopt_kickout_parent", "adopt_kickout_child");
                                        } else {
                                            if (playerFam.isMarried()) {
                                                playerFam.getPartner().withdrawPlayer("adopt_kickout_parent", 0.5);
                                                playerFam.withdrawPlayer("adopt_kickout_parent", 0.5);
                                            } else {
                                                playerFam.withdrawPlayer("adopt_kickout_parent");
                                            }
                                            childFam.withdrawPlayer("adopt_kickout_child");
                                        }

                                        playerFam.unadopt(childFam.getID());
                                    }

                                } else {
                                    sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_kickout_not_your_child").replace("%player%", childFam.getName()));
                                }
                            }
                        } else {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("adopt_kickout_no_child"));
                        }
                    } else if (checkIsSubcommand("help", subcommand)) {
                        String[] subcommandsHelp = {"propose", "kickout", "moveout"};

                        StringBuilder msg = new StringBuilder(LunaticFamily.prefix + " " + LunaticFamily.getMessage(label + "_help") + "\n");

                        for (final String sc : subcommandsHelp) {
                            msg.append(LunaticFamily.prefix).append(" ").append(LunaticFamily.getMessage(label + "_" + sc + "_help")).append("\n");
                        }
                        sender.sendMessage(msg.toString());
                    } else {
                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("wrong_usage"));
                    }
                }
            }


        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {

        List<String> adoptSubcommands = LunaticFamily.adoptSubcommands;
        List<String> adoptAdminSubcommands = LunaticFamily.adoptAdminSubcommands;
        List<String> list = new ArrayList<>();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("adopt")) {
                if (args.length == 0) {
                    if (player.hasPermission("lunaticFamily.admin.adopt")) {
                        list.addAll(adoptAdminSubcommands);
                    }
                    if (player.hasPermission("lunaticFamily.adopt")) {
                        list.addAll(adoptSubcommands);
                    }
                    Collections.sort(list);
                    return list;
                } else if (args.length == 1) {
                    if (player.hasPermission("lunaticFamily.admin.adopt")) {
                        for (String s : adoptAdminSubcommands) {
                            if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                list.add(s);
                            }
                        }
                    }
                    if (player.hasPermission("lunaticFamily.adopt")) {
                        for (String s : adoptSubcommands) {
                            if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                list.add(s);
                            }
                        }
                    }
                    Collections.sort(list);
                    return list;
                }
            }
        }
        // return null at the end.
        return null;
    }

    private boolean checkIsSubcommand(final String subcommand, final String arg) {
        return subcommand.equalsIgnoreCase(arg) || LunaticFamily.getAliases("adopt", subcommand).stream().anyMatch(element -> arg.equalsIgnoreCase(element));
    }
}
