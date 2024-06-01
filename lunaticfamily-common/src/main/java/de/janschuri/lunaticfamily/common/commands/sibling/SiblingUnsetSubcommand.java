package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.SiblingSubcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;

import java.util.UUID;

public class SiblingUnsetSubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey noSiblingMK = new CommandMessageKey(this,"no_sibling");
    private final CommandMessageKey unsetMK = new CommandMessageKey(this,"unset");


    @Override
    public String getPermission() {
        return "lunaticfamily.admin.sibling";
    }

    @Override
    public String getName() {
        return "unset";
    }

    @Override
    public SiblingSubcommand getParentCommand() {
        return new SiblingSubcommand();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
        } else {
            if (!sender.hasPermission("lunaticFamily.admin.sibling")) {
                sender.sendMessage(getMessage(NO_PERMISSION_MK));
                return true;
            } else if (args.length < 1) {
                sender.sendMessage(getMessage(WRONG_USAGE_MK));
                Logger.debugLog("SiblingUnsetSubcommand: Wrong usage");
                return true;
            }

            String player1Name = args[0];
            UUID player1UUID;

            if (Utils.isUUID(args[0])) {
                player1UUID = UUID.fromString(args[0]);

                if (PlayerDataTable.getID(player1UUID) < 0) {
                    sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                            .replaceText(getTextReplacementConfig("%player%", player1Name)));
                    return true;
                }
            } else {
                player1UUID = PlayerDataTable.getUUID(player1Name);

                if (player1UUID == null) {
                    sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                            .replaceText(getTextReplacementConfig("%player%", player1Name)));
                    return true;
                }
            }

            FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(player1UUID);


                if (!player1Fam.hasSibling()) {
                    sender.sendMessage(getMessage(noSiblingMK)
                            .replaceText(getTextReplacementConfig("%player%", player1Fam.getName())));
                } else {
                    FamilyPlayerImpl siblingFam = player1Fam.getSibling();
                    player1Fam.removeSibling();
                    sender.sendMessage(getMessage(unsetMK)
                            .replaceText(getTextReplacementConfig("%player1%", player1Fam.getName()))
                            .replaceText(getTextReplacementConfig("%player2%", siblingFam.getName())));
                }
        }
        return true;
    }
}
