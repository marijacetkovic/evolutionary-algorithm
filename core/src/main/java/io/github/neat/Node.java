package io.github.neat;

import java.util.ArrayList;

public class Node {
    int id;
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
        //activation function should be added!!
    }

    public ArrayList<Edge> getPrev() {
        return prev;
    }
}
