package de.janschuri.lunaticFamily.commands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.family.FamilySubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FamilyCommand implements CommandExecutor, TabCompleter {

    private final LunaticFamily plugin;
    private final FamilySubcommand familySubcommand = new FamilySubcommand();


    public FamilyCommand(LunaticFamily plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        familySubcommand.execute(sender, args, plugin);

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "family";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return new ArrayList<>(familySubcommand.tabComplete(sender, newArgs));
    }
}

