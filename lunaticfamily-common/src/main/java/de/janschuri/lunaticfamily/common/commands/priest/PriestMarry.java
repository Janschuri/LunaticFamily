package de.janschuri.lunaticfamily.common.commands.priest;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.MessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.command.HasParams;
import de.janschuri.lunaticlib.common.command.HasParentCommand;
import de.janschuri.lunaticlib.common.config.LunaticCommandMessageKey;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PriestMarry extends FamilyCommand implements HasParentCommand, HasParams {

    private static final PriestMarry INSTANCE = new PriestMarry();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% <%param%> <%param%> &7- Marry two players.")
            .defaultMessage("de", "&6/%command% %subcommand% <%param%> <%param%> &7- Verheirate zwei Spieler.");
    private static final CommandMessageKey ALREADY_PRIEST_MK = new LunaticCommandMessageKey(INSTANCE, "already_priest")
            .defaultMessage("en", "You are already a priest in another action.")
            .defaultMessage("de", "Du bist bereits Priester in einer anderen Aktion.");
    private static final CommandMessageKey REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "request")
            .defaultMessage("en", "%player1%, would you like to marry %player2% on this Minecraft server?")
            .defaultMessage("de", "%player1%, möchtest du %player2% auf diesem Minecraft-Server heiraten?");
    private static final CommandMessageKey PLAYER_ALREADY_MARRIED_MK = new LunaticCommandMessageKey(INSTANCE, "player_already_married")
            .defaultMessage("en", "%player% is already married.")
            .defaultMessage("de", "%player% ist bereits verheiratet.");
    private static final CommandMessageKey TOO_MANY_CHILDREN_MK = new LunaticCommandMessageKey(INSTANCE, "too_many_children")
            .defaultMessage("en", "%player1% and %player2% have more than 2 children together. %player1% and %player2% must remove %amount% children before they can marry.")
            .defaultMessage("de", "%player1% und %player2% haben mehr als 2 Kinder zusammen. %player1% und %player2% müssen %amount% Kinder entfernen, bevor sie heiraten können.");
    private static final CommandMessageKey SELF_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "self_request")
            .defaultMessage("en", "You cannot marry yourself!")
            .defaultMessage("de", "Du kannst dich nicht selbst heiraten!");
    private static final CommandMessageKey OPEN_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "open_request")
            .defaultMessage("en", "%player% already has an open proposal.")
            .defaultMessage("de", "%player% hat bereits einen offenen Antrag.");
    private static final CommandMessageKey SAME_PLAYER_MK = new LunaticCommandMessageKey(INSTANCE, "same_player")
            .defaultMessage("en", "You cannot marry someone to themselves!")
            .defaultMessage("de", "Du kannst niemanden mit sich selbst verheiraten!");
    private static final CommandMessageKey REQUEST_EXPIRED_PRIEST_MK = new LunaticCommandMessageKey(INSTANCE, "request_expired_priest")
            .defaultMessage("en", "The wedding between %player1% and %player2% has been canceled.")
            .defaultMessage("de", "Die Hochzeit zwischen %player1% und %player2% wurde abgesagt.");
    private static final CommandMessageKey REQUEST_EXPIRED_PLAYER_MK = new LunaticCommandMessageKey(INSTANCE, "request_expired_player")
            .defaultMessage("en", "Your wedding with %player% has been canceled.")
            .defaultMessage("de", "Deine Hochzeit mit %player% wurde abgesagt.");
    private static final CommandMessageKey FAMILY_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "family_request")
            .defaultMessage("en", "You cannot marry %player1% and %player2%. These players already belong to the same family.")
            .defaultMessage("de", "Du kannst %player1% und %player2% nicht heiraten. Diese Spieler gehören bereits zur selben Familie.");
    private static final CommandMessageKey YES_MK = new LunaticCommandMessageKey(INSTANCE, "yes")
            .defaultMessage("en", "Yes. I do!")
            .defaultMessage("de", "Ja. Ich will!");
    private static final CommandMessageKey NO_MK = new LunaticCommandMessageKey(INSTANCE, "no")
            .defaultMessage("en", "No. I don't want to.")
            .defaultMessage("de", "Nein. Ich will nicht.");



    @Override
    public String getPermission() {
        return "lunaticfamily.priest.marry";
    }

    @Override
    public String getName() {
        return "marry";
    }

    @Override
    public FamilyCommand getParentCommand() {
        return new Priest();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender player)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        UUID playerUUID = player.getUniqueId();

        if (args.length < 2) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            return true;
        }

        if (Utils.isPriest(playerUUID)) {
            sender.sendMessage(getMessage(ALREADY_PRIEST_MK));
            return true;
        }

        if (args[0].equalsIgnoreCase(player.getName()) || args[1].equalsIgnoreCase(player.getName())) {
            player.sendMessage(getMessage(SELF_REQUEST_MK));
            return true;
        }



        String player1Name = args[0];
        String player2Name = args[1];

        UUID player1UUID = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("name", player1Name).findOne().getUUID();
        UUID player2UUID = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("name", player2Name).findOne().getUUID();

        if (player1UUID == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", player1Name)));
            return true;
        }

        if (player2UUID == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", player2Name)));
            return true;
        }

        if (player1UUID.equals(player2UUID)) {
            sender.sendMessage(getMessage(SAME_PLAYER_MK));
            return true;
        }

        FamilyPlayer player1Fam = getFamilyPlayer(player1UUID);
        FamilyPlayer player2Fam = getFamilyPlayer(player2UUID);

        player1Fam.update();
        player2Fam.update();

        if (player1Fam.isFamilyMember(player2Fam)) {
            sender.sendMessage(getMessage(FAMILY_REQUEST_MK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", player2Fam.getName())));
            return true;
        }

        if (player2Fam.isFamilyMember(player1Fam)) {
            sender.sendMessage(getMessage(FAMILY_REQUEST_MK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", player2Fam.getName())));
            return true;
        }

        PlayerSender player1 = LunaticLib.getPlatform().getPlayerSender(player1UUID);
        PlayerSender player2 = LunaticLib.getPlatform().getPlayerSender(player2UUID);

        if (!player1.isOnline()) {
            sender.sendMessage(getMessage(PLAYER_OFFLINE_MK,
                placeholder("%player%", player1.getName())));
            return true;
        }

        if (!Utils.isPlayerOnRegisteredServer(player1)) {
            player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK,
                placeholder("%player%", player1.getName()),
                placeholder("%server%", player1.getServerName())));
            return true;
        }

        if (!player2.isOnline()) {
            sender.sendMessage(getMessage(PLAYER_OFFLINE_MK,
                placeholder("%player%", player2.getName())));
            return true;
        }

        if (!Utils.isPlayerOnRegisteredServer(player2)) {
            player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK,
                placeholder("%player%", player2.getName()),
                placeholder("%server%", player2.getServerName())));

            return true;
        }

        if (!player.isSameServer(player1.getUniqueId()) && LunaticFamily.getConfig().getMarryPriestRange() >= 0) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK,
                placeholder("%player%", player1.getName())));
            return true;
        }

        if (!player.isSameServer(player2.getUniqueId()) && LunaticFamily.getConfig().getMarryPriestRange() >= 0) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK,
                placeholder("%player%", player2.getName())));
            return true;
        }

        if (!player.isInRange(player1.getUniqueId(), LunaticFamily.getConfig().getMarryPriestRange())) {
            player.sendMessage(getMessage(PLAYER_TOO_FAR_AWAY_MK,
                placeholder("%player%", player1.getName())));
            return true;
        }

        if (!player.isInRange(player2.getUniqueId(), LunaticFamily.getConfig().getMarryPriestRange())) {
            player.sendMessage(getMessage(PLAYER_TOO_FAR_AWAY_MK,
                placeholder("%player%", player2.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), player1UUID, WithdrawKey.PRIEST_MARRY_PLAYER)) {
            sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", player1.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), player2UUID, WithdrawKey.PRIEST_MARRY_PLAYER)) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK,
                placeholder("%player%", player2.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_MARRY)) {
            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", player.getName())));
            return true;
        }


        if (player1Fam.isMarried()) {
            sender.sendMessage(getMessage(PLAYER_ALREADY_MARRIED_MK,
                placeholder("%player%", player1Fam.getName())));
            return true;
        }

        if (player2Fam.isMarried()) {
            sender.sendMessage(getMessage(PLAYER_ALREADY_MARRIED_MK,
                placeholder("%player%", player2Fam.getName())));
            return true;
        }

        if (LunaticFamily.marryRequests.containsKey(player1UUID) || LunaticFamily.marryPriests.containsValue(player1UUID)) {
            sender.sendMessage(getMessage(OPEN_REQUEST_MK,
                placeholder("%player%", player1Fam.getName())));
            return true;
        }

        if (LunaticFamily.marryRequests.containsKey(player2UUID) || LunaticFamily.marryPriests.containsValue(player2UUID)) {
            sender.sendMessage(getMessage(OPEN_REQUEST_MK,
                placeholder("%player%", player2Fam.getName())));
            return true;
        }

        if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
            int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
            sender.sendMessage(getMessage(TOO_MANY_CHILDREN_MK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", player2Fam.getName()),
                placeholder("%amount%", Integer.toString(amountDiff))));
            return true;
        }

        player.chat(getLanguageConfig().getMessageAsString(REQUEST_MK.noPrefix())
                .replace("%player1%", player1Fam.getName())
                .replace("%player2%", player2Fam.getName()));

        player1.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                Component.empty(),
                getMessage(YES_MK),
                "/family marry accept",
                getMessage(NO_MK),
                "/family marry deny"),
                LunaticFamily.getConfig().decisionAsInvGUI()
        );

        LunaticFamily.marryPriestRequests.put(player1UUID, player2UUID);
        LunaticFamily.marryPriests.put(player1UUID, playerUUID);

        Runnable runnable = () -> {
            if (LunaticFamily.marryPriestRequests.containsKey(player1UUID)) {
                LunaticFamily.marryPriestRequests.remove(player1UUID);
                LunaticFamily.marryPriests.remove(player1UUID);
                player.sendMessage(getMessage(REQUEST_EXPIRED_PRIEST_MK,
                placeholder("%player1%", player1.getName()),
                placeholder("%player2%", player2.getName())));
                player1.sendMessage(getMessage(REQUEST_EXPIRED_PLAYER_MK,
                placeholder("%player%", player2.getName())));
                player2.sendMessage(getMessage(REQUEST_EXPIRED_PLAYER_MK,
                placeholder("%player%", player1.getName())));
            }
        };

        Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);

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
