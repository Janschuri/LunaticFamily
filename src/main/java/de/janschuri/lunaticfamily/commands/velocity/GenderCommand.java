package de.janschuri.lunaticfamily.commands.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import de.janschuri.lunaticfamily.commands.subcommands.family.GenderSubcommand;
import de.janschuri.lunaticfamily.utils.Utils;
import de.janschuri.lunaticlib.senders.velocity.PlayerSender;
import de.janschuri.lunaticlib.senders.velocity.Sender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class GenderCommand implements SimpleCommand {

    private final GenderSubcommand genderSubcommand = new GenderSubcommand();

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        // Get the arguments after the command alias
        String[] args = invocation.arguments();

        if (source instanceof Player) {
            PlayerSender commandSender = new PlayerSender((Player) source);
            genderSubcommand.execute(commandSender, args);
        } else {
            Sender consoleCommandSender = new Sender(source);
            genderSubcommand.execute(consoleCommandSender, args);
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        boolean perm = invocation.source().hasPermission("lunaticfamily.gender");
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
        newArgs[0] = "gender";
        if (args.length == 0) {
            newArgs[1] = "";
        }
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return CompletableFuture.completedFuture(genderSubcommand.tabComplete(new PlayerSender(source), newArgs));
    }
}