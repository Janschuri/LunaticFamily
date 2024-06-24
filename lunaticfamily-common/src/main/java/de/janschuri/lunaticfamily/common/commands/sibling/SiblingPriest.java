package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.priest.Priest;
import de.janschuri.lunaticfamily.common.commands.priest.PriestSibling;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;

import java.util.List;
import java.util.Map;

public class SiblingPriest extends Subcommand {


    @Override
    public String getPermission() {
        return "lunaticfamily.priest.sibling";
    }

    @Override
    public String getName() {
        return "priest";
    }

    @Override
    public Sibling getParentCommand() {
        return new Sibling();
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return new PriestSibling().getHelpMessages();
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        return new PriestSibling().execute(sender, args);
    }

    @Override
    public List<String> getAliases() {
        return new Priest().getAliases();
    }


}
