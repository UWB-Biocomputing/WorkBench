package edu.uwb.braingrid.workbench.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.nio.file.Path;
import java.util.LinkedHashMap;

import edu.uwb.braingrid.workbench.FileManager;

/**
 * Represents a Workbench project which manages a collection of simulations.
 *
 * @author Steven Leighton
 */
@JsonSerialize(using = ProjectSerializer.class)
@JsonDeserialize(using = ProjectDeserializer.class)
public class Project {

    // <editor-fold defaultstate="collapsed" desc="Members">
    private String name;
    private LinkedHashMap<String, Simulation> simulations;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Construction">
    public Project(String projectName) {
        name = projectName;
        simulations = new LinkedHashMap<>();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashMap<String, Simulation> getSimulations() {
        return simulations;
    }

    public void setSimulations(LinkedHashMap<String, Simulation> simulations) {
        this.simulations = simulations;
    }

    public void addSimulation(Simulation simulation) {
        simulations.put(simulation.getName(), simulation);
    }

    public void removeSimulation(String simulationName) {
        simulations.remove(simulationName);
    }

    public boolean contains(String simulationName) {
        return simulations.containsKey(simulationName);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Path Info">
    /**
     * Provides the folder location for this project.
     *
     * @return The path to the project folder for this project
     */
    private Path getProjectLocation() {
        return FileManager.getProjectsDirectory().resolve(name);
    }

    /**
     * Provides the assumed folder location for a project of a given name.
     *
     * @return The path to the project folder for the specified project
     */
    public static Path getProjectLocation(String projectName) {
        return FileManager.getProjectsDirectory().resolve(projectName);
    }

    /**
     * Provides the full path, including the filename, containing the JSON for this project.
     *
     * @return The full path, including the filename, for the file containing the JSON for this
     *         project
     */
    public Path getProjectFilePath() {
        return getProjectLocation().resolve(name + ".json");
    }

    /**
     * Provides the full path, including the filename, containing the JSON for a project of a given
     * name.
     *
     * @return The full path, including the filename, for the file containing the JSON for the
     *         specified project
     */
    public static Path getProjectFilePath(String projectName) {
        return getProjectLocation(projectName).resolve(projectName + ".json");
    }
    // </editor-fold>
}
