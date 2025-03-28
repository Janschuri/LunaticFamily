package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.commands.priest.PriestSibling;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import de.janschuri.lunaticlib.common.logger.Logger;

import java.util.List;
import java.util.Map;

public class SiblingPriest extends PriestSibling {

    @Override
    public String getName() {
        return "priest";
    }


    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(new LunaticCommandMessageKey(new PriestSibling(), "help"), getPermission());
    }

    @Override
    public List<String> getAliases() {
        List<String> list = this.getLanguageConfig().getAliases(super.getParentCommand().getName());

        if (list.isEmpty()) {
            list.add(super.getParentCommand().getName());
        }

        return list;
    }
}
