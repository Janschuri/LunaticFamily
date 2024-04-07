package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.commands.subcommands.family.AdoptSubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdoptCommand implements CommandExecutor, TabCompleter {
    private final AdoptSubcommand familyAdoptSubcommand = new AdoptSubcommand();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        familyAdoptSubcommand.execute(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "adopt";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return new ArrayList<>(familyAdoptSubcommand.tabComplete(sender, newArgs));
    }
}
