package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SiblingUnset extends FamilyCommand implements HasParentCommand, HasParams {

    private static final SiblingUnset INSTANCE = new SiblingUnset();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &7- Unset the siblinghood between two players.")
            .defaultMessage("de", "&6/%command% %subcommand% &7- Entferne die Geschwisterschaft zwischen zwei Spielern.");
    private static final CommandMessageKey NO_SIBLING_MK = new LunaticCommandMessageKey(INSTANCE, "no_sibling")
            .defaultMessage("en", "%player% has no sibling.")
            .defaultMessage("de", "%player% hat keinen Bruder/keine Schwester.");
    private static final CommandMessageKey UNSET_MK = new LunaticCommandMessageKey(INSTANCE, "unset")
            .defaultMessage("en", "You have unset the sibling relationship between %player1% and %player2%.")
            .defaultMessage("de", "Du hast die Geschwisterbeziehung zwischen %player1% und %player2% entfernt.");


    @Override
    public String getPermission() {
        return "lunaticfamily.admin.sibling";
    }

    @Override
    public String getName() {
        return "unset";
    }

    @Override
    public Sibling getParentCommand() {
        return new Sibling();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        if (!sender.hasPermission("lunaticfamily.admin.sibling")) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            Logger.debugLog("SiblingUnset: Wrong usage");
            return true;
        }

        String player1Name = args[0];
        UUID player1UUID = Utils.getUUIDFromArg(player1Name);
        if (player1UUID == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", player1Name)));
            return true;
        }

        FamilyPlayer player1Fam = getFamilyPlayer(player1UUID);


        if (!player1Fam.hasSiblings()) {
            sender.sendMessage(getMessage(NO_SIBLING_MK,
                placeholder("%player%", player1Fam.getName())));
            return true;
        }


        FamilyPlayer siblingFam = player1Fam.getSibling();
        player1Fam.removeSiblings();
        sender.sendMessage(getMessage(UNSET_MK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", siblingFam.getName())));


        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getPermission()
        );
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(PLAYER_NAME_MK.noPrefix())
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam());
    }
}
