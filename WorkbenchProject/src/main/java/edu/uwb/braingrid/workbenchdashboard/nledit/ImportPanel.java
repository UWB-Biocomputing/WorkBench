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
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import edu.uwb.braingrid.general.FileSelectorDirMgr;
import edu.uwb.braingrid.workbenchdashboard.WorkbenchDashboard;

public class ImportPanel extends Pane implements EventHandler<ActionEvent> {

    /** Number of input fields. */
    public static final int nFields = 4;
    /** Field index of configuration file. */
    public static final int idxConfigFile = 0;
    /** Field index of inhibitory neurons list file. */
    public static final int idxInhList = 1;
    /** Field index of active neurons list file. */
    public static final int idxActList = 2;
    /** Field index of probed neurons list file. */
    public static final int idxPrbList = 3;

    private Label[] labels = new Label[nFields];
    public TextField[] tfields = new TextField[nFields];
    private Button[] btns = new Button[nFields];

    private static String configDir = "."; // directory for configuration file
    public static String nlistDir = "."; // directory for neurons list file

    public ImportPanel() {
        GridPane gp = new GridPane();

        labels[idxConfigFile] = new Label("Configuration file:");
        labels[idxInhList] = new Label("Inhibitory neurons list:");
        labels[idxActList] = new Label("Active neurons list:");
        labels[idxPrbList] = new Label("Probed neurons list:");
        gp.getChildren().addAll(labels[idxConfigFile], labels[idxInhList], labels[idxActList], labels[idxPrbList]);
        GridPane.setConstraints(labels[idxConfigFile], 0, 0);
        GridPane.setConstraints(labels[idxInhList], 0, 1);
        GridPane.setConstraints(labels[idxActList], 0, 2);
        GridPane.setConstraints(labels[idxPrbList], 0, 3);
        for (int i = 0; i < nFields; i++) {
            tfields[i] = new TextField();
            tfields[i].setEditable(true);
            gp.getChildren().add(tfields[i]);
            GridPane.setConstraints(tfields[i], 1, i);
            btns[i] = new Button("Browse...");
            btns[i].setOnAction(this);
            gp.getChildren().add(btns[i]);
            GridPane.setConstraints(btns[i], 2, i);
        }
        getChildren().add(gp);
    }

    FileSelectorDirMgr filemgr = new FileSelectorDirMgr();

    private void importFiles(ActionEvent e) {
        int iSource = 0;
        for (int i = 0; i < nFields; i++) {
            if (e.getSource() == btns[i]) {
                iSource = i;
                break;
            }
        }
        // create a file chooser
        // String curDir;
        // if (iSource == idxConfigFile) {
        // curDir = configDir;
        // } else {
        // curDir = nlistDir;
        // }

        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(filemgr.getLastDir());

        chooser.setTitle("Open File");
        // fileChooser.showOpenDialog(stage);
        ExtensionFilter filter = new ExtensionFilter("XML file (*.xml)", "xml");
        chooser.setSelectedExtensionFilter(filter);
        String dialogTitle = "";
        switch (iSource) {
        case idxConfigFile:
            chooser.setInitialFileName("config");
            dialogTitle = "Configuration file";
            break;
        case idxInhList:
            chooser.setInitialFileName("inh");
            dialogTitle = "Inhibitory neurons list";
            break;
        case idxActList:
            chooser.setInitialFileName("act");
            dialogTitle = "Active neurons list";
            break;
        case idxPrbList:
            chooser.setInitialFileName("prb");
            dialogTitle = "Probed neurons list";
            break;
        }
        chooser.setTitle(dialogTitle);

        File option = chooser.showOpenDialog(WorkbenchDashboard.primaryStage_);
        if (option != null) {
            tfields[iSource].setText(option.getAbsolutePath());
            filemgr.add(option.getParentFile());
            if (iSource == idxConfigFile) { // configuration files is specified.
                // parse config file, extract names of neurons list files, and
                // show them in the corresponding fields
                String configFile = option.getAbsolutePath();
                configDir = option.getParent();
                nlistDir = configDir;
                try {
                    Document doc = new SAXBuilder().build(new File(configFile));
                    Element root = doc.getRootElement();
                    Element layout = root.getChild("FixedLayout").getChild("LayoutFiles");
                    org.jdom2.Attribute attr;
                    if ((attr = layout.getAttribute("activeNListFileName")) != null) {
                        tfields[idxActList].setText(configDir + "/" + attr.getValue());
                    }
                    if ((attr = layout.getAttribute("inhNListFileName")) != null) {
                        tfields[idxInhList].setText(configDir + "/" + attr.getValue());
                    }
                    if ((attr = layout.getAttribute("probedNListFileName")) != null) {
                        tfields[idxPrbList].setText(configDir + "/" + attr.getValue());
                    }
                } catch (JDOMException je) {
                    // System.err.println(je);
                } catch (IOException ie) {
                    // System.err.println(ie);
                }
            } else {
                nlistDir = option.getParent();
            }
        }
    }

    @Override
    public void handle(ActionEvent arg0) {
        // TODO Auto-generated method stub
        importFiles(arg0);
    }
}
