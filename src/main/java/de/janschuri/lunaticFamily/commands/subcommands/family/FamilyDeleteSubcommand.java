package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.database.tables.PlayerDataTable;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractSender;

public class FamilyDeleteSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "delete";
    private static final String permission = "lunaticfamily.admin.delete";

    public FamilyDeleteSubcommand() {
        super(mainCommand, name, permission);
    }

    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            if (args.length < 2) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                return true;
            }

            boolean force = false;

            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("force")) {
                    force = true;
                }
            }


            if (Utils.isUUID(args[1])) {
                if (force) {
                    PlayerDataTable.deletePlayerData(args[1]);
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_delete").replace("%player%", args[1]));
                } else {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_delete_confirm").replace("%player%", args[1]));
                }
            } else {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist"));
            }
        }
        return true;
    }
}
