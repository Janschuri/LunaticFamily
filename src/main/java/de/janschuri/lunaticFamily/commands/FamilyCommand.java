package de.janschuri.lunaticFamily.commands;

import com.google.common.collect.BiMap;
import de.janschuri.lunaticFamily.utils.FamilyManager;
import de.janschuri.lunaticFamily.Main;
import de.janschuri.lunaticFamily.utils.FamilyTree;
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

    private final Main plugin;

    public FamilyCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 0) {
            if (!sender.hasPermission("lunaticFamily." + label)) {
                sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
            } else {
                List<String> subcommandsHelp = new ArrayList<>();
                if (sender.hasPermission("lunaticFamily.family.tree")) {
                    subcommandsHelp.add("tree");
                }
                if (sender.hasPermission("lunaticFamily.family.background")) {
                    subcommandsHelp.add("background");
                }

                TextComponent msg = new TextComponent(Main.getMessage(label + "_help") + "\n");

                for (String subcommand : subcommandsHelp) {
                    msg.addExtra(Main.prefix + " " + Main.getMessage(label + "_" + subcommand + "_help") + "\n");
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
                    TextComponent text = new TextComponent(Main.prefix + " " + Main.getMessage(label + "_" + commandHelp + "_help") + "\n");
                    text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandHelp + " help"));
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Main.getMessage(commandHelp + "_help")).create()));
                    msg.addExtra(text);
                }

                sender.sendMessage(msg);
            }
        } else {
            if (args[0].equalsIgnoreCase("gender") || Main.getAliases("gender").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                GenderCommand genderCommand = new GenderCommand();
                String stringLabel = "gender";
                PluginCommand pluginCommand = plugin.getCommand(stringLabel);
                String[] arrayArgs = new String[args.length - 1];
                System.arraycopy(args, 1, arrayArgs, 0, args.length - 1);
                assert pluginCommand != null;
                genderCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);

            } else if (args[0].equalsIgnoreCase("adopt") || Main.getAliases("adopt").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                AdoptCommand adoptCommand = new AdoptCommand(plugin);
                String stringLabel = "adopt";
                PluginCommand pluginCommand = plugin.getCommand(stringLabel);
                String[] arrayArgs = new String[args.length - 1];
                System.arraycopy(args, 1, arrayArgs, 0, args.length - 1);
                assert pluginCommand != null;
                adoptCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
            } else if (args[0].equalsIgnoreCase("marry") || Main.getAliases("marry").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                MarryCommand marryCommand = new MarryCommand(plugin);
                String stringLabel = "marry";
                PluginCommand pluginCommand = plugin.getCommand(stringLabel);
                String[] arrayArgs = new String[args.length - 1];
                System.arraycopy(args, 1, arrayArgs, 0, args.length - 1);
                assert pluginCommand != null;
                marryCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
            } else if (args[0].equalsIgnoreCase("sibling") || Main.getAliases("sibling").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                SiblingCommand siblingCommand = new SiblingCommand(plugin);
                String stringLabel = "sibling";
                PluginCommand pluginCommand = plugin.getCommand(stringLabel);
                String[] arrayArgs = new String[args.length - 1];
                System.arraycopy(args, 1, arrayArgs, 0, args.length - 1);
                assert pluginCommand != null;
                siblingCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
            } else if (args[0].equalsIgnoreCase("reload") || Main.getAliases("family", "reload").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                if (!sender.hasPermission("lunaticFamily.admin.reload")) {
                    sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                } else {
                    plugin.loadConfig(plugin);
                    sender.sendMessage(Main.prefix + Main.getMessage("admin_reload"));
                }
            } else if (args[0].equalsIgnoreCase("list") || Main.getAliases("family", "list").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {



                List<String> list = Main.familyList;

                if (!(sender instanceof Player) && args.length < 2) {
                    sender.sendMessage(Main.prefix + Main.getMessage("no_console_command"));
                } else if (args.length == 1) {
                    Player player = (Player) sender;
                    String uuid = player.getUniqueId().toString();
                    FamilyManager playerFam = new FamilyManager(uuid);

                    BiMap<String, Integer> familyList = playerFam.getFamilyList();
                    String msg = Main.prefix + Main.getMessage("family_list") + "\n";

                    for (String e : list) {
                        if (familyList.containsKey(e)) {
                            int relationID = familyList.get(e);
                            FamilyManager relationFam = new FamilyManager(relationID);
                            String relationKey = e.replace("first_", "")
                                    .replace("second_", "")
                                    .replace("third_", "")
                                    .replace("fourth_", "")
                                    .replace("fifth_", "")
                                    .replace("sixth_", "")
                                    .replace("seventh_", "")
                                    .replace("eighth_", "");
                            msg = msg + Main.getRelation(relationKey, relationFam.getGender()) + ": " + relationFam.getName() + "\n";
                        }
                    }
                    sender.sendMessage(msg);
                } else {
                    if (!sender.hasPermission("lunaticFamily.family.listothers")) {
                        sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                    } else if (!Main.playerExists(args[1])) {
                        sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", args[1]));
                    } else {
                        String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        FamilyManager player1Fam = new FamilyManager(player1);
                        BiMap<String, Integer> familyList = player1Fam.getFamilyList();
                        String msg = Main.prefix + Main.getMessage("family_others_list").replace("%player%", player1Fam.getName()) + "\n";
                        for (String e : list) {
                            if (familyList.containsKey(e)) {
                                int relationID = familyList.get(e);
                                FamilyManager relationFam = new FamilyManager(relationID);
                                String relationKey = e.replace("first_", "")
                                        .replace("second_", "")
                                        .replace("third_", "")
                                        .replace("fourth_", "")
                                        .replace("fifth_", "")
                                        .replace("sixth_", "")
                                        .replace("seventh_", "")
                                        .replace("eighth_", "");
                                msg = msg + Main.getRelation(relationKey, relationFam.getGender()) + ": " + relationFam.getName() + "\n";
                            }
                        }
                        sender.sendMessage(msg);
                    }
                }

            } else if (!(sender instanceof Player)) {
                sender.sendMessage(Main.prefix + Main.getMessage("no_console_command"));
            }
            else{
                Player player = (Player) sender;
                String uuid = player.getUniqueId().toString();
                FamilyManager playerFam = new FamilyManager(uuid);
                    if (args[0].equalsIgnoreCase("tree") || Main.getAliases("family", "tree").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                        if (!player.hasPermission("lunaticFamily.family.tree")) {
                            sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                        } else if (!Main.enabledCrazyAdvancementAPI) {
                            sender.sendMessage(Main.prefix + Main.getMessage("disabled_feature"));
                        } else {
                            new FamilyTree(playerFam.getID());
                            sender.sendMessage(Main.getMessage("tree_loaded"));
                        }
                    } else if (args[0].equalsIgnoreCase("background") || Main.getAliases("family", "background").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                        if (!player.hasPermission("lunaticFamily.family.background")) {
                            sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                        } else if (args.length == 2) {
                            playerFam.setBackground(args[1]);
                            sender.sendMessage(Main.prefix + Main.getMessage("family_background_set"));
                        } else {
                            sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                        }
                    } else if (args[0].equalsIgnoreCase("help") || Main.getAliases(label, "help").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                        if (!player.hasPermission("lunaticFamily.family.tree")) {
                            sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                        } else {
                            String[] subcommandsHelp = {"list", "tree", "background"};
                            String msg = Main.getMessage(label + "_help") + "\n";
                            for (String subcommand : subcommandsHelp) {
                                msg = msg + Main.getMessage(label + "_" + subcommand + "_help") + "\n";
                            }
                            sender.sendMessage(msg);
                        }
                    } else {
                        sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                    }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {

        List<String> familySubcommands = Main.familySubcommands;
        List<String> reloadCommands = Main.familyAdminSubcommands;
        List<String> backgrounds = Main.backgrounds;
        List<String> adoptCommands = Main.adoptCommands;
        List<String> adoptSubcommands = Main.adoptSubcommands;
        List<String> adoptAdminSubcommands = Main.adoptAdminSubcommands;
        List<String> genderCommands = Main.genderCommands;
        List<String> genderSubcommands = Main.genderSubcommands;
        List<String> genderAdminSubcommands = Main.genderAdminSubcommands;
        List<String> marryCommands = Main.marryCommands;
        List<String> marrySubcommands = Main.marrySubcommands;
        List<String> marryPriestSubcommands = Main.marryPriestSubcommands;
        List<String> marryAdminSubcommands = Main.marryAdminSubcommands;
        List<String> siblingCommands = Main.siblingCommands;
        List<String> siblingSubcommands = Main.siblingSubcommands;
        List<String> siblingAdminSubcommands = Main.siblingAdminSubcommands;

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
                } else if (args[0].equalsIgnoreCase("background") || Main.getAliases("background").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
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
                } else if (args[0].equalsIgnoreCase("adopt") || Main.getAliases("adopt").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
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
                } else if (args[0].equalsIgnoreCase("gender") || Main.getAliases("gender").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
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
                } else if (args[0].equalsIgnoreCase("marry") || Main.getAliases("marry").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                    if (player.hasPermission("lunaticFamily.marry") && args.length < 3) {
                        if (args[1].equalsIgnoreCase("")) {
                            list.addAll(marrySubcommands);
                            if (player.hasPermission("lunaticFamily.admin.marry")) {
                                list.addAll(marryAdminSubcommands);
                            }
                            if (player.hasPermission("lunaticFamily.marry.priest")) {
                                list.addAll(marryPriestSubcommands);
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
                            if (player.hasPermission("lunaticFamily.marry.priest")) {
                                for (String s : marryPriestSubcommands) {
                                    if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                        list.add(s);
                                    }
                                }
                            }
                        }
                    }
                    Collections.sort(list);
                    return list;
                } else if (args[0].equalsIgnoreCase("sibling") || Main.getAliases("sibling").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
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
        // return null at the end.
        return null;
    }
}

