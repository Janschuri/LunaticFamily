package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MarryPriestSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "priest";
    private static final String permission = "lunaticfamily.marry.priest";

    public MarryPriestSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            Player player = (Player) sender;
            String playerUUID = player.getUniqueId().toString();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

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
                                    "/lunaticfamily:marry accept",
                                    Language.getMessage("marry_no"),
                                    "/lunaticfamily:marry deny"));

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
                            }.runTaskLater(LunaticFamily.getInstance(), 600L);
                        }
                    }
                }
            }
        }
    }
}
