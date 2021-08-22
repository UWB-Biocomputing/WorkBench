package edu.uwb.braingrid.workbench.model;

import edu.uwb.braingrid.workbench.utils.DateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScriptHistoryTest {

    @Test
    public void testGetSetStartedAt() {
        ScriptHistory sh = factory();

        Assertions.assertEquals(DateTime.ERROR_TIME, sh.getStartedAt());
        long startedAt = DateTime.now();
        sh.setStartedAt(startedAt);
        Assertions.assertEquals(startedAt, sh.getStartedAt());
    }


    @Test
    public void testGetSetCompletedAt() {
        ScriptHistory sh = factory();

        Assertions.assertEquals(DateTime.ERROR_TIME, sh.getStartedAt());
        long completedAt = DateTime.now();
        sh.setCompletedAt(completedAt);
        Assertions.assertEquals(completedAt, sh.getCompletedAt());
    }

    @Test
    public void testGetSetOutputAnalyzed() {
        ScriptHistory sh = factory();
        Assertions.assertFalse(sh.isOutputAnalyzed());
        sh.setOutputAnalyzed(true);
        Assertions.assertTrue(sh.isOutputAnalyzed());
    }

    @Test
    public void testGetSetRan() {
        ScriptHistory sh = factory();
        Assertions.assertFalse(sh.isRan());
        sh.setRan(true);
        Assertions.assertTrue(sh.isRan());
    }

    @Test
    public void testGetSetFilename() {
        ScriptHistory sh = factory();
        Assertions.assertEquals(null, sh.getFilename());
        String filename = "Filename";
        sh.setFilename(filename);
        Assertions.assertEquals(filename, sh.getFilename());
    }

    private ScriptHistory factory() {
        return new ScriptHistory("None");
    }
}
