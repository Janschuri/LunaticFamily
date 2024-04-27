package de.janschuri.lunaticFamily.commands.paper;

import de.janschuri.lunaticFamily.commands.subcommands.family.AdoptSubcommand;
import de.janschuri.lunaticlib.senders.paper.PlayerSender;
import de.janschuri.lunaticlib.senders.paper.Sender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdoptCommand implements CommandExecutor, TabCompleter {
    private final AdoptSubcommand familyAdoptSubcommand = new AdoptSubcommand();

    @Override
    public boolean onCommand(@NotNull org.bukkit.command.CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (sender instanceof Player) {
            PlayerSender commandSender = new PlayerSender(sender);
            familyAdoptSubcommand.execute(commandSender, args);
        } else {
            Sender consoleCommandSender = new Sender(sender);
            familyAdoptSubcommand.execute(consoleCommandSender, args);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull org.bukkit.command.CommandSender sender, @NotNull Command cmd, @NotNull String commandLabel, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "adopt";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        PlayerSender playerCommandSender = new PlayerSender(sender);
        return new ArrayList<>(familyAdoptSubcommand.tabComplete(playerCommandSender, newArgs));
    }
}
