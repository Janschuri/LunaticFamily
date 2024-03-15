package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.Main;
import de.janschuri.lunaticFamily.utils.FamilyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenderCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Main.prefix + Main.getMessage("no_console_command"));
            } else {
                Player player = (Player) sender;
                String uuid = player.getUniqueId().toString();
                FamilyManager playerFam = new FamilyManager(uuid);

                if (playerFam.getGender().equalsIgnoreCase("fe")) {
                    player.sendMessage(Main.prefix + Main.getMessage("gender_fe"));
                } else if (playerFam.getGender().equalsIgnoreCase("ma")) {
                    player.sendMessage(Main.prefix + Main.getMessage("gender_ma"));
                }
            }
        } else {
            if (args.length == 1) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Main.prefix + Main.getMessage("no_console_command"));
                } else {
                    Player player = (Player) sender;
                    String uuid = player.getUniqueId().toString();
                    FamilyManager playerFam = new FamilyManager(uuid);
                    if (args[0].equalsIgnoreCase("fe") || Main.getAliases("gender", "fe").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                        if (playerFam.getGender().equalsIgnoreCase("fe")) {
                            player.sendMessage(Main.prefix + Main.getMessage("gender_already_fe"));
                        } else {
                            playerFam.setGender("fe");
                            sender.sendMessage(Main.prefix + Main.getMessage("gender_changed_fe"));
                        }
                    } else if (args[0].equalsIgnoreCase("ma") || Main.getAliases("gender", "ma").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                        if (playerFam.getGender().equalsIgnoreCase("ma")) {
                            player.sendMessage(Main.prefix + Main.getMessage("gender_already_ma"));
                        } else {
                            playerFam.setGender("ma");
                            sender.sendMessage(Main.prefix + Main.getMessage("gender_changed_ma"));
                        }
                    } else {
                        sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                    }
                }
            } else if (args.length > 2 && sender.hasPermission("lunaticFamily.admin.gender")) {

                if (args[0].equalsIgnoreCase("set") || Main.getAliases("gender", "set").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                    String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyManager player1Fam = new FamilyManager(player1);

                    if (args[2].equalsIgnoreCase("fe") || Main.getAliases("gender", "fe").stream().anyMatch(element -> args[2].equalsIgnoreCase(element))) {
                        if (player1Fam.getGender().equalsIgnoreCase("fe")) {
                            sender.sendMessage(Main.prefix + Main.getMessage("admin_gender_already_fe").replace("%player%", player1Fam.getName()));
                        } else {
                            player1Fam.setGender("fe");
                            sender.sendMessage(Main.prefix + Main.getMessage("admin_gender_changed_fe").replace("%player%", player1Fam.getName()));
                        }
                    } else if (args[2].equalsIgnoreCase("ma") || Main.getAliases("gender", "ma").stream().anyMatch(element -> args[2].equalsIgnoreCase(element))) {
                        if (player1Fam.getGender().equalsIgnoreCase("ma")) {
                            sender.sendMessage(Main.prefix + Main.getMessage("admin_gender_already_ma").replace("%player%", player1Fam.getName()));
                        } else {
                            player1Fam.setGender("ma");
                            sender.sendMessage(Main.prefix + Main.getMessage("admin_gender_changed_ma").replace("%player%", player1Fam.getName()));
                        }
                    } else {
                        sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                    }
                } else {
                    sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                }
            } else {
                sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {

        List<String> genderSubcommands = Main.genderSubcommands;
        List<String> genderAdminSubcommands = Main.genderAdminSubcommands;
        List<String> list = new ArrayList<>();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("gender")) {
                if (args.length == 0) {
                    if (player.hasPermission("lunaticFamily.admin.gender")) {
                        list.addAll(genderAdminSubcommands);
                    }
                    if (player.hasPermission("lunaticFamily.gender")) {
                        list.addAll(genderSubcommands);
                    }
                    Collections.sort(list);
                    return list;
                } else if (args.length == 1) {
                    if (player.hasPermission("lunaticFamily.admin.gender")) {
                        for (String s : genderAdminSubcommands) {
                            if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                list.add(s);
                            }
                        }
                    }
                    if (player.hasPermission("lunaticFamily.gender")) {
                        for (String s : genderSubcommands) {
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
