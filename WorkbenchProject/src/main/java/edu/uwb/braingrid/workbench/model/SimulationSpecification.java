package edu.uwb.braingrid.workbench.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.uwb.braingrid.workbench.project.ProjectMgr;

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

    //@cs-: JavadocVariable influence 18
    public static final String GIT_PULL_AND_CLONE = "Pull";
    public static final int GIT_PULL_AND_CLONE_INDEX = 1;
    public static final String GIT_NONE = "None";
    public static final int GIT_NONE_INDEX = 0;
    public static final String REMOTE_EXECUTION = ProjectMgr.REMOTE_EXECUTION;
    public static final String LOCAL_EXECUTION = ProjectMgr.LOCAL_EXECUTION;
    public static final String SEQUENTIAL_SIMULATION = "Sequential";
    public static final String BUILD_BUILD_OPTION = "Build";
    public static final String PRE_BUILT_BUILD_OPTION = "Pre-built";
    public static final int SEQUENTIAL_SIMULATION_INDEX = 0;
    public static final String PARALLEL_SIMULATION = "Parallel";
    public static final int PARALLEL_SIMULATION_INDEX = 1;
    public static final String END_SIM_SPEC_TEXT = "endSimSpec";
    public static final String SIM_EXEC_TEXT = "simExecutable";
    public static final String SIM_INPUTS_TEXT = "simInputs";
    public static final String SIM_OUTPUTS_TEXT = "simOutputs";
    public static final int REMOTE_EXECUTION_INDEX = 1;
    public static final int LOCAL_EXECUTION_INDEX = 0;

    private String username;
    private String hostAddress;
    private String sha1Key;
    private String buildOption;
    private String sourceCodeUpdating;
    private String simulationType;
    private String simulationLocale;
    private String simulationFolder;
    private String versionAnnotation;
    private String codeRepositoryLocation;
    private String simExecutable;
    private List<String> simInputs;
    private List<String> simOutputs;

    /**
     * Responsible for allocating the specification and instantiating members.
     */
    public SimulationSpecification() {
        simInputs = new ArrayList<>();
        simOutputs = new ArrayList<>();
        username = null;
        hostAddress = null;
        sha1Key = null;
        buildOption = null;
        sourceCodeUpdating = null;
        simulationType = null;
        simulationLocale = null;
        simulationFolder = null;
        versionAnnotation = null;
        codeRepositoryLocation = null;
        simExecutable = null;
    }

    /**
     * Provides the executable file name for a simulation based on the execution model provided.
     *
     * @param simulationType  Text to match against known simulation models and their respective
     *                        executable file names
     * @return The executable file name related to the specified simulation type
     */
    public static String getSimFilename(String simulationType) {
        String simExecutableToInvoke = null;
        if (simulationType != null) {
            simExecutableToInvoke = simulationType.equals(
                    SimulationSpecification.SEQUENTIAL_SIMULATION) ? "growth" : "growth_cuda";
        }
        return simExecutableToInvoke;
    }

    /**
     * Provides the file name for the executable file that will be invoked to start the simulation.
     *
     * @return The file name for the executable file that will be invoked to start the simulation
     */
    public String getSimExecutable() {
        return simExecutable;
    }

    /**
     * Provides a list of the simulation inputs. At the time when this function was written this can
     * only contain a single file path. It is maintained in a list for extensibility purposes.
     *
     * @return The list containing simulation input file locations
     */
    public List<String> getSimInputs() {
        return simInputs;
    }

    /**
     * Provides a list of the simulation outputs. At the time when this function was written this
     * list will only contain a single file path. It is maintained in a list for extensibility
     * purposes.
     *
     * @return The list containing simulation output file locations
     */
    public List<String> getSimOutputs() {
        return simOutputs;
    }

    /**
     * Provides the location of the folder where the simulator is executed, where its code is
     * downloaded to, and where it is built.
     *
     * @return The location of the simulator folder. If the simulator execution is specified to take
     *         place on a remote machine, this is relative to the root directory for the user.
     */
    public String getSimulatorFolder() {
        return simulationFolder;
    }

    /**
     * Provides the location for the simulator code. This location may be a repository depending on
     * what the source code updating option is set to, or it may simply be a folder where the code
     * resides if not.
     *
     * @return The location for the simulator code
     */
    public String getCodeLocation() {
        return codeRepositoryLocation;
    }

    /**
     * Provides the version annotation (note regarding the version of the code used to build the
     * software) for this simulation.
     *
     * @return The version annotation for this simulation
     */
    public String getVersionAnnotation() {
        return versionAnnotation;
    }

    /**
     * Provides the locale where the simulation will take place with respect to the machine where
     * the workbench is running. This value corresponds to the static members REMOTE_EXECUTION or
     * LOCAL_EXECUTION.
     *
     * @return The locale where the simulation will take place with respect to the machine where the
     *         workbench is running.
     */
    public String getSimulationLocale() {
        return simulationLocale;
    }

    /**
     * Provides the execution model for the simulation. This value should correspond to the toString
     * return of one of the values from the SimulatorType enumeration.
     *
     * @return A description of the execution model for the simulation
     * @see edu.uwb.braingrid.workbench.model.SimulationSpecification.SimulatorType
     */
    public String getSimulationType() {
        return simulationType;
    }

    /**
     * Provides a description of the source code updating type selected by the user. Possible values
     * include GIT_PULL_AND_CLONE and GIT_NONE. GIT_PULL_AND_CLONE means that the source code should
     * be pulled from the repository and should be built prior to execution. Whereas none, means
     * that there is no need to update the source code prior to executing the simulator file.
     *
     * TODO: Move GIT_PULL_CLONE to enum
     *
     * @return The description of the source code updating type
     */
    public String getSourceCodeUpdating() {
        return sourceCodeUpdating;
    }

    /**
     * Provides the host name or address specified for this simulation.
     *
     * @return The host name or address of the remote machine where the simulation should take
     *         place, or null if LOCAL_EXECUTION is the locale for the simulation
     */
    public String getHostAddr() {
        return hostAddress == null ? "" : hostAddress;
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
     * Provides the build option for the script. If this value is BUILD_BUILD_OPTION, then the
     * script should build the simulator executable prior to starting the simulation. If this value
     * is PRE_BUILT_BUILD_OPTION, then the script should simply start the simulation without first
     * building the simulator.
     *
     * TODO: Move to enum -Max
     *
     * @return The build option for the script
     */
    public String getBuildOption() {
        return buildOption;
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
     * Sets the location of the folder where the simulator will be (conditionally based on options
     * set in the specification) cloned/checked out to, built, and executed.
     *
     * @param simFolder  The location of the top-level folder of the simulation
     */
    public void setSimulatorFolder(String simFolder) {
        simulationFolder = simFolder;
    }

    /**
     * Sets the location of the source code. If source code updating is turned off in this
     * specification, then this location points to a folder on the machine where the simulation will
     * take place. If source code updating is turned on in this specification, then this location
     * points to a source code repository.
     *
     * @param url  The location of the source code for the simulation
     */
    public void setCodeLocation(String url) {
        codeRepositoryLocation = url;
    }

    /**
     * Sets the annotation (a note) text entered by the user to describe the version of the
     * simulator that will be executed.
     *
     * @param annotation  The annotation (a note) text entered by the user to describe the version
     *                    of the simulator that will be executed.
     */
    public void setVersionAnnotation(String annotation) {
        versionAnnotation = annotation;
    }

    /**
     * Sets the locale for the simulation. This can be remote or local, but should be set based on
     * the related static strings from this class.
     *
     * TODO: Make this an enum? -Max, or atleast move the values of REMOTE_EXECUTION and
     *       LOCAL_EXECUTION to ProjectMgr
     *
     * @param locale  The locale for the simulation. One of the following values:
     *                SimulationSpecification.REMOTE_EXECUTION or
     *                SimulationSpecification.LOCAL_EXECUTION
     */
    public void setSimulatorLocale(String locale) {
        simulationLocale = locale;
    }

    /**
     * Sets the text for the simulation execution model associated with this specification. This
     * text should be the same as the return of the toString method for one of the values in the
     * SimulatorType enumeration.
     *
     * @param type  Text representation of one of the values in the SimulatorType enumeration. The
     *              type describes the execution model for the simulation
     * @see edu.uwb.braingrid.workbench.model.SimulationSpecification.SimulatorType
     */
    public void setSimulationType(String type) {
        simulationType = type;
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
     * Sets the host name or address where the simulation will take place.
     *
     * @param hostAddr  The host name or address where the simulation will take place.
     */
    public void setHostAddr(String hostAddr) {
        hostAddress = hostAddr;
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
     * Sets the option for building the simulator prior to execution.
     *
     * @param buildOpt  The build directive. One of the following values: BUILD_BUILD_OPTION,
     *                  PRE_BUILT_BUILD_OPTION
     */
    public void setBuildOption(String buildOpt) {
        buildOption = buildOpt;
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
     * Sets the executable filename to invoke for this simulation.
     *
     * @param executableFilename  The executable filename to invoke for this simulation
     */
    public void setSimExecutable(String executableFilename) {
        simExecutable = executableFilename;
    }

    /**
     * Adds an input file path to the list of input files for this simulation. This path is based on
     * where the simulation was executed, not where the original input file was copied from.
     *
     * Note: When this function was written, all executable simulators are limited to a single input
     * file. However, input file locations are maintained in a list for extensibility.
     *
     * @param input  An input file path (relative to the simulation folder) to add to the list of
     *               input files for this simulation.
     */
    public void addInput(String input) {
        simInputs.add(input);
    }

    /**
     * Adds an output file path to the list of output files for this simulation. This path is based
     * on where the simulation was executed, not where the output file was copied to for workbench
     * use.
     *
     * Note: When this function was written, all executable simulators are limited to a single
     * output file. However, output file locations are maintained in a list for extensibility.
     *
     * @param output An output file path (relative to the simulation folder) to add to the list of
     *               output files for this simulation.
     */
    public void addOutput(String output) {
        simOutputs.add(output);
    }

    /**
     * Indicates whether or not this simulation should be executed on a remote machine or locally.
     * The default for this operation is an indication that the simulation should be executed
     * locally. This may occur when the locale was not previously set.
     *
     * @return True if the simulation should be executed on a remote machine, otherwise false
     */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimulationSpecification)) {
            return false;
        }
        SimulationSpecification that = (SimulationSpecification) o;
        return Objects.equals(getUsername(), that.getUsername())
                && Objects.equals(hostAddress, that.hostAddress)
                && Objects.equals(sha1Key, that.sha1Key)
                && Objects.equals(getBuildOption(), that.getBuildOption())
                && Objects.equals(getSourceCodeUpdating(), that.getSourceCodeUpdating())
                && Objects.equals(getSimulationType(), that.getSimulationType())
                && Objects.equals(getSimulationLocale(), that.getSimulationLocale())
                && Objects.equals(simulationFolder, that.simulationFolder)
                && Objects.equals(getVersionAnnotation(), that.getVersionAnnotation())
                && Objects.equals(codeRepositoryLocation, that.codeRepositoryLocation)
                && Objects.equals(getSimExecutable(), that.getSimExecutable())
                && Objects.equals(getSimInputs(), that.getSimInputs())
                && Objects.equals(getSimOutputs(), that.getSimOutputs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), hostAddress, sha1Key, getBuildOption(),
                getSourceCodeUpdating(), getSimulationType(), getSimulationLocale(),
                simulationFolder, getVersionAnnotation(), codeRepositoryLocation,
                getSimExecutable(), getSimInputs(), getSimOutputs());
    }
}
