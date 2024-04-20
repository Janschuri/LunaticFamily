package de.janschuri.lunaticFamily.utils.logger;

import de.janschuri.lunaticFamily.config.PluginConfig;

public abstract class Logger {

    private static Logger logger;

    public static void loadLogger(Logger logger) {
        Logger.logger = logger;
    }

    public static void debugLog(String msg) {
        if(PluginConfig.isDebug){
            logger.debug(msg);
        }
    }

    public static void infoLog(String msg) {
        logger.info(msg);
    }

    public static void warnLog(String msg) {
        logger.warn(msg);
    }

    public static void errorLog(String msg) {
        logger.error(msg);
    }

    public abstract void debug(String msg);
    public abstract void info(String msg);
    public abstract void warn(String msg);
    public abstract void error(String msg);
}
