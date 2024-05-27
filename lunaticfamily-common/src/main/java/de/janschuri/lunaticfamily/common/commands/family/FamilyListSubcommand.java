package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FamilyListSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "family";
    private static final String NAME = "list";
    private static final String PERMISSION = "lunaticfamily.family.list";

    public FamilyListSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else {
            List<String> list = LunaticFamily.getConfig().getFamilyList();

            if (!(sender instanceof PlayerSender) && args.length < 1) {
                sender.sendMessage(getPrefix() + getMessage("no_console_command"));
            } else if (args.length == 0) {
                PlayerSender player = (PlayerSender) sender;
                UUID uuid = player.getUniqueId();
                FamilyPlayerImpl playerFam = new FamilyPlayerImpl(uuid);

                playerFam.updateFamilyTree();

                Map<String, Integer> familyList = playerFam.getFamilyMap();
                StringBuilder msg = new StringBuilder(getPrefix() + getMessage("family_list") + "\n");

                sender.sendMessage(getFamilyListMessage(list, familyList, msg));
            } else {

                String playerName = args[0];
                UUID player1UUID = PlayerDataTable.getUUID(playerName);

                if (player1UUID == null) {
                    sender.sendMessage(getPrefix() + getMessage("player_not_exist").replace("%player%", playerName));
                    return true;
                }


                if (!sender.hasPermission("lunaticFamily.family.list.others")) {
                    sender.sendMessage(getPrefix() + getMessage("no_permission"));
                    return true;
                }

                    FamilyPlayerImpl player1Fam = new FamilyPlayerImpl(player1UUID);
                    Map<String, Integer> familyList = player1Fam.getFamilyMap();
                    StringBuilder msg = new StringBuilder(getPrefix() + getMessage("family_others_list").replace("%player%", player1Fam.getName()) + "\n");

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
