package edu.uwb.braingrid.workbenchdashboard.user;

import java.nio.file.Paths;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import edu.uwb.braingrid.workbenchdashboard.WorkbenchApp;

public class UserView extends WorkbenchApp {

    private VBox display = new VBox();

    private Label projectsDirLabel = new Label("Projects Directory: ");
    private TextField projectsDirField
            = new TextField(user.getProjectsDirectory().toString());

    private Label bgRepoDirLabel = new Label("BrainGrid Repo Directory: ");
    private TextField bgRepoDirField
            = new TextField(user.getBrainGridRepoDirectory().toString());

    private Label simulationsDirLabel = new Label("Simulations Directory: ");
    private TextField simulationsDirField
            = new TextField(user.getSimulationsDirectory());

    private Button saveButton = new Button("Save");

    private static User user = User.getUser();

    public UserView(Tab tab) {
        super(tab);
        tab.setText("User View");
        initAttributes();
    }

    private void initAttributes() {
        display.getChildren().addAll(projectsDirLabel, projectsDirField, bgRepoDirLabel,
                bgRepoDirField, simulationsDirLabel, simulationsDirField, saveButton);

        saveButton.setOnAction(actionEvent -> {
            user.setProjectsDirectory(Paths.get(projectsDirField.getText()));
            user.setBrainGridRepoDirectory(Paths.get(bgRepoDirField.getText()));
            user.setSimulationsDirectory(simulationsDirField.getText());
            User.save();
        });
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
