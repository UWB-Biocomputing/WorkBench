package edu.uwb.braingrid.workbenchdashboard.userModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbench.FileManagerShared;
import edu.uwb.braingrid.workbench.provvisualizer.ProVisGlobal;

/**
 *
 * @author Max
 */
public final class User implements FileManagerShared {

    private static final Logger LOG = Logger.getLogger(User.class.getName());
    private static final String USER_DATA_PATH = FileManager.getWorkbenchDirectory()
            + File.separator + "user.json";

    private static User user = null;

    private String projectsDirectory;
    private String simulationsDirectory;
    private String brainGridRepoDirectory;

    private User() {
        LOG.info("Initializing User Data");
        //TODO: get project and sim dirs from user
        setProjectsDirectory(System.getProperty("user.dir"));
        setSimulationsDirectory(System.getProperty("user.home"));
        setBrainGridRepoDirectory(getProjectsDirectory() + File.separator
                + ProVisGlobal.BG_REPOSITORY_LOCAL);
    }

    public static User getUser() {
        if (user == null) {
            load();
        }
        return user;
    }

    private static boolean load() {
        LOG.info("Loading User Information");
        ObjectMapper mapper = new ObjectMapper();

        File file = new File(USER_DATA_PATH);
        JsonNode json;
        if (file.exists()) {
            try {
                json = mapper.readTree(file);
                user = mapper.readValue(file, User.class);
            } catch (IOException e) {
                LOG.severe(e.getMessage());
                return false;
            }
        } else {
            LOG.info("No User Info Found");
            user = new User();
            save();
            return load();
        }
        LOG.info("User info loaded: "  + json.toString());
        return true;
    }

    public static boolean save() {
        LOG.info("Saving User Data");
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(USER_DATA_PATH);

        try {
            file.getParentFile().mkdirs();
            mapper.writeValue(file, user);
        } catch (IOException e) {
            LOG.severe(e.getMessage());
            return false;
        }
        return true;
    }

    public String getProjectsDirectory() {
        return this.projectsDirectory;
    }

    public void setProjectsDirectory(String projectsDirectory) {
        LOG.info("Workbench Projects Path: " + projectsDirectory);
        this.projectsDirectory = projectsDirectory;
    }

    public String getBrainGridRepoDirectory() {
        return brainGridRepoDirectory;
    }

    public void setBrainGridRepoDirectory(String brainGridRepoDirectory) {
        LOG.info("BrainGrid Repo Path: " + brainGridRepoDirectory);
        this.brainGridRepoDirectory = brainGridRepoDirectory;
    }

    public String getSimulationsDirectory() {
        return simulationsDirectory;
    }

    public void setSimulationsDirectory(String simulationsDirectory) {
        LOG.info("Workbench Simulations Path: " + simulationsDirectory);
        this.simulationsDirectory = simulationsDirectory;
    }
}
