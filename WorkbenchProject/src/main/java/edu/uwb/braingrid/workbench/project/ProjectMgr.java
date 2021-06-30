package edu.uwb.braingrid.workbench.project;
// CLEANED (but soon to be dead... recommended that the class be maintained
//          during testing of its replacement)

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbench.model.SimulationSpecification;
import edu.uwb.braingrid.workbench.utils.DateTime;

/**
 * Manages a Brain Grid the project specified within the Brain Grid Workbench.
 *
 * <p>Note: Provenance support is dealt with after the project manager is constructed</p>
 *
 * @author Del Davis, Extended by Joseph Conquest
 * @version 1.2
 */
public class ProjectMgr {

    // <editor-fold defaultstate="collapsed" desc="Members">
    private static final Logger LOG = Logger.getLogger(ProjectMgr.class.getName());

    private static final String PROJECT_TAG_NAME = "project";
    private static final String PROJECT_NAME_ATTRIBUTE = "name";
    private static final String PROV_TAG_NAME = "provenance";
    private static final String PROV_LOCATION_TAG_NAME = "location";
    private static final String PROV_ENABLED_ATTRIBUTE_NAME = "enabled";
    private static final String INPUT_TAG_NAME = "input";
    private static final String INPUT_TYPE_TAG_NAME = "type";
    private static final String INPUT_URI_TAG_NAME = "uri";
    private static final String SIMULATOR_TAG_NAME = "simulator";
    private static final String SIMULATOR_EXECUTION_MACHINE = "executionMachine";
    private static final String HOSTNAME_TAG_NAME = "hostname";
    private static final String SIM_FOLDER_TAG_NAME = "simulatorFolder";
    private static final String SIMULATION_TYPE_TAG_NAME = "ProcessingType";
    private static final String SIMULATOR_SOURCE_CODE_UPDATING_TAG_NAME = "sourceCodeUpdating";
    private static final String SHA1_KEY_TAG_NAME = "SHA1Key";
    private static final String BUILD_OPTION_TAG_NAME = "BuildOption";
    private static final String SCRIPT_VERSION_TAG_NAME = "scriptVersion";
    private static final String SCRIPT_VERSION_VERSION_TAG_NAME = "version";
    private static final String SIMULATOR_VERSION_ANNOTATION_TAG_NAME = "version";
    private static final String SIMULATOR_CODE_LOCATION_TAG_NAME = "repository";
    private static final String SCRIPT_TAG_NAME = "script";
    private static final String SCRIPT_FILE_TAG_NAME = "file";
    private static final String SCRIPT_RAN_RUN_ATTRIBUTE_NAME = "ran";
    private static final String SCRIPT_RAN_AT_ATTRIBUTE_NAME = "atMillis";
    private static final String SCRIPT_HOSTNAME_TAG_NAME = "hostname";
    private static final String SCRIPT_COMPLETED_AT_ATTRIBUTE_NAME = "completedAt";
    private static final String SCRIPT_ANALYZED_ATTRIBUTE_NAME = "outputAnalyzed";
    private static final String SCRIPT_CONFIG_FILE_TAG_NAME = "simConfigFile";
    private static final String SIMULATION_CONFIGURATION_FILE_ATTRIBUTE_NAME
            = "simulationConfigurationFile";

    public static final String REMOTE_EXECUTION = "Remote";
    public static final String LOCAL_EXECUTION = "Local";

    private Document doc;
    private String name;
    private Element root;
    private Element provElement;
    private Element scriptVersion;
    private List<Element> inputs;
    private Element simulator;
    private Element simulationConfigurationFile;
    private Element script;
    private boolean provEnabled;
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
    public ProjectMgr(String rootNodeName, boolean load) throws ParserConfigurationException,
            IOException, SAXException, NullPointerException {
        initState();
        name = rootNodeName.split("\\.")[0];
        LOG.info("New Project Manager for project: " + name);
        if (load) {
            load(determineProjectOutputLocation() + name + ".xml");
        } else {
            initXML(rootNodeName);
        }
    }

