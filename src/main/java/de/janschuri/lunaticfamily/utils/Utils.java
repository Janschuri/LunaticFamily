package de.janschuri.lunaticfamily.utils;

import de.janschuri.lunaticfamily.LunaticFamily;
import de.janschuri.lunaticfamily.PaperLunaticFamily;
import de.janschuri.lunaticfamily.config.PluginConfig;
import de.janschuri.lunaticfamily.futurerequests.SpawnParticlesCloudRequest;
import de.janschuri.lunaticfamily.futurerequests.UpdateFamilyTreeRequest;
import de.janschuri.lunaticfamily.handler.FamilyTree;
import de.janschuri.lunaticlib.LunaticLib;
import de.janschuri.lunaticlib.external.Vault;
import de.janschuri.lunaticlib.senders.AbstractPlayerSender;
import de.janschuri.lunaticlib.senders.AbstractSender;
import de.janschuri.lunaticlib.utils.Mode;

import java.util.*;

public abstract class Utils extends de.janschuri.lunaticlib.utils.Utils {
    public static boolean isPlayerOnRegisteredServer(UUID uuid) {
        if (LunaticFamily.getMode() == Mode.PROXY) {
            AbstractPlayerSender sender = AbstractSender.getPlayerSender(uuid);
            return PluginConfig.getServers().contains(sender.getServerName());
        }
        return true;
    }

    public static boolean hasEnoughMoney(String serverName, UUID uuid, String... withdrawKeys) {
        return hasEnoughMoney(serverName, uuid, 1.0, withdrawKeys);
    }

    public static boolean hasEnoughMoney(String serverName, UUID uuid, double factor, String... withdrawKeys) {
        if (PluginConfig.isUseVault()) {

            if(!LunaticLib.isInstalledVault()) {
                Logger.errorLog("Vault is not installed! Please install Vault or disable it in plugin config.yml.");
                return false;
            }

            double amount = 0.0;
            for (String key : withdrawKeys) {
                amount += PluginConfig.getCommandWithdraw(key);
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
        if (PluginConfig.isUseVault() || LunaticFamily.enabledProxy) {

            if (!LunaticLib.isInstalledVault()) {
                Logger.errorLog("Vault is not installed! Please install Vault or disable vault features in plugin config.yml.");
                return false;
            }

            double amount = 0.0;
            for (String key : withdrawKeys) {
                amount += PluginConfig.getCommandWithdraw(key);
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
        if (LunaticLib.getMode() == Mode.PROXY) {
            new UpdateFamilyTreeRequest().get(id);
        } else {
            if (PluginConfig.isUseCrazyAdvancementAPI() || LunaticLib.getMode() == Mode.BACKEND) {
                if (!LunaticFamily.installedCrazyAdvancementsAPI) {
                    Logger.errorLog("CrazyAdvancementsAPI is not installed! Please install CrazyAdvancementsAPI or disable it in plugin config.yml. 1");
                    return;
                }
                FamilyTree.updateFamilyTree(id);
            }
        }
    }
}
