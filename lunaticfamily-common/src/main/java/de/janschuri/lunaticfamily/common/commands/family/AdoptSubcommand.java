package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.adopt.*;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.commands.AbstractSubcommand;

public class AdoptSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "family";
    private static final String NAME = "adopt";
    private static final String PERMISSION = "lunaticfamily.adopt";

    public AdoptSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION, new AbstractSubcommand[] {
                        new AdoptAcceptSubcommand(),
                        new AdoptDenySubcommand(),
                        new AdoptKickoutSubcommand(),
                        new AdoptMoveoutSubcommand(),
                        new AdoptProposeSubcommand(),
                        new AdoptSetSubcommand(),
                        new AdoptUnsetSubcommand(),
               }
        );
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else {
            if (args.length == 0) {

            } else {
                final String subcommand = args[0];

                for (AbstractSubcommand sc : subcommands) {
                    if (checkIsSubcommand(NAME, sc.getName(), subcommand)) {
                        String[] newArgs = new String[args.length - 1];
                        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                        return sc.execute(sender, newArgs);
                    }
                }
                sender.sendMessage(getPrefix() + getMessage("wrong_usage"));
                Logger.debugLog("AdoptSubcommand: Wrong usage");
            }
        }
        return true;
    }
}
