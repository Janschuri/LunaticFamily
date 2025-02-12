package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.adopt.Adopt;
import de.janschuri.lunaticfamily.common.commands.gender.Gender;
import de.janschuri.lunaticfamily.common.commands.marry.Marry;
import de.janschuri.lunaticfamily.common.commands.priest.Priest;
import de.janschuri.lunaticfamily.common.commands.sibling.Sibling;
import de.janschuri.lunaticlib.Command;
import de.janschuri.lunaticlib.common.command.HasHelpCommand;
import de.janschuri.lunaticlib.common.command.HasSubcommands;
import de.janschuri.lunaticlib.common.command.LunaticHelpCommand;
import net.kyori.adventure.text.Component;

import java.util.List;

public class Family extends FamilyCommand implements HasHelpCommand, HasSubcommands {

    @Override
    public LunaticHelpCommand getHelpCommand() {
        return new LunaticHelpCommand(getLanguageConfig(), this);
    }

    @Override
    public Component pageParamName() {
        return getMessage(PAGE_MK);
    }

    @Override
    public List<Command> getSubcommands() {
        return List.of(
                new FamilyList(),
                new FamilyBackground(),
                new FamilyReload(),
                new FamilyTree(),
                new FamilyDelete(),
                new FamilyCreate(),
                new Adopt(),
                new Gender(),
                new Sibling(),
                new Marry(),
                new Priest(),
                new FamilyDBList(),
                getHelpCommand()
        );
    }

    @Override
    public String getPermission() {
        return "lunaticfamily.family";
    }

    @Override
    public String getName() {
        return "family";
    }

}