    private void initState() {
        name = "None";
        inputs = new ArrayList<>();
        simulator = null;
        simulationConfigurationFile = null;
        script = null;
        doc = null;
        root = null;
        provElement = null;
        provEnabled = false;
    }

    private void initXML(String rootNodeName) throws ParserConfigurationException, IOException {
        /* Build New XML Document */
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        /* Build Root Node */
        root = doc.createElement(PROJECT_TAG_NAME);
        doc.appendChild(root);
        // record the project name as an attribute of the root element
        root.setAttribute(PROJECT_NAME_ATTRIBUTE, rootNodeName);

        /* Build Provenance Node */
        provElement = doc.createElement(PROV_TAG_NAME);
        root.appendChild(provElement);
        // set enabled attribute to default: false on construction
        provElement.setAttribute(PROV_ENABLED_ATTRIBUTE_NAME, "");
        // set location of provenance file for this project
        Element provLocationElem = doc.createElement(PROV_LOCATION_TAG_NAME);
        Text locationText = doc.createTextNode(determineProvOutputLocation());
        provLocationElem.appendChild(locationText);
        provElement.appendChild(provLocationElem);
        initScriptVersion();
    }

    private void initScriptVersion() {
        scriptVersion = doc.createElement(SCRIPT_VERSION_TAG_NAME);
        Element version = doc.createElement(SCRIPT_VERSION_VERSION_TAG_NAME);
        Text versionText = doc.createTextNode("0");
        version.appendChild(versionText);
        scriptVersion.appendChild(version);
        root.appendChild(scriptVersion);
    }

    /**
     * Loads a project XML from disk.
     *
     * @param filename  The name of the file to load
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public final void load(String filename) throws ParserConfigurationException, SAXException,
            IOException {
        /* Load the Document */
        File file = new File(filename);

        // ParserConfigurationException and SAXException possibly thrown here
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        doc.getDocumentElement().normalize();

        /* Load Members */
        // load root element
        root = doc.getDocumentElement();
        // load project name from name attribute of root element
        name = root.getAttribute(PROJECT_NAME_ATTRIBUTE);

        // load prov element
        provElement = getElementFromDom(PROV_TAG_NAME);
        // determine if provenance is enabled
        String enabledValue = provElement.getAttribute(PROV_ENABLED_ATTRIBUTE_NAME);
        if (enabledValue == null) {
            provEnabled = false;
        } else {
            provEnabled = Boolean.parseBoolean(enabledValue);
        }

        // load project inputs
        inputs = new ArrayList<>();
        NodeList inputList = root.getElementsByTagName(INPUT_TAG_NAME);
        for (int i = 0, im = inputList.getLength(); i < im; i++) {
            inputs.add((Element) inputList.item(i));
        }

        simulationConfigurationFile = getElementFromDom(SCRIPT_CONFIG_FILE_TAG_NAME);

        // load simulator data
        simulator = getElementFromDom(SIMULATOR_TAG_NAME);

        // load specified script
        script = getElementFromDom(SCRIPT_TAG_NAME);

