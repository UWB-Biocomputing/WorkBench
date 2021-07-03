package edu.uwb.braingrid.workbenchdashboard.userView;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import edu.uwb.braingrid.workbenchdashboard.WorkbenchApp;
import edu.uwb.braingrid.workbenchdashboard.userModel.User;

public class UserView extends WorkbenchApp {

    private VBox display = new VBox();

    private Label userDir = new Label("Main Directory: ");
    private TextField userDirField = new TextField(User.getInstance().getRootDir());

    private Label bgRepoDir = new Label("Brain Grid Repos Directory: ");
    private TextField bgRepoDirField
            = new TextField(User.getInstance().getBrainGridRepoDirectory());

    public UserView(Tab tab) {
        super(tab);
        tab.setText("User View");
        initAttributes();
    }

    private void initAttributes() {
        display.getChildren().addAll(userDir, userDirField, bgRepoDir, bgRepoDirField);
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
