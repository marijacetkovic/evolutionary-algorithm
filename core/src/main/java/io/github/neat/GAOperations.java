package io.github.neat;

import java.util.ArrayList;
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
        } while (g.areConnected(a, b) || a == b);

        double weight = r.nextDouble(-1.0,1.001);
        int IN = INManager.getInstance().getInnovationID(a,b);
        Edge e = new Edge(a,b,weight,IN);
        g.addEdge(e);
    }

    public static void addNodeMutation(Genome g){
        ArrayList<Edge> edges = g.getEdgeGenes();
        ArrayList<Node> nodes = g.getNodeGenes();
        //add last available id
        int id = nodes.get(nodes.size()-1).getId();
        Node n = new Node(++id, HIDDEN,0.0);
        Edge e;
        do{
            e = edges.get(r.nextInt(edges.size()));
        }
        while(!e.isEnabled());
        e.toggleEnabled();
        Node src = e.getSourceNode();
        Node target = e.getTargetNode();
        //and innovation number check!!
        int IN1 = INManager.getInstance().getInnovationID(src,n);
        int IN2 = INManager.getInstance().getInnovationID(n,target);
        Edge e1 = new Edge(src, n, 1.0, IN1);
        Edge e2 = new Edge(n, target, e.getWeight(), IN2);
        edges.add(e1);
        edges.add(e2);
        nodes.add(n);
    }



    private static void getGenes(Genome p1, Genome p2,
                                 ArrayList<Edge> m, ArrayList<Edge> d,
                                 ArrayList<Edge> e){
        //not sure if this is needed
        ArrayList<Edge> e1 = p1.getGenesSorted();
        ArrayList<Edge> e2 = p2.getGenesSorted();

        //check fitness
        double f1 = p1.getFitness();
        double f2 = p2.getFitness();
        boolean fitter1 = f1 > f2;
        boolean fitter2 = f2 > f1;
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

    public static void crossover(Genome p1, Genome p2){
        double f1 = p1.getFitness();
        double f2 = p2.getFitness();
        ArrayList<Edge> m = new ArrayList<>();
        ArrayList<Edge> d = new ArrayList<>();
        ArrayList<Edge> e = new ArrayList<>();
        getGenes(p1,p2,m,d,e);
        Genome child = new Genome();
        //collected edge genes - what about node genes ???

        //create offspring
        //assign offspring a species through species manager

    }



}
