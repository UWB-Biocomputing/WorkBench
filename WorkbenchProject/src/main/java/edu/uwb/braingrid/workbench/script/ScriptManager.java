package edu.uwb.braingrid.workbench.script;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jena.rdf.model.Resource;
import org.xml.sax.SAXException;

import edu.uwb.braingrid.provenance.ProvMgr;
import edu.uwb.braingrid.provenance.WorkbenchOperationRecorder;
import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbench.comm.SecureFileTransfer;
import edu.uwb.braingrid.workbench.data.InputAnalyzer;
import edu.uwb.braingrid.workbench.model.Simulation;
import edu.uwb.braingrid.workbench.model.SimulationSpecification;
import edu.uwb.braingrid.workbench.ui.LoginCredentialsDialog;
import edu.uwb.braingrid.workbench.utils.DateTime;

/**
 * Manages script creation, script execution, and script output analysis.
 *
 * @author Del Davis, extended by Joseph Conquest
 */
public class ScriptManager {

    private static final Logger LOG = Logger.getLogger(ScriptManager.class.getName());

    private String outstandingMessages;

    /**
     * Responsible for construction of the script manager and initialization of queued messages
     * reported to the class maintaining this object.
     */
    public ScriptManager() {
        outstandingMessages = "";
    }

    /**
     * Generates a constructed (but not persisted) Script.
     *
     * @param simulationName  Name of the simulation that generated the script
     * @param simSpec  The specification for the simulator to execute in the script
     * @param simConfigFilename  Name of configuration file used as simulation input (XML file that
     *                           specifies simulation parameters, environment constants, and
     *                           simulated spatial information (or names of files which contain such
     *                           spatial data). This is the file that is constructed using the
     *                           simulation specification dialog).
     * @return A constructed script or null in the case that the script could not be constructed
     *         properly
     */
    public static Script generateScript(String simulationName, SimulationSpecification simSpec,
            String simConfigFilename) {
        boolean success;
        String scriptOutputDir = FileManager.getSimulationsDirectory() + "/" + simulationName;
        // make path safe for variable interpolation
        scriptOutputDir = replaceTildeWithHome(scriptOutputDir);
        // convert file path to simple file name
        String configFilename = FileManager.getSimpleFilename(simConfigFilename);
        // create a new script
        Script script = new Script();
        script.setScriptOutputDirectory(scriptOutputDir);
        script.setCmdOutputFilename(simulationName + "_" + Script.COMMAND_OUTPUT_FILENAME);
        script.setScriptStatusOutputFilename(simulationName + "_" + Script.SCRIPT_STATUS_FILENAME);

        /* Prep From Simulation Data */
        // central repository location
        String repoURI = simSpec.getCodeLocation();

        // git pull or do we assume it exists as is?
        String simulatorSourceCodeUpdatingType = simSpec.getSourceCodeUpdating();
        boolean updateRepo = false;
        if (simulatorSourceCodeUpdatingType != null) {
            updateRepo = simulatorSourceCodeUpdatingType.equals(
                    SimulationSpecification.CLONE_NEW);
        }

        /* Create Script */
        String workFolder = replaceTildeWithHome(FileManager.getSimulationsDirectory() + "/work");
        String binFolder = replaceTildeWithHome(scriptOutputDir + "/bin");
        simSpec.setSimulatorFolder(binFolder);
        String executableName = "cgraphitti";

        // create the work directory.
        String[] argsForMkdir = {"-p", workFolder};
        script.executeProgram("mkdir", argsForMkdir);

        // create the bin directory.
        argsForMkdir = new String[]{"-p", binFolder};
        script.executeProgram("mkdir", argsForMkdir);

        // change directories to the work folder.
        String[] argsForCd = {workFolder};
        script.executeProgram("cd", argsForCd);

        if (updateRepo) {
            /* if statement */
            script.addVerbatimStatement("if git status; then", false);

            // restore any unstaged changes.
            String[] gitCheckoutArgs = new String[]{"checkout", "."};
            script.executeProgram("git", gitCheckoutArgs);

            // pull.
            String[] gitPullArgs = new String[]{"pull", "origin", "master"};
            script.executeProgram("git", gitPullArgs);

            /* else statement */
            script.addVerbatimStatement("else", false);

            String[] rmArgs = new String[]{"-rf", "*"};
            script.executeProgram("rm", rmArgs);

            String[] gitCloneArgs = new String[]{"clone", repoURI, workFolder};
            script.executeProgram("git", gitCloneArgs);

            script.addVerbatimStatement("fi", false);

            /* Checkout a particular commit if necessary */
            String sha1CheckoutKey = simSpec.getSHA1CheckoutKey().trim();
            if (sha1CheckoutKey == null || sha1CheckoutKey.length() == 0) {
                sha1CheckoutKey = "master";
            }

            gitCheckoutArgs = new String[]{"checkout", sha1CheckoutKey};
            script.executeProgram("git", gitCheckoutArgs);

            /* Make the Simulator */
            // first, determine which version of the sim we are using (sequential or parallel) and
            // update CMakeLists.txt accordingly.
            String simType = simSpec.getSimulationType();
            String cudaOpt;
            if (simType.equals(SimulationSpecification.SimulatorType.CUDA)) {
                cudaOpt = "\"/^set(ENABLE_CUDA NO)/ s/NO/YES/\"";
            } else {
                // If the sim is sequential or unidentifiable we will attempt to build it as
                // sequential.
                cudaOpt = "\"/^set(ENABLE_CUDA YES)/ s/YES/NO/\"";
            }
            script.addVerbatimStatement("sed -i " + cudaOpt + " CMakeLists.txt", false);

            // now we can build, cd into the build folder.
            String[] cdArgs = {"build"};
            script.executeProgram("cd", cdArgs);

            // run cmake
            String[] cMakeArgs = {".."};
            script.executeProgram("cmake", cMakeArgs);
            if (simType.equals(SimulationSpecification.SimulatorType.CUDA)) {
                // If we're building the parallel (CUDA) version, run cmake again.
                script.executeProgram("cmake", cMakeArgs);
            }

            // run make
            String[] makeArgs = {};
            script.executeProgram("make", makeArgs);
        }

        if (!updateRepo) {
            // We need to CD into the build folder if we are not cloning.
            String[] cdArgs = new String[]{"build"};
            script.executeProgram("cd", cdArgs);
        }

        /* Copy the results from the build. */
        // copy the executable to the bin folder
        String[] cpArgs = {executableName, binFolder};
        script.executeProgram("cp", cpArgs);

        // copy over the runtime files
        cpArgs = new String[]{"-r",
                "RuntimeFiles/",
                replaceTildeWithHome(scriptOutputDir) + "/"};
        script.executeProgram("cp", cpArgs);

        // copy over the output files
        cpArgs = new String[]{"-r",
                "Output/",
                replaceTildeWithHome(scriptOutputDir) + "/"};
        script.executeProgram("cp", cpArgs);

        // Get commit info.
        script.addVerbatimStatement("git log --pretty=format:'%H' -n 1", scriptOutputDir + "/"
                + simulationName + "_" + Script.SHA1_KEY_FILENAME, false);

        /* Make Results Folder */
        // change directory to script output location
        String[] cdArgs = {scriptOutputDir};
        script.executeProgram("cd", cdArgs);

        String[] mkResultsDirArgs = {"-p", "results/"};
        script.executeProgram("mkdir", mkResultsDirArgs);

        /* Run the Simulator */
        script.addVerbatimStatement(binFolder + "/" + executableName + " -c "
                                        + "configfiles/" + configFilename,
                        scriptOutputDir + "/" + simulationName + "_" + Script.SIM_STATUS_FILENAME,
                            true);

        /* Put Script Together and Save */
        if (!script.construct()) {
            script = null; // or indicate unsuccessful operation
        }
        return script;
    }

