package de.janschuri.lunaticFamily.utils;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.futurerequests.SpawnParticlesCloudRequest;
import de.janschuri.lunaticFamily.futurerequests.UpdateFamilyTreeRequest;
import de.janschuri.lunaticFamily.handler.FamilyTree;
import de.janschuri.lunaticlib.LunaticLib;
import de.janschuri.lunaticlib.external.Vault;
import de.janschuri.lunaticlib.futurerequests.requests.GetNameRequest;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.Mode;
import org.bukkit.*;

import java.util.*;

public abstract class Utils extends de.janschuri.lunaticlib.utils.Utils {
    public static boolean isPlayerOnRegisteredServer(UUID uuid) {
        if (LunaticFamily.getMode() == Mode.PROXY) {
            if (!PluginConfig.enabledServerWhitelist) {
                return true;
            }
            AbstractPlayerSender sender = AbstractSender.getPlayerSender(uuid);
            return PluginConfig.servers.contains(sender.getServerName());
        }
        return true;
    }

    public static String getPlayerName(UUID uuid) {
        if (LunaticFamily.getMode() == Mode.PROXY) {
            return new GetNameRequest().get(uuid);
        } else {
            return Bukkit.getOfflinePlayer(uuid).getName();
        }
    }

    public static boolean hasEnoughMoney(UUID uuid, String... withdrawKeys) {
        return hasEnoughMoney(uuid, 1.0, withdrawKeys);
    }

    public static boolean hasEnoughMoney(UUID uuid, double factor, String... withdrawKeys) {
        if (PluginConfig.useVault) {

            if(!LunaticLib.installedVault) {
                Logger.errorLog("Vault is not installed! Please install Vault or disable it in plugin config.yml.");
                return false;
            }

            double amount = 0.0;
            for (String key : withdrawKeys) {
                if (PluginConfig.commandWithdraws.containsKey(key)) {
                    amount += PluginConfig.commandWithdraws.get(key);
                }
            }
            amount *= factor;

            return Vault.hasEnoughMoney(uuid, amount);
        }
        return true;
    }

    public static boolean withdrawMoney(UUID uuid, String... withdrawKeys) {
        return withdrawMoney(uuid, 1.0, withdrawKeys);
    }

    public static boolean withdrawMoney(UUID uuid, double factor, String... withdrawKeys) {
        if (PluginConfig.useVault || LunaticFamily.enabledProxy) {

            if (!LunaticLib.installedVault) {
                Logger.errorLog("Vault is not installed! Please install Vault or disable vault features in plugin config.yml.");
                return false;
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            double amount = 0.0;
            for (String key : withdrawKeys) {
                if (PluginConfig.commandWithdraws.containsKey(key)) {
                    amount += PluginConfig.commandWithdraws.get(key);
                }
            }
            amount *= factor;

            return Vault.withdrawMoney(uuid, amount);

        }
        return true;
    }

    public static boolean spawnParticleCloud(UUID uuid, double[] position, String particleString) {
        if (LunaticFamily.getMode() == Mode.PROXY) {
            return new SpawnParticlesCloudRequest().get(uuid, position, particleString);
        }

        if (Bukkit.getPlayer(uuid) == null) {
            return false;
        } else {
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
            return true;
        }
    }

    public static void updateFamilyTree(int id, UUID uuid) {
        if (LunaticLib.getMode() == Mode.PROXY) {
            new UpdateFamilyTreeRequest().get(id, uuid);
        } else {
            if (PluginConfig.useCrazyAdvancementAPI && Bukkit.getPlayer(uuid) != null) {
                new FamilyTree(id);
            }
        }
    }
}
