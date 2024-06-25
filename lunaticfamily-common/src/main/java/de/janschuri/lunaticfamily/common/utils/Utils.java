package de.janschuri.lunaticfamily.common.utils;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.tables.PlayerDataTable;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.utils.Mode;

import java.util.UUID;

public abstract class Utils extends de.janschuri.lunaticlib.common.utils.Utils {

    public static boolean isPlayerOnRegisteredServer(PlayerSender sender) {
        if (LunaticFamily.getMode() == Mode.PROXY) {
            return LunaticFamily.getConfig().getServers().contains(sender.getServerName());
        }
        return true;
    }

    public static boolean hasEnoughMoney(String serverName, UUID uuid, WithdrawKey... withdrawKeys) {
        return hasEnoughMoney(serverName, uuid, 1.0, withdrawKeys);
    }

    public static boolean hasEnoughMoney(String serverName, UUID uuid, double factor, WithdrawKey... withdrawKeys) {
        if (LunaticFamily.getConfig().isUseVault()) {

            double amount = 0.0;
            for (WithdrawKey key : withdrawKeys) {
                amount += LunaticFamily.getConfig().getCommandWithdraw(key.toString().toLowerCase());
            }
            amount *= factor;

            return LunaticLib.getPlatform().getVault().hasEnoughMoney(serverName, uuid, amount);
        }
        return true;
    }

    public static boolean withdrawMoney(String serverName, UUID uuid, WithdrawKey... withdrawKeys) {
        return withdrawMoney(serverName, uuid, 1.0, withdrawKeys);
    }

    public static boolean withdrawMoney(String serverName, UUID uuid, double factor, WithdrawKey... withdrawKeys) {
        if (LunaticFamily.getConfig().isUseVault()) {

            double amount = 0.0;
            for (WithdrawKey key : withdrawKeys) {
                amount += LunaticFamily.getConfig().getCommandWithdraw(key.toString().toLowerCase());
            }
            amount *= factor;

            return LunaticLib.getPlatform().getVault().withdrawMoney(serverName, uuid, amount);

        }
        return true;
    }

    public static boolean spawnParticleCloud(UUID uuid, double[] position, String particleString) {
        return LunaticFamily.getPlatform().spawnParticlesCloud(uuid, position, particleString);
    }

    public static boolean isPriest(UUID uuid) {
        return LunaticFamily.marryPriests.containsValue(uuid);
    }

    public static UUID getUUIDFromArg(String arg) {
        UUID uuid;

        if (isUUID(arg)) {
            uuid = UUID.fromString(arg);

            if (PlayerDataTable.getID(uuid) < 0) {
                return null;
            }
        } else {
            uuid = PlayerDataTable.getUUID(arg);
        }

        return uuid;
    }
}
