package de.janschuri.lunaticFamily.commands;

import com.google.common.collect.BiMap;
import de.janschuri.lunaticFamily.utils.FamilyManager;
import de.janschuri.lunaticFamily.Main;
import de.janschuri.lunaticFamily.utils.FamilyTree;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FamilyCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public FamilyCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {

        } else {
            if (args[0].equalsIgnoreCase("gender") || plugin.getAliases("gender").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                GenderCommand genderCommand = new GenderCommand(plugin);
                String stringLabel = "gender";
                PluginCommand pluginCommand = plugin.getCommand(stringLabel);
                String[] arrayArgs = new String[args.length - 1];

                // Copy elements starting from the second element of the original array
                for (int i = 1; i < args.length; i++) {
                    arrayArgs[i - 1] = args[i];
                }
                genderCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);

            } else if (args[0].equalsIgnoreCase("adopt") || plugin.getAliases("adopt").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                AdoptCommand adoptCommand = new AdoptCommand(plugin);
                String stringLabel = "adopt";
                PluginCommand pluginCommand = plugin.getCommand(stringLabel);
                String[] arrayArgs = new String[args.length - 1];

                // Copy elements starting from the second element of the original array
                for (int i = 1; i < args.length; i++) {
                    arrayArgs[i - 1] = args[i];
                }

                adoptCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
            } else if (args[0].equalsIgnoreCase("marry") || plugin.getAliases("marry").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                MarryCommand marryCommand = new MarryCommand(plugin);
                String stringLabel = "marry";
                PluginCommand pluginCommand = plugin.getCommand(stringLabel);
                String[] arrayArgs = new String[args.length - 1];

                // Copy elements starting from the second element of the original array
                for (int i = 1; i < args.length; i++) {
                    arrayArgs[i - 1] = args[i];
                }

                marryCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
            } else if (args[0].equalsIgnoreCase("sibling") || plugin.getAliases("sibling").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                SiblingCommand siblingCommand = new SiblingCommand(plugin);
                String stringLabel = "sibling";
                PluginCommand pluginCommand = plugin.getCommand(stringLabel);
                String[] arrayArgs = new String[args.length - 1];

                // Copy elements starting from the second element of the original array
                for (int i = 1; i < args.length; i++) {
                    arrayArgs[i - 1] = args[i];
                }

                siblingCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
            } else if (args[0].equalsIgnoreCase("reload") || plugin.getAliases("family", "reload").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                if (!sender.hasPermission("lunaticFamily.admin.adopt")) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("no_permission"));
                } else {
                    plugin.loadConfig(plugin);
                    sender.sendMessage(plugin.prefix + plugin.messages.get("admin_reload"));
                }
            } else if (args[0].equalsIgnoreCase("list") || plugin.getAliases("family", "list").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

                List<String> list = plugin.familyList;

                if (!(sender instanceof Player) && args.length < 2) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("no_console_command"));
                } else if (args.length == 1) {
                    Player player = (Player) sender;
                    String uuid = player.getUniqueId().toString();
                    FamilyManager playerFam = new FamilyManager(uuid, plugin);

                    BiMap familyList = playerFam.getFamilyList();
                    String msg = plugin.prefix + plugin.messages.get("family_list") + "\n";

                    for (String e : list) {
                        if (familyList.containsKey(e)) {
                            int relationID = (int) familyList.get(e);
                            FamilyManager relationFam = new FamilyManager(relationID, plugin);
                            String relationKey = e.replace("first", "")
                                    .replace("second", "")
                                    .replace("third", "")
                                    .replace("fourth", "")
                                    .replace("fifth", "")
                                    .replace("sixth", "")
                                    .replace("seventh", "")
                                    .replace("eighth", "");
                            msg = msg + plugin.relationships.get(relationFam.getGender()).get(relationKey) + ": " + relationFam.getName() + "\n";
                        }
                    }
                    sender.sendMessage(msg);
                } else {
                    if (!sender.hasPermission("lunaticFamily.family.listothers")) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("no_permission"));
                    } else if (!Main.playerExists(args[1])) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("player_not_exist").replace("%player%", args[1]));
                    } else {
                        String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        FamilyManager player1Fam = new FamilyManager(player1, plugin);
                        BiMap familyList = player1Fam.getFamilyList();
                        String msg = plugin.prefix + plugin.messages.get("family_others_list").replace("%player%", player1Fam.getName()) + "\n";
                        for (String e : list) {
                            if (familyList.containsKey(e)) {
                                int relationID = (int) familyList.get(e);
                                FamilyManager relationFam = new FamilyManager(relationID, plugin);
                                String relationKey = e.replace("first", "")
                                        .replace("second", "")
                                        .replace("third", "")
                                        .replace("fourth", "")
                                        .replace("fifth", "")
                                        .replace("sixth", "")
                                        .replace("seventh", "")
                                        .replace("eighth", "");
                                msg = msg + plugin.relationships.get(relationFam.getGender()).get(relationKey) + ": " + relationFam.getName() + "\n";
                            }
                        }
                        sender.sendMessage(msg);
                    }
                }

            } else if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.prefix + plugin.messages.get("no_console_command"));
            }
            else{
                Player player = (Player) sender;
                String uuid = player.getUniqueId().toString();
                FamilyManager playerFam = new FamilyManager(uuid, plugin);
                    if (args[0].equalsIgnoreCase("tree") || plugin.getAliases("family", "tree").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

                        if (Main.isCrazyAdvancementAPILoaded()) {
                            FamilyTree familyTree = new FamilyTree(playerFam.getID(), plugin);
                            sender.sendMessage(plugin.messages.get("tree_loaded"));
                        } else {
                            sender.sendMessage(plugin.messages.get("internal_error"));
                        }
                    } else if (args[0].equalsIgnoreCase("background") || plugin.getAliases("family", "background").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                        if (args.length == 2) {
                            playerFam.setBackground(args[1]);
                            sender.sendMessage(plugin.prefix + plugin.messages.get("family_background_set"));
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                        }
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                    }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        List<String> familySubcommands = plugin.familySubcommands;
        List<String> reloadCommands = plugin.familyAdminSubcommands;
        List<String> backgrounds = plugin.backgrounds;
        List<String> adoptCommands = plugin.adoptCommands;
        List<String> adoptSubcommands = plugin.adoptSubcommands;
        List<String> adoptAdminSubcommands = plugin.adoptAdminSubcommands;
        List<String> genderCommands = plugin.genderCommands;
        List<String> genderSubcommands = plugin.genderSubcommands;
        List<String> genderAdminSubcommands = plugin.genderAdminSubcommands;
        List<String> marryCommands = plugin.marryCommands;
        List<String> marrySubcommands = plugin.marrySubcommands;
        List<String> marryPriestSubcommands = plugin.marryPriestSubcommands;
        List<String> marryAdminSubcommands = plugin.marryAdminSubcommands;
        List<String> siblingCommands = plugin.siblingCommands;
        List<String> siblingSubcommands = plugin.siblingSubcommands;
        List<String> siblingAdminSubcommands = plugin.siblingAdminSubcommands;

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
                } else if (args[0].equalsIgnoreCase("background") || plugin.getAliases("background").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
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
                } else if (args[0].equalsIgnoreCase("adopt") || plugin.getAliases("adopt").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
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
                } else if (args[0].equalsIgnoreCase("gender") || plugin.getAliases("gender").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
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
                } else if (args[0].equalsIgnoreCase("marry") || plugin.getAliases("marry").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
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
                } else if (args[0].equalsIgnoreCase("sibling") || plugin.getAliases("sibling").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
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

