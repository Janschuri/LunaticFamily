package de.janschuri.lunaticfamily.common.commands.marry;

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

public class MarryUnset extends FamilyCommand implements HasParams, HasParentCommand {

    private final CommandMessageKey helpMK = new LunaticCommandMessageKey(this,"help");
    private final CommandMessageKey noPartnerMK = new LunaticCommandMessageKey(this,"no_partner");
    private final CommandMessageKey divorcedMK = new LunaticCommandMessageKey(this,"divorced");


    @Override
    public String getPermission() {
        return "lunaticfamily.admin.marry";
    }

    @Override
    public String getName() {
        return "unset";
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
            Logger.debugLog("MarryUnset: Wrong usage");
            return true;
        }

        String player1Arg = args[0];
        UUID player1UUID = Utils.getUUIDFromArg(player1Arg);
        if (player1UUID == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", player1Arg)));
            return true;
        }

        FamilyPlayer player1Fam = getFamilyPlayer(player1UUID);

        if (!player1Fam.isMarried()) {
            sender.sendMessage(getMessage(noPartnerMK,
                placeholder("%player%", player1Fam.getName())));
            return true;
        }
        FamilyPlayer partnerFam = player1Fam.getPartner();
        player1Fam.divorce();
        sender.sendMessage(getMessage(divorcedMK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", partnerFam.getName())));
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
