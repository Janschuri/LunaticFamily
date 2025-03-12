package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;

import java.util.Map;

public class FamilyReload extends FamilyCommand implements HasParentCommand {

    private static final FamilyReload INSTANCE = new FamilyReload();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", INSTANCE.getDefaultHelpMessage("Reload the configuration."))
            .defaultMessage("de", INSTANCE.getDefaultHelpMessage("Lade die Konfiguration neu."));
    private static final CommandMessageKey RELOADED_MK = new LunaticCommandMessageKey(INSTANCE, "reloaded")
            .defaultMessage("en", "The configuration has been reloaded.")
            .defaultMessage("de", "Die Konfiguration wurde neu geladen.");


    @Override
    public String getPermission() {
        return "lunaticfamily.admin.reload";
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public Family getParentCommand() {
        return new Family();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }


        LunaticFamily.loadConfig();
        sender.sendMessage(getMessage(RELOADED_MK));
        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }
}
