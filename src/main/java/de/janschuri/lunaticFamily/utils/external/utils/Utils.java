package de.janschuri.lunaticFamily.utils.external.utils;

import org.bukkit.Location;

import static java.lang.Math.sqrt;

public class Utils {

    public static double getDistance(Location loc1, Location loc2) {
        return sqrt((loc2.getZ() - loc1.getZ()) * (loc2.getZ() - loc1.getZ()) + (loc2.getX() - loc1.getX()) * (loc2.getX() - loc1.getX()));
    }

    public static double getDistance(double x1, double z1, double x2, double z2) {
        return sqrt((z2 - z1) * (z2 - z1) + (x2 - x1) * (x2 - x1));
    }

}
