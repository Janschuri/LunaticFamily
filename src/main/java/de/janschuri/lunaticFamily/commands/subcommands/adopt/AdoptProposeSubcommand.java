package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.utils.ClickableDecisionMessage;
import de.janschuri.lunaticFamily.senders.CommandSender;
import de.janschuri.lunaticFamily.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;

import java.util.TimerTask;
import java.util.UUID;
//import org.bukkit.Bukkit;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//import org.bukkit.scheduler.BukkitRunnable;

public class AdoptProposeSubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "propose";
    private static final String permission = "lunaticfamily.adopt";
    public AdoptProposeSubcommand() {
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

            boolean confirm = false;
            boolean cancel = false;

            if (args.length > 3) {
                if (args[3].equalsIgnoreCase("confirm")) {
                    confirm = true;
                }
                if (args[3].equalsIgnoreCase("cancel")) {
                    cancel = true;
                }
            }


            if (args.length < 2) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                return true;
            } else if (cancel) {
                sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_cancel").replace("%player%", args[2]));
                return true;
            } else if (!playerFam.isMarried() && !PluginConfig.allowSingleAdopt) {
                sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_no_single_adopt"));
                return true;
            } else if (playerFam.getChildrenAmount() > 1) {
                sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_limit"));
                return true;
            }

            PlayerCommandSender child = player.getPlayerCommandSender(args[1]);

            if (!child.exists()) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                return true;
            }
            if (!child.isOnline()) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", args[1]));
                return true;
            }

            if (!Utils.getUtils().isPlayerOnWhitelistedServer(child.getUniqueId())) {
                player.sendMessage(Language.prefix + Language.getMessage("player_not_on_whitelisted_server").replace("%player%", child.getName().replace("%server%", child.getServerName())));
                return true;
            }

            if (!Utils.getUtils().hasEnoughMoney(playerUUID, "adopt_parent")) {
                sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
            }

                if (!player.isSameServer(child.getUniqueId()) && PluginConfig.adoptProposeRange >= 0) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_same_server").replace("%player%", child.getName()));
                    return true;
                }

                if (!player.isInRange(child.getUniqueId(), PluginConfig.adoptProposeRange)) {
                    player.sendMessage(Language.prefix + Language.getMessage("player_too_far_away").replace("%player%", child.getName()));
                    return true;
                }

                UUID childUUID = child.getUniqueId();
                FamilyPlayer childFam = new FamilyPlayer(childUUID);

                if (args[1].equalsIgnoreCase(player.getName())) {
                    player.sendMessage(Language.prefix + Language.getMessage("adopt_propose_self_request"));
                } else if (playerFam.isFamilyMember(childFam.getID())) {
                    player.sendMessage(Language.prefix + Language.getMessage("marry_propose_family_request").replace("%player%", childFam.getName()));
                } else if (LunaticFamily.adoptRequests.containsKey(childUUID)) {
                    player.sendMessage(Language.prefix + Language.getMessage("adopt_propose_open_request").replace("%player%", childFam.getName()));
                } else if (childFam.getParents() == null) {
                    player.sendMessage(Language.prefix + Language.getMessage("adopt_propose_already_adopted").replace("%player%", childFam.getName()));
                } else if (childFam.hasSibling() && !confirm) {
                    player.sendMessage(new ClickableDecisionMessage(
                            Language.getMessage("adopt_propose_has_sibling").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()),
                            Language.getMessage("confirm"),
                            "/family adopt propose " + child.getName() + " confirm",
                            Language.getMessage("cancel"),
                            "/family adopt propose " + child.getName() + " cancel"));

                } else if (childFam.hasSibling() && playerFam.getChildrenAmount() > 0) {
                    sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_has_sibling_limit").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()));
                } else {
                    if (playerFam.isMarried()) {
                        child.sendMessage(new ClickableDecisionMessage(
                                Language.getMessage("adopt_propose_request").replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getPartner().getName()),
                                Language.getMessage("accept"),
                                "/family adopt accept",
                                Language.getMessage("deny"),
                                "/family adopt deny"));
                    } else {
                        child.sendMessage(new ClickableDecisionMessage(
                                Language.getMessage("adopt_propose_request_by_single").replace("%player%", playerFam.getName()),
                                Language.getMessage("accept"),
                                "/family adopt accept",
                                Language.getMessage("deny"),
                                "/family adopt deny"));
                    }
                    LunaticFamily.adoptRequests.put(childUUID, playerUUID);
                    sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_request_sent").replace("%player%", childFam.getName()));

                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            if (LunaticFamily.adoptRequests.containsKey(childUUID.toString())) {
                                LunaticFamily.adoptRequests.remove(childUUID.toString());
                                FamilyPlayer playerFam = new FamilyPlayer(playerUUID);
                                FamilyPlayer childFam = new FamilyPlayer(childUUID);
                                PlayerCommandSender child = player.getPlayerCommandSender(childUUID);
                                if (playerFam.isMarried()) {
                                    FamilyPlayer partnerFam = playerFam.getPartner();
                                    child.sendMessage(Language.prefix + Language.getMessage("adopt_propose_request_expired").replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()));
                                } else {
                                    child.sendMessage(Language.prefix + Language.getMessage("adopt_propose_request_by_single_expired").replace("%player%", playerFam.getName()));
                                }
                                player.sendMessage(Language.prefix + Language.getMessage("adopt_request_sent_expired").replace("%player%", childFam.getName()));
                            }
                        }
                    };

                    Utils.getTimer().schedule(task, 30 * 1000);
                }
        }
        return true;
    }
}
