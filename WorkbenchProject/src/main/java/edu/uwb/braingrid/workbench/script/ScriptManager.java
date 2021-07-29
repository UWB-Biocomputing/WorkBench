package edu.uwb.braingrid.workbench.script;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import edu.uwb.braingrid.provenance.workbenchprov.WorkbenchOperationRecorder;
import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbench.comm.SecureFileTransfer;
import edu.uwb.braingrid.workbench.data.InputAnalyzer;
import edu.uwb.braingrid.workbench.data.OutputAnalyzer;
import edu.uwb.braingrid.workbench.model.ExecutedCommand;
import edu.uwb.braingrid.workbench.model.SimulationSpecification;
import edu.uwb.braingrid.workbench.project.ProjectMgr;
//import edu.uwb.braingrid.workbench.project.ProjectManager;
import edu.uwb.braingrid.workbench.ui.LoginCredentialsDialog;
import edu.uwb.braingrid.workbench.utils.DateTime;
import edu.uwb.braingrid.workbenchdashboard.utils.SystemProperties;

/**
 * Manages script creation, script execution, and script output analysis.
 *
 * @author Del Davis, extended by Joseph Conquest
 */
public class ScriptManager {

    private static final Logger LOG = Logger.getLogger(ScriptManager.class.getName());

    private String outstandingMessages;
    //private ProjectManager projectManager = new ProjectManager();

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
     * @param projectName  Name of the project that generated the script
     * @param version  The version of the script (used in tracing output back to the script that
     *                 printed the output)
     * @param simSpec  The specification for the simulator to execute in the script
     * @param simConfigFilename  Name of configuration file used as simulation input (XML file that
     *                           specifies simulation parameters, environment constants, and
     *                           simulated spatial information (or names of files which contain such
     *                           spatial data). This is the file that is constructed using the
     *                           simulation specification dialog).
     * @return A constructed script or null in the case that the script could not be constructed
     *         properly
     */
    public static Script generateScript(String projectName, String version,
            SimulationSpecification simSpec, String simConfigFilename) {
        boolean success;
        String scriptOutputDir = FileManager.getSimulationsDirectory() + "/" + projectName;
        // make path safe for variable interpolation
        scriptOutputDir = replaceTildeWithHome(scriptOutputDir);
        // convert file path to simple file name
        String configFilename = FileManager.getSimpleFilename(simConfigFilename);
        // create a new script
        Script script = new Script();
        script.setScriptOutputDirectory(scriptOutputDir);
        script.setCmdOutputFilename(projectName
                + "_v"
                + version
                + "_"
                + Script.COMMAND_OUTPUT_FILENAME);
        script.setScriptStatusOutputFilename(projectName
                + "_v"
                + version
                + "_"
                + Script.SCRIPT_STATUS_FILENAME);
        /* Print Header Data */
        script.printf(Script.VERSION_TEXT, version, false);
        // determine which simulator file to execute
        String type = simSpec.getSimulationType();
        String simExecutableToInvoke;
        simExecutableToInvoke = SimulationSpecification.getSimFilename(type);
        success = simExecutableToInvoke != null;
        printfSimSpecToScript(script, simExecutableToInvoke, configFilename, true);
        /* Prep From ProjectMgr Data */
        // simulator build and execute location
        String simFolder = simSpec.getSimulatorFolder();
        // make path safe for variable interpolation
        simFolder = replaceTildeWithHome(simFolder);
        // central repository location
        String repoURI = simSpec.getCodeLocation();
        // git pull or do we assume it exists as is?
        String simulatorSourceCodeUpdatingType = simSpec.getSourceCodeUpdating();
        boolean updateRepo = false;
        if (simulatorSourceCodeUpdatingType != null) {
            updateRepo = simulatorSourceCodeUpdatingType.equals(
                    SimulationSpecification.GIT_PULL_AND_CLONE);
        }
        /* Create Script */
        // add a mkdir that will create intermediate directories
        String[] argsForMkdir = {"-p", simFolder};
        script.executeProgram("mkdir", argsForMkdir);
        // do a pull?
        if (updateRepo) {
            // first do a clone and maybe fail
            String[] gitCloneArgs = {"clone", repoURI, simFolder};
            script.executeProgram("git", gitCloneArgs);

            // change directory to do a pull
            // note: unnecessary with git 1.85 or higher, but git hasn't been
            // updated in quite some time on the UWB linux binaries :(
            String[] cdArg = {simFolder};
            script.executeProgram("cd", cdArg);

            // then do a pull and maybe fail (one of the two will work)
            String[] gitPullArgs = {"pull"};
            script.executeProgram("git", gitPullArgs);
            if (simSpec.hasCommitCheckout()) {
                String[] gitCheckoutSHA1Key = {"checkout", simSpec.getSHA1CheckoutKey()};
                script.executeProgram("git", gitCheckoutSHA1Key);
            }
        } else {
            String[] cdArg = {simFolder};
            script.executeProgram("cd", cdArg);
        }
        // record the latest commit key information
        script.addVerbatimStatement("git log --pretty=format:'%H' -n 1",
                scriptOutputDir + "/"
                + projectName
                + "_v"
                + version
                + "_"
                + Script.SHA1_KEY_FILENAME, false);
        /* Make the Simulator */
        // clean previous build
        if (simSpec.buildFirst()) {
            String[] cleanMakeArgs = {"-s", "clean"};
            script.executeProgram("make", cleanMakeArgs);
            // compile without hdf5
            String[] makeArgs = {"-s", simExecutableToInvoke, "CUSEHDF5='no'"};
            script.executeProgram("make", makeArgs);
        }
        // change directory to script output location
        String[] cdArg = {scriptOutputDir};
        script.executeProgram("cd", cdArg);
        /* Make Results Folder */
        String[] mkResultsDirArgs = {"results"};
        script.executeProgram("mkdir", mkResultsDirArgs);
        /* Run the Simulator */
        script.addVerbatimStatement(simFolder + "/"
                + simExecutableToInvoke
                + " -t "
                + "configfiles/" + configFilename,
                projectName
                + "_v"
                + version
                + "_"
                + Script.SIM_STATUS_FILENAME, true);
        /* Put Script Together and Save */
        if (!success || !script.construct()) {
            script = null; // or indicate unsuccessful operation
        }
        return script;
    }

