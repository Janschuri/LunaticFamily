package de.janschuri.lunaticFamily.commands.gender.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class GenderHelpSubcommand extends Subcommand {
    private static final String mainCommand = "gender";
    private static final String name = "help";
    private static final String permission = "lunaticfamily.gender";
    private static final Subcommand[] subcommands = {
            new GenderInfoSubcommand(),
            new GenderSetSubcommand()
    };

    public GenderHelpSubcommand() {
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
