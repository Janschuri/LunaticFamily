package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.AdoptSubcommand;
import de.janschuri.lunaticfamily.common.commands.family.PriestSubcommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestAdoptSubcommand;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;

import java.util.List;
import java.util.Map;

public class AdoptPriestSubcommand extends Subcommand {


    @Override
    public String getPermission() {
        return "lunaticfamily.priest.adopt";
    }

    @Override
    public String getName() {
        return "priest";
    }

    @Override
    public AdoptSubcommand getParentCommand() {
        return new AdoptSubcommand();
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return new PriestAdoptSubcommand().getHelpMessages();
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        return new PriestAdoptSubcommand().execute(sender, args);
    }

    @Override
    public List<String> getAliases() {
        return new PriestSubcommand().getAliases();
    }


}
