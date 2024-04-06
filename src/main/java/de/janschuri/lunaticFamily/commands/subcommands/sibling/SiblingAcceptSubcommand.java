package de.janschuri.lunaticFamily.commands.subcommands.sibling;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SiblingAcceptSubcommand extends Subcommand {
    private static final String mainCommand = "sibling";
    private static final String name = "accept";
    private static final String permission = "lunaticfamily.sibling";

    public SiblingAcceptSubcommand() {
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

                    for (String command : Config.successCommands.get("sibling")) {
                        command = command.replace("%player1%", playerFam.getName()).replace("%player2%", siblingFam.getName());
                        Utils.sendConsoleCommand(command);
                    }

                    playerFam.withdrawPlayer("sibling_proposed_player");
                    siblingFam.withdrawPlayer("sibling_proposing_player");
                }
            }
        }
    }
}
