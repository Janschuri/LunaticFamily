package de.janschuri.lunaticFamily.commands.family.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.gender.GenderCommand;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.List;

public class FamilyGenderSubcommand extends Subcommand {
    private static final String permission = "lunaticfamily.gender";
    private static final List<String> aliases = Language.getAliases("gender");
    private static final Subcommand[] subcommands = {

    };

    public FamilyGenderSubcommand() {
        super(permission, aliases, subcommands);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        GenderCommand genderCommand = new GenderCommand(plugin);
        String stringLabel = "gender";
        PluginCommand pluginCommand = plugin.getCommand(stringLabel);
        String[] arrayArgs = new String[args.length - 1];
        System.arraycopy(args, 1, arrayArgs, 0, args.length - 1);
        assert pluginCommand != null;
        genderCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
    }
}
