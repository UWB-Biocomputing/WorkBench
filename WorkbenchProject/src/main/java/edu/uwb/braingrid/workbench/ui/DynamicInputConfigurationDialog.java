package edu.uwb.braingrid.workbench.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.uwb.braingrid.general.FileSelectorDirMgr;
import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbench.SystemConfig;
import edu.uwb.braingrid.workbench.data.DynamicInputConfigurationManager;
import edu.uwb.braingrid.workbench.data.InputAnalyzer;
import edu.uwb.braingrid.workbench.data.InputAnalyzer.InputType;

/**
 * Dynamic Input Configuration Dialog The GUI is built dynamically according to the input
 * Configuration XML file.
 *
 * @author Tom Wong Extended by Joseph Conquest
 * @version 1.3
 */
public class DynamicInputConfigurationDialog extends javax.swing.JDialog {

    // <editor-fold defaultstate="collapsed" desc="Auto-Generated Code">
    /**
     * This method is called from within the constructor to initialize the form.
     *
     * WARNING: Do NOT modify this code. The content of this method is always regenerated by
     * the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabs = new javax.swing.JTabbedPane();
        cancelButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        buildButton = new javax.swing.JButton();
        configFilenameTextField = new javax.swing.JTextField();
        configFilenameLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        messageLabel = new javax.swing.JLabel();
        messageLabelText = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Simulator Configuration");
        setSize(new java.awt.Dimension(200, 200));

        tabs.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        nextButton.setText("Next");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        buildButton.setText("Build");
        buildButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildButtonActionPerformed(evt);
            }
        });

        configFilenameLabel.setText("Config Filename:");

        messageLabel.setText("Message:");

        messageLabelText.setText("None");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSeparator1)
                .addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(buildButton).addGap(18, 18, 18).addComponent(configFilenameLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(configFilenameTextField).addGap(18, 18, 18).addComponent(nextButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton))
                        .addGroup(layout.createSequentialGroup().addComponent(messageLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(messageLabelText).addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                .addComponent(tabs));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(tabs, javax.swing.GroupLayout.PREFERRED_SIZE, 261,
                                javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(messageLabel).addComponent(messageLabelText))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cancelButton).addComponent(nextButton).addComponent(buildButton)
                                .addComponent(configFilenameTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(configFilenameLabel))
                        .addContainerGap()));

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void buildButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buildButtonActionPerformed
        // gather param values
        ArrayList<String> paramStrs = new ArrayList<>();
        for (int i = 0; i < paramsTextFields.size(); i++) {
                paramStrs.add(paramsTextFields.get(i).getText());
        }

        lastResultFileName = ensureValidResultFileName(resultFileNameTextField.getText());

        icm.updateParamValues(paramStrs);

        try {
            String fileName = configFilenameTextField.getText();
            if (fileName != null && !fileName.isEmpty()) {
                fileName = icm.buildAndPersist(projectName, fileName);
                if (fileName != null) {
                    nextButton.setEnabled(true);
                    lastBuiltFile = fileName;
                    messageLabelText
                            .setText("<html><span style=\"color:green\">" + FileManager.getSimpleFilename(fileName)
                                    + " successfully persisted..." + "</span></html>");
                } else {
                    messageLabelText
                            .setText("<html><span style=\"color:red\">*All fields must be filled</span></html>");
                }
            }
        } catch (TransformerException | IOException e) {
            messageLabelText.setText(
                    "<html><span style=\"color:red\">" + e.getClass() + " prevented successful build...</span></html>");
            e.printStackTrace();
        }
    }// GEN-LAST:event_buildButtonActionPerformed

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okButtonActionPerformed
        runClicked = true;
        setVisible(false);
    }// GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
    }// GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buildButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel configFilenameLabel;
    private javax.swing.JTextField configFilenameTextField;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JLabel messageLabelText;
    private javax.swing.JButton nextButton;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Custom Members">
    private static final Logger LOG
            = Logger.getLogger(DynamicInputConfigurationDialog.class.getName());
    private static final long SERIAL_VERSION_UID = 1L;
    private DynamicInputConfigurationManager icm;
    private Document xmlDoc = null;
    private ArrayList<JTextField> paramsTextFields = new ArrayList<>();
    private JTextField resultFileNameTextField = null;
    private boolean runClicked = false;
    private String lastBuiltFile = null;
    private String lastResultFileName = null;
    private String projectName = null;
    private ArrayList<String> tabPaths = new ArrayList<>();
    private HashMap<Character, String> nListPresets = null;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Construction">
    /**
     *
     * @param projectName
     * @param modal
     * @param configFilename
     * @param aIcm
     * @param nListPresets
     */
    public DynamicInputConfigurationDialog(String projectName, boolean modal, String configFilename,
            DynamicInputConfigurationManager aIcm, HashMap<Character, String> nListPresets) {
        LOG.info("New " + getClass().getName() + " for " + projectName);
        initComponents();
        setModal(modal);
        this.projectName = projectName;
        this.nListPresets = nListPresets;
        try {
            icm = aIcm;
            xmlDoc = icm.getInputConfigDoc();
            // Get Node paths for each param class
            Node baseTemplateInfoNode = SystemConfig.getBaseTemplateInfoDoc().getFirstChild();
            NodeList paramsClassesNodes = baseTemplateInfoNode.getChildNodes();
            for (int i = 0; i < paramsClassesNodes.getLength(); i++) {
                Node paramsClassesNode = paramsClassesNodes.item(i);
                if (paramsClassesNode.getNodeType() == Node.ELEMENT_NODE) {
                    String paramsClassesNodePath = ((Element) paramsClassesNode)
                            .getAttribute(SystemConfig.NODE_PATH_ATTRIBUTE_NAME);
                    tabPaths.add(paramsClassesNodePath);
                }
            }
            buildTabsGUI(xmlDoc);

        } catch (Exception e) {
            System.err.println(e.toString());
        }
        if (icm != null) {
            setInitValues();
            if (configFilename != null && !configFilename.equals("")) {
                File configFile = new File(configFilename);
                configFilenameTextField.setText(projectName + ".xml");
            } else {
                File configFile = new File(projectName + ".xml");
                configFilenameTextField.setText(configFile.getName());
            }
            setPreferredSize(new Dimension(700, 365));
            nextButton.setEnabled(false);

            // show window center-screen
            pack();
            center();
            setVisible(true);
        }
    }

