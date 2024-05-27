package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;

public class SiblingAcceptSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "sibling";
    private static final String NAME = "accept";
    private static final String PERMISSION = "lunaticfamily.sibling";

    public SiblingAcceptSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getPrefix() + getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else {
            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

            if (!LunaticFamily.siblingRequests.containsKey(playerUUID)) {
                sender.sendMessage(getPrefix() + getMessage("sibling_accept_no_request"));
            } else {
                UUID siblingUUID = LunaticFamily.siblingRequests.get(playerUUID);
                PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingUUID);
                FamilyPlayerImpl siblingFam = new FamilyPlayerImpl(siblingUUID);

                if (playerFam.getChildrenAmount() + siblingFam.getChildrenAmount() > 2) {
                    int amountDiff = playerFam.getChildrenAmount() + siblingFam.getChildrenAmount() - 2;
                    sender.sendMessage(getPrefix() + getMessage("marry_accept_too_many_children").replace("%partner%", siblingFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                    return true;
                }
                if (!sibling.isOnline()) {
                    sender.sendMessage(getPrefix() + getMessage("player_offline").replace("%player%", siblingFam.getName()));
                    return true;
                }

                if (!Utils.isPlayerOnRegisteredServer(sibling.getUniqueId())) {
                    player.sendMessage(getPrefix() + getMessage("player_not_on_whitelisted_server").replace("%player%", sibling.getName().replace("%server%", sibling.getServerName())));
                    return true;
                }

                if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "sibling_proposed_player")) {
                    sender.sendMessage(getPrefix() + getMessage("not_enough_money"));
                    return true;
                }
                if (!Utils.hasEnoughMoney(player.getServerName(), siblingUUID, "sibling_proposing_player")) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_enough_money").replace("%player%", siblingFam.getName()));
                    return true;
                }


                    sender.sendMessage(getPrefix() + getMessage("sibling_accept_complete").replace("%player%", siblingFam.getName()));
                    sibling.sendMessage(getPrefix() + getMessage("sibling_accept_complete").replace("%player%", playerFam.getName()));

                    LunaticFamily.siblingRequests.remove(playerUUID);
                    LunaticFamily.siblingRequests.remove(siblingUUID);
                    playerFam.addSibling(siblingFam.getId());

                    for (String command : LunaticFamily.getConfig().getSuccessCommands("sibling")) {
                        command = command.replace("%player1%", playerFam.getName()).replace("%player2%", siblingFam.getName());
                        LunaticLib.getPlatform().sendConsoleCommand(command);
                    }

                    Utils.withdrawMoney(player.getServerName(), playerUUID, "sibling_proposed_player");
                    Utils.withdrawMoney(player.getServerName(), siblingUUID, "sibling_proposing_player");

            }
        }
        return true;
    }
}
