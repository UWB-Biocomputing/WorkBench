package edu.uwb.braingrid.workbench.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import org.apache.commons.lang3.ObjectUtils.Null;

import edu.uwb.braingrid.workbench.comm.SecureFileTransfer;

/**
 * This is the controller for the GUI for all of the
 * progress bar. Everything will be keep tracked of on
 * the mainstage.
 *
 */
public final class SimulationsFrame {
    private Stage mainFrame;
    private ArrayList<Scene> scenes;
    private ArrayList<Button> downLoadButtons;
    private ArrayList<TextArea> downLoadTextAreas;
    private ArrayList<ProgressBar> progressBars;
    private static SimulationsFrame mainStage = null;

    /**
     * Return the instance of the mainStage.
     *
     *  @return it returns the an static instance of the SimulationFrame
     */
    public static SimulationsFrame returnInstance() {
      if (mainStage == null) {
        mainStage = new SimulationsFrame();
      }
      return mainStage;
    }

  private SimulationsFrame() {
    mainFrame = new Stage();
    mainFrame.setTitle("Last Simulations");
  }

   /**
    * Connects to the last simulation.
    *
    *  @param simName  The  last simulation to connect to.
    *  @param currentProgress  The percentage of completed Epoch.
    *  @param subProgress  The percentage of completed simulation in that Epoch.
    *  @param tracker  The keep track of the progress
    */
    public void addSimulation(String simName, String currentProgress,
        String subProgress, SecureFileTransfer tracker) {
        StackPane sceneContent = new StackPane();
        Scene scene = new Scene(sceneContent);
        Button newButton = new Button("download");
        downLoadButtons.add(newButton);
        sceneContent.getChildren().add(newButton);
        TextArea simNameText = new TextArea(simName);
        downLoadTextAreas.add(simNameText);
        sceneContent.getChildren().add(simNameText);
        WorkBenchProgressBar progressBar = new WorkBenchProgressBar(
                currentProgress, subProgress, tracker, simName, sceneContent,
                scene, mainStage.mainFrame);
    }
}
