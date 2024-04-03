package de.janschuri.lunaticFamily.commands;

import com.google.common.collect.BiMap;
import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.handler.FamilyTree;
import de.janschuri.lunaticFamily.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FamilyCommand implements CommandExecutor, TabCompleter {

    private final LunaticFamily plugin;

    public FamilyCommand(LunaticFamily plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 0) {
            if (!sender.hasPermission("lunaticFamily." + label)) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
            } else {
                List<String> subcommandsHelp = new ArrayList<>();
                if (sender.hasPermission("lunaticFamily.family.background")) {
                    subcommandsHelp.add("background");
                }

                TextComponent msg = new TextComponent(Language.getMessage(label + "_help") + "\n");

                for (String subcommand : subcommandsHelp) {
                    msg.addExtra(Language.prefix + " " + Language.getMessage(label + "_" + subcommand + "_help") + "\n");
                }

                List<String> commandsHelp = new ArrayList<>();
                if (sender.hasPermission("lunaticFamily.adopt")) {
                    commandsHelp.add("adopt");
                }
                if (sender.hasPermission("lunaticFamily.marry")) {
                    commandsHelp.add("marry");
                }
                if (sender.hasPermission("lunaticFamily.gender")) {
                    commandsHelp.add("gender");
                }
                if (sender.hasPermission("lunaticFamily.sibling")) {
                    commandsHelp.add("sibling");
                }

                for (String commandHelp : commandsHelp) {
                    TextComponent text = new TextComponent(Language.prefix + " " + Language.getMessage(label + "_" + commandHelp + "_help") + "\n");
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandHelp + " help"));
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Language.getMessage(commandHelp + "_help")).create()));
                    msg.addExtra(text);
                }

                sender.sendMessage(msg);
            }
        } else {
            final String subcommand = args[0];
            if (checkIsSubcommand("gender", subcommand)) {
                GenderCommand genderCommand = new GenderCommand();
                String stringLabel = "gender";
                PluginCommand pluginCommand = plugin.getCommand(stringLabel);
                String[] arrayArgs = new String[args.length - 1];
                System.arraycopy(args, 1, arrayArgs, 0, args.length - 1);
                assert pluginCommand != null;
                genderCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);

            } else if (checkIsSubcommand("adopt", subcommand)) {
                AdoptCommand adoptCommand = new AdoptCommand(plugin);
                String stringLabel = "adopt";
                PluginCommand pluginCommand = plugin.getCommand(stringLabel);
                String[] arrayArgs = new String[args.length - 1];
                System.arraycopy(args, 1, arrayArgs, 0, args.length - 1);
                assert pluginCommand != null;
                adoptCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
            } else if (checkIsSubcommand("marry", subcommand)) {
                MarryCommand marryCommand = new MarryCommand(plugin);
                String stringLabel = "marry";
                PluginCommand pluginCommand = plugin.getCommand(stringLabel);
                String[] arrayArgs = new String[args.length - 1];
                System.arraycopy(args, 1, arrayArgs, 0, args.length - 1);
                assert pluginCommand != null;
                marryCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
            } else if (checkIsSubcommand("sibling", subcommand)) {
                SiblingCommand siblingCommand = new SiblingCommand(plugin);
                String stringLabel = "sibling";
                PluginCommand pluginCommand = plugin.getCommand(stringLabel);
                String[] arrayArgs = new String[args.length - 1];
                System.arraycopy(args, 1, arrayArgs, 0, args.length - 1);
                assert pluginCommand != null;
                siblingCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
            } else if (checkIsSubcommand("reload", subcommand)) {
                if (!sender.hasPermission("lunaticFamily.admin.reload")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                } else {
                    plugin.loadConfig(plugin);
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_reload"));
                }
            } else if (checkIsSubcommand("list", subcommand)) {


                List<String> list = Config.familyList;

                if (!(sender instanceof Player) && args.length < 2) {
                    sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
                } else if (args.length == 1) {
                    Player player = (Player) sender;
                    String uuid = player.getUniqueId().toString();
                    FamilyPlayer playerFam = new FamilyPlayer(uuid);

                    BiMap<String, Integer> familyList = playerFam.getFamilyList();
                    String msg = Language.prefix + Language.getMessage("family_list") + "\n";

                    for (String e : list) {
                        if (familyList.containsKey(e)) {
                            int relationID = familyList.get(e);
                            FamilyPlayer relationFam = new FamilyPlayer(relationID);
                            String relationKey = e.replace("first_", "")
                                    .replace("second_", "")
                                    .replace("third_", "")
                                    .replace("fourth_", "")
                                    .replace("fifth_", "")
                                    .replace("sixth_", "")
                                    .replace("seventh_", "")
                                    .replace("eighth_", "");
                            msg = msg + Language.getRelation(relationKey, relationFam.getGender()) + ": " + relationFam.getName() + "\n";
                        }
                    }
                    sender.sendMessage(msg);
                } else {
                    if (!sender.hasPermission("lunaticFamily.family.listothers")) {
                        sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                    } else if (!Utils.playerExists(args[1])) {
                        sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                    } else {
                        String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        FamilyPlayer player1Fam = new FamilyPlayer(player1);
                        BiMap<String, Integer> familyList = player1Fam.getFamilyList();
                        String msg = Language.prefix + Language.getMessage("family_others_list").replace("%player%", player1Fam.getName()) + "\n";
                        for (String e : list) {
                            if (familyList.containsKey(e)) {
                                int relationID = familyList.get(e);
                                FamilyPlayer relationFam = new FamilyPlayer(relationID);
                                String relationKey = e.replace("first_", "")
                                        .replace("second_", "")
                                        .replace("third_", "")
                                        .replace("fourth_", "")
                                        .replace("fifth_", "")
                                        .replace("sixth_", "")
                                        .replace("seventh_", "")
                                        .replace("eighth_", "");
                                msg = msg + Language.getRelation(relationKey, relationFam.getGender()) + ": " + relationFam.getName() + "\n";
                            }
                        }
                        sender.sendMessage(msg);
                    }
                }

            } else if (!(sender instanceof Player)) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
            } else {
                Player player = (Player) sender;
                String uuid = player.getUniqueId().toString();
                FamilyPlayer playerFam = new FamilyPlayer(uuid);
                if (checkIsSubcommand("background", subcommand)) {
                    if (!player.hasPermission("lunaticFamily.family.background")) {
                        sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                    } else if (args.length == 2) {
                        playerFam.setBackground(args[1]);
                        sender.sendMessage(Language.prefix + Language.getMessage("family_background_set"));
                    } else {
                        sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                    }
                } else if (checkIsSubcommand("help", subcommand)) {
                        String[] subcommands = {"list", "background"};
                        String msg = Language.getMessage(label + "_help") + "\n";
                        for (String sc : subcommands) {
                            msg = msg + Language.getMessage(label + "_" + sc + "_help") + "\n";
                        }
                        sender.sendMessage(msg);
                } else {
                    sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {

        List<String> familySubcommands = Language.familySubcommandsAliases;
        List<String> reloadCommands = Language.familyAdminSubcommandsAliases;
        List<String> backgrounds = Config.backgrounds;
        List<String> adoptCommands = Language.adoptCommandsAliases;
        List<String> adoptSubcommands = Language.adoptSubcommandsAliases;
        List<String> adoptAdminSubcommands = Language.adoptAdminSubcommandsAliases;
        List<String> genderCommands = Language.genderCommandsAliases;
        List<String> genderSubcommands = Language.genderSubcommandsAliases;
        List<String> genderAdminSubcommands = Language.genderAdminSubcommandsAliases;
        List<String> marryCommands = Language.marryCommandsAliases;
        List<String> marrySubcommands = Language.marrySubcommandsAliases;
        List<String> marryAdminSubcommands = Language.marryAdminSubcommandsAliases;
        List<String> siblingCommands = Language.siblingCommandsAliases;
        List<String> siblingSubcommands = Language.siblingSubcommandsAliases;
        List<String> siblingAdminSubcommands = Language.siblingAdminSubcommandsAliases;

        List<String> list = new ArrayList<>();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("family")) {
                if (args.length == 0) {
                    list.addAll(familySubcommands);
                    if (player.hasPermission("lunaticFamily.admin.reload")) {
                        list.addAll(reloadCommands);
                    }
                    if (player.hasPermission("lunaticFamily.adopt")) {
                        list.addAll(adoptCommands);
                    }
                    if (player.hasPermission("lunaticFamily.gender")) {
                        list.addAll(genderCommands);
                    }
                    if (player.hasPermission("lunaticFamily.marry")) {
                        list.addAll(marryCommands);
                    }
                    if (player.hasPermission("lunaticFamily.sibling")) {
                        list.addAll(siblingCommands);
                    }
                    Collections.sort(list);
                    return list;
                } else if (args.length == 1) {
                    if (player.hasPermission("lunaticFamily.admin.reload")) {
                        for (String s : reloadCommands) {
                            if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                list.add(s);
                            }
                        }
                    }
                    if (player.hasPermission("lunaticFamily.adopt")) {
                        for (String s : adoptCommands) {
                            if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                list.add(s);
                            }
                        }
                    }
                    if (player.hasPermission("lunaticFamily.gender")) {
                        for (String s : genderCommands) {
                            if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                list.add(s);
                            }
                        }
                    }
                    if (player.hasPermission("lunaticFamily.marry")) {
                        for (String s : marryCommands) {
                            if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                list.add(s);
                            }
                        }
                    }
                    if (player.hasPermission("lunaticFamily.sibling")) {
                        for (String s : siblingCommands) {
                            if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                list.add(s);
                            }
                        }
                    }
                    for (String s : familySubcommands) {
                        if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                            list.add(s);
                        }
                    }
                    Collections.sort(list);
                    return list;
                } else {
                    final String subcommand = args[0];
                    if (checkIsSubcommand("background", subcommand)) {
                        if (args.length < 3) {
                            if (args[1].equalsIgnoreCase("")) {
                                list.addAll(backgrounds);
                            } else {
                                for (String s : backgrounds) {
                                    if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                                        list.add(s);
                                    }
                                }
                            }
                        }
                        Collections.sort(list);
                        return list;
                    } else if (checkIsSubcommand("adopt", subcommand)) {
                        if (player.hasPermission("lunaticFamily.adopt") && args.length < 3) {


                            if (args[1].equalsIgnoreCase("")) {
                                list.addAll(adoptSubcommands);
                                if (player.hasPermission("lunaticFamily.admin.adopt")) {
                                    list.addAll(adoptAdminSubcommands);
                                }
                            } else {

                                for (String s : adoptSubcommands) {
                                    if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                                        list.add(s);
                                    }
                                }
                                if (player.hasPermission("lunaticFamily.admin.adopt")) {
                                    for (String s : adoptAdminSubcommands) {
                                        if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                            list.add(s);
                                        }
                                    }
                                }
                            }
                        }
                        Collections.sort(list);
                        return list;
                    } else if (checkIsSubcommand("gender", subcommand)) {
                        if (player.hasPermission("lunaticFamily.gender") && args.length < 3) {
                            if (args[1].equalsIgnoreCase("")) {
                                list.addAll(genderSubcommands);
                                if (player.hasPermission("lunaticFamily.admin.gender")) {
                                    list.addAll(genderAdminSubcommands);
                                }
                            } else {

                                for (String s : genderSubcommands) {
                                    if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                                        list.add(s);
                                    }
                                }
                                if (player.hasPermission("lunaticFamily.admin.gender")) {
                                    for (String s : genderAdminSubcommands) {
                                        if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                            list.add(s);
                                        }
                                    }
                                }
                            }
                        }
                        Collections.sort(list);
                        return list;
                    } else if (checkIsSubcommand("marry", subcommand)) {
                        if (player.hasPermission("lunaticFamily.marry") && args.length < 3) {
                            if (args[1].equalsIgnoreCase("")) {
                                list.addAll(marrySubcommands);
                                if (player.hasPermission("lunaticFamily.admin.marry")) {
                                    list.addAll(marryAdminSubcommands);
                                }
                            } else {

                                for (String s : marrySubcommands) {
                                    if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                                        list.add(s);
                                    }
                                }
                                if (player.hasPermission("lunaticFamily.admin.marry")) {
                                    for (String s : marryAdminSubcommands) {
                                        if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                            list.add(s);
                                        }
                                    }
                                }
                            }
                        }
                        Collections.sort(list);
                        return list;
                    } else if (checkIsSubcommand("sibling", subcommand)) {
                        if (player.hasPermission("lunaticFamily.sibling") && args.length < 3) {
                            if (args[1].equalsIgnoreCase("")) {
                                list.addAll(siblingSubcommands);
                                if (player.hasPermission("lunaticFamily.admin.sibling")) {
                                    list.addAll(siblingAdminSubcommands);
                                }
                            } else {

                                for (String s : siblingSubcommands) {
                                    if (s.toLowerCase().startsWith(args[1].toLowerCase())) {
                                        list.add(s);
                                    }
                                }
                                if (player.hasPermission("lunaticFamily.admin.sibling")) {
                                    for (String s : siblingAdminSubcommands) {
                                        if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                            list.add(s);
                                        }
                                    }
                                }
                            }
                        }
                        Collections.sort(list);
                        return list;
                    }
                }
            }
        }
        return null;
    }

    private boolean checkIsSubcommand(final String subcommand, final String arg) {
        return subcommand.equalsIgnoreCase(arg) || Language.getAliases("family", subcommand).stream().anyMatch(element -> arg.equalsIgnoreCase(element));
    }
}

