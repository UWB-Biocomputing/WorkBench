package edu.uwb.braingrid.workbench;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import edu.uwb.braingrid.general.LoggerHelper;
import edu.uwb.braingrid.provenance.ProvMgr;
import edu.uwb.braingrid.workbench.model.Project;
import edu.uwb.braingrid.workbench.model.Simulation;
import edu.uwb.braingrid.workbench.model.SimulationSpecification;
import edu.uwb.braingrid.workbench.script.Script;
import edu.uwb.braingrid.workbench.script.ScriptManager;
import edu.uwb.braingrid.workbench.ui.DynamicInputConfigurationDialog;
import edu.uwb.braingrid.workbench.ui.InputConfigClassSelectionDialog;
import edu.uwb.braingrid.workbench.ui.NewSimulationDialog;
import edu.uwb.braingrid.workbench.ui.ProvenanceQueryDialog;
import edu.uwb.braingrid.workbench.ui.SimulationSpecificationDialog;
import edu.uwb.braingrid.workbench.utils.DateTime;
import edu.uwb.braingrid.workbenchdashboard.WorkbenchDisplay;
import edu.uwb.braingrid.workbenchdashboard.WorkbenchStatusBar;
import edu.uwb.braingrid.workbenchdashboard.user.User;

/**
 * Manages all of the operations for the workbench. In turn, the operations manage instances of the
 * related data.
 *
 * @author Del Davis, Modified and Updated by Joseph Conquest
 * @version 1.3
 */
public final class WorkbenchManager {

    // <editor-fold defaultstate="collapsed" desc="Members">
    private static final Logger LOG = Logger.getLogger(WorkbenchManager.class.getName());

    /** Name of the default project. */
    public static final String DEFAULT_PROJECT_NAME = "Default";

    /** Single instance of WorkbenchManager. */
    private static WorkbenchManager instance = null;

    /** Messages for SimulationRuntimeDialog. */
    private String messageAccumulator;

    private Project project;
    private Simulation simulation;
    private ProvMgr prov;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Construction">
    /**
     * Responsible for allocating this manager and initializing all members.
     */
    private WorkbenchManager() {
        messageAccumulator = "";
        project = null;
        simulation = null;
        prov = null;
        initFileOutput();
        if (!openLastProject()) {
            initProject(DEFAULT_PROJECT_NAME);
        }
    }

    private void initFileOutput() {
        Path logsDir = FileManager.getWorkbenchDirectory().resolve("logs");
        String logFile = logsDir.resolve("WD-WorkbenchManager-log.%u").toString();
        FileHandler handler = null;
        try {
            handler = new FileHandler(logFile);
        } catch (SecurityException | IOException e) {
            LOG.severe(e.getMessage());
        }
        if (handler != null) {
            LOG.getParent().getHandlers()[0].setLevel(LoggerHelper.MIN_LOG_LEVEL);
            LOG.getParent().addHandler(handler);
        }
    }

