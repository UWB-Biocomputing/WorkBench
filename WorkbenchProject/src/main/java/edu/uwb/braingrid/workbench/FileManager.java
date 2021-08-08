package edu.uwb.braingrid.workbench;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;

import edu.uwb.braingrid.workbenchdashboard.user.User;

/**
 * Handles all file operations for the workbench. The purpose behind this manager is to:
 * <ol>
 *   <li>Provide conditional support between operating system file paths (this is not possible in
 *   a static manner without querying the system properties repeatedly);</li>
 *   <li>Provide a separation of concerns in terms of workbench configuration for file operations
 *   (such as folder hierarchical relationships and names);</li>
 *   <li>Provide workbench specific robustness in terms of file operations such as copy.</li>
 * </ol>
 *
 * @author Del Davis
 */
public final class FileManager {

    // <editor-fold defaultstate="collapsed" desc="Members">
    private static final Logger LOG = Logger.getLogger(FileManager.class.getName());
    private static final String DEFAULT_PROJECT_NAME = "Default";
    private static final String CONFIG_FILES_FOLDER_NAME = "configfiles";
    private static final String NEURON_LIST_FOLDER_NAME = "NList";

    private static User user = User.getUser();
    private static Pattern filenamePattern = null;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Construction">
    private FileManager() {
        // utility class cannot be instantiated
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    /**
     * Provides the filenames for all neurons list files for a given project.
     *
     * @param projectName  The project containing the neuron list files
     * @return The filenames for all neurons list files in the project directory
     * @throws IOException
     */
    public static String[] getNeuronListFilenames(String projectName) throws IOException {
        Path folder = getSimConfigDirectoryPath(projectName, false)
                .resolve(NEURON_LIST_FOLDER_NAME);
        return Files.list(folder)
                .filter(Files::isRegularFile)
                .map(Path::toString)
                .toArray(String[]::new);
    }

    /**
     * Provides the path to the workbench (data) directory. This path is not user-specified.
     *
     * @return The path to the workbench (data) directory
     */
    public static Path getWorkbenchDirectory() {
        return getUserHome().resolve(".workbench");
    }

    /**
     * Provides the path to the projects directory for the current user.
     *
     * @return The path to the projects directory
     */
    public static Path getProjectsDirectory() {
        return user.getProjectsDirectory();
    }

    /**
     * Provides the path to the local simulator repository for the current user.
     *
     * @return The path to the local simulator repository
     */
    public static Path getBrainGridRepoDirectory() {
        return user.getBrainGridRepoDirectory();
    }

    /**
     * Provides the path to the simulations directory for the current user. This path is relative to
     * the simulation machine which may be remote.
     *
     * @return A string representation of the simulations directory (may be a remote path)
     */
    public static String getSimulationsDirectory() {
        return user.getSimulationsDirectory();
    }

    /**
     * Provides the canonical location of a simulation configuration file with the specified
     * filename.
     *
     * @param projectName  The name of the project containing the simulation configuration file
     *                     (project name is used as the main directory name for the project. e.g. if
     *                     the BrainGrid working directory is folder/BrainGrid and the project name
     *                     is myProject, then folder/BrainGrid/myProject/ contains the simulation
     *                     configuration directory, and subsequently, the simulation configuration
     *                     file)
     * @param filename  The simple name of the configuration file (e.g. mySimConfigFile.xml, not
     *                  folder/mySimConfigFile.xml)
     * @param mkdirs  Indicates whether or not to build the parent directories in the case that they
     *                do not yet exist
     * @return The canonical location of the specified simulation configuration file.
     * @throws IOException
     */
    public static Path getSimConfigFilePath(String projectName, String filename, boolean mkdirs)
            throws IOException {
        return getSimConfigDirectoryPath(projectName, mkdirs).resolve(filename);
    }

    /**
     * Provides the canonical location of a neuron list configuration file with the specified
     * filename.
     *
     * @param projectName  The name of the project containing the neuron list file (project name
     *                     is used as the main directory name for the project. e.g. if the BrainGrid
     *                     working directory is folder/BrainGrid and the project name is myProject,
     *                     then folder/BrainGrid/myProject/ contains the simulation configuration
     *                     directory, and subsequently, the simulation configuration file)
     * @param filename  The simple name of the configuration file (e.g. myActiveNeuronList.xml, not
     *                  folder/myActiveNeuronList.xml)
     * @param mkdirs  Indicates whether or not to build the parent directories in the case that they
     *                do not yet exist
     * @return The canonical location of the specified simulation configuration file
     * @throws IOException
     */
    public static Path getNeuronListFilePath(String projectName, String filename, boolean mkdirs)
            throws IOException {
        Path folder = getSimConfigDirectoryPath(projectName, false)
                .resolve(NEURON_LIST_FOLDER_NAME);
        if (mkdirs) {
            Files.createDirectories(folder);
        }
        return folder.resolve(filename);
    }

    /**
     * Provides the canonical location of the parent directory for all simulation configuration
     * related files.
     *
     * @param simulationName  The name of the simulation. The simulation name is used as the parent
     *                        directory to the sim config directory.
     * @param mkdirs  Indicates whether or not to build the parent directories in the case that they
     *                do not yet exist
     * @return The canonical location of the parent directory for all simulation configuration
     *         related files.
     * @throws IOException
     */
    public static Path getSimConfigDirectoryPath(String simulationName, boolean mkdirs)
            throws IOException {
        Path dir = getSimulationDirectory(simulationName, mkdirs).resolve(CONFIG_FILES_FOLDER_NAME);
        if (mkdirs) {
            Files.createDirectories(dir);
        }
        return dir;
    }

    /**
     * Provides the canonical location of the project directory with the specified name.
     *
     * @param simulationName  The name of the project. This is used as the main folder within the
     *                     BrainGrid folder for all files related to a given project.
     * @param mkdirs  Indicates whether or not to build the parent directories in the case that they
     *                do not yet exist
     * @return The canonical location for the parent directory of all files and directories related
     *         to a given project.
     * @throws IOException
     */
    public static Path getSimulationDirectory(String simulationName, boolean mkdirs)
            throws IOException {
        Path dir = getDefaultProjectDirectory().resolve(simulationName);
        if (mkdirs) {
            Files.createDirectories(dir);
        }
        return dir;
    }

    /**
     * Provides the path to the default project.
     *
     * @return The path to the default project
     */
    public static Path getDefaultProjectDirectory() {
        return getProjectsDirectory().resolve(DEFAULT_PROJECT_NAME);
    }

    /**
     * Provides the working directory of the current user.
     *
     * @return The working directory of the current user
     */
    public static Path getUserDir() {
        return Paths.get(System.getProperty("user.dir"));
    }

    /**
     * Provides the home directory of the current user.
     *
     * @return The home directory of the current user
     */
    public static Path getUserHome() {
        return Paths.get(System.getProperty("user.home"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Utility">
    /**
     * Utility function that returns the last name of a specified path string containing parent
     * folders.
     *
     * @param longFilename  Path string containing parent folders
     * @return The last name of the file, including the base name and extension, but no parent
     *         folders
     */
    public static String getSimpleFilename(String longFilename) {
        return FilenameUtils.getName(longFilename);
    }

    /**
     * Utility function that returns the last name of a specified path containing parent folders.
     *
     * @param longFilename  Path containing parent folders
     * @return The last name of the file, including the base name and extension, but no parent
     *         folders
     */
    public static String getSimpleFilename(Path longFilename) {
        return FilenameUtils.getName(longFilename.toString());
    }

    /**
     * Utility function that returns the base filename (no extension) of a specified path string
     * containing parent folders and/or an extension.
     *
     * @param longFilename  Path string containing parent folders and/or extension
     * @return The base filename (i.e. no parent folders or extension)
     */
    public static String getBaseFilename(String longFilename) {
        return FilenameUtils.getBaseName(longFilename);
    }

    /**
     * Utility function that returns the base filename (no extension) of a specified path containing
     * parent folders and/or an extension.
     *
     * @param longFilename  Path containing parent folders and/or extension
     * @return The base filename (i.e. no parent folders or extension)
     */
    public static String getBaseFilename(Path longFilename) {
        return FilenameUtils.getBaseName(longFilename.toString());
    }

    /**
     * Utility function that builds a path string from one or more string arguments. The order of
     * the path strings provided as arguments is maintained in the final path string, and the file
     * separator character is based on the current file system.
     *
     * @param first  first path string to convert
     * @param more  more path strings to convert
     * @return The path string built from the given path string arguments
     */
    public static String buildPathString(String first, String... more) {
        return Paths.get(first, more).toString();
    }

    /**
     * Copies a whole directory to a new location preserving the file dates. This method copies the
     * specified directory and all its child directories and files to the specified destination. The
     * destination is the new location and name of the directory.
     *
     * The destination directory is created if it does not exist. If the destination directory did
     * exist, then this method merges the source with the destination, with the source taking
     * precedence.
     *
     * @param source  Location of the source directory
     * @param target  Location of the target directory to hold copies of all files and
     *                subdirectories from the source directory
     * @throws IOException
     */
    public static void copyFolder(Path source, Path target) throws IOException {
        FileUtils.copyDirectory(source.toFile(), target.toFile());
    }

    /**
     * Copies a file from the source path to the target path. If the parent directories required in
     * the tree to target are not present, they will be created.
     *
     * Note: Errors are hidden and may occur do to various exceptions. No security privileges are
     * requested. This is intentional.
     *
     * @param source  The path to the file to be copied
     * @param target  The path to the copy of the source
     * @return True if the file was copied successfully, false if the file represented by the source
     *         path does not existing
     * @throws java.io.IOException
     */
    public static boolean copyFile(Path source, Path target) throws IOException {
        boolean success = true;
        if (Files.exists(source)) {
            if (!Files.exists(target)) {
                Files.createDirectories(target.getParent());
                Files.createFile(target);
            }
        } else {
            success = false;
        }
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS);
        return success;
    }

    /**
     * Utility function provided for the purpose of manipulating file locations to a Posix-valid
     * form.
     *
     * @param stmt  A file path or other statement that may contain characters that could be
     *              misinterpreted by Bash as parts of a filename rather than individual, but
     *              concatenated, parent directories
     * @return A valid Posix file path or statement
     */
    public static String toBashValidNotation(String stmt) {
        return stmt.replaceAll("\\\\", "/");
    }

    /**
     * Checks a string representing a filename for adherence to Windows naming conventions.
     * Windows need not be the platform that this function is concerned with, it just happens to
     * have the most restrictions on filename characters.
     *
     * Source: https://stackoverflow.com/a/6804755
     *
     * @param filename  String representing a filename
     * @return True if the file name specified is valid, False if not
     */
    public static boolean isValidFilename(String filename) {
        if (filenamePattern == null) {
            filenamePattern = Pattern.compile(
                    "# Match a valid Windows filename (unspecified file system).          \n"
                        + "^                                # Anchor to start of string.        \n"
                        + "(?!                              # Assert filename is not: CON, PRN, \n"
                        + "  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n"
                        + "    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n"
                        + "    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n"
                        + "  )                              # LPT6, LPT7, LPT8, and LPT9...     \n"
                        + "  (?:\\.[^.]*)?                  # followed by optional extension    \n"
                        + "  $                              # and end of string                 \n"
                        + ")                                # End negative lookahead assertion. \n"
                        + "[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n"
                        + "[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n"
                        + "$                                # Anchor to end of string.            ",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);
        }
        Matcher matcher = filenamePattern.matcher(filename);
        boolean isMatch = matcher.matches();
        return isMatch;
    }
    // </editor-fold>
}
