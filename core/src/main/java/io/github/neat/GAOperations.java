package io.github.neat;

import java.io.IOException;

import java.util.*;

import static io.github.neat.Config.*;
import static io.github.neat.NodeType.HIDDEN;

public class GAOperations {
    static Random r = new Random();

    //for now just discarding invalid child - can be replaced w repairs
    public static Genome createOffspring(Genome p1, Genome p2){
        Genome child = crossover(p1,p2);;
        boolean childInvalid = false;
        try {
            mutate(child);
            child.updateStructure();
        } catch (RuntimeException ex) {
            childInvalid = true;
        }

        if (childInvalid) {
            System.out.println("Invalid child, replaced with mutated fitter parent.");
            Genome parent = p1.getAdjustedFitness() > p2.getAdjustedFitness() ? p1 : p2;
            child = new Genome(parent.getNodeGenes(), parent.getEdgeGenes());
            mutate(child);
            child.updateStructure();
        }
        return child;
    }


    //change the probabilities here
    public static void mutate(Genome g){
        //always do weight mutations
        if (r.nextDouble() < Config.WEIGHT_MUTATION_RATE) {
            mutateWeights(g);
        }

        if (r.nextDouble() < Config.STRUCTURAL_MUTATION_RATE) {
            mutateStructure(g);
        }
    }
    public static void addEdgeMutation(Genome g){
        ArrayList<Node> nodes = g.getNodeGenes();
        Node a;
        Node b;

        int max = 20;
        int i = 0;

        do {
            a = nodes.get(r.nextInt(nodes.size()));
            b = nodes.get(r.nextInt(nodes.size()));
            i++;
            if (i>max) return;
        //disallow self loops, alr existing and cycle forming edges
        } while (a.equals(b) || g.areConnected(a, b) || formsCycle(g,a,b));

        double weight = g.randomWeight();
        int IN = INManager.getInstance().getInnovationID(a,b);
        Edge e = new Edge(a,b,weight,IN);
        g.addEdge(e);
        //System.out.println("Mutated edge");
    }

    static boolean formsCycle(Genome g, Node a, Node b) {
        HashSet<Node> visited = new HashSet<>();
        //explore path from b to a; adding a->b then creates cycle
        return dfs(g,b,a,visited);
    }

    private static boolean dfs(Genome g, Node current, Node target, HashSet<Node> visited){
        if (current.equals(target)) return true;
        visited.add(current);
        for (Edge e: g.getOutgoingEdges(current)) {
            Node next = e.getTargetNode();
            if (!visited.contains(next) && dfs(g,e.getTargetNode(),target,visited)){
                return true;
            }
        }
        return false;
    }

        /*  public static void weightMutation(Genome g){
        ArrayList<Edge> edges = g.getEdgeGenes();
        Edge e = edges.get(r.nextInt(edges.size()));
        e.setWeight(g.randomWeight());
    }*/

    private static boolean structuralMutationEnabled(Genome g) {
        //add control wrt size of genome
        return true;
    }
    private static void mutateStructure(Genome g) {
        double prob = r.nextDouble();

        if (prob < ADD_EDGE_MUTATION_PROB) {
            addEdgeMutation(g);
        }
        if (prob < ADD_NODE_MUTATION_PROB) {
            addNodeMutation(g);
        }
        if (prob < TOGGLE_CONN_MUTATION_PROB) {
            toggleConnection(g);
        }
    }
    private static void toggleConnection(Genome g) {
        if (g.getEdgeGenes().isEmpty()) return;

        Edge edge = g.getEdgeGenes().get(r.nextInt(g.getEdgeGenes().size()));

        //try enabling
        if (!edge.isEnabled()) {
            Node source = edge.getSourceNode();
            Node target = edge.getTargetNode();

            // if doesnt form a cycle
            if (!formsCycle(g, source, target)) {
                edge.setEnabled(true);
            }
        } else {
            //safe to disable
            edge.setEnabled(false);
        }
    }
    public static void mutateWeights(Genome g) {
        for (Edge e : g.getEdgeGenes()) {
            if (r.nextDouble() < 0.8) {
                //small mutation
                if (r.nextDouble() < 0.9) {
                    e.setWeight(e.getWeight() + r.nextGaussian() * 0.1);
                } else {
                    //random
                    e.setWeight(g.randomWeight());
                }
                // clamp to -3,3
                e.setWeight(Math.max(-3.0, Math.min(3.0, e.getWeight())));
            }
        }
    }

