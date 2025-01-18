package io.github.evolutionary_algorithm;

public class Edge {
    int sourceNodeId;
    int targetNodeId;
    double weight;
    boolean enabled;
    int innovationNumber;

    public Edge(int sourceNodeId, int targetNodeId, double weight, int innovationNumber) {
        this.sourceNodeId = sourceNodeId;
        this.targetNodeId = targetNodeId;
        this.weight = weight;
        //this.enabled = enabled;
        this.innovationNumber = innovationNumber;
    }


    public int getSourceNode() {
        return sourceNodeId;
    }

    public void setSourceNode(int id) {
        this.sourceNodeId = id;
    }

    public int getTargetNode() {
        return targetNodeId;
    }

    public void setTargetNode(int id) {
        this.targetNodeId = id;
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
}
