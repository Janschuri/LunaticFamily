package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.family.Family;
import de.janschuri.lunaticlib.Command;
import de.janschuri.lunaticlib.common.command.HasHelpCommand;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.command.HasSubcommands;
import de.janschuri.lunaticlib.common.command.LunaticHelpCommand;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;

import java.util.List;

public class Marry extends FamilyCommand implements HasSubcommands, HasHelpCommand, HasParentCommand {

    @Override
    public List<Command> getSubcommands() {
        return List.of(
                new MarryAccept(),
                new MarryDeny(),
                new MarryDivorce(),
                new MarryGift(),
                new MarryEmoji(),
                new MarryKiss(),
                new MarryList(),
                new MarryPropose(),
                new MarrySet(),
                new MarryUnset(),
                new MarryPriest(),
                getHelpCommand()
        );
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
    public Family getParentCommand() {
        return new Family();
    }

    @Override
    public boolean isPrimaryCommand() {
        return true;
    }

    @Override
    public String getPermission() {
        return "lunaticfamily.marry";
    }

    @Override
    public String getName() {
        return "marry";
    }

    @Override
    public String getFullCommand() {
        return new Family().getName() + " " + getName();
    }
}
