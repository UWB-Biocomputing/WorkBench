package edu.uwb.braingrid.workbenchdashboard.nledit;

import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * The GPatternPanel class handles generate pattern dialog window. The dialog window contains two
 * radio rButtons to choose distribution pattern, random or regular, and two input fields to enter
 * ratio of inhibitory, and active neurons.
 *
 * @author Fumitaka Kawasaki
 * @version 1.2
 */
@SuppressWarnings({ "unused" })
public class GPatternPanel extends Pane {

    /** Number of input fields. */
    public static final int NUM_FIELDS = 2;
    /** Index of regular pattern button. */
    public static final int IDX_REG = 0;
    /** Index of random pattern button. */
    public static final int IDX_RND = 1;
    /** Index of inhibitory neurons field. */
    public static final int IDX_INH = 0;
    /** Index of active neurons field. */
    public static final int IDX_ACT = 1;

    /** The labels for this Pane. */
    private Label[] labels = new Label[NUM_FIELDS];
    /** The radio buttons for this Pane. */
    RadioButton[] rButtons = new RadioButton[NUM_FIELDS];
    /** The text fields for this Pane. */
    TextField[] tFields = new TextField[NUM_FIELDS];

    public GPatternPanel() {
        String radioStyle = "-fx-display:inline-block;" + "-fx-padding: 0 0 20 0;";
        String labelStyle = "-fx-display:inline-block;";
        String tFieldStyle = "-fx-display:inline-block;";

        ToggleGroup bgroup = new ToggleGroup();
        rButtons[IDX_REG] = new RadioButton("Regular pattern");
        rButtons[IDX_RND] = new RadioButton("Random pattern");
        rButtons[IDX_REG].setToggleGroup(bgroup);
        rButtons[IDX_RND].setToggleGroup(bgroup);
        VBox vbRadioBtns = new VBox(rButtons[IDX_REG], rButtons[IDX_RND]);
        vbRadioBtns.setStyle(radioStyle);
        rButtons[IDX_REG].setSelected(true);

        tFields[IDX_INH] = new TextField();
        labels[IDX_INH] = new Label("Inhibitory neurons ratio:");
        tFields[IDX_ACT] = new TextField();
        labels[IDX_ACT] = new Label("Active neurons ratio:");
        VBox vbLabels = new VBox(labels[IDX_INH], labels[IDX_ACT]);
        vbLabels.setStyle(labelStyle);
        VBox vbTField = new VBox(tFields[IDX_INH], tFields[IDX_ACT]);
        vbTField.setStyle(tFieldStyle);
        Text instruction = new Text("Please enter a decimal <= 1.0 for each."
                + " The sum should also be <= 1.0");

        HBox labelTField = new HBox(vbLabels, vbTField);
        VBox allItems = new VBox(vbRadioBtns, instruction, labelTField);
        allItems.setStyle("-fx-padding: 20 0 20 20");

        getChildren().add(allItems);
    }
}
