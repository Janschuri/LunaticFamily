package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.command.CommandSender;

public class FamilyDeleteSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "delete";
    private static final String permission = "lunaticfamily.admin.delete";

    public FamilyDeleteSubcommand() {
        super(mainCommand, name, permission);
    }
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            if (args.length < 2) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                return;
            }

            boolean force = false;

            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("force")) {
                    force = true;
                }
            }


            if (Utils.isUUID(args[1])) {
                if (force) {
                    LunaticFamily.getDatabase().deletePlayerData(args[1]);
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_delete").replace("%player%", args[1]));
                } else {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_delete_confirm").replace("%player%", args[1]));
                }
            } else {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist"));
            }
        }
    }
}
