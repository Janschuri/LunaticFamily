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
    private static final String MAIN_COMMAND = "sibling";
    private static final String NAME = "accept";
    private static final String PERMISSION = "lunaticfamily.sibling";

    public SiblingAcceptSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!(sender instanceof AbstractPlayerSender)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            AbstractPlayerSender player = (AbstractPlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (!LunaticFamily.siblingRequests.containsKey(playerUUID)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("sibling_accept_no_request"));
            } else {
                UUID siblingUUID = LunaticFamily.siblingRequests.get(playerUUID);
                AbstractPlayerSender sibling = AbstractSender.getPlayerSender(siblingUUID);
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

                if (!Utils.isPlayerOnRegisteredServer(sibling.getUniqueId())) {
                    player.sendMessage(language.getPrefix() + language.getMessage("player_not_on_whitelisted_server").replace("%player%", sibling.getName().replace("%server%", sibling.getServerName())));
                    return true;
                }

                if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "sibling_proposed_player")) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
                    return true;
                }
                if (!Utils.hasEnoughMoney(player.getServerName(), siblingUUID, "sibling_proposing_player")) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", siblingFam.getName()));
                    return true;
                }


                    sender.sendMessage(language.getPrefix() + language.getMessage("sibling_accept_complete").replace("%player%", siblingFam.getName()));
                    sibling.sendMessage(language.getPrefix() + language.getMessage("sibling_accept_complete").replace("%player%", playerFam.getName()));

                    LunaticFamily.siblingRequests.remove(playerUUID);
                    LunaticFamily.siblingRequests.remove(siblingUUID);
                    playerFam.addSibling(siblingFam.getID());

                    for (String command : PluginConfig.getSuccessCommands("sibling")) {
                        command = command.replace("%player1%", playerFam.getName()).replace("%player2%", siblingFam.getName());
                        Utils.sendConsoleCommand(command);
                    }

                    Utils.withdrawMoney(player.getServerName(), playerUUID, "sibling_proposed_player");
                    Utils.withdrawMoney(player.getServerName(), siblingUUID, "sibling_proposing_player");

            }
        }
        return true;
    }
}
