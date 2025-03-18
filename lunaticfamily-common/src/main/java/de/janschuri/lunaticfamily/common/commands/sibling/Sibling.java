package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.family.Family;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.Command;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.common.command.HasHelpCommand;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.command.LunaticHelpCommand;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;

public class Sibling extends FamilyCommand implements HasHelpCommand, HasParentCommand {

    private static final Sibling INSTANCE = new Sibling();
    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- List all sibling commands.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Liste alle Geschwister Befehle auf.");
    private static final CommandMessageKey HELP_HEADER_MK = new LunaticCommandMessageKey(INSTANCE, "help_header")
            .defaultMessage("en", "Sibling commands:")
            .defaultMessage("de", "Geschwister Befehle:");

    @Override
    public List<Command> getSubcommands() {
        return List.of(
                new SiblingAccept(),
                new SiblingDeny(),
                new SiblingUnsibling(),
                new SiblingPropose(),
                new SiblingSet(),
                new SiblingUnset(),
                new SiblingPriest(),
                new SiblingList(),
                new SiblingEmoji(),
                getHelpCommand()
        );
    }

    @Override
    public String getPermission() {
        return "lunaticfamily.sibling";
    }

    @Override
    public String getName() {
        return "sibling";
    }

    @Override
    public Family getParentCommand() {
        return new Family();
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
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        if (args.length == 0) {
            getHelpCommand().execute(sender, args);
            return true;
        }

        final String subcommand = args[0];

        for (Command sc : getSubcommands()) {
            if (checkIsSubcommand(sc, subcommand)) {
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, args.length - 1);
                return sc.execute(sender, newArgs);
            }
        }
        sender.sendMessage(getMessage(WRONG_USAGE_MK));

        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getName()
        );
    }

    @Override
    public boolean isPrimaryCommand() {
        return true;
    }

    @Override
    public String getFullCommand() {
        return new Family().getName() + " " + getName();
    }
}
