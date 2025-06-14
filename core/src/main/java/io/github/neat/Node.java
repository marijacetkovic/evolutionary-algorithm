package io.github.neat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Node implements Serializable {
    final int id;
    NodeType nodeType;
    double activationValue;
    ArrayList<Edge> prev;
    public Node(int id, NodeType nodeType, double activationValue) {
        this.id = id;
        this.nodeType = nodeType;
        this.activationValue = activationValue;
        prev = new ArrayList<>();
    }

    public Node(Node original) {
        this.id = original.id;
        this.nodeType = original.nodeType;
        this.activationValue = original.activationValue;
        this.prev = new ArrayList<>();
    }
    public int getId() {
        return id;
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

    public void addPrevEdge(Edge e){
        prev.add(e);
    }

    public void calculateValue(){
        //reset previously accumulated val
        activationValue = 0;
        for (Edge e:prev) {
            if(e.isEnabled()) activationValue+=e.getSourceNode().getActivationValue()*e.getWeight();
        }
    }

    public void activate(){
        if (nodeType == NodeType.HIDDEN) {
            activationValue = relu(activationValue);
        } else if (nodeType == NodeType.OUTPUT) {
            //activationValue = sigmoid(activationValue);

        }
    }

    public double relu(double x) {
        return Math.max(0, x);
    }

    public double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
    public double tanh(double x) {
        return Math.tanh(x);
    }


    public ArrayList<Edge> getPrev() {
        return prev;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
