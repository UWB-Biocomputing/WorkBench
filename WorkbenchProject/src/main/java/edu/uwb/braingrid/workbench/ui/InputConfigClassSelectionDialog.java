package edu.uwb.braingrid.workbench.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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

import edu.uwb.braingrid.workbench.SystemConfig;
import edu.uwb.braingrid.workbench.data.DynamicInputConfigurationManager;

/**
 * Dialog to select the parameter classes before going to the parameter config screen.
 *
 * @author Tom Wong
 */
public class InputConfigClassSelectionDialog extends javax.swing.JDialog {

    // <editor-fold defaultstate="collapsed" desc="Auto-Generated Code">
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always regenerated by the
     * Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        messageLabelText = new javax.swing.JLabel();
        neuronsParamsClassLbl = new javax.swing.JLabel();
        synapsesParamsClassLbl = new javax.swing.JLabel();
        connectionsParamsClassLbl = new javax.swing.JLabel();
        layoutParamsClassLbl = new javax.swing.JLabel();
        neuronsParamsClassCBox = new javax.swing.JComboBox<>();
        connectionsParamsClassCBox = new javax.swing.JComboBox<>();
        synapsesParamsClassCBox = new javax.swing.JComboBox<>();
        layoutParamsClassCBox = new javax.swing.JComboBox<>();

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

        neuronsParamsClassLbl.setText("NeuronsParams Class:");

        synapsesParamsClassLbl.setText("SynapsesParams Class:");

        connectionsParamsClassLbl.setText("ConnectionsParams Class:");

