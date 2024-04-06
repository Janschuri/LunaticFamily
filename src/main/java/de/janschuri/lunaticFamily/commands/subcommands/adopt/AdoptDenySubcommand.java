package de.janschuri.lunaticFamily.commands.subcommands.adopt;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdoptDenySubcommand extends Subcommand {
    private static final String mainCommand = "adopt";
    private static final String name = "deny";
    private static final String permission = "lunaticfamily.adopt";

    public AdoptDenySubcommand() {
        super(mainCommand, name, permission);
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
