package de.janschuri.lunaticfamily.common.commands.priest;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
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

public class PriestAdopt extends FamilyCommand implements HasParentCommand, HasParams {

    private static final PriestAdopt INSTANCE = new PriestAdopt();

    private static final CommandMessageKey HELP_MK = new LunaticCommandMessageKey(INSTANCE, "help")
            .defaultMessage("en", "&6/%command% %subcommand% <%param%> <%param%> &7- Arrange the adoption of a child by a player.")
            .defaultMessage("de", "&6/%command% %subcommand% <%param%> <%param%> &7- Arrangiere die Adoption eines Kindes durch einen Spieler.");
    private static final CommandMessageKey ALREADY_PRIEST_MK = new LunaticCommandMessageKey(INSTANCE, "already_priest")
            .defaultMessage("en", "You are already a priest in another action.")
            .defaultMessage("de", "Du bist bereits Priester in einer anderen Aktion.");
    private static final CommandMessageKey REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "request")
            .defaultMessage("en", "%player1%, would you like to adopt %player2% on this Minecraft server?")
            .defaultMessage("de", "%player1%, möchtest du %player2% auf diesem Minecraft-Server adoptieren?");
    private static final CommandMessageKey PLAYER_ALREADY_ADOPTED_MK = new LunaticCommandMessageKey(INSTANCE, "player_already_adopted")
            .defaultMessage("en", "%player% is already adopted.")
            .defaultMessage("de", "%player% ist bereits adoptiert.");
    private static final CommandMessageKey TOO_MANY_CHILDREN_MK = new LunaticCommandMessageKey(INSTANCE, "too_many_children")
            .defaultMessage("en", "%player% already has two children.")
            .defaultMessage("de", "%player% hat bereits zwei Kinder.");
    private static final CommandMessageKey SELF_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "self_request")
            .defaultMessage("en", "You cannot adopt yourself.")
            .defaultMessage("de", "Du kannst dich nicht selbst adoptieren.");
    private static final CommandMessageKey OPEN_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "open_request")
            .defaultMessage("en", "%player% already has an open adoption request.")
            .defaultMessage("de", "%player% hat bereits eine offene Adoptionsanfrage.");
    private static final CommandMessageKey SAME_PLAYER_MK = new LunaticCommandMessageKey(INSTANCE, "same_player")
            .defaultMessage("en", "You cannot make someone their own child.")
            .defaultMessage("de", "Du kannst niemanden zu seinem eigenen Kind machen.");
    private static final CommandMessageKey REQUEST_EXPIRED_PRIEST_MK = new LunaticCommandMessageKey(INSTANCE, "request_expired_priest")
            .defaultMessage("en", "The adoption of %player2% by %player1% has been canceled.")
            .defaultMessage("de", "Die Adoption von %player2% durch %player1% wurde abgebrochen.");
    private static final CommandMessageKey REQUEST_EXPIRED_PLAYER_MK = new LunaticCommandMessageKey(INSTANCE, "request_expired_player")
            .defaultMessage("en", "Your adoption has been canceled.")
            .defaultMessage("de", "Deine Adoption wurde abgebrochen.");
    private static final CommandMessageKey FAMILY_REQUEST_MK = new LunaticCommandMessageKey(INSTANCE, "family_request")
            .defaultMessage("en", "%player1% cannot adopt %player2%. These players already belong to the same family.")
            .defaultMessage("de", "%player1% kann %player2% nicht adoptieren. Diese Spieler gehören bereits zur selben Familie.");
    private static final CommandMessageKey YES_MK = new LunaticCommandMessageKey(INSTANCE, "yes")
            .defaultMessage("en", "Yes. I do!")
            .defaultMessage("de", "Ja. Ich will!");
    private static final CommandMessageKey NO_MK = new LunaticCommandMessageKey(INSTANCE, "no")
            .defaultMessage("en", "No. I don't want to.")
            .defaultMessage("de", "Nein. Ich will nicht.");



    @Override
    public String getPermission() {
        return "lunaticfamily.priest.adopt";
    }

    @Override
    public String getName() {
        return "adopt";
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

        FamilyPlayer parentFam = FamilyPlayer.find(player1Name);
        FamilyPlayer childFam = FamilyPlayer.find(player2Name);

        if (parentFam == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                    placeholder("%player%", player1Name)
            ));
            return true;
        }

        if (childFam == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", player2Name)
            ));
            return true;
        }

        if (parentFam.equals(childFam)) {
            sender.sendMessage(getMessage(SAME_PLAYER_MK));
            return true;
        }

        UUID player1UUID = parentFam.getUUID();
        UUID player2UUID = childFam.getUUID();

        parentFam.update();
        childFam.update();

        if (parentFam.isFamilyMember(childFam)) {
            sender.sendMessage(getMessage(FAMILY_REQUEST_MK,
                placeholder("%player1%", parentFam.getName()),
                placeholder("%player2%", childFam.getName())
            ));
            return true;
        }

        if (childFam.isFamilyMember(parentFam)) {
            sender.sendMessage(getMessage(FAMILY_REQUEST_MK,
                placeholder("%player1%", parentFam.getName()),
                placeholder("%player2%", childFam.getName())
            ));
            return true;
        }

        PlayerSender player1 = LunaticLib.getPlatform().getPlayerSender(player1UUID);
        PlayerSender player2 = LunaticLib.getPlatform().getPlayerSender(player2UUID);

        if (!player1.isOnline()) {
            sender.sendMessage(getMessage(PLAYER_OFFLINE_MK,
                    placeholder("%player%", player1.getName())
            ));
            return true;
        }

        if (!Utils.isPlayerOnRegisteredServer(player1)) {
            player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK,
                    placeholder("%player%", player1.getName()),
                    placeholder("%server%", player1.getServerName())
            ));
            return true;
        }

        if (!player2.isOnline()) {
            sender.sendMessage(getMessage(PLAYER_OFFLINE_MK,
                    placeholder("%player%", player2.getName())
            ));
            return true;
        }

        if (!Utils.isPlayerOnRegisteredServer(player2)) {
            player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK,
                    placeholder("%player%", player2.getName()),
                    placeholder("%server%", player2.getServerName())
            ));

            return true;
        }

        if (!player.isSameServer(player1.getUniqueId()) && LunaticFamily.getConfig().getAdoptPriestRange() >= 0) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK,
                placeholder("%player%", player1.getName())
            ));
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

        if (!Utils.hasEnoughMoney(player.getServerName(), player1UUID, WithdrawKey.PRIEST_ADOPT_PARENT)) {
            sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", player1.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), player2UUID, WithdrawKey.PRIEST_ADOPT_CHILD)) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK,
                placeholder("%player%", player2.getName())));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_ADOPT)) {
            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK,
                placeholder("%player%", player.getName())));
            return true;
        }

        if (childFam.isAdopted()) {
            sender.sendMessage(getMessage(PLAYER_ALREADY_ADOPTED_MK,
                placeholder("%player%", childFam.getName())));
            return true;
        }

        int newChildrenAmount = parentFam.getChildrenAmount() + childFam.getSiblingsAmount();

        if (LunaticFamily.exceedsAdoptLimit(newChildrenAmount)) {
            sender.sendMessage(getMessage(TOO_MANY_CHILDREN_MK,
                    placeholder("%player%", parentFam.getName()))
            );
            return true;
        }

        if (LunaticFamily.adoptRequests.containsKey(player1UUID) || LunaticFamily.adoptPriests.containsValue(player1UUID)) {
            sender.sendMessage(getMessage(OPEN_REQUEST_MK,
                placeholder("%player%", parentFam.getName())));
            return true;
        }

        if (LunaticFamily.adoptRequests.containsKey(player2UUID) || LunaticFamily.adoptPriests.containsValue(player2UUID)) {
            sender.sendMessage(getMessage(OPEN_REQUEST_MK,
                placeholder("%player%", childFam.getName())));
            return true;
        }

        player.chat(getLanguageConfig().getMessageAsString(REQUEST_MK.noPrefix(),
                placeholder("%player1%", parentFam.getName()),
                placeholder("%player2%", childFam.getName())
        ));

        player1.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                Component.empty(),
                getMessage(YES_MK),
                "/family adopt accept",
                getMessage(NO_MK),
                "/family adopt deny"),
                LunaticFamily.getConfig().decisionAsInvGUI()
        );

        LunaticFamily.adoptPriestRequests.put(player1UUID, player2UUID);
        LunaticFamily.adoptPriests.put(player1UUID, playerUUID);

        Runnable runnable = () -> {
            if (LunaticFamily.adoptPriestRequests.containsKey(player1UUID)) {
                LunaticFamily.adoptPriestRequests.remove(player1UUID);
                LunaticFamily.adoptPriests.remove(player1UUID);
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
