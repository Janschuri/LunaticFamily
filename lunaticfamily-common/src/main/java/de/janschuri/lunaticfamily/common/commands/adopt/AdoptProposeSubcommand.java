package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.AdoptSubcommand;
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

public class AdoptProposeSubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey limitMK = new CommandMessageKey(this,"limit");
    private final CommandMessageKey openRequestMK = new CommandMessageKey(this,"open_request");
    private final CommandMessageKey requestMK = new CommandMessageKey(this,"request");
    private final CommandMessageKey requestBySingleMK = new CommandMessageKey(this,"request_by_single");
    private final CommandMessageKey requestSentMK = new CommandMessageKey(this,"request_sent");
    private final CommandMessageKey requestExpiredMK = new CommandMessageKey(this,"request_expired");
    private final CommandMessageKey requestSentExpiredMK = new CommandMessageKey(this,"request_sent_expired");
    private final CommandMessageKey requestBySingleExpiredMK = new CommandMessageKey(this,"request_by_single_expired");
    private final CommandMessageKey selfRequestMK = new CommandMessageKey(this,"self_request");
    private final CommandMessageKey hasSiblingMK = new CommandMessageKey(this,"has_sibling");
    private final CommandMessageKey hasSiblingLimitMK = new CommandMessageKey(this,"has_sibling_limit");
    private final CommandMessageKey noSingleAdoptMK = new CommandMessageKey(this,"no_single_adopt");
    private final CommandMessageKey alreadyAdoptedMK = new CommandMessageKey(this,"already_adopted");
    private final CommandMessageKey familyRequestMK = new CommandMessageKey(this,"family_request");
    private final CommandMessageKey cancelMK = new CommandMessageKey(this,"cancel");


    @Override
    public String getPermission() {
        return "lunaticfamily.adopt";
    }

    @Override
    public String getName() {
        return "propose";
    }

    @Override
    public AdoptSubcommand getParentCommand() {
        return new AdoptSubcommand();
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
            } else if (cancel) {
                sender.sendMessage(getMessage(cancelMK).replaceText(getTextReplacementConfig("%player%", args[2])));
                return true;
            } else if (!playerFam.isMarried() && !LunaticFamily.getConfig().isAllowSingleAdopt()) {
                sender.sendMessage(getMessage(noSingleAdoptMK));
                return true;
            } else if (playerFam.getChildrenAmount() > 1) {
                sender.sendMessage(getMessage(limitMK));
                return true;
            }

            String childName = args[0];

            UUID childUUID = PlayerDataTable.getUUID(childName);

            if (childUUID == null) {
                player.sendMessage(getMessage(PLAYER_NOT_EXIST_MK).replaceText(getTextReplacementConfig("%player%", childName)));
                return true;
            }

            PlayerSender child = LunaticLib.getPlatform().getPlayerSender(childUUID);


            if (!child.isOnline()) {
                sender.sendMessage(getMessage(PLAYER_OFFLINE_MK).replaceText(getTextReplacementConfig("%player%", args[0])));
                return true;
            }

            if (!Utils.isPlayerOnRegisteredServer(child)) {
                player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK).replaceText(getTextReplacementConfig("%player%", child.getName().replace("%server%", child.getServerName()))));
                return true;
            }

            if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.ADOPT_PARENT)) {
                sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
                return true;
            }

                if (!player.isSameServer(child.getUniqueId()) && LunaticFamily.getConfig().getAdoptProposeRange() >= 0) {
                    sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK).replaceText(getTextReplacementConfig("%player%", child.getName())));
                    return true;
                }

                if (!player.isInRange(child.getUniqueId(), LunaticFamily.getConfig().getAdoptProposeRange())) {
                    player.sendMessage(getMessage(PLAYER_TOO_FAR_AWAY_MK).replaceText(getTextReplacementConfig("%player%", child.getName())));
                    return true;
                }

                FamilyPlayerImpl childFam = new FamilyPlayerImpl(childUUID);

                if (args[0].equalsIgnoreCase(player.getName())) {
                    player.sendMessage(getMessage(selfRequestMK));
                } else if (playerFam.isFamilyMember(childFam.getId())) {
                    player.sendMessage(getMessage(familyRequestMK)
                            .replaceText(getTextReplacementConfig("%player%", childFam.getName())));
                } else if (LunaticFamily.adoptRequests.containsKey(childUUID)) {
                    player.sendMessage(getMessage(openRequestMK)
                            .replaceText(getTextReplacementConfig("%player%", childFam.getName())));
                } else if (childFam.getParents() == null) {
                    player.sendMessage(getMessage(alreadyAdoptedMK)
                            .replaceText(getTextReplacementConfig("%player%", childFam.getName())));
                } else if (childFam.hasSibling() && !confirm) {
                    player.sendMessage(Utils.getClickableDecisionMessage(
                            getMessage(hasSiblingMK)
                                    .replaceText(getTextReplacementConfig("%player1%", childFam.getName()))
                                    .replaceText(getTextReplacementConfig("%player2%", childFam.getSibling().getName())),
                            getMessage(CONFIRM_MK, false),
                            "/family adopt propose " + child.getName() + " confirm",
                            getMessage(CANCEL_MK, false),
                            "/family adopt propose " + child.getName() + " cancel"));

                } else if (childFam.hasSibling() && playerFam.getChildrenAmount() > 0) {
                    sender.sendMessage(getMessage(hasSiblingLimitMK)
                            .replaceText(getTextReplacementConfig("%player1%", childFam.getName()))
                            .replaceText(getTextReplacementConfig("%player2%", childFam.getSibling().getName())));
                } else {
                    if (playerFam.isMarried()) {
                        child.sendMessage(Utils.getClickableDecisionMessage(
                                getMessage(requestMK)
                                        .replaceText(getTextReplacementConfig("%player1%", playerFam.getName()))
                                        .replaceText(getTextReplacementConfig("%player2%", playerFam.getPartner().getName())),
                                getMessage(ACCEPT_MK, false),
                                "/family adopt accept",
                                getMessage(DENY_MK, false),
                                "/family adopt deny"));
                    } else {
                        child.sendMessage(Utils.getClickableDecisionMessage(
                                getMessage(requestBySingleMK)
                                        .replaceText(getTextReplacementConfig("%player%", playerFam.getName())),
                                getMessage(ACCEPT_MK, false),
                                "/family adopt accept",
                                getMessage(DENY_MK, false),
                                "/family adopt deny"));
                    }
                    LunaticFamily.adoptRequests.put(childUUID, playerUUID);
                    sender.sendMessage(getMessage(requestSentMK)
                            .replaceText(getTextReplacementConfig("%player%", childFam.getName())));

                    Runnable runnable = () -> {
                        if (LunaticFamily.adoptRequests.containsKey(childUUID)) {
                            LunaticFamily.adoptRequests.remove(childUUID);
                            if (playerFam.isMarried()) {
                                FamilyPlayerImpl partnerFam = playerFam.getPartner();
                                child.sendMessage(getMessage(requestExpiredMK)
                                        .replaceText(getTextReplacementConfig("%player1%", playerFam.getName()))
                                        .replaceText(getTextReplacementConfig("%player2%", partnerFam.getName())));
                            } else {
                                child.sendMessage(getMessage(requestBySingleExpiredMK)
                                        .replaceText(getTextReplacementConfig("%player%", playerFam.getName())));
                            }
                            player.sendMessage(getMessage(requestSentExpiredMK)
                                    .replaceText(getTextReplacementConfig("%player%", childFam.getName())));
                        }
                    };

                    Utils.scheduleTask(runnable, 30, TimeUnit.SECONDS);
                }
        }
        return true;
    }

    @Override
    public Component getParamsName() {
        return getMessage(PLAYER_NAME_MK, false);
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam());
    }
}
