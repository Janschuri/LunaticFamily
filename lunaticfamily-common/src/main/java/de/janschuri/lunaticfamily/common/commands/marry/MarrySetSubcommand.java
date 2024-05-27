package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.Sender;

import java.util.UUID;

public class MarrySetSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "set";
    private static final String PERMISSION = "lunaticfamily.admin.marry";

    public MarrySetSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else {
            boolean force = false;

            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("force")) {
                    force = true;
                }
            }

            if (args.length < 1) {
                sender.sendMessage(getPrefix() + getMessage("wrong_usage"));
                Logger.debugLog("MarrySetSubcommand: Wrong usage");
                return true;
            } else if (args[0].equalsIgnoreCase("deny")) {
                sender.sendMessage(getPrefix() + getMessage("admin_marry_set_denied"));
                return true;
            } else if (args.length < 2) {
                sender.sendMessage(getPrefix() + getMessage("wrong_usage"));
                Logger.debugLog("MarrySetSubcommand: Wrong usage");
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

            if (Utils.isUUID(args[1])) {
                player2UUID = UUID.fromString(player2Arg);

                if (PlayerDataTable.getID(player1UUID) < 0) {
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




            if (player1UUID.equals(player2UUID)) {
                sender.sendMessage(getPrefix() + getMessage("admin_marry_set_same_player"));
                return true;
            }

                FamilyPlayerImpl player2Fam = new FamilyPlayerImpl(player2UUID);
                FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(player1UUID);

                if (player1Fam.isFamilyMember(player2Fam.getId())) {
                    sender.sendMessage(getPrefix() + getMessage("admin_already_family").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                    return true;
                }

                if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
                    int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                    sender.sendMessage(getPrefix() + getMessage("admin_marry_set_too_many_children").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                } else if (player1Fam.isMarried()) {
                    sender.sendMessage(getPrefix() + getMessage("admin_marry_set_already_married").replace("%player%", player1Fam.getName()));
                } else if (player2Fam.isMarried()) {
                    sender.sendMessage(getPrefix() + getMessage("admin_marry_set_already_married").replace("%player%", player2Fam.getName()));
                } else {
                    LunaticFamily.marryRequests.remove(player1UUID);
                    LunaticFamily.marryPriestRequests.remove(player1UUID);
                    LunaticFamily.marryPriest.remove(player1UUID);

                    LunaticFamily.marryRequests.remove(player1UUID);
                    LunaticFamily.marryPriestRequests.remove(player1UUID);
                    LunaticFamily.marryPriest.remove(player1UUID);

                    player1Fam.marry(player2Fam.getId());
                    sender.sendMessage(getPrefix() + getMessage("admin_marry_set_married").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                }
        }
        return true;
    }
}
