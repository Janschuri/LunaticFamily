package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
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

public class MarrySet extends FamilyCommand implements HasParentCommand, HasParams {

    private static final MarrySet INSTANCE = new MarrySet();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &b<%param%> <%param%> &7- Set the marriage between a couple.")
            .defaultMessage("de", "&6/%command% %subcommand% &b<%param%> <%param%> &7- Setze die Ehe zwischen einem Paar.");
    private static final CommandMessageKey SAME_PLAYER_MK = new LunaticCommandMessageKey(INSTANCE, "same_player")
            .defaultMessage("en", "A player cannot be their own partner.")
            .defaultMessage("de", "Ein Spieler kann nicht sein eigener Partner sein.");
    private static final CommandMessageKey ALREADY_MARRIED_MK = new LunaticCommandMessageKey(INSTANCE, "already_married")
            .defaultMessage("en", "%player% is already married.")
            .defaultMessage("de", "%player% ist bereits verheiratet.");
    private static final CommandMessageKey TOO_MANY_CHILDREN_MK = new LunaticCommandMessageKey(INSTANCE, "too_many_children")
            .defaultMessage("en", "%player1% and %player2% have more than 2 children together. %amount% children must be removed before %player1% and %player2% can marry.")
            .defaultMessage("de", "%player1% und %player2% haben zusammen mehr als 2 Kinder. %amount% Kinder müssen entfernt werden, bevor %player1% und %player2% heiraten können.");
    private static final CommandMessageKey DENIED_MK = new LunaticCommandMessageKey(INSTANCE, "denied")
            .defaultMessage("en", "You have cancelled the marriage setting.")
            .defaultMessage("de", "Du hast das Einstellen der Ehe abgebrochen.");
    private static final CommandMessageKey CONFIRM_MK = new LunaticCommandMessageKey(INSTANCE, "confirm")
            .defaultMessage("en", "Do you still want to set the marriage?")
            .defaultMessage("de", "Möchtest du die Ehe wirklich einstellen?");
    private static final CommandMessageKey MARRIED_MK = new LunaticCommandMessageKey(INSTANCE, "married")
            .defaultMessage("en", "You have married %player1% and %player2%.")
            .defaultMessage("de", "Du hast %player1% und %player2% verheiratet.");
    private static final CommandMessageKey SAME_FAMILY_MK = new LunaticCommandMessageKey(INSTANCE, "same_family")
            .defaultMessage("en", "%player1% and %player2% are already family.")
            .defaultMessage("de", "%player1% und %player2% sind bereits Familie.");



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
            sender.sendMessage(getMessage(DENIED_MK));
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
            sender.sendMessage(getMessage(SAME_PLAYER_MK));
            return true;
        }

        FamilyPlayer player2Fam = getFamilyPlayer(player2UUID);
        FamilyPlayer player1Fam = getFamilyPlayer(player1UUID);

        player1Fam.update();
        player2Fam.update();

        if (player1Fam.isFamilyMember(player2Fam)) {
            sender.sendMessage(getMessage(SAME_FAMILY_MK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", player2Fam.getName())
            ));
            return true;
        }

        if (player2Fam.isFamilyMember(player1Fam)) {
            sender.sendMessage(getMessage(SAME_FAMILY_MK,
                    placeholder("%player1%", player1Fam.getName()),
                    placeholder("%player2%", player2Fam.getName())
            ));
            return true;
        }

        if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
            int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
            sender.sendMessage(getMessage(TOO_MANY_CHILDREN_MK,
                    placeholder("%player1%", player1Fam.getName()),
                    placeholder("%player2%", player2Fam.getName()),
                    placeholder("%amount%", Integer.toString(amountDiff))
            ));
            return true;
        }

        if (player1Fam.isMarried()) {
            sender.sendMessage(getMessage(ALREADY_MARRIED_MK,
                    placeholder("%player%", player1Fam.getName())
            ));
            return true;
        }

        if (player2Fam.isMarried()) {
            sender.sendMessage(getMessage(ALREADY_MARRIED_MK,
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
        sender.sendMessage(getMessage(MARRIED_MK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", player2Fam.getName())
        ));
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
