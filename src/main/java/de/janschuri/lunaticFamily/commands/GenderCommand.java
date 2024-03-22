package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
            if (!sender.hasPermission("lunaticFamily." + label)) {
                sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("no_permission"));
            } else {
                String[] subcommandsHelp = {"set", "info"};

                StringBuilder msg = new StringBuilder(LunaticFamily.prefix + " " + LunaticFamily.getMessage(label + "_help") + "\n");

                for (String subcommand : subcommandsHelp) {
                    msg.append(LunaticFamily.prefix).append(" ").append(LunaticFamily.getMessage(label + "_" + subcommand + "_help")).append("\n");
                }
                sender.sendMessage(msg.toString());
            }
        } else {
            if (args[0].equalsIgnoreCase("set") || LunaticFamily.getAliases("gender", "set").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                if (args.length == 1) {
                    TextComponent msg = new TextComponent(LunaticFamily.prefix + LunaticFamily.getMessage("gender_set") + "\n");

                    for (String gender : LunaticFamily.genders) {
                        TextComponent text = new TextComponent(LunaticFamily.prefix + " - " + LunaticFamily.getGenderLang(gender) + "\n");
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gender set " + gender));
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(LunaticFamily.getMessage("gender_set_hover").replace("%gender%", LunaticFamily.getGenderLang(gender))).create()));
                        msg.addExtra(text);
                    }
                    sender.sendMessage(msg);

                } else if (args.length == 2) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("no_console_command"));
                    } else {
                        Player player = (Player) sender;
                        String uuid = player.getUniqueId().toString();
                        FamilyPlayer playerFam = new FamilyPlayer(uuid);

                        if (!LunaticFamily.genders.contains(args[1].toLowerCase())) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("gender_not_exist"));
                        } else if (playerFam.getGender().equalsIgnoreCase(args[1])) {
                            player.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("gender_already").replace("%gender%", LunaticFamily.getGenderLang(args[1])));
                        } else {
                            playerFam.setGender(args[1].toLowerCase());
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("gender_changed").replace("%gender%", LunaticFamily.getGenderLang(args[1])));
                        }
                    }
                } else {
                    if (!sender.hasPermission("lunaticFamily.admin.gender")) {
                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("no_permission"));
                    } else if (!LunaticFamily.playerExists(args[1])) {
                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("player_not_exist").replace("%player%", args[1]));
                    } else {
                        String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        FamilyPlayer player1Fam = new FamilyPlayer(player1);

                        if (!LunaticFamily.genders.contains(args[2].toLowerCase())) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("gender_not_exist"));
                        } else if (player1Fam.getGender().equalsIgnoreCase(args[2])) {
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("admin_gender_already").replace("%player%", LunaticFamily.getName(args[1])).replace("%gender%", LunaticFamily.getGenderLang(args[2])));
                        } else {
                            player1Fam.setGender(args[2].toLowerCase());
                            sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("admin_gender_changed").replace("%player%", LunaticFamily.getName(args[1])).replace("%gender%", LunaticFamily.getGenderLang(args[2])));
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("info") || LunaticFamily.getAliases("gender", "info").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                if (args.length == 1) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("no_console_command"));
                    } else {
                        Player player = (Player) sender;
                        String playerUUID = player.getUniqueId().toString();
                        FamilyPlayer playerFam = new FamilyPlayer(playerUUID);
                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("gender_info").replace("%gender%", LunaticFamily.getGenderLang(playerFam.getGender())));
                    }
                } else {
                    if (!sender.hasPermission("lunaticFamily.admin.gender") && !sender.hasPermission("lunaticFamily.gender.info.others")) {
                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("no_permission"));
                    } else if (!LunaticFamily.playerExists(args[1])) {
                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("player_not_exist").replace("%player%", args[1]));
                    } else {
                        sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("gender_info_others").replace("%player%", LunaticFamily.getName(args[1])).replace("%gender%", LunaticFamily.getGenderLang(args[1])));
                    }
                }
            } else if (args[0].equalsIgnoreCase("help") || LunaticFamily.getAliases(label, "help").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                String[] subcommandsHelp = {"set", "info"};

                String msg = LunaticFamily.getMessage(label + "_help") + "\n";

                for (String subcommand : subcommandsHelp) {
                    msg = msg + LunaticFamily.getMessage(label + "_" + subcommand + "_help") + "\n";
                }
                sender.sendMessage(msg);
            } else {
                sender.sendMessage(LunaticFamily.prefix + LunaticFamily.getMessage("wrong_usage"));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {

        List<String> genderSubcommands = LunaticFamily.genderSubcommands;
        List<String> genderAdminSubcommands = LunaticFamily.genderAdminSubcommands;
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

    private boolean hasPermissionAdminGender(CommandSender sender) {
        return sender.hasPermission("lunaticFamily.admin.gender");
    }

    private boolean hasPermissionGender(CommandSender sender, String... args) {
        final StringBuilder permission = new StringBuilder("lunaticFamily.gender");
        for(final String arg : args) {
            permission.append(".").append(arg);
        }
        return sender.hasPermission(permission.toString());
    }

}
