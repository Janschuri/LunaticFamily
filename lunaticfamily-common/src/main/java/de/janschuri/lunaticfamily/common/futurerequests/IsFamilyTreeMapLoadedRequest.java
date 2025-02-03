package de.janschuri.lunaticfamily.common.futurerequests;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.FamilyTreeManager;
import de.janschuri.lunaticlib.common.futurerequests.FutureRequest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class IsFamilyTreeMapLoadedRequest extends FutureRequest<Boolean> {

        private static final String REQUEST_NAME = "LunaticFamily:IsFamilyTreeMapLoaded";
        private static final ConcurrentHashMap<Integer, CompletableFuture<Boolean>> requestMap = new ConcurrentHashMap<>();

        public IsFamilyTreeMapLoadedRequest() {
            super(REQUEST_NAME, requestMap);
        }

        @Override
        protected void handleRequest(int requestId, ByteArrayDataInput in) {
            FamilyTreeManager familyTreeManager = LunaticFamily.getPlatform().getFamilyTree();


            boolean success = false;

            if (familyTreeManager == null) {
                Logger.errorLog("FamilyTree is null. Please check if CrazyAdvancementsAPI is installed or disable it!");
            } else {
                success = (familyTreeManager.isFamilyTreeMapLoaded());
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

        public Boolean get(String serverName) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            return sendRequest(serverName, out.toByteArray());
        }

}
