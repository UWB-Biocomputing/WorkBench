package edu.uwb.braingrid.workbenchdashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uwb.braingrid.provenance.ProvMgr;
import edu.uwb.braingrid.workbench.comm.SecureFileTransfer;
import edu.uwb.braingrid.workbench.model.Simulation;
import edu.uwb.braingrid.workbench.ui.LoginCredentialsDialog;
import edu.uwb.braingrid.workbench.ui.SimulationSpecificationDialog;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.eclipse.jgit.transport.CredentialItem.Username;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.application.Application;
import javafx.stage.Stage;
import edu.uwb.braingrid.general.LoggerHelper;
import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbench.WorkbenchManager;
import edu.uwb.braingrid.workbenchdashboard.utils.SystemProperties;

/**
 * Workbench Dashboard Application, contains main.
 *
 * @author Max Wright, Extended by Joseph Conquest
 */
public class WorkbenchDashboard extends Application {

    private static final Logger LOG = Logger.getLogger(WorkbenchDashboard.class.getName());

    /** Default width for the main display. */
    public static final int DEFAULT_SCENE_WIDTH = 900;
    /** Default height for the main display. */
    public static final int DEFAULT_SCENE_HEIGHT = 600;

    /** GSLE Growth Simulation Layout Editor. */
    private WorkbenchDisplay workbenchDisplay;
    /** True only if ctrl has been pressed. */
    private boolean ctrl = false;

