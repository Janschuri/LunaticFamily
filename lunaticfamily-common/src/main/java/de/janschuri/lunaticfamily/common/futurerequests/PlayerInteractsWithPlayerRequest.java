package de.janschuri.lunaticfamily.common.futurerequests;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.janschuri.lunaticfamily.common.listener.PlayerInteractsWithPlayerExecuter;
import de.janschuri.lunaticfamily.common.utils.Logger;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.futurerequests.FutureRequest;
import de.janschuri.lunaticlib.platform.bukkit.sender.PlayerSenderImpl;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerInteractsWithPlayerRequest extends FutureRequest<Boolean> {

    private static final String REQUEST_NAME = "LunaticFamily:PlayerInteractsWithPlayer";
    private static final ConcurrentHashMap<Integer, CompletableFuture<Boolean>> REQUEST_MAP = new ConcurrentHashMap<>();

    public PlayerInteractsWithPlayerRequest() {
        super(REQUEST_NAME, REQUEST_MAP);
    }

    @Override
    protected void handleRequest(int requestId, ByteArrayDataInput in) {
        UUID clickingPlayerUUID = UUID.fromString(in.readUTF());
        UUID clickedPlayerUUID = UUID.fromString(in.readUTF());
        boolean clickingPlayerIsSneaking = in.readBoolean();

        PlayerSender clickingPlayer = LunaticLib.getPlatform().getPlayerSender(clickingPlayerUUID);
        PlayerSender clickedPlayer = LunaticLib.getPlatform().getPlayerSender(clickedPlayerUUID);

        boolean success = PlayerInteractsWithPlayerExecuter.execute(clickingPlayer, clickedPlayer, clickingPlayerIsSneaking);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeBoolean(success);
        sendResponse(requestId, out.toByteArray());
    }

    @Override
    protected void handleResponse(int requestId, ByteArrayDataInput in) {
        boolean success = in.readBoolean();
        completeRequest(requestId, success);
    }

    public CompletableFuture<Boolean> get(PlayerSender clickingPlayer, PlayerSender clickedPlayer, boolean clickingPlayerIsSneaking) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(clickingPlayer.getUniqueId().toString());
        out.writeUTF(clickedPlayer.getUniqueId().toString());
        out.writeBoolean(clickingPlayerIsSneaking);
        return sendRequest(out.toByteArray());
    }
}
