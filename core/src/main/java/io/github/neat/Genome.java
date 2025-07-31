package io.github.neat;

import java.io.Serializable;
import java.util.*;
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
    private double fitness;
    //no need to serialize
    private transient ArrayList<Node> topologicallySortedNodes;
    private transient Map<Integer, List<Edge>> adjacencyList;

    private double adjustedFitness;

    public Genome(ArrayList<Node> nodeGenes, ArrayList<Edge> edgeGenes) {
        this.nodeGenes = new ArrayList<>(nodeGenes);
        this.edgeGenes = new ArrayList<>(edgeGenes);
        inputNodes = new ArrayList<>();
        hiddenNodes = new ArrayList<>();
        outputNodes = new ArrayList<>();
        this.fitness = 0;
        this.adjustedFitness = 0;
        updateStructure();
    }
    public Genome(Genome other) {
        Map<Integer, Node> newNodeMap = new HashMap<>();
        this.nodeGenes = new ArrayList<>();
        for (Node oldNode : other.nodeGenes) {
            Node newNode = new Node(oldNode);
            this.nodeGenes.add(newNode);
            newNodeMap.put(oldNode.getId(), newNode);
        }

        this.edgeGenes = new ArrayList<>();
        for (Edge oldEdge : other.edgeGenes) {
            Node src = newNodeMap.get(oldEdge.getSourceNode().getId());
            Node target = newNodeMap.get(oldEdge.getTargetNode().getId());

            Edge newEdge = new Edge(oldEdge, src, target);
            this.edgeGenes.add(newEdge);
        }

        this.fitness = 0;
        this.adjustedFitness = 0;
        this.inputNodes = new ArrayList<>();
        this.hiddenNodes = new ArrayList<>();
        this.outputNodes = new ArrayList<>();
        this.topologicallySortedNodes = null;
        updateStructure();

    }
    public Genome() {
        nodeGenes = new ArrayList<>();
        edgeGenes = new ArrayList<>();
        inputNodes = new ArrayList<>();
        hiddenNodes = new ArrayList<>();
        outputNodes = new ArrayList<>();
        this.fitness = 0;
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

    public void topoSort() {
        List<Node> sorted = new ArrayList<>();
        Set<Node> visited = new HashSet<>();

        for (Node node : nodeGenes) {
            if (!visited.contains(node)) {
                if (!visit(node, visited, new HashSet<>(), sorted)) {
                    //printGenotype();
                    throw new RuntimeException("Cycle detected in network.");
                }
            }
        }

        Collections.reverse(sorted);
        this.topologicallySortedNodes = (ArrayList<Node>) sorted;
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


    public void printGenotype() {

        System.out.println("Genome ID: ");
        System.out.println("Fitness: " + this.fitness);
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
        updateStructure();
    }

    //prevent returning genes directly

    public ArrayList<Node> getNodeGenes() {
        return new ArrayList<>(nodeGenes);
    }

    public ArrayList<Edge> getEdgeGenes() {
        return new ArrayList<>(edgeGenes);
    }

    public void setNodeGenes(ArrayList<Node> nodeGenes) {
        this.nodeGenes = new ArrayList<>(nodeGenes);
        categorizeNodes();
    }

    public void setEdgeGenes(ArrayList<Edge> edgeGenes) {
        this.edgeGenes = edgeGenes;
    }

    //<---- toposort can raise an exception here
    public void addNode(Node n){
        this.nodeGenes.add(n);
        updateStructure();
    }
    public void addEdge(Edge e){
        this.edgeGenes.add(e);
        updateStructure();
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
        // get all edges from node a
        List<Edge> fromA = adjacencyList.getOrDefault(a.getId(), Collections.emptyList());

        for (Edge e : fromA) {
            if (e.getTargetNode().equals(b)) {
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
    private int parseOutput(double[] probs) {
        /*int bestIndex = 0;
        double bestValue = -1;

        for (int i = 0; i < output.length; i++) {
            if (output[i] > bestValue) {
                bestValue = output[i];
                bestIndex = i;
            }
        }
        return bestIndex;*/
        int output = 0;
        for (int i = 1; i < probs.length; i++) {
            if (probs[i] > probs[output]) {
                output = i;
            }
        }
        return output;
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

                this.edgeGenes.add(edge);
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

    public double[] getOutput() {
        double[] output = new double[outputNodes.size()];
        for (int i = 0; i < outputNodes.size(); i++) {
            output[i] = outputNodes.get(i).getActivationValue();
        }
        double[] rawOutputValues = outputNodes.stream()
            .mapToDouble(Node::getActivationValue)
            .toArray();

        double[] probs = softmax(rawOutputValues);

        //System.out.print(this+"Network Output:");
//        for (int i = 0; i < output.length; i++) {
//            System.out.printf("%.4f", output[i]);
//            if (i < output.length - 1) {
//                System.out.print(", ");
//            }
//        }
        //System.out.println();

        return probs;
    }
    public static double[] softmax(double[] values) {
        //find max
        double max = values[0];
        for (double v : values) {
            if (v > max) {
                max = v;
            }
        }
        //find exp -max
        double sum = 0.0;
        double[] expValues = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            expValues[i] = Math.exp(values[i] - max);
            sum += expValues[i];
        }

        //normalize by sum
        for (int i = 0; i < expValues.length; i++) {
            expValues[i] /= sum;
        }

        return expValues;
    }

    private void buildAdjacencyList() {
        adjacencyList = new HashMap<>();
        for (Edge edge : edgeGenes) {
            int sourceNodeId = edge.getSourceNode().getId();
            adjacencyList.computeIfAbsent(sourceNodeId, k -> new ArrayList<>()).add(edge);
        }
    }

    // get all outgoing edges from a given node
//    public List<Edge> getOutgoingEdges(Node node) {
//        List<Edge> outgoing = new ArrayList<>();
//        for (Edge e : this.edgeGenes) {
//            if (e.isEnabled() && e.getSourceNode().equals(node)) {
//                outgoing.add(e);
//            }
//        }
//        return outgoing;
//    }
    public List<Edge> getOutgoingEdges(Node node) {
        //get all outgoing edges or empty list
        List<Edge> allOutgoing = adjacencyList.getOrDefault(node.getId(), Collections.emptyList());

        List<Edge> enabledOutgoing = new ArrayList<>();
        for (Edge e : allOutgoing) {
            if (e.isEnabled()) {
                enabledOutgoing.add(e);
            }
        }
        return enabledOutgoing;
    }

    public double getFitness() {
        return fitness;
    }

    //taken from Creature after fitness evaluation
    public void setFitness(double v) {
        fitness = v;
        //System.out.println("Updated fitness"+v);
    }

    //structure validation and update
    public void updateStructure() {
        categorizeNodes();
        setupIncomingEdges();
        buildAdjacencyList();
        topoSort();
    }

    public void validate(){
        topoSort();
    }

    //decides input of each node for propagation
    private void setupIncomingEdges() {
        for (Node n : nodeGenes) {
            n.getIncomingEdges().clear();
        }

        for (Edge e : edgeGenes) {
            e.getTargetNode().addIncomingEdge(e);
        }
    }

    public void setAdjustedFitness(double v) {
        this.adjustedFitness = v;
    }

    public double getAdjustedFitness() {
        return this.adjustedFitness;
    }
    public void setTopologicallySortedNodes(){
        this.topologicallySortedNodes = new ArrayList<>();
    }
}
