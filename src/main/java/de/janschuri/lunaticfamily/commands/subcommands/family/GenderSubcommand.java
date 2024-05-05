package de.janschuri.lunaticfamily.commands.subcommands.family;

import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.commands.subcommands.gender.GenderHelpSubcommand;
import de.janschuri.lunaticfamily.commands.subcommands.gender.GenderInfoSubcommand;
import de.janschuri.lunaticfamily.commands.subcommands.gender.GenderSetSubcommand;
import de.janschuri.lunaticlib.commands.AbstractSubcommand;
import de.janschuri.lunaticlib.senders.AbstractSender;

public class GenderSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "family";
    private static final String NAME = "gender";
    private static final String PERMISSION = "lunaticfamily.gender";

    public GenderSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION, new AbstractSubcommand[]{
                new GenderHelpSubcommand(),
                new GenderInfoSubcommand(),
                new GenderSetSubcommand(),
        });
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            if (args.length == 0) {
                new GenderHelpSubcommand().execute(sender, args);
            } else {
                final String subcommand = args[0];

                for (AbstractSubcommand sc : subcommands) {
                    if (language.checkIsSubcommand(NAME, sc.getName(), subcommand)) {
                        String[] newArgs = new String[args.length - 1];
                        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                        return sc.execute(sender, newArgs);
                    }
                }
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
            }
        }
        return true;
    }
}
