package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.Sender;

import java.util.UUID;

public class SiblingSetSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "sibling";
    private static final String NAME = "set";
    private static final String PERMISSION = "lunaticfamily.admin.sibling";

    public SiblingSetSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else {
            boolean forced = false;

            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("force")) {
                    forced = true;
                }
            }

            if (!sender.hasPermission("lunaticFamily.admin.sibling")) {
                sender.sendMessage(getPrefix() + getMessage("no_permission"));
                return true;
            } else if (args.length < 1) {
                sender.sendMessage(getPrefix() + getMessage("wrong_usage"));
                Logger.debugLog("SiblingSetSubcommand: Wrong usage");
                return true;
            }

            String player1Arg = args[0];
            String player2Arg = args[1];

            UUID player1UUID;
            UUID player2UUID;

            if (Utils.isUUID(player1Arg)) {
                player1UUID = UUID.fromString(args[0]);

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

            if (Utils.isUUID(player2Arg)) {
                player2UUID = UUID.fromString(args[0]);

                if (PlayerDataTable.getID(player2UUID) < 0) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", player2Arg));
                    return true;
                }
            } else {
                player2UUID = PlayerDataTable.getUUID(player2Arg);

                if (player2UUID == null) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", player2Arg));
                    return true;
                }
            }


            FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(player1UUID);
            FamilyPlayerImpl player2Fam = new FamilyPlayerImpl(player2UUID);

            if (player1Fam.isFamilyMember(player2Fam.getId())) {
                sender.sendMessage(getPrefix() + getMessage("admin_already_family").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                return true;
            }

            if (args[0].equalsIgnoreCase(args[1])) {
                sender.sendMessage(getPrefix() + getMessage("admin_marry_set_same_player"));
            } else {


                if (player1Fam.isAdopted() && player2Fam.isAdopted()) {
                    sender.sendMessage(getPrefix() + getMessage("admin_sibling_set_both_adopted").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                } else if (player1Fam.isAdopted()) {
                    sender.sendMessage(getPrefix() + getMessage("admin_sibling_is_adopted").replace("%player%", player1Fam.getName()));

                } else if (player2Fam.isAdopted()) {
                    sender.sendMessage(getPrefix() + getMessage("admin_sibling_is_adopted").replace("%player%", player2Fam.getName()));
                } else {
                    sender.sendMessage(getPrefix() + getMessage("admin_sibling_added").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                    player1Fam.addSibling(player2Fam.getId());
                }
            }
        }
        return true;
    }
}
