package edu.uwb.braingrid.workbench.script;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

/**
 * Manages script creation, script execution, and script output analysis.
 *
 * @author Del Davis, extended by Joseph Conquest
 */
public class ScriptManager {

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
        FileManager fileMgr = FileManager.getFileManager();
        String userDir = fileMgr.getUserDir();
        String folderDelimiter = fileMgr.getFolderDelimiter();
        if (simSpec.isRemote()) {
            userDir = "~/";
            folderDelimiter = "/";
        }
        // convert file path to simple file name
        String configFilename = FileManager.getSimpleFilename(simConfigFilename);
        // create a new script
        Script script = new Script();
        script.setCmdOutputFilename(projectName
                + "_v"
                + version
                + "_"
                + Script.COMMAND_OUTPUT_FILENAME);
        script.setScriptStatusOutputFilename(projectName
                + "_v"
                + version
                + "_"
                + Script.DEFAULT_SCRIPT_STATUS_FILENAME);
        /* Print Header Data */
        script.printf(Script.VERSION_TEXT, version, false);
        // determine which simulator file to execute
        String type = simSpec.getSimulationType();
        String simExecutableToInvoke;
        simExecutableToInvoke = SimulationSpecification.getSimFilename(type);
        success = simExecutableToInvoke != null;
        printfSimSpecToScript(script, simExecutableToInvoke, configFilename, true);
        /* Prep From ProjectMgr Data */
        // change directory for compilation and simulation execution
        String simFolder = simSpec.getSimulatorFolder();
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
        if (simSpec.isRemote()) {
            String[] argsForMkdir = {"-p", simFolder};
            script.executeProgram("mkdir", argsForMkdir);
        } else {
            String[] argsForMkdir = {"-p", userDir + simFolder};
            script.executeProgram("mkdir", argsForMkdir);
        }
        // do a pull?
        if (updateRepo) {
            if (simSpec.isRemote()) {
                // first do a clone and maybe fail
                String[] gitCloneArgs = {"clone", repoURI, simFolder};
                script.executeProgram("git", gitCloneArgs);
                // change directory to do a pull
                // note: unnecessary with git 1.85 or higher, but git hasn't been
                // updated in quite some time on the UWB linux binaries :(
                String[] cdArg = {simFolder};
                script.executeProgram("cd", cdArg);
            } else {
                // first do a clone and maybe fail
                String[] gitCloneArgs = {"clone", repoURI, userDir + simFolder};
                script.executeProgram("git", gitCloneArgs);
                // change directory to do a pull
                // note: unnecessary with git 1.85 or higher, but git hasn't been
                // updated in quite some time on the UWB linux binaries :(
                String[] cdArg = {userDir + simFolder};
                script.executeProgram("cd", cdArg);
            }
            // then do a pull and maybe fail (one of the two will work)
            String[] gitPullArgs = {"pull"};
            script.executeProgram("git", gitPullArgs);
            if (simSpec.hasCommitCheckout()) {
                String[] gitCheckoutSHA1Key = {"checkout", simSpec.getSHA1CheckoutKey()};
                script.executeProgram("git", gitCheckoutSHA1Key);
            }
        } else {
            if (simSpec.isRemote()) {
                String[] cdArg = {simFolder};
                script.executeProgram("cd", cdArg);
            } else {
                String[] cdArg = {userDir + simFolder};
                script.executeProgram("cd", cdArg);
            }
        }
        // Record the latest commit key information
        script.addVerbatimStatement("git log --pretty=format:'%H' -n 1",
                userDir + projectName
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
        /* Make Results Folder */
        if (simSpec.isRemote()) {
            String[] mkResultsDirArgs = {"results"};
            script.executeProgram("mkdir", mkResultsDirArgs);
            script.addVerbatimStatement("mkdir -p " + "workbenchconfigfiles" + folderDelimiter
                    + "NList", true);
        } else {
            String[] mkResultsDirArgs = {userDir + simFolder + folderDelimiter + "results"};
            script.executeProgram("mkdir", mkResultsDirArgs);
            script.addVerbatimStatement("mkdir -p " + userDir + simFolder + folderDelimiter
                    + "workbenchconfigfiles" + folderDelimiter + "NList", true);
        }
        /* Move Sim Config File */
        script.addVerbatimStatement("mv -f " + fileMgr.toBashValidNotation(userDir
                + configFilename) + " " + fileMgr.toBashValidNotation(userDir + simFolder
                + folderDelimiter + "workbenchconfigfiles" + folderDelimiter + configFilename),
                true);
        /* Move Neuron Lists */
        try {
            String[] nListFilenames = fileMgr.getNeuronListFilenames(projectName);
            if (nListFilenames != null) {
                for (String nListFilename : nListFilenames) {
                    script.addVerbatimStatement("mv -f " + fileMgr.toBashValidNotation(userDir
                                    + FileManager.getSimpleFilename(nListFilename)) + " "
                                    + fileMgr.toBashValidNotation(userDir
                                    + simFolder
                                    + folderDelimiter
                                    + "workbenchconfigfiles"
                                    + folderDelimiter
                                    + "NList"
                                    + folderDelimiter
                                    + FileManager.getSimpleFilename(nListFilename)),
                            true);
                }
            }
        } catch (IOException e) {
            success = false;
        }
        /* Run the Simulator */
        script.addVerbatimStatement("./"
                + simExecutableToInvoke
                + " -t "
                + fileMgr.toBashValidNotation("workbenchconfigfiles"
                        + folderDelimiter
                        + configFilename),
                userDir
                + projectName
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
    public static String getScriptFolder(String projectFolder) {
        return projectFolder + "scripts" + FileManager.getFileManager().getFolderDelimiter();
    }

    /**
     * Runs the script as specified in the associated simulation specification.
     *
     * @param provMgr  Manages provenance. Used to record operations executed from script.
     * @param simSpec  Holds information about the simulation
     * @param scriptPath  Indicates where the constructed script to execute resides on the file
     *                    system.
     * @param scriptVersion
     * @param nListFilenames  Neuron lists indicated in simulation configuration file
     * @param simConfigFilename  Name of file containing simulation constants and filenames. File,
     *                           itself, is used to specify all input for a simulation.
     * @return True if all operations associated with running the script were successful, otherwise
     *         false. Note: This does not indicate whether the simulation ran successfully.
     * @throws com.jcraft.jsch.JSchException
     * @throws java.io.FileNotFoundException
     * @throws com.jcraft.jsch.SftpException
     */
    public boolean runScript(ProvMgr provMgr, SimulationSpecification simSpec, String scriptPath,
            String scriptVersion, String[] nListFilenames, String simConfigFilename)
            throws JSchException, FileNotFoundException, SftpException {
        boolean success;
        String executionMachine = simSpec.getSimulationLocale();
        String remoteExecution = SimulationSpecification.REMOTE_EXECUTION;
        // run script remotely?
        if (executionMachine.equals(remoteExecution)) {
            LOG.info("Running Remote Script: " + scriptPath);
            success = runRemoteScript(provMgr, simSpec, scriptPath, scriptVersion, nListFilenames,
                    simConfigFilename);
        } else { // or run it locally
            LOG.info("Running Local Script " + scriptPath);
            success = runLocalScript(provMgr, simSpec, scriptPath, scriptVersion, nListFilenames,
                    simConfigFilename);
        }
        return success;
    }

    private boolean runRemoteScript(ProvMgr provMgr, SimulationSpecification simSpec,
            String scriptPath, String scriptVersion, String[] nListFilenames,
            String simConfigFilename) throws JSchException, FileNotFoundException, SftpException {
        Long functionStartTime = System.currentTimeMillis();
        Long accumulatedTime = 0L;
        char[] password = null;
        boolean success = true;
        String hostname = simSpec.getHostAddr();
        // get username and password
        LoginCredentialsDialog lcd = new LoginCredentialsDialog(hostname, true);
        if (lcd.okClicked()) {
            SecureFileTransfer sft = new SecureFileTransfer();
            password = lcd.getPassword();
            lcd.clearPassword();
            Date uploadStartTime = new Date();
            /* Upload Script */
            if (sft.uploadFile(scriptPath, "", hostname, lcd.getUsername(), password, null)) {
                // record provenance of upload
                if (provMgr != null) {
                    Long startTime = System.currentTimeMillis();
                    WorkbenchOperationRecorder.uploadFile(provMgr, scriptPath,
                            "~/" + FileManager.getSimpleFilename(scriptPath), "script",
                            simSpec.getHostAddr(), "uploadScript_v" + scriptVersion,
                            uploadStartTime, new Date());
                    accumulatedTime = DateTime.sumProvTiming(startTime, accumulatedTime);
                }
                outstandingMessages += "\n" + scriptPath + "\nuploaded to " + hostname + "\n";

                String filename;
                boolean loopSuccess;
                /* Upload Neuron List Files */
                if (nListFilenames != null) {
                    for (String nListFilename : nListFilenames) {
                        filename = FileManager.getSimpleFilename(nListFilename);
                        outstandingMessages += "\n" + "Uploaded " + nListFilename + "\nto "
                                + hostname + "\n";
                        uploadStartTime = new Date();
                        loopSuccess = sft.uploadFile(nListFilename, "", hostname,
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
                                        "~/" + FileManager.getSimpleFilename(nListFilename),
                                        "nlist", simSpec.getHostAddr(), "upload_" + nlType
                                                + "_NList_" + "for_Script_v" + scriptVersion,
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
                    success = sft.uploadFile(simConfigFilename, "", hostname, lcd.getUsername(),
                            password, null);
                    if (success) {
                        if (provMgr != null) {
                            Long startTime = System.currentTimeMillis();
                            WorkbenchOperationRecorder.uploadFile(provMgr, simConfigFilename,
                                    "~/" + FileManager.getSimpleFilename(simConfigFilename),
                                    "simulationConfigurationFile", simSpec.getHostAddr(),
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
                    String cmd = "nohup sh ./"
                            + FileManager.getSimpleFilename(scriptPath)
                            + " &";
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

    private boolean runLocalScript(ProvMgr provMgr, SimulationSpecification simSpec,
            String scriptLocation, String scriptVersion, String[] inputFilenames,
            String simConfigFilename) {
        LOG.info("Running Local Script");
        Long functionStartTime = System.currentTimeMillis();
        Long accumulatedTime = 0L;
        boolean success = true;
        Date copyStartTime;
        FileManager fm = FileManager.getFileManager();
        Path scriptSourcePath = Paths.get(scriptLocation);
        // get the location where the script will execute
        Path scriptTargetPath = Paths.get(fm.getUserDir()
                + FileManager.getSimpleFilename(scriptLocation));
        Path simConfigSourcePath = Paths.get(simConfigFilename);
        Path simConfigTargetPath = Paths.get(fm.getUserDir()
                + FileManager.getSimpleFilename(simConfigFilename));
        Path[] nListSourcePaths = null;
        Path[] nListTargetPaths = null;
        // calculate source and target paths
        if (inputFilenames != null && inputFilenames.length > 0) {
            nListSourcePaths = new Path[inputFilenames.length];
            nListTargetPaths = new Path[inputFilenames.length];
            for (int i = 0, im = inputFilenames.length; i < im; i++) {
                nListSourcePaths[i] = Paths.get(inputFilenames[i]);
            }
            for (int i = 0, im = inputFilenames.length; i < im; i++) {
                nListTargetPaths[i] = Paths.get(fm.getUserDir()
                        + FileManager.getSimpleFilename(inputFilenames[i]));
            }
        }
        try {
            copyStartTime = new Date();
            // copy the script
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
            outstandingMessages += "\nfrom being copied to: " + fm.getUserDir();
        }
        String oldWrkDir = System.getProperty("user.dir");
        //String homeDir = System.getProperty("user.home");
        System.setProperty("user.dir", System.getProperty("user.home"));
        String cmd = "sh " + fm.getUserDir()
                + FileManager.getSimpleFilename(scriptTargetPath.toString());
        // run the script
        try {
            if (Desktop.isDesktopSupported() && fm.isWindowsSystem()) {
                Desktop dt = Desktop.getDesktop();
               //  dt.open(scriptTargetPath.toFile());
                Runtime.getRuntime().exec("cmd.exe /c start " + cmd); // Windows
            } else {
                LOG.info("Running in Console: " + cmd);
                Runtime.getRuntime().exec(cmd); // Unix ?
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
                    + "An input/output error occured while executing: "
                    + "\n" + scriptTargetPath
                    + "\n";
            e.printStackTrace();
            LOG.info(e.getMessage());
        } finally {
            System.setProperty("user.dir", oldWrkDir);
        }
        DateTime.recordFunctionExecutionTime("ScriptManager", "runLocalScript",
                System.currentTimeMillis() - functionStartTime, provMgr != null);
        if (provMgr != null) {
            DateTime.recordAccumulatedProvTiming("ScriptManager", "runLocalScript",
                    accumulatedTime);
        }
        if (success) {
            LOG.info("Script " + scriptTargetPath + " Ran Succsesfully");
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
            ProvMgr provMgr, String outputTargetFolder) throws JSchException, SftpException,
            IOException {
        Long functionStartTime = System.currentTimeMillis();
        Long accumulatedTime = 0L;
        long timeCompleted = DateTime.ERROR_TIME;
        // get all the files produced by the script
        String localOutputFilename = fetchScriptOutputFiles(projectMgr, simSpec,
                outputTargetFolder);
        if (localOutputFilename != null) {
            OutputAnalyzer analyzer = new OutputAnalyzer();
            analyzer.analyzeOutput(localOutputFilename);
            /* Completed */
            long atTime;
            String simExec = simSpec.getSimExecutable();
            atTime = analyzer.completedAt("./" + simExec);
            timeCompleted = atTime;
            if (timeCompleted != DateTime.ERROR_TIME && provMgr != null) {
                Long startTime = System.currentTimeMillis();
                /* Set Remote Namespace Prefix */
                if (simSpec.isRemote()) {
                    provMgr.setNsPrefix("remote", simSpec.getHostAddr());
                }
                /* Simulation */
                ExecutedCommand sim = analyzer.getFirstCommand("./" + simExec);
                boolean simSuccessful = false;
                try {
                    String historyDumpFilename = projectMgr.determineProjectOutputLocation()
                            + projectMgr.getSimStateOutputFile();
                    simSuccessful = new File(historyDumpFilename).exists();
                } catch (IOException e) {
                }
                if (sim != null && simSuccessful) {
                    String userDir = FileManager.getFileManager().getUserDir();
                    Resource location = provMgr.addLocation(simSpec.getHostAddr(), null, false,
                            simSpec.isRemote(), false);
                    if (simSpec.isRemote()) {
                        userDir = "~/";
                    }
                    // get agent resource
                    String uri = userDir + simSpec.getSimulatorFolder() + "/" + simExec;
                    Resource simAgent = provMgr.addSoftwareAgent(uri, "simulator",
                            simSpec.isRemote(), false);
                    // get activity resource
                    Resource simActivity = provMgr.addActivity("simulation_" + UUID.randomUUID(),
                            "simulation", simSpec.isRemote(), false);
                    // connect the two
                    provMgr.wasAssociatedWith(simActivity, simAgent);
                    provMgr.startedAtTime(simActivity,
                            new Date(analyzer.startedAt("./" + simExec)));
                    provMgr.endedAtTime(simActivity, new Date(atTime));
                    String remoteOutputFilename = userDir + simSpec.getSimulatorFolder() + "/"
                            + projectMgr.getSimStateOutputFile();
                    // add entity for remote output file, don't replace if exists
                    Resource simOutputFile = provMgr.addEntity(remoteOutputFilename, "simOutput",
                            simSpec.getHostAddr(), simSpec.getUsername(), "sftp", false);
                    provMgr.atLocation(simOutputFile, location);
                    // show that the output was generated by the simulation
                    provMgr.addFileGeneration(simActivity, simAgent, simOutputFile);
                    // show that the inputs were used in the simulation
                    String remoteSimConfigFilename = userDir + simSpec.getSimulatorFolder()
                            + "/workbenchconfigfiles/"
                            + FileManager.getSimpleFilename(projectMgr.getSimConfigFilename());
                    Resource remoteSimConfigFileEnitity = provMgr.addEntity(remoteSimConfigFilename,
                            "simulationConfigurationFile", simSpec.getHostAddr(),
                            simSpec.getUsername(), "sftp", false);
                    provMgr.atLocation(remoteSimConfigFileEnitity, location);

                    provMgr.used(simActivity, remoteSimConfigFileEnitity);
                    String[] neuronLists = FileManager.getFileManager()
                            .getNeuronListFilenames(projectMgr.getName());
                    for (String neuronList : neuronLists) {
                        String movedNLFilename = userDir
                                + simSpec.getSimulatorFolder()
                                + "/workbenchconfigfiles"
                                + "/NList/"
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
                    String sha1Pathname = outputTargetFolder + sha1KeyFilename;
                    File sha1File = new File(sha1Pathname);
                    if (sha1File.exists()) {
                        // open the file
                        Scanner fileReader = null;
                        String sha1key = null;
                        /* Stage Error Handling */
                        try { // try to start reading from the given file path
                            fileReader = new Scanner(new FileReader(sha1File));
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
                            System.err.println("File not found: " + sha1Pathname);
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
            String outputStorageFolder) throws JSchException, SftpException, IOException {
        FileManager fm = FileManager.getFileManager();
        String filename = null;
        char[] password = null;
        String projectName = projectMgr.getName();
        String version = projectMgr.getScriptVersion();
        String scriptStatusFilename = projectName
                + "_v"
                + version
                + "_"
                + Script.DEFAULT_SCRIPT_STATUS_FILENAME;
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
        String scriptStatusFileTargetLocation = outputStorageFolder
                + scriptStatusFilename;
        // prep folder for sim output
        String localSimResultsFolder = projectMgr
                .determineProjectOutputLocation()
                + "results"
                + fm.getFolderDelimiter();
        new File(localSimResultsFolder).mkdirs();
        // calculate simulation history dump filename to write
        String historyDumpFilename = projectMgr.determineProjectOutputLocation()
                + projectMgr.getSimStateOutputFile();

        // run simulation here or on another machine?
        boolean remote = simSpec.isRemote();
        if (remote) {
            // download script status file
            SecureFileTransfer sft = new SecureFileTransfer();
            String hostname = simSpec.getHostAddr();
            LoginCredentialsDialog lcd = new LoginCredentialsDialog(hostname, true);
            password = lcd.getPassword();
            simSpec.setUsername(lcd.getUsername());
            lcd.clearPassword();
            outstandingMessages += "\nDownloading script status file:\n" + scriptStatusFilename
                    + "\nFrom: " + hostname + "\n";
            if (sft.downloadFile(scriptStatusFilename, scriptStatusFileTargetLocation, hostname,
                    lcd.getUsername(), password)) {
                outstandingMessages += "\nDownloading simulation status file:\n"
                        + simStatusFilename
                        + "\nFrom: " + hostname + "\n";
                try {
                    // download simulation stdout redirect file
                    sft.downloadFile(simStatusFilename, localSimResultsFolder + simStatusFilename,
                            hostname, lcd.getUsername(), password);
                    outstandingMessages += "\nLatest output from simulation:\n"
                            + getLastLine(localSimResultsFolder + simStatusFilename)
                            + "\n";
                } catch (SftpException e) {
                    outstandingMessages += "\nDownload failed for: "
                            + simStatusFilename
                            + "\n";
                }
                if (scriptComplete(scriptStatusFileTargetLocation, simSpec)) {
                    // track the file to analyze
                    filename = scriptStatusFileTargetLocation;
                    // calculate simulation output filename to read
                    String remoteHistoryDumpFilename = simSpec.getSimulatorFolder()
                            + "/" + projectMgr.getSimStateOutputFile();
                    outstandingMessages += "\nDownloading simulation history dump: \n"
                            + remoteHistoryDumpFilename
                            + "\nFrom: " + hostname + "\n";
                    try {
                        // download sim state output file
                        sft.downloadFile(remoteHistoryDumpFilename, historyDumpFilename, hostname,
                                lcd.getUsername(), password);
                    } catch (SftpException e) {
                        outstandingMessages += "\nDownload failed for: "
                                + remoteHistoryDumpFilename
                                + "\n";
                    }
                    // download sha1 key file
                    outstandingMessages += "\nDownloading simulator source code version report:\n"
                            + sha1KeyFilename
                            + "\nFrom: " + hostname + "\n";
                    try {
                        sft.downloadFile(sha1KeyFilename, outputStorageFolder + sha1KeyFilename,
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
            Path scriptStatusSourcePath = Paths.get(fm.getUserDir()
                    + fm.getFolderDelimiter()
                    + scriptStatusFilename);
            Path scriptStatusTargetPath = Paths.get(scriptStatusFileTargetLocation);
            outstandingMessages += "\nCopying script status file..."
                    + scriptStatusFilename
                    + "\nFrom: " + scriptStatusSourcePath.toString()
                    + "\nTo: " + scriptStatusTargetPath.toString()
                    + "\n";
            if (FileManager.copyFile(scriptStatusSourcePath, scriptStatusTargetPath)) {
                // get simulation stdout redirect file
                Path simStatusSourcePath = Paths.get(fm.getUserDir()
                        + fm.getFolderDelimiter()
                        + simStatusFilename);
                Path simStatusTargetPath = Paths.get(localSimResultsFolder
                        + simStatusFilename);
                outstandingMessages += "\nCopying simulation status file..."
                        + simStatusFilename
                        + "\nFrom: " + simStatusSourcePath.toString()
                        + "\nTo: " + simStatusTargetPath.toString()
                        + "\n";
                try {
                    FileManager.copyFile(simStatusSourcePath, simStatusTargetPath);
                    outstandingMessages += "\nLatest output from simulation:\n"
                            + getLastLine(simStatusTargetPath.toString())
                            + "\n";
                } catch (IOException e) {
                    outstandingMessages += "\nSimulation status copy operation failed: \n"
                            + e.getLocalizedMessage() + "\n";
                }
                // if the script is finished
                if (scriptComplete(scriptStatusFileTargetLocation, simSpec)) {
                    // track the file to analyze
                    filename = scriptStatusFileTargetLocation;
                    Path historyDumpSourcePath = Paths.get(fm.getUserDir()
                            + fm.getFolderDelimiter()
                            + simSpec.getSimulatorFolder()
                            + fm.getFolderDelimiter()
                            + projectMgr.getSimStateOutputFile());
                    Path historyDumpTargetPath = Paths.get(historyDumpFilename);
                    outstandingMessages += "\nCopying simulation history dump..."
                            + projectMgr.getSimStateOutputFile()
                            + "\nFrom: " + historyDumpSourcePath.toString()
                            + "\nTo: " + historyDumpTargetPath.toString()
                            + "\n";
                    try {
                        FileManager.copyFile(historyDumpSourcePath, historyDumpTargetPath);
                    } catch (IOException e) {
                        outstandingMessages += "\nSimulation history dump copy operation failed: \n"
                                + e.getLocalizedMessage() + "\n";
                    }
                    Path sha1FileSource = Paths.get(fm.getUserDir() + sha1KeyFilename);
                    Path sha1FileTarget = Paths.get(outputStorageFolder + sha1KeyFilename);
                    outstandingMessages += "\nCopying simulator source code version report:\n"
                            + sha1KeyFilename
                            + "\nFrom: " + sha1FileSource.toString()
                            + "\nTo: " + sha1FileTarget.toString()
                            + "\n";
                    try {
                        FileManager.copyFile(sha1FileSource, sha1FileTarget);
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
        timeCompleted = analyzer.completedAt("./" + simExec);
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

    public static String getLastLine(String filename) {
        String lastLine = "";
        File file = new File(filename);
        if (file.exists()) {
            // open the file
            Scanner fileReader;
            /* Stage Error Handling */
            try { // try to start reading from the given file path
                fileReader = new Scanner(new FileReader(file));
                while (fileReader.hasNext()) {
                    lastLine = fileReader.nextLine();
                }
            } catch (FileNotFoundException e) {
                System.err.println("File not found: " + filename);
            }
        }
        return lastLine;
    }

    private static final Logger LOG = Logger.getLogger(ScriptManager.class.getName());
}
