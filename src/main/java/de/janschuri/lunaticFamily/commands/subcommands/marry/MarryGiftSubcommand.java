package de.janschuri.lunaticFamily.commands.subcommands.marry;

import de.janschuri.lunaticFamily.commands.subcommands.Subcommand;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticFamily.utils.Logger;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;

import java.util.UUID;

public class MarryGiftSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "gift";
    private static final String PERMISSION = "lunaticfamily.marry.gift";

    public MarryGiftSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(AbstractSender sender, String[] args) {
        if (!(sender instanceof AbstractPlayerSender)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
        } else {
            AbstractPlayerSender player = (AbstractPlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);

            if (!playerFam.isMarried()) {
                player.sendMessage(language.getPrefix() + language.getMessage("marry_gift_no_partner"));
                return true;
            } else if (!player.hasPermission("lunaticFamily.marry.gift")) {
                player.sendMessage(language.getPrefix() + language.getMessage("no_permission"));
                return true;
            }

            UUID partnerUUID = playerFam.getPartner().getUniqueId();
            AbstractPlayerSender partner = AbstractSender.getPlayerSender(partnerUUID);

            if (!partner.isOnline()) {
                player.sendMessage(language.getPrefix() + language.getMessage("player_offline").replace("%player%", partner.getName()));
                return true;
            }

            if (!Utils.isPlayerOnRegisteredServer(partnerUUID)) {
                player.sendMessage(language.getPrefix() + language.getMessage("player_not_on_whitelisted_server").replace("%player%", partner.getName()).replace("%server%", partner.getServerName()));
                return true;
            }

            if (!player.hasItemInMainHand()) {
                player.sendMessage(language.getPrefix() + language.getMessage("marry_gift_empty_hand"));
                return true;
            }


                byte[] item = player.getItemInMainHand();
                if (partner.giveItemDrop(item)) {
                    player.removeItemInMainHand();
                    player.sendMessage(language.getPrefix() + language.getMessage("marry_gift_sent").replace("%player%", partner.getName()));
                    partner.sendMessage(language.getPrefix() + language.getMessage("marry_gift_got").replace("%player%", player.getName()));
                } else {
                    Logger.errorLog("Error while giving item to partner.");
                }
        }
        return true;
    }
}
