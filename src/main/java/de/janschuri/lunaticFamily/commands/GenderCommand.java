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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenderCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 0) {
            if (!sender.hasPermission("lunaticFamily." + label)) {
                sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
            } else {
                String[] subcommandsHelp = {"set", "info"};

                String msg = Main.prefix + " " + Main.getMessage(label + "_help") + "\n";

                for (String subcommand : subcommandsHelp) {
                    msg = msg + Main.prefix + " " + Main.getMessage(label + "_" + subcommand + "_help") + "\n";
                }
                sender.sendMessage(msg);
            }
        } else {
            if (args[0].equalsIgnoreCase("set") || Main.getAliases("gender", "set").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                if (args.length == 1) {
                    TextComponent msg = new TextComponent(Main.prefix + Main.getMessage("gender_set") + "\n");

                    for (String gender : Main.genders) {
                        TextComponent text = new TextComponent(Main.prefix + " - " + Main.getGenderLang(gender) + "\n");
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gender set " + gender));
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Main.getMessage("gender_set_hover").replace("%gender%", Main.getGenderLang(gender))).create()));
                        msg.addExtra(text);
                    }
                    sender.sendMessage(msg);

                } else if (args.length == 2) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Main.prefix + Main.getMessage("no_console_command"));
                    } else {
                        Player player = (Player) sender;
                        String uuid = player.getUniqueId().toString();
                        FamilyManager playerFam = new FamilyManager(uuid);

                        if (!Main.genders.contains(args[1].toLowerCase())) {
                            sender.sendMessage(Main.prefix + Main.getMessage("gender_not_exist"));
                        } else if (playerFam.getGender().equalsIgnoreCase(args[1])) {
                            player.sendMessage(Main.prefix + Main.getMessage("gender_already").replace("%gender%", Main.getGenderLang(args[1])));
                        } else {
                            playerFam.setGender(args[1].toLowerCase());
                            sender.sendMessage(Main.prefix + Main.getMessage("gender_changed").replace("%gender%", Main.getGenderLang(args[1])));
                        }
                    }
                } else {
                    if (!sender.hasPermission("lunaticFamily.admin.gender")) {
                        sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                    } else if (!Main.playerExists(args[1])) {
                        sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", args[1]));
                    } else {
                        String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        FamilyManager player1Fam = new FamilyManager(player1);

                        if (!Main.genders.contains(args[2].toLowerCase())) {
                            sender.sendMessage(Main.prefix + Main.getMessage("gender_not_exist"));
                        } else if (player1Fam.getGender().equalsIgnoreCase(args[2])) {
                            sender.sendMessage(Main.prefix + Main.getMessage("admin_gender_already").replace("%player%", Main.getName(args[1])).replace("%gender%", Main.getGenderLang(args[2])));
                        } else {
                            player1Fam.setGender(args[2].toLowerCase());
                            sender.sendMessage(Main.prefix + Main.getMessage("admin_gender_changed").replace("%player%", Main.getName(args[1])).replace("%gender%", Main.getGenderLang(args[2])));
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("info") || Main.getAliases("gender", "info").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                if (args.length == 1) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Main.prefix + Main.getMessage("no_console_command"));
                    } else {
                        Player player = (Player) sender;
                        String playerUUID = player.getUniqueId().toString();
                        FamilyManager playerFam = new FamilyManager(playerUUID);
                        sender.sendMessage(Main.prefix + Main.getMessage("gender_info").replace("%gender%", Main.getGenderLang(playerFam.getGender())));
                    }
                } else {
                    if (!sender.hasPermission("lunaticFamily.admin.gender") && !sender.hasPermission("lunaticFamily.gender.info.others")) {
                        sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                    } else if (!Main.playerExists(args[1])) {
                        sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", args[1]));
                    } else {
                        sender.sendMessage(Main.prefix + Main.getMessage("gender_info_others").replace("%player%", Main.getName(args[1])).replace("%gender%", Main.getGenderLang(args[1])));
                    }
                }
            } else if (args[0].equalsIgnoreCase("help") || Main.getAliases(label, "help").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                String[] subcommandsHelp = {"set", "info"};

                String msg = Main.getMessage(label + "_help") + "\n";

                for (String subcommand : subcommandsHelp) {
                    msg = msg + Main.getMessage(label + "_" + subcommand + "_help") + "\n";
                }
                sender.sendMessage(msg);
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
