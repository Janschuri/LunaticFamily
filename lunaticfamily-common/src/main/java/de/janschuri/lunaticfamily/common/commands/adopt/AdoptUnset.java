package de.janschuri.lunaticfamily.common.commands.adopt;

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

public class AdoptUnset extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey notAdoptedMK = new CommandMessageKey(this,"not_adopted");
    private final CommandMessageKey unsetMK = new CommandMessageKey(this,"unset");
    private final CommandMessageKey unsetBySingleMK = new CommandMessageKey(this,"unset_by_single");


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
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                    .replaceText(getTextReplacementConfig("%player%", player1Arg)));
            return true;
        }


        FamilyPlayer childFam = getFamilyPlayer(childUUID);

        if (!childFam.isAdopted()) {
            sender.sendMessage(getMessage(notAdoptedMK)
                    .replaceText(getTextReplacementConfig("%player%", childFam.getName())));
            return true;
        }
        FamilyPlayer firstParentFam = childFam.getParents().get(0);

        if (firstParentFam.isMarried()) {
            FamilyPlayer secondParentFam = firstParentFam.getPartner();
            sender.sendMessage(getMessage(unsetMK)
                    .replaceText(getTextReplacementConfig("%child%", childFam.getName()))
                    .replaceText(getTextReplacementConfig("%parent1%", firstParentFam.getName()))
                    .replaceText(getTextReplacementConfig("%parent2%", secondParentFam.getName())));
        } else {
            sender.sendMessage(getMessage(unsetBySingleMK)
                    .replaceText(getTextReplacementConfig("%child%", childFam.getName()))
                    .replaceText(getTextReplacementConfig("%parent%", firstParentFam.getName())));
        }

        firstParentFam.unadopt(childFam);



        return true;
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(PLAYER_NAME_MK, false)
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam());
    }
}
