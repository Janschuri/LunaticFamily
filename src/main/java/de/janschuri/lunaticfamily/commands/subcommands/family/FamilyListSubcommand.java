package de.janschuri.lunaticfamily.commands.subcommands.family;

import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.config.Language;
import de.janschuri.lunaticfamily.config.PluginConfig;
import de.janschuri.lunaticfamily.handler.FamilyPlayer;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

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
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            List<String> list = PluginConfig.getFamilyList();

            if (!(sender instanceof AbstractPlayerSender) && args.length < 1) {
                sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
            } else if (args.length == 0) {
                AbstractPlayerSender player = (AbstractPlayerSender) sender;
                String uuid = player.getUniqueId().toString();
                FamilyPlayer playerFam = new FamilyPlayer(uuid);

                playerFam.updateFamilyTree();

                Map<String, Integer> familyList = playerFam.getFamilyMap();
                StringBuilder msg = new StringBuilder(language.getPrefix() + language.getMessage("family_list") + "\n");

                for (String e : list) {
                    if (familyList.containsKey(e)) {
                        int relationID = familyList.get(e);
                        FamilyPlayer relationFam = new FamilyPlayer(relationID);
                        String relationKey = e.replace("first_", "")
                                .replace("second_", "")
                                .replace("third_", "")
                                .replace("fourth_", "")
                                .replace("fifth_", "")
                                .replace("sixth_", "")
                                .replace("seventh_", "")
                                .replace("eighth_", "");
                        msg.append(Language.getRelation(relationKey, relationFam.getGender())).append(": ").append(relationFam.getName()).append("\n");
                    }
                }
                sender.sendMessage(msg.toString());
            } else {
                AbstractPlayerSender player1 = AbstractSender.getPlayerSender(args[0]);
                UUID player1UUID = player1.getUniqueId();
                if (!sender.hasPermission("lunaticFamily.family.list.others")) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
                } else if (!player1.exists()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[0]));
                } else {
                    FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);
                    Map<String, Integer> familyList = player1Fam.getFamilyMap();
                    StringBuilder msg = new StringBuilder(language.getPrefix() + language.getMessage("family_others_list").replace("%player%", player1Fam.getName()) + "\n");
                    for (String e : list) {
                        if (familyList.containsKey(e)) {
                            int relationID = familyList.get(e);
                            FamilyPlayer relationFam = new FamilyPlayer(relationID);
                            String relationKey = e.replace("first_", "")
                                    .replace("second_", "")
                                    .replace("third_", "")
                                    .replace("fourth_", "")
                                    .replace("fifth_", "")
                                    .replace("sixth_", "")
                                    .replace("seventh_", "")
                                    .replace("eighth_", "");
                            msg.append(Language.getRelation(relationKey, relationFam.getGender())).append(": ").append(relationFam.getName()).append("\n");
                        }
                    }
                    sender.sendMessage(msg.toString());
                }
            }
        }
        return true;
    }
}
