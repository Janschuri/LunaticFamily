package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.commands.subcommands.adopt.*;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.commands.AbstractSubcommand;
import de.janschuri.lunaticlib.senders.AbstractSender;

public class AdoptSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "adopt";
    private static final String permission = "lunaticfamily.adopt";
    private static final AcceptSubcommand ACCEPT_SUBCOMMAND = new AcceptSubcommand();
    private static final AdoptDenySubcommand adoptDenySubcommand = new AdoptDenySubcommand();
    private static final AdoptHelpSubcommand adoptHelpSubcommand = new AdoptHelpSubcommand();
    private static final AdoptKickoutSubcommand adoptKickoutSubcommand = new AdoptKickoutSubcommand();
    private static final AdoptMoveoutSubcommand adoptMoveoutSubcommand = new AdoptMoveoutSubcommand();
    private static final AdoptProposeSubcommand adoptProposeSubcommand = new AdoptProposeSubcommand();
    private static final AdoptSetSubcommand adoptSetSubcommand = new AdoptSetSubcommand();
    private static final AdoptUnsetSubcommand adoptUnsetSubcommand = new AdoptUnsetSubcommand();
    public static final AbstractSubcommand[] subcommands = {
            ACCEPT_SUBCOMMAND,
            adoptDenySubcommand,
            adoptHelpSubcommand,
            adoptKickoutSubcommand,
            adoptMoveoutSubcommand,
            adoptProposeSubcommand,
            adoptSetSubcommand,
            adoptUnsetSubcommand
    };

    public AdoptSubcommand() {
        super(mainCommand, name, permission, subcommands);
    }

    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            if (args.length == 0) {
                adoptHelpSubcommand.execute(sender, args);
            } else {
                final String subcommand = args[0];
                if (language.checkIsSubcommand(name, "set", subcommand)) {
                    adoptSetSubcommand.execute(sender, args);
                } else if (language.checkIsSubcommand(name, "unset", subcommand)) {
                    adoptUnsetSubcommand.execute(sender, args);
                } else if (language.checkIsSubcommand(name, "propose", subcommand)) {
                    adoptProposeSubcommand.execute(sender, args);
                } else if (language.checkIsSubcommand(name, "accept", subcommand)) {
                    ACCEPT_SUBCOMMAND.execute(sender, args);
                } else if (language.checkIsSubcommand(name, "deny", subcommand)) {
                    adoptDenySubcommand.execute(sender, args);
                } else if (language.checkIsSubcommand(name, "moveout", subcommand)) {
                    adoptMoveoutSubcommand.execute(sender, args);
                } else if (language.checkIsSubcommand(name, "kickout", subcommand)) {
                    adoptKickoutSubcommand.execute(sender, args);
                } else if (language.checkIsSubcommand(name, "help", subcommand)) {
                    adoptHelpSubcommand.execute(sender, args);
                } else {
                    sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                }
            }
        }
        return true;
    }
}
