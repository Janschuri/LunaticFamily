package de.janschuri.lunaticfamily.common.commands.marry;

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

public class MarryPropose extends FamilyCommand implements HasParentCommand, HasParams {

    private static final MarryPropose INSTANCE = new MarryPropose();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &b<%param%> &7- Propose marriage to a player.")
            .defaultMessage("de", "&6/%command% %subcommand% &b<%param%> &7- Mache einem Spieler einen Heiratsantrag.");
    private static final CommandMessageKey ALREADY_MARRIED_MK = new LunaticCommandMessageKey(INSTANCE, "already_married")
            .defaultMessage("en", "You are already married to %player%.")
            .defaultMessage("de", "Du bist bereits mit %player% verheiratet.");
    private static final CommandMessageKey PLAYER_ALREADY_MARRIED_MK = new LunaticCommandMessageKey(INSTANCE, "player_already_married")
            .defaultMessage("en", "%player% is already married.")
            .defaultMessage("de", "%player% ist bereits verheiratet.");
    private static final CommandMessageKey REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "request")
            .defaultMessage("en", "%player1%, would you like to marry %player2% on this Minecraft server?")
            .defaultMessage("de", "%player1%, möchtest du %player2% auf diesem Minecraft-Server heiraten?");
    private static final CommandMessageKey REQUEST_SENT_MK = new LunaticCommandMessageKey(INSTANCE, "request_sent")
            .defaultMessage("en", "You have proposed marriage to %player%.")
            .defaultMessage("de", "Du hast %player% einen Heiratsantrag gemacht.");
    private static final CommandMessageKey REQUEST_EXPIRED_MK = new LunaticCommandMessageKey(INSTANCE, "request_expired")
            .defaultMessage("en", "The marriage proposal from %player% has expired.")
            .defaultMessage("de", "Der Heiratsantrag von %player% ist abgelaufen.");
    private static final CommandMessageKey REQUEST_SENT_EXPIRED_MK = new LunaticCommandMessageKey(INSTANCE, "request_sent_expired")
            .defaultMessage("en", "Your marriage proposal to %player% has expired.")
            .defaultMessage("de", "Dein Heiratsantrag an %player% ist abgelaufen.");
    private static final CommandMessageKey OPEN_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "open_request")
            .defaultMessage("en", "%player% already has an open proposal.")
            .defaultMessage("de", "%player% hat bereits einen offenen Antrag.");
    private static final CommandMessageKey FAMILY_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "family_request")
            .defaultMessage("en", "You cannot marry %player%. This player already belongs to your family.")
            .defaultMessage("de", "Du kannst %player% nicht heiraten. Dieser Spieler gehört bereits zu deiner Familie.");
    private static final CommandMessageKey TOO_MANY_CHILDREN_MK = new LunaticCommandMessageKey(INSTANCE, "too_many_children")
            .defaultMessage("en", "You and %player% have more than two children together. You must remove %amount% children before you and %player% can marry.")
            .defaultMessage("de", "Du und %player% habt zusammen mehr als zwei Kinder. Du musst %amount% Kinder entfernen, bevor du und %player% heiraten können.");
    private static final CommandMessageKey SELF_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "self_request")
            .defaultMessage("en", "You cannot marry yourself!")
            .defaultMessage("de", "Du kannst dich nicht selbst heiraten!");
    private static final CommandMessageKey MARRY_NO_MK = new LunaticCommandMessageKey(INSTANCE, "marry_no")
            .defaultMessage("en", "No, I don't want to.")
            .defaultMessage("de", "Nein, ich möchte nicht.");
    private static final CommandMessageKey MARRY_YES_MK = new LunaticCommandMessageKey(INSTANCE, "marry_yes")
            .defaultMessage("en", "Yes, I do.")
            .defaultMessage("de", "Ja, ich will.");



    @Override
    public String getPermission() {
        return "lunaticfamily.marry";
    }

    @Override
    public String getName() {
        return "propose";
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
