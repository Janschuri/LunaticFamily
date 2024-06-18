package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.SiblingSubcommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestMarrySubcommand;
import de.janschuri.lunaticfamily.common.commands.priest.PriestSiblingSubcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SiblingAcceptSubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey noRequestMK = new CommandMessageKey(this,"no_request");
    private final CommandMessageKey completeMK = new CommandMessageKey(this,"complete");
    private final CommandMessageKey openRequestSiblingMK = new CommandMessageKey(this,"open_request_sibling");
    private final CommandMessageKey isAdoptedMK = new CommandMessageKey(new PriestSiblingSubcommand(),"is_adopted");

    private final CommandMessageKey priestRequestMK = new CommandMessageKey(new PriestSiblingSubcommand(),"request");
    private final CommandMessageKey priestYesMK = new CommandMessageKey(new PriestSiblingSubcommand(),"yes");
    private final CommandMessageKey priestNoMK = new CommandMessageKey(new PriestSiblingSubcommand(),"no");
    private final CommandMessageKey priestCompleteMK = new CommandMessageKey(new PriestSiblingSubcommand(),"complete");
    private final CommandMessageKey priestRequestExpiredPriestMK = new CommandMessageKey(new PriestSiblingSubcommand(),"request_expired_priest");
    private final CommandMessageKey priestRequestExpiredPlayerMK = new CommandMessageKey(new PriestSiblingSubcommand(),"request_expired_player");


    @Override
    public String getPermission() {
        return "lunaticfamily.sibling";
    }

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    public SiblingSubcommand getParentCommand() {
        return new SiblingSubcommand();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            return true;
        }

        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
            return true;
        }

        PlayerSender player = (PlayerSender) sender;
        UUID playerUUID = player.getUniqueId();
        FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

        if (!LunaticFamily.siblingRequests.containsKey(playerUUID) && !LunaticFamily.siblingPriestRequests.containsKey(playerUUID)) {
            sender.sendMessage(getMessage(noRequestMK));
            return true;
        }

        if (LunaticFamily.siblingPriestRequests.containsValue(playerUUID)) {
            sender.sendMessage(getMessage(openRequestSiblingMK));
            return true;
        }

        if (LunaticFamily.siblingRequests.containsKey(playerUUID)) {

            UUID siblingUUID = LunaticFamily.siblingRequests.get(playerUUID);
            FamilyPlayerImpl siblingFam = new FamilyPlayerImpl(siblingUUID);
            PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingUUID);

            if (playerFam.isAdopted()) {
                sender.sendMessage(getMessage(isAdoptedMK)
                        .replaceText(getTextReplacementConfig("%player1%", playerFam.getName()))
                        .replaceText(getTextReplacementConfig("%player2%", siblingFam.getName())));
                return true;
            }

            if (siblingFam.isAdopted()) {
                sender.sendMessage(getMessage(isAdoptedMK)
                        .replaceText(getTextReplacementConfig("%player1%", siblingFam.getName()))
                        .replaceText(getTextReplacementConfig("%player2%", playerFam.getName())));
                return true;
            }

            if (!sibling.isOnline()) {
                sender.sendMessage(getMessage(PLAYER_OFFLINE_MK)
                        .replaceText(getTextReplacementConfig("%player%", siblingFam.getName())));
                return true;
            }

            if (!Utils.isPlayerOnRegisteredServer(sibling)) {
                player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK)
                        .replaceText(getTextReplacementConfig("%player%", sibling.getName().replace("%server%", sibling.getServerName()))));
                return true;
            }

            if (LunaticFamily.siblingPriests.containsKey(siblingUUID)) {
                UUID priestUUID = LunaticFamily.siblingPriests.get(siblingUUID);
                FamilyPlayerImpl priestFam = new FamilyPlayerImpl(priestUUID);
                PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);

                if (!Utils.hasEnoughMoney(player.getServerName(), priestUUID, WithdrawKey.PRIEST_SIBLING)) {
                    sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK)
                            .replaceText(getTextReplacementConfig("%player%", priestFam.getName())));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_SIBLING_PLAYER)) {
                    sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK)
                            .replaceText(getTextReplacementConfig("%player%", player.getName())));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), siblingUUID, WithdrawKey.PRIEST_SIBLING_PLAYER)) {
                    sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                            .replaceText(getTextReplacementConfig("%player%", siblingFam.getName())));
                } else {

                    Utils.withdrawMoney(player.getServerName(), priestUUID, WithdrawKey.PRIEST_SIBLING);
                    Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.PRIEST_SIBLING_PLAYER);
                    Utils.withdrawMoney(player.getServerName(), siblingUUID, WithdrawKey.PRIEST_SIBLING_PLAYER);

                    player.chat(getLanguageConfig().getMessageAsString(priestYesMK, false));

                    priest.chat(getLanguageConfig().getMessageAsString(priestCompleteMK, false)
                            .replace("%player1%", playerFam.getName())
                            .replace("%player2%", siblingFam.getName()));

                    LunaticFamily.siblingRequests.remove(playerUUID);
                    LunaticFamily.siblingPriestRequests.remove(siblingUUID);
                    LunaticFamily.siblingPriests.remove(siblingUUID);

                    playerFam.addSibling(siblingFam.getId(), priestFam.getId());

                    for (String command : LunaticFamily.getConfig().getSuccessCommands("sibling_priest")) {
                        command = command.replace("%player1%", playerFam.getName()).replace("%player2%", siblingFam.getName()).replace("%priest%", priestFam.getName());
                        LunaticLib.getPlatform().sendConsoleCommand(command);
                    }
                }
            } else {
                if (!Utils.hasEnoughMoney(player.getServerName(), siblingUUID, WithdrawKey.SIBLING_PROPOSED_PLAYER)) {
                    sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                            .replaceText(getTextReplacementConfig("%player%", siblingFam.getName())));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.SIBLING_PROPOSED_PLAYER)) {
                    sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK)
                            .replaceText(getTextReplacementConfig("%player%", player.getName())));
                } else {
                    sender.sendMessage(getMessage(completeMK));
                    sibling.sendMessage(getMessage(completeMK));

                    Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.SIBLING_PROPOSED_PLAYER);
                    Utils.withdrawMoney(player.getServerName(), siblingUUID, WithdrawKey.SIBLING_PROPOSING_PLAYER);

                    LunaticFamily.siblingRequests.remove(playerUUID);
                    LunaticFamily.siblingPriestRequests.remove(siblingUUID);
                    LunaticFamily.siblingPriests.remove(siblingUUID);

                    playerFam.addSibling(siblingFam.getId());

                    for (String command : LunaticFamily.getConfig().getSuccessCommands("sibling")) {
                        command = command.replace("%player1%", playerFam.getName()).replace("%player2%", siblingFam.getName());
                        LunaticLib.getPlatform().sendConsoleCommand(command);
                    }
                }
            }
            return true;
        }

        if (LunaticFamily.siblingPriestRequests.containsKey(playerUUID)) {

            UUID siblingUUID = LunaticFamily.siblingPriestRequests.get(playerUUID);
            FamilyPlayerImpl siblingFam = new FamilyPlayerImpl(siblingUUID);
            PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingUUID);

            if (playerFam.isAdopted()) {
                sender.sendMessage(getMessage(isAdoptedMK)
                        .replaceText(getTextReplacementConfig("%player1%", playerFam.getName()))
                        .replaceText(getTextReplacementConfig("%player2%", siblingFam.getName())));
                return true;
            }

            if (siblingFam.isAdopted()) {
                sender.sendMessage(getMessage(isAdoptedMK)
                        .replaceText(getTextReplacementConfig("%player1%", siblingFam.getName()))
                        .replaceText(getTextReplacementConfig("%player2%", playerFam.getName())));
                return true;
            }

                UUID priestUUID = LunaticFamily.siblingPriests.get(playerUUID);
                PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);

                if (!Utils.hasEnoughMoney(player.getServerName(), siblingUUID, WithdrawKey.SIBLING_PROPOSING_PLAYER)) {
                    sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                            .replaceText(getTextReplacementConfig("%player%", siblingFam.getName())));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.SIBLING_PROPOSED_PLAYER)) {
                    sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK)
                            .replaceText(getTextReplacementConfig("%player%", player.getName())));
                } else {

                    LunaticFamily.siblingPriestRequests.remove(playerUUID);
                    LunaticFamily.siblingRequests.put(siblingUUID, playerUUID);

                    Runnable runnable = () -> {
                        if (LunaticFamily.siblingRequests.containsKey(siblingUUID)) {
                            LunaticFamily.siblingRequests.remove(siblingUUID);
                            LunaticFamily.siblingPriests.remove(siblingUUID);
                            priest.sendMessage(getMessage(priestRequestExpiredPriestMK)
                                    .replaceText(getTextReplacementConfig("%player1%", player.getName()))
                                    .replaceText(getTextReplacementConfig("%player2%", sibling.getName())));
                            player.sendMessage(getMessage(priestRequestExpiredPlayerMK)
                                    .replaceText(getTextReplacementConfig("%player%", sibling.getName())));
                            sibling.sendMessage(getMessage(priestRequestExpiredPlayerMK)
                                    .replaceText(getTextReplacementConfig("%player%", player.getName())));
                        }
                    };

                    Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);

                    player.chat(getLanguageConfig().getMessageAsString(priestYesMK, false));


                    priest.chat(getLanguageConfig().getMessageAsString(priestRequestMK, false)
                            .replace("%player1%", siblingFam.getName())
                            .replace("%player2%", playerFam.getName()));


                    sibling.sendMessage(Utils.getClickableDecisionMessage(
                            getPrefix(),
                            getMessage(priestYesMK, false),
                            "/family sibling accept",
                            getMessage(priestNoMK, false),
                            "/family sibling deny"));

            }
            return true;
        }