    /**
     * Runs the script as specified in the associated simulation specification.
     *
     * @param provMgr  Manages provenance. Used to record operations executed from script.
     * @param simSpec  Holds information about the simulation
     * @param simulationName
     * @param scriptPath  Indicates where the constructed script to execute resides on the file
     *                    system.
     * @param nListFilenames  Neuron lists indicated in simulation configuration file
     * @param simConfigFilename  Name of file containing simulation constants and filenames. File,
     *                           itself, is used to specify all input for a simulation.
     * @return True if all operations associated with running the script were successful, otherwise
     *         false. Note: This does not indicate whether the simulation ran successfully.
     * @throws com.jcraft.jsch.JSchException
     * @throws com.jcraft.jsch.SftpException
     * @throws java.io.IOException
     */
    public boolean runScript(ProvMgr provMgr, SimulationSpecification simSpec,
            String simulationName, String scriptPath, String[] nListFilenames,
            String simConfigFilename) throws JSchException, SftpException, IOException {
        boolean success;
        String executionMachine = simSpec.getSimulationLocale();
        String remoteExecution = SimulationSpecification.REMOTE_EXECUTION;
        // run script remotely?
        if (executionMachine.equals(remoteExecution)) {
            LOG.info("Running Remote Script: " + scriptPath);
            success = runRemoteScript(provMgr, simSpec.getHostAddr(), simulationName, scriptPath,
                    nListFilenames, simConfigFilename);
        } else { // or run it locally
            LOG.info("Running Local Script " + scriptPath);
            success = runLocalScript(provMgr, simulationName, scriptPath, nListFilenames,
                    simConfigFilename);
        }
        return success;
    }

