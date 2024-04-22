package de.janschuri.lunaticFamily.commands.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.janschuri.lunaticFamily.commands.subcommands.family.MarrySubcommand;
import de.janschuri.lunaticFamily.senders.velocity.CommandSender;
import de.janschuri.lunaticFamily.senders.velocity.PlayerCommandSender;
import de.janschuri.lunaticFamily.utils.Utils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class MarryCommand implements SimpleCommand {

    private final MarrySubcommand marrySubcommand = new MarrySubcommand();

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        // Get the arguments after the command alias
        String[] args = invocation.arguments();

        if (source instanceof Player) {
            PlayerCommandSender commandSender = new PlayerCommandSender((Player) source);
            marrySubcommand.execute(commandSender, args);
        } else {
            CommandSender consoleCommandSender = new CommandSender(source);
            marrySubcommand.execute(consoleCommandSender, args);
        }
    }

    // This method allows you to control who can execute the command.
    // If the executor does not have the required permission,
    // the execution of the command and the control of its autocompletion
    // will be sent directly to the server on which the sender is located
    @Override
    public boolean hasPermission(final Invocation invocation) {
        boolean perm = invocation.source().hasPermission("lunaticfamily.marry");
        boolean onWhitelistedServer = true;
        if (invocation.source() instanceof Player) {
            onWhitelistedServer = Utils.getUtils().isPlayerOnWhitelistedServer(((Player) invocation.source()).getUniqueId());
        }
        return perm && onWhitelistedServer;
    }

    // Here you can offer argument suggestions in the same way as the previous method,
    // but asynchronously. It is recommended to use this method instead of the previous one
    // especially in cases where you make a more extensive logic to provide the suggestions
    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        int newSize = args.length > 0 ? args.length + 1 : 2;
        String[] newArgs = new String[newSize];
        newArgs[0] = "marry";
        if (args.length == 0) {
            newArgs[1] = "";
        }
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return CompletableFuture.completedFuture(marrySubcommand.tabComplete(new PlayerCommandSender(source), newArgs));
    }
}