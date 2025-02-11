package de.janschuri.lunaticfamily.common.utils;

import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticfamily.common.database.DatabaseRepository;
import de.janschuri.lunaticfamily.common.handler.FamilyPlayer;
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

        Logger.debugLog("arg: " + arg);

        if (isUUID(arg)) {
            Logger.debugLog("arg is UUID");

            uuid = UUID.fromString(arg);

            if (DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("uuid", uuid).findCount() == 0) {
                uuid = null;
            }
        } else {
            Logger.debugLog("arg is not UUID");
            uuid = DatabaseRepository.getDatabase().find(FamilyPlayer.class).where().eq("name", arg).findOne().getUUID();
        }

        if (uuid == null) {
            Logger.debugLog("UUID is null");
        }


        return uuid;
    }

    public static String getPercentageAsString(int active, int total) {
        if (total == 0) {
            return "0.00";
        }

        double percentage = (active / (double) total) * 100;
        return String.format("%.2f", percentage);
    }
}
