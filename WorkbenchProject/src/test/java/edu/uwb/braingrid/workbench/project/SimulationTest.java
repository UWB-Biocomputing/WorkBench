package edu.uwb.braingrid.workbench.project;

import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbench.model.SimulationSpecification;
import edu.uwb.braingrid.workbench.utils.DateTime;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * These tested are based on reading edu/uwb/braingrid/workbench/simulation/Simulation.java and then
 * determining the tests based on the public functions' intended functionality. These test provide
 * some insight but are not are extensive as they should be.
 *
 * -Max
 */
public class SimulationTest {

    @Test
    // Test Constructor
    public void constructorTest() {
        //////////////
        // Valid Cases
        //////////////
        // New Simulation
        Simulation sim = getSimNameFalseLoad();
        Assertions.assertNotEquals(null, sim);
        // Load Simulation
        sim = getSimNameTrueLoadActualSimulation();
        Assertions.assertNotEquals(null, sim);

        ////////////////
        // Exceptions
        ////////////////
        // Load a nonexistant simulation
        sim = getSimNameTrueLoadNotASimulation();
        Assertions.assertEquals(null, sim);
        // Null string as a name, new simulation
        sim = getSimNullNameFalseLoad();
        Assertions.assertEquals(null, sim);
        // Null string as a name, load simulation
        sim = getSimNullNameTrueLoad();
        Assertions.assertEquals(null, sim);
    }

    @Test
    // Test Load
    public void loadTest() {
        //////////////
        // Valid Cases
        //////////////
        // New Simulation
        Simulation simNew = getSimNameFalseLoad();
        try {
            simNew.load();
            Assertions.fail("An io exception should be thrown. This line should not be reached");
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }

        // Load Simulation
        Simulation simLoad = getSimNameTrueLoadActualSimulation();
        try {
            simLoad.load();
            Assertions.fail("An io exception should be thrown. This line should not be reached");
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }

        // TODO: ParserConfigurationExceptin and SAXException not tested
    }

