package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryAcceptSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "accept";
    private static final String PERMISSION = "lunaticfamily.marry";

    public MarryAcceptSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_permission"));
        } else {
            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

            if (!LunaticFamily.marryRequests.containsKey(playerUUID) && !LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_accept_no_request"));
            } else if (LunaticFamily.marryPriestRequests.containsValue(playerUUID)) {
                sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_accept_open_request_partner"));
            } else {

                if (LunaticFamily.marryRequests.containsKey(playerUUID)) {

                    UUID partnerUUID = LunaticFamily.marryRequests.get(playerUUID);
                    FamilyPlayerImpl partnerFam = new FamilyPlayerImpl(partnerUUID);
                    PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

                    if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                        int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                        sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_accept_too_many_children").replace("%partner%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                        return true;
                    }
                    if (!partner.isOnline()) {
                        sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("player_offline").replace("%player%", partnerFam.getName()));
                        return true;
                    }

                    if (!Utils.isPlayerOnRegisteredServer(partner.getUniqueId())) {
                        player.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("player_not_on_whitelisted_server").replace("%player%", partner.getName().replace("%server%", partner.getServerName())));
                        return true;
                    }

                    if (LunaticFamily.marryPriest.containsKey(partnerUUID)) {
                        UUID priestUUID = LunaticFamily.marryPriest.get(partnerUUID);
                        FamilyPlayerImpl priestFam = new FamilyPlayerImpl(priestUUID);
                        PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);

                        if (!Utils.hasEnoughMoney(player.getServerName(), priestUUID, "marry_priest")) {
                            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("not_enough_money").replace("%player%", priestFam.getName()));
                        } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "marry_priest_player")) {
                            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("not_enough_money").replace("%player%", player.getName()));
                        } else if (!Utils.hasEnoughMoney(player.getServerName(), partnerUUID, "marry_priest_player")) {
                            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("player_not_enough_money").replace("%player%", partnerFam.getName()));
                        } else {

                            Utils.withdrawMoney(player.getServerName(), priestUUID, "marry_priest");
                            Utils.withdrawMoney(player.getServerName(), playerUUID, "marry_priest_player");
                            Utils.withdrawMoney(player.getServerName(), partnerUUID, "marry_priest_player");

                            player.chat(LunaticFamily.getLanguageConfig().getMessage("marry_yes"));

                            Runnable runnable = () -> priest.chat(LunaticFamily.getLanguageConfig().getMessage("marry_priest_complete").replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()));
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
                        if (!Utils.hasEnoughMoney(player.getServerName(), partnerUUID, "marry_proposing_player")) {
                            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("player_not_enough_money").replace("%player%", partnerFam.getName()));
                        } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "marry_proposed_player")) {
                            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("not_enough_money").replace("%player%", player.getName()));
                        } else {
                            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_accept_complete"));
                            partner.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_accept_complete"));

                            Utils.withdrawMoney(player.getServerName(), playerUUID, "marry_proposed_player");
                            Utils.withdrawMoney(player.getServerName(), partnerUUID, "marry_proposing_player");

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
                        sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_accept_too_many_children").replace("%partner%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                    } else {

                        UUID priestUUID = LunaticFamily.marryPriest.get(playerUUID);
                        PlayerSender priest = LunaticLib.getPlatform().getPlayerSender(priestUUID);

                        if (!Utils.hasEnoughMoney(player.getServerName(), partnerUUID, "marry_proposing_player")) {
                            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("player_not_enough_money").replace("%player%", partnerFam.getName()));
                        } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "marry_proposed_player")) {
                            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("not_enough_money").replace("%player%", player.getName()));
                        } else {

                            LunaticFamily.marryPriestRequests.remove(playerUUID);
                            LunaticFamily.marryRequests.put(partnerUUID, playerUUID);
                            player.chat(LunaticFamily.getLanguageConfig().getMessage("marry_yes"));


                            priest.chat(LunaticFamily.getLanguageConfig().getMessage("marry_accept_request").replace("%player1%", partnerFam.getName()).replace("%player2%", playerFam.getName()));



                            partner.sendMessage(Utils.getClickableDecisionMessage(
                                    LunaticFamily.getLanguageConfig().getPrefix(),
                                    LunaticFamily.getLanguageConfig().getMessage("marry_yes"),
                                    "/family marry accept",
                                    LunaticFamily.getLanguageConfig().getMessage("marry_no"),
                                    "/family marry deny"));
                        }
                    }
                }
            }
        }
        return true;
    }
}
