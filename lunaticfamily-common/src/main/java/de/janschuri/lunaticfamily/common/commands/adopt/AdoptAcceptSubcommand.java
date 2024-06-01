package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.AdoptSubcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;

public class AdoptAcceptSubcommand extends Subcommand {

    private final CommandMessageKey gotAdoptedMK = new CommandMessageKey(this,"got_adopted");
    private final CommandMessageKey adoptedBySingleMK = new CommandMessageKey(this,"adopted_by_single");
    private final CommandMessageKey adoptedMK = new CommandMessageKey(this,"adopted");
    private final CommandMessageKey parentLimitMK = new CommandMessageKey(this,"parent_limit");
    private final CommandMessageKey noRequestMK = new CommandMessageKey(this,"no_request");

    @Override
    public String getPermission() {
        return "lunaticfamily.adopt";
    }

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public AdoptSubcommand getParentCommand() {
        return new AdoptSubcommand();
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

            if (!LunaticFamily.adoptRequests.containsKey(playerUUID)) {
                sender.sendMessage(getMessage(noRequestMK));
            } else {

                UUID parent1UUID = LunaticFamily.adoptRequests.get(playerUUID);
                FamilyPlayerImpl parentFam = new FamilyPlayerImpl(parent1UUID);
                PlayerSender parent = LunaticLib.getPlatform().getPlayerSender(parent1UUID);

                if (parentFam.getChildrenAmount() > 1) {
                    sender.sendMessage(getMessage(parentLimitMK).replaceText(getTextReplacementConfig("%player%", parentFam.getName())));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.ADOPT_CHILD)) {
                    sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), parent1UUID, WithdrawKey.ADOPT_PARENT)) {
                    sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK).replaceText(getTextReplacementConfig("%player%", parentFam.getName())));
                } else {

                    if (parentFam.isMarried()) {
                        sender.sendMessage(getMessage(gotAdoptedMK).replaceText(getTextReplacementConfig("%player1%", parentFam.getName())).replaceText(getTextReplacementConfig("%player2%", parentFam.getPartner().getName())));
                        parent.sendMessage(getMessage(adoptedMK).replaceText(getTextReplacementConfig("%player%", playerFam.getName())));
                        UUID parent2UUID = parentFam.getPartner().getUniqueId();
                        Utils.withdrawMoney(player.getServerName(), parent2UUID, 0.5, WithdrawKey.ADOPT_PARENT);
                        Utils.withdrawMoney(player.getServerName(), parent1UUID, 0.5, WithdrawKey.ADOPT_PARENT);
                        for (String command : LunaticFamily.getConfig().getSuccessCommands("adopt")) {
                            command = command.replace("%parent1%", parentFam.getName()).replace("%parent2%", parentFam.getPartner().getName()).replace("%child%", playerFam.getName());

                            LunaticLib.getPlatform().sendConsoleCommand(command);
                        }
                    } else {
                        sender.sendMessage(getMessage(adoptedBySingleMK).replaceText(getTextReplacementConfig("%player%", parentFam.getName())));
                        for (String command : LunaticFamily.getConfig().getSuccessCommands("adopt_single")) {
                            command = command.replace("%parent%", parentFam.getName()).replace("%child%", playerFam.getName());
                            LunaticLib.getPlatform().sendConsoleCommand(command);
                        }
                    }
                    Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.ADOPT_CHILD);

                    parent.sendMessage(getMessage(adoptedMK).replaceText(getTextReplacementConfig("%player%", playerFam.getName())));
                    LunaticFamily.adoptRequests.remove(playerUUID);
                    parentFam.adopt(playerFam.getId());
                }
            }
        }
        return true;
    }
}