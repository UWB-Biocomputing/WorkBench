package edu.uwb.braingrid.workbenchdashboard.simstarter;

import edu.uwb.braingrid.workbench.WorkbenchManager;
import edu.uwb.braingrid.workbench.utils.DateTime;
import edu.uwb.braingrid.workbench.ui.SimulationRuntimeDialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Date;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javafx.scene.control.TextArea;

/**
 * The SimStartWiz is responsible for handling all back-end events associated with
 * 	running the simulation start wizard. This code was adapted from Davis WorkbenchControlFrame
 *
 * @author Joseph Conquest
 * @version 0.1
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

    /**
	 * configureSimulation()
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
/*	 
    private void configureSimulationOldSchool() {
		if (workbenchManager.runScript()) {
			workbenchManager.invalidateScriptGenerated();
			workbenchManager.invalidateScriptRan();
			workbenchManager.invalidateScriptAnalyzed();
		}
		if (workbenchManager.configureSimulation())
			if(workbenchManager.specifyScript())
				if (workbenchManager.generateScript())
					if (workbenchManager.runScript())
						srd = new SimulationRuntimeDialog(workbenchManager);
        //setMsg();
       // pack();
    }
 */
	/**
	 * Prompts the user to specify the simulator used. This should be the file that
	 * was invoked, which used the input files specified, in order to write the
	 * output file that was specified.
	 */
	private boolean specifyScript() {
		boolean wasSuccessful = false;
		if (workbenchManager.specifyScript()) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			workbenchManager.invalidateScriptGenerated();
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			workbenchManager.invalidateScriptRan();
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			workbenchManager.invalidateScriptAnalyzed();
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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
		if (workbenchManager.newProject()) 
			if(configureSimulation())
				if(specifyScript())
					if(generateScript())
						if(runScript())
							srd = new SimulationRuntimeDialog(workbenchManager);
    }


 //  @Override
    /**
     * Fits the window to the maximum width and height of all the contained
     * components. The minimum size is reset to the current size after the pack
     * to make sure that the window can not get any smaller. As in the parent
     * component's implementation of pack, the window will always be within the
     * bounds of it's maximum size.
     */
/*    public void pack() {
        super.pack();
        setMinimumSize(getSize());
    }
*/

/*    private void updateProjectOverview() {
        resetUILabelText();
        projectTitleTextLabel.setText(workbenchManager.getProjectName());
        displaySimConfigFile();
        updateSimOverview();
        //transferProgressBar.setVisible(workbenchManager.isSimExecutionRemote());
        displayScriptGenerationOverview();
        displayScriptRunOverview();
        displayScriptAnalysisOverview();
        scriptGenerateButton.setEnabled(
                workbenchManager.scriptGenerationAvailable());
        runScriptButton.setEnabled(!workbenchManager.scriptRan()
                && workbenchManager.scriptGenerated());
        analyzeOutputButton.setEnabled(workbenchManager.scriptRan() && !workbenchManager.scriptAnalyzed());
        viewProvenanceMenuItem.setEnabled(workbenchManager.isProvEnabled());
    } */

//    private void setMacCopyPaste() {
//        if (FileManager.getFileManager().isMacSystem()) {
//            InputMap im = (InputMap) UIManager.get("TextField.focusInputMap");
//            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C,
//                    KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
//            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V,
//                    KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
//            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X,
//                    KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
//        }
//    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="User Communication">
   /**
     * Sets the workbench message content. The content of this message is based
     * on the accumulated messages of produced by the functions of the workbench
     * manager.
     *
     */
     public void setMsg() {
        msgText.setText(workbenchManager.getMessages());
    } 
    // </editor-fold>
}
