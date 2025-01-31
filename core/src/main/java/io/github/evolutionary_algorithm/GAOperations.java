package io.github.evolutionary_algorithm;

import java.util.ArrayList;
import java.util.Random;

import static io.github.evolutionary_algorithm.NodeType.HIDDEN;

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



    public static void crossover(Genome p1, Genome p2){
        double f1 = p1.getFitness();
        double f2 = p2.getFitness();
        if (f1>f2) getGenes(p1,p2);
        else getGenes(p2,p1);


        //create offspring
        //assign offspring a species through species manager

    }

    private static void getGenes(Genome p1, Genome p2){
        ArrayList<Edge> e1 = p1.getGenesSorted();
        ArrayList<Edge> e2 = p2.getGenesSorted();
        ArrayList<Edge> matching = new ArrayList<>();
        ArrayList<Edge> disjoint = new ArrayList<>();

        while(!e1.isEmpty()){
            if (!e2.isEmpty() && e1.get(0).getInnovationNumber()==
                e2.get(0).getInnovationNumber()){
                //matching gene
                matching.add(e1.get(0));
                e1.remove(0);
                e2.remove(0);
            }
            else{
                disjoint.add(e1.get(0));
                e1.remove(0);
            }
        }
    }



}
