package edu.uwb.braingrid.workbench.ui;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JProgressBar;
import javax.swing.JFrame;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import edu.uwb.braingrid.workbench.comm.SecureFileTransfer;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;

/**
 * Implements the progress bar to track the progress of the on-going simulations
 *
 * Created by Ben Yang on 16/03/2023.
 */
public class WorkBenchProgressBar {
    private int progress;
    private ProgressBar progressBar;
    private int mainPortion;
    private int subPortion;
    private SecureFileTransfer progressTracker;
    private String simName;
    private JFrame frame;
    /**
     * Constructor of the progress bar.
     *
     * @param currentProgress  The progress of Epoch
     * @param subProgress  The progress of simulation of the current Epoch
     * @param tracker  Tracker connects to the remote machine and check for progress update
     * @param simName  The name of the simulation
     */
    public WorkBenchProgressBar(String currentProgress, String subProgress,
      SecureFileTransfer tracker, String simName, Scene scene) {
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
      this.progress = numerator / denominator;
      this.progressTracker = tracker;
      this.simName = simName;
      this.progressBar = new JProgressBar(0, denominator * denominator2);
      int current = numerator * numerator2 + numerator;
      progressBar.setValue(current);
      frame = new JFrame("Progress Bar");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      progressBar.setStringPainted(true);
      frame.add(progressBar);
      final int length = 300;
      final int height = 75;
      frame.setSize(length, height);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
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
    progressBar.setValue((int) (newProgress * mainPortion * subPortion));
    progressBar.repaint();
    if (newProgress == 1) {
     frame.setVisible(false);
    }
  }
}
