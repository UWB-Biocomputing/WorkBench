package edu.uwb.braingrid.workbenchdashboard.nledit;

import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.io.File;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import edu.uwb.braingrid.workbenchdashboard.WorkbenchDashboard;
import edu.uwb.braingrid.workbenchdashboard.WorkbenchDisplay;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
/*
 * The GPatternPanel class handles generate pattern dialog window.
 * The dialog window contains two radio buttons to choose distribution patern,
 * random or regular, and two input fields to enter ratio of inhibitory, 
 * and active neurons.
 * 
 * @author Fumitaka Kawasaki
 * @version 1.2
 */
@SuppressWarnings({ "unused" })
public class GPatternPanel extends Pane {
	public RadioButton[] btns = new RadioButton[2];
	private Label[] labels = new Label[2];
	public TextField[] tfields = new TextField[2];

	public static final int idxREG = 0;
	public static final int idxRND = 1;
	public static final int idxINH = 0;
	public static final int idxACT = 1;

	public GPatternPanel() {
		
		String radio_style =
        "-fx-display:inline-block;" +
		"-fx-padding: 0 0 20 0;" ;
		
		String lbl_style =
        "-fx-display:inline-block;";

		String tfield_style =
        "-fx-display:inline-block;";


		ToggleGroup bgroup = new ToggleGroup();
		btns[idxREG] = new RadioButton("Regular pattern");
		btns[idxRND] = new RadioButton("Random pattern");
		btns[idxREG].setToggleGroup(bgroup);
		btns[idxRND].setToggleGroup(bgroup);
		VBox vb_radio_btns = new VBox(btns[idxREG], btns[idxRND]);
		vb_radio_btns.setStyle(radio_style);
		btns[idxREG].setSelected(true);
		
		tfields[idxINH] = new TextField();
		labels[idxINH] = new Label("Inhibitory neurons ratio:");
		tfields[idxACT] = new TextField();
		labels[idxACT] = new Label("Active neurons ratio:");
		VBox vb_lbls = new VBox(labels[idxINH], labels[idxACT]);
		vb_lbls.setStyle(lbl_style);
		VBox vb_tfield = new VBox(tfields[idxINH], tfields[idxACT]);
		vb_tfield.setStyle(tfield_style);
		Text instruc = new Text("Please enter a decimal <= 1 for each. The sum should also be <= 1.");

		HBox lbl_tfield = new HBox(vb_lbls, vb_tfield);
		VBox allItems = new VBox( vb_radio_btns, instruc, lbl_tfield);
		allItems.setStyle("-fx-padding: 20 0 20 20");


		getChildren().add(allItems);
		
	}
}
