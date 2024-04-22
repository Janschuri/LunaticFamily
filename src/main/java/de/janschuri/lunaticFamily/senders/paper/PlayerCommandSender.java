package de.janschuri.lunaticFamily.senders.paper;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.utils.ClickableDecisionMessage;
import de.janschuri.lunaticFamily.utils.ClickableMessage;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.utils.PaperUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class PlayerCommandSender extends de.janschuri.lunaticFamily.senders.PlayerCommandSender {
    private final UUID uuid;
    public PlayerCommandSender(CommandSender sender) {
        super(((OfflinePlayer) sender).getUniqueId());
        this.uuid = ((OfflinePlayer) sender).getUniqueId();
    }

    public PlayerCommandSender(UUID uuid) {
        super(uuid);
        this.uuid = uuid;
    }

    public PlayerCommandSender(String name) {
        super(name);
        this.uuid = getUniqueId(name);
    }

    @Override
    public boolean sendMessage(String message) {
        if (Bukkit.getPlayer(uuid) != null) {
            TextComponent msg = LegacyComponentSerializer.legacy('§').deserialize(message);
            Bukkit.getPlayer(uuid).sendMessage(msg);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasPermission(String permission) {
        return Bukkit.getPlayer(uuid).hasPermission(permission);
    }

    @Override
    public String getServerName() {
        return Bukkit.getServer().getName();
    }

    @Override
    public double[] getPosition() {
        return new double[] {
                Bukkit.getOfflinePlayer(uuid).getLocation().getX(),
                Bukkit.getOfflinePlayer(uuid).getLocation().getY(),
                Bukkit.getOfflinePlayer(uuid).getLocation().getZ()
        };
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
    public boolean sendMessage(ClickableDecisionMessage message) {
        if (Bukkit.getPlayer(uuid) != null) {
            Bukkit.getPlayer(uuid).sendMessage(
                    LegacyComponentSerializer.legacy('§').deserialize(Language.prefix + message.getText())
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
                    .toBuilder().build());
            return true;
        }
        return false;
    }

    @Override
    public boolean sendMessage(ClickableMessage message) {
        if (Bukkit.getPlayer(uuid) != null) {
            Bukkit.getPlayer(uuid).sendMessage(
                    LegacyComponentSerializer.legacy('§').deserialize(Language.prefix + message.getText())
                    .clickEvent(ClickEvent.runCommand(message.getCommand()))
                    .hoverEvent(HoverEvent.showText(
                            LegacyComponentSerializer.legacy('§').deserialize(message.getHoverText())
                    ))
                    .toBuilder().build());
            return true;
        }
        return false;
    }

    @Override
    public boolean sendMessage(List<ClickableMessage> msg) {
        if (Bukkit.getPlayer(uuid) != null) {
            Component component = LegacyComponentSerializer.legacy('§').deserialize(Language.prefix);
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
            Bukkit.getPlayer(uuid).sendMessage(component);
            return true;
        }
        return false;
    }

    @Override
    public boolean chat(String message) {
        if (Bukkit.getPlayer(uuid) != null) {
            Bukkit.getPlayer(uuid).chat(message);
            return true;
        }
        return false;
    }

    @Override
    public boolean chat(String message, int delay) {
        if (Bukkit.getPlayer(uuid) != null) {
            Bukkit.getScheduler().runTaskLater(LunaticFamily.getInstance(), () -> Bukkit.getPlayer(uuid).chat(message), delay);
            return true;
        }
        return false;
    }

    @Override
    public boolean hasItemInMainHand() {
        return !Bukkit.getPlayer(uuid).getInventory().getItemInMainHand().getType().isAir();
    }

    @Override
    public byte[] getItemInMainHand() {
        if (Bukkit.getPlayer(uuid) != null) {
            ItemStack item = Bukkit.getPlayer(uuid).getInventory().getItemInMainHand();

            return PaperUtils.serializeItemStack(item);
        }
        return null;
    }

    @Override
    public boolean removeItemInMainHand() {
        if (Bukkit.getPlayer(uuid) != null) {
            Bukkit.getPlayer(uuid).getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            return true;
        }
        return false;
    }

    @Override
    public boolean giveItemDrop(byte[] item) {
        if (Bukkit.getPlayer(uuid) != null) {
            ItemStack itemStack = PaperUtils.deserializeItemStack(item);
            Bukkit.getPlayer(uuid).getWorld().dropItem(Bukkit.getPlayer(uuid).getLocation(), itemStack);
            return true;
        }
        return false;
    }

    @Override
    public boolean isOnline() {
        return Bukkit.getPlayer(uuid) != null;
    }

    @Override
    public boolean isInRange(UUID playerUUID, double range) {
        if (range < 0) {
            return true;
        }
        Player player = Bukkit.getPlayer(playerUUID);
        return Bukkit.getPlayer(uuid).getLocation().distance(player.getLocation()) <= range;
    }

    @Override
    public boolean exists() {
        return Database.getDatabase().getID(uuid) != 0;
    }

    @Override
    public UUID getUniqueId(String name) {
        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }

    public String getName() {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    @Override
    public boolean isSameServer(UUID uuid) {
        return true;
    }
}
