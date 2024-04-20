package de.janschuri.lunaticFamily.commands.paper;

import de.janschuri.lunaticFamily.commands.senders.PaperCommandSender;
import de.janschuri.lunaticFamily.commands.senders.PaperPlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.family.MarrySubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MarryCommand implements CommandExecutor, TabCompleter {

    MarrySubcommand familyMarrySubcommand = new MarrySubcommand();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (sender instanceof Player) {
            PaperPlayerCommandSender commandSender = new PaperPlayerCommandSender(sender);
            familyMarrySubcommand.execute(commandSender, args);
        } else {
            PaperCommandSender consoleCommandSender = new PaperCommandSender(sender);
            familyMarrySubcommand.execute(consoleCommandSender, args);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "marry";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        PaperPlayerCommandSender playerCommandSender = new PaperPlayerCommandSender(sender);
        return new ArrayList<>(familyMarrySubcommand.tabComplete(playerCommandSender, newArgs));
    }
}

