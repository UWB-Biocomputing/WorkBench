package edu.uwb.braingrid.workbenchdashboard.threads;

import java.util.logging.Logger;

import edu.uwb.braingrid.workbenchdashboard.utils.Init;
import edu.uwb.braingrid.workbenchdashboard.utils.ThreadManager;

public class RunInit extends Thread implements Runnable {

    private static final Logger LOG = Logger.getLogger(RunUpdateRepo.class.getName());

    public RunInit() {
        // default constructor
    }

    public void run() {
        ThreadManager.addThread("Init");
        Init.init();
        ThreadManager.removeThread("Init");
    }
}
