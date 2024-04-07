package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.commands.subcommands.marry.*;
import de.janschuri.lunaticFamily.config.Language;
import org.bukkit.command.CommandSender;

public class MarrySubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "marry";
    private static final String permission = "lunaticfamily.marry";

    private static final MarrySetSubcommand marrySetSubcommand = new MarrySetSubcommand();
    private static final MarryUnsetSubcommand marryUnsetSubcommand = new MarryUnsetSubcommand();
    private static final MarryProposeSubcommand marryProposeSubcommand = new MarryProposeSubcommand();
    private static final MarryPriestSubcommand marryPriestSubcommand = new MarryPriestSubcommand();
    private static final AcceptSubcommand ACCEPT_SUBCOMMAND = new AcceptSubcommand();
    private static final MarryDenySubcommand marryDenySubcommand = new MarryDenySubcommand();
    private static final MarryDivorceSubcommand marryDivorceSubcommand = new MarryDivorceSubcommand();
    private static final MarryKissSubcommand marryKissSubcommand = new MarryKissSubcommand();
    private static final MarryGiftSubcommand marryGiftSubcommand = new MarryGiftSubcommand();
    private static final MarryBackpackSubcommand marryBackpackSubcommand = new MarryBackpackSubcommand();
    private static final MarryHeartSubcommand marryHeartSubcommand = new MarryHeartSubcommand();
    private static final MarryListSubcommand marryListSubcommand = new MarryListSubcommand();
    private static final MarryHelpSubcommand marryHelpSubcommand = new MarryHelpSubcommand();


    public static final Subcommand[] subcommands = {
            ACCEPT_SUBCOMMAND,
            marryBackpackSubcommand,
            marryDenySubcommand,
            marryDivorceSubcommand,
            marryGiftSubcommand,
            marryHelpSubcommand,
            marryKissSubcommand,
            marryListSubcommand,
            marryPriestSubcommand,
            marryProposeSubcommand,
            marrySetSubcommand,
            marryUnsetSubcommand,
            marryHeartSubcommand
    };

    public MarrySubcommand() {
        super(mainCommand, name, permission, subcommands);
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            if (args.length == 0) {
                marryHelpSubcommand.execute(sender, args);
            } else {
                final String subcommand = args[0];
                if (Language.checkIsSubcommand(name, "set", subcommand)) {
                    marrySetSubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "unset", subcommand)) {
                    marryUnsetSubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "propose", subcommand)) {
                    marryProposeSubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "priest", subcommand)) {
                    marryPriestSubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "accept", subcommand)) {
                    ACCEPT_SUBCOMMAND.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "deny", subcommand)) {
                    marryDenySubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "divorce", subcommand)) {
                    marryDivorceSubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "kiss", subcommand)) {
                    marryKissSubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "gift", subcommand)) {
                    marryGiftSubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "backpack", subcommand)) {
                    marryBackpackSubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "heart", subcommand)) {
                    marryHeartSubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "list", subcommand)) {
                    marryListSubcommand.execute(sender, args);
                } else if (Language.checkIsSubcommand(name, "help", subcommand)) {
                    marryHelpSubcommand.execute(sender, args);
                } else {
                    sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                }
            }
        }
    }
}
