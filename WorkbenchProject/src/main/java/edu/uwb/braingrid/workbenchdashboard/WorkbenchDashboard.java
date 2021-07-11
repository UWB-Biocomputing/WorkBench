package edu.uwb.braingrid.workbenchdashboard;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.application.Application;
import javafx.stage.Stage;

import edu.uwb.braingrid.general.LoggerHelper;
import edu.uwb.braingrid.workbenchdashboard.threads.RunInit;
import edu.uwb.braingrid.workbenchdashboard.userModel.User;
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
        FileHandler handler = null;
        try {
            handler = new FileHandler("WD-log.%u");
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

        // Get system
        SystemProperties.getInstance();

        //Load User
        User.load();

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

        Image logo = new Image(getClass().getResourceAsStream("/braingrid/color-logo.png"));
        primaryStage.setTitle("BrainGrid Workbench");
        primaryStage.getIcons().add(logo);

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        // Exit application on window close
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        // Init
        RunInit runInit = new RunInit();
        runInit.start();
    }
}
