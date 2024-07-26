package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.adopt.Adopt;
import de.janschuri.lunaticfamily.common.commands.gender.Gender;
import de.janschuri.lunaticfamily.common.commands.marry.Marry;
import de.janschuri.lunaticfamily.common.commands.priest.Priest;
import de.janschuri.lunaticfamily.common.commands.sibling.Sibling;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.LunaticCommand;
import de.janschuri.lunaticlib.common.command.LunaticHelpCommand;
import de.janschuri.lunaticlib.Sender;

import java.util.List;

public class Family extends Subcommand {

    @Override
    public LunaticHelpCommand getHelpCommand() {
        return new LunaticHelpCommand(getLanguageConfig(), this);
    }

    @Override
    public List<LunaticCommand> getSubcommands() {
        return List.of(
                new FamilyList(),
                new FamilyBackground(),
                new FamilyReload(),
                new FamilyTree(),
                new FamilyDelete(),
                new FamilyCreate(),
                new Adopt(),
                new Gender(),
                new Sibling(),
                new Marry(),
                new Priest(),
                new FamilyDBList(),
                getHelpCommand()
        );
    }

    @Override
    public String getPermission() {
        return "lunaticfamily.family";
    }

    @Override
    public String getName() {
        return "family";
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
        Logger.debugLog("Family: Wrong usage");


        return true;
    }

    @Override
    public boolean isPrimaryCommand() {
        return true;
    }

}
