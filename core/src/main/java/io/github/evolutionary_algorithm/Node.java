package io.github.evolutionary_algorithm;

public class Node {
    int id;
    NodeType nodeType;
    double activationValue;

    public Node(int id, NodeType nodeType) {
        this.id = id;
        this.nodeType = nodeType;
        //this.activationValue = activationValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public double getActivationValue() {
        return activationValue;
    }

    public void setActivationValue(double activationValue) {
        this.activationValue = activationValue;
    }
}
