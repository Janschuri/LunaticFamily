package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class MarrySetSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "set";
    private static final String permission = "lunaticfamily.admin.marry";

    public MarrySetSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            boolean force = false;

            if (args.length > 3) {
                if (args[3].equalsIgnoreCase("force")) {
                    force = true;
                }
            }

            if (args.length < 2) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
            } else if (args[1].equalsIgnoreCase("deny")) {
                sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_set_denied"));
            } else if (args.length < 3) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
            } else if (!Utils.playerExists(args[1]) && !force) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else if (!Utils.playerExists(args[2]) && !force) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[2]));
            } else if (args[1].equalsIgnoreCase(args[2])) {
                sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_set_same_player"));
            }
            else {

                String player1UUID;
                String player2UUID;
                if (Utils.isUUID(args[1])) {
                    player1UUID = args[1];
                } else {
                    player1UUID = Bukkit.getOfflinePlayer(args[1]).getUniqueId().toString();
                }
                if (Utils.isUUID(args[2])) {
                    player2UUID = args[2];
                } else {
                    player2UUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                }

                FamilyPlayer player2Fam = new FamilyPlayer(player2UUID);
                FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);

                if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
                    int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_set_too_many_children").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                } else if (player1Fam.isMarried()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_set_already_married").replace("%player%", player1Fam.getName()));
                } else if (player2Fam.isMarried()) {
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_set_already_married").replace("%player%", player2Fam.getName()));
                } else {
                    LunaticFamily.marryRequests.remove(player1UUID);
                    LunaticFamily.marryPriestRequests.remove(player1UUID);
                    LunaticFamily.marryPriest.remove(player1UUID);

                    LunaticFamily.marryRequests.remove(player1UUID);
                    LunaticFamily.marryPriestRequests.remove(player1UUID);
                    LunaticFamily.marryPriest.remove(player1UUID);

                    player1Fam.marry(player2Fam.getID());
                    sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_set_married").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                }
            }
        }
    }
}
