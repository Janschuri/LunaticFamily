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

public class AdoptCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public AdoptCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {

        } else if (args.length > 0) {

            if (!(sender instanceof Player)) {
                if (args[0].equalsIgnoreCase("set") && args.length == 3) {

                    String firstParentUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyManager firstParentFam = new FamilyManager(firstParentUUID, plugin);
                    String childUUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                    FamilyManager childFam = new FamilyManager(childUUID, plugin);

                    if (firstParentFam.getPartner() != null  || plugin.allowSingleAdopt) {

                        if (childFam.getFirstParent() == null) {

                            if (firstParentFam.getChildrenAmount() < 2) {

                                if (firstParentFam.getPartner() == null) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_by_single_set").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                                } else {
                                    FamilyManager secondParentFam = firstParentFam.getPartner();;
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_set").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                                }

                                plugin.adoptRequests.remove(childUUID);
                                firstParentFam.adopt(childFam.getID());
                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_limit").replace("%player%", firstParentFam.getName()));
                            }
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_already_adopted").replace("%child%", childFam.getName()));
                        }
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_not_married").replace("%player%", firstParentFam.getName()));
                    }
                } else if (args[0].equalsIgnoreCase("unset") && args.length == 2) {

                    String childUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyManager childFam = new FamilyManager(childUUID, plugin);

                    if (childFam.getFirstParent() != null) {
                        FamilyManager firstParentFam = childFam.getFirstParent();

                        firstParentFam.unadopt(childFam.getID());

                        if (firstParentFam.isMarried()) {
                            FamilyManager secondParentFam = firstParentFam.getPartner();
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_unset").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_by_single_unset").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                        }
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_not_adopted").replace("%child%", childFam.getName()));
                    }

                } else {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("no_console_command"));
                }
            } else {
                Player player = (Player) sender;
                if (args[0].equalsIgnoreCase("set") && player.hasPermission("lunaticFamily.admin.adopt") && args.length == 3) {

                    String firstParentUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyManager firstParentFam = new FamilyManager(firstParentUUID, plugin);
                    String childUUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                    FamilyManager childFam = new FamilyManager(childUUID, plugin);

                    if (firstParentFam.getPartner() != null  || plugin.allowSingleAdopt) {

                        if (childFam.getFirstParent() == null) {

                            if (firstParentFam.getChildrenAmount() < 2) {

                                if (firstParentFam.getPartner() == null) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_by_single_set").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                                } else {
                                    FamilyManager secondParentFam = firstParentFam.getPartner();
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_set").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                                }

                                plugin.adoptRequests.remove(childUUID);
                                firstParentFam.adopt(childFam.getID());
                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_limit").replace("%player%", firstParentFam.getName()));
                            }
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_already_adopted").replace("%child%", childFam.getName()));
                        }
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_not_married").replace("%player%", firstParentFam.getName()));
                    }
                } else if (args[0].equalsIgnoreCase("unset") && player.hasPermission("lunaticFamily.family.admin") && args.length == 2) {

                    String childUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyManager childFam = new FamilyManager(childUUID, plugin);

                    if (childFam.getFirstParent() != null) {
                        FamilyManager firstParentFam = childFam.getFirstParent();

                        firstParentFam.unadopt(childFam.getID());


                        if (firstParentFam.isMarried()) {
                            FamilyManager secondParentFam = firstParentFam.getPartner();
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_unset").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_by_single_unset").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                        }
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_not_adopted").replace("%child%", childFam.getName()));
                    }

                } else {
                    if (player.hasPermission("lunaticFamily.adopt")) {
                        String playerUUID = player.getUniqueId().toString();
                        FamilyManager playerFam = new FamilyManager(playerUUID, plugin);

                        if (args[0].equalsIgnoreCase("propose")) {
                            if (args.length > 1) {

                                if ((playerFam.getPartner() != null) || plugin.allowSingleAdopt) {
                                    if (playerFam.getFirstChild() == null || playerFam.getSecondChild() == null) {
                                        if (Bukkit.getPlayer(args[1]) != null) {

                                            if (args[1].equalsIgnoreCase(player.getName())) {
                                                player.sendMessage(plugin.prefix + plugin.messages.get("adopt_self_request"));
                                            } else if (args[1].equalsIgnoreCase(playerFam.getName())) {
                                                player.sendMessage(plugin.prefix + plugin.messages.get("adopt_family_request").replace("%player%", Bukkit.getPlayer(args[1]).getName()));
                                            } else {

                                                String child = Bukkit.getPlayer(args[1]).getUniqueId().toString();
                                                FamilyManager childFam = new FamilyManager(child, plugin);

                                                //child has open request
                                                if (plugin.adoptRequests.containsKey(child)) {

                                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_open_request").replace("%player%", childFam.getName()));
                                                } else if (childFam.getFirstParent() != null) {
                                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_already_adopted").replace("%player%", childFam.getName()));
                                                }

                                                //player has no open request
                                                else {
                                                    if (playerFam.isMarried()) {
                                                        FamilyManager partnerFam = playerFam.getPartner();
                                                        Bukkit.getPlayer(UUID.fromString(child)).sendMessage(plugin.prefix + plugin.messages.get("adopt_request").replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()));
                                                    } else {
                                                        Bukkit.getPlayer(UUID.fromString(child)).sendMessage(plugin.prefix + plugin.messages.get("adopt_by_single_request").replace("%player%", playerFam.getName()));

                                                    }
                                                    plugin.adoptRequests.put(child, playerUUID);
                                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_request_sent").replace("%player%", childFam.getName()));;

                                                    new BukkitRunnable() {
                                                        public void run() {
                                                            if (plugin.adoptRequests.containsKey(child)) {
                                                                plugin.adoptRequests.remove(child);
                                                                if (playerFam.isMarried()) {
                                                                    FamilyManager partnerFam = playerFam.getPartner();
                                                                    Bukkit.getPlayer(UUID.fromString(child)).sendMessage(plugin.prefix + plugin.messages.get("adopt_request_expired").replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()));
                                                                } else {
                                                                    Bukkit.getPlayer(UUID.fromString(child)).sendMessage(plugin.prefix + plugin.messages.get("adopt_by_single_request_expired").replace("%player%", playerFam.getName()));
                                                                }
                                                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_request_sent_expired").replace("%player%", childFam.getName()));
                                                            }
                                                        }
                                                    }.runTaskLater(plugin, 600L);
                                                }
                                            }

                                        } else {
                                            sender.sendMessage(plugin.prefix + plugin.messages.get("player_offline").replace("%player%", args[1]));
                                        }
                                    } else {
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_limit"));
                                    }
                                } else {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_not_married"));
                                }


                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                            }
                        } else if (args[0].equalsIgnoreCase("accept")) {

                            //check for request
                            if (plugin.adoptRequests.containsKey(playerUUID)) {

                                String firstParent = plugin.adoptRequests.get(playerUUID);
                                FamilyManager firstParentFam = new FamilyManager(firstParent, plugin);

                                if (firstParentFam.getChildrenAmount() < 3) {

                                    if (firstParentFam.isMarried()) {
                                        FamilyManager secondParentFam = firstParentFam.getPartner();
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_got_adopted").replace("%player1%", firstParentFam.getName()).replace("%player2%", secondParentFam.getName()));
                                        if (Bukkit.getPlayer(UUID.fromString(secondParentFam.getUUID())) != null) {
                                            Bukkit.getPlayer(UUID.fromString(secondParentFam.getUUID())).sendMessage(plugin.prefix + plugin.messages.get("adopt_adopted").replace("%player%", playerFam.getName()));
                                        }
                                    } else {
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_got_adopted_by_single").replace("%player%", firstParentFam.getName()));
                                    }

                                    Bukkit.getPlayer(UUID.fromString(firstParent)).sendMessage(plugin.prefix + plugin.messages.get("adopt_adopted").replace("%player%", playerFam.getName()));


                                    plugin.adoptRequests.remove(playerUUID);

                                    firstParentFam.adopt(playerFam.getID());
                                } else {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_parent_limit").replace("%player%", firstParentFam.getName()));
                                }
                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_no_request"));
                            }
                        } else if (args[0].equalsIgnoreCase("list")) {
                            if (playerFam.getFirstChild() != null || playerFam.getSecondChild() != null) {

                                if (playerFam.getChildrenAmount() == 1) {

                                    String child;

                                    if (playerFam.getFirstChild() == null) {
                                        child = playerFam.getSecondChild().getName();
                                    } else {
                                        child = playerFam.getFirstChild().getName();
                                    }


                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_one_child").replace("%player%", child));
                                }

                                if (playerFam.getChildrenAmount() == 2) {

                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_two_children").replace("%player1%", playerFam.getFirstChild().getName()).replace("%player2%", playerFam.getSecondChild().getName()));
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_limit"));
                                }
                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_no_child"));
                            }
                        } else if (args[0].equalsIgnoreCase("deny")) {
                            if (plugin.adoptRequests.containsKey(playerUUID)) {

                                String firstParent = plugin.adoptRequests.get(playerUUID);


                                Bukkit.getPlayer(UUID.fromString(firstParent)).sendMessage(plugin.prefix + plugin.messages.get("adopt_deny").replace("%player%", playerFam.getName()));


                                plugin.marryRequests.remove(playerUUID);
                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("marry_no_request"));
                            }
                        } else if (args[0].equalsIgnoreCase("moveout")) {
                            //TODO confirm moveout
                            if (playerFam.isAdopted()) {
                                FamilyManager firstParentFam = playerFam.getFirstParent();

                                Bukkit.getLogger().info(playerFam.getName() + "->1");
                                Bukkit.getLogger().info(firstParentFam.getName() + "->1");

                                if (playerFam.getSibling() != null) {
                                    FamilyManager siblingFam = playerFam.getSibling();
                                    if (Bukkit.getPlayer(UUID.fromString(siblingFam.getUUID())) != null) {
                                        Bukkit.getPlayer(UUID.fromString(siblingFam.getUUID())).sendMessage(plugin.prefix + plugin.messages.get("adopt_sibling_move_out"));
                                    }
                                    Bukkit.getLogger().info(playerFam.getName() + "->2");
                                    Bukkit.getLogger().info(firstParentFam.getName() + "->2");
                                }

                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_move_out"));

                                if (Bukkit.getPlayer(UUID.fromString(firstParentFam.getUUID())) != null) {
                                    Bukkit.getPlayer(UUID.fromString(firstParentFam.getUUID())).sendMessage(plugin.prefix + plugin.messages.get("adopt_child_move_out").replace("%player%", playerFam.getName()));
                                }
                                if (firstParentFam.isMarried()) {
                                    FamilyManager secondParentFam = firstParentFam.getPartner();
                                    if (Bukkit.getPlayer(UUID.fromString(secondParentFam.getUUID())) != null) {
                                        Bukkit.getPlayer(UUID.fromString(secondParentFam.getUUID())).sendMessage(plugin.prefix + plugin.messages.get("adopt_child_move_out").replace("%player%", playerFam.getName()));
                                    }
                                }
                                Bukkit.getLogger().info(playerFam.getName() + "->3");
                                Bukkit.getLogger().info(firstParentFam.getName() + "->3");

                                firstParentFam.unadopt(playerFam.getID());

                                Bukkit.getLogger().info(playerFam.getName() + "->4");
                                Bukkit.getLogger().info(firstParentFam.getName() + "->4");

                            }

                            else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_no_parents"));
                            }
                        } else if (args[0].equalsIgnoreCase("kickout")) {
                            if (playerFam.getFirstChild() != null || playerFam.getSecondChild() != null) {
                                if (args.length == 1) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_specify_child"));
                                } else {

//                                    String partner = playerFam.getPartner();
//                                    FamilyManager partnerFam = new FamilyManager(partner, plugin);
                                    String childUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                                    FamilyManager childFam = new FamilyManager(childUUID, plugin);
                                    if (childFam.isChildOf(playerFam.getID())) {


                                        sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_kicked_out").replace("%player%", childFam.getName()));
                                        if (playerFam.isMarried()) {
                                            if (Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) != null)
                                            Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())).sendMessage(plugin.prefix + plugin.messages.get("adopt_partner_kicked_out").replace("%player1%", playerFam.getName()).replace("%player2%", childFam.getName()));
                                        }

                                        if (childFam.hasSibling()) {
                                            FamilyManager siblingFam = childFam.getSibling();
                                            if (Bukkit.getPlayer(UUID.fromString(siblingFam.getUUID())) != null) {
                                                Bukkit.getPlayer(UUID.fromString(siblingFam.getUUID())).sendMessage(plugin.prefix + plugin.messages.get("adopt_sibling_kicked_out").replace("%player%", playerFam.getName()));
                                            }
                                        }
                                        playerFam.unadopt(childFam.getID());

                                    } else {
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_not_your_child"));
                                    }
                                }
                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_no_child"));
                            }
                        } else {
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
