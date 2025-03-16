package de.janschuri.lunaticfamily.common.commands.marry;

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

public class MarryPropose extends FamilyCommand implements HasParentCommand, HasParams {

    private static final MarryPropose INSTANCE = new MarryPropose();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% &b<%param%> &7- Propose marriage to a player.")
            .defaultMessage("de", "&6/%command% %subcommand% &b<%param%> &7- Mache einem Spieler einen Heiratsantrag.");
    private static final CommandMessageKey ALREADY_MARRIED_MK = new LunaticCommandMessageKey(INSTANCE, "already_married")
            .defaultMessage("en", "You are already married to %player%.")
            .defaultMessage("de", "Du bist bereits mit %player% verheiratet.");
    private static final CommandMessageKey PLAYER_ALREADY_MARRIED_MK = new LunaticCommandMessageKey(INSTANCE, "player_already_married")
            .defaultMessage("en", "%player% is already married.")
            .defaultMessage("de", "%player% ist bereits verheiratet.");
    private static final CommandMessageKey REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "request")
            .defaultMessage("en", "%player1%, would you like to marry %player2% on this Minecraft server?")
            .defaultMessage("de", "%player1%, möchtest du %player2% auf diesem Minecraft-Server heiraten?");
    private static final CommandMessageKey REQUEST_SENT_MK = new LunaticCommandMessageKey(INSTANCE, "request_sent")
            .defaultMessage("en", "You have proposed marriage to %player%.")
            .defaultMessage("de", "Du hast %player% einen Heiratsantrag gemacht.");
    private static final CommandMessageKey REQUEST_EXPIRED_MK = new LunaticCommandMessageKey(INSTANCE, "request_expired")
            .defaultMessage("en", "The marriage proposal from %player% has expired.")
            .defaultMessage("de", "Der Heiratsantrag von %player% ist abgelaufen.");
    private static final CommandMessageKey REQUEST_SENT_EXPIRED_MK = new LunaticCommandMessageKey(INSTANCE, "request_sent_expired")
            .defaultMessage("en", "Your marriage proposal to %player% has expired.")
            .defaultMessage("de", "Dein Heiratsantrag an %player% ist abgelaufen.");
    private static final CommandMessageKey OPEN_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "open_request")
            .defaultMessage("en", "%player% already has an open proposal.")
            .defaultMessage("de", "%player% hat bereits einen offenen Antrag.");
    private static final CommandMessageKey FAMILY_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "family_request")
            .defaultMessage("en", "You cannot marry %player%. This player already belongs to your family.")
            .defaultMessage("de", "Du kannst %player% nicht heiraten. Dieser Spieler gehört bereits zu deiner Familie.");
    private static final CommandMessageKey TOO_MANY_CHILDREN_MK = new LunaticCommandMessageKey(INSTANCE, "too_many_children")
            .defaultMessage("en", "You and %player% have more than two children together. You must remove %amount% children before you and %player% can marry.")
            .defaultMessage("de", "Du und %player% habt zusammen mehr als zwei Kinder. Du musst %amount% Kinder entfernen, bevor du und %player% heiraten können.");
    private static final CommandMessageKey SELF_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "self_request")
            .defaultMessage("en", "You cannot marry yourself!")
            .defaultMessage("de", "Du kannst dich nicht selbst heiraten!");
    private static final CommandMessageKey MARRY_NO_MK = new LunaticCommandMessageKey(INSTANCE, "marry_no")
            .defaultMessage("en", "No, I don't want to.")
            .defaultMessage("de", "Nein, ich möchte nicht.");
    private static final CommandMessageKey MARRY_YES_MK = new LunaticCommandMessageKey(INSTANCE, "marry_yes")
            .defaultMessage("en", "Yes, I do.")
            .defaultMessage("de", "Ja, ich will.");



    @Override
    public String getPermission() {
        return "lunaticfamily.marry";
    }

    @Override
    public String getName() {
        return "propose";
    }

    @Override
    public Marry getParentCommand() {
        return new Marry();
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
        FamilyPlayer playerFam = getFamilyPlayer(playerUUID);

        if (args.length < 1) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            return true;
        }

        if (playerFam.getName().equalsIgnoreCase(args[0])) {
            sender.sendMessage(getMessage(SELF_REQUEST_MK));
            return true;
        }

        if (playerFam.isMarried()) {
            sender.sendMessage(getMessage(ALREADY_MARRIED_MK,
                placeholder("%player%", playerFam.getName())));
            return true;
        }

        String partnerName = args[0];

        FamilyPlayer partnerFam = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("name", partnerName).findOneOrEmpty().orElse(null);

        if (partnerFam == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", partnerName)));
            return true;
        }

        UUID partnerUUID = partnerFam.getUUID();
        PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

        if (!partner.isOnline()) {
            sender.sendMessage(getMessage(PLAYER_OFFLINE_MK,
                placeholder("%player%", partner.getName())));
            return true;
        }

        if (!Utils.isPlayerOnRegisteredServer(partner)) {
            player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK,
                placeholder("%player%", partner.getName()),
                placeholder("%server%", partner.getServerName())));
            return true;
        }

        if (!player.isSameServer(partnerUUID) && LunaticFamily.getConfig().getMarryProposeRange() >= 0) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK,
                placeholder("%player%", partner.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_PROPOSING_PLAYER)) {
            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
            return true;
        }

        partnerFam.update();

        if (playerFam.isFamilyMember(partnerFam)) {
            sender.sendMessage(getMessage(FAMILY_REQUEST_MK,
                placeholder("%player%", partnerFam.getName())));
            return true;
        }

        if (partnerFam.isFamilyMember(playerFam)) {
            sender.sendMessage(getMessage(FAMILY_REQUEST_MK,
                placeholder("%player%", partnerFam.getName())));
            return true;
        }

        if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
            int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
            sender.sendMessage(getMessage(TOO_MANY_CHILDREN_MK,
                placeholder("%player%", partnerFam.getName()),
                placeholder("%amount%", Integer.toString(amountDiff))));
        }

        if (LunaticFamily.marryRequests.containsKey(partner.getUniqueId()) || LunaticFamily.marryPriests.containsKey(partner.getUniqueId())) {
            sender.sendMessage(getMessage(OPEN_REQUEST_MK,
                placeholder("%player%", partnerFam.getName())));
            return true;
        }

        if (partnerFam.isMarried()) {
            sender.sendMessage(getMessage(PLAYER_ALREADY_MARRIED_MK,
                placeholder("%player%", partnerFam.getName())));
            return true;
        }

        if (!player.isInRange(partner.getUniqueId(), LunaticFamily.getConfig().getMarryProposeRange()).thenApply(b -> b).join()) {
            player.sendMessage(getMessage(PLAYER_TOO_FAR_AWAY_MK,
                placeholder("%player%", partner.getName())));
            return true;
        }

        partner.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                getMessage(REQUEST_MK.noPrefix(),
                placeholder("%player1%", partnerFam.getName()),
                placeholder("%player2%", playerFam.getName())),
                getMessage(MARRY_YES_MK.noPrefix()),
                "/family marry accept",
                getMessage(MARRY_NO_MK.noPrefix()),
                "/family marry deny"),
                LunaticFamily.getConfig().decisionAsInvGUI()
        );


        LunaticFamily.marryRequests.put(partnerUUID, playerUUID);

        sender.sendMessage(getMessage(REQUEST_SENT_MK,
                placeholder("%player%", partnerFam.getName())
        ));

        Runnable runnable = () -> {
            if (LunaticFamily.marryRequests.containsKey(partnerUUID)) {
                LunaticFamily.marryRequests.remove(partnerUUID);
                player.sendMessage(getMessage(REQUEST_SENT_EXPIRED_MK,
                placeholder("%player%", partner.getName())));
                partner.sendMessage(getMessage(REQUEST_EXPIRED_MK,
                placeholder("%player%", player.getName())));
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
                PLAYER_NAME_MK
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam());
    }
}
