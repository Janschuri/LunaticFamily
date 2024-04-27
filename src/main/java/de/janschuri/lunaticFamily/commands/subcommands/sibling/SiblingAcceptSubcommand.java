package de.janschuri.lunaticFamily.commands.subcommands.sibling;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class SiblingAcceptSubcommand extends Subcommand {
    private static final String mainCommand = "sibling";
    private static final String name = "accept";
    private static final String permission = "lunaticfamily.sibling";

    public SiblingAcceptSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!(sender instanceof AbstractPlayerSender)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            AbstractPlayerSender player = (AbstractPlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (!LunaticFamily.siblingRequests.containsKey(playerUUID)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("sibling_accept_no_request"));
            } else {
                UUID siblingUUID = LunaticFamily.siblingRequests.get(playerUUID);
                AbstractPlayerSender sibling = player.getPlayerCommandSender(siblingUUID);
                FamilyPlayer siblingFam = new FamilyPlayer(siblingUUID);

                if (playerFam.getChildrenAmount() + siblingFam.getChildrenAmount() > 2) {
                    int amountDiff = playerFam.getChildrenAmount() + siblingFam.getChildrenAmount() - 2;
                    sender.sendMessage(language.getPrefix() + language.getMessage("marry_accept_too_many_children").replace("%partner%", siblingFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                    return true;
                }
                if (sibling.isOnline()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("player_offline").replace("%player%", siblingFam.getName()));
                    return true;
                }

                if (!Utils.getUtils().isPlayerOnWhitelistedServer(sibling.getUniqueId())) {
                    player.sendMessage(language.getPrefix() + language.getMessage("player_not_on_whitelisted_server").replace("%player%", sibling.getName().replace("%server%", sibling.getServerName())));
                    return true;
                }

                if (!Utils.getUtils().hasEnoughMoney(playerUUID, "sibling_proposed_player")) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
                    return true;
                }
                if (!Utils.getUtils().hasEnoughMoney(siblingUUID, "sibling_proposing_player")) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", siblingFam.getName()));
                    return true;
                }


                    sender.sendMessage(language.getPrefix() + language.getMessage("sibling_accept_complete").replace("%player%", siblingFam.getName()));
                    sibling.sendMessage(language.getPrefix() + language.getMessage("sibling_accept_complete").replace("%player%", playerFam.getName()));

                    LunaticFamily.siblingRequests.remove(playerUUID);
                    LunaticFamily.siblingRequests.remove(siblingUUID);
                    playerFam.addSibling(siblingFam.getID());

                    for (String command : PluginConfig.successCommands.get("sibling")) {
                        command = command.replace("%player1%", playerFam.getName()).replace("%player2%", siblingFam.getName());
                        Utils.getUtils().sendConsoleCommand(command);
                    }

                    Utils.getUtils().withdrawMoney(playerUUID, "sibling_proposed_player");
                    Utils.getUtils().withdrawMoney(siblingUUID, "sibling_proposing_player");

            }
        }
        return true;
    }
}
