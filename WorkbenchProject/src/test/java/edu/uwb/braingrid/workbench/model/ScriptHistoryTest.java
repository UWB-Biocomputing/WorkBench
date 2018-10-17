package edu.uwb.braingrid.workbench.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScriptHistoryTest {

    @Test
    public void testGetSetStartedAt() {
        ScriptHistory sh = factory();

        Assertions.assertEquals(null, sh.getStartedAt());
        String startedAt = "A String?";
        sh.setStartedAt(startedAt);
        Assertions.assertEquals(startedAt, sh.getStartedAt());
    }


    @Test
    public void testGetSetCompletedAt() {
        ScriptHistory sh = factory();

        Assertions.assertEquals(null, sh.getStartedAt());
        String completedAt = "A String";
        sh.setCompletedAt(completedAt);
        Assertions.assertEquals(completedAt, sh.getCompletedAt());
    }

    @Test
    public void testGetSetOutputAnalyzed() {
        ScriptHistory sh = factory();
        Assertions.assertFalse(sh.getOutputAnalyzed());
        sh.setOutputAnalyzed(true);
        Assertions.assertTrue(sh.getOutputAnalyzed());
    }

    @Test
    public void testGetSetRan() {
        ScriptHistory sh = factory();
        Assertions.assertFalse(sh.getRan());
        sh.setRan(true);
        Assertions.assertTrue(sh.getRan());
    }

    @Test
    public void testGetSetFilename() {
        ScriptHistory sh = factory();
        Assertions.assertEquals(null, sh.getFilename());
        String filename = "Filename";
        sh.setFilename(filename);
        Assertions.assertEquals(filename, sh.getFilename());
    }

    @Test
    public void testGetSetVersion() {
        ScriptHistory sh = factory();
        Assertions.assertEquals(0, sh.getVersion());
        int version = 10;
        sh.setVersion(version);
        Assertions.assertEquals(version, sh.getVersion());

    }

    @Test
    public void testIncrementVersion() {
        ScriptHistory sh = factory();
        sh.incrementVersion();
        int version = 2;
        sh.setVersion(version);
        sh.incrementVersion();
        Assertions.assertEquals(version + 1, sh.getVersion());
    }

    @Test
    public void testGetNextScriptVersion() {
        ScriptHistory sh = factory();
        Assertions.assertEquals("1", sh.getNextScriptVersion());
        Assertions.assertEquals("1", sh.getNextScriptVersion());
        int version = 10;
        sh.setVersion(version);
        Assertions.assertEquals(String.valueOf(version + 1), sh.getNextScriptVersion());
    }

    private ScriptHistory factory() {
        return new ScriptHistory();
    }
}
