package edu.uwb.braingrid.workbench;

import edu.uwb.braingrid.workbench.utils.DateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WorkbenchManagerTest {

    private final WorkbenchManager wbm = WorkbenchManager.getInstance();

    @Test
    public void test() {
        //Assertions.fail("Need to test still");
    }

    @Test
    public void newSimulationTest() {
        // UI Dialog, nothing to test
    }

    @Test
    public void configureSimulationTest() {
        // UI Dialog, nothing to test
    }

    @Test
    public void viewProvenanceTest() {
        // UI Dialog, nothing to test
    }

    @Test
    public void openProjectTest() {
        // UI Dialog, nothing to test
    }

    @Test
    public void saveProjectTest() {
        // UI Dialog, nothing to test
    }

    @Test
    public void launchNLEditTest() {
        // UI launch, nothing to test
    }

    @Test
    public void specifySimulationTest() {
        // UI launch, nothing to test
    }

    @Test
    public void analyzeScriptOutputTest() {
        analyzeScriptOutputTest1();
        analyzeScriptOutputTest2();
        analyzeScriptOutputTest3();
        analyzeScriptOutputTest4();
        analyzeScriptOutputTest5();
        analyzeScriptOutputTest6();
    }

    @Test
    // Project Manager is null
    private void analyzeScriptOutputTest1() {
        Assertions.assertEquals(DateTime.ERROR_TIME, wbm.analyzeScriptOutput());
    }

    @Test
    // ProjectManager is not null, and script has been analyzed
    private void analyzeScriptOutputTest2() {
        WorkbenchManager wm = initSimulationWorkbenchManagerFactory(true);
        test();
    }

    @Test
    // ProjectManager is not null, script has not been analyzed
    private void analyzeScriptOutputTest3() {
        test();
    }

    @Test
    // ProjectManager is not null, script has not been analyzed - IOException
    private void analyzeScriptOutputTest4() {
        test();
    }

    @Test
    // ProjectManager is not null, script has not been analyzed- JSchException
    private void analyzeScriptOutputTest5() {
        test();
    }

    @Test
    // ProjectManager is not null, script has not been analyzed - SftpException
    private void analyzeScriptOutputTest6() {
        test();
    }

    @Test
    public void generateScriptTest() {
        test();
    }

    @Test
    public void runScriptTest() {
        test();
    }

    private final String correctFileName = "correct";
    private final String incorrectFileName = "incorrect.?$%^&Y";

    @Test
    // Correct file name, provenance is enabled
    public void initSimulationTest1() {
        boolean result = wbm.initSimulation(correctFileName, true);
        Assertions.assertTrue(result);
    }

    @Test
    // Correct file name, provenance is not enabled
    public void initSimulationTest2() {
        boolean result = wbm.initSimulation(correctFileName, false);
        Assertions.assertTrue(result);
    }

    @Test
    // Incorrect file name, provenance is enabled
    public void initSimulationTest3() {
        boolean result = wbm.initSimulation(incorrectFileName, true);
        Assertions.assertTrue(result);
    }

    @Test
    // Incorrect file name, provenance is not enabled
    public void initSimulationTest4() {
        boolean result = wbm.initSimulation(incorrectFileName, false);
        Assertions.assertTrue(result);
    }

    @Test
    public void getSimulationNameTest() {
        Assertions.assertEquals("None", wbm.getSimulationName());
        boolean result = wbm.initSimulation(correctFileName, true);
        Assertions.assertEquals(correctFileName, wbm.getSimulationName());
    }

    @Test
    public void isProvEnabledTest() {
        test();
    }

    @Test
    public void getNextScriptNameTest() {
        test();
    }

    @Test
    public void getSimConfigFileOverviewTest() {
        test();
    }

    @Test
    public void getSimulationOverviewTest() {
        test();
    }

    @Test
    public void getScriptRunOverviewTest() {
        test();
    }

    @Test
    public void getScriptAnalysisOverviewTest() {
        test();
    }

    @Test
    public void getProvMgrTest() {
        test();
    }

    @Test
    public void getMessagesTest() {
        test();
    }

    @Test
    public void configureParamsClassesTest() {
        test();
    }

    private WorkbenchManager initSimulationWorkbenchManagerFactory(boolean provEnabled) {
        wbm.initSimulation(correctFileName, provEnabled);
        return wbm;
    }
}
