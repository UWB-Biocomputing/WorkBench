package edu.uwb.braingrid.workbenchdashboard.threads;

import java.io.IOException;
import java.util.logging.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;

import edu.uwb.braingrid.workbenchdashboard.utils.RepoManager;
import edu.uwb.braingrid.workbenchdashboard.utils.ThreadManager;

public class RunUpdateRepo extends Thread implements Runnable {

    private static final Logger LOG = Logger.getLogger(RunUpdateRepo.class.getName());

    public RunUpdateRepo() {
        // default constructor
    }

    public void run() {
        ThreadManager.addThread("Updating Master Repo");
        try {
            RepoManager.updateMaster();
        } catch (GitAPIException | IOException e) {
            LOG.severe(e.getMessage());
        }
        ThreadManager.removeThread("Updating Master Repo");
    }
}