    /**
     * Provides a string representation of the path to the folder where script related files are
     * stored.
     *
     * @param projectFolder  A string representation of the path to the folder where projects are
     *                       stored (in relation to where the workbench was invoked this is:
     *                       ./projects/the_project_name/scripts/. However, this is system
     *                       dependent)
     * @return A string representation of the path to the folder where script related files are
     *         stored. Depending on the form of the project folder provided, this may represent a
     *         relative path.
     */
    public static Path getScriptFolder(Path projectFolder) {
        return projectFolder.resolve("scripts");
    }

    /**
     * Runs the script as specified in the associated simulation specification.
     *
     * @param provMgr  Manages provenance. Used to record operations executed from script.
     * @param simSpec  Holds information about the simulation
     * @param projectName
     * @param scriptPath  Indicates where the constructed script to execute resides on the file
     *                    system.
     * @param scriptVersion
     * @param nListFilenames  Neuron lists indicated in simulation configuration file
     * @param simConfigFilename  Name of file containing simulation constants and filenames. File,
     *                           itself, is used to specify all input for a simulation.
     * @return True if all operations associated with running the script were successful, otherwise
     *         false. Note: This does not indicate whether the simulation ran successfully.
     * @throws com.jcraft.jsch.JSchException
     * @throws com.jcraft.jsch.SftpException
     * @throws java.io.IOException
     */
    public boolean runScript(ProvMgr provMgr, SimulationSpecification simSpec, String projectName,
            String scriptPath, String scriptVersion, String[] nListFilenames,
            String simConfigFilename) throws JSchException, SftpException, IOException {
        boolean success;
        String executionMachine = simSpec.getSimulationLocale();
        String remoteExecution = SimulationSpecification.REMOTE_EXECUTION;
        // run script remotely?
        if (executionMachine.equals(remoteExecution)) {
            LOG.info("Running Remote Script: " + scriptPath);
            success = runRemoteScript(provMgr, simSpec.getHostAddr(), projectName, scriptPath,
                    scriptVersion, nListFilenames, simConfigFilename);
        } else { // or run it locally
            LOG.info("Running Local Script " + scriptPath);
            success = runLocalScript(provMgr, projectName, scriptPath, scriptVersion,
                    nListFilenames, simConfigFilename);
        }
        return success;
    }

