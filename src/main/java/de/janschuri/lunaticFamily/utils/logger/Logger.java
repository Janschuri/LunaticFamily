package de.janschuri.lunaticFamily.utils.logger;

import de.janschuri.lunaticFamily.config.PluginConfig;

public abstract class Logger {

    private static Logger logger;

    public static void loadLogger(Logger logger) {
        Logger.logger = logger;
    }

    public static void debugLog(String msg) {
        if(PluginConfig.isDebug){
            logger.debugLog(msg);
        }
    }

    public static void infoLog(String msg) {
        logger.infoLog(msg);
    }

    public static void warnLog(String msg) {
        logger.warnLog(msg);
    }

    public static void errorLog(String msg) {
        logger.errorLog(msg);
    }
}
