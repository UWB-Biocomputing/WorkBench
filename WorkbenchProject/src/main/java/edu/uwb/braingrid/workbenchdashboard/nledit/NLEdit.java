package edu.uwb.braingrid.workbenchdashboard.nledit;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
import edu.uwb.braingrid.workbenchdashboard.WorkbenchDisplay;

public class NLEdit extends WorkbenchApp {

    // repeat type for modify size
    public enum RepType {
        REPEAT, ALT, CLEAR
    }

    private static final Logger LOG = Logger.getLogger(NLEdit.class.getName());

    private BorderPane borderPane = new BorderPane();

    // Toolbar
    private Button importItemBtn = new Button("Import");
    private Button exportItemBtn = new Button("Export");
    private Button clearItemBtn = new Button("Clear");
    private Button printItemBtn = new Button("Print");
    private Button bcellItemBtn = new Button("Zoom In");
    private Button scellItemBtn = new Button("Zoom Out");
    private Button gpatItemBtn = new Button("_Generate pattern");
    private Button aprbItemBtn = new Button("_Arrange probes");
    private Button sdatItemBtn = new Button("Stats");

    private ToggleGroup editGroup = new ToggleGroup();
    private RadioButton inhNItem = new RadioButton("Inhibitory neurons");
    private RadioButton activeNItem = new RadioButton("Active neurons");
    private RadioButton probedNItem = new RadioButton("Probed neurons");

    private ToggleGroup toggleGroup;
    private RadioButton newButton = new RadioButton("New");
    private RadioButton rptButton = new RadioButton("Repeat");
    private RadioButton altButton = new RadioButton("Alternate");

    // Reference to workbench (or other frame code launching NLEdit)
    private WorkbenchManager workbenchMgr;
    private LayoutPanel layoutPanel; // reference to the layout panel
    private NeuronsLayout neuronsLayout;
    private NLSimUtil nlSimUtil;

    public NLEdit(Tab tab) {
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
        return borderPane;
    }

    public BorderPane getBP() {
        return borderPane;
    }

