package edu.uwb.braingrid.workbenchdashboard.userModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbench.FileManagerShared;
import edu.uwb.braingrid.workbench.provvisualizer.ProvVisGlobal;

/**
 *
 * @author Max
 */
public final class User implements FileManagerShared {

    private static final Logger LOG = Logger.getLogger(User.class.getName());
    private static final String USER_DATA_PATH = "./user.json";

    private static User user = null;

    private String rootDir;
    private String homeDir;
    private String brainGridRepoDirectory;

    private User() {
        LOG.info("Initializing User Data");
        setRootDir(System.getProperty("user.dir"));
        setHomeDir(System.getProperty("user.home"));
        setBrainGridRepoDirectory(getRootDir() + File.separator
                + ProvVisGlobal.BG_REPOSITORY_LOCAL);
    }

    public static User getInstance() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public static boolean load() {
        LOG.info("Loading User Information");
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(USER_DATA_PATH);
        JsonNode json;
        if (file.exists()) {
            try {
                json = mapper.readTree(file);
                user = mapper.readValue(new File(USER_DATA_PATH), User.class);
                FileManager.updateStaticVals(user);
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

        try {
            mapper.writeValue(new File(USER_DATA_PATH), user);
        } catch (IOException e) {
            LOG.severe(e.getMessage());
            return false;
        }
        return true;
    }

    public String getRootDir() {
        return this.rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public String getBrainGridRepoDirectory() {
        return brainGridRepoDirectory;
    }

    public void setBrainGridRepoDirectory(String brainGridRepoDirectory) {
        FileManager.setBrainGridRepoDirectory(brainGridRepoDirectory);
        this.brainGridRepoDirectory = brainGridRepoDirectory;
    }

    public String getHomeDir() {
        return homeDir;
    }

    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }
}
