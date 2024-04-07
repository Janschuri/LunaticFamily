package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.commands.subcommands.adopt.*;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.CommandSender;

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
    public static final Subcommand[] subcommands = {
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
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            if (args.length == 0) {
                adoptHelpSubcommand.execute(sender, args);
            } else {
                final String subcommand = args[0];
                if (Language.checkIsSubcommand(name, "set", subcommand)) {
                    adoptSetSubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "unset", subcommand)) {
                    adoptUnsetSubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "propose", subcommand)) {
                    adoptProposeSubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "accept", subcommand)) {
                    ACCEPT_SUBCOMMAND.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "deny", subcommand)) {
                    adoptDenySubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "moveout", subcommand)) {
                    adoptMoveoutSubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "kickout", subcommand)) {
                    adoptKickoutSubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "help", subcommand)) {
                    adoptHelpSubcommand.execute(sender, args);
                } else {
                    sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                }
            }
        }
    }
}