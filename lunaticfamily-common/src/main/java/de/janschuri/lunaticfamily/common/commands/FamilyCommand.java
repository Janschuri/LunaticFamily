package de.janschuri.lunaticfamily.common.commands;


import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.config.LanguageConfigImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.command.LunaticCommand;
import de.janschuri.lunaticlib.common.config.HasMessageKeys;
import de.janschuri.lunaticlib.common.config.LunaticMessageKey;
import net.kyori.adventure.text.Component;

public abstract class FamilyCommand extends LunaticCommand implements HasMessageKeys {

    protected static final MessageKey NO_NUMBER_MK = new LunaticMessageKey("no_number")
            .defaultMessage("en", "%input% is not a full positive number.")
            .defaultMessage("de", "%input% ist keine positive Ganzzahl.");
    protected static final MessageKey WRONG_USAGE_MK = new LunaticMessageKey("wrong_usage")
            .defaultMessage("en", "Wrong usage of command.")
            .defaultMessage("de", "Falsche Verwendung des Befehls.");
    protected static final MessageKey PLAYER_NOT_EXIST_MK = new LunaticMessageKey("player_not_exist")
            .defaultMessage("en", "%player% does not exist.")
            .defaultMessage("de", "%player% existiert nicht.");
    protected static final MessageKey NOT_ENOUGH_MONEY_MK = new LunaticMessageKey("not_enough_money")
            .defaultMessage("en", "You do not have enough money to do this.")
            .defaultMessage("de", "Du hast nicht genug Geld, um das zu tun.");
    protected static final MessageKey PLAYER_NOT_ENOUGH_MONEY_MK = new LunaticMessageKey("player_not_enough_money")
            .defaultMessage("en", "%player% does not have enough money to do this.")
            .defaultMessage("de", "%player% hat nicht genug Geld, um das zu tun.");
    protected static final MessageKey NO_CONSOLE_COMMAND_MK = new LunaticMessageKey("no_console_command")
            .defaultMessage("en", "This command can only be executed by a player.")
            .defaultMessage("de", "Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
    protected static final MessageKey PLAYER_TOO_FAR_AWAY_MK = new LunaticMessageKey("player_too_far_away")
            .defaultMessage("en", "%player% is too far away.")
            .defaultMessage("de", "%player% ist zu weit entfernt.");
    protected static final MessageKey PLAYER_OFFLINE_MK = new LunaticMessageKey("player_offline")
            .defaultMessage("en", "%player% is offline.")
            .defaultMessage("de", "%player% ist offline.");
    protected static final MessageKey PLAYER_NOT_SAME_SERVER_MK = new LunaticMessageKey("player_not_same_server")
            .defaultMessage("en", "%player% is not on the same server.")
            .defaultMessage("de", "%player% ist nicht auf dem gleichen Server.");
    protected static final MessageKey NO_PERMISSION_MK = new LunaticMessageKey("no_permission")
            .defaultMessage("en", "You do not have permission to do this.")
            .defaultMessage("de", "Du hast keine Berechtigung, das zu tun.");
    protected static final MessageKey TAKE_PAYMENT_CONFIRM_MK = new LunaticMessageKey("take_payment_confirm")
            .defaultMessage("en", "Do you want cover the entire cost?")
            .defaultMessage("de", "Möchtest du die gesamten Kosten übernehmen?");
    protected static final MessageKey PLAYER_NOT_ON_WHITELISTED_SERVER_MK = new LunaticMessageKey("player_not_on_whitelisted_server")
            .defaultMessage("en", "%player% is not on a server where LunaticFamily is enabled.")
            .defaultMessage("de", "%player% ist nicht auf einem Server, auf dem LunaticFamily aktiviert ist.");
    protected static final MessageKey ACCEPT_MK = new LunaticMessageKey("accept")
            .defaultMessage("en", "Accept")
            .defaultMessage("de", "Akzeptieren");
    protected static final MessageKey DENY_MK = new LunaticMessageKey("deny")
            .defaultMessage("en", "Deny")
            .defaultMessage("de", "Ablehnen");
    protected static final MessageKey CONFIRM_MK = new LunaticMessageKey("confirm")
            .defaultMessage("en", "Confirm")
            .defaultMessage("de", "Bestätigen");
    protected static final MessageKey CANCEL_MK = new LunaticMessageKey("cancel")
            .defaultMessage("en", "Cancel")
            .defaultMessage("de", "Abbrechen");
    protected static final MessageKey PLAYER_NAME_MK = new LunaticMessageKey("player_name")
            .defaultMessage("en", "Player")
            .defaultMessage("de", "Spielername");
    protected static final MessageKey NO_UUID_MK = new LunaticMessageKey("no_uuid")
            .defaultMessage("en", "%input% is not a valid UUID.")
            .defaultMessage("de", "%input% ist keine gültige UUID.");
    protected static final MessageKey COLOR_MK = new LunaticMessageKey("color")
            .defaultMessage("en", "Color")
            .defaultMessage("de", "Farbe");
    protected static final MessageKey PAGE_MK = new LunaticMessageKey("page")
            .defaultMessage("en", "Page")
            .defaultMessage("de", "Seite");
    protected static final MessageKey BACKGROUND_MK = new LunaticMessageKey("background")
            .defaultMessage("en", "Background")
            .defaultMessage("de", "Hintergrund");
    protected static final MessageKey UUID_MK = new LunaticMessageKey("uuid")
            .defaultMessage("en", "UUID")
            .defaultMessage("de", "UUID");


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

    @Override
    public Component wrongUsageMessage(Sender sender, String[] strings) {
        return getMessage(WRONG_USAGE_MK);
    }

    @Override
    public Component noPermissionMessage(Sender sender, String[] strings) {
        return getMessage(NO_PERMISSION_MK);
    }

    @Override
    public boolean checkPermission(Sender commandSender, String[] args, boolean silent) {
        return Utils.isPlayerOnRegisteredServer(commandSender) && super.checkPermission(commandSender, args, silent);
    }
}
