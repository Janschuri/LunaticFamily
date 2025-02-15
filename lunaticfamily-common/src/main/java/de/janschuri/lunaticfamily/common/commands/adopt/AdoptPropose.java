package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.FamilyCommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
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

    private final CommandMessageKey helpMK = new LunaticCommandMessageKey(this,"help");
    private final CommandMessageKey limitMK = new LunaticCommandMessageKey(this,"limit");
    private final CommandMessageKey openRequestMK = new LunaticCommandMessageKey(this,"open_request");
    private final CommandMessageKey requestMK = new LunaticCommandMessageKey(this,"request");
    private final CommandMessageKey requestBySingleMK = new LunaticCommandMessageKey(this,"request_by_single");
    private final CommandMessageKey requestSentMK = new LunaticCommandMessageKey(this,"request_sent");
    private final CommandMessageKey requestExpiredMK = new LunaticCommandMessageKey(this,"request_expired");
    private final CommandMessageKey requestSentExpiredMK = new LunaticCommandMessageKey(this,"request_sent_expired");
    private final CommandMessageKey requestBySingleExpiredMK = new LunaticCommandMessageKey(this,"request_by_single_expired");
    private final CommandMessageKey selfRequestMK = new LunaticCommandMessageKey(this,"self_request");
    private final CommandMessageKey hasSiblingMK = new LunaticCommandMessageKey(this,"has_sibling");
    private final CommandMessageKey hasSiblingLimitMK = new LunaticCommandMessageKey(this,"has_sibling_limit");
    private final CommandMessageKey noSingleAdoptMK = new LunaticCommandMessageKey(this,"no_single_adopt");
    private final CommandMessageKey alreadyAdoptedMK = new LunaticCommandMessageKey(this,"already_adopted");
    private final CommandMessageKey familyRequestMK = new LunaticCommandMessageKey(this,"family_request");
    private final CommandMessageKey cancelMK = new LunaticCommandMessageKey(this,"cancel");


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
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(PLAYER_NAME_MK.noPrefix())
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam());
    }
}
