package de.janschuri.lunaticfamily.commands.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.janschuri.lunaticfamily.commands.subcommands.family.MarrySubcommand;
import de.janschuri.lunaticfamily.utils.Utils;
import de.janschuri.lunaticlib.senders.velocity.PlayerSender;
import de.janschuri.lunaticlib.senders.velocity.Sender;

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
            PlayerSender commandSender = new PlayerSender((Player) source);
            marrySubcommand.execute(commandSender, args);
        } else {
            Sender consoleCommandSender = new Sender(source);
            marrySubcommand.execute(consoleCommandSender, args);
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        boolean perm = invocation.source().hasPermission("lunaticfamily.marry");
        boolean onWhitelistedServer = true;
        if (invocation.source() instanceof Player) {
            onWhitelistedServer = Utils.isPlayerOnRegisteredServer(((Player) invocation.source()).getUniqueId());
        }
        return perm && onWhitelistedServer;
    }

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
        return CompletableFuture.completedFuture(marrySubcommand.tabComplete(new PlayerSender(source), newArgs));
    }
}