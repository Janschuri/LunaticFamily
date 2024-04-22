package de.janschuri.lunaticFamily.utils;

import de.janschuri.lunaticFamily.config.Language;

import java.util.Timer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Utils {

    private static Utils utils;
    Timer timer = new Timer();

    public static void loadUtils (Utils utils) {
        Utils.utils = utils;
    }

    public static Utils getUtils() {
        return utils;
    }
    public static Timer getTimer() {
        return utils.timer;
    }

    public abstract String getPlayerName(UUID uuid);

    public abstract void sendConsoleCommand(String command);
    public abstract void updateFamilyTree(int id);
    public abstract boolean isPlayerOnWhitelistedServer(UUID uuid);

    public static boolean checkIsSubcommand(final String command, final String subcommand, final String arg) {
        return subcommand.equalsIgnoreCase(arg) || Language.getAliases(command, subcommand).stream().anyMatch(element -> arg.equalsIgnoreCase(element));
    }

    public abstract boolean hasEnoughMoney(UUID uuid, String... withdrawKeys);
    public abstract boolean hasEnoughMoney(UUID uuid, double factor, String... withdrawKeys);
    public abstract boolean withdrawMoney(UUID uuid, String... withdrawKeys);
    public abstract boolean withdrawMoney(UUID uuid, double factor, String... withdrawKeys);
    public abstract void spawnParticleCloud(UUID uuid, double[] position, String particleString);
    public static boolean isUUID(String input) {
        Pattern UUID_PATTERN = Pattern.compile(
                "^([0-9a-fA-F]){8}-([0-9a-fA-F]){4}-([0-9a-fA-F]){4}-([0-9a-fA-F]){4}-([0-9a-fA-F]){12}$");
        return UUID_PATTERN.matcher(input).matches();
    }

    public static double[] getPositionBetweenLocations(double[] loc1, double[] loc2) {
        double[] midpoint = new double[3];
        midpoint[0] = (loc1[0] + loc2[0]) / 2;
        midpoint[1] = (loc1[1] + loc2[1]) / 2;
        midpoint[2] = (loc1[2] + loc2[2]) / 2;
        return midpoint;
    }

    public static boolean isValidHexCode(String hexCode) {
        Pattern pattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
        Matcher matcher = pattern.matcher(hexCode);
        return matcher.matches();
    }
}
