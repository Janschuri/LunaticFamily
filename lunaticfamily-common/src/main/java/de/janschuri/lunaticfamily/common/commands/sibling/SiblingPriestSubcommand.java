package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.SiblingSubcommand;
import de.janschuri.lunaticfamily.common.commands.family.PriestSubcommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestSiblingSubcommand;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;

import java.util.List;
import java.util.Map;

public class SiblingPriestSubcommand extends Subcommand {


    @Override
    public String getPermission() {
        return "lunaticfamily.priest.sibling";
    }

    @Override
    public String getName() {
        return "priest";
    }

    @Override
    public SiblingSubcommand getParentCommand() {
        return new SiblingSubcommand();
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return new PriestSiblingSubcommand().getHelpMessages();
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        return new PriestSiblingSubcommand().execute(sender, args);
    }

    @Override
    public List<String> getAliases() {
        return new PriestSubcommand().getAliases();
    }


}