        // load script version element
        scriptVersion = getElementFromDom(SCRIPT_VERSION_TAG_NAME);
    }

    private Element getElementFromDom(String tagName) {
        Element elem = null;
        if (root != null) {
            NodeList nl = root.getElementsByTagName(tagName);
            if (nl.getLength() != 0) {
                elem = (Element) nl.item(0);
            }
        }
        return elem;
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
     */
    public String persist() throws TransformerConfigurationException, TransformerException,
            IOException {
        // calculate the full path to the project file
        String projectFilename = getProjectFilename();

        // create any necessary non-existent directories
        new File(determineProjectOutputLocation()).mkdirs();

        // create the file we want to save
        File projectFile = new File(projectFilename);

        // write the content into xml file
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.transform(new DOMSource(doc), new StreamResult(projectFile));

        return projectFilename;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ProjectMgr Configuration">
    /**
     * Determines the folder location for storing provenance data for a given project.
     *
     * @return The path to the provenance folder for the specified project
     * @throws IOException
     */
    public String determineProvOutputLocation() throws IOException {
        String projectDirectory = determineProjectOutputLocation();
        String provOutputLocation = projectDirectory + "provenance"
                + FileManager.getFileManager().getFolderDelimiter();
        return provOutputLocation;
    }

    /**
     * Determines the folder location for storing provenance data for in relation to all other
     * projects.
     *
     * @return The path to the provenance folder for the specified project
     * @throws IOException
     */
    public String determineUniversalProvOutputLocation() throws IOException {
        String workingDirectory = FileManager.getCanonicalWorkingDirectory();
        String ps = FileManager.getFileManager().getFolderDelimiter();
        String provOutputLocation = workingDirectory + ps + "projects" + ps;
        return provOutputLocation;
    }

    /**
     * Determines the folder location for a project based on the currently loaded configuration.
     *
     * @return The path to the project folder for the specified project
     * @throws IOException
     */
    public String determineProjectOutputLocation() throws IOException {
        return ProjectMgr.determineProjectOutputLocation(this.getName());
    }

    /**
     * Determines the assumed folder location for a project of a given name.
     *
     * @return The path to the project folder for the specified project
     * @throws IOException
     */
    public static String determineProjectOutputLocation(String name) throws IOException {
        String workingDirectory = FileManager.getCanonicalWorkingDirectory();
        String ps = FileManager.getFileManager().getFolderDelimiter();
        String projectDirectory = workingDirectory + ps + "projects" + ps + name + ps;
        return projectDirectory;
    }

    /**
     * Provides the full path, including the filename, containing the XML for this project.
     *
     * @return The full path, including the filename, for the file containing the XML for this
     *         project
     * @throws IOException
     */
    public String getProjectFilename() throws IOException {
        if (name == null) {
            throw new IOException();
        }
        return determineProjectOutputLocation() + this.getName() + ".xml";
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    /**
     * Sets the project's name. This will also modify the name attribute for the project element of
     * the project XML model.
     *
     * Do not give a null string. For now, I don't have time to fix it right now. -Max
     * TODO: Refactor project and allow this to throw a nullpointer exception
     *
     * @param name The name given to the project
     */
    public void setName(String name) {
        this.name = name;
        root.setAttribute(PROJECT_NAME_ATTRIBUTE, name);
    }

    /**
     * Provides the name of the project.
     *
     * @return The name of the project
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value that used to determine if provenance support is enabled for this project. Also
     * sets the value of the related attribute for the project element in the project XML.
     *
     * @param enabled  Whether of not this project should record provenance
     */
    public void setProvenanceEnabled(boolean enabled) {
        provEnabled = enabled;
        provElement.setAttribute("enabled", String.valueOf(provEnabled));
    }

    /**
     * Indicates if provenance support is enabled for this project.
     *
     * @return True if provenance support is enabled, otherwise false
     */
    public boolean isProvenanceEnabled() {
        return provEnabled;
    }

    /**
     * Determines if prerequisite project data is available in order to generate a script.
     *
     * @return True if the prerequisite data is available for generating a script, false if not
     */
    public boolean scriptGenerationAvailable() {
        return script == null && simulationConfigurationFile != null && simulator != null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Data Manipulation">
    /**
     * Provides the current simulation specification based on the content of the elements in the
     * project XML document.
     *
     * @return A simulation specification as described by the text of related elements in the
     *         project
     */
    public SimulationSpecification getSimulationSpecification() {
        SimulationSpecification simSpec;
        simSpec = new SimulationSpecification();
        String simType = getSimulationType();
        String codeLocation = getSimulatorCodeLocation();
        String locale = getSimulatorLocale();
        String folder = getSimulatorFolderLocation();
        String hostname = getSimulatorHostname();
        String sha1 = getSHA1Key();
        String buildOption = getBuildOption();
        String updating = getSimulatorSourceCodeUpdatingType();
        String version = getSimulatorVersionAnnotation();
        String executable = null;
        if (simType != null && !simType.isEmpty()) {
            executable = SimulationSpecification.getSimFilename(simType);
        }
        simSpec.setSimulationType(simType);
        simSpec.setCodeLocation(codeLocation);
        simSpec.setSimulatorLocale(locale);
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
    public String getSimulatorLocale() {
        return getFirstChildTextContent(simulator, SIMULATOR_EXECUTION_MACHINE);
    }

    /**
     * Provides the version annotation for the simulation specification associated with this
     * project.
     *
     * @return The version annotation for the simulation
     */
    public String getSimulatorVersionAnnotation() {
        return getFirstChildTextContent(simulator, SIMULATOR_VERSION_ANNOTATION_TAG_NAME);
    }

    /**
     * Provides the repository location (local or otherwise) for the code for compiling the
     * simulator binaries.
     *
     * @return The central location (possibly a repository URL or URI) where the code resides for
     *         compiling the simulator binaries
     */
    public String getSimulatorCodeLocation() {
        return getFirstChildTextContent(simulator, SIMULATOR_CODE_LOCATION_TAG_NAME);
    }

    /**
     * Provides the folder location where the simulator code is moved to and the simulator is built
     * and executed.
     * <p>
     * Note: This may be an absolute or canonical path on the local file system, or it may be a path
     * on a remote machine relative to the starting path of a remote connection.
     *
     * @return The folder location where the simulator code is moved to and the simulator is built
     *         and executed.
     */
    public String getSimulatorFolderLocation() {
        return getFirstChildTextContent(simulator, SIM_FOLDER_TAG_NAME);
    }

    /**
     * Provides the host name of the machine where a simulation will be run. This value is not
     * guaranteed to be set for all simulations. If the simulation is set to be run locally, this
     * function will return a null value.
     *
     * @return The host name of the machine where a simulation will be run, or null if the
     *         simulation is set to run locally.
     */
    public String getSimulatorHostname() {
        return getFirstChildTextContent(simulator, HOSTNAME_TAG_NAME);
    }

    public String getSHA1Key() {
        return getFirstChildTextContent(simulator, SHA1_KEY_TAG_NAME);
    }

    public String getBuildOption() {
        return getFirstChildTextContent(simulator, BUILD_OPTION_TAG_NAME);
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
        return getFirstChildTextContent(simulator, SIMULATOR_SOURCE_CODE_UPDATING_TAG_NAME);
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
    public String getSimulationType() {
        return getFirstChildTextContent(simulator, SIMULATION_TYPE_TAG_NAME);
    }

    /**
     * Provides the version of the script currently associated with the project. This value can be
     * used to determine the base name of the script file name.
     *
     * @return The version of the script currently associated with the project
     */
    public String getScriptVersion() {
        return getFirstChildTextContent(scriptVersion, SCRIPT_VERSION_VERSION_TAG_NAME);
    }

    /**
     * Provides the version number of the next script that will be added to the project. This is a
     * convenience function, the version number is determined based on the current script version.
     *
     * @return The version number of the next script that will be added to the project when another
     *         script is generated.
     */
    public String getNextScriptVersion() {
        int scriptVerNum;
        try {
            scriptVerNum = Integer.parseInt(getScriptVersion());
            scriptVerNum++;
        } catch (NumberFormatException e) {
            scriptVerNum = 0;
        }
        return String.valueOf(scriptVerNum);
    }

    /**
     * Sets the text content of the script version for the project. This value is only changed in
     * the XML document for the project.
     *
     * @param version  The version number of the current script for the project
     */
    public void setScriptVersion(String version) {
        setFirstChildTextContent(scriptVersion, SCRIPT_VERSION_VERSION_TAG_NAME, version);
    }

    /**
     * Sets the value for the attribute used to determine whether the script has run or not.
     *
     * @param hasRun  Whether or not the script has been executed
     */
    public void setScriptRan(boolean hasRun) {
        if (script != null) {
            script.setAttribute(SCRIPT_RAN_RUN_ATTRIBUTE_NAME, String.valueOf(hasRun));
        }
    }

    /**
     * Determines whether or not the script has been executed.
     * <p>
     * Note: This should not be used to determine if the script has completed execution
     *
     * @return True if the script has been executed, otherwise false
     */
    public boolean getScriptRan() {
        String ranAttributeValue;
        boolean ran = false;
        if (script != null) {
            ranAttributeValue = script.getAttribute(SCRIPT_RAN_RUN_ATTRIBUTE_NAME);
            ran = Boolean.parseBoolean(ranAttributeValue);
        }
        return ran;
    }

    /**
     * Sets the attribute used to determine whether or not the script has been executed to "true".
     */
    public void setScriptRanAt() {
        script.setAttribute(SCRIPT_RAN_AT_ATTRIBUTE_NAME, String.valueOf(new Date().getTime()));
    }

    /**
     * Sets the attribute used to determine when the script completed execution.
     * <p>
     * Note: This should be verified through the OutputAnalyzer class first.
     *
     * @param timeCompleted  The number of milliseconds since January 1, 1970, 00:00:00 GMT when
     *                       execution completed for the script associated with this project
     */
    public void setScriptCompletedAt(long timeCompleted) {
        if (script != null) {
            script.setAttribute(SCRIPT_COMPLETED_AT_ATTRIBUTE_NAME, String.valueOf(timeCompleted));
        }
    }

    /**
     * Provides the number of milliseconds since January 1, 1970, 00:00:00 GMT when the execution
     * started for the script associated with this project.
     *
     * @return The number of milliseconds since January 1, 1970, 00:00:00 GMT when the execution
     *         started for the script associated with this project
     */
    public long getScriptRanAt() {
        String millisText;
        long millis = DateTime.ERROR_TIME;
        if (script != null) {
            millisText = script.getAttribute(SCRIPT_RAN_AT_ATTRIBUTE_NAME);
            if (!millisText.isEmpty()) {
                try {
                    millis = Long.parseLong(millisText);
                } catch (NumberFormatException e) {
                }
            }
        }
        return millis;
    }

    /**
     * Provides the number of milliseconds since January 1, 1970, 00:00:00 GMT when execution
     * completed for the script associated with this project.
     *
     * @return The number of milliseconds since January 1, 1970, 00:00:00 GMT when execution
     *         completed for the script associated with this project
     */
    public long getScriptCompletedAt() {
        long timeCompleted = DateTime.ERROR_TIME;
        String millisText;
        if (script != null) {
            millisText = script.getAttribute(SCRIPT_COMPLETED_AT_ATTRIBUTE_NAME);
            if (!millisText.isEmpty()) {
                try {
                    timeCompleted = Long.parseLong(millisText);
                } catch (NumberFormatException e) {
                }
            }
        }
        return timeCompleted;
    }

    /**
     * Provides the host name of the machine where the script associated with this project executed.
     * This should not be confused with getSimulatorHostname, which may point to a location
     * respecified after the script was executed.
     *
     * @return The host name of the machine where the script executed or null if the host name of
     *         the script has not been recorded
     */
    public String getScriptHostname() {
        return getFirstChildTextContent(script, SCRIPT_HOSTNAME_TAG_NAME);
    }

    /**
     * Sets the text content related to the host name where the script associated with this project
     * executed.
     *
     * @param hostname  The host name where the script executed
     * @return True if the operation succeeded. False if the script element has not been initialized
     *         as part of the project XML document
     */
    public boolean setScriptHostname(String hostname) {
        boolean success = true;
            if (!setFirstChildTextContent(script, SCRIPT_HOSTNAME_TAG_NAME, hostname)) {
                if (!createChildWithTextContent(script, SCRIPT_HOSTNAME_TAG_NAME, hostname)) {
                    success = false;
                }
            } else {
                success = false;
            }
        return success;
    }

    public boolean setSHA1Key(String sha1) {
        boolean success = true;
        if (simulator != null) {
            if (!setFirstChildTextContent(simulator, SHA1_KEY_TAG_NAME,
                    sha1)) {
                if (!createChildWithTextContent(simulator, SHA1_KEY_TAG_NAME,
                        sha1)) {
                    success = false;
                }
            } else {
                success = false;
            }
        }
        return success && simulator != null;
    }

    public boolean setBuildOption(String buildOption) {
        boolean success = true;
        if (simulator != null) {
            if (!setFirstChildTextContent(simulator, BUILD_OPTION_TAG_NAME,
                    buildOption)) {
                if (!createChildWithTextContent(simulator, BUILD_OPTION_TAG_NAME,
                        buildOption)) {
                    success = false;
                }
            } else {
                success = false;
            }
        }
        return success && simulator != null;
    }

    private boolean createChildWithTextContent(Element parent, String childTagName,
            String textContent) {
        boolean success = true;
        if (parent != null) {
            try {
                Element child = doc.createElement(childTagName);
                Text childText = doc.createTextNode(textContent);
                parent.appendChild(child.appendChild(childText));
            } catch (DOMException e) {
                success = false;
            }
        } else {
            success = false;
        }
        return success;
    }

    private boolean setFirstChildTextContent(Element parent, String childTagName,
            String textContent) {
        boolean success = true;
        if (parent != null) {
            NodeList nl = parent.getElementsByTagName(childTagName);
            if (nl.getLength() > 0) {
                Text text = (Text) nl.item(0).getFirstChild();
                text.setTextContent(textContent);
            } else {
                success = false;
            }
        } else {
            success = false;
        }
        return success;
    }

    private String getFirstChildTextContent(Element parent, String textElementTagName) {
        String textContent = null;
        if (parent != null) {
            NodeList nl = parent.getElementsByTagName(textElementTagName);
            if (nl.getLength() > 0) {
                Text t = (Text) nl.item(0).getFirstChild();
                if (t != null) {
                    textContent = t.getTextContent();
                }
            }
        }
        return textContent;
    }

    /**
     * Gets the location of the script file.
     *
     * @return The location of the script file, or null if no file path is set
     */
    public String getScriptCanonicalFilePath() {
        String path = null;
        if (script != null) {
            NodeList nl = script.getElementsByTagName(SCRIPT_FILE_TAG_NAME);
            if (nl.getLength() > 0) {
                Text scriptPathText = (Text) nl.item(0).getFirstChild();
                if (scriptPathText != null) {
                    path = scriptPathText.getTextContent();
                }
                if (path != null && (path.equals("null") || path.equals(""))) {
                    path = null;
                }
            }
        }
        return path;
    }

    private void removeInput(Element input) {
        if (input != null) {
            inputs.remove(input);
            input.getParentNode().removeChild(input);
        }
    }

    private void removeSimulator() {
        if (null != simulator) {
            simulator.getParentNode().removeChild(simulator);
            simulator = null;
        }
    }

    private void removeSimulationConfigurationFile() {
        if (null != simulationConfigurationFile) {
            simulationConfigurationFile.getParentNode().removeChild(simulationConfigurationFile);
            simulationConfigurationFile = null;
        }
    }

    /**
     * Removes the currently specified script from the project.
     */
    public void removeScript() {
        if (script != null) {
            script.getParentNode().removeChild(script);
            script = null;
        }
    }

    /**
     * Add a specification for the simulator used in this project to the model. The elements of a
     * specification are used to determine the content of an execution script as well as where it
     * will be executed.
     *
     * @param simulatorExecutionLocation  Indicates where the simulator will be executed (e.g.
     *                                    Remote, or Local)
     * @param hostname  The name of the remote host, if the simulator will be executed remotely
     * @param simFolder  The top folder where the script will be deployed to. This also serves as
     *                   the parent folder of the local copy of the simulator source code.
     * @param simulationType  Indicates which version of the simulator will be executed (e.g.
     *                        growth, or growth_CUDA)
     * @param versionAnnotation  A human interpretable note regarding the version of the simulator
     *                           that will be executed
     * @param codeLocation  The location of the repository that contains the code for the simulator
     * @param sourceCodeUpdating  Whether the source code should be updated prior to execution (e.g.
     *                            Pull, or None). If sourceCodeUpdating is set to first do a pull on
     *                            the repository, a clone will be attempted first in case the
     *                            repository has yet to be cloned.
     * @return True if the simulator was added to the model correctly, false if not
     */
    public boolean addSimulator(String simulatorExecutionLocation, String hostname,
            String simFolder, String simulationType, String codeLocation, String versionAnnotation,
            String sourceCodeUpdating, String sha1Key, String buildOption) {
        boolean success = true;
        // remove previously defined simulator
        removeSimulator();
        try {
            /* Create Elements */
            simulator = doc.createElement(SIMULATOR_TAG_NAME);
            Element simExecLocation = doc.createElement(SIMULATOR_EXECUTION_MACHINE);
            Element versionAnnotationElem = doc.createElement(
                    SIMULATOR_VERSION_ANNOTATION_TAG_NAME);
            Element codeLocationElem = doc.createElement(SIMULATOR_CODE_LOCATION_TAG_NAME);
            Element hostnameElem = doc.createElement(HOSTNAME_TAG_NAME);
            Element simFolderElem = doc.createElement(SIM_FOLDER_TAG_NAME);
            Element simulationTypeElem = doc.createElement(SIMULATION_TYPE_TAG_NAME);
            Element sourceCodeUpdatingElem = doc.createElement(
                    SIMULATOR_SOURCE_CODE_UPDATING_TAG_NAME);
            Element sha1KeyElem = doc.createElement(SHA1_KEY_TAG_NAME);
            Element buildOptionElem = doc.createElement(BUILD_OPTION_TAG_NAME);

            /* Add Values */
            // create text nodes to add to created elements
            Text simulatorExecutionLocationText = doc.createTextNode(simulatorExecutionLocation);
            Text versionAnnotationText = doc.createTextNode(versionAnnotation);
            Text codeLocationText = doc.createTextNode(codeLocation);
            Text hostnameText = doc.createTextNode(hostname);
            Text simFolderText = doc.createTextNode(simFolder);
            Text simulationTypeText = doc.createTextNode(simulationType);
            Text sourceCodeUpdatingText = doc.createTextNode(sourceCodeUpdating);
            Text sha1KeyText = doc.createTextNode(sha1Key);
            Text buildOptionText = doc.createTextNode(buildOption);

            // attach the text to respective elements
            simExecLocation.appendChild(simulatorExecutionLocationText);
            versionAnnotationElem.appendChild(versionAnnotationText);
            codeLocationElem.appendChild(codeLocationText);
            hostnameElem.appendChild(hostnameText);
            simFolderElem.appendChild(simFolderText);
            simulationTypeElem.appendChild(simulationTypeText);
            sourceCodeUpdatingElem.appendChild(sourceCodeUpdatingText);
            sha1KeyElem.appendChild(sha1KeyText);
            buildOptionElem.appendChild(buildOptionText);

            /* Attach Elements */
            // attach the parameter elements to the input element
            simulator.appendChild(simExecLocation);
            simulator.appendChild(versionAnnotationElem);
            simulator.appendChild(codeLocationElem);
            simulator.appendChild(hostnameElem);
            simulator.appendChild(simFolderElem);
            simulator.appendChild(simulationTypeElem);
            simulator.appendChild(sourceCodeUpdatingElem);
            simulator.appendChild(sha1KeyElem);
            simulator.appendChild(buildOptionElem);
            // attach the input element to the project element
            root.appendChild(simulator);
        } catch (DOMException e) {
            // Not 100% sure what is throwing the DOMException
            simulator = null;
            success = false;
        }
        return success;
    }

    /**
     * Replaces the current simulation configuration file if one exists or adds the simulation
     * configuration file to the project.
     *
     * @param filename  The full path to the newly added configuration file
     * @return True if the simulation configuration file was successfully added or replaced,
     *         otherwise false
     */
    public boolean addSimConfigFile(String filename) {
        boolean success = true;
        try {
            removeSimulationConfigurationFile();
            simulationConfigurationFile = doc.createElement(SCRIPT_CONFIG_FILE_TAG_NAME);
            Text configFileText = doc.createTextNode(filename);
            simulationConfigurationFile.appendChild(configFileText);
            root.appendChild(simulationConfigurationFile);
        } catch (DOMException e) {
            simulationConfigurationFile = null;
            success = false;
        }
        return success;
    }

    /**
     * Adds a generated script file to the project.
     *
     * @param scriptBasename  The base-name of the file path
     * @param extension  The extension of the file path
     * @return True if the script was added to the model correctly, false if not
     */
    public boolean addScript(String scriptBasename, String extension) {
        boolean success = true;
        try {
            /* Remove Previous Script */
            removeScript();

            /* Create Elements */
            // this will overwrite previously defined script
            script = doc.createElement(SCRIPT_TAG_NAME);
            // file element of the script element
            Element scriptFile = doc.createElement(SCRIPT_FILE_TAG_NAME);

            /* Add Values */
            // text element describing the file location
            Text scriptFileLocation = doc.createTextNode(scriptBasename + "." + extension);

            /* Attach Elements */
            scriptFile.appendChild(scriptFileLocation);
            script.appendChild(scriptFile);
            root.appendChild(script);

            int version = 0;
            try {
                version = Integer.parseInt(getScriptVersion());
            } catch (NumberFormatException e) { // version not present
                initScriptVersion();
            }
            version++;
            setScriptVersion(String.valueOf(version));
        } catch (DOMException | NumberFormatException e) {
            script = null;
            success = false;
        }
        return success;
    }

    /**
     * Provides the full path of the first simulation configuration file found.
     *
     * @return The full path of the first simulation configuration file found, or null if no file
     *         was specified in the project.
     */
    public String getSimConfigFilename() {
        return getFirstChildTextContent(root, SCRIPT_CONFIG_FILE_TAG_NAME);
    }

    /**
     * Indicates whether the current script (matching the current script version number in the
     * project) has been executed yet.
     *
     * @return True if the current version of the script has begun execution, otherwise false
     */
    public boolean scriptGenerated() {
        return script != null;
    }

    /**
     * Indicates wither the output of the current script version has been analyzed yet.
     *
     * @return True if the script output has been analyzed, otherwise false
     */
    public boolean scriptOutputAnalyzed() {
        String analyzedAttributeValue;
        boolean analyzed = false;
        if (script != null) {
            analyzedAttributeValue = script.getAttribute(SCRIPT_ANALYZED_ATTRIBUTE_NAME);
            analyzed = Boolean.parseBoolean(analyzedAttributeValue);
        }
        return analyzed;
    }

    /**
     * Sets a string representation of whether or not the output of the current script version has
     * been analyzed.
     *
     * @param analyzed  Indication of whether or not the analysis has been completed
     */
    public void setScriptAnalyzed(boolean analyzed) {
        if (script != null) {
            script.setAttribute(SCRIPT_ANALYZED_ATTRIBUTE_NAME, String.valueOf(analyzed));
        }
    }

    public void setSimStateOutputFile(String stateOutputFilename) {
        if (simulationConfigurationFile != null) {
            simulationConfigurationFile.setAttribute(SIMULATION_CONFIGURATION_FILE_ATTRIBUTE_NAME,
                    stateOutputFilename);
        }
    }

    /**
     * Provides the full path to the raw output of the simulation. This is redirected standard
     * output from the simulator executable. The filename provided is of the imported file (within
     * the project directory in the workbench managed directories).
     *
     * @return The filename of the raw simulation output imported into the workbench. To be clear,
     *         this is the filename of the target of the import, not the source of the import.
     */
    public String getSimStateOutputFile() {
        String filename = null;
        if (simulationConfigurationFile != null) {
            filename = simulationConfigurationFile.getAttribute(
                    SIMULATION_CONFIGURATION_FILE_ATTRIBUTE_NAME);
        }
        return filename;
    }
    // </editor-fold>
}
