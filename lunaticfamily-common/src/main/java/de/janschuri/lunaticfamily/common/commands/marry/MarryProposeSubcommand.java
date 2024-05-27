package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MarryProposeSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "propose";
    private static final String PERMISSION = "lunaticfamily.marry";

    public MarryProposeSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getPrefix() + getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else {
            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

            if (args.length < 1) {
                sender.sendMessage(getPrefix() + getMessage("wrong_usage"));
                Logger.debugLog("MarryProposeSubcommand: Wrong usage");
                return true;
            } else if (playerFam.getName().equalsIgnoreCase(args[0])) {
                sender.sendMessage(getPrefix() + getMessage("marry_propose_self_request"));
                return true;
            } else if (playerFam.isMarried()) {
                sender.sendMessage(getPrefix() + getMessage("marry_propose_already_married").replace("%player%", playerFam.getName()));
                return true;
            }

            String partnerName = args[0];

            UUID partnerUUID = PlayerDataTable.getUUID(partnerName);

            if (partnerUUID == null) {
                sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", partnerName));
                return true;
            }

            PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

            if (!partner.isOnline()) {
                sender.sendMessage(getPrefix() + getMessage("player_offline").replace("%player%", partner.getName()));
                return true;
            }

            if (!Utils.isPlayerOnRegisteredServer(partner.getUniqueId())) {
                player.sendMessage(getPrefix() + getMessage("player_not_on_whitelisted_server").replace("%player%", partner.getName().replace("%server%", partner.getServerName())));
                return true;
            }

            if (!player.isSameServer(partnerUUID) && LunaticFamily.getConfig().getMarryProposeRange() >= 0) {
                sender.sendMessage(getPrefix() + getMessage("player_not_same_server").replace("%player%", partner.getName()));
                return true;
            }

            if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "marry_proposing_player")) {
                sender.sendMessage(getPrefix() + getMessage("not_enough_money"));
                return true;
            }

                FamilyPlayerImpl partnerFam = new FamilyPlayerImpl(partnerUUID);
                if (playerFam.isFamilyMember(partnerFam.getId())) {
                    sender.sendMessage(getPrefix() + getMessage("marry_propose_family_request").replace("%player%", partnerFam.getName()));
                } else if (partnerFam.isFamilyMember(playerFam.getId())) {
                    sender.sendMessage(getPrefix() + getMessage("marry_propose_family_request").replace("%player%", partnerFam.getName()));
                } else if (playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() > 2) {
                    int amountDiff = playerFam.getChildrenAmount() + partnerFam.getChildrenAmount() - 2;
                    sender.sendMessage(getPrefix() + getMessage("marry_propose_too_many_children").replace("%player%", partnerFam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                } else if (LunaticFamily.marryRequests.containsKey(partner.getUniqueId()) || LunaticFamily.marryPriest.containsKey(partner.getUniqueId())) {
                    sender.sendMessage(getPrefix() + getMessage("marry_propose_open_request").replace("%player%", partnerFam.getName()));
                } else if (partnerFam.isMarried()) {
                    sender.sendMessage(getPrefix() + getMessage("marry_propose_player_already_married").replace("%player%", partnerFam.getName()));
                } else {

                    if (!player.isInRange(partner.getUniqueId(), LunaticFamily.getConfig().getMarryProposeRange())) {
                        player.sendMessage(getPrefix() + getMessage("player_too_far_away").replace("%player%", partner.getName()));
                        return true;
                    }

                    partner.sendMessage(Utils.getClickableDecisionMessage(
                            getPrefix() + getMessage("marry_propose_request").replace("%player1%", partnerFam.getName()).replace("%player2%", playerFam.getName()),
                            getMessage("marry_yes"),
                            "/family marry accept",
                            getMessage("marry_no"),
                            "/family marry deny"));


                    LunaticFamily.marryRequests.put(partnerUUID, playerUUID);

                    sender.sendMessage(getPrefix() + getMessage("marry_propose_request_sent").replace("%player%", partnerFam.getName()));

                    Runnable runnable = () -> {
                        if (LunaticFamily.marryRequests.containsKey(partnerUUID)) {
                            LunaticFamily.marryRequests.remove(partnerUUID);
                            player.sendMessage(getPrefix() + getMessage("marry_propose_request_sent_expired").replace("%player%", partner.getName()));
                            partner.sendMessage(getPrefix() + getMessage("marry_propose_request_expired").replace("%player%", player.getName()));
                        }
                    };

                    Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);

                }

        }
        return true;
    }
}
