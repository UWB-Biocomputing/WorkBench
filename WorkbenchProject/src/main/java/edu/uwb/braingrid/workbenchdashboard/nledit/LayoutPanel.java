package edu.uwb.braingrid.workbenchdashboard.nledit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javafx.embed.swing.SwingNode;

/**
 * The LayoutPanel class handles layout window of the Growth Simulation Layout
 * Editor. The class is a sub-class of JPanel, which shows neurons layout
 * consisting of three kind of neurons, as well as probed neurons. The panel
 * provides editable function of each neuron.
 * 
 * @author Fumitaka Kawasaki
 * @version 1.2
 */
@SuppressWarnings("serial")
public class LayoutPanel extends JPanel implements MouseListener {
    private static final Logger LOG = Logger.getLogger(LayoutPanel.class.getName());

    static final int defaultN = 100; // the default system size
    static final int defaultCellWidth = 28; // the default size of each cell
    static final int minCellWidth = 7;
    static final int maxCellWidth = 56;
    static final Color bgColor = new Color(255, 255, 255);// white background
    private int cellWidth; // each cell's width in the window
    private Insets theInsets; // the insets of the window
    private Color nColor[]; // neuron color

    // neuron type index
    /** neuron type index for other neurons */
    public static final int OTR = NeuronsLayout.OTR;
    /** neuron type index for inhibitory neurons */
    public static final int INH = NeuronsLayout.INH;
    /** neuron type index for active neurons */
    public static final int ACT = NeuronsLayout.ACT;
    /** neuron type index for probed neurons */
    public static final int PRB = NeuronsLayout.PRB;
    /** neuron type index for overlapping INH and ACT neurons */
    public static final int OVP = NeuronsLayout.OVP;

    private int xlen; // number of cells x-axis
    private int ylen; // number of cells y-axis

    /** minimum number of cells for x-axis */
    public static final int minXCells = 5;
    /** minimum number of cells for y-axis */
    public static final int minYCells = 5;
    /** maximum number of cells for x-axis */
    public static final int maxXCells = 500;
    /** maximum number of cells for y-axis */
    public static final int maxYCells = 500;
    /** default number of cells for x-axis */
    public static final int defXCells = 10;
    /** default number of cells for y-axis */
    public static final int defYCells = 10;

    private NeuronsLayout neurons_layout_;

    private JScrollPane scrollPane;

    private NLedit nledit_;

    /**
     * A class constructor, which initializes global stuff.
     * 
     * @param ctrlFrame
     *            reference to the control panel
     * @param size
     *            size of the layout.
     */
    public LayoutPanel(NLedit nledit, Dimension size, NeuronsLayout neurons_layout) {
        LOG.info("new " + getClass().getName());
        nledit_ = nledit;
        xlen = size.width;
        ylen = size.height;

        cellWidth = defaultCellWidth;

        // initialize window and graphics:
        theInsets = getInsets();

        // set the windows size
        setPreferredSize(new Dimension(xlen * cellWidth + theInsets.left + theInsets.right,
                ylen * cellWidth + theInsets.top + theInsets.bottom));

        // define colors of each type of neurons
        nColor = new Color[5];
        nColor[OTR] = new Color(0x00FF00); // other neurons - green
        nColor[INH] = new Color(0xFF0000); // inhibitory neurons - red
        nColor[ACT] = new Color(0x0000FF); // starter neurons - blue
        nColor[PRB] = new Color(0x000000); // probed neurons - black
        nColor[OVP] = new Color(0xFFFF00); // overlapping neurons - yellow

        // register for mouse events on the window
        addMouseListener(this);

        neurons_layout_ = neurons_layout;
    }

    /**
     * Initialize graphics objects
     */
    public void startGraphics() {
        Graphics g = getGraphics();
        g.setColor(bgColor);
        g.fillRect(theInsets.left, theInsets.top, xlen * cellWidth, ylen * cellWidth);
        
        
    }

    /**
     * Update the graphical window.
     * 
     * @param g
     *            a graphic object to draw
     */
    public void writeToGraphics(Graphics g) {
        // System.out.println(NLedit.HEADER + "Start Repaint");
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        for (int j = 0; j < ylen; j++) {
            for (int i = 0; i < xlen; i++) {
                if (true) {

                    int cIndex = neurons_layout_.getNeuronType(j * xlen + i);
                    g2.setColor(nColor[cIndex]);
                    int x = theInsets.left + i * cellWidth;
                    int y = theInsets.top + j * cellWidth;
                    g.fillOval(x, y, cellWidth, cellWidth);

                    if (neurons_layout_.isProbed(j * xlen + i)) {
                        g2.setColor(nColor[PRB]);
                        g2.drawOval(x, y, cellWidth, cellWidth);
                        if (cellWidth >= minCellWidth) { // MyPrintable may set
                                                            // smaller cellWidth
                            g2.drawOval(x + 1, y + 1, cellWidth - 2, cellWidth - 2);
                        }
                    }
                }
            }
        }
        // System.out.println(NLedit.HEADER + "End Repaint");
    }

