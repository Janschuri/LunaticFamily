package de.janschuri.lunaticfamily.common.futurerequests;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticfamily.TreeAdvancement;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.FamilyTreeManager;
import de.janschuri.lunaticlib.common.futurerequests.FutureRequest;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UpdateFamilyTreeRequest extends FutureRequest<Boolean> {

    private static final String REQUEST_NAME = "LunaticFamily:UpdateFamilyTree";
    private static final ConcurrentHashMap<Integer, CompletableFuture<Boolean>> requestMap = new ConcurrentHashMap<>();

    public UpdateFamilyTreeRequest() {
        super(REQUEST_NAME, requestMap);
    }

    @Override
    protected void handleRequest(int requestId, ByteArrayDataInput in) {
        boolean success = false;

        UUID uuid = UUID.fromString(in.readUTF());
        int length = in.readInt();
        byte[] byteArray = new byte[length];
        in.readFully(byteArray);

        List<TreeAdvancement> treeAdvancements = fromByteArray(byteArray);

        if (treeAdvancements != null) {
            FamilyTreeManager familyTreeManager = LunaticFamily.getPlatform().getFamilyTreeManager();

            success = familyTreeManager.update("", uuid, treeAdvancements);
        } else {
            Logger.errorLog("Failed to deserialize TreeAdvancements");
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

    public boolean get(String server, UUID uuid, List<TreeAdvancement> treeAdvancements) {

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(uuid.toString());

        byte[] byteArray = toByteArray(treeAdvancements);
        out.writeInt(byteArray.length);
        out.write(byteArray);

        return sendRequest(server, out.toByteArray());
    }

    public static byte[] toByteArray(List<TreeAdvancement> treeAdvancements) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream)) {

            // Serialize the list
            objectOutputStream.writeObject(treeAdvancements);

            return byteStream.toByteArray(); // Return serialized byte array
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0]; // Return empty array in case of failure
        }
    }

    public static List<TreeAdvancement> fromByteArray(byte[] byteArray) {
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(byteArray);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteStream)) {

            // Deserialize the byte array into a List<TreeAdvancement>
            return (List<TreeAdvancement>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null; // Return null in case of failure
        }
    }
}
