package de.janschuri.lunaticfamily.common.commands.family;

import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;

import java.util.UUID;

public class FamilyTreeSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "family";
    private static final String NAME = "tree";
    private static final String PERMISSION = "lunaticfamily.family";

    public FamilyTreeSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }

    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(getPrefix() + getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(getPrefix() + getMessage("no_permission"));
        } else {
            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);
            playerFam.updateFamilyTree();
            player.sendMessage(getPrefix() + getMessage("family_tree_reloaded"));
        }
        return true;
    }
}
