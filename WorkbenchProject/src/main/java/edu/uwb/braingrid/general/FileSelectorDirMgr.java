package edu.uwb.braingrid.general;

import java.io.File;
import java.util.ArrayList;

import edu.uwb.braingrid.workbench.FileManager;

/**
 * A class to manage starting directories the file explorer opens.
 */
public class FileSelectorDirMgr {

    private final ArrayList<File> dirs = new ArrayList<>();

    /**
     * Provides the default (home) directory.
     *
     * @return The default directory
     */
    public File getDefault() {
        return FileManager.getUserHome().toFile();
    }

    /**
     * Provides the last directory.
     *
     * @return The last directory
     */
    public File getLastDir() {
        if (dirs.isEmpty()) {
            return getDefault();
        }
        return dirs.get(dirs.size() - 1);
    }

    /**
     * Provides the directory at the given index.
     *
     * @param index  The index of the desired directory
     * @return The directory at the given index
     */
    public File getDir(int index) {
        if (dirs.isEmpty() || index >= dirs.size()) {
            return getDefault();
        }
        return dirs.get(index);
    }

    /**
     * Adds a new directory.
     *
     * @param newDir  The directory to be added
     */
    public void addDir(File newDir) {
        dirs.add(newDir);
    }
}
