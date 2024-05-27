package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.Sender;

public class FamilyDeleteSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "family";
    private static final String NAME = "delete";
    private static final String PERMISSION = "lunaticfamily.admin.delete";

    public FamilyDeleteSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else {
            if (args.length < 1) {
                sender.sendMessage(getPrefix() + getMessage("wrong_usage"));
                Logger.debugLog("FamilyDeleteSubcommand: Wrong usage");
                return true;
            }

            boolean force = false;

            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("force")) {
                    force = true;
                }
            }

            String playerArg = args[0];

            if (Utils.isUUID(playerArg)) {
                if (force) {
                    PlayerDataTable.deletePlayerData(playerArg);
                    sender.sendMessage(getPrefix() + getMessage("admin_delete").replace("%player%", playerArg));
                } else {
                    sender.sendMessage(getPrefix() + getMessage("admin_delete_confirm").replace("%player%", playerArg));
                }
            } else {
                sender.sendMessage(getPrefix() + getMessage("player_not_exist"));
            }
        }
        return true;
    }
}
