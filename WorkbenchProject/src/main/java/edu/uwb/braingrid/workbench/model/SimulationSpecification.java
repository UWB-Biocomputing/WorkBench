package edu.uwb.braingrid.workbench.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;

/**
 * Data model for a simulation. Contains parameters recorded at the beginning of a script and used
 * to execute a simulation at the end of the script. Additionally contains fields used to determine
 * what setup operations will occur within the script prior to executing the simulation.
 *
 * @author Del Davis
 */
public class SimulationSpecification {

    /**
     * Description of the execution model for a simulation (in particular, these values indicate the
     * thread or core model used in simulation processing).
     */
    public enum SimulatorType {
        /** The simulator will be executed with a single core. */
        SEQUENTIAL,
        /** The simulator will be executed through CUDA on multiple GPU cores. */
        CUDA,
        /** The simulator type could not be detected. */
        UNKNOWN_SIMULATOR_TYPE
    }

    // <editor-fold defaultstate="collapsed" desc="Members">
    //@cs-: JavadocVariable influence 16
    public static final String REMOTE_EXECUTION = "Remote";
    public static final String LOCAL_EXECUTION = "Local";
    public static final int REMOTE_EXECUTION_INDEX = 1;
    public static final int LOCAL_EXECUTION_INDEX = 0;
    public static final String SEQUENTIAL_SIMULATION = "Sequential";
    public static final String PARALLEL_SIMULATION = "Parallel";
    public static final int SEQUENTIAL_SIMULATION_INDEX = 0;
    public static final int PARALLEL_SIMULATION_INDEX = 1;
    public static final String BUILD_BUILD_OPTION = "Build";
    public static final String PRE_BUILT_BUILD_OPTION = "Pre-built";
    public static final String GIT_PULL_AND_CLONE = "Pull";
    public static final String GIT_NONE = "None";
    public static final int GIT_PULL_AND_CLONE_INDEX = 1;
    public static final int GIT_NONE_INDEX = 0;
    public static final String SIM_EXEC_TEXT = "simExecutable";

