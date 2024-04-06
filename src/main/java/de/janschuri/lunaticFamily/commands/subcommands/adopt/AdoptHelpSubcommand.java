package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdoptHelpSubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "help";
    private static final String permission = "lunaticfamily.adopt";
    private static final Subcommand[] subcommands = {
            new AdoptAcceptSubcommand(),
            new AdoptDenySubcommand(),
            new AdoptKickoutSubcommand(),
            new AdoptMoveoutSubcommand(),
            new AdoptProposeSubcommand(),
            new AdoptSetSubcommand(),
            new AdoptUnsetSubcommand()
    };

    public AdoptHelpSubcommand() {
        super(mainCommand, name, permission);
    }

    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            Component msg = Component.text(Language.getMessage(mainCommand + "_help") + "\n");

            for (Subcommand subcommand : subcommands) {
                if (!(subcommand instanceof AdoptHelpSubcommand)) {
                    msg = msg.append(subcommand.getHelp(sender));
                }
            }

            sender.sendMessage(msg);
        }
    }
}
