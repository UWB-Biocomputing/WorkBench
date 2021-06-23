package edu.uwb.braingrid.workbench.provvisualizer.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;

import edu.uwb.braingrid.workbench.provvisualizer.ProVis;
import edu.uwb.braingrid.workbench.provvisualizer.utility.ConnectionUtility;
import edu.uwb.braingrid.workbench.provvisualizer.utility.FileUtility;
import edu.uwb.braingrid.workbench.provvisualizer.utility.ProvUtility;
import edu.uwb.braingrid.workbench.provvisualizer.controller.AuthenticationController;
import edu.uwb.braingrid.workbench.provvisualizer.controller.TextComparisonController;
import edu.uwb.braingrid.workbench.provvisualizer.factory.EdgeFactory;
import edu.uwb.braingrid.workbench.provvisualizer.factory.NodeFactory;
import edu.uwb.braingrid.workbench.provvisualizer.model.*;
import edu.uwb.braingrid.workbench.provvisualizer.view.VisCanvas;
import edu.uwb.braingrid.workbenchdashboard.simstarter.SimStartWiz;

/**
 * Class contains the functionality of ProVis nodes, toggles, buttons, and updates textfields in
 * control panel.
 *
 * @author Tom Wong, Joseph Conquest, and unidentified contributor
 * @version 1.2.2
 */
public class ProVisCtrl {

    private Graph dataProvGraph;
    private LinkedHashMap<String, AuthenticationInfo> authInfoCache = new LinkedHashMap<>(5, (float) 0.75, true);
    private ProVis proVis_;
    private Model provModel;
    private AnimationTimer timer;
    private double zoomRatio = 1;
    private Node draggedNode;
    private Node selectedNode;
    private double zoomSpeed = .2;

    private double[] pressedXY;
    private double[] displayWindowLocation = new double[] { 0, 0 };
    private double[] displayWindowSize = new double[] { 10000, 10000 };
    private double[] displayWindowLocationTmp;

    private AuthenticationInfo authenticationInfo = null;

    private VisCanvas visCanvas;
    private BorderPane canvasPane;
    private Slider adjustForceSlider;
    private ToggleSwitch stopForces;
    private ToggleSwitch showNodeIds;
    private ToggleSwitch showRelationships;
    private ToggleSwitch showLegend;
    private ToggleSwitch builderModeToggle;
    private Button chooseFileBtn;
    private Button importFileBtn;
    private Button buildFromPrevButton;
    private Button clearPresetsButton;
    private TextField inputTextField;
    private TextField probedTextField;
    private TextField activeTextField;
    private TextField inhibitoryTextField;
    private TextField bGVersionTextField;

    private SimStartWiz simStartWiz;
    private boolean buildModeON = false;
    private String bGVersionSelected;
    String simSpecifications = null;
    HashMap<Character, String> nListPresets = new HashMap<>();

    private static final Logger LOG = Logger.getLogger(ProVisCtrl.class.getName());

    public ProVisCtrl(ProVis proVis, VisCanvas visCanvas, BorderPane canvasPane, Slider adjustForceSlider,
            ToggleSwitch stopForces, ToggleSwitch showNodeIds, ToggleSwitch showRelationships, ToggleSwitch showLegend,
            ToggleSwitch builderModetggl, Button importFileBtn, Button chooseFileBtn, TextField inputTextField,
            TextField probedTextField, TextField activeTextField, TextField inhibitoryTextField,
            TextField bGVersionTextField, Button clearPresetsButton, Button buildFromPrevButton) {
        this.proVis_ = proVis;
        this.visCanvas = visCanvas;
        this.canvasPane = canvasPane;
        this.adjustForceSlider = adjustForceSlider;
        this.stopForces = stopForces;
        this.showNodeIds = showNodeIds;
        this.showRelationships = showRelationships;
        this.showLegend = showLegend;
        this.builderModeToggle = builderModetggl;
        this.importFileBtn = importFileBtn;
        this.chooseFileBtn = chooseFileBtn;
        this.clearPresetsButton = clearPresetsButton;
        this.buildFromPrevButton = buildFromPrevButton;
        this.inputTextField = inputTextField;
        this.probedTextField = probedTextField;
        this.activeTextField = activeTextField;
        this.inhibitoryTextField = inhibitoryTextField;
        this.bGVersionTextField = bGVersionTextField;
        initialize();
    }

