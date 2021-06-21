package edu.uwb.braingrid.workbenchdashboard.nledit;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import org.apache.jena.rdf.model.Resource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import edu.uwb.braingrid.workbench.WorkbenchManager;
import edu.uwb.braingrid.workbench.utils.DateTime;
import edu.uwb.braingrid.workbenchdashboard.WorkbenchApp;
import edu.uwb.braingrid.workbenchdashboard.WorkbenchDashboard;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NLedit extends WorkbenchApp {
    private static final Logger LOG = Logger.getLogger(NLedit.class.getName());

    private BorderPane bp_ = new BorderPane();

    public NLedit(Tab tab) {
        super(tab);
        LOG.info("new " + getClass().getName());
        workbenchMgr = new WorkbenchManager();
        initSettingsPanel();
        initToolbar();
        initEditBar();
        generateSimulator();
        super.setTitle("NLEdit");
    }

    @Override
    public boolean close() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public Node getDisplay() {
        return bp_;
    }

    public BorderPane getBP() {
        return bp_;
    }

    private void generateSimulator() {
        NeuronsLayout neurons_layout = new NeuronsLayout();
        neurons_layout_ = neurons_layout;

        layoutPanel = new LayoutPanel(this, new Dimension(LayoutPanel.defXCells, LayoutPanel.defYCells),
                neurons_layout);
        JScrollPane scrollpane = new JScrollPane(layoutPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        layoutPanel.setScrollPane(scrollpane);

        SwingNode scroll_pane_node = new SwingNode();
        scroll_pane_node.setContent(scrollpane);
        bp_.setCenter(scroll_pane_node);
        nl_sim_util_ = new NL_Sim_Util(layoutPanel, neurons_layout);
    }

    /**
     * The public function getNeuronType returns the current edit mode, which is
     * called from LayoutPanel.
     *
     * @return Current edit mode: LayoutPanel.INH - inhibitory neurons edit mode.
     *         LayoutPanel.ACT - active neurons edit mode. LayoutPanel.PRB - probed
     *         neurons edit mode.
     */
    public int getNeuronType() {
        if (inhNItem.isSelected()) {
            return LayoutPanel.INH;
        } else if (activeNItem.isSelected()) {
            return LayoutPanel.ACT;
        } else if (probedNItem.isSelected()) {
            return LayoutPanel.PRB;
        }
        return LayoutPanel.OTR;
    }

    private void initEditBar() {
        inhNItem.setToggleGroup(editGroup);
        activeNItem.setToggleGroup(editGroup);
        probedNItem.setToggleGroup(editGroup);
        probedNItem.setOnAction(event -> {
        });
        inhNItem.setSelected(true);

        VBox vbox = new VBox(inhNItem, activeNItem, probedNItem);
        vbox.getStyleClass().add("neuronbox");
        bp_.setLeft(vbox);
    }

    private void initToolbar() {

        primeButton(clear_item_btn_, "/icons/baseline-clear-black-18/1x/baseline_clear_black_18dp.png",
                "Clear Neurons");
        clear_item_btn_.getStyleClass().add("clear-button");
        clear_item_btn_.setOnAction(event -> {
            actionClear();
        });

        primeButton(import_item_btn_, "/icons/baseline-input-black-18/1x/baseline_input_black_18dp.png",
                "Import Neuron Layout");
        import_item_btn_.setOnAction(event -> {
            actionImport();
        });

        primeButton(export_item_btn_, "/icons/baseline-save_alt-black-18/1x/baseline_save_alt_black_18dp.png",
                "Export Neuron Layout");
        export_item_btn_.setOnAction(event -> {
            actionExport();
        });

        primeButton(print_item_btn_,
                "/icons/baseline-local_printshop-black-18/1x/baseline_local_printshop_black_18dp.png", "Print");
        print_item_btn_.setOnAction(event -> {
            actionPrint();
        });

        primeButton(bcell_item_btn_, "/icons/baseline-zoom_in-black-18/1x/baseline_zoom_in_black_18dp.png", "Zoom In");
        bcell_item_btn_.setOnAction(event -> {
            actionBiggerCells();
        });

        primeButton(scell_item_btn_, "/icons/baseline-zoom_out-black-18/1x/baseline_zoom_out_black_18dp.png",
                "Zoom Out");
        scell_item_btn_.setOnAction(event -> {
            actionSmallerCells();
        });

        primeButton(sdat_item_btn_, "/icons/baseline-data_usage-black-18/1x/baseline_data_usage_black_18dp.png",
                "Stats");
        sdat_item_btn_.setOnAction(event -> {
            actionStatisticalData();
        });

        
        HBox toolbar = new HBox(bcell_item_btn_, scell_item_btn_, print_item_btn_, sdat_item_btn_, import_item_btn_, export_item_btn_, clear_item_btn_);

        toolbar.getStyleClass().add("toolbar");

        bp_.setTop(toolbar);
    }

    private void primeButton(Button button, String image_path, String tooltip) {
        setButtonImage(button, image_path);
        button.setTooltip(new Tooltip(tooltip));
    }

    private void setButtonImage(Button button, String path) {
        String url = "resources" + path;
        button.getStyleClass().add("toolbar-button");
        try {
            Image image = new Image(this.getClass().getClassLoader().getResourceAsStream(url));
            button.setGraphic(new ImageView(image));
        } catch (NullPointerException e) {
            System.out.println(e.toString());
            System.out.println("Error by toolbar button image set");
        }
    }

    private void initSettingsPanel() {
        Label lbl_sizeX = new Label("Size x:");
        Label lbl_sizeY = new Label("Size y:");

        TextField txtfld_x = new TextField("10");
        TextField txtfld_y = new TextField("10");

    
        Button btn_submit = new Button("Submit");
        btn_submit.setOnAction(event -> {
            int sizeX = 0, sizeY = 0;

            try {
                sizeX = Integer.parseInt(txtfld_x.getText());
                txtfld_x.setStyle("-fx-text-fill: black;");
            } catch (NumberFormatException e) {
                txtfld_x.setStyle("-fx-text-fill: red;");
            }

            try {
                sizeY = Integer.parseInt(txtfld_y.getText());
                txtfld_y.setStyle("-fx-text-fill: black;");
            } catch (NumberFormatException e) {
                txtfld_y.setStyle("-fx-text-fill: red;");
            }

            boolean inbounds_x = sizeX >= LayoutPanel.minXCells && sizeX <= LayoutPanel.maxXCells;
            boolean inbounds_y = sizeY >= LayoutPanel.minYCells && sizeY <= LayoutPanel.maxYCells;

            if (inbounds_x && inbounds_y) {
                actionModifySize(sizeX, sizeY);

                JScrollPane scrollpane = new JScrollPane(layoutPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                layoutPanel.setScrollPane(scrollpane);

                SwingNode scroll_pane_node = new SwingNode();
                scroll_pane_node.setContent(scrollpane);

                bp_.setCenter(scroll_pane_node);
            } else {
                if (!inbounds_x) {
                    txtfld_x.setStyle("-fx-text-fill: red;");
                }
                if (!inbounds_y) {
                    txtfld_y.setStyle("-fx-text-fill: red;");
                }
            }

        });
        
        gpat_item_btn_.setOnAction(event -> {
            actionGeneratePattern();
        });

        aprb_item_btn_.setOnAction(event -> {
            actionArrangeProbes();
        });
        

        toggle_group = new ToggleGroup();

        newButton.setToggleGroup(toggle_group);
        rptButton.setToggleGroup(toggle_group);
        altButton.setToggleGroup(toggle_group);

        HBox hbox_patternGen = new HBox(gpat_item_btn_, aprb_item_btn_);
        hbox_patternGen.setSpacing(10);

        HBox hbox_sizeX = new HBox(lbl_sizeX, txtfld_x);
        HBox hbox_sizeY = new HBox(lbl_sizeY, txtfld_y);
        HBox hbox_resize = new HBox(hbox_sizeX, hbox_sizeY, btn_submit);
        hbox_resize.setSpacing(10);

        HBox hbox_left = new HBox(hbox_resize, newButton, rptButton,
        altButton);
        hbox_left.setSpacing(20);

        HBox hbox_bottom = new HBox(hbox_left, hbox_patternGen);
        hbox_bottom.getStyleClass().add("sizebox");
        hbox_bottom.setSpacing(200);
        bp_.setBottom(hbox_bottom);
    }

    /**
     * The 'Clear' menu handler.
     */
    private void actionClear() {
        neurons_layout_.inhNList.clear();
        neurons_layout_.activeNList.clear();
        neurons_layout_.probedNList.clear();

        layoutPanel.repaintScrollpane();
    }

    /**
     * The 'Import...' menu handler.
     */
    private void actionImport() {
        importPopup();
    }

    public void importPopup() {
        ImportPanel myPanel = new ImportPanel();

        final Stage dialog = new Stage();
        dialog.setTitle("Select Input Files");
        Button imprt = new Button("Import");
        Button cancel = new Button("Cancel");

        dialog.initModality(Modality.NONE);
        dialog.initOwner(WorkbenchDashboard.primaryStage_);

        imprt.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                readNeuronListFromFile(myPanel.tfields[ImportPanel.idxInhList].getText(), neurons_layout_.inhNList,
                        LayoutPanel.INH);
                readNeuronListFromFile(myPanel.tfields[ImportPanel.idxActList].getText(), neurons_layout_.activeNList,
                        LayoutPanel.ACT);
                readNeuronListFromFile(myPanel.tfields[ImportPanel.idxPrbList].getText(), neurons_layout_.probedNList,
                        LayoutPanel.PRB);

                Graphics g = layoutPanel.getGraphics();
                layoutPanel.writeToGraphics(g);
                layoutPanel.repaint();
                dialog.close();
            }
        });
        cancel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                dialog.close();
            }
        });

        HBox hbox = new HBox(imprt, cancel);
        hbox.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(myPanel, hbox);
        Scene dialogScene = new Scene(vbox, 500, 150);
        // dialogScene.getStylesheets().add("//style sheet of your choice");
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * The function readNeuronListFromFile reads neurons list from the file
     * specified by nameNListFile and stores neurons index in list.
     *
     * @param nameNListFile
     *            file path of the neurons list (xml format).
     * @param list
     *            array list to store neurons index.
     * @param type
     *            type of neurons.
     */
    private void readNeuronListFromFile(String nameNListFile, ArrayList<Integer> list, int type) {
        if (nameNListFile == null || nameNListFile.length() == 0) {
            return;
        }

        try {
            // read a xml file
            Document doc = new SAXBuilder().build(new File(nameNListFile));
            Element root = doc.getRootElement();
            if ((root != null) && ((root.getName() == "A" && type == LayoutPanel.ACT)
                    || (root.getName() == "I" && type == LayoutPanel.INH)
                    || (root.getName() == "P" && type == LayoutPanel.PRB))) {
                list.clear();
                String[] parts = root.getValue().split("[ \n\r]");

                Dimension size = layoutPanel.getLayoutSize();
                int numNeurons = size.height * size.width;
                for (String part : parts) {
                    try {
                        int index = Integer.parseInt(part);
                        if (index < numNeurons) { // ignore indexes greater than
                            // numNeurons
                            list.add(index);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Illegal number :" + part);
                    }
                }
            }
        } catch (JDOMException je) {
            System.err.println(je);
        } catch (IOException ie) {
            System.err.println(ie);
        }
    }

    /**
     * The 'Export...' menu handler.
     */
    private void actionExport() {
        exportPopup();
    }

    public void exportPopup() {
        ExportPanel myPanel = new ExportPanel(ImportPanel.nlistDir);

        final Stage dialog = new Stage();
        dialog.setTitle("Export");
        Button yes = new Button("Yes");
        Button no = new Button("No");

        dialog.initModality(Modality.NONE);
        dialog.initOwner(WorkbenchDashboard.primaryStage_);

        Long functionStartTime = System.currentTimeMillis();
        Long accumulatedTime = 0L;

        yes.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                Long accumulatedTime = 0L;
                writeNeuronListToFile(myPanel.tfields[ExportPanel.idxInhList].getText(), neurons_layout_.inhNList,
                        LayoutPanel.INH);
                // add to workbench project
                if (null != workbenchMgr && workbenchMgr.isProvEnabled()) {
                    Long startTime = System.currentTimeMillis();
                    Resource file = workbenchMgr.getProvMgr().addFileGeneration(
                            "InhibitoryNeuronListExport" + java.util.UUID.randomUUID(), "neuronListExport", "NLEdit",
                            null, false, myPanel.tfields[ExportPanel.idxInhList].getText(), null, null);

                    // Tell Java to stop considering the file to be in it's control
                    try {
                        ((FileOutputStream) file).close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    accumulatedTime = DateTime.sumProvTiming(startTime, accumulatedTime);
                }

                writeNeuronListToFile(myPanel.tfields[ExportPanel.idxActList].getText(), neurons_layout_.activeNList,
                        LayoutPanel.ACT);
                // add to workbench project
                if (null != workbenchMgr && workbenchMgr.isProvEnabled()) {
                    Long startTime = System.currentTimeMillis();
                    Resource file = workbenchMgr.getProvMgr().addFileGeneration(
                            "ActiveNeuronListExport" + java.util.UUID.randomUUID(), "neuronListExport", "NLEdit", null,
                            false, myPanel.tfields[ExportPanel.idxActList].getText(), null, null);
                    accumulatedTime = DateTime.sumProvTiming(startTime, accumulatedTime);

                    // Tell Java to stop considering the file to be in it's control
                    try {
                        ((FileOutputStream) file).close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }

                writeNeuronListToFile(myPanel.tfields[ExportPanel.idxPrbList].getText(), neurons_layout_.probedNList,
                        LayoutPanel.PRB);
                // add to workbench project
                if (null != workbenchMgr && workbenchMgr.isProvEnabled()) {
                    Long startTime = System.currentTimeMillis();
                    Resource file = workbenchMgr.getProvMgr().addFileGeneration(
                            "ProbedNeuronListExport" + java.util.UUID.randomUUID(), "neuronListExport", "NLEdit", null,
                            false, myPanel.tfields[ExportPanel.idxPrbList].getText(), null, null);
                    accumulatedTime = DateTime.sumProvTiming(startTime, accumulatedTime);

                    // Tell Java to stop considering the file to be in it's control
                    try {
                        ((FileOutputStream) file).close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }

                // In the original function this executed at the end of the popup regarless of
                // what occured.
                DateTime.recordFunctionExecutionTime("ControlFrame", "actionExport",
                        System.currentTimeMillis() - functionStartTime, workbenchMgr.isProvEnabled());
                if (workbenchMgr.isProvEnabled()) {
                    DateTime.recordAccumulatedProvTiming("ControlFrame", "actionExport", accumulatedTime);
                }
                dialog.close();
            }
        });
        no.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                // In the original function this executed at the end of the popup regarless of
                // what occured.
                DateTime.recordFunctionExecutionTime("ControlFrame", "actionExport",
                        System.currentTimeMillis() - functionStartTime, workbenchMgr.isProvEnabled());
                if (workbenchMgr.isProvEnabled()) {
                    DateTime.recordAccumulatedProvTiming("ControlFrame", "actionExport", accumulatedTime);
                }
                dialog.close();
            }
        });

        HBox hbox = new HBox(yes, no);
        hbox.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(myPanel, hbox);
        Scene dialogScene = new Scene(vbox, 500, 150);
        // dialogScene.getStylesheets().add("//style sheet of your choice");
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * The function writeNeuronListToFile creates a neurons list file specified by
     * list and type.
     *
     * @param nameNListFile
     *            file path of the neurons list.
     * @param list
     *            array list of neurons index.
     * @param type
     *            type of neurons.
     */
    private void writeNeuronListToFile(String nameNListFile, ArrayList<Integer> list, int type) {
        if (nameNListFile == null || nameNListFile.length() == 0) {
            return;
        }

        try {
            Element root = null;
            if (type == LayoutPanel.INH) { // inhibitory neurons
                root = new Element("I");
            } else if (type == LayoutPanel.ACT) { // active neurons
                root = new Element("A");
            } else if (type == LayoutPanel.PRB) { // probed neurons
                root = new Element("P");
            }

            // create a xml file
            String sList = "";
            Iterator<Integer> iter = list.iterator();
            while (iter.hasNext()) {
                sList += " " + iter.next();
            }
            root.setText(sList);

            Document doc = new Document();
            doc.setRootElement(root);

            XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
            xmlOutput.output(doc, new FileOutputStream(nameNListFile));
        } catch (IOException ie) {
            System.err.println(ie);
        }
    }

    /**
     * The 'Bigger cells' menu handler.
     */
    private void actionBiggerCells() {
        layoutPanel.changeCellSize(true);

    }

    /**
     * The 'Smaller cells' menu handler.
     */
    private void actionSmallerCells() {
        layoutPanel.changeCellSize(false);
    }

    /**
     * The 'Print...' menu handler.
     */
    private void actionPrint() {
        // get PrinterJob
        PrinterJob job = PrinterJob.getPrinterJob();
        MyPrintable printable = new MyPrintable(job.defaultPage(), layoutPanel, nl_sim_util_);

        // setup Printable, Pageable
        job.setPrintable(printable);
        job.setPageable(printable);

        // display print dialog and print
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The 'Modify size...' menu handler.
     */
    private void actionModifySize(int sizeX, int sizeY) {
        RepType rtype = RepType.CLEAR;
        if (newButton.isSelected()) {
            rtype = RepType.CLEAR;
            // System.out.println("Clear");
        } else if (rptButton.isSelected()) {
            rtype = RepType.REPEAT;
            // System.out.println("Repeat");
        } else if (altButton.isSelected()) {
            rtype = RepType.ALT;
            // System.out.println("Alternate");
        }
        // System.out.println("Default");
        changeLayoutSize(new Dimension(sizeX, sizeY), rtype);

    }

    /**
     * The function changeLayoutSize generates new neurons lists of inhNList,
     * activeNList, and activeNList, and changes the size of the layout panel.
     *
     * @param newSize
     *            size for the new layout panel.
     * @param rtype
     *            repeat type, CLEAR, REPEAT, or ALT.
     */
    private void changeLayoutSize(Dimension newSize, RepType rtype) {
        neurons_layout_.inhNList = nl_sim_util_.repPattern(newSize, neurons_layout_.inhNList, rtype);
        neurons_layout_.activeNList = nl_sim_util_.repPattern(newSize, neurons_layout_.activeNList, rtype);
        neurons_layout_.probedNList = nl_sim_util_.repPattern(newSize, neurons_layout_.activeNList, rtype);

        layoutPanel.changeLayoutSize(newSize);
    }

    /**
     * The 'Generate pattern' menu handler.
     */
    private void actionGeneratePattern() {
        generatePatternPopup();
    }

    public void generatePatternPopup() {
        GPatternPanel myPanel = new GPatternPanel();

        final Stage dialog = new Stage();
        dialog.setTitle("Generate Pattern");
        Button yes = new Button("Yes");
        Button no = new Button("No");

        dialog.initModality(Modality.NONE);
        dialog.initOwner(WorkbenchDashboard.primaryStage_);

        yes.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                try {
                    float ratioInh = Float.parseFloat(myPanel.tfields[GPatternPanel.idxINH].getText());
                    float ratioAct = Float.parseFloat(myPanel.tfields[GPatternPanel.idxACT].getText());

                    // validate ratios
                    if ((ratioInh < 0 || ratioInh > 1.0) || (ratioAct < 0 || ratioAct > 1.0)
                            || (ratioInh + ratioAct > 1.0)) {
                        throw new NumberFormatException();
                    }

                    if (myPanel.btns[GPatternPanel.idxREG].isSelected()) {
                        nl_sim_util_.genRegularPattern(ratioInh, ratioAct);
                    } else if (myPanel.btns[GPatternPanel.idxRND].isSelected()) {
                        nl_sim_util_.genRandomPattern(ratioInh, ratioAct);
                    }

                    Graphics g = layoutPanel.getGraphics();
                    layoutPanel.writeToGraphics(g);
                } catch (NumberFormatException ne) {
                    JOptionPane.showMessageDialog(null, "Invalid ratio.");
                }
                dialog.close();
            }
        });
        no.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                dialog.close();
            }
        });

        HBox hbox = new HBox(yes, no);
        hbox.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(myPanel, hbox);
        Scene dialogScene = new Scene(vbox, 500, 300);
        // dialogScene.getStylesheets().add("//style sheet of your choice");
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * The 'Arrange probes' menu handler.
     */
    public void actionArrangeProbes() {
        arrangeProbesPopup();

    }

    public void arrangeProbesPopup() {
        AProbesPanel myPanel = new AProbesPanel();

        final Stage dialog = new Stage();
        dialog.setTitle("Generate Pattern");
        Button yes = new Button("Yes");
        Button no = new Button("No");

        dialog.initModality(Modality.NONE);
        dialog.initOwner(WorkbenchDashboard.primaryStage_);

        yes.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                try {
                    int numProbes = Integer.parseInt(myPanel.tfield.getText());

                    // validate number
                    Dimension dim = layoutPanel.getLayoutSize();
                    if (numProbes > dim.height * dim.width) {
                        throw new NumberFormatException();
                    }

                    nl_sim_util_.genProbes(numProbes);

                    Graphics g = layoutPanel.getGraphics();
                    layoutPanel.writeToGraphics(g);
                    dialog.close();
                    layoutPanel.repaintScrollpane();
                } catch (NumberFormatException ne) {
                    JOptionPane.showMessageDialog(null, "Invalid number.");
                }

            }
        });
        no.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {

                dialog.close();
            }
        });

        HBox hbox = new HBox(yes, no);
        hbox.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(myPanel, hbox);
        Scene dialogScene = new Scene(vbox, 500, 300);
        // dialogScene.getStylesheets().add("//style sheet of your choice");
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * The 'Statistical data...' menu handler.
     */
    private void actionStatisticalData() {
        String message = nl_sim_util_.getStatisticalMsg(true);

        JOptionPane.showMessageDialog(null, message, "Statistical data", JOptionPane.PLAIN_MESSAGE);
    }

    private LayoutPanel layoutPanel; // reference to the layout panel
    NL_Sim_Util nl_sim_util_;

    // Toolbar
    private Button import_item_btn_ = new Button("Import");
    private Button export_item_btn_ = new Button("Export");
    private Button clear_item_btn_ = new Button("Clear");
    private Button print_item_btn_ = new Button("Print");
    private Button bcell_item_btn_ = new Button("Zoom In");
    private Button scell_item_btn_ = new Button("Zoom Out");
    private Button gpat_item_btn_ = new Button("_Generate pattern");
    private Button aprb_item_btn_ = new Button("_Arrange probes");
    private Button sdat_item_btn_ = new Button("Stats");

    private ToggleGroup editGroup = new ToggleGroup();
    private RadioButton inhNItem = new RadioButton("Inhibitory neurons");
    private RadioButton activeNItem = new RadioButton("Active neurons");
    private RadioButton probedNItem = new RadioButton("Probed neurons");

    // Reference to workbench (or other frame code launching NLEdit)
    private WorkbenchManager workbenchMgr;

    private RadioButton newButton = new RadioButton("New");
    private RadioButton rptButton = new RadioButton("Repeat");
    private RadioButton altButton = new RadioButton("Alternate");

    ToggleGroup toggle_group;

    private NeuronsLayout neurons_layout_;

    // repeat type for modify size
    public enum RepType {
        REPEAT, ALT, CLEAR
    }

}
