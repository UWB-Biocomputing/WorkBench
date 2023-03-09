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
        checkProgress(simName);
	}
  
  public void updateProgress(double newProgress) {
      progressBar.setValue((int)(newProgress * mainPortion * subPortion));
      progressBar.repaint();
  }
  
  public double checkProgress(String simName) {
      try {
    	Channel channel = session.openChannel("exec");
        try {
//			((ChannelSftp) channel).cd(
//			    "WorkbenchSimulations//" + simName + "//Output//Debug");
        	((ChannelExec) channel).setCommand("tail -f -n 1 " +
    			"WorkbenchSimulations//" + simName + "//Output//Debug//workbench.txt");
        	channel.connect();
        	InputStream workBenchLog = channel.getInputStream();
            BufferedReader readLog = new BufferedReader(new InputStreamReader(workBenchLog));
            String line;
            String lastline = "";
            try {
				while ((line = readLog.readLine()) != null) {
				  lastline = line;
					if(lastline.contains("Complete")) {
						updateProgress(1);
						break;
					}
					else {
						int epochIndex = lastline.indexOf("Epoch: ");
		            	int simulationIndex = lastline.indexOf("simulating time:");
		                if (epochIndex >= 0 && simulationIndex >= 0) {
		                	String percent1 = lastline.substring(epochIndex + 7, simulationIndex).trim();
		                	String percent2 = lastline.substring(simulationIndex + 16).trim();
		                	int numDiv1 = percent1.indexOf("/");
		                	int numDiv2 = percent2.indexOf("/");
		 	        		double numerator1 = Double.parseDouble(percent1.substring(0, numDiv1)) - 1;
		 	        		double denominator1 = Integer.parseInt(percent1.substring(numDiv1 + 1, percent1.length()));
		 	        		double numerator2 = Double.parseDouble(percent2.substring(0, numDiv2));
		 	        		double denominator2 = Integer.parseInt(percent2.substring(numDiv2 + 1, percent2.length()));
		 	        		updateProgress(numerator1/denominator1 + numerator2/denominator2/denominator1);
		                }
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	} catch (JSchException e) {
		e.printStackTrace();
	}
    return 0;
  }
}
