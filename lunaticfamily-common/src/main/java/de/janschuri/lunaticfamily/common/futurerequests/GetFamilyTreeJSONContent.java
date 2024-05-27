package de.janschuri.lunaticfamily.common.futurerequests;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.FamilyTree;
import de.janschuri.lunaticlib.common.futurerequests.FutureRequest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class GetFamilyTreeJSONContent extends FutureRequest<String> {

    private static final String REQUEST_NAME = "LunaticFamily:UpdateFamilyTreeRequest";
    private static final ConcurrentHashMap<Integer, CompletableFuture<String>> requestMap = new ConcurrentHashMap<>();

    public GetFamilyTreeJSONContent() {
        super(REQUEST_NAME, requestMap);
    }

    @Override
    protected void handleRequest(int requestId, ByteArrayDataInput in) {
        FamilyTree familyTree = LunaticFamily.getPlatform().getFamilyTree();

        String content = "";

        if (familyTree == null) {
            Logger.errorLog("FamilyTree is null. Please check if CrazyAdvancementsAPI is installed or disable it!");
        } else {
            content = familyTree.getJSONContent();
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(content);
        sendResponse(requestId, out.toByteArray());
    }

    @Override
    protected void handleResponse(int requestId, ByteArrayDataInput in) {
        String content = in.readUTF();
        completeRequest(requestId, content);
    }

    public String get() {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        return sendRequest(out.toByteArray());
    }
}