    @Test
    public void loadPersistTest() {
        // New Simulation
        String newSimulationName = "NewSimulationToSave";
        Simulation simNew = null;
        try {
            simNew = new Simulation(newSimulationName, false);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        try {
            Assertions.assertEquals(simNew.getSimulationFilePath(), simNew.persist());
        } catch (ParserConfigurationException | TransformerException | IOException e) {
            Assertions.fail(e.getClass().getSimpleName());
        }

        // Load Simulation
        Simulation simLoad = getSimNameTrueLoadActualSimulation();
        try {
            Assertions.assertEquals(simLoad.getSimulationFilePath(), simLoad.persist());
        } catch (ParserConfigurationException | TransformerException | IOException e) {
            Assertions.fail(e.getClass().getSimpleName());
        }

        // TODO: ParserConfigurationExceptin and SAXException not tested
    }

    @Test
    public void getSimulationLocationTest() {
        // New Prj
        Simulation simNew = getSimNameFalseLoad();
        this.getSimulationLocationTestHelper(simNew);

        // Load Prj
        Simulation simLoad = getSimNameTrueLoadActualSimulation();
        this.getSimulationLocationTestHelper(simLoad);
    }

    private void getSimulationLocationTestHelper(Simulation sim) {
        String workingDirectory = FileManager.getProjectsDirectory().toString();

        String fs = File.separator;
        String simulationDirectory = workingDirectory + fs + "simulations" + fs
                + sim.getName() + fs;
        Assertions.assertEquals(simulationDirectory, sim.getSimulationLocation());
    }

    @Test
    public void getProvLocationTest() {
        // New Prj
        Simulation simNew = getSimNameFalseLoad();
        this.getProvLocationTestHelper(simNew);

        // Load Prj
        Simulation simLoad = getSimNameTrueLoadActualSimulation();
        this.getProvLocationTestHelper(simLoad);
    }

    private void getProvLocationTestHelper(Simulation sim) {
        String workingDirectory = FileManager.getProjectsDirectory().toString();

        String fs = File.separator;
        String simulationDirectory = workingDirectory + fs + "simulation" + fs + sim.getName() + fs;

        Assertions.assertEquals(simulationDirectory, sim.getSimulationLocation());
    }

    @Test
    public void scriptGenerationAvailableTest() {
        // New Prj
        Simulation simNew = getSimNameFalseLoad();
        this.addSimulatorToSim(simNew);
        simNew.removeScript();
        simNew.addSimConfigFile("Example");
        Assertions.assertTrue(simNew.scriptGenerationAvailable());
        //Assertions.fail("Sob violently. Ended here for today.");
    }

    @Test
    public void addSimulatorTest() {
        // New simulation
        Simulation simNew = getSimNameFalseLoad();
        String locale = SimulationSpecification.LOCAL_EXECUTION;
        String hostname = "";
        String simFolder = "C:\\Users\\Max\\Documents\\DOCUMENTS\\Braingrid-WD\\" +
                "BrainGrid\\Tools\\Workbench\\WorkbenchProject\\BrainGridRepos";
        String simType = SimulationSpecification.SimulatorType.SEQUENTIAL.toString();
        String buildOption = SimulationSpecification.PRE_BUILT_BUILD_OPTION;
        String codeLocation = "C:\\Users\\Max\\Documents\\DOCUMENTS\\Braingrid-WD\\" +
                "BrainGrid\\Tools\\Workbench\\WorkbenchProject\\BrainGridRepos";
        String sourceCodeUpdating = SimulationSpecification.GIT_NONE;
        String sha1Key = "";
        String version = "1.0.0";

        simNew.addSimulator(locale, hostname, simFolder, simType, buildOption, codeLocation,
                sourceCodeUpdating, sha1Key, version);
    }

    @Test
    public void removeAddIsScriptTest() {
        // New Simulation
        Simulation sim = getSimNameFalseLoad();
        Assertions.assertFalse(sim.scriptGenerated());
        this.addScriptToSim(sim);
        Assertions.assertTrue(sim.scriptGenerated());
        sim.removeScript();
        Assertions.assertFalse(sim.scriptGenerated());
    }

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters Tests">
    @Test
    public void getSimulationFileNameTest() {
        // New Simulation
        Simulation simNew = getSimNameFalseLoad();

        String simulationFile = simNew.getSimulationLocation().resolve(simNew.getName() + ".xml").toString();
        Assertions.assertEquals(simulationFile, simNew.getSimulationFilePath());

        // Load Simulation
        Simulation simLoad = getSimNameTrueLoadActualSimulation();

        simulationFile = simLoad.getSimulationLocation().resolve(simLoad.getName() + ".xml").toString();
        Assertions.assertEquals(simulationFile, simLoad.getSimulationFilePath());
    }

    @Test
    public void setAndGetNameTest() {
        // New Prj
        Simulation simNew = getSimNameFalseLoad();

        String newName = "NanananananananaBatman";
        simNew.setName(newName);
        Assertions.assertEquals(newName, simNew.getName());

        // Load Prj
        Simulation simLoad = getSimNameTrueLoadActualSimulation();
        simLoad.setName(newName);
        Assertions.assertEquals(newName, simLoad.getName());
    }

    @Test
    public void setAndGetProvenanceEnabledTest() {
        // New Prj
        Simulation simNew = getSimNameFalseLoad();

        simNew.setProvenanceEnabled(false);
        Assertions.assertFalse(simNew.isProvenanceEnabled());
        simNew.setProvenanceEnabled(true);
        Assertions.assertTrue(simNew.isProvenanceEnabled());

        // Load Prj
        Simulation simLoad = getSimNameTrueLoadActualSimulation();
        simLoad.setProvenanceEnabled(false);
        Assertions.assertFalse(simLoad.isProvenanceEnabled());
        simLoad.setProvenanceEnabled(true);
        Assertions.assertTrue(simLoad.isProvenanceEnabled());
    }

    @Test
    public void getSimulationSpecificationTest() {
        Simulation simNew = getSimNameFalseLoad();
        simNew.addSimulator(
                SimulationSpecification.LOCAL_EXECUTION,
                "",
                "C:\\Users\\Max\\Documents\\DOCUMENTS\\Braingrid-WD\\" +
                        "BrainGrid\\Tools\\Workbench\\WorkbenchProject\\BrainGridRepos",
                SimulationSpecification.SimulatorType.SEQUENTIAL.toString(),
                "C:\\Users\\Max\\Documents\\DOCUMENTS\\Braingrid-WD\\" +
                        "BrainGrid\\Tools\\Workbench\\WorkbenchProject\\BrainGridRepos",
                "1.0.0",
                SimulationSpecification.GIT_NONE,
                "",
                SimulationSpecification.PRE_BUILT_BUILD_OPTION);
        this.getSimulationSpecificationTestHelper(simNew);

        // No simulator specified
        Simulation sim = getSimNameFalseLoad();
        this.getSimulationSpecificationTestHelper(sim);
    }

    private void getSimulationSpecificationTestHelper(Simulation sim) {
        String simType = sim.getSimulationType();
        String codeLocation = sim.getSimulatorCodeLocation();
        String locale = sim.getSimulatorLocale();
        String folder = sim.getSimulatorFolderLocation();
        String hostname = sim.getSimulatorHostname();
        String sha1 = sim.getSHA1Key();
        String buildOption = sim.getBuildOption();
        String updating = sim.getSimulatorSourceCodeUpdatingType();
        String version = sim.getSimulatorVersionAnnotation();
        String executable = null;
        if (simType != null && !simType.isEmpty()) {
            executable = SimulationSpecification.getSimFilename(simType);
        }

        SimulationSpecification ss = new SimulationSpecification();
        ss.setSimulationType(simType);
        ss.setCodeLocation(codeLocation);
        ss.setSimulationLocale(locale);
        ss.setSimulatorFolder(folder);
        ss.setHostAddr(hostname);
        ss.setSHA1CheckoutKey(sha1);
        ss.setBuildOption(buildOption);
        ss.setSourceCodeUpdating(updating);
        ss.setVersionAnnotation(version);
        ss.setSimExecutable(executable);

        Assertions.assertTrue(sim.getSimSpec().equals(ss));
    }

    @Test
    public void getSimulatorLocaleTest() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(locale, sim.getSimulatorLocale());
    }

