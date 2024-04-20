package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.commands.ClickableDecisionMessage;
import de.janschuri.lunaticFamily.commands.senders.CommandSender;
import de.janschuri.lunaticFamily.commands.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;

import java.util.UUID;

public class MarryDivorceSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "divorce";
    private static final String permission = "lunaticfamily.marry";

    public MarryDivorceSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof PlayerCommandSender)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            PlayerCommandSender player = (PlayerCommandSender) sender;
            String playerUUID = player.getUniqueId().toString();
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


            if (!playerFam.isMarried()) {
                player.sendMessage(Language.prefix + Language.getMessage("marry_divorce_no_partner"));
                return true;
            }
            if (cancel) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_divorce_cancel"));
                return true;
            }
            if (!confirm) {
                player.sendMessage(new ClickableDecisionMessage(
                        Language.getMessage("marry_divorce_confirm"),
                        Language.getMessage("confirm"),
                        "/family marry divorce confirm",
                        Language.getMessage("cancel"),
                        "/family marry divorce cancel"));
                return true;
            }
            if (!player.hasEnoughMoney("marry_divorce_leaving_player")) {
                sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                return true;
            }

            UUID partnerUUID = playerFam.getPartner().getUniqueId();
            PlayerCommandSender partner = sender.getPlayerCommandSender(partnerUUID);

            if (!partner.hasEnoughMoney("marry_divorce_left_player")) {
                player.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", playerFam.getPartner().getName()));
                player.sendMessage(new ClickableDecisionMessage(
                        Language.getMessage("take_payment_confirm"),
                        Language.getMessage("confirm"),
                        "/family marry divorce confirm force",
                        Language.getMessage("cancel"),
                        "/family marry divorce cancel"));
                return true;
            }

            if (force && !player.hasEnoughMoney("marry_divorce_left_player", "marry_divorce_leaving_player")) {
                sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                return true;
            }


            sender.sendMessage(Language.prefix + Language.getMessage("marry_divorce_divorced"));
            partner.sendMessage(Language.prefix + Language.getMessage("marry_divorce_divorced"));

            for (String command : PluginConfig.successCommands.get("divorce")) {
                command = command.replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getPartner().getName());
                Utils.getUtils().sendConsoleCommand(command);
            }

            if (force) {
                player.withdrawMoney("marry_divorce_left_player", "marry_divorce_leaving_player");
            } else {
                player.withdrawMoney("marry_divorce_leaving_player");
                partner.withdrawMoney("marry_divorce_leaving_player");
            }

            playerFam.divorce();
            return true;
        }
        return false;
    }
}
