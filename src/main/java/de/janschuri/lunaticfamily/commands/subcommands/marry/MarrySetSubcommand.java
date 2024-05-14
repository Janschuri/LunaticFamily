package de.janschuri.lunaticfamily.commands.subcommands.marry;

import de.janschuri.lunaticfamily.LunaticFamily;
import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class MarrySetSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "set";
    private static final String PERMISSION = "lunaticfamily.admin.marry";

    public MarrySetSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            boolean force = false;

            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("force")) {
                    force = true;
                }
            }

            if (args.length < 1) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                return true;
            } else if (args[0].equalsIgnoreCase("deny")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("admin_marry_set_denied"));
                return true;
            } else if (args.length < 2) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                return true;
            }

            UUID player1UUID;
            UUID player2UUID;
            AbstractPlayerSender player1;
            AbstractPlayerSender player2;
            if (Utils.isUUID(args[0])) {
                player1UUID = UUID.fromString(args[0]);
                player1 = AbstractSender.getPlayerSender(player1UUID);
            } else {
                player1 = AbstractSender.getPlayerSender(args[0]);
                player1UUID = player1.getUniqueId();
            }
            if (Utils.isUUID(args[1])) {
                player2UUID = UUID.fromString(args[1]);
                player2 = AbstractSender.getPlayerSender(player2UUID);
            } else {
                player2 = AbstractSender.getPlayerSender(args[1]);
                player2UUID = player2.getUniqueId();
            }




            if (!Utils.playerExists(player1) && !force) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[0]));
            } else if (!Utils.playerExists(player2) && !force) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else if (args[0].equalsIgnoreCase(args[1])) {
                sender.sendMessage(language.getPrefix() + language.getMessage("admin_marry_set_same_player"));
            }
            else {

                FamilyPlayer player2Fam = new FamilyPlayer(player2UUID);
                FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);

                if (player1Fam.isFamilyMember(player2Fam.getID())) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_already_family").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                    return true;
                }

                if (player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() > 2) {
                    int amountDiff = player1Fam.getChildrenAmount() + player2Fam.getChildrenAmount() - 2;
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_marry_set_too_many_children").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()).replace("%amount%", Integer.toString(amountDiff)));
                } else if (player1Fam.isMarried()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_marry_set_already_married").replace("%player%", player1Fam.getName()));
                } else if (player2Fam.isMarried()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_marry_set_already_married").replace("%player%", player2Fam.getName()));
                } else {
                    LunaticFamily.marryRequests.remove(player1UUID);
                    LunaticFamily.marryPriestRequests.remove(player1UUID);
                    LunaticFamily.marryPriest.remove(player1UUID);

                    LunaticFamily.marryRequests.remove(player1UUID);
                    LunaticFamily.marryPriestRequests.remove(player1UUID);
                    LunaticFamily.marryPriest.remove(player1UUID);

                    player1Fam.marry(player2Fam.getID());
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_marry_set_married").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                }
            }
        }
        return true;
    }
}
