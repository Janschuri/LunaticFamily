package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class AdoptSetSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "adopt";
    private static final String NAME = "set";
    private static final String PERMISSION = "lunaticfamily.admin.adopt";
    public AdoptSetSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }

    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            boolean force = false;

            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("force")) {
                    force = true;
                }
            }

            if (args.length < 2) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                return true;
            }

            AbstractPlayerSender firstParent;
            AbstractPlayerSender child;
            UUID firstParentUUID;
            UUID childUUID;

            if (Utils.isUUID(args[0])) {
                firstParentUUID = UUID.fromString(args[0]);
                firstParent = AbstractSender.getPlayerSender(firstParentUUID);
            } else {
                force = false;
                firstParent = AbstractSender.getPlayerSender(args[0]);
                firstParentUUID = firstParent.getUniqueId();
            }
            if (Utils.isUUID(args[1])) {
                childUUID = UUID.fromString(args[1]);
                child = AbstractSender.getPlayerSender(childUUID);
            } else {
                force = false;
                child = AbstractSender.getPlayerSender(args[1]);
                childUUID = child.getUniqueId();
            }


            if (!firstParent.exists() && !force) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[0]));
            } else if (!child.exists() && !force) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else if (args[0].equalsIgnoreCase(args[1])) {
                sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_set_same_player"));
            } else {

                FamilyPlayer firstParentFam = new FamilyPlayer(firstParentUUID);
                FamilyPlayer childFam = new FamilyPlayer(childUUID);

                if (!firstParentFam.isMarried() && !PluginConfig.isAllowSingleAdopt()) {
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
