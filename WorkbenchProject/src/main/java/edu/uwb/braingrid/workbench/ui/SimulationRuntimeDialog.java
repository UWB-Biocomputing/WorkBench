package edu.uwb.braingrid.workbench.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
	* Panel for displaying simulation runtime status output to user
	* and the activation of analysis on simulation output by user.
	* This class is substantiated by the SimStartWiz object after the
	* completion of simulation specification.
	*
	* @author Joseph Conquest
	* @version 1.0
*/
public class SimulationRuntimeDialog extends javax.swing.JDialog {
	
	private javax.swing.JButton analyzeButton;
	private javax.swing.JButton exitButton;
	
	
	private static final Logger LOG = Logger.getLogger(SimulationRuntimeDialog.class.getName());
	
	public SimulationRuntimeDialog() {
		LOG.info("Opening Simulation Runtime Panel for Sim Execution");
		initComponents();
		center();
		setVisible(true);
	}
	
	private void initComponents() {
		this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Simulation Runtime Environment");
		setLocationByPlatform(true);
		setModal(true);
		
		analyzeButton = new javax.swing.JButton();
		exitButton = new javax.swing.JButton();
		
		analyzeButton.setText("Analyze");
		analyzeButton.setEnabled(true);
		analyzeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				analyzeButtonActionPerformed(evt);
			}
		});
	}
	
	private void analyzeButtonActionPerformed(java.awt.event.ActionEvent evt) {
		
	}
	
	/**
	 * Centers this panel
	 */
	private void center() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	}
}