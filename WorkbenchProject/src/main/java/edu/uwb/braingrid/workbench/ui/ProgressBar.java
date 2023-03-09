package edu.uwb.braingrid.workbench.ui;

import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.*;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import edu.uwb.braingrid.workbench.comm.SecureFileTransfer;

public class ProgressBar {
	private int progress;
	private JProgressBar progressBar;
	private int mainPortion;
	private int subPortion;
	private SecureFileTransfer progressTracker;
	private String simName;
	private Session session;	
	public ProgressBar(String currentProgress, String subProgress, SecureFileTransfer tracker, String simName, Session session) {
		int numDiv = currentProgress.indexOf("/");
		int numDiv2 = subProgress.indexOf("/");
		int numerator = Integer.parseInt(currentProgress.substring(0, numDiv));
		int numerator2 = Integer.parseInt(subProgress.substring(0, numDiv2));
		int denominator = Integer.parseInt(currentProgress.substring(numDiv + 1, currentProgress.length()));
		int denominator2 = Integer.parseInt(subProgress.substring(numDiv2 + 1, subProgress.length()));
		this.session = session;
		this.mainPortion = denominator;
		this.subPortion = denominator2;
		this.progress = numerator/denominator;
		this.progressTracker = tracker;
		this.simName = simName;
		this.progressBar = new JProgressBar(0 , denominator * denominator2);
		int current = numerator * numerator2 + numerator;
		progressBar.setValue(current);
		JFrame frame = new JFrame("Progress Bar");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        progressBar.setStringPainted(true);
        frame.add(progressBar);
        frame.setSize(300, 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        // Set the size of the frame and make it visible
        progressTracker.checkProgress(simName, this);
	}
  
  public void updateProgress(double newProgress) {
      progressBar.setValue((int)(newProgress * mainPortion * subPortion));
      progressBar.repaint();
  }
}
