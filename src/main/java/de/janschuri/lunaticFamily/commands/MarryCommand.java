package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.Main;
import de.janschuri.lunaticFamily.utils.FamilyManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Location;

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

                    FamilyManager partnerFam = playerFam.getPartner();
                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_already_married").replace("%player%", partnerFam.getName()));

                } else {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_no_partner"));
                }
            }

        } else if (args.length > 0) {
            if (!(sender instanceof Player)) {
                //TODO confirm set if player has to be divorced first
                if (args[0].equalsIgnoreCase("set")) {

                    if (args.length >= 3) {

                        String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        String player2UUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();

                        FamilyManager player2Fam = new FamilyManager(player2UUID, plugin);
                        FamilyManager player1Fam = new FamilyManager(player1UUID, plugin);

                        if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() < 3) {

                            if (player1Fam.isMarried() || player2Fam.isMarried()) {
                                if (args.length >= 4) {
                                    if (args[3].equalsIgnoreCase("force")) {
                                        //cancel marriage player1
                                        if (player1Fam.isMarried()) {
                                            FamilyManager partnerFam = player1Fam.getPartner();
                                            player1Fam.divorce();
                                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_unset_marry").replace("%player1%", player1Fam.getName()).replace("%player2%", partnerFam.getName()));
                                        }
                                        //cancel marriage player2
                                        if (player2Fam.isMarried()) {
                                            FamilyManager partnerFam = player2Fam.getPartner();
                                            player2Fam.divorce();
                                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_unset_marry").replace("%player1%", player2Fam.getName()).replace("%player2%", partnerFam.getName()));
                                        }
                                        plugin.marryRequests.remove(player1UUID);
                                        plugin.marryPriestRequests.remove(player1UUID);
                                        plugin.marryPriest.remove(player1UUID);

                                        plugin.marryRequests.remove(player1UUID);
                                        plugin.marryPriestRequests.remove(player1UUID);
                                        plugin.marryPriest.remove(player1UUID);

                                        player1Fam.marry(player2Fam.getID());


                                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_set_marry").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                                    } else {
                                        sender.sendMessage("already married");
                                    }
                                } else {
                                    sender.sendMessage("already married");
                                }
                            } else {

                                plugin.marryRequests.remove(player1UUID);
                                plugin.marryPriestRequests.remove(player1UUID);
                                plugin.marryPriest.remove(player1UUID);

                                plugin.marryRequests.remove(player1UUID);
                                plugin.marryPriestRequests.remove(player1UUID);
                                plugin.marryPriest.remove(player1UUID);

                                player1Fam.marry(player2Fam.getID());


                                sender.sendMessage(plugin.prefix + plugin.messages.get("admin_set_marry").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                            }
                        } else {
                            int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_too_many_children").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                        }
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                    }

                } else if (args[0].equalsIgnoreCase("unset")) {
                    if (args.length > 1) {

                        String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        FamilyManager player1Fam = new FamilyManager(player1, plugin);

                        if (player1Fam.getPartner() != null) {
                            FamilyManager partnerFam = player1Fam.getPartner();
                            player1Fam.divorce();
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_divorced").replace("%player1%", player1Fam.getName()).replace("%player2%", partnerFam.getName()));

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
                if (args[0].equalsIgnoreCase("set") && player.hasPermission("lunaticFamily.admin.marry")) {

                    if (args.length >= 3) {

                        String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        String player2UUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();

                        FamilyManager player2Fam = new FamilyManager(player2UUID, plugin);
                        FamilyManager player1Fam = new FamilyManager(player1UUID, plugin);

                        if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() < 3) {

                            if (player1Fam.isMarried() || player2Fam.isMarried()) {
                                if (args.length >= 4) {
                                    if (args[3].equalsIgnoreCase("force")) {
                                        //cancel marriage player1
                                        if (player1Fam.isMarried()) {
                                            FamilyManager partnerFam = player1Fam.getPartner();
                                            player1Fam.divorce();
                                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_unset_marry").replace("%player1%", player1Fam.getName()).replace("%player2%", partnerFam.getName()));
                                        }
                                        //cancel marriage player2
                                        if (player2Fam.isMarried()) {
                                            FamilyManager partnerFam = player2Fam.getPartner();
                                            player2Fam.divorce();
                                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_unset_marry").replace("%player1%", player2Fam.getName()).replace("%player2%", partnerFam.getName()));
                                        }
                                        plugin.marryRequests.remove(player1UUID);
                                        plugin.marryPriestRequests.remove(player1UUID);
                                        plugin.marryPriest.remove(player1UUID);

                                        plugin.marryRequests.remove(player1UUID);
                                        plugin.marryPriestRequests.remove(player1UUID);
                                        plugin.marryPriest.remove(player1UUID);

                                        player1Fam.marry(player2Fam.getID());


                                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_set_marry").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                                    } else {
                                        sender.sendMessage("already married");
                                    }
                                } else {
                                    sender.sendMessage("already married");
                                }
                            } else {

                                plugin.marryRequests.remove(player1UUID);
                                plugin.marryPriestRequests.remove(player1UUID);
                                plugin.marryPriest.remove(player1UUID);

                                plugin.marryRequests.remove(player1UUID);
                                plugin.marryPriestRequests.remove(player1UUID);
                                plugin.marryPriest.remove(player1UUID);

                                player1Fam.marry(player2Fam.getID());


                                sender.sendMessage(plugin.prefix + plugin.messages.get("admin_set_marry").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                            }
                        } else {
                            int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_too_many_children").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                        }
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                    }

                } else if (args[0].equalsIgnoreCase("unset") && player.hasPermission("lunaticFamily.admin.marry")) {
                    if (args.length > 1) {

                        String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        FamilyManager player1Fam = new FamilyManager(player1, plugin);

                        if (player1Fam.getPartner() != null) {
                            FamilyManager partnerFam = player1Fam.getPartner();
                            player1Fam.divorce();
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_divorced").replace("%player1%", player1Fam.getName()).replace("%player2%", partnerFam.getName()));

                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_no_partner").replace("%player%", player1Fam.getName()));
                        }
                    } else {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                    }
                } else {
                    if (player.hasPermission("lunaticFamily.marry")) {
                        String playerUUID = player.getUniqueId().toString();
                        FamilyManager playerFam = new FamilyManager(playerUUID, plugin);
                        if (args[0].equalsIgnoreCase("propose")) {
                            if (args.length == 2) {
                                if (Bukkit.getPlayer(args[1]) != null) {
                                    String partnerUUID = Bukkit.getPlayer(args[1]).getUniqueId().toString();
                                    Player partnerPlayer = Bukkit.getPlayer(args[1]);
                                    FamilyManager partnerFam = new FamilyManager(partnerUUID, plugin);
                                    if (playerFam.getID() == partnerFam.getID()) {
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("marry_self_request"));
                                    } else if (playerFam.isFamilyMember(partnerFam.getID())) {
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("marry_family_request").replace("%player%", partnerFam.getName()));
                                    } else if (partnerFam.isFamilyMember(playerFam.getID())) {
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("marry_family_request").replace("%player%", partnerFam.getName()));
                                    } else {
                                        if (playerFam.getPartner() == null) {
                                            if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() < 3) {
                                                //partner has open request
                                                if (plugin.marryRequests.containsKey(partnerUUID) || plugin.marryPriest.containsKey(partnerUUID)) {

                                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_open_request").replace("%player%", partnerFam.getName()));
                                                } else if (partnerFam.getPartner() != null) {
                                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_player_already_married").replace("%player%", partnerFam.getName()));
                                                }

                                                //player has no open request
                                                else {

                                                    if (Main.isInRange(player.getLocation(), partnerPlayer.getLocation(), 5)) {

                                                        partnerPlayer.sendMessage(plugin.prefix + plugin.messages.get("marry_request").replace("%player1%", partnerFam.getName()).replace("%player2%", playerFam.getName()));

                                                        TextComponent yes = new TextComponent(ChatColor.GREEN + "[" + plugin.messages.get("marry_yes") + "]");
                                                        yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/marry accept"));
                                                        yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "[" + plugin.messages.get("marry_yes") + "]").create()));

                                                        TextComponent no = new TextComponent(ChatColor.RED + "[" + plugin.messages.get("marry_no") + "]");
                                                        no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/marry deny"));
                                                        no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "[" + plugin.messages.get("marry_no") + "]").create()));

                                                        TextComponent space = new TextComponent(ChatColor.WHITE + "---");

                                                        partnerPlayer.sendMessage(yes, space, no);


                                                        plugin.marryRequests.put(partnerUUID, playerUUID);

                                                        sender.sendMessage(plugin.prefix + plugin.messages.get("marry_request_sent").replace("%player%", partnerFam.getName()));

                                                        new BukkitRunnable() {
                                                            public void run() {
                                                                if (plugin.marryRequests.containsKey(partnerUUID)) {
                                                                    plugin.marryRequests.remove(partnerUUID);
                                                                    partnerPlayer.sendMessage(plugin.prefix + plugin.messages.get("marry_request_expired").replace("%player%", playerFam.getName()));

                                                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_request_sent_expired").replace("%player%", partnerFam.getName()));
                                                                }
                                                            }
                                                        }.runTaskLater(plugin, 600L);
                                                    } else {
                                                        sender.sendMessage(plugin.prefix + plugin.messages.get("player_too_far_away").replace("%player%", partnerFam.getName()));
                                                    }
                                                }
                                            } else {
                                                int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                                                sender.sendMessage(plugin.prefix + plugin.messages.get("marry_too_many_children").replace("%partner%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                                            }
                                        } else {
                                            sender.sendMessage(plugin.prefix + plugin.messages.get("marry_already_married").replace("%player%", partnerFam.getName()));
                                        }
                                    }

                                }
                                //first parameter is not an online player
                                else {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("player_offline").replace("%player%", Bukkit.getOfflinePlayer(args[1]).getName()));
                                }

                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                            }
                        }
                        else if (args[0].equalsIgnoreCase("priest") && args.length > 2) {

                            if (player.hasPermission("lunaticFamily.marry.priest")) {
                                if (plugin.marryPriest.containsValue(playerUUID)) {
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

                                            if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() < 3) {
                                                //send request to player1
                                                player.chat(plugin.messages.get("marry_request").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                                                plugin.marryPriestRequests.put(player1, player2);
                                                plugin.marryPriest.put(player1, playerUUID);
                                            } else {
                                                int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                                                sender.sendMessage(plugin.prefix + plugin.messages.get("marry_priest_too_many_children").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                                            }

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
                        }
                        //player subcommand "accept"
                        else if (args[0].equalsIgnoreCase("accept")) {

                            //check for request
                            if (plugin.marryRequests.containsKey(playerUUID)) {

                                String partnerUUID = plugin.marryRequests.get(playerUUID);
                                FamilyManager partnerFam = new FamilyManager(partnerUUID, plugin);

                                if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() < 3) {

                                    if (Bukkit.getPlayer(UUID.fromString(partnerUUID)) != null) {

                                        if (plugin.marryPriest.containsKey(partnerUUID)) {
                                            String priest = plugin.marryPriest.get(partnerUUID);

                                            player.chat(plugin.messages.get("marry_yes"));

                                            new BukkitRunnable() {
                                                public void run() {
                                                    Bukkit.getPlayer(UUID.fromString(priest)).chat(plugin.messages.get("marry_complete"));
                                                }
                                            }.runTaskLater(plugin, 20L);

                                            plugin.marryPriest.remove(partnerUUID);
                                        } else {
                                            sender.sendMessage(plugin.prefix + plugin.messages.get("marry_complete"));
                                            Bukkit.getPlayer(UUID.fromString(partnerUUID)).sendMessage(plugin.prefix + plugin.messages.get("marry_complete"));
                                        }

                                        plugin.marryRequests.remove(playerUUID);
                                        plugin.marryPriestRequests.remove(playerUUID);
                                        plugin.marryPriest.remove(playerUUID);

                                        plugin.marryRequests.remove(partnerUUID);
                                        plugin.marryPriestRequests.remove(partnerUUID);
                                        plugin.marryPriest.remove(partnerUUID);

                                        playerFam.marry(partnerFam.getID());
                                    } else {
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("player_offline").replace("%player%", partnerFam.getName()));
                                    }
                                } else {
                                    int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_too_many_children").replace("%partner%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                                }
                            }

                            //check for priest request
                            else if (plugin.marryPriestRequests.containsKey(playerUUID)) {

                                String partner = plugin.marryPriestRequests.get(playerUUID);
                                FamilyManager partnerFam = new FamilyManager(partner, plugin);

                                if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() < 3) {
                                    String priest = plugin.marryPriest.get(playerUUID);

                                    plugin.marryPriestRequests.remove(playerUUID);
                                    plugin.marryRequests.put(partner, playerUUID);
                                    player.chat(plugin.messages.get("marry_yes"));

                                    new BukkitRunnable() {
                                        public void run() {
                                            Bukkit.getPlayer(UUID.fromString(priest)).chat(plugin.messages.get("marry_request").replace("%player1%", partnerFam.getName()).replace("%player2%", playerFam.getName()));
                                        }
                                    }.runTaskLater(plugin, 20L);
                                } else {
                                    int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_too_many_children").replace("%partner%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                                }

                            }

                            //no request
                            else {

                                if (plugin.marryPriestRequests.containsValue(playerUUID)) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_open_request_partner"));
                                } else {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_no_request"));
                                }
                            }
                        } else if (args[0].equalsIgnoreCase("deny")) {
                            if (plugin.marryRequests.containsKey(playerUUID)) {

                                String partner = plugin.marryRequests.get(playerUUID);

                                if (plugin.marryPriest.containsKey(partner)) {
                                    player.chat(plugin.messages.get("marry_no"));
                                    String priest = plugin.marryPriest.get(partner);
                                    Bukkit.getPlayer(UUID.fromString(priest)).chat(plugin.messages.get("marry_cancel"));
                                    plugin.marryPriest.remove(partner);
                                } else {

                                    Bukkit.getPlayer(UUID.fromString(partner)).sendMessage(plugin.prefix + plugin.messages.get("marry_deny").replace("%player%", playerFam.getName()));
                                }

                                plugin.marryRequests.remove(playerUUID);
                            } else if (plugin.marryPriestRequests.containsKey(playerUUID)) {
                                player.chat(plugin.messages.get("marry_no"));
                                String priest = plugin.marryPriest.get(playerUUID);
                                Bukkit.getPlayer(UUID.fromString(priest)).chat(plugin.messages.get("marry_cancel"));
                                plugin.marryPriestRequests.remove(playerUUID);
                                plugin.marryPriest.remove(playerUUID);

                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("marry_no_request"));
                            }
                        }
                        //player subcommand "divorce"
                        else if (args[0].equalsIgnoreCase("divorce")) {
                            //TODO confirm divorce
                            //player has partner
                            if (playerFam.getPartner() != null) {

                                playerFam.divorce();

                                sender.sendMessage(plugin.prefix + plugin.messages.get("marry_divorced"));
                                if (Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) != null) {
                                    Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())).sendMessage(plugin.prefix + plugin.messages.get("marry_divorced"));
                                }


                            }

                            //player has no partner
                            else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("marry_no_partner"));
                            }
                        }
                        else if (args[0].equalsIgnoreCase("kiss")) {
                            if (playerFam.getPartner() != null) {
                                if (Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) != null) {
                                    Player partnerPlayer = Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID()));

                                    if(Main.isInRange(partnerPlayer.getLocation(), player.getLocation(), 3)) {
                                        Location location = Main.getPositionBetweenLocations(player.getLocation(), partnerPlayer.getLocation());
                                        location.setY(location.getY() + 2);

                                        for (int i = 0; i < 6; i++) { // Spawn three clouds
                                            Bukkit.getScheduler().runTaskLater(plugin, () -> Main.spawnParticles(location, Particle.HEART), i * 5L); // Delay between clouds: i * 20 ticks (1 second)
                                        }
                                    }
                                    else {
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("player_too_far_away"));
                                    }
                                }
                                else {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("player_offline").replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(playerFam.getPartner().getUUID())).getName()));
                                }

                            }
                            else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("marry_no_partner"));
                            }

                        }
                        else if (args[0].equalsIgnoreCase("list")) {
                            List<Integer> marryList = Main.getDatabase().getMarryList();
                            String msg = plugin.prefix + "\n";
                            for (Integer e : marryList) {
                                FamilyManager player1Fam = new FamilyManager(e, plugin);
                                FamilyManager player2Fam = new FamilyManager(player1Fam.getPartner().getID(), plugin);

                                msg = msg + player1Fam.getName() + " \u2764 " + player2Fam.getName() + " (" + playerFam.getMarryDate() + ")" + "\n";
                            }
                            sender.sendMessage(msg);
                            Bukkit.getLogger().info(marryList.toString());

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

        List<String> marrySubcommands = plugin.marrySubcommands;
        List<String> marryPriestSubcommands = plugin.marryPriestSubcommands;
        List<String> marryAdminSubcommands = plugin.marryAdminSubcommands;
        List<String> list = new ArrayList<>();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("marry")) {
                if (args.length == 0) {
                    if (player.hasPermission("lunaticFamily.admin.marry")) {
                        list.addAll(marryAdminSubcommands);
                    }
                    if (player.hasPermission("lunaticFamily.marry.priest")) {
                        list.addAll(marryPriestSubcommands);
                    }
                    if (player.hasPermission("lunaticFamily.marry")) {
                        list.addAll(marrySubcommands);
                    }
                    Collections.sort(list);
                    return list;
                } else if (args.length == 1) {
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
                    if (player.hasPermission("lunaticFamily.marry")) {
                        for (String s : marrySubcommands) {
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

