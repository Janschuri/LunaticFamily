package de.janschuri.lunaticfamily.commands;

import de.janschuri.lunaticfamily.Main;
import de.janschuri.lunaticfamily.utils.FamilyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

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
                                    String secondParent = firstParentFam.getPartner();
                                    FamilyManager secondParentFam = new FamilyManager(secondParent, plugin);
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_set").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                                }

                                plugin.adoptRequests.remove(childUUID);
                                firstParentFam.adopt(childUUID);
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
                        String firstParentUUID = childFam.getFirstParent();
                        FamilyManager firstParentFam = new FamilyManager(firstParentUUID, plugin);

                        firstParentFam.unadopt(childUUID);

                        if (firstParentFam.isMarried()) {
                            String secondParentUUID = firstParentFam.getPartner();
                            FamilyManager secondParentFam = new FamilyManager(secondParentUUID, plugin);
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
                if (args[0].equalsIgnoreCase("set") && player.hasPermission("family.admin") && args.length == 3) {

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
                                    String secondParent = firstParentFam.getPartner();
                                    FamilyManager secondParentFam = new FamilyManager(secondParent, plugin);
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_set").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                                }

                                plugin.adoptRequests.remove(childUUID);
                                firstParentFam.adopt(childUUID);
                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_limit").replace("%player%", firstParentFam.getName()));
                            }
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_already_adopted").replace("%child%", childFam.getName()));
                        }
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_not_married").replace("%player%", firstParentFam.getName()));
                    }
                } else if (args[0].equalsIgnoreCase("unset") && player.hasPermission("family.admin") && args.length == 2) {

                    String childUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyManager childFam = new FamilyManager(childUUID, plugin);

                    if (childFam.getFirstParent() != null) {
                        String firstParentUUID = childFam.getFirstParent();
                        FamilyManager firstParentFam = new FamilyManager(firstParentUUID, plugin);

                        firstParentFam.unadopt(childUUID);


                        if (firstParentFam.isMarried()) {
                            String secondParentUUID = firstParentFam.getPartner();
                            FamilyManager secondParentFam = new FamilyManager(secondParentUUID, plugin);
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_unset").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_by_single_unset").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                        }
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_not_adopted").replace("%child%", childFam.getName()));
                    }

                } else {
                    if (player.hasPermission("family.adopt")) {
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
                                                        String partner = playerFam.getPartner();
                                                        FamilyManager partnerFam = new FamilyManager(partner, plugin);
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
                                                                    String partner = playerFam.getPartner();
                                                                    FamilyManager partnerFam = new FamilyManager(partner, plugin);
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
                                        String secondParent = firstParentFam.getPartner();
                                        FamilyManager secondParentFam = new FamilyManager(secondParent, plugin);
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_got_adopted").replace("%player1%", firstParentFam.getName()).replace("%player2%", secondParentFam.getName()));
                                        if (Bukkit.getPlayer(UUID.fromString(secondParent)) != null) {
                                            Bukkit.getPlayer(UUID.fromString(secondParent)).sendMessage(plugin.prefix + plugin.messages.get("adopt_adopted").replace("%player%", playerFam.getName()));
                                        }
                                    } else {
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_got_adopted_by_single").replace("%player%", firstParentFam.getName()));
                                    }

                                    Bukkit.getPlayer(UUID.fromString(firstParent)).sendMessage(plugin.prefix + plugin.messages.get("adopt_adopted").replace("%player%", playerFam.getName()));


                                    plugin.adoptRequests.remove(playerUUID);

                                    firstParentFam.adopt(playerUUID);
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
                                        child = playerFam.getSecondChild();
                                    } else {
                                        child = playerFam.getFirstChild();
                                    }

                                    FamilyManager childFam = new FamilyManager(child, plugin);


                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_one_child").replace("%player%", childFam.getName()));
                                }

                                if (playerFam.getChildrenAmount() == 2) {

                                    String firstChild = playerFam.getFirstChild();
                                    FamilyManager firstChildFam = new FamilyManager(firstChild, plugin);
                                    String secondChild = playerFam.getSecondChild();
                                    FamilyManager secondChildFam = new FamilyManager(secondChild, plugin);

                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_two_children").replace("%player1%", firstChildFam.getName()).replace("%player2%", secondChildFam.getName()));
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
                                String firstParent = playerFam.getFirstParent();
                                FamilyManager firstParentFam = new FamilyManager(firstParent, plugin);

                                Bukkit.getLogger().info(playerFam.getName() + "->1");
                                Bukkit.getLogger().info(firstParentFam.getName() + "->1");

                                if (playerFam.getSibling() != null) {
                                    String sibling = playerFam.getSibling();
                                    FamilyManager siblingFam = new FamilyManager(sibling, plugin);
                                    if (Bukkit.getPlayer(UUID.fromString(sibling)) != null) {
                                        Bukkit.getPlayer(UUID.fromString(sibling)).sendMessage(plugin.prefix + plugin.messages.get("adopt_sibling_move_out"));
                                    }
                                    Bukkit.getLogger().info(playerFam.getName() + "->2");
                                    Bukkit.getLogger().info(firstParentFam.getName() + "->2");
                                }

                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_move_out"));

                                if (Bukkit.getPlayer(UUID.fromString(firstParent)) != null) {
                                    Bukkit.getPlayer(UUID.fromString(firstParent)).sendMessage(plugin.prefix + plugin.messages.get("adopt_child_move_out").replace("%player%", playerFam.getName()));
                                }
                                if (firstParentFam.isMarried()) {
                                    String secondParent = firstParentFam.getPartner();
                                    FamilyManager secondParentFam = new FamilyManager(secondParent, plugin);
                                    if (Bukkit.getPlayer(UUID.fromString(secondParent)) != null) {
                                        Bukkit.getPlayer(UUID.fromString(secondParent)).sendMessage(plugin.prefix + plugin.messages.get("adopt_child_move_out").replace("%player%", playerFam.getName()));
                                    }
                                }
                                Bukkit.getLogger().info(playerFam.getName() + "->3");
                                Bukkit.getLogger().info(firstParentFam.getName() + "->3");

                                firstParentFam.unadopt(playerUUID);

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
                                    if (childFam.isChildOf(playerUUID)) {


                                        sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_kicked_out").replace("%player%", childFam.getName()));
                                        if (playerFam.isMarried()) {
                                            String partnerUUID = playerFam.getPartner();
                                            if (Bukkit.getPlayer(UUID.fromString(partnerUUID)) != null)
                                            Bukkit.getPlayer(UUID.fromString(partnerUUID)).sendMessage(plugin.prefix + plugin.messages.get("adopt_partner_kicked_out").replace("%player1%", playerFam.getName()).replace("%player2%", childFam.getName()));
                                        }

                                        if (childFam.hasSibling()) {
                                            String sibling = childFam.getSibling();
                                            FamilyManager siblingFam = new FamilyManager(sibling, plugin);
                                            if (Bukkit.getPlayer(UUID.fromString(sibling)) != null) {
                                                Bukkit.getPlayer(UUID.fromString(sibling)).sendMessage(plugin.prefix + plugin.messages.get("adopt_sibling_kicked_out").replace("%player%", playerFam.getName()));
                                            }
                                        }
                                        playerFam.unadopt(childUUID);

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
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
