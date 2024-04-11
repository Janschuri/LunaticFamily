package de.janschuri.lunaticFamily.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Logger;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class ProxyListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        LunaticFamily.isProxy = true;
        ByteArrayDataInput in = ByteStreams.newDataInput(message);

        String subchannel = in.readUTF();
        if (subchannel.equals("PlayerJoinEvent")) {
            String playerUUID = in.readUTF();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);
            if (playerFam.isMarried()) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("PlayerJoinEvent");
                out.writeUTF(playerUUID);
                out.writeUTF(playerFam.getPartner().getUUID());
                LunaticFamily.sendPluginMessage(out.toByteArray());
            }
        }
        if (subchannel.equals("PlayerJumpEvent")) {

        }
        if (subchannel.equals("PartnerIsOnline")) {
            String uuid = in.readUTF();
            String partnerUUID = in.readUTF();
            FamilyPlayer playerFam = new FamilyPlayer(uuid);
            FamilyPlayer partnerFam = new FamilyPlayer(partnerUUID);
            playerFam.sendMessage(Language.prefix + Language.getMessage("marry_partner_online") + 3);
            partnerFam.sendMessage(Language.prefix + Language.getMessage("marry_partner_online") + 4);
        }
        if (subchannel.equals("PartnerIsOffline")) {
            String uuid = in.readUTF();
            FamilyPlayer playerFam = new FamilyPlayer(uuid);
            if(playerFam.isMarried()) {
                playerFam.getPartner().sendMessage(Language.prefix + Language.getMessage("marry_partner_offline") + 5);
            }
        }
        if (subchannel.equals("PlayerLeaveEvent")) {
            String uuid = in.readUTF();
            FamilyPlayer offlineFamilyPlayer = new FamilyPlayer(uuid);
            if (offlineFamilyPlayer.isMarried()) {
                OfflinePlayer partner = offlineFamilyPlayer.getPartner().getOfflinePlayer();
                Logger.debugLog(partner.getName());
                if (partner.isOnline()) {
                    Player onlinePartner = partner.getPlayer();
                    onlinePartner.sendMessage(Language.prefix + Language.getMessage("marry_partner_offline") + 6);
                }
            }
        }
    }



}
