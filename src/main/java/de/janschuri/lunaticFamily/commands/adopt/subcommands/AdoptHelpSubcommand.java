package de.janschuri.lunaticFamily.commands.adopt.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdoptHelpSubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "help";
    private static final String permission = "lunaticfamily.adopt";
    private static final Subcommand[] subcommands = {
            new AdoptAcceptSubcommand(),
            new AdoptDenySubcommand(),
            new AdoptKickoutSubcommand(),
            new AdoptMoveoutSubcommand(),
            new AdoptProposeSubcommand(),
            new AdoptSetSubcommand(),
            new AdoptUnsetSubcommand()
    };

    public AdoptHelpSubcommand() {
        super(mainCommand, name, permission);
    }

    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            TextComponent msg = new TextComponent(Language.getMessage(mainCommand + "_help") + "\n");

            for (Subcommand subcommand : subcommands) {
                msg.addExtra(subcommand.getHelp(sender));
            }

            sender.sendMessage(msg);
        }
    }
}
