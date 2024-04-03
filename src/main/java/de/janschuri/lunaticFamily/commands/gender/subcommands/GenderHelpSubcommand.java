package de.janschuri.lunaticFamily.commands.gender.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GenderHelpSubcommand extends Subcommand {

    private static final String permission = "lunaticfamily.gender";
    private static final List<String> aliases = Language.getAliases("gender", "help");

    public GenderHelpSubcommand() {
        super(permission, aliases);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            String[] subcommandsHelp = {"set", "info"};

            StringBuilder msg = new StringBuilder(Language.prefix + " " + Language.getMessage("gender_help") + "\n");

            for (String subcommand : subcommandsHelp) {
                msg.append(Language.prefix).append(" ").append(Language.getMessage("gender_" + subcommand + "_help")).append("\n");
            }
            sender.sendMessage(msg.toString());
        }
    }
}
