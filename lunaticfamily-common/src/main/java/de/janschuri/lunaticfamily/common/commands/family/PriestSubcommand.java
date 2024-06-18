package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.marry.*;
import de.janschuri.lunaticfamily.common.commands.priest.PriestMarrySubcommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestSiblingSubcommand;
import de.janschuri.lunaticlib.LunaticCommand;
import de.janschuri.lunaticlib.common.command.LunaticHelpCommand;
import de.janschuri.lunaticlib.Sender;

import java.util.List;

public class PriestSubcommand extends Subcommand {

    @Override
    public List<LunaticCommand> getSubcommands() {
        return List.of(
                new PriestMarrySubcommand(),
                new PriestSiblingSubcommand(),
                getHelpCommand()
        );
    }

    @Override
    public LunaticHelpCommand getHelpCommand() {
        return new LunaticHelpCommand(getLanguageConfig(), this);
    }

    @Override
    public FamilySubcommand getParentCommand() {
        return new FamilySubcommand();
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