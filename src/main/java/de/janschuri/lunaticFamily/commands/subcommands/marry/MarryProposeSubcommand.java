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

public class MarryProposeSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "propose";
    private static final String permission = "lunaticfamily.marry";

    public MarryProposeSubcommand() {
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
                } else {

                    partnerFam.sendMessage(Utils.createClickableMessage(
                            Language.getMessage("marry_propose_request").replace("%player1%", partnerFam.getName()).replace("%player2%", playerFam.getName()),
                            Language.getMessage("marry_yes"),
                            "/lunaticfamily:marry accept",
                            Language.getMessage("marry_no"),
                            "/lunaticfamily:marry deny"));


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
                    }.runTaskLater(LunaticFamily.getInstance(), 600L);
                }
            }
        }
    }
}
