package de.janschuri.lunaticFamily.commands.family.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.sibling.SiblingCommand;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.List;

public class FamilySiblingSubcommand extends Subcommand {

    private static final String permission = "lunaticfamily.admin.reload";
    private static final List<String> aliases = Language.getAliases("family", "reload");
    private static final Subcommand[] subcommands = {

    };

    public FamilySiblingSubcommand() {
        super(permission, aliases, subcommands);
    }

    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        SiblingCommand siblingCommand = new SiblingCommand(plugin);
        String stringLabel = "sibling";
        PluginCommand pluginCommand = plugin.getCommand(stringLabel);
        String[] arrayArgs = new String[args.length - 1];
        System.arraycopy(args, 1, arrayArgs, 0, args.length - 1);
        assert pluginCommand != null;
        siblingCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
    }
}
