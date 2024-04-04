package de.janschuri.lunaticFamily.commands.sibling.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class SiblingSetSubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "set";
    private static final String permission = "lunaticfamily.admin.sibling";

    public SiblingSetSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            boolean forced = false;

            if (args.length > 3) {
                if (args[3].equalsIgnoreCase("force")) {
                    forced = true;
                }
            }

            if (!sender.hasPermission("lunaticFamily.admin.sibling")) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
            } else if (args.length < 2) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
            } else if (!Utils.playerExists(args[1]) && !forced) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else if (!Utils.playerExists(args[2]) && !forced) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[2]));
            } else if (args[1].equalsIgnoreCase(args[2])) {
                sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_set_same_player"));
            } else {

                String player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);
                String player2UUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                FamilyPlayer player2Fam = new FamilyPlayer(player2UUID);

                if (player1Fam.isAdopted() && player2Fam.isAdopted()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_sibling_set_both_adopted").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                } else if (player1Fam.isAdopted()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_sibling_is_adopted").replace("%player%", player1Fam.getName()));

                } else if (player2Fam.isAdopted()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_sibling_is_adopted").replace("%player%", player2Fam.getName()));
                } else {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_sibling_added").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                    player1Fam.addSibling(player2Fam.getID());
                }
            }
        }
    }
}
