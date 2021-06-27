package edu.uwb.braingrid.general;

import java.io.File;

import edu.uwb.braingrid.workbench.provvisualizer.ProVisGlobal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DirMgrTest {
    @Test
    public void getRootPathTest() {
        Assertions.assertEquals(System.getProperty("user.dir"), DirMgr.getRootPath());
    }

    @Test
    public void getBrainGridRepoDirectoryTest() {
        Assertions.assertEquals(DirMgr.getRootPath() + File.separator + ProVisGlobal.BG_REPOSITORY_LOCAL, DirMgr.getBrainGridRepoDirectory());
    }
}
