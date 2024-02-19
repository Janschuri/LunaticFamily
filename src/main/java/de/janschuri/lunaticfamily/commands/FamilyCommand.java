package de.janschuri.lunaticfamily.commands;

import com.google.common.collect.BiMap;
import de.janschuri.lunaticfamily.utils.FamilyManager;
import de.janschuri.lunaticfamily.Main;
import de.janschuri.lunaticfamily.utils.FamilyTree;
import eu.endercentral.crazy_advancements.CrazyAdvancementsAPI;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class FamilyCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public FamilyCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

            Player player = (Player) sender;
            String uuid = player.getUniqueId().toString();
            FamilyManager playerFam = new FamilyManager(uuid, plugin);

            if (args.length == 0) {


            } else if (args.length > 0) {

                if (args[0].equalsIgnoreCase("gender")) {
                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("fe") || args[1].equalsIgnoreCase("feminine")) {
                            if (playerFam.getGender().equalsIgnoreCase("fe")) {
                                player.sendMessage(plugin.prefix + plugin.messages.get("gender_already_fe"));
                            } else {
                                playerFam.setGender("fe");
                                sender.sendMessage(plugin.prefix + plugin.messages.get("gender_changed_fe"));
                            }
                        } else if (args[1].equalsIgnoreCase("ma") || args[1].equalsIgnoreCase("masculine")) {
                            if (playerFam.getGender().equalsIgnoreCase("ma")) {
                                player.sendMessage(plugin.prefix + plugin.messages.get("gender_already_ma"));
                            } else {
                                playerFam.setGender("ma");
                                sender.sendMessage(plugin.prefix + plugin.messages.get("gender_changed_ma"));
                            }
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                        }
                    }
                    else if (args.length > 2 && player.hasPermission("family.admin.gender")) {

                            String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                            Bukkit.getLogger().info(player1);
                            FamilyManager player1Fam = new FamilyManager(player1, plugin);

                            if (player1Fam.getName() == null) {
                                player1Fam.setName(args[1]);
                            }

                            if (args[2].equalsIgnoreCase("fe") || args[2].equalsIgnoreCase("feminine")) {
                                if (player1Fam.getGender().equalsIgnoreCase("fe")) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("admin_gender_already_fe").replace("%player%", player1Fam.getName()));
                                } else {
                                    player1Fam.setGender("fe");
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("admin_gender_changed_fe").replace("%player%", player1Fam.getName()));
                                }
                            } else if (args[2].equalsIgnoreCase("ma") || args[2].equalsIgnoreCase("masculine")) {
                                if (player1Fam.getGender().equalsIgnoreCase("ma")) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("admin_gender_already_ma").replace("%player%", player1Fam.getName()));
                                } else {
                                    player1Fam.setGender("ma");
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("admin_gender_changed_ma").replace("%player%", player1Fam.getName()));
                                }
                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                            }

                            player1Fam.savePlayerData();

                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                    }
                }

                else if (args[0].equalsIgnoreCase("list")) {

                    List<String> list = plugin.familyList;

                    if (args.length == 1) {

                        BiMap familyList = playerFam.getFamilyList();
                        String msg = plugin.prefix + plugin.messages.get("family_list").replace("%player%", playerFam.getName()) + "\n";

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
                    else if (args.length > 1 && player.hasPermission("family.listothers")) {


                        String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        FamilyManager player1Fam = new FamilyManager(player1, plugin);
                        if (player1Fam.getName() == null) {
                            player1Fam.setName(args[1]);
                        }
                        BiMap familyList = player1Fam.getFamilyList();
                        String msg = plugin.prefix + plugin.messages.get("family_list").replace("%player%", player1Fam.getName()) + "\n";

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
                    else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("no_permission"));
                    }

                }
                else if (args[0].equalsIgnoreCase("tree")) {
                    FamilyTree familyTree = new FamilyTree(uuid, plugin);
                    sender.sendMessage("tree reloaded");
                }

                else if (args[0].equalsIgnoreCase("reload") && player.hasPermission("family.admin")){
                    plugin.loadConfig(plugin);
                    sender.sendMessage(plugin.prefix + plugin.messages.get("admin_reload"));
                }

                else {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                }
            }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}

