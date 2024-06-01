package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticlib.CommandMessageKey;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FamilyListSubcommand extends Subcommand {

    private final CommandMessageKey helpMK = new CommandMessageKey(this,"help");
    private final CommandMessageKey helpOthersMK = new CommandMessageKey(this,"help_others");
    private final CommandMessageKey headerMK = new CommandMessageKey(this,"header");
    private final CommandMessageKey othersHeaderMK = new CommandMessageKey(this,"others_header");

    @Override
    public String getPermission() {
        return "lunaticfamily.family.list";
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public FamilySubcommand getParentCommand() {
        return new FamilySubcommand();
    }

    @Override
    public Map<CommandMessageKey, String> getHelpMessages() {
        return Map.of(
                helpMK, getPermission(),
                helpOthersMK, getPermission() + ".others"
        );
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(getMessage(NO_PERMISSION_MK));
        } else {
            List<String> list = LunaticFamily.getConfig().getFamilyList();

            if (!(sender instanceof PlayerSender) && args.length < 1) {
                sender.sendMessage(getMessage(NO_CONSOLE_COMMAND_MK));
            } else if (args.length == 0) {
                PlayerSender player = (PlayerSender) sender;
                UUID uuid = player.getUniqueId();
                FamilyPlayerImpl playerFam = new FamilyPlayerImpl(uuid);

                playerFam.updateFamilyTree();

                Map<String, Integer> familyList = playerFam.getFamilyMap();
                StringBuilder msg = new StringBuilder(getMessage(headerMK) + "\n");

                sender.sendMessage(getFamilyListMessage(list, familyList, msg));
            } else {

                String playerName = args[0];
                UUID player1UUID = PlayerDataTable.getUUID(playerName);

                if (player1UUID == null) {
                    sender.sendMessage(getMessage(PLAYER_NOT_EXIST_MK)
                            .replaceText(getTextReplacementConfig("%player%", playerName)));
                    return true;
                }


                if (!sender.hasPermission(getPermission() + ".others")) {
                    sender.sendMessage(getMessage(NO_PERMISSION_MK));
                    return true;
                }

                    FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(player1UUID);
                    Map<String, Integer> familyList = player1Fam.getFamilyMap();
                    StringBuilder msg = new StringBuilder(getMessage(othersHeaderMK)
                            .replaceText(getTextReplacementConfig("%player%", player1Fam.getName())) + "\n");

                    sender.sendMessage(getFamilyListMessage(list, familyList, msg));

            }
        }
        return true;
    }

    private String getFamilyListMessage(List<String> list, Map<String, Integer> familyList, StringBuilder msg) {
        for (String e : list) {
            if (familyList.containsKey(e)) {
                int relationID = familyList.get(e);
                FamilyPlayerImpl relationFam = new FamilyPlayerImpl(relationID);
                String relationKey = e.replace("first_", "")
                        .replace("second_", "")
                        .replace("third_", "")
                        .replace("fourth_", "")
                        .replace("fifth_", "")
                        .replace("sixth_", "")
                        .replace("seventh_", "")
                        .replace("eighth_", "");
                msg.append(getRelation(relationKey, relationFam.getGender())).append(": ").append(relationFam.getName()).append("\n");
            }
        }
        return msg.toString();
    }
}
