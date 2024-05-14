package de.janschuri.lunaticfamily.commands.subcommands.sibling;

import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class SiblingSetSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "sibling";
    private static final String NAME = "set";
    private static final String PERMISSION = "lunaticfamily.admin.sibling";

    public SiblingSetSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            boolean forced = false;

            if (args.length > 2) {
                if (args[2].equalsIgnoreCase("force")) {
                    forced = true;
                }
            }

            if (!sender.hasPermission("lunaticFamily.admin.sibling")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
                return true;
            } else if (args.length < 1) {
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
            FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);
            FamilyPlayer player2Fam = new FamilyPlayer(player2UUID);

            if (player1Fam.isFamilyMember(player2Fam.getID())) {
                sender.sendMessage(language.getPrefix() + language.getMessage("admin_already_family").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                return true;
            }

            if (!Utils.playerExists(player1) && !forced) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[0]));
            } else if (!Utils.playerExists(player2) && !forced) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else if (args[0].equalsIgnoreCase(args[1])) {
                sender.sendMessage(language.getPrefix() + language.getMessage("admin_marry_set_same_player"));
            } else {


                if (player1Fam.isAdopted() && player2Fam.isAdopted()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_sibling_set_both_adopted").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                } else if (player1Fam.isAdopted()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_sibling_is_adopted").replace("%player%", player1Fam.getName()));

                } else if (player2Fam.isAdopted()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_sibling_is_adopted").replace("%player%", player2Fam.getName()));
                } else {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_sibling_added").replace("%player1%", player1Fam.getName()).replace("%player2%", player2Fam.getName()));
                    player1Fam.addSibling(player2Fam.getID());
                }
            }
        }
        return true;
    }
}
