package de.janschuri.lunaticFamily.utils;

import de.janschuri.lunaticFamily.config.Language;

import java.util.Timer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Utils extends de.janschuri.lunaticlib.utils.Utils {

    private static Utils utils;

    public static void loadUtils (Utils utils) {
        Utils.utils = utils;
    }

    public static Utils getUtils() {
        return utils;
    }

    public abstract String getPlayerName(UUID uuid);

    public abstract void sendConsoleCommand(String command);
    public abstract void updateFamilyTree(int id);
    public abstract boolean isPlayerOnWhitelistedServer(UUID uuid);

    public abstract boolean hasEnoughMoney(UUID uuid, String... withdrawKeys);
    public abstract boolean hasEnoughMoney(UUID uuid, double factor, String... withdrawKeys);
    public abstract boolean withdrawMoney(UUID uuid, String... withdrawKeys);
    public abstract boolean withdrawMoney(UUID uuid, double factor, String... withdrawKeys);
    public abstract void spawnParticleCloud(UUID uuid, double[] position, String particleString);
}
