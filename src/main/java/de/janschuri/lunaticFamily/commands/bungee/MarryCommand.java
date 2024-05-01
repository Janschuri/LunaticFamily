package de.janschuri.lunaticFamily.commands.bungee;

import de.janschuri.lunaticFamily.commands.subcommands.family.GenderSubcommand;
import de.janschuri.lunaticFamily.commands.subcommands.family.MarrySubcommand;
import de.janschuri.lunaticlib.senders.AbstractSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class MarryCommand extends Command implements TabExecutor {

    private final MarrySubcommand marrySubcommand = new MarrySubcommand();

    public MarryCommand() {
        super("marry", "lunaticfamily.marry");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        AbstractSender commandSender = AbstractSender.getSender(sender);
        marrySubcommand.execute(commandSender, args);
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "marry";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        AbstractSender commandSender = AbstractSender.getSender(sender);
        return marrySubcommand.tabComplete(commandSender, newArgs);
    }
}