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

            if (args[0].equalsIgnoreCase("set")) {

                boolean forced = false;

                if (args.length > 3) {
                    if (args[3].equalsIgnoreCase("force")) {
                        forced = true;
                    }
                }

                if (!sender.hasPermission("lunaticFamily.admin.sibling")) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("no_permission"));
                } else if (args.length < 2) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                } else if (!Main.playerExists(args[1]) && !forced) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("player_not_exist").replace("%player%", args[1]));
                } else if (!Main.playerExists(args[2]) && !forced) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("player_not_exist").replace("%player%", args[2]));
                } else if (args[1].equalsIgnoreCase(args[2])) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_set_same_player"));
                } else {

                    String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyManager player1Fam = new FamilyManager(player1UUID, plugin);
                    String player2UUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                    FamilyManager player2Fam = new FamilyManager(player2UUID, plugin);

                    if (player1Fam.isAdopted() && player2Fam.isAdopted()) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_sibling_set_both_adopted").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                    } else if (player1Fam.isAdopted()) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_sibling_is_adopted").replace("%player%", player1Fam.getName()));

                    } else if (player2Fam.isAdopted()) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_sibling_is_adopted").replace("%player%", player2Fam.getName()));
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_sibling_added"));
                        player1Fam.addSibling(player2Fam.getID());
                    }
                }
            } else if (args[0].equalsIgnoreCase("unset")) {
                if (!sender.hasPermission("lunaticFamily.admin.sibling")) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("no_permission"));
                } else if (args.length < 2) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                } else if (!Main.playerExists(args[1])) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("player_not_exist").replace("%player%", Bukkit.getOfflinePlayer(args[1]).getName()));
                } else {
                    String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyManager player1Fam = new FamilyManager(player1UUID, plugin);

                    if (!player1Fam.hasSibling()) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_sibling_unset_no_sibling").replace("%player%", player1Fam.getName()));
                    } else {
                        FamilyManager siblingFam = player1Fam.getSibling();
                        player1Fam.removeSibling();
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_sibling_unset_sibling").replace("%player1%", player1Fam.getName()).replace("%player2%", siblingFam.getName()));
                    }
                }
            } else if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.prefix + plugin.messages.get("no_console_command"));
            }
            else{
                Player player = (Player) sender;

                    if (!player.hasPermission("lunaticFamily.sibling")) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("no_permission"));

                    } else {
                        String playerUUID = player.getUniqueId().toString();
                        FamilyManager playerFam = new FamilyManager(playerUUID, plugin);

                        if (args[0].equalsIgnoreCase("propose")) {
                            if (playerFam.hasSibling()) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("sibling_propose_has_sibling").replace("%player%", playerFam.getName()));
                            } else if (playerFam.isAdopted()) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("sibling_propose_is_adopted").replace("%player%", playerFam.getName()));
                            } else if (args.length < 2) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                            } else if (!Main.playerExists(args[1])) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("player_not_exist").replace("%player%", args[1]));
                            } else if (Bukkit.getPlayer(args[1]) == null) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("player_offline").replace("%player%", Bukkit.getOfflinePlayer(args[1]).getName()));
                            } else {
                                String siblingUUID = Bukkit.getPlayer(args[1]).getUniqueId().toString();
                                Player siblingPlayer = Bukkit.getPlayer(args[1]);
                                FamilyManager siblingFam = new FamilyManager(siblingUUID, plugin);
                                if (playerFam.getID() == siblingFam.getID()) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("sibling_propose_self_request"));
                                } else if (playerFam.isFamilyMember(siblingFam.getID())) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("sibling_propose_family_request").replace("%player%", siblingFam.getName()));
                                } else if (siblingFam.isFamilyMember(playerFam.getID())) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("sibling_propose_family_request").replace("%player%", siblingFam.getName()));
                                } else if (siblingFam.isAdopted()) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("sibling_propose_sibling_is_adopted").replace("%player%", siblingFam.getName()));
                                } else if (plugin.siblingRequests.containsKey(siblingUUID)) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("sibling_propose_open_request").replace("%player%", siblingFam.getName()));
                                }  else if (!Main.isInRange(player.getLocation(), siblingPlayer.getLocation(), 5)) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("player_too_far_away").replace("%player%", siblingFam.getName()));
                                } else {
                                    siblingPlayer.sendMessage(plugin.prefix + plugin.messages.get("sibling_propose_request").replace("%player%", siblingFam.getName()));


                                    plugin.siblingRequests.put(siblingUUID, playerUUID);

                                    sender.sendMessage(plugin.prefix + plugin.messages.get("sibling_propose_request_sent").replace("%player%", siblingFam.getName()));

                                    new BukkitRunnable() {
                                        public void run() {
                                            if (plugin.siblingRequests.containsKey(siblingUUID)) {
                                                plugin.siblingRequests.remove(siblingUUID);
                                                siblingPlayer.sendMessage(plugin.prefix + plugin.messages.get("sinling_propose_request_expired").replace("%player%", playerFam.getName()));

                                                sender.sendMessage(plugin.prefix + plugin.messages.get("sibling_propose_request_sent_expired").replace("%player%", siblingFam.getName()));
                                            }
                                        }
                                    }.runTaskLater(plugin, 600L);
                                }
                            }

                        } else if (args[0].equalsIgnoreCase("accept")) {
                            if (!plugin.marryRequests.containsKey(playerUUID)) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("sibling_accept_no_request"));
                            } else {
                                    String siblingUUID = plugin.marryRequests.get(playerUUID);
                                    FamilyManager siblingFam = new FamilyManager(siblingUUID, plugin);

                                    if (playerFam.getChildrenAmount() + siblingFam.getChildrenAmount() > 2) {
                                        int amountDiff = playerFam.getChildrenAmount() + siblingFam.getChildrenAmount() - 2;
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("marry_accept_too_many_children").replace("%partner%", siblingFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                                    } else if (Bukkit.getPlayer(UUID.fromString(siblingUUID)) == null) {
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("player_offline").replace("%player%", siblingFam.getName()));
                                    } else {

                                        sender.sendMessage(plugin.prefix + plugin.messages.get("propose_accept_complete").replace("%player%", siblingFam.getName()));
                                        Bukkit.getPlayer(UUID.fromString(siblingUUID)).sendMessage(plugin.prefix + plugin.messages.get("sibling_accept_complete").replace("%player%", playerFam.getName()));

                                        plugin.siblingRequests.remove(playerUUID);
                                        plugin.siblingRequests.remove(siblingUUID);
                                        playerFam.addSibling(siblingFam.getID());
                                    }
                            }
                        } else if (args[0].equalsIgnoreCase("deny")) {
                            if (!plugin.siblingRequests.containsKey(playerUUID) && !plugin.marryPriestRequests.containsKey(playerUUID)) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("propose_deny_no_request"));
                            } else {
                                if (!plugin.siblingRequests.containsKey(playerUUID)) {

                                }
                                String partnerUUID = plugin.marryRequests.get(playerUUID);
                                Bukkit.getPlayer(UUID.fromString(partnerUUID)).sendMessage(plugin.prefix + plugin.messages.get("propose_deny_denied").replace("%player%", playerFam.getName()));
                                plugin.marryRequests.remove(playerUUID);
                            }
                        } else if (args[0].equalsIgnoreCase("stab")) {
                            boolean confirm = false;

                            if (args.length > 1) {
                                if (args[1].equalsIgnoreCase("confirm")) {
                                    confirm = true;
                                }
                            }

                            if (!playerFam.isMarried()) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("sibling_stab_no_sibling"));
                            } else if (!confirm) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("sibling_stab_confirm"));
                            } else if (playerFam.isAdopted()) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("sibling_stab_is_adsibling"));
                            }else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("sibling_stab_complete"));
                                if (Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) != null) {
                                    Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())).sendMessage(plugin.prefix + plugin.messages.get("sibling_stab_complete"));
                                }
                                playerFam.removeSibling();
                            }
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                        }
                    }

            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        List<String> siblingSubcommands = plugin.siblingSubcommands;
        List<String> siblingAdminSubcommands = plugin.siblingAdminSubcommands;
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