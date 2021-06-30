package edu.uwb.braingrid.general;

import java.io.File;
import java.util.logging.Logger;

import edu.uwb.braingrid.workbench.provvisualizer.ProVisGlobal;

public final class DirMgr {

    private static final Logger LOG = Logger.getLogger(DirMgr.class.getName());

    private DirMgr() {
        // utility class cannot be instantiated
    }

    /**
     * Provides the root path for the system, as a string path.
     *
     * @return The root path for the system
     */
    public static String getRootPath() {
        LOG.info("Root Path: " + System.getProperty("user.dir"));
        return System.getProperty("user.dir");
    }

    /**
     * Provides the directory where the repos are stored, as a string path.
     *
     * @return The directory where the repos are stored
     */
    public static String getBrainGridRepoDirectory() {
        String bgReposPath = DirMgr.getRootPath() + File.separator
                + ProVisGlobal.BG_REPOSITORY_LOCAL;

        LOG.info("BrainGrid Repo Path: " + bgReposPath);
        return bgReposPath;
    }
}
