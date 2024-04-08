package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MarryDivorceSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "divorce";
    private static final String permission = "lunaticfamily.marry";

    public MarryDivorceSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            Player player = (Player) sender;
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
                sender.sendMessage(Language.prefix + Language.getMessage("marry_divorce_no_partner"));
            } else if (!confirm) {
                sender.sendMessage(Utils.createClickableMessage(
                        Language.getMessage("marry_divorce_confirm"),
                        Language.getMessage("confirm"),
                        "/lunaticfamily:marry divorce confirm",
                        Language.getMessage("cancel"),
                        "/lunaticfamily:marry divorce cancel"));
            } else if (cancel) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_divorce_cancel"));
            } else if (!playerFam.hasEnoughMoney("marry_divorce_leaving_player")) {
                sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
            } else if (!playerFam.getPartner().hasEnoughMoney("marry_divorce_left_player")) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", playerFam.getPartner().getName()));
                sender.sendMessage(Utils.createClickableMessage(
                        Language.getMessage("take_payment_confirm"),
                        Language.getMessage("confirm"),
                        "/lunaticfamily:marry divorce confirm force",
                        Language.getMessage("cancel"),
                        "/lunaticfamily:marry divorce cancel"));
            } else {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_divorce_divorced"));
                playerFam.getPartner().sendMessage(Language.prefix + Language.getMessage("marry_divorce_divorced"));

                playerFam.withdrawPlayer("marry_divorce_leaving_player");
                playerFam.getPartner().withdrawPlayer("marry_divorce_leaving_player");

                for (String command : PluginConfig.successCommands.get("divorce")) {
                    command = command.replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getPartner().getName());
                    Utils.sendConsoleCommand(command);
                }

                playerFam.divorce();
            }
        }
    }
}
