package de.janschuri.lunaticFamily.commands.family.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.commands.adopt.AdoptCommand;
import de.janschuri.lunaticFamily.commands.adopt.subcommands.*;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

public class FamilyAdoptSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "adopt";
    private static final String permission = "lunaticfamily.adopt";
    private static final Subcommand[] subcommands = {
            new AdoptAcceptSubcommand(),
            new AdoptDenySubcommand(),
            new AdoptHelpSubcommand(),
            new AdoptKickoutSubcommand(),
            new AdoptMoveoutSubcommand(),
            new AdoptProposeSubcommand(),
            new AdoptSetSubcommand(),
            new AdoptUnsetSubcommand()
    };

    public FamilyAdoptSubcommand() {
        super(mainCommand, name, permission, subcommands);
    }

    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            AdoptCommand adoptCommand = new AdoptCommand(plugin);
            String stringLabel = "adopt";
            PluginCommand pluginCommand = plugin.getCommand(stringLabel);
            String[] arrayArgs = new String[args.length - 1];
            System.arraycopy(args, 1, arrayArgs, 0, args.length - 1);
            assert pluginCommand != null;
            adoptCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
        }
    }
}
