package de.janschuri.lunaticfamily.common.commands;


import de.janschuri.lunaticfamily.LanguageConfig;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.common.command.AbstractLunaticCommand;

public abstract class Subcommand extends AbstractLunaticCommand {

    protected static final MessageKey NO_NUMBER_MK = new MessageKey("no_number");
    protected static final MessageKey WRONG_USAGE_MK = new MessageKey("wrong_usage");
    protected static final MessageKey PLAYER_NOT_EXIST_MK = new MessageKey("player_not_exist");
    protected static final MessageKey NOT_ENOUGH_MONEY_MK = new MessageKey("not_enough_money");
    protected static final MessageKey PLAYER_NOT_ENOUGH_MONEY_MK = new MessageKey("player_not_enough_money");
    protected static final MessageKey NO_CONSOLE_COMMAND_MK = new MessageKey("no_console_command");
    protected static final MessageKey PLAYER_TOO_FAR_AWAY_MK = new MessageKey("player_too_far_away");
    protected static final MessageKey PLAYER_OFFLINE_MK = new MessageKey("player_offline");
    protected static final MessageKey PLAYER_NOT_SAME_SERVER_MK = new MessageKey("player_not_same_server");
    protected static final MessageKey NO_PERMISSION_MK = new MessageKey("no_permission");
    protected static final MessageKey TAKE_PAYMENT_CONFIRM_MK = new MessageKey("take_payment_confirm");
    protected static final MessageKey PLAYER_NOT_ON_WHITELISTED_SERVER_MK = new MessageKey("player_not_on_whitelisted_server");
    protected static final MessageKey ACCEPT_MK = new MessageKey("accept");
    protected static final MessageKey DENY_MK = new MessageKey("deny");
    protected static final MessageKey CONFIRM_MK = new MessageKey("confirm");
    protected static final MessageKey CANCEL_MK = new MessageKey("cancel");
    protected static final MessageKey PLAYER_NAME_MK = new MessageKey("player_name");
    protected static final MessageKey NO_UUID_MK = new MessageKey("no_uuid");
    protected static final MessageKey COLOR_MK = new MessageKey("color");


    protected static String getRelation(String mainCommand, String subcommand) {
        return LunaticFamily.getLanguageConfig().getRelation(mainCommand, subcommand);
    }

    protected static String getGenderLang(String gender) {
        return LunaticFamily.getLanguageConfig().getGenderLang(gender);
    }

    @Override
    public LanguageConfig getLanguageConfig() {
        return LunaticFamily.getLanguageConfig();
    }
}
