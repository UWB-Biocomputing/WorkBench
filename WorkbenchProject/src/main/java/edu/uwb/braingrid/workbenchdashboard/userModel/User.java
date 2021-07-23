package edu.uwb.braingrid.workbenchdashboard.userModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private static User user = null;

    private Path projectsDirectory;
    private Path simulationsDirectory;
    private Path brainGridRepoDirectory;

    private User() {
        LOG.info("Initializing User Data");
        //TODO: get project and sim dirs from user
        Path projectsDir = FileManager.getUserHome()
                .resolve("Documents")
                .resolve("WorkbenchProjects");
        Path simulationsDir = FileManager.getUserHome();
        setProjectsDirectory(projectsDir);
        setSimulationsDirectory(simulationsDir);
        setBrainGridRepoDirectory(getProjectsDirectory()
                .resolve(ProVisGlobal.BG_REPOSITORY_LOCAL));
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

        File file = getUserDataPath().toFile();
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
        Path userDataPath = getUserDataPath();

        try {
            Files.createDirectories(userDataPath.getParent());
            mapper.writeValue(userDataPath.toFile(), user);
        } catch (IOException e) {
            LOG.severe(e.getMessage());
            return false;
        }
        return true;
    }

    public Path getProjectsDirectory() {
        return this.projectsDirectory;
    }

    public void setProjectsDirectory(Path projectsDirectory) {
        LOG.info("Workbench Projects Path: " + projectsDirectory);
        this.projectsDirectory = projectsDirectory;
    }

    public Path getBrainGridRepoDirectory() {
        return brainGridRepoDirectory;
    }

    public void setBrainGridRepoDirectory(Path brainGridRepoDirectory) {
        LOG.info("BrainGrid Repo Path: " + brainGridRepoDirectory);
        this.brainGridRepoDirectory = brainGridRepoDirectory;
    }

    public Path getSimulationsDirectory() {
        return simulationsDirectory;
    }

    public void setSimulationsDirectory(Path simulationsDirectory) {
        LOG.info("Workbench Simulations Path: " + simulationsDirectory);
        this.simulationsDirectory = simulationsDirectory;
    }

    private static Path getUserDataPath() {
        return FileManager.getWorkbenchDirectory().resolve("user.json");
    }
}
