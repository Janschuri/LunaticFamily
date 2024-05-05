package de.janschuri.lunaticfamily.commands.bungee;

import de.janschuri.lunaticfamily.commands.subcommands.family.MarrySubcommand;
import de.janschuri.lunaticfamily.config.Language;
import de.janschuri.lunaticlib.senders.AbstractSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class MarryCommand extends Command implements TabExecutor {

    private final MarrySubcommand marrySubcommand = new MarrySubcommand();

    public MarryCommand() {
        super("marry", "lunaticfamily.marry", Language.getLanguage().getAliases("marry").toArray(new String[0]));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        AbstractSender commandSender = AbstractSender.getSender(sender);
        marrySubcommand.execute(commandSender, args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "marry";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        AbstractSender commandSender = AbstractSender.getSender(sender);
        return marrySubcommand.tabComplete(commandSender, newArgs);
    }
}