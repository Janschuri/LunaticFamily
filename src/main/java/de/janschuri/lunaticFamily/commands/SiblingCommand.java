package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.commands.subcommands.family.SiblingSubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SiblingCommand implements CommandExecutor, TabCompleter {
    private final SiblingSubcommand familySiblingSubcommand = new SiblingSubcommand();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        familySiblingSubcommand.execute(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "sibling";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return new ArrayList<>(familySiblingSubcommand.tabComplete(sender, newArgs));
    }
}