package edu.uwb.braingrid.workbench.ui;

import java.util.logging.Logger;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.Priority;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;

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
	
	
	public SimulationRuntimeDialog(WorkbenchManager wbmng) {
		LOG.info("Opening Simulation Runtime Dialog for Sim Execution");
		workbenchManager = wbmng;
		initComponents();
		show();
	}
	
	private void initComponents() {
		setTitle("Simulation Runtime Environment");
		initModality(Modality.NONE);
		setResizable(true);
		
		Label outputTextFieldLabel = new Label("Simulation Status: ");
		analyzeButton = new Button("Analyze");
		analyzeButton.setOnAction(e -> analyzeButtonActionEvent());

		outputtextArea = new TextArea("Script Generated...\nRunning Script..");
		outputtextArea.setEditable(false);
		outputtextArea.setWrapText(true);
		
		outputtextArea.setMaxWidth(Double.MAX_VALUE);
		outputtextArea.setMaxHeight(Double.MAX_VALUE);
		
		GridPane pane = new GridPane();
		pane.setVgrow(outputtextArea, Priority.ALWAYS);
		pane.setHgrow(outputtextArea, Priority.ALWAYS);
		pane.setMaxWidth(Double.MAX_VALUE);
		pane.add(outputTextFieldLabel, 0, 0);
		pane.add(outputtextArea, 0, 1);
		pane.add(analyzeButton, 1, 2);

		// Set expandable Exception into the dialog pane.
		//getDialogPane().setExpandableContent(pane);
		getDialogPane().setContent(pane);
		
		ButtonType exitButton = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
		getDialogPane().getButtonTypes().add(exitButton);

		
	}
	
	private void analyzeButtonActionEvent() {
		long timeCompleted = workbenchManager.analyzeScriptOutput();
		if (timeCompleted != DateTime.ERROR_TIME) {
			System.out.println("Completed at: " + DateTime.getTime(timeCompleted));
		} else {
			System.out.println("Script execution incomplete, try again later.");
		}
	}


}