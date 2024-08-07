package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.priest.Priest;
import de.janschuri.lunaticfamily.common.commands.priest.PriestAdopt;
import de.janschuri.lunaticfamily.common.commands.priest.PriestSibling;
import de.janschuri.lunaticfamily.common.commands.sibling.Sibling;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.logger.Logger;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;

public class AdoptPriest extends PriestAdopt {

    @Override
    public String getName() {
        return "priest";
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(new CommandMessageKey(new PriestAdopt(), "help"), getPermission());
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
