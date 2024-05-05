package de.janschuri.lunaticfamily.commands.paper;

import de.janschuri.lunaticfamily.commands.subcommands.family.FamilySubcommand;
import de.janschuri.lunaticlib.senders.AbstractSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FamilyCommand implements CommandExecutor, TabCompleter {

    private final FamilySubcommand familySubcommand = new FamilySubcommand();

    @Override
    public boolean onCommand(@NotNull org.bukkit.command.CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        AbstractSender commandSender = AbstractSender.getSender(sender);
        return familySubcommand.execute(commandSender, args);
    }

    @Override
    public List<String> onTabComplete(@NotNull org.bukkit.command.CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "family";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        AbstractSender commandSender = AbstractSender.getSender(sender);
        return new ArrayList<>(familySubcommand.tabComplete(commandSender, newArgs));
    }
}

