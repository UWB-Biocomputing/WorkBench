package edu.uwb.braingrid.workbench.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import org.apache.commons.lang3.ObjectUtils.Null;

public class SimulationsFrame {
	private Stage mainFrame;
	private ArrayList<Scene> scenes;
	private ArrayList<Button> downLoadButtons;
	private ArrayList<TextArea> downLoadTextAreas;
	private ArrayList<ProgressBar> progressBars;
	private static SimulationsFrame mainStage = null;
	
	public static SimulationsFrame returnInstance() {
		if(mainStage == null) {
			mainStage = new SimulationsFrame();
		}
		return mainStage;
	}
	
	private SimulationsFrame() {
		mainFrame = new Stage();
		mainFrame.setTitle("Last Simulations");
	}
	
	private void addSimulation(JProgressBar progressBar, String simName) {
		Scene newScene = new Scene();
		Button newButton = new Button("download");
		downLoadButtons.add(newButton);
		mainFrame.(progressBar);
		TextArea simNameText = new TextArea(simName);
		mainFrame.add(simNameText);
	}
}
