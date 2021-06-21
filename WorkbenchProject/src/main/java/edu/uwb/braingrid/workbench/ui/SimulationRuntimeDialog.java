package edu.uwb.braingrid.workbench.ui;

import java.util.logging.Logger;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.Priority;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;
import javafx.stage.Modality;
import javafx.geometry.HPos;
import javafx.geometry.Insets; 

import edu.uwb.braingrid.workbench.WorkbenchManager;
import edu.uwb.braingrid.workbench.utils.DateTime;

/**
    * Panel for displaying simulation runtime status output to user
    * and the activation of analysis on simulation output by user.
    * This class is substantiated by the SimStartWiz object after the 
    * completion of simulation specification.
    *
    * @author Joseph Conquest
    * @version 1.0
*/
public class SimulationRuntimeDialog extends javafx.scene.control.Dialog {
    
    private static final Logger LOG = Logger.getLogger(SimulationRuntimeDialog.class.getName());
    private WorkbenchManager workbenchManager;
    private Button analyzeButton;
    private TextArea outputtextArea;
    private Label outputTextFieldLabel;
    private GridPane pane;
    
    
    public SimulationRuntimeDialog(WorkbenchManager wbmng, TextArea simStatusOutput) {
        LOG.info("new " + getClass().getName());
        workbenchManager = wbmng;
        outputtextArea = simStatusOutput;
        initComponents();
        show();
    }
    
    /**
    * Responsible for initalizing Simulation Runtime dialog and its components
    */
    private void initComponents() {
        setTitle("Simulation Runtime Environment");
        initModality(Modality.NONE);
        setResizable(true);
        
        outputTextFieldLabel = new Label("Simulation Status: ");

        outputtextArea.setEditable(false);
        outputtextArea.setWrapText(true);
        outputtextArea.setMaxWidth(Double.MAX_VALUE);
        outputtextArea.setMaxHeight(Double.MAX_VALUE);
        
        analyzeButton = new Button("Analyze");
        analyzeButton.setOnAction(e -> analyzeButtonActionEvent());
        
        pane = new GridPane();
        pane.setPadding(new Insets(10,10,0,10));
        pane.setVgap(5);
        pane.setHgap(5);
        pane.setVgrow(outputtextArea, Priority.ALWAYS);
        pane.setHgrow(outputtextArea, Priority.ALWAYS);
        pane.setMaxWidth(Double.MAX_VALUE);
        pane.setHalignment(analyzeButton, HPos.RIGHT);
        pane.add(outputTextFieldLabel, 0, 0);
        pane.add(outputtextArea, 0, 1);
        pane.add(analyzeButton, 0, 2);
        
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        getDialogPane().setContent(pane);
        getDialogPane().setPrefSize(650,490);
        
        //enable window closing
        Node closeButton = getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
        
    }
    
    /**
    * analyze action event checks script output to determine if simulation is
    * complete and updates outputTextArea accordingly
    */
    private void analyzeButtonActionEvent() {
        long timeCompleted = workbenchManager.analyzeScriptOutput();
        if (timeCompleted != DateTime.ERROR_TIME) {
            outputtextArea.appendText("\nCompleted at: " + DateTime.getTime(timeCompleted));
            setMsg();
            workbenchManager.saveProject();
        } else {
            outputtextArea.appendText("\nScript execution incomplete, try again later.");
            setMsg();
        }
    }
    
    /**
     * Sets the workbench message content. The content of this message is based
     * on the accumulated messages of produced by the functions of the workbench
     * manager.
     *
     */
     public void setMsg() {
        outputtextArea.setText(workbenchManager.getMessages());
    } 

}