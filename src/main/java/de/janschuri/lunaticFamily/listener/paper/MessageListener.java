package de.janschuri.lunaticFamily.listener.paper;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.external.FamilyTree;
import de.janschuri.lunaticFamily.utils.Utils;
import de.janschuri.lunaticFamily.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class MessageListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] message) {

        if (!channel.equals("lunaticfamily:proxy")) {
            return;
        }

        if (!LunaticFamily.enabledProxy) {
            LunaticFamily.enabledProxy = true;
            Logger.infoLog("Connected to Proxy.");
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        Logger.debugLog( "Received Plugin Message: " + subchannel);

        if (!PluginConfig.useProxy) {
            Logger.warnLog("Detected Proxy Message, but Proxy is disabled in the config. Enable it to connect to the Proxy.");
            return;
        }

        if (subchannel.equals("HasEnoughMoneyRequest")) {
            int requestId = in.readInt();
            UUID playerUUID = UUID.fromString(in.readUTF());
            double amount = in.readDouble();

            boolean hasEnoughMoney = Utils.getUtils().hasEnoughMoney(playerUUID, amount);

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

        if (subchannel.equals("SpawnParticleCloud")) {
            UUID playerUUID = UUID.fromString(in.readUTF());
            double x = in.readDouble();
            double y = in.readDouble();
            double z = in.readDouble();
            String particleType = in.readUTF();

            if (Bukkit.getPlayer(playerUUID) == null) {
                return;
            }

            double[] position = new double[]{x, y, z};

            Utils.getUtils().spawnParticleCloud(playerUUID, position, particleType);
        }
    }



}
