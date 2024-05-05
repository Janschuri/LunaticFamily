package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.commands.subcommands.marry.*;
import de.janschuri.lunaticlib.commands.AbstractSubcommand;
import de.janschuri.lunaticlib.senders.AbstractSender;

public class MarrySubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "family";
    private static final String NAME = "marry";
    private static final String PERMISSION = "lunaticfamily.marry";

    public MarrySubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION, new AbstractSubcommand[] {
                new MarryAcceptSubcommand(),
                new MarryDenySubcommand(),
                new MarryDivorceSubcommand(),
                new MarryGiftSubcommand(),
                new MarryHeartSubcommand(),
                new MarryHelpSubcommand(),
                new MarryKissSubcommand(),
                new MarryListSubcommand(),
                new MarryPriestSubcommand(),
                new MarryProposeSubcommand(),
                new MarrySetSubcommand(),
                new MarryUnsetSubcommand(),
        });
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            if (args.length == 0) {
                new MarryHelpSubcommand().execute(sender, args);
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
