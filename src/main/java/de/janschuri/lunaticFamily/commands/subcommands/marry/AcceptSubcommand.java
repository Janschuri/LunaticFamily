package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.ClickableDecisionMessage;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AcceptSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "accept";
    private static final String permission = "lunaticfamily.marry";

    public AcceptSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!(sender instanceof AbstractPlayerSender)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            AbstractPlayerSender player = (AbstractPlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (!LunaticFamily.marryRequests.containsKey(playerUUID) && !LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("marry_accept_no_request"));
            } else if (LunaticFamily.marryPriestRequests.containsValue(playerUUID)) {
                sender.sendMessage(language.getPrefix() + language.getMessage("marry_accept_open_request_partner"));
            } else {

                if (LunaticFamily.marryRequests.containsKey(playerUUID)) {

                    UUID partnerUUID = LunaticFamily.marryRequests.get(playerUUID);
                    FamilyPlayer partnerFam = new FamilyPlayer(partnerUUID);
                    AbstractPlayerSender partner = AbstractSender.getPlayerSender(partnerUUID);

                    if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                        int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                        sender.sendMessage(language.getPrefix() + language.getMessage("marry_accept_too_many_children").replace("%partner%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                        return true;
                    }
                    if (!partner.isOnline()) {
                        sender.sendMessage(language.getPrefix() + language.getMessage("player_offline").replace("%player%", partnerFam.getName()));
                        return true;
                    }

                    if (!Utils.getUtils().isPlayerOnWhitelistedServer(partner.getUniqueId())) {
                        player.sendMessage(language.getPrefix() + language.getMessage("player_not_on_whitelisted_server").replace("%player%", partner.getName().replace("%server%", partner.getServerName())));
                        return true;
                    }

                    if (LunaticFamily.marryPriest.containsKey(partnerUUID)) {
                        UUID priestUUID = LunaticFamily.marryPriest.get(partnerUUID);
                        FamilyPlayer priestFam = new FamilyPlayer(priestUUID);
                        AbstractPlayerSender priest = AbstractSender.getPlayerSender(priestUUID);

                        if (!Utils.getUtils().hasEnoughMoney(priestUUID, "marry_priest")) {
                            sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money").replace("%player%", priestFam.getName()));
                        } else if (!Utils.getUtils().hasEnoughMoney(playerUUID, "marry_priest_player")) {
                            sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money").replace("%player%", player.getName()));
                        } else if (!Utils.getUtils().hasEnoughMoney(partnerUUID, "marry_priest_player")) {
                            sender.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", partnerFam.getName()));
                        } else {

                            Utils.getUtils().withdrawMoney(priestUUID, "marry_priest");
                            Utils.getUtils().withdrawMoney(playerUUID, "marry_priest_player");
                            Utils.getUtils().withdrawMoney(partnerUUID, "marry_priest_player");

                            player.chat(language.getMessage("marry_yes"));

                            Runnable runnable = () -> priest.chat(language.getMessage("marry_priest_complete").replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()));
                            Utils.scheduleTask(runnable, 1, TimeUnit.SECONDS);

                            LunaticFamily.marryRequests.remove(playerUUID);
                            LunaticFamily.marryPriestRequests.remove(partnerUUID);
                            LunaticFamily.marryPriest.remove(partnerUUID);

                            playerFam.marry(partnerFam.getID(), priestFam.getID());

                            for (String command : PluginConfig.successCommands.get("marry_priest")) {
                                command = command.replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()).replace("%priest%", priestFam.getName());
                                Utils.getUtils().sendConsoleCommand(command);
                            }
                        }
                    } else {
                        if (!Utils.getUtils().hasEnoughMoney(partnerUUID, "marry_proposing_player")) {
                            sender.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", partnerFam.getName()));
                        } else if (!Utils.getUtils().hasEnoughMoney(playerUUID, "marry_proposed_player")) {
                            sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money").replace("%player%", player.getName()));
                        } else {
                            sender.sendMessage(language.getPrefix() + language.getMessage("marry_accept_complete"));
                            partner.sendMessage(language.getPrefix() + language.getMessage("marry_accept_complete"));

                            Utils.getUtils().withdrawMoney(playerUUID, "marry_proposed_player");
                            Utils.getUtils().withdrawMoney(partnerUUID, "marry_proposing_player");

                            LunaticFamily.marryRequests.remove(playerUUID);
                            LunaticFamily.marryPriestRequests.remove(partnerUUID);
                            LunaticFamily.marryPriest.remove(partnerUUID);

                            playerFam.marry(partnerFam.getID());

                            for (String command : PluginConfig.successCommands.get("marry")) {
                                command = command.replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName());
                                Utils.getUtils().sendConsoleCommand(command);
                            }
                        }
                    }

                }

                else if (LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {

                    UUID partnerUUID = LunaticFamily.marryPriestRequests.get(playerUUID);
                    FamilyPlayer partnerFam = new FamilyPlayer(partnerUUID);
                    AbstractPlayerSender partner = AbstractSender.getPlayerSender(partnerUUID);

                    if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                        int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                        sender.sendMessage(language.getPrefix() + language.getMessage("marry_accept_too_many_children").replace("%partner%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                    } else {

                        UUID priestUUID = LunaticFamily.marryPriest.get(playerUUID);
                        AbstractPlayerSender priest = AbstractSender.getPlayerSender(priestUUID);

                        if (!Utils.getUtils().hasEnoughMoney(partnerUUID, "marry_proposing_player")) {
                            sender.sendMessage(language.getPrefix() + language.getMessage("player_not_enough_money").replace("%player%", partnerFam.getName()));
                        } else if (!Utils.getUtils().hasEnoughMoney(playerUUID, "marry_proposed_player")) {
                            sender.sendMessage(language.getPrefix() + language.getMessage("not_enough_money").replace("%player%", player.getName()));
                        } else {

                            LunaticFamily.marryPriestRequests.remove(playerUUID);
                            LunaticFamily.marryRequests.put(partnerUUID, playerUUID);
                            player.chat(language.getMessage("marry_yes"));


                            priest.chat(language.getMessage("marry_accept_request").replace("%player1%", partnerFam.getName()).replace("%player2%", playerFam.getName()));



                            partner.sendMessage(new ClickableDecisionMessage(
                                    "",
                                    language.getMessage("marry_yes"),
                                    "/family marry accept",
                                    language.getMessage("marry_no"),
                                    "/family marry deny"));
                        }
                    }
                }
            }
        }
        return true;
    }
}
