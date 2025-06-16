package io.github.neat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import static io.github.neat.GAOperations.r;

public class Node implements Serializable {
    final int id;
    NodeType nodeType;
    double bias;
    double rawValue;
    double activationValue;
    ArrayList<Edge> incomingEdges; //inputs
    public Node(int id, NodeType nodeType) {
        this.id = id;
        this.nodeType = nodeType;
        this.rawValue = 0.0;
        this.activationValue = 0.0;
        incomingEdges = new ArrayList<>();
        if (nodeType == NodeType.HIDDEN || nodeType == NodeType.OUTPUT) {
            //rnd bias [-1,1]
            this.bias = (r.nextDouble() * 2) - 1;
        } else {
            this.bias = 0.0;
        }
    }

    public Node(Node original) {
        this.id = original.id;
        this.nodeType = original.nodeType;
        this.bias = original.bias;
        this.rawValue = 0.0;
        this.activationValue = 0.0;
        this.incomingEdges = new ArrayList<>();
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

    public void addIncomingEdge(Edge e){
        incomingEdges.add(e);
    }

    public void calculateValue() {
        // input nodes pass their set val
        if (nodeType == NodeType.INPUT) {
            this.rawValue = this.activationValue;
            return;
        }
        rawValue = 0.0;

        for (Edge e : incomingEdges) {
            if (e.isEnabled()) {
                rawValue += e.getSourceNode().getActivationValue() * e.getWeight();
            }
        }
        rawValue += bias;

        if (nodeType == NodeType.HIDDEN) {
            activationValue = relu(rawValue);
        } else if (nodeType == NodeType.OUTPUT) {
            activationValue = sigmoid(rawValue);
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


    public ArrayList<Edge> getIncomingEdges() {
        return incomingEdges;
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


    public double getBias() {
        return this.bias;
    }
    public void setBias(double bias){
        this.bias = bias;
    }
}
