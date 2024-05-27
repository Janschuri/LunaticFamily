package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;

public class MarryDivorceSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "divorce";
    private static final String PERMISSION = "lunaticfamily.marry";

    public MarryDivorceSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_permission"));
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


            if (!playerFam.isMarried()) {
                player.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_divorce_no_partner"));
                return true;
            }
            if (cancel) {
                sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_divorce_cancel"));
                return true;
            }
            if (!confirm) {
                player.sendMessage(Utils.getClickableDecisionMessage(
                        LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_divorce_confirm"),
                        LunaticFamily.getLanguageConfig().getMessage("confirm"),
                        "/family marry divorce confirm",
                        LunaticFamily.getLanguageConfig().getMessage("cancel"),
                        "/family marry divorce cancel"));
                return true;
            }
            if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "marry_divorce_leaving_player")) {
                sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("not_enough_money"));
                return true;
            }

            UUID partnerUUID = playerFam.getPartner().getUniqueId();
            PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

            if (!force && !Utils.hasEnoughMoney(player.getServerName(), partnerUUID, "marry_divorce_left_player")) {
                player.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("player_not_enough_money").replace("%player%", playerFam.getPartner().getName()));
                player.sendMessage(Utils.getClickableDecisionMessage(
                        LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("take_payment_confirm"),
                        LunaticFamily.getLanguageConfig().getMessage("confirm"),
                        "/family marry divorce confirm force",
                        LunaticFamily.getLanguageConfig().getMessage("cancel"),
                        "/family marry divorce cancel"));
                return true;
            }

            if (force && !Utils.hasEnoughMoney(player.getServerName(), playerUUID, "marry_divorce_left_player", "marry_divorce_leaving_player")) {
                sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("not_enough_money"));
                return true;
            }


            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_divorce_divorced"));
            partner.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_divorce_divorced"));

            for (String command : LunaticFamily.getConfig().getSuccessCommands("divorce")) {
                command = command.replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getPartner().getName());
                LunaticLib.getPlatform().sendConsoleCommand(command);
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