    private boolean runRemoteScript(ProvMgr provMgr, String hostname, String projectName,
            String scriptPath, String scriptVersion, String[] nListFilenames,
            String simConfigFilename) throws JSchException, FileNotFoundException, SftpException {
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
            String simDir = FileManager.getSimulationsDirectory() + "/" + projectName;
            String configDir = simDir + "/configfiles";
            String nListDir = simDir + "/configfiles/NList";
            String remoteScriptPath = simDir + "/" + FileManager.getSimpleFilename(scriptPath);
            String cmd = "mkdir -p " + nListDir;
            sft.executeCommand(cmd, hostname, lcd.getUsername(), password, true);
            /* Upload Script */
            Date uploadStartTime = new Date();
            if (sft.uploadFile(scriptPath, simDir, hostname, lcd.getUsername(), password, null)) {
                // record provenance of upload
                if (provMgr != null) {
                    Long startTime = System.currentTimeMillis();
                    WorkbenchOperationRecorder.uploadFile(provMgr, scriptPath, remoteScriptPath,
                            "script", hostname, "uploadScript_v" + scriptVersion, uploadStartTime,
                            new Date());
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
                                WorkbenchOperationRecorder.uploadFile(provMgr, nListFilename,
                                        nListDir + FileManager.getSimpleFilename(nListFilename),
                                        "nlist", hostname, "upload_" + nlType + "_NList_"
                                                + "for_Script_v" + scriptVersion,
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
                            WorkbenchOperationRecorder.uploadFile(provMgr, simConfigFilename,
                                    configDir + FileManager.getSimpleFilename(simConfigFilename),
                                    "simulationConfigurationFile", hostname,
                                    "upload_SimConfig_for_Script_v" + scriptVersion,
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

    private boolean runLocalScript(ProvMgr provMgr, String projectName, String scriptLocation,
            String scriptVersion, String[] inputFilenames, String simConfigFilename)
            throws IOException {
        LOG.info("Running Local Script");
        Long functionStartTime = System.currentTimeMillis();
        Long accumulatedTime = 0L;
        boolean success = true;
        Date copyStartTime;
        // create simulation directory and subdirectories
        String localSimsDir = FileManager.getSimulationsDirectory()
                .replaceFirst("^~", System.getProperty("user.home"));
        Path simDir = Paths.get(localSimsDir, projectName);
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
                        scriptTargetPath.toString(), "script", "copy_Script_v" + scriptVersion,
                        copyStartTime, new Date());
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
                            "copy_SimConfig_forScript_v", copyStartTime, new Date());
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
                        WorkbenchOperationRecorder.copyFile(provMgr,
                                nListSourcePaths[i].toString(),
                                nListTargetPaths[i].toString(), "nlist",
                                "copy_NList_" + i + "forScript_v"
                                + scriptVersion, copyStartTime, new Date());
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
//        String oldWrkDir = System.getProperty("user.dir");
//        System.setProperty("user.dir", System.getProperty("user.home"));
        String cmd = "sh " + scriptTargetPath.toString();
        // run the script
        try {
            if (Desktop.isDesktopSupported() && SystemProperties.isWindowsSystem()) {
                Desktop dt = Desktop.getDesktop();
               //  dt.open(scriptTargetPath.toFile());
                Runtime.getRuntime().exec("cmd.exe /c start " + cmd); // Windows
            } else {
                LOG.info("Running in Console: " + cmd);
                Runtime.getRuntime().exec(cmd); // Unix
            }
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
//        } finally {
//            System.setProperty("user.dir", oldWrkDir);
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
     * @param projectMgr
     * @param provMgr  Provenance manager used to create provenance based on analysis of the printf
     *                 output
     * @param outputTargetFolder  Location to store the redirected printf output
     * @return Time completed in seconds since the epoch, or an error code indicating that the
     *         script has not completed
     * @throws com.jcraft.jsch.JSchException
     * @throws com.jcraft.jsch.SftpException
     * @throws java.io.IOException
     */
    public long analyzeScriptOutput(SimulationSpecification simSpec, ProjectMgr projectMgr,
            ProvMgr provMgr, Path outputTargetFolder) throws JSchException, SftpException,
            IOException {
        Long functionStartTime = System.currentTimeMillis();
        Long accumulatedTime = 0L;
        long timeCompleted = DateTime.ERROR_TIME;
        // get all the files produced by the script
        String scriptStatusFilename = fetchScriptOutputFiles(projectMgr, simSpec,
                outputTargetFolder);
        if (scriptStatusFilename != null) {
            OutputAnalyzer analyzer = new OutputAnalyzer();
            analyzer.analyzeOutput(scriptStatusFilename);
            /* Completed */
            long atTime;
            String simExec = simSpec.getSimExecutable();
            atTime = analyzer.completedAt(simExec);
            timeCompleted = atTime;
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
                Path resultFilePath = projectMgr.getProjectLocation()
                        .resolve(projectMgr.getSimResultFile());
                boolean simSuccessful = Files.exists(resultFilePath);
                if (sim != null && simSuccessful) {
                    String simulationDir = FileManager.getSimulationsDirectory() + "/"
                            + projectMgr.getName();
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
                    provMgr.endedAtTime(simActivity, new Date(atTime));
                    String resultFile = simulationDir + "/" + projectMgr.getSimResultFile();
                    // add entity for simulation result file, don't replace if exists
                    Resource simResultFile = provMgr.addEntity(resultFile, "simResult",
                            simSpec.getHostAddr(), simSpec.getUsername(), "sftp", false);
                    provMgr.atLocation(simResultFile, location);
                    // show that the output was generated by the simulation
                    provMgr.addFileGeneration(simActivity, simAgent, simResultFile);
                    // show that the inputs were used in the simulation
                    String remoteSimConfigFilename = simulationDir
                            + "/configfiles/"
                            + FileManager.getSimpleFilename(projectMgr.getSimConfigFilename());
                    Resource remoteSimConfigFileEntity = provMgr.addEntity(remoteSimConfigFilename,
                            "simulationConfigurationFile", simSpec.getHostAddr(),
                            simSpec.getUsername(), "sftp", false);
                    provMgr.atLocation(remoteSimConfigFileEntity, location);

                    provMgr.used(simActivity, remoteSimConfigFileEntity);
                    String[] neuronLists = FileManager.getNeuronListFilenames(projectMgr.getName());
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
                    String sha1KeyFilename = projectMgr.getName()
                            + "_v"
                            + projectMgr.getScriptVersion()
                            + "_"
                            + Script.SHA1_KEY_FILENAME;
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
        //LEAVE THIS COMMENTED CODE (BELOW): THIS IS AUTOMATED PROV COLLECTION//
//                String scriptName = Script.getFilename(
//                        analyzer.getScriptVersion());
//                SimulationSpecification spec = analyzer.getSimSpec();
//                List<ExecutedCommand> allCommandsList = null;
//                Collection<ExecutedCommand> allCommands
//                        = analyzer.getAllCommands();
//                if (allCommands != null) {
//                    allCommandsList = new ArrayList(allCommands);
//                }
//                if (allCommandsList != null) {
//                    for (ExecutedCommand ec : allCommandsList) {
//                        //System.err.println(ec);
//                    }
//                }
                ///////////////////LEAVE COMMENTED CODE (ABOVE)/////////////////
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

    private String fetchScriptOutputFiles(ProjectMgr projectMgr, SimulationSpecification simSpec,
            Path scriptOutputFolder) throws JSchException, SftpException, IOException {
        String filename = null;
        char[] password = null;
        // determine script output filenames
        String projectName = projectMgr.getName();
        String version = projectMgr.getScriptVersion();
        String scriptStatusFilename = projectName
                + "_v"
                + version
                + "_"
                + Script.SCRIPT_STATUS_FILENAME;
        String simStatusFilename = projectName
                + "_v"
                + projectMgr.getScriptVersion()
                + "_"
                + Script.SIM_STATUS_FILENAME;
        String sha1KeyFilename = projectName
                + "_v"
                + version
                + "_"
                + Script.SHA1_KEY_FILENAME;
        String cmdFilename = projectName
                + "_v"
                + version
                + "_"
                + Script.COMMAND_OUTPUT_FILENAME;
        // determine script output file target paths
        Path scriptStatusFileTarget = scriptOutputFolder.resolve(scriptStatusFilename);
        Path simStatusFileTarget = scriptOutputFolder.resolve(simStatusFilename);
        Path sha1KeyFileTarget = scriptOutputFolder.resolve(sha1KeyFilename);
        // prep folder for simulation results
        Path simResultsFolder = projectMgr.getProjectLocation().resolve("results");
        Files.createDirectories(simResultsFolder);
        // determine simulation result file target path
        Path resultFileTarget = simResultsFolder.getParent().resolve(projectMgr.getSimResultFile());
        //TODO: remove getParent from above after removing path from resultFileName

        // run simulation here or on another machine?
        boolean remote = simSpec.isRemote();
        if (remote) {
            // determine remote script output file paths
            String scriptOutputDir = FileManager.getSimulationsDirectory() + "/" + projectName;
            String scriptStatusFileRemote = scriptOutputDir + "/" + scriptStatusFilename;
            String simStatusFileRemote = scriptOutputDir + "/" + simStatusFilename;
            String sha1KeyFileRemote = scriptOutputDir + "/" + sha1KeyFilename;
            String resultFileRemote = scriptOutputDir + "/" + projectMgr.getSimResultFile();
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
            Path scriptOutput = Paths.get(localSimsDir, projectName);
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
                    Path resultFileSource = scriptOutput.resolve(projectMgr.getSimResultFile());
                    outstandingMessages += "\nCopying simulation result file..."
                            + projectMgr.getSimResultFile()
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

    private static void printfSimSpecToScript(Script script, String simFile,
            String simInputFilename, boolean append) {
        script.printf(SimulationSpecification.SIM_EXEC_TEXT, simFile, append);
        String joinedInputs = simInputFilename;
        script.printf(SimulationSpecification.SIM_INPUTS_TEXT, joinedInputs, true);
        // printf the outputs
        script.printf(SimulationSpecification.SIM_OUTPUTS_TEXT, "output.xml", true);
        // printf the end tag for the sim spec data
        script.printf(SimulationSpecification.END_SIM_SPEC_TEXT, "", true);
    }

    public static String getScriptName(String projectName, String version) {
        return projectName + "_script" + version;
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
