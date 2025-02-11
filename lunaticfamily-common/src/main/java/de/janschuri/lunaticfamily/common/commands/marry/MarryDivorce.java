package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;

public class MarryDivorce extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey noPartnerMK = new CommandMessageKey(this,"no_partner");
    private final CommandMessageKey divorcedMK = new CommandMessageKey(this,"divorced");
    private final CommandMessageKey confirmMK = new CommandMessageKey(this,"confirm");
    private final CommandMessageKey cancelMK = new CommandMessageKey(this,"cancel");

    @Override
    public String getPermission() {
        return "lunaticfamily.marry";
    }

    @Override
    public String getName() {
        return "divorce";
    }

    @Override
    public Marry getParentCommand() {
        return new Marry();
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
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);

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


        if (!playerFam.isMarried()) {
            player.sendMessage(getMessage(noPartnerMK));
            return true;
        }
        if (cancel) {
            sender.sendMessage(getMessage(cancelMK));
            return true;
        }
        if (!confirm) {
            player.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(confirmMK.noPrefix()),
                    LunaticFamily.getLanguageConfig().getMessage(CONFIRM_MK.noPrefix()),
                    "/family marry divorce confirm",
                    LunaticFamily.getLanguageConfig().getMessage(CANCEL_MK.noPrefix()),
                    "/family marry divorce cancel"),
                    LunaticFamily.getConfig().decisionAsInvGUI()
            );
            return true;
        }
        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_DIVORCE_LEAVING_PLAYER)) {
            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
            return true;
        }

        UUID partnerUUID = playerFam.getPartner().getUUID();
        PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

        if (!force && !Utils.hasEnoughMoney(player.getServerName(), partnerUUID, WithdrawKey.MARRY_DIVORCE_LEFT_PLAYER)) {
            player.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", playerFam.getPartner().getName())));
            player.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(TAKE_PAYMENT_CONFIRM_MK.noPrefix()),
                    LunaticFamily.getLanguageConfig().getMessage(CONFIRM_MK.noPrefix()),
                    "/family marry divorce confirm force",
                    LunaticFamily.getLanguageConfig().getMessage(CANCEL_MK.noPrefix()),
                    "/family marry divorce cancel"),
                    LunaticFamily.getConfig().decisionAsInvGUI()
            );
            return true;
        }

        if (force && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_DIVORCE_LEFT_PLAYER, WithdrawKey.MARRY_DIVORCE_LEAVING_PLAYER)) {
            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
            return true;
        }


        sender.sendMessage(getMessage(divorcedMK));
        partner.sendMessage(getMessage(divorcedMK));

        for (String command : LunaticFamily.getConfig().getSuccessCommands("divorce")) {
            command = command.replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getPartner().getName());
            LunaticLib.getPlatform().sendConsoleCommand(command);
        }

        if (force) {
            Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_DIVORCE_LEFT_PLAYER, WithdrawKey.MARRY_DIVORCE_LEAVING_PLAYER);
        } else {
            Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_DIVORCE_LEAVING_PLAYER);
            Utils.withdrawMoney(player.getServerName(), partnerUUID, WithdrawKey.MARRY_DIVORCE_LEFT_PLAYER);
        }

        playerFam.divorce();
        return true;
    }
}
