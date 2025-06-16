package io.github.neat;

import java.io.Serializable;
import java.util.*;

import static io.github.evolutionary_algorithm.Config.MAX_INITIAL_WEIGHT;
import static io.github.evolutionary_algorithm.Config.MIN_INITIAL_WEIGHT;
import static io.github.neat.Config.*;
import static io.github.neat.GAOperations.r;
import static io.github.neat.NodeType.*;


public class Genome implements Serializable {
    private ArrayList<Node> nodeGenes;
    private ArrayList<Edge> edgeGenes;
    private ArrayList<Node> inputNodes;
    private ArrayList<Node> hiddenNodes;
    private ArrayList<Node> outputNodes;

    private Species species;
    private double fitness=1;
    //no need to serialize
    private transient ArrayList<Node> topologicallySortedNodes;



    public Genome(ArrayList<Node> nodeGenes, ArrayList<Edge> edgeGenes) {
        this.nodeGenes = new ArrayList<>(nodeGenes);
        this.edgeGenes = new ArrayList<>(edgeGenes);
        inputNodes = new ArrayList<>();
        hiddenNodes = new ArrayList<>();
        outputNodes = new ArrayList<>();
        categorizeNodes();
    }

    public Genome() {
        nodeGenes = new ArrayList<>();
        edgeGenes = new ArrayList<>();
        inputNodes = new ArrayList<>();
        hiddenNodes = new ArrayList<>();
        outputNodes = new ArrayList<>();
    }

    void categorizeNodes() {
        inputNodes.clear();
        hiddenNodes.clear();
        outputNodes.clear();
        for (Node n:nodeGenes) {
            if (n.nodeType == INPUT) inputNodes.add(n);
            else if (n.nodeType == HIDDEN) hiddenNodes.add(n);
            else outputNodes.add(n);
        }
    }

    public static Genome createRandomGenome() {
        Genome genome = new Genome();
        genome.initFirstIndividual();
        return genome;
    }

    public static Genome createFromSaved(Genome genome) {
        genome.initSavedIndividual(genome);
        return genome;
    }
    private void initSavedIndividual(Genome g) {
        //ArrayList<Genome> genomes = loadGenomeList("best_genomes.ser");
        //Genome g = genomes.get(r.nextInt(genomes.size()));
        //if (Math.random()<0.5){
           // mutate(g);
        //}
        this.nodeGenes = g.nodeGenes;
        this.edgeGenes = g.edgeGenes;
        topologicallySort();
        categorizeNodes();
        System.out.println(inputNodes.size()+" input nodes size");
    }

    public ArrayList<Node> topologicallySort() {
        List<Node> sorted = new ArrayList<>();
        Set<Node> visited = new HashSet<>();

        for (Node node : nodeGenes) {
            if (!visited.contains(node)) {
                if (!visit(node, visited, new HashSet<>(), sorted)) {
                    printGenotype(this);
                    throw new RuntimeException("Cycle detected in network.");
                }
            }
        }

        Collections.reverse(sorted);
        return (ArrayList<Node>) sorted;
    }

    private boolean visit(Node node, Set<Node> visited, Set<Node> stack, List<Node> sorted) {
        if (stack.contains(node)) return false;
        if (visited.contains(node)) return true;

        stack.add(node);
        for (Edge edge : getOutgoingEdges(node)) {
            if (!visit(edge.getTargetNode(), visited, stack, sorted)) return false;
        }
        stack.remove(node);

        visited.add(node);
        sorted.add(node);
        return true;
    }


    public void printGenotype(Genome genome) {
        if (genome == null) {
            System.out.println("Genome is null.");
            return;
        }

        System.out.println("Genome ID: ");
        System.out.println("Fitness: " + genome.getFitness());
        System.out.println("Nodes:");

        for (Node node : nodeGenes) {
            System.out.println("  - Node ID: " + node.getId() + " | Type: " + node.getNodeType());
        }

        System.out.println("Connections:");
        for (Edge conn : edgeGenes) {
            System.out.println("  - " + conn.getSourceNode().getId() + " → " + conn.getTargetNode().getId()
            );
        }
    }

    public void initFirstIndividual() {
        initInputNodes();
        initOutputNodes();
        initConnections();
    }

    //prevent returning genes directly

    public ArrayList<Node> getNodeGenes() {
        return nodeGenes;
    }

    public ArrayList<Edge> getEdgeGenes() {
        return edgeGenes;
    }

    public void setNodeGenes(ArrayList<Node> nodeGenes) {
        this.nodeGenes = nodeGenes;
        categorizeNodes();
    }

    public void setEdgeGenes(ArrayList<Edge> edgeGenes) {
        this.edgeGenes = edgeGenes;
    }

    public void addNode(Node n){
        this.nodeGenes.add(n);
        categorizeNodes();
    }
    public void addEdge(Edge e){
        this.edgeGenes.add(e);
        //this is bad
        e.getTargetNode().addIncomingEdge(e);
    }
    public void analyzeWeights() {
        double avg = 0, min = 0, max = 0;
        for(Edge e : edgeGenes) {
            avg += e.getWeight();
            min = Math.min(min, e.getWeight());
            max = Math.max(max, e.getWeight());
        }
        avg /= edgeGenes.size();
        System.out.printf("Weights: Avg=%.2f Min=%.2f Max=%.2f\n", avg, min, max);
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
            if ((e.getSourceNode().equals(a) && e.getTargetNode().equals(b))
                || (e.getSourceNode().equals(b) && e.getTargetNode().equals(a))){
                return true;
            }
        }

        return false;
    }


    //according to IN number
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
        if (input.length < Config.numInputs) {
            throw new RuntimeException("Required input size is " + Config.numInputs);
        }
        //reset old vals!!
        for (Node n : nodeGenes) {
            n.setActivationValue(0.0);
        }

        setInputNodeValue(input);

        for (Node n : topologicallySortedNodes) {
            if (n.getNodeType() != NodeType.INPUT) {
                n.calculateValue();
            }
        }

        return parseOutput(getOutput());
    }

    //choose max output
    private int parseOutput(double[] output) {
        int bestIndex = 0;
        double bestValue = -1;

        for (int i = 0; i < output.length; i++) {
            if (output[i] > bestValue) {
                bestValue = output[i];
                bestIndex = i;
            }
        }
        return bestIndex;
    }


    private void initInputNodes() {
        int numInputs = Config.numInputs;

        for (int i = 0; i < numInputs; i++) {
            int nodeID = i;
            Node n = new Node(nodeID, NodeType.INPUT);
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
            Node n = new Node(nodeID, OUTPUT);
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
                //System.out.println("Initial Edge: " + input.getId() + " -> " + output.getId() + " Weight: " + weight);

                addEdge(edge);
            }
        }
    }

    public double randomWeight() {
        double range = MAX_INITIAL_WEIGHT - MIN_INITIAL_WEIGHT;
        return MIN_INITIAL_WEIGHT + (r.nextDouble() * range);
    }


    private void setInputNodeValue(double[] input) {
//        System.out.println("num inputs "+numInputs);
        //System.out.println("input node size"+inputNodes.size());
//        for (Node n:inputNodes) {
//            System.out.println(n.getId() + " " +n.getNodeType() );
//        }


        for (int i = 0; i < inputNodes.size(); i++) {
            inputNodes.get(i).setActivationValue(input[i]);
        }
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


    public double getFitness() {
        return fitness;
    }

    public void setFitness(double v) {
        fitness = v;
    }
}
