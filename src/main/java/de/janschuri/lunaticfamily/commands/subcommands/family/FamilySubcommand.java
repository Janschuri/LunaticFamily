package de.janschuri.lunaticfamily.commands.subcommands.family;

import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticlib.commands.AbstractSubcommand;
import de.janschuri.lunaticlib.senders.AbstractSender;

public class FamilySubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "family";
    private static final String NAME = "family";
    private static final String PERMISSION = "lunaticfamily.family";

    public FamilySubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION, new AbstractSubcommand[] {
                new FamilyHelpSubcommand(),
                new FamilyListSubcommand(),
                new FamilyBackgroundSubcommand(),
                new FamilyReloadSubcommand(),
                new FamilyTreeSubcommand(),
                new FamilyDeleteSubcommand(),
                new AdoptSubcommand(),
                new GenderSubcommand(),
                new SiblingSubcommand(),
                new MarrySubcommand(),
        });
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            if (args.length == 0) {
                new FamilyHelpSubcommand().execute(sender, args);
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
