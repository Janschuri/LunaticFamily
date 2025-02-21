package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.family.Family;
import de.janschuri.lunaticlib.Command;
import de.janschuri.lunaticlib.common.command.*;
import net.kyori.adventure.text.Component;

import java.util.List;

public class Adopt extends FamilyCommand implements HasHelpCommand, HasSubcommands, HasParentCommand {

    @Override
    public String getPermission() {
        return "lunaticfamily.adopt";
    }

    @Override
    public LunaticHelpCommand getHelpCommand() {
        return new LunaticHelpCommand(this);
    }

    @Override
    public Component pageParamName() {
        return getMessage(PAGE_MK);
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
}
