package de.janschuri.lunaticfamily.commands;

import de.janschuri.lunaticfamily.Main;
import de.janschuri.lunaticfamily.utils.FamilyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

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

                    String firstParent = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyManager firstParentFam = new FamilyManager(firstParent, plugin);
                    String child = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                    FamilyManager childFam = new FamilyManager(child, plugin);

                    if (firstParentFam.getPartner() != null) {

                        String secondParent = firstParentFam.getPartner();
                        FamilyManager secondParentFam = new FamilyManager(secondParent, plugin);
                        Bukkit.getLogger().info(secondParent);

                        if (childFam.getFirstParent() == null) {

                            if (firstParentFam.getFirstChild() == null) {
                                firstParentFam.setFirstChild(child);
                                secondParentFam.setFirstChild(child);
                                childFam.setFirstParent(firstParent);
                                childFam.setSecondParent(secondParent);

                                if (firstParentFam.getSecondChild() != null) {
                                    String sibling = firstParentFam.getSecondChild();
                                    FamilyManager siblingFam = new FamilyManager(sibling, plugin);
                                    childFam.setSibling(sibling);
                                    siblingFam.setSibling(child);
                                }

                            } else if (firstParentFam.getSecondChild() == null) {
                                firstParentFam.setSecondChild(child);
                                secondParentFam.setSecondChild(child);
                                childFam.setFirstParent(firstParent);
                                childFam.setSecondParent(secondParent);

                                if (firstParentFam.getFirstChild() != null) {
                                    String sibling = firstParentFam.getFirstChild();
                                    FamilyManager siblingFam = new FamilyManager(sibling, plugin);
                                    childFam.setSibling(sibling);
                                    siblingFam.setSibling(child);
                                }
                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_limit").replace("%player%", firstParentFam.getName()));
                            }

                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_set").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));

                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_already_adopted").replace("%child%", childFam.getName()));
                        }
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_not_married").replace("%player%", firstParentFam.getName()));
                    }
                } else if (args[0].equalsIgnoreCase("unset") && args.length == 2) {

                    String child = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyManager childFam = new FamilyManager(child, plugin);

                    if (childFam.getFirstParent() != null) {
                        String firstParent = childFam.getFirstParent();
                        FamilyManager firstParentFam = new FamilyManager(firstParent, plugin);
                        String secondParent = childFam.getSecondParent();
                        FamilyManager secondParentFam = new FamilyManager(secondParent, plugin);

                        if (firstParentFam.getFirstChild().equalsIgnoreCase(child)) {
                            firstParentFam.setFirstChild(null);
                            secondParentFam.setFirstChild(null);
                        } else {
                            firstParentFam.setSecondChild(null);
                            secondParentFam.setSecondChild(null);
                        }

                        if (childFam.getSibling() != null) {
                            String sibling = childFam.getSibling();
                            FamilyManager siblingFam = new FamilyManager(sibling, plugin);
                            siblingFam.setSibling(null);
                        }
                        childFam.setSibling(null);
                        childFam.setFirstParent(null);
                        childFam.setSecondParent(null);


                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_unset").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));

                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_not_adopted").replace("%child%", childFam.getName()));
                    }

                } else {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("no_console_command"));
                }
            } else {
                Player player = (Player) sender;
                if (args[0].equalsIgnoreCase("set") && player.hasPermission("family.admin") && args.length == 3) {

                    String firstParent = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyManager firstParentFam = new FamilyManager(firstParent, plugin);
                    String child = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                    FamilyManager childFam = new FamilyManager(child, plugin);

                    if (firstParentFam.getPartner() != null) {
                        Bukkit.getLogger().info("test1");
                        String secondParent = firstParentFam.getPartner();
                        Bukkit.getLogger().info("test2");
                        FamilyManager secondParentFam = new FamilyManager(secondParent, plugin);
                        Bukkit.getLogger().info("test3");
                        Bukkit.getLogger().info(secondParent);
                        Bukkit.getLogger().info("test4");

                        if (childFam.getFirstParent() == null) {

                            if (firstParentFam.getFirstChild() == null) {
                                firstParentFam.setFirstChild(child);
                                secondParentFam.setFirstChild(child);
                                childFam.setFirstParent(firstParent);
                                childFam.setSecondParent(secondParent);

                                if (firstParentFam.getSecondChild() != null) {
                                    String sibling = firstParentFam.getSecondChild();
                                    FamilyManager siblingFam = new FamilyManager(sibling, plugin);
                                    childFam.setSibling(sibling);
                                    siblingFam.setSibling(child);
                                }

                            } else if (firstParentFam.getSecondChild() == null) {
                                firstParentFam.setSecondChild(child);
                                secondParentFam.setSecondChild(child);
                                childFam.setFirstParent(firstParent);
                                childFam.setSecondParent(secondParent);

                                if (firstParentFam.getFirstChild() != null) {
                                    String sibling = firstParentFam.getFirstChild();
                                    FamilyManager siblingFam = new FamilyManager(sibling, plugin);
                                    childFam.setSibling(sibling);
                                    siblingFam.setSibling(child);
                                }
                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_limit").replace("%player%", firstParentFam.getName()));
                            }

                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_set").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));

                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_already_adopted").replace("%child%", childFam.getName()));
                        }
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_not_married").replace("%player%", firstParentFam.getName()));
                    }
                } else if (args[0].equalsIgnoreCase("unset") && player.hasPermission("family.admin") && args.length == 2) {

                    String child = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyManager childFam = new FamilyManager(child, plugin);

                    if (childFam.getFirstParent() != null) {
                        String firstParent = childFam.getFirstParent();
                        FamilyManager firstParentFam = new FamilyManager(firstParent, plugin);
                        String secondParent = childFam.getSecondParent();
                        FamilyManager secondParentFam = new FamilyManager(secondParent, plugin);

                        if (firstParentFam.getFirstChild().equalsIgnoreCase(child)) {
                            firstParentFam.setFirstChild(null);
                            secondParentFam.setFirstChild(null);
                        } else {
                            firstParentFam.setSecondChild(null);
                            secondParentFam.setSecondChild(null);
                        }

                        if (childFam.getSibling() != null) {
                            String sibling = childFam.getSibling();
                            FamilyManager siblingFam = new FamilyManager(sibling, plugin);
                            siblingFam.setSibling(null);
                        }
                        childFam.setSibling(null);
                        childFam.setFirstParent(null);
                        childFam.setSecondParent(null);


                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_unset").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));

                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_not_adopted").replace("%child%", childFam.getName()));
                    }

                } else {
                    if (player.hasPermission("family.adopt")) {
                        String uuid = player.getUniqueId().toString();
                        FamilyManager playerFam = new FamilyManager(uuid, plugin);

                        if (args[0].equalsIgnoreCase("propose")) {
                            if (args.length > 1) {

                                if (playerFam.getPartner() != null) {
                                    String partner = playerFam.getPartner();
                                    FamilyManager partnerFam = new FamilyManager(partner, plugin);
                                    if (playerFam.getFirstChild() == null || playerFam.getSecondChild() == null) {
                                        //first parameter is online player
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
                                                    Bukkit.getPlayer(UUID.fromString(child)).sendMessage(plugin.prefix + plugin.messages.get("adopt_request").replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()));
                                                    plugin.adoptRequests.put(child, uuid);
                                                    sender.sendMessage(plugin.messages.get("adopt_request_sent").replace("%player%", childFam.getName()));;
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
                            if (plugin.adoptRequests.containsKey(uuid)) {

                                String firstParent = plugin.adoptRequests.get(uuid);
                                Bukkit.getLogger().info(Bukkit.getOfflinePlayer(UUID.fromString(firstParent)).getName());
                                FamilyManager firstParentFam = new FamilyManager(firstParent, plugin);
                                String secondParent = firstParentFam.getPartner();
                                Bukkit.getLogger().info(Bukkit.getOfflinePlayer(UUID.fromString(secondParent)).getName());
                                FamilyManager secondParentFam = new FamilyManager(secondParent, plugin);
                                Bukkit.getLogger().info(playerFam.getName());
                                Bukkit.getLogger().info(playerFam.getFirstParent());
                                Bukkit.getLogger().info(playerFam.getSecondParent());
                                playerFam.setFirstParent(firstParent);
                                Bukkit.getLogger().info(playerFam.getFirstParent());
                                playerFam.setSecondParent(secondParent);
                                Bukkit.getLogger().info(playerFam.getSecondParent());

                                if (firstParentFam.getFirstChild() != null) {
                                    firstParentFam.setSecondChild(uuid);
                                    playerFam.setSibling(firstParentFam.getFirstChild());
                                    String sibling = playerFam.getSibling();
                                    FamilyManager siblingFam = new FamilyManager(sibling, plugin);
                                    siblingFam.setSibling(uuid);
                                    playerFam.setSibling(sibling);

                                } else {
                                    firstParentFam.setFirstChild(uuid);
                                    if (firstParentFam.getSecondChild() != null) {
                                        playerFam.setSibling(firstParentFam.getSecondChild());
                                        String sibling = playerFam.getSibling();
                                        FamilyManager siblingFam = new FamilyManager(sibling, plugin);
                                        siblingFam.setSibling(uuid);
                                        playerFam.setSibling(sibling);
                                    }
                                }

                                if (secondParentFam.getFirstChild() != null) {
                                    secondParentFam.setSecondChild(uuid);
                                } else {
                                    secondParentFam.setFirstChild(uuid);
                                }

                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_got_adopted").replace("%player1%", firstParentFam.getName()).replace("%player2%", secondParentFam.getName()));

                                Bukkit.getPlayer(UUID.fromString(firstParent)).sendMessage(plugin.prefix + plugin.messages.get("adopt_adopted").replace("%player%", playerFam.getName()));

                                if (Bukkit.getPlayer(UUID.fromString(secondParent)) != null) {
                                    Bukkit.getPlayer(UUID.fromString(secondParent)).sendMessage(plugin.prefix + plugin.messages.get("adopt_adopted").replace("%player%", playerFam.getName()));
                                }

                                plugin.adoptRequests.remove(uuid);
                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_no_request"));
                            }
                        } else if (args[0].equalsIgnoreCase("list")) {
                            if (playerFam.getFirstChild() != null || playerFam.getSecondChild() != null) {

                                if (playerFam.getFirstChild() != null ^ playerFam.getSecondChild() != null) {

                                    String child;

                                    if (playerFam.getFirstChild() == null) {
                                        child = playerFam.getSecondChild();
                                    } else {
                                        child = playerFam.getFirstChild();
                                    }

                                    FamilyManager childFam = new FamilyManager(child, plugin);


                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_one_child").replace("%player%", childFam.getName()));
                                }

                                if (playerFam.getFirstChild() != null && playerFam.getSecondChild() != null) {

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
                            if (plugin.adoptRequests.containsKey(uuid)) {

                                String firstParent = plugin.adoptRequests.get(uuid);


                                Bukkit.getPlayer(UUID.fromString(firstParent)).sendMessage(plugin.prefix + plugin.messages.get("adopt_deny").replace("%player%", playerFam.getName()));


                                plugin.marryRequests.remove(uuid);
                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("marry_no_request"));
                            }
                        } else if (args[0].equalsIgnoreCase("moveout")) {

                            if (playerFam.getFirstParent() != null) {
                                String firstParent = playerFam.getFirstParent();
                                FamilyManager firstParentFam = new FamilyManager(firstParent, plugin);
                                String secondParent = playerFam.getSecondParent();
                                FamilyManager secondParentFam = new FamilyManager(secondParent, plugin);

                                if (firstParentFam.getFirstChild() != null && firstParentFam.getFirstChild().equalsIgnoreCase(uuid)) {
                                    firstParentFam.setFirstChild(null);
                                }
                                if (firstParentFam.getSecondChild() != null && firstParentFam.getSecondChild().equalsIgnoreCase(uuid)) {
                                    firstParentFam.setSecondChild(null);
                                }
                                if (secondParentFam.getFirstChild() != null && secondParentFam.getFirstChild().equalsIgnoreCase(uuid)) {
                                    secondParentFam.setFirstChild(null);
                                }
                                if (secondParentFam.getSecondChild() != null && secondParentFam.getSecondChild().equalsIgnoreCase(uuid)) {
                                    secondParentFam.setSecondChild(null);
                                }

                                if (playerFam.getSibling() != null) {
                                    String sibling = playerFam.getSibling();
                                    FamilyManager siblingFam = new FamilyManager(sibling, plugin);
                                    if (Bukkit.getPlayer(UUID.fromString(sibling)) != null) {
                                        Bukkit.getPlayer(UUID.fromString(sibling)).sendMessage(plugin.prefix + plugin.messages.get("adopt_sibling_move_out"));
                                    }
                                    playerFam.setSibling(null);
                                    siblingFam.setSibling(null);
                                }

                                playerFam.setFirstParent(null);
                                playerFam.setSecondParent(null);

                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_move_out"));

                                if (Bukkit.getPlayer(UUID.fromString(firstParent)) != null) {
                                    Bukkit.getPlayer(UUID.fromString(firstParent)).sendMessage(plugin.prefix + plugin.messages.get("adopt_child_move_out").replace("%player%", playerFam.getName()));
                                }
                                if (Bukkit.getPlayer(UUID.fromString(secondParent)) != null) {
                                    Bukkit.getPlayer(UUID.fromString(secondParent)).sendMessage(plugin.prefix + plugin.messages.get("adopt_child_move_out").replace("%player%", playerFam.getName()));
                                }


                            }

                            //player has no partner
                            else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_no_parents"));
                            }
                        } else if (args[0].equalsIgnoreCase("kickout")) {
                            if (playerFam.getFirstChild() != null || playerFam.getSecondChild() != null) {
                                if (args.length == 1) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_specify_child"));
                                } else {

                                    String partner = playerFam.getPartner();
                                    FamilyManager partnerFam = new FamilyManager(partner, plugin);
                                    if (playerFam.getFirstChild() != null && Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString().equalsIgnoreCase(playerFam.getFirstChild())) {
                                        String firstChild = playerFam.getFirstChild();
                                        FamilyManager firstChildFam = new FamilyManager(firstChild, plugin);

                                        sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_kicked_out").replace("%player%", firstChildFam.getName()));
                                        if (Bukkit.getPlayer(UUID.fromString(partner)) != null) {
                                            Bukkit.getPlayer(UUID.fromString(partner)).sendMessage(plugin.prefix + plugin.messages.get("adopt_partner_kicked_out").replace("%player1%", playerFam.getName()).replace("%player2%", firstChildFam.getName()));
                                        }

                                        if (playerFam.getSecondChild() != null) {
                                            String secondChild = playerFam.getSecondChild();
                                            FamilyManager secondChildFam = new FamilyManager(secondChild, plugin);
                                            if (Bukkit.getPlayer(UUID.fromString(secondChild)) != null) {
                                                Bukkit.getPlayer(UUID.fromString(secondChild)).sendMessage(plugin.prefix + plugin.messages.get("adopt_sibling_kicked_out").replace("%player%", playerFam.getName()));
                                            }
                                            secondChildFam.setSibling(null);
                                        }
                                        firstChildFam.setFirstParent(null);
                                        firstChildFam.setSecondParent(null);
                                        playerFam.setFirstChild(null);

                                        if (partnerFam.getFirstChild() != null && partnerFam.getFirstChild().equalsIgnoreCase(firstChild)) {
                                            partnerFam.setFirstChild(null);
                                        } else if (partnerFam.getSecondChild() != null && partnerFam.getSecondChild().equalsIgnoreCase(firstChild)) {
                                            partnerFam.setSecondChild(null);
                                        }

                                    } else if (playerFam.getSecondChild() != null && Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString().equalsIgnoreCase(playerFam.getSecondChild())) {
                                        String secondChild = playerFam.getSecondChild();
                                        FamilyManager secondChildFam = new FamilyManager(secondChild, plugin);

                                        sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_kicked_out").replace("%player%", secondChildFam.getName()));
                                        if (Bukkit.getPlayer(UUID.fromString(partner)) != null) {
                                            Bukkit.getPlayer(UUID.fromString(partner)).sendMessage(plugin.prefix + plugin.messages.get("adopt_partner_kicked_out").replace("%player1%", playerFam.getName()).replace("%player2%", secondChildFam.getName()));
                                        }

                                        if (playerFam.getFirstChild() != null) {
                                            String firstChild = playerFam.getFirstChild();
                                            FamilyManager firstChildFam = new FamilyManager(firstChild, plugin);
                                            if (Bukkit.getPlayer(UUID.fromString(firstChild)) != null) {
                                                Bukkit.getPlayer(UUID.fromString(firstChild)).sendMessage(plugin.prefix + plugin.messages.get("adopt_sibling_kicked_out").replace("%player%", playerFam.getName()));
                                            }
                                            firstChildFam.setSibling(null);
                                        }
                                        secondChildFam.setFirstParent(null);
                                        secondChildFam.setSecondParent(null);
                                        playerFam.setSecondChild(null);

                                        if (partnerFam.getFirstChild().equalsIgnoreCase(secondChild)) {
                                            partnerFam.setFirstChild(null);
                                        }
                                        if (partnerFam.getSecondChild().equalsIgnoreCase(secondChild)) {
                                            partnerFam.setSecondChild(null);
                                        }

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
