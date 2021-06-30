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
 * The LayoutPanel class handles layout window of the Growth Simulation Layout Editor. The class is
 * a sub-class of JPanel, which shows neurons layout consisting of three kind of neurons, as well as
 * probed neurons. The panel provides editable function of each neuron.
 *
 * @author Fumitaka Kawasaki
 * @version 1.2
 */
@SuppressWarnings("serial")
public class LayoutPanel extends JPanel implements MouseListener {

    private static final Logger LOG = Logger.getLogger(LayoutPanel.class.getName());

    /** The default system size. */
    public static final int DEFAULT_SIZE = 100;
    /** The default size of each cell. */
    public static final int DEFAULT_CELL_WIDTH = 28;
    /** The minimum width of a cell. */
    public static final int MIN_CELL_WIDTH = 7;
    /** The maximum width of a cell. */
    public static final int MAX_CELL_WIDTH = 56;
    /** Number of neuron colors. */
    public static final int NUM_NEURON_TYPES = 5;
    /** Other neurons color - green. */
    public static final int OTR_NEURON_COLOR = 0x00FF00;
    /** Inhibitory neurons color - red. */
    public static final int INH_NEURON_COLOR = 0xFF0000;
    /** Active neurons color - blue. */
    public static final int ACT_NEURON_COLOR = 0x0000FF;
    /** Probed neurons color - black. */
    public static final int PRB_NEURON_COLOR = 0x000000;
    /** Overlapping neurons color - yellow. */
    public static final int OVP_NEURON_COLOR = 0xFFFF00;
    /** White background color. */
    public static final Color BG_COLOR = new Color(255, 255, 255);

    // neuron type index
    /** Neuron type index for other neurons. */
    public static final int OTR = NeuronsLayout.OTR;
    /** Neuron type index for inhibitory neurons. */
    public static final int INH = NeuronsLayout.INH;
    /** Neuron type index for active neurons. */
    public static final int ACT = NeuronsLayout.ACT;
    /** Neuron type index for probed neurons. */
    public static final int PRB = NeuronsLayout.PRB;
    /** Neuron type index for overlapping INH and ACT neurons. */
    public static final int OVP = NeuronsLayout.OVP;

    /** Minimum number of cells for x-axis. */
    public static final int MIN_X_CELLS = 5;
    /** Minimum number of cells for y-axis. */
    public static final int MIN_Y_CELLS = 5;
    /** Maximum number of cells for x-axis. */
    public static final int MAX_X_CELLS = 500;
    /** Maximum number of cells for y-axis. */
    public static final int MAX_Y_CELLS = 500;
    /** Default number of cells for x-axis. */
    public static final int DEF_X_CELLS = 10;
    /** Default number of cells for y-axis. */
    public static final int DEF_Y_CELLS = 10;

    /** Each cell's width in the window. */
    private int cellWidth;
    /** The insets of the window. */
    private Insets theInsets;
    /** Colors for each type of neuron. */
    private Color[] nColor;

    /** Number of cells x-axis. */
    private int xlen;
    /** Number of cells y-axis. */
    private int ylen;

    private NeuronsLayout neuronsLayout;
    private JScrollPane scrollPane;
    private NLEdit nledit;
    private boolean mousePressed = false;

    /**
     * A class constructor, which initializes global stuff.
     *
     * @param nledit
     * @param size
     * @param neuronsLayout
     */
    public LayoutPanel(NLEdit nledit, Dimension size, NeuronsLayout neuronsLayout) {
        LOG.info("new " + getClass().getName());
        this.nledit = nledit;
        xlen = size.width;
        ylen = size.height;

        cellWidth = DEFAULT_CELL_WIDTH;

        // initialize window and graphics:
        theInsets = getInsets();

        // set the windows size
        setPreferredSize(new Dimension(xlen * cellWidth + theInsets.left + theInsets.right,
                ylen * cellWidth + theInsets.top + theInsets.bottom));

        // define colors of each type of neurons
        nColor = new Color[NUM_NEURON_TYPES];
        nColor[OTR] = new Color(OTR_NEURON_COLOR);
        nColor[INH] = new Color(INH_NEURON_COLOR);
        nColor[ACT] = new Color(ACT_NEURON_COLOR);
        nColor[PRB] = new Color(PRB_NEURON_COLOR);
        nColor[OVP] = new Color(OVP_NEURON_COLOR);

        // register for mouse events on the window
        addMouseListener(this);

        this.neuronsLayout = neuronsLayout;
    }

    /**
     * Initialize graphics objects.
     */
    public void startGraphics() {
        Graphics g = getGraphics();
        g.setColor(BG_COLOR);
        g.fillRect(theInsets.left, theInsets.top, xlen * cellWidth, ylen * cellWidth);
    }

