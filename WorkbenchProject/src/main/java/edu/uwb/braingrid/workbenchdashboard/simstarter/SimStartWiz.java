package edu.uwb.braingrid.workbenchdashboard.simstarter;

import edu.uwb.braingrid.workbench.WorkbenchManager;
import edu.uwb.braingrid.workbench.utils.DateTime;
import edu.uwb.braingrid.workbench.ui.SimulationRuntimeDialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Date;
import java.util.logging.Logger;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javafx.scene.control.TextArea;

/**
 * The SimStartWiz is responsible for handling all back-end events associated with
 * 	running the simulation start wizard.
 *
 * @author Joseph Conquest
 * @version 1.3
 */
public class SimStartWiz {

    /**
	 * Class Variables and objects
	 */
	private static final Logger LOG = Logger.getLogger(SimStartWiz.class.getName());
	private static final long serialVersionUID = 1L;
	private WorkbenchManager workbenchManager = new WorkbenchManager();
	private SimulationRuntimeDialog srd;
	private TextArea msgText = new TextArea("");
	private SimManager simManager;
	private String commitVersionSelected = null;

    /**
     * Prompts the user to select files for the simulation input. InputAnalyzer
     * files are created with NLEdit or by hand in XML. InputAnalyzer files
     * represent lists of neurons with regard to their position in a neuron
     * array (e.g. position 12 is x: 1, y: 2 on a 10x10 grid)
     */
	 private boolean configureSimulation() {
		 boolean wasSuccessful = false;
		 if (workbenchManager.configureSimulation()) {
			workbenchManager.invalidateScriptGenerated();
			workbenchManager.invalidateScriptRan();
			workbenchManager.invalidateScriptAnalyzed();
			wasSuccessful = true;
		}
		setMsg();
		return wasSuccessful;
	 }
	 
	 /**
     * Prompts the user to select files for the simulation input. InputAnalyzer
     * files are created with NLEdit or by hand in XML. InputAnalyzer files
     * represent lists of neurons with regard to their position in a neuron
     * array (e.g. position 12 is x: 1, y: 2 on a 10x10 grid)
	 *
	 * @param simInputPresets contains strings of inputs for configuring Sim
     */
	 private boolean configureSimulation(String simInputPresets, HashMap<Character,String> nListPresets) {
		 if(simInputPresets == null && nListPresets == null) return configureSimulation();
		 boolean wasSuccessful = false;
		 if (workbenchManager.configureSimulation(simInputPresets, nListPresets)) {
			workbenchManager.invalidateScriptGenerated();
			workbenchManager.invalidateScriptRan();
			workbenchManager.invalidateScriptAnalyzed();
			wasSuccessful = true;
		}
		setMsg();
		return wasSuccessful;
	 }

	/**
	 * Prompts the user to specify the simulator used. This should be the file that
	 * was invoked, which used the input files specified, in order to write the
	 * output file that was specified.
	 */
	private boolean specifyScript() {
		boolean wasSuccessful = false;
		if (workbenchManager.specifyScript()) {
			workbenchManager.invalidateScriptGenerated();
			workbenchManager.invalidateScriptRan();
			workbenchManager.invalidateScriptAnalyzed();
			wasSuccessful = true;
		}
		setMsg();
		return wasSuccessful;
	}
	
	/**
	 * Prompts the user to specify the simulator used. This should be the file that
	 * was invoked, which used the input files specified, in order to write the
	 * output file that was specified.
	 * @param runtimeSpecifcations is git commit version to be pulled
	 */
	private boolean specifyScript(String runtimeSpecifcations) {
		if(runtimeSpecifcations == null) return specifyScript();
		boolean wasSuccessful = false;
		if (workbenchManager.specifyScript(runtimeSpecifcations)) {
		workbenchManager.invalidateScriptGenerated();
		workbenchManager.invalidateScriptRan();
		workbenchManager.invalidateScriptAnalyzed();
		wasSuccessful = true;
		}
		setMsg();
		return wasSuccessful;
	}
	
	private boolean generateScript() {
		boolean wasSuccessful = false;
		if (workbenchManager.generateScript()) {
			wasSuccessful = true;
		}
		setMsg();
		return wasSuccessful;
	}
	
	/**
	 * Runs the script on the remote host.
	 *
	 * Connection information is entered in a SSHConnectionDialog
	 */
	private boolean runScript() {
		boolean wasSuccessful = false;
		if (workbenchManager.runScript()) {
			String time = DateTime.getTime(new Date().getTime());
			String msg = "Script execution started at: " + time;
			wasSuccessful = true;
		}
		setMsg();
		return wasSuccessful;
	}
	
    /**
	 * SimStartWiz(WorkbenchManager workbenchManager)
     * Responsible for allocating this frame and initializing auto-generated, as
     * well as custom, members
	 *
     */
    public SimStartWiz() {
		LOG.info("new " + getClass().getName());
		boolean cancelButtonClicked = false;
		if (workbenchManager.newProject()) { 
			if(configureSimulation()) {
				if(specifyScript()) {
					if(generateScript()) 
						if(runScript())
							srd = new SimulationRuntimeDialog(workbenchManager, msgText);
				}
				else {
					cancelButtonClicked = true;
				}
			}
		}
		simManager = new SimManager(workbenchManager);
		if(cancelButtonClicked) simManager.saveProject();
    }
	
	public SimStartWiz(String simSpecifications, String runtimeSpecifcations, HashMap<Character, String> nListPresets) {
		LOG.info("new " + getClass().getName());
		boolean cancelButtonClicked = false;
		if (workbenchManager.newProject()) { 
			if(configureSimulation(simSpecifications, nListPresets)) {
				if(specifyScript(runtimeSpecifcations)) {
					if(generateScript()) 
						if(runScript())
							srd = new SimulationRuntimeDialog(workbenchManager, msgText);
				}
				else {
					cancelButtonClicked = true;
				}
			}
		}
		simManager = new SimManager(workbenchManager);
		if(cancelButtonClicked) simManager.saveProject();
	}

   /**
     * Sets the workbench message content. The content of this message is based
     * on the accumulated messages of produced by the functions of the workbench
     * manager.
     *
     */
     public void setMsg() {
        msgText.setText(workbenchManager.getMessages());
    } 

}
