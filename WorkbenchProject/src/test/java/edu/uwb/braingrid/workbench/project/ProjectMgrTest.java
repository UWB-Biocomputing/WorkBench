package edu.uwb.braingrid.workbench.project;

import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbench.model.SimulationSpecification;
import edu.uwb.braingrid.workbench.utils.DateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * These tested are based on reading edu/uwb/braingrid/workbench/project/ProjectMgr.java and then
 * determining the tests based on the public functions intended functionality. These test provide
 * some insight but are not are extensive as they should be.
 *
 * -Max
 */
public class ProjectMgrTest {

    @Test
    // Test Constructor
    public void constructorTest() {
        //////////////
        // Valid Cases
        //////////////
        // New Project
        ProjectMgr pm = getPmNameFalseLoad();
        Assertions.assertNotEquals(null, pm);
        // Load project
        pm = getPmNameTrueLoadActualProject();
        Assertions.assertNotEquals(null, pm);

        ////////////////
        // Exceptions
        ////////////////
        // Load a nonexistant project
        pm = getPmNameTrueLoadNotAProject();
        Assertions.assertEquals(null, pm);
        // Null string as a name, new project
        pm = getPmNullNameFalseLoad();
        Assertions.assertEquals(null, pm);
        // Null string as a name, load project
        pm = getPmNullNameTrueLoad();
        Assertions.assertEquals(null, pm);
    }

    @Test
    // Test Load
    public void loadTest() {
        //////////////
        // Valid Cases
        //////////////
        // New Project
        ProjectMgr pmNew = getPmNameFalseLoad();
        try {
            pmNew.load("not-gunna-be-a-file");
            Assertions.fail("An io exception should be thrown. This line should not be reached");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }

        // Load project
        ProjectMgr pmLoad = getPmNameTrueLoadActualProject();
        try {
            pmLoad.load("not-gunna-be-a-file");
            Assertions.fail("An io exception should be thrown. This line should not be reached");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }

        // TODO: ParserConfigurationExceptin and SAXException not tested
    }

