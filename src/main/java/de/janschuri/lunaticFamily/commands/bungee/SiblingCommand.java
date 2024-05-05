package de.janschuri.lunaticFamily.commands.bungee;

import de.janschuri.lunaticFamily.commands.subcommands.family.SiblingSubcommand;
import de.janschuri.lunaticlib.senders.AbstractSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class SiblingCommand extends Command implements TabExecutor {

    private final SiblingSubcommand siblingSubcommand = new SiblingSubcommand();

    public SiblingCommand() {
        super("sibling", "lunaticfamily.sibling");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        AbstractSender commandSender = AbstractSender.getSender(sender);
        siblingSubcommand.execute(commandSender, args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "sibling";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        AbstractSender commandSender = AbstractSender.getSender(sender);
        return siblingSubcommand.tabComplete(commandSender, newArgs);
    }
}
