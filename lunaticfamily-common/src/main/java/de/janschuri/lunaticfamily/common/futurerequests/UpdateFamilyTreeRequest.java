package de.janschuri.lunaticfamily.common.futurerequests;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.FamilyTreeManager;
import de.janschuri.lunaticlib.common.futurerequests.FutureRequest;

import java.util.*;
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

        int familyPlayerID = in.readInt();

        FamilyTreeManager familyTreeManager = LunaticFamily.getPlatform().getFamilyTree();
        success = familyTreeManager.update("", familyPlayerID);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeBoolean(success);
        sendResponse(requestId, out.toByteArray());
    }

    @Override
    protected void handleResponse(int requestId, ByteArrayDataInput in) {
        boolean success = in.readBoolean();
        completeRequest(requestId, success);
    }

    public boolean get(String server, int familyPlayerID) {
        if (new IsFamilyTreeMapLoadedRequest().get(server)) {
            Logger.debugLog("FamilyTreeMap is loaded.");
        } else {
            Logger.debugLog("FamilyTreeMap is not loaded.");
            new LoadFamilyTreeMapRequest().get(server);
        }


        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeInt(familyPlayerID);

        return sendRequest(server, out.toByteArray());
    }
}
