package de.janschuri.lunaticFamily.utils;


import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.proxy.Player;
import de.janschuri.lunaticFamily.VelocityLunaticFamily;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class VelocityUtils extends Utils {

    private final AtomicInteger requestIdGenerator = new AtomicInteger(0);

    private final int timeout = 5;
    private final TimeUnit unit = TimeUnit.SECONDS;

    @Override
    public String getPlayerName(UUID uuid) {
        Optional<Player> player = VelocityLunaticFamily.getProxy().getPlayer(uuid);
        return player.map(Player::getUsername).orElse(null);
    }

    @Override
    public void sendConsoleCommand(String command) {

    }

    @Override
    public void updateFamilyTree(int id) {
        FamilyPlayer playerFam = new FamilyPlayer(id);
        UUID uuid = playerFam.getUniqueId();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("UpdateFamilyTree");
        out.writeInt(id);
        out.writeUTF(uuid.toString());
        VelocityLunaticFamily.sendPluginMessage(out.toByteArray());
    }

    @Override
    public boolean isPlayerOnWhitelistedServer(UUID uuid) {
        if(!PluginConfig.enabledServerWhitelist) {
            return true;
        }
        List<String> servers = PluginConfig.serverWhitelist;
        Optional<Player> player = VelocityLunaticFamily.getProxy().getPlayer(uuid);
        return player.map(value -> servers.contains(value.getCurrentServer().get().getServerInfo().getName())).orElse(false);
    }

    @Override
    public boolean hasEnoughMoney(UUID uuid, String... withdrawKeys) {
        return hasEnoughMoney(uuid, 1.0, withdrawKeys);
    }

    @Override
    public boolean hasEnoughMoney(UUID uuid, double factor, String... withdrawKeys) {
        if (PluginConfig.useVault) {
            double amount = 0.0;
            for (String key : withdrawKeys) {
                if (PluginConfig.commandWithdraws.containsKey(key)) {
                    amount += PluginConfig.commandWithdraws.get(key);
                }
            }
            amount *= factor;

            int requestId = requestIdGenerator.incrementAndGet();
            CompletableFuture<Boolean> responseFuture = new CompletableFuture<>();

            VelocityLunaticFamily.booleanRequestMap.put(requestId, responseFuture);

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("HasEnoughMoneyRequest");
            out.writeInt(requestId);
            out.writeUTF(uuid.toString());
            out.writeDouble(amount);
            VelocityLunaticFamily.sendPluginMessage(out.toByteArray());

            try {
                return responseFuture.get(timeout, unit);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
                return false;
            }



        } else {
            return true;
        }
    }

    @Override
    public boolean withdrawMoney(UUID uuid, String... withdrawKeys) {
        return withdrawMoney(uuid, 1.0, withdrawKeys);
    }

    @Override
    public boolean withdrawMoney(UUID uuid, double factor, String... withdrawKeys) {
        if (PluginConfig.useVault) {
            double amount = 0.0;
            for (String key : withdrawKeys) {
                if (PluginConfig.commandWithdraws.containsKey(key)) {
                    amount += PluginConfig.commandWithdraws.get(key);
                }
            }

            amount *= factor;

            int requestId = requestIdGenerator.incrementAndGet();
            CompletableFuture<Boolean> responseFuture = new CompletableFuture<>();

            VelocityLunaticFamily.booleanRequestMap.put(requestId, responseFuture);

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("WithdrawMoneyRequest");
            out.writeInt(requestId);
            out.writeUTF(uuid.toString());
            out.writeDouble(amount);
            VelocityLunaticFamily.sendPluginMessage(out.toByteArray());

            try {
                return responseFuture.get(timeout, unit);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
                return false;
            }

        } else {
            return true;
        }
    }

    @Override
    public void spawnParticleCloud(UUID uuid, double[] position, String particleString) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("SpawnParticleCloudRequest");
        out.writeUTF(uuid.toString());
        out.writeDouble(position[0]);
        out.writeDouble(position[1]);
        out.writeDouble(position[2]);
        out.writeUTF(particleString);
        VelocityLunaticFamily.sendPluginMessage(out.toByteArray());
    }
}
