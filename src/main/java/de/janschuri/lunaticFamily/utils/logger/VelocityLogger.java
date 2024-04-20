package de.janschuri.lunaticFamily.utils.logger;

public class VelocityLogger extends Logger {

    org.slf4j.Logger logger;

    public VelocityLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void debug(String msg) {
        logger.debug(msg);
    }

    @Override
    public void info(String msg) {
        logger.info(msg);
    }

    @Override
    public void warn(String msg) {
        logger.warn(msg);
    }

    @Override
    public void error(String msg) {
        logger.error(msg);
    }
}
