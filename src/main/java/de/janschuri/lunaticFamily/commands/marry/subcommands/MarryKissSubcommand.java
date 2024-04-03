package de.janschuri.lunaticFamily.commands.marry.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class MarryKissSubcommand extends Subcommand {
    private static final String permission = "lunaticfamily.marry";
    private static final List<String> aliases = Language.getAliases("marry", "kiss");

    public MarryKissSubcommand() {
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

            if (!playerFam.isMarried()) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_kiss_no_partner"));
            } else if (Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) == null) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(playerFam.getPartner().getUUID())).getName()));
            } else {
                Player partnerPlayer = Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID()));

                if (!Utils.isInRange(partnerPlayer.getLocation(), player.getLocation(), 3)) {
                    sender.sendMessage(Language.prefix + Language.getMessage("player_too_far_away").replace("%player%", partnerPlayer.getName()));
                } else {
                    Location location = Utils.getPositionBetweenLocations(player.getLocation(), partnerPlayer.getLocation());
                    location.setY(location.getY() + 2);

                    for (int i = 0; i < 6; i++) {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> Utils.spawnParticles(location, Particle.HEART), i * 5L);
                    }
                }
            }
        }
    }
}
