package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestMarry;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryAccept extends FamilyCommand implements HasParentCommand {

    private static final MarryAccept INSTANCE = new MarryAccept();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Accept a marriage proposal.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Eine Heiratsanfrage annehmen.");
    private static final CommandMessageKey OPEN_REQUEST_PARTNER_MK = new LunaticCommandMessageKey(INSTANCE, "open_request_partner")
            .defaultMessage("en", "You must wait for your future partner's response!")
            .defaultMessage("de", "Du musst auf die Antwort deines zukünftigen Partners warten!");
    private static final CommandMessageKey NO_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "no_request")
            .defaultMessage("en", "You have no marriage proposal.")
            .defaultMessage("de", "Du hast keine Heiratsanfrage.");
    private static final CommandMessageKey TOO_MANY_CHILDREN_MK = new LunaticCommandMessageKey(INSTANCE, "too_many_children")
            .defaultMessage("en", "%player1% and %player2% have more than 2 children together. %player1% and %player2% must remove %amount% children before they can marry.")
            .defaultMessage("de", "%player1% und %player2% haben zusammen mehr als 2 Kinder. %player1% und %player2% müssen %amount% Kinder entfernen, bevor sie heiraten können.");
    private static final CommandMessageKey COMPLETE_MK = new LunaticCommandMessageKey(INSTANCE, "complete")
            .defaultMessage("en", "You are married! You may now kiss!")
            .defaultMessage("de", "Ihr seid verheiratet! Ihr dürft euch jetzt küssen!");

    private final static PriestMarry PRIEST_MARRY_INSTANCE = new PriestMarry();

    private final static CommandMessageKey PRIEST_REQUEST_MK = new LunaticCommandMessageKey(PRIEST_MARRY_INSTANCE, "request")
            .defaultMessage("en", "%player1%, would you like to be siblings with %player2% on this Minecraft server?")
            .defaultMessage("de", "%player1%, möchtest du mit %player2% auf diesem Minecraft-Server Geschwister sein?");
    private final static CommandMessageKey PRIEST_NO_MK = new LunaticCommandMessageKey(PRIEST_MARRY_INSTANCE, "no")
            .defaultMessage("en", "No. I don't want to.")
            .defaultMessage("de", "Nein. Ich möchte nicht.");
    private final static CommandMessageKey PRIEST_YES_MK = new LunaticCommandMessageKey(PRIEST_MARRY_INSTANCE, "yes")
            .defaultMessage("en", "Yes. I do!")
            .defaultMessage("de", "Ja. Ich möchte!");
    private final static CommandMessageKey PRIEST_COMPLETE_MK = new LunaticCommandMessageKey(PRIEST_MARRY_INSTANCE, "complete")
            .defaultMessage("en", "You are siblings!")
            .defaultMessage("de", "Ihr seid Geschwister!");
    private final static CommandMessageKey PRIEST_REQUEST_EXPIRED_PRIEST_MK = new LunaticCommandMessageKey(PRIEST_MARRY_INSTANCE, "request_expired_priest")
            .defaultMessage("en", "The siblinghood between %player1% and %player2% has been canceled.")
            .defaultMessage("de", "Die Geschwisterbeziehung zwischen %player1% und %player2% wurde abgebrochen.");
    private final static CommandMessageKey PRIEST_REQUEST_EXPIRED_PLAYER_MK = new LunaticCommandMessageKey(PRIEST_MARRY_INSTANCE, "request_expired_player")
            .defaultMessage("en", "Your siblinghood with %player% has been canceled.")
            .defaultMessage("de", "Deine Geschwisterbeziehung mit %player% wurde abgebrochen.");


    @Override
    public String getPermission() {
        return "lunaticfamily.marry";
    }

    @Override
    public String getName() {
        return "accept";
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
