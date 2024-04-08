package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AdoptProposeSubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "propose";
    private static final String permission = "lunaticfamily.adopt";
    public AdoptProposeSubcommand() {
        super(mainCommand, name, permission);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            Player player = (Player) sender;
            String playerUUID = player.getUniqueId().toString();
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
            } else if (cancel) {
                sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_cancel").replace("%player%", args[2]));
            } else if (!playerFam.isMarried() && !PluginConfig.allowSingleAdopt) {
                sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_no_single_adopt"));
            } else if (playerFam.getChildrenAmount() > 1) {
                sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_limit"));
            } else if (!Utils.playerExists(args[1])) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else if (Bukkit.getPlayer(args[1]) == null) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", args[1]));
            } else if (!playerFam.hasEnoughMoney("adopt_parent")) {
                sender.sendMessage(Language.prefix + Language.getMessage("not_enough_money"));
            } else {
                String childUUID = Utils.getUUID(args[1]);
                FamilyPlayer childFam = new FamilyPlayer(childUUID);

                if (args[1].equalsIgnoreCase(player.getName())) {
                    player.sendMessage(Language.prefix + Language.getMessage("adopt_propose_self_request"));
                } else if (playerFam.isFamilyMember(childFam.getID())) {
                    sender.sendMessage(Language.prefix + Language.getMessage("marry_propose_family_request").replace("%player%", childFam.getName()));
                } else if (LunaticFamily.adoptRequests.containsKey(childUUID)) {
                    sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_open_request").replace("%player%", childFam.getName()));
                } else if (childFam.getParents() == null) {
                    sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_already_adopted").replace("%player%", childFam.getName()));
                } else if (childFam.hasSibling() && !confirm) {
                    sender.sendMessage(Utils.createClickableMessage(
                            Language.getMessage("adopt_propose_has_sibling").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()),
                            Language.getMessage("confirm"),
                            "/lunaticfamily:adopt propose " + Utils.getName(args[1]) + " confirm",
                            Language.getMessage("cancel"),
                            "/lunaticfamily:adopt propose " + Utils.getName(args[1]) + " cancel"));

                } else if (childFam.hasSibling() && playerFam.getChildrenAmount() > 0) {
                    sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_has_sibling_limit").replace("%player1%", childFam.getName()).replace("%player2%", childFam.getSibling().getName()));
                } else {
                    if (playerFam.isMarried()) {
                        childFam.sendMessage(Utils.createClickableMessage(
                                Language.getMessage("adopt_propose_request").replace("%player1%", playerFam.getName()).replace("%player2%", playerFam.getPartner().getName()),
                                Language.getMessage("accept"),
                                "/lunaticfamily:adopt accept",
                                Language.getMessage("deny"),
                                "/lunaticfamily:adopt deny"));
                    } else {
                        childFam.sendMessage(Utils.createClickableMessage(
                                Language.getMessage("adopt_propose_request_by_single").replace("%player%", playerFam.getName()),
                                Language.getMessage("accept"),
                                "/lunaticfamily:adopt accept",
                                Language.getMessage("deny"),
                                "/lunaticfamily:adopt deny"));
                    }
                    LunaticFamily.adoptRequests.put(childUUID, playerUUID);
                    sender.sendMessage(Language.prefix + Language.getMessage("adopt_propose_request_sent").replace("%player%", childFam.getName()));

                    new BukkitRunnable() {
                        public void run() {
                            if (LunaticFamily.adoptRequests.containsKey(childUUID)) {
                                LunaticFamily.adoptRequests.remove(childUUID);
                                if (playerFam.isMarried()) {
                                    FamilyPlayer partnerFam = playerFam.getPartner();
                                    childFam.sendMessage(Language.prefix + Language.getMessage("adopt_propose_request_expired").replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()));
                                } else {
                                    childFam.sendMessage(Language.prefix + Language.getMessage("adopt_propose_request_by_single_expired").replace("%player%", playerFam.getName()));
                                }
                                sender.sendMessage(Language.prefix + Language.getMessage("adopt_request_sent_expired").replace("%player%", childFam.getName()));
                            }
                        }
                    }.runTaskLater(LunaticFamily.getInstance(), 600L);
                }
            }
        }
    }
}
