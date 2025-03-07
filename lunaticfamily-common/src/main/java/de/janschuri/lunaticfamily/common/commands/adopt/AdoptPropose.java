package de.janschuri.lunaticfamily.common.commands.adopt;

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

public class AdoptPropose extends FamilyCommand implements HasParentCommand, HasParams {

    private static final AdoptPropose INSTANCE = new AdoptPropose();

    private static final CommandMessageKey helpMK = new LunaticCommandMessageKey(INSTANCE,"help")
            .defaultMessage("en", "&6/%command% %subcommand% &b<%param%> &7- Propose an adoption to a player.")
            .defaultMessage("de", "&6/%command% %subcommand% &b<%param%> &7- Schlage einem Spieler eine Adoption vor.");
    private static final CommandMessageKey limitMK = new LunaticCommandMessageKey(INSTANCE,"limit")
            .defaultMessage("en", "You cannot adopt another child. You have already reached the limit of two children.")
            .defaultMessage("de", "Du kannst kein weiteres Kind adoptieren. Du hast bereits das Limit von zwei Kindern erreicht.");
    private static final CommandMessageKey openRequestMK = new LunaticCommandMessageKey(INSTANCE,"open_request")
            .defaultMessage("en", "%player% already has an open adoption request.")
            .defaultMessage("de", "%player% hat bereits eine offene Adoptionsanfrage.");
    private static final CommandMessageKey requestMK = new LunaticCommandMessageKey(INSTANCE,"request")
            .defaultMessage("en", "%player1% and %player2% want to adopt you. Do you accept?")
            .defaultMessage("de", "%player1% und %player2% möchten dich adoptieren. Akzeptierst du?");
    private static final CommandMessageKey requestBySingleMK = new LunaticCommandMessageKey(INSTANCE,"request_by_single")
            .defaultMessage("en", "%player% wants to adopt you. Do you accept?")
            .defaultMessage("de", "%player% möchte dich adoptieren. Akzeptierst du?");
    private static final CommandMessageKey requestSentMK = new LunaticCommandMessageKey(INSTANCE,"request_sent")
            .defaultMessage("en", "You have sent an adoption request to %player%.")
            .defaultMessage("de", "Du hast eine Adoptionsanfrage an %player% gesendet.");
    private static final CommandMessageKey requestExpiredMK = new LunaticCommandMessageKey(INSTANCE,"request_expired")
            .defaultMessage("en", "The adoption request from %player1% and %player2% has expired.")
            .defaultMessage("de", "Die Adoptionsanfrage von %player1% und %player2% ist abgelaufen.");
    private static final CommandMessageKey requestSentExpiredMK = new LunaticCommandMessageKey(INSTANCE,"request_sent_expired")
            .defaultMessage("en", "The adoption request to %player% has expired.")
            .defaultMessage("de", "Die Adoptionsanfrage an %player% ist abgelaufen.");
    private static final CommandMessageKey requestBySingleExpiredMK = new LunaticCommandMessageKey(INSTANCE,"request_by_single_expired")
            .defaultMessage("en", "The adoption request by %player% has expired.")
            .defaultMessage("de", "Die Adoptionsanfrage von %player% ist abgelaufen.");
    private static final CommandMessageKey selfRequestMK = new LunaticCommandMessageKey(INSTANCE,"self_request")
            .defaultMessage("en", "You cannot adopt yourself.")
            .defaultMessage("de", "Du kannst dich nicht selbst adoptieren.");
    private static final CommandMessageKey hasSiblingMK = new LunaticCommandMessageKey(INSTANCE,"has_sibling")
            .defaultMessage("en", "%player1% and %player2% are siblings. Do you want to adopt both?")
            .defaultMessage("de", "%player1% und %player2% sind Geschwister. Möchtest du beide adoptieren?");
    private static final CommandMessageKey hasSiblingLimitMK = new LunaticCommandMessageKey(INSTANCE,"has_sibling_limit")
            .defaultMessage("en", "%player1% and %player2% are siblings. You already have 1 child and can only have a total of 2 children.")
            .defaultMessage("de", "%player1% und %player2% sind Geschwister. Du hast bereits 1 Kind und kannst insgesamt nur 2 Kinder haben.");
    private static final CommandMessageKey noSingleAdoptMK = new LunaticCommandMessageKey(INSTANCE,"no_single_adopt")
            .defaultMessage("en", "You cannot adopt a child as a single.")
            .defaultMessage("de", "Du kannst kein Kind als Single adoptieren.");
    private static final CommandMessageKey alreadyAdoptedMK = new LunaticCommandMessageKey(INSTANCE,"already_adopted")
            .defaultMessage("en", "%player% is already adopted.")
            .defaultMessage("de", "%player% ist bereits adoptiert.");
    private static final CommandMessageKey familyRequestMK = new LunaticCommandMessageKey(INSTANCE,"family_request")
            .defaultMessage("en", "%player% is already a family member.")
            .defaultMessage("de", "%player% ist bereits ein Familienmitglied.");
    private static final CommandMessageKey cancelMK = new LunaticCommandMessageKey(INSTANCE,"cancel")
            .defaultMessage("en", "You have canceled the adoption request to %player%.")
            .defaultMessage("de", "Du hast die Adoptionsanfrage an %player% abgebrochen.");


    @Override
    public String getPermission() {
        return "lunaticfamily.adopt";
    }

    @Override
    public String getName() {
        return "propose";
    }

