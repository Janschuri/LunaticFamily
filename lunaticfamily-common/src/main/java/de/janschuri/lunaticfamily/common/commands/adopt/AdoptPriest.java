package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.FamilyAdopt;
import de.janschuri.lunaticfamily.common.commands.family.FamilyPriest;
import de.janschuri.lunaticfamily.common.commands.priest.PriestAdopt;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;

import java.util.List;
import java.util.Map;

public class AdoptPriest extends Subcommand {


    @Override
    public String getPermission() {
        return "lunaticfamily.priest.adopt";
    }

    @Override
    public String getName() {
        return "priest";
    }

    @Override
    public FamilyAdopt getParentCommand() {
        return new FamilyAdopt();
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return new PriestAdopt().getHelpMessages();
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        return new PriestAdopt().execute(sender, args);
    }

    @Override
    public List<String> getAliases() {
        return new FamilyPriest().getAliases();
    }


}
