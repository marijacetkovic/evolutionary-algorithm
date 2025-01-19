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
    private static void addEdgeMutation(Genome g){
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
                //or exit mutation ??
            }
        } while (g.areConnected(a, b) || a == b);

        double weight = r.nextDouble(-1.0,1.001);
        //change innovation number!!
        Edge e = new Edge(a,b,weight,0);
        g.addEdge(e);
    }

    private static void addNodeMutation(Genome g){
        //should add node ids!!!do i need them
        Node n = new Node(-1, HIDDEN,0.0);
        ArrayList<Edge> edges = g.getEdgeGenes();
        Edge e;
        do{
            e = edges.get(r.nextInt(edges.size()));
        }
        while(!e.isEnabled());
        e.toggleEnabled();
        Node src = e.getSourceNode();
        Node target = e.getTargetNode();
        //and innovation number check!!
        Edge e1 = new Edge(src, n, 1.0, -1);
        Edge e2 = new Edge(n, target, e.getWeight(), -1);
        edges.add(e1);
        edges.add(e2);
    }



}
