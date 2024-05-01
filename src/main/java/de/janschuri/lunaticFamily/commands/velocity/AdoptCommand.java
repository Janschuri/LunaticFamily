package de.janschuri.lunaticFamily.commands.velocity;


import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.janschuri.lunaticFamily.commands.subcommands.family.AdoptSubcommand;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.velocity.PlayerSender;
import de.janschuri.lunaticlib.senders.velocity.Sender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class AdoptCommand implements SimpleCommand {

    private final AdoptSubcommand adoptSubcommand = new AdoptSubcommand();

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        // Get the arguments after the command alias
        String[] args = invocation.arguments();

        if (source instanceof Player) {
            PlayerSender commandSender = new PlayerSender((Player) source);
            adoptSubcommand.execute(commandSender, args);
        } else {
            Sender consoleCommandSender = new Sender(source);
            adoptSubcommand.execute(consoleCommandSender, args);
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        boolean perm = invocation.source().hasPermission("lunaticfamily.adopt");
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
        newArgs[0] = "adopt";
        if (args.length == 0) {
            newArgs[1] = "";
        }
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return CompletableFuture.completedFuture(adoptSubcommand.tabComplete(new PlayerSender(source), newArgs));
    }
}