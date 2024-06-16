package de.janschuri.lunaticfamily.common.futurerequests;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.handler.Placeholder;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.FamilyTree;
import de.janschuri.lunaticlib.common.futurerequests.FutureRequest;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class GetPlaceholderRequest extends FutureRequest<String> {

    private static final String REQUEST_NAME = "LunaticFamily:GetPlaceholder";
    private static final ConcurrentHashMap<Integer, CompletableFuture<String>> requestMap = new ConcurrentHashMap<>();

    public GetPlaceholderRequest() {
        super(REQUEST_NAME, requestMap);
    }

    @Override
    protected void handleRequest(int requestId, ByteArrayDataInput in) {
        UUID uuid = UUID.fromString(in.readUTF());
        String placeholder = in.readUTF();

        String result = "";

        if (Placeholder.getPlaceholder(uuid, placeholder) != null) {
            result = Placeholder.getPlaceholder(uuid, placeholder);
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(result);
        sendResponse(requestId, out.toByteArray());
    }

    @Override
    protected void handleResponse(int requestId, ByteArrayDataInput in) {
        String result = in.readUTF();
        completeRequest(requestId, result);
    }

    public String get(UUID uuid, String placeholder) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(uuid.toString());
        out.writeUTF(placeholder);
        return sendRequest(out.toByteArray());
    }

}