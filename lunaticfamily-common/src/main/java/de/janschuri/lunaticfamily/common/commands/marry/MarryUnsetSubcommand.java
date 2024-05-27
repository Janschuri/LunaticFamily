package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.Sender;

import java.util.UUID;

public class MarryUnsetSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "unset";
    private static final String PERMISSION = "lunaticfamily.admin.marry";

    public MarryUnsetSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else {
            if (args.length < 1) {
                sender.sendMessage(getPrefix() + getMessage("wrong_usage"));
                Logger.debugLog("MarryUnsetSubcommand: Wrong usage");
            }

            String player1Arg = args[0];
            UUID player1UUID;

            if (Utils.isUUID(player1Arg)) {
                player1UUID = UUID.fromString(player1Arg);

                if (PlayerDataTable.getID(player1UUID) < 0) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", player1Arg));
                    return true;
                }
            } else {
                player1UUID = PlayerDataTable.getUUID(player1Arg);

                if (player1UUID == null) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", player1Arg));
                    return true;
                }
            }

                FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(player1UUID);

                if (!player1Fam.isMarried()) {
                    sender.sendMessage(getPrefix() + getMessage("admin_marry_unset_no_partner").replace("%player%", player1Fam.getName()));
                } else {
                    FamilyPlayerImpl partnerFam = player1Fam.getPartner();
                    player1Fam.divorce();
                    sender.sendMessage(getPrefix() + getMessage("admin_marry_unset_divorced").replace("%player1%", player1Fam.getName()).replace("%player2%", partnerFam.getName()));
                }
        }
        return true;
    }
}
