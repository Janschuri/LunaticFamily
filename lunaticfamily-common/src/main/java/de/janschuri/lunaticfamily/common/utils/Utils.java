package de.janschuri.lunaticfamily.common.utils;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.utils.Mode;

import java.util.UUID;

public abstract class Utils extends de.janschuri.lunaticlib.common.utils.Utils {

    public static boolean isPlayerOnRegisteredServer(UUID uuid) {
        if (LunaticFamily.getMode() == Mode.PROXY) {
            PlayerSender sender = LunaticLib.getPlatform().getPlayerSender(uuid);
            return LunaticFamily.getConfig().getServers().contains(sender.getServerName());
        }
        return true;
    }

    public static boolean hasEnoughMoney(String serverName, UUID uuid, String... withdrawKeys) {
        return hasEnoughMoney(serverName, uuid, 1.0, withdrawKeys);
    }

    public static boolean hasEnoughMoney(String serverName, UUID uuid, double factor, String... withdrawKeys) {
        if (LunaticFamily.getConfig().isUseVault()) {

            double amount = 0.0;
            for (String key : withdrawKeys) {
                amount += LunaticFamily.getConfig().getCommandWithdraw(key);
            }
            amount *= factor;

            return LunaticLib.getPlatform().getVault().hasEnoughMoney(serverName, uuid, amount);
        }
        return true;
    }

    public static boolean withdrawMoney(String serverName, UUID uuid, String... withdrawKeys) {
        return withdrawMoney(serverName, uuid, 1.0, withdrawKeys);
    }

    public static boolean withdrawMoney(String serverName, UUID uuid, double factor, String... withdrawKeys) {
        if (LunaticFamily.getConfig().isUseVault()) {

            double amount = 0.0;
            for (String key : withdrawKeys) {
                amount += LunaticFamily.getConfig().getCommandWithdraw(key);
            }
            amount *= factor;

            return LunaticLib.getPlatform().getVault().withdrawMoney(serverName, uuid, amount);

        }
        return true;
    }

    public static boolean spawnParticleCloud(UUID uuid, double[] position, String particleString) {
        return LunaticFamily.getPlatform().spawnParticlesCloud(uuid, position, particleString);
    }
}
