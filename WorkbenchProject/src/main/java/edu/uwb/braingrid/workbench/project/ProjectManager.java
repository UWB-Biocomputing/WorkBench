package edu.uwb.braingrid.workbench.project;
// NOT CLEANED (Still Implementing / Testing / JavaDocs / Class Header)

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbench.model.ScriptHistory;
import edu.uwb.braingrid.workbench.model.SimulationSpecification;
import edu.uwb.braingrid.workbench.project.model.Datum;
import edu.uwb.braingrid.workbench.project.model.ProjectData;
import edu.uwb.braingrid.workbench.utils.DateTime;

/**
 *
 * @author Aaron
 */
public class ProjectManager {

    // <editor-fold defaultstate="collapsed" desc="Members">
    private static final int DEFAULT_SCRIPT_VERSION = 0;
//    private static final String PROJECT_TAG_NAME = "project";
//    private static final String PROJECT_NAME_ATTRIBUTE = "name";
    private static final String PROV_TAG_NAME = "provenance";

    // FIX THIS : Find where this is used
//    private static final String PROV_LOCATION_TAG_NAME = "location";

    private static final String PROV_ENABLED_ATTRIBUTE_NAME = "enabled";
    private static final String SIMULATOR_TAG_NAME = "simulator";
    private static final String SIMULATOR_EXECUTION_MACHINE = "executionMachine";
    private static final String HOSTNAME_TAG_NAME = "hostname";
    private static final String SIM_FOLDER_TAG_NAME = "simulatorFolder";
    private static final String SIMULATION_TYPE_TAG_NAME = "ProcessingType";
    private static final String SIMULATOR_SOURCE_CODE_UPDATING_TAG_NAME = "sourceCodeUpdating";
    private static final String SHA1_KEY_TAG_NAME = "SHA1Key";
    private static final String BUILD_OPTION_TAG_NAME = "BuildOption";
//    private static final String SCRIPT_VERSION_TAG_NAME = "scriptVersion";
    private static final String SCRIPT_VERSION_VERSION_TAG_NAME = "version";
    private static final String SIMULATOR_VERSION_ANNOTATION_TAG_NAME = "version";
    private static final String SIMULATOR_CODE_LOCATION_TAG_NAME = "repository";
    private static final String SCRIPT_TAG_NAME = "script";
    private static final String SCRIPT_FILE_TAG_NAME = "file";
    private static final String SCRIPT_RAN_RUN_ATTRIBUTE_NAME = "ran";
    private static final String SCRIPT_RAN_AT_ATTRIBUTE_NAME = "atMillis";
//    private static final String SCRIPT_HOSTNAME_TAG_NAME = "hostname";
    private static final String SCRIPT_COMPLETED_AT_ATTRIBUTE_NAME = "completedAt";
    private static final String SCRIPT_ANALYZED_ATTRIBUTE_NAME = "outputAnalyzed";
    private static final String SIM_CONFIG_FILE_TAG_NAME = "simConfigFile";
    private static final String SIMULATION_CONFIGURATION_FILE_TAG_NAME
            = "simulationConfigurationFile";
    private static final String RESULT_FILE_NAME_ATTRIBUTE_NAME = "resultFileName";

    private Project project;
//    private Document doc;
//    private String name;
//    private Element root;
//    private Element provElement;
//    private Element scriptVersion;
//    private List<Element> inputs;
//    private Element simulator;
//    private Element simulationConfigurationFile;
//    private Element script;
//    private boolean provEnabled;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Construction">
    /**
     * Constructs a project including the XML document that constitutes the project, as well as
     * project members.
     *
     * @param rootNodeName  Name of the project. Name given to the root node.
     * @param load  True if the project should be loaded from disk, false otherwise
     * @throws ParserConfigurationException
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     */
    public ProjectManager(String rootNodeName, boolean load) throws ParserConfigurationException,
            IOException, SAXException {
        initState();
        project.setProjectName(rootNodeName);
        if (load) {
            project.load(project.getProjectFilename());
        }
    }