    @Override
    public Adopt getParentCommand() {
        return new Adopt();
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

        boolean confirm = false;
        boolean cancel = false;

        if (args.length > 2) {
            if (args[2].equalsIgnoreCase("confirm")) {
                confirm = true;
            }
            if (args[2].equalsIgnoreCase("cancel")) {
                cancel = true;
            }
        }


        if (args.length < 1) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            Logger.debugLog("AdoptProposeSubcommand: Wrong usage");
            return true;
        }

        if (cancel) {
            sender.sendMessage(getMessage(cancelMK,
                placeholder("%player%", args[2])));
            return true;
        }

        if (!playerFam.isMarried() && !LunaticFamily.getConfig().isAllowSingleAdopt()) {
            sender.sendMessage(getMessage(noSingleAdoptMK));
            return true;
        }

        if (playerFam.getChildrenAmount() > 1) {
            sender.sendMessage(getMessage(limitMK));
            return true;
        }

        String childName = args[0];

        UUID childUUID = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("name", childName).findOne().getUUID();

        if (childUUID == null) {
            player.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                placeholder("%player%", childName)));
            return true;
        }

        PlayerSender child = LunaticLib.getPlatform().getPlayerSender(childUUID);


        if (!child.isOnline()) {
            sender.sendMessage(getMessage(PLAYER_OFFLINE_MK,
                placeholder("%player%", args[0])));
            return true;
        }

        if (!Utils.isPlayerOnRegisteredServer(child)) {
            player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK,
                placeholder("%player%", child.getName().replace("%server%", child.getServerName()))));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.ADOPT_PARENT)) {
            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
            return true;
        }

        if (!player.isSameServer(child.getUniqueId()) && LunaticFamily.getConfig().getAdoptProposeRange() >= 0) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK,
                placeholder("%player%", child.getName())));
            return true;
        }

        if (!player.isInRange(child.getUniqueId(), LunaticFamily.getConfig().getAdoptProposeRange())) {
            player.sendMessage(getMessage(PLAYER_TOO_FAR_AWAY_MK,
                placeholder("%player%", child.getName())));
            return true;
        }

        FamilyPlayer childFam = getFamilyPlayer(childUUID);

        if (args[0].equalsIgnoreCase(player.getName())) {
            player.sendMessage(getMessage(selfRequestMK));
            return true;
        }

        playerFam.update();

        if (playerFam.isFamilyMember(childFam)) {
            player.sendMessage(getMessage(familyRequestMK,
                placeholder("%player%", childFam.getName())));
            return true;
        }

        if (LunaticFamily.adoptRequests.containsKey(childUUID)) {
            player.sendMessage(getMessage(openRequestMK,
                placeholder("%player%", childFam.getName())));
            return true;
        }

        if (childFam.getParents() == null) {
            player.sendMessage(getMessage(alreadyAdoptedMK,
                placeholder("%player%", childFam.getName())));
            return true;
        }

        if (childFam.hasSiblings() && !confirm) {
            player.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(hasSiblingMK.noPrefix(),
                placeholder("%player1%", childFam.getName()),
                placeholder("%player2%", childFam.getSibling().getName())),
                    getMessage(CONFIRM_MK.noPrefix()),
                    "/family adopt propose " + child.getName() + " confirm",
                    getMessage(CANCEL_MK.noPrefix()),
                    "/family adopt propose " + child.getName() + " cancel"),
                    LunaticFamily.getConfig().decisionAsInvGUI()
            );
            return true;
        }

        if (childFam.hasSiblings() && playerFam.getChildrenAmount() > 0) {
            sender.sendMessage(getMessage(hasSiblingLimitMK,
                placeholder("%player1%", childFam.getName()),
                placeholder("%player2%", childFam.getSibling().getName())));
            return true;
        }

        if (playerFam.isMarried()) {
            child.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(requestMK.noPrefix(),
                        placeholder("%player1%", playerFam.getName()),
                        placeholder("%player2%", playerFam.getPartner().getName())
                    ),
                    getMessage(ACCEPT_MK.noPrefix()),
                    "/family adopt accept",
                    getMessage(DENY_MK.noPrefix()),
                    "/family adopt deny"),
                    LunaticFamily.getConfig().decisionAsInvGUI()
            );
        } else {
            child.sendMessage(Utils.getClickableDecisionMessage(
                    getPrefix(),
                    getMessage(requestBySingleMK.noPrefix(),
                placeholder("%player%", playerFam.getName())),
                    getMessage(ACCEPT_MK.noPrefix()),
                    "/family adopt accept",
                    getMessage(DENY_MK.noPrefix()),
                    "/family adopt deny"),
                    LunaticFamily.getConfig().decisionAsInvGUI()
            );
        }

        LunaticFamily.adoptRequests.put(childUUID, playerUUID);
        sender.sendMessage(getMessage(requestSentMK,
                placeholder("%player%", childFam.getName())));

        Runnable runnable = () -> {
            if (LunaticFamily.adoptRequests.containsKey(childUUID)) {
                LunaticFamily.adoptRequests.remove(childUUID);
                if (playerFam.isMarried()) {
                    FamilyPlayer partnerFam = playerFam.getPartner();
                    child.sendMessage(getMessage(requestExpiredMK,
                placeholder("%player1%", playerFam.getName()),
                placeholder("%player2%", partnerFam.getName())));
                } else {
                    child.sendMessage(getMessage(requestBySingleExpiredMK,
                placeholder("%player%", playerFam.getName())));
                }
                player.sendMessage(getMessage(requestSentExpiredMK,
                placeholder("%player%", childFam.getName())));
            }
        };

        Utils.scheduleTask(runnable, 30, TimeUnit.SECONDS);

        return true;
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                helpMK, getPermission()
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