    private boolean runRemoteScript(ProvMgr provMgr, String hostname, String simulationName,
            String scriptPath, String[] nListFilenames, String simConfigFilename)
            throws JSchException, FileNotFoundException, SftpException {
        Long functionStartTime = System.currentTimeMillis();
        Long accumulatedTime = 0L;
        char[] password = null;
        boolean success = true;
        // get username and password
        LoginCredentialsDialog lcd = new LoginCredentialsDialog(hostname, true);
        if (lcd.okClicked()) {
            SecureFileTransfer sft = new SecureFileTransfer();
            password = lcd.getPassword();
            lcd.clearPassword();
            /* Create Simulation Directory and Subdirectories */
            String simDir = FileManager.getSimulationsDirectory() + "/" + simulationName;
            String configDir = simDir + "/configfiles";
            String nListDir = simDir + "/configfiles/NList";
            String remoteScriptPath = simDir + "/" + FileManager.getSimpleFilename(scriptPath);
            String cmd = "mkdir -p " + nListDir;
            sft.executeCommand(cmd, hostname, lcd.getUsername(), password, true);
            FileOutputStream simDirLocation = new FileOutputStream(new File(makeLastSimulationDir() + "\\simdir"));
            try {
              ObjectOutputStream writeSimDir = new ObjectOutputStream(simDirLocation);
              writeSimDir.writeObject(simDir);
            } catch (IOException e) {
              e.printStackTrace();
            }
            /* Upload Script */
            Date uploadStartTime = new Date();
            if (sft.uploadFile(scriptPath, simDir, hostname, lcd.getUsername(), password, null)) {
                // record provenance of upload
                if (provMgr != null) {
                    Long startTime = System.currentTimeMillis();
                    WorkbenchOperationRecorder.recordFile(provMgr, scriptPath, remoteScriptPath,
                            "script", hostname, "uploadScript", uploadStartTime, new Date());
                    accumulatedTime = DateTime.sumProvTiming(startTime, accumulatedTime);
                }
                outstandingMessages += "\n" + scriptPath + "\nuploaded to " + hostname + "\n";
                /* Upload Neuron List Files */
                boolean loopSuccess;
                if (nListFilenames != null) {
                    for (String nListFilename : nListFilenames) {
                        String filename = FileManager.getSimpleFilename(nListFilename);
                        outstandingMessages += "\n" + "Uploaded " + nListFilename + "\nto "
                                + hostname + "\n";
                        uploadStartTime = new Date();
                        loopSuccess = sft.uploadFile(nListFilename, nListDir, hostname,
                                lcd.getUsername(), password, null);
                        if (!loopSuccess) {
                            success = false;
                            outstandingMessages += "\n" + "Problem uploading " + nListFilename
                                    + "\nto " + hostname + "\n";
                            break;
                        } else {
                            outstandingMessages += "\n" + filename + "\nuploaded to " + hostname
                                    + "\n";
                            // record upload provenance
                            if (provMgr != null) {
                                Long startTime = System.currentTimeMillis();
                                String nlType = "";
                                try {
                                    nlType = InputAnalyzer.getInputType(
                                            new File(nListFilename)).toString();
                                } catch (ParserConfigurationException | SAXException
                                        | IOException ex) {
                                }
                                WorkbenchOperationRecorder.recordFile(provMgr, nListFilename,
                                        nListDir + FileManager.getSimpleFilename(nListFilename),
                                        "nlist", hostname, "upload_" + nlType + "_NList",
                                        uploadStartTime, new Date());
                                accumulatedTime = DateTime.sumProvTiming(startTime,
                                        accumulatedTime);
                            }
                        }
                    }
                }
                /* Upload Simulation Configuration File */
                if (success) {
                    uploadStartTime = new Date();
                    success = sft.uploadFile(simConfigFilename, configDir, hostname,
                            lcd.getUsername(), password, null);
                    if (success) {
                        if (provMgr != null) {
                            Long startTime = System.currentTimeMillis();
                            WorkbenchOperationRecorder.recordFile(provMgr, simConfigFilename,
                                    configDir + FileManager.getSimpleFilename(simConfigFilename),
                                    "simulationConfigurationFile", hostname, "upload_SimConfig",
                                    uploadStartTime, new Date());
                            accumulatedTime = DateTime.sumProvTiming(startTime, accumulatedTime);
                        }
                        outstandingMessages += "\n"
                                + FileManager.getSimpleFilename(simConfigFilename)
                                + "\nuploaded to " + hostname + "\n";
                    } else {
                        outstandingMessages += "\n" + "Problem uploading "
                                + FileManager.getSimpleFilename(simConfigFilename)
                                + "\nto " + hostname + "\n";
                    }
                }
                /* Execute Script */
                if (success) {
                    cmd = "nohup sh " + remoteScriptPath + " &";
                    outstandingMessages += "\n" + "Executing " + cmd
                            + "\nat "
                            + hostname + "\n";
                    sft.executeCommand(cmd, hostname, lcd.getUsername(), password, false);
                    success = true;
                }
            } else {
                outstandingMessages += "\n" + "Failed to upload script to " + hostname + "\n";
            }
        } else {
            outstandingMessages += "\n" + "\nRemote Credentials Specification Cancelled\n";
        }
        outstandingMessages += "\n" + "Remote Operations Completed: " + new Date() + "\n";

        if (password != null) {
            Arrays.fill(password, '0');
        }
        DateTime.recordFunctionExecutionTime("ScriptManager", "runRemoteScript",
                System.currentTimeMillis() - functionStartTime, provMgr != null);
        if (provMgr != null) {
            DateTime.recordAccumulatedProvTiming("ScriptManager", "runRemoteScript",
                    accumulatedTime);
        }
        return success;
    }