    @Test
    public void loadPersistTest() {
        // New Project
        String newProjectName = "NewProjectToSave";
        ProjectMgr pmNew = null;
        try {
            pmNew = new ProjectMgr(newProjectName, false);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        try {
            Assertions.assertEquals(pmNew.getProjectFilename(), pmNew.persist());
        } catch (TransformerException e) {
            Assertions.fail("Transformer Exception");
            e.printStackTrace();
        } catch (IOException e) {
            Assertions.fail("IOException");
            e.printStackTrace();
        }

        // Load project
        ProjectMgr pmLoad = getPmNameTrueLoadActualProject();
        try {
            Assertions.assertEquals(pmLoad.getProjectFilename(), pmLoad.persist());
        } catch (TransformerException e) {
            Assertions.fail("Transformer Exception");
            e.printStackTrace();
        } catch (IOException e) {
            Assertions.fail("IOException");
            e.printStackTrace();
        }

        // TODO: ParserConfigurationExceptin and SAXException not tested
    }

    @Test
    public void getProjectLocationTest() {
        // New Prj
        ProjectMgr pmNew = getPmNameFalseLoad();
        this.getProjectLocationTestHelper(pmNew);

        // Load Prj
        ProjectMgr pmLoad = getPmNameTrueLoadActualProject();
        this.getProjectLocationTestHelper(pmLoad);
    }

    private void getProjectLocationTestHelper(ProjectMgr pm) {
        String workingDirectory = FileManager.getProjectsDirectory();

        String fs = File.separator;
        String projectDirectory = workingDirectory + fs + "projects" + fs
                + pm.getName() + fs;
        Assertions.assertEquals(projectDirectory, pm.getProjectLocation());
    }

    @Test
    public void getProvLocationTest() {
        // New Prj
        ProjectMgr pmNew = getPmNameFalseLoad();
        this.getProvLocationTestHelper(pmNew);

        // Load Prj
        ProjectMgr pmLoad = getPmNameTrueLoadActualProject();
        this.getProvLocationTestHelper(pmLoad);
    }

    private void getProvLocationTestHelper(ProjectMgr pm) {
        String workingDirectory = FileManager.getProjectsDirectory();

        String fs = File.separator;
        String projectDirectory = workingDirectory + fs + "projects" + fs
                + pm.getName() + fs;

        try {
            Assertions.assertEquals(projectDirectory, ProjectManager.getProjectLocation(pm.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void scriptGenerationAvailableTest() {
        // New Prj
        ProjectMgr pmNew = getPmNameFalseLoad();
        this.addSimulatorTo(pmNew);
        pmNew.removeScript();
        pmNew.addSimConfigFile("Example");
        Assertions.assertTrue(pmNew.scriptGenerationAvailable());
        //Assertions.fail("Sob violently. Ended here for today.");
    }

    @Test
    public void addSimulatorTest() {
        // New Prj
        ProjectMgr pmNew = getPmNameFalseLoad();
        Assertions.assertTrue(pmNew.addSimulator(
                ProjectMgr.LOCAL_EXECUTION,
                "",
                "C:\\Users\\Max\\Documents\\DOCUMENTS\\Braingrid-WD\\" +
                        "BrainGrid\\Tools\\Workbench\\WorkbenchProject\\BrainG" +
                        "ridRepos", SimulationSpecification.SimulatorType.SEQUENTIAL.toString(), "C:\\Users\\Max\\Documents\\DOCUMENTS\\Braingrid-WD\\" +
                        "BrainGrid\\Tools\\Workbench\\WorkbenchProject\\BrainG" +
                        "ridRepos", "1.0.0",
                SimulationSpecification.GIT_NONE,
                "",
                SimulationSpecification.PRE_BUILT_BUILD_OPTION));
    }

    @Test
    public void removeAddIsScriptTest() {
        // New Project
        ProjectMgr pm = getPmNameFalseLoad();
        Assertions.assertFalse(pm.scriptGenerated());
        this.addScriptToProject(pm);
        Assertions.assertTrue(pm.scriptGenerated());
        pm.removeScript();
        Assertions.assertFalse(pm.scriptGenerated());
    }

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters Tests">
    @Test
    public void getProjectFileNameTest() {
        // New Project
        ProjectMgr pmNew = getPmNameFalseLoad();
        try {
            String projectFile = Paths.get(pmNew.getProjectLocation(), pmNew.getName() + ".xml").toString();
            Assertions.assertEquals(projectFile, pmNew.getProjectFilename());
        } catch (IOException e) {

        }

        // Load Project
        ProjectMgr pmLoad = getPmNameTrueLoadActualProject();
        try {
            String projectFile = Paths.get(pmLoad.getProjectLocation(), pmLoad.getName() + ".xml").toString();
            Assertions.assertEquals(projectFile, pmLoad.getProjectFilename());
        } catch (IOException e) {

        }
    }

    @Test
    public void setAndGetNameTest() {
        // New Prj
        ProjectMgr pmNew = getPmNameFalseLoad();

        String newName = "NanananananananaBatman";
        pmNew.setName(newName);
        Assertions.assertEquals(newName, pmNew.getName());

        // Load Prj
        ProjectMgr pmLoad = getPmNameTrueLoadActualProject();
        pmLoad.setName(newName);
        Assertions.assertEquals(newName, pmLoad.getName());
    }

    @Test
    public void setAndGetProvenanceEnabledTest() {
        // New Prj
        ProjectMgr pmNew = getPmNameFalseLoad();

        pmNew.setProvenanceEnabled(false);
        Assertions.assertFalse(pmNew.isProvenanceEnabled());
        pmNew.setProvenanceEnabled(true);
        Assertions.assertTrue(pmNew.isProvenanceEnabled());

        // Load Prj
        ProjectMgr pmLoad = getPmNameTrueLoadActualProject();
        pmLoad.setProvenanceEnabled(false);
        Assertions.assertFalse(pmLoad.isProvenanceEnabled());
        pmLoad.setProvenanceEnabled(true);
        Assertions.assertTrue(pmLoad.isProvenanceEnabled());
    }

    @Test
    public void getSimulationSpecificationTest() {
        ProjectMgr pmNew = getPmNameFalseLoad();
        pmNew.addSimulator(
                ProjectMgr.LOCAL_EXECUTION,
                "",
                "C:\\Users\\Max\\Documents\\DOCUMENTS\\Braingrid-WD\\" +
                        "BrainGrid\\Tools\\Workbench\\WorkbenchProject\\BrainG" +
                        "ridRepos", SimulationSpecification.SimulatorType.SEQUENTIAL.toString(), "C:\\Users\\Max\\Documents\\DOCUMENTS\\Braingrid-WD\\" +
                        "BrainGrid\\Tools\\Workbench\\WorkbenchProject\\BrainG" +
                        "ridRepos", "1.0.0",
                SimulationSpecification.GIT_NONE,
                "",
                SimulationSpecification.PRE_BUILT_BUILD_OPTION);
        this.getSimulationSpecificationTestHelper(pmNew);

        // No simulator specified
        ProjectMgr pm = getPmNameFalseLoad();
        this.getSimulationSpecificationTestHelper(pm);
    }

    private void getSimulationSpecificationTestHelper(ProjectMgr pm) {
        String simType = pm.getSimulationType();
        String codeLocation = pm.getSimulatorCodeLocation();
        String locale = pm.getSimulatorLocale();
        String folder = pm.getSimulatorFolderLocation();
        String hostname = pm.getSimulatorHostname();
        String sha1 = pm.getSHA1Key();
        String buildOption = pm.getBuildOption();
        String updating = pm.getSimulatorSourceCodeUpdatingType();
        String version = pm.getSimulatorVersionAnnotation();
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

        Assertions.assertTrue(pm.getSimulationSpecification().equals(ss));
    }

    @Test
    public void getSimulatorLocaleTest() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        Assertions.assertEquals(locale, pm.getSimulatorLocale());
    }

    @Test
    public void getSimulatorVersionAnnotationTest() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        Assertions.assertEquals(version, pm.getSimulatorVersionAnnotation());
    }

    @Test
    public void getSimulatorCodeLocationTest() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        Assertions.assertEquals(simfolder, pm.getSimulatorCodeLocation());
    }

    @Test
    public void getSimulatorFolderLocationTest() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        Assertions.assertEquals(codeLocation, pm.getSimulatorCodeLocation());
    }

    @Test
    public void getSimulatorHostnameTest() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        Assertions.assertEquals(hostname, pm.getSimulatorHostname());
    }

