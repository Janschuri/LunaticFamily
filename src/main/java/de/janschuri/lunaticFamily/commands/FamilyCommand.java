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
            if (args[0].equalsIgnoreCase("gender")) {
                GenderCommand genderCommand = new GenderCommand(plugin);
                String stringLabel = "gender";
                PluginCommand pluginCommand = plugin.getCommand(stringLabel);
                String[] arrayArgs = new String[args.length - 1];

                // Copy elements starting from the second element of the original array
                for (int i = 1; i < args.length; i++) {
                    arrayArgs[i - 1] = args[i];
                }
                genderCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);

            } else if (args[0].equalsIgnoreCase("adopt")) {
                AdoptCommand adoptCommand = new AdoptCommand(plugin);
                String stringLabel = "adopt";
                PluginCommand pluginCommand = plugin.getCommand(stringLabel);
                String[] arrayArgs = new String[args.length - 1];

                // Copy elements starting from the second element of the original array
                for (int i = 1; i < args.length; i++) {
                    arrayArgs[i - 1] = args[i];
                }

                adoptCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
            } else if (args[0].equalsIgnoreCase("marry")) {
                MarryCommand marryCommand = new MarryCommand(plugin);
                String stringLabel = "marry";
                PluginCommand pluginCommand = plugin.getCommand(stringLabel);
                String[] arrayArgs = new String[args.length - 1];

                // Copy elements starting from the second element of the original array
                for (int i = 1; i < args.length; i++) {
                    arrayArgs[i - 1] = args[i];
                }

                marryCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
            } else if (args[0].equalsIgnoreCase("reload")) {
                plugin.loadConfig(plugin);
                sender.sendMessage(plugin.prefix + plugin.messages.get("admin_reload"));
            } else if (args[0].equalsIgnoreCase("list")) {

                List<String> list = plugin.familyList;

                if (!(sender instanceof Player) && args.length < 2) {

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
                            if (relationFam.getGender().equalsIgnoreCase("fe")) {
                                Bukkit.getLogger().info(relationKey + "fe");
                                msg = msg + plugin.relationshipsFe.get(relationKey) + ": " + relationFam.getName() + "\n";
                                Bukkit.getLogger().info((String) plugin.relationshipsFe.get(relationKey));
                            }
                            if (relationFam.getGender().equalsIgnoreCase("ma")) {
                                Bukkit.getLogger().info(relationKey + "ma");
                                msg = msg + plugin.relationshipsMa.get(relationKey) + ": " + relationFam.getName() + "\n";
                                Bukkit.getLogger().info((String) plugin.relationshipsMa.get(relationKey));
                            }
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
                                String relationUUID = (String) familyList.get(e);
                                FamilyManager relationFam = new FamilyManager(relationUUID, plugin);
                                String relationKey = e.replace("first", "")
                                        .replace("second", "")
                                        .replace("third", "")
                                        .replace("fourth", "")
                                        .replace("fifth", "")
                                        .replace("sixth", "")
                                        .replace("seventh", "")
                                        .replace("eighth", "");
                                if (relationFam.getGender().equalsIgnoreCase("fe")) {
                                    Bukkit.getLogger().info(relationKey + "fe");
                                    msg = msg + plugin.relationshipsFe.get(relationKey) + ": " + relationFam.getName() + "\n";
                                    Bukkit.getLogger().info((String) plugin.relationshipsFe.get(relationKey));
                                }
                                if (relationFam.getGender().equalsIgnoreCase("ma")) {
                                    Bukkit.getLogger().info(relationKey + "ma");
                                    msg = msg + plugin.relationshipsMa.get(relationKey) + ": " + relationFam.getName() + "\n";
                                    Bukkit.getLogger().info((String) plugin.relationshipsMa.get(relationKey));
                                }
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
                    if (args[0].equalsIgnoreCase("tree")) {

                        if (Main.isCrazyAdvancementAPILoaded()) {
                            FamilyTree familyTree = new FamilyTree(playerFam.getID(), plugin);
                            sender.sendMessage(plugin.messages.get("tree_loaded"));
                        } else {
                            sender.sendMessage(plugin.messages.get("internal_error"));
                        }
                    } else if (args[0].equalsIgnoreCase("background")) {
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
        List<String> adoptCommands = plugin.adoptCommands;
        List<String> adoptSubcommands = plugin.adoptSubcommands;
        List<String> adoptAdminSubcommands = plugin.adoptAdminSubcommands;
        List<String> marryCommands = plugin.marryCommands;
        List<String> marrySubcommands = plugin.marrySubcommands;
        List<String> marryPriestSubcommands = plugin.marryPriestSubcommands;
        List<String> marryAdminSubcommands = plugin.marryAdminSubcommands;
        List<String> reloadCommands = plugin.reloadCommands;

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
                    if (player.hasPermission("lunaticFamily.marry")) {
                        list.addAll(marryCommands);
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
                    if (player.hasPermission("lunaticFamily.marry")) {
                        for (String s : marryCommands) {
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
                } else if (args[0].equalsIgnoreCase("adopt")) {
                    if (player.hasPermission("lunaticFamily.adopt")) {


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
                } else if (args[0].equalsIgnoreCase("marry")) {
                    if (player.hasPermission("lunaticFamily.marry")) {
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
                }
            }
        }
        // return null at the end.
        return null;
    }
}

