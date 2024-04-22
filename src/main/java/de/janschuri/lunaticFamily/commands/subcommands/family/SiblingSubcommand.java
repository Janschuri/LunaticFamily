package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.senders.CommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.commands.subcommands.sibling.*;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.utils.Utils;

public class SiblingSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "sibling";
    private static final String permission = "lunaticfamily.sibling";

    private static final SiblingAcceptSubcommand siblingAcceptSubcommand = new SiblingAcceptSubcommand();
    private static final SiblingDenySubcommand siblingDenySubcommand = new SiblingDenySubcommand();
    private static final SiblingHelpSubcommand siblingHelpSubcommand = new SiblingHelpSubcommand();
    private static final SiblingProposeSubcommand siblingProposeSubcommand = new SiblingProposeSubcommand();
    private static final SiblingSetSubcommand siblingSetSubcommand = new SiblingSetSubcommand();
    private static final SiblingUnsetSubcommand siblingUnsetSubcommand = new SiblingUnsetSubcommand();
    private static final SiblingUnsiblingSubcommand siblingUnsiblingSubcommand = new SiblingUnsiblingSubcommand();

    public static final Subcommand[] subcommands = {
        siblingAcceptSubcommand,
        siblingDenySubcommand,
        siblingHelpSubcommand,
        siblingProposeSubcommand,
        siblingSetSubcommand,
        siblingUnsetSubcommand,
        siblingUnsiblingSubcommand
    };

    public SiblingSubcommand() {
        super(mainCommand, name, permission, subcommands);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            if (args.length == 0) {
                siblingHelpSubcommand.execute(sender, args);
            } else {
                final String subcommand = args[0];
                if (Utils.checkIsSubcommand(name, "set", subcommand)) {
                    siblingSetSubcommand.execute(sender, args);
                } else if (Utils.checkIsSubcommand(name, "unset", subcommand)) {
                    siblingUnsetSubcommand.execute(sender, args);
                } else if (Utils.checkIsSubcommand(name, "propose", subcommand)) {
                    siblingProposeSubcommand.execute(sender, args);
                } else if (Utils.checkIsSubcommand(name, "accept", subcommand)) {
                    siblingAcceptSubcommand.execute(sender, args);
                } else if (Utils.checkIsSubcommand(name, "deny", subcommand)) {
                    siblingDenySubcommand.execute(sender, args);
                } else if (Utils.checkIsSubcommand(name, "unsibling", subcommand)) {
                    siblingUnsiblingSubcommand.execute(sender, args);
                } else if (Utils.checkIsSubcommand(name, "help", subcommand)) {
                    siblingHelpSubcommand.execute(sender, args);
                } else {
                    sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                }
            }
        }
        return true;
    }
}
