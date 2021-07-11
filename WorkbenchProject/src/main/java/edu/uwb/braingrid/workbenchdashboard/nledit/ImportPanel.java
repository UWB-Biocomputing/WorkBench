package edu.uwb.braingrid.workbenchdashboard.nledit;

import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import edu.uwb.braingrid.general.FileSelectorDirMgr;
import edu.uwb.braingrid.workbenchdashboard.WorkbenchDisplay;

public class ImportPanel extends Pane implements EventHandler<ActionEvent> {

    /** Number of input fields. */
    public static final int NUM_FIELDS = 4;
    /** Field index of configuration file. */
    public static final int IDX_CONFIG_FILE = 0;
    /** Field index of inhibitory neurons list file. */
    public static final int IDX_INH_LIST = 1;
    /** Field index of active neurons list file. */
    public static final int IDX_ACT_LIST = 2;
    /** Field index of probed neurons list file. */
    public static final int IDX_PRB_LIST = 3;

    /** Directory for configuration file. */
    private static String configDir = ".";
    /** Directory for neurons list file. */
    private static String nListDir = ".";

    /** The labels for this Pane. */
    private Label[] labels = new Label[NUM_FIELDS];
    /** The buttons for this Pane. */
    private Button[] buttons = new Button[NUM_FIELDS];
    /** The text fields for this Pane. */
    TextField[] tFields = new TextField[NUM_FIELDS];

    private FileSelectorDirMgr fileMgr = new FileSelectorDirMgr();

    public ImportPanel() {
        GridPane gp = new GridPane();

        labels[IDX_CONFIG_FILE] = new Label("Configuration file:");
        labels[IDX_INH_LIST] = new Label("Inhibitory neurons list:");
        labels[IDX_ACT_LIST] = new Label("Active neurons list:");
        labels[IDX_PRB_LIST] = new Label("Probed neurons list:");
        gp.getChildren().addAll(labels[IDX_CONFIG_FILE], labels[IDX_INH_LIST], labels[IDX_ACT_LIST],
                labels[IDX_PRB_LIST]);
        GridPane.setConstraints(labels[IDX_CONFIG_FILE], 0, IDX_CONFIG_FILE);
        GridPane.setConstraints(labels[IDX_INH_LIST], 0, IDX_INH_LIST);
        GridPane.setConstraints(labels[IDX_ACT_LIST], 0, IDX_ACT_LIST);
        GridPane.setConstraints(labels[IDX_PRB_LIST], 0, IDX_PRB_LIST);
        for (int i = 0; i < NUM_FIELDS; i++) {
            tFields[i] = new TextField();
            tFields[i].setEditable(true);
            gp.getChildren().add(tFields[i]);
            GridPane.setConstraints(tFields[i], 1, i);
            buttons[i] = new Button("Browse...");
            buttons[i].setOnAction(this);
            gp.getChildren().add(buttons[i]);
            GridPane.setConstraints(buttons[i], 2, i);
        }
        getChildren().add(gp);
    }

    private void importFiles(ActionEvent event) {
        int iSource = 0;
        for (int i = 0; i < NUM_FIELDS; i++) {
            if (event.getSource() == buttons[i]) {
                iSource = i;
                break;
            }
        }
        // create a file chooser
        // String curDir;
        // if (iSource == IDX_CONFIG_FILE) {
        // curDir = configDir;
        // } else {
        // curDir = nListDir;
        // }

        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(fileMgr.getLastDir());

        chooser.setTitle("Open File");
        // fileChooser.showOpenDialog(stage);
        ExtensionFilter filter = new ExtensionFilter("XML file (*.xml)", "xml");
        chooser.setSelectedExtensionFilter(filter);
        String dialogTitle = "";
        switch (iSource) {
        case IDX_CONFIG_FILE:
            chooser.setInitialFileName("config");
            dialogTitle = "Configuration file";
            break;
        case IDX_INH_LIST:
            chooser.setInitialFileName("inh");
            dialogTitle = "Inhibitory neurons list";
            break;
        case IDX_ACT_LIST:
            chooser.setInitialFileName("act");
            dialogTitle = "Active neurons list";
            break;
        case IDX_PRB_LIST:
            chooser.setInitialFileName("prb");
            dialogTitle = "Probed neurons list";
            break;
        default:
            // do nothing
        }
        chooser.setTitle(dialogTitle);

        File option = chooser.showOpenDialog(WorkbenchDisplay.getPrimaryStage());
        if (option != null) {
            tFields[iSource].setText(option.getAbsolutePath());
            fileMgr.add(option.getParentFile());
            if (iSource == IDX_CONFIG_FILE) { // configuration files is specified.
                // parse config file, extract names of neurons list files, and
                // show them in the corresponding fields
                String configFile = option.getAbsolutePath();
                configDir = option.getParent();
                nListDir = configDir;
                try {
                    Document doc = new SAXBuilder().build(new File(configFile));
                    Element root = doc.getRootElement();
                    Element layout = root.getChild("FixedLayout").getChild("LayoutFiles");
                    Attribute attr = layout.getAttribute("activeNListFileName");
                    if (attr != null) {
                        tFields[IDX_ACT_LIST].setText(configDir + "/" + attr.getValue());
                    }
                    attr = layout.getAttribute("inhNListFileName");
                    if (attr != null) {
                        tFields[IDX_INH_LIST].setText(configDir + "/" + attr.getValue());
                    }
                    attr = layout.getAttribute("probedNListFileName");
                    if (attr != null) {
                        tFields[IDX_PRB_LIST].setText(configDir + "/" + attr.getValue());
                    }
                } catch (JDOMException | IOException e) {
                    // System.err.println(e);
                }
            } else {
                nListDir = option.getParent();
            }
        }
    }

    @Override
    public void handle(ActionEvent event) {
        importFiles(event);
    }

    public static String getNListDir() {
        return nListDir;
    }
}
