package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.SiblingSubcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;

public class SiblingAcceptSubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey noRequestMK = new CommandMessageKey(this,"no_request");
    private final CommandMessageKey completeMK = new CommandMessageKey(this,"complete");


    @Override
    public String getPermission() {
        return "lunaticfamily.sibling";
    }

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public SiblingSubcommand getParentCommand() {
        return new SiblingSubcommand();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
        } else if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
        } else {
            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

            if (!LunaticFamily.siblingRequests.containsKey(playerUUID)) {
                sender.sendMessage(getMessage(noRequestMK));
            } else {
                UUID siblingUUID = LunaticFamily.siblingRequests.get(playerUUID);
                PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingUUID);
                FamilyPlayerImpl siblingFam = new FamilyPlayerImpl(siblingUUID);

                if (!sibling.isOnline()) {
                    sender.sendMessage(getMessage(PLAYER_OFFLINE_MK)
                            .replaceText(getTextReplacementConfig("%player%", siblingFam.getName())));
                    return true;
                }

                if (!Utils.isPlayerOnRegisteredServer(sibling.getUniqueId())) {
                    player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK)
                            .replaceText(getTextReplacementConfig("%player%", sibling.getName()))
                            .replaceText(getTextReplacementConfig("%server%", sibling.getServerName())));
                    return true;
                }

                if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.SIBLING_PROPOSED_PLAYER)) {
                    sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
                    return true;
                }
                if (!Utils.hasEnoughMoney(player.getServerName(), siblingUUID, WithdrawKey.SIBLING_PROPOSING_PLAYER)) {
                    sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                            .replaceText(getTextReplacementConfig("%player%", siblingFam.getName())));
                    return true;
                }


                    sender.sendMessage(getMessage(completeMK)
                            .replaceText(getTextReplacementConfig("%player%", siblingFam.getName())));
                    sibling.sendMessage(getMessage(completeMK)
                            .replaceText(getTextReplacementConfig("%player%", playerFam.getName())));

                    LunaticFamily.siblingRequests.remove(playerUUID);
                    LunaticFamily.siblingRequests.remove(siblingUUID);
                    playerFam.addSibling(siblingFam.getId());

                    for (String command : LunaticFamily.getConfig().getSuccessCommands("sibling")) {
                        command = command.replace("%player1%", playerFam.getName()).replace("%player2%", siblingFam.getName());
                        LunaticLib.getPlatform().sendConsoleCommand(command);
                    }

                    Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.SIBLING_PROPOSED_PLAYER);
                    Utils.withdrawMoney(player.getServerName(), siblingUUID, WithdrawKey.MARRY_PROPOSING_PLAYER);

            }
        }
        return true;
    }
}
