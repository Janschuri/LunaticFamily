package de.janschuri.lunaticFamily.commands.bungee;

import de.janschuri.lunaticFamily.commands.subcommands.family.AdoptSubcommand;
import de.janschuri.lunaticFamily.commands.subcommands.family.FamilySubcommand;
import de.janschuri.lunaticlib.senders.AbstractSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class AdoptCommand extends Command implements TabExecutor {

    private final AdoptSubcommand adoptSubcommand = new AdoptSubcommand();

    public AdoptCommand() {
        super("adopt", "lunaticfamily.adopt");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        AbstractSender commandSender = AbstractSender.getSender(sender);
        adoptSubcommand.execute(commandSender, args);
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "adopt";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        AbstractSender commandSender = AbstractSender.getSender(sender);
        return adoptSubcommand.tabComplete(commandSender, newArgs);
    }
}
