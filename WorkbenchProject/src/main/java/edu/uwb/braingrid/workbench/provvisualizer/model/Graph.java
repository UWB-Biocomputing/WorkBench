package edu.uwb.braingrid.workbench.provvisualizer.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;

import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbench.provvisualizer.utility.GraphUtility;
import edu.uwb.braingrid.workbench.provvisualizer.utility.ProvUtility;
import edu.uwb.braingrid.workbenchdashboard.utils.RepoManager;
import edu.uwb.braingrid.workbench.provvisualizer.factory.EdgeFactory;
import edu.uwb.braingrid.workbench.provvisualizer.factory.NodeFactory;

/**
 * Main Logic for the ProVis graph, containing nodes and edges of graph.
 *
 * @author Tom Wong, Extended by Joseph Conquest
 * @version 1.2
 */
public class Graph {

    /** Font size for ProVis graph labels. */
    public static final double LABEL_FONT_SIZE = 20;

    private Git git = null;

    private HashMap<String, Node> nodes = new HashMap<>();
    private HashMap<String, Edge> edges = new HashMap<>();
    private boolean showAllNodeIds = false;
    private boolean showAllRelationships = false;
    private boolean showLegend = false;
    private HashSet<Node> dispNodeIds = new HashSet<>();
    private HashSet<Edge> dispRelationships = new HashSet<>();
    private HashSet<ActivityNode> selectedActivityNodes = new HashSet<>();
    private HashSet<Node> commitNodesList = new HashSet<>();

    private Node comparingNode;
    private Node mouseOnNode;
    private Edge mouseOnEdge;

    private double c1 = 2;  //default value = 2;
    private double c2 = 1;  //default value = 1;
    private double c3 = 1;  //default value = 1;
    private double c4 = 1;  //default value = 0.1;

    private double edgeArrowAngle = (3.4 / 4) * Math.PI;
    private double edgeArrowSize = 8;

    public Graph() {
        // default constructor
    }

    public double getC3() {
        return c3;
    }

