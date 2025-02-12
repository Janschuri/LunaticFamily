package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.command.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdoptUnset extends FamilyCommand implements HasParentCommand, HasParams {

    private final CommandMessageKey helpMK = new LunaticCommandMessageKey(this,"help");
    private final CommandMessageKey notAdoptedMK = new LunaticCommandMessageKey(this,"not_adopted");
    private final CommandMessageKey unsetMK = new LunaticCommandMessageKey(this,"unset");
    private final CommandMessageKey unsetBySingleMK = new LunaticCommandMessageKey(this,"unset_by_single");


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
            Logger.debugLog("AdoptUnsetSubcommand: Wrong usage");
            return true;
        }


        String player1Arg = args[0];
        UUID childUUID = Utils.getUUIDFromArg(player1Arg);
        if (childUUID == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", player1Arg)));
            return true;
        }


        FamilyPlayer childFam = getFamilyPlayer(childUUID);

        if (!childFam.isAdopted()) {
            sender.sendMessage(getMessage(notAdoptedMK,
                placeholder("%player%", childFam.getName())));
            return true;
        }
        FamilyPlayer firstParentFam = childFam.getParents().get(0);

        if (firstParentFam.isMarried()) {
            FamilyPlayer secondParentFam = firstParentFam.getPartner();
            sender.sendMessage(getMessage(unsetMK,
                placeholder("%child%", childFam.getName()),
                placeholder("%parent1%", firstParentFam.getName()),
                placeholder("%parent2%", secondParentFam.getName())));
        } else {
            sender.sendMessage(getMessage(unsetBySingleMK,
                placeholder("%child%", childFam.getName()),
                placeholder("%parent%", firstParentFam.getName())));
        }

        firstParentFam.unadopt(childFam);



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
