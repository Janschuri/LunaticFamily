package de.janschuri.lunaticFamily.commands.senders;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.commands.ClickableDecisionMessage;
import de.janschuri.lunaticFamily.commands.ClickableMessage;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.config.Language;
import de.janschuri.lunaticFamily.database.Database;
import de.janschuri.lunaticFamily.external.Vault;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.handler.FamilyTree;
import de.janschuri.lunaticFamily.utils.PaperUtils;
import de.janschuri.lunaticFamily.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class PaperPlayerCommandSender extends PlayerCommandSender {
    private final UUID uuid;
    public PaperPlayerCommandSender(CommandSender sender) {
        super(((OfflinePlayer) sender).getUniqueId());
        this.uuid = ((OfflinePlayer) sender).getUniqueId();
    }

    public PaperPlayerCommandSender(UUID uuid) {
        super(uuid);
        this.uuid = uuid;
    }

    public PaperPlayerCommandSender(String name) {
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
    public boolean hasEnoughMoney(String... withdrawKeys) {
        if (PluginConfig.enabledVault || PluginConfig.useProxy) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            double amount = 0.0;
            for (String key : withdrawKeys) {
                if (PluginConfig.commandWithdraws.containsKey(key)) {
                    amount += PluginConfig.commandWithdraws.get(key);
                }
            }
            return (amount < Vault.getEconomy().getBalance(player));
        } else {
            return true;
        }
    }

    @Override
    public boolean hasEnoughMoney(double factor, String... withdrawKeys) {
        if (PluginConfig.enabledVault || PluginConfig.useProxy) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            double amount = 0.0;
            for (String key : withdrawKeys) {
                if (PluginConfig.commandWithdraws.containsKey(key)) {
                    amount += PluginConfig.commandWithdraws.get(key);
                }
            }
            amount *= factor;

            return (amount < Vault.getEconomy().getBalance(player));
        } else {
            return true;
        }
    }

    @Override
    public PlayerCommandSender getPlayerCommandSender(UUID uuid) {
        return new PaperPlayerCommandSender(uuid);
    }

    @Override
    public PlayerCommandSender getPlayerCommandSender(String name) {
        return new PaperPlayerCommandSender(name);
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

    public boolean withdrawMoney(String... withdrawKeys) {
        if (PluginConfig.enabledVault) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            double amount = 0.0;
            for (String key : withdrawKeys) {
                if (PluginConfig.commandWithdraws.containsKey(key)) {
                    amount += PluginConfig.commandWithdraws.get(key);
                }
            }
            Vault.getEconomy().withdrawPlayer(player, amount);
            return true;
        } else {
            return true;
        }
    }

    @Override
    public boolean withdrawMoney(double factor, String... withdrawKey) {
        if (PluginConfig.enabledVault) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            double amount = 0.0;
            amount += PluginConfig.commandWithdraws.get(withdrawKey) * factor;


            if (amount > 0) {
                Vault.getEconomy().withdrawPlayer(player, amount);
                if (player.getPlayer() != null) {
                    player.getPlayer().sendMessage(Language.prefix + Language.getMessage("withdraw").replace("%amount%", amount + ""));
                }
            }
        }
        return true;
    }

    @Override
    public double[] getPositionBetween(UUID partnerUUID) {
        if (Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(partnerUUID) != null) {
            Location loc1 = Bukkit.getPlayer(uuid).getLocation();
            Location loc2 = Bukkit.getPlayer(partnerUUID).getLocation();
            double [] loc1Array = {loc1.getX(), loc1.getY(), loc1.getZ()};
            double [] loc2Array = {loc2.getX(), loc2.getY(), loc2.getZ()};
            return Utils.getPositionBetweenLocations(loc1Array, loc2Array);
        }
        return null;
    }

    @Override
    public void spawnParticleCloud(double[] position, String particleString) {
        if (Bukkit.getPlayer(uuid) != null) {
            Particle particle = Particle.valueOf(particleString.toUpperCase(Locale.ROOT));
            World world = Bukkit.getPlayer(uuid).getWorld();
            Location location = new Location(world, position[0], position[1], position[2]);

            Random random = new Random();

            double range = 2.0;

            for (int i = 0; i < 10; i++) {
                double offsetX = (random.nextDouble() - 0.5) * range * 2;
                double offsetY = (random.nextDouble() - 0.5) * range * 2;
                double offsetZ = (random.nextDouble() - 0.5) * range * 2;

                Location particleLocation = location.clone().add(offsetX, offsetY, offsetZ);

                world.spawnParticle(particle, particleLocation, 1);
            }
        }
    }

    @Override
    public void spawnKissParticles(UUID partnerUUID) {

        PaperPlayerCommandSender player = new PaperPlayerCommandSender(uuid);
        PaperPlayerCommandSender partner = new PaperPlayerCommandSender(partnerUUID);

            double[] position = getPositionBetween(partnerUUID);
            position[1] += 2;
            for (int i = 0; i < 6; i++) {
                Bukkit.getScheduler().runTaskLater(LunaticFamily.getInstance(), () -> spawnParticleCloud(position, "HEART"), i * 5L);
            }

    }

    @Override
    public boolean isOnline() {
        return Bukkit.getPlayer(uuid) != null;
    }

    @Override
    public boolean isInRange(UUID playerUUID, double range) {
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

    public void sendAdoptRequest(UUID childUUID) {
        new BukkitRunnable() {
            public void run() {
                if (LunaticFamily.adoptRequests.containsKey(childUUID.toString())) {
                    LunaticFamily.adoptRequests.remove(childUUID.toString());
                    FamilyPlayer playerFam = getFamilyPlayer();
                    FamilyPlayer childFam = new FamilyPlayer(childUUID);
                    PlayerCommandSender child = getPlayerCommandSender(childUUID);
                    if (playerFam.isMarried()) {
                        FamilyPlayer partnerFam = playerFam.getPartner();
                        child.sendMessage(Language.prefix + Language.getMessage("adopt_propose_request_expired").replace("%player1%", playerFam.getName()).replace("%player2%", partnerFam.getName()));
                    } else {
                        child.sendMessage(Language.prefix + Language.getMessage("adopt_propose_request_by_single_expired").replace("%player%", playerFam.getName()));
                    }
                    sendMessage(Language.prefix + Language.getMessage("adopt_request_sent_expired").replace("%player%", childFam.getName()));
                }
            }
        }.runTaskLater(LunaticFamily.getInstance(), 600L);
    }

    @Override
    public void sendMarryRequest(UUID uuid) {

        PaperPlayerCommandSender partner = new PaperPlayerCommandSender(uuid);

        if (!isInRange(partner.getUniqueId(), PluginConfig.marryProposeRange)) {
            sendMessage(Language.prefix + Language.getMessage("player_too_far_away").replace("%player%", partner.getName()));
            return;
        }

        new BukkitRunnable() {
            public void run() {
                if (LunaticFamily.marryRequests.containsKey(uuid.toString())) {
                    LunaticFamily.marryRequests.remove(uuid.toString());
                    partner.sendMessage(Language.prefix + Language.getMessage("marry_propose_request_expired").replace("%player%", getName()));

                    sendMessage(Language.prefix + Language.getMessage("marry_propose_request_sent_expired").replace("%player%", partner.getName()));
                }
            }
        }.runTaskLater(LunaticFamily.getInstance(), 600L);
    }

    @Override
    public void sendMarryPriestRequest(UUID player1UUID, UUID player2UUID) {
        PaperPlayerCommandSender player1 = new PaperPlayerCommandSender(player1UUID);
        PaperPlayerCommandSender player2 = new PaperPlayerCommandSender(player2UUID);
        new BukkitRunnable() {
            public void run() {
                if (LunaticFamily.marryPriest.containsValue(getUniqueId().toString())) {
                    sendMessage(Language.prefix + Language.getMessage("marry_priest_request_expired_priest").replace("%player1%", player1.getName()).replace("%player2%", player2.getName()));
                    player1.sendMessage(Language.prefix + Language.getMessage("marry_priest_request_expired_player").replace("%player%", player2.getName()));
                    player2.sendMessage(Language.prefix + Language.getMessage("marry_priest_request_expired_player").replace("%player%", player1.getName()));

                    LunaticFamily.marryRequests.remove(player2UUID);
                    LunaticFamily.marryPriestRequests.remove(player1UUID);
                    LunaticFamily.marryPriest.remove(player1UUID);
                }
            }
        }.runTaskLater(LunaticFamily.getInstance(), 600L);
    }

    @Override
    public void sendSiblingRequest(UUID siblingUUID) {
        PaperPlayerCommandSender sibling = new PaperPlayerCommandSender(siblingUUID);
        new BukkitRunnable() {
            public void run() {
                if (LunaticFamily.siblingRequests.containsKey(siblingUUID.toString())) {
                    LunaticFamily.siblingRequests.remove(siblingUUID.toString());
                    sibling.sendMessage(Language.prefix + Language.getMessage("sibling_propose_request_expired").replace("%player%", getName()));

                    sendMessage(Language.prefix + Language.getMessage("sibling_propose_request_sent_expired").replace("%player%", sibling.getName()));
                }
            }
        }.runTaskLater(LunaticFamily.getInstance(), 600L);
    }

    @Override
    public void onJoinEvent() {
        FamilyPlayer playerFam = getFamilyPlayer();
        new BukkitRunnable() {
            public void run() {
                if (PluginConfig.enabledCrazyAdvancementAPI) {
                    new FamilyTree(playerFam.getID());
                }
                if (playerFam.isMarried()) {
                    PlayerCommandSender partner = getPlayerCommandSender(playerFam.getPartner().getUniqueId());
                    if (!LunaticFamily.isProxy) {
                        if (partner.isOnline()) {
                            partner.sendMessage(Language.prefix + Language.getMessage("marry_partner_online"));
                            sendMessage(Language.prefix + Language.getMessage("marry_partner_online"));
                        } else {
                            sendMessage(Language.prefix + Language.getMessage("marry_partner_offline"));
                        }
                    }
                }
            }
        }.runTaskLater(LunaticFamily.getInstance(), 5L);
    }

    @Override
    public void onQuitEvent() {

    }

    @Override
    public boolean isSameServer(UUID uuid) {
        return true;
    }
}
