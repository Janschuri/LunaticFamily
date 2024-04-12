package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class AdoptSetSubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "set";
    private static final String permission = "lunaticfamily.admin.adopt";
    public AdoptSetSubcommand() {
        super(mainCommand, name, permission);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            boolean force = false;

            if (args.length > 3) {
                if (args[3].equalsIgnoreCase("force")) {
                    force = true;
                }
            }

            if (args.length < 3) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
            } else if (!Utils.playerExists(args[1]) && !force) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else if (!Utils.playerExists(args[2]) && !force) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[2]));
            } else if (args[1].equalsIgnoreCase(args[2])) {
                sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_set_same_player"));
            } else {


                String firstParentUUID;
                String childUUID;
                if (Utils.isUUID(args[1])) {
                    firstParentUUID = args[1];
                } else {
                    firstParentUUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                }
                if (Utils.isUUID(args[2])) {
                    childUUID = args[2];
                } else {
                    childUUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                }
                FamilyPlayer firstParentFam = new FamilyPlayer(firstParentUUID);
                FamilyPlayer childFam = new FamilyPlayer(childUUID);

                if (!firstParentFam.isMarried() && !Config.allowSingleAdopt) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_set_no_single_adopt").replace("%player%", firstParentFam.getName()));
                } else if (childFam.isAdopted()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_set_already_adopted").replace("%child%", childFam.getName()));
                } else if (firstParentFam.getChildrenAmount() > 1) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_limit").replace("%player%", firstParentFam.getName()));
                } else if (childFam.hasSibling()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_set_has_sibling").replace("%player%", childFam.getName()));
                } else if (childFam.hasSibling() && firstParentFam.getChildrenAmount() > 0) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_set_has_sibling_limit").replace("%player1%", childFam.getName()).replace("%player2%", firstParentFam.getName()));
                } else {

                    if (!firstParentFam.isMarried()) {
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_set_by_single").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                    } else {
                        FamilyPlayer secondParentFam = firstParentFam.getPartner();
                        sender.sendMessage(Language.prefix + Language.getMessage("admin_adopt_set").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                    }

                    LunaticFamily.adoptRequests.remove(childUUID);
                    firstParentFam.adopt(childFam.getID());
                }
            }
        }
    }
}
