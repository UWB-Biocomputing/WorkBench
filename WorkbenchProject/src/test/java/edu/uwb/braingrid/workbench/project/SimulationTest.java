package edu.uwb.braingrid.workbench.project;

import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbench.model.SimulationSpecification;
import edu.uwb.braingrid.workbench.utils.DateTime;

import java.io.File;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Simulation sim = getSimValidName();
        Assertions.assertNotEquals(null, sim);

        ////////////////
        // Exceptions
        ////////////////
        // null name
        sim = getSimNullName();
        Assertions.assertNull(sim);
    }

    @Test
    public void getSimulationLocationTest() {
        Simulation simNew = getSimValidName();
        this.getSimulationLocationTestHelper(simNew);
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
        // New Simulation
        Simulation simNew = getSimValidName();
        this.getProvLocationTestHelper(simNew);
    }

    private void getProvLocationTestHelper(Simulation sim) {
        String workingDirectory = FileManager.getProjectsDirectory().toString();

        String fs = File.separator;
        String simulationDirectory = workingDirectory + fs + "simulation" + fs + sim.getName() + fs;

        Assertions.assertEquals(simulationDirectory, sim.getSimulationLocation());
    }

    @Test
    public void scriptGenerationAvailableTest() {
        // New Simulation
        Simulation simNew = getSimValidName();
        this.addSimulatorToSim(simNew);
        simNew.removeScript();
        simNew.setSimConfigFile("Example");
        Assertions.assertTrue(simNew.scriptGenerationAvailable());
        //Assertions.fail("Sob violently. Ended here for today.");
    }

    @Test
    public void addSimulatorTest() {
        // New simulation
        Simulation simNew = getSimValidName();
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
        Simulation sim = getSimValidName();
        Assertions.assertFalse(sim.scriptGenerated());
        this.addScriptToSim(sim);
        Assertions.assertTrue(sim.scriptGenerated());
        sim.removeScript();
        Assertions.assertFalse(sim.scriptGenerated());
    }

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters Tests">
    @Test
    public void getSimulationFileNameTest() {
        Simulation simNew = getSimValidName();

        String simulationFile = simNew.getSimulationLocation().resolve(simNew.getName() + ".xml").toString();
        Assertions.assertEquals(simulationFile, simNew.getSimulationFilePath());
    }

    @Test
    public void setAndGetNameTest() {
        Simulation simNew = getSimValidName();

        String newName = "NanananananananaBatman";
        simNew.setName(newName);
        Assertions.assertEquals(newName, simNew.getName());
    }

    @Test
    public void setAndGetProvenanceEnabledTest() {
        Simulation simNew = getSimValidName();

        simNew.setProvenanceEnabled(false);
        Assertions.assertFalse(simNew.isProvenanceEnabled());
        simNew.setProvenanceEnabled(true);
        Assertions.assertTrue(simNew.isProvenanceEnabled());
    }

    @Test
    public void getSimulationSpecificationTest() {
        Simulation simNew = getSimValidName();
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

        // No simulation specified
        Simulation sim = getSimValidName();
        this.getSimulationSpecificationTestHelper(sim);
    }

    private void getSimulationSpecificationTestHelper(Simulation sim) {
        String simType = sim.getSimulationType();
        String codeLocation = sim.getSimulatorCodeLocation();
        String locale = sim.getSimulationLocale();
        String folder = sim.getSimulatorFolderLocation();
        String hostname = sim.getSimulatorHostname();
        String sha1 = sim.getSHA1Key();
        String buildOption = sim.getBuildOption();
        String updating = sim.getSimulatorSourceCodeUpdatingType();
        String version = sim.getSimulatorVersionAnnotation();

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

        Assertions.assertTrue(sim.getSimSpec().equals(ss));
    }

    @Test
    public void getSimulationLocaleTest() {
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(locale, sim.getSimulationLocale());
    }

    @Test
    public void getSimulatorVersionAnnotationTest() {
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(version, sim.getSimulatorVersionAnnotation());
    }

    @Test
    public void getSimulatorCodeLocationTest() {
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(simfolder, sim.getSimulatorCodeLocation());
    }

    @Test
    public void getSimulatorFolderLocationTest() {
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(codeLocation, sim.getSimulatorCodeLocation());
    }

    @Test
    public void getSimulatorHostnameTest() {
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(hostname, sim.getSimulatorHostname());
    }

    @Test
    public void getSetSHA1Key() {
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        this.addScriptToSim(sim);
        Assertions.assertEquals(sha1, sim.getSHA1Key());
        String sha1key = "qwerty";
        sim.getSimSpec().setSHA1CheckoutKey(sha1key);
        Assertions.assertEquals(sha1key, sim.getSHA1Key());
    }

    @Test
    public void getSetBuildOptionTest() {
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        this.addScriptToSim(sim);
        Assertions.assertEquals(buildOption, sim.getBuildOption());
        String build = SimulationSpecification.BUILD_BUILD_OPTION;
        sim.getSimSpec().setBuildOption(build);
        Assertions.assertEquals(build, sim.getBuildOption());
    }

    @Test
    public void getSimulatorSourceCodeUpdatingType() {
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(sourceCodeOption, sim.getSimulatorSourceCodeUpdatingType());
    }

    @Test
    public void getSimulationTypeTest() {
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(simulatorType.toString(), sim.getSimulationType());
    }

    @Test
    public void getSetScriptVersionTest() {
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(Integer.toString(0),sim.getScriptVersion());
        int scriptVersion = 2;
        sim.setScriptVersion(scriptVersion);
        Assertions.assertEquals(Integer.toString(scriptVersion),sim.getScriptVersion());
    }

    @Test
    public void getNextScriptVersionTest() {
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        Assertions.assertEquals(Integer.toString(1), sim.getNextScriptVersion());
    }

    @Test
    public void setGetScriptRanTest() {
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        this.addScriptToSim(sim);
        Assertions.assertFalse(sim.hasScriptRun());
        sim.setScriptRan(true);
        Assertions.assertTrue(sim.hasScriptRun());
    }

    @Test
    public void setGetScriptRanAtTest() {
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        this.addScriptToSim(sim);
        Assertions.assertEquals(DateTime.ERROR_TIME, sim.getScriptStartedAt());
        sim.getScriptStartedAt();
        Assertions.assertNotEquals(DateTime.ERROR_TIME, sim.getScriptStartedAt());
    }

    @Test
    public void setGetScriptCompletedAtTest() {
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        this.addScriptToSim(sim);
        Assertions.assertEquals(DateTime.ERROR_TIME, sim.getScriptCompletedAt());
        long time = 10000;
        sim.setScriptCompletedAt(time);
        Assertions.assertEquals(time, sim.getScriptCompletedAt());
    }

    @Test
    public void getScriptFilePathTest() {
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        this.addScriptToSim(sim);
        String comp = scriptFilename;
        Assertions.assertEquals(comp, sim.getScriptFilePath());
    }

    @Test
    public void getAddSimConfigFilename() {
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        this.addScriptToSim(sim);
        Assertions.assertEquals(null, sim.getSimConfigFilename());
        String filename = "Example";
        sim.setSimConfigFile(filename);
        Assertions.assertEquals(filename, sim.getSimConfigFilename());
    }

    @Test
    public void setIsScriptAnalyzedTest() {
        Simulation sim = getSimValidName();
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
        Simulation sim = getSimValidName();
        this.addSimulatorToSim(sim);
        this.addScriptToSim(sim);
        Assertions.assertNull(sim.getSimResultFile());
        String simResultFile = "TheResultFileToRuleThemAll.txt";
        sim.setSimConfigFile("Example");
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
    // valid name
    private Simulation getSimValidName() {
        return new Simulation("NewSimulation");
    }

    // null name
    private Simulation getSimNullName() {
        Simulation sim = null;
        try {
            sim = new Simulation(null);
            Assertions.fail("NullPointerException expected to be thrown. This line should not have been reached.");
        } catch (NullPointerException e) {

        }
        return sim;
    }
    // </editor-fold>
}
