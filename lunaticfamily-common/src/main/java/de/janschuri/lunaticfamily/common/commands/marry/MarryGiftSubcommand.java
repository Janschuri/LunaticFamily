package de.janschuri.lunaticfamily.common.commands.marry;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.commands.Subcommand;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayerImpl;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.common.utils.Utils;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;

import java.util.UUID;

public class MarryGiftSubcommand extends Subcommand {
    private static final String MAIN_COMMAND = "marry";
    private static final String NAME = "gift";
    private static final String PERMISSION = "lunaticfamily.marry.gift";

    public MarryGiftSubcommand() {
        super(MAIN_COMMAND, NAME, PERMISSION);
    }
    @Override
    public boolean execute(Sender sender, String[] args) {
        if (!(sender instanceof PlayerSender)) {
            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_console_command"));
        } else if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_permission"));
        } else {
            PlayerSender player = (PlayerSender) sender;
            UUID playerUUID = player.getUniqueId();
            FamilyPlayerImpl playerFam = new FamilyPlayerImpl(playerUUID);

            if (!playerFam.isMarried()) {
                player.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_gift_no_partner"));
                return true;
            } else if (!player.hasPermission("lunaticFamily.marry.gift")) {
                player.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("no_permission"));
                return true;
            }

            UUID partnerUUID = playerFam.getPartner().getUniqueId();
            PlayerSender partner = LunaticLib.getPlatform().getPlayerSender(partnerUUID);

            if (!partner.isOnline()) {
                player.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("player_offline").replace("%player%", partner.getName()));
                return true;
            }

            if (!Utils.isPlayerOnRegisteredServer(partnerUUID)) {
                player.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("player_not_on_whitelisted_server").replace("%player%", partner.getName()).replace("%server%", partner.getServerName()));
                return true;
            }

            if (!player.hasItemInMainHand()) {
                player.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_gift_empty_hand"));
                return true;
            }


                byte[] item = player.getItemInMainHand();
                if (partner.giveItemDrop(item)) {
                    player.removeItemInMainHand();
                    player.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_gift_sent").replace("%player%", partner.getName()));
                    partner.sendMessage(LunaticFamily.getLanguageConfig().getPrefix() + LunaticFamily.getLanguageConfig().getMessage("marry_gift_got").replace("%player%", player.getName()));
                } else {
                    Logger.errorLog("Error while giving item to partner.");
                }
        }
        return true;
    }
}
