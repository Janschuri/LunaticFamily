package de.janschuri.lunaticfamily.common.commands.adopt;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.AdoptSubcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;

public class AdoptSetSubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey noSingleAdoptMK = new CommandMessageKey(this,"no_single_adopt");
    private final CommandMessageKey adoptLimitMK = new CommandMessageKey(this,"adopt_limit");
    private final CommandMessageKey samePlayerMK = new CommandMessageKey(this,"same_player");
    private final CommandMessageKey alreadyAdoptedMK = new CommandMessageKey(this,"already_adopted");
    private final CommandMessageKey hasSiblingMK = new CommandMessageKey(this,"has_sibling");
    private final CommandMessageKey hasSiblingLimitMK = new CommandMessageKey(this,"has_sibling_limit");
    private final CommandMessageKey cancelMK = new CommandMessageKey(this,"cancel");
    private final CommandMessageKey setMK = new CommandMessageKey(this,"set");
    private final CommandMessageKey setBySingleMK = new CommandMessageKey(this,"set_by_single");
    private final CommandMessageKey sameFamilyMK = new CommandMessageKey(this,"same_family");



    @Override
    public String getPermission() {
        return "lunaticfamily.admin.adopt";
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public AdoptSubcommand getParentCommand() {
        return new AdoptSubcommand();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
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
                sender.sendMessage(getMessage(WRONG_USAGE_MK));
                Logger.debugLog("AdoptSetSubcommand: Wrong usage");
                return true;
            }

            if (cancel) {
                sender.sendMessage(getMessage(cancelMK)
                        .replaceText(getTextReplacementConfig("%parent%", args[0]))
                        .replaceText(getTextReplacementConfig("%child%", args[1])));
                return true;
            }

            String parentArg = args[0];
            String childArg = args[1];

            UUID firstParentUUID;
            UUID childUUID;


            if (Utils.isUUID(parentArg)) {
                firstParentUUID = UUID.fromString(parentArg);

                if (PlayerDataTable.getID(firstParentUUID) < 0) {
                    sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                            .replaceText(getTextReplacementConfig("%player%", parentArg)));
                    return true;
                }
            } else {
                firstParentUUID = PlayerDataTable.getUUID(parentArg);

                if (firstParentUUID == null) {
                    sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                            .replaceText(getTextReplacementConfig("%player%", parentArg)));
                    return true;
                }
            }

            if (Utils.isUUID(childArg)) {
                childUUID = UUID.fromString(childArg);

                if (PlayerDataTable.getID(childUUID) < 0) {
                    sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                            .replaceText(getTextReplacementConfig("%player%", childArg)));
                    return true;
                }
            } else {
                childUUID = PlayerDataTable.getUUID(childArg);

                if (childUUID == null) {
                    sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                            .replaceText(getTextReplacementConfig("%player%", childArg)));
                    return true;
                }
            }

            PlayerSender firstParent = LunaticLib.getPlatform().getPlayerSender(firstParentUUID);
            PlayerSender child = LunaticLib.getPlatform().getPlayerSender(childUUID);


            if (args[0].equalsIgnoreCase(args[1])) {
                sender.sendMessage(getMessage(samePlayerMK));
            } else {

                FamilyPlayerImpl firstParentFam = new FamilyPlayerImpl(firstParentUUID);
                FamilyPlayerImpl childFam = new FamilyPlayerImpl(childUUID);

                if (firstParentFam.isFamilyMember(childFam.getId())) {
                    sender.sendMessage(getMessage(sameFamilyMK)
                            .replaceText(getTextReplacementConfig("%player1%", firstParentFam.getName()))
                            .replaceText(getTextReplacementConfig("%player2%", child.getName())));
                    return true;
                }

                if (!firstParentFam.isMarried() && !LunaticFamily.getConfig().isAllowSingleAdopt()) {
                    sender.sendMessage(getMessage(noSingleAdoptMK)
                            .replaceText(getTextReplacementConfig("%player%", firstParentFam.getName())));
                } else if (childFam.isAdopted()) {
                    sender.sendMessage(getMessage(alreadyAdoptedMK)
                            .replaceText(getTextReplacementConfig("%child%", childFam.getName())));
                } else if (firstParentFam.getChildrenAmount() > 1) {
                    sender.sendMessage(getMessage(adoptLimitMK)
                            .replaceText(getTextReplacementConfig("%player%", firstParentFam.getName())));
                } else if (childFam.hasSibling() && !confirm) {

                    sender.sendMessage(Utils.getClickableDecisionMessage(
                            getMessage(hasSiblingMK)
                                    .replaceText(getTextReplacementConfig("%player%", childFam.getName())),
                            getMessage(CONFIRM_MK, false),
                            "/family adopt set " + firstParentFam.getName() + " " + childFam.getName() + " force confirm",
                            getMessage(CANCEL_MK, false),
                            "/family adopt set " + firstParentFam.getName() + " " + child.getName() + "force cancel"));

                } else if (childFam.hasSibling() && firstParentFam.getChildrenAmount() > 0) {
                    sender.sendMessage(getMessage(hasSiblingLimitMK)
                            .replaceText(getTextReplacementConfig("%player1%", childFam.getName()))
                            .replaceText(getTextReplacementConfig("%player2%", firstParentFam.getName())));
                } else {

                    if (!firstParentFam.isMarried()) {
                        sender.sendMessage(getMessage(setBySingleMK)
                                .replaceText(getTextReplacementConfig("%child%", childFam.getName()))
                                .replaceText(getTextReplacementConfig("%parent%", firstParentFam.getName())));
                    } else {
                        FamilyPlayerImpl secondParentFam = firstParentFam.getPartner();
                        sender.sendMessage(getMessage(setMK)
                                .replaceText(getTextReplacementConfig("%child%", childFam.getName()))
                                .replaceText(getTextReplacementConfig("%parent1%", firstParentFam.getName()))
                                .replaceText(getTextReplacementConfig("%parent2%", secondParentFam.getName())));
                    }

                    LunaticFamily.adoptRequests.remove(childUUID);
                    firstParentFam.adopt(childFam.getId());
                }
            }
        }
        return true;
    }
}
