package de.janschuri.lunaticFamily.commands.marry.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MarryHelpSubcommand extends Subcommand {
    private static final String permission = "lunaticfamily.marry";
    private static final List<String> aliases = Language.getAliases("marry", "help");

    public MarryHelpSubcommand() {
        super(permission, aliases);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            String[] subcommandsHelp = {"propose", "priest", "list", "divorce", "kiss", "gift"};

            String msg = Language.prefix + " " + Language.getMessage("marry_help") + "\n";

            for (String sc : subcommandsHelp) {
                msg = msg + Language.prefix + " " + Language.getMessage("marry_" + sc + "_help") + "\n";
            }
            sender.sendMessage(msg);
        }
    }
}
