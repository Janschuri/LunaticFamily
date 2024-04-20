package de.janschuri.lunaticFamily.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.senders.PaperPlayerCommandSender;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.handler.FamilyTree;
import de.janschuri.lunaticFamily.utils.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class ProxyListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] message) {
        if (!LunaticFamily.isProxy) {
            LunaticFamily.isProxy = true;
            Logger.infoLog("Connected to Proxy.");
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        Logger.debugLog("ProxyListener: " + subchannel);

        if (!PluginConfig.isBackend) {
            Logger.warnLog("Detected Proxy Message, but Proxy is disabled in the config. Enable it to connect to the Proxy.");
            return;
        }

        if (subchannel.equals("MarryKissEvent")) {
            UUID playerUUID = UUID.fromString(in.readUTF());
            UUID partnerUUID = UUID.fromString(in.readUTF());
            double range = in.readDouble();
            PaperPlayerCommandSender player = new PaperPlayerCommandSender(playerUUID);
            PaperPlayerCommandSender partner = new PaperPlayerCommandSender(partnerUUID);

            player.spawnKissParticles(partnerUUID);
        }

        if (subchannel.equals("IsInRangeRequest")) {
            int requestId = in.readInt();
            UUID playerUUID = UUID.fromString(in.readUTF());
            UUID partnerUUID = UUID.fromString(in.readUTF());
            double range = in.readDouble();

            PaperPlayerCommandSender player = new PaperPlayerCommandSender(playerUUID);
            PaperPlayerCommandSender partner = new PaperPlayerCommandSender(partnerUUID);
            if (!player.isOnline() || !partner.isOnline()) {
                return;
            }

            boolean isInRange = player.isInRange(partnerUUID, range);

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("IsInRangeResponse");
            out.writeInt(requestId);
            out.writeBoolean(isInRange);

            LunaticFamily.sendPluginMessage(out.toByteArray());
        }

        if (subchannel.equals("HasEnoughMoneyRequest")) {
            int requestId = in.readInt();
            UUID playerUUID = UUID.fromString(in.readUTF());
            double amount = in.readDouble();

            PaperPlayerCommandSender player = new PaperPlayerCommandSender(playerUUID);

            boolean hasEnoughMoney = player.hasEnoughMoney(amount);

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("HasEnoughMoneyResponse");
            out.writeInt(requestId);
            out.writeBoolean(hasEnoughMoney);

            LunaticFamily.sendPluginMessage(out.toByteArray());
        }
        if (subchannel.equals("UpdateFamilyTree")) {
            int id = in.readInt();
            UUID uuid = UUID.fromString(in.readUTF());

            if (Database.getDatabase().getUUID(id) == null) {
                Logger.warnLog("Player with ID " + id + " not found in the database. Proxy and " + Bukkit.getServer().getName() + " are not connected to the same Database.");
                return;
            }
            if (!Database.getDatabase().getUUID(id).equals(uuid)) {
                Logger.warnLog("UUID of Player with ID " + id + " does not match the database. Proxy and " + Bukkit.getServer().getName() + " are not connected to the same Database.");
                return;
            }

            if (Bukkit.getPlayer(uuid) != null) {
                new FamilyTree(id);
                return;
            }
        }

        if (subchannel.equals("HasItemInMainHandRequest")) {

            int requestId = in.readInt();
            UUID playerUUID = UUID.fromString(in.readUTF());

            if (Bukkit.getPlayer(playerUUID) == null) {
                return;
            }

            PaperPlayerCommandSender player = new PaperPlayerCommandSender(playerUUID);

            boolean hasItemInMainHand = player.hasItemInMainHand();

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("HasItemInMainHandResponse");
            out.writeInt(requestId);
            out.writeBoolean(hasItemInMainHand);

            LunaticFamily.sendPluginMessage(out.toByteArray());
        }

        if (subchannel.equals("GetItemInMainHandRequest")) {
            int requestId = in.readInt();
            UUID playerUUID = UUID.fromString(in.readUTF());

            if (Bukkit.getPlayer(playerUUID) == null) {
                return;
            }

            PaperPlayerCommandSender player = new PaperPlayerCommandSender(playerUUID);

            byte[] item = player.getItemInMainHand();

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("GetItemInMainHandResponse");
            out.writeInt(requestId);
            out.writeInt(item.length);
            out.write(item);

            LunaticFamily.sendPluginMessage(out.toByteArray());
        }

        if (subchannel.equals("RemoveItemInMainHandRequest")) {
            int requestId = in.readInt();
            UUID playerUUID = UUID.fromString(in.readUTF());

            if (Bukkit.getPlayer(playerUUID) == null) {
                return;
            }

            PaperPlayerCommandSender player = new PaperPlayerCommandSender(playerUUID);

            boolean removed = player.removeItemInMainHand();

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("RemoveItemInMainHandResponse");
            out.writeInt(requestId);
            out.writeBoolean(removed);

            LunaticFamily.sendPluginMessage(out.toByteArray());
        }

        if (subchannel.equals("GiveItemDropRequest")) {
            int requestId = in.readInt();
            UUID playerUUID = UUID.fromString(in.readUTF());

            if (Bukkit.getPlayer(playerUUID) == null) {
                return;
            }

            byte[] item = new byte[in.readInt()];
            in.readFully(item);

            PaperPlayerCommandSender player = new PaperPlayerCommandSender(playerUUID);

            boolean dropped = player.giveItemDrop(item);

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("GiveItemDropResponse");
            out.writeInt(requestId);
            out.writeBoolean(dropped);

            LunaticFamily.sendPluginMessage(out.toByteArray());
        }
    }



}
