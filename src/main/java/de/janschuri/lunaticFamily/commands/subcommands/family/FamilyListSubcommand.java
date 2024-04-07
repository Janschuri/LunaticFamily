package de.janschuri.lunaticFamily.commands.subcommands.family;

import com.google.common.collect.BiMap;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FamilyListSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "list";
    private static final String permission = "lunaticfamily.family.list";

    public FamilyListSubcommand() {
        super(mainCommand, name, permission);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            List<String> list = Config.familyList;

            if (!(sender instanceof Player) && args.length < 2) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
            } else if (args.length == 1) {
                Player player = (Player) sender;
                String uuid = player.getUniqueId().toString();
                FamilyPlayer playerFam = new FamilyPlayer(uuid);

                playerFam.updateFamilyTree();

                BiMap<String, Integer> familyList = playerFam.getFamilyList();
                StringBuilder msg = new StringBuilder(Language.prefix + Language.getMessage("family_list") + "\n");

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
                if (!sender.hasPermission("lunaticFamily.family.list.others")) {
                    sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                } else if (!Utils.playerExists(args[1])) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
                } else {
                    String player1 = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                    FamilyPlayer player1Fam = new FamilyPlayer(player1);
                    BiMap<String, Integer> familyList = player1Fam.getFamilyList();
                    StringBuilder msg = new StringBuilder(Language.prefix + Language.getMessage("family_others_list").replace("%player%", player1Fam.getName()) + "\n");
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
    }
}
