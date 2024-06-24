package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
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
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
        } else if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
        } else {
            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

            if (playerFam.hasSibling()) {
                sender.sendMessage(getMessage(hasSiblingMK)
                        .replaceText(getTextReplacementConfig("%player%", playerFam.getName())));
                return true;
            } else if (playerFam.isAdopted()) {
                sender.sendMessage(getMessage(isAdoptedMK)
                        .replaceText(getTextReplacementConfig("%player%", playerFam.getName())));
                return true;
            } else if (args.length < 1) {
                sender.sendMessage(getMessage(WRONG_USAGE_MK));
                Logger.debugLog("SiblingProposeSubcommand: Wrong usage");
                return true;
            }

            String siblingName = args[0];

            UUID siblingUUID = PlayerDataTable.getUUID(siblingName);

            if (siblingUUID == null) {
                sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                        .replaceText(getTextReplacementConfig("%player%", siblingName)));
                return true;
            }

            PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingUUID);

            if (!sibling.isOnline()) {
                sender.sendMessage(getMessage(PLAYER_OFFLINE_MK)
                        .replaceText(getTextReplacementConfig("%player%", sibling.getName())));
                return true;
            }

            if (!Utils.isPlayerOnRegisteredServer(sibling)) {
                player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK)
                        .replaceText(getTextReplacementConfig("%player%", sibling.getName()))
                        .replaceText(getTextReplacementConfig("%server%", sibling.getServerName())));
                return true;
            }

                FamilyPlayerImpl siblingFam = new FamilyPlayerImpl(siblingUUID);
                if (playerFam.getId() == siblingFam.getId()) {
                    sender.sendMessage(getMessage(selfRequestMK));
                } else if (playerFam.isFamilyMember(siblingFam.getId())) {
                    sender.sendMessage(getMessage(familyRequestMK)
                            .replaceText(getTextReplacementConfig("%player%", siblingFam.getName())));
                } else if (siblingFam.isFamilyMember(playerFam.getId())) {
                    sender.sendMessage(getMessage(familyRequestMK)
                            .replaceText(getTextReplacementConfig("%player%", siblingFam.getName())));
                } else if (siblingFam.isAdopted()) {
                    sender.sendMessage(getMessage(isAdoptedMK)
                            .replaceText(getTextReplacementConfig("%player%", siblingFam.getName())));
                } else if (LunaticFamily.siblingRequests.containsKey(siblingUUID)) {
                    sender.sendMessage(getMessage(openRequestMK)
                            .replaceText(getTextReplacementConfig("%player%", siblingFam.getName())));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.SIBLING_PROPOSING_PLAYER)) {
                    sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
                } else {

                    if (!player.isSameServer(sibling.getUniqueId()) && LunaticFamily.getConfig().getSiblingProposeRange() >= 0) {
                        sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK)
                                .replaceText(getTextReplacementConfig("%player%", sibling.getName())));
                        return true;
                    }

                    if (!player.isInRange(sibling.getUniqueId(), LunaticFamily.getConfig().getSiblingProposeRange())) {
                        player.sendMessage(getMessage(PLAYER_TOO_FAR_AWAY_MK)
                                .replaceText(getTextReplacementConfig("%player%", sibling.getName())));
                        return true;
                    }

                    sibling.sendMessage(Utils.getClickableDecisionMessage(
                            getMessage(requestMK)
                                    .replaceText(getTextReplacementConfig("%player%", siblingFam.getName())),
                            getMessage(ACCEPT_MK, false),
                            "/family sibling accept",
                            getMessage(DENY_MK, false),
                            "/family sibling deny"));

                    LunaticFamily.siblingRequests.put(siblingUUID, playerUUID);

                    sender.sendMessage(getMessage(requestSentMK)
                            .replaceText(getTextReplacementConfig("%player%", siblingFam.getName())));

                    Runnable runnable = () -> {
                        if (LunaticFamily.siblingRequests.containsKey(siblingUUID)) {
                            LunaticFamily.siblingRequests.remove(siblingUUID);
                            sibling.sendMessage(getMessage(requestExpiredMK)
                                    .replaceText(getTextReplacementConfig("%player%", player.getName())));

                            player.sendMessage(getMessage(requestSentExpiredMK)
                                    .replaceText(getTextReplacementConfig("%player%", sibling.getName())));
                        }
                    };

                    Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);
                }

        }
        return true;
    }

    @Override
    public List<Component> getParamsNames() {
        return List.of(
                getMessage(PLAYER_NAME_MK, false)
        );
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam());
    }
}
