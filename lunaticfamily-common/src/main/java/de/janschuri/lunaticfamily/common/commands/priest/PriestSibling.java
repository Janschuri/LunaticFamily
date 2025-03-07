package de.janschuri.lunaticfamily.common.commands.priest;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.commands.adopt.Adopt;
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

public class PriestSibling extends FamilyCommand implements HasParentCommand, HasParams {

    private static final PriestSibling INSTANCE = new PriestSibling();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% <%param%> <%param%> &7- Arrange the siblinghood of two players.")
            .defaultMessage("de", "&6/%command% %subcommand% <%param%> <%param%> &7- Arrangiere die Geschwisterschaft von zwei Spielern.");
    private static final CommandMessageKey ALREADY_PRIEST_MK = new LunaticCommandMessageKey(INSTANCE, "already_priest")
            .defaultMessage("en", "You are already a priest in another action.")
            .defaultMessage("de", "Du bist bereits ein Priester in einer anderen Aktion.");
    private static final CommandMessageKey REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "request")
            .defaultMessage("en", "%player1%, would you like to be siblings with %player2% on this Minecraft server?")
            .defaultMessage("de", "%player1%, möchtest du Geschwister mit %player2% auf diesem Minecraft-Server sein?");
    private static final CommandMessageKey IS_ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE, "is_adopted")
            .defaultMessage("en", "%player1% is adopted. You could set up the adoption of %player2% by %player1%'s parents.")
            .defaultMessage("de", "%player1% ist adoptiert. Du könntest die Adoption von %player2% durch %player1%'s Eltern einrichten.");
    private static final CommandMessageKey ALREADY_SIBLING_MK = new LunaticCommandMessageKey(INSTANCE, "already_sibling")
            .defaultMessage("en", "%player% already has a sibling.")
            .defaultMessage("de", "%player% hat bereits ein Geschwister.");
    private static final CommandMessageKey SELF_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "self_request")
            .defaultMessage("en", "You cannot be your own sibling.")
            .defaultMessage("de", "Du kannst nicht dein eigenes Geschwister sein.");
    private static final CommandMessageKey OPEN_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "open_request")
            .defaultMessage("en", "%player% already has an open sibling request.")
            .defaultMessage("de", "%player% hat bereits eine offene Geschwister Anfrage.");
    private static final CommandMessageKey SAME_PLAYER_MK = new LunaticCommandMessageKey(INSTANCE, "same_player")
            .defaultMessage("en", "You cannot make someone their own sibling.")
            .defaultMessage("de", "Du kannst niemanden zu seinem eigenen Geschwister machen.");
    private static final CommandMessageKey REQUEST_EXPIRED_PRIEST_MK = new LunaticCommandMessageKey(INSTANCE, "request_expired_priest")
            .defaultMessage("en", "The siblinghood between %player1% and %player2% has been canceled.")
            .defaultMessage("de", "Die Geschwisterschaft zwischen %player1% und %player2% wurde abgebrochen.");
    private static final CommandMessageKey REQUEST_EXPIRED_PLAYER_MK = new LunaticCommandMessageKey(INSTANCE, "request_expired_player")
            .defaultMessage("en", "Your siblinghood with %player% has been canceled.")
            .defaultMessage("de", "Deine Geschwisterschaft mit %player% wurde abgebrochen.");
    private static final CommandMessageKey FAMILY_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "family_request")
            .defaultMessage("en", "You cannot set up the siblinghood between %player1% and %player2%. These players already belong to the same family.")
            .defaultMessage("de", "Du kannst die Geschwisterschaft zwischen %player1% und %player2% nicht einrichten. Diese Spieler gehören bereits zur selben Familie.");
    private static final CommandMessageKey YES_MK = new LunaticCommandMessageKey(INSTANCE, "yes")
            .defaultMessage("en", "Yes. I do!")
            .defaultMessage("de", "Ja. Ich will!");
    private static final CommandMessageKey NO_MK = new LunaticCommandMessageKey(INSTANCE, "no")
            .defaultMessage("en", "No. I don't want to.")
            .defaultMessage("de", "Nein. Ich will nicht.");
    private static final CommandMessageKey COMPLETE_MK = new LunaticCommandMessageKey(INSTANCE, "complete")
            .defaultMessage("en", "You are siblings!")
            .defaultMessage("de", "Ihr seid Geschwister!");



    @Override
    public String getPermission() {
        return "lunaticfamily.priest.sibling";
    }

    @Override
    public String getName() {
        return "sibling";
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
            Logger.debugLog("PriestSiblingSubcommand: Wrong usage");
            return true;
        }

        if (LunaticFamily.siblingPriests.containsValue(playerUUID)) {
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

        if (!player.isSameServer(player1.getUniqueId()) && LunaticFamily.getConfig().getAdoptPriestRange() >= 0) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK,
                placeholder("%player%", player1.getName())));
            return true;
        }

        if (!player.isSameServer(player2.getUniqueId()) && LunaticFamily.getConfig().getAdoptPriestRange() >= 0) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK,
                placeholder("%player%", player2.getName())));
            return true;
        }

        if (!player.isInRange(player1.getUniqueId(), LunaticFamily.getConfig().getAdoptPriestRange())) {
            player.sendMessage(getMessage(PLAYER_TOO_FAR_AWAY_MK,
                placeholder("%player%", player1.getName())));
            return true;
        }

        if (!player.isInRange(player2.getUniqueId(), LunaticFamily.getConfig().getAdoptPriestRange())) {
            player.sendMessage(getMessage(PLAYER_TOO_FAR_AWAY_MK,
                placeholder("%player%", player2.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), player1UUID, WithdrawKey.PRIEST_SIBLING_PLAYER)) {
            sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", player1.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), player2UUID, WithdrawKey.PRIEST_SIBLING_PLAYER)) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK,
                placeholder("%player%", player2.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_SIBLING)) {
            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", player.getName())));
            return true;
        }


        if (player1Fam.hasSiblings()) {
            sender.sendMessage(getMessage(ALREADY_SIBLING_MK,
                placeholder("%player%", player1Fam.getName())));
            return true;
        }

        if (player2Fam.isMarried()) {
            sender.sendMessage(getMessage(ALREADY_SIBLING_MK,
                placeholder("%player%", player2Fam.getName())));
            return true;
        }

        if (LunaticFamily.siblingRequests.containsKey(player1UUID) || LunaticFamily.siblingPriests.containsValue(player1UUID)) {
            sender.sendMessage(getMessage(OPEN_REQUEST_MK,
                placeholder("%player%", player1Fam.getName())));
            return true;
        }

        if (LunaticFamily.siblingRequests.containsKey(player2UUID) || LunaticFamily.siblingPriests.containsValue(player2UUID)) {
            sender.sendMessage(getMessage(OPEN_REQUEST_MK,
                placeholder("%player%", player2Fam.getName())));
            return true;
        }

        if (player1Fam.isAdopted()) {
            sender.sendMessage(getMessage(IS_ADOPTED_MK,
                placeholder("%player1%", player1Fam.getName()),
                placeholder("%player2%", player2Fam.getName())));
            return true;
        }

        if (player2Fam.isAdopted()) {
            sender.sendMessage(getMessage(IS_ADOPTED_MK,
                placeholder("%player1%", player2Fam.getName()),
                placeholder("%player2%", player1Fam.getName())));
            return true;
        }

        player.chat(getLanguageConfig().getMessageAsString(REQUEST_MK.noPrefix())
                .replace("%player1%", player1Fam.getName())
                .replace("%player2%", player2Fam.getName()));

        player1.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                Component.empty(),
                getMessage(YES_MK),
                "/family sibling accept",
                getMessage(NO_MK),
                "/family sibling deny"),
                LunaticFamily.getConfig().decisionAsInvGUI()
        );

        LunaticFamily.siblingPriestRequests.put(player1UUID, player2UUID);
        LunaticFamily.siblingPriests.put(player1UUID, playerUUID);

        Runnable runnable = () -> {
            if (LunaticFamily.siblingPriestRequests.containsKey(player1UUID)) {
                LunaticFamily.siblingPriestRequests.remove(player1UUID);
                LunaticFamily.siblingPriests.remove(player1UUID);
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