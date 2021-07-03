package edu.uwb.braingrid.workbench.provvisualizer;

import java.util.logging.Logger;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import org.controlsfx.control.ToggleSwitch;

import edu.uwb.braingrid.workbench.provvisualizer.view.VisCanvas;
import edu.uwb.braingrid.workbench.provvisualizer.controller.ProVisCtrl;
import edu.uwb.braingrid.workbenchdashboard.WorkbenchApp;

/**
 * Code that builds and substantiates ProVis tab in WorkbenchApp. Declares GUI components and sets
 * basics of layout parameters. Substantiates ProVisController object. Updates in this class should
 * be reflected in ProVisCtrl.java.
 *
 * @author Joseph Conquest and Tom Wong
 * @version 1.3
 */
public class ProVis extends WorkbenchApp {

    private static final Logger LOG = Logger.getLogger(ProVis.class.getName());

    private BorderPane borderPane = new BorderPane();
    private VisCanvas visCanvas = new VisCanvas();
    private BorderPane canvasPane = new BorderPane();
    private Slider adjustForceSlider = new Slider(1.0, 100, 10);
    private ToggleSwitch stopForces = new ToggleSwitch("Stop Vertices     ");
    private ToggleSwitch showNodeIds = new ToggleSwitch("All Vertex IDs     ");
    private ToggleSwitch showRelationships = new ToggleSwitch("All Relationships");
    private ToggleSwitch showLegend = new ToggleSwitch("Legend               ");
    private ToggleSwitch builderModeToggle = new ToggleSwitch("Builder Mode");
    private Button chooseFileBtn = new Button("Choose Provenance");
    private Button importFileBtn = new Button("Import Provenance");
    private Button buildFromPrevButton = new Button("Derive New Activity");
    private Button clearPresetsButton = new Button("Clear Selected");
    private Label visualizerControlLabel = new Label("VISUALIZER CONTROL");
    private Label builderControlLabel = new Label("BUILDER CONTROL");
    private Label vertexSpeedLabel = new Label("Vertex Speed:");
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
    private GridPane builderLabelsAndTextFields;
    private ProVisCtrl proVisCtrl;

    public ProVis(Tab tab) {
        super(tab);
        LOG.info("new " + getClass().getName());

        adjustForceSlider.setBlockIncrement(20);
        adjustForceSlider.setMajorTickUnit(20);
        adjustForceSlider.setShowTickLabels(true);
        adjustForceSlider.setShowTickMarks(true);
        adjustForceSlider.setSnapToTicks(true);

        stopForces.setMnemonicParsing(false);
        showNodeIds.setMnemonicParsing(false);
        showRelationships.setMnemonicParsing(false);
        showLegend.setMnemonicParsing(false);
        builderModeToggle.setMnemonicParsing(false);

        proVisCtrl = new ProVisCtrl(this, visCanvas, canvasPane, adjustForceSlider, stopForces,
                showNodeIds, showRelationships, showLegend, builderModeToggle, importFileBtn,
                chooseFileBtn, inputTextField, probedTextField, activeTextField,
                inhibitoryTextField, bGVersionTextField, clearPresetsButton, buildFromPrevButton);

        canvasPane.getChildren().add(visCanvas);

        initLabelsAndTextFields();

        VBox vb = new VBox();
        vb.setSpacing(10);
        vb.getChildren().addAll(visualizerControlLabel, visualizerControlSeparator, importFileBtn,
                chooseFileBtn, vertexSpeedLabel, adjustForceSlider, stopForces, showNodeIds,
                showRelationships, showLegend, controlPanelSeparator, builderControlLabel,
                builderControlSeparator, builderModeToggle, builderLabelsAndTextFields,
                clearPresetsButton, buildFromPrevButton);

        vb.getStyleClass().add("controls");
        borderPane.setCenter(canvasPane);
        borderPane.setRight(vb);

        super.setTitle("ProVis");
    }

    @Override
    public boolean close() {
        return true;
    }

    @Override
    public Node getDisplay() {
        return borderPane;
    }

    private void initLabelsAndTextFields() {
        inputTextField.setEditable(false);
        probedTextField.setEditable(false);
        activeTextField.setEditable(false);
        inhibitoryTextField.setEditable(false);
        bGVersionTextField.setEditable(false);

        builderLabelsAndTextFields = new GridPane();
        builderLabelsAndTextFields.setHgap(5);
        builderLabelsAndTextFields.setVgap(5);
        builderLabelsAndTextFields.setPadding(new Insets(5));

        builderLabelsAndTextFields.add(selectedInputLabel, 0, 0);
        builderLabelsAndTextFields.add(inputTextField, 1, 0);
        builderLabelsAndTextFields.add(selectedProbedLabel, 0, 1);
        builderLabelsAndTextFields.add(probedTextField, 1, 1);
        builderLabelsAndTextFields.add(selectedActiveLabel, 0, 2);
        builderLabelsAndTextFields.add(activeTextField, 1, 2);
        builderLabelsAndTextFields.add(selectedInhibitoryLabel, 0, 3);
        builderLabelsAndTextFields.add(inhibitoryTextField, 1, 3);
        builderLabelsAndTextFields.add(selectedBGVersionLabel, 0, 4);
        builderLabelsAndTextFields.add(bGVersionTextField, 1, 4);
    }
}
