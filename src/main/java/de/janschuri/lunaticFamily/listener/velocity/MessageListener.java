package de.janschuri.lunaticFamily.listener.velocity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import de.janschuri.lunaticFamily.Velocity;

import java.util.concurrent.CompletableFuture;

public class MessageListener {

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        byte[] message = event.getData();
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        if (subchannel.equals("IsInRangeResponse")) {
            int requestId = in.readInt();
            boolean isInRange = in.readBoolean();
            CompletableFuture<Boolean> request = Velocity.booleanRequestMap.get(requestId);
            request.complete(isInRange);
        }
        if (subchannel.equals("HasEnoughMoneyResponse")) {
            int requestId = in.readInt();
            boolean hasEnoughMoney = in.readBoolean();
            CompletableFuture<Boolean> request = Velocity.booleanRequestMap.get(requestId);
            request.complete(hasEnoughMoney);
        }
        if (subchannel.equals("HasItemInMainHandResponse")) {
            int requestId = in.readInt();
            boolean hasItem = in.readBoolean();
            CompletableFuture<Boolean> request = Velocity.booleanRequestMap.get(requestId);
            request.complete(hasItem);
        }
        if (subchannel.equals("GetItemInMainHandResponse")) {
            int requestId = in.readInt();
            byte[] item = new byte[in.readInt()];
            in.readFully(item);
            CompletableFuture<byte[]> request = Velocity.byteArrayRequestMap.get(requestId);
            request.complete(item);
        }
        if (subchannel.equals("RemoveItemInMainHandResponse")) {
            int requestId = in.readInt();
            boolean success = in.readBoolean();
            CompletableFuture<Boolean> request = Velocity.booleanRequestMap.get(requestId);
            request.complete(success);
        }
        if (subchannel.equals("GiveItemDropResponse")) {
            int requestId = in.readInt();
            boolean success = in.readBoolean();
            CompletableFuture<Boolean> request = Velocity.booleanRequestMap.get(requestId);
            request.complete(success);
        }

        if (subchannel.equals("GetPositionResponse")) {
            int requestId = in.readInt();
            double[] position = new double[3];
            position[0] = in.readDouble();
            position[1] = in.readDouble();
            position[2] = in.readDouble();
            CompletableFuture<double[]> request = Velocity.doubleArrayRequestMap.get(requestId);
            request.complete(position);
        }
    }
}
