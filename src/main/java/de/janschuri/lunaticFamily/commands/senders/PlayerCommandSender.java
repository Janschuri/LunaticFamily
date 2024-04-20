package de.janschuri.lunaticFamily.commands.senders;

import de.janschuri.lunaticFamily.handler.FamilyPlayer;

import java.util.UUID;

public abstract class PlayerCommandSender extends CommandSender {
    private final UUID uuid;

    protected PlayerCommandSender(UUID uuid) {
        this.uuid = uuid;
    }

    protected PlayerCommandSender(String name) {
        this.uuid = getUniqueId(name);
    }

    public UUID getUniqueId() {
        return uuid;
    }
    public FamilyPlayer getFamilyPlayer() {
        return new FamilyPlayer(uuid);
    }
    public abstract UUID getUniqueId(String name);
    public abstract String getName();
    public abstract boolean chat(String message);
    public abstract boolean chat(String message, int delay);
    public abstract boolean hasItemInMainHand();
    public abstract byte[] getItemInMainHand();
    public abstract boolean removeItemInMainHand();
    public abstract boolean giveItemDrop(byte[] item);
    public abstract boolean hasPermission(String permission);

    public abstract boolean hasEnoughMoney(String... withdrawKeys);
    public abstract boolean hasEnoughMoney(double factor, String... withdrawKeys);
    public abstract boolean withdrawMoney(String... withdrawKeys);
    public abstract boolean withdrawMoney(double factor, String... withdrawKeys);
    public abstract double[] getPositionBetween(UUID partnerUUID);
    public abstract void spawnParticleCloud(double[] position, String particleString);
    public abstract void spawnKissParticles(UUID partnerUUID);
    public abstract boolean isOnline();
    public abstract boolean isInRange(UUID playerUUID, double range);
    public abstract boolean exists();
    public abstract PlayerCommandSender getPlayerCommandSender(UUID uuid);
    public abstract PlayerCommandSender getPlayerCommandSender(String name);
    public abstract void sendAdoptRequest(UUID uuid);
    public abstract void sendMarryRequest(UUID uuid);
    public abstract void sendMarryPriestRequest(UUID player1UUID, UUID player2UUID);
    public abstract void sendSiblingRequest(UUID uuid);
    public abstract void onJoinEvent();
    public abstract void onQuitEvent();

    public abstract boolean isSameServer(UUID uuid);

}
