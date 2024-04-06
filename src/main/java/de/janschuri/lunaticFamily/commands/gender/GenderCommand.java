package de.janschuri.lunaticFamily.commands.gender;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.family.subcommands.GenderSubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GenderCommand implements CommandExecutor, TabCompleter {

    private final LunaticFamily plugin;
    private final GenderSubcommand familyGenderSubcommand = new GenderSubcommand();

    public GenderCommand(LunaticFamily plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        familyGenderSubcommand.execute(sender, args, plugin);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "gender";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return new ArrayList<>(familyGenderSubcommand.tabComplete(sender, newArgs));
    }
}
