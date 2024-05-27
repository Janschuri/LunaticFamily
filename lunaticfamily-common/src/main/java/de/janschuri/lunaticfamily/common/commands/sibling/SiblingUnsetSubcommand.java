package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.Sender;

import java.util.UUID;

public class SiblingUnsetSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "sibling";
    private static final String NAME = "unset";
    private static final String PERMISSION = "lunaticfamily.admin.sibling";

    public SiblingUnsetSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else {
            if (!sender.hasPermission("lunaticFamily.admin.sibling")) {
                sender.sendMessage(getPrefix() + getMessage("no_permission"));
                return true;
            } else if (args.length < 1) {
                sender.sendMessage(getPrefix() + getMessage("wrong_usage"));
                Logger.debugLog("SiblingUnsetSubcommand: Wrong usage");
                return true;
            }

            String player1Name = args[0];
            UUID player1UUID;

            if (Utils.isUUID(args[0])) {
                player1UUID = UUID.fromString(args[0]);

                if (PlayerDataTable.getID(player1UUID) < 0) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", player1Name));
                    return true;
                }
            } else {
                player1UUID = PlayerDataTable.getUUID(player1Name);

                if (player1UUID == null) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", player1Name));
                    return true;
                }
            }

            FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(player1UUID);


                if (!player1Fam.hasSibling()) {
                    sender.sendMessage(getPrefix() + getMessage("admin_sibling_unset_no_sibling").replace("%player%", player1Fam.getName()));
                } else {
                    FamilyPlayerImpl siblingFam = player1Fam.getSibling();
                    player1Fam.removeSibling();
                    sender.sendMessage(getPrefix() + getMessage("admin_sibling_unset_sibling").replace("%player1%", player1Fam.getName()).replace("%player2%", siblingFam.getName()));
                }
        }
        return true;
    }
}
