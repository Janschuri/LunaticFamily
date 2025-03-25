package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdoptUnset extends FamilyCommand implements HasParentCommand, HasParams {

    private static final AdoptUnset INSTANCE = new AdoptUnset();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", INSTANCE.getDefaultHelpMessage("Unset the adoption of a child by a player."))
            .defaultMessage("de", INSTANCE.getDefaultHelpMessage("Setze die Adoption eines Kindes durch einen Spieler zurück."));
    private static final CommandMessageKey NOT_ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE, "not_adopted")
            .defaultMessage("en", "%player% is not adopted.")
            .defaultMessage("de", "%player% ist nicht adoptiert.");
    private static final CommandMessageKey UNSET_MK = new LunaticCommandMessageKey(INSTANCE, "unset")
            .defaultMessage("en", "You have dissolved the adoption of %child% by %parent1% and %parent2%.")
            .defaultMessage("de", "Du hast die Adoption von %child% durch %parent1% und %parent2% aufgelöst.");
    private static final CommandMessageKey UNSET_BY_SINGLE_MK = new LunaticCommandMessageKey(INSTANCE, "unset_by_single")
            .defaultMessage("en", "You have dissolved the adoption of %child% by %parent%.")
            .defaultMessage("de", "Du hast die Adoption von %child% durch %parent% aufgelöst.");



    @Override
    public String getPermission() {
        return "lunaticfamily.admin.adopt";
    }

    @Override
    public String getName() {
        return "unset";
    }

    @Override
    public Adopt getParentCommand() {
        return new Adopt();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            return true;
        }


        String childArg = args[0];
        FamilyPlayer childFam;

        if (Utils.isUUID(childArg)) {
            UUID player1UUID = UUID.fromString(childArg);
            childFam = FamilyPlayer.find(player1UUID);
        } else {
            childFam = FamilyPlayer.find(childArg);
        }

        if (childFam == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", childArg)));
            return true;
        }

        if (!childFam.isAdopted()) {
            sender.sendMessage(getMessage(NOT_ADOPTED_MK,
                placeholder("%player%", childFam.getName())));
            return true;
        }
        FamilyPlayer firstParentFam = childFam.getParents().get(0);

        if (firstParentFam.isMarried()) {
            FamilyPlayer secondParentFam = firstParentFam.getPartner();
            sender.sendMessage(getMessage(UNSET_MK,
                placeholder("%child%", childFam.getName()),
                placeholder("%parent1%", firstParentFam.getName()),
                placeholder("%parent2%", secondParentFam.getName())));
        } else {
            sender.sendMessage(getMessage(UNSET_BY_SINGLE_MK,
                placeholder("%child%", childFam.getName()),
                placeholder("%parent%", firstParentFam.getName())));
        }

        firstParentFam.unadopt(childFam);



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
