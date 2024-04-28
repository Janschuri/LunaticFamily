package de.janschuri.lunaticFamily.commands.subcommands.sibling;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class SiblingSetSubcommand extends Subcommand {
    private static final String mainCommand = "sibling";
    private static final String name = "set";
    private static final String permission = "lunaticfamily.admin.sibling";

    public SiblingSetSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            boolean forced = false;

            if (args.length > 3) {
                if (args[3].equalsIgnoreCase("force")) {
                    forced = true;
                }
            }

            if (!sender.hasPermission("lunaticFamily.admin.sibling")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
                return true;
            } else if (args.length < 2) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                return true;
            }

            UUID player1UUID;
            UUID player2UUID;
            AbstractPlayerSender player1;
            AbstractPlayerSender player2;
            if (Utils.isUUID(args[1])) {
                player1UUID = UUID.fromString(args[1]);
                player1 = AbstractSender.getPlayerSender(player1UUID);
            } else {
                forced = false;
                player1 = AbstractSender.getPlayerSender(args[1]);
                player1UUID = player1.getUniqueId();
            }
            if (Utils.isUUID(args[2])) {
                player2UUID = UUID.fromString(args[2]);
                player2 = AbstractSender.getPlayerSender(player2UUID);
            } else {
                forced = false;
                player2 = AbstractSender.getPlayerSender(args[2]);
                player2UUID = player2.getUniqueId();
            }
            FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);
            FamilyPlayer player2Fam = new FamilyPlayer(player2UUID);

            if (!player1.exists() && !forced) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else if (!player2.exists() && !forced) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[2]));
            } else if (args[1].equalsIgnoreCase(args[2])) {
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
