package de.janschuri.lunaticfamily.common.commands.priest;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.family.Family;
import de.janschuri.lunaticlib.Command;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.common.command.HasHelpCommand;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;

import java.util.List;
import java.util.Map;

public class Priest extends FamilyCommand implements HasParentCommand, HasHelpCommand {

    private static final Priest INSTANCE = new Priest();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- List all priest commands.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Liste alle Priester Befehle auf.");
    private static final CommandMessageKey HELP_HEADER_MK = new LunaticCommandMessageKey(INSTANCE, "help_header")
            .defaultMessage("en", "Priest commands:")
            .defaultMessage("de", "Priester Befehle:");


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
    public MessageKey pageParamName() {
        return PAGE_MK;
    }

    @Override
    public MessageKey getHelpHeader() {
        return HELP_HEADER_MK;
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
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }

    @Override
    public String getFullCommand() {
        return new Family().getName() + " " + getName();
    }
}