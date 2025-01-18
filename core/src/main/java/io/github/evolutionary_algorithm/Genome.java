package io.github.evolutionary_algorithm;

import java.util.ArrayList;

public class Genome {
    ArrayList<Node> nodeGenes;
    ArrayList<Edge> edgeGenes;

    public Genome(ArrayList<Node> nodeGenes, ArrayList<Edge> edgeGenes) {
        this.nodeGenes = nodeGenes;
        this.edgeGenes = edgeGenes;
    }

    public Genome() {
        nodeGenes = new ArrayList<>();
        edgeGenes = new ArrayList<>();
    }

    public ArrayList<Node> getNodeGenes() {
        return nodeGenes;
    }

    public void setNodeGenes(ArrayList<Node> nodeGenes) {
        this.nodeGenes = nodeGenes;
    }

    public ArrayList<Edge> getEdgeGenes() {
        return edgeGenes;
    }

    public void setEdgeGenes(ArrayList<Edge> edgeGenes) {
        this.edgeGenes = edgeGenes;
    }

    public void addNode(Node n){
        this.nodeGenes.add(n);
    }
    public void addEdge(Edge e){
        this.edgeGenes.add(e);
        e.getTargetNode().addPrevEdge(e);
    }

    public void displayGenotype(){
        System.out.println("nodes:");
        for (Node node : getNodeGenes()) {
            System.out.println("ID: " + node.getId() + ", type: " + node.getNodeType());
        }

        System.out.println("edges:");
        for (Edge edge : getEdgeGenes()) {
            System.out.println("from: " + edge.getSourceNode() +
                " to: " + edge.getTargetNode() +
                " weight: " + edge.getWeight() +
                " enabled: " + edge.isEnabled() +
                " innovation: " + edge.getInnovationNumber());
        }
    }

}
