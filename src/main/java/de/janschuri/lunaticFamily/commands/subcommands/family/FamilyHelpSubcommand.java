package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class FamilyHelpSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "help";
    private static final String permission = "lunaticfamily.family";

    public FamilyHelpSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            Component msg = Component.text(Language.getMessage(mainCommand + "_help") + "\n");

            for (Subcommand subcommand : FamilySubcommand.subcommands) {
                if (!(subcommand instanceof FamilyHelpSubcommand)) {
                    msg = msg.append(subcommand.getHelp(sender));
                }
            }

            sender.sendMessage(msg);
        }

    }
}
