package de.janschuri.lunaticfamily.commands.bungee;

import de.janschuri.lunaticfamily.commands.subcommands.family.AdoptSubcommand;
import de.janschuri.lunaticfamily.config.Language;
import de.janschuri.lunaticlib.senders.AbstractSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class AdoptCommand extends Command implements TabExecutor {

    private final AdoptSubcommand adoptSubcommand = new AdoptSubcommand();

    public AdoptCommand() {
        super("adopt", "lunaticfamily.adopt", Language.getLanguage().getAliases("adopt").toArray(new String[0]));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        AbstractSender commandSender = AbstractSender.getSender(sender);
        adoptSubcommand.execute(commandSender, args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "adopt";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        AbstractSender commandSender = AbstractSender.getSender(sender);
        return adoptSubcommand.tabComplete(commandSender, newArgs);
    }
}
