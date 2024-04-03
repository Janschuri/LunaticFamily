package de.janschuri.lunaticFamily.commands.marry.subcommands;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.Subcommand;
import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.external.Minepacks;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class MarryBackpackSubcommand extends Subcommand {
    private static final String permission = "lunaticfamily.marry.backpack";
    private static final List<String> aliases = Language.getAliases("marry", "backpack");

    public MarryBackpackSubcommand() {
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

            if (!player.hasPermission("lunaticFamily.marry.backpack")) {
                sender.sendMessage(Language.prefix + Language.getMessage("no_permission"));
            } else if(!Config.enabledMinepacks) {
                sender.sendMessage(Language.prefix + Language.getMessage("disabled_feature"));
            } else if (!playerFam.isMarried()) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_backpack_no_partner"));
            } else if (!Config.marryBackpackOffline && Bukkit.getPlayer(UUID.fromString(playerFam.getPartner().getUUID())) == null) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", playerFam.getPartner().getName()));
            } else {
                OfflinePlayer partnerPlayer = playerFam.getPartner().getOfflinePlayer();
                sender.sendMessage(Language.prefix + Language.getMessage("marry_backpack_open"));
                Minepacks.getMinepacks().openBackpack(player, partnerPlayer, true);
            }
        }
    }
}
