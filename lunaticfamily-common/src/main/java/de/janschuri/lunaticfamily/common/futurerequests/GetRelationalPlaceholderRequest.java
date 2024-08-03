package de.janschuri.lunaticfamily.common.futurerequests;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticfamily.common.handler.Placeholder;
import de.janschuri.lunaticlib.common.futurerequests.FutureRequest;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class GetRelationalPlaceholderRequest extends FutureRequest<String> {

    private static final String REQUEST_NAME = "LunaticFamily:GetPlaceholder";
    private static final ConcurrentHashMap<Integer, CompletableFuture<String>> requestMap = new ConcurrentHashMap<>();

    public GetRelationalPlaceholderRequest() {
        super(REQUEST_NAME, requestMap);
    }

    @Override
    protected void handleRequest(int requestId, ByteArrayDataInput in) {
        UUID uuid2 = UUID.fromString(in.readUTF());
        UUID uuid1 = UUID.fromString(in.readUTF());
        String placeholder = in.readUTF();

        String result = "";

        if (Placeholder.getPlaceholder(uuid1, uuid2, placeholder) != null) {
            result = Placeholder.getPlaceholder(uuid1, uuid2, placeholder);
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

    public String get(UUID uuid1, UUID uuid2, String placeholder) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(uuid1.toString());
        out.writeUTF(uuid2.toString());
        out.writeUTF(placeholder);
        return sendRequest(out.toByteArray());
    }

}