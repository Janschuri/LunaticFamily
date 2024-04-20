package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.CommandSender;
import de.janschuri.lunaticFamily.commands.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;

import java.util.UUID;

public class MarrySetSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "set";
    private static final String permission = "lunaticfamily.admin.marry";

    public MarrySetSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public boolean execute(CommandSender sender, String[] args) {
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
                return true;
            } else if (args[1].equalsIgnoreCase("deny")) {
                sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_set_denied"));
                return true;
            } else if (args.length < 3) {
                sender.sendMessage(Language.prefix + Language.getMessage("wrong_usage"));
                return true;
            }

            UUID player1UUID;
            UUID player2UUID;
            PlayerCommandSender player1;
            PlayerCommandSender player2 = sender.getPlayerCommandSender(args[2]);
            if (Utils.isUUID(args[1])) {
                player1UUID = UUID.fromString(args[1]);
                player1 = sender.getPlayerCommandSender(player1UUID);
            } else {
                force = false;
                player1 = sender.getPlayerCommandSender(args[1]);
                player1UUID = player1.getUniqueId();
            }
            if (Utils.isUUID(args[2])) {
                player2UUID = UUID.fromString(args[2]);
                player2 = sender.getPlayerCommandSender(player2UUID);
            } else {
                force = false;
                player2 = sender.getPlayerCommandSender(args[2]);
                player2UUID = player2.getUniqueId();
            }




            if (!player1.exists() && !force) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else if (!player2.exists() && !force) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_exist").replace("%player%", args[2]));
            } else if (args[1].equalsIgnoreCase(args[2])) {
                sender.sendMessage(Language.prefix + Language.getMessage("admin_marry_set_same_player"));
            }
            else {

                FamilyPlayer player2Fam = player2.getFamilyPlayer();
                FamilyPlayer player1Fam = player1.getFamilyPlayer();

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
        return true;
    }
}
