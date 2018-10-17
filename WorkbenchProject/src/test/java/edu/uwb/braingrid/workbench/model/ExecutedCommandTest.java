package edu.uwb.braingrid.workbench.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class ExecutedCommandTest {

    @Test
    public void testGetFullCommand() {
        ExecutedCommand ec = factory();
        Assertions.assertEquals(fullcommand, ec.getFullCommand());
    }

    @Test
    public void testGetSimpleCommand() {
        ExecutedCommand ec = factory();
        Assertions.assertEquals(fullcommand, ec.getSimpleCommand());
    }

    @Test
    public void testGetSetTimeStartedTest() {
        ExecutedCommand ec = factory();
        Assertions.assertNull(ec.getTimeStarted());
        Date date = new Date();
        ec.setTimeStarted(date);
        Assertions.assertEquals(date, ec.getTimeStarted());
    }

    @Test
    public void testGetSetTimeCompleted() {
        ExecutedCommand ec = factory();
        Assertions.assertNull(ec.getTimeCompleted());
        Date date = new Date();
        ec.setTimeCompleted(date);
        Assertions.assertEquals(date, ec.getTimeCompleted());
    }

    @Test
    public void testGetSetExitStatus() {
        ExecutedCommand ec = factory();
        Assertions.assertEquals(0, ec.getExitStatus());
        int executiveOrder = 66;
        ec.setExitStatus(executiveOrder);
        Assertions.assertEquals(executiveOrder, ec.getExitStatus());
    }

    @Test
    public void testHasStarted() {
        ExecutedCommand ec = factory();
        Assertions.assertFalse(ec.hasStarted());
        ec.setTimeStarted(new Date());
        Assertions.assertTrue(ec.hasStarted());

    }

    public void testHasCompleted() {
        ExecutedCommand ec = factory();
        Assertions.assertFalse(ec.hasCompleted());
        ec.setTimeCompleted(new Date());
        Assertions.assertTrue(ec.hasCompleted());
    }

    private final String simplecommand = "Cobra";
    private final String fullcommand = "Cobra Command";
    public ExecutedCommand factory() {
        return new ExecutedCommand(fullcommand);
    }
}
