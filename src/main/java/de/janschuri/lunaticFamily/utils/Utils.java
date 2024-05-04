package de.janschuri.lunaticFamily.utils;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.PaperLunaticFamily;
import de.janschuri.lunaticFamily.config.PluginConfig;
import de.janschuri.lunaticFamily.futurerequests.SpawnParticlesCloudRequest;
import de.janschuri.lunaticFamily.futurerequests.UpdateFamilyTreeRequest;
import de.janschuri.lunaticFamily.handler.FamilyTree;
import de.janschuri.lunaticlib.LunaticLib;
import de.janschuri.lunaticlib.external.Vault;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.Mode;

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

    public static boolean hasEnoughMoney(String serverName, UUID uuid, String... withdrawKeys) {
        return hasEnoughMoney(serverName, uuid, 1.0, withdrawKeys);
    }

    public static boolean hasEnoughMoney(String serverName, UUID uuid, double factor, String... withdrawKeys) {
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

            return Vault.hasEnoughMoney(serverName, uuid, amount);
        }
        return true;
    }

    public static boolean withdrawMoney(String serverName, UUID uuid, String... withdrawKeys) {
        return withdrawMoney(serverName, uuid, 1.0, withdrawKeys);
    }

    public static boolean withdrawMoney(String serverName, UUID uuid, double factor, String... withdrawKeys) {
        if (PluginConfig.useVault || LunaticFamily.enabledProxy) {

            if (!LunaticLib.installedVault) {
                Logger.errorLog("Vault is not installed! Please install Vault or disable vault features in plugin config.yml.");
                return false;
            }

            double amount = 0.0;
            for (String key : withdrawKeys) {
                if (PluginConfig.commandWithdraws.containsKey(key)) {
                    amount += PluginConfig.commandWithdraws.get(key);
                }
            }
            amount *= factor;

            return Vault.withdrawMoney(serverName, uuid, amount);

        }
        return true;
    }

    public static boolean spawnParticleCloud(UUID uuid, double[] position, String particleString) {
        if (LunaticFamily.getMode() == Mode.PROXY) {
            return new SpawnParticlesCloudRequest().get(uuid, position, particleString);
        }

        return PaperLunaticFamily.spawnParticleCloud(uuid, position, particleString);
    }

    public static void updateFamilyTree(int id, UUID uuid) {
        if (!LunaticFamily.installedCrazyAdvancementsAPI) {
            Logger.errorLog("CrazyAdvancementsAPI is not installed! Please install CrazyAdvancementsAPI or disable it in plugin config.yml.");
            return;
        }

        if (LunaticLib.getMode() == Mode.PROXY) {
            new UpdateFamilyTreeRequest().get(id);
        } else {
            if (PluginConfig.useCrazyAdvancementAPI || LunaticLib.getMode() == Mode.BACKEND) {
                FamilyTree.updateFamilyTree(id);
            }
        }
    }
}
