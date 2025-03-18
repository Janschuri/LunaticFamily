package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestSibling;
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

public class SiblingDeny extends FamilyCommand implements HasParentCommand {

    private static final SiblingDeny INSTANCE = new SiblingDeny();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Deny a sibling request.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Lehne eine Geschwisterschaft ab.");
    private static final CommandMessageKey NO_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "no_request")
            .defaultMessage("en", "You have no sibling request.")
            .defaultMessage("de", "Du hast keine Geschwisterschaft Anfrage.");
    private static final CommandMessageKey DENIED_MK = new LunaticCommandMessageKey(INSTANCE, "denied")
            .defaultMessage("en", "Sorry, %player% does not want to be your sibling.")
            .defaultMessage("de", "Tut mir leid, %player% will kein Geschwister von dir sein.");
    private static final CommandMessageKey DENY_MK = new LunaticCommandMessageKey(INSTANCE, "deny")
            .defaultMessage("en", "You have denied the sibling request from %player%.")
            .defaultMessage("de", "Du hast die Geschwisterschaft Anfrage von %player% abgelehnt.");


    private static final PriestSibling PRIEST_SIBLING_INSTANCE = new PriestSibling();

    private static final CommandMessageKey PRIEST_NO_MK = new LunaticCommandMessageKey(PRIEST_SIBLING_INSTANCE, "no")
            .defaultMessage("en", "No. I don't want to.")
            .defaultMessage("de", "Nein. Ich will nicht.");
    private static final CommandMessageKey PRIEST_CANCEL_MK = new LunaticCommandMessageKey(PRIEST_SIBLING_INSTANCE, "cancel")
            .defaultMessage("en", "The siblinghood has been canceled.")
            .defaultMessage("de", "Die Geschwisterschaft wurde abgebrochen.");


    @Override
    public String getPermission() {
        return "lunaticfamily.sibling";
    }

    @Override
    public String getName() {
        return "deny";
    }

    @Override
    public Sibling getParentCommand() {
        return new Sibling();
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
