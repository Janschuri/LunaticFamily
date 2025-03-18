package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.family.Family;
import de.janschuri.lunaticlib.Command;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.common.command.HasHelpCommand;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.command.HasSubcommands;
import de.janschuri.lunaticlib.common.command.LunaticHelpCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;

public class Marry extends FamilyCommand implements HasSubcommands, HasHelpCommand, HasParentCommand {

    private static final Marry INSTANCE = new Marry();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Show the marry help page.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Zeige die Marry-Hilfe.");
    private static final CommandMessageKey HELP_HEADER_MK = new LunaticCommandMessageKey(INSTANCE, "help_header")
            .defaultMessage("en", "Marry-Help")
            .defaultMessage("de", "Marry-Hilfe");

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
    public MessageKey pageParamName() {
        return PAGE_MK;
    }

    @Override
    public MessageKey getHelpHeader() {
        return HELP_HEADER_MK;
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

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }
}
