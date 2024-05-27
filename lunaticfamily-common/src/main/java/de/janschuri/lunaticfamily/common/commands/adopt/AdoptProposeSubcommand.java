package de.janschuri.lunaticfamily.common.commands.adopt;

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

public class AdoptProposeSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "adopt";
    private static final String NAME = "propose";
    private static final String PERMISSION = "lunaticfamily.adopt";
    public AdoptProposeSubcommand() {
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
                sender.sendMessage(getPrefix() + getMessage("wrong_usage"));
                Logger.debugLog("AdoptProposeSubcommand: Wrong usage");
                return true;
            } else if (cancel) {
                sender.sendMessage(getPrefix() + getMessage("adopt_propose_cancel").replace("%player%", args[2]));
                return true;
            } else if (!playerFam.isMarried() && !LunaticFamily.getConfig().isAllowSingleAdopt()) {
                sender.sendMessage(getPrefix() + getMessage("adopt_propose_no_single_adopt"));
                return true;
            } else if (playerFam.getChildrenAmount() > 1) {
                sender.sendMessage(getPrefix() + getMessage("adopt_propose_limit"));
                return true;
            }

            String childName = args[0];

            UUID childUUID = PlayerDataTable.getUUID(childName);

            if (childUUID == null) {
                player.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", childName));
                return true;
            }

            PlayerSender child = LunaticLib.getPlatform().getPlayerSender(childUUID);


            if (!child.isOnline()) {
                sender.sendMessage(getPrefix() + getMessage("player_offline").replace("%player%", args[0]));
                return true;
            }

            if (!Utils.isPlayerOnRegisteredServer(child.getUniqueId())) {
                player.sendMessage(getPrefix() + getMessage("player_not_on_whitelisted_server").replace("%player%", child.getName().replace("%server%", child.getServerName())));
                return true;
            }

            if (!Utils.hasEnoughMoney(player.getServerName(), playerUUID, "adopt_parent")) {
                sender.sendMessage(getPrefix() + getMessage("not_enough_money"));
                return true;
            }

                if (!player.isSameServer(child.getUniqueId()) && LunaticFamily.getConfig().getAdoptProposeRange() >= 0) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_same_server").replace("%player%", child.getName()));
                    return true;
                }

                if (!player.isInRange(child.getUniqueId(), LunaticFamily.getConfig().getAdoptProposeRange())) {
                    player.sendMessage(getPrefix() + getMessage("player_too_far_away").replace("%player%", child.getName()));
                    return true;
                }

                FamilyPlayerImpl childFam = new FamilyPlayerImpl(childUUID);

                if (args[0].equalsIgnoreCase(player.getName())) {
                    player.sendMessage(getPrefix() + getMessage("adopt_propose_self_request"));
                } else if (playerFam.isFamilyMember(childFam.getId())) {
                    player.sendMessage(getPrefix() + getMessage("marry_propose_family_request").replace("%player%", childFam.getName()));
                } else if (LunaticFamily.adoptRequests.containsKey(childUUID)) {
                    player.sendMessage(getPrefix() + getMessage("adopt_propose_open_request").replace("%player%", childFam.getName()));
                } else if (childFam.getParents() == null) {
                    player.sendMessage(getPrefix() + getMessage("adopt_propose_already_adopted").replace("%player%", childFam.getName()));
                } else if (childFam.hasSibling() && !confirm) {
                    player.sendMessage(Utils.getClickableDecisionMessage(
                            getPrefix() + getMessage("adopt_propose_has_sibling").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()),
                            getMessage("confirm"),
                            "/family adopt propose " + child.getName() + " confirm",
                            getMessage("cancel"),
                            "/family adopt propose " + child.getName() + " cancel"));

                } else if (childFam.hasSibling() && playerFam.getChildrenAmount() > 0) {
                    sender.sendMessage(getPrefix() + getMessage("adopt_propose_has_sibling_limit").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()));
                } else {
                    if (playerFam.isMarried()) {
                        child.sendMessage(Utils.getClickableDecisionMessage(
                                getPrefix() + getMessage("adopt_propose_request").replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getPartner().getName()),
                                getMessage("accept"),
                                "/family adopt accept",
                                getMessage("deny"),
                                "/family adopt deny"));
                    } else {
                        child.sendMessage(Utils.getClickableDecisionMessage(
                                getPrefix() + getMessage("adopt_propose_request_by_single").replace("%player%", playerFam.getName()),
                                getMessage("accept"),
                                "/family adopt accept",
                                getMessage("deny"),
                                "/family adopt deny"));
                    }
                    LunaticFamily.adoptRequests.put(childUUID, playerUUID);
                    sender.sendMessage(getPrefix() + getMessage("adopt_propose_request_sent").replace("%player%", childFam.getName()));

                    Runnable runnable = () -> {
                        if (LunaticFamily.adoptRequests.containsKey(childUUID)) {
                            LunaticFamily.adoptRequests.remove(childUUID);
                            if (playerFam.isMarried()) {
                                FamilyPlayerImpl partnerFam = playerFam.getPartner();
                                child.sendMessage(getPrefix() + getMessage("adopt_propose_request_expired").replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()));
                            } else {
                                child.sendMessage(getPrefix() + getMessage("adopt_propose_request_by_single_expired").replace("%player%", playerFam.getName()));
                            }
                            player.sendMessage(getPrefix() + getMessage("adopt_request_sent_expired").replace("%player%", childFam.getName()));
                        }
                    };

                    Utils.scheduleTask(runnable, 30, TimeUnit.SECONDS);
                }
        }
        return true;
    }
}
