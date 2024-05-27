package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.gender.GenderInfoSubcommand;
import de.janschuri.lunaticfamily.common.commands.gender.GenderSetSubcommand;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.commands.AbstractSubcommand;

public class GenderSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "family";
    private static final String NAME = "gender";
    private static final String PERMISSION = "lunaticfamily.gender";

    public GenderSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION, new AbstractSubcommand[]{
                new GenderInfoSubcommand(),
                new GenderSetSubcommand(),
        });
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
                Logger.debugLog("GenderSubcommand: Wrong usage");
            }
        }
        return true;
    }
}
