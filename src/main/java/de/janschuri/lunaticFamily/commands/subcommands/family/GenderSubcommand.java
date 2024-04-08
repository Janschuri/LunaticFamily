package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.commands.subcommands.gender.GenderHelpSubcommand;
import de.janschuri.lunaticFamily.commands.subcommands.gender.GenderInfoSubcommand;
import de.janschuri.lunaticFamily.commands.subcommands.gender.GenderSetSubcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.command.CommandSender;

public class GenderSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "gender";
    private static final String permission = "lunaticfamily.gender";
    private static final GenderHelpSubcommand genderHelpSubcommand = new GenderHelpSubcommand();
    private static final GenderInfoSubcommand genderInfoSubcommand = new GenderInfoSubcommand();
    private static final GenderSetSubcommand genderSetSubcommand = new GenderSetSubcommand();
    public static final Subcommand[] subcommands = {
        genderHelpSubcommand,
        genderInfoSubcommand,
        genderSetSubcommand
    };

    public GenderSubcommand() {
        super(mainCommand, name, permission, subcommands);
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            if (args.length == 0) {
                genderHelpSubcommand.execute(sender, args);
            } else {
                final String subcommand = args[0];
                if (Utils.checkIsSubcommand(name, "set", subcommand)) {
                    genderSetSubcommand.execute(sender, args);
                } else if (Utils.checkIsSubcommand(name, "info", subcommand)) {
                    genderInfoSubcommand.execute(sender, args);
                } else if (Utils.checkIsSubcommand(name, "help", subcommand)) {
                    genderHelpSubcommand.execute(sender, args);
                } else {
                    sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                }
            }
        }
    }
}
