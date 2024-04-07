package de.janschuri.lunaticFamily.commands.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.Language;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class HelpSubcommand extends Subcommand {

    private final Class<?> commandCommandClass;

    public HelpSubcommand(String mainCommand, String name, String permission, Class<?> commandClass) {
        super(mainCommand, name, permission);
        this.commandCommandClass = commandClass;
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            Component msg = Component.text(Language.getMessage(mainCommand + "_help") + "\n");

            try {
                Subcommand command = (Subcommand) commandCommandClass.getDeclaredConstructor().newInstance();

                for (Subcommand subcommand : command.subcommands) {
                    if (!(subcommand instanceof HelpSubcommand)) {
                        msg = msg.append(subcommand.getHelp(sender));
                    }
                }
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }

            sender.sendMessage(msg);
        }
    }
}
