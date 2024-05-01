package de.janschuri.lunaticFamily.futurerequests;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.handler.FamilyTree;
import de.janschuri.lunaticFamily.utils.Logger;
import de.janschuri.lunaticlib.futurerequests.FutureRequest;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UpdateFamilyTreeRequest extends FutureRequest<Boolean> {

    private static final String REQUEST_NAME = "LunaticFamily:UpdateFamilyTreeRequest";
    private static final ConcurrentHashMap<Integer, CompletableFuture<Boolean>> requestMap = new ConcurrentHashMap<>();

    public UpdateFamilyTreeRequest() {
        super(REQUEST_NAME, requestMap);
    }

    @Override
    protected void handleRequest(int requestId, ByteArrayDataInput in) {
        int id = in.readInt();
        UUID uuid = UUID.fromString(in.readUTF());
        boolean success = false;

        if (Database.getDatabase().getUUID(id) == null) {
            Logger.warnLog("Player with ID " + id + " not found in the database. Proxy and " + Bukkit.getServer().getName() + " are not connected to the same Database.");
        }
        if (!Database.getDatabase().getUUID(id).equals(uuid)) {
            Logger.warnLog("UUID of Player with ID " + id + " does not match the database. Proxy and " + Bukkit.getServer().getName() + " are not connected to the same Database.");
        }

        if (Bukkit.getPlayer(uuid) == null) {
            return;
        } else {
            new FamilyTree(id);
            success = true;
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

    public boolean get(int id, UUID uuid) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(id);
        out.writeUTF(uuid.toString());
        return sendRequest(out.toByteArray());
    }
}
