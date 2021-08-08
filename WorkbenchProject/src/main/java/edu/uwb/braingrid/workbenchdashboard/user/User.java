package edu.uwb.braingrid.workbenchdashboard.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbench.FileManagerShared;

/**
 * <p>Represents the active user profile. Provides user-specified information including paths to
 * this user's project data, local simulator repository, and simulation data directories.</p>
 *
 * <p>User information is saved to and loaded from a JSON file located in the user's Workbench Data
 * directory.</p>
 *
 * @author Max Wright
 */
public final class User implements FileManagerShared {

    private static final Logger LOG = Logger.getLogger(User.class.getName());

    private static User user = null;

    private Path projectsDirectory;
    private Path brainGridRepoDirectory;
    private String simulationsDirectory;

    /**
     * Creates a User object with the specified user-information. This private constructor can only
     * be accessed via the public getUser method if there is not already a loaded user profile.
     */
    private User() {
        LOG.info("Initializing User Data");
        //TODO: get project and sim dirs from user
        Path projectsDir = FileManager.getUserHome().resolve("WorkbenchProjects");
        Path repoDir = FileManager.getWorkbenchDirectory().resolve("BrainGridRepo");
        String simulationsDir = "~/WorkbenchSimulations"; // may be remote
        setProjectsDirectory(projectsDir);
        setBrainGridRepoDirectory(repoDir);
        setSimulationsDirectory(simulationsDir);
    }

    /**
     * Provides the single instance of the user profile.
     *
     * @return The current user profile
     */
    public static User getUser() {
        if (user == null) {
            load();
        }
        return user;
    }

    /**
     * Loads the user profile from disk, if one exists. If no user profile is found, creates a new
     * user profile, saves it, and then loads it.
     *
     * @return True if the user profile was loaded successfully, otherwise false
     */
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

    /**
     * Saves the active user profile to disk in JSON format.
     *
     * @return True if the user profile was saved successfully, otherwise false
     */
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

    /**
     * Provides the path to the user's projects directory.
     *
     * @return The path to the user's projects directory
     */
    public Path getProjectsDirectory() {
        return projectsDirectory;
    }

    /**
     * Sets the user's projects directory path.
     *
     * @param projectsDirectory  The new path to the user projects directory
     */
    public void setProjectsDirectory(Path projectsDirectory) {
        LOG.info("Workbench Projects Path: " + projectsDirectory);
        this.projectsDirectory = projectsDirectory;
    }

    /**
     * Provides the path to the user's local simulator repository.
     *
     * @return The path to the user's local simulator repository
     */
    public Path getBrainGridRepoDirectory() {
        return brainGridRepoDirectory;
    }

    /**
     * Sets the user's local simulator repository path.
     *
     * @param brainGridRepoDirectory  The new path to the local simulator repository
     */
    public void setBrainGridRepoDirectory(Path brainGridRepoDirectory) {
        LOG.info("BrainGrid Repo Path: " + brainGridRepoDirectory);
        this.brainGridRepoDirectory = brainGridRepoDirectory;
    }

    /**
     * Provides the location of the user's simulation data directory.
     *
     * Note: This directory is relative to the simulation machine (which may be remote) and may not
     * match the local file system. Since simulations can only run on a Linux machine, this will
     * always be a Posix path.
     *
     * @return The location of the user's simulation data directory
     */
    public String getSimulationsDirectory() {
        return simulationsDirectory;
    }

    /**
     * Sets the user's simulation data directory path.
     *
     * Note: This directory is relative to the simulation machine (which may be remote) and may not
     * match the local file system. Since simulations can only run on a Linux machine, this will
     * always be a Posix path.
     *
     * @param simulationsDirectory  The new path to the simulation data directory
     */
    public void setSimulationsDirectory(String simulationsDirectory) {
        String simsDir = FilenameUtils.separatorsToUnix(simulationsDirectory);
        LOG.info("Workbench Simulations Path: " + simsDir);
        this.simulationsDirectory = simsDir;
    }

    /**
     * Provides the path to the user profile JSON file.
     *
     * @return The path to the user profile JSON file
     */
    private static Path getUserDataPath() {
        return FileManager.getWorkbenchDirectory().resolve("user.json");
    }
}
