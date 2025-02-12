package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.commands.priest.PriestAdopt;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.common.command.LunaticCommandMessageKey;
import de.janschuri.lunaticlib.common.logger.Logger;

import java.util.List;
import java.util.Map;

public class AdoptPriest extends PriestAdopt {

    @Override
    public String getName() {
        return "priest";
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(new LunaticCommandMessageKey(new PriestAdopt(), "help"), getPermission());
    }

    @Override
    public List<String> getAliases() {
        List<String> list = this.getLanguageConfig().getAliases(super.getParentCommand().getName());

        if (list.isEmpty()) {
            list.add(super.getParentCommand().getName());
        }

        Logger.debugLog("Aliases: " + list);
        return list;
    }
}