    public void setC3(double c3) {
        this.c3 = c3;
    }

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        if (node instanceof CommitNode) {
            commitNodesList.add(node);
        }
    }

    public void addNodes(Node... moreNodes) {
        for (Node node : moreNodes) {
            nodes.put(node.getId(), node);
        }
    }

    public boolean isNodeAdded(String nodeId) {
        return this.nodes.containsKey(nodeId);
    }

    public void addOrRemoveSelectedActivityNode(ActivityNode node) {
        clearAllIdsRelationships();
        if (selectedActivityNodes.contains(node)) {
            selectedActivityNodes.remove(node);
        } else {
            selectedActivityNodes.add(node);
        }
    }

    public HashSet<ActivityNode> getSelectedActivityNodes() {
        return selectedActivityNodes;
    }

    public Node getNode(String nodeId) {
        return nodes.getOrDefault(nodeId, null);
    }

    public void addEdge(Edge edge) {
        edges.put(edge.getEdgeId(), edge);
    }

    public void addEdges(Edge... moreEdges) {
        for (Edge edge : moreEdges) {
            edges.put(edge.getEdgeId(), edge);
        }
    }

    public Edge getEdge(String edgeId) {
        return this.edges.get(edgeId);
    }

    public boolean isEdgeAdded(String edgeId) {
        return this.edges.containsKey(edgeId);
    }

    public void clearNodesNEdges() {
        nodes.clear();
        edges.clear();
    }

    public void generateCommitRelationships(double canvasWidth, double canvasHeight) {
        Path bgReposPath = FileManager.getGraphittiRepoDirectory();
        if (!Files.exists(bgReposPath)) {
            try {
                System.out.println("Repo manager: " + RepoManager.getMasterBranchDirectory());
                git = Git.cloneRepository()
                        .setURI("https://github.com/UWB-Biocomputing/BrainGrid.git")
                        .setDirectory(bgReposPath.toFile())
                        .call();
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        } else {
            try {
                git = Git.open(bgReposPath.toFile());
                git.pull().call();
            } catch (IOException | GitAPIException e) {
                e.printStackTrace();
            }
        }

        RevWalk walker = new RevWalk(git.getRepository());
        List<RevCommit> commits = new ArrayList<>();
        try {
            for (Node node:commitNodesList) {
                commits.add(walker.parseCommit(ObjectId.fromString(node.getDisplayId())));
            }
            walker.markStart(commits);
            walker.setRevFilter(RevFilter.MERGE_BASE);
            walker.sort(RevSort.COMMIT_TIME_DESC);

            for (RevCommit revCommit:walker) {
                if (!commits.contains(revCommit)) {
                    commits.add(revCommit);
                    Node node = NodeFactory.getInstance()
                            .createCommitNode()
                            .setId(ProvUtility.getCommitUri(revCommit.getId().getName()))
                            .setX(Math.random() * canvasWidth)
                            .setY(Math.random() * canvasHeight)
                            .setLabel(ProvUtility.LABEL_COMMIT);
                    nodes.put(node.getId(), node);
                }
            }

            commits.sort((commit1, commit2) -> commit2.getCommitTime() - commit1.getCommitTime());
            LinkedList<RevCommit> branches = new LinkedList<>();
            for (RevCommit commit : commits) {
                List<RevCommit> removalList = new ArrayList<>();
                for (RevCommit branchCommit : branches) {
                    if (walker.isMergedInto(commit, branchCommit)) {
                        Node commitNode1 = nodes.get(
                                ProvUtility.getCommitUri(branchCommit.getId().name()));
                        Node commitNode2 = nodes.get(
                                ProvUtility.getCommitUri(commit.getId().name()));
                        EdgeFactory edgeFactory = EdgeFactory.getInstance();
                        Edge edge = null;
                        if (Arrays.binarySearch(branchCommit.getParents(), commit) >= 0) {
                            edge = edgeFactory.createDefaultEdge()
                                    .setFromNodeId(commitNode1.getId())
                                    .setToNodeId(commitNode2.getId())
                                    .setRelationship(ProvUtility.PROV_WAS_DERIVED_FROM);
                        } else {
                            edge = edgeFactory.createDashEdge()
                                    .setFromNodeId(commitNode1.getId())
                                    .setToNodeId(commitNode2.getId())
                                    .setRelationship(ProvUtility.PROV_WAS_DERIVED_FROM);
                        }

                        edges.put(commitNode1.getId() + ProvUtility.PROV_WAS_DERIVED_FROM
                                + commitNode2.getId(), edge);

                        removalList.add(branchCommit);
                    }
                }
                branches.removeAll(removalList);
                branches.addLast(commit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            git.close();
        }
    }

    /**
     * Using Force-directed graph layout algorithm to optimize the node positions.
     *
     * @param draggedNode
     */
    public void moveNodes(Node draggedNode) {
        for (Node node1: nodes.values()) {
            // loop over all node pairs and calculate the net force
            double[] netForce = new double[]{0, 0};
            for (Node node2: nodes.values()) {
                if (!node1.equals(node2)) {
                    if (node1.isConnected(node2)) {
                        double[] repellingForce = repellingForce(node1, node2);
                        double[] attractiveForce = attractiveForce(node1, node2);
                        // if connected
                        netForce[0] = netForce[0] + repellingForce[0] + attractiveForce[0];
                        netForce[1] = netForce[1] + repellingForce[1] + attractiveForce[1];
                    } else {
                        // if not connected
                        double[] repellingForce = repellingForce(node1, node2);
                        netForce[0] = netForce[0] + repellingForce[0];
                        netForce[1] = netForce[1] + repellingForce[1];
                    }
                }
            }
            //apply the force to the node
            node1.setX(node1.getX() + c4 * netForce[0]);
            node1.setY(node1.getY() + c4 * netForce[1]);
        }
    }

    /**
     * Computes the vector of the attractive force between two node.
     *
     * @param from ID of the first node
     * @param to ID of the second node
     * @return force vector
     */
    public double[] attractiveForce(Node from, Node to) {
        double[] vec;
        double distance = getDistance(from, to);
        vec = computeNormalizedVector(from, to);  //*distance;
        double factor = c1 * Math.log(distance / c2);
        vec[0] = vec[0] * factor;
        vec[1] = vec[1] * factor;
        return vec;
    }

    /**
     * Computes the vector of the repelling force between two node.
     *
     * @param from ID of the first node
     * @param to ID of the second node
     * @return force vector
     */
    public double[] repellingForce(Node from, Node to) {
        double[] vec;
        double distance = getDistance(from, to);
        vec = computeNormalizedVector(from, to);  //*distance;
        double factor = -c3 / Math.pow(distance, 2);
        vec[0] = vec[0] * factor;
        vec[1] = vec[1] * factor;
        return vec;
    }

    /**
     * Computes the connecting vector between node1 and node2.
     *
     * @param node1  ID of the first node
     * @param node2  ID of the second node
     * @return The connecting vector between node1 and node2
     */
    public double[] computeNormalizedVector(Node node1, Node node2) {
        double vectorX = node2.getX() - node1.getX();
        double vectorY = node2.getY() - node1.getY();
        double length = Math.sqrt(Math.pow(Math.abs(vectorX), 2) + Math.pow(Math.abs(vectorY), 2));
        return new double[]{vectorX / length, vectorY / length};
    }

    /**
     * Computes the euclidean distance between two given nodes.
     *
     * @param node1 first node
     * @param node2 second node
     * @return euclidean distance between the nodes
     */
    public double getDistance(Node node1, Node node2) {
        return Math.sqrt(Math.pow(Math.abs(node1.getX() - node2.getX()), 2)
                + Math.pow(Math.abs(node1.getY() - node2.getY()), 2));
    }

    public void drawOnCanvas(Canvas canvas, double[] displayWindowLocation,
            double[] displayWindowSize, double zoomRatio) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        //draw background
        gc.setFill(Color.BEIGE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.BLACK);
        gc.setFont(Font.font(LABEL_FONT_SIZE));

        //draw edges first
        for (Edge edge : edges.values()) {
            Node fromNode = nodes.get(edge.getFromNodeId());
            Node toNode = nodes.get(edge.getToNodeId());
            if (fromNode != null && toNode != null) {
                if (fromNode.isInDisplayWindow(displayWindowLocation, displayWindowSize)
                        || toNode.isInDisplayWindow(displayWindowLocation, displayWindowSize)) {
                    drawEdge(gc, fromNode, toNode, edge.isDashLine(), displayWindowLocation,
                            zoomRatio, edge == mouseOnEdge);
                }
                //Initialize edge to node relationship
            }
        }

        //draw nodes/vertices
        for (Node node : nodes.values()) {
            if (node.isInDisplayWindow(displayWindowLocation, displayWindowSize)) {
                drawNode(gc, node, displayWindowLocation, zoomRatio, node == mouseOnNode);
            }
        }

        if (mouseOnNode instanceof ActivityNode) {
            highlightSelectedActivityCluster(canvas, mouseOnNode, displayWindowLocation, zoomRatio);
        }

        //Highlight Activity node and corresponding nodes
        for (ActivityNode node : selectedActivityNodes) {
            highlightSelectedActivityCluster(canvas, node, displayWindowLocation, zoomRatio);
        }

        //Draw Node/Vertices ID labels
        if (showAllNodeIds) {
            showAllNodeIds(canvas, displayWindowLocation, displayWindowSize, zoomRatio);
        } else {
            for (Node node : dispNodeIds) {
                if (node.isInDisplayWindow(displayWindowLocation, displayWindowSize)) {
                    showNodeId(node, canvas, displayWindowLocation, zoomRatio);
                }
            }

            if (comparingNode != null) {
                showNodeId(comparingNode, canvas, displayWindowLocation, zoomRatio, Color.YELLOW);
            }

            if (mouseOnNode != null) {
                showNodeId(mouseOnNode, canvas, displayWindowLocation, zoomRatio);
            }
        }

        //Draw Relationship labels
        if (showAllRelationships) {
            showAllRelationships(canvas, displayWindowLocation, displayWindowSize, zoomRatio);
        } else {
            for (Edge edge : dispRelationships) {
                Node fromNode = nodes.get(edge.getFromNodeId());
                Node toNode = nodes.get(edge.getToNodeId());
                if (fromNode != null && toNode != null) {
                    if (fromNode.isInDisplayWindow(displayWindowLocation, displayWindowSize)
                            || toNode.isInDisplayWindow(displayWindowLocation, displayWindowSize)) {
                        showRelationship(edge, canvas, displayWindowLocation, zoomRatio);
                    }
                }
            }
        }

        if (mouseOnEdge != null) {
            showRelationship(mouseOnEdge, canvas, displayWindowLocation, zoomRatio);
        }

        //Draw Relationship labels
        if (showLegend) {
            showALegend(canvas, displayWindowLocation, zoomRatio);
        }
    }

    private void highlightSelectedActivityCluster(Canvas canvas, Node node,
            double[] displayWindowLocation, double zoomRatio) {
        List<Node> tmpNodes = new LinkedList<>();
        List<Edge> tmpEdges = new LinkedList<>();
        GraphicsContext gc = canvas.getGraphicsContext2D();

        tmpNodes.add(node);
        drawNode(gc, node, displayWindowLocation, zoomRatio, true, 1.5);

        NodeFactory nodeFactory = NodeFactory.getInstance();
        String nodeId = node.getId();
        //get used nodes/inputs
        Set<String> keySet = edges.keySet()
                .stream()
                .filter(s -> s.startsWith(nodeId + ProvUtility.PROV_USED))
                .collect(Collectors.toSet());

        for (String key : keySet) {
            Edge edge = edges.get(key);
            tmpEdges.add(edge);
            Node inputNode = nodes.get(edge.getToNodeId());
            inputNode = nodeFactory.convertToInputEntityNode(inputNode);
            tmpNodes.add(inputNode);
        }

        //get generated nodes/outputs
        keySet = edges.keySet()
                .stream()
                .filter(s -> s.startsWith(nodeId + ProvUtility.PROV_GENERATED))
                .collect(Collectors.toSet());

        for (String key : keySet) {
            Edge edge = edges.get(key);
            tmpEdges.add(edge);
            Node outputNode = nodes.get(edge.getToNodeId());
            outputNode = nodeFactory.convertToOutputEntityNode(outputNode);
            tmpNodes.add(outputNode);
        }

        //get software Agent
        keySet = edges.keySet()
                .stream()
                .filter(s -> s.startsWith(nodeId + ProvUtility.PROV_WAS_ASSOCIATED_WITH))
                .collect(Collectors.toSet());

        for (String key : keySet) {
            Edge edge = edges.get(key);
            tmpEdges.add(edge);
            Node agentNode = nodes.get(edge.getToNodeId());
            tmpNodes.add(agentNode);

            //get commit
            String agentNodeId = agentNode.getId();
            Set<String> agentNodeKeySet = edges.keySet()
                    .stream()
                    .filter(s -> s.startsWith(agentNodeId + ProvUtility.PROV_WAS_DERIVED_FROM))
                    .collect(Collectors.toSet());

            for (String agentNodeKey : agentNodeKeySet) {
                Edge agentNodeEdge = edges.get(agentNodeKey);
                tmpEdges.add(agentNodeEdge);
                Node commitNode = nodes.get(agentNodeEdge.getToNodeId());
                tmpNodes.add(commitNode);
            }
        }

        for (Edge tmpEdge: tmpEdges) {
            drawEdge(gc, tmpEdge, tmpEdge.isDashLine(), displayWindowLocation, zoomRatio, true, 5,
                    Color.GREENYELLOW);
        }

        for (Node tmpNode: tmpNodes) {
            drawNode(gc, tmpNode, displayWindowLocation, zoomRatio, true, 1.5);
        }

        for (Node tmpNode: tmpNodes) {
            showNodeId(tmpNode, canvas, displayWindowLocation, zoomRatio);
        }
    }

    private void drawEdge(GraphicsContext gc, Edge edge, boolean dashedLine,
            double[] displayWindowLocation, double zoomRatio, boolean highlight) {
        drawEdge(gc, edge, dashedLine, displayWindowLocation, zoomRatio, highlight, 3);
    }

    private void drawEdge(GraphicsContext gc, Node fromNode, Node toNode, boolean dashedLine,
            double[] displayWindowLocation, double zoomRatio, boolean highlight) {
        drawEdge(gc, fromNode, toNode, dashedLine, displayWindowLocation, zoomRatio, highlight, 3);
    }

    private void drawEdge(GraphicsContext gc, Edge edge, boolean dashedLine,
            double[] displayWindowLocation, double zoomRatio, boolean highlight, double lineWidth) {
        drawEdge(gc, edge, dashedLine, displayWindowLocation, zoomRatio, highlight, lineWidth,
                Color.BLACK);
    }

    private void drawEdge(GraphicsContext gc, Node fromNode, Node toNode, boolean dashedLine,
            double[] displayWindowLocation, double zoomRatio, boolean highlight, double lineWidth) {
        drawEdge(gc, fromNode, toNode, dashedLine, displayWindowLocation, zoomRatio, highlight,
                lineWidth, Color.BLACK);
    }

    private void drawEdge(GraphicsContext gc, Edge edge, boolean dashedLine,
            double[] displayWindowLocation, double zoomRatio, boolean highlight, double lineWidth,
            Color lineColor) {
        Node fromNode = nodes.get(edge.getFromNodeId());
        Node toNode = nodes.get(edge.getToNodeId());
        drawEdge(gc, fromNode, toNode, dashedLine, displayWindowLocation, zoomRatio, highlight,
                lineWidth, lineColor);
    }

    private void drawEdge(GraphicsContext gc, Node fromNode, Node toNode, boolean dashedLine,
            double[] displayWindowLocation, double zoomRatio, boolean highlight, double lineWidth,
            Color lineColor) {
        double[] fromPoint = transformToRelativeXY(fromNode.getX(), fromNode.getY(),
                displayWindowLocation, zoomRatio);
        double[] toPoint = transformToRelativeXY(toNode.getX(), toNode.getY(),
                displayWindowLocation, zoomRatio);

        if (dashedLine) {
            gc.setLineDashes(5, 5);
        }
        if (highlight) {
            gc.setLineWidth(lineWidth);
            gc.setStroke(lineColor);
        }
        //draw the line
        gc.strokeLine(fromPoint[0], fromPoint[1], toPoint[0], toPoint[1]);

        //draw the arrow
        drawArrow(gc, fromPoint, toPoint);

        if (highlight) {
            gc.setLineWidth(1.0);
            gc.setStroke(Color.BLACK);
        }

        if (dashedLine) {
            gc.setLineDashes();
        }
    }

    private void drawNode(GraphicsContext gc, Node node, double[] displayWindowLocation,
            double zoomRatio, boolean highlight) {
        drawNode(gc, node, displayWindowLocation, zoomRatio, highlight, 1.1);
    }

    private void drawNode(GraphicsContext gc, Node aNode, double[] displayWindowLocation,
            double zoomRatio, boolean highlight, double nodeSizeRatio) {
        double[] nodeXY;
        Node node = aNode;
        // if this is the current comparing node, draw a copy of the node
        //  with its color changed to yellow
        if (node.equals(comparingNode)) {
            node = NodeFactory.getInstance().convertToComparingNode(node);
        }
        if (node.isAbsoluteXY()) {
            nodeXY = new double[]{node.getX(), node.getY()};
        } else {
            nodeXY = transformToRelativeXY(node.getX(), node.getY(), displayWindowLocation,
                    zoomRatio);
        }

        double nodeWidth = node.getWidth();
        double nodeHeight = node.getHeight();
        Color nodeColor = node.getColor();
        gc.setFill(nodeColor);
        if (highlight) {
            //center the node after resizing
            nodeWidth *= nodeSizeRatio;
            nodeHeight *= nodeSizeRatio;
        }

        nodeXY[0] -= nodeWidth / 2;
        nodeXY[1] -= nodeHeight / 2;

        if (node instanceof EntityNode) {
            gc.setFill(nodeColor);
            gc.fillRoundRect(nodeXY[0], nodeXY[1], nodeWidth, nodeHeight,
                    nodeWidth * 0.75, nodeHeight * 0.75);

            if (highlight) {
                gc.strokeRoundRect(nodeXY[0], nodeXY[1], nodeWidth, nodeHeight,
                        nodeWidth * 0.75, nodeHeight * 0.75);
            }
        } else if (node instanceof AgentNode) {
            double[] xs = new double[5];
            double[] ys = new double[5];
            xs[0] = nodeXY[0];
            ys[0] = nodeXY[1];
            xs[1] = xs[0] + nodeWidth / 2;
            ys[1] = ys[0] - nodeHeight / 3;
            xs[2] = xs[0] + nodeWidth;
            ys[2] = ys[0];
            xs[3] = xs[2];
            ys[3] = ys[2] + nodeHeight;
            xs[4] = xs[0];
            ys[4] = ys[3];

            gc.fillPolygon(xs, ys, 5);
            if (highlight) {
                gc.strokePolygon(xs, ys, 5);
            }
        } else if (node instanceof ActivityNode) {
            gc.fillRect(nodeXY[0], nodeXY[1], nodeWidth, nodeHeight);

            if (highlight) {
                gc.strokeRect(nodeXY[0], nodeXY[1], nodeWidth, nodeHeight);
            }
        } else if (node instanceof CommitNode) {
            gc.setFill(nodeColor.brighter().brighter().desaturate());
            gc.fillRoundRect(nodeXY[0], nodeXY[1], nodeWidth, nodeHeight,
                    nodeWidth * 0.75, nodeHeight * 0.75);

            gc.setFill(nodeColor);
            gc.fillRoundRect(nodeXY[0] + nodeWidth / 4, nodeXY[1] + nodeHeight / 4,
                    nodeWidth / 2, nodeHeight / 2, nodeWidth * 0.75, nodeHeight * 0.75);

            if (highlight) {
                gc.strokeRoundRect(nodeXY[0], nodeXY[1], nodeWidth, nodeHeight,
                        nodeWidth * 0.75, nodeHeight * 0.75);
            }
        }
    }

    private void drawArrow(GraphicsContext gc, double[] fromPoint, double[] toPoint) {
        double[] midPoint = GraphUtility.calculateMidPoint(fromPoint, toPoint);
        double edgeSlopeAngle = GraphUtility.calculateSlopeAngle(fromPoint, toPoint);

        double[] point1 = GraphUtility.findPointWithAngleDistance(midPoint,
                edgeSlopeAngle + edgeArrowAngle, edgeArrowSize);
        double[] point2 = GraphUtility.findPointWithAngleDistance(midPoint,
                edgeSlopeAngle - edgeArrowAngle, edgeArrowSize);
        gc.strokeLine(midPoint[0], midPoint[1], point1[0], point1[1]);
        gc.strokeLine(midPoint[0], midPoint[1], point2[0], point2[1]);
    }

    private void showNodeId(Node node, Canvas canvas, double[] displayWindowLocation,
            double zoomRatio, Color nodeIdColor) {
        showNodeId(node, canvas, displayWindowLocation, zoomRatio, nodeIdColor,
                Color.rgb(0, 0, 0, 0.3));
    }

    private void showNodeId(Node node, Canvas canvas, double[] displayWindowLocation,
            double zoomRatio) {
        showNodeId(node, canvas, displayWindowLocation, zoomRatio, Color.WHITE,
                Color.rgb(0, 0, 0, 0.3));
    }

    private void showNodeId(Node node, Canvas canvas, double[] displayWindowLocation,
            double zoomRatio, Color nodeIdColor, Color bgColor) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double[] transformedNodeXY = transformToRelativeXY(node.getX(), node.getY(),
                displayWindowLocation, zoomRatio);
        StringBuilder displayTextBuilder = new StringBuilder();

        displayTextBuilder.append(node.getDisplayId());
        if (node instanceof ActivityNode) {
            displayTextBuilder.append(System.lineSeparator()).append("Start Time: ")
                    .append(((ActivityNode) node).getStartTime());
            displayTextBuilder.append(System.lineSeparator()).append("End Time: ")
                    .append(((ActivityNode) node).getEndTime());
        }

        Text tmpText = new Text(displayTextBuilder.toString());
        tmpText.setFont(gc.getFont());
        tmpText.applyCss();

        gc.setFill(bgColor);
        gc.fillRoundRect(transformedNodeXY[0], transformedNodeXY[1] + node.getHeight(),
                tmpText.getLayoutBounds().getWidth(), tmpText.getLayoutBounds().getHeight(),
                0.5, 0.5);

        gc.setFill(nodeIdColor);
        gc.fillText(displayTextBuilder.toString(), transformedNodeXY[0],
                transformedNodeXY[1] + node.getHeight() + LABEL_FONT_SIZE);
    }

    private void showRelationship(Edge edge, Canvas canvas, double[] displayWindowLocation,
            double zoomRatio, Color nodeIdColor) {
        showRelationship(edge, canvas, displayWindowLocation, zoomRatio, nodeIdColor,
                Color.rgb(0, 0, 0, 0.3));
    }

    private void showRelationship(Edge edge, Canvas canvas, double[] displayWindowLocation,
            double zoomRatio) {
        showRelationship(edge, canvas, displayWindowLocation, zoomRatio, Color.WHITE,
                Color.rgb(0, 0, 0, 0.3));
    }

    private void showRelationship(Edge edge, Canvas canvas, double[] displayWindowLocation,
            double zoomRatio, Color relationshipColor, Color bgColor) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Node fromNode = nodes.get(edge.getFromNodeId());
        Node toNode = nodes.get(edge.getToNodeId());
        double[] transformedFromNodeXY = transformToRelativeXY(fromNode.getX(),
                fromNode.getY(), displayWindowLocation, zoomRatio);
        double[] transformedToNodeXY = transformToRelativeXY(toNode.getX(), toNode.getY(),
                displayWindowLocation, zoomRatio);
        double[] transformedMidXY = new double[]{
                (transformedFromNodeXY[0] + transformedToNodeXY[0]) / 2.0,
                (transformedFromNodeXY[1] + transformedToNodeXY[1]) / 2.0};

        Text tmpText = new Text(edge.getShortRelationship());
        tmpText.setFont(gc.getFont());
        tmpText.applyCss();

        gc.setFill(bgColor);
        gc.fillRoundRect(transformedMidXY[0], transformedMidXY[1],
                tmpText.getLayoutBounds().getWidth(),
                tmpText.getLayoutBounds().getHeight(), 0.5, 0.5);

        gc.setFill(Color.YELLOW);
        gc.fillText(edge.getShortRelationship(), transformedMidXY[0],
                transformedMidXY[1] + LABEL_FONT_SIZE);
    }

    private void showALegend(Canvas canvas, double[] displayWindowLocation, double zoomRatio) {
        double[] outerMarginXY = new double[]{10, 10};
        double[] innerMarginXY = new double[]{40, 20};
        double[] startXY = new double[]{
                outerMarginXY[0] + innerMarginXY[0],
                outerMarginXY[1] + innerMarginXY[1]};
        double[] penXY = new double[]{startXY[0], startXY[1]};
        double rowSpace = 10;
        double colSpace = 10;
        LinkedHashMap<String, Node> descNodeMap = new LinkedHashMap<>();

        NodeFactory factory = NodeFactory.getInstance();
        ActivityNode activityNode = factory.createActivityNode();
        activityNode.setId("").setX(penXY[0]).setY(penXY[1] + activityNode.getHeight() / 2)
                .setAbsoluteXY(true);
        descNodeMap.put("Activity", activityNode);
        penXY[1] += activityNode.getHeight() + rowSpace;

        AgentNode agentNode = factory.createAgentNode();
        agentNode.setId("").setX(penXY[0]).setY(penXY[1] + agentNode.getHeight() / 2)
                .setAbsoluteXY(true);
        descNodeMap.put("Software Agent", agentNode);
        penXY[1] += agentNode.getHeight() + rowSpace;

        EntityNode entityNode = factory.createEntityNode();
        entityNode.setId("").setX(penXY[0]).setY(penXY[1] + entityNode.getHeight() / 2)
                .setAbsoluteXY(true);
        descNodeMap.put("Entity", entityNode);
        penXY[1] += entityNode.getHeight() + rowSpace;

        CommitNode commitNode = factory.createCommitNode();
        commitNode.setId("").setX(penXY[0]).setY(penXY[1] + commitNode.getHeight() / 2)
                .setAbsoluteXY(true);
        descNodeMap.put("Commit", commitNode);
        penXY[1] += commitNode.getHeight() + rowSpace;

        EntityNode inputEntityNode = factory.convertToInputEntityNode(entityNode);
        inputEntityNode.setId("").setX(penXY[0]).setY(penXY[1] + entityNode.getHeight() / 2)
                .setAbsoluteXY(true);
        descNodeMap.put("Input Entity", inputEntityNode);
        penXY[1] += inputEntityNode.getHeight() + rowSpace;

        EntityNode outputEntityNode = factory.convertToOutputEntityNode(entityNode);
        outputEntityNode.setId("").setX(penXY[0]).setY(penXY[1] + entityNode.getHeight() / 2)
                .setAbsoluteXY(true);
        descNodeMap.put("Output Entity", outputEntityNode);
        penXY[1] += outputEntityNode.getHeight() + rowSpace;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.rgb(0, 0, 0, 0.3));
        gc.fillRect(outerMarginXY[0], outerMarginXY[1], 250,
                penXY[1] - rowSpace + innerMarginXY[1]);

        for (String key: descNodeMap.keySet()) {
            Node node = descNodeMap.get(key);
            drawNode(gc, node, displayWindowLocation, zoomRatio, false);
            gc.fillText(key, node.getX() + node.getWidth() / 2 + colSpace,
                    node.getY() + node.getHeight() / 2);
        }
    }

    private double[] transformToRelativeXY(double x, double y, double[] displayWindowLocation,
            double zoomRatio) {
        double[] xy = new double[2];
        xy[0] = (x - displayWindowLocation[0]) * zoomRatio;
        xy[1] = (y - displayWindowLocation[1]) * zoomRatio;

        return xy;
    }

    public Node getSelectedNode(double x, double y, double zoomRatio, boolean withTolerance) {
        for (Node node : nodes.values()) {
            if (node.isPointOnNode(x, y, zoomRatio, withTolerance)) {
                return node;
            }
        }
        return null;
    }

    public Node getComparingNode(double x, double y, Node draggedNode, double zoomRatio,
            boolean withTolerance) {
        for (Node node : nodes.values()) {
            if (node != draggedNode && node.isPointOnNode(x, y, zoomRatio, withTolerance)) {
                return node;
            }
        }
        return null;
    }

    public Edge getSelectedEdge(double x, double y, double zoomRatio) {
        for (Edge edge : edges.values()) {
            if (edge.isPointOnEdge(nodes, x, y, zoomRatio)) {
                return edge;
            }
        }
        return null;
    }

    public void setNeighbors() {
        for (Edge edge: edges.values()) {
            Node fromNode = nodes.get(edge.getFromNodeId());
            Node toNode = nodes.get(edge.getToNodeId());
            if (fromNode != null && toNode != null) {
                fromNode.getNeighborNodes().add(toNode);
                toNode.getNeighborNodes().add(fromNode);
            }
        }
    }

    public void showAllNodeIds(Canvas canvas, double[] displayWindowLocation,
            double[] displayWindowSize, double zoomRatio) {
        for (Node node : nodes.values()) {
            if (node.isInDisplayWindow(displayWindowLocation, displayWindowSize)) {
                showNodeId(node, canvas, displayWindowLocation, zoomRatio);
            }
        }
    }

    public void showAllRelationships(Canvas canvas, double[] displayWindowLocation,
            double[] displayWindowSize, double zoomRatio) {
        for (Edge edge : edges.values()) {
            showRelationship(edge, canvas, displayWindowLocation, zoomRatio);
        }
    }

    public void addOrRemoveDispNodeId(Node node) {
        if (dispNodeIds.contains(node)) {
            dispNodeIds.remove(node);
        } else {
            dispNodeIds.add(node);
        }
    }

    public void addOrRemoveDispRelationship(Edge edge) {
        if (dispRelationships.contains(edge)) {
            dispRelationships.remove(edge);
        } else {
            dispRelationships.add(edge);
        }
    }

    public boolean isShowAllNodeIds() {
        return showAllNodeIds;
    }

    public void setShowAllNodeIds(boolean showAllNodeIds) {
        this.showAllNodeIds = showAllNodeIds;
    }

    public boolean isShowAllRelationships() {
        return showAllRelationships;
    }

    public void setShowAllRelationships(boolean showAllRelationships) {
        this.showAllRelationships = showAllRelationships;
    }

    public void clearAllIdsRelationships() {
        showAllNodeIds = false;
        showAllRelationships = false;
        dispNodeIds.clear();
        dispRelationships.clear();
        selectedActivityNodes.clear();
    }

    public Node getMouseOnNode() {
        return this.mouseOnNode;
    }

    public void setMouseOnNode(Node mouseOnNode) {
        this.mouseOnNode = mouseOnNode;
    }

    public void setMouseOnEdge(Edge mouseOnEdge) {
        this.mouseOnEdge = mouseOnEdge;
    }

    public boolean isShowLegend() {
        return showLegend;
    }

    public void setShowLegend(boolean showLegend) {
        this.showLegend = showLegend;
    }

    public Node getComparingNode() {
        return comparingNode;
    }

    public void setComparingNode(Node comparingNode) {
        this.comparingNode = comparingNode;
    }

    public HashSet<Node> getCommitNodesList() {
        return commitNodesList;
    }

    public void setCommitNodesList(HashSet<Node> commitNodesList) {
        this.commitNodesList = commitNodesList;
    }
}
