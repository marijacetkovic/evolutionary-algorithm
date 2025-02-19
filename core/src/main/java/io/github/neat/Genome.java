package io.github.neat;

import java.util.ArrayList;
import java.util.Comparator;

import static io.github.neat.NodeType.HIDDEN;
import static io.github.neat.NodeType.OUTPUT;


public class Genome {
    private ArrayList<Node> nodeGenes;
    private ArrayList<Edge> edgeGenes;
    private ArrayList<Node> inputNodes;
    private ArrayList<Node> hiddenNodes;
    private ArrayList<Node> outputNodes;

    private double fitness;
    private Species species;


    public Genome(ArrayList<Node> nodeGenes, ArrayList<Edge> edgeGenes) {
        this.nodeGenes = nodeGenes;
        this.edgeGenes = edgeGenes;
    }

    public Genome() {
        nodeGenes = new ArrayList<>();
        edgeGenes = new ArrayList<>();
        initInputNodes();
        initOutputNodes();
        initConnections();
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
        //this is bad
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

    public int calcPropagation(double[] input) {
        setInputNodeValue(input);
        propagateLayer(HIDDEN);
        propagateLayer(OUTPUT);
        return parseOutput(getOutput());
    }


    private void propagateLayer(NodeType layer){
        for (Node n: nodeGenes) {
            if (n.getNodeType() == layer){
                n.calculateValue();
                n.activate();
            }
        }
    }

    private int parseOutput(double[] output){
        int idx = -1;
        double max = -1;
        for (int i = 0; i < output.length; i++) {
            if (output[i]>max){
                max = output[i];
                idx = i;
            }
        }
        return idx;
    }

    // for now 3 input nodes: food, nearest creature, energy level
    private void initInputNodes() {
        inputNodes = new ArrayList<>();
        int numInputs = Config.numInputs;

        for (int i = 0; i < numInputs; i++) {
            int nodeID = i;
            Node n = new Node(nodeID, NodeType.INPUT, 1.0);
            inputNodes.add(n);
            nodeGenes.add(n);
        }
    }


    // decisions creature makes : eat, breed, move?, do nothing
    private void initOutputNodes() {
        outputNodes = new ArrayList<>();
        int numOutputs = Config.numOutputs;
        int startID = Config.numInputs;

        for (int i = 0; i < numOutputs; i++) {
            int nodeID = startID + i;
            Node n = new Node(nodeID, OUTPUT, 1.0);
            outputNodes.add(n);
            nodeGenes.add(n);
        }
    }
    //fully connected input/output network
    private void initConnections() {
        edgeGenes = new ArrayList<>();
        INManager inManager = INManager.getInstance();

        for (Node input : inputNodes) {
            for (Node output : outputNodes) {
                int innovationID = inManager.getInnovationID(input, output);
                double weight = randomWeight();

                Edge edge = new Edge(input, output, weight, innovationID);
                addEdge(edge);
            }
        }
    }

    private double randomWeight() {
        return Math.random() * 2 - 1;
    }



    private void setInputNodeValue(double[] input) {
        for (int i = 0; i < inputNodes.size(); i++) {
            inputNodes.get(i).setActivationValue(input[i]);
        }
    }
    private void propagate(){

    }
    private double[] getOutput(){
        double[] output = new double[outputNodes.size()];
        for (int i = 0; i < outputNodes.size(); i++) {
            output[i] = outputNodes.get(i).getActivationValue();
        }
        return output;
    }
}
