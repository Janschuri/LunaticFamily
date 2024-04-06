package de.janschuri.lunaticFamily.commands.sibling.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SiblingDenySubcommand extends Subcommand {
    private static final String mainCommand = "sibling";
    private static final String name = "deny";
    private static final String permission = "lunaticfamily.sibling";

    public SiblingDenySubcommand() {
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

            if (!LunaticFamily.siblingRequests.containsKey(playerUUID) && !LunaticFamily.marryPriestRequests.containsKey(playerUUID)) {
                sender.sendMessage(Language.prefix + Language.getMessage("propose_deny_no_request"));
            } else {
                if (!LunaticFamily.siblingRequests.containsKey(playerUUID)) {

                }
                String partnerUUID = LunaticFamily.marryRequests.get(playerUUID);
                Bukkit.getPlayer(UUID.fromString(partnerUUID)).sendMessage(Language.prefix + Language.getMessage("propose_deny_denied").replace("%player%", playerFam.getName()));
                LunaticFamily.marryRequests.remove(playerUUID);
            }
        }
    }
}
