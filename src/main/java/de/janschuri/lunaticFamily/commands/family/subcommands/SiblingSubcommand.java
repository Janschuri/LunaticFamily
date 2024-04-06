package de.janschuri.lunaticFamily.commands.family.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.commands.sibling.subcommands.*;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.CommandSender;

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

    private static final Subcommand[] subcommands = {
//        new SiblingAcceptSubcommand(),
//        new SiblingDenySubcommand(),
//        new SiblingHelpSubcommand(),
//        new SiblingProposeSubcommand(),
//        new SiblingSetSubcommand(),
//        new SiblingUnsetSubcommand(),
//        new SiblingUnsiblingSubcommand()
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

    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            if (args.length == 0) {
                siblingHelpSubcommand.execute(sender, args, plugin);
            } else {
                final String subcommand = args[0];
                if (Language.checkIsSubcommand(name, "set", subcommand)) {
                    siblingSetSubcommand.execute(sender, args, plugin);
                } else if (Language.checkIsSubcommand(name, "unset", subcommand)) {
                    siblingUnsetSubcommand.execute(sender, args, plugin);
                } else if (Language.checkIsSubcommand(name, "propose", subcommand)) {
                    siblingProposeSubcommand.execute(sender, args, plugin);
                } else if (Language.checkIsSubcommand(name, "accept", subcommand)) {
                    siblingAcceptSubcommand.execute(sender, args, plugin);
                } else if (Language.checkIsSubcommand(name, "deny", subcommand)) {
                    siblingDenySubcommand.execute(sender, args, plugin);
                } else if (Language.checkIsSubcommand(name, "unsibling", subcommand)) {
                    siblingUnsiblingSubcommand.execute(sender, args, plugin);
                } else if (Language.checkIsSubcommand(name, "help", subcommand)) {
                    siblingHelpSubcommand.execute(sender, args, plugin);
                } else {
                    sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                }
            }
        }
    }
}
