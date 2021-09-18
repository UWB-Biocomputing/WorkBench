package edu.uwb.braingrid.general;

import java.util.logging.Level;

/**
 * Utility containing the logging level for all logger objects.
 */
public final class LoggerHelper {

    private LoggerHelper() {
        // utility class cannot be instantiated
    }

    /** Sets a global standard for what is logged by the logger object. */
    public static final Level MIN_LOG_LEVEL = Level.ALL;
}
