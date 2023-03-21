package edu.uwb.braingrid.workbench.ui;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JFrame;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import edu.uwb.braingrid.workbench.comm.SecureFileTransfer;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Implements the progress bar to track the progress of the on-going simulations
 *
 * Created by Ben Yang on 16/03/2023.
 */
public class WorkBenchProgressBar {
    private ProgressBar progressBar;
    private int mainPortion;
    private int subPortion;
    private SecureFileTransfer progressTracker;
    private String simName;
    private JFrame frame;
    
    public ProgressBar getProgressBar() {
    	return progressBar;
    }
    
    /**
     * Constructor of the progress bar.
     *
     * @param currentProgress  The progress of Epoch
     * @param subProgress  The progress of simulation of the current Epoch
     * @param tracker  Tracker connects to the remote machine and check for progress update
     * @param simName  The name of the simulation
     * @param sceneContent  The panel for the set of that progress bar(textarea and download button)
     * @param scene  The scene that contains the panel
     * @param stage  The main stage for all progress bars
     */
    public WorkBenchProgressBar(String currentProgress, String subProgress,
      SecureFileTransfer tracker, String simName, StackPane sceneContent,
      Scene scene, Stage stage) {
      int numDiv = currentProgress.indexOf("/");
      int numDiv2 = subProgress.indexOf("/");
      int numerator = Integer.parseInt(currentProgress.substring(0, numDiv));
      int numerator2 = Integer.parseInt(subProgress.substring(0, numDiv2));
      int denominator = Integer.parseInt(
        currentProgress.substring(numDiv + 1, currentProgress.length()));
      int denominator2 = Integer.parseInt(
        subProgress.substring(numDiv2 + 1, subProgress.length()));
      this.mainPortion = denominator;
      this.subPortion = denominator2;
      this.progressTracker = tracker;
      this.simName = simName;
      //0, denominator * denominator2
      this.progressBar = new ProgressBar();

      int current = numerator * numerator2 + numerator;
      double progress = (double) current / (double) (denominator * denominator2);

      progressBar.setProgress(progress);
      final int length = 200;
      final int height = 30;
      progressBar.setPrefWidth(length);
      progressBar.setPrefHeight(height);
      sceneContent.getChildren().add(progressBar);
      Platform.runLater(() -> {
          stage.setScene(scene);
      });
      // Set the size of the frame and make it visible
      progressTracker.checkProgress(simName, this);
    }
 /**
  * Updates the progress of the last simulation.
  *
  * @param newProgress  The updated progress of the simulation
  *
  */
  public void updateProgress(double newProgress) {
    progressBar.setProgress(newProgress);
    if (newProgress == 1) {
    	Platform.runLater(() -> {
    	  frame.setVisible(false);
        });
    }
  }
}
