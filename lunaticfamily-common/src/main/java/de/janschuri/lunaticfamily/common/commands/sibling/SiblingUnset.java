package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SiblingUnset extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey noSiblingMK = new CommandMessageKey(this,"no_sibling");
    private final CommandMessageKey unsetMK = new CommandMessageKey(this,"unset");


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
            sender.sendMessage(getMessage(noSiblingMK,
                placeholder("%player%", player1Fam.getName())));
            return true;
        }


        FamilyPlayer siblingFam = player1Fam.getSibling();
        player1Fam.removeSiblings();
        sender.sendMessage(getMessage(unsetMK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", siblingFam.getName())));


        return true;
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
