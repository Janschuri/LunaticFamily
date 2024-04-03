package de.janschuri.lunaticFamily.commands.adopt.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AdoptHelpSubcommand extends Subcommand {

    private static final String permission = "lunaticfamily.adopt";
    private static final List<String> aliases = Language.getAliases("adopt", "help");

    public AdoptHelpSubcommand() {
        super(permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            String[] subcommandsHelp = {"propose", "kickout", "moveout"};

            StringBuilder msg = new StringBuilder(Language.prefix + " " + Language.getMessage("adopt_help") + "\n");

            for (final String sc : subcommandsHelp) {
                msg.append(Language.prefix).append(" ").append(Language.getMessage("adopt_" + sc + "_help")).append("\n");
            }
            sender.sendMessage(msg.toString());
        }
    }
}