    private boolean runLocalScript(ProvMgr provMgr, String simulationName, String scriptLocation,
            String[] inputFilenames, String simConfigFilename) throws IOException {
        LOG.info("Running Local Script");
        Long functionStartTime = System.currentTimeMillis();
        Long accumulatedTime = 0L;
        boolean success = true;
        Date copyStartTime;
        // create simulation directory and subdirectories
        String localSimsDir = FileManager.getSimulationsDirectory()
                .replaceFirst("^~", System.getProperty("user.home"));
        Path simDir = Paths.get(localSimsDir, simulationName);
        Path configDir = simDir.resolve("configfiles");
        Path nListDir = configDir.resolve("NList");
        Files.createDirectories(nListDir);
        // get source and target paths
        Path scriptSourcePath = Paths.get(scriptLocation);
        Path scriptTargetPath = simDir.resolve(scriptSourcePath.getFileName());
        Path simConfigSourcePath = Paths.get(simConfigFilename);
        Path simConfigTargetPath = configDir.resolve(simConfigSourcePath.getFileName());
        Path[] nListSourcePaths = null;
        Path[] nListTargetPaths = null;
        if (inputFilenames != null && inputFilenames.length > 0) {
            nListSourcePaths = new Path[inputFilenames.length];
            nListTargetPaths = new Path[inputFilenames.length];
            for (int i = 0, im = inputFilenames.length; i < im; i++) {
                nListSourcePaths[i] = Paths.get(inputFilenames[i]);
            }
            for (int i = 0, im = nListSourcePaths.length; i < im; i++) {
                nListTargetPaths[i] = nListDir.resolve(nListSourcePaths[i].getFileName());
            }
        }
        try {
            // copy the script
            copyStartTime = new Date();
            FileManager.copyFile(scriptSourcePath, scriptTargetPath);
            // record provenance for copy operation
            if (provMgr != null) {
                Long startTime = System.currentTimeMillis();
                WorkbenchOperationRecorder.copyFile(provMgr, scriptSourcePath.toString(),
                        scriptTargetPath.toString(), "script", "copy_Script", copyStartTime,
                        new Date());
                accumulatedTime = DateTime.sumProvTiming(startTime, accumulatedTime);
            }
            outstandingMessages += "\nFile copied..."
                    + "\nFrom: " + scriptSourcePath
                    + "\nTo: " + scriptTargetPath + "\n";
        } catch (IOException e) {
            outstandingMessages += "\n"
                    + "An IOException prevented the script from being copied..."
                    + "\nFrom: " + scriptSourcePath
                    + "\nTo: " + scriptTargetPath
                    + "\n";
        }
        // copy the input configuration file
        try {
            if (simConfigSourcePath != null && simConfigTargetPath != null) {
                copyStartTime = new Date();
                FileManager.copyFile(simConfigSourcePath, simConfigTargetPath);
                // record file copy provenance for sim config file
                if (provMgr != null) {
                    Long startTime = System.currentTimeMillis();
                    WorkbenchOperationRecorder.copyFile(provMgr, simConfigSourcePath.toString(),
                            simConfigTargetPath.toString(), "simulationConfigurationFile",
                            "copy_SimConfig", copyStartTime, new Date());
                    accumulatedTime = DateTime.sumProvTiming(startTime, accumulatedTime);
                }
                outstandingMessages += "\nFile copied..."
                        + "\nFrom: " + simConfigSourcePath
                        + "\nTo: " + simConfigTargetPath + "\n";
            }
        } catch (IOException e) {
            outstandingMessages += "\n"
                    + "An IOException prevented the simulation configuration "
                    + "file from being copied"
                    + "\nFrom: " + simConfigSourcePath
                    + "\nTo :" + simConfigTargetPath
                    + "\n";
        }
        try {
            // copy the neuron list files specified in the config file
            if (nListSourcePaths != null && nListTargetPaths != null) {
                for (int i = 0, im = nListSourcePaths.length; i < im; i++) {
                    copyStartTime = new Date();
                    FileManager.copyFile(nListSourcePaths[i], nListTargetPaths[i]);
                    if (provMgr != null) {
                        Long startTime = System.currentTimeMillis();
                        WorkbenchOperationRecorder.copyFile(provMgr, nListSourcePaths[i].toString(),
                                nListTargetPaths[i].toString(), "nlist", "copy_NList_" + i,
                                copyStartTime, new Date());
                        accumulatedTime = DateTime.sumProvTiming(startTime, accumulatedTime);
                    }
                    outstandingMessages += "\nFile copied..."
                            + "\nFrom: " + nListSourcePaths[i]
                            + "\nTo: " + nListTargetPaths[i]
                            + "\n";
                }
            }
        } catch (IOException e) {
            outstandingMessages += "\n" + "An IOException prevented the following files: ";
            for (int i = 0, im = inputFilenames.length; i < im; i++) {
                if (i == im - 1 && i > 0) {
                    outstandingMessages += "and " + inputFilenames[i];
                } else {
                    outstandingMessages += inputFilenames[i] + ",\n";
                }
            }
            outstandingMessages += "\nfrom being copied to: " + nListDir.toString();
        }
        String cmd = "sh " + scriptTargetPath.toString();
        // run the script
        try {
            LOG.info("Running in Console: " + cmd);
            Runtime.getRuntime().exec(cmd);
        } catch (SecurityException e) {
            success = false;
            outstandingMessages += "\n"
                    + "A SecurityException prevented the script from execution"
                    + "\nAt: " + scriptTargetPath
                    + "\n";
            LOG.info(e.getMessage());
        } catch (IOException e) {
            success = false;
            outstandingMessages += "\n"
                    + "An input/output error occurred while executing: "
                    + "\n" + scriptTargetPath
                    + "\n";
            e.printStackTrace();
            LOG.info(e.getMessage());
        }
        DateTime.recordFunctionExecutionTime("ScriptManager", "runLocalScript",
                System.currentTimeMillis() - functionStartTime, provMgr != null);
        if (provMgr != null) {
            DateTime.recordAccumulatedProvTiming("ScriptManager", "runLocalScript",
                    accumulatedTime);
        }
        if (success) {
            LOG.info("Script " + scriptTargetPath + " Ran Successfully");
        } else {
            LOG.info("Script " + scriptTargetPath + " Failed");
        }

        return success;
    }

