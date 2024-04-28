package de.janschuri.lunaticFamily.commands.subcommands.family;

import com.google.common.collect.BiMap;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.List;
import java.util.UUID;

public class FamilyListSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "list";
    private static final String permission = "lunaticfamily.family.list";

    public FamilyListSubcommand() {
        super(mainCommand, name, permission);
    }

    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            List<String> list = PluginConfig.familyList;

            if (!(sender instanceof AbstractPlayerSender) && args.length < 2) {
                sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
            } else if (args.length == 1) {
                AbstractPlayerSender player = (AbstractPlayerSender) sender;
                String uuid = player.getUniqueId().toString();
                FamilyPlayer playerFam = new FamilyPlayer(uuid);

                playerFam.updateFamilyTree();

                BiMap<String, Integer> familyList = playerFam.getFamilyList();
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
                AbstractPlayerSender player1 = AbstractSender.getPlayerSender(args[1]);
                UUID player1UUID = player1.getUniqueId();
                if (!sender.hasPermission("lunaticFamily.family.list.others")) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
                } else if (!player1.exists()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[1]));
                } else {
                    FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);
                    BiMap<String, Integer> familyList = player1Fam.getFamilyList();
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