        layoutParamsClassLbl.setText("LayoutParams Class:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
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
                        .addGroup(layout.createSequentialGroup().addGroup(layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(connectionsParamsClassLbl)
                                .addComponent(layoutParamsClassLbl, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(synapsesParamsClassLbl, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(neuronsParamsClassLbl, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(neuronsParamsClassCBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(connectionsParamsClassCBox,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(synapsesParamsClassCBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(layoutParamsClassCBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 131, Short.MAX_VALUE)))
                        .addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                layout.createSequentialGroup().addContainerGap(17, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(neuronsParamsClassLbl)
                                .addComponent(neuronsParamsClassCBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(synapsesParamsClassLbl)
                                .addComponent(synapsesParamsClassCBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(connectionsParamsClassLbl)
                                .addComponent(connectionsParamsClassCBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(layoutParamsClassLbl)
                                .addComponent(layoutParamsClassCBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cancelButton).addComponent(okButton).addComponent(messageLabelText))
                        .addContainerGap()));

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_okButtonActionPerformed
        LOG.info("Ok Button Clicked");
        try {
            setConfigXMLDoc();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        okClicked = true;
        setVisible(false);
        LOG.info("Ok Button ended");
    }// GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
    }// GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox<String> connectionsParamsClassCBox;
    private javax.swing.JLabel connectionsParamsClassLbl;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JComboBox<String> layoutParamsClassCBox;
    private javax.swing.JLabel layoutParamsClassLbl;
    private javax.swing.JLabel messageLabelText;
    private javax.swing.JComboBox<String> neuronsParamsClassCBox;
    private javax.swing.JLabel neuronsParamsClassLbl;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox<String> synapsesParamsClassCBox;
    private javax.swing.JLabel synapsesParamsClassLbl;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Custom Members">
    private static final Logger LOG
            = Logger.getLogger(InputConfigClassSelectionDialog.class.getName());
    private static final long SERIAL_VERSION_UID = 1L;
    private DynamicInputConfigurationManager icm;
    private Document baseTemplateInfoDoc = null;
    private Document xmlDoc = null;
    private boolean okClicked = false;
    private ArrayList<String> neuronsParamsTemplatePaths = new ArrayList<>();
    private ArrayList<String> synapsesParamsTemplatePaths = new ArrayList<>();
    private ArrayList<String> connectionsParamsTemplatePaths = new ArrayList<>();
    private ArrayList<String> layoutParamsTemplatePaths = new ArrayList<>();
    private String neuronsParamsNodePath = null;
    private String synapsesParamsNodePath = null;
    private String connectionsParamsNodePath = null;
    private String layoutParamsNodePath = null;
    private String neuronsParamsClass = null;
    private String synapsesParamsClass = null;
    private String connectionsParamsClass = null;
    private String layoutParamsClass = null;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Construction">
    /**
     *
     * @param projectName
     * @param modal
     * @param configFilename
     */
    public InputConfigClassSelectionDialog(String projectName, boolean modal,
            String configFilename) {
        LOG.info("New InputConfigClassSelectionDialog for " + projectName);
        initComponents();
        setModal(modal);
        try {
            baseTemplateInfoDoc = SystemConfig.getBaseTemplateInfoDoc();
            Node baseTemplateInfoNode = baseTemplateInfoDoc.getFirstChild();

            icm = new DynamicInputConfigurationManager(configFilename);
            xmlDoc = icm.getInputConfigDoc();
            loadParamsClassCBoxes();
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
     * setup Parameter classes combo boxes. This is the first part of the popup you see.
     *
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws XPathExpressionException
     */
    private void loadParamsClassCBoxes() throws IOException, ParserConfigurationException,
            SAXException, XPathExpressionException {
        Document allParamsClassesDoc = SystemConfig.getAllParamsClassesDoc();
        NodeList roots = allParamsClassesDoc.getChildNodes();

        // build list of template paths for each params class while adding each class name to the
        // appropriate combo box
        for (int i = 0; i < roots.getLength(); i++) {
            Node root = roots.item(i);
            if (root.getNodeType() == Node.ELEMENT_NODE) {
                NodeList paramsClassesTypes = root.getChildNodes();
                for (int j = 0; j < paramsClassesTypes.getLength(); j++) {
                    Node paramsClassesType = paramsClassesTypes.item(j);
                    if (paramsClassesType.getNodeType() == Node.ELEMENT_NODE) {
                        String templateDirectory = ((Element) paramsClassesType)
                                .getAttribute(SystemConfig.TEMPLATE_DIRECTORY_ATTRIBUTE_NAME);
                        NodeList paramsClasses = paramsClassesType.getChildNodes();
                        for (int k = 0; k < paramsClasses.getLength(); k++) {
                            Node paramsClass = paramsClasses.item(k);
                            if (paramsClass.getNodeType() == Node.ELEMENT_NODE) {
                                String className = ((Element) paramsClass)
                                        .getAttribute(SystemConfig.NAME_ATTRIBUTE_NAME);
                                String templatePath = templateDirectory
                                        + ((Element) paramsClass).getAttribute(
                                                SystemConfig.TEMPLATE_FILE_NAME_ATTRIBUTE_NAME);
                                String paramsClassesTypeName = paramsClassesType.getNodeName();
                                switch (paramsClassesTypeName) {
                                case SystemConfig.NEURONS_PARAMS_CLASSES_TAG_NAME:
                                    neuronsParamsClassCBox.addItem(className);
                                    neuronsParamsTemplatePaths.add(templatePath);
                                    break;
                                case SystemConfig.SYNAPSES_PARAMS_CLASSES_TAG_NAME:
                                    synapsesParamsClassCBox.addItem(className);
                                    synapsesParamsTemplatePaths.add(templatePath);
                                    break;
                                case SystemConfig.CONNECTIONS_PARAMS_CLASSES_TAG_NAME:
                                    connectionsParamsClassCBox.addItem(className);
                                    connectionsParamsTemplatePaths.add(templatePath);
                                    break;
                                case SystemConfig.LAYOUT_PARAMS_CLASSES_TAG_NAME:
                                    layoutParamsClassCBox.addItem(className);
                                    layoutParamsTemplatePaths.add(templatePath);
                                    break;
                                default:
                                    // unknown param class type
                                }
                            }
                        }
                    }
                }
            }
        }

        // get Node paths for each param class
        Node baseTemplateInfoNode = baseTemplateInfoDoc.getFirstChild();
        NodeList paramsClassesNodes = baseTemplateInfoNode.getChildNodes();
        for (int i = 0; i < paramsClassesNodes.getLength(); i++) {
            Node paramsClassesNode = paramsClassesNodes.item(i);
            if (paramsClassesNode.getNodeType() == Node.ELEMENT_NODE) {
                String paramsClassesType = ((Element) paramsClassesNode)
                        .getAttribute(SystemConfig.NAME_ATTRIBUTE_NAME);
                String paramsClassesNodePath = ((Element) paramsClassesNode)
                        .getAttribute(SystemConfig.NODE_PATH_ATTRIBUTE_NAME);
                switch (paramsClassesType) {
                case SystemConfig.NEURONS_PARAMS_CLASSES_TAG_NAME:
                    neuronsParamsNodePath = paramsClassesNodePath;
                    break;
                case SystemConfig.SYNAPSES_PARAMS_CLASSES_TAG_NAME:
                    synapsesParamsNodePath = paramsClassesNodePath;
                    break;
                case SystemConfig.CONNECTIONS_PARAMS_CLASSES_TAG_NAME:
                    connectionsParamsNodePath = paramsClassesNodePath;
                    break;
                case SystemConfig.LAYOUT_PARAMS_CLASSES_TAG_NAME:
                    layoutParamsNodePath = paramsClassesNodePath;
                    break;
                default:
                    // unknown param class type
                }
            }
        }

        // set combo box options according to params classes from sim config XML file
        XPathExpression xpath = XPathFactory.newInstance().newXPath()
                .compile(neuronsParamsNodePath);
        NodeList nodeList = (NodeList) xpath.evaluate(xmlDoc, XPathConstants.NODESET);
        Node node = nodeList.item(0);
        neuronsParamsClass = ((Element) node).getAttribute(SystemConfig.CLASS_ATTRIBUTE_NAME);
        neuronsParamsClassCBox.setSelectedItem(neuronsParamsClass);

        xpath = XPathFactory.newInstance().newXPath().compile(synapsesParamsNodePath);
        nodeList = (NodeList) xpath.evaluate(xmlDoc, XPathConstants.NODESET);
        node = nodeList.item(0);
        synapsesParamsClass = ((Element) node).getAttribute(SystemConfig.CLASS_ATTRIBUTE_NAME);
        synapsesParamsClassCBox.setSelectedItem(synapsesParamsClass);

        xpath = XPathFactory.newInstance().newXPath().compile(connectionsParamsNodePath);
        nodeList = (NodeList) xpath.evaluate(xmlDoc, XPathConstants.NODESET);
        node = nodeList.item(0);
        connectionsParamsClass = ((Element) node).getAttribute(SystemConfig.CLASS_ATTRIBUTE_NAME);
        connectionsParamsClassCBox.setSelectedItem(connectionsParamsClass);

        xpath = XPathFactory.newInstance().newXPath().compile(layoutParamsNodePath);
        nodeList = (NodeList) xpath.evaluate(xmlDoc, XPathConstants.NODESET);
        node = nodeList.item(0);
        layoutParamsClass = ((Element) node).getAttribute(SystemConfig.CLASS_ATTRIBUTE_NAME);
        layoutParamsClassCBox.setSelectedItem(layoutParamsClass);
    }

    /**
     * Set up XML config file according to the selected items in combo boxes.
     */
    private void setConfigXMLDoc() throws Exception {
        LOG.info("Start setConfigXMLDoc");
        // get neurons Params Node from the path
        XPathExpression xpath = XPathFactory.newInstance().newXPath()
                .compile(neuronsParamsNodePath);
        NodeList nodeList = (NodeList) xpath.evaluate(xmlDoc, XPathConstants.NODESET);
        Node node = nodeList.item(0);
        if (!neuronsParamsClass.equals(neuronsParamsClassCBox.getSelectedItem())) {
            String templateFileURL = neuronsParamsTemplatePaths.get(
                    neuronsParamsClassCBox.getSelectedIndex());
            Document templateNode = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(getClass().getResourceAsStream("/templates/" + templateFileURL));
            Node parentNode = node.getParentNode();
            Node newNode = xmlDoc.importNode(templateNode.getFirstChild(), true);
            parentNode.replaceChild(newNode, node);
        }

        // get synapses Params Node from the path
        xpath = XPathFactory.newInstance().newXPath().compile(synapsesParamsNodePath);
        nodeList = (NodeList) xpath.evaluate(xmlDoc, XPathConstants.NODESET);
        node = nodeList.item(0);
        if (!synapsesParamsClass.equals(synapsesParamsClassCBox.getSelectedItem())) {
            String templateFileURL = synapsesParamsTemplatePaths.get(
                    synapsesParamsClassCBox.getSelectedIndex());
            Document templateNode = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(getClass().getResourceAsStream("/templates/" + templateFileURL));
            Node parentNode = node.getParentNode();
            Node newNode = xmlDoc.importNode(templateNode.getFirstChild(), true);
            parentNode.replaceChild(newNode, node);
        }

        // get connections Params Node from the path
        xpath = XPathFactory.newInstance().newXPath().compile(connectionsParamsNodePath);
        nodeList = (NodeList) xpath.evaluate(xmlDoc, XPathConstants.NODESET);
        node = nodeList.item(0);
        if (!connectionsParamsClass.equals(
                connectionsParamsClassCBox.getSelectedItem())) {
            String templateFileURL = connectionsParamsTemplatePaths.get(
                    connectionsParamsClassCBox.getSelectedIndex());
            Document templateNode = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(getClass().getResourceAsStream("/templates/" + templateFileURL));
            Node parentNode = node.getParentNode();
            Node newNode = xmlDoc.importNode(templateNode.getFirstChild(), true);
            parentNode.replaceChild(newNode, node);
        }

        // get layout Params Node from the path
        xpath = XPathFactory.newInstance().newXPath().compile(layoutParamsNodePath);
        nodeList = (NodeList) xpath.evaluate(xmlDoc, XPathConstants.NODESET);
        node = nodeList.item(0);
        if (!layoutParamsClass.equals(layoutParamsClassCBox.getSelectedItem())) {
            String templateFileURL = layoutParamsTemplatePaths.get(
                    layoutParamsClassCBox.getSelectedIndex());
            Document templateNode = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(getClass().getResourceAsStream("/templates/" + templateFileURL));
            Node parentNode = node.getParentNode();
            Node newNode = xmlDoc.importNode(templateNode.getFirstChild(), true);
            parentNode.replaceChild(newNode, node);
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
