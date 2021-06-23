package edu.uwb.braingrid.workbenchdashboard;

import javafx.scene.Node;
import javafx.scene.control.Tab;

/**
 * This class provides an abstract framework and umbrella for all tabs that appear in the workbench.
 *
 * @author Max Wright
 */
public abstract class WorkbenchApp {

    private Tab tab;

    /**
     * @param tab  a Tab object whose display is the FX Node object from the superclass
     */
    public WorkbenchApp(Tab tab) {
        this.tab = tab;
    }

    /**
     * Called when a close operation is committed on a tab. Currently not used.
     *
     * @return true if can close, false if not
     */
    public abstract boolean close();

    /**
     * @return The FX Node that shows the complete GUI content.
     */
    public abstract Node getDisplay();

    /**
     * Sets the title of the tab associated with this object given in the constructor.
     *
     * @param title  the new title of the tab as a string.
     */
    public void setTitle(String title) {
        tab.setText(title);
    }
}
