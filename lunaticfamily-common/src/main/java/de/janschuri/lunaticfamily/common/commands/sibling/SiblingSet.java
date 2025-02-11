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

public class SiblingSet extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey addedMK = new CommandMessageKey(this,"added");
    private final CommandMessageKey isAdoptedMK = new CommandMessageKey(this,"is_adopted");
    private final CommandMessageKey setBothAdoptedMK = new CommandMessageKey(this,"set_both_adopted");
    private final CommandMessageKey sameFamilyMK = new CommandMessageKey(this,"same_family");
    private final CommandMessageKey samePlayerMK = new CommandMessageKey(this,"same_player");



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
        } else if (args.length < 1) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            Logger.debugLog("SiblingSetSubcommand: Wrong usage");
            return true;
        }

        String player1Arg = args[0];
        UUID player1UUID = Utils.getUUIDFromArg(player1Arg);
        if (player1UUID == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                    .replaceText(getTextReplacementConfig("%player%", player1Arg)));
            return true;
        }

        String player2Arg = args[1];
        UUID player2UUID = Utils.getUUIDFromArg(player2Arg);
        if (player2UUID == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                    .replaceText(getTextReplacementConfig("%player%", player2Arg)));
            return true;
        }


        FamilyPlayer player1Fam = getFamilyPlayer(player1UUID);
        FamilyPlayer player2Fam = getFamilyPlayer(player2UUID);

        player1Fam.update();
        player2Fam.update();

        if (player1Fam.isFamilyMember(player2Fam)) {
            sender.sendMessage(getMessage(sameFamilyMK)
                    .replaceText(getTextReplacementConfig("%player1%", player1Fam.getName()))
                    .replaceText(getTextReplacementConfig("%player2%", player2Fam.getName())));
            return true;
        }

        if (player2Fam.isFamilyMember(player1Fam)) {
            sender.sendMessage(getMessage(sameFamilyMK)
                    .replaceText(getTextReplacementConfig("%player1%", player2Fam.getName()))
                    .replaceText(getTextReplacementConfig("%player2%", player1Fam.getName())));
            return true;
        }

        if (args[0].equalsIgnoreCase(args[1])) {
            sender.sendMessage(getMessage(samePlayerMK));
            return true;
        }


        if (player1Fam.isAdopted() && player2Fam.isAdopted()) {
            sender.sendMessage(getMessage(setBothAdoptedMK)
                    .replaceText(getTextReplacementConfig("%player1%", player1Fam.getName()))
                    .replaceText(getTextReplacementConfig("%player2%", player2Fam.getName())));
            return true;
        }

        if (player1Fam.isAdopted()) {
            sender.sendMessage(getMessage(isAdoptedMK)
                    .replaceText(getTextReplacementConfig("%player%", player1Fam.getName())));
            return true;
        }

        if (player2Fam.isAdopted()) {
            sender.sendMessage(getMessage(isAdoptedMK)
                    .replaceText(getTextReplacementConfig("%player%", player2Fam.getName())));
            return true;
        }

        sender.sendMessage(getMessage(addedMK)
                .replaceText(getTextReplacementConfig("%player1%", player1Fam.getName()))
                .replaceText(getTextReplacementConfig("%player2%", player2Fam.getName())));
        player1Fam.addSibling(player2Fam);

        return true;
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(PLAYER_NAME_MK, false),
                getMessage(PLAYER_NAME_MK, false)
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam(), getOnlinePlayersParam());
    }
}
