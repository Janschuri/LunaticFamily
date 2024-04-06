package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.commands.subcommands.family.MarrySubcommand;
import de.janschuri.lunaticFamily.config.Language;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class MarryHelpSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "help";
    private static final String permission = "lunaticfamily.marry";

    public MarryHelpSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            Component msg = Component.text(Language.getMessage(mainCommand + "_help") + "\n");

            for (Subcommand subcommand : MarrySubcommand.subcommands) {
                if (!(subcommand instanceof MarryHelpSubcommand)) {
                    msg = msg.append(subcommand.getHelp(sender));
                }
            }

            sender.sendMessage(msg);
        }
    }
}
