package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class MarrySetSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "set";
    private static final String permission = "lunaticfamily.admin.marry";
    private static Language language;

    public MarrySetSubcommand() {
        super(mainCommand, name, permission);
        language = language.getInstance();
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            boolean force = false;

            if (args.length > 3) {
                if (args[3].equalsIgnoreCase("force")) {
                    force = true;
                }
            }

            if (args.length < 2) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                return true;
            } else if (args[1].equalsIgnoreCase("deny")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("admin_marry_set_denied"));
                return true;
            } else if (args.length < 3) {
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
                force = false;
                player1 = AbstractSender.getPlayerSender(args[1]);
                player1UUID = player1.getUniqueId();
            }
            if (Utils.isUUID(args[2])) {
                player2UUID = UUID.fromString(args[2]);
                player2 = AbstractSender.getPlayerSender(player2UUID);
            } else {
                force = false;
                player2 = AbstractSender.getPlayerSender(args[2]);
                player2UUID = player2.getUniqueId();
            }




            if (!player1.exists() && !force) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[1]));
            } else if (!player2.exists() && !force) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", args[2]));
            } else if (args[1].equalsIgnoreCase(args[2])) {
                sender.sendMessage(language.getPrefix() + language.getMessage("admin_marry_set_same_player"));
            }
            else {

                FamilyPlayer player2Fam = new FamilyPlayer(player2UUID);
                FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);

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
