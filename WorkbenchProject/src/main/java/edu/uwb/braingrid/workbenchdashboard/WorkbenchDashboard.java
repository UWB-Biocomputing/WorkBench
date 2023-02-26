package edu.uwb.braingrid.workbenchdashboard;

import java.awt.FlowLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

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
import edu.uwb.braingrid.workbench.comm.SecureFileTransfer;
import edu.uwb.braingrid.workbench.ui.LoginCredentialsDialog;
import edu.uwb.braingrid.workbench.ui.SimulationSpecificationDialog;
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
    String simPath = workDir + "\\LastSimulation\\simdir";
    File lastSim = new File(simPath);
    if (lastSim.exists() && lastSim.isFile() && lastSim.length() != 0) {
      LOG.info("Last Simulation detected");
      JFrame frame = new JFrame();
      String[] options = {"Resume", "No"};
      int option = JOptionPane.showOptionDialog(frame,
          "Do you want to resume last simulation?", "Confirmation",
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                  null, options, options[0]);
      if (option == JOptionPane.YES_NO_OPTION) {
        try {
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
              FileInputStream readSimlationName = new FileInputStream(new File(
                  workingDir() + "\\LastSimulation\\simName"));
              ObjectInputStream readSimNameObj = new ObjectInputStream(readSimlationName);
              String simName = (String) readSimNameObj.readObject();
              LoginCredentialsDialog loginToResume = new
                  LoginCredentialsDialog(realHostInfo, true);
              String username = loginToResume.getUsername();
              String password = new String(loginToResume.getPassword());
              SecureFileTransfer fileTransfer = new SecureFileTransfer();
              fileTransfer.checkLastSim(realHostInfo, username, password, simName);
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
    }
  }
}
