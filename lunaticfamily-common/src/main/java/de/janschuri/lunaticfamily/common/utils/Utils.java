package de.janschuri.lunaticfamily.common.utils;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
import de.janschuri.lunaticlib.PlayerSender;
import de.janschuri.lunaticlib.Sender;
import de.janschuri.lunaticlib.common.LunaticLib;
import de.janschuri.lunaticlib.common.utils.Mode;

import java.util.UUID;

public abstract class Utils extends de.janschuri.lunaticlib.common.utils.Utils {

    public static boolean isPlayerOnRegisteredServer(Sender sender) {
        if (!(sender instanceof PlayerSender player)) {
            return true;
        }

        if (LunaticFamily.getMode() == Mode.PROXY) {
            return LunaticFamily.getConfig().getServers().contains(player.getServerName());
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

            return LunaticLib.getPlatform().getVault().hasEnoughMoney(serverName, uuid, amount)
                    .thenApply(hasEnoughMoney -> hasEnoughMoney).join();
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

            return LunaticLib.getPlatform().getVault().withdrawMoney(serverName, uuid, amount)
                    .thenApply(withdrawn -> withdrawn).join();

        }
        return true;
    }

    public static boolean spawnParticleCloud(UUID uuid, double[] position, String particleString) {
        return LunaticFamily.getPlatform().spawnParticlesCloud(uuid, position, particleString);
    }

    public static boolean isPriest(UUID uuid) {
        return LunaticFamily.marryPriests.containsValue(uuid);
    }

    public static String getPercentageAsString(int active, int total) {
        if (total == 0) {
            return "0.00";
        }

        double percentage = (active / (double) total) * 100;
        return String.format("%.2f", percentage);
    }
}
