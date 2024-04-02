package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.external.Minepacks;
import de.janschuri.lunaticFamily.utils.Utils;
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

    private final LunaticFamily plugin;

    public MarryCommand(LunaticFamily plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {


        if (args.length == 0) {
            if (!sender.hasPermission("lunaticFamily." + label)) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
            } else {

                List<String> subcommandsHelp = new ArrayList<>();
                subcommandsHelp.add("propose");
                subcommandsHelp.add("list");
                subcommandsHelp.add("divorce");

                if (sender.hasPermission("lunaticFamily.marry.priest")) {
                    subcommandsHelp.add("priest");
                }
                if (sender.hasPermission("lunaticFamily.marry.kiss")) {
                    subcommandsHelp.add("kiss");
                }
                if (sender.hasPermission("lunaticFamily.marry.gift")) {
                    subcommandsHelp.add("gift");
                }
                if (sender.hasPermission("lunaticFamily.marry.backpack")) {
                    subcommandsHelp.add("backpack");
                }

                String msg = Language.prefix + " " + Language.getMessage(label + "_help") + "\n";

                for (String subcommand : subcommandsHelp) {
                    msg = msg + Language.prefix + " " + Language.getMessage(label + "_" + subcommand + "_help") + "\n";
                }
                sender.sendMessage(msg);
            }
        } else {
            final String subcommand = args[0];
            if (checkIsSubcommand("set", subcommand)) {

                boolean force = false;

                if (args.length > 3) {
                    if (args[3].equalsIgnoreCase("force")) {
                        force = true;
                    }
                }

                if (!sender.hasPermission("lunaticFamily.admin.marry")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                } else if (args.length < 2) {
                    sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                } else if (args[1].equalsIgnoreCase("deny")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_set_denied"));
                } else if (args.length < 3) {
                    sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                } else if (!Utils.playerExists(args[1]) && !force) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                } else if (!Utils.playerExists(args[2]) && !force) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[2]));
                } else if (args[1].equalsIgnoreCase(args[2])) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_set_same_player"));
                }
                else {

                    String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    String player2UUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();

                    FamilyPlayer player2Fam = new FamilyPlayer(player2UUID);
                    FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);

                    if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
                        int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_set_too_many_children").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                    } else if (player1Fam.isMarried()) {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_set_already_married").replace("%player%", player1Fam.getName()));
                    } else if (player2Fam.isMarried()) {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_set_already_married").replace("%player%", player2Fam.getName()));
                    } else {
                        LunaticFamily.marryRequests.remove(player1UUID);
                        LunaticFamily.marryPriestRequests.remove(player1UUID);
                        LunaticFamily.marryPriest.remove(player1UUID);

                        LunaticFamily.marryRequests.remove(player1UUID);
                        LunaticFamily.marryPriestRequests.remove(player1UUID);
                        LunaticFamily.marryPriest.remove(player1UUID);

                        player1Fam.marry(player2Fam.getID());
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_set_married").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                    }
                }

            } else if (checkIsSubcommand("unset", subcommand)) {
                if (!sender.hasPermission("lunaticFamily.admin.marry")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                } else if (args.length < 2) {
                    sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                } else if (!Utils.playerExists(args[1])) {
                        sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                } else {
                    String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);

                    if (!player1Fam.isMarried()) {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_unset_no_partner").replace("%player%", player1Fam.getName()));
                    } else {
                        FamilyPlayer partnerFam = player1Fam.getPartner();
                        player1Fam.divorce();
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_unset_divorced").replace("%player1%", player1Fam.getName()).replace("%player2%", partnerFam.getName()));
                    }
                }
            } else if (!(sender instanceof Player)) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
            } else {
                Player player = (Player) sender;
                if (!player.hasPermission("lunaticFamily.marry")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                } else {
                    String playerUUID = player.getUniqueId().toString();
                    FamilyPlayer playerFam = new FamilyPlayer(playerUUID);
                    if (checkIsSubcommand("propose", subcommand)) {
                        if (args.length < 2) {
                            sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                        } else if (playerFam.getName().equalsIgnoreCase(args[1])) {
                            sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_self_request"));
                        } else if (playerFam.isMarried()) {
                            sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_already_married").replace("%player%", playerFam.getName()));
                        } else if (!Utils.playerExists(args[1])) {
                            sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                        } else if (Bukkit.getPlayer(args[1]) == null) {
                            sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", Utils.getName(args[1])));
                        } else if (!playerFam.hasEnoughMoney("marry_proposing_player")) {
                            sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                        } else {
                            String partnerUUID = Utils.getUUID(args[1]);
                            FamilyPlayer partnerFam = new FamilyPlayer(partnerUUID);
                            if (playerFam.isFamilyMember(partnerFam.getID())) {
                                sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_family_request").replace("%player%", partnerFam.getName()));
                            } else if (partnerFam.isFamilyMember(playerFam.getID())) {
                                sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_family_request").replace("%player%", partnerFam.getName()));
                            } else if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                                int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                                sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_too_many_children").replace("%player%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                            } else if (LunaticFamily.marryRequests.containsKey(partnerUUID) || LunaticFamily.marryPriest.containsKey(partnerUUID)) {
                                sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_open_request").replace("%player%", partnerFam.getName()));
                            } else if (partnerFam.isMarried()) {
                                sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_player_already_married").replace("%player%", partnerFam.getName()));
                            } else if (!Utils.isInRange(player.getLocation(), partnerFam.getPlayer().getLocation(), 5)) {
                                sender.sendMessage(Language.prefix + Language.getMessage("player_too_far_away").replace("%player%", partnerFam.getName()));
                            } else {

                                partnerFam.sendMessage(Utils.createClickableMessage(
                                        Language.getMessage("marry_propose_request").replace("%player1%", partnerFam.getName()).replace("%player2%", playerFam.getName()),
                                        Language.getMessage("marry_yes"),
                                        "/marry accept",
                                        Language.getMessage("marry_no"),
                                        "/marry deny"));


                                LunaticFamily.marryRequests.put(partnerUUID, playerUUID);

                                sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_request_sent").replace("%player%", partnerFam.getName()));

                                new BukkitRunnable() {
                                    public void run() {
                                        if (LunaticFamily.marryRequests.containsKey(partnerUUID)) {
                                            LunaticFamily.marryRequests.remove(partnerUUID);
                                            partnerFam.sendMessage(Language.prefix + Language.getMessage("marry_propose_request_expired").replace("%player%", playerFam.getName()));

                                            playerFam.sendMessage(Language.prefix + Language.getMessage("marry_propose_request_sent_expired").replace("%player%", partnerFam.getName()));
                                        }
                                    }
                                }.runTaskLater(plugin, 600L);
                            }
                        }
                    } else if (checkIsSubcommand("priest", subcommand)) {
                        if (!player.hasPermission("lunaticFamily.marry.priest")) {
                            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                        } else if (args.length < 3) {
                            sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                        } else if (LunaticFamily.marryPriest.containsValue(playerUUID)) {
                            sender.sendMessage(Language.prefix + Language.getMessage("marry_priest_already_priest"));
                        } else if (args[1].equalsIgnoreCase(player.getName()) || args[2].equalsIgnoreCase(player.getName())) {
                            player.sendMessage(Language.prefix + Language.getMessage("marry_priest_self_request"));
                        } else {
                            String player1Name = args[1];
                            String player2Name = args[2];
                            if (!Utils.playerExists(player1Name)) {
                                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", player1Name));
                            } else if (Bukkit.getPlayer(player1Name) == null) {
                                sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", Utils.getName(player1Name)));
                            } else if (!Utils.playerExists(player2Name)) {
                                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", player2Name));
                            } else if (Bukkit.getPlayer(player2Name) == null) {
                                sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", Utils.getName(player2Name)));
                            } else {
                                String player1UUID = Utils.getUUID(player1Name);
                                FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);
                                String player2UUID = Utils.getUUID(player2Name);
                                FamilyPlayer player2Fam = new FamilyPlayer(player2UUID);
                                if (!player1Fam.hasEnoughMoney("marry_priest_player")) {
                                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", Utils.getName(player1Name)));
                                } else if (!player1Fam.hasEnoughMoney("marry_priest_player")) {
                                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", Utils.getName(player2Name)));
                                } else if (!playerFam.hasEnoughMoney("marry_priest")) {
                                    sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money").replace("%player%", player.getName()));
                                } else {


                                    if (player1Fam.isMarried()) {
                                        sender.sendMessage(Language.prefix + Language.getMessage("marry_priest_player_already_married").replace("%player%", player1Fam.getName()));
                                    } else if (player2Fam.isMarried()) {
                                        sender.sendMessage(Language.prefix + Language.getMessage("marry_priest_player_already_married").replace("%player%", player2Fam.getName()));
                                    } else if (LunaticFamily.marryRequests.containsKey(player1UUID) || LunaticFamily.marryPriest.containsValue(player1UUID)) {
                                        sender.sendMessage(Language.prefix + Language.getMessage("marry_priest_open_request").replace("%player%", player1Fam.getName()));
                                    } else if (LunaticFamily.marryRequests.containsKey(player2UUID) || LunaticFamily.marryPriest.containsValue(player2UUID)) {
                                        sender.sendMessage(Language.prefix + Language.getMessage("marry_priest_open_request").replace("%player%", player2Fam.getName()));
                                    } else if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
                                        int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                                        sender.sendMessage(Language.prefix + Language.getMessage("marry_priest_too_many_children").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                                    } else {
                                        player.chat(Language.getMessage("marry_priest_request").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));

                                        Player player1 = Bukkit.getPlayer(player1Name);
                                        player1.sendMessage(Utils.createClickableMessage(
                                                "",
                                                Language.getMessage("marry_yes"),
                                                "/marry accept",
                                                Language.getMessage("marry_no"),
                                                "/marry deny"));

                                        LunaticFamily.marryPriestRequests.put(player1UUID, player2UUID);
                                        LunaticFamily.marryPriest.put(player1UUID, playerUUID);
                                        new BukkitRunnable() {
                                            public void run() {
                                                if (LunaticFamily.marryPriest.containsValue(playerUUID)) {
                                                    playerFam.sendMessage(Language.prefix + Language.getMessage("marry_priest_request_expired_priest").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                                                    player1Fam.sendMessage(Language.prefix + Language.getMessage("marry_priest_request_expired_player").replace("%player%", player2Fam.getName()));
                                                    player2Fam.sendMessage(Language.prefix + Language.getMessage("marry_priest_request_expired_player").replace("%player%", player1Fam.getName()));

                                                    LunaticFamily.marryRequests.remove(player2UUID);
                                                    LunaticFamily.marryPriestRequests.remove(player1UUID);
                                                    LunaticFamily.marryPriest.remove(player1UUID);
                                                }
                                            }
                                        }.runTaskLater(plugin, 600L);
                                    }
                                }
                            }
                        }
                    }
                    //player subcommand "accept"
                    else if (checkIsSubcommand("accept", subcommand)) {

                        if (!LunaticFamily.marryRequests.containsKey(playerUUID) && !LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                            sender.sendMessage(Language.prefix + Language.getMessage("marry_accept_no_request"));
                        } else if (LunaticFamily.marryPriestRequests.containsValue(playerUUID)) {
                            sender.sendMessage(Language.prefix + Language.getMessage("marry_accept_open_request_partner"));
                        } else {
                            //check for request
                            if (LunaticFamily.marryRequests.containsKey(playerUUID)) {

                                String partnerUUID = LunaticFamily.marryRequests.get(playerUUID);
                                FamilyPlayer partnerFam = new FamilyPlayer(partnerUUID);

                                if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                                    int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                                    sender.sendMessage(Language.prefix + Language.getMessage("marry_accept_too_many_children").replace("%partner%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                                } else if (!partnerFam.isOnline()) {
                                    sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", partnerFam.getName()));
                                } else if (LunaticFamily.marryPriest.containsKey(partnerUUID)) {
                                    String priestUUID = LunaticFamily.marryPriest.get(partnerUUID);
                                    FamilyPlayer priestFam = new FamilyPlayer(priestUUID);

                                    if (!priestFam.hasEnoughMoney("marry_priest")) {
                                        sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money").replace("%player%", priestFam.getName()));
                                    } else if (!playerFam.hasEnoughMoney("marry_priest_player")) {
                                        sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money").replace("%player%", player.getName()));
                                    } else if (!partnerFam.hasEnoughMoney("marry_priest_player")) {
                                        sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", partnerFam.getName()));
                                    } else {

                                        priestFam.withdrawPlayer("marry_priest");
                                        playerFam.withdrawPlayer("marry_priest_player");
                                        partnerFam.withdrawPlayer("marry_priest_player");

                                        player.chat(Language.getMessage("marry_yes"));

                                        new BukkitRunnable() {
                                            public void run() {
                                                priestFam.chat(Language.getMessage("marry_priest_complete"));
                                            }
                                        }.runTaskLater(plugin, 20L);

                                        LunaticFamily.marryRequests.remove(playerUUID);
                                        LunaticFamily.marryPriestRequests.remove(partnerUUID);
                                        LunaticFamily.marryPriest.remove(partnerUUID);

                                        playerFam.marry(partnerFam.getID(), priestFam.getID());
                                    }
                                } else {
                                    if (!partnerFam.hasEnoughMoney("marry_proposing_player")) {
                                        sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", partnerFam.getName()));
                                    } else if (!playerFam.hasEnoughMoney("marry_proposed_player")) {
                                        sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money").replace("%player%", player.getName()));
                                    } else {
                                        sender.sendMessage(Language.prefix + Language.getMessage("marry_accept_complete"));
                                        partnerFam.sendMessage(Language.prefix + Language.getMessage("marry_accept_complete"));

                                        playerFam.withdrawPlayer("marry_proposed_player");
                                        partnerFam.withdrawPlayer("marry_proposing_player");

                                        LunaticFamily.marryRequests.remove(playerUUID);
                                        LunaticFamily.marryPriestRequests.remove(partnerUUID);
                                        LunaticFamily.marryPriest.remove(partnerUUID);

                                        playerFam.marry(partnerFam.getID());
                                    }
                                }

                            }

                            //check for priest request
                            else if (LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {

                                String partnerUUID = LunaticFamily.marryPriestRequests.get(playerUUID);
                                FamilyPlayer partnerFam = new FamilyPlayer(partnerUUID);
                                OfflinePlayer partnerPlayer = Bukkit.getOfflinePlayer(UUID.fromString(partnerUUID));

                                if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                                    int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                                    sender.sendMessage(Language.prefix + Language.getMessage("marry_accept_too_many_children").replace("%partner%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                                } else {

                                    String priestUUID = LunaticFamily.marryPriest.get(playerUUID);
                                    FamilyPlayer priestFam = new FamilyPlayer(priestUUID);

                                    if (!partnerFam.hasEnoughMoney("marry_proposing_player")) {
                                        sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", partnerFam.getName()));
                                    } else if (!playerFam.hasEnoughMoney("marry_proposed_player")) {
                                        sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money").replace("%player%", player.getName()));
                                    } else {

                                        LunaticFamily.marryPriestRequests.remove(playerUUID);
                                        LunaticFamily.marryRequests.put(partnerUUID, playerUUID);
                                        player.chat(Language.getMessage("marry_yes"));

                                        new BukkitRunnable() {
                                            public void run() {
                                                priestFam.chat(Language.getMessage("marry_accept_request").replace("%player1%", partnerFam.getName()).replace("%player2%", playerFam.getName()));
                                            }
                                        }.runTaskLater(plugin, 20L);


                                        partnerFam.sendMessage(Utils.createClickableMessage(
                                                "",
                                                Language.getMessage("marry_yes"),
                                                "/marry accept",
                                                Language.getMessage("marry_no"),
                                                "/marry deny"));
                                    }
                                }
                            }
                        }
                    } else if (checkIsSubcommand("deny", subcommand)) {

                        if (!LunaticFamily.marryRequests.containsKey(playerUUID) && !LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                            sender.sendMessage(Language.prefix + Language.getMessage("marry_deny_no_request"));
                        } else {
                            if (LunaticFamily.marryRequests.containsKey(playerUUID)) {
                                String partnerUUID = LunaticFamily.marryRequests.get(playerUUID);
                                FamilyPlayer partnerFam = new FamilyPlayer(partnerUUID);
                                if (!LunaticFamily.marryPriest.containsKey(partnerUUID)) {
                                    partnerFam.sendMessage(Language.prefix + Language.getMessage("marry_deny_denied").replace("%player%", playerFam.getName()));
                                } else {
                                    String priestUUID = LunaticFamily.marryPriest.get(partnerUUID);
                                    FamilyPlayer priestFam = new FamilyPlayer(priestUUID);
                                    player.chat(Language.getMessage("marry_deny_no"));
                                    priestFam.chat(Language.getMessage("marry_deny_cancel"));
                                    LunaticFamily.marryPriest.remove(partnerUUID);
                                }
                                LunaticFamily.marryRequests.remove(playerUUID);

                            } else if (LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                                player.chat(Language.getMessage("marry_deny_no"));
                                String priestUUID = LunaticFamily.marryPriest.get(playerUUID);
                                FamilyPlayer priestFam = new FamilyPlayer(priestUUID);
                                priestFam.chat(Language.getMessage("marry_deny_cancel"));
                                LunaticFamily.marryPriestRequests.remove(playerUUID);
                                LunaticFamily.marryPriest.remove(playerUUID);
                            }
                        }
                    }
                    else if (checkIsSubcommand("divorce", subcommand)) {

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
                            sender.sendMessage(Language.prefix + Language.getMessage("marry_divorce_no_partner"));
                        } else if (!confirm) {
                            sender.sendMessage(Utils.createClickableMessage(
                                    Language.getMessage("marry_divorce_confirm"),
                                    Language.getMessage("confirm"),
                                    "/marry divorce confirm",
                                    Language.getMessage("cancel"),
                                    "/marry divorce cancel"));
                        } else if (cancel) {
                            sender.sendMessage(Language.prefix + Language.getMessage("marry_divorce_cancel"));
                        } else if (!playerFam.hasEnoughMoney("marry_divorce_leaving_player")) {
                            sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                        } else if (!playerFam.getPartner().hasEnoughMoney("marry_divorce_left_player")) {
                            sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", playerFam.getPartner().getName()));
                            sender.sendMessage(Utils.createClickableMessage(
                                    Language.getMessage("take_payment_confirm"),
                                    Language.getMessage("confirm"),
                                    "/marry divorce confirm force",
                                    Language.getMessage("cancel"),
                                    "/marry divorce cancel"));
                        } else {
                            sender.sendMessage(Language.prefix + Language.getMessage("marry_divorce_divorced"));
                            playerFam.getPartner().sendMessage(Language.prefix + Language.getMessage("marry_divorce_divorced"));

                            playerFam.withdrawPlayer("marry_divorce_leaving_player");
                            playerFam.getPartner().withdrawPlayer("marry_divorce_leaving_player");

                            playerFam.divorce();
                        }
                    } else if (checkIsSubcommand("kiss", subcommand)) {

                        if (!playerFam.isMarried()) {
                            sender.sendMessage(Language.prefix + Language.getMessage("marry_kiss_no_partner"));
                        } else if (Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) == null) {
                            sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(playerFam.getPartner().getUUID())).getName()));
                        } else {
                            Player partnerPlayer = Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID()));

                            if (!Utils.isInRange(partnerPlayer.getLocation(), player.getLocation(), 3)) {
                                sender.sendMessage(Language.prefix + Language.getMessage("player_too_far_away").replace("%player%", partnerPlayer.getName()));
                            } else {
                                Location location = Utils.getPositionBetweenLocations(player.getLocation(), partnerPlayer.getLocation());
                                location.setY(location.getY() + 2);

                                for (int i = 0; i < 6; i++) {
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> Utils.spawnParticles(location, Particle.HEART), i * 5L);
                                }
                            }
                        }
                    } else if (checkIsSubcommand("gift", subcommand)) {
                        if (!playerFam.isMarried()) {
                            sender.sendMessage(Language.prefix + Language.getMessage("marry_gift_no_partner"));
                        } else if (!player.hasPermission("lunaticFamily.marry.gift")) {
                            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                        } else if (Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) == null) {
                            sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(playerFam.getPartner().getUUID())).getName()));
                        } else if (player.getInventory().getItemInMainHand().isEmpty()) {
                            sender.sendMessage(Language.prefix + Language.getMessage("marry_gift_hand_empty"));
                        } else {
                            Player partnerPlayer = playerFam.getPartner().getPlayer();
                            if (partnerPlayer.getInventory().firstEmpty() == -1) {
                                sender.sendMessage(Language.prefix + Language.getMessage("marry_gift_partner_full_inv"));
                            } else {
                                ItemStack item = player.getInventory().getItemInMainHand();
                                player.getInventory().remove(item);
                                partnerPlayer.getInventory().addItem(item);
                                Material material = item.getType();
                                int amount = item.getAmount();

                                ItemMeta itemMeta = item.getItemMeta();



                                String[] msgPlayer = Language.getMessage("marry_gift_sent").split("%item%");
                                String[] msgPartner = Language.getMessage("marry_gift_got").split("%item%");

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
                                    TranslatableComponent componentItem = new TranslatableComponent(Utils.getItemKey(material));

                                    player.sendMessage(componentPlayer1, componentAmount, componentItem, componentPlayer2);
                                    partnerPlayer.sendMessage(componentPartner1, componentAmount, componentItem, componentPartner2);
                                }



                            }

                        }
                    } else if (checkIsSubcommand("backpack", subcommand)) {
                        if (!player.hasPermission("lunaticFamily.marry.backpack")) {
                            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                        } else if(!Config.enabledMinepacks) {
                            sender.sendMessage(Language.prefix + Language.getMessage("disabled_feature"));
                        } else if (!playerFam.isMarried()) {
                            sender.sendMessage(Language.prefix + Language.getMessage("marry_backpack_no_partner"));
                        } else if (!Config.marryBackpackOffline && Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) == null) {
                            sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", playerFam.getPartner().getName()));
                        } else {
                            OfflinePlayer partnerPlayer = playerFam.getPartner().getOfflinePlayer();
                            sender.sendMessage(Language.prefix + Language.getMessage("marry_backpack_open"));
                            Minepacks.getMinepacks().openBackpack(player, partnerPlayer, true);
                        }
                    } else if (checkIsSubcommand("list", subcommand)) {
                        int page = 1;
                        if (args.length > 1) {
                            try {
                                page = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(Language.prefix + Language.getMessage("marry_list_no_number").replace("%input%", args[1]));
                            }
                        }

                        List<Integer> marryList = LunaticFamily.getDatabase().getMarryList(page, 10);
                        TextComponent msg = new TextComponent(Language.prefix + Language.getMessage("marry_list") + "\n");
                        int index = 1 + (10*(page-1));
                        for (Integer e : marryList) {
                            FamilyPlayer player1Fam = new FamilyPlayer(e);
                            FamilyPlayer player2Fam = new FamilyPlayer(player1Fam.getPartner().getID());

                            TextComponent text = new TextComponent(Language.prefix + " " + index + ": " + player1Fam.getName() + " ❤ " + player2Fam.getName() + "\n");

                            String hoverText = " (" + player1Fam.getMarriageDate() + ")";
                            if (player1Fam.getPriest() != null) {
                                hoverText = hoverText + " -> " + player1Fam.getPriest().getName();
                            }

                            text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
                            msg.addExtra(text);

                            index++;
                        }
                        sender.sendMessage(msg);

                    } else if (checkIsSubcommand("help", subcommand)) {
                        String[] subcommandsHelp = {"propose", "priest", "list", "divorce", "kiss", "gift"};

                        String msg = Language.prefix + " " + Language.getMessage(label + "_help") + "\n";

                        for (String sc : subcommandsHelp) {
                            msg = msg + Language.prefix + " " + Language.getMessage(label + "_" + sc + "_help") + "\n";
                        }
                        sender.sendMessage(msg);
                    } else {
                        sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                    }
                }
            }
        }

        return true;

    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {

        List<String> marrySubcommands = Language.marrySubcommandsAliases;
        List<String> marryAdminSubcommands = Language.marryAdminSubcommandsAliases;
        List<String> list = new ArrayList<>();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("marry")) {
                if (args.length == 0) {
                    if (player.hasPermission("lunaticFamily.admin.marry")) {
                        list.addAll(marryAdminSubcommands);
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
        return null;
    }

    private boolean checkIsSubcommand(final String subcommand, final String arg) {
        return subcommand.equalsIgnoreCase(arg) || Language.getAliases("marry", subcommand).stream().anyMatch(element -> arg.equalsIgnoreCase(element));
    }
}