    public static void mutateBias(Genome g) {
        for (Node n : g.getNodeGenes()) {
            if (n.getNodeType() == NodeType.INPUT) {
                continue;
            }
            if (r.nextDouble() < NODE_BIAS_MUTATION_RATE) {
                double newBias;
                //small mutation
                if (r.nextDouble() < GAUSSIAN_BIAS_MUTATION_PROB) {
                    newBias = n.getBias() + r.nextGaussian() * BIAS_MUTATION_STRENGTH;
                } else {
                    //rnd restart
                    newBias = g.randomWeight();
                }
                //clamp
                newBias = Math.max(MIN_BIAS, Math.min(MAX_BIAS, newBias));
                n.setBias(newBias);
            }
        }
    }
    public static void addNodeMutation(Genome g){
        ArrayList<Edge> edges = g.getEdgeGenes();
        ArrayList<Node> nodes = g.getNodeGenes();
        Edge e;
        do{
            e = edges.get(r.nextInt(edges.size()));
        }
        while(!e.isEnabled());
        e.disable();
        //System.out.println("Split edge "+e.getInnovationNumber());
        int id = INManager.getInstance().getNodeID(e.getInnovationNumber());
        Node n = new Node(id, HIDDEN);
        Node src = e.getSourceNode();
        Node target = e.getTargetNode();
        int IN1 = INManager.getInstance().getInnovationID(src,n);
        int IN2 = INManager.getInstance().getInnovationID(n,target);
        Edge e1 = new Edge(src, n, 1.0, IN1);
        Edge e2 = new Edge(n, target, e.getWeight(), IN2);
        g.addEdge(e1);
        g.addEdge(e2);
        g.addNode(n);
        //System.out.println("Mutated node");

    }


    //makes a distinction between m-matching, d-distinct, e-excess genes for flexibility
    //if equally fit chooses one parent at random - can be changed
    private static void getGenes(Genome p1, Genome p2,
                                 ArrayList<Edge> m, ArrayList<Edge> d,
                                 ArrayList<Edge> e){
        //align edges by innovation number
        ArrayList<Edge> e1 = p1.getGenesSorted();
        ArrayList<Edge> e2 = p2.getGenesSorted();

        //check fitness
        double f1 = p1.getFitness();
        double f2 = p2.getFitness();
        boolean fitter1 = f1 > f2;
        boolean fitter2 = f2 > f1;

        //in case of equal fitness choose rnd parent
        //can be adjusted
        if(!fitter1&&!fitter2){
            if (Math.random()<0.5) fitter1 = true;
            else fitter2 = true;
        }

        int i = 0, j = 0;

        while (i < e1.size() && j < e2.size()) {
            Edge edge1 = e1.get(i);
            Edge edge2 = e2.get(j);
            //matching gene
            if (edge1.getInnovationNumber() == edge2.getInnovationNumber()){
                m.add(edge1);
                i++;
                j++;
            }
            //disjoint from parent 1
            else if(edge1.getInnovationNumber() < edge2.getInnovationNumber()){
                if (fitter1){
                    d.add(edge1);
                }
                i++;
            }
            //disjoint from parent 2
            else{
                if (fitter2){
                    d.add(edge2);
                }
                j++;
            }
        }
        //excess genes
        while(fitter1&& i<e1.size()){
            e.add(e1.get(i));
            i++;
        }
        while(fitter2&& j<e2.size()){
            e.add(e2.get(j));
            j++;
        }

    }

    public static Object[] collectEdgesAndNodes(ArrayList<Edge> m, ArrayList<Edge> d, ArrayList<Edge> e) {
        ArrayList<Edge> allEdges = new ArrayList<>();
        allEdges.addAll(m);
        allEdges.addAll(d);
        allEdges.addAll(e);

        // Track nodes by ID to prevent duplicates
        HashMap<Integer, Node> idToNode = new HashMap<>();

        for (Edge edge : allEdges) {
            Node source = edge.getSourceNode();
            Node target = edge.getTargetNode();

            if (!idToNode.containsKey(source.getId())) {
                idToNode.put(source.getId(), new Node(source));
            }

            if (!idToNode.containsKey(target.getId())) {
                idToNode.put(target.getId(), new Node(target));
            }
        }

        ArrayList<Edge> childEdges = new ArrayList<>();
        for (Edge edge : allEdges) {
            Node source = idToNode.get(edge.getSourceNode().getId());
            Node target = idToNode.get(edge.getTargetNode().getId());
            Edge copiedEdge = new Edge(edge, source, target);
            childEdges.add(copiedEdge);
        }

        ArrayList<Node> childNodes = new ArrayList<>(idToNode.values());

        return new Object[]{childEdges, childNodes};
    }

    static Genome crossover(Genome p1, Genome p2){
        ArrayList<Edge> m = new ArrayList<>();
        ArrayList<Edge> d = new ArrayList<>();
        ArrayList<Edge> e = new ArrayList<>();
        getGenes(p1,p2,m,d,e);

        //collected edge genes - what about node genes ???
        Object[] result = collectEdgesAndNodes(m,d,e);
        ArrayList<Edge> childEdgeGenes = (ArrayList<Edge>) result[0];
        ArrayList<Node> childNodeGenes = (ArrayList<Node>) result[1];
        //create offspring
        Genome child = new Genome(childNodeGenes, childEdgeGenes);

        return child;
    }

    public static Genome tournamentSelect(List<Genome> parents) {
        Genome best = null;
        double bestFitness = Double.NEGATIVE_INFINITY;

        if (r.nextDouble() < TOURNAMENT_RND_PROB) {
            return parents.get(r.nextInt(parents.size()));
        }

        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            Genome candidate = parents.get(r.nextInt(parents.size()));
            if (candidate.getFitness() > bestFitness) {
                best = candidate;
                bestFitness = candidate.getFitness();
            }
        }
        return best;
    }
    public static double getInitialRandomWeight() {
        //[-0.2,0.2]
        return (r.nextDouble() * 0.4) - 0.2;
    }

}
