package de.janschuri.lunaticfamily.common.commands.priest;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.family.Family;
import de.janschuri.lunaticlib.Command;
import de.janschuri.lunaticlib.common.command.HasHelpCommand;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;

import java.util.List;

public class Priest extends FamilyCommand implements HasParentCommand, HasHelpCommand {

    @Override
    public List<Command> getSubcommands() {
        return List.of(
                new PriestMarry(),
                new PriestSibling(),
                new PriestAdopt(),
                new PriestStats(),
                getHelpCommand()
        );
    }

    @Override
    public Component pageParamName() {
        return getMessage(PAGE_MK);
    }

    @Override
    public Family getParentCommand() {
        return new Family();
    }

    @Override
    public boolean isPrimaryCommand() {
        return true;
    }

    @Override
    public String getPermission() {
        return "lunaticfamily.priest";
    }

    @Override
    public String getName() {
        return "priest";
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }
        if (args.length == 0) {
            getHelpCommand().execute(sender, args);
            return true;
        }

        final String subcommand = args[0];

        for (Command sc : getSubcommands()) {
            if (checkIsSubcommand(sc, subcommand)) {
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                return sc.execute(sender, newArgs);
            }
        }
        sender.sendMessage(getMessage(WRONG_USAGE_MK));
        return true;
    }

    @Override
    public String getFullCommand() {
        return new Family().getName() + " " + getName();
    }
}