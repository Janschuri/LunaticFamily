package de.janschuri.lunaticFamily.commands.sibling.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SiblingHelpSubcommand extends Subcommand {
    private static final String mainCommand = "sibling";
    private static final String name = "help";
    private static final String permission = "lunaticfamily.sibling";
    private static final Subcommand[] subcommands = {
            new SiblingAcceptSubcommand(),
            new SiblingDenySubcommand(),
            new SiblingProposeSubcommand(),
            new SiblingSetSubcommand(),
            new SiblingUnsetSubcommand(),
            new SiblingUnsiblingSubcommand()
    };

    public SiblingHelpSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
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