    /**
     * Initializes the controller.
     */
    @FXML
    public void initialize() {
        dataProvGraph = new Graph();
        dataProvGraph.setC3(adjustForceSlider.getValue() * 1500);

        // Bind canvas size to stack pane size.
        visCanvas.widthProperty().bind(canvasPane.widthProperty());
        visCanvas.heightProperty().bind(canvasPane.heightProperty());

        initMouseEvents();
        initGUIEvents();
        enableBuildButton(false);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!stopForces.isSelected()) {
                    dataProvGraph.moveNodes(draggedNode);
                }
                dataProvGraph.drawOnCanvas(visCanvas, displayWindowLocation, displayWindowSize, zoomRatio);
            }
        };
        
        timer.start();
    }

    private void initGUIEvents() {
        adjustForceSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                dataProvGraph.setC3(new_val.doubleValue() * 1500);
            }
        });

        showNodeIds.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                if (new_val) {
                    dataProvGraph.setShowAllNodeIds(true);
                } else {
                    dataProvGraph.setShowAllNodeIds(false);
                }
            }
        });

        showRelationships.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                if (new_val) {
                    dataProvGraph.setShowAllRelationships(true);
                } else {
                    dataProvGraph.setShowAllRelationships(false);
                }
            }
        });

        showLegend.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                if (new_val) {
                    dataProvGraph.setShowLegend(true);
                } else {
                    dataProvGraph.setShowLegend(false);
                }
            }
        });
        
        builderModeToggle.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                if (new_val) {
                    buildModeON = true;
                } else if (old_val) {
                    buildModeON = false;
                    enableBuildButton(false);
                    clearBuildControlDisplay();
                }
            }
        });

        chooseFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select provenance file");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Turtle Files", "*.ttl"));
                fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

                File selectedFile = null;
                selectedFile = fileChooser.showOpenDialog(canvasPane.getScene().getWindow());

                if (selectedFile != null) {
                    dataProvGraph.clearAllIdsRelationships();
                    dataProvGraph.clearNodesNEdges();
                    showNodeIds.setSelected(false);
                    showRelationships.setSelected(false);
                    initNodeEdge(selectedFile.getAbsolutePath());
                    proVis_.setTitle(selectedFile.getName());
                }
            }
        });

        importFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dataProvGraph.clearAllIdsRelationships();
                dataProvGraph.clearNodesNEdges();
                showNodeIds.setSelected(false);
                showRelationships.setSelected(false);
                openUniversalProvenance();
            }
        });

        clearPresetsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clearBuildControlDisplay();
                bGVersionSelected = null;
                simSpecifications = null;
                nListPresets.clear();
            }
        });

        buildFromPrevButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                simStartWiz = new SimStartWiz(simSpecifications, bGVersionSelected, nListPresets);
            }
        });
    }

    private void initMouseEvents() {
        visCanvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown()) {
                    if (draggedNode != null) { // drag node
                        draggedNode.setX(event.getX() / zoomRatio + displayWindowLocation[0]);
                        draggedNode.setY(event.getY() / zoomRatio + displayWindowLocation[1]);

                        Node comparingNode = dataProvGraph.getComparingNode(
                                event.getX() / zoomRatio + displayWindowLocation[0],
                                event.getY() / zoomRatio + displayWindowLocation[1], draggedNode, zoomRatio, true);
                        if (draggedNode instanceof EntityNode && comparingNode != null
                                && comparingNode instanceof EntityNode) {
                            dataProvGraph.setComparingNode(comparingNode);
                        } else {
                            dataProvGraph.setComparingNode(null);
                        }
                    } else {
                        displayWindowLocation[0] = displayWindowLocationTmp[0] + pressedXY[0]
                                - event.getX() / zoomRatio;
                        displayWindowLocation[1] = displayWindowLocationTmp[1] + pressedXY[1]
                                - event.getY() / zoomRatio;
                    }
                }
            }
        });

        visCanvas.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown()) {
                    draggedNode = dataProvGraph.getSelectedNode(event.getX() / zoomRatio + displayWindowLocation[0],
                            event.getY() / zoomRatio + displayWindowLocation[1], zoomRatio, false);
                    pressedXY = new double[] {event.getX() / zoomRatio, event.getY() / zoomRatio};

                    if (draggedNode == null) {
                        displayWindowLocationTmp = displayWindowLocation.clone();
                    }
                }
            }
        });

        visCanvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    if (event.getClickCount() == 1) {
                        Edge edge = dataProvGraph.getSelectedEdge(event.getX() / zoomRatio + displayWindowLocation[0],
                                event.getY() / zoomRatio + displayWindowLocation[1], zoomRatio);

                        if (edge != null) {
                            dataProvGraph.addOrRemoveDispRelationship(edge);
                        }

                        if (buildModeON) {
                            selectedNode = dataProvGraph.getSelectedNode(event.getX() / zoomRatio + displayWindowLocation[0],
                            event.getY() / zoomRatio + displayWindowLocation[1], zoomRatio, false);
                            if (selectedNode != null) {
                                prepBuildInputParams(selectedNode);
                            }
                        }
                    } else if (event.getClickCount() == 2) {
                        dataProvGraph.clearAllIdsRelationships();
                    }
                }
            }
        });

        visCanvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Node node = dataProvGraph.getSelectedNode(event.getX() / zoomRatio + displayWindowLocation[0],
                        event.getY() / zoomRatio + displayWindowLocation[1], zoomRatio, false);

                dataProvGraph.setMouseOnNode(node);

                Edge edge = dataProvGraph.getSelectedEdge(event.getX() / zoomRatio + displayWindowLocation[0],
                        event.getY() / zoomRatio + displayWindowLocation[1], zoomRatio);

                dataProvGraph.setMouseOnEdge(edge);
            }
        });

        visCanvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (draggedNode != null && pressedXY[0] == event.getX() / zoomRatio
                        && pressedXY[1] == event.getY() / zoomRatio) {
                    dataProvGraph.addOrRemoveDispNodeId(draggedNode);

                    if (draggedNode instanceof ActivityNode) {
                        dataProvGraph.addOrRemoveSelectedActivityNode((ActivityNode) draggedNode);
                    }
                }

                Node comparingNode = dataProvGraph.getComparingNode();
                if (comparingNode != null) {
                    boolean comparingNodeFileReady = false;
                    boolean draggedNodeFileReady = false;

                    // check if the files exist in local file system
                    // download the files if they are not in the file system.
                    comparingNodeFileReady = checkIfNodeFileExists(comparingNode);
                    if (!comparingNodeFileReady) {
                        comparingNodeFileReady = downloadNodeFile(comparingNode);
                    }

                    draggedNodeFileReady = checkIfNodeFileExists(draggedNode);
                    if (!draggedNodeFileReady) {
                        draggedNodeFileReady = downloadNodeFile(draggedNode);
                    }

                    if (comparingNodeFileReady && draggedNodeFileReady) {
                        // start comparing files
                        compareNodes(draggedNode, comparingNode);
                    }
                }

                dataProvGraph.setComparingNode(null);
                draggedNode = null;
            }
        });

        visCanvas.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                // update zoomRatio
                double deltaY = event.getDeltaY();
                double oldZoomRatio = zoomRatio;

                if (deltaY > 0) {
                    zoomRatio = zoomRatio * (1 + zoomSpeed);
                } else if (deltaY < 0) {
                    zoomRatio = zoomRatio / (1 + zoomSpeed);
                }

                if (deltaY != 0) {
                    displayWindowSize[0] = visCanvas.getWidth() / zoomRatio;
                    displayWindowSize[1] = visCanvas.getHeight() / zoomRatio;
                    displayWindowLocation[0] = ((zoomRatio - oldZoomRatio) / (zoomRatio * oldZoomRatio)) * event.getX()
                            + displayWindowLocation[0];
                    displayWindowLocation[1] = ((zoomRatio - oldZoomRatio) / (zoomRatio * oldZoomRatio)) * event.getY()
                            + displayWindowLocation[1];
                }
            }
        });
    }

    private void compareNodes(Node node1, Node node2) {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/provvisualizer/view/TextComparisonView.fxml"));
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextComparisonController controller = loader.getController();
        controller.getFileInfoLblLeft().setText(node1.getDisplayId());
        controller.getFileInfoLblRight().setText(node2.getDisplayId());
        controller.loadFiles(FileUtility.getNodeFileLocalAbsolutePath(node1),
                FileUtility.getNodeFileLocalAbsolutePath(node2));

        Stage modal_dialog = new Stage(StageStyle.DECORATED);
        modal_dialog.initModality(Modality.WINDOW_MODAL);
        modal_dialog.initOwner(canvasPane.getScene().getWindow());
        Scene scene = new Scene(parent);
        modal_dialog.setScene(scene);
        modal_dialog.setTitle("Comparing " + node1.getDisplayId() + " and " + node2.getDisplayId());
        modal_dialog.setMaximized(true);
        modal_dialog.showAndWait();
    }

    private boolean checkIfNodeFileExists(Node node) {
        File nodeFile = new File(FileUtility.getNodeFileLocalAbsolutePath(node));
        return nodeFile.exists();
    }

    private boolean downloadNodeFile(Node node) {
        String protocol = null;
        String username = null;
        String hostname = null;
        String nodeFileRemoteFullPath = node.getId();
        String[] splitStrs = null;
        String nodeFileLclPath = FileUtility.getNodeFileLocalAbsolutePath(node);
        String nodeFileRemoteRelPath = FileUtility.getNodeFileRemoteRelativePath(node);
        boolean downloadSuccess = false;
        authenticationInfo = null;

        if (nodeFileRemoteFullPath.contains("://")) {
            splitStrs = nodeFileRemoteFullPath.split("://");
            protocol = splitStrs[0];
        }

        // currently only support download via sftp
        if (protocol == null || protocol != null && !protocol.equals("sftp"))
            return false;

        if (splitStrs[1].contains("@")) {
            splitStrs = splitStrs[1].split("@");
            username = splitStrs[0];
        }

        if (splitStrs[1].contains("/")) {
            hostname = splitStrs[1].split("/")[0];
        }
        String cacheKey = username + "@" + hostname;
        if (authInfoCache.containsKey(cacheKey)) {
            authenticationInfo = authInfoCache.get(cacheKey);
        }

        if (authenticationInfo != null) {
            do {
                downloadSuccess = ConnectionUtility.downloadFileViaSftp(nodeFileRemoteRelPath, nodeFileLclPath,
                        authenticationInfo);
            } while (!downloadSuccess && requestAuthenticationInfo(hostname, username));
        } else {
            while (!downloadSuccess && requestAuthenticationInfo(hostname, username)) {
                downloadSuccess = ConnectionUtility.downloadFileViaSftp(nodeFileRemoteRelPath, nodeFileLclPath,
                        authenticationInfo);
            }
        }

        if (downloadSuccess) {
            // save authentication info
            authInfoCache.put(authenticationInfo.getUsername() + "@" + authenticationInfo.getHostname(),
                    authenticationInfo);
        }

        return downloadSuccess;
    }

    private boolean requestAuthenticationInfo(String hostname, String username) {
        Parent parent = null;
        authenticationInfo = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/provvisualizer/view/AuthenticationView.fxml"));
            parent = loader.load();
            AuthenticationController controller = loader.getController();
            controller.setHostname(hostname);
            controller.setUsername(username);
            controller.setOkBtnCallback(authInfo -> authenticationInfo = authInfo);

            Stage modal_dialog = new Stage(StageStyle.DECORATED);
            modal_dialog.initModality(Modality.WINDOW_MODAL);
            modal_dialog.initOwner(canvasPane.getScene().getWindow());
            Scene scene = new Scene(parent);
            modal_dialog.setScene(scene);
            modal_dialog.setTitle("Login");
            modal_dialog.showAndWait();

            if (authenticationInfo != null) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void initNodeEdge(String provFileURI) {
        LOG.info("Init Node Edge");
        provModel = RDFDataMgr.loadModel(provFileURI);
        StmtIterator iter = provModel.listStatements();
        NodeFactory nodeFactory = NodeFactory.getInstance();
        EdgeFactory edgeFactory = EdgeFactory.getInstance();
        Statement stmt;

        while (iter.hasNext()) {
            stmt = iter.nextStatement();
            if (stmt.getSubject().toString().contains("local:")) {
                continue;
            }
            String predicateStr = stmt.getPredicate().toString();
            if (predicateStr.equals(ProvUtility.RDF_TYPE)) {
                String subjectStr = stmt.getSubject().toString();
                String objectStr = stmt.getObject().toString();
                if (objectStr.equals(ProvUtility.PROV_ACTIVITY)) {
                    if (dataProvGraph.isNodeAdded(subjectStr)) {
                        Node node = dataProvGraph.getNode(subjectStr);
                        if (!(node instanceof ActivityNode)) {
                            node = nodeFactory.convertToActivityNode(node);
                        }

                        dataProvGraph.addNode(node);
                    } else { // create Activity Node
                        Node activityNode = nodeFactory.createActivityNode();
                        activityNode.setId(subjectStr).setX(Math.random() * visCanvas.getWidth())
                                .setY(Math.random() * visCanvas.getHeight());
                        dataProvGraph.addNode(activityNode);
                    }
                } else if (objectStr.equals(ProvUtility.PROV_SW_AGENT)) {
                    if (dataProvGraph.isNodeAdded(subjectStr)) {
                        dataProvGraph.addNode(nodeFactory.convertToAgentNode(dataProvGraph.getNode(subjectStr)));
                    } else { // create Agent Node
                        Node agentNode = nodeFactory.createAgentNode();
                        agentNode.setId(subjectStr).setX(Math.random() * visCanvas.getWidth())
                                .setY(Math.random() * visCanvas.getHeight());
                        dataProvGraph.addNode(agentNode);
                    }
                } else if (objectStr.equals(ProvUtility.PROV_ENTITY)) {
                    if (dataProvGraph.isNodeAdded(subjectStr)) {
                        if (dataProvGraph.getNode(subjectStr).getLabel().equals("commit")) { // convert to commit node
                            dataProvGraph.addNode(nodeFactory.convertToCommitNode(dataProvGraph.getNode(subjectStr)));
                        } else {
                            dataProvGraph.addNode(nodeFactory.convertToEntityNode(dataProvGraph.getNode(subjectStr)));
                        }
                    } else { // create Entity Node
                        Node entityNode = nodeFactory.createEntityNode();
                        entityNode.setId(subjectStr).setX(Math.random() * visCanvas.getWidth())
                                .setY(Math.random() * visCanvas.getHeight());
                        dataProvGraph.addNode(entityNode);
                    }
                }
            } else if (predicateStr.equals(ProvUtility.RDF_LABEL)) {
                String subjectStr = stmt.getSubject().toString();
                String objectStr = stmt.getObject().toString();

                if (dataProvGraph.isNodeAdded(subjectStr)) {
                    dataProvGraph.getNode(subjectStr).setLabel(objectStr);

                    if (objectStr.equals("commit")) { // convert to commit node
                        dataProvGraph.addNode(nodeFactory.convertToCommitNode(dataProvGraph.getNode(subjectStr)));
                    }
                } else { // create a Default Node to store the label value.
                    Node node = null;
                    if (objectStr.equals("commit")) {
                        node = nodeFactory.createCommitNode();
                    } else {
                        node = nodeFactory.createDefaultNode();
                    }

                    node.setId(subjectStr).setX(Math.random() * visCanvas.getWidth())
                            .setY(Math.random() * visCanvas.getHeight()).setLabel(objectStr);
                    dataProvGraph.addNode(node);
                }
            } else if (predicateStr.equals(ProvUtility.PROV_STARTED_AT_TIME)) {
                String subjectStr = stmt.getSubject().toString();
                String objectStr = stmt.getObject().toString();
                String dateTime = objectStr.substring(0, objectStr.indexOf("^^"));

                if (dataProvGraph.isNodeAdded(subjectStr)) {
                    Node node = dataProvGraph.getNode(subjectStr);
                    if (!(node instanceof ActivityNode)) {
                        node = nodeFactory.convertToActivityNode(node);
                    }
                    ((ActivityNode) node).setStartTime(dateTime);

                    dataProvGraph.addNode(node);
                } else {
                    // create Activity Node
                    ActivityNode activityNode = nodeFactory.createActivityNode();
                    activityNode.setStartTime(dateTime).setId(subjectStr).setX(Math.random() * visCanvas.getWidth())
                            .setY(Math.random() * visCanvas.getHeight());
                    dataProvGraph.addNode(activityNode);
                }
            } else if (predicateStr.equals(ProvUtility.PROV_ENDED_AT_TIME)) {
                String subjectStr = stmt.getSubject().toString();
                String objectStr = stmt.getObject().toString();
                String dateTime = objectStr.substring(0, objectStr.indexOf("^^"));

                if (dataProvGraph.isNodeAdded(subjectStr)) {
                    Node node = dataProvGraph.getNode(subjectStr);
                    if (!(node instanceof ActivityNode)) {
                        node = nodeFactory.convertToActivityNode(node);
                    }
                    ((ActivityNode) node).setEndTime(dateTime);

                    dataProvGraph.addNode(node);
                } else {
                    // create Activity Node
                    ActivityNode activityNode = nodeFactory.createActivityNode();
                    activityNode.setEndTime(dateTime).setId(subjectStr).setX(Math.random() * visCanvas.getWidth())
                            .setY(Math.random() * visCanvas.getHeight());
                    dataProvGraph.addNode(activityNode);
                }
            } else if (!predicateStr.equals(ProvUtility.PROV_AT_LOCATION) && stmt.getObject().isURIResource()) {
                // Skip "wasGeneratedBY" edge to avoid duplicate relationship display temporary, will find out a better way to display two or more relationship later
                if (stmt.getPredicate().toString().equals(ProvUtility.PROV_WAS_GENERATED_BY)) {
                    continue;
                }
                // create a Default Node to store the label value.
                Edge defaultEdge = edgeFactory.createDefaultEdge();
                defaultEdge.setFromNodeId(stmt.getSubject().toString()).setToNodeId(stmt.getObject().toString())
                        .setRelationship(stmt.getPredicate().toString());
                dataProvGraph.addEdge(defaultEdge);
            }
            // System.out.println(stmt.getSubject().toString() + " " + stmt.getPredicate().toString() + " " + stmt.getObject().toString());
        }

        dataProvGraph.generateCommitRelationships(visCanvas.getWidth(), visCanvas.getHeight());
        dataProvGraph.setNeighbors();
    }

    /**
     * Enables or disables Build from Previous buttons. Enable occurs when builder mode is toggled
     * on, and Disable occurs when builder mode is toggled off by user.
     *
     * @param enable  true if builder mode is toggled on, false otherwise
     */
    private void enableBuildButton(boolean enable) {
        buildFromPrevButton.setDisable(!enable);
        clearPresetsButton.setDisable(!enable);
    }

    /**
     * Populates textfields in builder control given node input selected by user. Sets up necessary
     * information for builder action to occur.
     */
    private void prepBuildInputParams(Node selectedNode) {
        if (selectedNode instanceof ActivityNode) {
            ArrayList<Node> neighbors = selectedNode.getNeighborNodes();
            for(Node neighbor : neighbors) {
                prepBuildInputParams(neighbor);
            }
        } else if (selectedNode instanceof AgentNode) {
            ArrayList<Node> neighbors = selectedNode.getNeighborNodes();
            for(Node neighbor : neighbors) {
                if(neighbor instanceof CommitNode) {
                    prepBuildInputParams(neighbor);
                    return;
                }
            }
        } else if (selectedNode instanceof EntityNode) {
            parseEntityNodeFile(selectedNode);
        } else if (selectedNode instanceof CommitNode) {
            bGVersionTextField.appendText(selectedNode.getDisplayId());
            bGVersionSelected = selectedNode.getDisplayId();
            buildFromPrevButton.setDisable(false);
        } else {
            //System.out.println("No Node type detected");
            return;
        }
        clearPresetsButton.setDisable(false);
    }

    /**
     * Clears the textfields for all selected node information for builder input.
     */
    private void clearBuildControlDisplay() {
        inputTextField.clear();
        probedTextField.clear();
        activeTextField.clear();
        inhibitoryTextField.clear();
        bGVersionTextField.clear();
    }

    /**
     * Check if the files exist in local file system.
     * Download the files if they are not in the file system.
     * Call loadFile if success in opening.
     *
     * @return True if successful loadFile completion, otherwise false
     */
    private boolean parseEntityNodeFile(Node selectedNode) {
        boolean selectedNodeFileReady = false;
        char typeOfNode = 'N';
        if (selectedNode == null) return false;
        
        selectedNodeFileReady = checkIfNodeFileExists(selectedNode);
        if (!selectedNodeFileReady) {
            selectedNodeFileReady = downloadNodeFile(selectedNode);
        }
        if (selectedNodeFileReady) {
            typeOfNode = loadFile(FileUtility.getNodeFileLocalAbsolutePath(selectedNode));
        }

        if (typeOfNode == 'N') {
            return false;
        } else if (typeOfNode == 'S') {
            inputTextField.clear();
            inputTextField.appendText(selectedNode.getDisplayId());
            simSpecifications = FileUtility.getNodeFileLocalAbsolutePath(selectedNode);
        } else if(typeOfNode == 'I') {
            inhibitoryTextField.clear();
            inhibitoryTextField.appendText(selectedNode.getDisplayId());
            setNLEditforBuild(typeOfNode, selectedNode);
        } else if(typeOfNode == 'A') {
            activeTextField.clear();
            activeTextField.appendText(selectedNode.getDisplayId());
            setNLEditforBuild(typeOfNode, selectedNode);
        } else if(typeOfNode == 'P') {
            probedTextField.clear();
            probedTextField.appendText(selectedNode.getDisplayId());
            setNLEditforBuild(typeOfNode, selectedNode);
        }
        buildFromPrevButton.setDisable(false);
        return true;
    }

    /**
     * Returns char N for failure, A = Active, I = inhibitory, P = probed, S = Sim Input.
     */
    private char loadFile(String filePath) {
        Scanner in = null;
        try {
            in= new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 'N';
        }

        in.useDelimiter("\\A");
        in.nextLine();
        String inputFromSelected = in.nextLine();
        String determineType = inputFromSelected.substring(0, 3);
        if (determineType.equals("<A>")) {
            return 'A';
        } else if(determineType.equals("<I>")) {
            return 'I';
        } else if(determineType.equals("<P>")) {
            return 'P';
        } else if(determineType.equals("<!-")) {
            //Do nothing, this is a ouput file
        } else { // simulation input file
            return 'S';
        }
        return 'N';
    }

    /**
     * Adds NList inputs selected by user to simStartWiz called by buildFromPrevButton.
     */
    private void setNLEditforBuild(char inputType, Node inputNode) {
        nListPresets.put(inputType, FileUtility.getNodeFileLocalAbsolutePath(inputNode));
    }

    /**
     * Open universalProvenance.ttl in projects dir if exists.
     * Display universalProvenance on ProVis.
     */
    public void openUniversalProvenance() {
        File universalProvenance = null;
        universalProvenance = new File(System.getProperty("user.dir") + File.separator + "projects" + File.separator
                + "UniversalProvenance.ttl");
        if (universalProvenance != null) {
            dataProvGraph.clearNodesNEdges();
            initNodeEdge(universalProvenance.getAbsolutePath());
            proVis_.setTitle(universalProvenance.getName());
        }
    }
}
