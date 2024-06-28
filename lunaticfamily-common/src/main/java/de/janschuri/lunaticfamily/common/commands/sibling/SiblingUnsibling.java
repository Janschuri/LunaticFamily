package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;

public class SiblingUnsibling extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey noSiblingMK = new CommandMessageKey(this,"no_sibling");
    private final CommandMessageKey adoptedMK = new CommandMessageKey(this,"adopted");
    private final CommandMessageKey confirmMK = new CommandMessageKey(this,"confirm");
    private final CommandMessageKey cancelMK = new CommandMessageKey(this,"cancel");
    private final CommandMessageKey completeMK = new CommandMessageKey(this,"complete");
    private final CommandMessageKey unsiblingedCompleteMK = new CommandMessageKey(this,"unsiblinged_complete");


    @Override
    public String getPermission() {
        return "lunaticfamily.sibling";
    }

    @Override
    public String getName() {
        return "unsibling";
    }

    @Override
    public Sibling getParentCommand() {
        return new Sibling();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender player)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }
        UUID playerUUID = player.getUniqueId();
        String name = player.getName();
        FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID, name);

        boolean confirm = false;
        boolean cancel = false;
        boolean force = false;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("confirm")) {
                confirm = true;
            }
            if (args[0].equalsIgnoreCase("cancel")) {
                cancel = true;
            }
        }

        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("force")) {
                force = true;
            }
        }

        if (!playerFam.hasSibling()) {
            sender.sendMessage(getMessage(noSiblingMK));
            return true;
        }

        UUID siblingUUID = playerFam.getSibling().getUniqueId();
        PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingUUID);

        if (playerFam.isAdopted()) {
            sender.sendMessage(getMessage(adoptedMK));
            return true;
        }

        if (cancel) {
            sender.sendMessage(getMessage(cancelMK));
            return true;
        }

        if (!confirm) {
            player.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(confirmMK, false),
                    getMessage(CONFIRM_MK, false),
                    "/family sibling unsibling confirm",
                    getMessage(CANCEL_MK, false),
                    "/family sibling unsibling cancel"),
                    LunaticFamily.getConfig().decisionAsInvGUI()
            );
            return true;
        }

        if (!force && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.SIBLING_UNSIBLING_LEAVING_PLAYER)) {
            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
            return true;
        }

        if (!force && !Utils.hasEnoughMoney(player.getServerName(), siblingUUID, WithdrawKey.SIBLING_UNSIBLING_LEFT_PLAYER)) {
            sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                    .replaceText(getTextReplacementConfig("%player%", playerFam.getSibling().getName())));
            player.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(TAKE_PAYMENT_CONFIRM_MK, false),
                    getMessage(CONFIRM_MK, false),
                    "/family sibling unsibling confirm force",
                    getMessage(CANCEL_MK, false),
                    "/family sibling unsibling cancel"),
                    LunaticFamily.getConfig().decisionAsInvGUI()
            );
            return true;
        }

        sender.sendMessage(getMessage(completeMK));
        sibling.sendMessage(getMessage(completeMK));

        if (force) {
            Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.SIBLING_UNSIBLING_LEAVING_PLAYER, WithdrawKey.SIBLING_UNSIBLING_LEFT_PLAYER);
        } else {
            Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.SIBLING_UNSIBLING_LEAVING_PLAYER);
            Utils.withdrawMoney(player.getServerName(), siblingUUID, WithdrawKey.SIBLING_UNSIBLING_LEFT_PLAYER);
        }

        for (String command : LunaticFamily.getConfig().getSuccessCommands("unsibling")) {
            command = command.replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getSibling().getName());
            LunaticLib.getPlatform().sendConsoleCommand(command);
        }

        playerFam.removeSibling();

        return true;
    }
}
