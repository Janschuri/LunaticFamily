package de.janschuri.lunaticFamily.commands.adopt.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AdoptDenySubcommand extends Subcommand {
    private static final String permission = "lunaticfamily.adopt";
    private static final List<String> aliases = Language.getAliases("adopt", "deny");

    public AdoptDenySubcommand() {
        super(permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args, LunaticFamily plugin) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(permission)) {
            sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
        } else {
            Player player = (Player) sender;
            String playerUUID = player.getUniqueId().toString();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (!LunaticFamily.adoptRequests.containsKey(playerUUID)) {
                sender.sendMessage(Language.prefix + Language.getMessage("adopt_deny_no_request"));
            } else {
                String parent = LunaticFamily.adoptRequests.get(playerUUID);
                FamilyPlayer parentFam = new FamilyPlayer(parent);
                parentFam.sendMessage(Language.prefix + Language.getMessage("adopt_deny").replace("%player%", playerFam.getName()));
                sender.sendMessage(Language.prefix + Language.getMessage("adopt_denied").replace("%player%", parentFam.getName()));
                LunaticFamily.adoptRequests.remove(playerUUID);
            }
        }
    }
}
