package de.janschuri.lunaticfamily.commands.subcommands.adopt;

import de.janschuri.lunaticfamily.LunaticFamily;
import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.config.PluginConfig;
import de.janschuri.lunaticfamily.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.ClickableDecisionMessage;

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
            boolean confirm = false;
            boolean cancel = false;

            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("force")) {
                    force = true;
                }
            }

            if (args.length > 3) {
                if (args[3].equalsIgnoreCase("confirm")) {
                    confirm = true;
                }
                if (args[3].equalsIgnoreCase("cancel")) {
                    cancel = true;
                }
            }

            if (args.length < 2) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                return true;
            }

            if (cancel) {
                sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_set_cancel").replace("%parent%", args[0]).replace("%child%", args[1]));
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
                firstParent = AbstractSender.getPlayerSender(args[0]);
                firstParentUUID = firstParent.getUniqueId();
            }
            if (Utils.isUUID(args[1])) {
                childUUID = UUID.fromString(args[1]);
                child = AbstractSender.getPlayerSender(childUUID);
            } else {
                child = AbstractSender.getPlayerSender(args[1]);
                childUUID = child.getUniqueId();
            }


            if (!Utils.playerExists(firstParent) && !force) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[0]));
            } else if (!Utils.playerExists(child) && !force) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else if (args[0].equalsIgnoreCase(args[1])) {
                sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_set_same_player"));
            } else {

                FamilyPlayer firstParentFam = new FamilyPlayer(firstParentUUID);
                FamilyPlayer childFam = new FamilyPlayer(childUUID);

                if (firstParentFam.isFamilyMember(childFam.getID())) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_already_family").replace("%player1%", firstParentFam.getName()).replace("%player2%", child.getName()));
                    return true;
                }

                if (!firstParentFam.isMarried() && !PluginConfig.isAllowSingleAdopt()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_set_no_single_adopt").replace("%player%", firstParentFam.getName()));
                } else if (childFam.isAdopted()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_set_already_adopted").replace("%child%", childFam.getName()));
                } else if (firstParentFam.getChildrenAmount() > 1) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_adopt_limit").replace("%player%", firstParentFam.getName()));
                } else if (childFam.hasSibling() && !confirm) {

                    sender.sendMessage(new ClickableDecisionMessage(
                            language.getPrefix() + language.getMessage("admin_adopt_set_has_sibling").replace("%player%", childFam.getName()),
                            language.getMessage("confirm"),
                            "/family adopt set " + firstParentFam.getName() + " " + childFam.getName() + " force confirm",
                            language.getMessage("cancel"),
                            "/family adopt set " + firstParentFam.getName() + " " + child.getName() + "force cancel"));

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
