package io.github.neat;

import java.util.ArrayList;
import java.util.Comparator;

public class Genome {
    private ArrayList<Node> nodeGenes;
    private ArrayList<Edge> edgeGenes;
    private double fitness;
    private Species species;


    public Genome(ArrayList<Node> nodeGenes, ArrayList<Edge> edgeGenes) {
        this.nodeGenes = nodeGenes;
        this.edgeGenes = edgeGenes;
    }

    public Genome() {
        nodeGenes = new ArrayList<>();
        edgeGenes = new ArrayList<>();
        fitness = 1;
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

    public boolean areConnected(Node a, Node b) {
        //should add adjacency list for this check
        for (Edge e:edgeGenes) {
            if ((e.getSourceNode()==a && e.getTargetNode()==b)
            //  uncommented for checking a->b direction only
            //    || (e.getSourceNode()==b && e.getTargetNode()==a)
            ){
                return true;
            }
        }

        return false;
    }

    public double getFitness(){
        return fitness;
    }

    public ArrayList<Edge> getGenesSorted(){
        ArrayList<Edge> e = new ArrayList<Edge>(edgeGenes);
        e.sort(Comparator.comparingInt(Edge::getInnovationNumber));
        return e;
    }

    public void setSpecies(Species species) {
        this.species = species;
    }
    public Species getSpecies(){
        return species;
    }
}
