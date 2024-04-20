package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.commands.CommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.utils.Utils;

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

    public static final Subcommand[] subcommands = {
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
    public boolean execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            if (args.length == 0) {
                familyHelpSubcommand.execute(sender, args);
            } else {
                final String subcommand = args[0];
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                if (Utils.checkIsSubcommand("family", "gender", subcommand)) {
                    familyGenderSubcommand.execute(sender, newArgs);
                } else if (Utils.checkIsSubcommand("family", "adopt", subcommand)) {
                    familyAdoptSubcommand.execute(sender, newArgs);
                } else if (Utils.checkIsSubcommand("family", "marry", subcommand)) {
                    familyMarrySubcommand.execute(sender, newArgs);
                } else if (Utils.checkIsSubcommand("family", "sibling", subcommand)) {
                    familySiblingSubcommand.execute(sender, newArgs);
                } else if (Utils.checkIsSubcommand("family", "reload", subcommand)) {
                    familyReloadSubcommand.execute(sender, args);
                } else if (Utils.checkIsSubcommand("family", "list", subcommand)) {
                    familyListSubcommand.execute(sender, args);
                } else if (Utils.checkIsSubcommand("family", "background", subcommand)) {
                    familyBackgroundSubcommand.execute(sender, args);
                } else if (Utils.checkIsSubcommand("family", "help", subcommand)) {
                    familyHelpSubcommand.execute(sender, args);
                } else if (Utils.checkIsSubcommand("family", "tree", subcommand)) {
                    familyTreeSubcommand.execute(sender, args);
                } else if (Utils.checkIsSubcommand("family", "delete", subcommand)) {
                    familyDeleteSubcommand.execute(sender, args);
                } else if (Utils.checkIsSubcommand("family", "help", subcommand)) {
                    familyHelpSubcommand.execute(sender, args);
                } else {
                    sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                }
            }
        }
        return true;
    }

}
