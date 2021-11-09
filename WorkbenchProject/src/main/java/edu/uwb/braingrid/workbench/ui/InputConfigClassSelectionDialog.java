package edu.uwb.braingrid.workbench.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.LayoutStyle;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.uwb.braingrid.workbench.data.DynamicInputConfigurationManager;
import edu.uwb.braingrid.workbench.data.SystemConfig;

/**
 * Dialog to select the parameter classes before going to the parameter config screen.
 *
 * @author Tom Wong
 */
public class InputConfigClassSelectionDialog extends javax.swing.JDialog {

    // <editor-fold defaultstate="collapsed" desc="Initialize Form">

    /**
     * This method is called from within the constructor to initialize the form.
     * This is the first part of the popup you see.
     */
    // <editor-fold defaultstate="collapsed" desc="Initialize Components">
    private void initComponents() throws IOException, ParserConfigurationException, SAXException {

        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        messageLabelText = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Simulator Configuration");
        setSize(new java.awt.Dimension(200, 200));

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        messageLabelText.setText("None");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        GroupLayout.ParallelGroup horizontalComponentsLabels = layout
                .createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.ParallelGroup horizontalComponentsCBoxes = layout
                .createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.SequentialGroup verticalComponents = layout
                .createSequentialGroup().addContainerGap(17, Short.MAX_VALUE);

        loadComponents(layout, horizontalComponentsLabels, horizontalComponentsCBoxes, verticalComponents);

        verticalComponents
                .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(cancelButton).addComponent(okButton).addComponent(messageLabelText))
                .addContainerGap();

        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSeparator1)
                .addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup().addComponent(messageLabelText)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(okButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cancelButton))
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(horizontalComponentsLabels)
                                        .addGap(18, 18, 18)
                                        .addGroup(horizontalComponentsCBoxes)
                                        .addGap(0, 131, Short.MAX_VALUE)))
                        .addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING, verticalComponents));

        pack();
    }

    /**
     * Load components and Template paths.
     *
     * @param layout
     * @param horizontalComponentsLabels
     * @param horizontalComponentsCBoxes
     * @param verticalComponents
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    private void loadComponents(GroupLayout layout,
                                GroupLayout.ParallelGroup horizontalComponentsLabels,
                                GroupLayout.ParallelGroup horizontalComponentsCBoxes,
                                GroupLayout.SequentialGroup verticalComponents)
            throws IOException, SAXException, ParserConfigurationException {
        Document allParamsClassesDoc = SystemConfig.getAllParamsClassesDoc();
        NodeList roots = allParamsClassesDoc.getChildNodes();
        for (int i = 0; i < roots.getLength(); i++) {
            Node root = roots.item(i);
            if (root.getNodeType() == Node.ELEMENT_NODE) {
                NodeList paramsClassesTypes = root.getChildNodes();
                for (int j = 0; j < paramsClassesTypes.getLength(); j++) {
                    Node paramsClassesType = paramsClassesTypes.item(j);
                    if (paramsClassesType.getNodeType() == Node.ELEMENT_NODE) {
                        ArrayList<String> paramsTemplatePaths = new ArrayList<>();
                        String templateDirectory = ((Element) paramsClassesType)
                                .getAttribute(SystemConfig.TEMPLATE_DIRECTORY_ATTRIBUTE_NAME);
                        String paramsClassesTypeName = paramsClassesType.getNodeName();
                        JLabel classType;
                        if (paramsClassesTypeName.contains("Classes"))
                            classType = new JLabel(paramsClassesTypeName
                                    .substring(0, paramsClassesTypeName.length() - 7) + " Class:");
                        else
                            classType = new JLabel(paramsClassesTypeName);
                        JComboBox<Object> classTypeCBox = new JComboBox<>();
                        paramsClassesCBoxes.add(classTypeCBox);
                        horizontalComponentsLabels
                                .addComponent(classType, GroupLayout.Alignment.TRAILING);
                        horizontalComponentsCBoxes
                                .addComponent(classTypeCBox,
                                        GroupLayout.PREFERRED_SIZE,
                                        GroupLayout.DEFAULT_SIZE,
                                        GroupLayout.PREFERRED_SIZE);
                        verticalComponents
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(classType)
                                        .addComponent(classTypeCBox, GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18);
                        NodeList paramsClasses = paramsClassesType.getChildNodes();
                        for (int k = 0; k < paramsClasses.getLength(); k++) {
                            Node paramsClass = paramsClasses.item(k);
                            if (paramsClass.getNodeType() == Node.ELEMENT_NODE) {
                                String className = ((Element) paramsClass)
                                        .getAttribute(SystemConfig.NAME_ATTRIBUTE_NAME);
                                classTypeCBox.addItem(className);
                                String templatePath = templateDirectory
                                        + ((Element) paramsClass).getAttribute(
                                        SystemConfig.TEMPLATE_FILE_NAME_ATTRIBUTE_NAME);
                                paramsTemplatePaths.add(templatePath);
                            }
                        }
                        templatePaths.add(paramsTemplatePaths);
                    }
                }
            }
        }
    }
    // </editor-fold>//

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        LOG.info("Ok Button Clicked");
        try {
            setConfigXMLDoc();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        okClicked = true;
        setVisible(false);
        LOG.info("Ok Button ended");
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }

    // Variables declaration - do not modify
    private javax.swing.JButton cancelButton;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel messageLabelText;
    private javax.swing.JButton okButton;
    // End of variables declaration
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Custom Members">
    private static final Logger LOG
            = Logger.getLogger(InputConfigClassSelectionDialog.class.getName());
    private DynamicInputConfigurationManager icm;
    private Document baseTemplateInfoDoc = null;
    private Document xmlDoc = null;
    private boolean okClicked = false;

    private ArrayList<ArrayList<String>> templatePaths = new ArrayList<>();
    private ArrayList<String> nodePaths = new ArrayList<>();
    private ArrayList<JComboBox> paramsClassesCBoxes = new ArrayList<>();
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Construction">

    /**
     * @param projectName
     * @param modal
     * @param configFilename
     */
    public InputConfigClassSelectionDialog(String projectName, boolean modal,
                                           String configFilename) {
        LOG.info("New InputConfigClassSelectionDialog for " + projectName);
        try {
            initComponents();
            setModal(modal);
            baseTemplateInfoDoc = SystemConfig.getBaseTemplateInfoDoc();
            Node baseTemplateInfoNode = baseTemplateInfoDoc.getFirstChild();

            icm = new DynamicInputConfigurationManager(configFilename);
            xmlDoc = icm.getInputConfigDoc();
            loadNodePaths();
            setComboBoxOptions();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        if (icm != null) {
            // show window center-screen
            pack();
            center();
            setVisible(true);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public boolean getSuccess() {
        return okClicked;
    }

    public DynamicInputConfigurationManager getInputConfigMgr() {
        return icm;
    }

    /**
     * Get Node paths for each param class.
     */
    private void loadNodePaths() {
        Node baseTemplateInfoNode = baseTemplateInfoDoc.getFirstChild();
        NodeList paramsClassesNodes = baseTemplateInfoNode.getChildNodes();
        for (int i = 0; i < paramsClassesNodes.getLength(); i++) {
            Node paramsClassesNode = paramsClassesNodes.item(i);
            if (paramsClassesNode.getNodeType() == Node.ELEMENT_NODE) {
                String paramsClassesType = ((Element) paramsClassesNode)
                        .getAttribute(SystemConfig.NAME_ATTRIBUTE_NAME);
                String paramsClassesNodePath = ((Element) paramsClassesNode)
                        .getAttribute(SystemConfig.NODE_PATH_ATTRIBUTE_NAME);
                if (!paramsClassesType.contains("SimInfo")) {
                    nodePaths.add(paramsClassesNodePath);
                }
            }
        }
    }

    /**
     * Set combo box options according to params classes from sim config XML file.
     *
     * @throws XPathExpressionException
     */
    private void setComboBoxOptions() throws XPathExpressionException {
        XPathExpression xpath;
        for (int i = 0; i < nodePaths.size(); i++) {
            xpath = XPathFactory.newInstance().newXPath().compile(nodePaths.get(i));
            NodeList nodeList = (NodeList) xpath.evaluate(xmlDoc, XPathConstants.NODESET);
            Node node = nodeList.item(0);
            String paramsClass = ((Element) node).getAttribute(SystemConfig.CLASS_ATTRIBUTE_NAME);
            paramsClassesCBoxes.get(i).setSelectedItem(paramsClass);
        }
    }

    /**
     * Set up XML config file according to the selected items in combo boxes.
     *
     * @throws Exception
     */
    private void setConfigXMLDoc() throws Exception {
        LOG.info("Start setConfigXMLDoc");
        XPathExpression xpath;
        for (int i = 0; i < nodePaths.size(); i++) {
            xpath = XPathFactory.newInstance().newXPath().compile(nodePaths.get(i));
            NodeList nodeList = (NodeList) xpath.evaluate(xmlDoc, XPathConstants.NODESET);
            Node node = nodeList.item(0);
            String paramsClass = ((Element) node).getAttribute(SystemConfig.CLASS_ATTRIBUTE_NAME);
            if (!paramsClass.equals(paramsClassesCBoxes.get(i).getSelectedItem())) {
                String templateFileURL = templatePaths.get(i).get(
                        paramsClassesCBoxes.get(i).getSelectedIndex());
                Document templateNode = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .parse(getClass().getResourceAsStream("/templates/" + templateFileURL));
                Node parentNode = node.getParentNode();
                Node newNode = xmlDoc.importNode(templateNode.getFirstChild(), true);
                parentNode.replaceChild(newNode, node);
            }
        }
        LOG.info("End setConfigXMLDoc");
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
}
