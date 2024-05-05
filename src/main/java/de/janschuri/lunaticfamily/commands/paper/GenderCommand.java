package de.janschuri.lunaticfamily.commands.paper;

import de.janschuri.lunaticfamily.commands.subcommands.family.GenderSubcommand;
import de.janschuri.lunaticlib.senders.AbstractSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GenderCommand implements CommandExecutor, TabCompleter {
    private final GenderSubcommand genderSubcommand = new GenderSubcommand();


    @Override
    public boolean onCommand(@NotNull org.bukkit.command.CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        AbstractSender commandSender = AbstractSender.getSender(sender);
        return genderSubcommand.execute(commandSender, args);
    }

    @Override
    public List<String> onTabComplete(@NotNull org.bukkit.command.CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "gender";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        AbstractSender commandSender = AbstractSender.getSender(sender);
        return new ArrayList<>(genderSubcommand.tabComplete(commandSender, newArgs));
    }
}