    private String simulationLocale;
    private String hostname;
    private String username;
    private String simulatorFolder;
    private String simulatorType;
    private String buildOption;
    private String codeRepository;
    private String sourceCodeUpdating;
    private String sha1Key;
    private String versionAnnotation;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Construction">
    /**
     * Responsible for allocating the specification and instantiating members.
     */
    public SimulationSpecification() {
        simulationLocale = null;
        hostname = null;
        username = null;
        simulatorFolder = null;
        simulatorType = null;
        buildOption = null;
        codeRepository = null;
        sourceCodeUpdating = null;
        sha1Key = null;
        versionAnnotation = null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    /**
     * Provides the locale where the simulation will take place with respect to the machine where
     * the workbench is running. This value corresponds to the static members REMOTE_EXECUTION or
     * LOCAL_EXECUTION.
     *
     * @return The locale where the simulation will take place with respect to the machine where the
     *         workbench is running
     */
    public String getSimulationLocale() {
        return simulationLocale;
    }

    /**
     * Sets the locale where the simulation will take place with respect to the machine where the
     * workbench is running. This value corresponds to the static members REMOTE_EXECUTION or
     * LOCAL_EXECUTION.
     *
     * TODO: Make this an enum? -Max
     *
     * @param locale  The locale where the simulation will take place with respect to the machine
     *                where the workbench is running
     */
    public void setSimulationLocale(String locale) {
        simulationLocale = locale;
    }

    /**
     * Provides the host name or address of the remote machine where the simulation will take place,
     * or an empty string if the respective field is null.
     *
     * @return The host name or address of the remote machine where the simulation will take place,
     *         or an empty string if the respective field is null
     */
    public String getHostAddr() {
        return hostname == null ? "" : hostname;
    }

    /**
     * Sets the host name or address of the remote machine where the simulation will take place.
     *
     * @param hostAddr  The host name or address of the remote machine where the simulation will
     *                  take place
     */
    public void setHostAddr(String hostAddr) {
        hostname = hostAddr;
    }

    /**
     * Provides the username provided during a specification. This is not necessarily the login used
     * to actually stage files and execute the script or simulation on the remote machine.
     *
     * @return The username provided during simulation specification
     */
    public String getUsername() {
        return username == null ? "" : username;
    }

    /**
     * Sets the username specified for this simulation. This should only be used to indicate the
     * username, if any, that was provided during specification. When a remote connection is
     * requested, a new username is requested as well. That username should be used to indicate
     * connection information, not this one.
     *
     * @param name  The username specified at specification of the simulation
     */
    public void setUsername(String name) {
        username = name;
    }

    /**
     * Provides the location of the folder where the simulator is downloaded, built, and executed.
     * If the simulator execution is specified to take place on a remote machine, this is relative
     * to the home directory for the user.
     *
     * @return The location of the simulator folder
     */
    public String getSimulatorFolder() {
        return simulatorFolder;
    }

    /**
     * Sets the location of the folder where the simulator is downloaded, built, and executed. If
     * the simulator execution is specified to take place on a remote machine, this is relative to
     * the home directory for the user.
     *
     * @param simFolder  The location of the simulator folder
     */
    public void setSimulatorFolder(String simFolder) {
        // convert to absolute path
        String fullPath = simFolder;
        if (!simFolder.startsWith("~") && !simFolder.startsWith("/")) {
            fullPath = "~/" + simFolder;
        }
        simulatorFolder = fullPath;
    }

    /**
     * Provides the execution model for the simulation. This value should correspond to the toString
     * return of one of the values from the SimulatorType enumeration.
     *
     * @return A description of the execution model for the simulation
     * @see edu.uwb.braingrid.workbench.model.SimulationSpecification.SimulatorType
     */
    public String getSimulationType() {
        return simulatorType;
    }

    /**
     * Sets the execution model for the simulation. This value should correspond to the toString
     * return of one of the values from the SimulatorType enumeration.
     *
     * @param type  A description of the execution model for the simulation
     * @see edu.uwb.braingrid.workbench.model.SimulationSpecification.SimulatorType
     */
    public void setSimulationType(String type) {
        simulatorType = type;
    }

    /**
     * Provides the location for the simulator code. If source code updating is turned on, then the
     * location is a source code repository, otherwise it is simply the folder where the source code
     * resides.
     *
     * @return The location of the source code for the simulation
     */
    public String getCodeLocation() {
        return codeRepository;
    }

    /**
     * Sets the location for the simulator code. If source code updating is turned on, then the
     * location is a source code repository, otherwise it is simply the folder where the source code
     * resides.
     *
     * @param url  The location of the source code for the simulation
     */
    public void setCodeLocation(String url) {
        codeRepository = url;
    }

    /**
     * Provides a description of the source code updating type selected by the user. Possible values
     * include GIT_PULL_AND_CLONE and GIT_NONE. GIT_PULL_AND_CLONE means that the source code should
     * be pulled from the repository and should be built prior to execution. Whereas none, means
     * that there is no need to update the source code prior to executing the simulator file.
     *
     * TODO: Move GIT_PULL_CLONE to enum
     *
     * @return A description of the source code updating type
     */
    public String getSourceCodeUpdating() {
        return sourceCodeUpdating;
    }

    /**
     * Sets the text associated with source code updating type for this simulation. Source code
     * updating occurs just prior to simulation execution.
     *
     * @param updatingType  Text associated with the source code updating type for this simulation.
     *                      This should match one of the related static Strings in this class.
     */
    public void setSourceCodeUpdating(String updatingType) {
        sourceCodeUpdating = updatingType;
    }

    /**
     * Provides the SHA1 key used to checkout a specific version of the simulator.
     *
     * @return The SHA1 key for git checkout
     */
    public String getSHA1CheckoutKey() {
        return sha1Key == null ? "" : sha1Key;
    }

    /**
     * Sets the git commit key used to checkout a specific version of the software after a pull.
     *
     * @param sha1  An SHA1 hex key associated with a particular commit in the repository history
     */
    public void setSHA1CheckoutKey(String sha1) {
        sha1Key = sha1;
    }

    /**
     * Provides the annotation text (a note) entered by the user to describe the version of the
     * simulator that will be executed.
     *
     * @return The version annotation for this simulation
     */
    public String getVersionAnnotation() {
        return versionAnnotation;
    }

    /**
     * Sets the annotation text (a note) entered by the user to describe the version of the
     * simulator that will be executed.
     *
     * @param annotation  The version annotation for this simulation
     */
    public void setVersionAnnotation(String annotation) {
        versionAnnotation = annotation;
    }

    /**
     * Provides the file name for the executable file that will be invoked to start the simulation.
     *
     * @return The file name for the executable file that will be invoked to start the simulation
     */
    @JsonIgnore
    public String getSimExecutable() {
        return getSimExecutable(simulatorType);
    }

    /**
     * Provides the executable file name for a simulation based on the execution model provided.
     *
     * @param simulatorType  Text to match against known simulation models and their respective
     *                       executable file names
     * @return The executable file name related to the specified simulation type
     */
    public static String getSimExecutable(String simulatorType) {
        String executable = null;
        if (simulatorType != null) {
            executable = simulatorType.equals(SimulationSpecification.SEQUENTIAL_SIMULATION)
                    ? "growth" : "growth_cuda";
        }
        executable = "cgraphitti";
        return executable;
    }

    /**
     * Indicates whether or not this simulation should be executed on a remote machine or locally.
     * The default for this operation is an indication that the simulation should be executed
     * locally. This may occur when the locale was not previously set.
     *
     * @return True if the simulation should be executed on a remote machine, otherwise false
     */
    @JsonIgnore
    public boolean isRemote() {
        boolean remote = false;
        if (simulationLocale != null) {
            remote = simulationLocale.equals(REMOTE_EXECUTION);
        }
        return remote;
    }

    /**
     * Indicates whether or not the simulator should be build prior to execution. The default for
     * this operation is an indication that the simulator should be built first.
     *
     * @return True if the simulator should be built prior to executing the simulation
     */
    public boolean buildFirst() {
        boolean buildFirst = false;
        if (buildOption != null) {
            buildFirst = buildOption.equals(BUILD_BUILD_OPTION);
        }
        return buildFirst;
    }

    /**
     * Indicates whether or not the simulator for this simulation should be built from code that is
     * checked out based a particular commit.
     *
     * @return True if the simulator for this simulation should be built from code that is checked
     *         out based a particular commit, otherwise false
     */
    public boolean hasCommitCheckout() {
        return sha1Key != null && !sha1Key.isEmpty();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Overrides">
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimulationSpecification)) {
            return false;
        }
        SimulationSpecification that = (SimulationSpecification) o;
        return Objects.equals(simulationLocale, that.simulationLocale)
                && Objects.equals(hostname, that.hostname)
                && Objects.equals(username, that.username)
                && Objects.equals(simulatorFolder, that.simulatorFolder)
                && Objects.equals(simulatorType, that.simulatorType)
                && Objects.equals(buildOption, that.buildOption)
                && Objects.equals(codeRepository, that.codeRepository)
                && Objects.equals(sourceCodeUpdating, that.sourceCodeUpdating)
                && Objects.equals(sha1Key, that.sha1Key)
                && Objects.equals(versionAnnotation, that.versionAnnotation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(simulationLocale, hostname, username, simulatorFolder, simulatorType,
                buildOption, codeRepository, sourceCodeUpdating, sha1Key, versionAnnotation);
    }
    // </editor-fold>
}
