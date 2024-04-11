package de.janschuri.lunaticFamily.utils;

import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticFamily.config.Config;
import org.bukkit.Bukkit;

public class Logger {

    private static final LunaticFamily plugin = LunaticFamily.getInstance();
    private static java.util.logging.Logger logger;

    public Logger(java.util.logging.Logger logger) {
        Logger.logger = logger;
    }

    public static void debugLog(String msg) {
        if(Config.isDebug){
            Bukkit.getLogger().info("[" +plugin.getName() + "] " + msg);
        }
    }
    public static void infoLog(String msg) {
        logger.info("[" + logger.getName() + "] " + msg);
    }

    public static void warnLog(String msg) {
        Bukkit.getLogger().warning("[" + plugin.getName() + "] Warning: " + msg);
        logger.warning("[" + logger.getName() + "] " + msg);
    }

    public static void errorLog(String msg) {
        Bukkit.getLogger().severe("[" + plugin.getName() + "] Error: " + msg);
    }
}
