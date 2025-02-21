package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.family.Family;
import de.janschuri.lunaticlib.Command;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.common.command.*;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;

public class Adopt extends FamilyCommand implements HasHelpCommand, HasSubcommands, HasParentCommand {

    private static final Adopt INSTANCE = new Adopt();
    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Show the adopt help page.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Zeige die Adopt Hilfe Seite.");

    @Override
    public String getPermission() {
        return "lunaticfamily.adopt";
    }

    @Override
    public LunaticHelpCommand getHelpCommand() {
        return new LunaticHelpCommand(this);
    }

    @Override
    public MessageKey pageParamName() {
        return PAGE_MK;
    }

    @Override
    public MessageKey getHelpHeader() {
        return null;
    }

    @Override
    public String getName() {
        return "adopt";
    }

    @Override
    public Family getParentCommand() {
        return new Family();
    }

    @Override
    public boolean isPrimaryCommand() {
        return true;
    }

    @Override
    public List<Command> getSubcommands() {
        return List.of(
                new AdoptAccept(),
                new AdoptDeny(),
                new AdoptKickout(),
                new AdoptMoveout(),
                new AdoptPropose(),
                new AdoptSet(),
                new AdoptUnset(),
                new AdoptPriest(),
                new AdoptEmoji(),
                new AdoptList(),
                getHelpCommand()
        );
    }

    @Override
    public String getFullCommand() {
        return new Family().getName() + " " + getName();
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }
}
