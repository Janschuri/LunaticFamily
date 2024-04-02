package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
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
                sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
            } else {
                final String[] subcommandsHelp = {"propose", "kickout", "moveout"};

                StringBuilder msg = new StringBuilder(Language.prefix + " " + Language.getMessage(label + "_help") + "\n");

                for (String subcommand : subcommandsHelp) {
                    msg.append(Language.prefix).append(" ").append(Language.getMessage(label + "_" + subcommand + "_help")).append("\n");
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
                    sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                } else if (args.length < 3) {
                    sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                } else if (!Utils.playerExists(args[1]) && !force) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                } else if (!Utils.playerExists(args[2]) && !force) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[2]));
                } else if (args[1].equalsIgnoreCase(args[2])) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_set_same_player"));
                } else {

                    String firstParentUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyPlayer firstParentFam = new FamilyPlayer(firstParentUUID);
                    String childUUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                    FamilyPlayer childFam = new FamilyPlayer(childUUID);

                    if (!firstParentFam.isMarried() && !Config.allowSingleAdopt) {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_set_no_single_adopt").replace("%player%", firstParentFam.getName()));
                    } else if (childFam.isAdopted()) {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_set_already_adopted").replace("%child%", childFam.getName()));
                    } else if (firstParentFam.getChildrenAmount() > 1) {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_limit").replace("%player%", firstParentFam.getName()));
                    } else if (childFam.hasSibling()) {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_set_has_sibling").replace("%player%", childFam.getName()));
                    } else if (childFam.hasSibling() && firstParentFam.getChildrenAmount() > 0) {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_set_has_sibling_limit").replace("%player1%", childFam.getName()).replace("%player2%", firstParentFam.getName()));
                    } else {

                        if (!firstParentFam.isMarried()) {
                            sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_set_by_single").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                        } else {
                            FamilyPlayer secondParentFam = firstParentFam.getPartner();
                            sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_set").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                        }

                        LunaticFamily.adoptRequests.remove(childUUID);
                        firstParentFam.adopt(childFam.getID());
                    }

                }
            } else if (checkIsSubcommand("unset", subcommand)) {

                if (!sender.hasPermission("lunaticFamily.admin.adopt.unset")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                } else if (args.length < 2) {
                    sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                } else if (!Utils.playerExists(args[1])) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", Utils.getName(args[1])));
                } else {

                    String childUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyPlayer childFam = new FamilyPlayer(childUUID);

                    if (!childFam.isAdopted()) {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_unset_not_adopted").replace("%player%", childFam.getName()));
                    } else {
                        FamilyPlayer firstParentFam = childFam.getParents().get(0);

                        if (firstParentFam.isMarried()) {
                            FamilyPlayer secondParentFam = firstParentFam.getPartner();
                            sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_unset").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                        } else {
                            sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_unset_by_single").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                        }
                        firstParentFam.unadopt(childFam.getID());
                    }
                }

            } else if (!(sender instanceof Player)) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
            } else {
                Player player = (Player) sender;
                if (!player.hasPermission("lunaticFamily.adopt")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                } else {
                    String playerUUID = player.getUniqueId().toString();
                    FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

                    if (args[0].equalsIgnoreCase("propose") || Language.getAliases("adopt", "propose").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

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
                            sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                        } else if (cancel) {
                            sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_cancel").replace("%player%", args[2]));
                        } else if (!playerFam.isMarried() && !Config.allowSingleAdopt) {
                            sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_no_single_adopt"));
                        } else if (playerFam.getChildrenAmount() > 1) {
                            sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_limit"));
                        } else if (!Utils.playerExists(args[1])) {
                            sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                        } else if (Bukkit.getPlayer(args[1]) == null) {
                            sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", args[1]));
                        } else if (!playerFam.hasEnoughMoney("adopt_parent")) {
                            sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                        } else {
                            String childUUID = Utils.getUUID(args[1]);
                            FamilyPlayer childFam = new FamilyPlayer(childUUID);

                            if (args[1].equalsIgnoreCase(player.getName())) {
                                player.sendMessage(Language.prefix + Language.getMessage("adopt_propose_self_request"));
                            } else if (playerFam.isFamilyMember(childFam.getID())) {
                                sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_family_request").replace("%player%", childFam.getName()));
                            } else if (LunaticFamily.adoptRequests.containsKey(childUUID)) {
                                sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_open_request").replace("%player%", childFam.getName()));
                            } else if (childFam.getParents() == null) {
                                sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_already_adopted").replace("%player%", childFam.getName()));
                            } else if (childFam.hasSibling() && !confirm) {
                                sender.sendMessage(Utils.createClickableMessage(
                                        Language.getMessage("adopt_propose_has_sibling").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()),
                                        Language.getMessage("confirm"),
                                        "/adopt propose " + Utils.getName(args[1]) + " confirm",
                                        Language.getMessage("cancel"),
                                        "/adopt propose " + Utils.getName(args[1]) + " cancel"));

                            } else if (childFam.hasSibling() && playerFam.getChildrenAmount() > 0) {
                                sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_has_sibling_limit").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()));
                            } else {
                                if (playerFam.isMarried()) {
                                    childFam.sendMessage(Utils.createClickableMessage(
                                            Language.getMessage("adopt_propose_request").replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getPartner().getName()),
                                            Language.getMessage("accept"),
                                            "/adopt accept",
                                            Language.getMessage("deny"),
                                            "/adopt deny"));
                                } else {
                                    childFam.sendMessage(Utils.createClickableMessage(
                                            Language.getMessage("adopt_propose_request_by_single").replace("%player%", playerFam.getName()),
                                            Language.getMessage("accept"),
                                            "/adopt accept",
                                            Language.getMessage("deny"),
                                            "/adopt deny"));
                                }
                                LunaticFamily.adoptRequests.put(childUUID, playerUUID);
                                sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_request_sent").replace("%player%", childFam.getName()));

                                new BukkitRunnable() {
                                    public void run() {
                                        if (LunaticFamily.adoptRequests.containsKey(childUUID)) {
                                            LunaticFamily.adoptRequests.remove(childUUID);
                                            if (playerFam.isMarried()) {
                                                FamilyPlayer partnerFam = playerFam.getPartner();
                                                childFam.sendMessage(Language.prefix + Language.getMessage("adopt_propose_request_expired").replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()));
                                            } else {
                                                childFam.sendMessage(Language.prefix + Language.getMessage("adopt_propose_request_by_single_expired").replace("%player%", playerFam.getName()));
                                            }
                                            sender.sendMessage(Language.prefix + Language.getMessage("adopt_request_sent_expired").replace("%player%", childFam.getName()));
                                        }
                                    }
                                }.runTaskLater(plugin, 600L);
                            }
                        }

                    } else if (checkIsSubcommand("accept", subcommand)) {

                        //check for request
                        if (!LunaticFamily.adoptRequests.containsKey(playerUUID)) {
                            sender.sendMessage(Language.prefix + Language.getMessage("adopt_accept_no_request"));
                        } else {

                            String parent = LunaticFamily.adoptRequests.get(playerUUID);
                            FamilyPlayer parentFam = new FamilyPlayer(parent);

                            if (parentFam.getChildrenAmount() > 1) {
                                sender.sendMessage(Language.prefix + Language.getMessage("adopt_accept_parent_limit").replace("%player%", parentFam.getName()));
                            } else if (!playerFam.hasEnoughMoney("adopt_child")) {
                                sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                            } else if (!parentFam.hasEnoughMoney("adopt_parent")) {
                                sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", parentFam.getName()));
                            } else {

                                if (parentFam.isMarried()) {
                                    sender.sendMessage(Language.prefix + Language.getMessage("adopt_accept_got_adopted").replace("%player1%", parentFam.getName()).replace("%player2%", parentFam.getPartner().getName()));
                                    parentFam.getPartner().sendMessage(Language.prefix + Language.getMessage("adopt_accept_adopted").replace("%player%", playerFam.getName()));
                                    parentFam.getPartner().withdrawPlayer("adopt_parent", 0.5);
                                    parentFam.withdrawPlayer("adopt_parent", 0.5);
                                } else {
                                    sender.sendMessage(Language.prefix + Language.getMessage("adopt_accept_adopted_by_single").replace("%player%", parentFam.getName()));
                                }
                                playerFam.withdrawPlayer("adopt_child");

                                parentFam.sendMessage(Language.prefix + Language.getMessage("adopt_accept_adopted").replace("%player%", playerFam.getName()));
                                LunaticFamily.adoptRequests.remove(playerUUID);
                                parentFam.adopt(playerFam.getID());
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("list") || Language.getAliases("adopt", "list").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                        if (playerFam.getChildren().get(0) != null || playerFam.getChildren().get(1) != null) {
                            sender.sendMessage(Language.prefix + Language.getMessage("adopt_list_no_child"));
                        } else {
                            String msg = Language.prefix + Language.getMessage("adopt_list") + "\n";
                            if (playerFam.getChildren().get(0) != null) {
                                msg = msg + playerFam.getChildren().get(0).getName() + "\n";
                            }
                            if (playerFam.getChildren().get(1) != null) {
                                msg = msg + playerFam.getChildren().get(1).getName() + "\n";
                            }
                            sender.sendMessage(msg);
                        }
                    } else if (args[0].equalsIgnoreCase("deny") || Language.getAliases("adopt", "deny").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                        if (!LunaticFamily.adoptRequests.containsKey(playerUUID)) {
                            sender.sendMessage(Language.prefix + Language.getMessage("adopt_deny_no_request"));
                        } else {
                            String parent = LunaticFamily.adoptRequests.get(playerUUID);
                            FamilyPlayer parentFam = new FamilyPlayer(parent);
                            parentFam.sendMessage(Language.prefix + Language.getMessage("adopt_deny").replace("%player%", playerFam.getName()));
                            sender.sendMessage(Language.prefix + Language.getMessage("adopt_denied").replace("%player%", parentFam.getName()));
                            LunaticFamily.adoptRequests.remove(playerUUID);
                        }
                    } else if (args[0].equalsIgnoreCase("moveout") || Language.getAliases("adopt", "moveout").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

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
                                    "/adopt moveout confirm",
                                    Language.getMessage("cancel"),
                                    "/adopt moveout cancel"));
                        } else if (cancel) {
                            sender.sendMessage(Language.prefix + Language.getMessage("adopt_moveout_cancel"));
                        } else if (!force && !playerFam.hasEnoughMoney("adopt_moveout_child")) {
                            sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                        } else if (!force && playerFam.getParents().size() == 2 && !playerFam.getParents().get(0).hasEnoughMoney("adopt_moveout_parent", 0.5)) {
                            sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(1).getName()));
                            sender.sendMessage(Utils.createClickableMessage(
                                    Language.getMessage("take_payment_confirm"),
                                    Language.getMessage("confirm"),
                                    "/adopt moveout confirm force",
                                    Language.getMessage("cancel"),
                                    "/adopt moveout cancel"));
                        } else if (!force && playerFam.getParents().size() == 1 && !playerFam.getParents().get(0).hasEnoughMoney("adopt_moveout_parent")) {
                            sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(0).getName()));
                            sender.sendMessage(Utils.createClickableMessage(
                                    Language.getMessage("take_payment_confirm"),
                                    Language.getMessage("confirm"),
                                    "/adopt moveout confirm force",
                                    Language.getMessage("cancel"),
                                    "/adopt moveout cancel"));
                        } else if (!force && playerFam.getParents().size() == 2 && !playerFam.getParents().get(1).hasEnoughMoney("adopt_moveout_parent", 0.5)) {
                            sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(1).getName()));
                            sender.sendMessage(Utils.createClickableMessage(
                                    Language.getMessage("take_payment_confirm"),
                                    Language.getMessage("confirm"),
                                    "/adopt moveout confirm force",
                                    Language.getMessage("cancel"),
                                    "/adopt moveout cancel"));
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
                                                "/adopt kickout " + args[1] + " confirm",
                                                Language.getMessage("cancel"),
                                                "/adopt kickout " + args[1] + " cancel"));
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
                                                "/adopt kickout confirm force",
                                                Language.getMessage("cancel"),
                                                "/adopt kickout confirm force"));
                                    } else if (!force && playerFam.isMarried() && !playerFam.getPartner().hasEnoughMoney("adopt_kickout_parent")) {
                                        sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", playerFam.getPartner().getName()));
                                        sender.sendMessage(Utils.createClickableMessage(
                                                Language.getMessage("take_payment_confirm"),
                                                Language.getMessage("confirm"),
                                                "/adopt kickout confirm force",
                                                Language.getMessage("cancel"),
                                                "/adopt kickout confirm force"));
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
                                            } else {
                                                playerFam.withdrawPlayer("adopt_kickout_parent");
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
                    } else if (checkIsSubcommand("help", subcommand)) {
                        String[] subcommandsHelp = {"propose", "kickout", "moveout"};

                        StringBuilder msg = new StringBuilder(Language.prefix + " " + Language.getMessage(label + "_help") + "\n");

                        for (final String sc : subcommandsHelp) {
                            msg.append(Language.prefix).append(" ").append(Language.getMessage(label + "_" + sc + "_help")).append("\n");
                        }
                        sender.sendMessage(msg.toString());
                    } else {
                        sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                    }
                }
            }


        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {

        List<String> adoptSubcommands = Language.getAliases("adopt", "propose", "accept", "deny", "kickout", "moveout", "list");
        List<String> adoptAdminSubcommands = Language.getAliases("adopt", "set", "unset");
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
        return subcommand.equalsIgnoreCase(arg) || Language.getAliases("adopt", subcommand).stream().anyMatch(element -> arg.equalsIgnoreCase(element));
    }
}
