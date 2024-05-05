package de.janschuri.lunaticfamily.commands.bungee;

import de.janschuri.lunaticfamily.commands.subcommands.family.GenderSubcommand;
import de.janschuri.lunaticlib.senders.AbstractSender;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class GenderCommand extends Command implements TabExecutor {

    private final GenderSubcommand genderSubcommand = new GenderSubcommand();

    public GenderCommand() {
        super("gender", "lunaticfamily.gender");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        AbstractSender commandSender = AbstractSender.getSender(sender);
        genderSubcommand.execute(commandSender, args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        String[] newArgs = new String[args.length + 1];
        newArgs[0] = "gender";
        System.arraycopy(args, 0, newArgs, 1, args.length);
        AbstractSender commandSender = AbstractSender.getSender(sender);
        return genderSubcommand.tabComplete(commandSender, newArgs);
    }
}

