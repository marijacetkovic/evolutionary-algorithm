package io.github.neat;

public class Edge {
    private Node sourceNode;
    private Node targetNode;
    private double weight;
    private boolean enabled;
    private int innovationNumber;

    public Edge(Node sourceNode, Node targetNode, double weight, int innovationNumber) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.weight = weight;
        this.enabled = true;
        this.innovationNumber = innovationNumber;
    }


    public Node getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(Node sourceNode) {
        this.sourceNode = sourceNode;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public int getInnovationNumber() {
        return innovationNumber;
    }

    public void setInnovationNumber(int innovationNumber) {
        this.innovationNumber = innovationNumber;
    }

    public void toggleEnabled() {
        if(enabled) enabled = false;
        else enabled = true;
    }
}
