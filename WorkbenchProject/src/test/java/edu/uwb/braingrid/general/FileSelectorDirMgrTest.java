package edu.uwb.braingrid.general;

import java.io.File;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileSelectorDirMgrTest {

    private FileSelectorDirMgr fileSelectorDirMgrFactory() {
        FileSelectorDirMgr fs = new FileSelectorDirMgr();

        File main = new File("./main");
        fs.addDir(main);
        File resources = new File("./resources");
        fs.addDir(resources);
        Assertions.assertEquals(resources, fs.getLastDir());

        return fs;
    }

    @Test
    public void getLastDirTestAndAddTestAndDefault() {

        FileSelectorDirMgr fs = new FileSelectorDirMgr();
        Assertions.assertNull(fs.getLastDir());

        File main = new File("./main");
        fs.addDir(main);
        Assertions.assertEquals(main, fs.getLastDir());

        File resources = new File("./resources");
        fs.addDir(resources);
        Assertions.assertEquals(resources, fs.getLastDir());
    }
}
