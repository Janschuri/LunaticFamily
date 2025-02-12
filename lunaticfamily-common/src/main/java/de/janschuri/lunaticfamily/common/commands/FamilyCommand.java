package de.janschuri.lunaticfamily.common.commands;


import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.config.LanguageConfigImpl;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.LunaticCommand;
import de.janschuri.lunaticlib.common.command.LunaticMessageKey;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public abstract class FamilyCommand extends LunaticCommand {

    protected static final MessageKey NO_NUMBER_MK = new LunaticMessageKey("no_number");
    protected static final MessageKey WRONG_USAGE_MK = new LunaticMessageKey("wrong_usage");
    protected static final MessageKey PLAYER_NOT_EXIST_MK = new LunaticMessageKey("player_not_exist");
    protected static final MessageKey NOT_ENOUGH_MONEY_MK = new LunaticMessageKey("not_enough_money");
    protected static final MessageKey PLAYER_NOT_ENOUGH_MONEY_MK = new LunaticMessageKey("player_not_enough_money");
    protected static final MessageKey NO_CONSOLE_COMMAND_MK = new LunaticMessageKey("no_console_command");
    protected static final MessageKey PLAYER_TOO_FAR_AWAY_MK = new LunaticMessageKey("player_too_far_away");
    protected static final MessageKey PLAYER_OFFLINE_MK = new LunaticMessageKey("player_offline");
    protected static final MessageKey PLAYER_NOT_SAME_SERVER_MK = new LunaticMessageKey("player_not_same_server");
    protected static final MessageKey NO_PERMISSION_MK = new LunaticMessageKey("no_permission");
    protected static final MessageKey TAKE_PAYMENT_CONFIRM_MK = new LunaticMessageKey("take_payment_confirm");
    protected static final MessageKey PLAYER_NOT_ON_WHITELISTED_SERVER_MK = new LunaticMessageKey("player_not_on_whitelisted_server");
    protected static final MessageKey ACCEPT_MK = new LunaticMessageKey("accept");
    protected static final MessageKey DENY_MK = new LunaticMessageKey("deny");
    protected static final MessageKey CONFIRM_MK = new LunaticMessageKey("confirm");
    protected static final MessageKey CANCEL_MK = new LunaticMessageKey("cancel");
    protected static final MessageKey PLAYER_NAME_MK = new LunaticMessageKey("player_name");
    protected static final MessageKey NO_UUID_MK = new LunaticMessageKey("no_uuid");
    protected static final MessageKey COLOR_MK = new LunaticMessageKey("color");
    protected static final MessageKey PAGE_MK = new LunaticMessageKey("page");


    protected static String getRelation(String mainCommand, String subcommand) {
        return LunaticFamily.getLanguageConfig().getRelation(mainCommand, subcommand);
    }

    protected static String getGenderLang(String gender) {
        return LunaticFamily.getLanguageConfig().getGenderLang(gender);
    }

    @Override
    public LanguageConfigImpl getLanguageConfig() {
        return LunaticFamily.getLanguageConfig();
    }

    protected static FamilyPlayer getFamilyPlayer(UUID playerUUID) {
        return FamilyPlayer.findOrCreate(playerUUID);
    }

    protected static FamilyPlayer getFamilyPlayer(int playerID) {
        return FamilyPlayer.find(playerID);
    }

    @Override
    public Component wrongUsageMessage(Sender sender, String[] strings) {
        return getMessage(WRONG_USAGE_MK);
    }

    @Override
    public Component noPermissionMessage(Sender sender, String[] strings) {
        return getMessage(NO_PERMISSION_MK);
    }
}
