package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.commands.family.MarrySubcommand;
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

public class MarrySetSubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey samePlayerMK = new CommandMessageKey(this,"same_player");
    private final CommandMessageKey alreadyMarriedMK = new CommandMessageKey(this,"already_married");
    private final CommandMessageKey tooManyChildrenMK = new CommandMessageKey(this,"too_many_children");
    private final CommandMessageKey deniedMK = new CommandMessageKey(this,"denied");
    private final CommandMessageKey confirmMK = new CommandMessageKey(this,"confirm");
    private final CommandMessageKey marriedMK = new CommandMessageKey(this,"married");
    private final CommandMessageKey sameFamilyMK = new CommandMessageKey(this,"same_family");



    @Override
    public String getPermission() {
        return "lunaticfamily.admin.marry";
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public MarrySubcommand getParentCommand() {
        return new MarrySubcommand();
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
        } else {
            boolean force = false;

            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("force")) {
                    force = true;
                }
            }

            if (args.length < 1) {
                sender.sendMessage(getMessage(WRONG_USAGE_MK));
                Logger.debugLog("MarrySetSubcommand: Wrong usage");
                return true;
            } else if (args[0].equalsIgnoreCase("deny")) {
                sender.sendMessage(getMessage(deniedMK));
                return true;
            } else if (args.length < 2) {
                sender.sendMessage(getMessage(WRONG_USAGE_MK));
                Logger.debugLog("MarrySetSubcommand: Wrong usage");
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

            if (Utils.isUUID(args[1])) {
                player2UUID = UUID.fromString(player2Arg);

                if (PlayerDataTable.getID(player1UUID) < 0) {
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




            if (player1UUID.equals(player2UUID)) {
                sender.sendMessage(getMessage(samePlayerMK));
                return true;
            }

                FamilyPlayerImpl player2Fam = new FamilyPlayerImpl(player2UUID);
                FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(player1UUID);

                if (player1Fam.isFamilyMember(player2Fam.getId())) {
                    sender.sendMessage(getMessage(samePlayerMK)
                            .replaceText(getTextReplacementConfig("%player1%", player1Fam.getName()))
                            .replaceText(getTextReplacementConfig("%player2%", player2Fam.getName())));
                    return true;
                }

                if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
                    int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                    sender.sendMessage(getMessage(tooManyChildrenMK)
                            .replaceText(getTextReplacementConfig("%player1%", player1Fam.getName()))
                            .replaceText(getTextReplacementConfig("%player2%", player2Fam.getName()))
                            .replaceText(getTextReplacementConfig("%amount%", Integer.toString(amountDiff))));
                } else if (player1Fam.isMarried()) {
                    sender.sendMessage(getMessage(alreadyMarriedMK)
                            .replaceText(getTextReplacementConfig("%player%", player1Fam.getName())));
                } else if (player2Fam.isMarried()) {
                    sender.sendMessage(getMessage(alreadyMarriedMK)
                            .replaceText(getTextReplacementConfig("%player%", player2Fam.getName())));
                } else {
                    LunaticFamily.marryRequests.remove(player1UUID);
                    LunaticFamily.marryPriestRequests.remove(player1UUID);
                    LunaticFamily.marryPriest.remove(player1UUID);

                    LunaticFamily.marryRequests.remove(player1UUID);
                    LunaticFamily.marryPriestRequests.remove(player1UUID);
                    LunaticFamily.marryPriest.remove(player1UUID);

                    player1Fam.marry(player2Fam.getId());
                    sender.sendMessage(getMessage(marriedMK)
                            .replaceText(getTextReplacementConfig("%player1%", player1Fam.getName()))
                            .replaceText(getTextReplacementConfig("%player2%", player2Fam.getName())));
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
