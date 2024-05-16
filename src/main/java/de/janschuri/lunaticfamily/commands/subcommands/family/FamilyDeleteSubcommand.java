package de.janschuri.lunaticfamily.commands.subcommands.family;

import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.utils.Logger;
import de.janschuri.lunaticfamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractSender;

public class FamilyDeleteSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "family";
    private static final String NAME = "delete";
    private static final String PERMISSION = "lunaticfamily.admin.delete";

    public FamilyDeleteSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }

    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            if (args.length < 1) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                Logger.debugLog("FamilyDeleteSubcommand: Wrong usage");
                return true;
            }

            boolean force = false;

            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("force")) {
                    force = true;
                }
            }


            if (Utils.isUUID(args[0])) {
                if (force) {
                    PlayerDataTable.deletePlayerData(args[0]);
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_delete").replace("%player%", args[0]));
                } else {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_delete_confirm").replace("%player%", args[0]));
                }
            } else {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist"));
            }
        }
        return true;
    }
}
