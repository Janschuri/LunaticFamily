package de.janschuri.lunaticFamily.commands.marry;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.family.subcommands.MarrySubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MarryCommand implements CommandExecutor, TabCompleter {

    private final LunaticFamily plugin;
    MarrySubcommand familyMarrySubcommand = new MarrySubcommand();

    public MarryCommand(LunaticFamily plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        familyMarrySubcommand.execute(sender, args, plugin);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "marry";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return new ArrayList<>(familyMarrySubcommand.tabComplete(sender, newArgs));
    }
}

