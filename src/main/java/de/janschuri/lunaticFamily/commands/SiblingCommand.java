package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SiblingCommand implements CommandExecutor, TabCompleter {

    private final LunaticFamily plugin;

    public SiblingCommand(LunaticFamily plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            if (!sender.hasPermission("lunaticFamily." + label)) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
            } else {
                String[] subcommandsHelp = {"propose", "unsibling"};

                String msg = Language.prefix + " " + Language.getMessage(label + "_help") + "\n";

                for (String subcommand : subcommandsHelp) {
                    msg = msg + Language.prefix + " " + Language.getMessage(label + "_" + subcommand + "_help") + "\n";
                }
                sender.sendMessage(msg);
            }
        } else {
            final String subcommand = args[0];
            if (checkIsSubcommand("set", subcommand)) {

                boolean forced = false;

                if (args.length > 3) {
                    if (args[3].equalsIgnoreCase("force")) {
                        forced = true;
                    }
                }

                if (!sender.hasPermission("lunaticFamily.admin.sibling")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                } else if (args.length < 2) {
                    sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                } else if (!Utils.playerExists(args[1]) && !forced) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                } else if (!Utils.playerExists(args[2]) && !forced) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[2]));
                } else if (args[1].equalsIgnoreCase(args[2])) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_set_same_player"));
                } else {

                    String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);
                    String player2UUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                    FamilyPlayer player2Fam = new FamilyPlayer(player2UUID);

                    if (player1Fam.isAdopted() && player2Fam.isAdopted()) {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_sibling_set_both_adopted").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                    } else if (player1Fam.isAdopted()) {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_sibling_is_adopted").replace("%player%", player1Fam.getName()));

                    } else if (player2Fam.isAdopted()) {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_sibling_is_adopted").replace("%player%", player2Fam.getName()));
                    } else {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_sibling_added").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                        player1Fam.addSibling(player2Fam.getID());
                    }
                }
            } else if (checkIsSubcommand("unset", subcommand)) {
                if (!sender.hasPermission("lunaticFamily.admin.sibling")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                } else if (args.length < 2) {
                    sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                } else if (!Utils.playerExists(args[1])) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", Utils.getName(args[1])));
                } else {
                    String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);

                    if (!player1Fam.hasSibling()) {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_sibling_unset_no_sibling").replace("%player%", player1Fam.getName()));
                    } else {
                        FamilyPlayer siblingFam = player1Fam.getSibling();
                        player1Fam.removeSibling();
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_sibling_unset_sibling").replace("%player1%", player1Fam.getName()).replace("%player2%", siblingFam.getName()));
                    }
                }
            } else if (!(sender instanceof Player)) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
            } else {
                Player player = (Player) sender;

                if (!player.hasPermission("lunaticFamily.sibling")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));

                } else {
                    String playerUUID = player.getUniqueId().toString();
                    FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

                    if (checkIsSubcommand("propose", subcommand)) {
                        if (playerFam.hasSibling()) {
                            sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_has_sibling").replace("%player%", playerFam.getName()));
                        } else if (playerFam.isAdopted()) {
                            sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_is_adopted").replace("%player%", playerFam.getName()));
                        } else if (args.length < 2) {
                            sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                        } else if (!Utils.playerExists(args[1])) {
                            sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                        } else if (Bukkit.getPlayer(args[1]) == null) {
                            sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", Utils.getName(args[1])));
                        } else {
                            String siblingUUID = Bukkit.getPlayer(args[1]).getUniqueId().toString();
                            Player siblingPlayer = Bukkit.getPlayer(args[1]);
                            FamilyPlayer siblingFam = new FamilyPlayer(siblingUUID);
                            if (playerFam.getID() == siblingFam.getID()) {
                                sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_self_request"));
                            } else if (playerFam.isFamilyMember(siblingFam.getID())) {
                                sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_family_request").replace("%player%", siblingFam.getName()));
                            } else if (siblingFam.isFamilyMember(playerFam.getID())) {
                                sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_family_request").replace("%player%", siblingFam.getName()));
                            } else if (siblingFam.isAdopted()) {
                                sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_sibling_is_adopted").replace("%player%", siblingFam.getName()));
                            } else if (LunaticFamily.siblingRequests.containsKey(siblingUUID)) {
                                sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_open_request").replace("%player%", siblingFam.getName()));
                            } else if (!Utils.isInRange(player.getLocation(), siblingPlayer.getLocation(), 5)) {
                                sender.sendMessage(Language.prefix + Language.getMessage("player_too_far_away").replace("%player%", siblingFam.getName()));
                            } else if (!playerFam.hasEnoughMoney("sibling_proposing_player")) {
                                sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                            } else {
                                siblingFam.sendMessage(Utils.createClickableMessage(
                                        Language.getMessage("sibling_propose_request").replace("%player%", siblingFam.getName()),
                                        Language.getMessage("accept"),
                                        "/sibling accept",
                                        Language.getMessage("deny"),
                                        "/sibling deny"));

                                LunaticFamily.siblingRequests.put(siblingUUID, playerUUID);

                                sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_request_sent").replace("%player%", siblingFam.getName()));

                                new BukkitRunnable() {
                                    public void run() {
                                        if (LunaticFamily.siblingRequests.containsKey(siblingUUID)) {
                                            LunaticFamily.siblingRequests.remove(siblingUUID);
                                            siblingPlayer.sendMessage(Language.prefix + Language.getMessage("sibling_propose_request_expired").replace("%player%", playerFam.getName()));

                                            sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_request_sent_expired").replace("%player%", siblingFam.getName()));
                                        }
                                    }
                                }.runTaskLater(plugin, 600L);
                            }
                        }

                    } else if (checkIsSubcommand("accept", subcommand)) {
                        if (!LunaticFamily.siblingRequests.containsKey(playerUUID)) {
                            sender.sendMessage(Language.prefix + Language.getMessage("sibling_accept_no_request"));
                        } else {
                            String siblingUUID = LunaticFamily.siblingRequests.get(playerUUID);
                            FamilyPlayer siblingFam = new FamilyPlayer(siblingUUID);

                            if (playerFam.getChildrenAmount() + siblingFam.getChildrenAmount() > 2) {
                                int amountDiff = playerFam.getChildrenAmount() + siblingFam.getChildrenAmount() - 2;
                                sender.sendMessage(Language.prefix + Language.getMessage("marry_accept_too_many_children").replace("%partner%", siblingFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                            } else if (Bukkit.getPlayer(UUID.fromString(siblingUUID)) == null) {
                                sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", siblingFam.getName()));
                            } else if (!playerFam.hasEnoughMoney("sibling_proposed_player")) {
                                sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                            } else if (!playerFam.hasEnoughMoney("sibling_proposing_player")) {
                                sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", siblingFam.getName()));
                            } else {

                                sender.sendMessage(Language.prefix + Language.getMessage("sibling_accept_complete").replace("%player%", siblingFam.getName()));
                                Bukkit.getPlayer(UUID.fromString(siblingUUID)).sendMessage(Language.prefix + Language.getMessage("sibling_accept_complete").replace("%player%", playerFam.getName()));

                                LunaticFamily.siblingRequests.remove(playerUUID);
                                LunaticFamily.siblingRequests.remove(siblingUUID);
                                playerFam.addSibling(siblingFam.getID());

                                playerFam.withdrawPlayer("sibling_proposed_player");
                                siblingFam.withdrawPlayer("sibling_proposing_player");
                            }
                        }
                    } else if (checkIsSubcommand("deny", subcommand)) {
                        if (!LunaticFamily.siblingRequests.containsKey(playerUUID) && !LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                            sender.sendMessage(Language.prefix + Language.getMessage("propose_deny_no_request"));
                        } else {
                            if (!LunaticFamily.siblingRequests.containsKey(playerUUID)) {

                            }
                            String partnerUUID = LunaticFamily.marryRequests.get(playerUUID);
                            Bukkit.getPlayer(UUID.fromString(partnerUUID)).sendMessage(Language.prefix + Language.getMessage("propose_deny_denied").replace("%player%", playerFam.getName()));
                            LunaticFamily.marryRequests.remove(playerUUID);
                        }
                    } else if (checkIsSubcommand("unsibling", subcommand)) {
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

                        if (!playerFam.hasSibling()) {
                            sender.sendMessage(Language.prefix + Language.getMessage("sibling_unsibling_no_sibling"));
                        } else if (!confirm) {
                            sender.sendMessage(Utils.createClickableMessage(
                                    Language.getMessage("sibling_unsibling_confirm"),
                                    Language.getMessage("confirm"),
                                    "/sibling unsibling confirm",
                                    Language.getMessage("cancel"),
                                    "/sibling unsibling cancel"));
                        } else if (cancel) {
                            sender.sendMessage(Language.prefix + Language.getMessage("sibling_unsibling_cancel"));
                        } else if (playerFam.isAdopted()) {
                            sender.sendMessage(Language.prefix + Language.getMessage("sibling_unsibling_adopted"));
                        } else if (!force && !playerFam.hasEnoughMoney("sibling_unsibling_leaving_player")) {
                            sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                        } else if (!force && !playerFam.getSibling().hasEnoughMoney("sibling_unsibling_left_player")) {
                            sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", playerFam.getSibling().getName()));
                            sender.sendMessage(Utils.createClickableMessage(
                                    Language.getMessage("take_payment_confirm"),
                                    Language.getMessage("confirm"),
                                    "/sibling unsibling confirm force",
                                    Language.getMessage("cancel"),
                                    "/sibling unsibling cancel"));
                        } else {
                            sender.sendMessage(Language.prefix + Language.getMessage("sibling_unsibling_complete"));
                            playerFam.getSibling().sendMessage(Language.prefix + Language.getMessage("sibling_unsiblinged_complete"));

                            if (force) {
                                playerFam.withdrawPlayer("sibling_unsibling_leaving_player");
                                playerFam.withdrawPlayer("sibling_unsibling_left_player");
                            } else {
                                playerFam.withdrawPlayer("sibling_unsibling_leaving_player");
                                playerFam.getSibling().withdrawPlayer("sibling_unsibling_left_player");
                            }
                            playerFam.removeSibling();
                        }
                    } else if (checkIsSubcommand("help", subcommand)) {
                        String[] subcommandsHelp = {"propose", "unsibling"};

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

        List<String> siblingSubcommands = Language.siblingSubcommandsAliases;
        List<String> siblingAdminSubcommands = Language.siblingAdminSubcommandsAliases;
        List<String> list = new ArrayList<>();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("sibling")) {
                if (args.length == 0) {
                    if (player.hasPermission("lunaticFamily.admin.sibling")) {
                        list.addAll(siblingAdminSubcommands);
                    }
                    if (player.hasPermission("lunaticFamily.sibling")) {
                        list.addAll(siblingSubcommands);
                    }
                    Collections.sort(list);
                    return list;
                } else if (args.length == 1) {
                    if (player.hasPermission("lunaticFamily.admin.sibling")) {
                        for (String s : siblingAdminSubcommands) {
                            if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
                                list.add(s);
                            }
                        }
                    }
                    if (player.hasPermission("lunaticFamily.sibling")) {
                        for (String s : siblingSubcommands) {
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
        return subcommand.equalsIgnoreCase(arg) || Language.getAliases("sibling", subcommand).stream().anyMatch(element -> arg.equalsIgnoreCase(element));
    }
}