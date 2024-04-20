package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.commands.senders.CommandSender;
import de.janschuri.lunaticFamily.commands.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;

public class MarryKissSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "kiss";
    private static final String permission = "lunaticfamily.marry";

    public MarryKissSubcommand() {
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

            if (!playerFam.isMarried()) {
                sender.sendMessage(Language.prefix + Language.getMessage("marry_kiss_no_partner"));
                return true;
            }

            PlayerCommandSender partner = player.getPlayerCommandSender(playerFam.getPartner().getUniqueId());

            if (!partner.isOnline()) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", partner.getName()));
                return true;
            }

            if (!player.isSameServer(partner.getUniqueId())) {
                sender.sendMessage(Language.prefix + Language.getMessage("player_not_same_server").replace("%player%", partner.getName()));
                return true;
            }

            if (!player.isInRange(partner.getUniqueId(), PluginConfig.marryKissRange)) {
                player.sendMessage(Language.prefix + Language.getMessage("player_too_far_away").replace("%player%", partner.getName()));
                return true;
            }

            player.spawnKissParticles(partner.getUniqueId());

        }
        return true;
    }
}
