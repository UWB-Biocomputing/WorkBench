package edu.uwb.braingrid.workbenchdashboard.nledit;

import java.io.File;

import edu.uwb.braingrid.general.FileSelectorDirMgr;
import edu.uwb.braingrid.workbenchdashboard.WorkbenchDashboard;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * The ExportPanel class handles export xml neurons list files dialog window.
 * The window contains three input fields and Browse buttons, each of which
 * corresponds width four different kinds of files, inhibitory neuron list,
 * active neuron list, and probed neuron list files.
 * 
 * @author Fumitaka Kawasaki
 * @version 1.2
 */
public class ExportPanel extends Pane implements EventHandler<javafx.event.ActionEvent> {
    private Label[] labels = new Label[3];
    public TextField[] tfields = new TextField[3];
    private Button[] btns = new Button[3];
//    private static String nlistDir = "."; // directory for neurons list file

    static final int nFields = 3; // number of input fields
    /** field index of inhibitory neurons list file */
    public static final int idxInhList = 0;
    /** field index of active neurons list file */
    public static final int idxActList = 1;
    /** field index of probed neurons list file */
    public static final int idxPrbList = 2;

    /**
     * A class constructor, which creates UI components, and registers action
     * listener.
     * 
     * @param dir
     *            directory for neurons list file
     */
    public ExportPanel(String dir) {
        GridPane gp = new GridPane();
//        nlistDir = dir;

        labels[idxInhList] = new Label("Inhibitory neurons list:");
        labels[idxActList] = new Label("Active neurons list:");
        labels[idxPrbList] = new Label("Probed neurons list:");

        gp.getChildren().addAll(labels[idxInhList], labels[idxActList], labels[idxPrbList]);
        GridPane.setConstraints(labels[idxInhList], 0, 0);
        GridPane.setConstraints(labels[idxActList], 0, 1);
        GridPane.setConstraints(labels[idxPrbList], 0, 2);

        for (int i = 0; i < nFields; i++) {
            tfields[i] = new TextField();
            tfields[i].setEditable(true);
            btns[i] = new Button("Browse...");
            btns[i].setOnAction(this);
            gp.getChildren().addAll(tfields[i], btns[i]);
            GridPane.setConstraints(tfields[i], 1, i);
            GridPane.setConstraints(btns[i], 2, i);
        }
        getChildren().add(gp);
    }

    FileSelectorDirMgr filemgr = new FileSelectorDirMgr();
    
    @Override
    public void handle(javafx.event.ActionEvent arg0) {
        int iSource = 0;
        for (int i = 0; i < nFields; i++) {
            if (arg0.getSource() == btns[i]) {
                iSource = i;
                break;
            }
        }
        // create a file chooser
        
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(filemgr.getLastDir());
        chooser.setTitle("Save File");

        ExtensionFilter filter = new ExtensionFilter("XML file (*.xml)", "xml");
        chooser.setSelectedExtensionFilter(filter);
        
//        String dialogTitle = "";
        switch (iSource) {
        case idxInhList:
            chooser.setInitialFileName("inh.xml");
//            dialogTitle = "Inhibitory neurons list";
            break;
        case idxActList:
            chooser.setInitialFileName("act.xml");
//            dialogTitle = "Active neurons list";
            break;
        case idxPrbList:
            chooser.setInitialFileName("prb.xml");
//            dialogTitle = "Probed neurons list";
            break;
        }

        File option = chooser.showSaveDialog(WorkbenchDashboard.primaryStage_);
        
        if (option != null) {

            tfields[iSource].setText(option.getAbsolutePath());
//            nlistDir = option.getParent();
            filemgr.add(option.getParentFile());
        }
    }
}
