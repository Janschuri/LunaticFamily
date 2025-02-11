package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
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

public class MarrySet extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey samePlayerMK = new CommandMessageKey(this,"same_player");
    private final CommandMessageKey alreadyMarriedMK = new CommandMessageKey(this,"already_married");
    private final CommandMessageKey tooManyChildrenMK = new CommandMessageKey(this,"too_many_children");
    private final CommandMessageKey deniedMK = new CommandMessageKey(this,"denied");
    private final CommandMessageKey marriedMK = new CommandMessageKey(this,"married");
    private final CommandMessageKey sameFamilyMK = new CommandMessageKey(this,"same_family");


    @Override
    public String getPermission() {
        return "lunaticfamily.admin.marry";
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public Marry getParentCommand() {
        return new Marry();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            Logger.debugLog("MarrySet: Wrong usage");
            return true;
        }

        if (args[0].equalsIgnoreCase("deny")) {
            sender.sendMessage(getMessage(deniedMK));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            Logger.debugLog("MarrySetSubcommand: Wrong usage");
            return true;
        }

        String player1Arg = args[0];
        UUID player1UUID = Utils.getUUIDFromArg(player1Arg);
        if (player1UUID == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK.noPrefix(),
                    placeholder("%player%", player1Arg)
            ));
            return true;
        }

        String player2Arg = args[1];
        UUID player2UUID = Utils.getUUIDFromArg(player2Arg);
        if (player2UUID == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                    placeholder("%player%", player2Arg)
            ));
            return true;
        }


        if (player1UUID.equals(player2UUID)) {
            sender.sendMessage(getMessage(samePlayerMK));
            return true;
        }

        FamilyPlayer player2Fam = getFamilyPlayer(player2UUID);
        FamilyPlayer player1Fam = getFamilyPlayer(player1UUID);

        player1Fam.update();
        player2Fam.update();

        if (player1Fam.isFamilyMember(player2Fam)) {
            sender.sendMessage(getMessage(sameFamilyMK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", player2Fam.getName())
            ));
            return true;
        }

        if (player2Fam.isFamilyMember(player1Fam)) {
            sender.sendMessage(getMessage(sameFamilyMK,
                    placeholder("%player1%", player1Fam.getName()),
                    placeholder("%player2%", player2Fam.getName())
            ));
            return true;
        }

        if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
            int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
            sender.sendMessage(getMessage(tooManyChildrenMK,
                    placeholder("%player1%", player1Fam.getName()),
                    placeholder("%player2%", player2Fam.getName()),
                    placeholder("%amount%", Integer.toString(amountDiff))
            ));
            return true;
        }

        if (player1Fam.isMarried()) {
            sender.sendMessage(getMessage(alreadyMarriedMK,
                    placeholder("%player%", player1Fam.getName())
            ));
            return true;
        }

        if (player2Fam.isMarried()) {
            sender.sendMessage(getMessage(alreadyMarriedMK,
                    placeholder("%player%", player2Fam.getName())
            ));
            return true;
        }


        LunaticFamily.marryRequests.remove(player1UUID);
        LunaticFamily.marryPriestRequests.remove(player1UUID);
        LunaticFamily.marryPriests.remove(player1UUID);

        LunaticFamily.marryRequests.remove(player1UUID);
        LunaticFamily.marryPriestRequests.remove(player1UUID);
        LunaticFamily.marryPriests.remove(player1UUID);

        player1Fam.marry(player2Fam);
        sender.sendMessage(getMessage(marriedMK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", player2Fam.getName())
        ));
        return true;
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(PLAYER_NAME_MK.noPrefix()),
                getMessage(PLAYER_NAME_MK.noPrefix())
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam(), getOnlinePlayersParam());
    }
}
