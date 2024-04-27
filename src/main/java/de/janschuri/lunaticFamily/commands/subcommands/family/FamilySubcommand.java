package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.commands.AbstractSubcommand;
import de.janschuri.lunaticlib.senders.AbstractSender;

public class FamilySubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "family";
    private static final String permission = "lunaticfamily.family";

    private static final FamilyListSubcommand familyListSubcommand = new FamilyListSubcommand();
    private static final AdoptSubcommand familyAdoptSubcommand = new AdoptSubcommand();
    private static final GenderSubcommand familyGenderSubcommand = new GenderSubcommand();
    private static final SiblingSubcommand familySiblingSubcommand = new SiblingSubcommand();
    private static final FamilyBackgroundSubcommand familyBackgroundSubcommand = new FamilyBackgroundSubcommand();
    private static final FamilyHelpSubcommand familyHelpSubcommand = new FamilyHelpSubcommand();
    private static final MarrySubcommand familyMarrySubcommand = new MarrySubcommand();
    private static final FamilyReloadSubcommand familyReloadSubcommand = new FamilyReloadSubcommand();
    private static final FamilyTreeSubcommand familyTreeSubcommand = new FamilyTreeSubcommand();
    private static final FamilyDeleteSubcommand familyDeleteSubcommand = new FamilyDeleteSubcommand();

    public static final AbstractSubcommand[] subcommands = {
            familyListSubcommand,
            familyBackgroundSubcommand,
            familyHelpSubcommand,
            familyTreeSubcommand,
            familyMarrySubcommand,
            familyAdoptSubcommand,
            familyGenderSubcommand,
            familySiblingSubcommand,
            familyReloadSubcommand,
            familyDeleteSubcommand
    };

    public FamilySubcommand() {
        super(mainCommand, name, permission, subcommands);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            if (args.length == 0) {
                familyHelpSubcommand.execute(sender, args);
            } else {
                final String subcommand = args[0];
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                if (language.checkIsSubcommand("family", "gender", subcommand)) {
                    familyGenderSubcommand.execute(sender, newArgs);
                } else if (language.checkIsSubcommand("family", "adopt", subcommand)) {
                    familyAdoptSubcommand.execute(sender, newArgs);
                } else if (language.checkIsSubcommand("family", "marry", subcommand)) {
                    familyMarrySubcommand.execute(sender, newArgs);
                } else if (language.checkIsSubcommand("family", "sibling", subcommand)) {
                    familySiblingSubcommand.execute(sender, newArgs);
                } else if (language.checkIsSubcommand("family", "reload", subcommand)) {
                    familyReloadSubcommand.execute(sender, args);
                } else if (language.checkIsSubcommand("family", "list", subcommand)) {
                    familyListSubcommand.execute(sender, args);
                } else if (language.checkIsSubcommand("family", "background", subcommand)) {
                    familyBackgroundSubcommand.execute(sender, args);
                } else if (language.checkIsSubcommand("family", "help", subcommand)) {
                    familyHelpSubcommand.execute(sender, args);
                } else if (language.checkIsSubcommand("family", "tree", subcommand)) {
                    familyTreeSubcommand.execute(sender, args);
                } else if (language.checkIsSubcommand("family", "delete", subcommand)) {
                    familyDeleteSubcommand.execute(sender, args);
                } else if (language.checkIsSubcommand("family", "help", subcommand)) {
                    familyHelpSubcommand.execute(sender, args);
                } else {
                    sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                }
            }
        }
        return true;
    }

}
