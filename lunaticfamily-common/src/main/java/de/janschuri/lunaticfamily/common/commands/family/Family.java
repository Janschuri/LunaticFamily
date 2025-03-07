package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.adopt.Adopt;
import de.janschuri.lunaticfamily.common.commands.gender.Gender;
import de.janschuri.lunaticfamily.common.commands.marry.Marry;
import de.janschuri.lunaticfamily.common.commands.priest.Priest;
import de.janschuri.lunaticfamily.common.commands.sibling.Sibling;
import de.janschuri.lunaticlib.Command;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.common.command.HasHelpCommand;
import de.janschuri.lunaticlib.common.command.HasSubcommands;
import de.janschuri.lunaticlib.common.command.LunaticHelpCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;

import java.util.List;
import java.util.Map;

public class Family extends FamilyCommand implements HasHelpCommand, HasSubcommands {

    private static final Family INSTANCE = new Family();
    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Show the family help page.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Zeige die Familien Hilfe Seite.");
    private static final CommandMessageKey HELP_HEADER_MK = new LunaticCommandMessageKey(INSTANCE, "help_header")
            .defaultMessage("en", "Family-Help")
            .defaultMessage("de", "Familien-Hilfe");

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
        return HELP_HEADER_MK;
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
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }

    @Override
    public boolean isPrimaryCommand() {
        return true;
    }

    @Override
    public String getName() {
        return "family";
    }

}
