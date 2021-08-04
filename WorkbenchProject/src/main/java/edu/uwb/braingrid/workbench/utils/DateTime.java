package edu.uwb.braingrid.workbench.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uwb.braingrid.workbench.FileManager;

/**
 * Collection of static helper functions centered around temporal operations.
 *
 * @author Del Davis
 */
public final class DateTime {

    //TODO: Consider refactoring to remove ERROR_TIME. This doesn't appear to be a meaningful
    // constant and should likely be replaced with either null or a boolean wherever it is used.
    /** Represents an invalid time. */
    public static final long ERROR_TIME = -1L;
    private static final String TIME_PATTERN = "HH:mm:ss";

    private DateTime() {
        // utility class cannot be instantiated
    }

    /**
     * Provides the current time as the number of milliseconds since January 1, 1970, 00:00:00 GMT.
     *
     * @return The current time as the number of milliseconds since January 1, 1970, 00:00:00 GMT
     */
    public static long now() {
        return new Date().getTime();
    }

    /**
     * Converts the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this
     * date.
     *
     * @param millisFromEpoch  The number of milliseconds since January 1, 1970, 00:00:00 GMT
     *                         represented by this date
     * @return Description of time in the form hours:minutes:seconds (e.g. 14:52:47)
     */
    public static String getTime(Long millisFromEpoch) {
        DateFormat dateFormat = new SimpleDateFormat(TIME_PATTERN);
        String time = dateFormat.format(millisFromEpoch);
        return time;
    }

//    public static void recordProvTiming(String className, String functionName, String codeLine,
//            Long startingMillis) {
//        long time = (System.currentTimeMillis() - startingMillis);
//        try {
//            PrintWriter out = new PrintWriter(new BufferedWriter(
//                    new FileWriter("provPerformance.txt", true)));
//            out.println(className + "." + functionName + ",line#" + codeLine
//                    + ";milliseconds prov overhead:" + time);
//            out.close();
//        } catch (IOException e) {
//            System.err.println("Problem in writing to the prov performance file.");
//            e.printStackTrace();
//        }
//    }

    public static Long sumProvTiming(Long startingMillis, Long millisSoFar) {
        long time = System.currentTimeMillis() - startingMillis;
        return millisSoFar + time;
    }

    public static void recordFunctionExecutionTime(String className, String functionName,
            Long totalTime, boolean provEnabled) {
        String filename = FileManager.getWorkbenchDirectory().resolve("logs")
                .resolve("provOverhead.txt").toString();
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new FileWriter(filename, true)));
            out.println(className + "." + functionName + ";provEnabled=" + provEnabled
                    + ";total milliseconds:" + totalTime);
            out.close();
        } catch (IOException e) {
            System.err.println("Problem in writing to the prov performance file.");
            e.printStackTrace();
        }
    }

    public static void recordAccumulatedProvTiming(String className, String functionName,
            Long totalProvTime) {
        String filename = FileManager.getWorkbenchDirectory().resolve("logs")
                .resolve("provOverhead.txt").toString();
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new FileWriter(filename, true)));
            out.println(className + "." + functionName + ";total prov overhead:" + totalProvTime);
            out.close();
        } catch (IOException e) {
            System.err.println("Problem in writing to the prov performance file.");
            e.printStackTrace();
        }
    }
}
