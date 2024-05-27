package de.janschuri.lunaticfamily.common.utils;


import de.janschuri.lunaticfamily.common.LunaticFamily;
import de.janschuri.lunaticlib.common.logger.AbstractLogger;

public class Logger extends AbstractLogger {

    private static final org.slf4j.Logger logger = AbstractLogger.getLogger("LunaticFamily");

    public static boolean isDebug() {
        return LunaticFamily.isDebug;
    }

    public static void debugLog(String msg) {
        if (isDebug()) {
            debug(logger, msg);
        }
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


