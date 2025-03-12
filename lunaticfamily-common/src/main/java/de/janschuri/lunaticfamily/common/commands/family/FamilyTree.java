package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;

import java.util.Map;
import java.util.UUID;

public class FamilyTree extends FamilyCommand implements HasParentCommand {

    private static final FamilyTree INSTANCE = new FamilyTree();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", INSTANCE.getDefaultHelpMessage("Update your family tree."))
            .defaultMessage("de", INSTANCE.getDefaultHelpMessage("Aktualisiere deinen Familienstammbaum."));
    private static final CommandMessageKey RELOADED_MK = new LunaticCommandMessageKey(INSTANCE, "reloaded")
            .defaultMessage("en", "Your family tree has been reloaded.")
            .defaultMessage("de", "Dein Familienstammbaum wurde neu geladen.");
    private static final CommandMessageKey FAILED_MK = new LunaticCommandMessageKey(INSTANCE, "failed")
            .defaultMessage("en", "Failed to reload your family tree.")
            .defaultMessage("de", "Fehler beim Neuladen deines Familienstammbaums.");
    private static final CommandMessageKey DISABLED_MK = new LunaticCommandMessageKey(INSTANCE, "disabled")
            .defaultMessage("en", "The family tree is disabled.")
            .defaultMessage("de", "Der Familienstammbaum ist deaktiviert.");


    @Override
    public String getPermission() {
        return "lunaticfamily.family.tree";
    }

    @Override
    public String getName() {
        return "tree";
    }

    @Override
    public Family getParentCommand() {
        return new Family();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender player)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        if (!LunaticFamily.getConfig().isUseCrazyAdvancementAPI()){
            sender.sendMessage(getMessage(DISABLED_MK));
            return true;
        }

        UUID playerUUID = player.getUniqueId();
            String name = player.getName();
            FamilyPlayer playerFam = getFamilyPlayer(playerUUID);
            playerFam.save();

            if (playerFam.updateFamilyTree()) {
                player.sendMessage(getMessage(RELOADED_MK));
            } else {
                sender.sendMessage(getMessage(FAILED_MK));
                Logger.errorLog("Failed to reload family tree for player " + name + " (" + playerUUID + ").");
                Logger.errorLog("Is the correct version of CrazyAdvancementsAPI installed?");
                return false;
            }

        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }
}
