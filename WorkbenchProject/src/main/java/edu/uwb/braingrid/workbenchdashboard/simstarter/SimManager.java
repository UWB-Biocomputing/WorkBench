package edu.uwb.braingrid.workbenchdashboard.simstarter;

import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

import edu.uwb.braingrid.workbench.WorkbenchManager;

public class SimManager {

    private static final Logger LOG = Logger.getLogger(SimManager.class.getName());

    private BorderPane borderPane = new BorderPane();
    private Label projectTitleLabel = new Label();
    private Label currentProjectLabel = new Label();
    private ProgressBar progressBar = new ProgressBar(0);
    private TextArea msgText = new TextArea("");

    private WorkbenchManager workbenchManager;

    public SimManager() {
        LOG.info("new " + getClass().getName());
        workbenchManager = new WorkbenchManager();
    }

    public SimManager(WorkbenchManager wbmng) {
        LOG.info("new " + getClass().getName());
        workbenchManager = wbmng;
    }

    /**
     * Sets the workbench message content. The content of this message is based on the accumulated
     * messages produced by the functions of the workbench manager.
     *
     */
    public void setMsg() {
        msgText.setText(workbenchManager.getMessages());
    }

    public void openProject() {
        int code = workbenchManager.openProject();
        switch (code) {
        case JFileChooser.APPROVE_OPTION:
            updateProjectOverview();
            break;
        case JFileChooser.CANCEL_OPTION:
        case JFileChooser.ERROR_OPTION:
        case WorkbenchManager.EXCEPTION_OPTION:
        default:
        }
    }

    public void saveProject() {
        workbenchManager.saveProject();
        setMsg();
    }

    public void viewProvenance() {
        setMsg();
        workbenchManager.viewProvenance();
    }

    public void manageParamsClasses() {
        if (workbenchManager.configureParamsClasses()) {
            updateProjectOverview();
        }
        setMsg();
    }

    public void updateProjectOverview() {
        currentProjectLabel.setText(workbenchManager.getProjectName());
        // transferProgressBar.setVisible(workbenchManager.isSimExecutionRemote());
    }
}