    private void initState() {
        project = new Project("None");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Persistence">
    /**
     * Writes the document representing this project to disk.
     *
     * @return The full path to the file that was persisted
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws java.io.IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    public String persist() throws TransformerConfigurationException, TransformerException,
            IOException, ParserConfigurationException {
        return project.persist();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ProjectMgr Configuration">
    /**
     * Provides the folder location for storing provenance data for a given project.
     *
     * @return The path to the provenance folder for the specified project
     * @throws IOException
     */
    public String getProvLocation() throws IOException {
        return Paths.get(project.getProjectLocation(), "provenance").toString();
    }

    /**
     * Provides the assumed folder location for a project of a given name.
     *
     * @return The path to the project folder for the specified project
     * @throws IOException
     */
    public static String getProjectLocation(String name) throws IOException {
        return Paths.get(FileManager.getProjectsDirectory(), "projects", name).toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    /**
     * Sets the project's name. This will also modify the name attribute for the project element of
     * the project XML model.
     *
     * @param name  The name given to the project
     */
    public void setName(String name) {
        project.setProjectName(name);
    }

    /**
     * Provides the name of the project.
     *
     * @return The name of the project
     */
    public String getName() {
        return project.getProjectName();
    }

    /**
     * Sets the value that used to determine if provenance support is enabled for this project. Also
     * sets the value of the related attribute for the project element in the project XML.
     *
     * @param enabled  Whether of not this project should record provenance
     */
    public void setProvenanceEnabled(boolean enabled) {
        ProjectData provData = new ProjectData(PROV_TAG_NAME);
        provData.addAttribute(PROV_ENABLED_ATTRIBUTE_NAME, String.valueOf(enabled));
        project.addProjectData(provData);
    }

    /**
     * Indicates if provenance support is enabled for this project.
     *
     * @return True if provenance support is enabled, otherwise false
     */
    public boolean isProvenanceEnabled() {
        boolean provEnabled = false;
        ProjectData provData = project.getProjectData(PROV_TAG_NAME);
        if (provData != null) {
            String provEnabledAttribute = provData.getAttribute(PROV_ENABLED_ATTRIBUTE_NAME);
            if (provEnabledAttribute != null) {
                provEnabled = Boolean.getBoolean(provEnabledAttribute);
            }
        }
        return provEnabled;
    }

    /**
     * Determines if prerequisite project data is available in order to generate a script.
     *
     * @return True if the prerequisite data is available for generating a script, false if not
     */
    public boolean scriptGenerationAvailable() {
        ProjectData scriptData = project.getProjectData(SCRIPT_TAG_NAME);
        ProjectData simulatorData = project.getProjectData(SIMULATOR_TAG_NAME);
        Datum simConfigData = null;
        if (scriptData != null) {
            simConfigData = scriptData.getDatum(SCRIPT_FILE_TAG_NAME);
        }
        return simulatorData != null && simConfigData != null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Data Manipulation">
    /**
     * Provides the current simulation specification based on the content of the elements in the
     * project XML document.
     *
     * Note: guaranteed to be successful... If the simSpec has yet to be added to the project, it is
     * automatically added here.
     *
     * @return A simulation specification as described by the text of related elements in the
     *         project
     */
    public SimulationSpecification getSimulationSpecification() {
        SimulationSpecification simSpec = new SimulationSpecification();
        ProjectData simData = project.getProjectData(SIMULATOR_TAG_NAME);

        String simType = getSimulationType(simData);
        String codeLocation = getSimulatorCodeLocation(simData);
        String locale = getSimulatorLocale(simData);
        String folder = getSimulatorFolderLocation(simData);
        String hostname = getSimulatorHostname(simData);
        String sha1 = getSHA1Key(simData);
        String buildOption = getBuildOption(simData);
        String updating = getSimulatorSourceCodeUpdatingType(simData);
        String version = getSimulatorVersionAnnotation(simData);
        String executable = null;
        if (simType != null && !simType.isEmpty()) {
            executable = SimulationSpecification.getSimFilename(simType);
        }
        simSpec.setSimulationType(simType);
        simSpec.setCodeLocation(codeLocation);
        simSpec.setSimulationLocale(locale);
        simSpec.setSimulatorFolder(folder);
        simSpec.setHostAddr(hostname);
        simSpec.setSHA1CheckoutKey(sha1);
        simSpec.setBuildOption(buildOption);
        simSpec.setSourceCodeUpdating(updating);
        simSpec.setVersionAnnotation(version);
        simSpec.setSimExecutable(executable);
        return simSpec;
    }

    /**
     * Provides the locale for the simulator. In other words the relationship between where a
     * simulation should take place and the machine running the workbench. Since the simulator
     * locale is set based on values from SimulationSpecification, the return value is indirectly
     * dependent upon the definitions provided by the SimulationSpecification class.
     *
     * @return The locale for simulations associated with this project
     * @see edu.uwb.braingrid.workbench.model.SimulationSpecification
     */
    private String getSimulatorLocale(ProjectData simData) {
        return getChildDataContent(simData, SIMULATOR_EXECUTION_MACHINE);
    }

    /**
     * Provides the version annotation for the simulation specification associated with this
     * project.
     *
     * @return The version annotation for the simulation
     */
    private String getSimulatorVersionAnnotation(ProjectData simData) {
        return getChildDataContent(simData, SIMULATOR_VERSION_ANNOTATION_TAG_NAME);
    }

    /**
     * Provides the repository location (local or otherwise) for the code for compiling the
     * simulator binaries.
     *
     * @return The central location (possibly a repository URL or URI) where the code resides for
     *         compiling the simulator binaries
     */
    private String getSimulatorCodeLocation(ProjectData simData) {
        return getChildDataContent(simData, SIMULATOR_CODE_LOCATION_TAG_NAME);
    }

    /**
     * Provides the folder location where the simulator code is moved to and the simulator is built
     * and executed.
     *
     * Note: This may be an absolute or canonical path on the local file system, or it may be a path
     * on a remote machine relative to the starting path of a remote connection.
     *
     * @return The folder location where the simulator code is moved to and the simulator is built
     *         and executed.
     */
    private String getSimulatorFolderLocation(ProjectData simData) {
        return getChildDataContent(simData, SIM_FOLDER_TAG_NAME);
    }

    /**
     * Provides the host name of the machine where a simulation will be run. This value is not
     * guaranteed to be set for all simulations. If the simulation is set to be run locally, this
     * function will return a null value.
     *
     * @return The host name of the machine where a simulation will be run, or null if the
     *         simulation is set to run locally.
     */
    private String getSimulatorHostname(ProjectData simData) {
        return getChildDataContent(simData, HOSTNAME_TAG_NAME);
    }

    private String getSHA1Key(ProjectData simData) {
        return getChildDataContent(simData, SHA1_KEY_TAG_NAME);
    }

    private String getBuildOption(ProjectData simData) {
        return getChildDataContent(simData, BUILD_OPTION_TAG_NAME);
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
    private String getSimulatorSourceCodeUpdatingType(ProjectData simData) {
        return getChildDataContent(simData, SIMULATOR_SOURCE_CODE_UPDATING_TAG_NAME);
    }

    /**
     * Provides the simulation type associated with the simulation for this project. The possible
     * values are indirectly determined by the Simulation Specification. In general, these values
     * indicate the processing model for the simulation (The threading or core model). This value
     * can be used to determine which executable to invoke in running the simulation.
     *
     * @return The simulation type associated with the simulation for this project
     * @see edu.uwb.braingrid.workbench.model.SimulationSpecification.SimulatorType
     */
    private String getSimulationType(ProjectData simData) {
        return getChildDataContent(simData, SIMULATION_TYPE_TAG_NAME);
    }

    private String getChildDataContent(ProjectData parentData, String tagName) {
        String content = null;
        if (parentData != null) {
            content = parentData.getDatum(tagName).getContent();
            if (content.isEmpty()) {
                content = null;
            }
        }
        return content;
    }

    private void setChildDataContent(ProjectData parentData, String tagName, String content) {
        if (parentData != null) {
            parentData.addDatum(tagName, content, null);
        }
    }

    /**
     * Note: This should not be used to determine if the script has completed execution.
     *
     * @return
     */
    public ScriptHistory getScriptHistory() {
        ScriptHistory scriptHistory = new ScriptHistory();
        ProjectData scriptData = project.getProjectData(SCRIPT_TAG_NAME);
        String startedAt = getScriptTimeStarted(scriptData);
        String completedAt = getScriptTimeCompleted(scriptData);
        boolean outputAnalyzed = wasScriptAnalyzed(scriptData);
        boolean ran = hasScriptRun(scriptData);
        String filename = getScriptFilename(scriptData);
        int version = getScriptVersion(scriptData);

        scriptHistory.setStartedAt(startedAt);
        scriptHistory.setCompletedAt(completedAt);
        scriptHistory.setOutputAnalyzed(outputAnalyzed);
        scriptHistory.setRan(ran);
        scriptHistory.setFilename(filename);
        scriptHistory.setVersion(version);
        return scriptHistory;
    }

    private String getScriptTimeStarted(ProjectData scriptData) {
        return scriptData.getAttribute(SCRIPT_RAN_AT_ATTRIBUTE_NAME);
    }

    private String getScriptTimeCompleted(ProjectData scriptData) {
        return scriptData.getAttribute(SCRIPT_COMPLETED_AT_ATTRIBUTE_NAME);
    }

    private boolean wasScriptAnalyzed(ProjectData scriptData) {
        return Boolean.parseBoolean(scriptData.getAttribute(SCRIPT_ANALYZED_ATTRIBUTE_NAME));
    }

    private boolean hasScriptRun(ProjectData scriptData) {
        return Boolean.parseBoolean(scriptData.getAttribute(SCRIPT_RAN_RUN_ATTRIBUTE_NAME));
    }

    private String getScriptFilename(ProjectData scriptData) {
        return getChildDataContent(scriptData, SCRIPT_FILE_TAG_NAME);
    }

    private int getScriptVersion(ProjectData scriptData) {
        int version;
        try {
            version = Integer.parseInt(getChildDataContent(scriptData,
                    SCRIPT_VERSION_VERSION_TAG_NAME));
        } catch (NumberFormatException e) {
            version = DEFAULT_SCRIPT_VERSION;
        }
        return version;
    }

    /**
     * Provides the version of the script currently associated with the project. This value can be
     * used to determine the base name of the script file name.
     *
     * Note: This should not be used to determine if the script has completed execution
     *
     * @return The version of the script currently associated with the project
     */
    public String getScriptVersion() {
        return String.valueOf(getScriptVersion(project.getProjectData(SCRIPT_TAG_NAME)));
    }

    /**
     * Sets the text content of the script version for the project. This value is only changed in
     * the XML document for the project.
     *
     * @param version  The version number of the current script for the project
     */
    public boolean setScriptVersion(String version) {
        boolean success = true;
        try {
            int versionNumber = Integer.getInteger(version);
            if (versionNumber >= 0) {
                setChildDataContent(project.getProjectData(SCRIPT_TAG_NAME),
                        SCRIPT_VERSION_VERSION_TAG_NAME, version);
            } else {
                success = false;
            }
        } catch (NumberFormatException e) {
            success = false;
        }
        return success;
    }

    /**
     * Sets the value for the attribute used to determine whether the script has run or not.
     *
     * Note: guaranteed to be successful... If the script has yet to be added to the project, it is
     * automatically added here.
     *
     * @param hasRun  Whether or not the script has been executed
     */
    public void setScriptRan(boolean hasRun) {
        setChildDataContent(project.getProjectData(SCRIPT_TAG_NAME), SCRIPT_RAN_RUN_ATTRIBUTE_NAME,
                String.valueOf(hasRun));
    }

    /**
     * Determines whether or not the script has been executed.
     *
     * Note: If the script has yet to be added to the project, it is automatically added here.
     *
     * Note: This should not be used to determine if the script has completed execution.
     *
     * @return True if the script has been executed, otherwise false
     */
    public boolean getScriptRan() {
        ProjectData script = project.getProjectData(SCRIPT_TAG_NAME);
        return Boolean.parseBoolean(script.getAttribute(SCRIPT_RAN_RUN_ATTRIBUTE_NAME));
    }

    /**
     *
     * Note: guaranteed to be successful... If the script has yet to be added to the project, it is
     * automatically added here. Sets the attribute used to determine whether or not the script has
     * been executed to "true".
     */
    public void setScriptRanAt() {
        ProjectData script = project.getProjectData(SCRIPT_TAG_NAME);
        script.addAttribute(SCRIPT_RAN_AT_ATTRIBUTE_NAME, String.valueOf(new Date().getTime()));
    }

    /**
     * Sets the attribute used to determine when the script completed execution.
     *
     * Note: guaranteed to be successful... If the script has yet to be added to the project, it is
     * automatically added here.
     *
     * Note: This should be verified through the OutputAnalyzer class first.
     *
     * @param timeCompleted  The number of milliseconds since January 1, 1970, 00:00:00 GMT when
     *                       execution completed for the script associated with this project
     */
    public void setScriptCompletedAt(long timeCompleted) {
        ProjectData script = project.getProjectData(SCRIPT_TAG_NAME);
        script.addAttribute(SCRIPT_COMPLETED_AT_ATTRIBUTE_NAME, String.valueOf(timeCompleted));
    }

    /**
     * Provides the number of milliseconds since January 1, 1970, 00:00:00 GMT when the execution
     * started for the script associated with this project.
     *
     * Note: guaranteed to be successful... If the script has yet to be added to the project, it is
     * automatically added here.
     *
     * @return The number of milliseconds since January 1, 1970, 00:00:00 GMT when the execution
     *         started for the script associated with this project
     */
    public long getScriptRanAt() {
        String millisText;
        long millis = DateTime.ERROR_TIME;
        ProjectData script = project.getProjectData(SCRIPT_TAG_NAME);
        millisText = script.getAttribute(SCRIPT_RAN_AT_ATTRIBUTE_NAME);
        if (millisText != null) {
            try {
                millis = Long.parseLong(millisText);
            } catch (NumberFormatException e) {
            }
        }
        return millis;
    }

    /**
     * Provides the number of milliseconds since January 1, 1970, 00:00:00 GMT when execution
     * completed for the script associated with this project.
     *
     * Note: guaranteed to be successful... If the script has yet to be added to the project, it is
     * automatically added here.
     *
     * @return The number of milliseconds since January 1, 1970, 00:00:00 GMT when execution
     *         completed for the script associated with this project
     */
    public long getScriptCompletedAt() {
        String millisText;
        long timeCompleted = DateTime.ERROR_TIME;
        ProjectData script = project.getProjectData(SCRIPT_TAG_NAME);
        millisText = script.getAttribute(SCRIPT_COMPLETED_AT_ATTRIBUTE_NAME);
        if (millisText != null) {
            try {
                timeCompleted = Long.parseLong(millisText);
            } catch (NumberFormatException e) {
            }
        }
        return timeCompleted;
    }

    /**
     * Gets the location of the script file.
     *
     * @return The location of the script file, or null if no file path is set
     */
    public String getScriptCanonicalFilePath() {
        String path = null;
        ProjectData script = project.getProjectData(SCRIPT_TAG_NAME);
        Datum fileDatum = script.getDatum(SCRIPT_FILE_TAG_NAME);

        if (fileDatum != null) {
            path = fileDatum.getContent();
            if (path.isEmpty()) {
                path = null;
            }
        }
        return path;
    }

    public void removeScript() {
        project.remove(SCRIPT_TAG_NAME);
    }

    /**
     * Specifies the script for this project.
     *
     * @param scriptSpec  Model containing specification data
     */
    public void addSimulator(SimulationSpecification scriptSpec) {
        ProjectData parentData = project.getProjectData(SIMULATOR_TAG_NAME);
        setChildDataContent(parentData, SIMULATOR_EXECUTION_MACHINE,
                scriptSpec.getSimulationLocale());
        setChildDataContent(parentData, SIMULATOR_VERSION_ANNOTATION_TAG_NAME,
                scriptSpec.getVersionAnnotation());
        setChildDataContent(parentData, SIMULATOR_CODE_LOCATION_TAG_NAME,
                scriptSpec.getCodeLocation());
        setChildDataContent(parentData, HOSTNAME_TAG_NAME, scriptSpec.getHostAddr());
        setChildDataContent(parentData, SIM_FOLDER_TAG_NAME, scriptSpec.getSimulatorFolder());
        setChildDataContent(parentData, SIMULATION_TYPE_TAG_NAME, scriptSpec.getSimulationType());
        setChildDataContent(parentData, SIMULATOR_SOURCE_CODE_UPDATING_TAG_NAME,
                scriptSpec.getSourceCodeUpdating());
        setChildDataContent(parentData, SHA1_KEY_TAG_NAME, scriptSpec.getSHA1CheckoutKey());
        setChildDataContent(parentData, BUILD_OPTION_TAG_NAME, scriptSpec.getBuildOption());
    }

    /**
     * Specifies the location of the simulation configuration file.
     *
     * @param filename  Name of file on the local workbench system
     */
    public void addSimConfigFile(String filename) {
        setChildDataContent(project.getProjectData(SIM_CONFIG_FILE_TAG_NAME),
                SIMULATION_CONFIGURATION_FILE_TAG_NAME, filename);
    }

//    /**
//     * Adds a generated script file to the project
//     *
//     * @param scriptBasename - The base-name of the file path
//     * @param extension - The extension of the file path
//     * @return True if the script was added to the model correctly, false if not
//     */
//    public boolean addScript(String scriptBasename, String extension) {
//        boolean success = true;
//        try {
//            /* Remove Previous Script */
//            removeScript();
//
//            /* Create Elements */
//            // this will overwrite previously defined script
//            script = doc.createElement(SCRIPT_TAG_NAME);
//            // file element of the script element
//            Element scriptFile = doc.createElement(SCRIPT_FILE_TAG_NAME);
//
//            /* Add Values */
//            // text element describing the file location
//            Text scriptFileLocation = doc.createTextNode(scriptBasename + "." + extension);
//
//            /* Attach Elements */
//            scriptFile.appendChild(scriptFileLocation);
//            script.appendChild(scriptFile);
//            root.appendChild(script);
//
//            int version = 0;
//            try {
//                version = Integer.valueOf(getScriptVersion());
//            } catch (NumberFormatException e) { // version not present
//                initScriptVersion();
//            }
//            version++;
//            setScriptVersion(String.valueOf(version));
//        } catch (DOMException | NumberFormatException e) {
//            script = null;
//            success = false;
//        }
//        return success;
//    }
//
//    public String getSimConfigFilename() {
//        return getFirstChildTextContent(root, SIM_CONFIG_FILE_TAG_NAME);
//    }
//
//    public boolean scriptGenerated() {
//        return script != null;
//    }
//
//    public boolean scriptOutputAnalyzed() {
//        String analyzedAttributeValue;
//        boolean analyzed = false;
//        if (script != null) {
//            analyzedAttributeValue = script.getAttribute(SCRIPT_ANALYZED_ATTRIBUTE_NAME);
//            analyzed = Boolean.valueOf(analyzedAttributeValue);
//        }
//        return analyzed;
//    }
//
//    public void setScriptAnalyzed(boolean analyzed) {
//        if (script != null) {
//            script.setAttribute(SCRIPT_ANALYZED_ATTRIBUTE_NAME, String.valueOf(analyzed));
//        }
//    }
//
//    public void setSimResultFile(String resultFileName) {
//        if (simulationConfigurationFile != null) {
//            simulationConfigurationFile.setAttribute(RESULT_FILE_NAME_ATTRIBUTE_NAME,
//                    resultFileName);
//        }
//    }
//
//    public String getSimResultFile() {
//        String filename = null;
//        if (simulationConfigurationFile != null) {
//            filename = simulationConfigurationFile.getAttribute(RESULT_FILE_NAME_ATTRIBUTE_NAME);
//        }
//        return filename;
//    }*/
    // </editor-fold>
}
