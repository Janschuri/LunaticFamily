package de.janschuri.lunaticFamily.commands.paper;

import de.janschuri.lunaticFamily.commands.senders.PaperCommandSender;
import de.janschuri.lunaticFamily.commands.senders.PaperPlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.family.SiblingSubcommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SiblingCommand implements CommandExecutor, TabCompleter {
    private final SiblingSubcommand familySiblingSubcommand = new SiblingSubcommand();


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            PaperPlayerCommandSender commandSender = new PaperPlayerCommandSender(sender);
            familySiblingSubcommand.execute(commandSender, args);
        } else {
            PaperCommandSender consoleCommandSender = new PaperCommandSender(sender);
            familySiblingSubcommand.execute(consoleCommandSender, args);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "sibling";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        PaperPlayerCommandSender playerCommandSender = new PaperPlayerCommandSender(sender);
        return new ArrayList<>(familySiblingSubcommand.tabComplete(playerCommandSender, newArgs));
    }
}