package edu.uwb.braingrid.workbench.provvisualizer;

import java.util.logging.Logger;

import org.controlsfx.control.ToggleSwitch;

import edu.uwb.braingrid.workbench.provvisualizer.view.VisCanvas;
import edu.uwb.braingrid.workbench.provvisualizer.controller.ProVisCtrl;
import edu.uwb.braingrid.workbenchdashboard.WorkbenchApp;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.HPos;
import javafx.geometry.Insets;

/**
* Code that builds and substantiates ProVis tab in WorkbenchApp
* Declares GUI components and sets basics of layout parameters
* Substantiates ProVisController object
* Updates in this class should be reflected in ProVisCtrl.java
*
* @author Joseph Conquest and Tom Wong
* @version 1.3
*/
public class ProVis extends WorkbenchApp {
	private static final Logger LOG = Logger.getLogger(ProVis.class.getName());

	public ProVis(Tab tab) {
		super(tab);
		LOG.info("new " + getClass().getName());
		
		adjust_force_slider_.setBlockIncrement(20);
		adjust_force_slider_.setMajorTickUnit(20);
		//adjust_force_slider_.setMinorTickCount(1);
		adjust_force_slider_.setShowTickLabels(true);
		adjust_force_slider_.setShowTickMarks(true);
		adjust_force_slider_.setSnapToTicks(true);

		stop_forces_.setMnemonicParsing(false);
		show_node_ids_.setMnemonicParsing(false);
		show_relationships_.setMnemonicParsing(false);
		show_legend_.setMnemonicParsing(false);
		builderModeToggle.setMnemonicParsing(false);

		pro_vis_ctrl_ = new ProVisCtrl(this, vis_canvas_, canvas_pane_, adjust_force_slider_, stop_forces_,
				show_node_ids_, show_relationships_, show_legend_, builderModeToggle, import_file_btn_, choose_file_btn_, 
				inputTextField, probedTextField, activeTextField, inhibitoryTextField, bGVersionTextField, reRunActivityButton, buildFromPrevButton);

		canvas_pane_.getChildren().add(vis_canvas_);
		
		initLabelsandTextFields();

		VBox vb = new VBox();
		vb.setSpacing(10);
		vb.getChildren().addAll(visualizerControlLabel, visualizerControlSeparator, import_file_btn_, choose_file_btn_, stop_forces_, show_node_ids_, show_relationships_, show_legend_, adjust_force_slider_,
				controlPanelSeparator, builderControlLabel, builderControlSeparator, builderModeToggle, buidlerLabelsandTextfields, reRunActivityButton, buildFromPrevButton);
	
		vb.getStyleClass().add("controls");
		bp_.setCenter(canvas_pane_);
		bp_.setRight(vb);
		
		//pro_vis_ctrl_.openUniversalProvenance();
		
		super.setTitle("ProVis");   
	}

	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Node getDisplay() {
		// TODO Auto-generated method stub
		return bp_;
	}
	
	private void initLabelsandTextFields() {
		inputTextField.setEditable(false);
		probedTextField.setEditable(false);
		activeTextField.setEditable(false);
		inhibitoryTextField.setEditable(false);
		bGVersionTextField.setEditable(false);
		
		buidlerLabelsandTextfields = new GridPane();
		buidlerLabelsandTextfields.setHgap(5);
		buidlerLabelsandTextfields.setVgap(5);
		buidlerLabelsandTextfields.setPadding(new Insets(5));
		
		
		
		buidlerLabelsandTextfields.add(selectedInputLabel,0,0);
		buidlerLabelsandTextfields.add(inputTextField,1,0);
		buidlerLabelsandTextfields.add(selectedProbedLabel,0,1);
		buidlerLabelsandTextfields.add(probedTextField,1,1);
		buidlerLabelsandTextfields.add(selectedActiveLabel,0,2);
		buidlerLabelsandTextfields.add(activeTextField,1,2);
		buidlerLabelsandTextfields.add(selectedInhibitoryLabel,0,3);
		buidlerLabelsandTextfields.add(inhibitoryTextField,1,3);
		buidlerLabelsandTextfields.add(selectedBGVersionLabel,0,4);
		buidlerLabelsandTextfields.add(bGVersionTextField,1,4);

	}

	// private SplitPane sp_ = new SplitPane();
	BorderPane bp_ = new BorderPane();
	private VisCanvas vis_canvas_ = new VisCanvas();
	private BorderPane canvas_pane_ = new BorderPane();
	private Slider adjust_force_slider_ = new Slider(1.0, 100, 10);
	private ToggleSwitch stop_forces_ = new ToggleSwitch("Stop Vertices     ");
	private ToggleSwitch show_node_ids_ = new ToggleSwitch("All Vertex IDs     ");
	private ToggleSwitch show_relationships_ = new ToggleSwitch("All Relationships");
	private ToggleSwitch show_legend_ = new ToggleSwitch("Legend              ");
	private ToggleSwitch builderModeToggle = new ToggleSwitch("Builder Mode");
	private Button choose_file_btn_ = new Button("Choose Provenance");
	private Button import_file_btn_ = new Button("Import Provenance");
	private Button reRunActivityButton = new Button("Re-Run Activity");
	private Button buildFromPrevButton = new Button("Derive New Activity");
	private Label visualizerControlLabel = new Label("VISUALIZER CONTROL"); 
	private Label builderControlLabel = new Label("BUILDER CONTROL");
	private Label selectedInputLabel = new Label("Input: ");
	private Label selectedProbedLabel = new Label("Probed: ");
	private Label selectedActiveLabel = new Label("Active: ");
	private Label selectedInhibitoryLabel = new Label("Inhibitory: ");
	private Label selectedBGVersionLabel = new Label("BrainGrid Version: ");
	private TextField inputTextField = new TextField();
	private TextField probedTextField = new TextField();
	private TextField activeTextField = new TextField();   
	private TextField inhibitoryTextField = new TextField();
	private TextField bGVersionTextField = new TextField();
	private Separator controlPanelSeparator = new Separator();
	private Separator visualizerControlSeparator = new Separator();
	private Separator builderControlSeparator = new Separator();
	private GridPane buidlerLabelsandTextfields;
	private ProVisCtrl pro_vis_ctrl_; 
}
