package edu.uwb.braingrid.workbench;

import java.nio.file.Path;

public interface FileManagerShared {

    Path getProjectsDirectory();

    Path getBrainGridRepoDirectory();

    String getSimulationsDirectory();
}