    @Test
    public void getSetSHA1Key() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        this.addScriptToProject(pm);
        Assertions.assertEquals(sha1, pm.getSHA1Key());
        String sha1key = "qwerty";
        Assertions.assertFalse(pm.setSHA1Key(sha1key)); // Not sure why false is right
        Assertions.assertEquals(sha1key, pm.getSHA1Key());
    }

    @Test
    public void getSetBuildOptionTest() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        this.addScriptToProject(pm);
        Assertions.assertEquals(buildOption, pm.getBuildOption());
        String build = SimulationSpecification.BUILD_BUILD_OPTION;
        Assertions.assertFalse(pm.setBuildOption(build)); // Not sure why false is right
        Assertions.assertEquals(build, pm.getBuildOption());
    }

    @Test
    public void getSimulatorSourceCodeUpdatingType() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        Assertions.assertEquals(sourceCodeOption, pm.getSimulatorSourceCodeUpdatingType());
    }

    @Test
    public void getSimulationTypeTest() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        Assertions.assertEquals(simulatorType.toString(), pm.getSimulationType());
    }

    @Test
    public void getSetScriptVersionTest() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        Assertions.assertEquals(Integer.toString(0),pm.getScriptVersion());
        int scriptVersion = 2;
        pm.setScriptVersion("" + scriptVersion);
        Assertions.assertEquals(Integer.toString(scriptVersion),pm.getScriptVersion());
    }

    @Test
    public void getNextScriptVersionTest() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        Assertions.assertEquals(Integer.toString(1), pm.getNextScriptVersion());
    }

    @Test
    public void setGetScriptRanTest() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        this.addScriptToProject(pm);
        Assertions.assertFalse(pm.getScriptRan());
        pm.setScriptRan(true);
        Assertions.assertTrue(pm.getScriptRan());
    }

    @Test
    public void setGetScriptRanAtTest() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        this.addScriptToProject(pm);
        Assertions.assertEquals(DateTime.ERROR_TIME, pm.getScriptRanAt());
        pm.setScriptRanAt();
        Assertions.assertNotEquals(DateTime.ERROR_TIME, pm.getScriptRanAt());
    }

    @Test
    public void setGetScriptCompletedAtTest() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        this.addScriptToProject(pm);
        Assertions.assertEquals(DateTime.ERROR_TIME, pm.getScriptCompletedAt());
        long time = 10000;
        pm.setScriptCompletedAt(time);
        Assertions.assertEquals(time, pm.getScriptCompletedAt());
    }

    @Test
    public void getSetScriptHostnameTest() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        this.addScriptToProject(pm);
        Assertions.assertEquals(null, pm.getScriptHostname());
       // Assertions.assertEquals(hostname, pm.getScriptHostname());
        String host = "Hosty";
        Assertions.assertTrue(pm.setScriptHostname(host));
        // Assertions.assertEquals(host, pm.getScriptHostname());
    }

    @Test
    public void getScriptCanonicalFilePathTest() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        this.addScriptToProject(pm);
        String comp = scriptName + "." + scriptExtension;
        Assertions.assertEquals(comp, pm.getScriptCanonicalFilePath());
    }

    @Test
    public void getAddSimConfigFilename() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        this.addScriptToProject(pm);
        Assertions.assertEquals(null, pm.getSimConfigFilename());
        String filename = "Example";
        Assertions.assertTrue(pm.addSimConfigFile(filename));
        Assertions.assertEquals(filename, pm.getSimConfigFilename());
    }

    @Test
    public void setIsScriptAnalyzedTest() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        pm.setScriptAnalyzed(true);
        Assertions.assertFalse(pm.scriptOutputAnalyzed());
        this.addScriptToProject(pm);
        pm.setScriptAnalyzed(true);
        Assertions.assertTrue(pm.scriptOutputAnalyzed());
        pm.setScriptAnalyzed(false);
        Assertions.assertFalse(pm.scriptOutputAnalyzed());
    }

    @Test
    public void setGetSimResultFileTest() {
        ProjectMgr pm = getPmNameFalseLoad();
        this.addSimulatorTo(pm);
        this.addScriptToProject(pm);
        Assertions.assertNull(pm.getSimResultFile());
        String simResultFile = "TheResultFileToRuleThemAll.txt";
        pm.addSimConfigFile("Example");
        pm.setSimResultFile(simResultFile);
        Assertions.assertEquals(simResultFile, pm.getSimResultFile());
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Simulator"
    private String locale = ProjectMgr.LOCAL_EXECUTION;
    private String hostname = "Hostname";
    private String simfolder = "C:\\Users\\Max\\Documents\\DOCUMENTS\\Braingrid-WD\\" +
            "BrainGrid\\Tools\\Workbench\\WorkbenchProject\\BrainG" +
            "ridRepos";
    private String codeLocation = "C:\\Users\\Max\\Documents\\DOCUMENTS\\Braingrid-WD\\" +
            "BrainGrid\\Tools\\Workbench\\WorkbenchProject\\BrainG" +
            "ridRepos";
    private SimulationSpecification.SimulatorType simulatorType = SimulationSpecification.SimulatorType.SEQUENTIAL;
    private String version = "1.0.0";
    private String sourceCodeOption = SimulationSpecification.GIT_NONE;
    private String sha1 = "";
    private String buildOption = SimulationSpecification.PRE_BUILT_BUILD_OPTION;

    private void addSimulatorTo(ProjectMgr pm) {
        pm.addSimulator(
                locale,
                hostname,
                simfolder,
                simulatorType.toString(),
                codeLocation, version,
                sourceCodeOption,
                sha1,
                buildOption);
    }

    String scriptName = "Hello";
    String scriptExtension = "xml";
    private void addScriptToProject(ProjectMgr pm) {
        pm.addScript(scriptName, scriptExtension);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Factories">
    // name, true, invalid project
    private ProjectMgr getPmNameTrueLoadActualProject() {
        ProjectMgr pm = null;
        try {
            pm = new ProjectMgr("Example", true);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        IOException e;
        return pm;
    }

    private ProjectMgr getPmNameTrueLoadNotAProject() {
        ProjectMgr pm = null;
        try {
            pm = new ProjectMgr("NotARealProject", true);
            Assertions.fail("IO Exception expected to be thrown. This line should not have been reached.");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {

        } catch (SAXException e) {
            e.printStackTrace();
        }

        IOException e;
        return pm;
    }

    // name, false
    private ProjectMgr getPmNameFalseLoad() {
        ProjectMgr pm = null;
        try {
            pm = new ProjectMgr("NewProject", false);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return pm;
    }

    // null name, true
    private ProjectMgr getPmNullNameTrueLoad() {
        ProjectMgr pm = null;
        try {
            pm = new ProjectMgr(null, true);
            Assertions.fail("NullPointerException expected to be thrown. This line should not have been reached.");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {

        }
        return pm;
    }

    // null name, false
    private ProjectMgr getPmNullNameFalseLoad() {
        ProjectMgr pm = null;
        try {
            pm = new ProjectMgr(null, false);
            Assertions.fail("NullPointerException expected to be thrown. This line should not have been reached.");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {

        }
        return pm;
    }
    // </editor-fold>
}
