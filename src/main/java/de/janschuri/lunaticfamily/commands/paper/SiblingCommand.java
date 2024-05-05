package de.janschuri.lunaticfamily.commands.paper;

import de.janschuri.lunaticfamily.commands.subcommands.family.SiblingSubcommand;
import de.janschuri.lunaticlib.senders.AbstractSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SiblingCommand implements CommandExecutor, TabCompleter {
    private final SiblingSubcommand familySiblingSubcommand = new SiblingSubcommand();


    @Override
    public boolean onCommand(org.bukkit.command.CommandSender sender, Command command, String label, String[] args) {
        AbstractSender commandSender = AbstractSender.getSender(sender);
        return familySiblingSubcommand.execute(commandSender, args);
    }

    @Override
    public List<String> onTabComplete(@NotNull org.bukkit.command.CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "sibling";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        AbstractSender commandSender = AbstractSender.getSender(sender);
        return new ArrayList<>(familySiblingSubcommand.tabComplete(commandSender, newArgs));
    }
}