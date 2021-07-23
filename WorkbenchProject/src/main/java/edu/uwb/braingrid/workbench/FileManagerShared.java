package edu.uwb.braingrid.workbench;

import java.nio.file.Path;

public interface FileManagerShared {

    Path getProjectsDirectory();

    Path getSimulationsDirectory();

    Path getBrainGridRepoDirectory();
}
