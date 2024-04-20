package de.janschuri.lunaticFamily.commands.velocity;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import java.util.concurrent.CompletableFuture;
import java.util.List;

import com.velocitypowered.api.proxy.Player;
import de.janschuri.lunaticFamily.commands.senders.VelocityCommandSender;
import de.janschuri.lunaticFamily.commands.senders.VelocityPlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.family.FamilySubcommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class VelocityFamilyCommand implements SimpleCommand {

    private final FamilySubcommand familySubcommand = new FamilySubcommand();

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();
        // Get the arguments after the command alias
        String[] args = invocation.arguments();

        if (source instanceof Player) {
            VelocityPlayerCommandSender commandSender = new VelocityPlayerCommandSender((Player) source);
            familySubcommand.execute(commandSender, args);
        } else {
            VelocityCommandSender consoleCommandSender = new VelocityCommandSender(source);
            familySubcommand.execute(consoleCommandSender, args);
        }
    }

    // This method allows you to control who can execute the command.
    // If the executor does not have the required permission,
    // the execution of the command and the control of its autocompletion
    // will be sent directly to the server on which the sender is located
    @Override
    public boolean hasPermission(final Invocation invocation) {
//        return invocation.source().hasPermission("lunaticfamily.family");
        return true;
    }

    // With this method you can control the suggestions to send
    // to the CommandSource according to the arguments
    // it has already written or other requirements you need
    @Override
    public List<String> suggest(final Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        return familySubcommand.tabComplete(new VelocityPlayerCommandSender(source), args);
    }

    // Here you can offer argument suggestions in the same way as the previous method,
    // but asynchronously. It is recommended to use this method instead of the previous one
    // especially in cases where you make a more extensive logic to provide the suggestions
    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        return CompletableFuture.completedFuture(List.of());
    }
}