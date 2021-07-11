package edu.uwb.braingrid.workbench;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

import edu.uwb.braingrid.general.DirMgr;
import edu.uwb.braingrid.workbenchdashboard.userModel.User;

/**
 * Handles all file operations for the workbench. The purpose behind this singleton manager is
 * two-fold:
 * 1. Provides conditional support between operating system file paths (this is not possible in
 *    static manner without querying the system properties repeatedly);
 * 2. Provides a separation of concerns in terms of workbench configuration for file operations
 *    (such as folder hierarchical relationships and names);
 * 3. Provides workbench specific robustness in terms of file operations such as copy.
 *
 * Note: As mentioned in the overview above, this is a singleton class. Many methods are non-static.
 * In order to use the file manager, obtain a reference by calling FileManager.getFileManager.
 *
 * @author Del Davis
 */
public final class FileManager {

    // <editor-fold defaultstate="collapsed" desc="Members">
    private static final Logger LOG = Logger.getLogger(FileManager.class.getName());
    private static FileManager instance = null;
    private static String brainGridRepoDirectory = DirMgr.getBrainGridRepoDirectory();
    private static Pattern filenamePattern = null;
    private static Pattern directoryNamePattern = null;

    private final boolean isWindowsSystem;
    private final String folderDelimiter;
    private final String projectsFolderName = "projects";
    private final String configFilesFolderName = "configfiles";
    private final String neuronListFolderName = "NList";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Construction">
    /**
     * This is here to make sure that classes from other packages cannot instantiate the file
     * manager. This is to ensure that only one file manager is present in the workbench for the
     * life of the workbench control thread.
     */
    private FileManager() {
        LOG.info("New " + getClass().getName());
        String osName = System.getProperty("os.name").toLowerCase();
        isWindowsSystem = osName.startsWith("windows");
        folderDelimiter = isWindowsSystem ? "\\" : "/";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    /**
     * Gets or instantiates this file manager.
     *
     * Note: This is a singleton class with lazy instantiation.
     *
     * @return The file manager for the workbench
     */
    public static FileManager getFileManager() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    public String[] getNeuronListFilenames(String projectName) throws IOException {
        String[] filenames = null;
        File[] files;
        String folder = getSimConfigDirectoryPath(projectName, false)
                + neuronListFolderName + folderDelimiter;
        File folderFile = Paths.get(folder).toFile();
        if (folderFile.isDirectory()) {
            files = folderFile.listFiles();
            filenames = new String[files.length];
            for (int i = 0, im = files.length; i < im; i++) {
                filenames[i] = files[i].getCanonicalPath();
            }
        }
        return filenames;
    }

    /**
     * Provides the operating system dependent folder delimiter, not to be confused with the poorly
     * named File.PathSeperator.
     *
     * @return The string that delimits folders from parent folders on the operating system where
     *         the workbench was invoked
     */
    public String getFolderDelimiter() {
        return folderDelimiter;
    }

    /**
     * Returns the canonical form of the current working directory.
     *
     * @return A string representation of the system dependent unique canonical form of the current
     *         working directory
     * @throws java.io.IOException
     */
    public static String getCanonicalWorkingDirectory() throws IOException {
        return Paths.get("").toFile().getCanonicalPath();
    }

    public String getCanonicalProjectsDirectory() throws IOException {
        return getCanonicalWorkingDirectory() + folderDelimiter + projectsFolderName;
    }

    /**
     * Indicates whether or not the operating system is some version of Microsoft Windows.
     *
     * @return True if the system is a windows-based system, otherwise false. As of the implementing
     *         of this workbench, false can be interpreted as a Posix-based system.
     */
    public boolean isWindowsSystem() {
        return isWindowsSystem;
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
    public String getSimConfigFilePath(String projectName, String filename, boolean mkdirs)
            throws IOException {
        return getSimConfigDirectoryPath(projectName, mkdirs) + filename;
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
    public String getNeuronListFilePath(String projectName, String filename, boolean mkdirs)
            throws IOException {
        String folder = getSimConfigDirectoryPath(projectName, mkdirs)
                + neuronListFolderName + folderDelimiter;
        if (mkdirs) {
            new File(folder).mkdirs();
        }
        return folder + filename;
    }

    /**
     * Provides the canonical location of the parent directory for all simulation configuration
     * related files.
     *
     * @param projectName  The name of the project. The project name is used as the parent
     *                     directory to the sim config directory (project name is used as the main
     *                     directory name for the project. e.g. if the BrainGrid working directory
     *                     is folder/BrainGrid and the project name is myProject, then
     *                     folder/BrainGrid/myProject/ contains the simulation configuration
     *                     directory, and subsequently, the simulation configuration file).
     * @param mkdirs  Indicates whether or not to build the parent directories in the case that they
     *                do not yet exist
     * @return The canonical location of the parent directory for all simulation configuration
     *         related files.
     * @throws IOException
     */
    public String getSimConfigDirectoryPath(String projectName, boolean mkdirs) throws IOException {
        String folder = getProjectDirectory(projectName, mkdirs)
                + configFilesFolderName + folderDelimiter;
        if (mkdirs) {
            new File(folder).mkdirs();
        }
        return folder;
    }

    /**
     * Provides the canonical location of the project directory with the specified name.
     *
     * @param projectName  The name of the project. This is used as the main folder within the
     *                     BrainGrid folder for all files related to a given project.
     * @param mkdirs  Indicates whether or not to build the parent directories in the case that they
     *                do not yet exist
     * @return The canonical location for the parent directory of all files and directories related
     *         to a given project.
     * @throws IOException
     */
    public String getProjectDirectory(String projectName, boolean mkdirs) throws IOException {
        String directory = getCanonicalProjectsDirectory()
                + folderDelimiter + projectName + folderDelimiter;
        if (mkdirs) {
            new File(directory).mkdirs();
        }
        return directory;
    }

    /**
     * Provides the canonical location of the working directory of the current user.
     *
     * @return The canonical location of the working directory of the current user
     */
    public String getUserDir() {
        return User.getInstance().getRootDir() + folderDelimiter;
    }

    /**
     * Provides the canonical location of the home directory of the current user.
     *
     * @return The canonical location of the home directory of the current user
     */
    public String getUserHome() {
        return User.getInstance().getHomeDir() + folderDelimiter;
    }

    public static void updateStaticVals(FileManagerShared obj) {
        setBrainGridRepoDirectory(obj.getBrainGridRepoDirectory());
    }

    public static String getBrainGridRepoDirectory() {
        return FileManager.brainGridRepoDirectory;
    }

    public static void setBrainGridRepoDirectory(String brainGridRepoDirectory) {
        FileManager.brainGridRepoDirectory = brainGridRepoDirectory;
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
        String filename = longFilename;
        if (longFilename.contains("\\")) {
            int lastBackslash = longFilename.lastIndexOf('\\');
            if (lastBackslash != longFilename.length() - 1) {
                filename = longFilename.substring(lastBackslash + 1);
            }
        } else if (longFilename.contains("/")) {
            int lastForwardslash = longFilename.lastIndexOf('/');
            if (lastForwardslash != longFilename.length() - 1) {
                filename = longFilename.substring(lastForwardslash + 1);
            }
        } else {
            filename = longFilename;
        }
        return filename;
    }

    public static String getLastNamePrefix(String longFilename) {
        String lastNamePrefix = null;
        if (longFilename != null) {
            String filename = getSimpleFilename(longFilename);
            int lastIndexOfDot = filename.lastIndexOf('.');
            lastNamePrefix = filename.substring(0, lastIndexOfDot);
        }
        return lastNamePrefix;
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
     * @param src  Location of the source directory
     * @param dst  Location of the target directory to hold copies of all files and subdirectories
     *             from the source directory
     * @throws IOException
     */
    public static void copyFolder(String src, String dst) throws IOException {
        FileUtils.copyDirectory(new File(src), new File(dst));
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
        File fromFile = source.toFile();
        if (fromFile.exists()) {
            File toFile = target.toFile();
            if (!toFile.exists()) {
                target.getParent().toFile().mkdirs();
                toFile.createNewFile();
            }
        } else {
            success = false;
        }
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES, LinkOption.NOFOLLOW_LINKS);
        return success;
    }

    /**
     * Utility function provides for the purpose of manipulating file locations to a Posix-valid
     * form.
     *
     * @param stmt  A file name or other statement that may contain characters that could be
     *              misinterpreted by Bash as parts of a filename rather than individual, but
     *              concatenated, parent directories
     * @return
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
