package de.janschuri.lunaticFamily.futurerequests;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.database.tables.PlayerDataTable;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.handler.FamilyTree;
import de.janschuri.lunaticFamily.utils.Logger;
import de.janschuri.lunaticlib.config.AbstractDatabaseConfig;
import de.janschuri.lunaticlib.futurerequests.FutureRequest;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import org.bukkit.Bukkit;

import java.util.*;
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

        boolean success = false;

        UUID uuid = UUID.fromString(in.readUTF());
        String background = in.readUTF();
        int size = in.readInt();

        List<String> familyList = new ArrayList<>();
        Map<String, UUID> uuids = new HashMap<>();
        Map<String, String> names = new HashMap<>();
        Map<String, String> skins = new HashMap<>();
        Map<String, String> relationLangs = new HashMap<>();

        for (int i = 0; i < size; i++) {
            String relation = in.readUTF();
            UUID relationUUID = UUID.fromString(in.readUTF());
            String name = in.readUTF();
            String skinURL = in.readUTF();
            String relationLang = in.readUTF();

            familyList.add(relation);
            uuids.put(relation, relationUUID);
            names.put(relation, name);
            skins.put(relation, skinURL);
            relationLangs.put(relation, relationLang);
        }

        if (Bukkit.getPlayer(uuid) == null) {
            Logger.debugLog( "UpdateFamilyTreeRequest: Player with UUID " + uuid + " not found on the server.");
            return;
        } else {
            Logger.debugLog( "UpdateFamilyTreeRequest: Player with UUID " + uuid + " found on the server.");
            FamilyTree.updateFamilyTree(uuid, background, familyList, uuids, names, skins, relationLangs);
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

    public boolean get(int id) {
        FamilyPlayer familyPlayer = new FamilyPlayer(id);
        UUID uuid = familyPlayer.getUniqueId();
        String background = familyPlayer.getBackground();
        Map<String, Integer> familyMap = familyPlayer.getFamilyMap();
        familyMap.put("ego", id);
        int size = familyMap.size();

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(uuid.toString());
        out.writeUTF(background);
        out.writeInt(size);
        for (Map.Entry<String, Integer> entry : familyMap.entrySet()) {
            FamilyPlayer relationFam = new FamilyPlayer(entry.getValue());
            String relationLang = Language.getRelation(entry.getKey(), relationFam.getGender());
            UUID relationUUID = relationFam.getUniqueId();


            String skinURL = relationFam.getSkinURL();

            out.writeUTF(entry.getKey());
            out.writeUTF(relationUUID.toString());
            out.writeUTF(relationFam.getName());
            out.writeUTF(skinURL);
            out.writeUTF(relationLang);
        }

        return sendRequest(out.toByteArray());
    }
}
