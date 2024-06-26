package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.Family;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.LunaticCommand;
import de.janschuri.lunaticlib.common.command.LunaticHelpCommand;
import de.janschuri.lunaticlib.Sender;

import java.util.List;

public class Adopt extends Subcommand {

    @Override
    public String getPermission() {
        return "lunaticfamily.adopt";
    }

    @Override
    public LunaticHelpCommand getHelpCommand() {
        return new LunaticHelpCommand(getLanguageConfig(), this);
    }

    @Override
    public String getName() {
        return "adopt";
    }

    @Override
    public Family getParentCommand() {
        return new Family();
    }

    @Override
    public List<LunaticCommand> getSubcommands() {
        return List.of(
                new AdoptAccept(),
                new AdoptDeny(),
                new AdoptKickout(),
                new AdoptMoveout(),
                new AdoptPropose(),
                new AdoptSet(),
                new AdoptUnset(),
                new AdoptPriest(),
                new AdoptEmoji(),
                new AdoptList(),
                getHelpCommand()
        );
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

        for (LunaticCommand sc : getSubcommands()) {
            if (checkIsSubcommand(sc, subcommand)) {
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                return sc.execute(sender, newArgs);
            }
        }

        sender.sendMessage(getMessage(WRONG_USAGE_MK));
        Logger.debugLog("Adopt: Wrong usage");

        return true;
    }

    @Override
    public boolean isPrimaryCommand() {
        return true;
    }

    @Override
    public String getFullCommand() {
        return new Family().getName() + " " + getName();
    }
}
