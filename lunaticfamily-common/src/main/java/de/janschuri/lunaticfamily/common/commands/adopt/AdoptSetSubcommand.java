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

public class AdoptSetSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "adopt";
    private static final String NAME = "set";
    private static final String PERMISSION = "lunaticfamily.admin.adopt";
    public AdoptSetSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
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
                sender.sendMessage(getPrefix() + getMessage("wrong_usage"));
                Logger.debugLog("AdoptSetSubcommand: Wrong usage");
                return true;
            }

            if (cancel) {
                sender.sendMessage(getPrefix() + getMessage("admin_adopt_set_cancel").replace("%parent%", args[0]).replace("%child%", args[1]));
                return true;
            }

            String parentArg = args[0];
            String childArg = args[1];

            UUID firstParentUUID;
            UUID childUUID;


            if (Utils.isUUID(parentArg)) {
                firstParentUUID = UUID.fromString(parentArg);

                if (PlayerDataTable.getID(firstParentUUID) < 0) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", parentArg));
                    return true;
                }
            } else {
                firstParentUUID = PlayerDataTable.getUUID(parentArg);

                if (firstParentUUID == null) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", parentArg));
                    return true;
                }
            }

            if (Utils.isUUID(childArg)) {
                childUUID = UUID.fromString(childArg);

                if (PlayerDataTable.getID(childUUID) < 0) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", childArg));
                    return true;
                }
            } else {
                childUUID = PlayerDataTable.getUUID(childArg);

                if (childUUID == null) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", childArg));
                    return true;
                }
            }

            PlayerSender firstParent = LunaticLib.getPlatform().getPlayerSender(firstParentUUID);
            PlayerSender child = LunaticLib.getPlatform().getPlayerSender(childUUID);


            if (args[0].equalsIgnoreCase(args[1])) {
                sender.sendMessage(getPrefix() + getMessage("admin_adopt_set_same_player"));
            } else {

                FamilyPlayerImpl firstParentFam = new FamilyPlayerImpl(firstParentUUID);
                FamilyPlayerImpl childFam = new FamilyPlayerImpl(childUUID);

                if (firstParentFam.isFamilyMember(childFam.getId())) {
                    sender.sendMessage(getPrefix() + getMessage("admin_already_family").replace("%player1%", firstParentFam.getName()).replace("%player2%", child.getName()));
                    return true;
                }

                if (!firstParentFam.isMarried() && !LunaticFamily.getConfig().isAllowSingleAdopt()) {
                    sender.sendMessage(getPrefix() + getMessage("admin_adopt_set_no_single_adopt").replace("%player%", firstParentFam.getName()));
                } else if (childFam.isAdopted()) {
                    sender.sendMessage(getPrefix() + getMessage("admin_adopt_set_already_adopted").replace("%child%", childFam.getName()));
                } else if (firstParentFam.getChildrenAmount() > 1) {
                    sender.sendMessage(getPrefix() + getMessage("admin_adopt_limit").replace("%player%", firstParentFam.getName()));
                } else if (childFam.hasSibling() && !confirm) {

                    sender.sendMessage(Utils.getClickableDecisionMessage(
                            getPrefix() + getMessage("admin_adopt_set_has_sibling").replace("%player%", childFam.getName()),
                            getMessage("confirm"),
                            "/family adopt set " + firstParentFam.getName() + " " + childFam.getName() + " force confirm",
                            getMessage("cancel"),
                            "/family adopt set " + firstParentFam.getName() + " " + child.getName() + "force cancel"));

                } else if (childFam.hasSibling() && firstParentFam.getChildrenAmount() > 0) {
                    sender.sendMessage(getPrefix() + getMessage("admin_adopt_set_has_sibling_limit").replace("%player1%", childFam.getName()).replace("%player2%", firstParentFam.getName()));
                } else {

                    if (!firstParentFam.isMarried()) {
                        sender.sendMessage(getPrefix() + getMessage("admin_adopt_set_by_single").replace("%child%", childFam.getName()).replace("%parent%", firstParentFam.getName()));
                    } else {
                        FamilyPlayerImpl secondParentFam = firstParentFam.getPartner();
                        sender.sendMessage(getPrefix() + getMessage("admin_adopt_set").replace("%child%", childFam.getName()).replace("%parent1%", firstParentFam.getName()).replace("%parent2%", secondParentFam.getName()));
                    }

                    LunaticFamily.adoptRequests.remove(childUUID);
                    firstParentFam.adopt(childFam.getId());
                }
            }
        }
        return true;
    }
}
