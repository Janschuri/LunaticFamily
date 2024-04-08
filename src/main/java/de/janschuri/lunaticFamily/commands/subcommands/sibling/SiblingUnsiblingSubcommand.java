package de.janschuri.lunaticFamily.commands.subcommands.sibling;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SiblingUnsiblingSubcommand extends Subcommand {
    private static final String mainCommand = "sibling";
    private static final String name = "unsibling";
    private static final String permission = "lunaticfamily.sibling";

    public SiblingUnsiblingSubcommand() {
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

            if (!playerFam.hasSibling()) {
                sender.sendMessage(Language.prefix + Language.getMessage("sibling_unsibling_no_sibling"));
            } else if (!confirm) {
                sender.sendMessage(Utils.createClickableMessage(
                        Language.getMessage("sibling_unsibling_confirm"),
                        Language.getMessage("confirm"),
                        "/lunaticfamily:sibling unsibling confirm",
                        Language.getMessage("cancel"),
                        "/lunaticfamily:sibling unsibling cancel"));
            } else if (cancel) {
                sender.sendMessage(Language.prefix + Language.getMessage("sibling_unsibling_cancel"));
            } else if (playerFam.isAdopted()) {
                sender.sendMessage(Language.prefix + Language.getMessage("sibling_unsibling_adopted"));
            } else if (!force && !playerFam.hasEnoughMoney("sibling_unsibling_leaving_player")) {
                sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
            } else if (!force && !playerFam.getSibling().hasEnoughMoney("sibling_unsibling_left_player")) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_enough_money").replace("%player%", playerFam.getSibling().getName()));
                sender.sendMessage(Utils.createClickableMessage(
                        Language.getMessage("take_payment_confirm"),
                        Language.getMessage("confirm"),
                        "/lunaticfamily:sibling unsibling confirm force",
                        Language.getMessage("cancel"),
                        "/lunaticfamily:sibling unsibling cancel"));
            } else {
                sender.sendMessage(Language.prefix + Language.getMessage("sibling_unsibling_complete"));
                playerFam.getSibling().sendMessage(Language.prefix + Language.getMessage("sibling_unsiblinged_complete"));

                if (force) {
                    playerFam.withdrawPlayer("sibling_unsibling_leaving_player");
                    playerFam.withdrawPlayer("sibling_unsibling_left_player");
                } else {
                    playerFam.withdrawPlayer("sibling_unsibling_leaving_player");
                    playerFam.getSibling().withdrawPlayer("sibling_unsibling_left_player");
                }

                for (String command : PluginConfig.successCommands.get("unsibling")) {
                    command = command.replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getSibling().getName());
                    Utils.sendConsoleCommand(command);
                }

                playerFam.removeSibling();
            }
        }
    }
}
