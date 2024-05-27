package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;

public class SiblingUnsiblingSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "sibling";
    private static final String NAME = "unsibling";
    private static final String PERMISSION = "lunaticfamily.sibling";

    public SiblingUnsiblingSubcommand() {
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
                sender.sendMessage(getPrefix() + getMessage("sibling_unsibling_no_sibling"));
            }

            UUID siblingUUID = playerFam.getSibling().getUniqueId();
            PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingUUID);

            if (playerFam.isAdopted()) {
                sender.sendMessage(getPrefix() + getMessage("sibling_unsibling_adopted"));
                return true;
            }

            if (cancel) {
                sender.sendMessage(getPrefix() + getMessage("sibling_unsibling_cancel"));
                return true;
            }

            if (!confirm) {
                sender.sendMessage(Utils.getClickableDecisionMessage(
                        getPrefix() + getMessage("sibling_unsibling_confirm"),
                        getMessage("confirm"),
                        "/family sibling unsibling confirm",
                        getMessage("cancel"),
                        "/family sibling unsibling cancel"));
                return true;
            }

            if (!force && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, "sibling_unsibling_leaving_player")) {
                sender.sendMessage(getPrefix() + getMessage("not_enough_money"));
                return true;
            }
            if (!force && !Utils.hasEnoughMoney(player.getServerName(), siblingUUID, "sibling_unsibling_left_player")) {
                sender.sendMessage(getPrefix() + getMessage("player_not_enough_money").replace("%player%", playerFam.getSibling().getName()));
                sender.sendMessage(Utils.getClickableDecisionMessage(
                        getPrefix() + getMessage("take_payment_confirm"),
                        getMessage("confirm"),
                        "/family sibling unsibling confirm force",
                        getMessage("cancel"),
                        "/family sibling unsibling cancel"));
                return true;
            }

            sender.sendMessage(getPrefix() + getMessage("sibling_unsibling_complete"));
            sibling.sendMessage(getPrefix() + getMessage("sibling_unsiblinged_complete"));

            if (force) {
                Utils.withdrawMoney(player.getServerName(), playerUUID, "sibling_unsibling_leaving_player", "sibling_unsibling_left_player");
            } else {
                Utils.withdrawMoney(player.getServerName(), playerUUID, "sibling_unsibling_leaving_player");
                Utils.withdrawMoney(player.getServerName(), siblingUUID, "sibling_unsibling_left_player");
            }

            for (String command : LunaticFamily.getConfig().getSuccessCommands("unsibling")) {
                command = command.replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getSibling().getName());
                LunaticLib.getPlatform().sendConsoleCommand(command);
            }

            playerFam.removeSibling();
        }
        return true;
    }
}
