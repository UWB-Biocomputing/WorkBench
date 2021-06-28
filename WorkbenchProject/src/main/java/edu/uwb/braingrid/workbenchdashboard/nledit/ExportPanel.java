package edu.uwb.braingrid.workbenchdashboard.nledit;

import java.io.File;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import edu.uwb.braingrid.general.FileSelectorDirMgr;
import edu.uwb.braingrid.workbenchdashboard.WorkbenchDisplay;

/**
 * The ExportPanel class handles export xml neurons list files dialog window. The window contains
 * three input fields and Browse buttons, each of which corresponds width four different kinds of
 * files, inhibitory neuron list, active neuron list, and probed neuron list files.
 *
 * @author Fumitaka Kawasaki
 * @version 1.2
 */
public class ExportPanel extends Pane implements EventHandler<javafx.event.ActionEvent> {

    /** Number of input fields. */
    public static final int NUM_FIELDS = 3;
    /** Field index of inhibitory neurons list file. */
    public static final int IDX_INH_LIST = 0;
    /** Field index of active neurons list file. */
    public static final int IDX_ACT_LIST = 1;
    /** Field index of probed neurons list file. */
    public static final int IDX_PRB_LIST = 2;
//    private static String nListDir = "."; // directory for neurons list file

    /** The labels for this Pane. */
    private Label[] labels = new Label[NUM_FIELDS];
    /** The buttons for this Pane. */
    private Button[] buttons = new Button[NUM_FIELDS];
    /** The text fields for this Pane. */
    TextField[] tFields = new TextField[NUM_FIELDS];

    private FileSelectorDirMgr fileMgr = new FileSelectorDirMgr();

    /**
     * A class constructor, which creates UI components, and registers action listener.
     *
     * @param dir  directory for neurons list file
     */
    public ExportPanel(String dir) {
        GridPane gp = new GridPane();
//        nListDir = dir;

        labels[IDX_INH_LIST] = new Label("Inhibitory neurons list:");
        labels[IDX_ACT_LIST] = new Label("Active neurons list:");
        labels[IDX_PRB_LIST] = new Label("Probed neurons list:");

        gp.getChildren().addAll(labels[IDX_INH_LIST], labels[IDX_ACT_LIST], labels[IDX_PRB_LIST]);
        GridPane.setConstraints(labels[IDX_INH_LIST], 0, IDX_INH_LIST);
        GridPane.setConstraints(labels[IDX_ACT_LIST], 0, IDX_ACT_LIST);
        GridPane.setConstraints(labels[IDX_PRB_LIST], 0, IDX_PRB_LIST);

        for (int i = 0; i < NUM_FIELDS; i++) {
            tFields[i] = new TextField();
            tFields[i].setEditable(true);
            buttons[i] = new Button("Browse...");
            buttons[i].setOnAction(this);
            gp.getChildren().addAll(tFields[i], buttons[i]);
            GridPane.setConstraints(tFields[i], 1, i);
            GridPane.setConstraints(buttons[i], 2, i);
        }
        getChildren().add(gp);
    }

    @Override
    public void handle(javafx.event.ActionEvent arg0) {
        int iSource = 0;
        for (int i = 0; i < NUM_FIELDS; i++) {
            if (arg0.getSource() == buttons[i]) {
                iSource = i;
                break;
            }
        }
        // create a file chooser
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(fileMgr.getLastDir());
        chooser.setTitle("Save File");

        ExtensionFilter filter = new ExtensionFilter("XML file (*.xml)", "xml");
        chooser.setSelectedExtensionFilter(filter);

//        String dialogTitle = "";
        switch (iSource) {
        case IDX_INH_LIST:
            chooser.setInitialFileName("inh.xml");
//            dialogTitle = "Inhibitory neurons list";
            break;
        case IDX_ACT_LIST:
            chooser.setInitialFileName("act.xml");
//            dialogTitle = "Active neurons list";
            break;
        case IDX_PRB_LIST:
            chooser.setInitialFileName("prb.xml");
//            dialogTitle = "Probed neurons list";
            break;
        default:
            // do nothing
        }

        File option = chooser.showSaveDialog(WorkbenchDisplay.getPrimaryStage());

        if (option != null) {
            tFields[iSource].setText(option.getAbsolutePath());
//            nListDir = option.getParent();
            fileMgr.add(option.getParentFile());
        }
    }
}
