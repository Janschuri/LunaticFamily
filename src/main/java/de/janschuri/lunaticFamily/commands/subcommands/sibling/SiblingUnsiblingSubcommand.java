package de.janschuri.lunaticFamily.commands.subcommands.sibling;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.ClickableDecisionMessage;

import java.util.UUID;

public class SiblingUnsiblingSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "sibling";
    private static final String NAME = "unsibling";
    private static final String PERMISSION = "lunaticfamily.sibling";

    public SiblingUnsiblingSubcommand() {
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
                sender.sendMessage(language.getPrefix() + language.getMessage("sibling_unsibling_no_sibling"));
            }

            UUID siblingUUID = playerFam.getSibling().getUniqueId();
            AbstractPlayerSender sibling = AbstractSender.getPlayerSender(siblingUUID);

            if (playerFam.isAdopted()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("sibling_unsibling_adopted"));
                return true;
            }

            if (cancel) {
                sender.sendMessage(language.getPrefix() + language.getMessage("sibling_unsibling_cancel"));
                return true;
            }

            if (!confirm) {
                sender.sendMessage(new ClickableDecisionMessage(
                        language.getPrefix() + language.getMessage("sibling_unsibling_confirm"),
                        language.getMessage("confirm"),
                        "/family sibling unsibling confirm",
                        language.getMessage("cancel"),
                        "/family sibling unsibling cancel"));
                return true;
            }

            if (!force && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, "sibling_unsibling_leaving_player")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
                return true;
            }
            if (!force && !Utils.hasEnoughMoney(player.getServerName(), siblingUUID, "sibling_unsibling_left_player")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", playerFam.getSibling().getName()));
                sender.sendMessage(new ClickableDecisionMessage(
                        language.getPrefix() + language.getMessage("take_payment_confirm"),
                        language.getMessage("confirm"),
                        "/family sibling unsibling confirm force",
                        language.getMessage("cancel"),
                        "/family sibling unsibling cancel"));
                return true;
            }

            sender.sendMessage(language.getPrefix() + language.getMessage("sibling_unsibling_complete"));
            sibling.sendMessage(language.getPrefix() + language.getMessage("sibling_unsiblinged_complete"));

            if (force) {
                Utils.withdrawMoney(player.getServerName(), playerUUID, "sibling_unsibling_leaving_player", "sibling_unsibling_left_player");
            } else {
                Utils.withdrawMoney(player.getServerName(), playerUUID, "sibling_unsibling_leaving_player");
                Utils.withdrawMoney(player.getServerName(), siblingUUID, "sibling_unsibling_left_player");
            }

            for (String command : PluginConfig.getSuccessCommands("unsibling")) {
                command = command.replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getSibling().getName());
                Utils.sendConsoleCommand(command);
            }

            playerFam.removeSibling();
        }
        return true;
    }
}
