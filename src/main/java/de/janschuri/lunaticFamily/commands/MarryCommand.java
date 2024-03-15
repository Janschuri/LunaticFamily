package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.Main;
import de.janschuri.lunaticFamily.utils.FamilyManager;
import de.janschuri.lunaticFamily.utils.Minepacks;
import de.janschuri.lunaticFamily.utils.Vault;
import net.md_5.bungee.api.chat.*;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MarryCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public MarryCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {


        if (args.length == 0) {
            if (!sender.hasPermission("lunaticFamily." + label)) {
                sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
            } else {
                String[] subcommandsHelp = {"propose", "priest", "list", "divorce", "kiss", "gift"};

                String msg = Main.prefix + " " + Main.getMessage(label + "_help") + "\n";

                for (String subcommand : subcommandsHelp) {
                    msg = msg + Main.prefix + " " + Main.getMessage(label + "_" + subcommand + "_help") + "\n";
                }
                sender.sendMessage(msg);
            }
        } else {
            //admin subcommand "set"
            if (args[0].equalsIgnoreCase("set") || Main.getAliases("marry", "set").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

                boolean force = false;

                if (args.length > 3) {
                    if (args[3].equalsIgnoreCase("force")) {
                        force = true;
                    }
                }

                if (!sender.hasPermission("lunaticFamily.admin.marry")) {
                    sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                } else if (args.length < 2) {
                    sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                } else if (args[1].equalsIgnoreCase("deny")) {
                    sender.sendMessage(Main.prefix + Main.getMessage("admin_marry_set_denied"));
                } else if (args.length < 3) {
                    sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                } else if (!Main.playerExists(args[1]) && !force) {
                    sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", args[1]));
                } else if (!Main.playerExists(args[2]) && !force) {
                    sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", args[2]));
                } else if (args[1].equalsIgnoreCase(args[2])) {
                    sender.sendMessage(Main.prefix + Main.getMessage("admin_marry_set_same_player"));
                }
                else {

                    String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    String player2UUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();

                    FamilyManager player2Fam = new FamilyManager(player2UUID);
                    FamilyManager player1Fam = new FamilyManager(player1UUID);

                    if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
                        int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                        sender.sendMessage(Main.prefix + Main.getMessage("admin_marry_set_too_many_children").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                    } else if (player1Fam.isMarried()) {
                        sender.sendMessage(Main.prefix + Main.getMessage("admin_marry_set_already_married").replace("%player%", player1Fam.getName()));
                    } else if (player2Fam.isMarried()) {
                        sender.sendMessage(Main.prefix + Main.getMessage("admin_marry_set_already_married").replace("%player%", player2Fam.getName()));
                    } else {
                        Main.marryRequests.remove(player1UUID);
                        Main.marryPriestRequests.remove(player1UUID);
                        Main.marryPriest.remove(player1UUID);

                        Main.marryRequests.remove(player1UUID);
                        Main.marryPriestRequests.remove(player1UUID);
                        Main.marryPriest.remove(player1UUID);

                        player1Fam.marry(player2Fam.getID());
                        sender.sendMessage(Main.prefix + Main.getMessage("admin_marry_set_married").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                    }
                }

            } else if (args[0].equalsIgnoreCase("unset") || Main.getAliases("marry", "unset").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                if (!sender.hasPermission("lunaticFamily.admin.marry")) {
                    sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                } else if (args.length < 2) {
                    sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                } else if (!Main.playerExists(args[1])) {
                        sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", args[1]));
                } else {
                    String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyManager player1Fam = new FamilyManager(player1UUID);

                    if (!player1Fam.isMarried()) {
                        sender.sendMessage(Main.prefix + Main.getMessage("admin_marry_unset_no_partner").replace("%player%", player1Fam.getName()));
                    } else {
                        FamilyManager partnerFam = player1Fam.getPartner();
                        player1Fam.divorce();
                        sender.sendMessage(Main.prefix + Main.getMessage("admin_marry_unset_divorced").replace("%player1%", player1Fam.getName()).replace("%player2%", partnerFam.getName()));
                    }
                }
            } else if (!(sender instanceof Player)) {
                sender.sendMessage(Main.prefix + Main.getMessage("no_console_command"));
            } else {
                Player player = (Player) sender;
                if (!player.hasPermission("lunaticFamily.marry")) {
                    sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                } else {
                    String playerUUID = player.getUniqueId().toString();
                    FamilyManager playerFam = new FamilyManager(playerUUID);
                    if (args[0].equalsIgnoreCase("propose") || Main.getAliases("marry", "propose").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                        if (args.length < 2) {
                            sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                        } else if (playerFam.getName().equalsIgnoreCase(args[1])) {
                            sender.sendMessage(Main.prefix + Main.getMessage("marry_propose_self_request"));
                        } else if (playerFam.isMarried()) {
                            sender.sendMessage(Main.prefix + Main.getMessage("marry_propose_already_married").replace("%player%", playerFam.getName()));
                        } else if (!Main.playerExists(args[1])) {
                            sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", args[1]));
                        } else if (Bukkit.getPlayer(args[1]) == null) {
                            sender.sendMessage(Main.prefix + Main.getMessage("player_offline").replace("%player%", Main.getName(args[1])));
                        } else if (!playerFam.hasEnoughMoney("marry_proposing_player")) {
                            sender.sendMessage(Main.prefix + Main.getMessage("not_enough_money"));
                        } else {
                            String partnerUUID = Main.getUUID(args[1]);
                            FamilyManager partnerFam = new FamilyManager(partnerUUID);
                            if (playerFam.isFamilyMember(partnerFam.getID())) {
                                sender.sendMessage(Main.prefix + Main.getMessage("marry_propose_family_request").replace("%player%", partnerFam.getName()));
                            } else if (partnerFam.isFamilyMember(playerFam.getID())) {
                                sender.sendMessage(Main.prefix + Main.getMessage("marry_propose_family_request").replace("%player%", partnerFam.getName()));
                            } else if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                                int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                                sender.sendMessage(Main.prefix + Main.getMessage("marry_propose_too_many_children").replace("%player%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                            } else if (Main.marryRequests.containsKey(partnerUUID) || Main.marryPriest.containsKey(partnerUUID)) {
                                sender.sendMessage(Main.prefix + Main.getMessage("marry_propose_open_request").replace("%player%", partnerFam.getName()));
                            } else if (partnerFam.isMarried()) {
                                sender.sendMessage(Main.prefix + Main.getMessage("marry_propose_player_already_married").replace("%player%", partnerFam.getName()));
                            } else if (!Main.isInRange(player.getLocation(), partnerFam.getPlayer().getLocation(), 5)) {
                                sender.sendMessage(Main.prefix + Main.getMessage("player_too_far_away").replace("%player%", partnerFam.getName()));
                            } else {

                                partnerFam.sendMessage(Main.createClickableMessage(
                                        Main.getMessage("marry_propose_request").replace("%player1%", partnerFam.getName()).replace("%player2%", playerFam.getName()),
                                        Main.getMessage("marry_yes"),
                                        "/marry accept",
                                        Main.getMessage("marry_no"),
                                        "/marry deny"));


                                Main.marryRequests.put(partnerUUID, playerUUID);

                                sender.sendMessage(Main.prefix + Main.getMessage("marry_propose_request_sent").replace("%player%", partnerFam.getName()));

                                new BukkitRunnable() {
                                    public void run() {
                                        if (Main.marryRequests.containsKey(partnerUUID)) {
                                            Main.marryRequests.remove(partnerUUID);
                                            partnerFam.sendMessage(Main.prefix + Main.getMessage("marry_propose_request_expired").replace("%player%", playerFam.getName()));

                                            playerFam.sendMessage(Main.prefix + Main.getMessage("marry_propose_request_sent_expired").replace("%player%", partnerFam.getName()));
                                        }
                                    }
                                }.runTaskLater(plugin, 600L);
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("priest") || Main.getAliases("marry", "priest").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                        if (!player.hasPermission("lunaticFamily.marry.priest")) {
                            sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                        } else if (args.length < 3) {
                            sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                        } else if (Main.marryPriest.containsValue(playerUUID)) {
                            sender.sendMessage(Main.prefix + Main.getMessage("marry_priest_already_priest"));
                        } else if (args[1].equalsIgnoreCase(player.getName()) || args[2].equalsIgnoreCase(player.getName())) {
                            player.sendMessage(Main.prefix + Main.getMessage("marry_priest_self_request"));
                        } else {
                            String player1Name = args[1];
                            String player2Name = args[2];
                            if (!Main.playerExists(player1Name)) {
                                sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", player1Name));
                            } else if (Bukkit.getPlayer(player1Name) == null) {
                                sender.sendMessage(Main.prefix + Main.getMessage("player_offline").replace("%player%", Main.getName(player1Name)));
                            } else if (!Main.playerExists(player2Name)) {
                                sender.sendMessage(Main.prefix + Main.getMessage("player_not_exist").replace("%player%", player2Name));
                            } else if (Bukkit.getPlayer(player2Name) == null) {
                                sender.sendMessage(Main.prefix + Main.getMessage("player_offline").replace("%player%", Main.getName(player2Name)));
                            } else if (Main.commandWithdraws.get("marry_priest_player") > Vault.getEconomy().getBalance(Bukkit.getOfflinePlayer(args[1]))) {
                                sender.sendMessage(Main.prefix + Main.getMessage("player_not_enough_money").replace("%player%", Main.getName(player1Name)));
                            } else if (Main.commandWithdraws.get("marry_priest") > Vault.getEconomy().getBalance(player)) {
                                sender.sendMessage(Main.prefix + Main.getMessage("not_enough_money").replace("%player%", player.getName()));
                            } else if (Main.commandWithdraws.get("marry_priest_player") > Vault.getEconomy().getBalance(Bukkit.getOfflinePlayer(args[2]))) {
                                sender.sendMessage(Main.prefix + Main.getMessage("player_not_enough_money").replace("%player%", Main.getName(player2Name)));
                            }else {
                                String player1UUID = Main.getUUID(player1Name);
                                FamilyManager player1Fam = new FamilyManager(player1UUID);
                                String player2UUID = Main.getUUID(player2Name);
                                FamilyManager player2Fam = new FamilyManager(player2UUID);

                                if (player1Fam.isMarried()) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("marry_priest_player_already_married").replace("%player%", player1Fam.getName()));
                                } else if (player2Fam.isMarried()) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("marry_priest_player_already_married").replace("%player%", player2Fam.getName()));
                                } else if (Main.marryRequests.containsKey(player1UUID) || Main.marryPriest.containsValue(player1UUID)) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("marry_priest_open_request").replace("%player%", player1Fam.getName()));
                                } else if (Main.marryRequests.containsKey(player2UUID) || Main.marryPriest.containsValue(player2UUID)) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("marry_priest_open_request").replace("%player%", player2Fam.getName()));
                                } else if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
                                    int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                                    sender.sendMessage(Main.prefix + Main.getMessage("marry_priest_too_many_children").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                                } else {
                                    player.chat(Main.getMessage("marry_priest_request").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));


                                    Player player1 = Bukkit.getPlayer(player1Name);
                                    player1.sendMessage(Main.createClickableMessage(
                                            "",
                                            Main.getMessage("marry_yes"),
                                            "/marry accept",
                                            Main.getMessage("marryMain"),
                                            "/marry deny"));

                                    Main.marryPriestRequests.put(player1UUID, player2UUID);
                                    Main.marryPriest.put(player1UUID, playerUUID);
                                }
                            }
                        }
                    }
                    //player subcommand "accept"
                    else if (args[0].equalsIgnoreCase("accept") || Main.getAliases("marry", "accept").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

                        if (!Main.marryRequests.containsKey(playerUUID) && !Main.marryPriestRequests.containsKey(playerUUID)) {
                            sender.sendMessage(Main.prefix + Main.getMessage("marry_accept_no_request"));
                        } else if (Main.marryPriestRequests.containsValue(playerUUID)) {
                            sender.sendMessage(Main.prefix + Main.getMessage("marry_accept_open_request_partner"));
                        } else {
                            //check for request
                            if (Main.marryRequests.containsKey(playerUUID)) {

                                String partnerUUID = Main.marryRequests.get(playerUUID);
                                FamilyManager partnerFam = new FamilyManager(partnerUUID);

                                if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                                    int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                                    sender.sendMessage(Main.prefix + Main.getMessage("marry_accept_too_many_children").replace("%partner%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                                } else if (!partnerFam.isOnline()) {
                                    sender.sendMessage(Main.prefix + Main.getMessage("player_offline").replace("%player%", partnerFam.getName()));
                                } else if (Main.marryPriest.containsKey(partnerUUID)) {
                                    String priestUUID = Main.marryPriest.get(partnerUUID);
                                    FamilyManager priestFam = new FamilyManager(priestUUID);

                                    if (!priestFam.hasEnoughMoney("marry_priest")) {
                                        sender.sendMessage(Main.prefix + Main.getMessage("not_enough_money").replace("%player%", priestFam.getName()));
                                    } else if (!playerFam.hasEnoughMoney("marry_priest_player")) {
                                        sender.sendMessage(Main.prefix + Main.getMessage("not_enough_money").replace("%player%", player.getName()));
                                    } else if (!partnerFam.hasEnoughMoney("marry_priest_player")) {
                                        sender.sendMessage(Main.prefix + Main.getMessage("player_not_enough_money").replace("%player%", partnerFam.getName()));
                                    } else {

                                        priestFam.withdrawPlayer("marry_priest");
                                        playerFam.withdrawPlayer("marry_priest_player");
                                        partnerFam.withdrawPlayer("marry_priest_player");

                                        player.chat(Main.getMessage("marry_accept_yes"));

                                        new BukkitRunnable() {
                                            public void run() {
                                                if (priestFam.getPlayer() != null) {
                                                    priestFam.getPlayer().chat(Main.getMessage("marry_priest_complete"));
                                                }
                                            }
                                        }.runTaskLater(plugin, 20L);

                                        Main.marryPriest.remove(partnerUUID);

                                        Main.marryRequests.remove(playerUUID);
                                        Main.marryPriestRequests.remove(playerUUID);
                                        Main.marryPriest.remove(playerUUID);

                                        Main.marryRequests.remove(partnerUUID);
                                        Main.marryPriestRequests.remove(partnerUUID);
                                        Main.marryPriest.remove(partnerUUID);

                                        playerFam.marry(partnerFam.getID());
                                    }
                                } else {
                                    if (!partnerFam.hasEnoughMoney("marry_proposing_player")) {
                                        sender.sendMessage(Main.prefix + Main.getMessage("player_not_enough_money").replace("%player%", partnerFam.getName()));
                                    } else if (!playerFam.hasEnoughMoney("marry_proposed_player")) {
                                        sender.sendMessage(Main.prefix + Main.getMessage("not_enough_money").replace("%player%", player.getName()));
                                    } else {
                                        sender.sendMessage(Main.prefix + Main.getMessage("marry_accept_complete"));
                                        partnerFam.sendMessage(Main.prefix + Main.getMessage("marry_accept_complete"));

                                        playerFam.withdrawPlayer("marry_proposed_player");
                                        partnerFam.withdrawPlayer("marry_proposing_player");

                                        Main.marryRequests.remove(playerUUID);
                                        Main.marryPriestRequests.remove(playerUUID);
                                        Main.marryPriest.remove(playerUUID);

                                        Main.marryRequests.remove(partnerUUID);
                                        Main.marryPriestRequests.remove(partnerUUID);
                                        Main.marryPriest.remove(partnerUUID);

                                        playerFam.marry(partnerFam.getID());
                                    }
                                }

                            }

                            //check for priest request
                            else if (Main.marryPriestRequests.containsKey(playerUUID)) {

                                String partnerUUID = Main.marryPriestRequests.get(playerUUID);
                                FamilyManager partnerFam = new FamilyManager(partnerUUID);
                                OfflinePlayer partnerPlayer = Bukkit.getOfflinePlayer(UUID.fromString(partnerUUID));

                                if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                                    int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                                    sender.sendMessage(Main.prefix + Main.getMessage("marry_accept_too_many_children").replace("%partner%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                                } else {

                                    String priestUUID = Main.marryPriest.get(playerUUID);
                                    FamilyManager priestFam = new FamilyManager(priestUUID);

                                    if (Main.commandWithdraws.get("marry_proposing_player") > Vault.getEconomy().getBalance(partnerPlayer)) {
                                        sender.sendMessage(Main.prefix + Main.getMessage("player_not_enough_money").replace("%player%", partnerFam.getName()));
                                    } else if (Main.commandWithdraws.get("marry_proposed_player") > Vault.getEconomy().getBalance(player)) {
                                        sender.sendMessage(Main.prefix + Main.getMessage("not_enough_money").replace("%player%", player.getName()));
                                    } else {

                                        Main.marryPriestRequests.remove(playerUUID);
                                        Main.marryRequests.put(partnerUUID, playerUUID);
                                        player.chat(Main.getMessage("marry_accept_yes"));

                                        new BukkitRunnable() {
                                            public void run() {
                                                priestFam.chat(Main.getMessage("marry_accept_request").replace("%player1%", partnerFam.getName()).replace("%player2%", playerFam.getName()));
                                            }
                                        }.runTaskLater(plugin, 20L);
                                    }
                                }
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("deny") || Main.getAliases("marry", "deny").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

                        if (!Main.marryRequests.containsKey(playerUUID) && !Main.marryPriestRequests.containsKey(playerUUID)) {
                            sender.sendMessage(Main.prefix + Main.getMessage("marry_deny_no_request"));
                        } else {
                            if (Main.marryRequests.containsKey(playerUUID)) {
                                String partnerUUID = Main.marryRequests.get(playerUUID);
                                FamilyManager partnerFam = new FamilyManager(partnerUUID);
                                if (!Main.marryPriest.containsKey(partnerUUID)) {
                                    partnerFam.sendMessage(Main.prefix + Main.getMessage("marry_deny_denied").replace("%player%", playerFam.getName()));
                                } else {
                                    String priestUUID = Main.marryPriest.get(partnerUUID);
                                    FamilyManager priestFam = new FamilyManager(priestUUID);
                                    player.chat(Main.getMessage("marry_deny_no"));
                                    priestFam.chat(Main.getMessage("marry_deny_cancel"));
                                    Main.marryPriest.remove(partnerUUID);
                                }
                                Main.marryRequests.remove(playerUUID);

                            } else if (Main.marryPriestRequests.containsKey(playerUUID)) {
                                player.chat(Main.getMessage("marry_deny_no"));
                                String priestUUID = Main.marryPriest.get(playerUUID);
                                FamilyManager priestFam = new FamilyManager(priestUUID);
                                priestFam.chat(Main.getMessage("marry_deny_cancel"));
                                Main.marryPriestRequests.remove(playerUUID);
                                Main.marryPriest.remove(playerUUID);
                            }
                        }
                    }
                    //player subcommand "divorce"
                    else if (args[0].equalsIgnoreCase("divorce") || Main.getAliases("marry", "divorce").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

                        boolean confirm = false;
                        boolean cancel = false;
                        boolean force = false;

                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("confirm")) {
                                confirm = true;
                            }
                            if (args[1].equalsIgnoreCase("cancel")) {
                                cancel = true;
                            }
                        }
                        if (args.length > 2) {
                            if (args[2].equalsIgnoreCase("force")) {
                                force = true;
                            }
                        }


                        if (!playerFam.isMarried()) {
                            sender.sendMessage(Main.prefix + Main.getMessage("marry_divorce_no_partner"));
                        } else if (!confirm) {
                            sender.sendMessage(Main.createClickableMessage(
                                    Main.getMessage("marry_divorce_confirm"),
                                    Main.getMessage("confirm"),
                                    "/marry divorce confirm",
                                    Main.getMessage("cancel"),
                                    "/marry divorce cancel"));
                        } else if (cancel) {
                            sender.sendMessage(Main.prefix + Main.getMessage("marry_divorce_cancel"));
                        } else if (Main.commandWithdraws.get("marry_divorce_leaving_player") > Vault.getEconomy().getBalance(player)) {
                            sender.sendMessage(Main.prefix + Main.getMessage("not_enough_money"));
                        } else if (Main.commandWithdraws.get("marry_divorce_left_player") > Vault.getEconomy().getBalance(Bukkit.getOfflinePlayer(UUID.fromString(playerFam.getPartner().getUUID())))) {
                            sender.sendMessage(Main.prefix + Main.getMessage("player_not_enough_money").replace("%player%", playerFam.getPartner().getName()));
                            sender.sendMessage(Main.createClickableMessage(
                                    Main.getMessage("take_payment_confirm"),
                                    Main.getMessage("confirm"),
                                    "/marry divorce confirm force",
                                    Main.getMessage("cancel"),
                                    "/marry divorce cancel"));
                        } else {
                            sender.sendMessage(Main.prefix + Main.getMessage("marry_divorce_divorced"));
                            playerFam.getPartner().sendMessage(Main.prefix + Main.getMessage("marry_divorce_divorced"));

                            playerFam.withdrawPlayer("marry_divorce_leaving_player");
                            playerFam.getPartner().withdrawPlayer("marry_divorce_leaving_player");

                            playerFam.divorce();
                        }
                    } else if (args[0].equalsIgnoreCase("kiss") || Main.getAliases("marry", "kiss").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {

                        if (!playerFam.isMarried()) {
                            sender.sendMessage(Main.prefix + Main.getMessage("marry_kiss_no_partner"));
                        } else if (Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) == null) {
                            sender.sendMessage(Main.prefix + Main.getMessage("player_offline").replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(playerFam.getPartner().getUUID())).getName()));
                        } else {
                            Player partnerPlayer = Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID()));

                            if (!Main.isInRange(partnerPlayer.getLocation(), player.getLocation(), 3)) {
                                sender.sendMessage(Main.prefix + Main.getMessage("player_too_far_away").replace("%player%", partnerPlayer.getName()));
                            } else {
                                Location location = Main.getPositionBetweenLocations(player.getLocation(), partnerPlayer.getLocation());
                                location.setY(location.getY() + 2);

                                for (int i = 0; i < 6; i++) { // Spawn three clouds
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> Main.spawnParticles(location, Particle.HEART), i * 5L); // Delay between clouds: i * 20 ticks (1 second)
                                }
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("gift") || Main.getAliases("marry", "gift").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                        if (!playerFam.isMarried()) {
                            sender.sendMessage(Main.prefix + Main.getMessage("marry_gift_no_partner"));
                        } else if (!player.hasPermission("lunaticFamily.marry.gift")) {
                            sender.sendMessage(Main.prefix + Main.getMessage("no_permission"));
                        } else if (Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) == null) {
                            sender.sendMessage(Main.prefix + Main.getMessage("player_offline").replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(playerFam.getPartner().getUUID())).getName()));
                        } else if (player.getInventory().getItemInMainHand().isEmpty()) {
                            sender.sendMessage(Main.prefix + Main.getMessage("marry_gift_hand_empty"));
                        } else {
                            Player partnerPlayer = playerFam.getPartner().getPlayer();
                            if (partnerPlayer.getInventory().firstEmpty() == -1) {
                                sender.sendMessage(Main.prefix + Main.getMessage("marry_gift_partner_full_inv"));
                            } else {
                                ItemStack item = player.getInventory().getItemInMainHand();
                                player.getInventory().remove(item);
                                partnerPlayer.getInventory().addItem(item);
                                Material material = item.getType();
                                int amount = item.getAmount();

                                ItemMeta itemMeta = item.getItemMeta();



                                String[] msgPlayer = Main.getMessage("marry_gift_sent").split("%item%");
                                String[] msgPartner = Main.getMessage("marry_gift_got").split("%item%");

                                TextComponent componentAmount = new TextComponent(amount + "x ");

                                TextComponent componentPlayer1 = new TextComponent(msgPlayer[0]);
                                TextComponent componentPlayer2 = new TextComponent(msgPlayer[1]);

                                TextComponent componentPartner1 = new TextComponent(msgPartner[0]);
                                TextComponent componentPartner2 = new TextComponent(msgPartner[1]);

                                if (itemMeta.hasDisplayName()) {
                                    TextComponent componentItem = new TextComponent(itemMeta.getDisplayName());

                                    Bukkit.getLogger().info(itemMeta.getDisplayName());

                                    player.sendMessage(componentPlayer1, componentAmount, componentItem, componentPlayer2);
                                    partnerPlayer.sendMessage(componentPartner1, componentAmount, componentItem, componentPartner2);
                                } else {
                                    TranslatableComponent componentItem = new TranslatableComponent(Main.getKey(material));

                                    player.sendMessage(componentPlayer1, componentAmount, componentItem, componentPlayer2);
                                    partnerPlayer.sendMessage(componentPartner1, componentAmount, componentItem, componentPartner2);
                                }



                            }

                        }
                    } else if (args[0].equalsIgnoreCase("backpack") || Main.getAliases("marry", "backpack").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                        if(!Main.enabledMinepacks) {
                            sender.sendMessage(Main.getMessage("disabled_feature"));
                        } else if (!playerFam.isMarried()) {
                            sender.sendMessage(Main.prefix + Main.getMessage("marry_backpack_no_partner"));
                        } else if (!Main.marryBackpackOffline && Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) == null) {
                            sender.sendMessage(Main.prefix + Main.getMessage("player_offline").replace("%player%", playerFam.getPartner().getName()));
                        } else {
                            OfflinePlayer partnerPlayer = playerFam.getPartner().getOfflinePlayer();
                            sender.sendMessage(Main.prefix + Main.getMessage("marry_backpack_open"));
                            Minepacks.getMinepacks().openBackpack(player, partnerPlayer, true);
                        }
                    } else if (args[0].equalsIgnoreCase("list") || Main.getAliases("marry", "list").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                        int page = 1;
                        if (args.length > 1) {
                            try {
                                page = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(Main.prefix + Main.getMessage("marry_list_no_number").replace("%input%", args[1]));
                            }
                        }

                        List<Integer> marryList = Main.getDatabase().getMarryList(page, 10);
                        String msg = Main.prefix + Main.getMessage("marry_list") + "\n";
                        int index = 1 + (10*(page-1));
                        for (Integer e : marryList) {
                            FamilyManager player1Fam = new FamilyManager(e);
                            FamilyManager player2Fam = new FamilyManager(player1Fam.getPartner().getID());

                            msg = msg + index + ": " + player1Fam.getName() + " \u2764 " + player2Fam.getName() + " (" + player1Fam.getMarriageDate() + ")" + "\n";
                            index++;
                        }
                        sender.sendMessage(msg);

                    } else if (args[0].equalsIgnoreCase("help") || Main.getAliases(label, "help").stream().anyMatch(element -> args[0].equalsIgnoreCase(element))) {
                        String[] subcommandsHelp = {"propose", "priest", "list", "divorce", "kiss", "gift"};

                        String msg = Main.prefix + " " + Main.getMessage(label + "_help") + "\n";

                        for (String subcommand : subcommandsHelp) {
                            msg = msg + Main.prefix + " " + Main.getMessage(label + "_" + subcommand + "_help") + "\n";
                        }
                        sender.sendMessage(msg);
                    } else {
                        sender.sendMessage(Main.prefix + Main.getMessage("wrong_usage"));
                    }
                }
            }
        }

        return true;

    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {

        List<String> marrySubcommands = Main.marrySubcommands;
        List<String> marryPriestSubcommands = Main.marryPriestSubcommands;
        List<String> marryAdminSubcommands = Main.marryAdminSubcommands;
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

