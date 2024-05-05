package de.janschuri.lunaticfamily.commands.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

import java.util.concurrent.CompletableFuture;
import java.util.List;

import com.velocitypowered.api.proxy.Player;
import de.janschuri.lunaticfamily.commands.subcommands.family.FamilySubcommand;
import de.janschuri.lunaticfamily.utils.Utils;
import de.janschuri.lunaticlib.senders.velocity.PlayerSender;
import de.janschuri.lunaticlib.senders.velocity.Sender;

public final class FamilyCommand implements SimpleCommand {

    private final FamilySubcommand familySubcommand = new FamilySubcommand();

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        // Get the arguments after the command alias
        String[] args = invocation.arguments();

        if (source instanceof Player) {
            PlayerSender commandSender = new PlayerSender((Player) source);
            familySubcommand.execute(commandSender, args);
        } else {
            Sender consoleCommandSender = new Sender(source);
            familySubcommand.execute(consoleCommandSender, args);
        }
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        boolean perm = invocation.source().hasPermission("lunaticfamily.family");
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
        newArgs[0] = "family";
        if (args.length == 0) {
            newArgs[1] = "";
        }
        System.arraycopy(args, 0, newArgs, 1, args.length);
        return CompletableFuture.completedFuture(familySubcommand.tabComplete(new PlayerSender(source), newArgs));
    }
}