    /**
     * WorkbenchDashboard main executable.
     *
     * @param args  The command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void initFileOutput() {
        Path logsDir = FileManager.getWorkbenchDirectory().resolve("logs");
        String logFile = logsDir.resolve("WD-log.%u").toString();
        FileHandler handler = null;
        try {
            Files.createDirectories(logsDir);
            handler = new FileHandler(logFile);
        } catch (SecurityException | IOException e) {
            LOG.severe("");
            e.printStackTrace();
        }
        if (handler != null) {
            LOG.getParent().getHandlers()[0].setLevel(LoggerHelper.MIN_LOG_LEVEL);
            LOG.getParent().addHandler(handler);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Setup Logger
        LOG.setLevel(LoggerHelper.MIN_LOG_LEVEL);
        initFileOutput();

        // Initialize System Properties
        SystemProperties.getInstance();

        // Start Application
        LOG.info("Starting Application");
        // Create main display
        workbenchDisplay = new WorkbenchDisplay(primaryStage);
        // Create a scene out of the display
        Scene scene = new Scene(workbenchDisplay, DEFAULT_SCENE_WIDTH, DEFAULT_SCENE_HEIGHT);

        // Add CSS files
        scene.getStylesheets().add(getClass().getResource("/simstarter/css/temp.css")
                .toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/simstarter/css/tempII.css")
                .toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/nledit/css/design.css")
                .toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/workbenchdisplay/css/design.css")
                .toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/provvisualizer/css/design.css")
                .toExternalForm());

        // Create Events
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.CONTROL) {
                ctrl = true;
            }
            if (event.getCode() == KeyCode.G && ctrl) {
                workbenchDisplay.pushGSLEPane();
            }
            if (event.getCode() == KeyCode.S && ctrl) {
                workbenchDisplay.pushSimWizPop();
            }
            if (event.getCode() == KeyCode.P && ctrl) {
                workbenchDisplay.pushProVisStarterPage();
            }
            if (event.getCode() == KeyCode.U && ctrl) {
                workbenchDisplay.pushUserViewPage();
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.CONTROL) {
                ctrl = false;
            }
        });

        Image logo = new Image(getClass().getResourceAsStream("/graphitti/color-logo.png"));
        primaryStage.setTitle("Graphitti Workbench");
        primaryStage.getIcons().add(logo);

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
        // Exit application on window close
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        // Initialize Workbench Manager
        WorkbenchManager.getInstance();
    checkLastSim();
  }

  private String workingDir() {
    String dir = System.getProperty("user.dir");
    String target = "\\target";
    if (dir.endsWith(target)) {
      dir = dir.substring(0, dir.length() - target.length());
    }
    return dir;
  }

  private void checkLastSim() {
    String workDir = workingDir();
    String substr = "\\target";
    if (workDir.endsWith(substr)) {
      workDir = workDir.substring(0, workDir.length() - substr.length());
    }
    String simPath = workDir + "\\LastSimulations";
    File lastSim = new File(simPath);
    if (lastSim.exists() && lastSim.isDirectory() && lastSim.list().length > 0) {
      LOG.info("Last Simulation detected");
      JFrame frame = new JFrame();
      String[] options = {"Resume", "No"};
      int option = JOptionPane.showOptionDialog(frame,
          "Do you want to resume last simulation?", "Last Simulation Detected",
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                  null, options, options[0]);
      if (option == JOptionPane.YES_NO_OPTION) {
        try {
          JOptionPane.getRootFrame().dispose();
          FileInputStream readKey = new FileInputStream(
              new File(workingDir() + "\\Key"));
          try {
            ObjectInputStream readKeyObj = new ObjectInputStream(readKey);
            String key;
            try {
              key = (String) readKeyObj.readObject();
              File hostInfo = new File(
                  workingDir() + "\\Cache\\hostname.encrypted");
              SimulationSpecificationDialog tempDiaLog = new SimulationSpecificationDialog();
              String realHostInfo = tempDiaLog.decrypt(key, hostInfo, hostInfo, "");
              File[] files = lastSim.listFiles();
              String[] simNames = new String[files.length];
              for (int i = 0; i < files.length; i++) {
                  simNames[i] = files[i].getName();
              }
              LoginCredentialsDialog loginToResume = new
                  LoginCredentialsDialog(realHostInfo, true);
              final String username = loginToResume.getUsername();
              final String password = new String(loginToResume.getPassword());
              final SecureFileTransfer fileTransfer = new SecureFileTransfer();
              String[] provUris = new String[files.length];
              String[] localUris = new String[files.length];
              String[] remoteUris = new String[files.length];
              String[] msgs = new String[files.length];
              Simulation[] simulations = new Simulation[files.length];
              Model[] models = new Model[files.length];
              ProvMgr[] lastMgrs = new ProvMgr[files.length];

              for (int i = 0; i < files.length; i++) {
                  File simulationInput = new File(
                      lastSimulationsDir() + "\\" + simNames[i] + "\\simulation");
                  FileInputStream simIn = new FileInputStream(simulationInput);
                  ObjectInputStream simInObj = new ObjectInputStream(simIn);
                  BufferedReader readUri = new BufferedReader(
                      new FileReader(lastSimulationsDir() + "\\" + simNames[i] + "\\uri"));
                  provUris[i] = readUri.readLine();
                  localUris[i] = readUri.readLine();
                  remoteUris[i] = readUri.readLine();
                  Model model = ModelFactory.createDefaultModel();
                  model.read(lastSimulationsDir() + "\\" + simNames[i] + "\\model.ttl", "TURTLE");
                  models[i] = model;
                  ObjectInputStream msgReader = new ObjectInputStream(
                      new FileInputStream(new File(
                          lastSimulationsDir() + simNames[i] + "message")));
                  msgs[i] = (String) msgReader.readObject();
                  simulations[i] = (Simulation) simInObj.readObject();
                  lastMgrs[i] = new ProvMgr(provUris[i], localUris[i], remoteUris[i], models[i]);
                  WorkbenchManager.getInstance().simulationSetter(simulations[i]);
                  WorkbenchManager.getInstance().provMgrSetter(lastMgrs[i]);
                  WorkbenchManager.getInstance().setMessages(msgs[i]);
                  fileTransfer.checkLastSim(realHostInfo, username,
                          password, simNames[i], WorkbenchManager.getInstance());
              }
            } catch (ClassNotFoundException e) {
              e.printStackTrace();
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      }
    } else {
      JOptionPane.getRootFrame().dispose();
    }
  }
  private String lastSimulationsDir() {
    return workingDir() + "\\LastSimulations";
  }
}
