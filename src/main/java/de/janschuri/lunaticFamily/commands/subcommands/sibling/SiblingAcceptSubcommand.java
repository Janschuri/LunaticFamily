package de.janschuri.lunaticFamily.commands.subcommands.sibling;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.senders.CommandSender;
import de.janschuri.lunaticFamily.commands.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;

import java.util.UUID;

public class SiblingAcceptSubcommand extends Subcommand {
    private static final String mainCommand = "sibling";
    private static final String name = "accept";
    private static final String permission = "lunaticfamily.sibling";

    public SiblingAcceptSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof PlayerCommandSender)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            PlayerCommandSender player = (PlayerCommandSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = player.getFamilyPlayer();

            if (!LunaticFamily.siblingRequests.containsKey(playerUUID.toString())) {
                sender.sendMessage(Language.prefix + Language.getMessage("sibling_accept_no_request"));
            } else {
                UUID siblingUUID = UUID.fromString(LunaticFamily.siblingRequests.get(playerUUID.toString()));
                PlayerCommandSender sibling = player.getPlayerCommandSender(siblingUUID);
                FamilyPlayer siblingFam = sibling.getFamilyPlayer();

                if (playerFam.getChildrenAmount() + siblingFam.getChildrenAmount() > 2) {
                    int amountDiff = playerFam.getChildrenAmount() + siblingFam.getChildrenAmount() - 2;
                    sender.sendMessage(Language.prefix + Language.getMessage("marry_accept_too_many_children").replace("%partner%", siblingFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                } else if (sibling.isOnline()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", siblingFam.getName()));
                } else if (!player.hasEnoughMoney("sibling_proposed_player")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                } else if (!player.hasEnoughMoney("sibling_proposing_player")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", siblingFam.getName()));
                } else {

                    sender.sendMessage(Language.prefix + Language.getMessage("sibling_accept_complete").replace("%player%", siblingFam.getName()));
                    sibling.sendMessage(Language.prefix + Language.getMessage("sibling_accept_complete").replace("%player%", playerFam.getName()));

                    LunaticFamily.siblingRequests.remove(playerUUID);
                    LunaticFamily.siblingRequests.remove(siblingUUID);
                    playerFam.addSibling(siblingFam.getID());

                    for (String command : PluginConfig.successCommands.get("sibling")) {
                        command = command.replace("%player1%", playerFam.getName()).replace("%player2%", siblingFam.getName());
                        Utils.getUtils().sendConsoleCommand(command);
                    }

                    player.withdrawMoney("sibling_proposed_player");
                    sibling.withdrawMoney("sibling_proposing_player");
                }
            }
        }
        return true;
    }
}
