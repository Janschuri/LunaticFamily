package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestMarry;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryDeny extends FamilyCommand implements HasParentCommand {

    private static final MarryDeny INSTANCE = new MarryDeny();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Deny a marriage proposal.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Eine Heiratsanfrage ablehnen.");
    private static final CommandMessageKey NO_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "no_request")
            .defaultMessage("en", "You have no marriage proposal.")
            .defaultMessage("de", "Du hast keine Heiratsanfrage.");
    private static final CommandMessageKey DENIED_MK = new LunaticCommandMessageKey(INSTANCE, "denied")
            .defaultMessage("en", "Sorry, %player% does not want to marry you.")
            .defaultMessage("de", "Sorry, %player% möchte dich nicht heiraten.");
    private static final CommandMessageKey DENY_MK = new LunaticCommandMessageKey(INSTANCE, "deny")
            .defaultMessage("en", "You have denied the marriage proposal from %player%.")
            .defaultMessage("de", "Du hast die Heiratsanfrage von %player% abgelehnt.");


    private static final PriestMarry PRIEST_MARRY_INSTANCE = new PriestMarry();

    private static final CommandMessageKey priestCancelMK = new LunaticCommandMessageKey(PRIEST_MARRY_INSTANCE,"cancel")
            .defaultMessage("en", "The wedding has been canceled.")
            .defaultMessage("de", "Die Heirat wurde abgebrochen.");
    private static final CommandMessageKey priestNoMK = new LunaticCommandMessageKey(PRIEST_MARRY_INSTANCE,"no")
            .defaultMessage("en", "No. I don't want to.")
            .defaultMessage("de", "Nein. Ich möchte nicht.");

    @Override
    public String getPermission() {
        return "lunaticfamily.marry";
    }

    @Override
    public String getName() {
        return "deny";
    }

    @Override
    public Marry getParentCommand() {
        return new Marry();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }
}
