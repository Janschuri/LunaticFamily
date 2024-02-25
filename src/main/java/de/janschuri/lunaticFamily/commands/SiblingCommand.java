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

import java.util.*;

public class SiblingCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public SiblingCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {

        } else if (args.length > 0) {

            if (!(sender instanceof Player)) {
                String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                FamilyManager player1Fam = new FamilyManager(player1UUID, plugin);
                String player2UUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                FamilyManager player2Fam = new FamilyManager(player2UUID, plugin);

                if (args[0].equalsIgnoreCase("set") && args.length == 3) {

                    if(!player1Fam.isAdopted() && !player2Fam.isAdopted()) {

                        player1Fam.addSibling(player2Fam.getID());
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_sibling_added"));


                    } else {
                        if(player1Fam.isAdopted() && !player2Fam.isAdopted()) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_sibling_is_adopted").replace("%player%", player1Fam.getName()));
                        } else if(player2Fam.isAdopted() && !player1Fam.isAdopted()) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_sibling_is_adopted").replace("%player%", player2Fam.getName()));
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_sibling_both_adopted").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                        }
                    }

                } else if (args[0].equalsIgnoreCase("unset") && args.length == 2) {

                } else {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("no_console_command"));
                }
            } else {
                Player player = (Player) sender;
                if (args[0].equalsIgnoreCase("set") && player.hasPermission("lunaticFamily.admin.sibling") && args.length == 3) {
                    String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyManager player1Fam = new FamilyManager(player1UUID, plugin);
                    String player2UUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                    FamilyManager player2Fam = new FamilyManager(player2UUID, plugin);

                    if(!player1Fam.isAdopted() && !player2Fam.isAdopted()) {

                        player1Fam.addSibling(player2Fam.getID());
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_sibling_is_adopted").replace("%player%", player2Fam.getName()));


                    } else {
                        if(player1Fam.isAdopted() && !player2Fam.isAdopted()) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_sibling_is_adopted").replace("%player%", player1Fam.getName()));
                        } else if(player2Fam.isAdopted() && !player1Fam.isAdopted()) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_sibling_is_adopted").replace("%player%", player2Fam.getName()));
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_sibling_both_adopted").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                        }
                    }

                } else if (args[0].equalsIgnoreCase("unset") && player.hasPermission("lunaticFamily.admin.sibling") && args.length == 2) {


                } else {
                    if (player.hasPermission("lunaticFamily.sibling")) {
                        String playerUUID = player.getUniqueId().toString();
                        FamilyManager playerFam = new FamilyManager(playerUUID, plugin);

                        if (args[0].equalsIgnoreCase("propose")) {

                        } else if (args[0].equalsIgnoreCase("accept")) {

                        } else if (args[0].equalsIgnoreCase("deny")) {

                        } else if (args[0].equalsIgnoreCase("stab")) {

                        }  else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                        }
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("no_permission"));
                    }
                }
            }


        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        List<String> adoptSubcommands = plugin.adoptSubcommands;
        List<String> adoptAdminSubcommands = plugin.adoptAdminSubcommands;
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