package io.github.neat;

import java.io.Serializable;
import java.util.Objects;

public class Edge implements Serializable {
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
    public Edge (Edge originalEdge, Node src, Node target){
        this.sourceNode = src;
        this.targetNode = target;
        this.weight = originalEdge.getWeight();
        this.enabled = originalEdge.isEnabled();
        this.innovationNumber = originalEdge.getInnovationNumber();
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
    public void disable(){
        enabled = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Edge edge = (Edge) obj;
        return Objects.equals(innovationNumber, edge.innovationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(innovationNumber);
    }
}
