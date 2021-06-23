package edu.uwb.braingrid.workbenchdashboard.nledit;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * The AProbesPanel class handles arrange probed neurons dialog window. The dialog window contains
 * one input field to enter number of probes.
 *
 * @author Fumitaka Kawasaki
 * @version 1.2
 */
public class AProbesPanel extends Pane {

    private Label label = new Label("Number of probes:");
    private TextField tField = new TextField();;

    public AProbesPanel() {
        HBox hbox = new HBox(label, tField);
        hbox.setStyle("-fx-padding: 20 0 20 20");
        getChildren().add(hbox);
    }

    public TextField getTField() {
        return tField;
    }
}
