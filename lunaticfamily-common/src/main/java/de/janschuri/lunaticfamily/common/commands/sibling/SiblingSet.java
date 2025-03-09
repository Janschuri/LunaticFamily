package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SiblingSet extends FamilyCommand implements HasParentCommand, HasParams {

    private static final SiblingSet INSTANCE = new SiblingSet();

    private static final CommandMessageKey SAME_PLAYER_MK = new LunaticCommandMessageKey(INSTANCE, "same_player")
            .defaultMessage("en", "You cannot be your own sibling.")
            .defaultMessage("de", "Du kannst nicht dein eigenes Geschwisterkind sein.");
    private static final CommandMessageKey SAME_FAMILY_MK = new LunaticCommandMessageKey(INSTANCE, "same_family")
            .defaultMessage("en", "%player1% and %player2% are already family.")
            .defaultMessage("de", "%player1% und %player2% sind bereits Familie.");
    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &b<%param%> &7- Set the siblinghood between two players.")
            .defaultMessage("de", "&6/%command% %subcommand% &b<%param%> &7- Setze die Geschwisterschaft zwischen zwei Spielern.");
    private static final CommandMessageKey ADDED_MK = new LunaticCommandMessageKey(INSTANCE, "added")
            .defaultMessage("en", "The siblings %player1% and %player2% have been successfully added!")
            .defaultMessage("de", "Die Geschwister %player1% und %player2% wurden erfolgreich hinzugefügt!");
    private static final CommandMessageKey IS_ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE, "is_adopted")
            .defaultMessage("en", "%player% is adopted. Set the adoption by the parents to make %player% a sibling.")
            .defaultMessage("de", "%player% ist adoptiert. Setze die Adoption durch die Eltern, um %player% zu einem Geschwisterking zu machen.");
    private static final CommandMessageKey SET_BOTH_ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE, "set_both_adopted")
            .defaultMessage("en", "%player1% and %player2% are adopted. Set the adoption by the parents or marry the parents to make %player1% and %player2% siblings.")
            .defaultMessage("de", "%player1% und %player2% sind adoptiert. Setze die Adoption durch die Eltern oder verheirate die Eltern, um %player1% und %player2% zu Geschwistern zu machen.");




    @Override
    public String getPermission() {
        return "lunaticfamily.admin.sibling";
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public Sibling getParentCommand() {
        return new Sibling();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage((getMessage(NO_PERMISSION_MK)));
            return true;
        }

        if (!sender.hasPermission("lunaticfamily.admin.sibling")) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        } else if (args.length < 2) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            return true;
        }

        String player1Arg = args[0];
        UUID player1UUID = Utils.getUUIDFromArg(player1Arg);
        if (player1UUID == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", player1Arg)));
            return true;
        }

        String player2Arg = args[1];
        UUID player2UUID = Utils.getUUIDFromArg(player2Arg);
        if (player2UUID == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", player2Arg)));
            return true;
        }


        FamilyPlayer player1Fam = getFamilyPlayer(player1UUID);
        FamilyPlayer player2Fam = getFamilyPlayer(player2UUID);

        player1Fam.update();
        player2Fam.update();

        if (player1Fam.isFamilyMember(player2Fam)) {
            sender.sendMessage(getMessage(SAME_FAMILY_MK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", player2Fam.getName())));
            return true;
        }

        if (player2Fam.isFamilyMember(player1Fam)) {
            sender.sendMessage(getMessage(SAME_FAMILY_MK,
                placeholder("%player1%", player2Fam.getName()),
                placeholder("%player2%", player1Fam.getName())));
            return true;
        }

        if (args[0].equalsIgnoreCase(args[1])) {
            sender.sendMessage(getMessage(SAME_PLAYER_MK));
            return true;
        }


        if (player1Fam.isAdopted() && player2Fam.isAdopted()) {
            sender.sendMessage(getMessage(SET_BOTH_ADOPTED_MK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", player2Fam.getName())));
            return true;
        }

        if (player1Fam.isAdopted()) {
            sender.sendMessage(getMessage(IS_ADOPTED_MK,
                placeholder("%player%", player1Fam.getName())));
            return true;
        }

        if (player2Fam.isAdopted()) {
            sender.sendMessage(getMessage(IS_ADOPTED_MK,
                placeholder("%player%", player2Fam.getName())));
            return true;
        }

        sender.sendMessage(getMessage(ADDED_MK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", player2Fam.getName())));
        player1Fam.addSibling(player2Fam);

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
