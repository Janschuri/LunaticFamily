package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.Main;
import de.janschuri.lunaticFamily.utils.FamilyManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SiblingCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public SiblingCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {

        } else{

            if (args[0].equalsIgnoreCase("set") || Main.getAliases("sibling", "set").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

                boolean forced = false;

                if (args.length > 3) {
                    if (args[3].equalsIgnoreCase("force")) {
                        forced = true;
                    }
                }

                if (!sender.hasPermission("lunaticFamily.admin.sibling")) {
                    sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                } else if (args.length < 2) {
                    sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                } else if (!Main.playerExists(args[1]) && !forced) {
                    sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", args[1]));
                } else if (!Main.playerExists(args[2]) && !forced) {
                    sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", args[2]));
                } else if (args[1].equalsIgnoreCase(args[2])) {
                    sender.sendMessage(Main.prefix + Main.getMessage("admin_marry_set_same_player"));
                } else {

                    String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyManager player1Fam = new FamilyManager(player1UUID);
                    String player2UUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                    FamilyManager player2Fam = new FamilyManager(player2UUID);

                    if (player1Fam.isAdopted() && player2Fam.isAdopted()) {
                        sender.sendMessage(Main.prefix + Main.getMessage("admin_sibling_set_both_adopted").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                    } else if (player1Fam.isAdopted()) {
                        sender.sendMessage(Main.prefix + Main.getMessage("admin_sibling_is_adopted").replace("%player%", player1Fam.getName()));

                    } else if (player2Fam.isAdopted()) {
                        sender.sendMessage(Main.prefix + Main.getMessage("admin_sibling_is_adopted").replace("%player%", player2Fam.getName()));
                    } else {
                        sender.sendMessage(Main.prefix + Main.getMessage("admin_sibling_added").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                        player1Fam.addSibling(player2Fam.getID());
                    }
                }
            } else if (args[0].equalsIgnoreCase("unset") || Main.getAliases("sibling", "unset").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                if (!sender.hasPermission("lunaticFamily.admin.sibling")) {
                    sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                } else if (args.length < 2) {
                    sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                } else if (!Main.playerExists(args[1])) {
                    sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", Main.getName(args[1])));
                } else {
                    String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyManager player1Fam = new FamilyManager(player1UUID);

                    if (!player1Fam.hasSibling()) {
                        sender.sendMessage(Main.prefix + Main.getMessage("admin_sibling_unset_no_sibling").replace("%player%", player1Fam.getName()));
                    } else {
                        FamilyManager siblingFam = player1Fam.getSibling();
                        player1Fam.removeSibling();
                        sender.sendMessage(Main.prefix + Main.getMessage("admin_sibling_unset_sibling").replace("%player1%", player1Fam.getName()).replace("%player2%", siblingFam.getName()));
                    }
                }
            } else if (!(sender instanceof Player)) {
                sender.sendMessage(Main.prefix + Main.getMessage("no_console_command"));
            }
            else{
                Player player = (Player) sender;

                    if (!player.hasPermission("lunaticFamily.sibling")) {
                        sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));

                    } else {
                        String playerUUID = player.getUniqueId().toString();
                        FamilyManager playerFam = new FamilyManager(playerUUID);

                        if (args[0].equalsIgnoreCase("propose") || Main.getAliases("sibling", "propose").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                            if (playerFam.hasSibling()) {
                                sender.sendMessage(Main.prefix + Main.getMessage("sibling_propose_has_sibling").replace("%player%", playerFam.getName()));
                            } else if (playerFam.isAdopted()) {
                                sender.sendMessage(Main.prefix + Main.getMessage("sibling_propose_is_adopted").replace("%player%", playerFam.getName()));
                            } else if (args.length < 2) {
                                sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                            } else if (!Main.playerExists(args[1])) {
                                sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", args[1]));
                            } else if (Bukkit.getPlayer(args[1]) == null) {
                                sender.sendMessage(Main.prefix + Main.getMessage("player_offline").replace("%player%", Main.getName(args[1])));
                            } else {
                                String siblingUUID = Bukkit.getPlayer(args[1]).getUniqueId().toString();
                                Player siblingPlayer = Bukkit.getPlayer(args[1]);
                                FamilyManager siblingFam = new FamilyManager(siblingUUID);
                                if (playerFam.getID() == siblingFam.getID()) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("sibling_propose_self_request"));
                                } else if (playerFam.isFamilyMember(siblingFam.getID())) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("sibling_propose_family_request").replace("%player%", siblingFam.getName()));
                                } else if (siblingFam.isFamilyMember(playerFam.getID())) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("sibling_propose_family_request").replace("%player%", siblingFam.getName()));
                                } else if (siblingFam.isAdopted()) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("sibling_propose_sibling_is_adopted").replace("%player%", siblingFam.getName()));
                                } else if (Main.siblingRequests.containsKey(siblingUUID)) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("sibling_propose_open_request").replace("%player%", siblingFam.getName()));
                                }  else if (!Main.isInRange(player.getLocation(), siblingPlayer.getLocation(), 5)) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("player_too_far_away").replace("%player%", siblingFam.getName()));
                                } else if (!playerFam.hasEnoughMoney("sibling_proposing_player")) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("not_enough_money"));
                                } else {
                                    siblingFam.sendMessage(Main.createClickableMessage(
                                            Main.getMessage("sibling_propose_request").replace("%player%", siblingFam.getName()),
                                            Main.getMessage("accept"),
                                            "/sibling accept",
                                            Main.getMessage("deny"),
                                            "/sibling deny"));

                                    Main.siblingRequests.put(siblingUUID, playerUUID);

                                    sender.sendMessage(Main.prefix + Main.getMessage("sibling_propose_request_sent").replace("%player%", siblingFam.getName()));

                                    new BukkitRunnable() {
                                        public void run() {
                                            if (Main.siblingRequests.containsKey(siblingUUID)) {
                                                Main.siblingRequests.remove(siblingUUID);
                                                siblingPlayer.sendMessage(Main.prefix + Main.getMessage("sinling_propose_request_expired").replace("%player%", playerFam.getName()));

                                                sender.sendMessage(Main.prefix + Main.getMessage("sibling_propose_request_sent_expired").replace("%player%", siblingFam.getName()));
                                            }
                                        }
                                    }.runTaskLater(plugin, 600L);
                                }
                            }

                        } else if (args[0].equalsIgnoreCase("accept") || Main.getAliases("sibling", "accept").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                            if (!Main.marryRequests.containsKey(playerUUID)) {
                                sender.sendMessage(Main.prefix + Main.getMessage("sibling_accept_no_request"));
                            } else {
                                    String siblingUUID = Main.marryRequests.get(playerUUID);
                                    FamilyManager siblingFam = new FamilyManager(siblingUUID);

                                    if (playerFam.getChildrenAmount() + siblingFam.getChildrenAmount() > 2) {
                                        int amountDiff = playerFam.getChildrenAmount() + siblingFam.getChildrenAmount() - 2;
                                        sender.sendMessage(Main.prefix + Main.getMessage("marry_accept_too_many_children").replace("%partner%", siblingFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                                    } else if (Bukkit.getPlayer(UUID.fromString(siblingUUID)) == null) {
                                        sender.sendMessage(Main.prefix + Main.getMessage("player_offline").replace("%player%", siblingFam.getName()));
                                    } else if (!playerFam.hasEnoughMoney("sibling_proposed_player")) {
                                        sender.sendMessage(Main.prefix + Main.getMessage("not_enough_money"));
                                    } else if (!playerFam.hasEnoughMoney("sibling_proposing_player")) {
                                        sender.sendMessage(Main.prefix + Main.getMessage("player_not_enough_money").replace("%player%", siblingFam.getName()));
                                    } else {

                                        sender.sendMessage(Main.prefix + Main.getMessage("propose_accept_complete").replace("%player%", siblingFam.getName()));
                                        Bukkit.getPlayer(UUID.fromString(siblingUUID)).sendMessage(Main.prefix + Main.getMessage("sibling_accept_complete").replace("%player%", playerFam.getName()));

                                        Main.siblingRequests.remove(playerUUID);
                                        Main.siblingRequests.remove(siblingUUID);
                                        playerFam.addSibling(siblingFam.getID());

                                        playerFam.withdrawPlayer("sibling_proposed_player");
                                        siblingFam.withdrawPlayer("sibling_proposing_player");
                                    }
                            }
                        } else if (args[0].equalsIgnoreCase("deny") || Main.getAliases("sibling", "deny").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                            if (!Main.siblingRequests.containsKey(playerUUID) && !Main.marryPriestRequests.containsKey(playerUUID)) {
                                sender.sendMessage(Main.prefix + Main.getMessage("propose_deny_no_request"));
                            } else {
                                if (!Main.siblingRequests.containsKey(playerUUID)) {

                                }
                                String partnerUUID = Main.marryRequests.get(playerUUID);
                                Bukkit.getPlayer(UUID.fromString(partnerUUID)).sendMessage(Main.prefix + Main.getMessage("propose_deny_denied").replace("%player%", playerFam.getName()));
                                Main.marryRequests.remove(playerUUID);
                            }
                        } else if (args[0].equalsIgnoreCase("unsibling") || Main.getAliases("sibling", "unsibling").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
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

                            if (!playerFam.hasSibling()) {
                                sender.sendMessage(Main.prefix + Main.getMessage("sibling_unsibling_no_sibling"));
                            } else if (!confirm) {
                                sender.sendMessage(Main.prefix + Main.getMessage("sibling_unsibling_confirm"));
                            } else if (cancel) {
                                sender.sendMessage(Main.prefix + Main.getMessage("sibling_unsibling_cancel"));
                            } else if (playerFam.isAdopted()) {
                                sender.sendMessage(Main.prefix + Main.getMessage("sibling_unsibling_adopted"));
                            } else if (!force && !playerFam.hasEnoughMoney("sibling_unsibling_leaving_player")) {
                                sender.sendMessage(Main.prefix + Main.getMessage("not_enough_money"));
                            } else if (!force && !playerFam.getSibling().hasEnoughMoney("sibling_unsibling_left_player")) {
                                sender.sendMessage(Main.prefix + Main.getMessage("player_not_enough_money").replace("%player%", playerFam.getSibling().getName()));
                                sender.sendMessage(Main.createClickableMessage(
                                        Main.getMessage("take_payment_confirm"),
                                        Main.getMessage("confirm"),
                                        "/sibling unsibling confirm force",
                                        Main.getMessage("cancel"),
                                        "/sibling unsibling cancel"));
                            } else {
                                sender.sendMessage(Main.prefix + Main.getMessage("sibling_unsibling_complete"));
                                    playerFam.getSibling().sendMessage(Main.prefix + Main.getMessage("sibling_unsibling_complete"));

                                    if(force) {
                                        playerFam.withdrawPlayer("sibling_unsibling_leaving_player");
                                        playerFam.withdrawPlayer("sibling_unsibling_left_player");
                                    } else {
                                        playerFam.withdrawPlayer("sibling_unsibling_leaving_player");
                                        playerFam.getSibling().withdrawPlayer("sibling_unsibling_left_player");
                                    }
                                playerFam.removeSibling();
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

        List<String> siblingSubcommands = Main.siblingSubcommands;
        List<String> siblingAdminSubcommands = Main.siblingAdminSubcommands;
        List<String> list = new ArrayList<>();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("sibling")) {
                if (args.length == 0) {
                    if (player.hasPermission("lunaticFamily.admin.sibling")) {
                        list.addAll(siblingAdminSubcommands);
                    }
                    if (player.hasPermission("lunaticFamily.sibling")) {
                        list.addAll(siblingSubcommands);
                    }
                    Collections.sort(list);
                    return list;
                } else if (args.length == 1) {
                    if (player.hasPermission("lunaticFamily.admin.sibling")) {
                        for (String s : siblingAdminSubcommands) {
                            if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                list.add(s);
                            }
                        }
                    }
                    if (player.hasPermission("lunaticFamily.sibling")) {
                        for (String s : siblingSubcommands) {
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