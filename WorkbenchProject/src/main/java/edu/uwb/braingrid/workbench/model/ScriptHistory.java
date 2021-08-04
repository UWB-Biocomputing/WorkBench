package edu.uwb.braingrid.workbench.model;

import edu.uwb.braingrid.workbench.utils.DateTime;

/**
 * Represents the history for a script associated with a simulation.
 *
 * @author Aaron Conrad
 */
public class ScriptHistory {

    private long startedAt;
    private long completedAt;
    private boolean outputAnalyzed;
    private boolean ran;
    private String filename;
    private int version;

    public ScriptHistory(String filename) {
        this.filename = filename;
        startedAt = DateTime.ERROR_TIME;
        completedAt = DateTime.ERROR_TIME;
        outputAnalyzed = false;
        ran = false;
        version = 0;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }

    public long getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(long completedAt) {
        this.completedAt = completedAt;
    }

    public boolean wasOutputAnalyzed() {
        return outputAnalyzed;
    }

    public void setOutputAnalyzed(boolean outputAnalyzed) {
        this.outputAnalyzed = outputAnalyzed;
    }

    public boolean hasRun() {
        return ran;
    }

    public void setRan(boolean ran) {
        this.ran = ran;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void incrementVersion() {
        version++;
    }
}
