package de.janschuri.lunaticFamily.commands.sibling.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SiblingProposeSubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "propose";
    private static final String permission = "lunaticfamily.sibling";

    public SiblingProposeSubcommand() {
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
        }
    }
}