    public static WorkbenchManager getInstance() {
        if (instance == null) {
            instance = new WorkbenchManager();
        }
        return instance;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Project Controls">
    /**
     * Creates a new project through the NewProjectDialog.
     *
     * @return True if a new project was initialized, otherwise false. Note, failure and
     *         cancellation are returned as the same value.
     */
    public boolean newProject() {
        LOG.info("Making New Project");
        boolean success;
        // Ask the user for a new project name
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Project");
        dialog.setHeaderText("Enter project name:");
        dialog.setContentText("Project");

        Optional<String> projectName = dialog.showAndWait();

        if (projectName.isPresent()) {
            success = initProject(projectName.get());
        } else {
            success = false;
            LOG.info("New project specification canceled");
        }
        return success;
    }

    /**
     * Initializes a new project, making it the current project. Note, the current project is saved
     * before the new project is created.
     *
     * @param name  Name to give the new project (as well as the name of the directory to store
     *              project data)
     * @return True if the new project was created and persisted successfully, otherwise false
     */
    public boolean initProject(String name) {
        // check if project already exists
        if (Files.exists(Project.getProjectFilePath(name))) {
            LOG.info("Project " + name + " already exists");
            return false;
        }

        LOG.info("Initializing a New Project: " + name);
        boolean success;

        // save current project
        saveProject();
        // make a new project
        project = new Project(name);
        projectChanged();
        // save new project
        success = saveProject();

        return success;
    }

    /**
     * Opens a project from a JSON file.
     *
     * @return True if the project was opened successfully, otherwise false
     */
    public boolean openProject() {
        boolean success = false;
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select a Project Specification...");
        chooser.setInitialDirectory(FileManager.getProjectsDirectory().toFile());
        ExtensionFilter filter = new ExtensionFilter("JSON file (*.json)", "*.json");
        chooser.getExtensionFilters().add(filter);

        File chosenFile = chooser.showOpenDialog(WorkbenchDisplay.getPrimaryStage());
        if (chosenFile != null) {
            String projectName = FileManager.getBaseFilename(chosenFile.getName());
            success = openProject(projectName);
        } else {
            LOG.info("Open Project Operation Cancelled");
        }

        return success;
    }

    private boolean openProject(String projectName) {
        LOG.info("Loading Project");
        ObjectMapper mapper = new ObjectMapper();

        Path projectFilePath = Project.getProjectFilePath(projectName);
        if (Files.exists(projectFilePath)) {
            try {
                saveProject();
                project = mapper.readValue(projectFilePath.toFile(), Project.class);
                projectChanged();
            } catch (IOException e) {
                LOG.severe(e.getMessage());
                return false;
            }
        } else {
            LOG.info("Project Not Found");
            return false;
        }
        projectChanged();
        LOG.info("Project loaded: "  + project.getName());
        return true;
    }

    private boolean openLastProject() {
        String last = User.getUser().getLastProject();
        return openProject(last);
    }

    /**
     * Saves the current project to disk in JSON format.
     *
     * @return True if the project was saved successfully, otherwise false
     */
    public boolean saveProject() {
        if (project == null) {
            return false;
        }

        LOG.info("Saving Project");
        ObjectMapper mapper = new ObjectMapper();

        Path projectPath = project.getProjectFilePath();
        try {
            Files.createDirectories(projectPath.getParent());
            mapper.writerWithDefaultPrettyPrinter().writeValue(projectPath.toFile(), project);
        } catch (IOException e) {
            LOG.severe(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Performs necessary tasks after changing the current project.
     */
    private void projectChanged() {
        WorkbenchStatusBar.updateProject(project.getName());
        User.getUser().setLastProject(project.getName());
        simulation = null;
        prov = null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Simulation Controls">
    /**
     * Creates a new simulation through the NewSimulationDialog.
     *
     * @return True if a new simulation was initialized, otherwise false. Note, failure and
     *         cancellation are returned as the same value, with the only difference being the
     *         messages that will be delivered through getMsg
     */
    public boolean newSimulation() {
        LOG.info("Making New Simulation");
        boolean success;
        clearMessages();
        // Ask the user for a new simulation name (validation in dialogue)
        NewSimulationDialog nsd = new NewSimulationDialog(true);

        if (nsd.getSuccess()) {
            success = initSimulation(nsd.getSimulationName(), nsd.isProvEnabled());
        } else {
            success = false;
            messageAccumulator += "\n" + "New simulation specification canceled\n";
        }
        return success;
    }

    /**
     * Initializes a new simulation.
     *
     * @param name  Name to give the new simulation
     * @param provEnabled  True if provenance should be enabled for the simulation
     * @return
     */
    public boolean initSimulation(String name, boolean provEnabled) {
        LOG.info("Initializing Simulation: " + name);
        Long functionStartTime = System.currentTimeMillis();
        Long accumulatedTime = 0L;
        boolean success = true;
        try {
            // create a new simulation
            simulation = new Simulation(name);

            // set provenance
            simulation.setProvenanceEnabled(provEnabled);
            if (provEnabled) {
                Long startTime = System.currentTimeMillis();
                try {
                    prov = new ProvMgr(simulation, false);
                } catch (IOException ex) {
                    messageAccumulator += "\n"
                            + ">Error initializing provenance"
                            + "home directory for this simulation...\n" + ex
                            + "\n";
                    throw ex;
                }
                accumulatedTime = DateTime.sumProvTiming(startTime, accumulatedTime);
            } else {
                prov = null;
            }
        } catch (IOException | NullPointerException e) {
            success = false;
            messageAccumulator += "\n"
                    + "Exception occurred while creating the simulation"
                    + "\n" + e.toString();
            simulation = null;
            prov = null;
        }
        DateTime.recordFunctionExecutionTime("WorkbenchManager", "initSimulation",
                System.currentTimeMillis() - functionStartTime, simulation.isProvenanceEnabled());
        if (simulation.isProvenanceEnabled()) {
            DateTime.recordAccumulatedProvTiming("WorkbenchManager", "initSimulation",
                    accumulatedTime);
        }
        return success;
    }

    /**
     * Adds the current simulation to the current project. If provenance is enabled, the provenance
     * file is persisted as well.
     *
     * <i>Assumption: This action is unreachable prior to specifying a new simulation or loading a
     * simulation from disk</i>
     */
    public void saveSimulation() {
        Long functionStartTime = System.currentTimeMillis();
        Long accumulatedTime = 0L;
        if (simulation != null) {
            // add simulation to project
            project.addSimulation(simulation);
            // persist project
            saveProject();
            // persist provenance
            if (simulation.isProvenanceEnabled()) {
                Long startTime = System.currentTimeMillis();
                persistProvenance();
                accumulatedTime = DateTime.sumProvTiming(startTime, accumulatedTime);
            }
            messageAccumulator += "\n" + "Simulation saved to "
                    + project.getProjectFilePath().toString()
                    + "\n";
        }
        DateTime.recordFunctionExecutionTime("WorkbenchManager", "saveSimulation",
                System.currentTimeMillis() - functionStartTime, simulation.isProvenanceEnabled());
        if (simulation.isProvenanceEnabled()) {
            DateTime.recordAccumulatedProvTiming("WorkbenchManager", "saveSimulation",
                    accumulatedTime);
        }
    }

    /**
     * Allows the user to configure the input for the simulation.
     *
     * @return True if the user followed through on the specification, False if the user canceled
     *         the specification.
     */
    public boolean configureSimulation() {
        String simulationName = getSimulationName();
        LOG.info("Configuring Simulation for " + simulationName);
        boolean success = false;

        if (!simulationName.equals("None")) {
            String configFilename = simulation.getSimConfigFilename();
            InputConfigClassSelectionDialog iccsd
                    = new InputConfigClassSelectionDialog(simulationName, true, configFilename);
            if (iccsd.getSuccess()) {
                DynamicInputConfigurationDialog icd = new DynamicInputConfigurationDialog(
                        simulationName, true, configFilename, iccsd.getInputConfigMgr(), null);
                String simConfigFile;
                String resultFileName;
                if (icd.getSuccess()) {
                    simConfigFile = icd.getBuiltFile();
                    resultFileName = icd.getResultFileName();
                    if (simConfigFile != null && resultFileName != null) {
                        simulation.setSimConfigFile(simConfigFile);
                        simulation.setSimResultFile(resultFileName);
                        if (simulation.isProvenanceEnabled()) {
                            prov.addFileGeneration("simulation_input_file_generation", null,
                                    "workbench", null, false, simConfigFile, null, null);
                        }
                        success = true;
                    }
                }
            }
        }
        return success;
    }

    /**
     * Allows the user to configure the input for the simulation via interactions with ProVis.
     *
     * @return True if the user followed through on the specification, False if the user canceled
     *         the specification.
     */
    public boolean configureSimulation(String inputPresets,
            HashMap<Character, String> nListPresets) {
        String simulationName = getSimulationName();
        LOG.info("Configuring Simulation for " + simulationName);
        boolean success = false;

        if (!simulationName.equals("None")) {
            String configFilename = inputPresets;
            InputConfigClassSelectionDialog iccsd = new InputConfigClassSelectionDialog(
                    simulationName, true, configFilename);
            if (iccsd.getSuccess()) {
                DynamicInputConfigurationDialog icd = new DynamicInputConfigurationDialog(
                        simulationName, true, configFilename, iccsd.getInputConfigMgr(),
                        nListPresets);
                String simConfigFile;
                String resultFileName;
                if (icd.getSuccess()) {
                    simConfigFile = icd.getBuiltFile();
                    resultFileName = icd.getResultFileName();
                    if (simConfigFile != null && resultFileName != null) {
                        simulation.setSimConfigFile(simConfigFile);
                        simulation.setSimResultFile(resultFileName);
                        if (simulation.isProvenanceEnabled()) {
                            prov.addFileGeneration("simulation_input_file_generation", null,
                                    "workbench", null, false, simConfigFile, null, null);
                        }
                        success = true;
                    }
                }
            }
        }
        return success;
    }

    /**
     * Updates the simulation specification for the currently open project based on user inputs
     * entered in a SimulationSpecificationDialog.
     *
     * @return True if the user clicked the OkButton in the SimulationSpecificationDialog (which
     *         validates required input in order for the action to be performed)
     */
    public boolean specifySimulation() {
        LOG.info("Specifying Simulation");
        SimulationSpecificationDialog spd = new SimulationSpecificationDialog(true);
        boolean success = spd.getSuccess();
        if (success) {
            simulation.setSimSpec(spd.toSimulationSpecification());
            messageAccumulator += "\n" + "New simulation specified: " + simulation.getName() + "\n";
        } else {
            messageAccumulator += "\n"
                    + "New simulation specification canceled\n";
        }
        return success;
    }

    /**
     * Updates the simulation specification for the currently open project based on user inputs
     * entered in a SimulationSpecificationDialog.
     *
     * @return True if the user clicked the OkButton in the SimulationSpecificationDialog (which
     *         validates required input in order for the action to be performed)
     */
    public boolean specifySimulation(String commitVersion) {
        LOG.info("Specifying Simulation");
        SimulationSpecificationDialog spd;
        SimulationSpecification simSpec;
        if (commitVersion != null) {
            simSpec = new SimulationSpecification();
            simSpec.setSHA1CheckoutKey(commitVersion);
            simSpec.setSourceCodeUpdating("Pull");
            simSpec.setBuildOption("Build");
            simSpec.setSimulatorFolder("BrainGrid");
            spd = new SimulationSpecificationDialog(true, simSpec);
        } else {
            spd = new SimulationSpecificationDialog(true);
        }
        boolean success = spd.getSuccess();
        if (success) {
            simulation.setSimSpec(spd.toSimulationSpecification());
            messageAccumulator += "\n" + "New simulation specified\n";
        } else {
            messageAccumulator += "\n" + "New simulation specification canceled\n";
        }
        return success;
    }

    /**
     * Generates a script based on simulator input files and the simulation specification.
     *
     * @return True if the script was generated and persisted successfully, otherwise false
     */
    public boolean generateScript() {
        LOG.info("Generate Script for " + simulation.getName());
        boolean success = false;
        Script script = ScriptManager.generateScript(simulation.getName(), simulation.getSimSpec(),
                simulation.getSimConfigFilename());
        if (script != null) {
            try {
                Path scriptFolder = simulation.getScriptLocation();
                Files.createDirectories(scriptFolder);
                String scriptName = simulation.getName() + "_script";
                String scriptFilename = scriptFolder.resolve(scriptName + ".sh").toString();
                script.persist(scriptFilename);
                simulation.addScript(scriptFilename);
                messageAccumulator += "\n" + "Script generated at: " + scriptFilename + "\n";
                success = true;
                // this is where prov would be if we didn't want to wait till
                // script execution to record the script's existence
            } catch (Exception e) {
                success = false;
                messageAccumulator += "\nThe script could not be generated.\n"
                        + e.getClass().toString() + " occurred:" + e.toString()
                        + "\n";
            }
        }
        return success;
    }

    /**
     * Runs the last generated script file. This entails moving the script to the directory
     * specified in the last specified simulation specification (which may be to a remote machine).
     * This also entails moving any required files for the successful execution of all commands
     * embedded in the script.
     *
     * @return True if all files were uploaded/copied successfully and the script was started,
     *         otherwise false
     */
    public boolean runScript() {
        boolean success = false;
        ScriptManager sm = new ScriptManager();
        try {
            String simulationName = simulation.getName();
            String scriptPath = simulation.getScriptFilePath();
            String[] neuronLists = FileManager.getNeuronListFilenames(simulationName);
            success = sm.runScript(prov, simulation.getSimSpec(), simulationName, scriptPath,
                    neuronLists, simulation.getSimConfigFilename());
            simulation.setScriptRan(success);
            simulation.setScriptStartedAt();
            messageAccumulator += sm.getOutstandingMessages();
        } catch (JSchException | SftpException | IOException | NullPointerException e) {
            messageAccumulator += "\n" + "Script did not run do to "
                    + e.getClass() + "...\n";
            messageAccumulator += "Exception message: " + e.getMessage();
        }

        return success;
    }

    /**
     * Analyzes the redirected provenance output from an executed script.
     *
     * @return The time in milliseconds since January 1, 1970, 00:00:00 GMT when the simulator
     *         finished execution. DateTime.ERROR_TIME indicates that the simulator has not finished
     *         execution
     * @see edu.uwb.braingrid.workbench.utils.DateTime
     */
    public long analyzeScriptOutput() {
        long timeCompleted = DateTime.ERROR_TIME;
        if (simulation != null) {
            if (!simulation.wasScriptOutputAnalyzed()) {
                ScriptManager scriptMgr = new ScriptManager();
                try {
                    messageAccumulator += "\n"
                            + "Gathering simulation provenance...\n";
                    Path targetFolder = simulation.getScriptLocation();
                    timeCompleted = scriptMgr.analyzeScriptOutput(simulation.getSimSpec(),
                            simulation, prov, targetFolder);
                    if (timeCompleted != DateTime.ERROR_TIME) {
                        simulation.setScriptCompletedAt(timeCompleted);
                        simulation.setScriptOutputAnalyzed(true);
                    }
                    messageAccumulator += scriptMgr.getOutstandingMessages();
                    messageAccumulator += "\n" + "Simulation provenance gathered\n";
                } catch (IOException | JSchException | SftpException e) {
                    messageAccumulator += scriptMgr.getOutstandingMessages();
                    messageAccumulator += "\n"
                            + "Simulation provenance could not be gathered due to "
                            + e.getClass() + "...\n";
                    messageAccumulator += "Exception message: " + e.getMessage();
                    e.printStackTrace();
                }
            } else {
                messageAccumulator += "\n"
                        + "Script output has already been analyzed for this simulation run"
                        + "\nTo analyze another run, respecify simulation or input and run again"
                        + "\n";
            }
        } else {
            messageAccumulator += "\n"
                    + "No project loaded... nothing to analyze.\n";
        }
        return timeCompleted;
    }

    /**
     * Removes the script from the project. Invalidation should occur whenever the script
     * specification or simulation specification changes. This is a safety measure meant to protect
     * against utilizing an expired script.
     */
    public void invalidateScriptGenerated() {
        simulation.removeScript();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Provenance Controls">
    /**
     * Allows the user to query the provenance for the currently open project.
     *
     * Note: In order for this action helper to be invoked, there must be a provenance file
     * associated with a project. Implicitly, a project must be loaded, otherwise this code should
     * not be reachable.
     */
    public void viewProvenance() {
        ProvenanceQueryDialog pqd = new ProvenanceQueryDialog(true, prov);
    }

    private void persistProvenance() {
        if (simulation != null) {
            try {
                prov.persist(simulation);
                messageAccumulator += "\n"
                        + "Provenance persisted to: "
                        + prov.getProvFileURI() + "\n";
            } catch (IOException e) {
                messageAccumulator += "\n"
                        + "Unable to persist provenance\n"
                        + e.toString() + "\n";
            }
        } else {
            messageAccumulator += "\n"
                    + "Unable to persist provenance... no project is loaded\n";
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    /**
     * Gets the name of the project that was last specified while opening or creating a new project.
     *
     * @return The name of the currently open project
     */
    public String getProjectName() {
        String name;
        if (project != null) {
            name = project.getName();
        } else {
            name = DEFAULT_PROJECT_NAME;
        }
        return name;
    }

    /**
     * Provides the name of the simulation that was last specified.
     *
     * @return The name of the current simulation
     */
    public String getSimulationName() {
        String name;
        if (simulation != null) {
            name = simulation.getName();
        } else {
            name = "None";
        }
        return name;
    }

    /**
     * Answers a query regarding whether or not provenance is enabled for the currently open
     * project.
     *
     * Note: This implicitly means that a project must be loaded for this code to be reachable.
     *
     * @return True if provenance is enabled for the currently open project, otherwise false
     */
    public boolean isProvEnabled() {
        boolean isEnabled = false;

        if (simulation != null) {
            isEnabled = simulation.isProvenanceEnabled();
        }

        return isEnabled;
    }

    /**
     * Provides the status of moving and executing the script.
     *
     * @return The status of moving and executing the script in the form of the time when the script
     *         was executed, if it was copied and executed successfully, or if not, the default text
     *         for this status prior to the attempt to execute
     */
    public String getScriptRunOverview() {
        String scriptRunMsg = "None";
        long runAt;
        if (simulation != null) {
            runAt = simulation.getScriptStartedAt();
            if (runAt != DateTime.ERROR_TIME) {
                String time = DateTime.getTime(runAt);
                scriptRunMsg = "Script execution started at: " + time;
            }
        }
        return scriptRunMsg;
    }

    /**
     * Provides an overview the analysis process for the output generated by executing a script. In
     * particular, the provenance related output.
     *
     * @return An overview of the analysis process if the output redirected from the script was
     *         downloaded/copied successfully and the script finished execution. If the script
     *         hasn't finished executing, but the copy/download was successful, then a message
     *         indicating that the execution is incomplete is returned. If the script never ran
     *         (was not downloaded or uploaded and executed in the first place) the initial text
     *         for this status is returned.
     */
    public String getScriptAnalysisOverview() {
        String overview = "None";
        long completedAt;
        if (simulation != null) {
            completedAt = simulation.getScriptCompletedAt();
            if (completedAt != DateTime.ERROR_TIME) {
                overview = "Completed at: " + DateTime.getTime(completedAt);
            } else {
                if (simulation.hasScriptRun()) {
                    overview = "Script execution incomplete, try again later.";
                }
            }
        }
        return overview;
    }

    public ProvMgr getProvMgr() {
        return prov;
    }

    /**
     * Provides all of the messages that have accumulated since the construction of this manager or
     * since the messages were last cleared.
     *
     * @return The messages that have accumulated since the construction of this manager or since
     *         the messages were last cleared
     */
    public String getMessages() {
        return messageAccumulator;
    }

    /**
     * Clears the accumulated messages for this manager.
     */
    private void clearMessages() {
        messageAccumulator = "";
    }
    // </editor-fold>

    public boolean configureParamsClasses() {
        // This function will be able to add/modify/delete parameter classes.
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
