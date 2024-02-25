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
        } else {
            //admin subcommand "set"
            if (args[0].equalsIgnoreCase("set") && sender.hasPermission("lunaticFamily.admin.marry")) {

                if (!sender.hasPermission("lunaticFamily.admin.marry")) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("no_permission"));
                } else if (args.length < 2) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                } else if (args[1].equalsIgnoreCase("deny")) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_set_denied"));
                } else if (args.length < 3) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                } else if (!Main.playerExists(args[1])) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("player_not_exist").replace("%player%", args[1]));
                } else if (!Main.playerExists(args[2])) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("player_not_exist").replace("%player%", args[2]));
                } else if (args[1].equalsIgnoreCase(args[2])) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_set_same_player"));
                }
                else {

                    String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    String player2UUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();

                    FamilyManager player2Fam = new FamilyManager(player2UUID, plugin);
                    FamilyManager player1Fam = new FamilyManager(player1UUID, plugin);

                    Boolean forced = false;

                    if (args.length > 3) {
                        if (args[3].equalsIgnoreCase("force")) {
                            forced = true;
                        }
                    }

                    if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
                        int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_set_too_many_children").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                    } else if ((player1Fam.isMarried() || player2Fam.isMarried()) && !forced) {
                        if (player1Fam.isMarried()) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_set_already_married").replace("%player%", player1Fam.getName()));
                        }
                        if (player2Fam.isMarried()) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_set_already_married").replace("%player%", player2Fam.getName()));
                        }
                        TextComponent confirm = new TextComponent(ChatColor.GREEN + " \u2713");
                        confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/marry set " + args[1] + " " + args[2] + " force"));
                        confirm.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + " \u2713").create()));


                        TextComponent deny = new TextComponent(ChatColor.RED + " \u274C");
                        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/marry set deny"));
                        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + " \u274C").create()));

                        TextComponent prefix = new TextComponent(plugin.prefix);
                        TextComponent msg = new TextComponent(plugin.messages.get("admin_marry_set_confirm"));

                        sender.sendMessage(prefix, msg, confirm, deny);
                    } else {
                        //cancel marriage player1
                        if (player1Fam.isMarried()) {
                            FamilyManager partnerFam = player1Fam.getPartner();
                            player1Fam.divorce();
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_unset_divorced").replace("%player1%", player1Fam.getName()).replace("%player2%", partnerFam.getName()));
                        }
                        //cancel marriage player2
                        if (player2Fam.isMarried()) {
                            FamilyManager partnerFam = player2Fam.getPartner();
                            player2Fam.divorce();
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_unset_divorced").replace("%player1%", player2Fam.getName()).replace("%player2%", partnerFam.getName()));
                        }

                        plugin.marryRequests.remove(player1UUID);
                        plugin.marryPriestRequests.remove(player1UUID);
                        plugin.marryPriest.remove(player1UUID);

                        plugin.marryRequests.remove(player1UUID);
                        plugin.marryPriestRequests.remove(player1UUID);
                        plugin.marryPriest.remove(player1UUID);

                        player1Fam.marry(player2Fam.getID());

                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_set_married").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                    }
                }

            } else if (args[0].equalsIgnoreCase("unset") && sender.hasPermission("lunaticFamily.admin.marry")) {
                if (!sender.hasPermission("lunaticFamily.admin.marry")) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("no_permission"));
                } else if (args.length < 2) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                } else {
                    String player1Name = args[1];

                    if (!Main.playerExists(player1Name)) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("player_not_exist").replace("%player%", Bukkit.getOfflinePlayer(player1Name).getName()));
                    } else {
                        String player1UUID = Bukkit.getOfflinePlayer(player1Name).getUniqueId().toString();
                        FamilyManager player1Fam = new FamilyManager(player1UUID, plugin);

                        if (!player1Fam.isMarried()) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_unset_no_partner").replace("%player%", player1Fam.getName()));
                        } else {
                            FamilyManager partnerFam = player1Fam.getPartner();
                            player1Fam.divorce();
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_marry_unset_divorced").replace("%player1%", player1Fam.getName()).replace("%player2%", partnerFam.getName()));
                        }
                    }
                }
            } else if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.hasPermission("lunaticFamily.marry")) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("no_permission"));
                } else {
                    String playerUUID = player.getUniqueId().toString();
                    FamilyManager playerFam = new FamilyManager(playerUUID, plugin);
                    if (args[0].equalsIgnoreCase("propose")) {
                        if (playerFam.isMarried()) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("marry_propose_already_married").replace("%player%", playerFam.getName()));
                        } else if (args.length < 2) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                        } else {
                            String partnerName = args[1];
                            if (!Main.playerExists(partnerName)) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("player_not_exist").replace("%player%", partnerName));
                            } else if (Bukkit.getPlayer(partnerName) == null) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("player_offline").replace("%player%", Bukkit.getOfflinePlayer(partnerName).getName()));
                            } else {
                                String partnerUUID = Bukkit.getPlayer(partnerName).getUniqueId().toString();
                                partnerName = Bukkit.getPlayer(partnerName).getName();
                                Player partnerPlayer = Bukkit.getPlayer(partnerName);
                                FamilyManager partnerFam = new FamilyManager(partnerUUID, plugin);
                                if (playerFam.getID() == partnerFam.getID()) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_propose_self_request"));
                                } else if (playerFam.isFamilyMember(partnerFam.getID())) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_propose_family_request").replace("%player%", partnerFam.getName()));
                                } else if (partnerFam.isFamilyMember(playerFam.getID())) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_propose_family_request").replace("%player%", partnerFam.getName()));
                                } else if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                                    int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_propose_too_many_children").replace("%partner%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                                } else if (plugin.marryRequests.containsKey(partnerUUID) || plugin.marryPriest.containsKey(partnerUUID)) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_propose_open_request").replace("%player%", partnerFam.getName()));
                                } else if (partnerFam.isMarried()) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_propose_player_already_married").replace("%player%", partnerFam.getName()));
                                } else if (!Main.isInRange(player.getLocation(), partnerPlayer.getLocation(), 5)) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("player_too_far_away").replace("%player%", partnerFam.getName()));
                                } else {
                                    partnerPlayer.sendMessage(plugin.prefix + plugin.messages.get("marry_propose_request").replace("%player1%", partnerFam.getName()).replace("%player2%", playerFam.getName()));

                                    TextComponent yes = new TextComponent(ChatColor.GREEN + "[" + plugin.messages.get("marry_yes") + "]");
                                    yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/marry accept"));
                                    yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "[" + plugin.messages.get("marry_yes") + "]").create()));

                                    TextComponent no = new TextComponent(ChatColor.RED + "[" + plugin.messages.get("marry_no") + "]");
                                    no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/marry deny"));
                                    no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "[" + plugin.messages.get("marry_no") + "]").create()));

                                    TextComponent space = new TextComponent(ChatColor.WHITE + "---");

                                    partnerPlayer.sendMessage(yes, space, no);


                                    plugin.marryRequests.put(partnerUUID, playerUUID);

                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_propose_request_sent").replace("%player%", partnerFam.getName()));

                                    new BukkitRunnable() {
                                        public void run() {
                                            if (plugin.marryRequests.containsKey(partnerUUID)) {
                                                plugin.marryRequests.remove(partnerUUID);
                                                partnerPlayer.sendMessage(plugin.prefix + plugin.messages.get("marry_propose_request_expired").replace("%player%", playerFam.getName()));

                                                sender.sendMessage(plugin.prefix + plugin.messages.get("marry_propose_request_sent_expired").replace("%player%", partnerFam.getName()));
                                            }
                                        }
                                    }.runTaskLater(plugin, 600L);
                                }
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("priest")) {
                        if (!player.hasPermission("lunaticFamily.marry.priest")) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("no_permission"));
                        } else if (args.length < 3) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                        } else if (plugin.marryPriest.containsValue(playerUUID)) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("marry_priest_already_priest"));
                        } else if (args[1].equalsIgnoreCase(player.getName()) || args[2].equalsIgnoreCase(player.getName())) {
                            player.sendMessage(plugin.prefix + plugin.messages.get("marry_priest_self_request"));
                        } else {
                            String player1Name = args[1];
                            String player2Name = args[2];
                            if (!Main.playerExists(player1Name)) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("player_not_exist").replace("%player%", player1Name));
                            } else if (Bukkit.getPlayer(player1Name) == null) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("player_offline").replace("%player%", Bukkit.getOfflinePlayer(player1Name).getName()));
                            } else if (!Main.playerExists(player2Name)) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("player_not_exist").replace("%player%", player2Name));
                            } else if (Bukkit.getPlayer(player2Name) == null) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("player_offline").replace("%player%", Bukkit.getOfflinePlayer(player2Name).getName()));
                            } else {
                                String player1UUID = Bukkit.getPlayer(player1Name).getUniqueId().toString();
                                FamilyManager player1Fam = new FamilyManager(player1UUID, plugin);
                                String player2UUID = Bukkit.getPlayer(player2Name).getUniqueId().toString();
                                FamilyManager player2Fam = new FamilyManager(player2UUID, plugin);

                                if (player1Fam.isMarried()) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_priest_player_already_married").replace("%player%", player1Fam.getName()));
                                } else if (player2Fam.isMarried()) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_priest_player_already_married").replace("%player%", player2Fam.getName()));
                                } else if (plugin.marryRequests.containsKey(player1UUID) || plugin.marryPriest.containsValue(player1UUID)) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_priest_open_request").replace("%player%", player1Fam.getName()));
                                } else if (plugin.marryRequests.containsKey(player2UUID) || plugin.marryPriest.containsValue(player2UUID)) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_priest_open_request").replace("%player%", player2Fam.getName()));
                                } else if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
                                    int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_priest_too_many_children").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                                } else {
                                    player.chat(plugin.messages.get("marry_priest_request").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                                    plugin.marryPriestRequests.put(player1UUID, player2UUID);
                                    plugin.marryPriest.put(player1UUID, playerUUID);
                                }
                            }
                        }
                    }
                    //player subcommand "accept"
                    else if (args[0].equalsIgnoreCase("accept")) {

                        if (!plugin.marryRequests.containsKey(playerUUID) && !plugin.marryPriestRequests.containsKey(playerUUID)) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("marry_accept_no_request"));
                        } else if (plugin.marryPriestRequests.containsValue(playerUUID)) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("marry_accept_open_request_partner"));
                        } else {
                            //check for request
                            if (plugin.marryRequests.containsKey(playerUUID)) {

                                String partnerUUID = plugin.marryRequests.get(playerUUID);
                                FamilyManager partnerFam = new FamilyManager(partnerUUID, plugin);

                                if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                                    int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_accept_too_many_children").replace("%partner%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                                } else if (Bukkit.getPlayer(UUID.fromString(partnerUUID)) == null) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("player_offline").replace("%player%", partnerFam.getName()));
                                }
                                if (plugin.marryPriest.containsKey(partnerUUID)) {
                                    String priestUUID = plugin.marryPriest.get(partnerUUID);

                                    player.chat(plugin.messages.get("marry_accept_yes"));

                                    new BukkitRunnable() {
                                        public void run() {
                                            Bukkit.getPlayer(UUID.fromString(priestUUID)).chat(plugin.messages.get("marry_priest_complete"));
                                        }
                                    }.runTaskLater(plugin, 20L);

                                    plugin.marryPriest.remove(partnerUUID);
                                } else {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_complete"));
                                    Bukkit.getPlayer(UUID.fromString(partnerUUID)).sendMessage(plugin.prefix + plugin.messages.get("marry_accept_complete"));
                                }

                                plugin.marryRequests.remove(playerUUID);
                                plugin.marryPriestRequests.remove(playerUUID);
                                plugin.marryPriest.remove(playerUUID);

                                plugin.marryRequests.remove(partnerUUID);
                                plugin.marryPriestRequests.remove(partnerUUID);
                                plugin.marryPriest.remove(partnerUUID);

                                playerFam.marry(partnerFam.getID());
                            }

                            //check for priest request
                            else if (plugin.marryPriestRequests.containsKey(playerUUID)) {

                                String partnerUUID = plugin.marryPriestRequests.get(playerUUID);
                                FamilyManager partnerFam = new FamilyManager(partnerUUID, plugin);

                                if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                                    int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_accept_too_many_children").replace("%partner%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                                } else {

                                    String priest = plugin.marryPriest.get(playerUUID);

                                    plugin.marryPriestRequests.remove(playerUUID);
                                    plugin.marryRequests.put(partnerUUID, playerUUID);
                                    player.chat(plugin.messages.get("marry_accept_yes"));

                                    new BukkitRunnable() {
                                        public void run() {
                                            Bukkit.getPlayer(UUID.fromString(priest)).chat(plugin.messages.get("marry_accept_request").replace("%player1%", partnerFam.getName()).replace("%player2%", playerFam.getName()));
                                        }
                                    }.runTaskLater(plugin, 20L);
                                }
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("deny")) {

                        if (!plugin.marryRequests.containsKey(playerUUID) && !plugin.marryPriestRequests.containsKey(playerUUID)) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("marry_deny_no_request"));
                        } else {
                            if (plugin.marryRequests.containsKey(playerUUID)) {
                                String partnerUUID = plugin.marryRequests.get(playerUUID);
                                if (!plugin.marryPriest.containsKey(partnerUUID)) {
                                    Bukkit.getPlayer(UUID.fromString(partnerUUID)).sendMessage(plugin.prefix + plugin.messages.get("marry_deny_denied").replace("%player%", playerFam.getName()));
                                } else {
                                    String priestUUID = plugin.marryPriest.get(partnerUUID);
                                    player.chat(plugin.messages.get("marry_deny_no"));
                                    Bukkit.getPlayer(UUID.fromString(priestUUID)).chat(plugin.messages.get("marry_deny_cancel"));
                                    plugin.marryPriest.remove(partnerUUID);
                                }
                                plugin.marryRequests.remove(playerUUID);

                            } else if (plugin.marryPriestRequests.containsKey(playerUUID)) {
                                player.chat(plugin.messages.get("marry_deny_no"));
                                String priest = plugin.marryPriest.get(playerUUID);
                                Bukkit.getPlayer(UUID.fromString(priest)).chat(plugin.messages.get("marry_deny_cancel"));
                                plugin.marryPriestRequests.remove(playerUUID);
                                plugin.marryPriest.remove(playerUUID);
                            }
                        }
                    }
                    //player subcommand "divorce"
                    else if (args[0].equalsIgnoreCase("divorce")) {

                        Boolean confirm = false;

                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("confirm")) {
                                confirm = true;
                            }
                        }

                        if (!playerFam.isMarried()) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("marry_divorce_no_partner"));
                        } else if (!confirm) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("marry_divorce_confirm"));
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("marry_divorce_divorced"));
                            if (Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) != null) {
                                Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())).sendMessage(plugin.prefix + plugin.messages.get("marry_divorced"));
                            }
                            playerFam.divorce();
                        }
                    } else if (args[0].equalsIgnoreCase("kiss")) {

                        if (!playerFam.isMarried()) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("marry_kiss_no_partner"));
                        } else if (Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) != null) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("player_offline").replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(playerFam.getPartner().getUUID())).getName()));
                        } else {
                            Player partnerPlayer = Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID()));

                            if (!Main.isInRange(partnerPlayer.getLocation(), player.getLocation(), 3)) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("player_too_far_away"));
                            } else {
                                Location location = Main.getPositionBetweenLocations(player.getLocation(), partnerPlayer.getLocation());
                                location.setY(location.getY() + 2);

                                for (int i = 0; i < 6; i++) { // Spawn three clouds
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> Main.spawnParticles(location, Particle.HEART), i * 5L); // Delay between clouds: i * 20 ticks (1 second)
                                }
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("list")) {
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
