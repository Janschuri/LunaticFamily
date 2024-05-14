package de.janschuri.lunaticfamily.commands.subcommands.sibling;

import de.janschuri.lunaticfamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticfamily.handler.FamilyPlayer;
import de.janschuri.lunaticfamily.utils.Utils;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class SiblingUnsetSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "sibling";
    private static final String NAME = "unset";
    private static final String PERMISSION = "lunaticfamily.admin.sibling";

    public SiblingUnsetSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            if (!sender.hasPermission("lunaticFamily.admin.sibling")) {
                sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
                return true;
            } else if (args.length < 1) {
                sender.sendMessage(language.getPrefix() + language.getMessage("wrong_usage"));
                return true;
            }

            UUID player1UUID;
            AbstractPlayerSender player1;
            if (Utils.isUUID(args[0])) {
                player1UUID = UUID.fromString(args[0]);
                player1 = AbstractSender.getPlayerSender(player1UUID);
            } else {
                player1 = AbstractSender.getPlayerSender(args[0]);
                player1UUID = player1.getUniqueId();
            }
            FamilyPlayer player1Fam = new FamilyPlayer(player1UUID);

            if (!Utils.playerExists(player1)) {
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
