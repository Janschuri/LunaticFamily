package de.janschuri.lunaticFamily.commands.marry.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class MarryAcceptSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "accept";
    private static final String permission = "lunaticfamily.marry";

    public MarryAcceptSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            Player player = (Player) sender;
            String playerUUID = player.getUniqueId().toString();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (!LunaticFamily.marryRequests.containsKey(playerUUID) && !LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_accept_no_request"));
            } else if (LunaticFamily.marryPriestRequests.containsValue(playerUUID)) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_accept_open_request_partner"));
            } else {

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

                            for (String command : Config.marrySuccessCommands) {
                                Utils.sendConsoleCommand(command);
                            }
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

                            for (String command : Config.marrySuccessCommands) {
                                Utils.sendConsoleCommand(command);
                            }
                        }
                    }

                }

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
        }
    }
}
