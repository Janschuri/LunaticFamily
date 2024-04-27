package de.janschuri.lunaticFamily.commands.subcommands.sibling;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class SiblingUnsetSubcommand extends Subcommand {
    private static final String mainCommand = "sibling";
    private static final String name = "unset";
    private static final String permission = "lunaticfamily.admin.sibling";

    public SiblingUnsetSubcommand() {
        super(mainCommand, name, permission);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            if (!sender.hasPermission("lunaticFamily.admin.sibling")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
                return true;
            } else if (args.length < 2) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                return true;
            }

            UUID player1UUID;
            AbstractPlayerSender player1;
            if (Utils.isUUID(args[1])) {
                player1UUID = UUID.fromString(args[1]);
                player1 = sender.getPlayerCommandSender(player1UUID);
            } else {
                player1 = sender.getPlayerCommandSender(args[1]);
                player1UUID = player1.getUniqueId();
            }
            FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);

            if (!player1.exists()) {
                sender.sendMessage(language.getPrefix() + language.getMessage("player_not_exist").replace("%player%", player1.getName()));
            } else {

                if (!player1Fam.hasSibling()) {
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_sibling_unset_no_sibling").replace("%player%", player1Fam.getName()));
                } else {
                    FamilyPlayer siblingFam = player1Fam.getSibling();
                    player1Fam.removeSibling();
                    sender.sendMessage(language.getPrefix() + language.getMessage("admin_sibling_unset_sibling").replace("%player1%", player1Fam.getName()).replace("%player2%", siblingFam.getName()));
                }
            }
        }
        return true;
    }
}
