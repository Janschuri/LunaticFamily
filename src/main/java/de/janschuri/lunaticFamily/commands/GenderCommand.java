package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
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
                sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
            } else {
                String[] subcommandsHelp = {"set", "info"};

                StringBuilder msg = new StringBuilder(Language.prefix + " " + Language.getMessage(label + "_help") + "\n");

                for (String subcommand : subcommandsHelp) {
                    msg.append(Language.prefix).append(" ").append(Language.getMessage(label + "_" + subcommand + "_help")).append("\n");
                }
                sender.sendMessage(msg.toString());
            }
        } else {
            final String subcommand = args[0];
            if (checkIsSubcommand("set", subcommand)) {
                if (args.length == 1) {
                    TextComponent msg = new TextComponent(Language.prefix + Language.getMessage("gender_set") + "\n");

                    for (String gender : Language.genders) {
                        TextComponent text = new TextComponent(Language.prefix + " - " + Language.getGenderLang(gender) + "\n");
                        text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/gender set " + gender));
                        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Language.getMessage("gender_set_hover").replace("%gender%", Language.getGenderLang(gender))).create()));
                        msg.addExtra(text);
                    }
                    sender.sendMessage(msg);

                } else if (args.length == 2) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
                    } else {
                        Player player = (Player) sender;
                        String uuid = player.getUniqueId().toString();
                        FamilyPlayer playerFam = new FamilyPlayer(uuid);

                        if (!Language.genders.contains(args[1].toLowerCase())) {
                            sender.sendMessage(Language.prefix + Language.getMessage("gender_not_exist"));
                        } else if (playerFam.getGender().equalsIgnoreCase(args[1])) {
                            player.sendMessage(Language.prefix + Language.getMessage("gender_already").replace("%gender%", Language.getGenderLang(args[1])));
                        } else {
                            playerFam.setGender(args[1].toLowerCase());
                            sender.sendMessage(Language.prefix + Language.getMessage("gender_changed").replace("%gender%", Language.getGenderLang(args[1])));
                        }
                    }
                } else {
                    if (!sender.hasPermission("lunaticFamily.admin.gender")) {
                        sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                    } else if (!Utils.playerExists(args[1])) {
                        sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                    } else {
                        String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        FamilyPlayer player1Fam = new FamilyPlayer(player1);

                        if (!Language.genders.contains(args[2].toLowerCase())) {
                            sender.sendMessage(Language.prefix + Language.getMessage("gender_not_exist"));
                        } else if (player1Fam.getGender().equalsIgnoreCase(args[2])) {
                            sender.sendMessage(Language.prefix + Language.getMessage("admin_gender_already").replace("%player%", Utils.getName(args[1])).replace("%gender%", Language.getGenderLang(args[2])));
                        } else {
                            player1Fam.setGender(args[2].toLowerCase());
                            sender.sendMessage(Language.prefix + Language.getMessage("admin_gender_changed").replace("%player%", Utils.getName(args[1])).replace("%gender%", Language.getGenderLang(args[2])));
                        }
                    }
                }
            } else if (checkIsSubcommand("info", subcommand)) {
                if (args.length == 1) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
                    } else {
                        Player player = (Player) sender;
                        String playerUUID = player.getUniqueId().toString();
                        FamilyPlayer playerFam = new FamilyPlayer(playerUUID);
                        sender.sendMessage(Language.prefix + Language.getMessage("gender_info").replace("%gender%", Language.getGenderLang(playerFam.getGender())));
                    }
                } else {
                    if (!sender.hasPermission("lunaticFamily.admin.gender") && !sender.hasPermission("lunaticFamily.gender.info.others")) {
                        sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                    } else if (!Utils.playerExists(args[1])) {
                        sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                    } else {
                        sender.sendMessage(Language.prefix + Language.getMessage("gender_info_others").replace("%player%", Utils.getName(args[1])).replace("%gender%", Language.getGenderLang(args[1])));
                    }
                }
            } else if (checkIsSubcommand("help", subcommand)) {
                String[] subcommandsHelp = {"set", "info"};

                String msg = Language.getMessage(label + "_help") + "\n";

                for (String sc : subcommandsHelp) {
                    msg = msg + Language.getMessage(label + "_" + sc + "_help") + "\n";
                }
                sender.sendMessage(msg);
            } else {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {

        List<String> genderSubcommands = Language.genderSubcommandsAliases;
        List<String> genderAdminSubcommands = Language.genderAdminSubcommandsAliases;
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

    private boolean checkIsSubcommand(final String subcommand, final String arg) {
        return subcommand.equalsIgnoreCase(arg) || Language.getAliases("gender", subcommand).stream().anyMatch(element -> arg.equalsIgnoreCase(element));
    }
}
