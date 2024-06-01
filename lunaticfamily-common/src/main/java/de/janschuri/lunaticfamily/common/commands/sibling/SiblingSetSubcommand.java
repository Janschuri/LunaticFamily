package de.janschuri.lunaticfamily.common.commands.sibling;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.SiblingSubcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.Sender;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SiblingSetSubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey addedMK = new CommandMessageKey(this,"added");
    private final CommandMessageKey isAdoptedMK = new CommandMessageKey(this,"is_adopted");
    private final CommandMessageKey setBothAdoptedMK = new CommandMessageKey(this,"set_both_adopted");
    private final CommandMessageKey sameFamilyMK = new CommandMessageKey(this,"same_family");
    private final CommandMessageKey samePlayerMK = new CommandMessageKey(this,"same_player");



    @Override
    public String getPermission() {
        return "lunaticfamily.admin.sibling";
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public SiblingSubcommand getParentCommand() {
        return new SiblingSubcommand();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage((getMessage(NO_PERMISSION_MK)));
        } else {
            boolean forced = false;

            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("force")) {
                    forced = true;
                }
            }

            if (!sender.hasPermission("lunaticFamily.admin.sibling")) {
                sender.sendMessage(getMessage(NO_PERMISSION_MK));
                return true;
            } else if (args.length < 1) {
                sender.sendMessage(getMessage(WRONG_USAGE_MK));
                Logger.debugLog("SiblingSetSubcommand: Wrong usage");
                return true;
            }

            String player1Arg = args[0];
            String player2Arg = args[1];

            UUID player1UUID;
            UUID player2UUID;

            if (Utils.isUUID(player1Arg)) {
                player1UUID = UUID.fromString(args[0]);

                if (PlayerDataTable.getID(player1UUID) < 0) {
                    sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                            .replaceText(getTextReplacementConfig("%player%", player1Arg)));
                    return true;
                }
            } else {
                player1UUID = PlayerDataTable.getUUID(player1Arg);

                if (player1UUID == null) {
                    sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                            .replaceText(getTextReplacementConfig("%player%", player1Arg)));
                    return true;
                }
            }

            if (Utils.isUUID(player2Arg)) {
                player2UUID = UUID.fromString(args[0]);

                if (PlayerDataTable.getID(player2UUID) < 0) {
                    sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                            .replaceText(getTextReplacementConfig("%player%", player2Arg)));
                    return true;
                }
            } else {
                player2UUID = PlayerDataTable.getUUID(player2Arg);

                if (player2UUID == null) {
                    sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                            .replaceText(getTextReplacementConfig("%player%", player2Arg)));
                    return true;
                }
            }


            FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(player1UUID);
            FamilyPlayerImpl player2Fam = new FamilyPlayerImpl(player2UUID);

            if (player1Fam.isFamilyMember(player2Fam.getId())) {
                sender.sendMessage(getMessage(sameFamilyMK)
                        .replaceText(getTextReplacementConfig("%player1%", player1Fam.getName()))
                        .replaceText(getTextReplacementConfig("%player2%", player2Fam.getName())));
                return true;
            }

            if (args[0].equalsIgnoreCase(args[1])) {
                sender.sendMessage(getMessage(samePlayerMK));
            } else {


                if (player1Fam.isAdopted() && player2Fam.isAdopted()) {
                    sender.sendMessage(getMessage(setBothAdoptedMK)
                            .replaceText(getTextReplacementConfig("%player1%", player1Fam.getName()))
                            .replaceText(getTextReplacementConfig("%player2%", player2Fam.getName())));
                } else if (player1Fam.isAdopted()) {
                    sender.sendMessage(getMessage(isAdoptedMK)
                            .replaceText(getTextReplacementConfig("%player%", player1Fam.getName())));

                } else if (player2Fam.isAdopted()) {
                    sender.sendMessage(getMessage(isAdoptedMK)
                            .replaceText(getTextReplacementConfig("%player%", player2Fam.getName())));
                } else {
                    sender.sendMessage(getMessage(addedMK)
                            .replaceText(getTextReplacementConfig("%player1%", player1Fam.getName()))
                            .replaceText(getTextReplacementConfig("%player2%", player2Fam.getName())));
                    player1Fam.addSibling(player2Fam.getId());
                }
            }
        }
        return true;
    }

    @Override
    public Component getParamsName() {
        return getMessage(PLAYER_NAME_MK, false);
    }

    @Override
    public List<Map<String, String>> getParams() {
        return List.of(getOnlinePlayersParam(), getOnlinePlayersParam());
    }
}
