package de.janschuri.lunaticFamily.commands.family.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class FamilyHelpSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "help";
    private static final String permission = "lunaticfamily.family";
    private static final Subcommand[] subcommands = {
            new FamilyBackgroundSubcommand(),
            new FamilyListSubcommand(),
            new FamilyReloadSubcommand(),
            new GenderSubcommand(),
            new AdoptSubcommand(),
            new MarrySubcommand(),
            new SiblingSubcommand()
    };

    public FamilyHelpSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            Component msg = Component.text(Language.getMessage(mainCommand + "_help") + "\n");

            for (Subcommand subcommand : subcommands) {
                msg = msg.append(subcommand.getHelp(sender));
            }

            sender.sendMessage(msg);
        }

    }
}
