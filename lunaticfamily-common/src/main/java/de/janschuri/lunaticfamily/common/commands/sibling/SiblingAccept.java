package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestSibling;
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

public class SiblingAccept extends FamilyCommand implements HasParentCommand {

    private static final SiblingAccept INSTANCE = new SiblingAccept();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Accept a sibling request.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Akzeptiere eine Geschwister Anfrage.");
    private static final CommandMessageKey NO_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "no_request")
            .defaultMessage("en", "You have no sibling request.")
            .defaultMessage("de", "Du hast keine Geschwister Anfrage.");
    private static final CommandMessageKey COMPLETE_MK = new LunaticCommandMessageKey(INSTANCE, "complete")
            .defaultMessage("en", "You and %player% are now siblings!")
            .defaultMessage("de", "Du und %player% seid jetzt Geschwister!");
    private static final CommandMessageKey IS_ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE, "is_adopted")
            .defaultMessage("en", "You are adopted. Your parents can adopt a sibling for you.")
            .defaultMessage("de", "Du bist adoptiert. Deine Eltern können ein Geschwister für dich adoptieren.");


    private static final PriestSibling PRIEST_SIBLING_INSTANCE = new PriestSibling();

    private static final CommandMessageKey PRIEST_REQUEST_MK = new LunaticCommandMessageKey(PRIEST_SIBLING_INSTANCE,"request")
            .defaultMessage("en", "%player1%, would you like to be siblings with %player2% on this Minecraft server?")
            .defaultMessage("de", "%player1%, möchtest du Geschwister mit %player2% auf diesem Minecraft Server sein?");
    private static final CommandMessageKey PRIEST_YES_MK = new LunaticCommandMessageKey(PRIEST_SIBLING_INSTANCE,"yes")
            .defaultMessage("en", "Yes. I do!")
            .defaultMessage("de", "Ja. Ich will!");
    private static final CommandMessageKey PRIEST_NO_MK = new LunaticCommandMessageKey(PRIEST_SIBLING_INSTANCE,"no")
            .defaultMessage("en", "No. I don't want to.")
            .defaultMessage("de", "Nein. Ich will nicht.");
    private static final CommandMessageKey PRIEST_COMPLETE_MK = new LunaticCommandMessageKey(PRIEST_SIBLING_INSTANCE,"complete")
            .defaultMessage("en", "You are siblings!")
            .defaultMessage("de", "Ihr seid Geschwister!");
    private static final CommandMessageKey PRIEST_REQUEST_EXPIRED_PRIEST_MK = new LunaticCommandMessageKey(PRIEST_SIBLING_INSTANCE,"request_expired_priest")
            .defaultMessage("en", "The siblinghood between %player1% and %player2% has been canceled.")
            .defaultMessage("de", "Die Geschwisterschaft zwischen %player1% und %player2% wurde abgebrochen.");
    private static final CommandMessageKey PRIEST_REQUEST_EXPIRED_PLAYER_MK = new LunaticCommandMessageKey(PRIEST_SIBLING_INSTANCE,"request_expired_player")
            .defaultMessage("en", "Your siblinghood with %player% has been canceled.")
            .defaultMessage("de", "Deine Geschwisterschaft mit %player% wurde abgebrochen.");
    private static final CommandMessageKey PRIEST_IS_ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE, "is_adopted")
            .defaultMessage("en", "You are adopted. Your parents can adopt a sibling for you.")
            .defaultMessage("de", "Du bist adoptiert. Deine Eltern können ein Geschwister für dich adoptieren.");


    @Override
    public String getPermission() {
        return "lunaticfamily.sibling";
    }

    @Override
    public String getName() {
        return "accept";
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
