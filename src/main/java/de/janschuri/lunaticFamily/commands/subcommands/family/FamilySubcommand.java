package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.CommandSender;

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

    public static final Subcommand[] subcommands = {
            familyListSubcommand,
            familyBackgroundSubcommand,
            familyHelpSubcommand,
            familyTreeSubcommand,
            familyMarrySubcommand,
            familyAdoptSubcommand,
            familyGenderSubcommand,
            familySiblingSubcommand,
            familyReloadSubcommand
    };

    public FamilySubcommand() {
        super(mainCommand, name, permission, subcommands);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            if (args.length == 0) {
                familyHelpSubcommand.execute(sender, args, plugin);
            } else {
                final String subcommand = args[0];
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                if (Language.checkIsSubcommand("family", "gender", subcommand)) {
                    familyGenderSubcommand.execute(sender, newArgs, plugin);
                } else if (Language.checkIsSubcommand("family", "adopt", subcommand)) {
                    familyAdoptSubcommand.execute(sender, newArgs, plugin);
                } else if (Language.checkIsSubcommand("family", "marry", subcommand)) {
                    familyMarrySubcommand.execute(sender, newArgs, plugin);
                } else if (Language.checkIsSubcommand("family", "sibling", subcommand)) {
                    familySiblingSubcommand.execute(sender, newArgs, plugin);
                } else if (Language.checkIsSubcommand("family", "reload", subcommand)) {
                    familyReloadSubcommand.execute(sender, args, plugin);
                } else if (Language.checkIsSubcommand("family", "list", subcommand)) {
                    familyListSubcommand.execute(sender, args, plugin);
                } else if (Language.checkIsSubcommand("family", "background", subcommand)) {
                    familyBackgroundSubcommand.execute(sender, args, plugin);
                } else if (Language.checkIsSubcommand("family", "tree", subcommand)) {
                    familyTreeSubcommand.execute(sender, args, plugin);
                } else if (Language.checkIsSubcommand("family", "help", subcommand)) {
                    familyHelpSubcommand.execute(sender, args, plugin);
                } else {
                    sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                }
            }
        }
    }

}
