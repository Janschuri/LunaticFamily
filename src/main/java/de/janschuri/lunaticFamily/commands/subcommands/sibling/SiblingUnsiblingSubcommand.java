package de.janschuri.lunaticFamily.commands.subcommands.sibling;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.ClickableDecisionMessage;

import java.util.UUID;

public class SiblingUnsiblingSubcommand extends Subcommand {
    private static final String mainCommand = "sibling";
    private static final String name = "unsibling";
    private static final String permission = "lunaticfamily.sibling";

    public SiblingUnsiblingSubcommand() {
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

            boolean confirm = false;
            boolean cancel = false;
            boolean force = false;

            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("confirm")) {
                    confirm = true;
                }
                if (args[1].equalsIgnoreCase("cancel")) {
                    cancel = true;
                }
            }
            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("force")) {
                    force = true;
                }
            }

            if (!playerFam.hasSibling()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("sibling_unsibling_no_sibling"));
            }

            UUID siblingUUID = playerFam.getSibling().getUniqueId();
            AbstractPlayerSender sibling = AbstractSender.getPlayerSender(siblingUUID);

            if (!confirm) {
                sender.sendMessage(new ClickableDecisionMessage(
                        language.getMessage("sibling_unsibling_confirm"),
                        language.getMessage("confirm"),
                        "/family sibling unsibling confirm",
                        language.getMessage("cancel"),
                        "/family sibling unsibling cancel"));
            } else if (cancel) {
                sender.sendMessage(language.getPrefix() + language.getMessage("sibling_unsibling_cancel"));
            } else if (playerFam.isAdopted()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("sibling_unsibling_adopted"));
            } else if (!force && !Utils.getUtils().hasEnoughMoney(playerUUID, "sibling_unsibling_leaving_player")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
            } else if (!force && !Utils.getUtils().hasEnoughMoney(siblingUUID, "sibling_unsibling_left_player")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", playerFam.getSibling().getName()));
                sender.sendMessage(new ClickableDecisionMessage(
                        language.getMessage("take_payment_confirm"),
                        language.getMessage("confirm"),
                        "/family sibling unsibling confirm force",
                        language.getMessage("cancel"),
                        "/family sibling unsibling cancel"));
            } else {
                sender.sendMessage(language.getPrefix() + language.getMessage("sibling_unsibling_complete"));
                sibling.sendMessage(language.getPrefix() + language.getMessage("sibling_unsiblinged_complete"));

                if (force) {
                    Utils.getUtils().withdrawMoney(playerUUID, "sibling_unsibling_leaving_player", "sibling_unsibling_left_player");
                } else {
                    Utils.getUtils().withdrawMoney(playerUUID, "sibling_unsibling_leaving_player");
                    Utils.getUtils().withdrawMoney(siblingUUID, "sibling_unsibling_left_player");
                }

                for (String command : PluginConfig.successCommands.get("unsibling")) {
                    command = command.replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getSibling().getName());
                    Utils.getUtils().sendConsoleCommand(command);
                }

                playerFam.removeSibling();
            }
        }
        return true;
    }
}