    /**
     * Build the GUI according to the input XML File. This is the second part of the configure
     * popup.
     *
     * @param aDoc
     * @throws javax.xml.xpath.XPathExpressionException
     */
    public void buildTabsGUI(Document aDoc) throws XPathExpressionException {
        ArrayList<Node> inputElements = new ArrayList<>();

        for (String tabPath : tabPaths) {
            XPathExpression xpath = XPathFactory.newInstance().newXPath().compile(tabPath);
            NodeList nodeList = (NodeList) xpath.evaluate(aDoc, XPathConstants.NODESET);
            Node tab = nodeList.item(0);

            if (tab == null) {
                continue;
            }

            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
            // add tab to GUI
            tabs.add(((Element) tab).getAttribute(SystemConfig.NAME_ATTRIBUTE_NAME),
                    new JScrollPane(contentPanel));

            // go to next node if current node is empty
            if (!tab.hasChildNodes() || (tab.getChildNodes().getLength() == 1
                    && tab.getFirstChild().getNodeType() == Node.TEXT_NODE
                    && tab.getFirstChild().getNodeValue().trim().isEmpty())) {
                contentPanel.add(new JLabel("No parameters to configure"));
                continue;
            }

            // get section nodes.
            NodeList sections = tab.getChildNodes();
            for (int j = 0; j < sections.getLength(); j++) {
                Node section = sections.item(j);
                if (section.getNodeType() == Node.ELEMENT_NODE) {
                    JPanel subPanel = new JPanel();
                    GroupLayout subLayout = new GroupLayout(subPanel);
                    subLayout.setAutoCreateGaps(true);
                    subPanel.setLayout(subLayout);
                    JLabel label = new JLabel(((Element) section).getAttribute(
                            SystemConfig.NAME_ATTRIBUTE_NAME));

                    subLayout.setHorizontalGroup(subLayout.createSequentialGroup()
                            .addComponent(label));
                    subLayout.setVerticalGroup(subLayout.createSequentialGroup()
                            .addGroup(subLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(label)));

                    contentPanel.add(subPanel);

                    // Get parameter nodes
                    NodeList params = sections.item(j).getChildNodes();
                    for (int k = 0; k < params.getLength(); k++) {
                        Node param = params.item(k);
                        if (param.getNodeType() == Node.ELEMENT_NODE) {
                            inputElements.add(param);

                            subPanel = new JPanel();
                            subLayout = new GroupLayout(subPanel);
                            subPanel.setLayout(subLayout);
                            subLayout.setAutoCreateGaps(true);
                            label = new JLabel(((Element) param).getAttribute(
                                    SystemConfig.NAME_ATTRIBUTE_NAME));

                            JTextField field = new JTextField(param.getTextContent());
                            String nodeName = ((Element) param).getNodeName();
                            String type = ((Element) param).getAttribute("type");
                            if (type.equals("InputFile")) {
                                JButton button = new JButton("Import");
                                button.addActionListener(new ImportFileButtonListener(
                                        SystemConfig.TAG_NAME_INPUT_TYPE_MAPPING.get(nodeName),
                                        field));
                                field.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                                        field.getPreferredSize().height));
                                subLayout.setHorizontalGroup(subLayout.createSequentialGroup()
                                        .addComponent(label).addComponent(field)
                                        .addComponent(button));
                                subLayout.setVerticalGroup(subLayout.createSequentialGroup()
                                        .addGroup(subLayout
                                                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(label)
                                                .addComponent(field).addComponent(button)));
                                if (nListPresets != null) {
                                    if (label.getText().equals("activeNListFileName")
                                            && nListPresets.containsKey('A')) {
                                        importNeuronList(SystemConfig.TAG_NAME_INPUT_TYPE_MAPPING
                                                .get(nodeName), field, nListPresets.get('A'));
                                    } else if (label.getText().equals("inhNListFileName")
                                            && nListPresets.containsKey('I')) {
                                        importNeuronList(SystemConfig.TAG_NAME_INPUT_TYPE_MAPPING
                                                .get(nodeName), field, nListPresets.get('I'));
                                    } else if (label.getText().equals("prbNListFileName")
                                            && nListPresets.containsKey('P')) {
                                        importNeuronList(SystemConfig.TAG_NAME_INPUT_TYPE_MAPPING
                                                .get(nodeName), field, nListPresets.get('P'));
                                    }
                                }
                            } else {
                                field.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                                        field.getPreferredSize().height));
                                subLayout.setHorizontalGroup(subLayout.createSequentialGroup()
                                        .addComponent(label).addComponent(field));
                                subLayout.setVerticalGroup(subLayout.createSequentialGroup()
                                        .addGroup(subLayout
                                                .createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(label).addComponent(field)));
                            }

                            if (nodeName.equals(SystemConfig.RESULT_FILE_NAME_TAG_NAME)) {
                                field.setText(setInitialResultFileName());
                                resultFileNameTextField = field;
                                lastResultFileName = field.getText();
                            }
                            paramsTextFields.add(field);
                            contentPanel.add(subPanel);
                        }
                    }
                }
            }
        }

        icm.setInputParamElements(inputElements);
    }

    private FileSelectorDirMgr fileSelector = new FileSelectorDirMgr();
    // File Button Listener used to handle the copy of the file and set the path
    private class ImportFileButtonListener implements ActionListener {
        private InputType type;
        private JTextField field;

        ImportFileButtonListener(InputType aType, JTextField aField) {
            LOG.info("New " + getClass().getName() + " Type: " + aType.toString());
            type = aType;
            field = aField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            importNeuronList(type, field);
        }

        private void importNeuronList(InputType aType, JTextField aField) {
            // get starting folder
            String simConfigFilesDir;
            if (fileSelector.getLastDir() == null) {
                simConfigFilesDir = FileManager.getDefaultProjectDirectory().toString();
            } else {
                simConfigFilesDir = fileSelector.getLastDir().getAbsolutePath();
            }

            JFileChooser dlg = new JFileChooser(simConfigFilesDir);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("XML file (*.xml)", "xml");
            dlg.addChoosableFileFilter(filter);
            dlg.setFileFilter(filter);
            dlg.setMultiSelectionEnabled(true);
            String dialogTitle = "Select Input Files for a Simulation";
            dlg.setDialogTitle(dialogTitle);
            if (dlg.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                try {
                    File sourceFile = dlg.getSelectedFile();
                    // if type is correct
                    if (InputAnalyzer.getInputType(sourceFile) == aType) {
                        Path destFilePath = FileManager.getNeuronListFilePath(projectName,
                                sourceFile.getName(), true);
                        if (FileManager.copyFile(sourceFile.toPath(), destFilePath)) {
                            aField.setText("configfiles/NList/" + sourceFile.getName());
                            fileSelector.addDir(sourceFile.getParentFile());
                        }
                        messageLabelText.setText("<html><span style=\"color:green\">"
                                + "Good!</span></html>");
                    } else {
                        messageLabelText.setText("<html><span style=\"color:orange\">"
                                + "The selected file did not match the type: "
                                + aType.toString() + "</span></html>");
                    }
                } catch (ParserConfigurationException | SAXException | IOException ex) {
                    messageLabelText.setText("<html><span style=\"color:red\">" + ex.getClass()
                            + "occurred, import failed...</span></html>");
                }
            } else {
                messageLabelText.setText("<html><span style=\"color:red\">"
                        + "Import Cancelled...</span></html>");
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public boolean getSuccess() {
        return runClicked;
    }

    public String getBuiltFile() {
        String builtFile = null;
        if (runClicked) {
            builtFile = lastBuiltFile;
        }
        return builtFile;
    }

    public String getResultFileName() {
        String fileName = null;
        if (runClicked) {
            fileName = lastResultFileName;
        }
        return fileName;
    }

    // set up each of the text fields with default values
    private void setInitValues() {
        // load values from template
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="UI Manipulation">
    private void center() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
    }
    // </editor-fold>

    /**
     * Helper function ensures resultFileName has "results/" as prefix and ".xml" as postfix.
     *
     * @param resultFileName  The result file name to be validated
     * @return An result file name that is guaranteed to have the proper prefix and postfix
     */
    private String ensureValidResultFileName(String resultFileName) {
        String validResultFileName = resultFileName;
        if (!resultFileName.startsWith("results/")) {
            validResultFileName = "results/" + resultFileName;
        }
        if (!resultFileName.endsWith(".xml")) {
            validResultFileName += ".xml";
        }
        return validResultFileName;
    }

    private String setInitialResultFileName() {
        return "results/" + projectName + "-out.xml";
    }

    private void importNeuronList(InputType aType, JTextField aField, String filename) {
        try {
            File sourceFile = new File(filename);
            // if type is correct
            if (InputAnalyzer.getInputType(sourceFile) == aType) {
                Path destFilePath = FileManager.getNeuronListFilePath(projectName,
                        sourceFile.getName(), true);
                if (FileManager.copyFile(sourceFile.toPath(), destFilePath)) {
                    aField.setText("configfiles/NList/" + sourceFile.getName());
                    fileSelector.addDir(sourceFile.getParentFile());
                }
                messageLabelText.setText("<html><span style=\"color:green\">"
                        + "Good!</span></html>");
            } else {
                messageLabelText.setText("<html><span style=\"color:orange\">"
                        + "The selected file did not match the type: " + aType.toString()
                        + "</span></html>");
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            messageLabelText.setText("<html><span style=\"color:red\">" + ex.getClass()
                    + "occurred, import failed...</span></html>");
        }
    }
}
