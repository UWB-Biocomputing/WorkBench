package edu.uwb.braingrid.general;

import java.io.File;
import java.util.logging.Logger;

import edu.uwb.braingrid.workbench.provvisualizer.ProvVisGlobal;

public final class DirMgr {

    private static final Logger LOG = Logger.getLogger(DirMgr.class.getName());

    private DirMgr() {
        // utility class cannot be instantiated
    }

    /**
     * @return The root path for the system as a string path
     */
    public static String getRootPath() {
        LOG.info("Root Path: " + System.getProperty("user.dir"));
        return System.getProperty("user.dir");
    }

    /**
     * @return The directory where the repos are stored as a string path
     */
    public static String getBrainGridRepoDirectory() {
        String bgReposPath = DirMgr.getRootPath() + File.separator
                + ProvVisGlobal.BG_REPOSITORY_LOCAL;

        LOG.info("BrainGrid Repo Path: " + bgReposPath);
        return bgReposPath;
    }
}
