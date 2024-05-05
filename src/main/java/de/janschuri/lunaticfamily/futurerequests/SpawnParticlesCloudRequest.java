package de.janschuri.lunaticfamily.futurerequests;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticfamily.utils.Utils;
import de.janschuri.lunaticlib.futurerequests.FutureRequest;
import org.bukkit.Bukkit;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SpawnParticlesCloudRequest extends FutureRequest<Boolean> {

    private static final String REQUEST_NAME = "LunaticFamily:SpawnParticlesCloudRequest";
    private static final ConcurrentHashMap<Integer, CompletableFuture<Boolean>> REQUEST_MAP = new ConcurrentHashMap<>();

    public SpawnParticlesCloudRequest() {
        super(REQUEST_NAME, REQUEST_MAP);
    }

    @Override
    protected void handleRequest(int requestId, ByteArrayDataInput in) {
        UUID uuid = UUID.fromString(in.readUTF());
        double[] position = new double[3];
        position[0] = in.readDouble();
        position[1] = in.readDouble();
        position[2] = in.readDouble();
        String particleString = in.readUTF();

        boolean success = false;

        if (Bukkit.getPlayer(uuid) == null) {
            return;
        } else {
            success = Utils.spawnParticleCloud(uuid, position, particleString.toLowerCase(Locale.ROOT));
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeBoolean(success);
        sendResponse(requestId, out.toByteArray());
    }

    @Override
    protected void handleResponse(int requestId, ByteArrayDataInput in) {
        boolean success = in.readBoolean();
        completeRequest(requestId, success);
    }

    public boolean get(UUID uuid, double[] position, String particleString) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(uuid.toString());
        out.writeDouble(position[0]);
        out.writeDouble(position[1]);
        out.writeDouble(position[2]);
        out.writeUTF(particleString);
        return sendRequest(out.toByteArray());
    }
}
