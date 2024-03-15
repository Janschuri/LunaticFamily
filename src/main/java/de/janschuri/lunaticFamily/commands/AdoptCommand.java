package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.Main;
import de.janschuri.lunaticFamily.utils.FamilyManager;
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

    private final Main plugin;

    public AdoptCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 0) {

        } else {

                if (args[0].equalsIgnoreCase("set") || Main.getAliases("adopt", "set").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

                    boolean force = false;

                    if (args.length > 3) {
                        if (args[3].equalsIgnoreCase("force")) {
                            force = true;
                        }
                    }

                    if (!sender.hasPermission("lunaticFamily.admin.adopt")) {
                        sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                    } else if (args.length < 3) {
                        sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                    } else if (!Main.playerExists(args[1]) && !force) {
                        sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", args[1]));
                    } else if (!Main.playerExists(args[2]) && !force) {
                        sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", args[2]));
                    } else if (args[1].equalsIgnoreCase(args[2])) {
                        sender.sendMessage(Main.prefix + Main.getMessage("admin_adopt_set_same_player"));
                    } else {

                        String firstParentUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        FamilyManager firstParentFam = new FamilyManager(firstParentUUID);
                        String childUUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                        FamilyManager childFam = new FamilyManager(childUUID);

                        if (!firstParentFam.isMarried() && !Main.allowSingleAdopt) {
                            sender.sendMessage(Main.prefix + Main.getMessage("admin_adopt_set_no_single_adopt").replace("%player%", firstParentFam.getName()));
                        } else if (childFam.isAdopted()) {
                            sender.sendMessage(Main.prefix + Main.getMessage("admin_adopt_set_already_adopted").replace("%child%", childFam.getName()));
                        } else if (firstParentFam.getChildrenAmount() > 1) {
                            sender.sendMessage(Main.prefix + Main.getMessage("admin_adopt_limit").replace("%player%", firstParentFam.getName()));
                        } else if (childFam.hasSibling()) {
                            sender.sendMessage(Main.prefix + Main.getMessage("admin_adopt_set_has_sibling").replace("%player%", childFam.getName()));
                        } else if (childFam.hasSibling() && firstParentFam.getChildrenAmount() > 0) {
                            sender.sendMessage(Main.prefix + Main.getMessage("admin_adopt_set_has_sibling_limit").replace("%player1%", childFam.getName()).replace("%player2%", firstParentFam.getName()));
                        } else {

                            if (!firstParentFam.isMarried()) {
                                sender.sendMessage(Main.prefix + Main.getMessage("admin_adopt_set_by_single").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                            } else {
                                FamilyManager secondParentFam = firstParentFam.getPartner();
                                sender.sendMessage(Main.prefix + Main.getMessage("admin_adopt_set").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                            }

                            Main.adoptRequests.remove(childUUID);
                            firstParentFam.adopt(childFam.getID());
                        }

                    }
                } else if (args[0].equalsIgnoreCase("unset") || Main.getAliases("adopt", "unset").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

                    if (!sender.hasPermission("lunaticFamily.admin.adopt")) {
                        sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                    } else if (args.length < 2) {
                        sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                    } else if (!Main.playerExists(args[1])) {
                        sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", Main.getName(args[1])));
                    } else {

                        String childUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        FamilyManager childFam = new FamilyManager(childUUID);

                        if (!childFam.isAdopted()) {
                            sender.sendMessage(Main.prefix + Main.getMessage("admin_adopt_unset_not_adopted").replace("%player%", childFam.getName()));
                        } else {
                            FamilyManager firstParentFam = childFam.getParents().get(0);

                            if (firstParentFam.isMarried()) {
                                FamilyManager secondParentFam = firstParentFam.getPartner();
                                sender.sendMessage(Main.prefix + Main.getMessage("admin_adopt_unset").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                            } else {
                                sender.sendMessage(Main.prefix + Main.getMessage("admin_adopt_unset_by_single").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                            }
                            firstParentFam.unadopt(childFam.getID());
                        }
                    }

                } else if (!(sender instanceof Player)) {
                    sender.sendMessage(Main.prefix + Main.getMessage("no_console_command"));
                } else {
                    Player player = (Player) sender;
                    if (!player.hasPermission("lunaticFamily.adopt")) {
                        sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                    } else {
                        String playerUUID = player.getUniqueId().toString();
                        FamilyManager playerFam = new FamilyManager(playerUUID);

                        if (args[0].equalsIgnoreCase("propose") || Main.getAliases("adopt", "propose").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

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
                                sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                            } else if (cancel) {
                                sender.sendMessage(Main.prefix + Main.getMessage("adopt_propose_cancel").replace("%player%", args[2]));
                            } else if (!playerFam.isMarried() && !Main.allowSingleAdopt) {
                                sender.sendMessage(Main.prefix + Main.getMessage("adopt_propose_no_single_adopt"));
                            } else if (playerFam.getChildrenAmount() > 1){
                                sender.sendMessage(Main.prefix + Main.getMessage("adopt_propose_limit"));
                            } else if (!Main.playerExists(args[1])) {
                                sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", args[1]));
                            } else if (Bukkit.getPlayer(args[1]) == null) {
                                sender.sendMessage(Main.prefix + Main.getMessage("player_offline").replace("%player%", args[1]));
                            } else if (!playerFam.hasEnoughMoney("adopt_parent")) {
                                sender.sendMessage(Main.prefix + Main.getMessage("not_enough_money"));
                            } else {
                                String child = Main.getUUID(args[1]);
                                FamilyManager childFam = new FamilyManager(child);

                                if (args[1].equalsIgnoreCase(player.getName())) {
                                    player.sendMessage(Main.prefix + Main.getMessage("adopt_propose_self_request"));
                                } else if (playerFam.isFamilyMember(childFam.getID())) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("marry_propose_family_request").replace("%player%", childFam.getName()));
                                } else if (Main.adoptRequests.containsKey(child)) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("adopt_propose_open_request").replace("%player%", childFam.getName()));
                                } else if (childFam.getParents().get(0) != null) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("adopt_propose_already_adopted").replace("%player%", childFam.getName()));
                                } else if (childFam.hasSibling() && !confirm){
                                    sender.sendMessage(Main.createClickableMessage(
                                            Main.getMessage("adopt_propose_has_sibling").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()),
                                            Main.getMessage("confirm"),
                                            "/adopt propose " + Main.getName(args[1]) + " confirm",
                                            Main.getMessage("cancel"),
                                            "/adopt propose " + Main.getName(args[1]) + " cancel"));

                                } else if (childFam.hasSibling() && playerFam.getChildrenAmount() > 0){
                                    sender.sendMessage(Main.prefix + Main.getMessage("adopt_propose_has_sibling_limit").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()));
                                } else {
                                    if (playerFam.isMarried()) {
                                        childFam.sendMessage(Main.createClickableMessage(
                                                Main.getMessage("adopt_propose_request").replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getPartner().getName()),
                                                Main.getMessage("accept"),
                                                "/adopt accept",
                                                Main.getMessage("deny"),
                                                "/adopt deny"));
                                    } else {
                                        childFam.sendMessage(Main.createClickableMessage(
                                                Main.getMessage("adopt_propose_request_by_single").replace("%player%", playerFam.getName()),
                                                Main.getMessage("accept"),
                                                "/adopt accept",
                                                Main.getMessage("deny"),
                                                "/adopt deny"));
                                    }
                                    Main.adoptRequests.put(child, playerUUID);
                                    sender.sendMessage(Main.prefix + Main.getMessage("adopt_propose_request_sent").replace("%player%", childFam.getName()));

                                    new BukkitRunnable() {
                                        public void run() {
                                            if (Main.adoptRequests.containsKey(child)) {
                                                Main.adoptRequests.remove(child);
                                                if (playerFam.isMarried()) {
                                                    FamilyManager partnerFam = playerFam.getPartner();
                                                    childFam.sendMessage(Main.prefix + Main.getMessage("adopt_propose_request_expired").replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()));
                                                } else {
                                                    childFam.sendMessage(Main.prefix + Main.getMessage("adopt_propose_request_by_single_expired").replace("%player%", playerFam.getName()));
                                                }
                                                sender.sendMessage(Main.prefix + Main.getMessage("adopt_request_sent_expired").replace("%player%", childFam.getName()));
                                            }
                                        }
                                    }.runTaskLater(plugin, 600L);
                                }
                            }

                        } else if (args[0].equalsIgnoreCase("accept") || Main.getAliases("adopt", "accept").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

                            //check for request
                            if (!Main.adoptRequests.containsKey(playerUUID)) {
                                sender.sendMessage(Main.prefix + Main.getMessage("adopt_accept_no_request"));
                            } else {

                                String parent = Main.adoptRequests.get(playerUUID);
                                FamilyManager parentFam = new FamilyManager(parent);

                                if (parentFam.getChildrenAmount() > 1) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("adopt_accept_parent_limit").replace("%player%", parentFam.getName()));
                                } else if (!playerFam.hasEnoughMoney("adopt_child")) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("not_enough_money"));
                                } else if (!parentFam.hasEnoughMoney("adopt_parent")) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("player_not_enough_money").replace("%player%", parentFam.getName()));
                                } else {

                                    if (parentFam.isMarried()) {
                                        sender.sendMessage(Main.prefix + Main.getMessage("adopt_accept_got_adopted").replace("%player1%", parentFam.getName()).replace("%player2%", parentFam.getPartner().getName()));
                                        parentFam.getPartner().sendMessage(Main.prefix + Main.getMessage("adopt_accept_adopted").replace("%player%", playerFam.getName()));
                                        parentFam.getPartner().withdrawPlayer("adopt_parent", 0.5);
                                        parentFam.withdrawPlayer("adopt_parent", 0.5);
                                    } else {
                                        sender.sendMessage(Main.prefix + Main.getMessage("adopt_accept_adopted_by_single").replace("%player%", parentFam.getName()));
                                    }
                                    playerFam.withdrawPlayer("adopt_child");

                                    parentFam.sendMessage(Main.prefix + Main.getMessage("adopt_accept_adopted").replace("%player%", playerFam.getName()));
                                    Main.adoptRequests.remove(playerUUID);
                                    parentFam.adopt(playerFam.getID());
                                }
                            }
                        } else if (args[0].equalsIgnoreCase("list") || Main.getAliases("adopt", "list").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                            if (playerFam.getChildren().get(0) != null || playerFam.getChildren().get(1) != null) {
                                sender.sendMessage(Main.prefix + Main.getMessage("adopt_list_no_child"));
                            } else {
                                    String msg = Main.prefix + Main.getMessage("adopt_list") + "\n";
                                    if (playerFam.getChildren().get(0) != null) {
                                        msg = msg + playerFam.getChildren().get(0).getName() + "\n";
                                    }
                                    if (playerFam.getChildren().get(1) != null) {
                                        msg = msg + playerFam.getChildren().get(1).getName() + "\n";
                                    }
                                    sender.sendMessage(msg);
                            }
                        } else if (args[0].equalsIgnoreCase("deny") || Main.getAliases("adopt", "deny").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                            if (!Main.adoptRequests.containsKey(playerUUID)) {
                                sender.sendMessage(Main.prefix + Main.getMessage("adopt_deny_no_request"));
                            } else {
                                String parent = Main.adoptRequests.get(playerUUID);
                                FamilyManager parentFam = new FamilyManager(parent);
                                parentFam.sendMessage(Main.prefix + Main.getMessage("adopt_deny").replace("%player%", playerFam.getName()));
                                Main.marryRequests.remove(playerUUID);
                            }
                        } else if (args[0].equalsIgnoreCase("moveout") || Main.getAliases("adopt", "moveout").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

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
                                sender.sendMessage(Main.prefix + Main.getMessage("adopt_moveout_no_parents"));
                            } else if (!confirm) {
                                sender.sendMessage(Main.createClickableMessage(
                                        Main.getMessage("adopt_moveout_confirm"),
                                        Main.getMessage("confirm"),
                                        "/adopt moveout confirm",
                                        Main.getMessage("cancel"),
                                        "/adopt moveout cancel"));
                            } else if (cancel) {
                                sender.sendMessage(Main.prefix + Main.getMessage("adopt_moveout_cancel"));
                            } else if (!force && !playerFam.hasEnoughMoney("adopt_moveout_child")) {
                                sender.sendMessage(Main.prefix + Main.getMessage("not_enough_money"));
                            } else if (!force && playerFam.getParents().size() == 2 && !playerFam.getParents().get(0).hasEnoughMoney("adopt_moveout_parent", 0.5)) {
                                sender.sendMessage(Main.prefix + Main.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(1).getName()));
                                sender.sendMessage(Main.createClickableMessage(
                                        Main.getMessage("take_payment_confirm"),
                                        Main.getMessage("confirm"),
                                        "/adopt moveout confirm force",
                                        Main.getMessage("cancel"),
                                        "/adopt moveout cancel"));
                            } else if (!force && playerFam.getParents().size() == 1 && !playerFam.getParents().get(0).hasEnoughMoney("adopt_moveout_parent")) {
                                sender.sendMessage(Main.prefix + Main.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(0).getName()));
                                sender.sendMessage(Main.createClickableMessage(
                                        Main.getMessage("take_payment_confirm"),
                                        Main.getMessage("confirm"),
                                        "/adopt moveout confirm force",
                                        Main.getMessage("cancel"),
                                        "/adopt moveout cancel"));
                            } else if (!force && playerFam.getParents().size() == 2 && !playerFam.getParents().get(1).hasEnoughMoney("adopt_moveout_parent", 0.5)) {
                                sender.sendMessage(Main.prefix + Main.getMessage("player_not_enough_money").replace("%player%", playerFam.getParents().get(1).getName()));
                                sender.sendMessage(Main.createClickableMessage(
                                        Main.getMessage("take_payment_confirm"),
                                        Main.getMessage("confirm"),
                                        "/adopt moveout confirm force",
                                        Main.getMessage("cancel"),
                                        "/adopt moveout cancel"));
                            } else if (force && !playerFam.hasEnoughMoney("adopt_moveout_parent", "adopt_moveout_child")) {
                                sender.sendMessage(Main.prefix + Main.getMessage("not_enough_money"));
                            } else {
                                FamilyManager firstParentFam = playerFam.getParents().get(0);

                                if (playerFam.hasSibling()) {
                                    FamilyManager siblingFam = playerFam.getSibling();
                                    siblingFam.sendMessage(Main.prefix + Main.getMessage("adopt_moveout_sibling"));
                                }

                                sender.sendMessage(Main.prefix + Main.getMessage("adopt_moveout"));


                                firstParentFam.sendMessage(Main.prefix + Main.getMessage("adopt_moveout_child").replace("%player%", playerFam.getName()));
                                if (firstParentFam.isMarried()) {
                                        FamilyManager secondParentFam = firstParentFam.getPartner();
                                        secondParentFam.sendMessage(Main.prefix + Main.getMessage("adopt_moveout_child").replace("%player%", playerFam.getName()));
                                }

                                if (force) {
                                    playerFam.withdrawPlayer("moveout_child");
                                    playerFam.withdrawPlayer("adopt_moveout_parent", 0.5);
                                    playerFam.withdrawPlayer("adopt_moveout_parent", 0.5);
                                } else {
                                    if (firstParentFam.isMarried()) {
                                        FamilyManager secondParentFam = firstParentFam.getPartner();
                                        secondParentFam.withdrawPlayer("adopt_moveout_parent", 0.5);
                                        firstParentFam.withdrawPlayer("adopt_moveout_parent", 0.5);
                                    } else {
                                        firstParentFam.withdrawPlayer("adopt_moveout_parent");

                                    }
                                    playerFam.withdrawPlayer("moveout_child");
                                }

                                firstParentFam.unadopt(playerFam.getID());

                            }
                        } else if (args[0].equalsIgnoreCase("kickout") || Main.getAliases("adopt", "kickout").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                            if (playerFam.getChildren().get(0) != null || playerFam.getChildren().get(1) != null) {
                                if (args.length == 1) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("adopt_kickout_specify_child"));
                                } else {
                                    String childUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                                    FamilyManager childFam = new FamilyManager(childUUID);
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
                                            sender.sendMessage(Main.createClickableMessage(
                                                    Main.getMessage("adopt_kickout_confirm"),
                                                    Main.getMessage("confirm"),
                                                    "/adopt kickout confirm",
                                                    Main.getMessage("cancel"),
                                                    "/adopt kickout cancel"));
                                        } else if (cancel) {
                                            sender.sendMessage(Main.prefix + Main.getMessage("adopt_kickout_cancel"));
                                        } else if (!force && playerFam.isMarried() && !playerFam.hasEnoughMoney("adopt_kickout_parent", 0.5)) {
                                            sender.sendMessage(Main.prefix + Main.getMessage("not_enough_money"));
                                        } else if (!force && !playerFam.isMarried() && !playerFam.hasEnoughMoney("adopt_kickout_parent")) {
                                            sender.sendMessage(Main.prefix + Main.getMessage("not_enough_money"));
                                        } else if (!childFam.hasEnoughMoney("adopt_kickout_child")) {
                                            sender.sendMessage(Main.prefix + Main.getMessage("player_not_enough_money").replace("%player%", childFam.getName()));
                                            sender.sendMessage(Main.createClickableMessage(
                                                    Main.getMessage("take_payment_confirm"),
                                                    Main.getMessage("confirm"),
                                                    "/adopt kickout confirm force",
                                                    Main.getMessage("cancel"),
                                                    "/adopt kickout confirm force"));
                                        } else if (!force && playerFam.isMarried() && !playerFam.getPartner().hasEnoughMoney("adopt_kickout_parent")) {
                                            sender.sendMessage(Main.prefix + Main.getMessage("player_not_enough_money").replace("%player%", playerFam.getPartner().getName()));
                                            sender.sendMessage(Main.createClickableMessage(
                                                    Main.getMessage("take_payment_confirm"),
                                                    Main.getMessage("confirm"),
                                                    "/adopt kickout confirm force",
                                                    Main.getMessage("cancel"),
                                                    "/adopt kickout confirm force"));
                                        } else {
                                            sender.sendMessage(Main.prefix + Main.getMessage("adopt_kickout").replace("%player%", childFam.getName()));
                                            if (playerFam.isMarried()) {
                                                playerFam.getPartner().sendMessage(Main.prefix + Main.getMessage("adopt_kickout_partner").replace("%player1%", playerFam.getName()).replace("%player2%", childFam.getName()));
                                            }

                                            if (childFam.hasSibling()) {
                                                FamilyManager siblingFam = childFam.getSibling();
                                                siblingFam.sendMessage(Main.prefix + Main.getMessage("adopt_kickout_sibling").replace("%player%", playerFam.getName()));
                                            }
                                            childFam.sendMessage(Main.prefix + Main.getMessage("adopt_kickout_child"));

                                            if (force) {
                                                playerFam.withdrawPlayer("adopt_kickout_parent", "adopt_kickout_child");
                                            } else {
                                                if (playerFam.isMarried()) {
                                                    playerFam.getPartner().withdrawPlayer("adopt_kickout_parent", 0.5);
                                                    playerFam.withdrawPlayer("adopt_kickout_parent", 0.5);
                                                }
                                                else {
                                                    playerFam.withdrawPlayer("adopt_kickout_parent");
                                                }
                                                childFam.withdrawPlayer("adopt_kickout_child");
                                            }

                                            playerFam.unadopt(childFam.getID());
                                        }

                                    } else {
                                        sender.sendMessage(Main.prefix + Main.getMessage("adopt_kickout_not_your_child").replace("%player%", childFam.getName()));
                                    }
                                }
                            } else {
                                sender.sendMessage(Main.prefix + Main.getMessage("adopt_kickout_no_child"));
                            }
                        } else {
                            sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                        }
                    }
                }


        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {

        List<String> adoptSubcommands = Main.adoptSubcommands;
        List<String> adoptAdminSubcommands = Main.adoptAdminSubcommands;
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
}
