package edu.uwb.braingrid.workbench.ui;

import java.util.logging.Logger;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.Priority;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;

/**
	* Panel for displaying simulation runtime status output to user
	* and the activation of analysis on simulation output by user.
	* This class is substantiated by the SimStartWiz object after the ////////////But it is being called by DynamicInputConfigurationDialog??
	* completion of simulation specification.
	*
	* @author Joseph Conquest
	* @version 1.0
*/
public class SimulationRuntimeDialog extends javafx.scene.control.Dialog {
	
	private static final Logger LOG = Logger.getLogger(SimulationRuntimeDialog.class.getName());
	
	public SimulationRuntimeDialog() {
		LOG.info("Opening Simulation Runtime Dialog for Sim Execution");
		initComponents();
		show();
	}
	
	private void initComponents() {
		setTitle("Simulation Runtime Environment");
		initModality(Modality.NONE);
		
		//ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		
		Label outputTextFieldLabel = new Label("Simulation Status: ");

		TextArea outputtextArea = new TextArea("Script Generated...\nRunning Script..");
		outputtextArea.setEditable(false);
		outputtextArea.setWrapText(true);
		
		outputtextArea.setMaxWidth(Double.MAX_VALUE);
		outputtextArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(outputtextArea, Priority.ALWAYS);
		GridPane.setHgrow(outputtextArea, Priority.ALWAYS);
		
		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(outputTextFieldLabel, 0, 0);
		expContent.add(outputtextArea, 0, 1);
		//expContent.add(cancelButton, 0, 2);

		// Set expandable Exception into the dialog pane.
		getDialogPane().setExpandableContent(expContent);

		
	}


}