package de.janschuri.lunaticFamily.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.Language;
import jdk.jshell.execution.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.Vector;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Utils {

    private static Utils utils;

    public static void loadUtils (Utils utils) {
        Utils.utils = utils;
    }

    public static Utils getUtils() {
        return utils;
    }

    public static boolean isValidHexCode(String hexCode) {
        Pattern pattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
        Matcher matcher = pattern.matcher(hexCode);
        return matcher.matches();
    }

    public abstract String getPlayerName(UUID uuid);

    public abstract void sendConsoleCommand(String command);
    public abstract void updateFamilyTree(int id);

    public static boolean checkIsSubcommand(final String command, final String subcommand, final String arg) {
        return subcommand.equalsIgnoreCase(arg) || Language.getAliases(command, subcommand).stream().anyMatch(element -> arg.equalsIgnoreCase(element));
    }

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

    public abstract boolean isPlayerOnWhitelistedServer(UUID uuid);

}
