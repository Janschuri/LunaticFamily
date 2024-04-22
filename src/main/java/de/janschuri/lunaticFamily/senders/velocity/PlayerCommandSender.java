package de.janschuri.lunaticFamily.senders.velocity;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import de.janschuri.lunaticFamily.Velocity;
import de.janschuri.lunaticFamily.utils.ClickableDecisionMessage;
import de.janschuri.lunaticFamily.utils.ClickableMessage;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerCommandSender extends de.janschuri.lunaticFamily.senders.PlayerCommandSender {

    private final UUID uuid;
    private final AtomicInteger requestIdGenerator = new AtomicInteger(0);

    private final int timeout = 5;
    private final TimeUnit unit = TimeUnit.SECONDS;

    public void executeAsync(Runnable task, long delayTicks) {
        ScheduledTask scheduledTask = Velocity.getProxy().getScheduler().buildTask(Velocity.getInstance(), task)
                .delay(delayTicks/20, TimeUnit.SECONDS)
                .schedule();
    }
    public PlayerCommandSender(CommandSource sender) {
        super(((Player) sender).getUniqueId());
        this.uuid = ((Player) sender).getUniqueId();
    }

    public PlayerCommandSender(UUID uuid) {
        super(uuid);
        this.uuid = uuid;
    }

    public PlayerCommandSender(String name) {
        super(name);
        this.uuid = Database.getDatabase().getUUID(name);
    }

    @Override
    public boolean sendMessage(String message) {
        Optional<Player> player = Velocity.getProxy().getPlayer(uuid);
        player.ifPresent(value -> value.sendMessage(
                Component.text(message)
        ));
        return player.isPresent();
    }

    @Override
    public boolean sendMessage(ClickableMessage message) {
        Optional<Player> player = Velocity.getProxy().getPlayer(uuid);
        player.ifPresent(value -> value.sendMessage(
                        LegacyComponentSerializer.legacy('§').deserialize(message.getText())
                                .clickEvent(ClickEvent.runCommand(message.getCommand()))
                                .hoverEvent(HoverEvent.showText(
                                        LegacyComponentSerializer.legacy('§').deserialize(message.getHoverText())
                                ))
                                .toBuilder().build()
        ));
        return player.isPresent();
    }

    @Override
    public boolean sendMessage(ClickableDecisionMessage message) {
        Optional<Player> player = Velocity.getProxy().getPlayer(uuid);
        player.ifPresent(value -> value.sendMessage(
                LegacyComponentSerializer.legacy('§').deserialize(message.getText())
                        .append(Component.text(" ✓", NamedTextColor.GREEN, TextDecoration.BOLD).clickEvent(ClickEvent.runCommand(
                                message.getConfirmCommand()
                        )))
                        .hoverEvent(HoverEvent.showText(
                                LegacyComponentSerializer.legacy('§').deserialize(message.getConfirmHoverText())
                        ))
                        .append(Component.text(" ❌", NamedTextColor.RED, TextDecoration.BOLD).clickEvent(ClickEvent.runCommand(
                                message.getCancelCommand()
                        )))
                        .hoverEvent(HoverEvent.showText(
                                LegacyComponentSerializer.legacy('§').deserialize(message.getCancelHoverText())
                        ))
                        .toBuilder().build()
        ));
        return player.isPresent();
    }

    @Override
    public boolean sendMessage(List<ClickableMessage> msg) {
        Optional<Player> player = Velocity.getProxy().getPlayer(uuid);
        if (player.isPresent()) {
            Component component = Component.empty();
            for (ClickableMessage message : msg) {
                Component text = Component.text(message.getText());
                if (message.getCommand() != null) {
                    text = text.clickEvent(ClickEvent.runCommand(message.getCommand()));
                }
                if (message.getHoverText() != null) {
                    text = text.hoverEvent(HoverEvent.showText(Component.text(message.getHoverText())));
                }
                if (message.getColor() != null) {
                    text = text.color(TextColor.fromHexString(message.getColor()));
                }
                component = component.append(text);
            }
            player.get().sendMessage(component);
        }
        return player.isPresent();
    }

    @Override
    public UUID getUniqueId(String name) {
        if (uuid != null) {
            return uuid;
        }
        return Database.getDatabase().getUUID(name);
    }

    @Override
    public String getName() {
        FamilyPlayer player = new FamilyPlayer(uuid);
        return player.getName();
    }

    @Override
    public boolean chat(String message) {
        Optional<Player> playerOptional = Velocity.getProxy().getPlayer(uuid);
        if (playerOptional.isPresent()) {
            playerOptional.get().sendMessage(
                LegacyComponentSerializer.legacy('§').deserialize(message)
            );
            return true;
        }
        return false;
    }

    @Override
    public boolean chat(String message, int delay) {
        Optional<Player> playerOptional = Velocity.getProxy().getPlayer(uuid);

        Runnable task = () -> {
            playerOptional.ifPresent(player -> player.spoofChatInput(
                    message
            ));
        };

        executeAsync(task, delay);
        return true;
    }

    @Override
    public boolean hasItemInMainHand() {
        int requestId = requestIdGenerator.incrementAndGet();
        CompletableFuture<Boolean> responseFuture = new CompletableFuture<>();

        Velocity.booleanRequestMap.put(requestId, responseFuture);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("HasItemInMainHandRequest");
        out.writeInt(requestId);
        out.writeUTF(uuid.toString());
        Velocity.sendPluginMessage(out.toByteArray());

        try {
            // Wait for the response with a timeout
            return responseFuture.get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return false; // An error occurred while waiting for response or timeout
        }
    }

    @Override
    public byte[] getItemInMainHand() {
        int requestId = requestIdGenerator.incrementAndGet();
        CompletableFuture<byte[]> responseFuture = new CompletableFuture<>();

        Velocity.byteArrayRequestMap.put(requestId, responseFuture);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetItemInMainHandRequest");
        out.writeInt(requestId);
        out.writeUTF(uuid.toString());
        Velocity.sendPluginMessage(out.toByteArray());

        try {
            // Wait for the response with a timeout
            return responseFuture.get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return null; // An error occurred while waiting for response or timeout
        }
    }

    @Override
    public boolean removeItemInMainHand() {
        int requestId = requestIdGenerator.incrementAndGet();
        CompletableFuture<Boolean> responseFuture = new CompletableFuture<>();

        Velocity.booleanRequestMap.put(requestId, responseFuture);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("RemoveItemInMainHandRequest");
        out.writeInt(requestId);
        out.writeUTF(uuid.toString());
        Velocity.sendPluginMessage(out.toByteArray());

        try {
            // Wait for the response with a timeout
            return responseFuture.get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return false; // An error occurred while waiting for response or timeout
        }
    }

    @Override
    public boolean giveItemDrop(byte[] item) {
        int requestId = requestIdGenerator.incrementAndGet();
        CompletableFuture<Boolean> responseFuture = new CompletableFuture<>();

        Velocity.booleanRequestMap.put(requestId, responseFuture);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GiveItemDropRequest");
        out.writeInt(requestId);
        out.writeUTF(uuid.toString());
        out.writeInt(item.length);
        out.write(item);


        Velocity.sendPluginMessage(out.toByteArray());

        try {
            // Wait for the response with a timeout
            return responseFuture.get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return false; // An error occurred while waiting for response or timeout
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        Optional<Player> playerOptional = Velocity.getProxy().getPlayer(uuid);
        return playerOptional.map(player -> player.hasPermission(permission)).orElse(false);
    }

    @Override
    public String getServerName() {
        Optional<Player> playerOptional = Velocity.getProxy().getPlayer(uuid);
        return playerOptional.map(player -> player.getCurrentServer().get().getServerInfo().getName()).orElse(null);
    }

    @Override
    public double[] getPosition() {
        int requestId = requestIdGenerator.incrementAndGet();
        CompletableFuture<double[]> responseFuture = new CompletableFuture<>();

        Velocity.doubleArrayRequestMap.put(requestId, responseFuture);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetPositionRequest");
        out.writeInt(requestId);
        out.writeUTF(uuid.toString());
        Velocity.sendPluginMessage(out.toByteArray());

        try {
            // Wait for the response with a timeout
            return responseFuture.get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return null; // An error occurred while waiting for response or timeout
        }
    }

    @Override
    public boolean isOnline() {
        Collection<Player> players = Velocity.getProxy().getAllPlayers();
        for (Player player : players) {
            if (player.getUniqueId().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInRange(UUID playerUUID, double range) {
        if (range < 0) {
            return true;
        }

        int requestId = requestIdGenerator.incrementAndGet();
        CompletableFuture<Boolean> responseFuture = new CompletableFuture<>();

        Velocity.booleanRequestMap.put(requestId, responseFuture);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("IsInRangeRequest");
        out.writeInt(requestId);
        out.writeUTF(uuid.toString());
        out.writeUTF(playerUUID.toString());
        out.writeDouble(range);
        Velocity.sendPluginMessage(out.toByteArray());

        try {
            // Wait for the response with a timeout
            return responseFuture.get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return false; // An error occurred while waiting for response or timeout
        }
    }

    @Override
    public boolean exists() {
        return uuid != null;
    }

    @Override
    public de.janschuri.lunaticFamily.senders.PlayerCommandSender getPlayerCommandSender(UUID uuid) {
        return new PlayerCommandSender(uuid);
    }

    @Override
    public de.janschuri.lunaticFamily.senders.PlayerCommandSender getPlayerCommandSender(String name) {
        return new PlayerCommandSender(name);
    }
    @Override
    public boolean isSameServer(UUID player1UUID) {
        Optional<Player> player1 = Velocity.getProxy().getPlayer(player1UUID);
        Optional<Player> player = Velocity.getProxy().getPlayer(uuid);

        return player1.isPresent() && player.isPresent() && player1.get().getCurrentServer().get().getServerInfo().getName().equals(player.get().getCurrentServer().get().getServerInfo().getName());

    }
}