    /**
     * Update the graphical window.
     *
     * @param g  A graphic object to draw
     */
    public void writeToGraphics(Graphics g) {
        // System.out.println(NLEdit.HEADER + "Start Repaint");
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        for (int j = 0; j < ylen; j++) {
            for (int i = 0; i < xlen; i++) {
                int cIndex = neuronsLayout.getNeuronType(j * xlen + i);
                g2.setColor(nColor[cIndex]);
                int x = theInsets.left + i * cellWidth;
                int y = theInsets.top + j * cellWidth;
                g.fillOval(x, y, cellWidth, cellWidth);

                if (neuronsLayout.isProbed(j * xlen + i)) {
                    g2.setColor(nColor[PRB]);
                    g2.drawOval(x, y, cellWidth, cellWidth);
                    if (cellWidth >= MIN_CELL_WIDTH) { // MyPrintable may set smaller cellWidth
                        g2.drawOval(x + 1, y + 1, cellWidth - 2, cellWidth - 2);
                    }
                }
            }
        }
        // System.out.println(NLEdit.HEADER + "End Repaint");
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

    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        if (!mousePressed) {
            Point pt = e.getPoint();
            int i = (pt.x - theInsets.left) / cellWidth;
            int j = (pt.y - theInsets.top) / cellWidth;
            if (i >= xlen || j >= ylen) {
                LOG.info("Out of bounds");
            } else {
                int index = j * xlen + i;
                int neuronType = nledit.getNeuronType();

                neuronsLayout.changeIndex(neuronType, index);

                Graphics g = getGraphics();
                writeToGraphics(g);

                LOG.info(NeuronsLayout.getNeuronTypeName(neuronType)
                        + " placed at " + i + ", " + j);
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
     * @return Reference to the scroll panel.
     */
    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    /**
     * Change the layout size.
     *
     * @param size  new layout size.
     */
    public void changeLayoutSize(Dimension size) {
        xlen = size.width;
        ylen = size.height;

        resetLayoutPanel();
    }

    /**
     * Change cell size.
     *
     * @param inc  true when increasing cell size, false decreasing cell size
     */
    public void changeCellSize(boolean inc) {
        if (inc && cellWidth != MAX_CELL_WIDTH) {
            cellWidth *= 2;
        } else if (!inc && cellWidth != MIN_CELL_WIDTH) {
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

        SwingNode scrollPaneNode = new SwingNode();
        scrollPaneNode.setContent(scrollPane);

        nledit.getBP().setCenter(scrollPaneNode);
        Rectangle screen = getUsableScreenBounds();
        Dimension size = scrollPane.getPreferredSize();
        if (size.width > screen.width) {
            size.width = screen.width;
        }
        if (size.height > screen.height - 20) { //@cs-: MagicNumber influence 1
            size.height = screen.height - 20;
        }
        scrollPane.setPreferredSize(size);
        scrollPane.repaint();
    }

    /**
     * Returns the usable area of the screen where applications can place its windows. The method
     * subtracts from the screen the area of taskbars, system menus and the like.
     *
     * @param gconf  The GraphicsConfiguration of the monitor
     * @return The rectangle of the screen where one can place windows
     */
    public static Rectangle getUsableScreenBounds(GraphicsConfiguration gconf) {
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

    public static Rectangle getUsableScreenBounds() {
        GraphicsConfiguration gconf = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();
        return getUsableScreenBounds(gconf);
    }

    //TODO: Consider removing this method. Does it have any use?
//    public int solution(int[] A, int[] E) {
//        // write your code in Java SE 8
//        // A[J] = first node
//
//        int maxLen = 0;
//
//        boolean[] touched = new boolean[A.length];
//        for (int i = 0; i < A.length; ++i) {
//            touched[i] = false;
//        }
//
//        // What has not been touched?
//        int index = 0;
//        for (int i = 0; i < touched.length; ++i) {
//            if (!touched[i]) {
//                index = i;
//                i = touched.length;
//            }
//        }
//
//        // what is the length of that connecting index.
//        // int tempLen = 0;
//        // Iterate through nodes and assess connections
//        for (int i = 0; i < E.length; i += 2) {
//            // Is the starting connection that same as the node found?
//            if (i / 2 == index) {
//
//            } else {
//                // Is the ending connection the same as the node found?
//                if (i / 2 + 1 == index) {
//
//                }
//            }
//        }
//
//        return maxLen;
//    }

    /**
     * Gets the layout size.
     *
     * @return layout size
     */
    public Dimension getLayoutSize() {
        return new Dimension(xlen, ylen);
    }

    /**
     * Gets the cell width.
     *
     * @return the cell width
     */
    public int getCellWidth() {
        return cellWidth;
    }

    /**
     * Sets the cell width.
     *
     * @param cellWidth  Cell width to set
     */
    public void setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
    }

    /**
     * Gets the reference to the CtrlFrame class object.
     *
     * @return Reference to the CtrlFrame class object
     */
    public NLEdit getCtrlFrame() {
        return nledit;
    }
}
