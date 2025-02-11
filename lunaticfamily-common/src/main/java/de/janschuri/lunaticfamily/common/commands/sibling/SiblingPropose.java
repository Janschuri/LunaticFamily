package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SiblingPropose extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey hasSiblingMK = new CommandMessageKey(this,"has_sibling");
    private final CommandMessageKey isAdoptedMK = new CommandMessageKey(this,"is_adopted");
    private final CommandMessageKey playerIsAdoptedMK = new CommandMessageKey(this,"player_is_adopted");
    private final CommandMessageKey selfRequestMK = new CommandMessageKey(this,"self_request");
    private final CommandMessageKey familyRequestMK = new CommandMessageKey(this,"family_request");
    private final CommandMessageKey openRequestMK = new CommandMessageKey(this,"open_request");
    private final CommandMessageKey requestMK = new CommandMessageKey(this,"request");
    private final CommandMessageKey requestSentMK = new CommandMessageKey(this,"request_sent");
    private final CommandMessageKey requestExpiredMK = new CommandMessageKey(this,"request_expired");
    private final CommandMessageKey requestSentExpiredMK = new CommandMessageKey(this,"request_sent_expired");


    @Override
    public String getPermission() {
        return "lunaticfamily.sibling";
    }

    @Override
    public String getName() {
        return "propose";
    }

    @Override
    public Sibling getParentCommand() {
        return new Sibling();
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

        if (playerFam.hasSiblings()) {
            sender.sendMessage(getMessage(hasSiblingMK,
                    placeholder("%player%", playerFam.getName())
            ));
            return true;
        }

        if (playerFam.isAdopted()) {
            sender.sendMessage(getMessage(isAdoptedMK,
                    placeholder("%player%", playerFam.getName())
            ));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(getMessage(WRONG_USAGE_MK));
            Logger.debugLog("SiblingPropose: Wrong usage");
            return true;
        }

        String siblingName = args[0];

        FamilyPlayer siblingFam = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("name", siblingName).findOneOrEmpty().orElse(null);

        if (siblingFam == null) {
            sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK,
                    placeholder("%player%", siblingName)
            ));
            return true;
        }

        UUID siblingUUID = siblingFam.getUUID();
        PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingUUID);

        if (!sibling.isOnline()) {
            sender.sendMessage(getMessage(PLAYER_OFFLINE_MK,
                    placeholder("%player%", sibling.getName())
            ));
            return true;
        }

        if (!Utils.isPlayerOnRegisteredServer(sibling)) {
            player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK,
                    placeholder("%player%", sibling.getName()),
                    placeholder("%server%", sibling.getServerName())
            ));
            return true;
        }

        if (playerFam.getId() == siblingFam.getId()) {
            sender.sendMessage(getMessage(selfRequestMK));
            return true;
        }

        playerFam.update();

        if (playerFam.isFamilyMember(siblingFam)) {
            sender.sendMessage(getMessage(familyRequestMK,
                    placeholder("%player%", siblingFam.getName())
            ));
            return true;
        }

        siblingFam.update();

        if (siblingFam.isFamilyMember(playerFam)) {
            sender.sendMessage(getMessage(familyRequestMK,
                    placeholder("%player%", siblingFam.getName())
            ));
            return true;
        }

        if (siblingFam.isAdopted()) {
            sender.sendMessage(getMessage(playerIsAdoptedMK,
                    placeholder("%player%", siblingFam.getName())
            ));
            return true;
        }

        if (LunaticFamily.siblingRequests.containsKey(siblingUUID)) {
            sender.sendMessage(getMessage(openRequestMK,
                    placeholder("%player%", siblingFam.getName())
            ));
            return true;
        }

        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.SIBLING_PROPOSING_PLAYER)) {
            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
            return true;
        }

        if (!player.isSameServer(sibling.getUniqueId()) && LunaticFamily.getConfig().getSiblingProposeRange() >= 0) {
            sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK,
                    placeholder("%player%", sibling.getName())
            ));
            return true;
        }

        if (!player.isInRange(sibling.getUniqueId(), LunaticFamily.getConfig().getSiblingProposeRange())) {
            player.sendMessage(getMessage(PLAYER_TOO_FAR_AWAY_MK,
                    placeholder("%player%", sibling.getName())
            ));
            return true;
        }

        sibling.sendMessage(Utils.getClickableDecisionMessage(
                getPrefix(),
                getMessage(requestMK.noPrefix(), placeholder("%player%", playerFam.getName())),
                getMessage(ACCEPT_MK.noPrefix()),
                "/family sibling accept",
                getMessage(DENY_MK.noPrefix()),
                "/family sibling deny"),
                LunaticFamily.getConfig().decisionAsInvGUI()
        );

        LunaticFamily.siblingRequests.put(siblingUUID, playerUUID);

        sender.sendMessage(getMessage(requestSentMK,
                placeholder("%player%", siblingFam.getName())
        ));

        Runnable runnable = () -> {
            if (LunaticFamily.siblingRequests.containsKey(siblingUUID)) {
                LunaticFamily.siblingRequests.remove(siblingUUID);
                sibling.sendMessage(getMessage(requestExpiredMK,
                        placeholder("%player%", playerFam.getName())
                ));

                player.sendMessage(getMessage(requestSentExpiredMK,
                        placeholder("%player%", sibling.getName())
                ));
            }
        };

        Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);



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