    @Test
    public void getSimulatorVersionAnnotationTest() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(version, sim.getSimulatorVersionAnnotation());
    }

    @Test
    public void getSimulatorCodeLocationTest() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(simfolder, sim.getSimulatorCodeLocation());
    }

    @Test
    public void getSimulatorFolderLocationTest() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(codeLocation, sim.getSimulatorCodeLocation());
    }

    @Test
    public void getSimulatorHostnameTest() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(hostname, sim.getSimulatorHostname());
    }

    @Test
    public void getSetSHA1Key() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        this.addScriptToSim(sim);
        Assertions.assertEquals(sha1, sim.getSHA1Key());
        String sha1key = "qwerty";
        sim.getSimSpec().setSHA1CheckoutKey(sha1key);
        Assertions.assertEquals(sha1key, sim.getSHA1Key());
    }

    @Test
    public void getSetBuildOptionTest() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        this.addScriptToSim(sim);
        Assertions.assertEquals(buildOption, sim.getBuildOption());
        String build = SimulationSpecification.BUILD_BUILD_OPTION;
        sim.getSimSpec().setBuildOption(build);
        Assertions.assertEquals(build, sim.getBuildOption());
    }

    @Test
    public void getSimulatorSourceCodeUpdatingType() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(sourceCodeOption, sim.getSimulatorSourceCodeUpdatingType());
    }

    @Test
    public void getSimulationTypeTest() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(simulatorType.toString(), sim.getSimulationType());
    }

    @Test
    public void getSetScriptVersionTest() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(Integer.toString(0),sim.getScriptVersion());
        int scriptVersion = 2;
        sim.setScriptVersion(scriptVersion);
        Assertions.assertEquals(Integer.toString(scriptVersion),sim.getScriptVersion());
    }

    @Test
    public void getNextScriptVersionTest() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(Integer.toString(1), sim.getNextScriptVersion());
    }

    @Test
    public void setGetScriptRanTest() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        this.addScriptToSim(sim);
        Assertions.assertFalse(sim.hasScriptRun());
        sim.setScriptRan(true);
        Assertions.assertTrue(sim.hasScriptRun());
    }

    @Test
    public void setGetScriptRanAtTest() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        this.addScriptToSim(sim);
        Assertions.assertEquals(DateTime.ERROR_TIME, sim.getScriptStartedAt());
        sim.getScriptStartedAt();
        Assertions.assertNotEquals(DateTime.ERROR_TIME, sim.getScriptStartedAt());
    }

    @Test
    public void setGetScriptCompletedAtTest() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        this.addScriptToSim(sim);
        Assertions.assertEquals(DateTime.ERROR_TIME, sim.getScriptCompletedAt());
        long time = 10000;
        sim.setScriptCompletedAt(time);
        Assertions.assertEquals(time, sim.getScriptCompletedAt());
    }

    @Test
    public void getScriptFilePathTest() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        this.addScriptToSim(sim);
        String comp = scriptFilename;
        Assertions.assertEquals(comp, sim.getScriptFilePath());
    }

    @Test
    public void getAddSimConfigFilename() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        this.addScriptToSim(sim);
        Assertions.assertEquals(null, sim.getSimConfigFilename());
        String filename = "Example";
        sim.addSimConfigFile(filename);
        Assertions.assertEquals(filename, sim.getSimConfigFilename());
    }

    @Test
    public void setIsScriptAnalyzedTest() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        sim.setScriptOutputAnalyzed(true);
        Assertions.assertFalse(sim.wasScriptOutputAnalyzed());
        this.addScriptToSim(sim);
        sim.setScriptOutputAnalyzed(true);
        Assertions.assertTrue(sim.wasScriptOutputAnalyzed());
        sim.setScriptOutputAnalyzed(false);
        Assertions.assertFalse(sim.wasScriptOutputAnalyzed());
    }

    @Test
    public void setGetSimResultFileTest() {
        Simulation sim = getSimNameFalseLoad();
        this.addSimulatorToSim(sim);
        this.addScriptToSim(sim);
        Assertions.assertNull(sim.getSimResultFile());
        String simResultFile = "TheResultFileToRuleThemAll.txt";
        sim.addSimConfigFile("Example");
        sim.setSimResultFile(simResultFile);
        Assertions.assertEquals(simResultFile, sim.getSimResultFile());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Simulator"
    private String locale = SimulationSpecification.LOCAL_EXECUTION;
    private String hostname = "Hostname";
    private String simfolder = "C:\\Users\\Max\\Documents\\DOCUMENTS\\Braingrid-WD\\" +
            "BrainGrid\\Tools\\Workbench\\WorkbenchProject\\BrainGridRepos";
    private String codeLocation = "C:\\Users\\Max\\Documents\\DOCUMENTS\\Braingrid-WD\\" +
            "BrainGrid\\Tools\\Workbench\\WorkbenchProject\\BrainGridRepos";
    private SimulationSpecification.SimulatorType simulatorType = SimulationSpecification.SimulatorType.SEQUENTIAL;
    private String version = "1.0.0";
    private String sourceCodeOption = SimulationSpecification.GIT_NONE;
    private String sha1 = "";
    private String buildOption = SimulationSpecification.PRE_BUILT_BUILD_OPTION;

    private void addSimulatorToSim(Simulation sim) {
        sim.addSimulator(
                locale,
                hostname,
                simfolder,
                simulatorType.toString(),
                codeLocation, version,
                sourceCodeOption,
                sha1,
                buildOption);
    }

    String scriptFilename = "Hello.sh";
    private void addScriptToSim(Simulation sim) {
        sim.addScript(scriptFilename);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Factories">
    // name, true, invalid simulation
    private Simulation getSimNameTrueLoadActualSimulation() {
        Simulation sim = null;
        try {
            sim = new Simulation("Example", true);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return sim;
    }

    private Simulation getSimNameTrueLoadNotASimulation() {
        Simulation sim = null;
        try {
            sim = new Simulation("NotARealSimulation", true);
            Assertions.fail("IO Exception expected to be thrown. This line should not have been reached.");
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return sim;
    }

    // name, false
    private Simulation getSimNameFalseLoad() {
        Simulation sim = null;
        try {
            sim = new Simulation("NewSimulation", false);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return sim;
    }

    // null name, true
    private Simulation getSimNullNameTrueLoad() {
        Simulation sim = null;
        try {
            sim = new Simulation(null, true);
            Assertions.fail("NullPointerException expected to be thrown. This line should not have been reached.");
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {

        }
        return sim;
    }

    // null name, false
    private Simulation getSimNullNameFalseLoad() {
        Simulation sim = null;
        try {
            sim = new Simulation(null, false);
            Assertions.fail("NullPointerException expected to be thrown. This line should not have been reached.");
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {

        }
        return sim;
    }
    // </editor-fold>
}
