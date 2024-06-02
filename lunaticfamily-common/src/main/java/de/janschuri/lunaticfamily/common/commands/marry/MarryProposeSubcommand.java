package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.MarrySubcommand;
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

public class MarryProposeSubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey alreadyMarriedMK = new CommandMessageKey(this,"already_married");
    private final CommandMessageKey playerAlreadyMarriedMK = new CommandMessageKey(this,"player_already_married");
    private final CommandMessageKey requestMK = new CommandMessageKey(this,"request");
    private final CommandMessageKey requestSentMK = new CommandMessageKey(this,"request_sent");
    private final CommandMessageKey requestExpiredMK = new CommandMessageKey(this,"request_expired");
    private final CommandMessageKey requestSentExpiredMK = new CommandMessageKey(this,"request_sent_expired");
    private final CommandMessageKey openRequestMK = new CommandMessageKey(this,"open_request");
    private final CommandMessageKey familyRequestMK = new CommandMessageKey(this,"family_request");
    private final CommandMessageKey tooManyChildrenMK = new CommandMessageKey(this,"too_many_children");
    private final CommandMessageKey selfRequestMK = new CommandMessageKey(this,"self_request");
    private final CommandMessageKey marryYesMK = new CommandMessageKey(new MarrySubcommand(),"yes");
    private final CommandMessageKey marryNoMK = new CommandMessageKey(new MarrySubcommand(),"no");


    @Override
    public String getPermission() {
        return "lunaticfamily.marry";
    }

    @Override
    public String getName() {
        return "propose";
    }

    @Override
    public MarrySubcommand getParentCommand() {
        return new MarrySubcommand();
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

            if (args.length < 1) {
                sender.sendMessage(getMessage(WRONG_USAGE_MK));
                Logger.debugLog("MarryProposeSubcommand: Wrong usage");
                return true;
            } else if (playerFam.getName().equalsIgnoreCase(args[0])) {
                sender.sendMessage(getMessage(selfRequestMK));
                return true;
            } else if (playerFam.isMarried()) {
                sender.sendMessage(getMessage(alreadyMarriedMK)
                        .replaceText(getTextReplacementConfig("%player%", playerFam.getName())));
                return true;
            }

            String partnerName = args[0];

            UUID partnerUUID = PlayerDataTable.getUUID(partnerName);

            if (partnerUUID == null) {
                sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                        .replaceText(getTextReplacementConfig("%player%", partnerName)));
                return true;
            }

            PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

            if (!partner.isOnline()) {
                sender.sendMessage(getMessage(PLAYER_OFFLINE_MK)
                        .replaceText(getTextReplacementConfig("%player%", partner.getName())));
                return true;
            }

            if (!Utils.isPlayerOnRegisteredServer(partner)) {
                player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK)
                        .replaceText(getTextReplacementConfig("%player%", partner.getName()))
                        .replaceText(getTextReplacementConfig("%server%", partner.getServerName())));
                return true;
            }

            if (!player.isSameServer(partnerUUID) && LunaticFamily.getConfig().getMarryProposeRange() >= 0) {
                sender.sendMessage(getMessage(PLAYER_NOT_SAME_SERVER_MK)
                        .replaceText(getTextReplacementConfig("%player%", partner.getName())));
                return true;
            }

            if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_PROPOSING_PLAYER)) {
                sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
                return true;
            }

                FamilyPlayerImpl partnerFam = new FamilyPlayerImpl(partnerUUID);
                if (playerFam.isFamilyMember(partnerFam.getId())) {
                    sender.sendMessage(getMessage(familyRequestMK)
                            .replaceText(getTextReplacementConfig("%player%", partnerFam.getName())));
                } else if (partnerFam.isFamilyMember(playerFam.getId())) {
                    sender.sendMessage(getMessage(familyRequestMK)
                            .replaceText(getTextReplacementConfig("%player%", partnerFam.getName())));
                } else if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                    int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                    sender.sendMessage(getMessage(tooManyChildrenMK)
                            .replaceText(getTextReplacementConfig("%player%", partnerFam.getName()))
                            .replaceText(getTextReplacementConfig("%amount%", Integer.toString(amountDiff))));
                } else if (LunaticFamily.marryRequests.containsKey(partner.getUniqueId()) || LunaticFamily.marryPriest.containsKey(partner.getUniqueId())) {
                    sender.sendMessage(getMessage(openRequestMK)
                            .replaceText(getTextReplacementConfig("%player%", partnerFam.getName())));
                } else if (partnerFam.isMarried()) {
                    sender.sendMessage(getMessage(playerAlreadyMarriedMK)
                            .replaceText(getTextReplacementConfig("%player%", partnerFam.getName())));
                } else {

                    if (!player.isInRange(partner.getUniqueId(), LunaticFamily.getConfig().getMarryProposeRange())) {
                        player.sendMessage(getMessage(PLAYER_TOO_FAR_AWAY_MK)
                                .replaceText(getTextReplacementConfig("%player%", partner.getName())));
                        return true;
                    }

                    partner.sendMessage(Utils.getClickableDecisionMessage(
                            getMessage(requestMK)
                                    .replaceText(getTextReplacementConfig("%player1%", partnerFam.getName()))
                                    .replaceText(getTextReplacementConfig("%player2%", playerFam.getName())),
                            getMessage(marryYesMK, false),
                            "/family marry accept",
                            getMessage(marryNoMK, false),
                            "/family marry deny"));


                    LunaticFamily.marryRequests.put(partnerUUID, playerUUID);

                    sender.sendMessage(getMessage(requestSentMK)
                            .replaceText(getTextReplacementConfig("%player%", partnerFam.getName())));

                    Runnable runnable = () -> {
                        if (LunaticFamily.marryRequests.containsKey(partnerUUID)) {
                            LunaticFamily.marryRequests.remove(partnerUUID);
                            player.sendMessage(getMessage(requestSentExpiredMK)
                                    .replaceText(getTextReplacementConfig("%player%", partner.getName())));
                            partner.sendMessage(getMessage(requestExpiredMK)
                                    .replaceText(getTextReplacementConfig("%player%", player.getName())));
                        }
                    };

                    Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);

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
