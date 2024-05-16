package de.janschuri.lunaticfamily.commands.subcommands.family;

import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.commands.subcommands.adopt.*;
import de.janschuri.lunaticfamily.utils.Logger;
import de.janschuri.lunaticlib.commands.AbstractSubcommand;
import de.janschuri.lunaticlib.senders.AbstractSender;

public class AdoptSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "family";
    private static final String NAME = "adopt";
    private static final String PERMISSION = "lunaticfamily.adopt";
    private final AdoptHelpSubcommand adoptHelpSubcommand = new AdoptHelpSubcommand();

    public AdoptSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION, new AbstractSubcommand[] {
                        new AdoptAcceptSubcommand(),
                        new AdoptDenySubcommand(),
                        new AdoptKickoutSubcommand(),
                        new AdoptMoveoutSubcommand(),
                        new AdoptProposeSubcommand(),
                        new AdoptSetSubcommand(),
                        new AdoptUnsetSubcommand(),
                        new AdoptHelpSubcommand(),
               }
        );
    }

    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            if (args.length == 0) {
                adoptHelpSubcommand.execute(sender, args);
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
                Logger.debugLog("AdoptSubcommand: Wrong usage");
            }
        }
        return true;
    }
}
