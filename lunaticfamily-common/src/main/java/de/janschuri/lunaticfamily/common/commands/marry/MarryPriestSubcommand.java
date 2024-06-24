package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.MarrySubcommand;
import de.janschuri.lunaticfamily.common.commands.family.PriestSubcommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestMarrySubcommand;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.LunaticHelpCommand;

import java.util.List;
import java.util.Map;

public class MarryPriestSubcommand extends Subcommand {


    @Override
    public String getPermission() {
        return "lunaticfamily.priest.marry";
    }

    @Override
    public String getName() {
        return "priest";
    }

    @Override
    public MarrySubcommand getParentCommand() {
        return new MarrySubcommand();
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return new PriestMarrySubcommand().getHelpMessages();
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        return new PriestMarrySubcommand().execute(sender, args);
    }

    @Override
    public List<String> getAliases() {
        return new PriestSubcommand().getAliases();
    }


}
