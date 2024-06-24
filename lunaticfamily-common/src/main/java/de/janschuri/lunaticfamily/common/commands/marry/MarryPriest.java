package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.FamilyMarry;
import de.janschuri.lunaticfamily.common.commands.family.FamilyPriest;
import de.janschuri.lunaticfamily.common.commands.priest.PriestMarry;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;

import java.util.List;
import java.util.Map;

public class MarryPriest extends Subcommand {


    @Override
    public String getPermission() {
        return "lunaticfamily.priest.marry";
    }

    @Override
    public String getName() {
        return "priest";
    }

    @Override
    public FamilyMarry getParentCommand() {
        return new FamilyMarry();
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return new PriestMarry().getHelpMessages();
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        return new PriestMarry().execute(sender, args);
    }

    @Override
    public List<String> getAliases() {
        return new FamilyPriest().getAliases();
    }


}
