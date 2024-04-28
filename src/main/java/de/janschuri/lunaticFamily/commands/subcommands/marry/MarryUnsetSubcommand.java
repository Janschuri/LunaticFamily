package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class MarryUnsetSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "unset";
    private static final String permission = "lunaticfamily.admin.marry";

    public MarryUnsetSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            if (args.length < 2) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
            }

            UUID player1UUID;
            AbstractPlayerSender player1;
            if (Utils.isUUID(args[1])) {
                player1UUID = UUID.fromString(args[1]);
                player1 = AbstractSender.getPlayerSender(player1UUID);
            } else {
                player1 = AbstractSender.getPlayerSender(args[1]);
                player1UUID = player1.getUniqueId();
            }

            if (!player1.exists()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else {
                FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);

                if (!player1Fam.isMarried()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_marry_unset_no_partner").replace("%player%", player1Fam.getName()));
                } else {
                    FamilyPlayer partnerFam = player1Fam.getPartner();
                    player1Fam.divorce();
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_marry_unset_divorced").replace("%player1%", player1Fam.getName()).replace("%player2%", partnerFam.getName()));
                }
            }
        }
        return true;
    }
}
