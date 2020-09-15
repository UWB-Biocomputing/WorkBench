package edu.uwb.braingrid.workbench;

import edu.uwb.braingrid.workbench.WorkbenchManager;
import edu.uwb.braingrid.workbench.utils.DateTime;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Date;
import javax.swing.JFileChooser;

/**
 * The SimStartWiz is responsible for handling all back-end events associated with
 * 	running the simulation start wizard. This code was adapted from Davis WorkbenchControlFrame
 *
 * @author Joseph Conquest
 * @version 0.1
 */
public class SimStartWiz {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


    private void initComponents() {

    }// </editor-fold>//GEN-END:initComponents


    /**
     * Prompts the user to select files for the simulation input. InputAnalyzer
     * files are created with NLEdit or by hand in XML. InputAnalyzer files
     * represent lists of neurons with regard to their position in a neuron
     * array (e.g. position 12 is x: 1, y: 2 on a 10x10 grid)
     *
     * @param evt - The event that triggered this action
     */
    private void configureSimulation() {
		if (workbenchManager.runScript()) {
			workbenchManager.invalidateScriptGenerated();
			workbenchManager.invalidateScriptRan();
			workbenchManager.invalidateScriptAnalyzed();
		}
		workbenchManager.configureSimulation();
     //   setMsg();
       // pack();
    }

    // <editor-fold defaultstate="collapsed" desc="Custom Members">
    private WorkbenchManager workbenchManager;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Construction">
    /**
     * Responsible for allocating this frame and initializing auto-generated, as
     * well as custom, members
     */
    public SimStartWiz(WorkbenchManager workbenchManager) {
		System.out.println("entering SimStartWiz");
		this.workbenchManager = workbenchManager;
		configureSimulation();
		System.out.println("End of SimStartWiz()");
    }

    // </editor-fold>

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
  /*   public void setMsg() {
        msgText.setText(workbenchManager.getMessages());
    } */
    // </editor-fold>
}
