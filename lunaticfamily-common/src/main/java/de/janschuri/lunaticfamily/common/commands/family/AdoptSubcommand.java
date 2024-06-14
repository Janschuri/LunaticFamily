package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.adopt.*;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.LunaticCommand;
import de.janschuri.lunaticlib.common.command.LunaticHelpCommand;
import de.janschuri.lunaticlib.Sender;

import java.util.List;

public class AdoptSubcommand extends Subcommand {

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
    public FamilySubcommand getParentCommand() {
        return new FamilySubcommand();
    }

    @Override
    public List<LunaticCommand> getSubcommands() {
        return List.of(
                new AdoptAcceptSubcommand(),
                new AdoptDenySubcommand(),
                new AdoptKickoutSubcommand(),
                new AdoptMoveoutSubcommand(),
                new AdoptProposeSubcommand(),
                new AdoptSetSubcommand(),
                new AdoptUnsetSubcommand(),
                getHelpCommand()
        );
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
        } else {
            if (args.length == 0) {
                getHelpCommand().execute(sender, args);
            } else {
                final String subcommand = args[0];

                for (LunaticCommand sc : getSubcommands()) {
                    if (checkIsSubcommand(sc, subcommand)) {
                        String[] newArgs = new String[args.length - 1];
                        System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                        return sc.execute(sender, newArgs);
                    }
                }
                sender.sendMessage(getMessage(WRONG_USAGE_MK));
                Logger.debugLog("AdoptSubcommand: Wrong usage");
            }
        }
        return true;
    }

    @Override
    public boolean isPrimaryCommand() {
        return true;
    }

    @Override
    public String getFullCommand() {
        return new FamilySubcommand().getName() + " " + getName();
    }
}
