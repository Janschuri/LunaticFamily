package de.janschuri.lunaticFamily.commands.subcommands.sibling;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.ClickableDecisionMessage;
import de.janschuri.lunaticFamily.commands.senders.CommandSender;
import de.janschuri.lunaticFamily.commands.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;

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
            } else if (sibling.isOnline()) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", sibling.getName()));
            } else {
                UUID siblingUUID = sibling.getUniqueId();
                FamilyPlayer siblingFam = sibling.getFamilyPlayer();
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
                } else if (!player.hasEnoughMoney("sibling_proposing_player")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
                } else {

                    if (!player.isSameServer(sibling.getUniqueId())) {
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
                            "/lunaticfamily:sibling accept",
                            Language.getMessage("deny"),
                            "/lunaticfamily:sibling deny"));

                    LunaticFamily.siblingRequests.put(siblingUUID.toString(), playerUUID.toString());

                    sender.sendMessage(Language.prefix + Language.getMessage("sibling_propose_request_sent").replace("%player%", siblingFam.getName()));

                    player.sendSiblingRequest(siblingUUID);
                }
            }
        }
        return true;
    }
}
