package de.janschuri.lunaticFamily.commands.family.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.commands.gender.subcommands.GenderHelpSubcommand;
import de.janschuri.lunaticFamily.commands.gender.subcommands.GenderInfoSubcommand;
import de.janschuri.lunaticFamily.commands.gender.subcommands.GenderSetSubcommand;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.CommandSender;

public class GenderSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "gender";
    private static final String permission = "lunaticfamily.gender";
    private static final GenderHelpSubcommand genderHelpSubcommand = new GenderHelpSubcommand();
    private static final GenderInfoSubcommand genderInfoSubcommand = new GenderInfoSubcommand();
    private static final GenderSetSubcommand genderSetSubcommand = new GenderSetSubcommand();
    private static final Subcommand[] subcommands = {
        genderHelpSubcommand,
        genderInfoSubcommand,
        genderSetSubcommand
    };

    public GenderSubcommand() {
        super(mainCommand, name, permission, subcommands);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            if (args.length == 0) {
                genderHelpSubcommand.execute(sender, args, plugin);
            } else {
                final String subcommand = args[0];
                if (Language.checkIsSubcommand(name, "set", subcommand)) {
                    genderSetSubcommand.execute(sender, args, plugin);
                } else if (Language.checkIsSubcommand(name, "info", subcommand)) {
                    genderInfoSubcommand.execute(sender, args, plugin);
                } else if (Language.checkIsSubcommand(name, "help", subcommand)) {
                    genderHelpSubcommand.execute(sender, args, plugin);
                } else {
                    sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                }
            }
        }
    }
}
