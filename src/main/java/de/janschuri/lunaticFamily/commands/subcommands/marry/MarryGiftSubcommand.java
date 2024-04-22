package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.senders.CommandSender;
import de.janschuri.lunaticFamily.senders.PlayerCommandSender;
import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticFamily.utils.logger.Logger;

import java.util.UUID;

public class MarryGiftSubcommand extends Subcommand {
    private static final String mainCommand = "marry";
    private static final String name = "gift";
    private static final String permission = "lunaticfamily.marry.gift";

    public MarryGiftSubcommand() {
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
            UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (!playerFam.isMarried()) {
                player.sendMessage(Language.prefix + Language.getMessage("marry_gift_no_partner"));
                return true;
            } else if (!player.hasPermission("lunaticFamily.marry.gift")) {
                player.sendMessage(Language.prefix + Language.getMessage("no_permission"));
                return true;
            }

            UUID partnerUUID = playerFam.getPartner().getUniqueId();
            PlayerCommandSender partner = sender.getPlayerCommandSender(partnerUUID);

            if (!partner.isOnline()) {
                player.sendMessage(Language.prefix + Language.getMessage("player_offline").replace("%player%", partner.getName()));
                return true;
            }

            if (!Utils.getUtils().isPlayerOnWhitelistedServer(partnerUUID)) {
                player.sendMessage(Language.prefix + Language.getMessage("player_not_on_whitelisted_server").replace("%player%", partner.getName().replace("%server%", partner.getServerName())));
                return true;
            }

            if (!player.hasItemInMainHand()) {
                player.sendMessage(Language.prefix + Language.getMessage("marry_gift_empty_hand"));
                return true;
            }


                byte[] item = player.getItemInMainHand();
                if (partner.giveItemDrop(item)) {
                    player.removeItemInMainHand();
                    player.sendMessage(Language.prefix + Language.getMessage("marry_gift_sent").replace("%player%", partner.getName()));
                    partner.sendMessage(Language.prefix + Language.getMessage("marry_gift_got").replace("%player%", player.getName()));
                } else {
                    Logger.errorLog("Error while giving item to partner.");
                }
        }
        return true;
    }
}
