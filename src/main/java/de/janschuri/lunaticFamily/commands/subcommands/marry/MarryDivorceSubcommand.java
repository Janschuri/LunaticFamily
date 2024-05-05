package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.ClickableDecisionMessage;

import java.util.UUID;

public class MarryDivorceSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "divorce";
    private static final String PERMISSION = "lunaticfamily.marry";

    public MarryDivorceSubcommand() {
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


            if (!playerFam.isMarried()) {
                player.sendMessage(language.getPrefix() + language.getMessage("marry_divorce_no_partner"));
                return true;
            }
            if (cancel) {
                sender.sendMessage(language.getPrefix() + language.getMessage("marry_divorce_cancel"));
                return true;
            }
            if (!confirm) {
                player.sendMessage(new ClickableDecisionMessage(
                        language.getPrefix() + language.getMessage("marry_divorce_confirm"),
                        language.getMessage("confirm"),
                        "/family marry divorce confirm",
                        language.getMessage("cancel"),
                        "/family marry divorce cancel"));
                return true;
            }
            if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "marry_divorce_leaving_player")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
                return true;
            }

            UUID partnerUUID = playerFam.getPartner().getUniqueId();
            AbstractPlayerSender partner = AbstractSender.getPlayerSender(partnerUUID);

            if (!force && !Utils.hasEnoughMoney(player.getServerName(), partnerUUID, "marry_divorce_left_player")) {
                player.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", playerFam.getPartner().getName()));
                player.sendMessage(new ClickableDecisionMessage(
                        language.getPrefix() + language.getMessage("take_payment_confirm"),
                        language.getMessage("confirm"),
                        "/family marry divorce confirm force",
                        language.getMessage("cancel"),
                        "/family marry divorce cancel"));
                return true;
            }

            if (force && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, "marry_divorce_left_player", "marry_divorce_leaving_player")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money"));
                return true;
            }


            sender.sendMessage(language.getPrefix() + language.getMessage("marry_divorce_divorced"));
            partner.sendMessage(language.getPrefix() + language.getMessage("marry_divorce_divorced"));

            for (String command : PluginConfig.getSuccessCommands("divorce")) {
                command = command.replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getPartner().getName());
                Utils.sendConsoleCommand(command);
            }

            if (force) {
                Utils.withdrawMoney(player.getServerName(), playerUUID, "marry_divorce_left_player", "marry_divorce_leaving_player");
            } else {
                Utils.withdrawMoney(player.getServerName(), playerUUID, "marry_divorce_leaving_player");
                Utils.withdrawMoney(player.getServerName(), partnerUUID, "marry_divorce_leaving_player");
            }

            playerFam.divorce();
            return true;
        }
        return false;
    }
}
