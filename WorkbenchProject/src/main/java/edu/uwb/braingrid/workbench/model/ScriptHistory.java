package edu.uwb.braingrid.workbench.model;

import java.io.Serializable;

import edu.uwb.braingrid.workbench.utils.DateTime;

/**
 * Represents the history for a script associated with a simulation.
 *
 * @author Aaron Conrad
 */
public class ScriptHistory implements Serializable {

    // <editor-fold defaultstate="collapsed" desc="Members">
    private long startedAt;
    private long completedAt;
    private boolean outputAnalyzed;
    private boolean ran;
    private String filename;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Construction">
    /**
     * Creates a ScriptHistory object with the given script filename and default values for the
     * remaining instance variables.
     *
     * @param filename  The filename of the script, including the full path
     */
    public ScriptHistory(String filename) {
        this.filename = filename;
        startedAt = DateTime.ERROR_TIME;
        completedAt = DateTime.ERROR_TIME;
        outputAnalyzed = false;
        ran = false;
    }

    /**
     * Creates a ScriptHistory object with all default values. Note, this no argument constructor is
     * required for JSON deserialization.
     */
    public ScriptHistory() {
        this("None");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    /**
     * Provides the time at which the script started, in milliseconds since January 1, 1970,
     * 00:00:00 GMT. DateTime.ERROR_TIME is used to indicate that the script was not executed.
     *
     * @return The time at which the script started, in milliseconds since January 1, 1970,
     *         00:00:00 GMT or DateTime.ERROR_TIME if the script was not executed.
     */
    public long getStartedAt() {
        return startedAt;
    }

    /**
     * Sets the time at which the script started, in milliseconds since January 1, 1970,
     * 00:00:00 GMT.
     *
     * @param startedAt  The time at which the script started, in milliseconds since January 1,
     *                   1970, 00:00:00 GMT
     */
    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }

    /**
     * Provides the time at which the script completed, in milliseconds since January 1, 1970,
     * 00:00:00 GMT. DateTime.ERROR_TIME is used to indicate that the script was not executed.
     *
     * @return The time at which the script completed, in milliseconds since January 1, 1970,
     *         00:00:00 GMT or DateTime.ERROR_TIME if the script was not executed.
     */
    public long getCompletedAt() {
        return completedAt;
    }

    /**
     * Sets the time at which the script completed, in milliseconds since January 1, 1970,
     * 00:00:00 GMT.
     *
     * @param completedAt  The time at which the script completed, in milliseconds since January 1,
     *                     1970, 00:00:00 GMT
     */
    public void setCompletedAt(long completedAt) {
        this.completedAt = completedAt;
    }

    /**
     * Indicates whether or not the script output has been analyzed.
     *
     * @return True if the script output has been analyzed, false if not
     */
    public boolean isOutputAnalyzed() {
        return outputAnalyzed;
    }

    /**
     * Sets the value used to determine whether or not the script output has been analyzed.
     *
     * @param outputAnalyzed  boolean value indicating whether or not the script output has been
     *                        analyzed
     */
    public void setOutputAnalyzed(boolean outputAnalyzed) {
        this.outputAnalyzed = outputAnalyzed;
    }

    /**
     * Indicates whether or not the script ran.
     *
     * @return True if the script ran, false if not
     */
    public boolean isRan() {
        return ran;
    }

    /**
     * Sets the value used to determine whether or not the script ran.
     *
     * @param ran  boolean value indicating whether or not the script ran
     */
    public void setRan(boolean ran) {
        this.ran = ran;
    }

    /**
     * Provides the full path to the script. Note, this is the local copy of the script relative to
     * workbench application.
     *
     * @return The filename of the script, including the full path
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename of the script, including the full path. Note, this is the local copy of the
     * script relative to workbench application.
     *
     * @param filename  The filename of the script, including the full path
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }
    // </editor-fold>
}
