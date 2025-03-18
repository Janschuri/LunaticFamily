package de.janschuri.lunaticfamily.common.commands.marry;

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

public class MarryUnset extends FamilyCommand implements HasParams, HasParentCommand {

    private static final MarryUnset INSTANCE = new MarryUnset();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &b<%param%> &7- Unset the marriage between a couple.")
            .defaultMessage("de", "&6/%command% %subcommand% &b<%param%> &7- Hebe die Ehe zwischen einem Paar auf.");
    private static final CommandMessageKey NO_PARTNER_MK = new LunaticCommandMessageKey(INSTANCE, "no_partner")
            .defaultMessage("en", "%player% is not married.")
            .defaultMessage("de", "%player% ist nicht verheiratet");
    private static final CommandMessageKey DIVORCED_MK = new LunaticCommandMessageKey(INSTANCE, "divorced")
            .defaultMessage("en", "You have dissolved the marriage between %player1% and %player2%.")
            .defaultMessage("de", "Du hast die Ehe von %player1% und %player2% geschieden.");


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
            sender.sendMessage(getMessage(NO_PARTNER_MK,
                placeholder("%player%", player1Fam.getName())));
            return true;
        }
        FamilyPlayer partnerFam = player1Fam.getPartner();
        player1Fam.divorce();
        sender.sendMessage(getMessage(DIVORCED_MK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", partnerFam.getName())));
        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                HELP_MK, getName()
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
