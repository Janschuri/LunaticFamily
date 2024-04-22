package de.janschuri.lunaticFamily.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.external.Vault;
import de.janschuri.lunaticFamily.handler.FamilyPlayer;
import de.janschuri.lunaticFamily.external.FamilyTree;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class PaperUtils extends Utils {

    public String getPlayerName(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    @Override
    public void updateFamilyTree(int id) {
        FamilyPlayer player = new FamilyPlayer(id);
        player.getUniqueId();

        if (Bukkit.getPlayer(player.getUniqueId()) != null) {
            new FamilyTree(id);
        }
    }

    @Override
    public boolean isPlayerOnWhitelistedServer(UUID uuid) {
        return true;
    }

    @Override
    public boolean hasEnoughMoney(UUID uuid, String... withdrawKeys) {
        return hasEnoughMoney(uuid, 1.0, withdrawKeys);
    }

    @Override
    public boolean hasEnoughMoney(UUID uuid, double factor, String... withdrawKeys) {
        if (PluginConfig.enabledVault || PluginConfig.isBackend) {
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
    public boolean withdrawMoney(UUID uuid, String... withdrawKeys) {
        return withdrawMoney(uuid, 1.0, withdrawKeys);
    }

    @Override
    public boolean withdrawMoney(UUID uuid, double factor, String... withdrawKeys) {
        if (PluginConfig.enabledVault) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            double amount = 0.0;
            for (String key : withdrawKeys) {
                if (PluginConfig.commandWithdraws.containsKey(key)) {
                    amount += PluginConfig.commandWithdraws.get(key);
                }
            }
            amount *= factor;
            Vault.getEconomy().withdrawPlayer(player, amount);
            return true;
        } else {
            return true;
        }
    }

    @Override
    public void spawnParticleCloud(UUID uuid, double[] position, String particleString) {
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

    public static ItemStack getSkull(String url) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            urlObject = new URL(url);
        } catch (MalformedURLException exception) {
            throw new RuntimeException("Invalid URL", exception);
        }
        textures.setSkin(urlObject);
        profile.setTextures(textures);

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setPlayerProfile(profile);
        head.setItemMeta(meta);

        return head;
    }

    public void sendConsoleCommand(String command) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
    }

    public static byte[] serializeItemStack(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack deserializeItemStack(byte[] data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
