package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class AdoptSetSubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "set";
    private static final String permission = "lunaticfamily.admin.adopt";
    public AdoptSetSubcommand() {
        super(mainCommand, name, permission);
    }

    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            boolean force = false;

            if (args.length > 3) {
                if (args[3].equalsIgnoreCase("force")) {
                    force = true;
                }
            }

            if (args.length < 3) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                return true;
            }

            AbstractPlayerSender firstParent;
            AbstractPlayerSender child;
            UUID firstParentUUID;
            UUID childUUID;

            if (Utils.isUUID(args[1])) {
                firstParentUUID = UUID.fromString(args[1]);
                firstParent = sender.getPlayerCommandSender(firstParentUUID);
            } else {
                force = false;
                firstParent = sender.getPlayerCommandSender(args[1]);
                firstParentUUID = firstParent.getUniqueId();
            }
            if (Utils.isUUID(args[2])) {
                childUUID = UUID.fromString(args[2]);
                child = sender.getPlayerCommandSender(childUUID);
            } else {
                force = false;
                child = sender.getPlayerCommandSender(args[2]);
                childUUID = child.getUniqueId();
            }


            if (!firstParent.exists() && !force) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else if (!child.exists() && !force) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[2]));
            } else if (args[1].equalsIgnoreCase(args[2])) {
                sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_set_same_player"));
            } else {

                FamilyPlayer firstParentFam = new FamilyPlayer(firstParentUUID);
                FamilyPlayer childFam = new FamilyPlayer(childUUID);

                if (!firstParentFam.isMarried() && !PluginConfig.allowSingleAdopt) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_set_no_single_adopt").replace("%player%", firstParentFam.getName()));
                } else if (childFam.isAdopted()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_set_already_adopted").replace("%child%", childFam.getName()));
                } else if (firstParentFam.getChildrenAmount() > 1) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_limit").replace("%player%", firstParentFam.getName()));
                } else if (childFam.hasSibling()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_set_has_sibling").replace("%player%", childFam.getName()));
                } else if (childFam.hasSibling() && firstParentFam.getChildrenAmount() > 0) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_set_has_sibling_limit").replace("%player1%", childFam.getName()).replace("%player2%", firstParentFam.getName()));
                } else {

                    if (!firstParentFam.isMarried()) {
                        sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_set_by_single").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                    } else {
                        FamilyPlayer secondParentFam = firstParentFam.getPartner();
                        sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_set").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                    }

                    LunaticFamily.adoptRequests.remove(childUUID);
                    firstParentFam.adopt(childFam.getID());
                }
            }
        }
        return true;
    }
}
