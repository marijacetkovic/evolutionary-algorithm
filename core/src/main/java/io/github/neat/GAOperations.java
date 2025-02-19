package io.github.neat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import static io.github.neat.NodeType.HIDDEN;

public class GAOperations {
    static Random r = new Random(4);

    public static void mutate(Genome g){
        if(r.nextDouble()>0.5){
            addEdgeMutation(g);
        }
        else{
            addNodeMutation(g);
        }
    }
    public static void addEdgeMutation(Genome g){
        ArrayList<Node> nodes = g.getNodeGenes();
        Node a = nodes.get(r.nextInt(nodes.size()));
        Node b;

        int max = 10;
        int i = 0;

        do {
            b = nodes.get(r.nextInt(nodes.size()));
            i++;
            if (i >= max) {
                a = nodes.get(r.nextInt(nodes.size()));
                break;
            }
        } while (g.areConnected(a, b));
        // a == b check disallows self loops
        double weight = r.nextDouble(-1.0,1.001);
        int IN = INManager.getInstance().getInnovationID(a,b);
        Edge e = new Edge(a,b,weight,IN);
        g.addEdge(e);
    }

    public static void addNodeMutation(Genome g){
        ArrayList<Edge> edges = g.getEdgeGenes();
        ArrayList<Node> nodes = g.getNodeGenes();
        Edge e;
        do{
            e = edges.get(r.nextInt(edges.size()));
        }
        while(!e.isEnabled());
        e.toggleEnabled();
        int id = INManager.getInstance().getNodeID(e.getInnovationNumber());
        Node n = new Node(id, HIDDEN,0.0);
        Node src = e.getSourceNode();
        Node target = e.getTargetNode();
        int IN1 = INManager.getInstance().getInnovationID(src,n);
        int IN2 = INManager.getInstance().getInnovationID(n,target);
        Edge e1 = new Edge(src, n, 1.0, IN1);
        Edge e2 = new Edge(n, target, e.getWeight(), IN2);
        edges.add(e1);
        edges.add(e2);
        nodes.add(n);
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
    //making node copies so child nodes cannot affect parent nodes for propagation
    private static ArrayList<Node> collectNodes(ArrayList<Edge> edges) {
        HashSet<Node> uniqueNodes = new HashSet<>();
        for (Edge e : edges) {
            uniqueNodes.add(e.getSourceNode());
            uniqueNodes.add(e.getTargetNode());
        }

        ArrayList<Node> nodeList = new ArrayList<>();
        for (Node originalNode : uniqueNodes) {
            nodeList.add(new Node(originalNode));
        }

        return nodeList;
    }

    //child nodes reference parent edges assuming same weight is inherited
    //check this!
    private static ArrayList<Edge> collectGenes(ArrayList<Edge> m,
                                                ArrayList<Edge> d, ArrayList<Edge> e){
        ArrayList<Edge> childEdgeGenes = new ArrayList<>();
        childEdgeGenes.addAll(m);
        childEdgeGenes.addAll(d);
        childEdgeGenes.addAll(e);
        return childEdgeGenes;
    }
    public static void crossover(Genome p1, Genome p2){
        //double f1 = p1.getFitness();
        //double f2 = p2.getFitness();
        ArrayList<Edge> m = new ArrayList<>();
        ArrayList<Edge> d = new ArrayList<>();
        ArrayList<Edge> e = new ArrayList<>();
        getGenes(p1,p2,m,d,e);

        //collected edge genes - what about node genes ???
        ArrayList<Edge> childEdgeGenes = collectGenes(m,d,e);
        ArrayList<Node> childNodeGenes = collectNodes(childEdgeGenes);
        //create offspring
        Genome child = new Genome(childNodeGenes, childEdgeGenes);

        //assign offspring a species through species manager
        SpeciesManager speciesManager = SpeciesManager.getInstance();
        speciesManager.addGenome(child);

    }



}
