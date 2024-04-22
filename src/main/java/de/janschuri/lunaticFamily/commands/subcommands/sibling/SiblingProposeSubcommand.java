package de.janschuri.lunaticFamily.commands.subcommands.sibling;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.utils.ClickableDecisionMessage;
import de.janschuri.lunaticFamily.senders.CommandSender;
import de.janschuri.lunaticFamily.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;

import java.util.TimerTask;
import java.util.UUID;

public class SiblingProposeSubcommand extends Subcommand {
    private static final String mainCommand = "sibling";
    private static final String name = "propose";
    private static final String permission = "lunaticfamily.sibling";

    public SiblingProposeSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof PlayerCommandSender)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            PlayerCommandSender player = (PlayerCommandSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (playerFam.hasSibling()) {
                sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_has_sibling").replace("%player%", playerFam.getName()));
                return true;
            } else if (playerFam.isAdopted()) {
                sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_is_adopted").replace("%player%", playerFam.getName()));
                return true;
            } else if (args.length < 2) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                return true;
            }

            PlayerCommandSender sibling = player.getPlayerCommandSender(args[1]);

            if (!sibling.exists()) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                return true;
            }
            if (sibling.isOnline()) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", sibling.getName()));
                return true;
            }

            if (!Utils.getUtils().isPlayerOnWhitelistedServer(sibling.getUniqueId())) {
                player.sendMessage(Language.prefix + Language.getMessage("player_not_on_whitelisted_server").replace("%player%", sibling.getName().replace("%server%", sibling.getServerName())));
                return true;
            }

                UUID siblingUUID = sibling.getUniqueId();
                FamilyPlayer siblingFam = new FamilyPlayer(siblingUUID);
                if (playerFam.getID() == siblingFam.getID()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_self_request"));
                } else if (playerFam.isFamilyMember(siblingFam.getID())) {
                    sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_family_request").replace("%player%", siblingFam.getName()));
                } else if (siblingFam.isFamilyMember(playerFam.getID())) {
                    sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_family_request").replace("%player%", siblingFam.getName()));
                } else if (siblingFam.isAdopted()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_sibling_is_adopted").replace("%player%", siblingFam.getName()));
                } else if (LunaticFamily.siblingRequests.containsKey(siblingUUID)) {
                    sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_open_request").replace("%player%", siblingFam.getName()));
                } else if (!Utils.getUtils().hasEnoughMoney(playerUUID, "sibling_proposing_player")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                } else {

                    if (!player.isSameServer(sibling.getUniqueId()) && PluginConfig.siblingProposeRange >= 0) {
                        sender.sendMessage(Language.prefix + Language.getMessage("player_not_same_server").replace("%player%", sibling.getName()));
                        return true;
                    }

                    if (!player.isInRange(sibling.getUniqueId(), PluginConfig.siblingProposeRange)) {
                        player.sendMessage(Language.prefix + Language.getMessage("player_too_far_away").replace("%player%", sibling.getName()));
                        return true;
                    }

                    sibling.sendMessage(new ClickableDecisionMessage(
                            Language.getMessage("sibling_propose_request").replace("%player%", siblingFam.getName()),
                            Language.getMessage("accept"),
                            "/family sibling accept",
                            Language.getMessage("deny"),
                            "/family sibling deny"));

                    LunaticFamily.siblingRequests.put(siblingUUID, playerUUID);

                    sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_request_sent").replace("%player%", siblingFam.getName()));

                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            if (LunaticFamily.siblingRequests.containsKey(siblingUUID)) {
                                LunaticFamily.siblingRequests.remove(siblingUUID);
                                sibling.sendMessage(Language.prefix + Language.getMessage("sibling_propose_request_expired").replace("%player%", player.getName()));

                                player.sendMessage(Language.prefix + Language.getMessage("sibling_propose_request_sent_expired").replace("%player%", sibling.getName()));
                            }
                        }
                    };

                    Utils.getTimer().schedule(task, 30 * 1000);
                }

        }
        return true;
    }
}
