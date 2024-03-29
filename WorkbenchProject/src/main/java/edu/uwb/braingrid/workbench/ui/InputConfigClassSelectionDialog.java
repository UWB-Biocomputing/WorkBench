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

import edu.uwb.braingrid.workbench.data.DynamicInputConfigurationManager;
import edu.uwb.braingrid.workbench.data.SystemConfig;

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
        verticesParamsClassLbl = new javax.swing.JLabel();
        edgesParamsClassLbl = new javax.swing.JLabel();
        connectionsParamsClassLbl = new javax.swing.JLabel();
        layoutParamsClassLbl = new javax.swing.JLabel();
        recorderParamsClassLbl = new javax.swing.JLabel();
        verticesParamsClassCBox = new javax.swing.JComboBox<>();
        connectionsParamsClassCBox = new javax.swing.JComboBox<>();
        edgesParamsClassCBox = new javax.swing.JComboBox<>();
        layoutParamsClassCBox = new javax.swing.JComboBox<>();
        recorderParamsClassCBox = new javax.swing.JComboBox<>();

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

        verticesParamsClassLbl.setText("VerticesParams Class:");

        edgesParamsClassLbl.setText("EdgesParams Class:");

        connectionsParamsClassLbl.setText("ConnectionsParams Class:");

        layoutParamsClassLbl.setText("LayoutParams Class:");

        recorderParamsClassLbl.setText("RecorderParams Class:");

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
                                .addComponent(edgesParamsClassLbl, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(verticesParamsClassLbl, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(recorderParamsClassLbl, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(verticesParamsClassCBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(connectionsParamsClassCBox,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(edgesParamsClassCBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(layoutParamsClassCBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(recorderParamsClassCBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 131, Short.MAX_VALUE)))
                        .addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                layout.createSequentialGroup().addContainerGap(17, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(verticesParamsClassLbl)
                                .addComponent(verticesParamsClassCBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(edgesParamsClassLbl)
                                .addComponent(edgesParamsClassCBox, javax.swing.GroupLayout.PREFERRED_SIZE,
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(recorderParamsClassLbl)
                                .addComponent(recorderParamsClassCBox, javax.swing.GroupLayout.PREFERRED_SIZE,
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
    private javax.swing.JComboBox<String> verticesParamsClassCBox;
    private javax.swing.JLabel verticesParamsClassLbl;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox<String> edgesParamsClassCBox;
    private javax.swing.JLabel edgesParamsClassLbl;

    private javax.swing.JComboBox<String> recorderParamsClassCBox;
    private javax.swing.JLabel recorderParamsClassLbl;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Custom Members">
    private static final Logger LOG
            = Logger.getLogger(InputConfigClassSelectionDialog.class.getName());
    private DynamicInputConfigurationManager icm;
    private Document baseTemplateInfoDoc = null;
    private Document xmlDoc = null;
    private boolean okClicked = false;
    private ArrayList<String> verticesParamsTemplatePaths = new ArrayList<>();
    private ArrayList<String> edgesParamsTemplatePaths = new ArrayList<>();
    private ArrayList<String> connectionsParamsTemplatePaths = new ArrayList<>();
    private ArrayList<String> layoutParamsTemplatePaths = new ArrayList<>();
    private ArrayList<String> recorderParamsTemplatePaths = new ArrayList<>();
    private String verticesParamsNodePath = null;
    private String edgesParamsNodePath = null;
    private String connectionsParamsNodePath = null;
    private String layoutParamsNodePath = null;
    private String recorderParamsNodePath = null;
    private String verticesParamsClass = null;
    private String edgesParamsClass = null;
    private String connectionsParamsClass = null;
    private String layoutParamsClass = null;
    private String recorderParamsClass = null;
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
                                case SystemConfig.VERTICES_PARAMS_CLASSES_TAG_NAME:
                                    verticesParamsClassCBox.addItem(className);
                                    verticesParamsTemplatePaths.add(templatePath);
                                    break;
                                case SystemConfig.EDGES_PARAMS_CLASSES_TAG_NAME:
                                    edgesParamsClassCBox.addItem(className);
                                    edgesParamsTemplatePaths.add(templatePath);
                                    break;
                                case SystemConfig.CONNECTIONS_PARAMS_CLASSES_TAG_NAME:
                                    connectionsParamsClassCBox.addItem(className);
                                    connectionsParamsTemplatePaths.add(templatePath);
                                    break;
                                case SystemConfig.LAYOUT_PARAMS_CLASSES_TAG_NAME:
                                    layoutParamsClassCBox.addItem(className);
                                    layoutParamsTemplatePaths.add(templatePath);
                                    break;
                                case SystemConfig.RECORDER_PARAMS_CLASSES_TAG_NAME:
                                    recorderParamsClassCBox.addItem(className);
                                    recorderParamsTemplatePaths.add(templatePath);
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
                case SystemConfig.VERTICES_PARAMS_CLASSES_TAG_NAME:
                    verticesParamsNodePath = paramsClassesNodePath;
                    break;
                case SystemConfig.EDGES_PARAMS_CLASSES_TAG_NAME:
                    edgesParamsNodePath = paramsClassesNodePath;
                    break;
                case SystemConfig.CONNECTIONS_PARAMS_CLASSES_TAG_NAME:
                    connectionsParamsNodePath = paramsClassesNodePath;
                    break;
                case SystemConfig.LAYOUT_PARAMS_CLASSES_TAG_NAME:
                    layoutParamsNodePath = paramsClassesNodePath;
                    break;
                case SystemConfig.RECORDER_PARAMS_CLASSES_TAG_NAME:
                    recorderParamsNodePath = paramsClassesNodePath;
                    break;
                default:
                    // unknown param class type
                }
            }
        }

        // set combo box options according to params classes from sim config XML file
        XPathExpression xpath = XPathFactory.newInstance().newXPath()
                .compile(verticesParamsNodePath);
        NodeList nodeList = (NodeList) xpath.evaluate(xmlDoc, XPathConstants.NODESET);
        Node node = nodeList.item(0);
        verticesParamsClass = ((Element) node).getAttribute(SystemConfig.CLASS_ATTRIBUTE_NAME);
        verticesParamsClassCBox.setSelectedItem(verticesParamsClass);

        xpath = XPathFactory.newInstance().newXPath().compile(edgesParamsNodePath);
        nodeList = (NodeList) xpath.evaluate(xmlDoc, XPathConstants.NODESET);
        node = nodeList.item(0);
        edgesParamsClass = ((Element) node).getAttribute(SystemConfig.CLASS_ATTRIBUTE_NAME);
        edgesParamsClassCBox.setSelectedItem(edgesParamsClass);

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

        xpath = XPathFactory.newInstance().newXPath().compile(recorderParamsNodePath);
        nodeList = (NodeList) xpath.evaluate(xmlDoc, XPathConstants.NODESET);
        node = nodeList.item(0);
        recorderParamsClass = ((Element) node).getAttribute(SystemConfig.CLASS_ATTRIBUTE_NAME);
        recorderParamsClassCBox.setSelectedItem(recorderParamsClass);
    }

    /**
     * Set up XML config file according to the selected items in combo boxes.
     */
    private void setConfigXMLDoc() throws Exception {
        LOG.info("Start setConfigXMLDoc");
        // get vertices Params Node from the path
        XPathExpression xpath = XPathFactory.newInstance().newXPath()
                .compile(verticesParamsNodePath);
        NodeList nodeList = (NodeList) xpath.evaluate(xmlDoc, XPathConstants.NODESET);
        Node node = nodeList.item(0);
        if (!verticesParamsClass.equals(verticesParamsClassCBox.getSelectedItem())) {
            String templateFileURL = verticesParamsTemplatePaths.get(
                    verticesParamsClassCBox.getSelectedIndex());
            Document templateNode = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(getClass().getResourceAsStream("/templates/" + templateFileURL));
            Node parentNode = node.getParentNode();
            Node newNode = xmlDoc.importNode(templateNode.getFirstChild(), true);
            parentNode.replaceChild(newNode, node);
        }

        // get edges Params Node from the path
        xpath = XPathFactory.newInstance().newXPath().compile(edgesParamsNodePath);
        nodeList = (NodeList) xpath.evaluate(xmlDoc, XPathConstants.NODESET);
        node = nodeList.item(0);
        if (!edgesParamsClass.equals(edgesParamsClassCBox.getSelectedItem())) {
            String templateFileURL = edgesParamsTemplatePaths.get(
                    edgesParamsClassCBox.getSelectedIndex());
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

        // get recorder Params Node from the path
        xpath = XPathFactory.newInstance().newXPath().compile(recorderParamsNodePath);
        nodeList = (NodeList) xpath.evaluate(xmlDoc, XPathConstants.NODESET);
        node = nodeList.item(0);
        if (!recorderParamsClass.equals(recorderParamsClassCBox.getSelectedItem())) {
            String templateFileURL = recorderParamsTemplatePaths.get(
                    recorderParamsClassCBox.getSelectedIndex());
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
