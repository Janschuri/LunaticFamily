package de.janschuri.lunaticfamily.commands;

import de.janschuri.lunaticfamily.Main;
import de.janschuri.lunaticfamily.utils.FamilyManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MarryCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public MarryCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String uuid = player.getUniqueId().toString();
                FamilyManager playerFam = new FamilyManager(uuid, plugin);
                if (playerFam.getPartner() != null) {

                    String partner = playerFam.getPartner();
                    FamilyManager partnerFam = new FamilyManager(partner, plugin);
                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_already_married").replace("%player%", partnerFam.getName()));

                } else {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_no_partner"));
                }
            }

        } else if (args.length > 0) {
            if (!(sender instanceof Player)) {
                if (args[0].equalsIgnoreCase("set")) {

                    if (args.length == 3) {

                        String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        String player2 = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();

                        //remove requests
                        plugin.marryRequests.remove(player1);
                        plugin.marryRequests.remove(player2);
                        plugin.marryPriestRequests.remove(player1);
                        plugin.marryPriestRequests.remove(player2);

                        FamilyManager player1Fam = new FamilyManager(player1, plugin);
                        if (player1Fam.getName() == null) {
                            player1Fam.setName(args[1]);
                        }
                        FamilyManager player2Fam = new FamilyManager(player2, plugin);
                        if (player2Fam.getName() == null) {
                            player2Fam.setName(args[2]);
                        }
                        //cancel marriage player1
                        if (player1Fam.getPartner() != null) {
                            String partner = player1Fam.getPartner();
                            FamilyManager partnerFam = new FamilyManager(partner, plugin);
                            partnerFam.setPartner(null);
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_unset_marry").replace("%player1%", player1Fam.getName()).replace("%player2%", partnerFam.getName()));
                        }
                        //cancel marriage player2
                        if (player2Fam.getPartner() != null) {
                            String partner = player2Fam.getPartner();
                            FamilyManager partnerFam = new FamilyManager(partner, plugin);
                            partnerFam.setPartner(null);
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_unset_marry").replace("%player1%", player2Fam.getName()).replace("%player2%", partnerFam.getName()));
                        }
                        //remove as parent player1
                        if (player1Fam.getFirstChild() != null) {
                            String firstChild = player1Fam.getFirstChild();
                            FamilyManager firstChildFam = new FamilyManager(firstChild, plugin);
                            firstChildFam.setFirstParent(null);
                            firstChildFam.setSecondParent(null);
                            firstChildFam.setSibling(null);
                            player1Fam.setFirstChild(null);
                        }
                        if (player1Fam.getSecondChild() != null) {
                            String secondChild = player1Fam.getSecondChild();
                            FamilyManager secondChildFam = new FamilyManager(secondChild, plugin);
                            secondChildFam.setFirstParent(null);
                            secondChildFam.setSecondParent(null);
                            secondChildFam.setSibling(null);
                            player1Fam.setSecondChild(null);
                        }

                        //remove as parent player2
                        if (player2Fam.getFirstChild() != null) {
                            String firstChild = player2Fam.getFirstChild();
                            FamilyManager firstChildFam = new FamilyManager(firstChild, plugin);
                            firstChildFam.setFirstParent(null);
                            firstChildFam.setSecondParent(null);
                            firstChildFam.setSibling(null);
                            player2Fam.setFirstChild(null);
                        }
                        if (player2Fam.getSecondChild() != null) {
                            String secondChild = player2Fam.getSecondChild();
                            FamilyManager secondChildFam = new FamilyManager(secondChild, plugin);
                            secondChildFam.setFirstParent(null);
                            secondChildFam.setSecondParent(null);
                            secondChildFam.setSibling(null);
                            player2Fam.setSecondChild(null);
                        }


                        //set marriage
                        Bukkit.getLogger().info(player1 + "+" + player2);
                        player1Fam.setPartner(player2);
                        player2Fam.setPartner(player1);

                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_set_marry").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                    }

                } else if (args[0].equalsIgnoreCase("unset")) {
                    if (args.length > 1) {

                        String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        FamilyManager player1Fam = new FamilyManager(player1, plugin);

                        if (player1Fam.getPartner() != null) {
                            String player2 = player1Fam.getPartner();
                            FamilyManager player2Fam = new FamilyManager(player2, plugin);
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_divorced").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));

                            //remove as parent player1
                            if (player1Fam.getFirstChild() != null) {
                                String firstChild = player1Fam.getFirstChild();
                                FamilyManager firstChildFam = new FamilyManager(firstChild, plugin);
                                firstChildFam.setFirstParent(null);
                                firstChildFam.setSecondParent(null);
                                firstChildFam.setSibling(null);
                                player1Fam.setFirstChild(null);
                            }
                            if (player1Fam.getSecondChild() != null) {
                                String secondChild = player1Fam.getSecondChild();
                                FamilyManager secondChildFam = new FamilyManager(secondChild, plugin);
                                secondChildFam.setFirstParent(null);
                                secondChildFam.setSecondParent(null);
                                secondChildFam.setSibling(null);
                                player1Fam.setSecondChild(null);
                            }

                            //remove as parent player2
                            if (player2Fam.getFirstChild() != null) {
                                String firstChild = player2Fam.getFirstChild();
                                FamilyManager firstChildFam = new FamilyManager(firstChild, plugin);
                                firstChildFam.setFirstParent(null);
                                firstChildFam.setSecondParent(null);
                                firstChildFam.setSibling(null);
                                player2Fam.setFirstChild(null);
                            }
                            if (player2Fam.getSecondChild() != null) {
                                String secondChild = player2Fam.getSecondChild();
                                FamilyManager secondChildFam = new FamilyManager(secondChild, plugin);
                                secondChildFam.setFirstParent(null);
                                secondChildFam.setSecondParent(null);
                                secondChildFam.setSibling(null);
                                player2Fam.setSecondChild(null);
                            }

                            player1Fam.setPartner(null);
                            player2Fam.setPartner(null);

                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_no_partner").replace("%player%", player1Fam.getName()));
                        }
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                    }
                } else {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("no_console_command"));
                }
            } else {
                Player player = (Player) sender;
                //admin subcommand "set"
                if (args[0].equalsIgnoreCase("set") && player.hasPermission("family.admin.marry")) {

                    if (args.length == 3) {

                        String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        String player2 = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();

                        //remove requests
                        plugin.marryRequests.remove(player1);
                        plugin.marryRequests.remove(player2);
                        plugin.marryPriestRequests.remove(player1);
                        plugin.marryPriestRequests.remove(player2);

                        FamilyManager player1Fam = new FamilyManager(player1, plugin);
                        if (player1Fam.getName() == null) {
                            player1Fam.setName(args[1]);
                        }
                        FamilyManager player2Fam = new FamilyManager(player2, plugin);
                        if (player2Fam.getName() == null) {
                            player2Fam.setName(args[2]);
                        }
                        //cancel marriage player1
                        if (player1Fam.getPartner() != null) {
                            String partner = player1Fam.getPartner();
                            FamilyManager partnerFam = new FamilyManager(partner, plugin);
                            partnerFam.setPartner(null);
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_unset_marry").replace("%player1%", player1Fam.getName()).replace("%player2%", partnerFam.getName()));
                        }
                        //cancel marriage player2
                        if (player2Fam.getPartner() != null) {
                            String partner = player2Fam.getPartner();
                            FamilyManager partnerFam = new FamilyManager(partner, plugin);
                            partnerFam.setPartner(null);
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_unset_marry").replace("%player1%", player2Fam.getName()).replace("%player2%", partnerFam.getName()));
                        }
                        //remove as parent player1
                        if (player1Fam.getFirstChild() != null) {
                            String firstChild = player1Fam.getFirstChild();
                            FamilyManager firstChildFam = new FamilyManager(firstChild, plugin);
                            firstChildFam.setFirstParent(null);
                            firstChildFam.setSecondParent(null);
                            firstChildFam.setSibling(null);
                            player1Fam.setFirstChild(null);
                        }
                        if (player1Fam.getSecondChild() != null) {
                            String secondChild = player1Fam.getSecondChild();
                            FamilyManager secondChildFam = new FamilyManager(secondChild, plugin);
                            secondChildFam.setFirstParent(null);
                            secondChildFam.setSecondParent(null);
                            secondChildFam.setSibling(null);
                            player1Fam.setSecondChild(null);
                        }

                        //remove as parent player2
                        if (player2Fam.getFirstChild() != null) {
                            String firstChild = player2Fam.getFirstChild();
                            FamilyManager firstChildFam = new FamilyManager(firstChild, plugin);
                            firstChildFam.setFirstParent(null);
                            firstChildFam.setSecondParent(null);
                            firstChildFam.setSibling(null);
                            player2Fam.setFirstChild(null);
                        }
                        if (player2Fam.getSecondChild() != null) {
                            String secondChild = player2Fam.getSecondChild();
                            FamilyManager secondChildFam = new FamilyManager(secondChild, plugin);
                            secondChildFam.setFirstParent(null);
                            secondChildFam.setSecondParent(null);
                            secondChildFam.setSibling(null);
                            player2Fam.setSecondChild(null);
                        }


                        //set marriage
                        Bukkit.getLogger().info(player1 + "+" + player2);
                        player1Fam.setPartner(player2);
                        player2Fam.setPartner(player1);

                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_set_marry").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                    }

                } else if (args[0].equalsIgnoreCase("unset") && player.hasPermission("family.admin.marry")) {
                    if (args.length > 1) {

                        String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        FamilyManager player1Fam = new FamilyManager(player1, plugin);

                        if (player1Fam.getPartner() != null) {
                            String player2 = player1Fam.getPartner();
                            FamilyManager player2Fam = new FamilyManager(player2, plugin);
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_divorced").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));

                            //remove as parent player1
                            if (player1Fam.getFirstChild() != null) {
                                String firstChild = player1Fam.getFirstChild();
                                FamilyManager firstChildFam = new FamilyManager(firstChild, plugin);
                                firstChildFam.setFirstParent(null);
                                firstChildFam.setSecondParent(null);
                                firstChildFam.setSibling(null);
                                player1Fam.setFirstChild(null);
                            }
                            if (player1Fam.getSecondChild() != null) {
                                String secondChild = player1Fam.getSecondChild();
                                FamilyManager secondChildFam = new FamilyManager(secondChild, plugin);
                                secondChildFam.setFirstParent(null);
                                secondChildFam.setSecondParent(null);
                                secondChildFam.setSibling(null);
                                player1Fam.setSecondChild(null);
                            }

                            //remove as parent player2
                            if (player2Fam.getFirstChild() != null) {
                                String firstChild = player2Fam.getFirstChild();
                                FamilyManager firstChildFam = new FamilyManager(firstChild, plugin);
                                firstChildFam.setFirstParent(null);
                                firstChildFam.setSecondParent(null);
                                firstChildFam.setSibling(null);
                                player2Fam.setFirstChild(null);
                            }
                            if (player2Fam.getSecondChild() != null) {
                                String secondChild = player2Fam.getSecondChild();
                                FamilyManager secondChildFam = new FamilyManager(secondChild, plugin);
                                secondChildFam.setFirstParent(null);
                                secondChildFam.setSecondParent(null);
                                secondChildFam.setSibling(null);
                                player2Fam.setSecondChild(null);
                            }

                            player1Fam.setPartner(null);
                            player2Fam.setPartner(null);

                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_no_partner").replace("%player%", player1Fam.getName()));
                        }
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                    }
                } else {
                    if (player.hasPermission("family.marry")) {

                        String uuid = player.getUniqueId().toString();
                        FamilyManager playerFam = new FamilyManager(uuid, plugin);
                        //player subcommand "propose"
                        if (args[0].equalsIgnoreCase("propose")) {
                            if (args.length == 2) {
                                //first parameter is online player
                                if (Bukkit.getPlayer(args[1]) != null) {

                                    if (args[1].equalsIgnoreCase(player.getName())) {
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("marry_self_request"));
                                    } else if (playerFam.getFamilyList().containsValue(Bukkit.getPlayer(args[1]).getUniqueId().toString())) {
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("marry_family_request").replace("%player%", Bukkit.getPlayer(args[1]).getName()));
                                    } else {
                                        if (playerFam.getPartner() == null) {
                                            String partner = Bukkit.getPlayer(args[1]).getUniqueId().toString();
                                            FamilyManager partnerFam = new FamilyManager(partner, plugin);

                                            //partner has open request
                                            if (plugin.marryRequests.containsKey(partner) || plugin.marryPriest.containsKey(partner)) {

                                                sender.sendMessage(plugin.prefix + plugin.messages.get("marry_open_request").replace("%player%", partnerFam.getName()));
                                            } else if (partnerFam.getPartner() != null) {
                                                sender.sendMessage(plugin.prefix + plugin.messages.get("marry_player_already_married").replace("%player%", partnerFam.getName()));
                                            }

                                            //player has no open request
                                            else {
                                                Bukkit.getPlayer(UUID.fromString(partner)).sendMessage(plugin.prefix + plugin.messages.get("marry_request").replace("%player1%", partnerFam.getName()).replace("%player2", playerFam.getName()));

                                                TextComponent yes = new TextComponent(ChatColor.GREEN + "[" + plugin.messages.get("marry_yes") + "]");
                                                yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/marry accept"));
                                                yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "[" + plugin.messages.get("marry_yes") + "]").create()));

                                                TextComponent no = new TextComponent(ChatColor.RED + "[" + plugin.messages.get("marry_no") + "]");
                                                no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/marry deny"));
                                                no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "[" + plugin.messages.get("marry_no") + "]").create()));

                                                TextComponent space = new TextComponent(ChatColor.WHITE + "---");

                                                Bukkit.getPlayer(UUID.fromString(partner)).spigot().sendMessage(yes, space, no);


                                                plugin.marryRequests.put(partner, uuid);
                                            }
                                        } else {
                                            String partner = playerFam.getPartner();
                                            FamilyManager partnerFam = new FamilyManager(partner, plugin);
                                            sender.sendMessage(plugin.prefix + plugin.messages.get("marry_already_married").replace("%player%", partnerFam.getName()));
                                        }
                                    }

                                }

                                //first parameter is not an online player
                                else {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("player_offline").replace("%player%", Bukkit.getOfflinePlayer(args[1]).getName()));
                                }

                            } else if (args.length > 2) {

                                if (player.hasPermission("family.marry.priest")) {
                                    if (plugin.marryPriest.containsValue(uuid)) {
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("marry_already_priest"));
                                    } else {
                                        if (args[2].equalsIgnoreCase(player.getName())) {
                                            player.sendMessage(plugin.prefix + plugin.messages.get("marry_self_request"));
                                        }

                                        //second parameter is online player
                                        else if (Bukkit.getPlayer(args[2]) != null) {

                                            String player1 = Bukkit.getPlayer(args[1]).getUniqueId().toString();
                                            FamilyManager player1Fam = new FamilyManager(player1, plugin);
                                            String player2 = Bukkit.getPlayer(args[2]).getUniqueId().toString();
                                            FamilyManager player2Fam = new FamilyManager(player2, plugin);

                                            //player1 or player2 have open request
                                            if (plugin.marryRequests.containsKey(player1) || plugin.marryRequests.containsKey(player2) || plugin.marryPriest.containsValue(player1) || plugin.marryPriest.containsValue(player2)) {
                                                if (plugin.marryRequests.containsKey(player1) || plugin.marryPriest.containsValue(player1)) {
                                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_open_request").replace("%player%", player1Fam.getName()));
                                                }

                                                if (plugin.marryRequests.containsKey(player2) || plugin.marryPriest.containsValue(player2)) {
                                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_open_request").replace("%player%", player2Fam.getName()));
                                                }
                                            }

                                            //player1 or player2 is already married
                                            else if (player1Fam.getPartner() != null || player2Fam.getPartner() != null) {
                                                if (player1Fam.getPartner() != null) {
                                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_player_already_married").replace("%player%", player1Fam.getName()));
                                                }

                                                if (player2Fam.getPartner() != null) {
                                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_player_already_married").replace("%player%", player2Fam.getName()));
                                                }
                                            }

                                            //player1 and player2 have no open request
                                            else {
                                                //send request to player1
                                                player.chat(plugin.messages.get("marry_request").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                                                plugin.marryPriestRequests.put(player1, player2);
                                                plugin.marryPriest.put(player1, uuid);

                                            }

                                        }

                                        //second parameter is not a online player
                                        else {
                                            sender.sendMessage(plugin.prefix + plugin.messages.get("player_offline").replace("%player%", Bukkit.getOfflinePlayer(args[2]).getName()));
                                        }
                                    }
                                } else {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("no_permission"));
                                }
                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                            }
                        }
                        //player subcommand "accept"
                        else if (args[0].equalsIgnoreCase("accept")) {

                            //check for request
                            if (plugin.marryRequests.containsKey(uuid)) {

                                String partner = plugin.marryRequests.get(uuid);
                                playerFam.setPartner(partner);
                                FamilyManager partnerFam = new FamilyManager(partner, plugin);
                                partnerFam.setPartner(uuid);

                                if (plugin.marryPriest.containsKey(partner)) {
                                    String priest = plugin.marryPriest.get(partner);

                                    player.chat(plugin.messages.get("marry_yes"));

                                    new BukkitRunnable() {
                                        public void run() {
                                            Bukkit.getPlayer(UUID.fromString(priest)).chat(plugin.messages.get("marry_complete"));
                                        }
                                    }.runTaskLater(plugin, 20L);

                                    plugin.marryPriest.remove(partner);
                                } else {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_complete"));
                                    Bukkit.getPlayer(UUID.fromString(partner)).sendMessage(plugin.prefix + plugin.messages.get("marry_complete"));
                                }

                                plugin.marryRequests.remove(uuid);
                            }

                            //check for priest request
                            else if (plugin.marryPriestRequests.containsKey(uuid)) {
                                String partner = plugin.marryPriestRequests.get(uuid);
                                FamilyManager partnerFam = new FamilyManager(partner, plugin);

                                String priest = plugin.marryPriest.get(uuid);

                                plugin.marryPriestRequests.remove(uuid);
                                plugin.marryRequests.put(partner, uuid);
                                player.chat(plugin.messages.get("marry_yes"));

                                new BukkitRunnable() {
                                    public void run() {
                                        Bukkit.getPlayer(UUID.fromString(priest)).chat(plugin.messages.get("marry_request").replace("%player1%", partnerFam.getName()).replace("%player2%", playerFam.getName()));
                                    }
                                }.runTaskLater(plugin, 20L);

                            }

                            //no request
                            else {

                                if (plugin.marryPriestRequests.containsValue(uuid)) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_open_request_partner"));
                                } else {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_no_request"));
                                }
                            }
                        } else if (args[0].equalsIgnoreCase("deny")) {
                            if (plugin.marryRequests.containsKey(uuid)) {

                                String partner = plugin.marryRequests.get(uuid);

                                if (plugin.marryPriest.containsKey(partner)) {
                                    player.chat(plugin.messages.get("marry_no"));
                                    String priest = plugin.marryPriest.get(partner);
                                    Bukkit.getPlayer(UUID.fromString(priest)).chat(plugin.messages.get("marry_cancel"));
                                    plugin.marryPriest.remove(partner);
                                } else {

                                    Bukkit.getPlayer(UUID.fromString(partner)).sendMessage(plugin.prefix + plugin.messages.get("marry_deny").replace("%player%", playerFam.getName()));
                                }

                                plugin.marryRequests.remove(uuid);
                            } else if (plugin.marryPriestRequests.containsKey(uuid)) {
                                player.chat(plugin.messages.get("marry_no"));
                                String priest = plugin.marryPriest.get(uuid);
                                Bukkit.getPlayer(UUID.fromString(priest)).chat(plugin.messages.get("marry_cancel"));
                                plugin.marryPriestRequests.remove(uuid);
                                plugin.marryPriest.remove(uuid);

                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("marry_no_request"));
                            }
                        }
                        //player subcommand "divorce"
                        else if (args[0].equalsIgnoreCase("divorce")) {

                            //player has partner
                            if (playerFam.getPartner() != null) {
                                String partner = playerFam.getPartner();
                                FamilyManager partnerFam = new FamilyManager(partner, plugin);

                                partnerFam.setPartner(null);
                                playerFam.setPartner(null);

                                if (playerFam.getFirstChild() != null) {
                                    String firstChild = playerFam.getFirstChild();
                                    FamilyManager firstChildFam = new FamilyManager(firstChild, plugin);
                                    firstChildFam.setFirstParent(null);
                                    firstChildFam.setSecondParent(null);
                                    firstChildFam.setSibling(null);
                                    playerFam.setFirstParent(null);
                                    partnerFam.setFirstChild(null);
                                }
                                if (playerFam.getSecondChild() != null) {
                                    String secondChild = playerFam.getSecondChild();
                                    FamilyManager secondChildFam = new FamilyManager(secondChild, plugin);
                                    secondChildFam.setFirstParent(null);
                                    secondChildFam.setSecondParent(null);
                                    secondChildFam.setSibling(null);
                                    playerFam.setSecondParent(null);
                                    partnerFam.setSecondChild(null);
                                }

                                sender.sendMessage(plugin.prefix + plugin.messages.get("marry_divorced"));
                                if (Bukkit.getPlayer(UUID.fromString(partner)) != null) {
                                    Bukkit.getPlayer(UUID.fromString(partner)).sendMessage(plugin.prefix + plugin.messages.get("marry_divorced"));
                                }


                            }

                            //player has no partner
                            else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("marry_no_partner"));
                            }
                        }
                        //subcommand does not exist
                        else {
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

        List<String> mainSubcommands = Arrays.asList("propose", "accept", "deny", "divorce");
        List<String> adminSubcommands = Arrays.asList("set", "unset");
        List<String> list = new ArrayList<>();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("marry")) {
                if (args.length == 0) {
                    if (player.hasPermission("family.admin")) {
                        list.addAll(adminSubcommands);
                    }
                    list.addAll(mainSubcommands);
                    Collections.sort(list);
                    return list;
                } else if (args.length == 1) {
                    if (player.hasPermission("family.admin")) {

                        for (String s : adminSubcommands) {
                            if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                list.add(s);
                            }
                        }
                    }
                    for (String s : mainSubcommands) {
                        if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                            list.add(s);
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