    /**
     * Analyzes script output for provenance data. Relays that data to the provenance manager.
     *
     * Note: This class defines the context between provenance data. The provenance manager is used
     * to connect such data as determined by this function. This means that if script generation
     * changes this function may need to change, in turn, and vice-versa.
     *
     * @param simSpec  Specification used to indicate the context in which the simulation was
     *                 specified when the script was generated
     * @param simulation  The simulation to be analyzed
     * @param provMgr  Provenance manager used to create provenance based on analysis of the printf
     *                 output
     * @param outputTargetFolder  Location to store the redirected printf output
     * @return Time completed in seconds since the epoch, or an error code indicating that the
     *         script has not completed
     * @throws com.jcraft.jsch.JSchException
     * @throws com.jcraft.jsch.SftpException
     * @throws java.io.IOException
     */
    public long analyzeScriptOutput(SimulationSpecification simSpec, Simulation simulation,
            ProvMgr provMgr, Path outputTargetFolder) throws JSchException, SftpException,
            IOException {
        Long functionStartTime = System.currentTimeMillis();
        Long accumulatedTime = 0L;
        long timeCompleted = DateTime.ERROR_TIME;
        // get all the files produced by the script
        String scriptStatusFilename = fetchScriptOutputFiles(simulation, simSpec,
                outputTargetFolder);
        if (scriptStatusFilename != null) {
            OutputAnalyzer analyzer = new OutputAnalyzer();
            analyzer.analyzeOutput(scriptStatusFilename);
            /* Completed */
            String simExec = simSpec.getSimExecutable();
            timeCompleted = analyzer.completedAt(simExec);
            if (timeCompleted != DateTime.ERROR_TIME && provMgr != null) {
                Long startTime = System.currentTimeMillis();
                /* Set Remote Namespace Prefix */
                if (simSpec.isRemote()) {
                    provMgr.setNsPrefix("remote", simSpec.getHostAddr());
                }
                /* Simulation */
                //TODO: use a different condition to check sim success that does not require a local
                //  copy of the results file
                ExecutedCommand sim = analyzer.getFirstCommand(simExec);
                Path resultFilePath = simulation.getSimulationLocation()
                        .resolve(simulation.getSimResultFile());
                boolean simSuccessful = Files.exists(resultFilePath);
                if (sim != null && simSuccessful) {
                    String simulationDir = FileManager.getSimulationsDirectory() + "/"
                            + simulation.getName();
                    Resource location = provMgr.addLocation(simSpec.getHostAddr(), null, false,
                            simSpec.isRemote(), false);
                    // get agent resource
                    String uri = simSpec.getSimulatorFolder() + "/" + simExec;
                    Resource simAgent = provMgr.addSoftwareAgent(uri, "simulator",
                            simSpec.isRemote(), false);
                    // get activity resource
                    Resource simActivity = provMgr.addActivity("simulation_" + UUID.randomUUID(),
                            "simulation", simSpec.isRemote(), false);
                    // connect the two
                    provMgr.wasAssociatedWith(simActivity, simAgent);
                    provMgr.startedAtTime(simActivity, new Date(analyzer.startedAt(simExec)));
                    provMgr.endedAtTime(simActivity, new Date(timeCompleted));
                    String resultFile = simulationDir + "/" + simulation.getSimResultFile();
                    // add entity for simulation result file, don't replace if exists
                    Resource simResultFile = provMgr.addEntity(resultFile, "simResult",
                            simSpec.getHostAddr(), simSpec.getUsername(), "sftp", false);
                    provMgr.atLocation(simResultFile, location);
                    // show that the output was generated by the simulation
                    provMgr.addFileGeneration(simActivity, simAgent, simResultFile);
                    // show that the inputs were used in the simulation
                    String remoteSimConfigFilename = simulationDir
                            + "/configfiles/"
                            + FileManager.getSimpleFilename(simulation.getSimConfigFilename());
                    Resource remoteSimConfigFileEntity = provMgr.addEntity(remoteSimConfigFilename,
                            "simulationConfigurationFile", simSpec.getHostAddr(),
                            simSpec.getUsername(), "sftp", false);
                    provMgr.atLocation(remoteSimConfigFileEntity, location);

                    provMgr.used(simActivity, remoteSimConfigFileEntity);
                    String[] neuronLists = FileManager.getNeuronListFilenames(simulation.getName());
                    for (String neuronList : neuronLists) {
                        String movedNLFilename = simulationDir
                                + "/configfiles/NList/"
                                + FileManager.getSimpleFilename(neuronList);
                        Resource movedNLFileEntity = provMgr.addEntity(movedNLFilename, "nlist",
                                simSpec.getHostAddr(), simSpec.getUsername(), "sftp", false);
                        provMgr.atLocation(movedNLFileEntity, location);
                        provMgr.used(simActivity, movedNLFileEntity);
                    }
                    // get the sha1key from the file if possible
                    String sha1KeyFilename = simulation.getName() + "_" + Script.SHA1_KEY_FILENAME;
                    Path sha1KeyFilePath = outputTargetFolder.resolve(sha1KeyFilename);
                    if (Files.exists(sha1KeyFilePath)) {
                        // open the file
                        Scanner fileReader;
                        String sha1key;
                        /* Stage Error Handling */
                        try { // try to start reading from the given file path
                            fileReader = new Scanner(new FileReader(sha1KeyFilePath.toFile()));
                            if (fileReader.hasNext()) {
                                // read the line to create a revision entity
                                sha1key = fileReader.nextLine();
                                if (!sha1key.contains("fatal")) {
                                    String codeLocation = simSpec.getCodeLocation();
                                    provMgr.wasDerivedFrom(simAgent, provMgr.addEntity(
                                            codeLocation.substring(codeLocation.indexOf(":") + 1,
                                            codeLocation.lastIndexOf(".")) + "/commit/" + sha1key,
                                            "commit", null, null, "https", false));
                                }
                            }
                        } catch (FileNotFoundException e) {
                            System.err.println("File not found: " + sha1KeyFilePath);
                        }
                    }
                }
                // collect output file and standard output redirect file
                accumulatedTime = DateTime.sumProvTiming(startTime, accumulatedTime);
            }
            DateTime.recordFunctionExecutionTime("ScriptManager", "analyzeScriptOutput",
                    System.currentTimeMillis() - functionStartTime, provMgr != null);
            if (provMgr != null) {
                DateTime.recordAccumulatedProvTiming("ScriptManager", "analyzeScriptOutput",
                        accumulatedTime);
            }
        }
        return timeCompleted;
    }
    
