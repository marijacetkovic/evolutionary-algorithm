package io.github.neat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import static io.github.neat.Config.numInputs;
import static io.github.neat.NodeType.*;


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
        inputNodes = new ArrayList<>();
        hiddenNodes = new ArrayList<>();
        outputNodes = new ArrayList<>();
        categorizeNodes();
    }

    private void categorizeNodes() {
        for (Node n:nodeGenes) {
            if (n.nodeType == INPUT) inputNodes.add(n);
            else if (n.nodeType == HIDDEN) hiddenNodes.add(n);
            else outputNodes.add(n);
        }
    }

    public Genome() {
        nodeGenes = new ArrayList<>();
        edgeGenes = new ArrayList<>();
        inputNodes = new ArrayList<>();
        hiddenNodes = new ArrayList<>();
        outputNodes = new ArrayList<>();
        initFirstIndividual();
        fitness = 1;
    }

    public void initFirstIndividual() {
        initInputNodes();
        initOutputNodes();
        initConnections();
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
        ArrayList<Edge> e = new ArrayList<>(edgeGenes);
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
        if (input.length< numInputs){
            throw new RuntimeException("Required input size is "+ numInputs);
        }
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
    //choose max output
    private int parseOutput(double[] output){
        int idx = -1;
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < output.length; i++) {
            if (output[i]>max){
                max = output[i];
                idx = i;
            }
        }
        return idx;
    }

    private void initInputNodes() {
        int numInputs = Config.numInputs;

        for (int i = 0; i < numInputs; i++) {
            int nodeID = i;
            Node n = new Node(nodeID, NodeType.INPUT, 0);
            inputNodes.add(n);
            nodeGenes.add(n);
        }
    }


    // decisions creature makes : eat, breed, move?, do nothing
    private void initOutputNodes() {
        int numOutputs = Config.numOutputs;
        int startID = numInputs;

        for (int i = 0; i < numOutputs; i++) {
            int nodeID = startID + i;
            Node n = new Node(nodeID, OUTPUT, 0);
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
//        System.out.println("num inputs "+numInputs);
//        System.out.println("input node size"+inputNodes.size());
//        for (Node n:inputNodes) {
//            System.out.println(n.getId() + " " +n.getNodeType() );
//        }


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

    // get all outgoing edges from a given node
    public List<Edge> getOutgoingEdges(Node node) {
        List<Edge> outgoing = new ArrayList<>();
        for (Edge e : this.edgeGenes) {
            if (e.getSourceNode().equals(node)) {
                outgoing.add(e);
            }
        }
        return outgoing;
    }

    public void setFitness(double v) {
        this.fitness = v;
    }

}
