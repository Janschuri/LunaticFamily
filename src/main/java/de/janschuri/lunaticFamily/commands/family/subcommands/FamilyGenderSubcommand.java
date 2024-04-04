package de.janschuri.lunaticFamily.commands.family.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.commands.gender.GenderCommand;
import de.janschuri.lunaticFamily.commands.gender.subcommands.GenderHelpSubcommand;
import de.janschuri.lunaticFamily.commands.gender.subcommands.GenderInfoSubcommand;
import de.janschuri.lunaticFamily.commands.gender.subcommands.GenderSetSubcommand;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

public class FamilyGenderSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "gender";
    private static final String permission = "lunaticfamily.gender";
    private static final Subcommand[] subcommands = {
        new GenderHelpSubcommand(),
        new GenderInfoSubcommand(),
        new GenderSetSubcommand()
    };

    public FamilyGenderSubcommand() {
        super(mainCommand, name, permission, subcommands);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            GenderCommand genderCommand = new GenderCommand(plugin);
            String stringLabel = "gender";
            PluginCommand pluginCommand = plugin.getCommand(stringLabel);
            String[] arrayArgs = new String[args.length - 1];
            System.arraycopy(args, 1, arrayArgs, 0, args.length - 1);
            assert pluginCommand != null;
            genderCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
        }
    }
}
