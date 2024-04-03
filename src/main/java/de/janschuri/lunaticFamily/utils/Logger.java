package de.janschuri.lunaticFamily.utils;

import de.janschuri.lunaticFamily.config.Config;
import de.janschuri.lunaticFamily.LunaticFamily;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logger {

    private static final LunaticFamily plugin = LunaticFamily.getInstance();

    public static void log(final String message, LoggingSeverity severity) {
        switch (severity) {
            case INFO:
                infoLog(message);
                break;
            case DEBUG:
                debugLog(message);
                break;
            case WARN:
                warnLog(message);
                break;
            case ERROR:
                errorLog(message);
                break;
        }
    }

    public static void debugLog(String msg) {
        if(Config.isDebug){
            Bukkit.getLogger().info("[" +plugin.getName() + "] " + msg);
        }
    }
    public static void infoLog(String msg) {
        Bukkit.getLogger().info("[" + plugin.getName() + "] " + msg);
    }

    public static void warnLog(String msg) {
        Bukkit.getLogger().warning("[" + plugin.getName() + "] Warning: " + msg);
    }

    public static void errorLog(String msg) {
        Bukkit.getLogger().severe("[" + plugin.getName() + "] Error: " + msg);
    }
}
