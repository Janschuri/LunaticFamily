package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticlib.Command;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;

import java.util.Map;

public class FamilyMigrate extends FamilyCommand implements HasParentCommand {

    private static final FamilyMigrate INSTANCE = new FamilyMigrate();
    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", INSTANCE.getDefaultHelpMessage("Migrate the database."))
            .defaultMessage("de", INSTANCE.getDefaultHelpMessage("Migriere die Datenbank."));
    private static final CommandMessageKey NOTHING_TO_MIGRATE_MK = new LunaticCommandMessageKey(INSTANCE, "nothingToMigrate")
            .defaultMessage("en", "There is nothing to migrate. The corresponding database tables were not found.")
            .defaultMessage("de", "Es gibt nichts zu migrieren. Die entsprechenden Datenbanktabellen wurden nicht gefunden.");
    private static final CommandMessageKey MIGRATED_MK = new LunaticCommandMessageKey(INSTANCE, "migrated")
            .defaultMessage("en", "The database has been migrated.")
            .defaultMessage("de", "Die Datenbank wurde migriert.");
    private static final CommandMessageKey FAILED_MK = new LunaticCommandMessageKey(INSTANCE, "failed")
            .defaultMessage("en", "The database migration failed.")
            .defaultMessage("de", "Die Datenbankmigration ist fehlgeschlagen.");

    @Override
    public String getName() {
        return "migrate";
    }

    @Override
    public String getPermission() {
        return "lunaticfamily.admin.migrate";
    }

    @Override
    public boolean execute(Sender sender, String[] strings) {
        if (!DatabaseRepository.canMigrate()) {
            sender.sendMessage(getMessage(NOTHING_TO_MIGRATE_MK));
            return true;
        }

        if (DatabaseRepository.migrateMarriageMaster()) {
            sender.sendMessage(getMessage(MIGRATED_MK));
        } else {
            sender.sendMessage(getMessage(FAILED_MK));
        }
        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }

    @Override
    public Family getParentCommand() {
        return new Family();
    }
}
