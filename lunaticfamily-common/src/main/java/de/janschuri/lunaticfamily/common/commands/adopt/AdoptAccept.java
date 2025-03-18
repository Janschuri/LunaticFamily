package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestAdopt;
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

public class AdoptAccept extends FamilyCommand implements HasParentCommand {

    private static final AdoptAccept INSTANCE = new AdoptAccept();
    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE,"help")
            .defaultMessage("en", "&6/%command% %subcommand% &7 - Accept an adoption request.")
            .defaultMessage("de", "&6/%command% %subcommand% &7 - Akzeptiere eine Adoptionsanfrage.");
    private static final CommandMessageKey GOT_ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE,"got_adopted")
            .defaultMessage("en", "You got adopted by %player1% and %player2%.")
            .defaultMessage("de", "Du wurdest von %player1% und %player2% adoptiert.");
    private static final CommandMessageKey ADOPTED_BY_SINGLE_MK = new LunaticCommandMessageKey(INSTANCE,"adopted_by_single")
            .defaultMessage("en", "You got adopted by %player%.")
            .defaultMessage("de", "Du wurdest von %player% adoptiert.");
    private static final CommandMessageKey ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE,"adopted")
            .defaultMessage("en", "You adopted %player%.")
            .defaultMessage("de", "Du hast %player% adoptiert.");
    private static final CommandMessageKey PARENT_LIMIT_MK = new LunaticCommandMessageKey(INSTANCE,"parent_limit")
            .defaultMessage("en", "%player% cannot adopt another child. %player% has already reached the limit of two children.")
            .defaultMessage("de", "%player% kann kein weiteres Kind adoptieren. %player% hat bereits das Limit von zwei Kindern erreicht.");
    private static final CommandMessageKey NO_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE,"no_request")
            .defaultMessage("en", "You don't have any open adoption requests.")
            .defaultMessage("de", "Du hast keine offenen Adoptionsanfragen.");


    private static final PriestAdopt PRIEST_ADOPT_INSTANCE = new PriestAdopt();
    private static final CommandMessageKey PRIEST_YES_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"yes")
            .defaultMessage("en", "Yes, I do.")
            .defaultMessage("de", "Ja, ich will.");
    private static final CommandMessageKey PRIEST_NO_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"no")
            .defaultMessage("en", "No, I don't want to.")
            .defaultMessage("de", "Nein, ich will nicht.");
    private static final CommandMessageKey PRIEST_COMPLETE_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"complete")
            .defaultMessage("en", "The adoption of %child% by %parent% is completed.")
            .defaultMessage("de", "Die Adoption von %child% durch %parent% ist abgeschlossen.");
    private static final CommandMessageKey PRIEST_ALREADY_ADOPTED_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"already_adopted")
            .defaultMessage("en", "%player% is already adopted.")
            .defaultMessage("de", "%player% ist bereits adoptiert.");
    private static final CommandMessageKey PRIEST_REQUEST_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"request")
            .defaultMessage("en", "%player1%, would you like to adopt %player2% on this Minecraft server?")
            .defaultMessage("de", "%player1%, möchtest du %player2% auf diesem Minecraft-Server adoptieren?");
    private static final CommandMessageKey PRIEST_REQUEST_EXPIRED_PRIEST_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"request_expired_priest")
            .defaultMessage("en", "The adoption request of %parent% for %child% has expired.")
            .defaultMessage("de", "Die Adoptionsanfrage von %parent% für %child% ist abgelaufen.");
    private static final CommandMessageKey PRIEST_REQUEST_EXPIRED_PARENT_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"request_expired_parent")
            .defaultMessage("en", "The adoption request for %child% has expired.")
            .defaultMessage("de", "Die Adoptionsanfrage für %child% ist abgelaufen.");
    private static final CommandMessageKey PRIEST_REQUEST_EXPIRED_CHILD_MK = new LunaticCommandMessageKey(PRIEST_ADOPT_INSTANCE,"request_expired_child")
            .defaultMessage("en", "The adoption request of %parent% for you has expired.")
            .defaultMessage("de", "Die Adoptionsanfrage von %parent% für dich ist abgelaufen.");


    @Override
    public String getPermission() {
        return "lunaticfamily.adopt";
    }

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public Adopt getParentCommand() {
        return new Adopt();
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