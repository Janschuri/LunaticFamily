package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.external.Minepacks;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MarryBackpackSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "backpack";
    private static final String permission = "lunaticfamily.marry.backpack";

    public MarryBackpackSubcommand() {
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

            if (!player.hasPermission("lunaticFamily.marry.backpack")) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
            } else if(!PluginConfig.enabledMinepacks) {
                sender.sendMessage(Language.prefix + Language.getMessage("disabled_feature"));
            } else if (!playerFam.isMarried()) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_backpack_no_partner"));
            } else if (!PluginConfig.marryBackpackOffline && Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) == null) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", playerFam.getPartner().getName()));
            } else {
                OfflinePlayer partnerPlayer = playerFam.getPartner().getOfflinePlayer();
                sender.sendMessage(Language.prefix + Language.getMessage("marry_backpack_open"));
                Minepacks.getMinepacks().openBackpack(player, partnerPlayer, true);
            }
        }
    }
}
