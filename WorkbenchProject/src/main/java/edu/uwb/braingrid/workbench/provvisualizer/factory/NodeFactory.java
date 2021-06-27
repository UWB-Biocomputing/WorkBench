package edu.uwb.braingrid.workbench.provvisualizer.factory;

import javafx.scene.paint.Color;

import edu.uwb.braingrid.workbench.provvisualizer.model.ActivityNode;
import edu.uwb.braingrid.workbench.provvisualizer.model.AgentNode;
import edu.uwb.braingrid.workbench.provvisualizer.model.CommitNode;
import edu.uwb.braingrid.workbench.provvisualizer.model.EntityNode;
import edu.uwb.braingrid.workbench.provvisualizer.model.Node;

public final class NodeFactory {

    /* Node properties */
    private static final double NODE_WIDTH = 45;
    private static final double NODE_HEIGHT = 30;
    private static final Color ACTIVITY_NODE_COLOR = Color.GREEN;
    private static final Color AGENT_NODE_COLOR = Color.RED;
    private static final Color ENTITY_NODE_COLOR = Color.BLUE;
    private static final Color INPUT_ENTITY_NODE_COLOR = Color.LIGHTSKYBLUE;
    private static final Color OUTPUT_ENTITY_NODE_COLOR = Color.PINK;
    private static final Color COMPARING_ENTITY_NODE_COLOR = Color.YELLOW;
    private static final Color COMMIT_NODE_COLOR = Color.BLUEVIOLET;

    private static NodeFactory nodeFactory = null;

    /* Prototype nodes */
    private static Node defaultNode = null;
    private static EntityNode entityNode = null;
    private static AgentNode agentNode = null;
    private static ActivityNode activityNode = null;
    private static CommitNode commitNode = null;

    private NodeFactory() {
        // singleton constructor
    }

    public static NodeFactory getInstance() {
        if (nodeFactory == null) {
            nodeFactory = new NodeFactory();
        }

        return nodeFactory;
    }

    public Node createDefaultNode() {
        if (defaultNode == null) {
            defaultNode = new Node();
        }

        return defaultNode.clone();
    }

    public ActivityNode createActivityNode() {
        if (activityNode == null) {
            activityNode = new ActivityNode(NODE_WIDTH, NODE_HEIGHT, ACTIVITY_NODE_COLOR);
        }

        return activityNode.clone();
    }

    public AgentNode createAgentNode() {
        if (agentNode == null) {
            agentNode = new AgentNode(NODE_WIDTH, NODE_HEIGHT, AGENT_NODE_COLOR);
        }

        return agentNode.clone();
    }

    public CommitNode createCommitNode() {
        if (commitNode == null) {
            commitNode = new CommitNode(NODE_WIDTH, NODE_HEIGHT, COMMIT_NODE_COLOR);
        }

        return commitNode.clone();
    }

    public EntityNode createEntityNode() {
        if (entityNode == null) {
            entityNode = new EntityNode(NODE_WIDTH, NODE_HEIGHT, ENTITY_NODE_COLOR);
        }

        return entityNode.clone();
    }

    public ActivityNode convertToActivityNode(Node node) {
        ActivityNode activityNode = createActivityNode();
        activityNode.setId(node.getId()).setX(node.getX()).setY(node.getY())
                .setLabel(node.getLabel());

        return activityNode;
    }

    public AgentNode convertToAgentNode(Node node) {
        AgentNode agentNode = createAgentNode();
        agentNode.setId(node.getId()).setX(node.getX()).setY(node.getY())
                .setLabel(node.getLabel());

        return agentNode;
    }

    public CommitNode convertToCommitNode(Node node) {
        CommitNode commitNode = createCommitNode();
        commitNode.setId(node.getId()).setX(node.getX()).setY(node.getY())
                .setLabel(node.getLabel());

        return commitNode;
    }

    public EntityNode convertToEntityNode(Node node) {
        EntityNode entityNode = createEntityNode();
        entityNode.setId(node.getId()).setX(node.getX()).setY(node.getY())
                .setLabel(node.getLabel());

        return entityNode;
    }

    public EntityNode convertToInputEntityNode(Node node) {
        EntityNode inputEntityNode = (EntityNode) node.clone();
        inputEntityNode.setColor(INPUT_ENTITY_NODE_COLOR);

        return inputEntityNode;
    }

    public EntityNode convertToOutputEntityNode(Node node) {
        EntityNode outputEntityNode = (EntityNode) node.clone();
        outputEntityNode.setColor(OUTPUT_ENTITY_NODE_COLOR);

        return outputEntityNode;
    }

    public Node convertToComparingNode(Node node) {
        Node comparingEntityNode = node.clone();
        comparingEntityNode.setColor(COMPARING_ENTITY_NODE_COLOR);

        return comparingEntityNode;
    }
}
