package de.janschuri.lunaticfamily.commands.subcommands.marry;

import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class MarryUnsetSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "unset";
    private static final String PERMISSION = "lunaticfamily.admin.marry";

    public MarryUnsetSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            if (args.length < 1) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
            }

            UUID player1UUID;
            AbstractPlayerSender player1;
            if (Utils.isUUID(args[0])) {
                player1UUID = UUID.fromString(args[0]);
                player1 = AbstractSender.getPlayerSender(player1UUID);
            } else {
                player1 = AbstractSender.getPlayerSender(args[0]);
                player1UUID = player1.getUniqueId();
            }

            if (!player1.exists()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[0]));
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
