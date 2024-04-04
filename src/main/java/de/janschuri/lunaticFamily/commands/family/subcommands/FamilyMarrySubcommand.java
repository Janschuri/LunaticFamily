package de.janschuri.lunaticFamily.commands.family.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.commands.marry.MarryCommand;
import de.janschuri.lunaticFamily.commands.marry.subcommands.*;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

public class FamilyMarrySubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "marry";
    private static final String permission = "lunaticfamily.marry";

    private static final Subcommand[] subcommands = {
        new MarryAcceptSubcommand(),
        new MarryBackpackSubcommand(),
        new MarryDenySubcommand(),
        new MarryDivorceSubcommand(),
        new MarryDivorceSubcommand(),
        new MarryGiftSubcommand(),
        new MarryHelpSubcommand(),
        new MarryKissSubcommand(),
        new MarryListSubcommand(),
        new MarryPriestSubcommand(),
        new MarryProposeSubcommand(),
        new MarrySetSubcommand(),
        new MarryUnsetSubcommand()
    };

    public FamilyMarrySubcommand() {
        super(mainCommand, name, permission, subcommands);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            MarryCommand marryCommand = new MarryCommand(plugin);
            String stringLabel = "marry";
            PluginCommand pluginCommand = plugin.getCommand(stringLabel);
            String[] arrayArgs = new String[args.length - 1];
            System.arraycopy(args, 1, arrayArgs, 0, args.length - 1);
            assert pluginCommand != null;
            marryCommand.onCommand(sender, pluginCommand, stringLabel, arrayArgs);
        }
    }
}
