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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class ProxyListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        LunaticFamily.isProxy = true;
        Logger.debugLog("ProxyListener: ");
        ByteArrayDataInput in = ByteStreams.newDataInput(message);

        String subchannel = in.readUTF();
        Logger.debugLog("ProxyListener: " + subchannel);
        if (subchannel.equals("PlayerJoinEvent")) {
            String playerUUID = in.readUTF();
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);
            new BukkitRunnable() {
                public void run() {
                    if (playerFam.isMarried()) {
                        if (LunaticFamily.isPlayerOnline(playerFam.getPartner().getUUID())) {
                            playerFam.sendMessage(Language.prefix + Language.getMessage("marry_partner_online"));
                            playerFam.getPartner().sendMessage(Language.prefix + Language.getMessage("marry_partner_online"));
                        } else {
                            playerFam.sendMessage(Language.prefix + Language.getMessage("marry_partner_offline"));
                        }
                    }
                }
            }.runTaskLater(LunaticFamily.getInstance(), 5L);
        }
        if (subchannel.equals("PlayerJumpEvent")) {

        }
        if (subchannel.equals("OnlinePlayers")) {
            Set<String> players = new HashSet<>();
            try {
                while(true) {
                    players.add(in.readUTF());
                }
            } catch (IllegalStateException e) {
                LunaticFamily.proxyPlayers = players;
            }
        }
        if (subchannel.equals("PlayerLeaveEvent")) {
            String playerUUID = in.readUTF();
            LunaticFamily.proxyPlayers.remove(playerUUID);
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);
            if (playerFam.isMarried()) {
                playerFam.getPartner().sendMessage(Language.prefix + Language.getMessage("marry_partner_offline"));
            }
        }
    }



}
