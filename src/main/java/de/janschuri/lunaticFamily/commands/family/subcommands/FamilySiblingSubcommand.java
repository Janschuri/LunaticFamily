package de.janschuri.lunaticFamily.commands.family.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.commands.sibling.SiblingCommand;
import de.janschuri.lunaticFamily.commands.sibling.subcommands.*;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

public class FamilySiblingSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "sibling";
    private static final String permission = "lunaticfamily.sibling";
    private static final Subcommand[] subcommands = {
        new SiblingAcceptSubcommand(),
        new SiblingDenySubcommand(),
        new SiblingHelpSubcommand(),
        new SiblingProposeSubcommand(),
        new SiblingSetSubcommand(),
        new SiblingUnsetSubcommand(),
        new SiblingUnsiblingSubcommand()
    };

    public FamilySiblingSubcommand() {
        super(mainCommand, name, permission, subcommands);
    }

    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            SiblingCommand siblingCommand = new SiblingCommand(plugin);
            String stringLabel = "sibling";
            PluginCommand pluginCommand = plugin.getCommand(stringLabel);
            String[] arrayArgs = new String[args.length - 1];
            System.arraycopy(args, 1, arrayArgs, 0, args.length - 1);
            assert pluginCommand != null;
            siblingCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
        }
    }
}