//        PlayerSender player = (PlayerSender) sender;
//        UUID playerUUID = player.getUniqueId();
//        FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);
//
//        if (!LunaticFamily.siblingRequests.containsKey(playerUUID)) {
//            sender.sendMessage(getMessage(noRequestMK));
//            return true;
//        }
//
//
//        UUID siblingUUID = LunaticFamily.siblingRequests.get(playerUUID);
//        PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingUUID);
//        FamilyPlayerImpl siblingFam = new FamilyPlayerImpl(siblingUUID);
//
//        if (!sibling.isOnline()) {
//            sender.sendMessage(getMessage(PLAYER_OFFLINE_MK)
//                    .replaceText(getTextReplacementConfig("%player%", siblingFam.getName())));
//            return true;
//        }
//
//        if (!Utils.isPlayerOnRegisteredServer(sibling)) {
//            player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK)
//                    .replaceText(getTextReplacementConfig("%player%", sibling.getName()))
//                    .replaceText(getTextReplacementConfig("%server%", sibling.getServerName())));
//            return true;
//        }
//
//        if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.SIBLING_PROPOSED_PLAYER)) {
//            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK));
//            return true;
//        }
//        if (!Utils.hasEnoughMoney(player.getServerName(), siblingUUID, WithdrawKey.SIBLING_PROPOSING_PLAYER)) {
//            sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
//                    .replaceText(getTextReplacementConfig("%player%", siblingFam.getName())));
//            return true;
//        }
//
//
//        sender.sendMessage(getMessage(completeMK)
//                .replaceText(getTextReplacementConfig("%player%", siblingFam.getName())));
//        sibling.sendMessage(getMessage(completeMK)
//                .replaceText(getTextReplacementConfig("%player%", playerFam.getName())));
//
//        LunaticFamily.siblingRequests.remove(playerUUID);
//        LunaticFamily.siblingRequests.remove(siblingUUID);
//        playerFam.addSibling(siblingFam.getId());
//
//        for (String command : LunaticFamily.getConfig().getSuccessCommands("sibling")) {
//            command = command.replace("%player1%", playerFam.getName()).replace("%player2%", siblingFam.getName());
//            LunaticLib.getPlatform().sendConsoleCommand(command);
//        }
//
//        Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.SIBLING_PROPOSED_PLAYER);
//        Utils.withdrawMoney(player.getServerName(), siblingUUID, WithdrawKey.SIBLING_PROPOSING_PLAYER);


        return true;
    }
}
