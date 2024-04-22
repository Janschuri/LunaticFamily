package de.janschuri.lunaticFamily.utils.logger;

public class VelocityLogger extends Logger {

    static org.slf4j.Logger logger;

    public VelocityLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    public static void debugLog(String msg) {
        logger.debug(msg);
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
}