  private String makeLastSimulationDir() {
    String lastSim = System.getProperty("user.dir") + "\\LastSimulation";
    File lastSimDirectory = new File(lastSim);
    if (!lastSimDirectory.exists() || !lastSimDirectory.isDirectory()) {
      lastSimDirectory.mkdir();
    }
    return lastSim;
  }

    private String fetchScriptOutputFiles(Simulation simulation, SimulationSpecification simSpec,
            Path scriptOutputFolder) throws JSchException, SftpException, IOException {
        String filename = null;
        char[] password = null;
        // determine script output filenames
        String simulationName = simulation.getName();
        String scriptStatusFilename = simulationName + "_" + Script.SCRIPT_STATUS_FILENAME;
        String simStatusFilename = simulationName + "_" + Script.SIM_STATUS_FILENAME;
        String sha1KeyFilename = simulationName + "_" + Script.SHA1_KEY_FILENAME;
        String cmdFilename = simulationName + "_" + Script.COMMAND_OUTPUT_FILENAME;
        // determine script output file target paths
        Path scriptStatusFileTarget = scriptOutputFolder.resolve(scriptStatusFilename);
        Path simStatusFileTarget = scriptOutputFolder.resolve(simStatusFilename);
        Path sha1KeyFileTarget = scriptOutputFolder.resolve(sha1KeyFilename);
        // prep folder for simulation results
        Path simResultsFolder = simulation.getSimulationLocation().resolve("results");
        Files.createDirectories(simResultsFolder);
        // determine simulation result file target path
        Path resultFileTarget = simResultsFolder.getParent().resolve(simulation.getSimResultFile());
        //TODO: remove getParent from above after removing path from resultFileName

        // run simulation here or on another machine?
        boolean remote = simSpec.isRemote();
        if (remote) {
            // determine remote script output file paths
            String scriptOutputDir = FileManager.getSimulationsDirectory() + "/" + simulationName;
            String scriptStatusFileRemote = scriptOutputDir + "/" + scriptStatusFilename;
            String simStatusFileRemote = scriptOutputDir + "/" + simStatusFilename;
            String sha1KeyFileRemote = scriptOutputDir + "/" + sha1KeyFilename;
            String resultFileRemote = scriptOutputDir + "/" + simulation.getSimResultFile();
            // download script status file
            SecureFileTransfer sft = new SecureFileTransfer();
            String hostname = simSpec.getHostAddr();
            LoginCredentialsDialog lcd = new LoginCredentialsDialog(hostname, true);
            password = lcd.getPassword();
            simSpec.setUsername(lcd.getUsername());
            lcd.clearPassword();
            outstandingMessages += "\nDownloading script status file:\n" + scriptStatusFilename
                    + "\nFrom: " + hostname + "\n";
            if (sft.downloadFile(scriptStatusFileRemote, scriptStatusFileTarget.toString(),
                    hostname, lcd.getUsername(), password)) {
                outstandingMessages += "\nDownloading simulation status file:\n"
                        + simStatusFilename
                        + "\nFrom: " + hostname + "\n";
                try {
                    // download simulation stdout redirect file
                    sft.downloadFile(simStatusFileRemote, simStatusFileTarget.toString(), hostname,
                            lcd.getUsername(), password);
                    outstandingMessages += "\nLatest output from simulation:\n"
                            + getLastLine(simStatusFileTarget)
                            + "\n";
                } catch (SftpException e) {
                    outstandingMessages += "\nDownload failed for: "
                            + simStatusFilename
                            + "\n";
                }
                if (scriptComplete(scriptStatusFileTarget.toString(), simSpec)) {
                    // track the file to analyze
                    filename = scriptStatusFileTarget.toString();
                    outstandingMessages += "\nDownloading simulation result file: \n"
                            + resultFileRemote
                            + "\nFrom: " + hostname + "\n";
                    try {
                        // download sim result file
                        sft.downloadFile(resultFileRemote, resultFileTarget.toString(),
                                hostname, lcd.getUsername(), password);
                    } catch (SftpException e) {
                        outstandingMessages += "\nDownload failed for: "
                                + resultFileRemote
                                + "\n";
                    }
                    // download SHA1 key file
                    outstandingMessages += "\nDownloading simulator source code version report:\n"
                            + sha1KeyFilename
                            + "\nFrom: " + hostname + "\n";
                    try {
                        sft.downloadFile(sha1KeyFileRemote, sha1KeyFileTarget.toString(),
                                hostname, lcd.getUsername(), password);
                    } catch (SftpException e) {
                        outstandingMessages += "\nDownload failed for: "
                                + sha1KeyFilename
                                + "\n";
                    }
                }
            }
        } else {
            // get script printf redirect output file
            String localSimsDir = FileManager.getSimulationsDirectory()
                    .replaceFirst("^~", System.getProperty("user.home"));
            Path scriptOutput = Paths.get(localSimsDir, simulationName);
            Path scriptStatusFileSource = scriptOutput.resolve(scriptStatusFilename);
            outstandingMessages += "\nCopying script status file..."
                    + scriptStatusFilename
                    + "\nFrom: " + scriptStatusFileSource.toString()
                    + "\nTo: " + scriptStatusFileTarget.toString()
                    + "\n";
            if (FileManager.copyFile(scriptStatusFileSource, scriptStatusFileTarget)) {
                // get simulation stdout redirect file
                Path simStatusFileSource = scriptOutput.resolve(simStatusFilename);
                outstandingMessages += "\nCopying simulation status file..."
                        + simStatusFilename
                        + "\nFrom: " + simStatusFileSource.toString()
                        + "\nTo: " + simStatusFileTarget.toString()
                        + "\n";
                try {
                    FileManager.copyFile(simStatusFileSource, simStatusFileTarget);
                    outstandingMessages += "\nLatest output from simulation:\n"
                            + getLastLine(simStatusFileTarget)
                            + "\n";
                } catch (IOException e) {
                    outstandingMessages += "\nSimulation status copy operation failed: \n"
                            + e.getLocalizedMessage() + "\n";
                }
                // if the script is finished
                if (scriptComplete(scriptStatusFileTarget.toString(), simSpec)) {
                    // track the file to analyze
                    filename = scriptStatusFileTarget.toString();
                    Path resultFileSource = scriptOutput.resolve(simulation.getSimResultFile());
                    outstandingMessages += "\nCopying simulation result file..."
                            + simulation.getSimResultFile()
                            + "\nFrom: " + resultFileSource.toString()
                            + "\nTo: " + resultFileTarget.toString()
                            + "\n";
                    try {
                        FileManager.copyFile(resultFileSource, resultFileTarget);
                    } catch (IOException e) {
                        outstandingMessages += "\nSimulation result file copy operation failed: \n"
                                + e.getLocalizedMessage() + "\n";
                    }
                    Path sha1KeyFileSource = scriptOutput.resolve(sha1KeyFilename);
                    outstandingMessages += "\nCopying simulator source code version report:\n"
                            + sha1KeyFilename
                            + "\nFrom: " + sha1KeyFileSource.toString()
                            + "\nTo: " + sha1KeyFileTarget.toString()
                            + "\n";
                    try {
                        FileManager.copyFile(sha1KeyFileSource, sha1KeyFileTarget);
                    } catch (IOException e) {
                        outstandingMessages +=
                                "\nSimulator source code version report copy operation failed: \n"
                                + e.getLocalizedMessage() + "\n";
                    }
                }
            }
        }
        if (password != null) {
            Arrays.fill(password, '0');
        }
        return filename;
    }

