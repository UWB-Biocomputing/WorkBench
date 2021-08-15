package edu.uwb.braingrid.workbench.project;

import java.nio.file.Path;
import java.util.logging.Logger;

import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbench.model.ScriptHistory;
import edu.uwb.braingrid.workbench.model.SimulationSpecification;
import edu.uwb.braingrid.workbench.utils.DateTime;

/**
 * Represents a BrainGrid simulation as part of a Workbench project.
 *
 * <p>Note: Provenance support is dealt with after the simulation is constructed</p>
 *
 * @author Steven Leighton
 */
public class Simulation {

    // <editor-fold defaultstate="collapsed" desc="Members">
    private static final Logger LOG = Logger.getLogger(Simulation.class.getName());

    private String name;
    private boolean provEnabled;
    private String simConfigFile;
    private String resultFileName;
    private SimulationSpecification simSpec;
    private ScriptHistory scriptHistory;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Construction">
    /**
     * Constructs a simulation, either by creating a new simulation or by loading an existing
     * simulation from disk.
     *
     * @param simulationName  Name of the simulation
     */
    public Simulation(String simulationName) {
        LOG.info("New simulation: " + simulationName);
        initState(simulationName);
    }

    private void initState(String simulationName) {
        name = simulationName;
        provEnabled = false;
        simConfigFile = null;
        resultFileName = null;
        simSpec = null;
        scriptHistory = null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    /**
     * Provides the name of the simulation.
     *
     * @return The name of the simulation
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the simulation's name.
     *
     * @param name  The name given to the simulation
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Indicates if provenance support is enabled for this simulation.
     *
     * @return True if provenance support is enabled, otherwise false
     */
    public boolean isProvenanceEnabled() {
        return provEnabled;
    }

    /**
     * Sets the value used to determine if provenance support is enabled for this simulation.
     *
     * @param enabled  Whether or not this simulation should record provenance
     */
    public void setProvenanceEnabled(boolean enabled) {
        provEnabled = enabled;
    }

    /**
     * Provides the full path to the simulation configuration file.
     *
     * @return The full path to simulation configuration file, or null if no file was specified
     */
    public String getSimConfigFilename() {
        return simConfigFile;
    }

    /**
     * Specifies the location of the simulation configuration file.
     *
     * @param filename  The full path to the newly added configuration file
     */
    public void setSimConfigFile(String filename) {
        simConfigFile = filename;
    }

    /**
     * Provides the full path to the simulation result file. The filename provided is of the
     * imported file (within the simulation directory in the workbench managed directories).
     *
     * @return The filename of the simulation result file imported into the workbench. To be clear,
     *         this is the filename of the target of the import, not the source of the import.
     */
    public String getSimResultFile() {
        return resultFileName;
    }

    public void setSimResultFile(String filename) {
        this.resultFileName = filename;
    }

    /**
     * Provides the current simulation specification. If the simSpec has yet to be added to the
     * simulation, it is automatically added here.
     *
     * @return The current simulation specification
     */
    public SimulationSpecification getSimSpec() {
        if (simSpec == null) {
            simSpec = new SimulationSpecification();
        }
        return simSpec;
    }

    public void setSimSpec(SimulationSpecification simSpec) {
        this.simSpec = simSpec;
    }

    /**
     * Add a specification for the simulator used in this simulation to the model. The elements of a
     * specification are used to determine the content of an execution script as well as where it
     * will be executed.
     *
     * @param simulationLocale  Indicates where the simulator will be executed (e.g.
     *                                    Remote, or Local)
     * @param hostname  The name of the remote host, if the simulator will be executed remotely
     * @param simFolder  The top folder where the script will be deployed to. This also serves as
     *                   the parent folder of the local copy of the simulator source code.
     * @param simulationType  Indicates which version of the simulator will be executed (e.g.
     *                        growth, or growth_CUDA)
     * @param buildOption
     * @param codeLocation  The location of the repository that contains the code for the simulator
     * @param sourceCodeUpdating  Whether the source code should be updated prior to execution (e.g.
     *                            Pull, or None). If sourceCodeUpdating is set to first do a pull on
     *                            the repository, a clone will be attempted first in case the
     *                            repository has yet to be cloned.
     * @param sha1Key
     * @param versionAnnotation  A human interpretable note regarding the version of the simulator
     *                           that will be executed
     */
    public void addSimulator(String simulationLocale, String hostname, String simFolder,
            String simulationType, String buildOption, String codeLocation,
            String sourceCodeUpdating, String sha1Key, String versionAnnotation) {
        simSpec = new SimulationSpecification();
        simSpec.setSimulationLocale(simulationLocale);
        simSpec.setHostAddr(hostname);
        simSpec.setSimulatorFolder(simFolder);
        simSpec.setSimulationType(simulationType);
        simSpec.setBuildOption(buildOption);
        simSpec.setCodeLocation(codeLocation);
        simSpec.setSourceCodeUpdating(sourceCodeUpdating);
        simSpec.setSHA1CheckoutKey(sha1Key);
        simSpec.setVersionAnnotation(versionAnnotation);
    }

    /**
     * Removes the currently specified simulator from the simulation.
     */
    public void removeSimulator() {
        simSpec = null;
    }

    public ScriptHistory getScriptHistory() {
        return scriptHistory;
    }

    public void setScriptHistory(ScriptHistory scriptHistory) {
        this.scriptHistory = scriptHistory;
    }

    /**
     * Adds a generated script file to the simulation.
     *
     * @param scriptFilename  The base-name of the file path
     */
    public void addScript(String scriptFilename) {
        int version = (scriptHistory != null) ? scriptHistory.getVersion() + 1 : 1;
        scriptHistory = new ScriptHistory(scriptFilename);
        scriptHistory.setVersion(version);
    }

    private void addScript(String filename, String version, String startedAt, String completedAt,
            String analyzed, String ran) {
        scriptHistory = new ScriptHistory(filename);

        // add details, if available
        try {
            scriptHistory.setVersion(Integer.parseInt(version));
        } catch (NumberFormatException ignored) {
        }
        try {
            scriptHistory.setStartedAt(Long.parseLong(startedAt));
            scriptHistory.setCompletedAt(Long.parseLong(completedAt));
        } catch (NumberFormatException ignored) {
        }
        try {
            scriptHistory.setOutputAnalyzed(Boolean.parseBoolean(analyzed));
        } catch (NumberFormatException ignored) {
        }
        try {
            scriptHistory.setRan(Boolean.parseBoolean(ran));
        } catch (NumberFormatException ignored) {
        }
    }

    /**
     * Removes the currently specified script from the simulation.
     */
    public void removeScript() {
        scriptHistory = null;
    }

    /**
     * Determines if prerequisite simulation data is available in order to generate a script.
     *
     * @return True if the prerequisite data is available for generating a script, false if not
     */
    public boolean scriptGenerationAvailable() {
        return scriptHistory == null && simConfigFile != null && simSpec != null;
    }

    /**
     * Indicates whether or not a script has been generated for this simulation.
     *
     * @return True if the a script has been generated for this simulation, otherwise false
     */
    public boolean scriptGenerated() {
        return scriptHistory != null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Data Manipulation">
    /**
     * Provides the locale for the simulation. In other words the relationship between where a
     * simulation should take place and the machine running the workbench. Since the simulation
     * locale is set based on values from SimulationSpecification, the return value is indirectly
     * dependent upon the definitions provided by the SimulationSpecification class.
     *
     * @return The locale for the simulation
     * @see edu.uwb.braingrid.workbench.model.SimulationSpecification
     */
    public String getSimulationLocale() {
        return simSpec.getSimulationLocale();
    }

    /**
     * Provides the host name of the machine where a simulation will be run. This value is not
     * guaranteed to be set for all simulations. If the simulation is set to be run locally, this
     * function will return a null value.
     *
     * @return The host name of the machine where a simulation will be run, or null if the
     *         simulation is set to run locally
     */
    public String getSimulatorHostname() {
        return simSpec.getHostAddr();
    }

    /**
     * Provides the folder location where the simulator code is moved to and the simulator is built
     * and executed.
     *
     * <p>Note: This may be an absolute or canonical path on the local file system, or it may be a
     * path on a remote machine relative to the starting path of a remote connection.</p>
     *
     * @return The folder location where the simulator code is moved to and the simulator is built
     *         and executed.
     */
    public String getSimulatorFolderLocation() {
        return simSpec.getSimulatorFolder();
    }

    /**
     * Provides the simulation type associated with this simulation. The possible values are
     * indirectly determined by the Simulation Specification. In general, these values indicate the
     * processing model for the simulation (The threading or core model). This value can be used to
     * determine which executable to invoke in running the simulation.
     *
     * @return The simulation type associated with this simulation
     * @see edu.uwb.braingrid.workbench.model.SimulationSpecification.SimulatorType
     */
    public String getSimulationType() {
        return simSpec.getSimulationType();
    }

    public String getBuildOption() {
        return simSpec.getBuildOption();
    }

    /**
     * Provides the repository location (local or otherwise) for the code for compiling the
     * simulator binaries.
     *
     * @return The central location (possibly a repository URL or URI) where the code resides for
     *         compiling the simulator binaries
     */
    public String getSimulatorCodeLocation() {
        return simSpec.getCodeLocation();
    }

    /**
     * Provides the source code updating type for the simulation. Possible values are indirectly
     * dependent on the SimulationSpecification model. Generally, this determines whether or not to
     * update the code in the folder location where the simulator will be compiled and run prior to
     * executing the simulation.
     *
     * @return The source code updating type for the simulation
     * @see edu.uwb.braingrid.workbench.model.SimulationSpecification
     */
    public String getSimulatorSourceCodeUpdatingType() {
        return simSpec.getSourceCodeUpdating();
    }

    public String getSHA1Key() {
        return simSpec.getSHA1CheckoutKey();
    }

    /**
     * Provides the version annotation for the simulation specification associated with this
     * simulation.
     *
     * @return The version annotation for the simulation
     */
    public String getSimulatorVersionAnnotation() {
        return simSpec.getVersionAnnotation();
    }

    /**
     * Provides the name of the simulator executable associated with this simulation.
     *
     * @return The name of the simulator executable associated with this simulation.
     */
    public String getSimulatorExecutable() {
        return simSpec.getSimExecutable();
    }

    /**
     * Gets the location of the script file.
     *
     * @return The location of the script file, or null if no file path is set
     */
    public String getScriptFilePath() {
        return (scriptHistory != null) ? scriptHistory.getFilename() : null;
    }

    /**
     * Provides the version of the script currently associated with the simulation. This value can
     * be used to determine the base name of the script file name.
     *
     * @return The version of the script currently associated with the simulation
     */
    public String getScriptVersion() {
        return (scriptHistory != null) ? String.valueOf(scriptHistory.getVersion()) : null;
    }

    /**
     * Provides the version number of the next script that will be added to the simulation. This is
     * a convenience function, the version number is determined based on the current script version.
     *
     * @return The version number of the next script that will be added to the simulation when
     *         another script is generated.
     */
    public String getNextScriptVersion() {
        return (scriptHistory != null) ? String.valueOf(scriptHistory.getVersion() + 1) : "1";
    }

    /**
     * Sets the script version for the simulation.
     *
     * @param version  The version number of the current script for the simulation
     */
    public void setScriptVersion(int version) {
        scriptHistory.setVersion(version);
    }

    /**
     * Provides the number of milliseconds since January 1, 1970, 00:00:00 GMT when the execution
     * started for the script associated with this simulation.
     *
     * @return The number of milliseconds since January 1, 1970, 00:00:00 GMT when the execution
     *         started for the script associated with this simulation
     */
    public long getScriptStartedAt() {
        return (scriptHistory != null) ? scriptHistory.getStartedAt() : DateTime.ERROR_TIME;
    }

    /**
     * Sets the datetime when the script execution started. Also sets the attribute used to
     * determine whether or not the script has been executed to "true".
     */
    public void setScriptStartedAt() {
        scriptHistory.setStartedAt(DateTime.now());
        scriptHistory.setRan(true);
    }

    /**
     * Provides the number of milliseconds since January 1, 1970, 00:00:00 GMT when execution
     * completed for the script associated with this simulation.
     *
     * @return The number of milliseconds since January 1, 1970, 00:00:00 GMT when execution
     *         completed for the script associated with this simulation
     */
    public long getScriptCompletedAt() {
        return (scriptHistory != null) ? scriptHistory.getCompletedAt() : DateTime.ERROR_TIME;
    }

    /**
     * Sets the datetime when the script execution completed. Also sets the attribute used to
     * determine when the script completed execution.
     *
     * Note: This should be verified through the OutputAnalyzer class first.
     *
     * @param timeCompleted  The number of milliseconds since January 1, 1970, 00:00:00 GMT when
     *                       execution completed for the script associated with this simulation
     */
    public void setScriptCompletedAt(long timeCompleted) {
        scriptHistory.setCompletedAt(timeCompleted);
    }

    /**
     * Indicates wither the output of the current script version has been analyzed yet.
     *
     * @return True if the script output has been analyzed, otherwise false
     */
    public boolean wasScriptOutputAnalyzed() {
        return scriptHistory != null && scriptHistory.isOutputAnalyzed();
    }

    /**
     * Sets a string representation of whether or not the output of the current script version has
     * been analyzed.
     *
     * @param analyzed  Indication of whether or not the analysis has been completed
     */
    public void setScriptOutputAnalyzed(boolean analyzed) {
        scriptHistory.setOutputAnalyzed(analyzed);
    }

    /**
     * Determines whether or not the script has been executed.
     *
     * Note: This should not be used to determine if the script has completed execution
     *
     * @return True if the script has been executed, otherwise false
     */
    public boolean hasScriptRun() {
        return scriptHistory != null && scriptHistory.isRan();
    }

    /**
     * Sets the value for the attribute used to determine whether the script has run or not.
     *
     * @param isRan  Whether or not the script has been executed
     */
    public void setScriptRan(boolean isRan) {
        scriptHistory.setRan(isRan);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Path Info">
    /**
     * Provides the folder location for this simulation.
     *
     * @return The path to the simulation folder for this simulation
     */
    public Path getSimulationLocation() {
        return getProjectLocation().resolve(name);
    }

    /**
     * Provides the assumed folder location for a simulation of a given name.
     *
     * @param simulationName  Name of the simulation
     * @return The path to the simulation folder for the specified simulation
     */
    public static Path getSimulationLocation(String simulationName) {
        return getProjectLocation().resolve(simulationName);
    }

    /**
     * Provides the folder location for the current project.
     *
     * @return The path to the project folder for the current project
     */
    private static Path getProjectLocation() {
        return FileManager.getCurrentProjectDirectory();
    }

    /**
     * Provides the folder location for storing provenance data for this simulation.
     *
     * @return The path to the provenance folder for this simulation
     */
    public Path getProvLocation() {
        return getSimulationLocation().resolve("provenance");
    }

    /**
     * Provides the full path, including the filename, containing the XML for this simulation.
     *
     * @return The full path, including the filename, for the file containing the XML for this
     *         simulation
     */
    public Path getSimulationFilePath() {
        return getSimulationLocation().resolve(name + ".xml");
    }
    // </editor-fold>
}
