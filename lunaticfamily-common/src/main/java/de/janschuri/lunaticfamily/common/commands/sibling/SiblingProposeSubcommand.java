package de.janschuri.lunaticfamily.common.commands.sibling;

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

public class SiblingProposeSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "sibling";
    private static final String NAME = "propose";
    private static final String PERMISSION = "lunaticfamily.sibling";

    public SiblingProposeSubcommand() {
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

            if (playerFam.hasSibling()) {
                sender.sendMessage(getPrefix() + getMessage("sibling_propose_has_sibling").replace("%player%", playerFam.getName()));
                return true;
            } else if (playerFam.isAdopted()) {
                sender.sendMessage(getPrefix() + getMessage("sibling_propose_is_adopted").replace("%player%", playerFam.getName()));
                return true;
            } else if (args.length < 1) {
                sender.sendMessage(getPrefix() + getMessage("wrong_usage"));
                Logger.debugLog("SiblingProposeSubcommand: Wrong usage");
                return true;
            }

            String siblingName = args[0];

            UUID siblingUUID = PlayerDataTable.getUUID(siblingName);

            if (siblingUUID == null) {
                sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", siblingName));
                return true;
            }

            PlayerSender sibling = LunaticLib.getPlatform().getPlayerSender(siblingUUID);

            if (!sibling.isOnline()) {
                sender.sendMessage(getPrefix() + getMessage("player_offline").replace("%player%", sibling.getName()));
                return true;
            }

            if (!Utils.isPlayerOnRegisteredServer(sibling.getUniqueId())) {
                player.sendMessage(getPrefix() + getMessage("player_not_on_whitelisted_server").replace("%player%", sibling.getName().replace("%server%", sibling.getServerName())));
                return true;
            }

                FamilyPlayerImpl siblingFam = new FamilyPlayerImpl(siblingUUID);
                if (playerFam.getId() == siblingFam.getId()) {
                    sender.sendMessage(getPrefix() + getMessage("sibling_propose_self_request"));
                } else if (playerFam.isFamilyMember(siblingFam.getId())) {
                    sender.sendMessage(getPrefix() + getMessage("sibling_propose_family_request").replace("%player%", siblingFam.getName()));
                } else if (siblingFam.isFamilyMember(playerFam.getId())) {
                    sender.sendMessage(getPrefix() + getMessage("sibling_propose_family_request").replace("%player%", siblingFam.getName()));
                } else if (siblingFam.isAdopted()) {
                    sender.sendMessage(getPrefix() + getMessage("sibling_propose_sibling_is_adopted").replace("%player%", siblingFam.getName()));
                } else if (LunaticFamily.siblingRequests.containsKey(siblingUUID)) {
                    sender.sendMessage(getPrefix() + getMessage("sibling_propose_open_request").replace("%player%", siblingFam.getName()));
                } else if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "sibling_proposing_player")) {
                    sender.sendMessage(getPrefix() + getMessage("not_enough_money"));
                } else {

                    if (!player.isSameServer(sibling.getUniqueId()) && LunaticFamily.getConfig().getSiblingProposeRange() >= 0) {
                        sender.sendMessage(getPrefix() + getMessage("player_not_same_server").replace("%player%", sibling.getName()));
                        return true;
                    }

                    if (!player.isInRange(sibling.getUniqueId(), LunaticFamily.getConfig().getSiblingProposeRange())) {
                        player.sendMessage(getPrefix() + getMessage("player_too_far_away").replace("%player%", sibling.getName()));
                        return true;
                    }

                    sibling.sendMessage(Utils.getClickableDecisionMessage(
                            getPrefix() + getMessage("sibling_propose_request").replace("%player%", siblingFam.getName()),
                            getMessage("accept"),
                            "/family sibling accept",
                            getMessage("deny"),
                            "/family sibling deny"));

                    LunaticFamily.siblingRequests.put(siblingUUID, playerUUID);

                    sender.sendMessage(getPrefix() + getMessage("sibling_propose_request_sent").replace("%player%", siblingFam.getName()));

                    Runnable runnable = () -> {
                        if (LunaticFamily.siblingRequests.containsKey(siblingUUID)) {
                            LunaticFamily.siblingRequests.remove(siblingUUID);
                            sibling.sendMessage(getPrefix() + getMessage("sibling_propose_request_expired").replace("%player%", player.getName()));

                            player.sendMessage(getPrefix() + getMessage("sibling_propose_request_sent_expired").replace("%player%", sibling.getName()));
                        }
                    };

                    Utils.scheduleTask(runnable, 30L, TimeUnit.SECONDS);
                }

        }
        return true;
    }
}
