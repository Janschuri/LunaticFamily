package de.janschuri.lunaticFamily.commands.family.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.marry.MarryCommand;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.List;

public class FamilyMarrySubcommand extends Subcommand {
    private static final String permission = "lunaticfamily.marry";
    private static final List<String> aliases = Language.getAliases("marry");

    private static final Subcommand[] subcommands = {

    };

    public FamilyMarrySubcommand() {
        super(permission, aliases, subcommands);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        MarryCommand marryCommand = new MarryCommand(plugin);
        String stringLabel = "marry";
        PluginCommand pluginCommand = plugin.getCommand(stringLabel);
        String[] arrayArgs = new String[args.length - 1];
        System.arraycopy(args, 1, arrayArgs, 0, args.length - 1);
        assert pluginCommand != null;
        marryCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
    }
}
