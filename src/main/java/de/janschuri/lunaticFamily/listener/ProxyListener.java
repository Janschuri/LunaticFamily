package de.janschuri.lunaticFamily.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.utils.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
            return;
        }
        if (subchannel.equals("PlayerJumpEvent")) {
            return;
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
            return;
        }
        if (subchannel.equals("PlayerLeaveEvent")) {
            String playerUUID = in.readUTF();
            LunaticFamily.proxyPlayers.remove(playerUUID);
            FamilyPlayer playerFam = new FamilyPlayer(playerUUID);
            if (playerFam.isMarried()) {
                playerFam.getPartner().sendMessage(Language.prefix + Language.getMessage("marry_partner_offline"));
            }
            return;
        }
        if (subchannel.equals("MessageToPlayer")) {
            String playerUUID = in.readUTF();
            String messageToPlayer = in.readUTF();
            if (Bukkit.getPlayer(playerUUID) != null) {
                Bukkit.getPlayer(playerUUID).sendMessage(messageToPlayer);
            }
            return;
        }
        if (subchannel.equals("ComponentMessageToPlayer")) {
            String playerUUID = in.readUTF();
            Component messageToPlayer = GsonComponentSerializer.gson().deserialize(in.readUTF());
            Logger.debugLog(Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)).getName());
            if (Bukkit.getPlayer(UUID.fromString(playerUUID)) != null) {
                Bukkit.getPlayer(UUID.fromString(playerUUID)).sendMessage(messageToPlayer);
            }
            return;
        }
        if (subchannel.equals("DropItemToPlayer")) {
            String playerUUID = in.readUTF();
            Logger.debugLog(Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)).getName());
            if (Bukkit.getPlayer(UUID.fromString(playerUUID)) != null) {
                int arrayLength = in.readInt();
                byte[] itemBytes = new byte[arrayLength];
                for (int i = 0; i < arrayLength; i++) {
                    itemBytes[i] = in.readByte();
                }
                ItemStack item = ItemStack.deserializeBytes(itemBytes);
                Bukkit.getPlayer(UUID.fromString(playerUUID)).getWorld().dropItem(player.getLocation(), item);
            }
            return;
        }
    }



}
