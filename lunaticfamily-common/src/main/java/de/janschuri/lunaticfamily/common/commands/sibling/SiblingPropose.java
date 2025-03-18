package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SiblingPropose extends FamilyCommand implements HasParentCommand, HasParams {

    private static final SiblingPropose INSTANCE = new SiblingPropose();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &b<%param%> &7- Propose siblinghood to a player.")
            .defaultMessage("de", "&6/%command% %subcommand% &b<%param%> &7- Schlage einem Spieler eine Geschwisterschaft vor.");
    private static final CommandMessageKey HAS_SIBLING_MK = new LunaticCommandMessageKey(INSTANCE, "has_sibling")
            .defaultMessage("en", "You already have a sibling.")
            .defaultMessage("de", "Du hast bereits ein Geschwister.");
    private static final CommandMessageKey IS_ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE, "is_adopted")
            .defaultMessage("en", "You are adopted. Your parents can adopt a sibling for you.")
            .defaultMessage("de", "Du bist adoptiert. Deine Eltern können ein Geschwister für dich adoptieren.");
    private static final CommandMessageKey PLAYER_IS_ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE, "player_is_adopted")
            .defaultMessage("en", "%player% is adopted. %player%'s parents can adopt a sibling for %player%.")
            .defaultMessage("de", "%player% ist adoptiert. %player%'s Eltern können ein Geschwister für %player% adoptieren.");
    private static final CommandMessageKey SELF_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "self_request")
            .defaultMessage("en", "You cannot be your own sibling.")
            .defaultMessage("de", "Du kannst kein Geschwister von dir selbst sein.");
    private static final CommandMessageKey FAMILY_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "family_request")
            .defaultMessage("en", "%player% is already part of your family.")
            .defaultMessage("de", "%player% ist bereits Teil deiner Familie.");
    private static final CommandMessageKey SIBLING_IS_ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE, "sibling_is_adopted")
            .defaultMessage("en", "%player% is already adopted. Their parents can adopt you to become your sibling.")
            .defaultMessage("de", "%player% ist bereits adoptiert. Ihre Eltern können dich adoptieren, um dein Geschwister zu werden.");
    private static final CommandMessageKey OPEN_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "open_request")
            .defaultMessage("en", "%player% already has an open sibling request.")
            .defaultMessage("de", "%player% hat bereits eine offene Geschwisterschaft Anfrage.");
    private static final CommandMessageKey REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "request")
            .defaultMessage("en", "%player% wants you to be their sibling. Are you okay with it?")
            .defaultMessage("de", "%player% möchte, dass du ihr Geschwister bist. Bist du damit einverstanden?");
    private static final CommandMessageKey REQUEST_SENT_MK = new LunaticCommandMessageKey(INSTANCE, "request_sent")
            .defaultMessage("en", "You have offered to be siblings with %player%.")
            .defaultMessage("de", "Du hast angeboten, Geschwister mit %player% zu sein.");
    private static final CommandMessageKey REQUEST_EXPIRED_MK = new LunaticCommandMessageKey(INSTANCE, "request_expired")
            .defaultMessage("en", "The sibling request from %player% has expired.")
            .defaultMessage("de", "Die Geschwisterschaft Anfrage von %player% ist abgelaufen.");
    private static final CommandMessageKey REQUEST_SENT_EXPIRED_MK = new LunaticCommandMessageKey(INSTANCE, "request_sent_expired")
            .defaultMessage("en", "The sibling request to %player% has expired.")
            .defaultMessage("de", "Die Geschwisterschaft Anfrage an %player% ist abgelaufen.");



    @Override
    public String getPermission() {
        return "lunaticfamily.sibling";
    }

    @Override
    public String getName() {
        return "propose";
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

    @Override
    public List<MessageKey> getParamsNames() {
        return List.of(
                PLAYER_NAME_MK
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam());
    }
}
