package de.janschuri.lunaticfamily.common.commands.priest;

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

public class PriestAdopt extends FamilyCommand implements HasParentCommand, HasParams {

    private static final PriestAdopt INSTANCE = new PriestAdopt();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% <%param%> <%param%> &7- Arrange the adoption of a child by a player.")
            .defaultMessage("de", "&6/%command% %subcommand% <%param%> <%param%> &7- Arrangiere die Adoption eines Kindes durch einen Spieler.");
    private static final CommandMessageKey ALREADY_PRIEST_MK = new LunaticCommandMessageKey(INSTANCE, "already_priest")
            .defaultMessage("en", "You are already a priest in another action.")
            .defaultMessage("de", "Du bist bereits Priester in einer anderen Aktion.");
    private static final CommandMessageKey REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "request")
            .defaultMessage("en", "%player1%, would you like to adopt %player2% on this Minecraft server?")
            .defaultMessage("de", "%player1%, möchtest du %player2% auf diesem Minecraft-Server adoptieren?");
    private static final CommandMessageKey PLAYER_ALREADY_ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE, "player_already_adopted")
            .defaultMessage("en", "%player% is already adopted.")
            .defaultMessage("de", "%player% ist bereits adoptiert.");
    private static final CommandMessageKey TOO_MANY_CHILDREN_MK = new LunaticCommandMessageKey(INSTANCE, "too_many_children")
            .defaultMessage("en", "%player% already has two children.")
            .defaultMessage("de", "%player% hat bereits zwei Kinder.");
    private static final CommandMessageKey SELF_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "self_request")
            .defaultMessage("en", "You cannot adopt yourself.")
            .defaultMessage("de", "Du kannst dich nicht selbst adoptieren.");
    private static final CommandMessageKey OPEN_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "open_request")
            .defaultMessage("en", "%player% already has an open adoption request.")
            .defaultMessage("de", "%player% hat bereits eine offene Adoptionsanfrage.");
    private static final CommandMessageKey SAME_PLAYER_MK = new LunaticCommandMessageKey(INSTANCE, "same_player")
            .defaultMessage("en", "You cannot make someone their own child.")
            .defaultMessage("de", "Du kannst niemanden zu seinem eigenen Kind machen.");
    private static final CommandMessageKey REQUEST_EXPIRED_PRIEST_MK = new LunaticCommandMessageKey(INSTANCE, "request_expired_priest")
            .defaultMessage("en", "The adoption of %player2% by %player1% has been canceled.")
            .defaultMessage("de", "Die Adoption von %player2% durch %player1% wurde abgebrochen.");
    private static final CommandMessageKey REQUEST_EXPIRED_PLAYER_MK = new LunaticCommandMessageKey(INSTANCE, "request_expired_player")
            .defaultMessage("en", "Your adoption has been canceled.")
            .defaultMessage("de", "Deine Adoption wurde abgebrochen.");
    private static final CommandMessageKey FAMILY_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "family_request")
            .defaultMessage("en", "%player1% cannot adopt %player2%. These players already belong to the same family.")
            .defaultMessage("de", "%player1% kann %player2% nicht adoptieren. Diese Spieler gehören bereits zur selben Familie.");
    private static final CommandMessageKey YES_MK = new LunaticCommandMessageKey(INSTANCE, "yes")
            .defaultMessage("en", "Yes. I do!")
            .defaultMessage("de", "Ja. Ich will!");
    private static final CommandMessageKey NO_MK = new LunaticCommandMessageKey(INSTANCE, "no")
            .defaultMessage("en", "No. I don't want to.")
            .defaultMessage("de", "Nein. Ich will nicht.");



    @Override
    public String getPermission() {
        return "lunaticfamily.priest.adopt";
    }

    @Override
    public String getName() {
        return "adopt";
    }

    @Override
    public FamilyCommand getParentCommand() {
        return new Priest();
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
                PLAYER_NAME_MK,
                PLAYER_NAME_MK
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam(), getOnlinePlayersParam());
    }
}
