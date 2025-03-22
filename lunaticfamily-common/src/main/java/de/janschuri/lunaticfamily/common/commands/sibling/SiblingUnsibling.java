package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;

import java.util.Map;
import java.util.UUID;

public class SiblingUnsibling extends FamilyCommand implements HasParentCommand {

    private static final SiblingUnsibling INSTANCE = new SiblingUnsibling();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Un-sibling with your sibling.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Entferne die Geschwisterbeziehung zu deinem Bruder/deiner Schwester.");
    private static final CommandMessageKey NO_SIBLING_MK = new LunaticCommandMessageKey(INSTANCE, "no_sibling")
            .defaultMessage("en", "You have no sibling.")
            .defaultMessage("de", "Du hast keinen Bruder/keine Schwester.");
    private static final CommandMessageKey ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE, "adopted")
            .defaultMessage("en", "You cannot unsibling your sibling, cause you and your sibling are adopted.")
            .defaultMessage("de", "Du kannst die Geschwisterbeziehung nicht entfernen, weil du und dein Bruder/deine Schwester adoptiert seid.");
    private static final CommandMessageKey CONFIRM_MK = new LunaticCommandMessageKey(INSTANCE, "confirm")
            .defaultMessage("en", "Please confirm that you want to unsibling your sibling.")
            .defaultMessage("de", "Bitte bestätige, dass du die Geschwisterbeziehung entfernen möchtest.");
    private static final CommandMessageKey CANCEL_MK = new LunaticCommandMessageKey(INSTANCE, "cancel")
            .defaultMessage("en", "You haven't unsiblinged your sibling after all.")
            .defaultMessage("de", "Du hast die Geschwisterbeziehung doch nicht entfernt.");
    private static final CommandMessageKey COMPLETE_MK = new LunaticCommandMessageKey(INSTANCE, "complete")
            .defaultMessage("en", "You have unsiblinged your sibling.")
            .defaultMessage("de", "Du hast die Geschwisterbeziehung entfernt.");



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
        FamilyPlayer playerFam = FamilyPlayer.find(playerUUID);

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

        if (!playerFam.hasSiblings()) {
            sender.sendMessage(getMessage(NO_SIBLING_MK));
            return true;
        }

        UUID siblingUUID = playerFam.getSibling().getUUID();
        PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingUUID);

        if (playerFam.isAdopted()) {
            sender.sendMessage(getMessage(ADOPTED_MK));
            return true;
        }

        if (cancel) {
            sender.sendMessage(getMessage(CANCEL_MK));
            return true;
        }

        if (!confirm) {
            player.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(CONFIRM_MK.noPrefix()),
                    getMessage(CONFIRM_MK.noPrefix()),
                    "/family sibling unsibling confirm",
                    getMessage(CANCEL_MK.noPrefix()),
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
            sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                    placeholder("%player%", sibling.getName())
                    ));
            player.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(TAKE_PAYMENT_CONFIRM_MK.noPrefix()),
                    getMessage(CONFIRM_MK.noPrefix()),
                    "/family sibling unsibling confirm force",
                    getMessage(CANCEL_MK.noPrefix()),
                    "/family sibling unsibling cancel"),
                    LunaticFamily.getConfig().decisionAsInvGUI()
            );
            return true;
        }

        sender.sendMessage(getMessage(COMPLETE_MK));
        sibling.sendMessage(getMessage(COMPLETE_MK));

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

        playerFam.removeSiblings();

        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }
}
