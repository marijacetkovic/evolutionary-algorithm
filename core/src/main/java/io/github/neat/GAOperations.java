package io.github.neat;

import java.util.*;

import static io.github.neat.Config.TOURNAMENT_SIZE;
import static io.github.neat.Config.structuralMutation;
import static io.github.neat.NodeType.HIDDEN;

public class GAOperations {
    static Random r = new Random(4);

    public static Genome createOffspring(Genome p1, Genome p2){
        Genome child = crossover(p1,p2);
        mutate(child);
        System.out.println(p1.getNodeGenes().size()+" node nr parent");
        System.out.println(child.getNodeGenes().size()+" node nr child");
        return child;
    }

    //change the probabilities here
    public static void mutate(Genome g){
        if (!structuralMutation){
            if(r.nextDouble()<0.7) {
                perturbWeights(g);
            }
        }
        else {
            if (r.nextDouble() < 0.9) {
                addEdgeMutation(g);
            } else {
                addNodeMutation(g);
            }
        }
        g.topologicallySort();
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

        double weight = r.nextDouble(-1.0,1.001);
        int IN = INManager.getInstance().getInnovationID(a,b);
        Edge e = new Edge(a,b,weight,IN);
        g.addEdge(e);
        System.out.println("Mutated edge");
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
      public static void perturbWeights(Genome g) {
          for (Edge e : g.getEdgeGenes()) {
              if (r.nextDouble() < 0.8) {
                  e.setWeight(e.getWeight() + r.nextGaussian() * 0.1);
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
        System.out.println("Split edge "+e.getInnovationNumber());
        int id = INManager.getInstance().getNodeID(e.getInnovationNumber());
        Node n = new Node(id, HIDDEN,0.0);
        Node src = e.getSourceNode();
        Node target = e.getTargetNode();
        int IN1 = INManager.getInstance().getInnovationID(src,n);
        int IN2 = INManager.getInstance().getInnovationID(n,target);
        Edge e1 = new Edge(src, n, 1.0, IN1);
        Edge e2 = new Edge(n, target, e.getWeight(), IN2);
        g.addEdge(e1);
        g.addEdge(e2);
        nodes.add(n);
        System.out.println("Mutated node");

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

    public static Genome crossover(Genome p1, Genome p2){
        //double f1 = p1.getFitness();
        //double f2 = p2.getFitness();
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

        //assign offspring a species through species manager
        //SpeciesManager speciesManager = SpeciesManager.getInstance();
        //speciesManager.addGenome(child);

        return child;
    }

    public static Genome tournamentSelect(List<Genome> bestGenomes) {
        Genome best = null;
        double bestFitness = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            Genome candidate = bestGenomes.get(r.nextInt(bestGenomes.size()));
            if (candidate.getFitness() > bestFitness) {
                best = candidate;
                bestFitness = candidate.getFitness();
            }
        }
        return best;
    }


}
