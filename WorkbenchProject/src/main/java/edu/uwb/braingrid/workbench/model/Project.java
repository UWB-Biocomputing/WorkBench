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
    /** Name of the project. */
    private String name;
    /** Simulations managed by this project. */
    private LinkedHashMap<String, Simulation> simulations;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Construction">
    /**
     * Creates an empty Project with the given name.
     *
     * @param projectName  Name given to the project
     */
    public Project(String projectName) {
        name = projectName;
        simulations = new LinkedHashMap<>();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    /**
     * Provides the name of this project.
     *
     * @return The name of this project
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the project name.
     *
     * @param name  The new project name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Provides the collection of simulations managed by this project.
     *
     * @return  The collection of simulations managed by this project
     */
    public LinkedHashMap<String, Simulation> getSimulations() {
        return simulations;
    }

    /**
     * Adds a simulation to this project.
     *
     * @param simulation  The simulation to be added
     */
    public void addSimulation(Simulation simulation) {
        simulations.put(simulation.getName(), simulation);
    }

    /**
     * Removes a simulation with the given name from this project.
     *
     * @param simulationName  The name of the simulation to be removed
     */
    public void removeSimulation(String simulationName) {
        simulations.remove(simulationName);
    }

    /**
     * Indicates whether or not a simulation with the given name is contained within this project.
     *
     * @param simulationName  The name of the simulation to check
     * @return True if a simulation with the given name is present, false if not
     */
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
     * @param projectName  the name of the project
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
     * @param projectName  the name of the project
     * @return The full path, including the filename, for the file containing the JSON for the
     *         specified project
     */
    public static Path getProjectFilePath(String projectName) {
        return getProjectLocation(projectName).resolve(projectName + ".json");
    }
    // </editor-fold>
}