    private void generateSimulator() {
        neuronsLayout = new NeuronsLayout();

        layoutPanel = new LayoutPanel(this, new Dimension(LayoutPanel.DEF_X_CELLS,
                LayoutPanel.DEF_Y_CELLS), neuronsLayout);
        JScrollPane scrollpane = new JScrollPane(layoutPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        layoutPanel.setScrollPane(scrollpane);

        SwingNode scrollPaneNode = new SwingNode();
        scrollPaneNode.setContent(scrollpane);
        borderPane.setCenter(scrollPaneNode);
        nlSimUtil = new NLSimUtil(layoutPanel, neuronsLayout);
    }

    /**
     * The public function getNeuronType returns the current edit mode, which is called from
     * LayoutPanel.
     *
     * @return Current edit mode: LayoutPanel.INH - inhibitory neurons edit mode
     *                            LayoutPanel.ACT - active neurons edit mode
     *                            LayoutPanel.PRB - probed neurons edit mode
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
        borderPane.setLeft(vbox);
    }

    private void initToolbar() {
        primeButton(clearItemBtn,
                "/icons/baseline-clear-black-18/1x/baseline_clear_black_18dp.png",
                "Clear Neurons");
        clearItemBtn.getStyleClass().add("clear-button");
        clearItemBtn.setOnAction(event -> {
            actionClear();
        });

        primeButton(importItemBtn,
                "/icons/baseline-input-black-18/1x/baseline_input_black_18dp.png",
                "Import Neuron Layout");
        importItemBtn.setOnAction(event -> {
            actionImport();
        });

        primeButton(exportItemBtn,
                "/icons/baseline-save_alt-black-18/1x/baseline_save_alt_black_18dp.png",
                "Export Neuron Layout");
        exportItemBtn.setOnAction(event -> {
            actionExport();
        });

        primeButton(printItemBtn, "/icons/baseline-local_printshop-black-18/1x/"
                + "baseline_local_printshop_black_18dp.png", "Print");
        printItemBtn.setOnAction(event -> {
            actionPrint();
        });

        primeButton(bcellItemBtn,
                "/icons/baseline-zoom_in-black-18/1x/baseline_zoom_in_black_18dp.png", "Zoom In");
        bcellItemBtn.setOnAction(event -> {
            actionBiggerCells();
        });

        primeButton(scellItemBtn, "/icons/baseline-zoom_out-black-18/1x/"
                + "baseline_zoom_out_black_18dp.png", "Zoom Out");
        scellItemBtn.setOnAction(event -> {
            actionSmallerCells();
        });

        primeButton(sdatItemBtn,
                "/icons/baseline-data_usage-black-18/1x/baseline_data_usage_black_18dp.png",
                "Stats");
        sdatItemBtn.setOnAction(event -> {
            actionStatisticalData();
        });

        HBox toolbar = new HBox(bcellItemBtn, scellItemBtn, printItemBtn, sdatItemBtn,
                importItemBtn, exportItemBtn, clearItemBtn);

        toolbar.getStyleClass().add("toolbar");

        borderPane.setTop(toolbar);
    }

    private void primeButton(Button button, String imagePath, String tooltip) {
        setButtonImage(button, imagePath);
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
        Label lblSizeX = new Label("Size x:");
        Label lblSizeY = new Label("Size y:");

        TextField tFieldX = new TextField("10");
        TextField tFieldY = new TextField("10");

        Button btnSubmit = new Button("Submit");
        btnSubmit.setOnAction(event -> {
            int sizeX = 0;
            int sizeY = 0;

            try {
                sizeX = Integer.parseInt(tFieldX.getText());
                tFieldX.setStyle("-fx-text-fill: black;");
            } catch (NumberFormatException e) {
                tFieldX.setStyle("-fx-text-fill: red;");
            }

            try {
                sizeY = Integer.parseInt(tFieldY.getText());
                tFieldY.setStyle("-fx-text-fill: black;");
            } catch (NumberFormatException e) {
                tFieldY.setStyle("-fx-text-fill: red;");
            }

            boolean inboundsX = sizeX >= LayoutPanel.MIN_X_CELLS
                    && sizeX <= LayoutPanel.MAX_X_CELLS;
            boolean inboundsY = sizeY >= LayoutPanel.MIN_Y_CELLS
                    && sizeY <= LayoutPanel.MAX_Y_CELLS;

            if (inboundsX && inboundsY) {
                actionModifySize(sizeX, sizeY);

                JScrollPane scrollpane = new JScrollPane(layoutPanel,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                layoutPanel.setScrollPane(scrollpane);

                SwingNode scrollPaneNode = new SwingNode();
                scrollPaneNode.setContent(scrollpane);

                borderPane.setCenter(scrollPaneNode);
            } else {
                if (!inboundsX) {
                    tFieldX.setStyle("-fx-text-fill: red;");
                }
                if (!inboundsY) {
                    tFieldY.setStyle("-fx-text-fill: red;");
                }
            }
        });

        gpatItemBtn.setOnAction(event -> {
            actionGeneratePattern();
        });

        aprbItemBtn.setOnAction(event -> {
            actionArrangeProbes();
        });

        toggleGroup = new ToggleGroup();

        newButton.setToggleGroup(toggleGroup);
        rptButton.setToggleGroup(toggleGroup);
        altButton.setToggleGroup(toggleGroup);

        HBox hboxPatternGen = new HBox(gpatItemBtn, aprbItemBtn);
        hboxPatternGen.setSpacing(10);

        HBox hboxSizeX = new HBox(lblSizeX, tFieldX);
        HBox hboxSizeY = new HBox(lblSizeY, tFieldY);
        HBox hboxResize = new HBox(hboxSizeX, hboxSizeY, btnSubmit);
        hboxResize.setSpacing(10);

        HBox hboxLeft = new HBox(hboxResize, newButton, rptButton, altButton);
        hboxLeft.setSpacing(20);

        HBox hboxBottom = new HBox(hboxLeft, hboxPatternGen);
        hboxBottom.getStyleClass().add("sizebox");
        hboxBottom.setSpacing(200);
        borderPane.setBottom(hboxBottom);
    }

    /**
     * The 'Clear' menu handler.
     */
    private void actionClear() {
        neuronsLayout.inhNList.clear();
        neuronsLayout.activeNList.clear();
        neuronsLayout.probedNList.clear();

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
        dialog.initOwner(WorkbenchDisplay.getPrimaryStage());

        imprt.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                readNeuronListFromFile(myPanel.tFields[ImportPanel.IDX_INH_LIST].getText(),
                        neuronsLayout.inhNList, LayoutPanel.INH);
                readNeuronListFromFile(myPanel.tFields[ImportPanel.IDX_ACT_LIST].getText(),
                        neuronsLayout.activeNList, LayoutPanel.ACT);
                readNeuronListFromFile(myPanel.tFields[ImportPanel.IDX_PRB_LIST].getText(),
                        neuronsLayout.probedNList, LayoutPanel.PRB);

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
     * The function readNeuronListFromFile reads neurons list from the file specified by
     * nameNListFile and stores neurons index in list.
     *
     * @param nameNListFile  file path of the neurons list (xml format)
     * @param list  array list to store neurons index
     * @param type  type of neurons
     */
    private void readNeuronListFromFile(String nameNListFile, ArrayList<Integer> list, int type) {
        if (nameNListFile == null || nameNListFile.length() == 0) {
            return;
        }

        try {
            // read a xml file
            Document doc = new SAXBuilder().build(new File(nameNListFile));
            Element root = doc.getRootElement();
            if ((root != null) && ((root.getName().equals("A") && type == LayoutPanel.ACT)
                    || (root.getName().equals("I") && type == LayoutPanel.INH)
                    || (root.getName().equals("P") && type == LayoutPanel.PRB))) {
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
        } catch (JDOMException | IOException e) {
            System.err.println(e);
        }
    }

    /**
     * The 'Export...' menu handler.
     */
    private void actionExport() {
        exportPopup();
    }

    public void exportPopup() {
        ExportPanel myPanel = new ExportPanel(ImportPanel.getNListDir());

        final Stage dialog = new Stage();
        dialog.setTitle("Export");
        Button yes = new Button("Yes");
        Button no = new Button("No");

        dialog.initModality(Modality.NONE);
        dialog.initOwner(WorkbenchDisplay.getPrimaryStage());

        Long functionStartTime = System.currentTimeMillis();
        Long accumulatedTime = 0L;

        yes.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                Long accumulatedTime = 0L;
                writeNeuronListToFile(myPanel.tFields[ExportPanel.IDX_INH_LIST].getText(),
                        neuronsLayout.inhNList, LayoutPanel.INH);
                // add to workbench project
                if (null != workbenchMgr && workbenchMgr.isProvEnabled()) {
                    Long startTime = System.currentTimeMillis();
                    Resource file = workbenchMgr.getProvMgr().addFileGeneration(
                            "InhibitoryNeuronListExport" + java.util.UUID.randomUUID(),
                            "neuronListExport", "NLEdit", null, false,
                            myPanel.tFields[ExportPanel.IDX_INH_LIST].getText(), null, null);

                    // Tell Java to stop considering the file to be in it's control
                    try {
                        ((FileOutputStream) file).close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    accumulatedTime = DateTime.sumProvTiming(startTime, accumulatedTime);
                }

                writeNeuronListToFile(myPanel.tFields[ExportPanel.IDX_ACT_LIST].getText(),
                        neuronsLayout.activeNList, LayoutPanel.ACT);
                // add to workbench project
                if (null != workbenchMgr && workbenchMgr.isProvEnabled()) {
                    Long startTime = System.currentTimeMillis();
                    Resource file = workbenchMgr.getProvMgr().addFileGeneration(
                            "ActiveNeuronListExport" + java.util.UUID.randomUUID(),
                            "neuronListExport", "NLEdit", null, false,
                            myPanel.tFields[ExportPanel.IDX_ACT_LIST].getText(), null, null);
                    accumulatedTime = DateTime.sumProvTiming(startTime, accumulatedTime);

                    // Tell Java to stop considering the file to be in it's control
                    try {
                        ((FileOutputStream) file).close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }

                writeNeuronListToFile(myPanel.tFields[ExportPanel.IDX_PRB_LIST].getText(),
                        neuronsLayout.probedNList, LayoutPanel.PRB);
                // add to workbench project
                if (null != workbenchMgr && workbenchMgr.isProvEnabled()) {
                    Long startTime = System.currentTimeMillis();
                    Resource file = workbenchMgr.getProvMgr().addFileGeneration(
                            "ProbedNeuronListExport" + java.util.UUID.randomUUID(),
                            "neuronListExport", "NLEdit", null, false,
                            myPanel.tFields[ExportPanel.IDX_PRB_LIST].getText(), null, null);
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
                        System.currentTimeMillis() - functionStartTime,
                        workbenchMgr.isProvEnabled());
                if (workbenchMgr.isProvEnabled()) {
                    DateTime.recordAccumulatedProvTiming("ControlFrame", "actionExport",
                            accumulatedTime);
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
                        System.currentTimeMillis() - functionStartTime,
                        workbenchMgr.isProvEnabled());
                if (workbenchMgr.isProvEnabled()) {
                    DateTime.recordAccumulatedProvTiming("ControlFrame", "actionExport",
                            accumulatedTime);
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
     * The function writeNeuronListToFile creates a neurons list file specified by list and type.
     *
     * @param nameNListFile  File path of the neurons list
     * @param list  Array list of neurons index
     * @param type  Type of neurons
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
            for (Integer integer : list) {
                sList += " " + integer;
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
        MyPrintable printable = new MyPrintable(job.defaultPage(), layoutPanel, nlSimUtil);

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
        RepType repType = RepType.CLEAR;
        if (newButton.isSelected()) {
            repType = RepType.CLEAR;
            // System.out.println("Clear");
        } else if (rptButton.isSelected()) {
            repType = RepType.REPEAT;
            // System.out.println("Repeat");
        } else if (altButton.isSelected()) {
            repType = RepType.ALT;
            // System.out.println("Alternate");
        }
        // System.out.println("Default");
        changeLayoutSize(new Dimension(sizeX, sizeY), repType);
    }

    /**
     * The function changeLayoutSize generates new neurons lists of inhNList, activeNList, and
     * activeNList, and changes the size of the layout panel.
     *
     * @param newSize  Size for the new layout panel
     * @param repType  Repeat type, CLEAR, REPEAT, or ALT
     */
    private void changeLayoutSize(Dimension newSize, RepType repType) {
        neuronsLayout.inhNList = nlSimUtil.repPattern(newSize,
                neuronsLayout.inhNList, repType);
        neuronsLayout.activeNList = nlSimUtil.repPattern(newSize,
                neuronsLayout.activeNList, repType);
        neuronsLayout.probedNList = nlSimUtil.repPattern(newSize,
                neuronsLayout.activeNList, repType);

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
        dialog.initOwner(WorkbenchDisplay.getPrimaryStage());

        yes.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                try {
                    float ratioInh = Float.parseFloat(myPanel.tFields[GPatternPanel.IDX_INH]
                            .getText());
                    float ratioAct = Float.parseFloat(myPanel.tFields[GPatternPanel.IDX_ACT]
                            .getText());

                    // validate ratios
                    if ((ratioInh < 0 || ratioInh > 1.0) || (ratioAct < 0 || ratioAct > 1.0)
                            || (ratioInh + ratioAct > 1.0)) {
                        throw new NumberFormatException();
                    }

                    if (myPanel.rButtons[GPatternPanel.IDX_REG].isSelected()) {
                        nlSimUtil.genRegularPattern(ratioInh, ratioAct);
                    } else if (myPanel.rButtons[GPatternPanel.IDX_RND].isSelected()) {
                        nlSimUtil.genRandomPattern(ratioInh, ratioAct);
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
        dialog.initOwner(WorkbenchDisplay.getPrimaryStage());

        yes.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                try {
                    int numProbes = Integer.parseInt(myPanel.tField.getText());

                    // validate number
                    Dimension dim = layoutPanel.getLayoutSize();
                    if (numProbes > dim.height * dim.width) {
                        throw new NumberFormatException();
                    }

                    nlSimUtil.genProbes(numProbes);

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
        String message = nlSimUtil.getStatisticalMsg(true);

        JOptionPane.showMessageDialog(null, message, "Statistical data", JOptionPane.PLAIN_MESSAGE);
    }
}
