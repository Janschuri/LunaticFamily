package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.commands.CommandSender;
import de.janschuri.lunaticFamily.commands.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;

public class FamilyTreeSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "tree";
    private static final String permission = "lunaticfamily.family";

    public FamilyTreeSubcommand() {
        super(mainCommand, name, permission);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof PlayerCommandSender)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            PlayerCommandSender player = (PlayerCommandSender) sender;
            String playerUUID = player.getUniqueId().toString();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);
            playerFam.updateFamilyTree();
            player.sendMessage(Language.prefix + Language.getMessage("family_tree_reloaded"));
        }
        return true;
    }
}
