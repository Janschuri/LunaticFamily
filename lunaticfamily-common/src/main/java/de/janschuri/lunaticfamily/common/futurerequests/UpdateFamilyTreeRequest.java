package de.janschuri.lunaticfamily.common.futurerequests;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticfamily.platform.FamilyTree;
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

        UUID uuid = UUID.fromString(in.readUTF());
        String background = in.readUTF();
        int size = in.readInt();

        List<String> familyList = new ArrayList<>();
        Map<String, String> names = new HashMap<>();
        Map<String, String> skins = new HashMap<>();
        Map<String, String> relationLangs = new HashMap<>();

        for (int i = 0; i < size; i++) {
            String relation = in.readUTF();
            String name = in.readUTF();
            String skinURL = in.readUTF();
            String relationLang = in.readUTF();

            familyList.add(relation);
            names.put(relation, name);
            skins.put(relation, skinURL);
            relationLangs.put(relation, relationLang);
        }

        FamilyTree familyTree = LunaticFamily.getPlatform().getFamilyTree();

        if (familyTree == null) {
            Logger.errorLog("FamilyTree is null. Please check if CrazyAdvancementsAPI is installed or disable it!");
        } else {
            success = familyTree.update("", uuid, background, familyList, names, skins, relationLangs);
        }

        if (!success) {
            Logger.debugLog( "UpdateFamilyTreeRequest: Failed to update family tree for player with UUID " + uuid + ".");
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

    public boolean get(String server, UUID uuid, String background, List<String> familyList, Map<String, String> names, Map<String, String> skins, Map<String, String> relationLangs) {
        if (new IsFamilyTreeMapLoadedRequest().get(server)) {
            Logger.debugLog("FamilyTreeMap is loaded.");
        } else {
            Logger.debugLog("FamilyTreeMap is not loaded.");
            new LoadFamilyTreeMapRequest().get(server);
        }


        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(uuid.toString());
        out.writeUTF(background);

        Logger.debugLog("UpdateFamilyTreeRequest: " + familyList.toString());

        int size = familyList.size();
        out.writeInt(size);


        for (String relation : familyList) {
            out.writeUTF(relation);
            out.writeUTF(names.get(relation));
            out.writeUTF(skins.get(relation));
            out.writeUTF(relationLangs.get(relation));
        }

        return sendRequest(server, out.toByteArray());
    }
}
