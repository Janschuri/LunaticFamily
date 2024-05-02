package de.janschuri.lunaticFamily.utils;


import de.janschuri.lunaticFamily.LunaticFamily;
import de.janschuri.lunaticlib.logger.AbstractLogger;

public class Logger extends AbstractLogger {

    private static org.slf4j.Logger logger = AbstractLogger.getLogger("LunaticFamily");

    public boolean isDebug() {
        return LunaticFamily.isDebug;
    }

    public static void debugLog(String msg) {
        debug(logger, msg);
    }

    public static void infoLog(String msg) {
        info(logger, msg);
    }

    public static void warnLog(String msg) {
        warn(logger, msg);
    }

    public static void errorLog(String msg) {
        error(logger, msg);
    }
}


