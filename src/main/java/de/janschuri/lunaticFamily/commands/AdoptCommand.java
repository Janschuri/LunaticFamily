package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.Main;
import de.janschuri.lunaticFamily.utils.FamilyManager;
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

public class AdoptCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public AdoptCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {

        } else {

                if (args[0].equalsIgnoreCase("set") || plugin.getAliases("adopt", "set").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

                    boolean forced = false;

                    if (args.length > 3) {
                        if (args[3].equalsIgnoreCase("force")) {
                            forced = true;
                        }
                    }

                    if (!sender.hasPermission("lunaticFamily.admin.adopt")) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("no_permission"));
                    } else if (args.length < 3) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                    } else if (!Main.playerExists(args[1]) && !forced) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("player_not_exist").replace("%player%", args[1]));
                    } else if (!Main.playerExists(args[2]) && !forced) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("player_not_exist").replace("%player%", args[2]));
                    } else if (args[1].equalsIgnoreCase(args[2])) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_set_same_player"));
                    } else {

                        String firstParentUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        FamilyManager firstParentFam = new FamilyManager(firstParentUUID, plugin);
                        String childUUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                        FamilyManager childFam = new FamilyManager(childUUID, plugin);

                        if (!firstParentFam.isMarried() && !plugin.allowSingleAdopt) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_set_no_single_adopt").replace("%player%", firstParentFam.getName()));
                        } else if (childFam.isAdopted()) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_set_already_adopted").replace("%child%", childFam.getName()));
                        } else if (firstParentFam.getChildrenAmount() > 1) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_limit").replace("%player%", firstParentFam.getName()));
                        } else if (childFam.hasSibling() && !forced) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_set_has_sibling").replace("%player%", childFam.getName()));
                        } else if (childFam.hasSibling() && firstParentFam.getChildrenAmount() > 0) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_set_has_sibling_limit").replace("%player1%", childFam.getName()).replace("%player2%", firstParentFam.getName()));
                        } else {
                            if (childFam.hasSibling()) {
                                if (firstParentFam.getChildren().get(0) != null) {
                                    firstParentFam.unadopt(firstParentFam.getChildren().get(0).getID());
                                }
                                if (firstParentFam.getChildren().get(1) != null) {
                                    firstParentFam.unadopt(firstParentFam.getChildren().get(1).getID());
                                }

                                if (firstParentFam.isMarried()) {
                                    FamilyManager partnerFam = firstParentFam.getPartner();
                                    if (partnerFam.getChildren().get(0) != null) {
                                        partnerFam.unadopt(partnerFam.getChildren().get(0).getID());
                                    }
                                    if (partnerFam.getChildren().get(1) != null) {
                                        partnerFam.unadopt(partnerFam.getChildren().get(1).getID());
                                    }
                                }
                            }

                            if (firstParentFam.getPartner() == null) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_set_by_single").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                            } else {
                                FamilyManager secondParentFam = firstParentFam.getPartner();
                                sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_set").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                            }

                            plugin.adoptRequests.remove(childUUID);
                            firstParentFam.adopt(childFam.getID());
                        }

                    }
                } else if (args[0].equalsIgnoreCase("unset") || plugin.getAliases("adopt", "unset").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

                    if (!sender.hasPermission("lunaticFamily.admin.adopt")) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("no_permission"));
                    } else if (args.length < 2) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                    } else if (!Main.playerExists(args[1])) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("player_not_exist").replace("%player%", Bukkit.getOfflinePlayer(args[1]).getName()));
                    } else {

                        String childUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                        FamilyManager childFam = new FamilyManager(childUUID, plugin);

                        if (!childFam.isAdopted()) {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_unset_not_adopted").replace("%player%", childFam.getName()));
                        } else {
                            FamilyManager firstParentFam = childFam.getParents().get(0);

                            if (firstParentFam.isMarried()) {
                                FamilyManager secondParentFam = firstParentFam.getPartner();
                                sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_unset").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("admin_adopt_unset_by_single").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                            }
                            firstParentFam.unadopt(childFam.getID());
                        }
                    }

                } else if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.prefix + plugin.messages.get("no_console_command"));
                } else {
                    Player player = (Player) sender;
                    if (!player.hasPermission("lunaticFamily.adopt")) {
                        sender.sendMessage(plugin.prefix + plugin.messages.get("no_permission"));
                    } else {
                        String playerUUID = player.getUniqueId().toString();
                        FamilyManager playerFam = new FamilyManager(playerUUID, plugin);

                        if (args[0].equalsIgnoreCase("propose") || plugin.getAliases("adopt", "propose").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

                            boolean confirm = false;

                            if (args.length > 2) {
                                if (args[2].equalsIgnoreCase("confirm")) {
                                    confirm = true;
                                }
                            }

                            Bukkit.getLogger().info(String.valueOf(confirm));

                            if (args.length < 2) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
                            } else if (!playerFam.isMarried() && !plugin.allowSingleAdopt) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_propose_no_single_adopt"));
                            } else if (playerFam.getChildrenAmount() > 1){
                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_propose_limit"));
                            } else if (Bukkit.getPlayer(args[1]) == null) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("player_offline").replace("%player%", args[1]));
                            } else {
                                String child = Bukkit.getPlayer(args[1]).getUniqueId().toString();
                                FamilyManager childFam = new FamilyManager(child, plugin);

                                if (args[1].equalsIgnoreCase(player.getName())) {
                                    player.sendMessage(plugin.prefix + plugin.messages.get("adopt_propose_self_request"));
                                } else if (playerFam.isFamilyMember(childFam.getID())) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("marry_propose_family_request").replace("%player%", childFam.getName()));
                                } else if (plugin.adoptRequests.containsKey(child)) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_propose_open_request").replace("%player%", childFam.getName()));
                                } else if (childFam.getParents().get(0) != null) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_propose_already_adopted").replace("%player%", childFam.getName()));
                                } else if (childFam.hasSibling() && !confirm){
                                    TextComponent yes = new TextComponent(ChatColor.GREEN + " \u2713");
                                    yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/adopt propose " + args[1] + " confirm"));
                                    yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + " \u2713").create()));

                                    TextComponent prefix = new TextComponent(plugin.prefix);
                                    TextComponent msg = new TextComponent(plugin.messages.get("adopt_propose_has_sibling").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()));
                                    TextComponent newRow = new TextComponent("\n");

                                    sender.sendMessage(prefix, msg, yes, newRow);

                                } else if (childFam.hasSibling() && playerFam.getChildrenAmount() > 0){
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_propose_has_sibling_limit").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()));
                                } else {
                                    if (playerFam.isMarried()) {
                                        FamilyManager partnerFam = playerFam.getPartner();
                                        Bukkit.getPlayer(UUID.fromString(child)).sendMessage(plugin.prefix + plugin.messages.get("adopt_propose_request").replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()));
                                    } else {
                                        Bukkit.getPlayer(UUID.fromString(child)).sendMessage(plugin.prefix + plugin.messages.get("adopt_propose_request_by_single").replace("%player%", playerFam.getName()));

                                    }
                                    plugin.adoptRequests.put(child, playerUUID);
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_propose_request_sent").replace("%player%", childFam.getName()));
                                    ;

                                    new BukkitRunnable() {
                                        public void run() {
                                            if (plugin.adoptRequests.containsKey(child)) {
                                                plugin.adoptRequests.remove(child);
                                                if (playerFam.isMarried()) {
                                                    FamilyManager partnerFam = playerFam.getPartner();
                                                    Bukkit.getPlayer(UUID.fromString(child)).sendMessage(plugin.prefix + plugin.messages.get("adopt_propose_request_expired").replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()));
                                                } else {
                                                    Bukkit.getPlayer(UUID.fromString(child)).sendMessage(plugin.prefix + plugin.messages.get("adopt_propose_request_by_single_expired").replace("%player%", playerFam.getName()));
                                                }
                                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_request_sent_expired").replace("%player%", childFam.getName()));
                                            }
                                        }
                                    }.runTaskLater(plugin, 600L);
                                }
                            }

                        } else if (args[0].equalsIgnoreCase("accept") || plugin.getAliases("adopt", "accept").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

                            //check for request
                            if (!plugin.adoptRequests.containsKey(playerUUID)) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_accept_no_request"));
                            } else {

                                String firstParent = plugin.adoptRequests.get(playerUUID);
                                FamilyManager firstParentFam = new FamilyManager(firstParent, plugin);

                                if (firstParentFam.getChildrenAmount() > 1) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_accept_parent_limit").replace("%player%", firstParentFam.getName()));
                                } else {

                                    if (firstParentFam.isMarried()) {
                                        FamilyManager secondParentFam = firstParentFam.getPartner();
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_accept_got_adopted").replace("%player1%", firstParentFam.getName()).replace("%player2%", secondParentFam.getName()));
                                        if (Bukkit.getPlayer(UUID.fromString(secondParentFam.getUUID())) != null) {
                                            Bukkit.getPlayer(UUID.fromString(secondParentFam.getUUID())).sendMessage(plugin.prefix + plugin.messages.get("adopt_accept_adopted").replace("%player%", playerFam.getName()));
                                        }
                                    } else {
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_accept_adopted_by_single").replace("%player%", firstParentFam.getName()));
                                    }

                                    Bukkit.getPlayer(UUID.fromString(firstParent)).sendMessage(plugin.prefix + plugin.messages.get("adopt_accept_adopted").replace("%player%", playerFam.getName()));
                                    plugin.adoptRequests.remove(playerUUID);
                                    firstParentFam.adopt(playerFam.getID());
                                }
                            }
                        } else if (args[0].equalsIgnoreCase("list") || plugin.getAliases("adopt", "list").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                            if (playerFam.getChildren().get(0) != null || playerFam.getChildren().get(1) != null) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_list_no_child"));
                            } else {
                                    String msg = plugin.prefix + plugin.messages.get("adopt_list") + "\n";
                                    if (playerFam.getChildren().get(0) != null) {
                                        msg = msg + playerFam.getChildren().get(0).getName() + "\n";
                                    }
                                    if (playerFam.getChildren().get(1) != null) {
                                        msg = msg + playerFam.getChildren().get(1).getName() + "\n";
                                    }
                                    sender.sendMessage(msg);
                            }
                        } else if (args[0].equalsIgnoreCase("deny") || plugin.getAliases("adopt", "deny").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                            if (!plugin.adoptRequests.containsKey(playerUUID)) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_deny_no_request"));
                            } else {
                                String firstParent = plugin.adoptRequests.get(playerUUID);
                                Bukkit.getPlayer(UUID.fromString(firstParent)).sendMessage(plugin.prefix + plugin.messages.get("adopt_deny").replace("%player%", playerFam.getName()));
                                plugin.marryRequests.remove(playerUUID);
                            }
                        } else if (args[0].equalsIgnoreCase("moveout") || plugin.getAliases("adopt", "moveout").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

                            boolean confirm = false;

                            if (args.length > 1) {
                                if (args[1].equalsIgnoreCase("confirm")) {
                                    confirm = true;
                                }
                            }

                            if (!playerFam.isAdopted()) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_moveout_no_parents"));

                            } else if (!confirm) {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_moveout_confirm"));
                            } else {
                                FamilyManager firstParentFam = playerFam.getParents().get(0);

                                if (playerFam.hasSibling()) {
                                    FamilyManager siblingFam = playerFam.getSibling();
                                    if (Bukkit.getPlayer(UUID.fromString(siblingFam.getUUID())) != null) {
                                        Bukkit.getPlayer(UUID.fromString(siblingFam.getUUID())).sendMessage(plugin.prefix + plugin.messages.get("adopt_moveout_sibling"));
                                    }
                                }

                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_moveout"));

                                if (Bukkit.getPlayer(UUID.fromString(firstParentFam.getUUID())) != null) {
                                    Bukkit.getPlayer(UUID.fromString(firstParentFam.getUUID())).sendMessage(plugin.prefix + plugin.messages.get("adopt_moveout_child").replace("%player%", playerFam.getName()));
                                }
                                if (firstParentFam.isMarried()) {
                                    FamilyManager secondParentFam = firstParentFam.getPartner();
                                    if (Bukkit.getPlayer(UUID.fromString(secondParentFam.getUUID())) != null) {
                                        Bukkit.getPlayer(UUID.fromString(secondParentFam.getUUID())).sendMessage(plugin.prefix + plugin.messages.get("adopt_moveout_child").replace("%player%", playerFam.getName()));
                                    }
                                }

                                firstParentFam.unadopt(playerFam.getID());

                            }
                        } else if (args[0].equalsIgnoreCase("kickout") || plugin.getAliases("adopt", "kickout").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                            if (playerFam.getChildren().get(0) != null || playerFam.getChildren().get(1) != null) {
                                if (args.length == 1) {
                                    sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_kickout_specify_child"));
                                } else {
                                    String childUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                                    FamilyManager childFam = new FamilyManager(childUUID, plugin);
                                    if (childFam.isChildOf(playerFam.getID())) {

                                        sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_kickout").replace("%player%", childFam.getName()));
                                        if (playerFam.isMarried()) {
                                            if (Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) != null)
                                            Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())).sendMessage(plugin.prefix + plugin.messages.get("adopt_kickout_partner").replace("%player1%", playerFam.getName()).replace("%player2%", childFam.getName()));
                                        }

                                        if (childFam.hasSibling()) {
                                            FamilyManager siblingFam = childFam.getSibling();
                                            if (Bukkit.getPlayer(UUID.fromString(siblingFam.getUUID())) != null) {
                                                Bukkit.getPlayer(UUID.fromString(siblingFam.getUUID())).sendMessage(plugin.prefix + plugin.messages.get("adopt_kickout_sibling").replace("%player%", playerFam.getName()));
                                            }
                                        }

                                        if (Bukkit.getPlayer(UUID.fromString(childUUID)) != null) {
                                            Bukkit.getPlayer(UUID.fromString(childUUID)).sendMessage(plugin.prefix + plugin.messages.get("adopt_kickout_child"));
                                        }


                                        playerFam.unadopt(childFam.getID());

                                    } else {
                                        sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_kickout_not_your_child").replace("%player%", childFam.getName()));
                                    }
                                }
                            } else {
                                sender.sendMessage(plugin.prefix + plugin.messages.get("adopt_kickout_no_child"));
                            }
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.messages.get("wrong_usage"));
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
