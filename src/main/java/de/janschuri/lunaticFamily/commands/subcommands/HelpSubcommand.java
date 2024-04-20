package de.janschuri.lunaticFamily.commands.subcommands;

import de.janschuri.lunaticFamily.commands.ClickableMessage;
import de.janschuri.lunaticFamily.commands.senders.CommandSender;
import de.janschuri.lunaticFamily.commands.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.config.Language;
//import net.kyori.adventure.text.Component;
//import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class HelpSubcommand extends Subcommand {

    private final Class<?> commandClass;

    public HelpSubcommand(String mainCommand, String name, String permission, Class<?> commandClass) {
        super(mainCommand, name, permission);
        this.commandClass = commandClass;
    }
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof PlayerCommandSender)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
            return true;
        }
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
            return true;
        }

        PlayerCommandSender playerCommandSender = (PlayerCommandSender) sender;
        List<ClickableMessage> msg = new ArrayList<>();
        msg.add(new ClickableMessage(Language.prefix + Language.getMessage(mainCommand + "_help") + "\n"));

        try {
            Subcommand command = (Subcommand) commandClass.getDeclaredConstructor().newInstance();
            for (Subcommand subcommand : command.subcommands) {
                if (!(subcommand instanceof HelpSubcommand)) {
                    msg.add(subcommand.getHelp(sender));
                }
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }

        playerCommandSender.sendMessage(msg);

        return true;
    }
}