    private boolean scriptComplete(String localScriptOutputFilename,
            SimulationSpecification simSpec) {
        long timeCompleted;
        OutputAnalyzer analyzer = new OutputAnalyzer();
        analyzer.analyzeOutput(localScriptOutputFilename);
        String simExec = simSpec.getSimExecutable();
        timeCompleted = analyzer.completedAt(simExec);
        return timeCompleted != DateTime.ERROR_TIME;
    }

    /**
     * Gets the messages that have accumulated within a public function call. The accumulated
     * messages are discarded during this call.
     *
     * @return The messages that have accumulated since the last call to this function in the form
     *         of a single String.
     */
    public String getOutstandingMessages() {
        String msg = outstandingMessages;
        outstandingMessages = "";
        return msg;
    }

    public static String getLastLine(Path filePath) {
        String lastLine = "";
        if (Files.exists(filePath)) {
            // open the file
            Scanner fileReader;
            /* Stage Error Handling */
            try { // try to start reading from the given file path
                fileReader = new Scanner(new FileReader(filePath.toFile()));
                while (fileReader.hasNext()) {
                    lastLine = fileReader.nextLine();
                }
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + filePath);
            }
        }
        return lastLine;
    }

    /**
     * Utility function that replaces the tilde (~) character at the beginning of a Posix path with
     * "$HOME". This is provided for the purpose of variable interpolation when the path is used in
     * a bash script and enclosed by double quotes.
     *
     * @param path  The path string beginning with "~/"
     * @return A new path string beginning with "$HOME"
     */
    public static String replaceTildeWithHome(String path) {
        if (path.startsWith("~/")) {
            return "$HOME" + path.substring(1);
        }
        return path;
    }
}
