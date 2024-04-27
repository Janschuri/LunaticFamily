package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.commands.subcommands.gender.GenderHelpSubcommand;
import de.janschuri.lunaticFamily.commands.subcommands.gender.GenderInfoSubcommand;
import de.janschuri.lunaticFamily.commands.subcommands.gender.GenderSetSubcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.commands.AbstractSubcommand;
import de.janschuri.lunaticlib.senders.AbstractSender;

public class GenderSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "gender";
    private static final String permission = "lunaticfamily.gender";
    private static final GenderHelpSubcommand genderHelpSubcommand = new GenderHelpSubcommand();
    private static final GenderInfoSubcommand genderInfoSubcommand = new GenderInfoSubcommand();
    private static final GenderSetSubcommand genderSetSubcommand = new GenderSetSubcommand();
    public static final AbstractSubcommand[] subcommands = {
        genderHelpSubcommand,
        genderInfoSubcommand,
        genderSetSubcommand
    };

    public GenderSubcommand() {
        super(mainCommand, name, permission, subcommands);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            if (args.length == 0) {
                genderHelpSubcommand.execute(sender, args);
            } else {
                final String subcommand = args[0];
                if (language.checkIsSubcommand(name, "set", subcommand)) {
                    genderSetSubcommand.execute(sender, args);
                } else if (language.checkIsSubcommand(name, "info", subcommand)) {
                    genderInfoSubcommand.execute(sender, args);
                } else if (language.checkIsSubcommand(name, "help", subcommand)) {
                    genderHelpSubcommand.execute(sender, args);
                } else {
                    sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                }
            }
        }
        return true;
    }
}
