package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.commands.priest.PriestMarry;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.common.logger.Logger;

import java.util.List;
import java.util.Map;

public class MarryPriest extends PriestMarry {

    @Override
    public String getName() {
        return "priest";
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(new CommandMessageKey(new PriestMarry(), "help"), getPermission());
    }

    @Override
    public List<String> getAliases() {
        List<String> list = this.getLanguageConfig().getAliases(super.getParentCommand().getName());

        if (list.isEmpty()) {
            list.add(super.getParentCommand().getName());
        }

        Logger.debugLog("Aliases: " + list.toString());
        return list;
    }
}
