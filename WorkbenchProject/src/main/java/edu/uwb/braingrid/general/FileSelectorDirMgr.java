package edu.uwb.braingrid.general;

import java.io.File;

/**
 * A class to manage starting directories the file explorer opens.
 */
public class FileSelectorDirMgr {

    private File lastDir = null;

    /**
     * Provides the last directory.
     *
     * @return The last directory
     */
    public File getLastDir() {
        return lastDir;
    }

    /**
     * Replaces the last directory.
     *
     * @param dir  The directory to be added
     */
    public void addDir(File dir) {
        lastDir = dir;
    }

    /**
     * Clears the last directory.
     */
    public void clear() {
        lastDir = null;
    }
}
