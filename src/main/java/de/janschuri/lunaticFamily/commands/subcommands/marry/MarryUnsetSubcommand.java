package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.senders.CommandSender;
import de.janschuri.lunaticFamily.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;

import java.util.UUID;

public class MarryUnsetSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "unset";
    private static final String permission = "lunaticfamily.admin.marry";

    public MarryUnsetSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            if (args.length < 2) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
            }

            UUID player1UUID;
            PlayerCommandSender player1;
            if (Utils.isUUID(args[1])) {
                player1UUID = UUID.fromString(args[1]);
                player1 = sender.getPlayerCommandSender(player1UUID);
            } else {
                player1 = sender.getPlayerCommandSender(args[1]);
                player1UUID = player1.getUniqueId();
            }

            if (!player1.exists()) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else {
                FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);

                if (!player1Fam.isMarried()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_unset_no_partner").replace("%player%", player1Fam.getName()));
                } else {
                    FamilyPlayer partnerFam = player1Fam.getPartner();
                    player1Fam.divorce();
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_unset_divorced").replace("%player1%", player1Fam.getName()).replace("%player2%", partnerFam.getName()));
                }
            }
        }
        return true;
    }
}
