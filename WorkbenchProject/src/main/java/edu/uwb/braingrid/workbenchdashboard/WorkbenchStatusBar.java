package edu.uwb.braingrid.workbenchdashboard;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import edu.uwb.braingrid.workbenchdashboard.utils.ThreadManager;

public class WorkbenchStatusBar extends HBox {

    /** The time between status bar updates, in seconds. */
    public static final int UPDATE_WAIT_TIME = 5;
    private static Label statusLabel = new Label(ThreadManager.getStatus());

    public WorkbenchStatusBar() {
        updateStatusMessage();
        this.setAlignment(Pos.BOTTOM_RIGHT);
        statusLabel.setAlignment(Pos.BOTTOM_RIGHT);
        this.getChildren().add(statusLabel);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(UPDATE_WAIT_TIME),
                event -> updateStatusMessage()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        getStyleClass().add("updates-bar");
    }

    private static void updateStatusMessage() {
        String status = ThreadManager.getStatus();
        if (ThreadManager.getProcessesRunning() > 0) {
            status += ", " + ThreadManager.getProcessesRunning() + " Process Running";
        }
        statusLabel.setText(status);
    }
}
