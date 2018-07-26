package edu.uniba.di.lacam.kdde.ws4j.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkArgument;

public class Log {

    private enum LOG {
        LEVEL_ERROR,
        LEVEL_WARNING,
        LEVEL_INFO,
        LEVEL_DEBUG
    }

    private static int defaultLogLevel = LOG.LEVEL_DEBUG.ordinal();
    private static Logger logger = LoggerFactory.getLogger("");

    public static void setLogLevel(final int logLevel) {
        checkArgument(logLevel <= LOG.LEVEL_DEBUG.ordinal() && logLevel >= LOG.LEVEL_ERROR.ordinal(),
                "Invalid log level.");
        defaultLogLevel = logLevel;
    }

    public static void info(final String string, final Object... args) {
        if (defaultLogLevel >= LOG.LEVEL_INFO.ordinal()) logger.info(buildString(string, args));
    }

    public static void debug(final String string, final Object... args) {
        if (defaultLogLevel >= LOG.LEVEL_DEBUG.ordinal()) logger.debug(buildString(string, args));
    }

    public static void warning(final String string, final Object... args) {
        if (defaultLogLevel >= LOG.LEVEL_WARNING.ordinal()) logger.warn(buildString(string, args));
    }

    public static void error(final String string, final Object... args) {
        if (defaultLogLevel >= LOG.LEVEL_ERROR.ordinal()) logger.error(buildString(string, args));
    }

    private static String buildString(final String string, final Object... strFmtArgs) {
        Object[] args;
        if (strFmtArgs != null) args = strFmtArgs;
        else args = new Object[0];
        return String.format("%s", String.format(string == null ? "<null>" : string, args));
    }
}


