package de.janschuri.lunaticFamily.commands.subcommands.family;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FamilyTreeSubcommand extends Subcommand {
    private static final String mainCommand = "family";
    private static final String name = "tree";
    private static final String permission = "lunaticfamily.family";

    public FamilyTreeSubcommand() {
        super(mainCommand, name, permission);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            Player player = (Player) sender;
            String playerUUID = player.getUniqueId().toString();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);
            playerFam.updateFamilyTree();
            player.sendMessage(Language.getMessage("family_tree_reloaded"));
        }
    }
}