    public void repaintScrollpane() {
        scrollPane.repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics g) {
        if (g != null) {
            writeToGraphics(g);

        }
    }

    /*
     * (non-Javadoc) Toggle inhibitory, active or probed neuron type depending on
     * the current edit mode (neuron type).
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    boolean mousePressed = false;

    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        if (!mousePressed) {
            Point pt = e.getPoint();
            int i = (pt.x - theInsets.left) / cellWidth;
            int j = (pt.y - theInsets.top) / cellWidth;
            if (i >= xlen || j >= ylen) {
                LOG.info("Out of bounds");
            } else {
                Integer index = j * xlen + i;

                int neuronType = nledit_.getNeuronType();

                neurons_layout_.changeIndex(neuronType, index);

                Graphics g = getGraphics();
                writeToGraphics(g);

                LOG.info(NeuronsLayout.getNeuronTypeName(neuronType) + " placed at " + i + ", " + j);
                scrollPane.repaint();
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        // find a point to click
        mousePressed = false;
    }

    /**
     * Set the scroll panel.
     * 
     * @param scrollpane
     */
    public void setScrollPane(JScrollPane scrollpane) {
        this.scrollPane = scrollpane;
    }

    /**
     * Get the scroll panel.
     * 
     * @return reference to the scroll panel.
     */
    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    /**
     * Change the layout size.
     * 
     * @param size
     *            new layout size.
     */
    public void changeLayoutSize(Dimension size) {
        xlen = size.width;
        ylen = size.height;

        resetLayoutPanel();
    }

    /**
     * Change cell size.
     * 
     * @param inc
     *            true when increasing cell size, false decreasing cell size.
     */
    public void changeCellSize(boolean inc) {
        if (inc == true && cellWidth != maxCellWidth) {
            cellWidth *= 2;
        } else if (inc == false && cellWidth != minCellWidth) {
            cellWidth /= 2;
        } else {
            return;
        }

        resetLayoutPanel();
    }

    /**
     * Adjust scroll pane and window size.
     */
    private void resetLayoutPanel() {
        // set the windows size
        theInsets = getInsets();
        setPreferredSize(new Dimension(xlen * cellWidth + theInsets.left + theInsets.right,
                ylen * cellWidth + theInsets.top + theInsets.bottom));

        scrollPane = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        SwingNode scroll_pane_node = new SwingNode();
        scroll_pane_node.setContent(scrollPane);

        nledit_.getBP().setCenter(scroll_pane_node);
        Rectangle screen = getUsableScreenBounds(null);
        Dimension size = scrollPane.getPreferredSize();
        if (size.width > screen.width) {
            size.width = screen.width;
        }
        if (size.height > screen.height - 20) {
            size.height = screen.height - 20;
        }
        scrollPane.setPreferredSize(size);
        scrollPane.repaint();
    }

    /**
     * Returns the usable area of the screen where applications can place its
     * windows. The method subtracts from the screen the area of taskbars, system
     * menus and the like.
     * 
     * @param gconf
     *            the GraphicsConfiguration of the monitor
     * @return the rectangle of the screen where one can place windows
     */
    public static Rectangle getUsableScreenBounds(GraphicsConfiguration gconf) {
        if (gconf == null) {
            gconf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                    .getDefaultConfiguration();
        }
        Rectangle bounds = new Rectangle(gconf.getBounds());
        try {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Insets insets = toolkit.getScreenInsets(gconf);
            bounds.y += insets.top;
            bounds.x += insets.left;
            bounds.height -= (insets.top + insets.bottom);
            bounds.width -= (insets.left + insets.right);
        } catch (Exception ex) {
            System.out.println("There was a problem getting screen-related information.");
        }
        return bounds;
    }

    public int solution(int[] A, int[] E) {
        // write your code in Java SE 8
        // A[J] = first node
        //

        int maxLen = 0;

        boolean[] touched = new boolean[A.length];
        for (int i = 0; i < A.length; ++i) {
            touched[i] = false;
        }

        // What has not been touched?
        int index = 0;
        for (int i = 0; i < touched.length; ++i) {
            if (!touched[i]) {
                index = i;
                i = touched.length;
            }
        }

        // what is the length of that connecting indeex.
        // int tempLen = 0;
        // Iterate through nodes and assess connections
        for (int i = 0; i < E.length; i += 2) {
            // Is the starting connection that same as the node found?
            if (i / 2 == index) {

            } else {
                // Is the ending conenction the same as the node found?
                if (i / 2 + 1 == index) {

                }
            }
        }

        return maxLen;
    }

    /**
     * Gets the layout size.
     * 
     * @return layout size.
     */
    public Dimension getLayoutSize() {

        return new Dimension(xlen, ylen);
    }

    /**
     * Gets the cell width.
     * 
     * @return the cell width.
     */
    public int getCellWidth() {
        return cellWidth;
    }

    /**
     * Sets the cell width.
     * 
     * @param cellWidth
     *            - cell width to set.
     */
    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    /**
     * Gets the reference to the CtrlFrame class object.
     * 
     * @return reference to the CtrlFrame class object.
     */
    public NLedit getCtrlFrame() {
        return nledit_;
    }
}
