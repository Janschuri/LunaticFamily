package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.MarrySubcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticfamily.common.utils.WithdrawKey;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryAcceptSubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey openRequestPartnerMK = new CommandMessageKey(this,"open_request_partner");
    private final CommandMessageKey noRequestMK = new CommandMessageKey(this,"no_request");
    private final CommandMessageKey tooManyChildrenMK = new CommandMessageKey(this,"too_many_children");
    private final CommandMessageKey completeMK = new CommandMessageKey(this,"complete");
    private final CommandMessageKey requestMK = new CommandMessageKey(this,"request");
    private final CommandMessageKey marryYesMK = new CommandMessageKey(new MarrySubcommand(),"yes");
    private final CommandMessageKey marryNoMK = new CommandMessageKey(new MarrySubcommand(),"no");
    private final CommandMessageKey priestCompleteMK = new CommandMessageKey(this,"priest_complete");



    @Override
    public String getPermission() {
        return "lunaticfamily.marry";
    }

    @Override
    public String getName() {
        return "accept";
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

            if (!LunaticFamily.marryRequests.containsKey(playerUUID) && !LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                sender.sendMessage(getMessage(noRequestMK));
            } else if (LunaticFamily.marryPriestRequests.containsValue(playerUUID)) {
                sender.sendMessage(getMessage(openRequestPartnerMK));
            } else {

                if (LunaticFamily.marryRequests.containsKey(playerUUID)) {

                    UUID partnerUUID = LunaticFamily.marryRequests.get(playerUUID);
                    FamilyPlayerImpl partnerFam = new FamilyPlayerImpl(partnerUUID);
                    PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

                    if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                        int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                        sender.sendMessage(getMessage(tooManyChildrenMK)
                                .replaceText(getTextReplacementConfig("%partner%", partnerFam.getName()))
                                .replaceText(getTextReplacementConfig("%amount%", Integer.toString(amountDiff))));
                        return true;
                    }
                    if (!partner.isOnline()) {
                        sender.sendMessage(getMessage(PLAYER_OFFLINE_MK)
                                .replaceText(getTextReplacementConfig("%player%", partnerFam.getName())));
                        return true;
                    }

                    if (!Utils.isPlayerOnRegisteredServer(partner)) {
                        player.sendMessage(getMessage(PLAYER_NOT_ON_WHITELISTED_SERVER_MK)
                                .replaceText(getTextReplacementConfig("%player%", partner.getName().replace("%server%", partner.getServerName()))));
                        return true;
                    }

                    if (LunaticFamily.marryPriest.containsKey(partnerUUID)) {
                        UUID priestUUID = LunaticFamily.marryPriest.get(partnerUUID);
                        FamilyPlayerImpl priestFam = new FamilyPlayerImpl(priestUUID);
                        PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);

                        if (!Utils.hasEnoughMoney(player.getServerName(), priestUUID, WithdrawKey.MARRY_PRIEST)) {
                            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK)
                                    .replaceText(getTextReplacementConfig("%player%", priestFam.getName())));
                        } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_PRIEST_PLAYER)) {
                            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK)
                                    .replaceText(getTextReplacementConfig("%player%", player.getName())));
                        } else if (!Utils.hasEnoughMoney(player.getServerName(), partnerUUID, WithdrawKey.MARRY_PRIEST_PLAYER)) {
                            sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                                    .replaceText(getTextReplacementConfig("%player%", partnerFam.getName())));
                        } else {

                            Utils.withdrawMoney(player.getServerName(), priestUUID, WithdrawKey.MARRY_PRIEST);
                            Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_PRIEST_PLAYER);
                            Utils.withdrawMoney(player.getServerName(), partnerUUID, WithdrawKey.MARRY_PRIEST_PLAYER);

                            player.chat(getMessage(marryYesMK).toString());

                            Runnable runnable = () -> priest.chat(getMessage(priestCompleteMK)
                                    .replaceText(getTextReplacementConfig("%player1%", playerFam.getName()))
                                    .replaceText(getTextReplacementConfig("%player2%", partnerFam.getName())).toString());

                            Utils.scheduleTask(runnable, 1, TimeUnit.SECONDS);

                            LunaticFamily.marryRequests.remove(playerUUID);
                            LunaticFamily.marryPriestRequests.remove(partnerUUID);
                            LunaticFamily.marryPriest.remove(partnerUUID);

                            playerFam.marry(partnerFam.getId(), priestFam.getId());

                            for (String command : LunaticFamily.getConfig().getSuccessCommands("marry_priest")) {
                                command = command.replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()).replace("%priest%", priestFam.getName());
                                LunaticLib.getPlatform().sendConsoleCommand(command);
                            }
                        }
                    } else {
                        if (!Utils.hasEnoughMoney(player.getServerName(), partnerUUID, WithdrawKey.MARRY_PROPOSED_PLAYER)) {
                            sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                                    .replaceText(getTextReplacementConfig("%player%", partnerFam.getName())));
                        } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_PROPOSED_PLAYER)) {
                            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK)
                                    .replaceText(getTextReplacementConfig("%player%", player.getName())));
                        } else {
                            sender.sendMessage(getMessage(completeMK));
                            partner.sendMessage(getMessage(completeMK));

                            Utils.withdrawMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_PROPOSED_PLAYER);
                            Utils.withdrawMoney(player.getServerName(), partnerUUID, WithdrawKey.MARRY_PROPOSING_PLAYER);

                            LunaticFamily.marryRequests.remove(playerUUID);
                            LunaticFamily.marryPriestRequests.remove(partnerUUID);
                            LunaticFamily.marryPriest.remove(partnerUUID);

                            playerFam.marry(partnerFam.getId());

                            for (String command : LunaticFamily.getConfig().getSuccessCommands("marry")) {
                                command = command.replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName());
                                LunaticLib.getPlatform().sendConsoleCommand(command);
                            }
                        }
                    }

                }

                else if (LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {

                    UUID partnerUUID = LunaticFamily.marryPriestRequests.get(playerUUID);
                    FamilyPlayerImpl partnerFam = new FamilyPlayerImpl(partnerUUID);
                    PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

                    if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                        int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                        sender.sendMessage(getMessage(tooManyChildrenMK)
                                .replaceText(getTextReplacementConfig("%partner%", partnerFam.getName()))
                                .replaceText(getTextReplacementConfig("%amount%", Integer.toString(amountDiff))));
                    } else {

                        UUID priestUUID = LunaticFamily.marryPriest.get(playerUUID);
                        PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);

                        if (!Utils.hasEnoughMoney(player.getServerName(), partnerUUID, WithdrawKey.MARRY_PROPOSING_PLAYER)) {
                            sender.sendMessage(getMessage(PLAYER_NOT_ENOUGH_MONEY_MK)
                                    .replaceText(getTextReplacementConfig("%player%", partnerFam.getName())));
                        } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, WithdrawKey.MARRY_PROPOSED_PLAYER)) {
                            sender.sendMessage(getMessage(NOT_ENOUGH_MONEY_MK)
                                    .replaceText(getTextReplacementConfig("%player%", player.getName())));
                        } else {

                            LunaticFamily.marryPriestRequests.remove(playerUUID);
                            LunaticFamily.marryRequests.put(partnerUUID, playerUUID);
                            player.chat(LunaticFamily.getLanguageConfig().getMessage(marryYesMK).toString());


                            priest.chat(LunaticFamily.getLanguageConfig().getMessage(requestMK)
                                    .replaceText(getTextReplacementConfig("%player1%", partnerFam.getName()))
                                    .replaceText(getTextReplacementConfig("%player2%", playerFam.getName())).toString());



                            partner.sendMessage(Utils.getClickableDecisionMessage(
                                    LunaticFamily.getLanguageConfig().getPrefix(),
                                    LunaticFamily.getLanguageConfig().getMessage(marryYesMK),
                                    "/family marry accept",
                                    LunaticFamily.getLanguageConfig().getMessage(marryNoMK),
                                    "/family marry deny"));
                        }
                    }
                }
            }
        }
        return true;
    }
}
