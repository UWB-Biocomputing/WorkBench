package edu.uwb.braingrid.workbenchdashboard.user;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbenchdashboard.WorkbenchApp;

public class UserView extends WorkbenchApp {

    private VBox display = new VBox();

    private Label projectDir = new Label("Projects Directory: ");
    private TextField projectDirField
            = new TextField(FileManager.getProjectsDirectory().toString());

    private Label bgRepoDir = new Label("Brain Grid Repos Directory: ");
    private TextField bgRepoDirField
            = new TextField(FileManager.getBrainGridRepoDirectory().toString());

    public UserView(Tab tab) {
        super(tab);
        tab.setText("User View");
        initAttributes();
    }

    private void initAttributes() {
        display.getChildren().addAll(projectDir, projectDirField, bgRepoDir, bgRepoDirField);
    }

    @Override
    public boolean close() {
        return true;
    }

    @Override
    public Node getDisplay() {
        return display;
    }
}
